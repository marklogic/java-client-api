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
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.InputOutputEndpoint;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
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
        try {
            return getResponseData(callContext, input, true);
        } catch(Exception ex) {
            throw new InternalError("Exception occurred while fetching data");
        }
    }

    @Override
    @Deprecated
    public InputOutputEndpoint.BulkInputOutputCaller bulkCaller() {
        return bulkCaller(newCallContext());
    }

    @Override
    public InputStream[] call(CallContext callContext, InputStream[] input) {
        try {
            return getResponseData(callContext, input, false);
        } catch(Exception ex) {
            throw new InternalError("Exception occurred while fetching data");
        }
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext callContext) {
        return new BulkInputOutputCallerImpl(this, getBatchSize(), callContext);
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext[] callContexts) {
        if(callContexts == null || callContexts.length == 0)
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

    private InputStream[] getResponseData(CallContext callContext, InputStream[] input, boolean withEndpointState) throws IOException {

        checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
        InputStream[] values = getCaller().arrayCall(getClient(), callContext.getEndpointState(),
                callContext.getSessionState(), callContext.getWorkUnit(), input);
        ByteArrayOutputStream endpointState = new ByteArrayOutputStream();
        // values[0].transferTo(endpointState);
        Utilities.write(values[0], endpointState);
        callContext.withEndpointState(values[0]);

        if(withEndpointState) {
            values[0] = new ByteArrayInputStream(endpointState.toByteArray());
            return values;
        }
        InputStream[] outputValues = Arrays.copyOfRange(values, 1, values.length);
        return outputValues;
    }

    final static class BulkInputOutputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements InputOutputEndpoint.BulkInputOutputCaller {

        private InputOutputEndpointImpl endpoint;
        private int batchSize;
        private LinkedBlockingQueue<InputStream> inputQueue;
        private Consumer<InputStream> outputListener;
        private ErrorListener errorListener;

        BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize, CallContext callContext) {
            super(callContext);
            checkEndpoint(endpoint, "InputOutputEndpointImpl");
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.inputQueue = new LinkedBlockingQueue<>();
        }

        private BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize, CallContext[] callContexts,
                                          int threadCount) {
            super(callContexts, threadCount, (2*callContexts.length));
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.inputQueue = new LinkedBlockingQueue<>();
        }

        private InputOutputEndpointImpl getEndpoint() {
            return endpoint;
        }
        private int getBatchSize() {
            return batchSize;
        }
        private LinkedBlockingQueue<InputStream> getInputQueue() {
            return inputQueue;
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

            boolean hasBatch = queueInput(input, getInputQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }
        @Override
        public void acceptAll(InputStream[] input) {
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

        private void processInput() {
            InputStream[] inputBatch = getInputBatch(getInputQueue(), getBatchSize());
            if(getCallContext()!=null)
                processInput(getCallContext(), inputBatch);
                // TODO : optimize the case of a single thread with a callContextQueue.
            else if(getCallContextQueue() != null ){
                    BulkCallableImpl bulkCallableImpl = new BulkCallableImpl(this, inputBatch);
                    submitTask(bulkCallableImpl);
            }
            else {
                throw new IllegalArgumentException("Cannot process input without Callcontext.");
            }
        }

        private void processInput(CallContextImpl callContext, InputStream[] inputBatch) {
            logger.trace("input endpoint running endpoint={} count={} state={}", (callContext).getEndpoint().getEndpointPath(), getCallCount(),
                    callContext.getEndpointState());

            InputStream[] output = null;
            try {
                output = getEndpoint().getCaller().arrayCall(
                        callContext.getClient(), callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit(),
                        inputBatch
                );


            incrementCallCount();
            if (callContext.getEndpoint().allowsEndpointState()) {
                callContext.withEndpointState(output[0]);
            }
            output = Arrays.copyOfRange(output, 1, output.length);
            processOutputBatch(output, getOutputListener());
            } catch(Throwable throwable) {
                throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
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

                if(getCallerThreadPoolExecutor() != null)
                    getCallerThreadPoolExecutor().awaitTermination();
            } catch (Throwable throwable) {
                throw new RuntimeException("Error occurred while awaiting termination "+throwable.getMessage());
            }
        }

        private class BulkCallableImpl implements Callable<Boolean> {
            private BulkInputOutputCallerImpl bulkInputOutputCallerImpl;
            private InputStream[] inputBatch;
            BulkCallableImpl(BulkInputOutputCallerImpl BulkInputOutputCallerImpl, InputStream[] inputBatch) {
                this.bulkInputOutputCallerImpl = BulkInputOutputCallerImpl;
                this.inputBatch = inputBatch;
            }
            @Override
            public Boolean call() {
                try {
                    CallContext callContext = bulkInputOutputCallerImpl.getCallContextQueue().poll();

                    if(callContext != null) {
                        bulkInputOutputCallerImpl.processInput((CallContextImpl) callContext, inputBatch);
                        bulkInputOutputCallerImpl.getCallContextQueue().put(callContext);
                    }
                    if(getCallerThreadPoolExecutor().isAwaitingTermination() && getInputQueue().isEmpty()) {
                        getCallerThreadPoolExecutor().shutdown();
                    }
                } catch(Exception ex) {
                    throw new InternalError("Error occurred while processing CallContext - "+ex.getMessage());
                }
                return true;
            }
        }
    }
}
