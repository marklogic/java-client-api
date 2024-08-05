/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.ExecCaller;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecEndpointImpl<I,O> extends IOEndpointImpl<I,O> implements ExecCaller {
    private static final Logger logger = LoggerFactory.getLogger(ExecEndpointImpl.class);
    private final ExecCallerImpl<I,O> caller;

    public ExecEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
        this(client, new ExecCallerImpl<>(apiDecl));
    }
    private ExecEndpointImpl(DatabaseClient client, ExecCallerImpl<I,O> caller) {
        super(client, caller);
        this.caller = caller;
    }

    private ExecCallerImpl<I,O> getCaller() {
        return this.caller;
    }

    @Override
    public void call() {
        call(newCallContext());
    }
    @Override
    public void call(CallContext callContext) {
        getCaller().call(getClient(), checkAllowedArgs(callContext));
    }

    @Override
    public BulkExecCaller bulkCaller() {
        return new BulkExecCallerImpl<>(this);
    }
    @Override
    public BulkExecCaller bulkCaller(CallContext callContext) {
        return new BulkExecCallerImpl<>(this, checkAllowedArgs(callContext));
    }
    @Override
    public BulkExecCaller bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length == 0)
            throw new IllegalArgumentException("CallContext cannot be null or empty.");
        return bulkCaller(callContexts, callContexts.length);
    }
    @Override
    public BulkExecCaller bulkCaller(CallContext[] callContexts, int threadCount) {
        if(callContexts == null)
            throw new IllegalArgumentException("CallContext cannot be null.");
        if(threadCount > callContexts.length)
            throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

        switch(callContexts.length) {
            case 0: throw new IllegalArgumentException("CallContext cannot be empty");
            case 1: return new BulkExecCallerImpl<>(this, checkAllowedArgs(callContexts[0]));
            default: return new BulkExecCallerImpl<>(this, checkAllowedArgs(callContexts), threadCount);
        }
    }

    public static class BulkExecCallerImpl<I,O>
            extends IOEndpointImpl.BulkIOEndpointCallerImpl<I,O>
            implements ExecCaller.BulkExecCaller
    {
        private final ExecEndpointImpl<I,O> endpoint;
        private ErrorListener errorListener;
        private AtomicInteger aliveCallContextCount;

        public BulkExecCallerImpl(ExecEndpointImpl<I,O> endpoint) {
            this(endpoint, endpoint.checkAllowedArgs(endpoint.newCallContext()));
        }
        private BulkExecCallerImpl(ExecEndpointImpl<I,O> endpoint, CallContextImpl<I,O> callContext) {
            super(endpoint, callContext);
            checkEndpoint(endpoint, "ExecEndpointImpl");
            this.endpoint = endpoint;
        }
        private BulkExecCallerImpl(ExecEndpointImpl<I,O> endpoint, CallContextImpl<I,O>[] callContexts, int threadCount) {
            super(endpoint, callContexts, threadCount, threadCount);
            this.endpoint = endpoint;
            this.aliveCallContextCount = new AtomicInteger(threadCount);
        }

        private ExecEndpointImpl<I,O> getEndpoint() {
            return this.endpoint;
        }

        @Override
        public void awaitCompletion() {
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
                    if(getCallerThreadPoolExecutor() != null)
                        getCallerThreadPoolExecutor().awaitTermination();
                }
                catch(Throwable throwable) {
                    throw new RuntimeException("Error occurred while awaiting termination ", throwable);
                }
            } else {
                throw new IllegalArgumentException("Cannot process output without Callcontext.");
            }
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        private ErrorListener getErrorListener() {
            return this.errorListener;
        }

        private boolean processExec(CallContextImpl<I,O> callContext) {
            ErrorDisposition error = ErrorDisposition.RETRY;

            retry:
            for (int retryCount = 0; retryCount < DEFAULT_MAX_RETRIES && error == ErrorDisposition.RETRY; retryCount++) {
                Throwable throwable = null;
                try {
                    logger.trace("exec calling endpoint={} count={} state={}",
                            callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointState());
                    boolean hasState = getEndpoint().getCaller().call(callContext.getClient(), callContext);
                    incrementCallCount();
                    return hasState;
                } catch(Throwable catchedThrowable) {
                    // TODO: logging
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
                                continue retry;
                            case SKIP_CALL:
                                if(callContext.getEndpoint().allowsEndpointState()) {
                                    callContext.withEndpointState(null);
                                }
                                break retry;
                            case STOP_ALL_CALLS:
                                if (getCallerThreadPoolExecutor() != null) {
                                    getCallerThreadPoolExecutor().shutdown();
                                }
                                break retry;
                        }
                    }
                }
            }
            return false;
        }

        private void processOutput() {
            CallContextImpl<I,O> callContext = getCallContext();
            if(callContext != null) {
                while (processOutput(callContext));
            }
        }

        private boolean processOutput(CallContextImpl<I,O> callContext){
            boolean continueCalling = processExec(callContext);

            switch(getPhase()) {
                case INTERRUPTING:
                    setPhase(WorkPhase.INTERRUPTED);
                    logger.info("exec interrupted endpoint={} count={} work={}",
                            callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointConstants());
                    return false;
                case RUNNING:
                    if (!continueCalling) {
                        if(getCallerThreadPoolExecutor() == null || aliveCallContextCount.get() == 0)
                            setPhase(WorkPhase.COMPLETED);
                        logger.info("exec completed endpoint={} count={} work={}",
                                callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointConstants());
                        return false;
                    }
                    return true;
                default:
                    throw new MarkLogicInternalException(
                            "unexpected state for "+callContext.getEndpoint().getEndpointPath()+" during loop: "+getPhase().name()
                    );
            }
        }

        static private class BulkCallableImpl<I,O> implements Callable<Boolean> {
            private final BulkExecCallerImpl<I,O> bulkExecCallerImpl;

            BulkCallableImpl(BulkExecCallerImpl<I,O> bulkExecCallerImpl) {
                this.bulkExecCallerImpl = bulkExecCallerImpl;
            }

            @Override
            public Boolean call() throws InterruptedException{
                CallContextImpl<I,O> callContext = bulkExecCallerImpl.getCallContextQueue().poll();

                boolean continueCalling = (callContext == null) ? false : bulkExecCallerImpl.processOutput(callContext);

                if(continueCalling) {
                    bulkExecCallerImpl.getCallContextQueue().put(callContext);
                    bulkExecCallerImpl.submitTask(this);
                }
                else {
                    if (bulkExecCallerImpl.aliveCallContextCount.decrementAndGet() == 0 && bulkExecCallerImpl.getCallerThreadPoolExecutor() != null) {
                        bulkExecCallerImpl.getCallerThreadPoolExecutor().shutdown();
                    }
                }

                return true;
            }
        }
    }
}
