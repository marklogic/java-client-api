/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.QueryFailureListener;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.QueryBatchException;
import com.marklogic.client.datamovement.QueryEvent;
import com.marklogic.client.datamovement.QueryBatcherListener;
import com.marklogic.client.impl.*;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.SearchQueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/* For implementation explanation, see the comments below above startQuerying,
 * startIterating, withForestConfig, and retry.
 */
public class QueryBatcherImpl extends BatcherImpl implements QueryBatcher {
  private static Logger logger = LoggerFactory.getLogger(QueryBatcherImpl.class);
  private String queryMethod;
  private SearchQueryDefinition query;
  private SearchQueryDefinition originalQuery;
  private Boolean filtered;
  private Iterator<String> iterator;
  private boolean threadCountSet = false;
  private List<QueryBatchListener> urisReadyListeners = new ArrayList<>();
  private List<QueryFailureListener> failureListeners = new ArrayList<>();
  private List<QueryBatcherListener> jobCompletionListeners = new ArrayList<>();
  private QueryThreadPoolExecutor threadPool;
  private boolean consistentSnapshot = false;
  private final AtomicLong batchNumber = new AtomicLong(0);
  private final AtomicLong resultsSoFar = new AtomicLong(0);
  private final AtomicLong serverTimestamp = new AtomicLong(-1);
  private final AtomicReference<List<DatabaseClient>> clientList = new AtomicReference<>();
  private Map<Forest,AtomicLong> forestResults = new HashMap<>();
  private Map<Forest,AtomicBoolean> forestIsDone = new HashMap<>();
  private Map<Forest, AtomicInteger> retryForestMap = new HashMap<>();
  private AtomicBoolean runJobCompletionListeners = new AtomicBoolean(false);
  private final Object lock = new Object();
  private final Map<Forest,List<QueryTask>> blackListedTasks = new HashMap<>();
  private boolean isSingleThreaded = false;
  private long maxUris = Long.MAX_VALUE;
  private long maxBatches = Long.MAX_VALUE;
  private int maxDocToUriBatchRatio;
  private int docToUriBatchRatio;
  private int defaultDocBatchSize;
  private int maxUriBatchSize;
  private int threadThrottleFactor;

  QueryBatcherImpl(
          SearchQueryDefinition originalQuery, DataMovementManager moveMgr, ForestConfiguration forestConfig,
          String serializedCtsQuery, Boolean filtered, int maxDocToUriBatchRatio, int defaultDocBatchSize, int maxUriBatchSize
  ) {
    this(moveMgr, forestConfig, maxDocToUriBatchRatio, defaultDocBatchSize, maxUriBatchSize);
    // TODO:  skip conversion in DataMovementManagerImpl.newQueryBatcherImpl() unless canSerializeQueryAsJSON()
    if (serializedCtsQuery != null && serializedCtsQuery.length() > 0 &&
            originalQuery instanceof AbstractSearchQueryDefinition &&
            ((AbstractSearchQueryDefinition) originalQuery).canSerializeQueryAsJSON()) {
      QueryManagerImpl queryMgr = (QueryManagerImpl) getPrimaryClient().newQueryManager();
      this.queryMethod = "POST";
      this.query = queryMgr.newRawCtsQueryDefinition(new StringHandle(serializedCtsQuery).withFormat(Format.JSON));
      this.originalQuery = originalQuery;
      if (filtered != null) {
        this.filtered = filtered;
      }
    } else {
      initQuery(originalQuery);
    }
  }
  public QueryBatcherImpl(SearchQueryDefinition query, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    this(moveMgr, forestConfig);
    initQuery(query);
  }
  public QueryBatcherImpl(Iterator<String> iterator, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    this(moveMgr, forestConfig);
    this.iterator = iterator;
  }
  private QueryBatcherImpl(DataMovementManager moveMgr, ForestConfiguration forestConfig,
                           int maxDocToUriBatchRatio, int defaultDocBatchSize, int maxUriBatchSize) {
    this(moveMgr, forestConfig);
    this.maxDocToUriBatchRatio = maxDocToUriBatchRatio;
    this.defaultDocBatchSize = defaultDocBatchSize;
    this.maxUriBatchSize = maxUriBatchSize;
    withBatchSize(defaultDocBatchSize);
  }
  private QueryBatcherImpl(DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super(moveMgr);
    withForestConfig(forestConfig);
  }
  public ThreadPoolExecutor getThreadPool () { //for testing purpose
    return threadPool;
  }
  private void initQuery(SearchQueryDefinition query) {
    if (query == null) {
      throw new IllegalArgumentException("Cannot create QueryBatcher with null query");
    }
    // post if the effective version is at least 10.0-5
    this.queryMethod =
        (Long.compareUnsigned(getMoveMgr().getServerVersion(), Long.parseUnsignedLong("10000500")) >= 0) ?
        "POST" : "GET";
    this.query = query;
    if (query instanceof RawQueryDefinition) {
      RawQueryDefinition rawQuery = (RawQueryDefinition) query;

      StructureWriteHandle handle = rawQuery.getHandle();

      @SuppressWarnings("rawtypes")
      HandleImplementation baseHandle = HandleAccessor.checkHandle(handle, "queryBatcher");

      Format inputFormat = baseHandle.getFormat();
      switch(inputFormat) {
        case UNKNOWN:
          baseHandle.setFormat(Format.XML);
          break;
        case JSON:
        case XML:
          break;
        default:
          throw new UnsupportedOperationException("Only XML and JSON raw query definitions are possible.");
      }
    }
  }

