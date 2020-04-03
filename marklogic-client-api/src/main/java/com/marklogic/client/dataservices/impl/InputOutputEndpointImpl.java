/*
 * Copyright 2019 MarkLogic Corporation
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
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.InputOutputEndpoint;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InputOutputEndpointImpl extends IOEndpointImpl implements InputOutputEndpoint {
    private static Logger logger = LoggerFactory.getLogger(InputOutputEndpointImpl.class);

    private InputOutputCallerImpl caller;
    private int batchSize;

    public InputOutputEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
        this(client, new InputOutputCallerImpl(apiDecl));
    }
    private InputOutputEndpointImpl(DatabaseClient client, InputOutputCallerImpl caller) {
        super(client, caller);
        this.caller = caller;
        this.batchSize = initBatchSize(caller);
    }

    private InputOutputCallerImpl getCaller() {
        return this.caller;
    }

    private int getBatchSize() {
        return this.batchSize;
    }

    @Override
    public InputStream[] call(InputStream[] input) {
        return call(newCallContext(), input);
    }

    @Override
    @Deprecated
    public InputStream[] call(InputStream endpointState, SessionState session, InputStream workUnit, InputStream[] input) {

        CallContext callContext = newCallContext().withEndpointState(endpointState).withSessionState(session)
                .withWorkUnit(workUnit);
        return call(callContext, input);
    }

    @Override
    @Deprecated
    public InputOutputEndpoint.BulkInputOutputCaller bulkCaller() {
        return bulkCaller(newCallContext());
    }

    @Override
    public InputStream[] call(CallContext callContext, InputStream[] input) {
        checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
        return getCaller().arrayCall(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
                callContext.getWorkUnit(), input);
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext callContext) {
        return new BulkInputOutputCallerImpl(this, getBatchSize(), callContext);
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length==0)
            throw new IllegalArgumentException("CallContext cannot be null or empty");
        return bulkCaller(callContexts, callContexts.length);
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext[] callContexts, int threadCount) {
        if(callContexts == null)
            throw new IllegalArgumentException("CallContext cannot be null");
        if(threadCount > callContexts.length)
            throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

        switch(callContexts.length) {
            case 0: throw new IllegalArgumentException("CallContext cannot be empty");
            case 1: return new BulkInputOutputCallerImpl(this, getBatchSize(), callContexts[0]);
            default: return new BulkInputOutputCallerImpl(this, getBatchSize(), callContexts, threadCount);
        }
    }

    final static class BulkInputOutputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements InputOutputEndpoint.BulkInputOutputCaller {

        private InputOutputEndpointImpl endpoint;
        private int batchSize;
        private LinkedBlockingQueue<InputStream> queue;
        private Consumer<InputStream> outputListener;
        private ErrorListener errorListener;
        private int threadCount;

        BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize, CallContext callContext) {
            super(endpoint, callContext);
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.queue = new LinkedBlockingQueue<>();
        }

        private BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize, CallContext[] callContexts,
                                          int threadCount) {
            super(endpoint, callContexts, threadCount, (2*callContexts.length));
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.queue = new LinkedBlockingQueue<>();
            this.threadCount = threadCount;
        }

        private InputOutputEndpointImpl getEndpoint() {
            return endpoint;
        }
        private int getBatchSize() {
            return batchSize;
        }
        private LinkedBlockingQueue<InputStream> getQueue() {
            return queue;
        }
        private Consumer<InputStream> getOutputListener() {
            return outputListener;
        }

        @Override
        public void setOutputListener(Consumer<InputStream> listener) {
            this.outputListener = listener;
        }

        @Override
        public void accept(InputStream input) {
            if (getOutputListener() == null)
                throw new IllegalStateException("Must configure output consumer before providing input");

            boolean hasBatch = queueInput(input, getQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }
        @Override
        public void acceptAll(InputStream[] input) {
            if (getOutputListener() == null)
                throw new IllegalStateException("Must configure output consumer before providing input");

            boolean hasBatch = queueAllInput(input, getQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }

        @Override
        public void setErrorListener(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        private void processInput() {
            if(threadCount == 1)
                processInput(getCallContext());
            else {
                for (int i = 0; i < threadCount; i++) {
                    BulkCallableImpl bulkCallableImpl = new BulkCallableImpl(this);
                    try {
                        bulkCallableImpl.submit(bulkCallableImpl);
                        getCallerThreadPoolExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                    } catch(Throwable throwable) {
                        throw new RuntimeException("Error occurred while awaiting Termination ", throwable);
                    }
                }
            }
        }

        private void processInput(CallContext callContext) {
            logger.trace("input endpoint running endpoint={} count={} state={}", getEndpointPath(), getCallCount(),
                    callContext.getEndpointState());

            InputStream[] output = null;
            try {
                output = getEndpoint().getCaller().arrayCall(
                        getClient(), callContext.getEndpointState(), getSession(), callContext.getWorkUnit(),
                        getInputBatch(getQueue(), getBatchSize())
                );
            } catch(Throwable throwable) {
                throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
            }

            incrementCallCount();

            processOutputBatch(output, getOutputListener());
        }

        @Override
        public void awaitCompletion() {
            if (getQueue() == null)
                return;

            while (!getQueue().isEmpty()) {
                processInput();
            }
        }

        private class BulkCallableImpl implements Callable<Boolean> {
            private BulkInputOutputCallerImpl bulkInputOutputCallerImpl;
            BulkCallableImpl(BulkInputOutputCallerImpl BulkInputOutputCallerImpl) {
                this.bulkInputOutputCallerImpl = BulkInputOutputCallerImpl;
            }
            @Override
            public Boolean call() {
                try {
                    CallContext callContext = bulkInputOutputCallerImpl.getCallContexts().poll();

                    bulkInputOutputCallerImpl.processInput(callContext);
                    if(getPhase() == WorkPhase.COMPLETED && bulkInputOutputCallerImpl.getCallContexts().isEmpty()) {
                        getCallerThreadPoolExecutor().shutdown();
                    } else {
                        bulkInputOutputCallerImpl.getCallContexts().put(callContext);
                    }
                } catch(Exception ex) {
                    logger.error("Error occurred while processing CallContext - "+ex.getMessage());
                    return false;
                }
                return true;
            }

            private void submit(BulkCallableImpl bulkCallableImpl) {
                FutureTask futureTask = new FutureTask(bulkCallableImpl);
                getCallerThreadPoolExecutor().execute(futureTask);
            }
        }
    }
}
