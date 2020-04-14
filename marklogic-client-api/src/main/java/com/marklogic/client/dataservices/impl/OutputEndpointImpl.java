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
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class OutputEndpointImpl extends IOEndpointImpl implements OutputEndpoint {
    private static Logger logger = LoggerFactory.getLogger(OutputEndpointImpl.class);
    private OutputCallerImpl caller;
    private InputStream[] values;

    public OutputEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
        this(client, new OutputCallerImpl(apiDecl));
    }
    private OutputEndpointImpl(DatabaseClient client, OutputCallerImpl caller) {
        super(client, caller);
        this.caller = caller;
    }
    private OutputCallerImpl getCaller() {
        return this.caller;
    }


    @Override
    public InputStream[] call() {
        call(newCallContext());
        return this.values;
    }

    @Override
    @Deprecated
    public InputStream[] call(InputStream endpointState, SessionState session, InputStream workUnit) {
        CallContext callContext = newCallContext().withEndpointState(endpointState).withSessionState(session)
                .withWorkUnit(workUnit);
        call(callContext);
        return this.values;
    }

    @Override
    @Deprecated
    public OutputEndpoint.BulkOutputCaller bulkCaller() {
        return bulkCaller(newCallContext());
    }

    @Override
    public void call(CallContext callContext) {
        try {
            checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
            this.values = getCaller().arrayCall(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                    callContext.getWorkUnit());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            values[0].transferTo(baos);
            callContext.withEndpointState(baos.toByteArray());
            values[0] = new ByteArrayInputStream(baos.toByteArray());
        } catch(Exception ex) {
            throw new InternalError("Error occurred while fetching data.");
        }
    }

    @Override
    public BulkOutputCaller bulkCaller(CallContext callContext) {
        return new BulkOutputCallerImpl(this, callContext);
    }

    @Override
    public BulkOutputCaller bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length == 0)
            throw new IllegalArgumentException("CallContext cannot be null or empty");
        return bulkCaller(callContexts, callContexts.length);
    }

    @Override
    public BulkOutputCaller bulkCaller(CallContext[] callContexts, int threadCount) {
        if(callContexts == null)
            throw new IllegalArgumentException("CallContext cannot be null");
        if(threadCount > callContexts.length)
            throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

        switch(callContexts.length) {
            case 0: throw new IllegalArgumentException("CallContext cannot be empty");
            case 1: return new BulkOutputCallerImpl(this, callContexts[0]);
            default: return new BulkOutputCallerImpl(this, callContexts, threadCount);
        }
    }

    final static class BulkOutputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements OutputEndpoint.BulkOutputCaller {

        private OutputEndpointImpl endpoint;
        private Consumer<InputStream> outputListener;
        private CallContext callContext;
        private ErrorListener errorListener;

        private BulkOutputCallerImpl(OutputEndpointImpl endpoint, CallContext callContext) {
            super(callContext);
            checkEndpoint(endpoint, "OutputEndpointImpl");
            this.endpoint = endpoint;
            this.callContext = callContext;
        }
        private BulkOutputCallerImpl(OutputEndpointImpl endpoint, CallContext[] callContexts, int threadCount) {
            super(callContexts, threadCount, threadCount);
            checkEndpoint(endpoint, "OutputEndpointImpl");
            this.endpoint = endpoint;
        }

        private OutputEndpointImpl getEndpoint() {
            return endpoint;
        }
        private Consumer<InputStream> getOutputListener() {
            return outputListener;
        }

        @Override
        public void setOutputListener(Consumer<InputStream> listener) {
            this.outputListener = listener;
        }

        @Override
        public InputStream[] next() {
            if (getOutputListener() != null)
                throw new IllegalStateException("Cannot call next while current output consumer is not empty.");
            return getOutput(getOutputStream());
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        @Override
        public void awaitCompletion() {
            if (getOutputListener() == null)
                throw new IllegalStateException("Output consumer is null");

            logger.trace("output endpoint running endpoint={} work={}", getEndpointPath(), callContext.getWorkUnit());

            if(getPhase() != WorkPhase.INITIALIZING) {
                throw new IllegalStateException(
                        "Cannot process output since current phase is  " + getPhase().name());
            }

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
                    getCallerThreadPoolExecutor().awaitTermination();
                }
                catch(Throwable throwable) {
                    throw new RuntimeException("Error occurred while awaiting termination ", throwable);
                }
            } else {
                throw new IllegalArgumentException("Cannot process output without Callcontext.");
            }
        }

        private InputStream[] getOutputStream() {
            InputStream[] output;
            try {
                output = getEndpoint().getCaller().arrayCall(
                        getClient(), callContext.getEndpointState(), getSession(), callContext.getWorkUnit()
                );
            } catch(Throwable throwable) {
                throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
            }

            incrementCallCount();
            return output;
        }

        private void processOutput() {
            CallContext callContext = getCallContext();
            if(callContext != null) {
                while (processOutput(callContext));
            }
        }

        private boolean processOutput(CallContext callContext){
                logger.trace("output endpoint={} count={} state={}",
                        getEndpointPath(), getCallCount(), callContext.getEndpointState());

                InputStream[] output = getOutputStream();

                processOutputBatch(output, getOutputListener());

                switch(getPhase()) {
                    case INTERRUPTING:
                        setPhase(WorkPhase.INTERRUPTED);
                        logger.info("output interrupted endpoint={} count={} work={}",
                                getEndpointPath(), getCallCount(), getWorkUnit());
                        return false;
                    case RUNNING:
                        if (output == null || output.length == 0) {
                            setPhase(WorkPhase.COMPLETED);
                            logger.info("output completed endpoint={} count={} work={}",
                                    getEndpointPath(), getCallCount(), callContext.getWorkUnit());
                            return false;
                        }
                        return true;
                    case INTERRUPTED:
                    case COMPLETED:
                        throw new IllegalStateException(
                                "cannot process more output as current phase is  " + getPhase().name());
                    default:
                        throw new MarkLogicInternalException(
                                "unexpected state for "+getEndpointPath()+" during loop: "+getPhase().name()
                        );
                }

        }

        private class BulkCallableImpl implements Callable<Boolean> {
            private BulkOutputCallerImpl bulkOutputCallerImpl;
            private Boolean continueCalling = true;

            BulkCallableImpl(BulkOutputCallerImpl bulkOutputCallerImpl) {
                this.bulkOutputCallerImpl = bulkOutputCallerImpl;
            }
            @Override
            public Boolean call() throws InterruptedException {
                CallContext callContext = bulkOutputCallerImpl.getCallContextQueue().poll();

                continueCalling = (callContext == null)? false:bulkOutputCallerImpl.processOutput(callContext);
                if(continueCalling) {
                    bulkOutputCallerImpl.getCallContextQueue().put(callContext);
                    submitTask(this);
                }

                else if(bulkOutputCallerImpl.getCallContextQueue().isEmpty() &&
                        getCallerThreadPoolExecutor().getActiveCount() == 0) {
                    getCallerThreadPoolExecutor().shutdown();
                }
                return true;
            }
        }
    }
}
