/*
 * Copyright 2020 MarkLogic Corporation
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
package com.marklogic.client.datamovement.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.BatchPlanImpl;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowBatcherImpl extends BatcherImpl implements RowBatcher {

    private  int threadCount;
    private ForestConfiguration forestConfig;
    private String jobId;
    private  String jobName;
    private JobTicket jobTicket;
    private Calendar jobStartTime;
    private Calendar jobEndTime;
    private WriteBatcherImpl.HostInfo[] hostInfos;
    AtomicLong batchNum = new AtomicLong(0);
    private long maxBatches = Long.MAX_VALUE;
    private long rowCount = 0;
    long bucketSize;
    private BatchThreadPoolExecutor threadPool;
    private final Object lock = new Object();
    private static Logger logger = LoggerFactory.getLogger(RowBatcherImpl.class);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private List<QueryFailureListener> failureListeners = new ArrayList<>();
    private List<QueryBatcherListener> jobCompletionListeners = new ArrayList<>();
    private RowBatchSuccessListener[] listeners;
    private RequestParameters params;
    private BatchPlanImpl batchPlanImpl;
    private PlanBuilder.ModifyPlan viewPlan;

    public <T extends StructureReadHandle> RowBatcherImpl(T sampleHandle, DataMovementManager moveMgr) {
        super(moveMgr);
        params = new RequestParameters();
    }

    @Override
    public RowBatcher withBatchView(PlanBuilder.ModifyPlan viewPlan) {
        // produces an batching plan from the supplied
        if(this.isStarted())
            throw new IllegalStateException(("Cannot change batch view after the job is started."));

       this.viewPlan = viewPlan;
        return this;
    }

    @Override
    public RowBatcher withBatchSize(int batchSize) {
        if(this.isStarted())
            throw new IllegalStateException(("Cannot change batch size after the job is started."));

        super.withBatchSize(batchSize);
        return this;
    }

    @Override
    public Batcher withThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public RowBatcher onSuccess(RowBatchSuccessListener listener) {
        return this;
    }

    @Override
    public RowBatcher onFailure(RowBatchFailureListener listener) {
        return this;
    }

    @Override
    public RowBatcher withJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    @Override
    public String getJobId() {
        return this.jobId;
    }

    @Override
    public RowBatcher withJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }

    @Override
    public RowBatcher withConsistentSnapshot() {

        return this;
    }

    @Override
    public RowBatchSuccessListener[] getSuccessListeners() {
        return new RowBatchSuccessListener[0];
    }

    @Override
    public RowBatchFailureListener[] getFailureListeners() {
        return failureListeners.toArray(new RowBatchFailureListener[failureListeners.size()]);
    }

    @Override
    public void setSuccessListeners(RowBatchSuccessListener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void setFailureListeners(RowBatchFailureListener... listeners) {

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
        if ( threadPool == null ) {
            throw new IllegalStateException("Job not started.");
        }
        return threadPool.awaitTermination(timeout, unit);
    }

    @Override
    public void retry(RowBatchRequestEvent event) {
        if ( isStopped() == true ) {
            logger.warn("Job is now stopped, aborting the retry");
            return;
        }
        start(getJobTicket());
    }

    @Override
    public void retryWithFailureListeners(RowBatchRequestEvent event) {
        retry(event);
    }

    @Override
    public long getRowEstimate() {
        return this.rowCount;
    }

    @Override
    public JobTicket getJobTicket() {
        return this.jobTicket;
    }

    @Override
    public void stop() {
        stopped.set(true);
        if ( threadPool != null ) threadPool.shutdownNow();
        jobEndTime = Calendar.getInstance();

    }

    @Override
    public Calendar getJobStartTime() {
        return this.jobStartTime;
    }

    @Override
    public Calendar getJobEndTime() {
        return this.jobEndTime;
    }

    @Override
    public DatabaseClient getPrimaryClient() {
        return null;
    }

    @Override
    public synchronized void start(JobTicket ticket) {

        if(this.viewPlan == null)
            throw new InternalError("Plan must be supplied before starting the job");
        if ( threadPool != null ) {
            logger.warn("startJob called more than once");
            return;
        }

        if ( super.getBatchSize() <= 0 ) {
            withBatchSize(1);
            logger.warn("batchSize should be 1 or greater--setting batchSize to 1");
        }
        this.threadPool = new BatchThreadPoolExecutor(threadCount);
        for(int i=0; i<threadCount; i++) {
            RowBatchCallable rowBatchCallable = new RowBatchCallable(this);
            rowBatchCallable.call();
        }

        jobTicket = ticket;

        getRowCount();
        jobStartTime = Calendar.getInstance();
        started.set(true);
    }

    @Override
    public boolean isStopped() {
        return stopped.get();
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public synchronized RowBatcher withForestConfig(ForestConfiguration forestConfig) {
        super.withForestConfig(forestConfig);

        this.hostInfos = super.forestHosts(forestConfig, this.hostInfos);
        return this;
    }

    private void getRowCount(){
        for(WriteBatcherImpl.HostInfo i:hostInfos) {
            this.batchPlanImpl = new BatchPlanImpl(this.viewPlan, i.client);

            params.add("schema", batchPlanImpl.getSchema());
            params.add("view", batchPlanImpl.getView());

            JsonNode jn = ((DatabaseClientImpl) i.client).getServices()
                    .getResource(null, "internal/viewinfo", i.getTransactionInfo().getTransaction(), params,
                            new JacksonHandle())
                    .get();
        }
        long batchCount = (this.rowCount/super.getBatchSize());
        this.bucketSize = (Long.MAX_VALUE/batchCount);
    }

    private void submit(Callable<Boolean> callable) {
        FutureTask futureTask = new FutureTask(callable);
        submit(futureTask);
    }
    private void submit(FutureTask<Boolean> task) {
        threadPool.execute(task);
    }

    static private class RowBatchCallable implements Callable<Boolean> {
        private RowBatcherImpl rowBatcher;
        RowBatchCallable(RowBatcherImpl rowBatcher) {
            this.rowBatcher = rowBatcher;
        }
        @Override
        public Boolean call() {
            long lowerBound = rowBatcher.batchNum.get() * rowBatcher.getBatchSize();
            long upperBound = lowerBound+rowBatcher.getBatchSize();
            BatchPlanImpl batchPlan = rowBatcher.batchPlanImpl;

            // construct the query for this batch of rows by binding the boundaries
            // make the request for the rows using the RowBatcher's RowManager
            // if the response indicates success
            //     then call the success listener with the row response
            //     else call the failure listener and proceed as appropriate
            // if the upper boundary is Long.MAX_VALUE
            //     then call shutdown on the RowBatcher's threadpool
            //     else construct and submit a new RowBatchCallable
            // return true or false for success or failure
            return false;
        }
    }

    private class BatchThreadPoolExecutor extends ThreadPoolExecutor {
        BatchThreadPoolExecutor(int threadCount) {

            super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadCount), new ThreadPoolExecutor.CallerRunsPolicy());
           /* super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<FutureTask<Boolean>>(threadCount),
                    new ThreadPoolExecutor.CallerRunsPolicy());*/
        }

    }
}