  @Override
  public QueryBatcherImpl onUrisReady(QueryBatchListener listener) {
    if ( listener == null ) throw new IllegalArgumentException("listener must not be null");
    urisReadyListeners.add(listener);
    return this;
  }

  @Override
  public QueryBatcherImpl onQueryFailure(QueryFailureListener listener) {
    if ( listener == null ) throw new IllegalArgumentException("listener must not be null");
    failureListeners.add(listener);
    return this;
  }

  /* Accepts a QueryEvent (usually a QueryBatchException sent to a
   * onQueryFailure listener) and retries that task.  If the task succeeds, it
   * will spawn the task for the next page in the result set, which will spawn
   * the task for the next page, etc.  A failure in this attempt will not call
   * onQueryFailure listeners (as that might lead to infinite recursion since
   * this is usually called by an onQueryFailure listener), but instead will
   * directly throw the Exception.  In order to use the latest
   * ForestConfiguration yet still query the correct forest for this
   * QueryEvent, we look for a forest from the current ForestConfiguration
   * which has the same forest id, then we use the preferred host for the
   * forest from the current ForestConfiguration.  If the current
   * ForestConfiguration does not have a matching forest, this method throws
   * IllegalStateException.  This works perfectly with the approach used by
   * HostAvailabilityListener of black-listing unavailable hosts then retrying
   * the QueryEvent that failed.
   */
  @Override
  public void retry(QueryEvent queryEvent) {
    retry(queryEvent, false);
  }

  @Override
  public void retryWithFailureListeners(QueryEvent queryEvent) {
    retry(queryEvent, true);
  }

  private void retry(QueryEvent queryEvent, boolean callFailListeners) {
    if ( isStopped() == true ) {
      logger.warn("Job is now stopped, aborting the retry");
      return;
    }
    Forest retryForest = null;
    for ( Forest forest : getForestConfig().listForests() ) {
      if ( forest.equals(queryEvent.getForest()) ) {
        // while forest and queryEvent.getForest() have equivalent forest id,
        // we expect forest to have the currently available host info
        retryForest = forest;
        break;
      }
    }
    if ( retryForest == null ) {
      throw new IllegalStateException("Forest for queryEvent (" + queryEvent.getForest().getForestName() +
        ") is not in current getForestConfig()");
    }
    // we're obviously not done with this forest
    forestIsDone.get(retryForest).set(false);
    retryForestMap.get(retryForest).incrementAndGet();
    long start = queryEvent.getForestResultsSoFar() + 1;
    logger.trace("retryForest {} on retryHost {} at start {}",
      retryForest.getForestName(), retryForest.getPreferredHost(), start);
    QueryTask runnable = new QueryTask(getMoveMgr(), this, retryForest, queryMethod, query, filtered,
      queryEvent.getForestBatchNumber(), start, null, queryEvent.getLastUriForForest(),
      queryEvent.getJobBatchNumber(), callFailListeners);
    runnable.run();
  }
  /*
   * Accepts a QueryBatch which was successfully retrieved from the server and a 
   * QueryBatchListener which was failed to apply and retry that listener on the batch. 
   * 
   */
  @Override
  public void retryListener(QueryBatch batch, QueryBatchListener queryBatchListener) {
    // We get the batch and modify the client alone in order to make use
    // of the new forest client in case if the original host is unavailable.
    DatabaseClient client = null;
    Forest[] forests = batch.getBatcher().getForestConfig().listForests();
    for(Forest forest : forests) {
      if(forest.equals(batch.getForest()))
        client = getMoveMgr().getForestClient(forest);
    }
    QueryBatchImpl retryBatch = new QueryBatchImpl()
        .withClient( client )
        .withBatcher( batch.getBatcher() )
        .withTimestamp( batch.getTimestamp() )
        .withServerTimestamp( batch.getServerTimestamp() )
        .withItems( batch.getItems() )
        .withJobTicket( batch.getJobTicket() )
        .withJobBatchNumber( batch.getJobBatchNumber() )
        .withJobResultsSoFar( batch.getJobResultsSoFar() )
        .withForestBatchNumber( batch.getForestBatchNumber() )
        .withForestResultsSoFar( batch.getForestResultsSoFar() )
        .withForest( batch.getForest() )
        .withJobTicket( batch.getJobTicket() );
    queryBatchListener.processEvent(retryBatch);
  }

  @Override
  public QueryBatchListener[]   getQuerySuccessListeners() {
    return getUrisReadyListeners();
  }

  @Override
  public QueryBatchListener[]   getUrisReadyListeners() {
    return urisReadyListeners.toArray(new QueryBatchListener[urisReadyListeners.size()]);
  }

  @Override
  public QueryFailureListener[] getQueryFailureListeners() {
    return failureListeners.toArray(new QueryFailureListener[failureListeners.size()]);
  }

  @Override
  public void setUrisReadyListeners(QueryBatchListener... listeners) {
    requireNotStarted();
    urisReadyListeners.clear();
    if ( listeners != null ) {
      for ( QueryBatchListener listener : listeners ) {
        urisReadyListeners.add(listener);
      }
    }
  }

  @Override
  public void setQueryFailureListeners(QueryFailureListener... listeners) {
    requireNotStarted();
    failureListeners.clear();
    if ( listeners != null ) {
      for ( QueryFailureListener listener : listeners ) {
        failureListeners.add(listener);
      }
    }
  }

  @Override
  public QueryBatcher withJobName(String jobName) {
    requireNotStarted();
    super.withJobName(jobName);
    return this;
  }

  @Override
  public QueryBatcher withJobId(String jobId) {
    requireNotStarted();
    setJobId(jobId);
    return this;
  }

