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
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.datamovement.impl.BatcherImpl;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallFailureListener;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallManager.CallArgs;
import com.marklogic.client.dataservices.CallSuccessListener;
import com.marklogic.client.dataservices.impl.CallManagerImpl.CallArgsImpl;

import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.impl.RESTServices.CallField;
import com.marklogic.client.impl.RESTServices.SingleAtomicCallField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CallBatcherImpl<W, E extends CallManager.CallEvent> extends BatcherImpl implements CallBatcher<W,E> {
    private static Logger logger = LoggerFactory.getLogger(CallBatcherImpl.class);

    private CallManagerImpl.CallerImpl<E> caller;
    private Class<W> inputType;
    private boolean isMultiple = false;
    private CallManagerImpl.ParamFieldifier<W> fieldifier;
    private JobTicket jobTicket;
    private CallingThreadPoolExecutor threadPool;
    private List<DatabaseClient> clients;
    private List<CallSuccessListener<E>> successListeners = new ArrayList<>();
    private List<CallFailureListener> failureListeners = new ArrayList<>();
    private LinkedBlockingQueue<W> queue;
    private AtomicLong callCount = new AtomicLong();
    private Calendar jobStartTime;
    private Calendar jobEndTime;
    private Set<RESTServices.CallField> defaultArgs;
    private CallArgsGenerator<E> callArgsGenerator;
    private String forestParamName;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    CallBatcherImpl(DatabaseClient client, CallManagerImpl.CallerImpl<E> caller, Class<W> inputType) {
        super(client.newDataMovementManager());
        if (caller == null) {
            throw new IllegalArgumentException("null caller");
        }
        if (inputType == null) {
            throw new IllegalArgumentException("null inputType");
        }
        this.caller = caller;
        this.inputType = inputType;
    }

    CallBatcherImpl(DatabaseClient client, CallManagerImpl.CallerImpl<E> caller, Class<W> inputType,
                    String paramName, CallManagerImpl.ParamFieldifier<W> fieldifier) {
        this(client, caller, inputType);
        if (paramName == null) {
            throw new IllegalArgumentException("null parameter name");
        }
        if (fieldifier == null) {
            throw new IllegalArgumentException("null field implementation");
        }
        this.fieldifier = fieldifier;
        this.isMultiple = caller.getParamdefs().get(paramName).isMultiple();
        if (this.isMultiple) {
            this.queue = new LinkedBlockingQueue<>();
        }
    }
    
    CallBatcherImpl(DatabaseClient client, CallManagerImpl.CallerImpl<E> caller, Class<W> inputType, CallArgsGenerator<E> generator) {
        this(client, caller, inputType);
        if(generator == null)
            throw new IllegalArgumentException("Call Argument Generator cannot be null.");
        this.callArgsGenerator = generator;
    }
    
    CallBatcherImpl(DatabaseClient client, CallManagerImpl.CallerImpl<E> caller, Class<W> inputType, CallArgsGenerator<E> generator, String forestName) {
        this(client, caller, inputType, generator);
        if(forestName == null || forestName.length() == 0)
            throw new IllegalArgumentException("Forest name cannot be null or empty.");
        CallManager.Paramdef paramdef = caller.getParamdefs().get(forestName);
        if(paramdef == null)
            throw new IllegalArgumentException("Forest name parameter of caller cannot be null.");
        if(!"string".equals(paramdef.getDataType()) || paramdef.isMultiple())
            throw new IllegalArgumentException("Forest name parameter cannot be multiple and needs to be a string.");
        
        this.forestParamName = forestName;
    }

    private CallBatcherImpl finishConstruction() {
        withForestConfig(getDataMovementManager().readForestConfig());
        
        if(forestParamName!=null)
            super.withThreadCount(super.getForestConfig().listForests().length);
        else 
            withThreadCount(clients.size());
        withBatchSize(isMultiple ? 100 : 1);
        return this;
    }

    private void sendSuccessToListeners(E event) {
// TODO: other actions
        for (CallSuccessListener<E> listener : successListeners) {
            listener.processEvent(event);
        }
    }

    private void sendThrowableToListeners(Throwable t, String message, CallManagerImpl.CallEvent event) {
// TODO: other actions
        for (CallFailureListener listener : failureListeners) {
            listener.processFailure(event, t);
        }
    }

    @Override
    public CallBatcher onCallSuccess(CallSuccessListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("null success listener");
        }
        successListeners.add(listener);
        return this;
    }

    @Override
    public CallBatcher onCallFailure(CallFailureListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("null failure listener");
        }
        failureListeners.add(listener);
        return this;
    }

    @Override
    public CallBatcher withBatchSize(int batchSize) {
        requireInitialized(false);
        if (queue == null) {
            if (batchSize != 1) {
                throw new IllegalArgumentException(
                        "batch size must be 1 unless batching a parameter that takes multiple values"
                );
            }
        } else if (batchSize > 100) {
            throw new IllegalArgumentException("batch size must be 100 or less");
        }
        super.withBatchSize(batchSize);
        return this;
    }

    @Override
    public CallBatcher withForestConfig(ForestConfiguration forestConfig) {
        requireInitialized(false);
        super.withForestConfig(forestConfig);
        Forest[] forests = forests(forestConfig);
// TODO: cache hosts as well as forests and clients?
        Set<String> hosts = hosts(forests);
        clients = clients(hosts);
// TODO fire host unavailable listeners if host list changed
        return this;
    }

    @Override
    public CallBatcher withJobId(String jobId) {
        requireInitialized(false);
        setJobId(jobId);
        return this;
    }

    @Override
    public CallBatcher withJobName(String jobName) {
        requireInitialized(false);
        super.withJobName(jobName);
        return this;
    }

    @Override
    public CallBatcher withThreadCount(int threadCount) {
        requireInitialized(false);
        if(forestParamName != null)
            throw new MarkLogicInternalException("The number of threads will be based on the number of forests.");
        super.withThreadCount(threadCount);
        return this;
    }

    @Override
    public CallBatcherImpl<W, E> withDefaultArgs(CallArgs args) {
        if (args == null) {
            this.defaultArgs = null;
            return this;
        } else if (!(args instanceof CallArgsImpl)) {
            throw new IllegalArgumentException("Unsupported implementation of arguments: " +
                    args.getClass().getCanonicalName());
        }
        CallArgsImpl callArgsImpl = (CallArgsImpl) args;

        if (callArgsImpl.getEndpoint().getEndpointPath() != caller.getEndpointPath())
            throw new IllegalArgumentException("Endpoints are different");

        if (callArgsImpl.getCallFields() == null || callArgsImpl.getCallFields().size() == 0) {
            this.defaultArgs = null;
            return this;
        }

        Set<RESTServices.CallField> callFieldList = callArgsImpl.getCallFields().stream().map(p -> p.toBuffered()).collect(Collectors.toSet());
        this.defaultArgs = (callFieldList.size() == 0) ? null : callFieldList;

        return this;
    }

    CallArgsImpl addDefaultArgs(RESTServices.CallField callField) {
        if (callField == null) {
            return makeDefaultArgs();
        }

        Set<RESTServices.CallField> newCallFields = new HashSet<>();
        Set<String> assignedParams = new HashSet<>();

        newCallFields.add(callField);
        assignedParams.add(callField.getParamName());

        if (defaultArgs == null) {
            return new CallArgsImpl(caller.getEndpoint(), newCallFields, assignedParams);
        }

        return addDefaultArgs(newCallFields, assignedParams);
    }
    CallArgsImpl addDefaultArgs(CallArgsImpl callArgsImpl) {
        if (callArgsImpl == null || callArgsImpl.getCallFields() == null ||
                callArgsImpl.getCallFields().size() == 0) {
            return makeDefaultArgs();
        }

        if (defaultArgs == null) {
            return callArgsImpl;
        }

        Set<RESTServices.CallField> newCallFields = new HashSet<>();
        Set<String> assignedParams = new HashSet<>();

        newCallFields.addAll(callArgsImpl.getCallFields());
        assignedParams.addAll(callArgsImpl.getAssignedParams());

        return addDefaultArgs(newCallFields, assignedParams);
    }
    CallArgsImpl addDefaultArgs(Set<RESTServices.CallField> newCallFields, Set<String> assignedParams) {
        for (RESTServices.CallField i : defaultArgs) {
            if (!assignedParams.contains(i.getParamName())) {
                assignedParams.add(i.getParamName());
                newCallFields.add(i);
            }
        }

        return new CallArgsImpl(caller.getEndpoint(), newCallFields, assignedParams);
    }
    CallArgsImpl makeDefaultArgs() {
        if (defaultArgs == null) {
            return new CallArgsImpl(caller.getEndpoint());
        }

        return new CallArgsImpl(
                caller.getEndpoint(),
                defaultArgs,
                defaultArgs.stream().map(RESTServices.CallField::getParamName).collect(Collectors.toSet())
        );
    }

    @Override
    public CallSuccessListener<E>[] getCallSuccessListeners() {
        return successListeners.toArray(new CallSuccessListener[successListeners.size()]);
    }

    @Override
    public CallFailureListener[] getCallFailureListeners() {
        return failureListeners.toArray(new CallFailureListener[failureListeners.size()]);
    }

    @Override
    public void setCallSuccessListeners(CallSuccessListener<E>... listeners) {
        requireInitialized(false);
        successListeners = Arrays.asList(listeners);
    }

    @Override
    public void setCallFailureListeners(CallFailureListener... listeners) {
        requireInitialized(false);
        failureListeners = Arrays.asList(listeners);
    }

    @Override
    public CallBatcher add(W input) {
        requireNotStopped();
        // initialize implicitly if not initialized explicitly previously
        initialize();

        CallManagerImpl.CallArgsImpl args = null;
        if (input == null) {
            if(inputType == Void.class)
                throw new IllegalArgumentException("Cannot call add() when supplying arguments with generator.");
            args = makeDefaultArgs();
        // batched param taking multiple values
        } else if (queue != null) {
            queue.add(input);
            boolean timeToCallBatch = (queue.size() % getBatchSize()) == 0;
            if (!timeToCallBatch) return this;

            List<W> batch = new ArrayList<>();
            int batchSize = queue.drainTo(batch, getBatchSize());
            if (batchSize < 1) return this;

            RESTServices.CallField field = (batchSize == 1) ?
                    fieldifier.field(batch.get(0)) : fieldifier.field(batch.stream());
            args = addDefaultArgs(field);
        // batched param taking one value
        } else if (fieldifier != null) {
            RESTServices.CallField field = fieldifier.field(input);
            args = addDefaultArgs(field);
        // explicit arguments
        } else if (input instanceof CallManager.CallArgs) {
            if (!(input instanceof CallManagerImpl.CallArgsImpl)) {
                throw new IllegalArgumentException("unsupported implementation of call arguments: " +
                        input.getClass().getCanonicalName());
            }
            args = addDefaultArgs((CallManagerImpl.CallArgsImpl) input);
        } else {
            throw new MarkLogicInternalException("Unknown input");
        }

// TODO: skip check for batched parameter after first call?
        caller.checkArgs(args);

// TODO
        submitCall(args);
        return this;
    }
    void submitCall(CallManagerImpl.CallArgsImpl args) {
        long callNumber = callCount.incrementAndGet();
        threadPool.execute(new CallTask(this, callNumber, args));
    }

    @Override
    public void addAll(Stream<W> input) {
        if (input == null) {
            throw new IllegalArgumentException("null input stream");
        }
        input.forEach(this::add);
    }

    CallManagerImpl.EventedCaller<E> getCaller() {
        return caller;
    }

    DatabaseClient getClient(long callNumber) {
        int clientSize = (clients == null) ? 0 : clients.size();
        if (clientSize < 2 || getDataMovementManager().getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) {
            return getPrimaryClient();
        }
        int clientNumber = (int) (callNumber % clients.size());
        return clients.get(clientNumber);
    }

    private void requireNotStopped() {
        if (isStopped() == true) throw new IllegalStateException("This instance has been stopped");
    }

    private void requireInitialized(boolean state) {
        if (initialized.get() != state) {
            throw new IllegalStateException(state ?
                    "This operation must be called after starting this job" :
                    "This operation must be called before starting this job");
        }
    }

    @Override
    public boolean awaitCompletion() {
        return awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public boolean awaitCompletion(long timeout, TimeUnit unit) {
        requireNotStopped();
        requireInitialized(true);
        return threadPool.awaitCompletion(timeout, unit);
    }

    @Override
    public void flushAndWait() {
        flush(true);
    }

    @Override
    public void flushAsync() {
        flush(false);
    }

    private void flush(boolean waitForCompletion) {
        if (queue != null) {
            for (
                    List<W> batch = new ArrayList<>();
                    queue.drainTo(batch, getBatchSize()) > 0;
                    batch = new ArrayList<>()
            ) {
                RESTServices.CallField field = (batch.size() == 1) ?
                        fieldifier.field(batch.get(0)) : fieldifier.field(batch.stream());
                CallManagerImpl.CallArgsImpl args = addDefaultArgs(field);
// TODO: skip check for batched parameter after first call?
                caller.checkArgs(args);
                submitCall(args);
            }
        }

        if (waitForCompletion == true) awaitCompletion();
    }

    @Override
    public DataMovementManager getDataMovementManager() {
        return super.getMoveMgr();
    }

    @Override
    public JobTicket startJob() {
        return getDataMovementManager().startJob(this);
    }

    @Override
    public void stopJob() {
        getDataMovementManager().stopJob(this);
    }

    @Override
    public JobTicket getJobTicket() {
        requireInitialized(true);
        return jobTicket;
    }

    @Override
    public Calendar getJobStartTime() {
        if (this.isStarted()) {
            return jobStartTime;
        }
        return null;
    }

    @Override
    public Calendar getJobEndTime() {
        if (this.isStopped()) {
            return jobEndTime;
        }
        return null;
    }

    @Override
    public void start(JobTicket ticket) {
        jobTicket = ticket;
// TODO
        initialize();
    }

    private void initialize() {
        if (getBatchSize() <= 0) {
            withBatchSize(1);
            logger.warn("batchSize should be 1 or greater -- setting batchSize to 1");
        }
        if (getThreadCount() <= 0) {
            int threadCount = clients.size();
            withThreadCount(threadCount);
            logger.warn("threadCount should be 1 or greater -- setting threadCount to number of hosts: " + threadCount);
        }
        if (initialized.getAndSet(true)) return;
        
        threadPool = new CallingThreadPoolExecutor(this, getThreadCount());
        jobStartTime = Calendar.getInstance();
        started.set(true);
        
        if(callArgsGenerator != null) {
            String[] forestNames = null;
            if(forestParamName!=null) {
                Forest[] forests;
                forests = super.getForestConfig().listForests();
                forestNames = new String[forests.length];
                int j=0;
                for(Forest i:forests) {
                    forestNames[j] = i.getForestName();
                    j++;
                }
            }
            for (int i=0;i<getThreadCount(); i++) {
                    CallArgs newInput = callArgsGenerator.apply(null);
                    if(forestNames!=null && forestNames[i]!=null) {
                        newInput.param(forestParamName, forestNames[i]);
                    }
                    if(newInput != null) {
                        if(!(newInput instanceof CallArgsImpl))
                            throw new MarkLogicInternalException("Unsupported implementation of call arguments.");
                        submitCall((CallArgsImpl) newInput);
                    }
            }
        }
    }

    @Override
    public void stop() {
        jobEndTime = Calendar.getInstance();
        stopped.set(true);
        if (threadPool != null) {
            try {
                threadPool.shutdown();
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.warn("interrupted while awaiting termination", e);
                threadPool.shutdownNow();
            }
        }
// TODO
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public boolean isStopped() {
        return stopped.get();
    }

    @Override
    public void retry(CallManager.CallEvent event) {
        retry(event, false);
    }

    @Override
    public void retryWithFailureListeners(CallManager.CallEvent event) {
        retry(event, true);
    }

    private void retry(CallManager.CallEvent event, boolean callFailListeners) {
        if (isStopped()) {
            logger.warn("Job is now stopped, aborting the retry");
            return;
        }
        if (event == null) throw new IllegalArgumentException("event must not be null");

        CallManager.CallArgs args = event.getArgs();
        if (args != null && !(args instanceof CallManagerImpl.CallArgsImpl)) {
            throw new IllegalArgumentException("unsupported implementation of call arguments");
        }
        CallTask<W, E> callTask = new CallTask(
                this, event.getJobBatchNumber(), (CallManagerImpl.CallArgsImpl) args
        );
        callTask.withFailureListeners(callFailListeners).run();
    }

    static class BuilderImpl<E extends CallManager.CallEvent> implements CallBatcherBuilder<E> {
        private CallManagerImpl.CallerImpl<E> caller;
        private DatabaseClient client;

        BuilderImpl(DatabaseClient client, CallManagerImpl.CallerImpl<E> caller) {
            this.caller = caller;
            this.client = client;
        }

        @Override
        public <W> CallBatcherImpl<W, E> forBatchedParam(String paramName, Class<W> paramType) {
            if (paramName == null || paramName.length() == 0) {
                throw new IllegalArgumentException("null or empty name for batched parameter");
            }
            if (paramType == null) {
                throw new IllegalArgumentException("null type for batched parameter");
            }

            CallManagerImpl.ParamdefImpl paramdef = (CallManagerImpl.ParamdefImpl) caller.getParamdefs().get(paramName);
            if (paramdef == null) {
                throw new IllegalArgumentException("no defintion for batched parameter of name: " + paramName);
            }

            CallManagerImpl.BaseFieldifier fielder = paramdef.getFielder();
            if (fielder == null) {
                throw new IllegalArgumentException("unsupported type " + paramType.getCanonicalName() + " for batched parameter of name: " + paramName);
            }

            CallManagerImpl.ParamFieldifier<W> fieldifier = fielder.fieldifierFor(paramName, paramType);

            return new CallBatcherImpl(client, caller, paramType, paramName, fieldifier).finishConstruction();
        }

        @Override
        public CallBatcherImpl<CallManager.CallArgs, E> forArgs() {
            return new CallBatcherImpl(client, caller, CallManager.CallArgs.class).finishConstruction();
        }

        @Override
        public CallBatcher<Void, E> forArgsGenerator(CallArgsGenerator<E> generator) {
            return new CallBatcherImpl(client, caller, Void.class, generator).finishConstruction();
        }

        @Override
        public CallBatcher<Void, E> forArgsGenerator(CallArgsGenerator<E> generator, String forestName) {
            return new CallBatcherImpl(client, caller, Void.class, generator, forestName).finishConstruction();
        }
    }

    static class CallingThreadPoolExecutor<W, E extends CallManager.CallEvent> extends ThreadPoolExecutor {
        private CallBatcherImpl<W, E> batcher;
        private Set<CallTask<W, E>> queuedAndExecutingTasks;
        private CountDownLatch idleLatch;

// TODO review including whether CallerRunsPolicy requires a derivation and whether retry or shutdown affects queue
        CallingThreadPoolExecutor(CallBatcherImpl<W, E> batcher, int threadCount) {
            super(threadCount, threadCount, 1, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(threadCount * 15),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            this.batcher = batcher;
            // reserve capacity for all executing threads as well as executor queue
            this.queuedAndExecutingTasks = ConcurrentHashMap.newKeySet(getQueue().size() + threadCount);
            if (batcher.callArgsGenerator != null) {
                idleLatch = new CountDownLatch(threadCount);
            }
        }

        @Override
        public <T> Future<T> submit(Callable<T> callable) {
            throw new MarkLogicInternalException("submit of callable not supported");
        }
        @Override
        public Future<Boolean> submit(Runnable command) {
            throw new MarkLogicInternalException("submit of task not supported");
        }
        @Override
        public <T> Future<T> submit(Runnable command, T result) {
            throw new MarkLogicInternalException("submit of task with default result not supported");
        }
        @Override
        public void execute(Runnable command) {
            if (!(command instanceof CallTask<?,?>)) {
                throw new MarkLogicInternalException("submitted unknown implementation of task");
            }
            queuedAndExecutingTasks.add((CallTask<W,E>) command);
            super.execute(command);
        }
        @Override
        protected void afterExecute(Runnable command, Throwable t) {
            queuedAndExecutingTasks.remove(command);
            super.afterExecute(command, t);
        }
        
        void threadIdling() {
            idleLatch.countDown();
        }

        boolean awaitCompletion(long timeout, TimeUnit unit) {
            try {
                if (isTerminated()) return true;
                
                if (batcher.callArgsGenerator != null) {
                   idleLatch.await(timeout, unit);
                } else {
                    // take a snapshot of the queue at the current time
                    Set<CallTask<W, E>> queue = new HashSet<>();
                    queue.addAll(queuedAndExecutingTasks);
                    if (queue.isEmpty())
                        return true;
                    // wait for the future of every queued or executing task
                    for (CallTask<W, E> task : queue) {
                        if (task.isCancelled() || task.isDone())
                            continue;
                        task.get(timeout, unit);
                    }
                }
                return true;
            } catch (InterruptedException e) {
                logger.warn("interrupted while awaiting completion", e);
            } catch (ExecutionException e) {
                logger.warn("access exception while awaiting completion", e);
            } catch (TimeoutException e) {
                throw new DataMovementException("timed out while awaiting completion", e);
            }
            return false;
        }
    }

    static class CallTask<W, E extends CallManager.CallEvent> extends FutureTask<Boolean> {
        private CallMaker<W, E> callMaker;
        CallTask(CallBatcherImpl<W, E> batcher, long callNumber, CallManagerImpl.CallArgsImpl args) {
            this(new CallMaker(batcher, callNumber, args));
        }
        CallTask(CallMaker<W, E> callMaker) {
            super(callMaker);
            this.callMaker = callMaker;
        }
        CallTask<W, E> withFailureListeners(boolean enable) {
            callMaker.setFailureListeners(enable);
            return this;
        }
    }

    static class CallMaker<W, E extends CallManager.CallEvent> implements Callable<Boolean> {
        private CallManagerImpl.CallArgsImpl args;
        private CallBatcherImpl<W, E> batcher;
        private long callNumber;
        private Future<Boolean> future;
        private boolean fireFailureListeners = true;

        CallMaker(CallBatcherImpl<W, E> batcher, long callNumber, CallManagerImpl.CallArgsImpl args) {
            this.batcher = batcher;
            this.callNumber = callNumber;
            this.args = args;
        }

        void setFailureListeners(boolean enable) {
            fireFailureListeners = enable;
        }

        void initEvent(CallManager.CallEvent event, Calendar callTime) {
            ((CallManagerImpl.CallEventImpl) event)
                    .withJobBatchNumber(callNumber)
                    .withJobTicket(batcher.getJobTicket())
                    .withTimestamp(callTime);
        }

        @Override
        public Boolean call() {
            CallManagerImpl.EventedCaller<E> caller = batcher.getCaller();
            DatabaseClient client = batcher.getClient(callNumber);
            Calendar callTime = Calendar.getInstance();

            E output = null;
            try {
                output = caller.callForEvent(client, args);
            } catch (Throwable throwable) {
                if (fireFailureListeners) {
                    CallManagerImpl.CallEventImpl input = new CallManagerImpl.CallEventImpl(client, args);
                    initEvent(input, callTime);
                    batcher.sendThrowableToListeners(throwable, "failure calling " + caller.getEndpointPath() + " {}", input);
                    return false;
                } else if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                } else {
                    throw new DataMovementException("Failed to retry call", throwable);
                }
            }

            initEvent(output, callTime);
            batcher.sendSuccessToListeners(output);
            if(batcher.callArgsGenerator != null) {
                // TODO:check how WriteBatcher and QueryBatcher tolerate listener errors.
                CallArgs newInput = batcher.callArgsGenerator.apply(output);
                if(newInput != null) {
                    if(!(newInput instanceof CallArgsImpl))
                        throw new MarkLogicInternalException("Unsupported implementation of call arguments.");
                    Set<CallField> callFieldSet = ((CallArgsImpl)output.getArgs()).getCallFields();
                    CallField[] callFieldList = callFieldSet.stream().toArray(CallField[]::new);
                    for(int i=0; i<callFieldList.length; i++) {
                        CallField callField= callFieldList[i];
                        if(callField.getParamName().equals(batcher.forestParamName)) {
                            newInput.param(batcher.forestParamName,((SingleAtomicCallField)callField).getParamValue());
                            break;
                        }
                    }
                    batcher.submitCall((CallArgsImpl) newInput);
                } else {
                    batcher.threadPool.threadIdling();
                }
            }
            return true;
        }
    }
}