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
import java.util.concurrent.TimeUnit;

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
        return call(callContext);
    }

    @Override
    @Deprecated
    public ExecEndpoint.BulkExecCaller bulkCaller() {
        return bulkCaller(newCallContext());
    }

    @Override
    public InputStream call(CallContext callContext) {
        checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
        return getCaller().call(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                callContext.getWorkUnit());
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
            this.endpoint = endpoint;
        }
        private BulkExecCallerImpl(ExecEndpointImpl endpoint, CallContext[] callContexts, int threadCount) {
            super(callContexts, threadCount, threadCount);
            this.endpoint = endpoint;
        }

        private ExecEndpointImpl getEndpoint() {
            return this.endpoint;
        }

        @Override
        public void awaitCompletion() {
            setPhase(WorkPhase.RUNNING);
            logger.trace("exec running endpoint={} work={}", getEndpointPath(), getCallContext().getWorkUnit());

            if(getThreadCount() == 1 && getCallContexts().size()==1)
                processOutput();
            else if(getThreadCount() == 1 && getCallContexts().size()>1) {
                while(!getCallContexts().isEmpty()) {
                    processOutput();
                }
            }
            else {
                try {
                    for (int i = 0; i < getThreadCount(); i++) {
                        BulkCallableImpl bulkCallableImpl = new BulkCallableImpl(this);
                        submitTask(bulkCallableImpl);
                    }
                    getCallerThreadPoolExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                }
                catch(Throwable throwable) {
                    throw new RuntimeException("Error occurred while awaiting termination ", throwable);
                }
            }
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        private void processOutput() {
            CallContext callContext = getCallContexts().poll();
            if(callContext != null) {
                calling:while (true){
                    if(!processOutput(callContext))
                        break calling;
                }
            }
        }

        private Boolean processOutput(CallContext callContext){
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
            BulkCallableImpl(BulkExecCallerImpl bulkExecCallerImpl) {
                this.bulkExecCallerImpl = bulkExecCallerImpl;
            }

            @Override
            public Boolean call() throws InterruptedException{
                CallContext callContext = bulkExecCallerImpl.getCallContexts().poll();
                if(callContext!=null)
                    bulkExecCallerImpl.processOutput(callContext);

                if(getPhase() == WorkPhase.COMPLETED && bulkExecCallerImpl.getCallContexts().isEmpty()) {
                    getCallerThreadPoolExecutor().shutdown();
                }
                else if(callContext != null) {
                    bulkExecCallerImpl.getCallContexts().put(callContext);
                    submitTask(this);
                }
              return true;
            }
        }
    }
}
