/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class RowBatcherImpl<T>  extends BatcherImpl implements RowBatcher<T> {
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
    private RowBatchSuccessListener[] successListeners;

    private RawPlanDefinition pagedPlan;
    private long rowCount = 0;

    private HostInfo[] hostInfos;

    private boolean consistentSnapshot = false;
    private final AtomicLong serverTimestamp = new AtomicLong(-1);

	private final ContentHandle<T> rowsHandle;
	private final RowManager defaultRowManager;

	RowBatcherImpl(DataMovementManagerImpl moveMgr, ContentHandle<T> rowsHandle) {
        super(moveMgr);
		validateRowsHandle(rowsHandle);
        this.rowsHandle = rowsHandle;

		defaultRowManager = getPrimaryClient().newRowManager();
        super.withBatchSize(DEFAULT_BATCH_SIZE);
        if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.DIRECT) {
            withForestConfig(moveMgr.getForestConfig());
        }
    }

	private void validateRowsHandle(ContentHandle<T> rowsHandle) {
		if (rowsHandle == null) {
			throw new IllegalArgumentException("Cannot create RowBatcher with null rows manager");
		}
		if (!(rowsHandle instanceof StructureReadHandle)) {
			throw new IllegalArgumentException("Rows handle must also be StructureReadHandle");
		}
		if (!(rowsHandle instanceof BaseHandle)) {
			throw new IllegalArgumentException("Rows handle must also be BaseHandle");
		}
		if (((BaseHandle) rowsHandle).getFormat() == Format.UNKNOWN) {
			throw new IllegalArgumentException("Rows handle must specify a format");
		}

		Class<T> rowsClass = rowsHandle.getContentClass();
		if (rowsClass == null) {
			throw new IllegalArgumentException("Rows handle cannot have a null content class");
		} else if (!DatabaseClientFactory.getHandleRegistry().isRegistered(rowsClass)) {
			throw new IllegalArgumentException("Rows handle must be registered with DatabaseClientFactory.HandleFactoryRegistry");
		}
	}

    @Override
    public RowManager getRowManager() {
        return defaultRowManager;
    }

    @Override
    public RowBatcher<T> withBatchView(PlanBuilder.ModifyPlan inputPlan) {
        if (inputPlan == null)
            throw new IllegalArgumentException("Plan cannot be null");
        analyzePlan(inputPlan.export(new StringHandle().withFormat(Format.JSON)));
        return this;
    }
    @Override
    public RowBatcher<T> withBatchView(RawPlanDefinition viewPlan) {
        if (viewPlan == null)
            throw new IllegalArgumentException("Raw plan definition cannot be null");
        analyzePlan(viewPlan.getHandle());
        return this;
    }
    @Override
    public RowBatcher<T> withBatchView(RawQueryDSLPlan viewPlan) {
        if (viewPlan == null)
            throw new IllegalArgumentException("Raw query DSL plan cannot be null");
        analyzePlan(viewPlan.getHandle());
        return this;
    }

	/**
	 * Calls the MarkLogic internal/viewinfo endpoint to obtain two critical items - the estimate of matching rows,
	 * and a modified version of the user's plan that includes "lower bounds" and "upper bounds" parameters. The
	 * estimate of matching rows allows for partitions to be defined based on the user-provided thread count.
	 * The user's modified plan is then run with a lower/upper bounds row ID value based on the calculated partitions.
	 *
	 * @param userPlan
	 */
    private void analyzePlan(AbstractWriteHandle userPlan) {
        requireNotStarted("Must specify batch view before starting job");

        DatabaseClientImpl client = (DatabaseClientImpl) getPrimaryClient();
        JsonNode viewInfo = client.getServices().postResource(
           null, "internal/viewinfo", null, null, userPlan, new JacksonHandle()
           ).get();

        this.rowCount   = viewInfo.get("rowCount").asLong(0);
        this.pagedPlan  = getRowManager().newRawPlanDefinition(new JacksonHandle(viewInfo.get("modifiedPlan")));

		JsonNode schemaNode = viewInfo.get("schemaName");
        logger.info("plan analysis schema name: {}, view name: {}, row estimate: {}",
			(schemaNode != null) ? schemaNode.asText(null) : null,
			viewInfo.get("viewName").asText(null),
			this.rowCount
		);
    }

    @Override
    public RowBatcher<T> withBatchSize(int batchSize) {
        requireNotStarted("Must set batch size before starting job");
        super.withBatchSize(batchSize);
        return this;
    }
    @Override
    public RowBatcher<T> withThreadCount(int threadCount) {
        requireNotStarted("Must set thread count before starting job");
        super.withThreadCount(threadCount);
        return this;
    }

    @Override
    public RowBatcher<T> onSuccess(RowBatchSuccessListener listener) {
        requireNotStarted("Must set success listener before starting job");
        if (listener == null) {
            successListeners = null;
        } else if (successListeners == null || successListeners.length == 0) {
            successListeners = new RowBatchSuccessListener[]{listener};
        } else {
            successListeners = Arrays.copyOf(successListeners, successListeners.length + 1);
            successListeners[successListeners.length - 1] = listener;
        }
        return this;
    }
    @Override
    public RowBatcher<T> onFailure(RowBatchFailureListener listener) {
        requireNotStarted("Must set failure listener before starting job");
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
        requireNotStarted("Must set job id before starting job");
        super.setJobId(jobId);
        return this;
    }
    @Override
    public RowBatcher<T> withJobName(String jobName) {
        requireNotStarted("Must set job name before starting job");
        super.withJobName(jobName);
        return this;
    }

    @Override
    public RowBatcher<T> withConsistentSnapshot() {
        requireNotStarted("Must set consistent snapshot before starting job");
        if (!(rowsHandle instanceof BaseHandle)) {
            throw new IllegalStateException("Content handle for consistent snapshot must extend BaseHandle");
        }
        consistentSnapshot = true;
        return this;
    }

    @Override
    public RowBatchSuccessListener[] getSuccessListeners() {
        return successListeners;
    }
    @Override
    public RowBatchFailureListener[] getFailureListeners() {
        return failureListeners;
    }
    @Override
    public void setSuccessListeners(RowBatchSuccessListener... listeners) {
        requireNotStarted("Must set success listeners before starting job");
        this.successListeners = listeners;
    }
    @Override
    public void setFailureListeners(RowBatchFailureListener... listeners) {
        requireNotStarted("Must set failure listeners before starting job");
        this.failureListeners = listeners;
    }
    private void initRequestEvent(RowBatchEventImpl event) {
        event.withClient(getPrimaryClient());
        event.withJobTicket(getJobTicket());
    }
    private void notifySuccess(RowBatchSuccessListener.RowBatchResponseEvent<T> event) {
        if (successListeners == null || successListeners.length == 0) return;
        for (RowBatchSuccessListener successListener: successListeners) {
            try {
                successListener.processEvent(event);
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
        requireStarted("Must start job before awaiting completion");
        if (threadPool != null) {
            return threadPool.awaitTermination(timeout, unit);
        }
        return true;
    }

    @Override
    public long getRowEstimate() {
        if (this.pagedPlan == null) {
            throw new IllegalStateException("Must supply plan before getting the row estimate");
        }
        return this.rowCount;
    }
    @Override
    public long getBatchCount() {
        requireStarted("Must start job before getting batch count");
        return this.batchNum.get();
    }
    @Override
    public long getFailedBatches() {
        requireStarted("Must start job before getting failed batches");
        return this.failedBatches.get();
    }
    @Override
    public JobTicket getJobTicket() {
        requireStarted("Must start job before getting ticket");
        return super.getJobTicket();
    }
    @Override
    public Long getServerTimestamp() {
        long val = this.serverTimestamp.get();
        return val > -1 ? val : null;
    }
    private void requireNotStarted(String msg) {
        if (this.isStarted()) {
            throw new IllegalStateException(msg);
        }
    }
    private void requireStarted(String msg) {
        if (!this.isStarted()) {
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void stop() {
        if (isStoppedTrue()) return;
		setStoppedToTrue();
        if (threadPool != null) threadPool.shutdownNow();
        super.setJobEndTime();
    }
    private void orderlyStop() {
        if (isStoppedTrue()) return;
		setStoppedToTrue();
        if (threadPool != null) threadPool.shutdown();
        super.setJobEndTime();
    }

    @Override
    public synchronized void start(JobTicket ticket) {
        requireNotStarted("Job already started");

        if (this.pagedPlan == null)
            throw new IllegalStateException("Plan must be supplied before starting the job");

        if (successListeners == null || successListeners.length == 0)
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
		// It is not expected that batch size will be meaningful to a user. It is more likely to be confusing since it's
		// not the same value that a user would have provided via withBatchSize. And we don't want to log it when it's
		// -1, which will be the case for a single batch.
		if (logger.isDebugEnabled() && this.batchSize > 0) {
			logger.debug("batch count: {}, calculated batch size: {}", batchCount, batchSize);
		} else {
			logger.info("batch count: {}", batchCount);
		}

        if (this.hostInfos != null && getMoveMgr().getConnectionType() == DatabaseClient.ConnectionType.DIRECT) {
            RowManager.RowSetPart    datatypeStyle = getRowManager().getDatatypeStyle();
            RowManager.RowStructure structureStyle = getRowManager().getRowStructureStyle();
            for (HostInfo hostInfo: this.hostInfos) {
                hostInfo.rowMgr.setDatatypeStyle(datatypeStyle);
                hostInfo.rowMgr.setRowStructureStyle(structureStyle);
            }
        }

        this.threadPool = new BatchThreadPoolExecutor(super.getThreadCount());
        this.runningThreads.set(super.getThreadCount());

        super.setJobTicket(ticket);
        super.setJobStartTime();
		setStartedToTrue();

        for (int i=0; i<super.getThreadCount(); i++) {
            ContentHandle<T> threadHandle = rowsHandle.newHandle();
            RowBatchCallable<T> threadCallable = new RowBatchCallable<T>(this, threadHandle);
            if (i == 0 && consistentSnapshot) {
                // make the first call synchronously to establish the timestamp
                readRows(threadCallable);
            }
            submit(threadCallable);
        }
    }

    private boolean readRows(RowBatchCallable<T> callable) {
        // assumes a batch size of at least 2 to avoid unsigned overflow
        long currentBatch = this.batchNum.incrementAndGet();
        // submitted before reaching last batch
        if (currentBatch > this.batchCount) {
            endThread();
            return false;
        }

        long lowerBound = (currentBatch - 1) * this.batchSize;
        String lowerBoundStr = Long.toUnsignedString(lowerBound);
        String upperBoundStr = Long.toUnsignedString(
                (currentBatch == this.batchCount) ? MAX_UNSIGNED_LONG : (lowerBound + (this.batchSize - 1))
        );
        logger.debug("current batch: {}, lower bound: {}, upper bound: {}", currentBatch, lowerBoundStr, upperBoundStr);

        PlanBuilder.Plan plan = this.pagedPlan
                .bindParam(LOWER_BOUND, lowerBoundStr)
                .bindParam(UPPER_BOUND, upperBoundStr);
        ContentHandle<T> threadHandle = callable.getHandle();

        boolean isDirect =
                (this.hostInfos != null && getMoveMgr().getConnectionType() == DatabaseClient.ConnectionType.DIRECT);

        RowBatchFailureEventImpl requestEvent = null;
        for (int batchRetries = 0; shouldRequestBatch(requestEvent, batchRetries); batchRetries++) {
            RowManager requestRowMgr = isDirect ?
                    // batches round-robin over the direct hosts as do retries
                    this.hostInfos[(int) ((currentBatch + batchRetries) % hostInfos.length)].rowMgr :
                    this.getRowManager();

            Throwable throwable = null;
            T rowsDoc = null;
            try {
                BaseHandle baseThreadHandle = (BaseHandle) threadHandle;
                if (consistentSnapshot && baseThreadHandle.getPointInTimeQueryTimestamp() == -1) {
                  long snapshotTimestamp = serverTimestamp.get();
                  if (snapshotTimestamp > -1) {
                    logger.info("Initializing thread snapshot timestamp=[{}]", snapshotTimestamp);
                    baseThreadHandle.setPointInTimeQueryTimestamp(snapshotTimestamp);
                  }
                }
                if (requestRowMgr.resultDoc(plan, (StructureReadHandle) threadHandle) != null) {
                    rowsDoc = threadHandle.get();
                }
                if (consistentSnapshot && serverTimestamp.get() == -1) {
                    long snapshotTimestamp = baseThreadHandle.getServerTimestamp();
                    if (serverTimestamp.compareAndSet(-1, snapshotTimestamp)) {
                        logger.info("Established snapshot timestamp=[{}]", snapshotTimestamp);
                        baseThreadHandle.setPointInTimeQueryTimestamp(snapshotTimestamp);
                    } else {
                        logger.info("Correcting thread snapshot timestamp=[{}]", snapshotTimestamp);
                        baseThreadHandle.setPointInTimeQueryTimestamp(serverTimestamp.get());
                    }
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
            logger.debug("stopped for failed batch: {}", currentBatch);
            this.orderlyStop();
        } else {
            logger.debug("finished batch: {}", currentBatch);
            if (this.batchNum.get() >= this.batchCount) {
                logger.debug("finished thread after batch: {}", currentBatch);
                endThread();
            } else {
                this.submit(callable);
            }
        }

        return (requestEvent == null);
    }
    private boolean shouldRequestBatch(RowBatchFailureEventImpl requestEvent, int batchRetries) {
        if (batchRetries == 0)        return true;  // first request
        if (requestEvent == null)     return false; // request succeeded
        if (isStoppedTrue()) return false; // stopped
        // whether to retry request
        return (requestEvent.getDisposition() == RowBatchFailureListener.BatchFailureDisposition.RETRY &&
                batchRetries < requestEvent.getMaxRetries());
    }
    private void endThread() {
        int stillRunning = this.runningThreads.decrementAndGet();
        if (stillRunning == 0) {
            this.orderlyStop();
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
            try {
                return rowBatcher.readRows(this);
            } catch(Throwable e) {
                logger.error("internal error", e);
                return false;
            }
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

        if (hostInfos != null) {
            for (HostInfo hostInfo : hostInfos) {
                existingHostInfos.put(hostInfo.hostName, hostInfo);
            }
        }
        logger.info("(withForestConfig) Using forests on {} hosts for \"{}\"", hosts, forests[0].getDatabaseName());
        // initialize a DatabaseClient for each host
        HostInfo[] newHostInfos = new HostInfo[hosts.size()];
        int i = 0;
        for (String host : hosts) {
            HostInfo existingHost = existingHostInfos.get(host);
            if (existingHost != null) {
                newHostInfos[i] = existingHost;
            } else {
                existingHost = new HostInfo();
                newHostInfos[i] = existingHost;
                existingHost.hostName = host;
                // this is a host-specific client (no DatabaseClient is actually forest-specific)
                existingHost.client = getMoveMgr().getHostClient(host);
                if (getMoveMgr().getConnectionType() == DatabaseClient.ConnectionType.DIRECT) {
                    logger.info("Adding DatabaseClient on port {} for host \"{}\" to the rotation",
                            newHostInfos[i].client.getPort(), host);
                    existingHost.rowMgr = existingHost.client.newRowManager();
                }
            }
            i++;
        }

        return newHostInfos;
    }

    private static class HostInfo {
        private String hostName;
        private DatabaseClient client;
        private RowManager rowMgr;
    }
}
