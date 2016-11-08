/*
 * Copyright 2015 MarkLogic Corporation
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
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.QueryHostException;
import com.marklogic.client.datamovement.QueryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.impl.QueryManagerImpl;
import com.marklogic.client.impl.UrisHandle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class QueryBatcherImpl extends BatcherImpl implements QueryBatcher {
  private static Logger logger = LoggerFactory.getLogger(QueryBatcherImpl.class);
  private QueryDefinition query;
  private Iterator<String> iterator;
  private DataMovementManager moveMgr;
  private boolean threadCountSet = false;
  private List<QueryBatchListener> urisReadyListeners = new ArrayList<>();
  private List<QueryFailureListener> failureListeners = new ArrayList<>();
  private QueryThreadPoolExecutor threadPool;
  private boolean consistentSnapshot = false;
  private final AtomicLong batchNumber = new AtomicLong(0);
  private final AtomicLong resultsSoFar = new AtomicLong(0);
  private final AtomicLong serverTimestamp = new AtomicLong(-1);
  private final AtomicReference<List<DatabaseClient>> clientList = new AtomicReference<>();
  private Map<Forest,AtomicLong> forestResults = new HashMap<>();
  private Map<Forest,AtomicBoolean> forestIsDone = new HashMap<>();
  private final AtomicBoolean stopped = new AtomicBoolean(false);
  private final Map<Forest,List<QueryTask>> blackListedTasks = new HashMap<>();

  public QueryBatcherImpl(QueryDefinition query, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super();
    this.moveMgr = moveMgr;
    this.query = query;
    withForestConfig(forestConfig);
    withBatchSize(1000);
  }

  public QueryBatcherImpl(Iterator<String> iterator, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super();
    this.moveMgr = moveMgr;
    this.iterator = iterator;
    withForestConfig(forestConfig);
    withBatchSize(1000);
  }

  @Override
  public QueryBatcherImpl onUrisReady(QueryBatchListener listener) {
    urisReadyListeners.add(listener);
    return this;
  }

  @Override
  public QueryBatcherImpl onQueryFailure(QueryFailureListener listener) {
    failureListeners.add(listener);
    return this;
  }

  @Override
  public void retry(QueryEvent queryEvent) {
    boolean callFailListeners = false;
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
    long start = queryEvent.getForestResultsSoFar() + 1;
    logger.trace("retryForest: {}, retryHost: {}, start: {}",
      retryForest.getForestName(), retryForest.getPreferredHost(), start);
    QueryTask runnable = new QueryTask(moveMgr, this, retryForest, query,
      queryEvent.getForestBatchNumber(), start, queryEvent.getJobBatchNumber(), callFailListeners);
    runnable.run();
  }


  @Override
  public QueryBatchListener[]   getQuerySuccessListeners() {
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
  public QueryBatcher withBatchSize(int batchSize) {
    requireNotStarted();
    super.withBatchSize(batchSize);
    return this;
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
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    requireJobStarted();
    return threadPool.awaitTermination(timeout, unit);
  }

  @Override
  public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
    return awaitTermination(timeout, unit);
  }

  @Override
  public boolean awaitCompletion() {
    try {
      return awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch(InterruptedException e) {
      return false;
    }
  }

  @Override
  public boolean isStopped() {
    return threadPool != null && threadPool.isTerminated();
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

  synchronized void start() {
    if ( threadPool != null ) logger.warn("startJob called more than once");
    if ( getBatchSize() <= 0 ) {
      withBatchSize(1);
      logger.warn("batchSize should be 1 or greater--setting batchSize to 1");
    }
    initialize();
    if ( query != null ) {
      startQuerying();
    } else {
      startIterating();
    }
  }

  private synchronized void initialize() {
    if ( threadCountSet == false ) {
      if ( query != null ) {
        Forest[] forests = getForestConfig().listForests();
        logger.warn("threadCount not set--defaulting to number of forests ({})", forests.length);
        withThreadCount(forests.length);
      } else {
        int hostCount = clientList.get().size();
        logger.warn("threadCount not set--defaulting to number of hosts ({})", hostCount);
        withThreadCount( hostCount );
      }
      // now we've set the threadCount
      threadCountSet = true;
    }
    logger.info("Starting job batchSize={}, threadCount={}, onUrisReady listeners={}, failure listeners={}",
        getBatchSize(), getThreadCount(), urisReadyListeners.size(), failureListeners.size());
    threadPool = new QueryThreadPoolExecutor(1, this);
    threadPool.setCorePoolSize(getThreadCount());
    threadPool.setMaximumPoolSize(getThreadCount());
  }

  @Override
  public synchronized QueryBatcher withForestConfig(ForestConfiguration forestConfig) {
    super.withForestConfig(forestConfig);
    Forest[] forests = forestConfig.listForests();
    Set<Forest> oldForests = new HashSet<>(forestResults.keySet());
    Map<String,Forest> hosts = new HashMap<>();
    for ( Forest forest : forests ) {
      if ( forest.getPreferredHost() == null ) throw new IllegalStateException("Hostname must not be null for any forest");
      hosts.put(forest.getPreferredHost(), forest);
      if ( forestResults.get(forest) == null ) forestResults.put(forest, new AtomicLong());
      if ( forestIsDone.get(forest) == null  ) forestIsDone.put(forest, new AtomicBoolean(false));
    }
    logger.info("(withForestConfig) Using {} hosts with forests for \"{}\"", hosts.keySet(), forests[0].getDatabaseName());
    List<DatabaseClient> newClientList = new ArrayList<>();
    for ( String host : hosts.keySet() ) {
      Forest forest = hosts.get(host);
      DatabaseClient client = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
      newClientList.add(client);
    }
    clientList.set(newClientList);
    boolean started = (threadPool != null);
    if ( started == true && oldForests.size() > 0 ) calucluateDeltas(oldForests, forests);
    return this;
  }

  private synchronized void calucluateDeltas(Set<Forest> oldForests, Forest[] forests) {
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
        threadPool.execute(new QueryTask(moveMgr, this, forest, query, 1, 1));
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
      }
      // we can clear blackListedTaskList because we have a synchronized lock
      blackListedTaskList.clear();
    }
  }

  private List<String> getForestNames(Collection<Forest> forests) {
    return forests.stream().map((forest)->forest.getForestName()).collect(Collectors.toList());
  }

  private List<String> getHostNames(Collection<Forest> forests) {
    return forests.stream().map((forest)->forest.getPreferredHost()).distinct().collect(Collectors.toList());
  }

  private synchronized void startQuerying() {
    boolean consistentSnapshotFirstQueryHasRun = false;
    for ( Forest forest : getForestConfig().listForests() ) {
      QueryTask runnable = new QueryTask(moveMgr, this, forest, query, 1, 1);
      if ( consistentSnapshot == true && consistentSnapshotFirstQueryHasRun == false ) {
        // let's run this first time in-line so we'll have the serverTimestamp set
        // before we launch all the parallel threads
        runnable.run();
        consistentSnapshotFirstQueryHasRun = true;
      } else {
        threadPool.execute(runnable);
      }
    }
  }

  private class QueryTask implements Runnable {
    private DataMovementManager moveMgr;
    private QueryBatcher batcher;
    private Forest forest;
    private QueryDefinition query;
    private long forestBatchNum;
    private long start;
    private long retryBatchNumber;
    private boolean callFailListeners;

    QueryTask(DataMovementManager moveMgr, QueryBatcher batcher, Forest forest,
      QueryDefinition query, long forestBatchNum, long start)
    {
      this(moveMgr, batcher, forest, query, forestBatchNum, start, -1, true);
    }

    QueryTask(DataMovementManager moveMgr, QueryBatcher batcher, Forest forest,
      QueryDefinition query, long forestBatchNum, long start, long retryBatchNumber, boolean callFailListeners)
    {
      this.moveMgr = moveMgr;
      this.batcher = batcher;
      this.forest = forest;
      this.query = query;
      this.forestBatchNum = forestBatchNum;
      this.start = start;
      this.retryBatchNumber = retryBatchNumber;
      this.callFailListeners = callFailListeners;
    }

    public void run() {
      AtomicBoolean isDone = forestIsDone.get(forest);
      if ( isDone.get() == true ) return;
      if ( stopped.get() == true ) return;
      DatabaseClient client = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
      Calendar queryStart = Calendar.getInstance();
      QueryBatchImpl batch = new QueryBatchImpl()
        .withBatcher(batcher)
        .withClient(client)
        .withTimestamp(queryStart)
        .withForestBatchNumber(forestBatchNum)
        .withForest(forest);
      if ( retryBatchNumber != -1 ) {
        batch = batch.withJobBatchNumber(retryBatchNumber);
      } else {
        batch = batch.withJobBatchNumber(batchNumber.incrementAndGet());
      }
      try {
        QueryManagerImpl queryMgr = (QueryManagerImpl) client.newQueryManager();
        queryMgr.setPageLength(getBatchSize());
        List<String> uris = new ArrayList<>();
        UrisHandle handle = new UrisHandle();
        if ( consistentSnapshot == true && serverTimestamp.get() > -1 ) {
          handle.setPointInTimeQueryTimestamp(serverTimestamp.get());
        }
        try ( UrisHandle results = queryMgr.uris(query, handle, start, null, forest.getForestName()) ) {
          if ( consistentSnapshot == true && serverTimestamp.get() == -1 ) {
            serverTimestamp.set(results.getServerTimestamp());
            logger.info("Consistent snapshot timestamp=[{}]", serverTimestamp);
          }
          uris = new ArrayList<>();
          for ( String uri : results ) {
            uris.add( uri );
          }
          if ( uris.size() == getBatchSize() ) {
            // this is a full batch
            launchNextTask();
          } else {
            // we're done if we get a partial batch (always the last)
            isDone.set(true);
            shutdownIfAllForestsAreDone();
          }
          batch = batch
            .withItems(uris.toArray(new String[uris.size()]))
            .withServerTimestamp(serverTimestamp.get())
            .withJobResultsSoFar(resultsSoFar.addAndGet(uris.size()))
            .withForestResultsSoFar(forestResults.get(forest).addAndGet(uris.size()));
          logger.trace("batch size={}, jobBatchNumber={}, jobResultsSoFar={}, forest={}", uris.size(),
              batch.getJobBatchNumber(), batch.getJobResultsSoFar(), forest.getForestName());
          // let's handle errors from listeners specially
          for (QueryBatchListener listener : urisReadyListeners) {
            try {
              listener.processEvent(batch);
            } catch (Throwable t) {
              logger.error("Exception thrown by an onUrisReady listener", t);
            }
          }
        }
      } catch (ResourceNotFoundException e) {
        // we're done if we get a 404 NOT FOUND which throws ResourceNotFoundException
        isDone.set(true);
        shutdownIfAllForestsAreDone();
      } catch (Throwable t) {
        // any error outside listeners is grounds for stopping queries to this forest
        isDone.set(true);
        if ( callFailListeners == true ) {
          batch = batch
            .withJobResultsSoFar(resultsSoFar.get())
            .withForestResultsSoFar(forestResults.get(forest).get());
          for ( QueryFailureListener listener : failureListeners ) {
            try {
              listener.processFailure(new QueryHostException(batch, t));
            } catch (Throwable e2) {
              logger.error("Exception thrown by an onQueryFailure listener", e2);
            }
          }
          shutdownIfAllForestsAreDone();
        } else if ( t instanceof RuntimeException ) {
          throw (RuntimeException) t;
        } else {
          throw new DataMovementException("Failed to retry batch", t);
        }
      }
    }

    private void launchNextTask() {
      if ( stopped.get() == true ) {
        // we're stopping, so don't do anything more
        return;
      }
      AtomicBoolean isDone = forestIsDone.get(forest);
      // we made it to the end, so don't launch anymore tasks
      if ( isDone.get() == true ) return;
      long nextStart = start + getBatchSize();
      threadPool.execute(new QueryTask(moveMgr, batcher, forest, query, forestBatchNum + 1, nextStart));
    }
  };

  private void shutdownIfAllForestsAreDone() {
    for ( AtomicBoolean isDone : forestIsDone.values() ) {
      // if even one isn't done, short-circuit out of this method and don't shutdown
      if ( isDone.get() == false ) return;
    }
    // if we made it this far, all forests are done.  let's shutdown.
    threadPool.shutdown();
  }


  private void startIterating() {
    final QueryBatcher batcher = this;
    Runnable queueUris = new Runnable() {
      public void run() {
        try {
          final AtomicLong batchNumber = new AtomicLong(0);
          final AtomicLong resultsSoFar = new AtomicLong(0);
          List<String> uriQueue = new ArrayList<>(getBatchSize());
          while ( iterator.hasNext() ) {
            try {
              uriQueue.add(iterator.next());
              // if we've hit batchSize or the end of the iterator
              if ( uriQueue.size() == getBatchSize() || ! iterator.hasNext() ) {
                final List<String> uris = uriQueue;
                uriQueue = new ArrayList<>(getBatchSize());
                Runnable processBatch = new Runnable() {
                  public void run() {
                    long currentBatchNumber = batchNumber.incrementAndGet();
                    // round-robin from client 0 to (clientList.size() - 1);
                    List<DatabaseClient> currentClientList = clientList.get();
                    int clientIndex = (int) (currentBatchNumber % currentClientList.size());
                    DatabaseClient client = currentClientList.get(clientIndex);
                    QueryBatchImpl batch = new QueryBatchImpl()
                      .withClient(client)
                      .withBatcher(batcher)
                      .withTimestamp(Calendar.getInstance())
                      .withJobBatchNumber(currentBatchNumber)
                      .withJobResultsSoFar(resultsSoFar.addAndGet(uris.size()));
                      batch = batch.withItems(uris.toArray(new String[uris.size()]));
                      logger.trace("batch size={}, jobBatchNumber={}, jobResultsSoFar={}", uris.size(),
                          batch.getJobBatchNumber(), batch.getJobResultsSoFar());
                      for (QueryBatchListener listener : urisReadyListeners) {
                        try {
                          listener.processEvent(batch);
                        } catch (Throwable e) {
                          logger.error("Exception thrown by an onUrisReady listener", e);
                        }
                      }
                  }
                };
                threadPool.execute(processBatch);
              }
            } catch (Throwable t) {
              QueryBatchImpl batch = new QueryBatchImpl()
                .withItems(new String[0])
                .withClient(clientList.get().get(0))
                .withBatcher(batcher)
                .withTimestamp(Calendar.getInstance())
                .withJobResultsSoFar(0);
              for ( QueryFailureListener listener : failureListeners ) {
                try {
                  listener.processFailure(new QueryHostException(batch, t));
                } catch (Throwable e) {
                  logger.error("Exception thrown by an onQueryFailure listener", e);
                }
              }
              logger.warn("Error iterating to queue uris", t.toString());
            }
          }
        } catch (Throwable t) {
          for ( QueryFailureListener listener : failureListeners ) {
            try {
              QueryBatchImpl batch = new QueryBatchImpl()
                .withItems(new String[0])
                .withClient(clientList.get().get(0))
                .withBatcher(batcher)
                .withTimestamp(Calendar.getInstance())
                .withJobResultsSoFar(0);
              listener.processFailure(new QueryHostException(batch, t));
            } catch (Throwable e) {
              logger.error("Exception thrown by an onQueryFailure listener", e);
            }
          }
          logger.warn("Error iterating to queue uris", t.toString());
        }
        threadPool.shutdown();
      }
    };
    threadPool.execute(queueUris);
  }

  public void stop() {
    stopped.set(true);
    if ( threadPool != null ) threadPool.shutdownNow();
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
  }

  protected void finalize() {
    if ( stopped.get() == false ) {
      logger.warn("QueryBatcher instance \"{}\" was never cleanly stopped.  You should call dataMovementManager.stopJob.",
        getJobName());
    }
  }

  private class QueryThreadPoolExecutor extends ThreadPoolExecutor {
    private Object objectToNotifyFrom;

    QueryThreadPoolExecutor(int threadCount, Object objectToNotifyFrom) {
      super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(threadCount * 5), new ThreadPoolExecutor.CallerRunsPolicy());
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
    protected void terminated() {
      super.terminated();
      synchronized(objectToNotifyFrom) {
        objectToNotifyFrom.notifyAll();
      }
    }
  }
}
