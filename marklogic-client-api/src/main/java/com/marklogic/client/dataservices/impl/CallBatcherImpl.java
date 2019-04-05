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
import com.marklogic.client.dataservices.CallSuccessListener;
import com.marklogic.client.impl.RESTServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class CallBatcherImpl<W, E extends CallManager.CallEvent> extends BatcherImpl implements CallBatcher<W,E> {
    private static Logger logger = LoggerFactory.getLogger(CallBatcherImpl.class);

    private CallManagerImpl.EventedCaller<E>   caller;
    private Class<W>                           inputType;
    private boolean                            isMultiple = false;
    private CallManagerImpl.ParamFieldifier<W> fieldifier;
    private JobTicket                          jobTicket;
    private CallingThreadPoolExecutor          threadPool;
    private List<DatabaseClient>               clients;
    private List<CallSuccessListener<E>>       successListeners = new ArrayList<>();
    private List<CallFailureListener>          failureListeners = new ArrayList<>();
    private LinkedBlockingQueue<W>             queue;
    private AtomicLong                         callCount = new AtomicLong();
    private Calendar                           jobStartTime;
    private Calendar                           jobEndTime;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean stopped     = new AtomicBoolean(false);
    private final AtomicBoolean started     = new AtomicBoolean(false);

    CallBatcherImpl(DatabaseClient client, CallManagerImpl.EventedCaller<E> caller, Class<W> inputType) {
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
    CallBatcherImpl(DatabaseClient client, CallManagerImpl.EventedCaller<E> caller, Class<W> inputType,
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
    private CallBatcherImpl finishConstruction() {
        withForestConfig(getDataMovementManager().readForestConfig());
        withThreadCount(clients.size());
        withBatchSize(isMultiple ? 100 : 1);
        return this;
    }

    private void sendSuccessToListeners(E event) {
// TODO: other actions
        for (CallSuccessListener<E> listener: successListeners) {
            listener.processEvent(event);
        }
    }
    private void sendThrowableToListeners(Throwable t, String message, CallManagerImpl.CallEvent event) {
// TODO: other actions
        for (CallFailureListener listener: failureListeners) {
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
        super.withThreadCount(threadCount);
        return this;
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
        CallManager.CallArgs args = null;
        if (queue != null) {
           queue.add(input);
           boolean timeToCallBatch = (queue.size() % getBatchSize()) == 0;
           if (!timeToCallBatch) return this;

           List<W> batch = new ArrayList<>();
           int batchSize = queue.drainTo(batch, getBatchSize());
           if (batchSize < 1) return this;

           RESTServices.CallField field =
               (batchSize == 1) ? fieldifier.field(batch.get(0)) : fieldifier.field(batch.stream());

// TODO: construct CallArgsImpl with field and default arguments and assign to args
// TODO: first-time check that all required fields are set
// TODO: skip check on required fields
            args = ((CallManagerImpl.CallerImpl) caller).args(field);
        } else if (fieldifier != null) {
            RESTServices.CallField field = fieldifier.field(input);
// TODO: construct CallArgsImpl with field and default arguments and assign to args
// TODO: first-time check that all required fields are set
// TODO: skip check on required fields
            args = ((CallManagerImpl.CallerImpl) caller).args(field);
        } else if (input instanceof CallManager.CallArgs) {
// TODO: add default arguments, check that all required fields are set
           args = (CallManager.CallArgs) input;
// TODO: input from argument generator callback
        } else {
            throw new MarkLogicInternalException("Unknown input");
        }
// TODO
        long callNumber = callCount.incrementAndGet();
        threadPool.submit(new CallTask(this, callNumber, args));
        return this;
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
        try {
            return awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            return false;
        }
    }
    @Override
    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
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
          List<W> batch = new ArrayList<>();
          while (queue.drainTo(batch, getBatchSize()) > 0) {
             Stream<W> paramValues = batch.stream();
// TODO: construct CallArgsImpl with paramName, paramValues, and default arguments and assign to args
             CallManager.CallArgs args = null;
             long callNumber = callCount.incrementAndGet();
             threadPool.submit(new CallTask(this, callNumber, args));
             batch.clear();
          }
       }

// TODO: different semantics for await completion in CallBatcher and WriteBatcher
        if ( waitForCompletion == true ) awaitCompletion();
    }

    @Override
    public DataMovementManager getDataMovementManager() {
        return super.getMoveMgr();
    }
    @Override
    public JobTicket startJob​() {
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
    	if(this.isStopped()) {
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
            logger.warn("threadCount should be 1 or greater -- setting threadCount to number of hosts: "+threadCount);
        }
        if (initialized.getAndSet(true)) return;
// TODO
        threadPool = new CallingThreadPoolExecutor(this, getThreadCount());
        jobStartTime = Calendar.getInstance();
        started.set(true);
    }
    @Override
    public void stop() {
    	jobEndTime = Calendar.getInstance();
        stopped.set(true);
        if ( threadPool != null ) {
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

        CallTask<W,E> callTask = new CallTask(this, event.getJobBatchNumber(), event.getArgs());
        callTask.withFailureListeners(callFailListeners).call();
    }

    static class BuilderImpl<E extends CallManager.CallEvent> implements CallBatcherBuilder<E> {
        private CallManagerImpl.EventedCaller<E> caller;
        private DatabaseClient                   client;

        BuilderImpl(DatabaseClient client, CallManagerImpl.EventedCaller<E> caller) {
            this.caller  = caller;
            this.client  = client;
        }

        @Override
        public CallBatcherBuilder<E> defaultArgs(CallManager.CallArgs args) {
// TODO
            return null;
        }

        @Override
        public <W> CallBatcherImpl<W,E> forBatchedParam(String paramName, Class<W> paramType) {
           if (paramName == null || paramName.length() == 0) {
              throw new IllegalArgumentException("null or empty name for batched parameter");
           }
           if (paramType == null) {
              throw new IllegalArgumentException("null type for batched parameter");
           }

           CallManagerImpl.ParamdefImpl paramdef = (CallManagerImpl.ParamdefImpl) caller.getParamdefs().get(paramName);
           if (paramdef == null) {
              throw new IllegalArgumentException("no defintion for batched parameter of name: "+paramName);
           }

           CallManagerImpl.BaseFieldifier fielder = paramdef.getFielder();
           if (fielder == null) {
              throw new IllegalArgumentException("unsupported type "+paramType.getCanonicalName()+" for batched parameter of name: "+paramName);
           }

           CallManagerImpl.ParamFieldifier<W> fieldifier = fielder.fieldifierFor(paramName, paramType);

           return new CallBatcherImpl(client, caller, paramType, paramName, fieldifier).finishConstruction();
        }

        @Override
        public CallBatcherImpl<CallManager.CallArgs, E> forArgs() {
            return new CallBatcherImpl(client, caller, CallManager.CallArgs.class).finishConstruction();
        }
    }

    static class CallingThreadPoolExecutor<W,E extends CallManager.CallEvent> extends ThreadPoolExecutor {
        private CallBatcherImpl<W,E> batcher;
// TODO review including whether CallerRunsPolicy requires a derivation
        CallingThreadPoolExecutor(CallBatcherImpl<W,E> batcher, int threadCount) {
            super(threadCount, threadCount, 1, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(threadCount * 25),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            this.batcher = batcher;
        }

        @Override
        public Future<?> submit(Runnable task) {
            throw new MarkLogicInternalException("must submit call");
        }
        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            throw new MarkLogicInternalException("must submit call");
        }

        boolean awaitCompletion(long timeout, TimeUnit unit) {
            // get the last runnable task in the queue and wait for its future
            // guarantees that the current tail of the queue has drained
            // but not that all threads have finished execution
            try {
                if (isTerminated()) return true;
                BlockingQueue<Runnable> queue = getQueue();
                if (queue.isEmpty()) return true;
                RunnableFuture<Boolean> future = null;
                for (Runnable runnable: queue) {
                    future = (RunnableFuture<Boolean>) runnable;
                }
                if (future == null || future.isCancelled() || future.isDone()) return true;
                future.get(timeout, unit);
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

    static class CallTask<W,E extends CallManager.CallEvent> implements Callable<Boolean> {
        private CallManager.CallArgs args;
        private CallBatcherImpl<W,E> batcher;
        private long                 callNumber;
        private Future<Boolean>      future;
        private boolean              fireFailureListeners = true;

        CallTask(CallBatcherImpl<W,E> batcher, long callNumber, CallManager.CallArgs args) {
            this.batcher    = batcher;
            this.callNumber = callNumber;
            this.args       = args;
        }

        CallTask<W,E> withFailureListeners(boolean enable) {
            fireFailureListeners = enable;
            return this;
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
            try {
                E output = caller.callForEvent(client, args);
                initEvent(output, callTime);
                batcher.sendSuccessToListeners(output);
                return true;
            } catch (Throwable throwable) {
                if (fireFailureListeners) {
                    CallManagerImpl.CallEventImpl input = new CallManagerImpl.CallEventImpl(client, args);
                    initEvent(input, callTime);
                    batcher.sendThrowableToListeners(throwable, "failure calling "+caller.getEndpointPath()+" {}", input);
                    return false;
                } else if (throwable instanceof RuntimeException ) {
                    throw (RuntimeException) throwable;
                } else {
                    throw new DataMovementException("Failed to retry call", throwable);
                }
            }
        }
    }
}
