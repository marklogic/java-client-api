/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.InputOutputCaller;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class InputOutputEndpointImpl<I,O> extends IOEndpointImpl<I,O> implements InputOutputCaller<I,O> {
    private static final Logger logger = LoggerFactory.getLogger(InputOutputEndpointImpl.class);
    private final InputOutputCallerImpl<I,O> caller;
    private final int batchSize;

    public InputOutputEndpointImpl(
            DatabaseClient client, JSONWriteHandle apiDecl, HandleProvider<I,O> handleProvider
    ) {
        this(client, new InputOutputCallerImpl<>(apiDecl, handleProvider));
    }
    private InputOutputEndpointImpl(DatabaseClient client, InputOutputCallerImpl<I,O> caller) {
        super(client, caller);
        this.caller = caller;
        this.batchSize = initBatchSize(caller);
    }

    private InputOutputCallerImpl<I,O> getCaller() {
        return this.caller;
    }

    private int getBatchSize() {
        return this.batchSize;
    }

    @Override
    public O[] call(I[] input) {
        return getResponseData(newCallContext(true), input);
    }
    @Override
    public O[] call(CallContext callContext, I[] input) {
        return getResponseData(callContext, input);
    }

    @Override
    public BulkInputOutputCaller<I,O> bulkCaller() {
        return new BulkInputOutputCallerImpl<>(this);
    }
    @Override
    public BulkInputOutputCaller<I,O> bulkCaller(CallContext callContext) {
        return new BulkInputOutputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContext));
    }
    @Override
    public BulkInputOutputCaller<I,O> bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length == 0)
            throw new IllegalArgumentException("CallContext cannot be null or empty");
        return bulkCaller(callContexts, callContexts.length);
    }
    @Override
    public BulkInputOutputCaller<I,O> bulkCaller(CallContext[] callContexts, int threadCount) {
        if(callContexts == null)
            throw new IllegalArgumentException("CallContext cannot be null");
        if(threadCount > callContexts.length)
            throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

        switch(callContexts.length) {
            case 0: throw new IllegalArgumentException("CallContext cannot be empty");
            case 1: return new BulkInputOutputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContexts[0]));
            default: return new BulkInputOutputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContexts), threadCount);
        }
    }

    private O[] getResponseData(CallContext callContext, I[] input) {
        InputOutputCallerImpl<I,O> callerImpl = getCaller();
        BufferableContentHandle<?,?>[] inputHandles = callerImpl.bufferableInputHandleOn(input);
        return callerImpl.arrayCall(getClient(), checkAllowedArgs(callContext), inputHandles);
    }

    static public class BulkInputOutputCallerImpl<I,O> extends IOEndpointImpl.BulkIOEndpointCallerImpl<I,O>
            implements InputOutputCaller.BulkInputOutputCaller<I,O> {

        private final InputOutputEndpointImpl<I,O> endpoint;
        private final int batchSize;
        private final LinkedBlockingQueue<I> inputQueue;
        private Consumer<O> outputListener;
        private ErrorListener errorListener;

        public BulkInputOutputCallerImpl(InputOutputEndpointImpl<I,O> endpoint) {
            this(endpoint, endpoint.getBatchSize(), endpoint.checkAllowedArgs(endpoint.newCallContext()));
        }
        private BulkInputOutputCallerImpl(InputOutputEndpointImpl<I,O> endpoint, int batchSize, CallContextImpl<I,O> callContext) {
            super(endpoint, callContext);
            checkEndpoint(endpoint, "InputOutputEndpointImpl");
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.inputQueue = new LinkedBlockingQueue<>();
        }
        private BulkInputOutputCallerImpl(InputOutputEndpointImpl<I,O> endpoint, int batchSize, CallContextImpl<I,O>[] callContexts,
                                          int threadCount) {
            super(endpoint, callContexts, threadCount, (2*callContexts.length));
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.inputQueue = new LinkedBlockingQueue<>();
        }

        private InputOutputEndpointImpl<I,O> getEndpoint() {
            return endpoint;
        }
        private int getBatchSize() {
            return batchSize;
        }
        private LinkedBlockingQueue<I> getInputQueue() {
            return inputQueue;
        }
        private Consumer<O> getOutputListener() {
            return outputListener;
        }

        @Override
        public void setOutputListener(Consumer<O> listener) {
            this.outputListener = listener;
        }

        @Override
        public void accept(I input) {
            if (getOutputListener() == null)
                throw new IllegalStateException("Must configure output consumer before providing input");

            boolean hasBatch = queueInput(input, getInputQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }
        @Override
        public void acceptAll(I[] input) {
            if (getOutputListener() == null)
                throw new IllegalStateException("Must configure output consumer before providing input");

            boolean hasBatch = queueAllInput(input, getInputQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        private ErrorListener getErrorListener() {
            return this.errorListener;
        }

        private void processInput() {
            I[] inputBatch = getInputBatch(getInputQueue(), getBatchSize());
            if(getCallContext()!=null) {
                processInput(getCallContext(), inputBatch);
                // TODO : optimize the case of a single thread with a callContextQueue.
            } else if(getCallContextQueue() != null ){
                    BulkCallableImpl<I,O> bulkCallableImpl = new BulkCallableImpl<>(this, inputBatch);
                    submitTask(bulkCallableImpl);
            }
            else {
                throw new IllegalArgumentException("Cannot process input without Callcontext.");
            }
        }

        private void processInput(CallContextImpl<I,O> callContext, I[] inputBatch) {
            logger.trace("input endpoint running endpoint={} count={} state={}", (callContext).getEndpoint().getEndpointPath(), getCallCount(),
                    callContext.getEndpointState());
            InputOutputCallerImpl<I,O> callerImpl = getEndpoint().getCaller();

            ErrorDisposition error = ErrorDisposition.RETRY;

            BufferableContentHandle<?,?>[] inputHandles = callerImpl.bufferableInputHandleOn(inputBatch);
            for (int retryCount = 0; retryCount < DEFAULT_MAX_RETRIES && error == ErrorDisposition.RETRY; retryCount++) {
                Throwable throwable = null;
                O[] output = null;
                try {
                    output = callerImpl.arrayCall(callContext.getClient(), callContext, inputHandles);

                    incrementCallCount();
                    processOutputBatch(output, getOutputListener());
                    return;
                } catch(Throwable catchedThrowable) {
                    throwable = catchedThrowable;
                }

                if (throwable != null) {
                    if (getErrorListener() != null) {
                        try {
                            if (retryCount < DEFAULT_MAX_RETRIES - 1) {
                                error = getErrorListener().processError(
                                        retryCount, throwable, callContext, inputHandles
                                );
                            } else {
                                error = ErrorDisposition.SKIP_CALL;
                            }
                        } catch (Throwable throwable1) {
                            logger.error("Error Listener failed with " , throwable1);
                            error = ErrorDisposition.STOP_ALL_CALLS;
                        }

                        switch (error) {
                            case RETRY:
                                continue;

                            case SKIP_CALL:
                                return;

                            case STOP_ALL_CALLS:
                                if (getCallerThreadPoolExecutor() != null) {
                                    getCallerThreadPoolExecutor().shutdown();
                                }
                        }
                    // executing in the application thread
                    } else if (getCallContext() != null) {
                        throw new RuntimeException("Failed to produce output from input", throwable);
                    // executing in a concurrent worker thread
                    } else {
                        logger.error("No error listener set. Stop all calls. " + getEndpoint().getEndpointPath(), throwable);
                        error = ErrorDisposition.STOP_ALL_CALLS;
                    }
                }
            }
        }

        @Override
        public void awaitCompletion() {
            try {
                if (getInputQueue() != null) {
                    while (!getInputQueue().isEmpty()) {
                        processInput();
                    }
                }

                // calling in concurrent threads
                if(getCallerThreadPoolExecutor() != null) {
                    getCallerThreadPoolExecutor().shutdown();
                    getCallerThreadPoolExecutor().awaitTermination();
                }
            } catch (Throwable throwable) {
                throw new RuntimeException("Error occurred while awaiting termination "+throwable.getMessage());
            }
        }

        static private class BulkCallableImpl<I,O> implements Callable<Boolean> {
            private final BulkInputOutputCallerImpl<I,O> bulkInputOutputCallerImpl;
            private final I[] inputBatch;
            BulkCallableImpl(BulkInputOutputCallerImpl<I,O> BulkInputOutputCallerImpl, I[] inputBatch) {
                this.bulkInputOutputCallerImpl = BulkInputOutputCallerImpl;
                this.inputBatch = inputBatch;
            }
            @Override
            public Boolean call() {
                try {
                    CallContextImpl<I,O> callContext = bulkInputOutputCallerImpl.getCallContextQueue().take();

                    bulkInputOutputCallerImpl.processInput(callContext, inputBatch);
                    bulkInputOutputCallerImpl.getCallContextQueue().put(callContext);
                } catch(Exception ex) {
                    throw new InternalError("Error occurred while processing CallContext - "+ex.getMessage());
                }
                return true;
            }
        }
    }
}
