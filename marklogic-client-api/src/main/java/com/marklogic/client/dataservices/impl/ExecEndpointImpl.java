/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import com.marklogic.client.dataservices.ExecEndpoint;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

final public class ExecEndpointImpl extends IOEndpointImpl implements ExecEndpoint {
    private static Logger logger = LoggerFactory.getLogger(ExecEndpointImpl.class);
    private ExecCallerImpl caller;

    public ExecEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
        this(client, new ExecCallerImpl(apiDecl));
    }
    private ExecEndpointImpl(DatabaseClient client, ExecCallerImpl caller) {
        super(client, caller);
        this.caller = caller;
    }

    private ExecCallerImpl getCaller() {
        return this.caller;
    }

    @Override
    public void call() {
        call(newCallContext());
    }
    @Override
    @Deprecated
    public InputStream call(InputStream endpointState, SessionState session, InputStream workUnit) {
        CallContext callContext = newCallContext().withEndpointState(endpointState).withSessionState(session)
                .withWorkUnit(workUnit);
        call(callContext);
        return callContext.getEndpointState();
    }

    @Override
    @Deprecated
    public ExecEndpoint.BulkExecCaller bulkCaller() {
        return bulkCaller(newCallContext());
    }

    @Override
    public void call(CallContext callContext) {
        checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
        callContext.withEndpointState(getCaller().call(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                callContext.getWorkUnit()));
    }

    @Override
    public BulkExecCaller bulkCaller(CallContext callContext) {
        return new BulkExecCallerImpl(this, callContext);
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
            case 1: return new BulkExecCallerImpl(this, callContexts[0]);
            default: return new BulkExecCallerImpl(this, callContexts, threadCount);
        }
    }

    final static class BulkExecCallerImpl
            extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements ExecEndpoint.BulkExecCaller
    {
        private ExecEndpointImpl endpoint;
        private ErrorListener errorListener;
        private AtomicInteger aliveCallContextCount;

        private BulkExecCallerImpl(ExecEndpointImpl endpoint, CallContext callContext) {
            super(callContext);
            checkEndpoint(endpoint, "ExecEndpointImpl");
            this.endpoint = endpoint;
        }
        private BulkExecCallerImpl(ExecEndpointImpl endpoint, CallContext[] callContexts, int threadCount) {
            super(callContexts, threadCount, threadCount);
            this.endpoint = endpoint;
            this.aliveCallContextCount = new AtomicInteger(threadCount);
        }

        private ExecEndpointImpl getEndpoint() {
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
                        BulkCallableImpl bulkCallableImpl = new BulkCallableImpl(this);
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

        private ExecEndpoint.BulkExecCaller.ErrorListener getErrorListener() {
            return this.errorListener;
        }

        private InputStream processExec(CallContextImpl callContext) {
            final int DEFAULT_MAX_RETRIES = 10;
            ErrorDisposition error = ErrorDisposition.RETRY;
            InputStream output = null;

            for (int retryCount = 0; retryCount < DEFAULT_MAX_RETRIES && error == ErrorDisposition.RETRY; retryCount++) {
                Throwable throwable = null;
                try {
                    logger.trace("exec calling endpoint={} count={} state={}",
                            callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getEndpointState());
                    // TODO: use byte[] for IO internally (and InputStream externally)
                    output = getEndpoint().getCaller().call(
                            callContext.getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                            callContext.getWorkUnit()
                    );
                    if (callContext.getEndpoint().allowsEndpointState()) {
                        callContext.withEndpointState(output);
                    }
                    incrementCallCount();
                    return output;
                } catch(Throwable catchedThrowable) {
                    // TODO: logging
                    throwable = catchedThrowable;
                }
                // TODO -- retry with new session if times out

                if (throwable != null) {
                    if (getErrorListener() == null) {
                        logger.error("Error while calling " + getEndpoint().getEndpointPath(), throwable);
                        throw new RuntimeException("Error while calling " + getEndpoint().getEndpointPath(), throwable);
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
                                    callContext.withEndpointState((InputStream) null);
                                }
                                return output;

                            case STOP_ALL_CALLS:
                                getCallerThreadPoolExecutor().shutdown();
                        }
                    }
                }
            }
            return output;
        }

        private void processOutput() {
            CallContextImpl callContext = getCallContext();
            if(callContext != null) {
                while (processOutput(callContext));
            }
        }

        private boolean processOutput(CallContextImpl callContext){
            InputStream output = processExec(callContext);

            switch(getPhase()) {
                case INTERRUPTING:
                    setPhase(WorkPhase.INTERRUPTED);
                    logger.info("exec interrupted endpoint={} count={} work={}",
                            callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getWorkUnit());
                    return false;
                case RUNNING:
                    if (output == null ) {
                        if(getCallerThreadPoolExecutor() == null || getCallerThreadPoolExecutor().getActiveCount() <= 1)
                            setPhase(WorkPhase.COMPLETED);
                        logger.info("exec completed endpoint={} count={} work={}",
                                callContext.getEndpoint().getEndpointPath(), getCallCount(), callContext.getWorkUnit());
                        return false;
                    }
                    return true;
                default:
                    throw new MarkLogicInternalException(
                            "unexpected state for "+callContext.getEndpoint().getEndpointPath()+" during loop: "+getPhase().name()
                    );
            }
        }

        private class BulkCallableImpl implements Callable<Boolean> {
            private BulkExecCallerImpl bulkExecCallerImpl;
            private Boolean continueCalling = true;

            BulkCallableImpl(BulkExecCallerImpl bulkExecCallerImpl) {
                this.bulkExecCallerImpl = bulkExecCallerImpl;
            }

            @Override
            public Boolean call() throws InterruptedException{
                CallContext callContext = bulkExecCallerImpl.getCallContextQueue().poll();

                continueCalling = (callContext == null)? false:bulkExecCallerImpl.processOutput((CallContextImpl) callContext);

                if(continueCalling) {
                    bulkExecCallerImpl.getCallContextQueue().put(callContext);
                    submitTask(this);
                }
                else {
                    aliveCallContextCount.decrementAndGet();
                    if (aliveCallContextCount.get() == 0) {
                        getCallerThreadPoolExecutor().shutdown();
                    }
                }

                return true;
            }
        }
    }
}
