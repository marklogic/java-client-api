/*
 * Copyright (c) 2022 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.OutputCaller;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class OutputEndpointImpl<I,O> extends IOEndpointImpl<I,O> implements OutputCaller<O> {
    private static final Logger logger = LoggerFactory.getLogger(OutputEndpointImpl.class);
    private final OutputCallerImpl<I,O> caller;

    public OutputEndpointImpl(
        DatabaseClient client, JSONWriteHandle apiDecl, HandleProvider<I,O> handleProvider
    ) {
        this(client, new OutputCallerImpl<>(apiDecl, handleProvider));
    }
    private OutputEndpointImpl(DatabaseClient client, OutputCallerImpl<I,O> caller) {
        super(client, caller);
        this.caller = caller;
    }
    private OutputCallerImpl<I,O> getCaller() {
        return this.caller;
    }

    @Override
    public O[] call() {
        return getResponseData(newCallContext());
    }
    @Override
    public O[] call(CallContext callContext) {
        return getResponseData(callContext);
    }

    @Override
    public BulkOutputCaller<O> bulkCaller() {
        return new BulkOutputCallerImpl<>(this);
    }
    @Override
    public BulkOutputCaller<O> bulkCaller(CallContext callContext) {
        return new BulkOutputCallerImpl<>(this, checkAllowedArgs(callContext));
    }
    @Override
    public BulkOutputCaller<O> bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length == 0)
            throw new IllegalArgumentException("CallContext cannot be null or empty");
        return bulkCaller(callContexts, callContexts.length);
    }
    @Override
    public BulkOutputCaller<O> bulkCaller(CallContext[] callContexts, int threadCount) {
        if(callContexts == null)
            throw new IllegalArgumentException("CallContext cannot be null");
        if(threadCount > callContexts.length)
            throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

        switch(callContexts.length) {
            case 0: throw new IllegalArgumentException("CallContext cannot be empty");
            case 1: return new BulkOutputCallerImpl<>(this, checkAllowedArgs(callContexts[0]));
            default: return new BulkOutputCallerImpl<>(this, checkAllowedArgs(callContexts), threadCount);
        }
    }

    private O[] getResponseData(CallContext callContext) {
        return getCaller().arrayCall(getClient(), checkAllowedArgs(callContext));
    }

    static public class BulkOutputCallerImpl<I,O> extends IOEndpointImpl.BulkIOEndpointCallerImpl<I,O>
            implements OutputCaller.BulkOutputCaller<O> {

        private final OutputEndpointImpl<I,O> endpoint;
        private Consumer<O> outputListener;
        private ErrorListener errorListener;
        private AtomicInteger aliveCallContextCount;

        public BulkOutputCallerImpl(OutputEndpointImpl<I,O> endpoint) {
            this(endpoint, endpoint.checkAllowedArgs(endpoint.newCallContext()));
        }
        private BulkOutputCallerImpl(OutputEndpointImpl<I,O> endpoint, CallContextImpl<I,O> callContext) {
            super(endpoint, callContext);
            checkEndpoint(endpoint, "OutputEndpointImpl");
            this.endpoint = endpoint;
        }
        private BulkOutputCallerImpl(OutputEndpointImpl<I,O> endpoint, CallContextImpl<I,O>[] callContexts, int threadCount) {
            super(endpoint, callContexts, threadCount, threadCount);
            this.endpoint = endpoint;
            this.aliveCallContextCount = new AtomicInteger(threadCount);
        }

        private OutputEndpointImpl<I,O> getEndpoint() {
            return endpoint;
        }
        private Consumer<O> getOutputListener() {
            return outputListener;
        }

        @Override
        public void setOutputListener(Consumer<O> listener) {
            this.outputListener = listener;
        }

        @Override
        public O[] next() {
            if(getCallContext() == null)
                throw new UnsupportedOperationException("Callcontext cannot be null.");
            if (getOutputListener() != null)
                throw new IllegalStateException("Cannot call next while current output consumer is not empty.");
            return getOutputStream(getCallContext());
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        private ErrorListener getErrorListener() {
            return this.errorListener;
        }

        @Override
        public void awaitCompletion() {
            if (getOutputListener() == null)
                throw new IllegalStateException("Output consumer is null");

            if (getPhase() != WorkPhase.INITIALIZING) {
                throw new IllegalStateException(
                        "Can only await completion when starting output and not while output is "+
                        getPhase().name().toLowerCase());
            }

            setPhase(WorkPhase.RUNNING);
            if(getCallContext() != null)
                processOutput();
                // TODO : optimize the case of a single thread with a callContextQueue.

            else if(getCallContextQueue() != null && !getCallContextQueue().isEmpty()){
                try {
                    for (int i = 0; i < getThreadCount(); i++) {
                        BulkCallableImpl<I,O> bulkCallableImpl = new BulkCallableImpl(this);
                        submitTask(bulkCallableImpl);
                    }
                    getCallerThreadPoolExecutor().awaitTermination();
                }
                catch(Throwable throwable) {
                    throw new RuntimeException("Error occurred while awaiting termination ", throwable);
                }
            } else {
                throw new IllegalArgumentException("Cannot process output without Callcontext.");
            }
        }

        private O[] getOutputStream(CallContextImpl<I,O> callContext) {
            ErrorDisposition error = ErrorDisposition.RETRY;
            O[] output = null;

            for (int retryCount = 0; retryCount < DEFAULT_MAX_RETRIES && error == ErrorDisposition.RETRY; retryCount++) {
                Throwable throwable = null;
                try {
                    output = getEndpoint().getCaller().arrayCall(callContext.getClient(), callContext);
                    incrementCallCount();
                    return output;
                } catch(Throwable catchedThrowable) {
                    throwable = catchedThrowable;
                }

                if (throwable != null) {
                    if (getErrorListener() == null) {
                        logger.error("No error listener set. Stop all calls. " + getEndpoint().getEndpointPath(), throwable);
                        error = ErrorDisposition.STOP_ALL_CALLS;
                    } else {

                        try {
                            if (retryCount < DEFAULT_MAX_RETRIES - 1) {

                                error = getErrorListener().processError(retryCount, throwable, callContext);
                            } else {
                                error = ErrorDisposition.SKIP_CALL; //used to be STOP_ALL_CALLS
                            }
                        } catch (Throwable throwable1) {
                            logger.error("Error Listener failed with " , throwable1);
                            error = ErrorDisposition.STOP_ALL_CALLS;
                        }

                        switch (error) {
                            case RETRY:
                                continue;

                            case SKIP_CALL:
                                if(callContext.getEndpoint().allowsEndpointState()) {
                                    callContext.withEndpointState(null);
                                }
                                return getEndpoint().getCaller().newContentOutputArray(0);

                            case STOP_ALL_CALLS:
                                if (getCallerThreadPoolExecutor() != null) {
                                    getCallerThreadPoolExecutor().shutdown();
                                }
                        }
                    }
                }
            }

            return (output == null) ? getEndpoint().getCaller().newContentOutputArray(0) : output;
        }

        private void processOutput() {
            CallContextImpl<I,O> callContext = getCallContext();
            if(callContext != null) {
                while (processOutput(callContext));
            }
        }

        private boolean processOutput(CallContextImpl<I,O> callContext){
                logger.trace("output endpoint={} count={} state={}",
                        (callContext).getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointState());

                O[] output = getOutputStream(callContext);

                processOutputBatch(output, getOutputListener());

                switch(getPhase()) {
                    case INTERRUPTING:
                        setPhase(WorkPhase.INTERRUPTED);
                        logger.info("output interrupted endpoint={} count={} work={}",
                                (callContext).getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointConstants());
                        return false;
                    case RUNNING:
                        if (output == null || output.length == 0) {
                            if(getCallerThreadPoolExecutor() == null || aliveCallContextCount.get() == 0)
                                setPhase(WorkPhase.COMPLETED);
                            logger.info("output completed endpoint={} count={} work={}",
                                    (callContext).getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointConstants());
                            return false;
                        }
                        return true;
                    case INTERRUPTED:
                    case COMPLETED:
                        throw new IllegalStateException(
                                "cannot process more output as current phase is  " + getPhase().name());
                    default:
                        throw new MarkLogicInternalException(
                                "unexpected state for "+(callContext).getEndpoint().getEndpointPath()+" during loop: "+getPhase().name()
                        );
                }

        }

        static private class BulkCallableImpl<I,O> implements Callable<Boolean> {
            private final BulkOutputCallerImpl<I,O> bulkOutputCallerImpl;

            BulkCallableImpl(BulkOutputCallerImpl<I,O> bulkOutputCallerImpl) {
                this.bulkOutputCallerImpl = bulkOutputCallerImpl;
            }
            @Override
            public Boolean call() {
                try {
                    CallContextImpl<I,O> callContext = bulkOutputCallerImpl.getCallContextQueue().poll();

                    boolean continueCalling = (callContext == null) ? false : bulkOutputCallerImpl.processOutput(callContext);
                    if (continueCalling) {
                        bulkOutputCallerImpl.getCallContextQueue().put(callContext);
                        bulkOutputCallerImpl.submitTask(this);
                    } else {
                        if (bulkOutputCallerImpl.getCallerThreadPoolExecutor() != null && bulkOutputCallerImpl.aliveCallContextCount.decrementAndGet() == 0) {
                            bulkOutputCallerImpl.getCallerThreadPoolExecutor().shutdown();
                        }
                    }

                    return true;
                } catch (Throwable throwable) {
                    throw new InternalError("Error occurred while processing CallContext - "+throwable.getMessage());
                }
            }
        }
    }
}
