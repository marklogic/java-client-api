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

        private BulkExecCallerImpl(ExecEndpointImpl endpoint, CallContext callContext) {
            super(callContext);
            checkEndpoint(endpoint, "ExecEndpointImpl");
            this.endpoint = endpoint;
        }
        private BulkExecCallerImpl(ExecEndpointImpl endpoint, CallContext[] callContexts, int threadCount) {
            super(callContexts, threadCount, threadCount);
            checkEndpoint(endpoint, "ExecEndpointImpl");
            this.endpoint = endpoint;
        }

        private ExecEndpointImpl getEndpoint() {
            return this.endpoint;
        }

        @Override
        public void awaitCompletion() {
            setPhase(WorkPhase.RUNNING);
            logger.trace("exec running endpoint={} work={}", getEndpointPath(), getCallContext().getWorkUnit());

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

        private void processOutput() {
            CallContext callContext = getCallContext();
            if(callContext != null) {
                while (processOutput(callContext));
            }
        }

        private boolean processOutput(CallContext callContext){
                InputStream output = null;
                try {
                    logger.trace("exec calling endpoint={} count={} state={}",
                            getEndpointPath(), getCallCount(), callContext.getEndpointState());
// TODO: use byte[] for IO internally (and InputStream externally)
                    output = getEndpoint().getCaller().call(
                            getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                            callContext.getWorkUnit()
                    );

                    incrementCallCount();
                } catch(Throwable throwable) {
                    // TODO: logging
                    throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
                }
// TODO -- retry with new session if times out

                if (allowsEndpointState()) {
                    callContext.withEndpointState(output);
                }

                switch(getPhase()) {
                    case INTERRUPTING:
                        setPhase(WorkPhase.INTERRUPTED);
                        logger.info("exec interrupted endpoint={} count={} work={}",
                                getEndpointPath(), getCallCount(), callContext.getWorkUnit());
                        return false;
                    case RUNNING:
                        if (output == null) {
                            setPhase(WorkPhase.COMPLETED);
                            logger.info("exec completed endpoint={} count={} work={}",
                                    getEndpointPath(), getCallCount(), callContext.getWorkUnit());
                            return false;
                        }
                        return true;
                    default:
                        throw new MarkLogicInternalException(
                                "unexpected state for "+getEndpointPath()+" during loop: "+getPhase().name()
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

                continueCalling = (callContext == null)? false:bulkExecCallerImpl.processOutput(callContext);

                if(continueCalling) {
                    bulkExecCallerImpl.getCallContextQueue().put(callContext);
                    submitTask(this);
                }
                else if(bulkExecCallerImpl.getCallContextQueue().isEmpty() &&
                        getCallerThreadPoolExecutor().getActiveCount() == 0) {
                    getCallerThreadPoolExecutor().shutdown();
                }

                return true;
            }
        }
    }
}
