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
        return null;
    }

    @Override
    public BulkInputOutputCaller bulkCaller(CallContext[] callContexts, int threadCount) {
        return null;
    }

    @Override
    public CallContext newCallContext() {
        return new CallContextImpl(this);
    }

    final static class BulkInputOutputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements InputOutputEndpoint.BulkInputOutputCaller {

        private InputOutputEndpointImpl endpoint;
        private int batchSize;
        private LinkedBlockingQueue<InputStream> queue;
        private Consumer<InputStream> outputListener;
        private ErrorListener errorListener;
        private CallContext callContext;

        BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize, CallContext callContext) {
            super(endpoint);
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.queue = new LinkedBlockingQueue<InputStream>();
            this.callContext = callContext;
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

        static class ErrorListenerImpl implements BulkInputOutputCaller.ErrorListener {

            @Override
            public BulkIOEndpointCaller.ErrorDisposition processError(int retryCount, Throwable throwable, CallContext callContext, InputStream[] input) {
                return null;
            }
        }
    }
}
