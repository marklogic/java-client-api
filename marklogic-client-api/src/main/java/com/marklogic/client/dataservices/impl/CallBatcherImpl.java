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
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.impl.BatcherImpl;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallFailureListener;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallSuccessListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CallBatcherImpl<W, E extends CallManager.CallEvent> extends BatcherImpl implements CallBatcher<W,E> {
    private static Logger logger = LoggerFactory.getLogger(CallBatcherImpl.class);

    private DatabaseClient                   client;
    private CallManagerImpl.EventedCaller<E> caller;
    private JobTicket                        jobTicket;
    private CallingThreadPoolExecutor        threadPool;
    private List<CallSuccessListener<E>>     successListeners = new ArrayList<>();
    private List<CallFailureListener>        failureListeners = new ArrayList<>();

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean stopped     = new AtomicBoolean(false);
    private final AtomicBoolean started     = new AtomicBoolean(false);

    CallBatcherImpl(DatabaseClient client, CallManagerImpl.EventedCaller<E> caller) {
        super(client.newDataMovementManager());
        this.client = client;
        this.caller = caller;
// TODO: default to 1 in args case
    }

    private void sendSuccessToListeners(E event) {
// TODO: other actions
        for (CallSuccessListener<E> listener: successListeners) {
            listener.processEvent(event);
        }
    }
    private void sendThrowableToListeners(Throwable t, String message, CallManager.CallEvent event) {
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
// TODO: error if not 1 in args case
        super.withBatchSize(batchSize);
        return this;
    }
    @Override
    public CallBatcher withForestConfig(ForestConfiguration forestConfig) {
        requireInitialized(false);
// TODO
        return this;
    }
    @Override
    public CallBatcher withJobId(String jobId) {
        requireInitialized(false);
        super.withJobId(jobId);
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
    public ForestConfiguration getForestConfig() {
// TODO
        return null;
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
        requireInitialized(true);
        requireNotStopped();
// TODO: construct the args in a different way depending on the input type
        CallManager.CallArgs args = (CallManager.CallArgs) input;
// TODO
        threadPool.submit(new Callable(caller, args, this));
        return this;
    }
    @Override
    public void addAll(Stream<W> input) {
        if (input == null) {
            throw new IllegalArgumentException("null input stream");
        }
        input.forEach(this::add);
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
// TODO: possibly too naive
        threadPool.shutdown();
        return threadPool.awaitTermination(timeout, unit);
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
// TODO: handle batched parameter
        if ( waitForCompletion == true ) awaitCompletion();
    }

    @Override
    public DataMovementManager getDataMovementManager() {
        return super.getMoveMgr();
    }
    @Override
    public JobTicket getJobTicket() {
        requireInitialized(true);
        return jobTicket;
    }
    @Override
    public Calendar getJobStartTime() {
// TODO
        return null;
    }
    @Override
    public Calendar getJobEndTime() {
// TODO
        return null;
    }
    @Override
    public DatabaseClient getPrimaryClient() {
        return client;
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
// TODO: default based on forest count
            withThreadCount(1);
            logger.warn("threadCount should be 1 or greater -- setting threadCount to 1");
        }
        if (initialized.getAndSet(true)) return;
// TODO
        threadPool = new CallingThreadPoolExecutor(getThreadCount());
        started.set(true);
    }
    @Override
    public void stop() {
        stopped.set(true);
        if ( threadPool != null ) threadPool.shutdownNow();
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

        Callable<W,E> callable = new Callable(caller, event.getArgs(), this);
        callable.withFailureListeners(callFailListeners).run();
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
        public CallBatcherImpl<CallManager.CallArgs, E> forArgs() {
            return new CallBatcherImpl(client, caller);
        }
    }

    static class CallingThreadPoolExecutor extends ThreadPoolExecutor {
        CallingThreadPoolExecutor(int threadCount) {
// TODO: probably a larger queue capacity
// TODO: must pass a ThreadPoolExecutor.CallerRunsPolicy as the RejectedExecutionHandler parameter
            super(threadCount, threadCount, 1, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(threadCount * 3)
            );
        }
// TODO - implement awaitCompletion()?
    }

    static class Callable<W,E extends CallManager.CallEvent> implements Runnable {
        private CallManagerImpl.EventedCaller<E> caller;
        private CallManager.CallArgs             args;
        private CallBatcherImpl<W,E>             batcher;
        private boolean                          fireFailureListeners = true;

        Callable(CallManagerImpl.EventedCaller<E> caller, CallManager.CallArgs args, CallBatcherImpl<W,E> batcher) {
            this.caller  = caller;
            this.args    = args;
            this.batcher = batcher;
        }

        Callable<W,E> withFailureListeners(boolean enable) {
            fireFailureListeners = enable;
            return this;
        }

        @Override
        public void run() {
            E output = null;
            try {
                output = caller.callForEvent(args);
                batcher.sendSuccessToListeners(output);
// TODO: if not retrying and if the thread pool has only one task, submit a shutdownIfLast runnable
// TODO: in shutdownIfLast runnable, if synchronized check confirms that the thread pool has only this task, execute thread.shutdown()
            } catch (Throwable throwable) {
                if (fireFailureListeners) {
                    CallManager.CallEvent input = new CallManagerImpl.CallEventImpl(args);
                    batcher.sendThrowableToListeners(throwable, "failure calling "+caller.getEndpointPath()+" {}", input);
                } else if (throwable instanceof RuntimeException ) {
                    throw (RuntimeException) throwable;
                } else {
                    throw new DataMovementException("Failed to retry call", throwable);
                }
            }
        }
    }
}
