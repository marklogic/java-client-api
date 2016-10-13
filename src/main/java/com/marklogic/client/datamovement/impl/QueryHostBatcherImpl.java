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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.BatchListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.FailureListener;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.QueryHostBatcher;
import com.marklogic.client.datamovement.QueryHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.impl.QueryManagerImpl;
import com.marklogic.client.impl.UrisHandle;
import java.util.List;
import java.util.Map;

public class QueryHostBatcherImpl extends HostBatcherImpl implements QueryHostBatcher {
  private static Logger logger = LoggerFactory.getLogger(QueryHostBatcherImpl.class);
  private QueryDefinition query;
  private Iterator<String> iterator;
  private DataMovementManager moveMgr;
  private boolean threadCountSet = false;
  private ForestConfiguration forestConfig;
  private List<BatchListener<String>> urisReadyListeners = new ArrayList<>();
  private List<FailureListener<QueryHostException>> failureListeners = new ArrayList<>();
  private QueryThreadPoolExecutor threadPool;
  private boolean consistentSnapshot = false;
  private final AtomicLong batchNumber = new AtomicLong(0);
  private final AtomicLong resultsSoFar = new AtomicLong(0);
  private Forest[] forests;
  private final AtomicLong serverTimestamp = new AtomicLong(-1);
  private List<DatabaseClient> clientList = new ArrayList<>();
  private Map<Forest,AtomicLong> forestResults = new HashMap<>();
  private Map<Forest,AtomicBoolean> forestIsDone = new HashMap<>();
  private final AtomicBoolean stopped = new AtomicBoolean(false);

  public QueryHostBatcherImpl(QueryDefinition query, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super();
    this.moveMgr = moveMgr;
    this.query = query;
    this.forestConfig = forestConfig;
    withBatchSize(1000);
  }

  public QueryHostBatcherImpl(Iterator<String> iterator, DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super();
    this.moveMgr = moveMgr;
    this.iterator = iterator;
    this.forestConfig = forestConfig;
    withBatchSize(1000);
  }

  @Override
  public QueryHostBatcherImpl onUrisReady(BatchListener<String> listener) {
    urisReadyListeners.add(listener);
    return this;
  }

  @Override
  public QueryHostBatcherImpl onQueryFailure(FailureListener<QueryHostException> listener) {
    failureListeners.add(listener);
    return this;
  }

  @Override
  public BatchListener<String>[]               getQuerySuccessListeners() {
    return urisReadyListeners.toArray(new BatchListener[urisReadyListeners.size()]);
  }

  @Override
  public FailureListener<QueryHostException>[] getQueryFailureListeners() {
    return failureListeners.toArray(new FailureListener[failureListeners.size()]);
  }

  @Override
  public void setBatchSuccessListeners(BatchListener<String>... listeners) {
    requireNotStarted();
    urisReadyListeners.clear();
    if ( listeners != null ) {
      for ( BatchListener<String> listener : listeners ) {
        urisReadyListeners.add(listener);
      }
    }
  }

  @Override
  public void setBatchFailureListeners(FailureListener<QueryHostException>... listeners) {
    requireNotStarted();
    failureListeners.clear();
    if ( listeners != null ) {
      for ( FailureListener<QueryHostException> listener : listeners ) {
        failureListeners.add(listener);
      }
    }
  }

  @Override
  public QueryHostBatcher withJobName(String jobName) {
    requireNotStarted();
    super.withJobName(jobName);
    return this;
  }

  @Override
  public QueryHostBatcher withBatchSize(int batchSize) {
    requireNotStarted();
    super.withBatchSize(batchSize);
    return this;
  }

  @Override
  public QueryHostBatcher withThreadCount(int threadCount) {
    requireNotStarted();
    if ( getThreadCount() <= 0 ) {
      throw new IllegalArgumentException("threadCount must be 1 or greater");
    }
    threadCountSet = true;
    super.withThreadCount(threadCount);
    return this;
  }

