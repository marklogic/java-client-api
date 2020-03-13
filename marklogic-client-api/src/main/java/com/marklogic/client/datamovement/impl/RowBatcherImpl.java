package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.BatchPlanImpl;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowBatcherImpl extends BatcherImpl implements RowBatcher {

    private int batchSize;
    private  int threadCount;
    private ForestConfiguration forestConfig;
    private String jobId;
    private  String jobName;
    private JobTicket jobTicket;
    private Calendar jobStartTime;
    private Calendar jobEndTime;
    private PlanBuilder.PreparePlan preparePlan;
    private DatabaseClient client;
    private AtomicLong lowerBound = new AtomicLong(0);
    private long maxBatches = Long.MAX_VALUE;
    private long rowCount;
    private Long bucketSize;
    private QueryThreadPoolExecutor threadPool;
    private final Object lock = new Object();
    private static Logger logger = LoggerFactory.getLogger(RowBatcherImpl.class);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private List<QueryFailureListener> failureListeners = new ArrayList<>();
    private List<QueryBatcherListener> jobCompletionListeners = new ArrayList<>();
    private RowBatchSuccessListener[] listeners;
    private String viewName;
    private RequestParameters params;

    public <T extends StructureReadHandle> RowBatcherImpl(DatabaseClient client,T sampleHandle, DataMovementManager moveMgr) {
        super(moveMgr);
        this.client = client;
        params = new RequestParameters();
    }

    @Override
    public RowBatcher withBatchView(PlanBuilder.ModifyPlan viewPlan) {
        // produces an batching plan from the supplied
        if(this.isStarted())
            throw new IllegalStateException(("Cannot change batch view after the job is started."));

       // this.preparePlan = BatchPlanImpl.modifyChain(viewPlan, client, lowerBound.get(),this.batchSize);
        BatchPlanImpl batchPlanImpl = new BatchPlanImpl();

        params.add("schema", batchPlanImpl.getSchema());
        //  params.add("view", BatchPlanImpl.getView());
        return this;
    }

    @Override
    public RowBatcher withBatchSize(int batchSize) {
        if(this.isStarted())
            throw new IllegalStateException(("Cannot change batch size after the job is started."));

        this.batchSize = batchSize;
        return this;
    }

    @Override
    public int getBatchSize() {
        return this.batchSize;
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
    public void start(JobTicket ticket) {
        if ( threadPool != null ) {
            logger.warn("startJob called more than once");
            return;
        }
        if ( getBatchSize() <= 0 ) {
            withBatchSize(1);
            logger.warn("batchSize should be 1 or greater--setting batchSize to 1");
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

    private void getRowCount(){

        ((DatabaseClientImpl) client).getServices()
                .getResource(null, "internal/viewinfo", null, params,
                        new JacksonHandle())
                .get();
        getBucketSize();
    }

    private void getBucketSize() {
        long batchCount = (this.rowCount/this.batchSize);
        this.bucketSize = (Long.MAX_VALUE/batchCount);
    }

    private class QueryThreadPoolExecutor extends ThreadPoolExecutor {
        private Object objectToNotifyFrom;

        QueryThreadPoolExecutor(int threadCount, Object objectToNotifyFrom) {
            super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadCount * 25), new BlockingRunsPolicy());
            this.objectToNotifyFrom = objectToNotifyFrom;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            boolean returnValue = super.awaitTermination(timeout, unit);

            return returnValue;
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            synchronized ( lock ) {
                lock.notify();
            }
        }

        @Override
        protected void terminated() {
            super.terminated();
            synchronized(objectToNotifyFrom) {
                objectToNotifyFrom.notifyAll();
            }
            synchronized ( lock ) {
                lock.notify();
            }
        }
    }

    private class BlockingRunsPolicy implements RejectedExecutionHandler {
        /**
         * Waits for the work queue to become empty and then submits the rejected task,
         * unless the executor has been shut down, in which case the task is discarded.
         *
         * @param runnable the runnable task requested to be executed
         * @param executor the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                return;
            }
            try {
                synchronized ( lock ) {
                    while (executor.getQueue().remainingCapacity() == 0) {
                        lock.wait();
                    }
                    if (!executor.isShutdown()) executor.execute(runnable);
                }
            } catch ( InterruptedException e ) {
                logger.warn("Thread interrupted while waiting for the work queue to become empty" + e);
            }
        }
    }
}
