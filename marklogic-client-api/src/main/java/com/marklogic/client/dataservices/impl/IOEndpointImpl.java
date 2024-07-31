/*
 * Copyright (c) 2022 MarkLogic Corporation
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

abstract class IOEndpointImpl<I,O> implements IOEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(IOEndpointImpl.class);

    final static int DEFAULT_MAX_RETRIES  = 100;
    final static int DEFAULT_BATCH_SIZE   = 100;

    private final DatabaseClient    client;
    private final IOCallerImpl<I,O> caller;

    public IOEndpointImpl(DatabaseClient client, IOCallerImpl<I,O> caller) {
        if (client == null)
            throw new IllegalArgumentException("null client");
        if (caller == null)
            throw new IllegalArgumentException("null caller");
        this.client = client;
        this.caller = caller;
    }

    int initBatchSize(IOCallerImpl<I,O> caller) {
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
    private IOCallerImpl<I,O> getCaller() {
        return this.caller;
    }

    @Override
    public String getEndpointPath() {
        return getCaller().getEndpointPath();
    }
    @Override
    public boolean allowsEndpointState() {
        return (getEndpointStateParamdef() != null);
    }
    BaseCallerImpl.ParamdefImpl getEndpointStateParamdef() {
        return getCaller().getEndpointStateParamdef();
    }
    @Override
    public boolean allowsEndpointConstants() {
        return (getEndpointConstantsParamdef() != null);
    }
    BaseCallerImpl.ParamdefImpl getEndpointConstantsParamdef() {
        return getCaller().getEndpointConstantsParamdef();
    }
    @Override
    public boolean allowsInput() {
        return (getInputParamdef() != null);
    }
    BaseCallerImpl.ParamdefImpl getInputParamdef() {
        return getCaller().getInputParamdef();
    }
    @Override
    public boolean allowsSession() {
        return (getSessionParamdef() != null);
    }
    BaseCallerImpl.ParamdefImpl getSessionParamdef() {
        return getCaller().getSessionParamdef();
    }

    @Override
    public SessionState newSessionState() {
        if (!allowsEndpointState())
            throw new IllegalStateException("endpoint does not support session state");
        return getCaller().newSessionState();
    }
    @Override
    public CallContextImpl<I,O> newCallContext(){
        return newCallContext(false);
    }
    CallContextImpl<I,O> newCallContext(boolean legacyContext){
        return new CallContextImpl<>(this, legacyContext);
    }

    CallContextImpl<I,O>[] checkAllowedArgs(IOEndpoint.CallContext[] callCtxts) {
        if (callCtxts == null || callCtxts.length ==0)
            throw new IllegalArgumentException("null or empty contexts for call");
        CallContextImpl<I,O>[] contexts = new CallContextImpl[callCtxts.length];
        for (int i=0; i < callCtxts.length; i++) {
            contexts[i] = checkAllowedArgs(callCtxts[i]);
        }
        return contexts;
    }
    CallContextImpl<I,O> checkAllowedArgs(CallContext callCtxt) {
        if (!(callCtxt instanceof CallContextImpl)) {
            throw new IllegalArgumentException("Unknown implementation of call context");
        }
        CallContextImpl<I,O> context = (CallContextImpl<I,O>) callCtxt;
        if (context.getEndpointState() != null && !allowsEndpointState())
            throw new IllegalArgumentException("endpoint does not accept endpointState parameter");
        if (context.getSessionState() != null && !allowsSession())
            throw new IllegalArgumentException("endpoint does not accept session parameter");
        if (context.getEndpointConstants() != null && !allowsEndpointConstants())
            throw new IllegalArgumentException(
                    "endpoint does not accept "+context.getEndpointConstantsParamName()+" parameter");
        return context;
    }

    static abstract class BulkIOEndpointCallerImpl<I,O> implements IOEndpoint.BulkIOEndpointCaller {
        enum WorkPhase {
            INITIALIZING, RUNNING, INTERRUPTING, INTERRUPTED, COMPLETED
        }

        private final IOEndpointImpl<I,O> endpoint;
        private WorkPhase phase = WorkPhase.INITIALIZING;

        private CallContextImpl<I,O> callContext;

        private CallerThreadPoolExecutor<I,O> callerThreadPoolExecutor;
        private LinkedBlockingQueue<CallContextImpl<I,O>> callContextQueue;
        private int threadCount;

        private long callCount = 0;

        // constructor for calling in the application thread
        BulkIOEndpointCallerImpl(IOEndpointImpl<I,O> endpoint, CallContextImpl<I,O> callContext) {
            this.endpoint = endpoint;
            this.callContext = callContext;
// TODO: should only create a session ID if needed
            getSession();
        }
        // constructor for concurrent calling in multiple worker threads
        BulkIOEndpointCallerImpl(IOEndpointImpl<I,O> endpoint, CallContextImpl<I,O>[] callContexts, int threadCount, int queueSize) {
            this.endpoint = endpoint;
            this.callerThreadPoolExecutor = new CallerThreadPoolExecutor<>(threadCount, queueSize, this);
            this.callContextQueue = new LinkedBlockingQueue<>(Arrays.asList(callContexts));
            this.threadCount = threadCount;
        }
        private void init(IOEndpointImpl<I,O> endpoint, int threadCount, int queueSize) {
        }

        long getCallCount() {
            return callCount;
        }
        void incrementCallCount() {
            callCount++;
        }
        CallContextImpl<I,O> getCallContext() {
            return this.callContext;
        }
        CallerThreadPoolExecutor<I,O> getCallerThreadPoolExecutor() {
            return this.callerThreadPoolExecutor;
        }
        LinkedBlockingQueue<CallContextImpl<I,O>> getCallContextQueue() {
            return this.callContextQueue;
        }
        int getThreadCount(){
            return this.threadCount;
        }

        boolean allowsEndpointState() {
            return callContext.getEndpoint().allowsEndpointState();
        }
        boolean allowsEndpointConstants() {
            checkCallContext();
            return callContext.getEndpoint().allowsEndpointConstants();
        }

		boolean allowsSession() {
            return callContext.getEndpoint().allowsSession();
        }
        SessionState getSession() {
            if (!allowsSession())
                return null;
            checkCallContext();
            if (callContext.getSessionState() == null) {
                // no need to refresh the session id preemptively before timeout
                // because a timed-out session id is merely a new session id
                callContext.withSessionState(callContext.getEndpoint().getCaller().newSessionState());
            }
            return callContext.getSessionState();
        }
        boolean allowsInput() {
            return callContext.getEndpoint().allowsInput();
        }

        boolean queueInput(I input, BlockingQueue<I> queue, int batchSize) {
            if (input == null) return false;
            try {
                queue.put(input);
            } catch (InterruptedException e) {
                throw new IllegalStateException("InputStream was not added to the queue." + e.getMessage());
            }
            return checkQueue(queue, batchSize);
        }
        boolean queueAllInput(I[] input, BlockingQueue<I> queue, int batchSize) {
            if (input == null || input.length == 0) return false;
            try {
                for (I item: input) {
                    queue.put(item);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("InputStream was not added to the queue." + e.getMessage());
            }
            return checkQueue(queue, batchSize);
        }
        boolean checkQueue(BlockingQueue<I> queue, int batchSize) {
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
                        "can only accept input when initializing or running and not when input is "+
                        getPhase().name().toLowerCase());
                default:
                    throw new MarkLogicInternalException(
                            "unexpected state for " + callContext.getEndpoint().getEndpointPath() + " during loop: " + getPhase().name());
            }

            return true;
        }
        I[] getInputBatch(BlockingQueue<I> queue, int batchSize) {
            List<I> inputStreamList = new ArrayList<>();
            queue.drainTo(inputStreamList, batchSize);
            return inputStreamList.toArray(endpoint.getCaller().newContentInputArray(inputStreamList.size()));
        }
        void processOutputBatch(O[] output, Consumer<O> outputListener) {
            if (output == null || output.length == 0) return;

            for (O value: output) {
                outputListener.accept(value);
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

        private void checkCallContext() {
            if(this.callContext == null)
                throw new InternalError("Can only call set and get methods for call state when using a single CallContext.");
        }

        void submitTask(Callable<Boolean> callable) throws RejectedExecutionException{
            FutureTask<Boolean> futureTask = new FutureTask<>(callable);
            getCallerThreadPoolExecutor().execute(futureTask);
        }

        void checkEndpoint(IOEndpointImpl<I,O> endpoint, String endpointType) {
            if(getCallContext().getEndpoint() != endpoint)
                throw new IllegalArgumentException("Endpoint must be "+endpointType);
        }

        static class CallerThreadPoolExecutor<I,O> extends ThreadPoolExecutor {

            private Boolean awaitingTermination;
            private final BulkIOEndpointCallerImpl<I,O> bulkIOEndpointCaller;
            CallerThreadPoolExecutor(int threadCount, int queueSize, BulkIOEndpointCallerImpl<I,O> bulkIOEndpointCaller) {

                super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(queueSize), new CallerRunsPolicy());
                this.bulkIOEndpointCaller = bulkIOEndpointCaller;
            }

            Boolean isAwaitingTermination() {
                return this.awaitingTermination;
            }
            synchronized void awaitTermination() throws InterruptedException {
                if (bulkIOEndpointCaller.getCallContextQueue().isEmpty() && getActiveCount()<=1) {
                    shutdown();
                }
                else {
                    awaitingTermination = true;
                    awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                }
            }
        }
    }
}