  @Override
  public QueryHostBatcher withConsistentSnapshot() {
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
  public boolean isTerminated() {
    requireJobStarted();
    return threadPool.isTerminated();
  }

  @Override
  public boolean isTerminating() {
    requireJobStarted();
    return threadPool.isTerminating();
  }

  private void requireJobStarted() {
    if ( threadPool == null ) {
      throw new IllegalStateException("Job not started. First call DataMovementManager.startJob(QueryHostBatcher)");
    }
  }

  private void requireNotStarted() {
    if ( threadPool != null ) {
      throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    }
  }

  void start() {
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

  private void initialize() {
    forests = forestConfig.listForests();
    Map<String,Forest> hosts = new HashMap<>();
    for ( Forest forest : forests ) {
      if ( forest.getHost() == null ) throw new IllegalStateException("Hostname must not be null for any forest");
      hosts.put(forest.getHost(), forest);
      forestResults.put(forest, new AtomicLong());
      forestIsDone.put(forest, new AtomicBoolean(false));
    }
    for ( String host : hosts.keySet() ) {
      Forest forest = hosts.get(host);
      DatabaseClient client = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
      clientList.add(client);
    }
    if ( threadCountSet == false ) {
      if ( query != null ) {
        logger.warn("threadCount not set--defaulting to number of forests ({})", forests.length);
        withThreadCount(forests.length);
      } else {
        logger.warn("threadCount not set--defaulting to number of hosts ({})", hosts.size());
        withThreadCount( hosts.size() );
      }
    }
    logger.info("Starting job batchSize={}, threadCount={}, onUrisReady listeners={}, failure listeners={}",
        getBatchSize(), getThreadCount(), urisReadyListeners.size(), failureListeners.size());
    threadPool = new QueryThreadPoolExecutor(1, this);
    threadPool.setCorePoolSize(getThreadCount());
    threadPool.setMaximumPoolSize(getThreadCount());
  }

  private void startQuerying() {
    boolean consistentSnapshotFirstQueryHasRun = false;
    for ( final Forest forest : forests ) {
      QueryTask runnable = new QueryTask(moveMgr, forest, query, 1, 1);
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
    private Forest forest;
    private QueryDefinition query;
    private long forestBatchNum;
    private long start;

    QueryTask(DataMovementManager moveMgr, Forest forest, QueryDefinition query, long forestBatchNum, long start) {
      this.moveMgr = moveMgr;
      this.forest = forest;
      this.query = query;
      this.forestBatchNum = forestBatchNum;
      this.start = start;
    }

    public void run() {
      AtomicBoolean isDone = forestIsDone.get(forest);
      if ( isDone.get() == true ) return;
      DatabaseClient client = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
      try {
        QueryManagerImpl queryMgr = (QueryManagerImpl) client.newQueryManager();
        queryMgr.setPageLength(getBatchSize());
        Calendar queryStart = Calendar.getInstance();
        List<String> uris = new ArrayList<>();
        UrisHandle handle = new UrisHandle();
        if ( consistentSnapshot == true && serverTimestamp.get() > -1 ) {
          handle.setPointInTimeQueryTimestamp(serverTimestamp.get());
        }
        UrisHandle results = queryMgr.uris(query, handle, start, null, forest.getForestName());
        try {
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
          Batch<String> batch = new BatchImpl<String>()
            .withItems(uris.toArray(new String[uris.size()]))
            .withTimestamp(queryStart)
            .withServerTimestamp(serverTimestamp.get())
            .withJobBatchNumber(batchNumber.incrementAndGet())
            .withJobResultsSoFar(resultsSoFar.addAndGet(uris.size()))
            .withForestBatchNumber(forestBatchNum)
            .withForestResultsSoFar(forestResults.get(forest).addAndGet(uris.size()))
            .withForest(forest);
          logger.trace("batch size={}, jobBatchNumber={}, jobResultsSoFar={}, forest={}", uris.size(),
              batch.getJobBatchNumber(), batch.getJobResultsSoFar(), forest.getForestName());
          // let's handle errors from listeners specially
          try {
            for (BatchListener<String> listener : urisReadyListeners) {
              listener.processEvent(client, batch);
            }
          } catch (Throwable t) {
            for ( FailureListener<QueryHostException> listener : failureListeners ) {
              listener.processFailure(client, new QueryHostException(null, t));
            }
          }
        } finally {
          results.close();
        }
      } catch (ResourceNotFoundException e) {
        // we're done if we get a 404 NOT FOUND which throws ResourceNotFoundException
        isDone.set(true);
        shutdownIfAllForestsAreDone();
      } catch (Throwable t) {
        for ( FailureListener<QueryHostException> listener : failureListeners ) {
          listener.processFailure(client, new QueryHostException(null, t));
        }
        // any error outside listeners is grounds for stopping this job
        isDone.set(true);
        shutdownIfAllForestsAreDone();
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
      threadPool.execute(new QueryTask(moveMgr, forest, query, forestBatchNum + 1, nextStart));
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
    final AtomicLong batchNumber = new AtomicLong(0);
    final AtomicLong resultsSoFar = new AtomicLong(0);
    List<String> uriQueue = new ArrayList<>(getBatchSize());
    while ( iterator.hasNext() ) {
      uriQueue.add(iterator.next());
      // if we've hit batchSize or the end of the iterator
      if ( uriQueue.size() == getBatchSize() || ! iterator.hasNext() ) {
        final List<String> uris = uriQueue;
        uriQueue = new ArrayList<>(getBatchSize());
        Runnable runnable = new Runnable() {
          public void run() {
            long currentBatchNumber = batchNumber.incrementAndGet();
            // round-robin from client 0 to (clientList.size() - 1);
            int clientIndex = (int) (currentBatchNumber % clientList.size());
            DatabaseClient client = clientList.get(clientIndex);
            try {
              Batch<String> batch = new BatchImpl<String>()
                  .withItems(uris.toArray(new String[uris.size()]))
                  .withTimestamp(Calendar.getInstance())
                  .withJobBatchNumber(currentBatchNumber)
                  .withJobResultsSoFar(resultsSoFar.addAndGet(uris.size()));
              logger.trace("batch size={}, jobBatchNumber={}, jobResultsSoFar={}", uris.size(),
                  batch.getJobBatchNumber(), batch.getJobResultsSoFar());
              for (BatchListener<String> listener : urisReadyListeners) {
                listener.processEvent(client, batch);
              }
            } catch (Throwable t) {
              for ( FailureListener<QueryHostException> listener : failureListeners ) {
                listener.processFailure(client, new QueryHostException(null, t));
              }
            }
          }
        };
        threadPool.execute(runnable);
      }
    }
    threadPool.shutdown();
  }

  public void stop() {
    stopped.set(true);
    threadPool.shutdownNow();
  }

  private class QueryThreadPoolExecutor extends ThreadPoolExecutor {
    private Object objectToNotifyFrom;

    QueryThreadPoolExecutor(int threadCount, Object objectToNotifyFrom) {
      super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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