  @Override
  public QueryBatcher withBatchSize(int docBatchSize) {
    if (docBatchSize > this.maxUriBatchSize) {
      logger.debug("docBatchSize is beyond maxDocBatchSize, which is {}.", this.maxUriBatchSize);
    }
    if (docBatchSize < 1) {
      throw new IllegalArgumentException("docBatchSize cannot be less than 1");
    }
    requireNotStarted();
    super.withBatchSize(docBatchSize);
    this.docToUriBatchRatio = Math.min(this.maxDocToUriBatchRatio, this.maxUriBatchSize / docBatchSize);
    if (this.docToUriBatchRatio == 0) {
      this.docToUriBatchRatio = 1;
    }
    this.threadThrottleFactor = 0;
    return this;
  }

  @Override
  public QueryBatcher withBatchSize(int docBatchSize, int docToUriBatchRatio) {
    if (docToUriBatchRatio > this.maxDocToUriBatchRatio) {
      throw new IllegalArgumentException("docToUriBatchRatio is beyond maxDocToUriBatchRatio");
    }
    if (docBatchSize * docToUriBatchRatio > this.maxUriBatchSize) {
      throw new IllegalArgumentException("docToUriBatchRatio is beyond maxUriBatchSize/docBatchSize");
    }
    if (docToUriBatchRatio < 1) {
      throw new IllegalArgumentException("docToUriBatchRatio is less than 1");
    }
    withBatchSize(docBatchSize);
    this.docToUriBatchRatio = docToUriBatchRatio;
    return this;
  }

/*  @Override
  public QueryBatcher withBatchSize(int docBatchSize, int docToUriBatchRatio, int threadThrottleFactor) {
    if (threadThrottleFactor < 0 || threadThrottleFactor > this.maxDocToUriBatchRatio) {
      throw new IllegalArgumentException("threadThrottleFactor is less than 0 or " +
              "threadThrottleFactor is larger than maxDocToUriBatchRatio");
    }
    if (threadThrottleFactor >= docToUriBatchRatio) {
      throw new IllegalArgumentException("threadThrottleFactor must be less than docToUriBatchRatio");
    }
    withBatchSize(docBatchSize, docToUriBatchRatio);
    this.threadThrottleFactor = threadThrottleFactor;
    return this;
  }*/

  @Override
  public int getDocToUriBatchRatio() {
    return this.docToUriBatchRatio;
  }

//  @Override
  public int getThreadThrottleFactor() {
    return this.threadThrottleFactor;
  }

  @Override
  public int getDefaultDocBatchSize() {
    return this.defaultDocBatchSize;
  }

  @Override
  public int getMaxUriBatchSize() {
    return this.maxUriBatchSize;
  }

  @Override
  public int getMaxDocToUriBatchRatio() {
    return this.maxDocToUriBatchRatio;
  }

  @Override
  public QueryBatcher withThreadCount(int threadCount) {
    requireNotStarted();
    if ( getThreadCount() <= 0 ) {
      throw new IllegalArgumentException("threadCount must be 1 or greater");
    }
    threadCountSet = true;
    super.withThreadCount(threadCount);
    return this;
  }

  @Override
  public QueryBatcher withConsistentSnapshot() {
    requireNotStarted();
    consistentSnapshot = true;
    return this;
  }

  @Override
  public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
    requireJobStarted();
    return threadPool.awaitTermination(timeout, unit);
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
  public boolean isStopped() {
    return threadPool != null && threadPool.isTerminated();
  }

  @Override
  public JobTicket getJobTicket() {
    requireJobStarted();
    return super.getJobTicket();
  }

  private void requireJobStarted() {
    if ( threadPool == null ) {
      throw new IllegalStateException("Job not started. First call DataMovementManager.startJob(QueryBatcher)");
    }
  }

  private void requireNotStarted() {
    if ( threadPool != null ) {
      throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    }
  }

  @Override
  public synchronized void start(JobTicket ticket) {
    if ( threadPool != null ) {
      logger.warn("startJob called more than once");
      return;
    }
    if ( getBatchSize() <= 0 ) {
      withBatchSize(1);
      logger.warn("docBatchSize should be 1 or greater--setting docBatchSize to 1");
    }
    super.setJobTicket(ticket);
    initialize();
    for (QueryBatchListener urisReadyListener : urisReadyListeners) {
      urisReadyListener.initializeListener(this);
    }
    super.setJobStartTime();
    super.getStarted().set(true);
    if(this.maxBatches < Long.MAX_VALUE) {
    	setMaxUris(getMaxBatches());
    }
    if (query != null) {
      startQuerying();
    } else if (iterator != null) {
      startIterating();
    } else {
      throw new IllegalStateException("Cannot start QueryBatcher without query or iterator");
    }
  }

  private synchronized void initialize() {
    if ( threadCountSet == false ) {
      if ( query != null ) {
        Forest[] forests = getForestConfig().listForests();
        logger.warn("threadCount not set--defaulting to number of forests ({})", forests.length);
        withThreadCount(forests.length * (docToUriBatchRatio - threadThrottleFactor));
      } else {
        int hostCount = clientList.get().size();
        logger.warn("threadCount not set--defaulting to number of hosts ({})", hostCount);
        withThreadCount( hostCount );
      }
      // now we've set the threadCount
      threadCountSet = true;
    }
    // If we are iterating and if we have the thread count to 1, we have a single thread acting as both
    // consumer and producer of the ThreadPoolExecutor queue. Hence, we produce till the maximum and start
    // consuming and produce again. Since the thread count is 1, there is no worry about thread utilization.
    if(getThreadCount() == 1) {
      isSingleThreaded = true;
    }
    logger.info("Starting job docBatchSize={}, docToUriBatchRatio={}, threadThrottleFactor= {}, threadCount={}, " +
                    "onUrisReady listeners={}, failure listeners={}",
      getBatchSize(), getDocToUriBatchRatio(), getThreadThrottleFactor(), getThreadCount(),
            urisReadyListeners.size(), failureListeners.size());
    threadPool = new QueryThreadPoolExecutor(getThreadCount(), this);
  }

