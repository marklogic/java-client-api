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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    public Stream<InputStream> call(InputStream workUnit, Stream<InputStream> input) {
        return getCaller().call(getClient(), null, null, workUnit, input);
    }

    @Override
    public BulkInputOutputCaller bulkCaller() {
        return new BulkInputOutputCallerImpl(this, getBatchSize());
    }

    final static class BulkInputOutputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements InputOutputEndpoint.BulkInputOutputCaller {

        private InputOutputEndpointImpl endpoint;
        private int batchSize;
        private LinkedBlockingQueue<InputStream> queue;
        private Consumer<InputStream> outputConsumer;

        BulkInputOutputCallerImpl(InputOutputEndpointImpl endpoint, int batchSize) {
            super(endpoint);
            this.endpoint = endpoint;
            this.batchSize = batchSize;
            this.queue = new LinkedBlockingQueue<InputStream>();
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
        private Consumer<InputStream> getOutputConsumer() {
            return outputConsumer;
        }

        @Override
        public void forEachOutput(Consumer<InputStream> outputConsumer) {
            this.outputConsumer = outputConsumer;
        }

        @Override
        public void accept(InputStream input) {
            if (getOutputConsumer() == null)
                throw new IllegalStateException("Output consumer is null");

            boolean hasBatch = queueInput(input, getQueue(), getBatchSize());
            if (hasBatch)
                processInput();
        }
        private void processInput() {
            logger.trace("input endpoint running endpoint={} count={} state={}", getEndpointPath(), getCallCount(),
                    getEndpointState());

            Stream<InputStream> output = null;
            try {
                output = getEndpoint().getCaller().call(
                        getClient(), getEndpointState(), getSession(), getWorkUnit(), getInputBatch(getQueue(), getBatchSize())
                );
            } catch(Throwable throwable) {
                throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
            }

            incrementCallCount();

            processOutputBatch(output, getOutputConsumer());
        }

        @Override
        public void awaitCompletion() {
            if (getQueue() == null)
                return;

            while (!getQueue().isEmpty()) {
                processInput();
            }
        }
    }
}
