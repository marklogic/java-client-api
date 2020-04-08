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

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.BufferableHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

abstract class IOEndpointImpl implements IOEndpoint {
    private static Logger logger = LoggerFactory.getLogger(IOEndpointImpl.class);

    final static int DEFAULT_BATCH_SIZE = 100;

    private DatabaseClient client;
    private IOCallerImpl   caller;
    private CallContext callContext;
    private int threadCount;

    public CallContext getCallContext() {
        return callContext;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setCallContext(CallContext callContext) {
        this.callContext = callContext;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public IOEndpointImpl(DatabaseClient client, IOCallerImpl caller) {
        if (client == null)
            throw new IllegalArgumentException("null client");
        if (caller == null)
            throw new IllegalArgumentException("null caller");
        this.client = client;
        this.caller = caller;
        setCallContext(new CallContextImpl());
    }

    int initBatchSize(IOCallerImpl caller) {
        JsonNode apiDeclaration = caller.getApiDeclaration();
        if (apiDeclaration.has("$bulk") && apiDeclaration.get("$bulk").isObject()
                && apiDeclaration.get("$bulk").has("inputBatchSize")
                && apiDeclaration.get("$bulk").get("inputBatchSize").isInt()) {
            return apiDeclaration.get("$bulk").get("inputBatchSize").asInt();
        }
        return DEFAULT_BATCH_SIZE;
    }

    DatabaseClient getClient() {
        return this.client;
    }
    private IOCallerImpl getCaller() {
        return this.caller;
    }

    @Override
    public String getEndpointPath() {
        return getCaller().getEndpointPath();
    }
    @Override
    public boolean allowsEndpointState() {
        return (getCaller().getEndpointStateParamdef() != null);
    }
    @Override
    public boolean allowsWorkUnit() {
        return (getCaller().getWorkUnitParamdef() != null);
    }
    @Override
    public boolean allowsInput() {
        return (getCaller().getInputParamdef() != null);
    }
    @Override
    public boolean allowsSession() {
        return (getCaller().getSessionParamdef() != null);
    }

    @Override
    public SessionState newSessionState() {
        if (!allowsEndpointState())
            throw new IllegalStateException("endpoint does not support session state");
        return getCaller().newSessionState();
    }
    @Override
    public CallContext newCallContext(){
        return new CallContextImpl();
    }

    public void checkAllowedArgs(InputStream endpointState, SessionState session, InputStream workUnit) {
        if (endpointState != null && !allowsEndpointState())
            throw new IllegalArgumentException("endpoint does not accept endpoint state");
        if (session != null && !allowsSession())
            throw new IllegalArgumentException("endpoint does not accept session");
        if (workUnit != null && !allowsWorkUnit())
            throw new IllegalArgumentException("endpoint does not accept work unit");
    }

    static abstract class BulkIOEndpointCallerImpl implements IOEndpoint.BulkIOEndpointCaller {
        enum WorkPhase {
            INITIALIZING, RUNNING, INTERRUPTING, INTERRUPTED, COMPLETED;
        }

        private WorkPhase phase = WorkPhase.INITIALIZING;
        private SessionState   session;
        private CallContext callContext;
        private long           callCount = 0;
        private int threadCount;

        private CallerThreadPoolExecutor callerThreadPoolExecutor;
        private LinkedBlockingQueue<CallContext> callContextQueue;

        BulkIOEndpointCallerImpl(CallContext callContext) {
            this.callContext = callContext;
        }

        BulkIOEndpointCallerImpl(CallContext[] callContexts, int threadCount, int queueSize) {
            this.callerThreadPoolExecutor = new CallerThreadPoolExecutor(threadCount, queueSize, this);
            this.callContextQueue = new LinkedBlockingQueue<>(Arrays.asList(callContexts));
            this.threadCount = threadCount;
        }

        private IOEndpointImpl getEndpoint() {
            return getEndpoint();
        }

        String getEndpointPath() {
            return getEndpoint().getEndpointPath();
        }
        long getCallCount() {
            return callCount;
        }
        void incrementCallCount() {
            callCount++;
        }
        CallContext getCallContext() {
            return this.callContext;
        }
        CallerThreadPoolExecutor getCallerThreadPoolExecutor() {
            return this.callerThreadPoolExecutor;
        }
        LinkedBlockingQueue<CallContext> getCallContextQueue() {
            return this.callContextQueue;
        }
        int getThreadCount(){
            return this.threadCount;
        }

        boolean allowsEndpointState() {
            return getEndpoint().allowsEndpointState();
        }
        @Override
        @Deprecated
        public InputStream getEndpointState() {
            checkCallContext();
            return callContext.getEndpointState();
        }
        @Override
        @Deprecated
        public void setEndpointState(byte[] endpointState) {
            checkCallContext();
            if (allowsEndpointState())
                callContext.withEndpointState(endpointState);
            else if (endpointState != null)
                throw new IllegalArgumentException("endpoint state not accepted by endpoint: "+ getEndpointPath());
        }
        @Override
        @Deprecated
        public void setEndpointState(InputStream endpointState) {
            checkCallContext();
            callContext.withEndpointState(NodeConverter.InputStreamToBytes(endpointState));
        }
        @Override
        @Deprecated
        public void setEndpointState(BufferableHandle endpointState) {
            checkCallContext();
            callContext.withEndpointState((endpointState == null) ? null : endpointState.toBuffer());
        }

        boolean allowsWorkUnit() {
            return getEndpoint().allowsWorkUnit();
        }

        @Override
        @Deprecated
        public InputStream getWorkUnit() {
            checkCallContext();
            return callContext.getWorkUnit();
        }
        @Override
        @Deprecated
        public void setWorkUnit(byte[] workUnit) {
            checkCallContext();
            if (allowsWorkUnit())
                callContext.withWorkUnit(workUnit);
            else if (workUnit != null)
                throw new IllegalArgumentException("work unit not accepted by endpoint: "+ getEndpointPath());
        }
        @Override
        @Deprecated
        public void setWorkUnit(InputStream workUnit) {
            checkCallContext();
            callContext.withWorkUnit(NodeConverter.InputStreamToBytes(workUnit));
        }
        @Override
        @Deprecated
        public void setWorkUnit(BufferableHandle workUnit) {
            checkCallContext();
            callContext.withWorkUnit((workUnit == null) ? null : workUnit.toBuffer());
        }

        DatabaseClient getClient() {
            return getEndpoint().getClient();
        }
        boolean allowsSession() {
            return getEndpoint().allowsSession();
        }
        SessionState getSession() {
            if (!allowsSession())
                return null;

            if (session == null) {
                // no need to refresh the session id preemptively before timeout
                // because a timed-out session id is merely a new session id
                session = getEndpoint().getCaller().newSessionState();
            }
            return session;
        }
        boolean allowsInput() {
            return getEndpoint().allowsInput();
        }

        boolean queueInput(InputStream input, BlockingQueue<InputStream> queue, int batchSize) {
            if (input == null) return false;
            try {
                queue.put(input);
            } catch (InterruptedException e) {
                throw new IllegalStateException("InputStream was not added to the queue." + e.getMessage());
            }
            return checkQueue(queue, batchSize);
        }
        boolean queueAllInput(InputStream[] input, BlockingQueue<InputStream> queue, int batchSize) {
            if (input == null || input.length == 0) return false;
            try {
                for (InputStream item: input) {
                    queue.put(item);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("InputStream was not added to the queue." + e.getMessage());
            }
            return checkQueue(queue, batchSize);
        }
        boolean checkQueue(BlockingQueue<InputStream> queue, int batchSize) {
            if ((queue.size() % batchSize) > 0)
                return false;

            switch (getPhase()) {
                case INITIALIZING:
                    setPhase(WorkPhase.RUNNING);
                    break;
                case RUNNING:
                    break;
                case INTERRUPTING:
                case INTERRUPTED:
                case COMPLETED:
                    throw new IllegalStateException(
                            "cannot accept more input as current phase is  " + getPhase().name()
                    );
                default:
                    throw new MarkLogicInternalException(
                            "unexpected state for " + getEndpointPath() + " during loop: " + getPhase().name());
            }

            return true;
        }
        InputStream[] getInputBatch(BlockingQueue<InputStream> queue, int batchSize) {
            List<InputStream> inputStreamList = new ArrayList<InputStream>();
            queue.drainTo(inputStreamList, batchSize);
            return inputStreamList.toArray(new InputStream[inputStreamList.size()]);
        }
        void processOutputBatch(InputStream[] output, Consumer<InputStream> outputListener) {
            if (output == null || output.length == 0) return;

            assignEndpointState(output);

            for (int i=allowsEndpointState()?1:0; i < output.length; i++) {
                outputListener.accept(output[i]);
            }
        }

        WorkPhase getPhase() {
            return this.phase;
        }
        void setPhase(WorkPhase phase) {
            this.phase = phase;
        }

        @Override
        public void interrupt() {
            if (this.phase == WorkPhase.RUNNING)
                setPhase(WorkPhase.INTERRUPTING);
        }

        void assignEndpointState(InputStream[] output) {
            if (allowsEndpointState() && output.length > 0) {
                setEndpointState(output[0]);
            }
        }

        InputStream[] getOutput(InputStream[] output) {
            assignEndpointState(output);
            if(allowsEndpointState() && output.length>0) {
                 return Arrays.copyOfRange(output, 1, output.length);
            }
            return output;
        }

        private void checkCallContext() {
            if(this.callContext == null)
                throw new InternalError("Can only call methods with single callcontext.");
        }

        void submitTask(Callable<Boolean> callable) {
            FutureTask futureTask = new FutureTask(callable);
            getCallerThreadPoolExecutor().execute(futureTask);
        }


        static class CallerThreadPoolExecutor extends ThreadPoolExecutor {

            private Boolean awaitingTermination;
            private BulkIOEndpointCallerImpl bulkIOEndpointCaller;
            CallerThreadPoolExecutor(int threadCount, int queueSize, BulkIOEndpointCallerImpl bulkIOEndpointCaller) {

                super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(queueSize), new CallerRunsPolicy());
                this.bulkIOEndpointCaller = bulkIOEndpointCaller;
            }

            Boolean isAwaitingTermination() {
                return this.awaitingTermination;
            }
            synchronized void awaitTermination() throws InterruptedException {
                if(bulkIOEndpointCaller.getCallContextQueue().isEmpty())
                    shutdown();
                awaitingTermination = true;
                awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            }
        }
    }

    static class CallContextImpl implements IOEndpoint.CallContext {

        private IOEndpointImpl endpoint;
        private byte[]         endpointState;
        private byte[]         workUnit;
        private SessionState   session;

        public IOEndpointImpl getEndpoint() {
            return endpoint;
        }

        @Override
        public InputStream getEndpointState() {
            return (this.endpointState == null) ? null : new ByteArrayInputStream(this.endpointState);
        }

        @Override
        public CallContextImpl withEndpointState(byte[] endpointState) {
            this.endpointState = endpointState;
            return this;
        }

        @Override
        public CallContextImpl withEndpointState(InputStream endpointState) {
            return withEndpointState(NodeConverter.InputStreamToBytes(endpointState));
        }

        @Override
        public CallContextImpl withEndpointState(BufferableHandle endpointState) {
            return withEndpointState((endpointState == null) ? null : endpointState.toBuffer());
        }

        @Override
        public InputStream getWorkUnit() {
            return (this.workUnit == null) ? null : new ByteArrayInputStream(this.workUnit);
        }

        @Override
        public CallContextImpl withWorkUnit(byte[] workUnit) {
            this.workUnit = workUnit;
            return this;
        }

        @Override
        public CallContextImpl withWorkUnit(InputStream workUnit) {
            return withWorkUnit(NodeConverter.InputStreamToBytes(workUnit));
        }

        @Override
        public CallContextImpl withWorkUnit(BufferableHandle workUnit) {
            return withWorkUnit((workUnit == null) ? null : workUnit.toBuffer());
        }

        @Override
        public SessionState getSessionState() {
            return this.session;
        }

        @Override
        public CallContextImpl withSessionState(SessionState sessionState) {
            this.session = sessionState;
            return this;
        }
    }
}