  /* When withForestConfig is called before the job starts, it just provides
   * the list of forests (and thus hosts) to talk to.  When withForestConfig is
   * called mid-job, every attempt is made to switch any queued or future task
   * to use the new ForestConfiguration.  This allows monitoring listeners like
   * HostAvailabilityListener to black-list hosts immediately when a host is
   * detected to be unavailable.  In theory customer listeners could do even
   * more advanced monitoring.  By decoupling the monitoring from the task
   * management, all a listener has to do is inform us what forests and what
   * hosts to talk to (by calling withForestConfig), and we'll manage ensuring
   * any queued or future tasks only talk to those forests and hosts.  We
   * update clientList with a DatabaseClient per host which is used for
   * round-robin communication by startIterating (the version of QueryBatcher
   * that accepts an Iterator<String>).  We also loop through any queued tasks
   * and point them to hosts and forests that are in the new
   * ForestConfiguration.  If any queued tasks point to forests that are
   * missing from the new ForestConfiguration, those tasks are held in
   * blackListedTasks on the assumption that those tasks can be restarted once
   * those forests come back online.  If withForestConfig is called later with
   * those forests back online, those tasks will be restarted.  If the job
   * finishes before those forests come back online (and are provided this job
   * by a call to withForestConfig), then any blackListedTasks are left
   * unfinished and it's likely that not all documents that should have matched
   * the query will be processed.  The only solution to this is to have a
   * cluster that is available during the job run (or if there's an outage, it
   * gets resolved during the job run).  Simply put, there's no way for a job to
   * get documents from unavailable forests.
   *
   * If the ForestConfiguration provides new forests, jobs will be started to
   * get documenst from those forests (the code is in cleanupExistingTasks).
   */
  @Override
  public synchronized QueryBatcher withForestConfig(ForestConfiguration forestConfig) {
    super.withForestConfig(forestConfig);
    Forest[] forests = forests(forestConfig);
    Set<Forest> oldForests = new HashSet<>(forestResults.keySet());
    Map<String,Forest> hosts = new HashMap<>();
    for ( Forest forest : forests ) {
      if ( forest.getPreferredHost() == null ) throw new IllegalStateException("Hostname must not be null for any forest");
      hosts.put(forest.getPreferredHost(), forest);
      if ( forestResults.get(forest) == null ) forestResults.put(forest, new AtomicLong());
      if ( forestIsDone.get(forest) == null  ) forestIsDone.put(forest, new AtomicBoolean(false));
      if ( retryForestMap.get(forest) == null ) retryForestMap.put(forest, new AtomicInteger(0));
    }
    Set<String> hostNames = hosts.keySet();
    logger.info("(withForestConfig) Using forests on {} hosts for \"{}\"", hostNames, forests[0].getDatabaseName());
    List<DatabaseClient> newClientList = clients(hostNames);
    clientList.set(newClientList);
    boolean started = (threadPool != null);
    if ( started == true && oldForests.size() > 0 ) calculateDeltas(oldForests, forests);
    return this;
  }

  private synchronized void calculateDeltas(Set<Forest> oldForests, Forest[] forests) {
    // the forests we haven't known about yet
    Set<Forest> addedForests = new HashSet<>();
    // the forests that we knew about but they were black-listed and are no longer black-listed
    Set<Forest> restartedForests = new HashSet<>();
    // any known forest might now be black-listed
    Set<Forest> blackListedForests = new HashSet<>(oldForests);
    for ( Forest forest : forests ) {
      if ( ! oldForests.contains(forest) ) {
        // we need to do special handling since we're adding this new forest after we're started
        addedForests.add(forest);
      }
      // if we have blackListedTasks for this forest, let's restart them
      if ( blackListedTasks.get(forest) != null ) restartedForests.add(forest);
      // this forest is not black-listed
      blackListedForests.remove(forest);
    }
    if ( blackListedForests.size() > 0 ) {
      DataMovementManagerImpl moveMgrImpl = getMoveMgr();
      String primaryHost = moveMgrImpl.getPrimaryClient().getHost();
      if ( getHostNames(blackListedForests).contains(primaryHost) ) {
        int randomPos = new Random().nextInt(clientList.get().size());
        moveMgrImpl.setPrimaryClient(clientList.get().get(randomPos));
      }
    }
    cleanupExistingTasks(addedForests, restartedForests, blackListedForests);
  }

