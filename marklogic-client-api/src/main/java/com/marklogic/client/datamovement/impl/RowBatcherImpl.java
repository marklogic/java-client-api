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
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowBatcherImpl extends BatcherImpl implements RowBatcher {

    private JobTicket jobTicket;
    private Calendar jobStartTime;
    private Calendar jobEndTime;
    AtomicLong batchNum = new AtomicLong(0);
    private long rowCount = 0;
    private long bucketSize;
    private BatchThreadPoolExecutor threadPool;
    private final Object lock = new Object();
    private static Logger logger = LoggerFactory.getLogger(RowBatcherImpl.class);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private RowBatchFailureListener[] failureListeners;
    private RowBatchSuccessListener[] sucessListeners;
    private RequestParameters params;
    private BatchPlanImpl batchPlanImpl;
    private HostInfo[] hostInfos;

    public <T extends StructureReadHandle> RowBatcherImpl(T sampleHandle, DataMovementManager moveMgr) {
        super(moveMgr);
        params = new RequestParameters();
    }

    @Override
    public RowBatcher withBatchView(PlanBuilder.ModifyPlan viewPlan) {
        // produces an batching plan from the supplied
        if(this.isStarted())
            throw new IllegalStateException(("Cannot change batch view after the job is started."));

        this.batchPlanImpl = new BatchPlanImpl(viewPlan, getPrimaryClient());
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
        super.withThreadCount(threadCount);
        return this;
    }

    @Override
    public int getThreadCount() {
        return super.getThreadCount();
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
        super.setJobId(jobId);
        return this;
    }

    @Override
    public String getJobId() {
        return super.getJobId();
    }

    @Override
    public RowBatcher withJobName(String jobName) {
        super.withJobName(jobName);
        return this;
    }

    @Override
    public String getJobName() {
        return super.getJobName();
    }

    @Override
    public RowBatcher withConsistentSnapshot() {

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
    public void setSuccessListeners(RowBatchSuccessListener[] listeners) {
        this.sucessListeners = listeners;
    }

    @Override
    public void setFailureListeners(RowBatchFailureListener... listeners) {
        this.failureListeners = listeners;
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

        if(this.batchPlanImpl.getView() == null)
            throw new InternalError("Plan must be supplied before starting the job");
        if ( threadPool != null ) {
            logger.warn("startJob called more than once");
            return;
        }
        params.add("schema", batchPlanImpl.getSchema());
        params.add("view", batchPlanImpl.getView());
        if ( super.getBatchSize() <= 0 ) {
            withBatchSize(1);
            logger.warn("batchSize should be 1 or greater--setting batchSize to 1");
        }
        this.threadPool = new BatchThreadPoolExecutor(super.getThreadCount());
        getRowCount();
        for(int i=0; i<super.getThreadCount(); i++) {
            RowBatchCallable rowBatchCallable = new RowBatchCallable(this);
            rowBatchCallable.call();
        }

        jobTicket = ticket;
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

        this.hostInfos = forestHosts(forestConfig, this.hostInfos);
        return this;
    }

    private void getRowCount(){

            JsonNode jn = ((DatabaseClientImpl) getPrimaryClient()).getServices()
                    .getResource(null, "internal/viewinfo", null, params,
                            new JacksonHandle())
                    .get();
        // extract tableID and rowCount
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
            long lowerBound = rowBatcher.batchNum.getAndIncrement() * rowBatcher.bucketSize;
            long upperBound = lowerBound+rowBatcher.bucketSize;

            BatchPlanImpl batchPlan = rowBatcher.batchPlanImpl;
            PlanBuilder.Plan plan = batchPlan.getEncapsulatedPlan().bindParam(BatchPlanImpl.LOWER_BOUND, lowerBound)
                    .bindParam(BatchPlanImpl.UPPER_BOUND, upperBound);
            RowManager rowMgr = rowBatcher.getPrimaryClient().newRowManager();

            RowSet<RowRecord> rowSet = rowMgr.resultRows(plan);
            if(rowSet == null) {
                rowBatcher.onFailure((batch, throwable) -> {
                    throw new InternalError(throwable);
                });
                return false;
            }
            else {
                Iterator<RowRecord> rowItr = rowSet.iterator();
                while(rowItr.hasNext()) {
                    rowBatcher.onSuccess((RowBatchSuccessListener) rowItr.next());
                }
                if(upperBound == Long.MAX_VALUE)
                    rowBatcher.threadPool.shutdown();
                else
                    rowBatcher.submit(this);
                return true;
            }

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
