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
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.impl.BatcherImpl;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallFailureListener;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CallBatcherImpl<W, E extends CallManager.CallEvent> extends BatcherImpl implements CallBatcher<W,E> {
    private DatabaseClient                   client;
    private CallManagerImpl.EventedCaller<E> caller;
    private JobTicket                        jobTicket;
    private CallingThreadPoolExecutor        threadPool;
    private List<CallSuccessListener<E>>     successListeners = new ArrayList<>();

    CallBatcherImpl(DatabaseClient client, CallManagerImpl.EventedCaller<E> caller) {
        super(client.newDataMovementManager());
        this.client = client;
        this.caller = caller;
    }

    private void sendSuccessToListeners(E event) {
// TODO
        for (CallSuccessListener<E> listener: successListeners) {
            listener.processEvent(event);
        }
    }
    private void sendThrowableToListeners(Throwable t, String message, CallManager.CallEvent event) {
// TODO
    }

    @Override
    public CallBatcher onCallSuccess(CallSuccessListener listener) {
// TODO
        return null;
    }
    @Override
    public CallBatcher onCallFailure(CallFailureListener listener) {
// TODO
        return null;
    }
    @Override
    public CallBatcher withBatchSize(int batchSize) {
        super.withBatchSize(batchSize);
        return this;
    }
    @Override
    public CallBatcher withForestConfig(ForestConfiguration forestConfig) {
// TODO
        return null;
    }
    @Override
    public boolean isStarted() {
// TODO
        return false;
    }
    @Override
    public CallBatcher withJobId(String jobId) {
        super.withJobId(jobId);
        return this;
    }
    @Override
    public CallBatcher withJobName(String jobName) {
        super.withJobName(jobName);
        return this;
    }
    @Override
    public CallBatcher withThreadCount(int threadCount) {
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
// TODO
        return new CallFailureListener[0];
    }
    @Override
    public void setCallSuccessListeners(CallSuccessListener<E>[] listeners) {
        successListeners = Arrays.asList(listeners);
    }
    @Override
    public void setCallFailureListeners(CallFailureListener... listeners) {
// TODO
    }
    @Override
    public CallBatcher add(W input) {
// TODO: construct the args in a different way depending on the input type
        CallManager.CallArgs args = (CallManager.CallArgs) input;
// TODO
        threadPool.submit(new Callable(caller, args, this));
        return this;
    }
    @Override
    public void addAll(Stream input) {
// TODO
    }
    @Override
    public void awaitCompletion() {
// TODO
    }
    @Override
    public void awaitCompletion(long timeout, TimeUnit unit) {
// TODO
    }
    @Override
    public void flushAndWait() {
// TODO
    }
    @Override
    public void flushAsync() {
// TODO
    }
    @Override
    public DataMovementManager getDataMovementManager() {
        return super.getMoveMgr();
    }
    @Override
    public JobTicket getJobTicket() {
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
// TODO
        threadPool = new CallingThreadPoolExecutor(getThreadCount());
    }
    @Override
    public void stop() {
// TODO
    }
    @Override
    public boolean isStopped() {
// TODO
        return false;
    }
    @Override
    public void retry(CallManager.CallEvent event) {
// TODO
    }
    @Override
    public void retryWithFailureListeners(CallManager.CallEvent event) {
// TODO
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
            super(threadCount, threadCount, 1, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(threadCount * 3)
            );
        }
// TODO
    }

    static class Callable<W,E extends CallManager.CallEvent> implements Runnable {
        private CallManagerImpl.EventedCaller<E> caller;
        private CallManager.CallArgs             args;
        private CallBatcherImpl<W,E>             batcher;

        Callable(CallManagerImpl.EventedCaller<E> caller, CallManager.CallArgs args, CallBatcherImpl<W,E> batcher) {
            this.caller  = caller;
            this.args    = args;
            this.batcher = batcher;
        }

        @Override
        public void run() {
            try {
                batcher.sendSuccessToListeners(caller.callForEvent(args));
            } catch (Throwable e) {
                CallManager.CallEvent event = new CallManagerImpl.CallEventImpl(args);
                batcher.sendThrowableToListeners(e, "failure calling "+caller.getEndpointPath()+" {}", event);
            }
        }
    }
}