  private synchronized void cleanupExistingTasks(Set<Forest> addedForests, Set<Forest> restartedForests, Set<Forest> blackListedForests) {
    if ( blackListedForests.size() > 0 ) {
      logger.warn("removing jobs related to hosts [{}] from the queue", getHostNames(blackListedForests));
      // since some forests have been removed, let's remove from the queue any jobs that were targeting that forest
      List<Runnable> tasks = new ArrayList<>();
      threadPool.getQueue().drainTo(tasks);
      for ( Runnable task : tasks ) {
        if ( task instanceof QueryTask ) {
          QueryTask queryTask = (QueryTask) task;
          if ( blackListedForests.contains(queryTask.forest) ) {
            // this batch was targeting a forest that's no longer on the list
            // so keep track of it in case this forest comes back on-line
            List<QueryTask> blackListedTaskList = blackListedTasks.get(queryTask.forest);
            if ( blackListedTaskList == null ) {
              blackListedTaskList = new ArrayList<QueryTask>();
              blackListedTasks.put(queryTask.forest, blackListedTaskList);
            }
            blackListedTaskList.add(queryTask);
            // jump to the next task
            continue;
          }
        }
        // this task is still valid so add it back to the queue
        threadPool.execute(task);
      }
    }
    if ( addedForests.size() > 0 ) {
      logger.warn("adding jobs for forests [{}] to the queue", getForestNames(addedForests));
    }
    for ( Forest forest : addedForests ) {
      // we don't need to worry about consistentSnapshotFirstQueryHasRun because that's already done
      // or we wouldn't be here because we wouldn't have a synchronized lock on this
      threadPool.execute(new QueryTask(
          getMoveMgr(), this, forest, queryMethod, query, filtered, 1, 1, null
      ));
    }
    if ( restartedForests.size() > 0 ) {
      logger.warn("re-adding jobs related to forests [{}] to the queue", getForestNames(restartedForests));
    }
    for ( Forest forest : restartedForests ) {
      List<QueryTask> blackListedTaskList = blackListedTasks.get(forest);
      if ( blackListedTaskList != null ) {
        // let's start back up where we left off
        for ( QueryTask task : blackListedTaskList ) {
          threadPool.execute(task);
        }
        // we can clear blackListedTaskList because we have a synchronized lock
        blackListedTaskList.clear();
      }
    }
  }

  private List<String> getForestNames(Collection<Forest> forests) {
    return forests.stream().map((forest)->forest.getForestName()).collect(Collectors.toList());
  }

  private List<String> getHostNames(Collection<Forest> forests) {
    return forests.stream().map((forest)->forest.getPreferredHost()).distinct().collect(Collectors.toList());
  }

  /* All we do to startQuerying is create a task per forest that queries that
   * forest for the first page of results, and process part of the results.
   * Then spawns (docToUriBatchRatio - 1) tasks to process other results. After
   * that spawns a new task to query for the next page of results, etc.
   * Tasks are handled by threadPool which is a
   * slightly modified ThreadPoolExecutor with threadCount threads.  We don't
   * know whether we're at the end of the result set from a forest until we get
   * the last batch that isn't full (batch size != uri batch size).  Therefore, any
   * error to retrieve a batch might prevent us from getting the next batch and
   * all remaining batches.  To mitigate the risk of one error effectively
   * cancelling the rest of the pagination for that forest,
   * HostAvailabilityListener is configured to retry any batch that encounters
   * a "host unavailable" error (see HostAvailabilityListener for more
   * details).  HostAvailabilityListener is also intended to act as an example
   * so comparable client-specific listeners can be built to handle other
   * failure scenarios and retry those batches.
   */
  private synchronized void startQuerying() {
    Forest[] forests = getForestConfig().listForests();
    boolean runInApplicationThread = (consistentSnapshot && forests.length > 1);
    for (Forest forest:  forests) {
      QueryTask runnable = new QueryTask(
              getMoveMgr(), this, forest, queryMethod, query, filtered, 1, 1, null
      );
      if ( runInApplicationThread) {
        // let's run this first time in-line so we'll have the serverTimestamp set
        // before we launch all the parallel threads
        runInApplicationThread = false;
        runnable.run();
      } else {
        threadPool.execute(runnable);
      }
    }
  }

  private class QueryTask implements Runnable {
    private DataMovementManager moveMgr;
    private QueryBatcherImpl batcher;
    private Forest forest;
    private String queryMethod;
    private SearchQueryDefinition query;
    private Boolean filtered;
    private long forestBatchNum;
    private long start;
    private long retryBatchNumber;
    private boolean callFailListeners;
    private String afterUri;
    private String nextAfterUri;
    boolean isQueryBatch;
    private QueryBatchImpl batch;
    private int totalProcessedCount = 0;
    private boolean isLastBatch;
    private int lastBatchNum;

    QueryTask(DataMovementManager moveMgr, QueryBatcherImpl batcher, Forest forest,
        String queryMethod, SearchQueryDefinition query, Boolean filtered, long forestBatchNum, long start, QueryBatchImpl batch
              ) {
      this(moveMgr, batcher, forest, queryMethod, query, filtered, forestBatchNum, start, batch, null, -1, true);
    }
    QueryTask(DataMovementManager moveMgr, QueryBatcherImpl batcher, Forest forest,
        String queryMethod, SearchQueryDefinition query, Boolean filtered, long forestBatchNum, long start, QueryBatchImpl batch, String afterUri
    ) {
      this(moveMgr, batcher, forest, queryMethod, query, filtered, forestBatchNum, start, batch, afterUri,
              -1, true);
    }
    QueryTask(DataMovementManager moveMgr, QueryBatcherImpl batcher, Forest forest,
        String queryMethod, SearchQueryDefinition query, Boolean filtered, long forestBatchNum, long start,
              QueryBatchImpl batch, String afterUri, long retryBatchNumber, boolean callFailListeners
    ) {
      this.moveMgr = moveMgr;
      this.batcher = batcher;
      this.forest = forest;
      this.queryMethod = queryMethod;
      this.query = query;
      this.filtered = filtered;
      this.forestBatchNum = forestBatchNum;
      this.start = start;
      this.isQueryBatch = isQueryBatch;
      this.retryBatchNumber = retryBatchNumber;
      this.callFailListeners = callFailListeners;
      this.batch = batch;

      // ignore the afterUri if the effective version is less than 9.0-9
      if (Long.compareUnsigned(getMoveMgr().getServerVersion(), Long.parseUnsignedLong("9000900")) >= 0) {
        this.afterUri = afterUri;
      }
    }

