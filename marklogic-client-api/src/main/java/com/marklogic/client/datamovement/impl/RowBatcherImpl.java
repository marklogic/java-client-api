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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.BatchPlanImpl;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.row.RowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowBatcherImpl<T>  extends BatcherImpl implements RowBatcher<T> {
    private JobTicket jobTicket;
    private Calendar jobStartTime;
    private Calendar jobEndTime;
    AtomicLong batchNum = new AtomicLong(0);
    private long bucketSize = 0;
    private long batchCount = 0;
    private long lastBucket = 0;
    private BatchThreadPoolExecutor threadPool;
    private static Logger logger = LoggerFactory.getLogger(RowBatcherImpl.class);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private RowBatchFailureListener[] failureListeners;
    private RowBatchSuccessListener[] sucessListeners;
    private BatchPlanImpl batchPlanImpl;
    private RowManager rowMgr;
    private ContentHandle<T> rowsHandle;
    private Class<T> rowsClass;
    private HostInfo[] hostInfos;

    // TODO: START EXAMPLE DELETE
    static public void main(String... args) {
        DatabaseClient db = DatabaseClientFactory.newClient("localhost", 8012,
                new DatabaseClientFactory.DigestAuthContext("rest-writer", "x"));

        DataMovementManager moveMgr = db.newDataMovementManager();

        RowBatcher<String> rowBatcher =
                moveMgr.newRowBatcher(new StringHandle().withFormat(Format.JSON).withMimetype("application/json-seq"));

        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);
        rowMgr.setRowStructureStyle(RowManager.RowStructure.ARRAY);

        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan = planBuilder.fromView("test", "record");

        rowBatcher.withBatchView(plan)
                  .onSuccess(event -> System.out.println(
                      "batch="+event.getLowerBound()+" through "+event.getUpperBound()+"\n"+
                      event.getRowsDoc()
                      ));

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
    }
    // TODO: END EXAMPLE DELETE

    public RowBatcherImpl(DataMovementManager moveMgr, ContentHandle<T> rowsHandle) {
        super(moveMgr);
        if (rowsHandle == null)
            throw new IllegalArgumentException("Cannot create RowBatcher with null rows manager");
        if (!(rowsHandle instanceof StructureReadHandle))
            throw new IllegalArgumentException("Rows handle must also be StructureReadHandle");
        if (!(rowsHandle instanceof BaseHandle))
            throw new IllegalArgumentException("Rows handle must also be BaseHandle");
        if (((BaseHandle) rowsHandle).getFormat() == Format.UNKNOWN)
            throw new IllegalArgumentException("Rows handle must specify a format");
        this.rowsHandle = rowsHandle;
        this.rowsClass = rowsHandle.getContentClass();
        if (this.rowsClass == null)
            throw new IllegalArgumentException("Rows handle cannot have a null content class");
        if (!DatabaseClientFactory.getHandleRegistry().isRegistered(this.rowsClass))
            throw new IllegalArgumentException(
                    "Rows handle must be registered with DatabaseClientFactory.HandleFactoryRegistry"
            );
        rowMgr = getPrimaryClient().newRowManager();
    }

    @Override
    public RowManager getRowManager() {
        return rowMgr;
    }

    @Override
    public RowBatcher withBatchView(PlanBuilder.ModifyPlan viewPlan) {
        // produces an batching plan from the supplied
        if(this.isStarted())
            throw new IllegalStateException("Cannot change batch view after the job is started.");

        this.batchPlanImpl = new BatchPlanImpl(viewPlan, getPrimaryClient());
        return this;
    }

    @Override
    public RowBatcher withBatchSize(int batchSize) {
        if(this.isStarted())
            throw new IllegalStateException("Cannot change batch size after the job is started.");

        super.withBatchSize(batchSize);
        return this;
    }

    @Override
    public Batcher withThreadCount(int threadCount) {
        super.withThreadCount(threadCount);
        return this;
    }

    @Override
    public RowBatcher onSuccess(RowBatchSuccessListener listener) {
        if (listener == null) {
            sucessListeners = null;
        } else if (sucessListeners == null || sucessListeners.length == 0) {
            sucessListeners = new RowBatchSuccessListener[]{listener};
        } else {
            sucessListeners = Arrays.copyOf(sucessListeners, sucessListeners.length + 1);
            sucessListeners[sucessListeners.length - 1] = listener;
        }
        return this;
    }

    @Override
    public RowBatcher onFailure(RowBatchFailureListener listener) {
        // TODO
        return this;
    }

    @Override
    public RowBatcher withJobId(String jobId) {
        super.setJobId(jobId);
        return this;
    }

    @Override
    public RowBatcher withJobName(String jobName) {
        super.withJobName(jobName);
        return this;
    }

    @Override
    public RowBatcher withConsistentSnapshot() {
        // TODO
        return this;
    }

    @Override
    public RowBatchSuccessListener[] getSuccessListeners() {
        return sucessListeners;
    }
    @Override
    public RowBatchFailureListener[] getFailureListeners() {
        return failureListeners;
    }
    @Override
    public void setSuccessListeners(RowBatchSuccessListener... listeners) {
        this.sucessListeners = listeners;
    }
    @Override
    public void setFailureListeners(RowBatchFailureListener... listeners) {
        this.failureListeners = listeners;
    }
    private void notifySuccess(RowBatchResponseEvent<T> event) {
        for (RowBatchSuccessListener sucessListener: sucessListeners) {
            sucessListener.processEvent(event);
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
        start(event.getJobTicket());
    }

    @Override
    public void retryWithFailureListeners(RowBatchRequestEvent event) {
        retry(event);
    }

    @Override
    public long getRowEstimate() {
        if (this.batchPlanImpl == null)
            throw new IllegalStateException("Plan must be supplied before getting the row estimate");
        return this.batchPlanImpl.getRowCount();
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
    public synchronized void start(JobTicket ticket) {
        if (this.batchPlanImpl == null)
            throw new InternalError("Plan must be supplied before starting the job");
        if (threadPool != null) {
            logger.warn("startJob called more than once");
            return;
        }

        if (sucessListeners == null || sucessListeners.length == 0)
            throw new IllegalStateException("No listener for rows");

        if (super.getBatchSize() <= 0) {
            withBatchSize(1000);
            logger.warn("batchSize should be 1 or greater--setting batchSize to 1000");
        }
        this.threadPool = new BatchThreadPoolExecutor(super.getThreadCount());

        this.batchCount = (getRowEstimate() / super.getBatchSize());
        this.bucketSize = (Long.MAX_VALUE / this.batchCount);
        this.lastBucket = Long.MAX_VALUE - bucketSize;

        BaseHandle<?,?> rowsBaseHandle = (BaseHandle<?,?>) rowsHandle;
        Format rowsFormat = rowsBaseHandle.getFormat();
        String rowsMimeType = rowsBaseHandle.getMimetype();

        for (int i=0; i<super.getThreadCount(); i++) {
            ContentHandle<T> threadHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(rowsClass);
            BaseHandle<?,?> threadBaseHandle = (BaseHandle<?,?>) threadHandle;
            threadBaseHandle.setFormat(rowsFormat);
            threadBaseHandle.setMimetype(rowsMimeType);
            RowBatchCallable<T> threadCallable = new RowBatchCallable<T>(this, threadHandle);
            submit(threadCallable);
        }

        jobTicket = ticket;
        jobStartTime = Calendar.getInstance();
        started.set(true);
    }

    private boolean readRows(RowBatchCallable<T> callable) {
        long lowerBound = this.batchNum.getAndIncrement() * this.bucketSize;
        boolean isLastBucket = (lowerBound >= this.lastBucket);
        long upperBound = isLastBucket ? Long.MAX_VALUE : lowerBound + this.bucketSize;

        PlanBuilder.Plan plan = this.batchPlanImpl.getEncapsulatedPlan()
                .bindParam(BatchPlanImpl.LOWER_BOUND, lowerBound)
                .bindParam(BatchPlanImpl.UPPER_BOUND, upperBound);

        ContentHandle<T> threadHandle = callable.getHandle();

        T rowsDoc = null;
        try {
            if (this.rowMgr.resultDoc(plan, (StructureReadHandle) threadHandle) != null) {
                rowsDoc = threadHandle.get();
            }
        } catch(Throwable e) {
            // TODO: send to failure listener
        }

        // if the plan filters the rows, a bucket could be empty
        if (rowsDoc != null) {
            notifySuccess(new RowBatchResponseEventImpl<>(lowerBound, upperBound, rowsDoc));
        }

        if (isLastBucket) {
            this.threadPool.shutdown();
        } else {
            this.submit(callable);
        }

        return (rowsDoc != null);
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

        this.hostInfos = forestHosts(forestConfig, this.hostInfos);
        return this;
    }

    private void submit(Callable<Boolean> callable) {
        FutureTask futureTask = new FutureTask(callable);
        submit(futureTask);
    }
    private void submit(FutureTask<Boolean> task) {
        threadPool.execute(task);
    }

    static private class RowBatchCallable<T> implements Callable<Boolean> {
        private RowBatcherImpl rowBatcher;
        private ContentHandle<T> handle;
        RowBatchCallable(RowBatcherImpl<T> rowBatcher, ContentHandle<T> handle) {
            this.rowBatcher = rowBatcher;
            this.handle = handle;
        }
        private ContentHandle<T> getHandle() {
            return handle;
        }
        @Override
        public Boolean call() {
            return rowBatcher.readRows(this);
        }
    }

    static private class RowBatchResponseEventImpl<T> extends BatchEventImpl implements RowBatchResponseEvent<T> {
        private long lowerBound = 0;
        private long upperBound = 0;
        private T handle;
        public RowBatchResponseEventImpl(long lowerBound, long upperBound, T handle) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.handle     = handle;
        }
        @Override
        public T getRowsDoc() {
            return handle;
        }
        @Override
        public long getLowerBound() {
            return lowerBound;
        }
        @Override
        public long getUpperBound() {
            return upperBound;
        }
    }

    private class BatchThreadPoolExecutor extends ThreadPoolExecutor {
        BatchThreadPoolExecutor(int threadCount) {
            super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadCount), new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    synchronized HostInfo[] forestHosts(ForestConfiguration forestConfig, HostInfo[] hostInfos) {
        // get the list of hosts to use
        Forest[] forests = forests(forestConfig);
        Set<String> hosts = hosts(forests);
        Map<String, HostInfo> existingHostInfos = new HashMap<>();
        Map<String, HostInfo> removedHostInfos = new HashMap<>();

        if (hostInfos != null) {
            for (HostInfo hostInfo : hostInfos) {
                existingHostInfos.put(hostInfo.hostName, hostInfo);
                removedHostInfos.put(hostInfo.hostName, hostInfo);
            }
        }
        logger.info("(withForestConfig) Using forests on {} hosts for \"{}\"", hosts, forests[0].getDatabaseName());
        // initialize a DatabaseClient for each host
        HostInfo[] newHostInfos = new HostInfo[hosts.size()];
        int i = 0;
        for (String host : hosts) {
            if (existingHostInfos.get(host) != null) {
                newHostInfos[i] = existingHostInfos.get(host);
                removedHostInfos.remove(host);
            } else {
                newHostInfos[i] = new HostInfo();
                newHostInfos[i].hostName = host;
                // this is a host-specific client (no DatabaseClient is actually forest-specific)
                newHostInfos[i].client = getMoveMgr().getHostClient(host);
                if (getMoveMgr().getConnectionType() == DatabaseClient.ConnectionType.DIRECT) {
                    logger.info("Adding DatabaseClient on port {} for host \"{}\" to the rotation",
                            newHostInfos[i].client.getPort(), host);
                }
            }
            i++;
        }
        super.withForestConfig(forestConfig);

        return newHostInfos;
    }

    private static class HostInfo {
            public String hostName;
            public DatabaseClient client;
    }
}
