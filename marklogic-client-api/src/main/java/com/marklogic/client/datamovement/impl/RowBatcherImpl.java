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
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RowBatcherImpl<T>  extends BatcherImpl implements RowBatcher<T> {
    final static private int DEFAULT_BATCH_SIZE = 1000;
    final static private long MAX_UNSIGNED_LONG = -1;

    private static Logger logger = LoggerFactory.getLogger(RowBatcherImpl.class);

    final private static String LOWER_BOUND = "ML_LOWER_BOUND";
    final private static String UPPER_BOUND = "ML_UPPER_BOUND";

    private long batchSize = 0;
    private long batchCount = 0;
    private BatchThreadPoolExecutor threadPool;
    private final AtomicLong batchNum = new AtomicLong(0);
    private final AtomicLong failedBatches = new AtomicLong(0);
    private final AtomicInteger runningThreads = new AtomicInteger(0);
    private RowBatchFailureListener[] failureListeners;
    private RowBatchSuccessListener[] sucessListeners;

    // TODO: remove inputPlan, schemaName, and viewName?
    private PlanBuilder.ModifyPlan inputPlan;
    private String schemaName;
    private String viewName;

    private RawPlanDefinition pagedPlan;
    private long rowCount = 0;

    private RowManager rowMgr;
    private ContentHandle<T> rowsHandle;
    private Class<T> rowsClass;
    private HostInfo[] hostInfos;

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
        super.withBatchSize(DEFAULT_BATCH_SIZE);
    }

    @Override
    public RowManager getRowManager() {
        return rowMgr;
    }

    @Override
    public RowBatcher<T> withBatchView(PlanBuilder.ModifyPlan inputPlan) {
        if (this.isStarted())
            throw new IllegalStateException("Cannot change batch view after the job is started.");
        analyzePlan(inputPlan);
        return this;
    }
    private void analyzePlan(PlanBuilder.ModifyPlan inputPlan) {
        if (inputPlan == null)
            throw new IllegalArgumentException("modify plan cannot be null");
        this.inputPlan = inputPlan;

        StringHandle initialPlan = inputPlan.export(new StringHandle().withFormat(Format.JSON));

        DatabaseClientImpl client = (DatabaseClientImpl) getPrimaryClient();
        JsonNode viewInfo = client.getServices().postResource(
           null, "internal/viewinfo", null, null, initialPlan, new JacksonHandle()
           ).get();
        // System.out.println(viewInfo.toPrettyString());

        JsonNode schemaNode = viewInfo.get("schemaName");
        this.schemaName = (schemaNode != null) ? schemaNode.asText(null) : null;
        this.viewName   = viewInfo.get("viewName").asText(null);
        this.rowCount   = viewInfo.get("rowCount").asLong(0);
        this.pagedPlan  = client.newRowManager().newRawPlanDefinition(new JacksonHandle(viewInfo.get("modifiedPlan")));
// TODO: also viewColumns? tableID?
    }

    @Override
    public RowBatcher<T> withBatchSize(int batchSize) {
        if (this.isStarted())
            throw new IllegalStateException("Cannot change batch size after the job is started.");
        super.withBatchSize(batchSize);
        return this;
    }
    @Override
    public RowBatcher<T> withThreadCount(int threadCount) {
        if (this.isStarted())
            throw new IllegalStateException("Cannot change thread count after the job is started.");
        super.withThreadCount(threadCount);
        return this;
    }

    @Override
    public RowBatcher<T> onSuccess(RowBatchSuccessListener listener) {
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
    public RowBatcher<T> onFailure(RowBatchFailureListener listener) {
        if (listener == null) {
            failureListeners = null;
        } else if (failureListeners == null || failureListeners.length == 0) {
            failureListeners = new RowBatchFailureListener[]{listener};
        } else {
            failureListeners = Arrays.copyOf(failureListeners, failureListeners.length + 1);
            failureListeners[failureListeners.length - 1] = listener;
        }
        return this;
    }

    @Override
    public RowBatcher<T> withJobId(String jobId) {
        super.setJobId(jobId);
        return this;
    }
    @Override
    public RowBatcher<T> withJobName(String jobName) {
        super.withJobName(jobName);
        return this;
    }

    @Override
    public RowBatcher<T> withConsistentSnapshot() {
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
    private void initRequestEvent(RowBatchEventImpl event) {
        event.withClient(getPrimaryClient());
        event.withJobTicket(getJobTicket());
    }
    private void notifySuccess(RowBatchSuccessListener.RowBatchResponseEvent<T> event) {
        if (sucessListeners == null || sucessListeners.length == 0) return;
        for (RowBatchSuccessListener sucessListener: sucessListeners) {
            try {
                sucessListener.processEvent(event);
            } catch(Throwable e) {
                logger.info("error in success listener: {}", e.toString());
            }
        }
    }
    private void notifyFailure(RowBatchFailureEventImpl event, Throwable throwable) {
        RowBatchFailureListener.BatchFailureDisposition priorDisposition = null;
        int priorMaxRetries = 0;
        // notify all failure listeners
        for (RowBatchFailureListener failureListener: failureListeners) {
            priorDisposition = event.getDisposition();
            priorMaxRetries  = event.getMaxRetries();

            try {
                failureListener.processFailure(event, throwable);
            } catch(Throwable e) {
                logger.info("error in failure listener: {}", e.toString());
            }

            int nextMaxRetries = event.getMaxRetries();
            if (priorMaxRetries < nextMaxRetries) {
                event.withMaxRetries(priorMaxRetries);
            }

            RowBatchFailureListener.BatchFailureDisposition nextDisposition = event.getDisposition();
            if (priorDisposition != nextDisposition) {
                // in precedence order
                switch(priorDisposition) {
                    case SKIP:
                        break;
                    case RETRY:
                        if (nextDisposition == RowBatchFailureListener.BatchFailureDisposition.SKIP)
                            event.withDisposition(priorDisposition);
                        break;
                    case STOP:
                        event.withDisposition(priorDisposition);
                        break;
                    default:
                        throw new MarkLogicInternalException(
                                "unknown failure disposition: "+priorDisposition.toString()
                        );
                }
            }
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
        if (!this.isStarted()) {
            throw new IllegalStateException("Job not started.");
        }
        if (threadPool != null) {
            return threadPool.awaitTermination(timeout, unit);
        }
        return true;
    }

    @Override
    public long getRowEstimate() {
        if (this.pagedPlan == null)
            throw new IllegalStateException("Plan must be supplied before getting the row estimate");
        return this.rowCount;
    }
    @Override
    public long getBatchCount() {
        if (!this.isStarted()) {
            throw new IllegalStateException("Job not started.");
        }
        return this.batchNum.get();
    }
    @Override
    public long getFailedBatches() {
        if (!this.isStarted()) {
            throw new IllegalStateException("Job not started.");
        }
        return this.failedBatches.get();
    }

    @Override
    public JobTicket getJobTicket() {
        if (!this.isStarted()) {
            throw new IllegalStateException("Job not started.");
        }
        return super.getJobTicket();
    }

    @Override
    public void stop() {
// TODO: also set stop and jobEndTime after awaiting completion
        super.getStopped().set(true);
        if ( threadPool != null ) threadPool.shutdownNow();
        super.setJobEndTime();
    }

    @Override
    public synchronized void start(JobTicket ticket) {
        if (this.pagedPlan == null)
            throw new InternalError("Plan must be supplied before starting the job");
        if (threadPool != null) {
            logger.warn("job already started");
            return;
        }

        if (sucessListeners == null || sucessListeners.length == 0)
            throw new IllegalStateException("No listener for rows");

        if (failureListeners == null || failureListeners.length == 0) {
            logger.warn("starting job with default failure listener");
            onFailure((batch, throwable) -> {
                logger.warn("batch "+batch.getJobBatchNumber()+" failed with error: "+throwable.getMessage());
            });
        }

        if (super.getBatchSize() <= 0) {
            logger.warn("batchSize must be 1 or greater--setting batchSize to "+DEFAULT_BATCH_SIZE);
            super.withBatchSize(DEFAULT_BATCH_SIZE);
        }

        this.batchCount = (getRowEstimate() / super.getBatchSize()) + 1;
        this.batchSize = Long.divideUnsigned(MAX_UNSIGNED_LONG, this.batchCount);
        logger.info("batch count: {}, batch size: {}", batchCount, batchSize);

        BaseHandle<?,?> rowsBaseHandle = (BaseHandle<?,?>) rowsHandle;
        Format rowsFormat = rowsBaseHandle.getFormat();
        String rowsMimeType = rowsBaseHandle.getMimetype();

        this.threadPool = new BatchThreadPoolExecutor(super.getThreadCount());
        this.runningThreads.set(super.getThreadCount());
        for (int i=0; i<super.getThreadCount(); i++) {
            ContentHandle<T> threadHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(rowsClass);
            BaseHandle<?,?> threadBaseHandle = (BaseHandle<?,?>) threadHandle;
            threadBaseHandle.setFormat(rowsFormat);
            threadBaseHandle.setMimetype(rowsMimeType);
            RowBatchCallable<T> threadCallable = new RowBatchCallable<T>(this, threadHandle);
            submit(threadCallable);
        }

        super.setJobTicket(ticket);
        super.setJobStartTime();
        super.getStarted().set(true);
    }

    private boolean readRows(RowBatchCallable<T> callable) {
        long currentBatch = this.batchNum.incrementAndGet();
        if (currentBatch > this.batchCount) {
            endThread();
            return false;
        }

        // assumes a batch size of at least 2 to avoid unsigned overflow
        boolean isLastBatch = (currentBatch == this.batchCount);

        long lowerBound = (currentBatch - 1) * this.batchSize;

        String lowerBoundStr = Long.toUnsignedString(lowerBound);
        String upperBoundStr = Long.toUnsignedString(
                isLastBatch ? MAX_UNSIGNED_LONG : (lowerBound + (this.batchSize - 1))
        );

        logger.info("current batch: {}, lower bound: {}, upper bound: {}, last batch: {}",
                currentBatch, lowerBoundStr, upperBoundStr, isLastBatch);

        PlanBuilder.Plan plan = this.pagedPlan
                .bindParam(LOWER_BOUND, lowerBoundStr)
                .bindParam(UPPER_BOUND, upperBoundStr);
        ContentHandle<T> threadHandle = callable.getHandle();

        RowBatchFailureEventImpl requestEvent = null;
        for (int batchRetries = 0; shouldRequestBatch(requestEvent, batchRetries); batchRetries++) {
            Throwable throwable = null;
            T rowsDoc = null;
            try {
// TODO: should round-robin over available row managers (one per client in the non-gateway scenario)
                if (this.rowMgr.resultDoc(plan, (StructureReadHandle) threadHandle) != null) {
                    rowsDoc = threadHandle.get();
                }
            } catch(Throwable e) {
                throwable = e;
            }

            if (throwable != null) {
                logger.debug("failed for batch: {}, retry: {}", currentBatch, batchRetries);
                if (requestEvent == null) {
                    requestEvent = new RowBatchFailureEventImpl(
                            currentBatch, lowerBoundStr, upperBoundStr
                    );
                    initRequestEvent(requestEvent);
                }
                notifyFailure(
                    requestEvent
                        .withBatchRetries(batchRetries)
                        .withFailedJobBatches(this.getFailedBatches()),
                    throwable);
            // if the plan filters the rows, a batch could be empty
            } else if (rowsDoc != null) {
                RowBatchResponseEventImpl responseEvent = new RowBatchResponseEventImpl<>(
                        currentBatch, lowerBoundStr, upperBoundStr, rowsDoc
                );
                initRequestEvent(responseEvent);
                notifySuccess(responseEvent);
                if (requestEvent != null)
                    requestEvent = null;
                break;
            }
        }
        if (requestEvent != null) {
            this.failedBatches.incrementAndGet();
        }

        if (requestEvent != null && requestEvent.getDisposition() == RowBatchFailureListener.BatchFailureDisposition.STOP) {
// TODO: set stopped and end time
            logger.debug("stopped for failed batch: {}", currentBatch);
            this.threadPool.shutdown();
        } else if (isLastBatch) {
            logger.debug("last batch: {}", currentBatch);
            endThread();
        } else {
            logger.debug("finished batch: {}", currentBatch);
            this.submit(callable);
        }

        return (requestEvent == null);
    }
    private boolean shouldRequestBatch(RowBatchFailureEventImpl requestEvent, int batchRetries) {
        if (batchRetries == 0)      return true; // first request
        if (requestEvent == null) return false;  // request succeeded
        // retry request
        return (requestEvent.getDisposition() == RowBatchFailureListener.BatchFailureDisposition.RETRY &&
                batchRetries < requestEvent.getMaxRetries());
    }
    private void endThread() {
        int stillRunning = this.runningThreads.decrementAndGet();
        if (stillRunning == 0) {
            this.threadPool.shutdown();
        }
    }

    @Override
    public synchronized RowBatcher<T> withForestConfig(ForestConfiguration forestConfig) {
        super.withForestConfig(forestConfig);
        this.hostInfos = forestHosts(forestConfig, this.hostInfos);
        return this;
    }

    private void submit(Callable<Boolean> callable) {
        submit(new FutureTask(callable));
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

    static private class RowBatchEventImpl extends BatchEventImpl {
        private String lowerBound = "0";
        private String upperBound = "0";
        private RowBatchEventImpl(long batchnum, String lowerBound, String upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            withJobBatchNumber(batchnum);
        }
        public String getLowerBound() {
            return lowerBound;
        }
        public String getUpperBound() {
            return upperBound;
        }
    }
    static private class RowBatchFailureEventImpl extends RowBatchEventImpl
            implements RowBatchFailureListener.RowBatchFailureEvent {
        private final static int DEFAULT_MAX_RETRIES = 10;

        private RowBatchFailureListener.BatchFailureDisposition disposition;
        private int  maxRetries       = DEFAULT_MAX_RETRIES;
        private int  batchRetries     = 0;
        private long failedJobBatches = 0;
        private RowBatchFailureEventImpl(long batchnum, String lowerBound, String upperBound) {
            super(batchnum, lowerBound, upperBound);
            disposition = RowBatchFailureListener.BatchFailureDisposition.SKIP;
        }

        @Override
        public int getBatchRetries() {
            return this.batchRetries;
        }
        private RowBatchFailureEventImpl withBatchRetries(int batchRetries) {
            this.batchRetries = batchRetries;
            return this;
        }
        @Override
        public long getFailedJobBatches() {
            return this.failedJobBatches;
        }
        private RowBatchFailureEventImpl withFailedJobBatches(long failedJobBatches) {
            this.failedJobBatches = failedJobBatches;
            return this;
        }
        @Override
        public RowBatchFailureListener.BatchFailureDisposition getDisposition() {
            return this.disposition;
        }
        @Override
        public RowBatchFailureListener.RowBatchFailureEvent withDisposition(
                RowBatchFailureListener.BatchFailureDisposition disposition
        ) {
            this.disposition = disposition;
            return this;
        }
        @Override
        public int getMaxRetries() {
            return this.maxRetries;
        }
        @Override
        public RowBatchFailureEventImpl withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
    }
    static private class RowBatchResponseEventImpl<T> extends RowBatchEventImpl
            implements RowBatchSuccessListener.RowBatchResponseEvent<T> {
        private T handle;
        private RowBatchResponseEventImpl(long batchnum, String lowerBound, String upperBound, T handle) {
            super(batchnum, lowerBound, upperBound);
            this.handle = handle;
        }
        @Override
        public T getRowsDoc() {
            return handle;
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