    public void run() {
      // don't proceed if this job is stopped (because dataMovementManager.stopJob was called)
      if (batcher.getStopped().get() == true) {
        logger.warn("Cancelling task to query forest '{}' forestBatchNum {} with start {} after the job is stopped",
                forest.getForestName(), forestBatchNum, start);
        return;
      }
      DatabaseClient client = getMoveMgr().getForestClient(forest);
      Calendar queryStart = Calendar.getInstance();
      List<List<String>> uris = new ArrayList<>();
      AtomicBoolean isDone = forestIsDone.get(forest);
      boolean hasLastBatch = false;
      int lastBatchNum = 0;

      if (this.batch == null) { // if it's query batch
        this.batch = new QueryBatchImpl()
                .withBatcher(batcher)
                .withClient(client)
                .withTimestamp(queryStart)
                .withJobTicket(getJobTicket())
                .withForestBatchNumber(forestBatchNum)
                .withForest(forest);

        QueryManagerImpl queryMgr = (QueryManagerImpl) client.newQueryManager();
        queryMgr.setPageLength(getBatchSize() * getDocToUriBatchRatio());
        UrisHandle handle = new UrisHandle();

        if (consistentSnapshot == true && serverTimestamp.get() > -1) {
          handle.setPointInTimeQueryTimestamp(serverTimestamp.get());
        }
        // this try-with-resources block will call results.close() once the block is done
        // here we call the /v1/internal/uris endpoint to get the text/uri-list of documents
        // matching this structured or string query
        try (UrisHandle results = queryMgr.uris(queryMethod, query, filtered, handle, start, afterUri, forest.getForestName())) {
          // if we're doing consistentSnapshot and this is the first result set, let's capture the
          // serverTimestamp so we can use it for all future queries
          if (consistentSnapshot == true && serverTimestamp.get() == -1) {
            if (serverTimestamp.compareAndSet(-1, results.getServerTimestamp())) {
              logger.info("Consistent snapshot timestamp=[{}]", serverTimestamp);
            }
          }

          for (int i = 0; i < getDocToUriBatchRatio(); i++) {
            uris.add(new ArrayList<>());
          }

          totalProcessedCount = 0;
          for (String uri : results) {
            int batchNum = totalProcessedCount / getBatchSize();
            uris.get(batchNum).add(uri);
            totalProcessedCount++;
          }

          if (totalProcessedCount == 0) {
            isDone.set(true);
          } else if (totalProcessedCount != docToUriBatchRatio * getBatchSize()) {
            hasLastBatch = true;
            if (totalProcessedCount % getBatchSize() == 0) {
              lastBatchNum = totalProcessedCount / getBatchSize() - 1;
            } else {
              lastBatchNum = totalProcessedCount / getBatchSize();
            }
          }
        } catch (ResourceNotFoundException e) {
          // we're done if we get a 404 NOT FOUND which throws ResourceNotFoundException
          // this should only happen if the last query retrieved a full batch so it thought
          // there would be more and queued this task which retrieved 0 results
          isDone.set(true);
          shutdownIfAllForestsAreDone();
          return;
        }

        batch = batch
                .withItems(uris.get(0).toArray(new String[uris.get(0).size()]))
                .withServerTimestamp(serverTimestamp.get())
                .withJobResultsSoFar(resultsSoFar.addAndGet(uris.get(0).size()))
                .withForestResultsSoFar(forestResults.get(forest).addAndGet(uris.get(0).size()));

        if(hasLastBatch && lastBatchNum == 0) {
          batch.withIsLastBatch(true);
        }

        for (int i = 1; i < getDocToUriBatchRatio(); i++) {
          if (uris.get(i).size() == 0) {
            continue;
          }

          QueryBatchImpl docBatch = new QueryBatchImpl() //have to create a new batch, otherwise withItems cannot overwrite
                  .withBatcher(batcher)
                  .withClient(client)
                  .withTimestamp(queryStart)
                  .withJobTicket(getJobTicket())
                  .withForestBatchNumber(forestBatchNum)
                  .withForest(forest)
                  .withItems(uris.get(i).toArray(new String[uris.get(i).size()]))
                  .withServerTimestamp(serverTimestamp.get())
                  .withJobResultsSoFar(resultsSoFar.addAndGet(uris.get(i).size()))
                  .withForestResultsSoFar(forestResults.get(forest).addAndGet(uris.get(i).size()));

          if (hasLastBatch && lastBatchNum == i) {
            docBatch.withIsLastBatch(true);
          }

          threadPool.execute(
                  new QueryTask(moveMgr, batcher, forest, QueryBatcherImpl.this.queryMethod, query, filtered,
                          forestBatchNum + i, start, docBatch, nextAfterUri
                  )
          );
        }
      } //end query batch if

      if (batch.getItems().length != 0) {
        processDocs(batch);
      }

      if (maxUris <= (resultsSoFar.longValue())) {
        isDone.set(true);
      }

      if (totalProcessedCount == getBatchSize() * getDocToUriBatchRatio()) {
        //document processing batch won't step in
        nextAfterUri = uris.get(getDocToUriBatchRatio() - 1).get(getBatchSize() - 1);
        launchNextTask();
      }

      if (isDone.get()) {
        shutdownIfAllForestsAreDone();
      }
    }

    private void processDocs(QueryBatchImpl batch) {
      AtomicBoolean isDone = forestIsDone.get(forest);

      try {
        if (retryBatchNumber != -1) {
          batch = batch.withJobBatchNumber(retryBatchNumber);
        } else {
          batch = batch.withJobBatchNumber(batchNumber.incrementAndGet());
        }

        logger.trace("Uri batch size={}, jobBatchNumber={}, jobResultsSoFar={}, forest={}", batch.getItems().length,
                batch.getJobBatchNumber(), batch.getJobResultsSoFar(), forest.getForestName());
        // now that we have the QueryBatch, let's send it to each onUrisReady listener
        for (QueryBatchListener listener : urisReadyListeners) {
          try {
            listener.processEvent(batch);
          } catch (Throwable t) {
            logger.error("Exception thrown by an onUrisReady listener", t);
          }
        }
        if (batch.getItems().length != getBatchSize()) {
          // we're done if we get a partial batch (always the last)
          isDone.set(true);
        }
        if (batch.getIsLastBatch()) {
          isDone.set(true);
        }
      } catch (Throwable t) {
        // any error outside listeners is grounds for stopping queries to this forest
        if (callFailListeners == true) {
          batch = batch
                  .withJobResultsSoFar(resultsSoFar.get())
                  .withForestResultsSoFar(forestResults.get(forest).get());
          for (QueryFailureListener listener : failureListeners) {
            try {
              listener.processFailure(new QueryBatchException(batch, t));
            } catch (Throwable e2) {
              logger.error("Exception thrown by an onQueryFailure listener", e2);
            }
          }
          if (retryForestMap.get(forest).get() == 0) {
            isDone.set(true);
          } else {
            retryForestMap.get(forest).decrementAndGet();
          }
        } else if (t instanceof RuntimeException) {
          throw (RuntimeException) t;
        } else {
          throw new DataMovementException("Failed to retry batch", t);
        }
      }
    }

    private void launchNextTask() {
      if (batcher.getStopped().get() == true ) {
        // we're stopping, so don't do anything more
        return;
      }
      AtomicBoolean isDone = forestIsDone.get(forest);
      // we made it to the end, so don't launch anymore tasks
      if ( isDone.get() == true ) {
    	  shutdownIfAllForestsAreDone();
    	  return;
      }
      long nextStart = start + getBatchSize() * getDocToUriBatchRatio();
      threadPool.execute(new QueryTask(
          moveMgr, batcher, forest, QueryBatcherImpl.this.queryMethod, query, filtered, forestBatchNum + getBatchSize(), nextStart, null, nextAfterUri
      ));
    }
  };

  private void shutdownIfAllForestsAreDone() {
    for ( AtomicBoolean isDone : forestIsDone.values() ) {
      // if even one isn't done, short-circuit out of this method and don't shutdown
      if ( isDone.get() == false ) return;
    }
    // if we made it this far, all forests are done. let's run the Job
    // completion listeners and shutdown.
    if(runJobCompletionListeners.compareAndSet(false, true)) runJobCompletionListeners();
    threadPool.shutdown();
  }

  private void runJobCompletionListeners() {
    for (QueryBatcherListener listener : jobCompletionListeners) {
      try {
        listener.processEvent(QueryBatcherImpl.this);
      } catch (Throwable e) {
        logger.error("Exception thrown by an onJobCompletion listener", e);
      }
    }
    super.setJobEndTime();
  }

  private class IteratorTask implements Runnable {

    private QueryBatcher batcher;

    IteratorTask(QueryBatcher batcher) {
      this.batcher = batcher;
    }

    @Override
    public void run() {
      try {
        boolean lastBatch = false;
        List<String> uriQueue = new ArrayList<>(getBatchSize());
        while (iterator.hasNext() && !lastBatch) {
          uriQueue.add(iterator.next());
          if(!iterator.hasNext()) lastBatch = true;
          // if we've hit batchSize or the end of the iterator
          if (uriQueue.size() == getBatchSize() || !iterator.hasNext() || lastBatch) {
            final List<String> uris = uriQueue;
            final boolean finalLastBatch = lastBatch;
            final long results = resultsSoFar.addAndGet(uris.size());
            if(maxUris <= results) 
                lastBatch = true;
            uriQueue = new ArrayList<>(getBatchSize());
            Runnable processBatch = new Runnable() {
              public void run() {
                QueryBatchImpl batch = new QueryBatchImpl()
                    .withBatcher(batcher)
                    .withTimestamp(Calendar.getInstance())
                    .withJobTicket(getJobTicket());
                try {
                  long currentBatchNumber = batchNumber.incrementAndGet();
                  // round-robin from client 0 to (clientList.size() - 1);
                  List<DatabaseClient> currentClientList = clientList.get();
                  int clientIndex = (int) (currentBatchNumber % currentClientList.size());
                  DatabaseClient client = currentClientList.get(clientIndex);
                  batch = batch.withJobBatchNumber(currentBatchNumber)
                      .withClient(client)
                      .withJobResultsSoFar(results)
                      .withItems(uris.toArray(new String[uris.size()]));
                  logger.trace("batch size={}, jobBatchNumber={}, jobResultsSoFar={}", uris.size(),
                      batch.getJobBatchNumber(), batch.getJobResultsSoFar());
                  for (QueryBatchListener listener : urisReadyListeners) {
                    try {
                      listener.processEvent(batch);
                    } catch (Throwable e) {
                      logger.error("Exception thrown by an onUrisReady listener", e);
                    }
                  }
                } catch (Throwable t) {
                  batch = batch.withItems(uris.toArray(new String[uris.size()]));
                  for (QueryFailureListener listener : failureListeners) {
                    try {
                      listener.processFailure(new QueryBatchException(batch, t));
                    } catch (Throwable e) {
                      logger.error("Exception thrown by an onQueryFailure listener", e);
                    }
                  }
                  logger.warn("Error iterating to queue uris: {}", t.toString());
                }
                if(finalLastBatch) {
                  runJobCompletionListeners();
                }
              }
            };
            threadPool.execute(processBatch);
            // If the queue is almost full, stop producing and add a task to continue later
            if (isSingleThreaded && threadPool.getQueue().remainingCapacity() <= 2 && iterator.hasNext()) {
              threadPool.execute(new IteratorTask(batcher));
              return;
            }
          }
        }
      } catch (Throwable t) {
        for (QueryFailureListener listener : failureListeners) {
          QueryBatchImpl batch = new QueryBatchImpl()
              .withItems(new String[0])
              .withClient(clientList.get().get(0))
              .withBatcher(batcher)
              .withTimestamp(Calendar.getInstance())
              .withJobResultsSoFar(0);

          try {
            listener.processFailure(new QueryBatchException(batch, t));
          } catch (Throwable e) {
            logger.error("Exception thrown by an onQueryFailure listener", e);
          }
        }
        logger.warn("Error iterating to queue uris: {}", t.toString());
      }
      threadPool.shutdown();
    }
  }

  /* startIterating launches in a separate thread (actually a task handled by
   * threadPool) and just loops through the Iterator<String>, batching uris of
   * batchSize, and queueing tasks to process each batch via onUrisReady
   * listeners.  Therefore, this method doesn't talk directly to MarkLogic
   * Server.  Only the registered onUrisReady listeners can talk to the server,
   * using the DatabaseClient provided by QueryBatch.getClient().  In order to
   * fully utilize the cluster, we provide DatabaseClient instances to batches
   * in round-robin fashion, looping through the hosts provided to
   * withForestConfig and cached in clientList.  Errors calling
   * iterator.hasNext() or iterator.next() are handled by onQueryFailure
   * listeners.  Errors calling listeners (onUrisReady or onQueryFailure) are
   * logged by our slf4j lgoger at level "error".  If customers want errors in
   * their listeners handled, they should use try-catch and handle them.
   */
  private void startIterating() {
    threadPool.execute(new IteratorTask(this));
  }

  @Override
  public void stop() {
    super.getStopped().set(true);
    if ( threadPool != null ) threadPool.shutdownNow();
    super.setJobEndTime();
    if ( query != null ) {
      for ( AtomicBoolean isDone : forestIsDone.values() ) {
        // if even one isn't done, log a warning
        if ( isDone.get() == false ) {
          logger.warn("QueryBatcher instance \"{}\" stopped before all results were retrieved",
            getJobName());
          break;
        }
      }
    } else {
      if ( iterator != null && iterator.hasNext() ) {
        logger.warn("QueryBatcher instance \"{}\" stopped before all results were processed",
          getJobName());
      }
    }
    closeAllListeners();
  }

  private void closeAllListeners() {
    for (QueryBatchListener listener : getUrisReadyListeners()) {
      if ( listener instanceof AutoCloseable ) {
        try {
          ((AutoCloseable) listener).close();
        } catch (Exception e) {
          logger.error("onUrisReady listener cannot be closed", e);
        }
      }
    }
    for (QueryFailureListener listener : getQueryFailureListeners()) {
      if ( listener instanceof AutoCloseable ) {
        try {
          ((AutoCloseable) listener).close();
        } catch (Exception e) {
          logger.error("onQueryFailure listener cannot be closed", e);
        }
      }
    }
  }

  protected void finalize() {
    if (this.getStopped().get() == false ) {
      logger.warn("QueryBatcher instance \"{}\" was never cleanly stopped.  You should call dataMovementManager.stopJob.",
        getJobName());
    }
  }

  /**
   * A handler for rejected tasks that waits for the work queue to
   * become empty and then submits the rejected task
   */
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
      logger.info("Job complete, jobBatchNumber={}, jobResultsSoFar={}",
        batchNumber.get(), resultsSoFar.get());
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

  @Override
  public QueryBatcher onJobCompletion(QueryBatcherListener listener) {
    if ( listener == null ) throw new IllegalArgumentException("listener must not be null");
    jobCompletionListeners.add(listener);
    return this;
  }

  @Override
  public QueryBatcherListener[] getQueryJobCompletionListeners() {
    return jobCompletionListeners.toArray(new QueryBatcherListener[jobCompletionListeners.size()]);
  }

  @Override
  public void setQueryJobCompletionListeners(QueryBatcherListener... listeners) {
    requireNotStarted();
    jobCompletionListeners.clear();
    if ( listeners != null ) {
      for (QueryBatcherListener listener : listeners) {
        jobCompletionListeners.add(listener);
      }
    }
  }

  @Override
  public Calendar getJobStartTime() {
    if(! this.isStarted()) {
      return null;
    } else {
      return super.getJobStartTime();
    }
  }

  @Override
  public Calendar getJobEndTime() {
    if(! this.isStopped()) {
      return null;
    } else {
      return super.getJobEndTime();
    }
  }

@Override
public void setMaxBatches(long maxBatches) {
	Long max_limit = Long.MAX_VALUE/getBatchSize();
	if(maxBatches > max_limit)
		throw new IllegalArgumentException("Number of batches cannot be more than "+ max_limit);
	this.maxBatches = maxBatches;
	if(isStarted())
		setMaxUris(maxBatches);
}

private void setMaxUris(long maxBatches) {
	this.maxUris = (maxBatches * getBatchSize());
}

@Override
public long getMaxBatches() {
    return this.maxBatches;
}

@Override
public void setMaxBatches() {
	this.maxBatches = -1L;
	if(isStarted())
		setMaxUris(getMaxBatches());
}
}
