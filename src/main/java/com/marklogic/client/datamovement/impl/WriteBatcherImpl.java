/*
 * Copyright 2015-2016 MarkLogic Corporation
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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatchListener;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.datamovement.WriteFailureListener;
import com.marklogic.client.datamovement.WriteBatcher;

/**
 * The implementation of WriteBatcher.
 * Features
 *   - multiple threads can concurrently call add/addAs
 *     - we don't manage these threads, they're outside this
 *     - no synchronization or unnecessary delays while queueing
 *     - won't launch extra threads until a batch is ready to write
 *     - (warning) we don't proactively read streams, so don't leave them in the queue too long
 *   - topology-aware by calling /v1/forestinfo
 *     - get list of hosts which have writeable forests
 *     - each write hits the next writeable host for round-robin network calls
 *   - manage an internal threadPool of size threadCount for network calls
 *   - when batchSize reached, writes a batch
 *     - using a thread from threadPool
 *     - no synchronization or unnecessary delays while emptying queue
 *     - and calls each successListener (if not using transactions)
 *   - if usingTransactions (transactionSize > 1)
 *     - opens transactions as needed
 *       - using a thread from threadPool
 *       - but not before, lest we increase likelihood of transaction timeout
 *       - threads needing a transaction will open one then make it available to others up to transactionSize
 *     - after each batch write, check if transactionSize reached and if so commit the transaction
 *       - don't check before write to avoid race condition where the last batch writes and commits
 *         before the second to last batch writes
 *       - don't commit if another thread is in process with the transaction
 *         - instead queue the transaction for commit later
 *       - if commit is successful call each successListener for each transaction batch
 *   - when a batch fails, calls each failureListener
 *     - and calls rollback (if using transactions)
 *       - using a thread from threadPool
 *       - then calls each failureListener for each transaction batch
 *   - flush() writes all queued documents whether the last batch is full or not
 *     - and commits the transaction for each batch so nothing is left uncommitted (ignores transactionSize)
 *     - and resets counter so the next batch will be a normal batch size
 *     - and finishes any unfinished transactions
 *       - those without error are committed
 *       - those with error are made to rollback
 *   - awaitCompletion allows the calling thread to block until all WriteBatcher threads are finished
 *     writing batches or committing transactions (or calling rollback)
 *
 * Design
 *   - think asynchronously
 *     - so that many external threads and many internal threads can be constantly
 *       updating state without creating conflict
 *     - avoid race conditions and logic which depends on state remaining unchanged
 *       from one statement to the next
 *     - when triggering periodic processing such as writing a batch, opening a
 *       transaction, or choosing the next host to use
 *       - use logic where multiple concurrent threads can arrive at the same point and
 *         see the same state yet only one of the threads will perform the processing
 *         - do this by using AtomicLong.incrementAndGet() so each thread gets a different
 *           number, then trigger the logic with the thread that gets the correct number
 *         - for example, we decide to write a batch by
 *           timeToWriteBatch = (recordNum % getBatchSize()) == 0;
 *           - in other words, when we reach a recordNum which is a multiple of getBatchSize
 *           - only one thread will get the correct number and that thread will have
 *             timeToWriteBatch == true
 *           - we don't reset recordNum at each batch as that would introduce a race condition
 *           - however, when flush is called we want subsequent batches to start over, so
 *             in that case we reset recordNum to 0
 *     - use classes from java.util.concurrent and java.util.concurrent.atomic
 *       - so external threads don't block when calling add/addAs
 *       - so internal state doesn't get confused by race conditions
 *     - avoid deadlock
 *       - don't ask threads to block
 *       - use non-blocking queues where possible
 *       - we use a blocking queue for the thread pool since that's required and it makes sense
 *         for threads to block while awaiting more tasks
 *       - we use a blocking queue for the DocumentToWrite main queue just so we can have
 *         the atomic drainTo method used by flush.  But LinkedBlockingQueue is unbounded so
 *         nothing should block on put() and we use poll() to get things so we don't block there either.
 *       - we only use one synchronized block inside initialize() to ensure it only runs once
 *         - after the first call is complete, calls to initialize() won't hit the synchronized block
 *   - try to do what's expected
 *     - try to write documents in the order they are sent to add/addAs
 *       - accepting that asynchronous threads will proceed unpredictably
 *         - for example, thread A might start before thread B and perform less work, but 
 *           thread B might still complete first
 *     - try to match batch sizes to batchSize
 *       - except when flush is called, then immediately write all queued docs
 *     - try to match number of batches in each transaction to transactionSize
 *       - except when any batch fails, then stop writing to that transaction
 *       - except when flush is called, then commit all open transactions
 *   - track
 *     - one queue of DocumentToWrite
 *     - batchCounter to decide if it's time to write a batch
 *       - flush resets this so after flush batch sizes will be normal
 *     - batchNumber to decide which host to use next (round-robin)
 *     - initialized to ensure configuration doesn't change after add/addAs are called
 *     - threadPool of threadCount size for most calls to the server
 *       - not calls during forestinfo or flush
 *     - each host
 *       - host name
 *       - client (contains http connection pool)
 *         - auth challenge once per client
 *       - number of batches
 *         - used to kick off a transaction each time we hit transactionSize
 *       - current transactions (transactionInfos object)
 *         - with batches already written
 *       - unfinishedTransactions
 *         - ready to commit or rollback, but waiting for all threads to stop processing it first
 *     - each transaction
 *       - host
 *       - inProcess == true if any thread is currently working in the transaction
 *       - transactionPermits track how many more batches can use the transaction
 *       - batchesFinished tracks number of batches written (after they're done)
 *         - so we can commit only after batchesFinished = transactionSize
 *       - written == true if any batches have started writing with this transaction
 *         - so we won't commit or rollback an unwritten transaction
 *       - throwable if an error occured but rollback couldn't be called immediately
 *         because another thread was still processing
 *       - alive = false if the transaction has been finished (commit / rollback)
 *       - queuedForCleanup tracks if the transaction is now in unfinishedTransactions
 *       - any batches waiting for finish (commit/rollback) before calling successListeners or failureListeners
 *
 * Known issues
 *   - does not guarantee minimal batch loss on transaction failure
 *     - if two batches attempt to write at the same time and one fails, the other will be part of
 *       the rollback whether it fails or not
 *     - however, any subsequent batches that attempt to write will be in a new transaction
 */
public class WriteBatcherImpl
  extends BatcherImpl
  implements WriteBatcher
{
  private static Logger logger = LoggerFactory.getLogger(WriteBatcherImpl.class);
  private DataMovementManager moveMgr;
  private int transactionSize;
  private String temporalCollection;
  private ServerTransform transform;
  private ForestConfiguration forestConfig;
  private LinkedBlockingQueue<DocumentToWrite> queue = new LinkedBlockingQueue<>();
  private List<WriteBatchListener> successListeners = new ArrayList<>();
  private List<WriteFailureListener> failureListeners = new ArrayList<>();
  private AtomicLong batchNumber = new AtomicLong(0);
  private AtomicLong batchCounter = new AtomicLong(0);
  private AtomicLong itemsSoFar = new AtomicLong(0);
  private HostInfo[] hostInfos;
  private boolean initialized = false;
  private WriteThreadPoolExecutor threadPool;
  private final AtomicBoolean stopped = new AtomicBoolean(false);
  private boolean usingTransactions = false;
  private JobTicket jobTicket;

  public WriteBatcherImpl(DataMovementManager moveMgr, ForestConfiguration forestConfig) {
    super();
    if (moveMgr == null)      throw new IllegalArgumentException("moveMgr must not be null");
    if (forestConfig == null) throw new IllegalArgumentException("forestConfig must not be null");
    this.moveMgr = moveMgr;
    withForestConfig( forestConfig );
  }

  public void initialize() {
    if ( initialized == true ) return;
    synchronized(this) {
      if ( initialized == true ) return;
      if ( getBatchSize() <= 0 ) {
        withBatchSize(1);
        logger.warn("batchSize should be 1 or greater--setting batchSize to 1");
      }
      if ( transactionSize > 1 ) usingTransactions = true;
      int threadCount = getThreadCount();
      // if threadCount is negative or 0, use one thread per host
      if ( threadCount <= 0 ) {
        threadCount = hostInfos.length;
        logger.warn("threadCount should be 1 or greater--setting threadCount to number of hosts ({})", hostInfos.length);
      }
      // create a threadPool where threads are kept alive for up to one minute of inactivity
      threadPool = new WriteThreadPoolExecutor(threadCount, this);

      initialized = true;

      logger.info("threadCount={}", getThreadCount());
      logger.info("batchSize={}", getBatchSize());
      if ( usingTransactions == true ) logger.info("transactionSize={}", transactionSize);
    }
  }

  @Override
  public WriteBatcher add(String uri, AbstractWriteHandle contentHandle) {
    add(uri, null, contentHandle);
    return this;
  }

  @Override
  public WriteBatcher addAs(String uri, Object content) {
    return addAs(uri, null, content);
  }

  @Override
  public WriteBatcher add(String uri, DocumentMetadataWriteHandle metadataHandle,
      AbstractWriteHandle contentHandle)
  {
    initialize();
    requireNotStopped();
    queue.add( new DocumentToWrite(uri, metadataHandle, contentHandle) );
    logger.trace("add uri={}", uri);
    // if we have queued batchSize, it's time to flush a batch
    long recordNum = batchCounter.incrementAndGet();
    boolean timeToWriteBatch = (recordNum % getBatchSize()) == 0;
    if ( timeToWriteBatch ) {
      BatchWriteSet writeSet = newBatchWriteSet(false);
      int i=0;
      for ( ; i < getBatchSize(); i++ ) {
        DocumentToWrite doc = queue.poll();
        if ( doc != null ) {
          writeSet.getWriteSet().add(doc.uri, doc.metadataHandle, doc.contentHandle);
        } else {
          // strange, there should have been a full batch of docs in the queue...
          break;
        }
      }
      writeSet.setItemsSoFar(itemsSoFar.addAndGet(i));
      if ( writeSet.getWriteSet().size() > 0 ) {
        threadPool.submit( new BatchWriter(writeSet) );
      }
    }
    return this;
  }

  @Override
  public WriteBatcher add(WriteEvent... docs) {
    for ( WriteEvent doc : docs ) {
      add( doc.getTargetUri(), doc.getMetadata(), doc.getContent() );
    }
    return this;
  }

  @Override
  public WriteBatcher addAs(String uri, DocumentMetadataWriteHandle metadataHandle,
      Object content) {
    if (content == null) throw new IllegalArgumentException("content must not be null");

    AbstractWriteHandle handle;
    Class<?> as = content.getClass();
    if (AbstractWriteHandle.class.isAssignableFrom(as)) {
      handle = (AbstractWriteHandle) content;
    } else {
      ContentHandle<?> contentHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
      Utilities.setHandleContent(contentHandle, content);
      handle = contentHandle;
    }
    return add(uri, metadataHandle, handle);
  }

  private void requireInitialized() {
    if ( initialized == false ) {
      throw new IllegalStateException("This operation must be called after starting this job");
    }
  }

  private void requireNotInitialized() {
    if ( initialized == true ) {
      throw new IllegalStateException("Configuration cannot be changed after starting this job or calling add or addAs");
    }
  }

  private void requireNotStopped() {
    if ( stopped.get() == true ) throw new IllegalStateException("This instance has been stopped");
  }

  private BatchWriteSet newBatchWriteSet(boolean forceNewTransaction) {
    long batchNum = batchNumber.incrementAndGet();
    return newBatchWriteSet(forceNewTransaction, batchNum);
  }

  private BatchWriteSet newBatchWriteSet(boolean forceNewTransaction, long batchNum) {
    int hostToUse = (int) (batchNum % hostInfos.length);
    HostInfo host = hostInfos[hostToUse];
    DatabaseClient hostClient = host.client;
    BatchWriteSet batchWriteSet = new BatchWriteSet(this, hostClient.newDocumentManager().newWriteSet(),
      hostClient, getTransform(), getTemporalCollection());
    batchWriteSet.setBatchNumber(batchNum);
    if ( usingTransactions ) {
      // before we write, see if we need to open a transaction
      batchWriteSet.onBeforeWrite( () -> {
        long transactionCount = host.transactionCounter.getAndIncrement();
        // if this is the first batch in this transaction, it's time to initialize a transaction
        boolean timeForNewTransaction = (transactionCount % getTransactionSize()) == 0;
        if ( timeForNewTransaction ) {
          batchWriteSet.setTransactionInfo( transactionOpener(host, hostClient, transactionSize) );
        } else {
          TransactionInfo transactionInfo = host.getTransactionInfo();
          if ( transactionInfo != null ) {
            // we have an open transaction to use
            batchWriteSet.setTransactionInfo( transactionInfo );
            transactionInfo.inProcess.incrementAndGet();
          } else {
            // no transactions were ready, so open a new one
            batchWriteSet.setTransactionInfo( transactionOpener(host, hostClient, transactionSize) );
          }
        }
      });
    }
    batchWriteSet.onSuccess( () -> {
        // if we're not using transactions then timeToCommit is always true
        boolean timeToCommit = true;
        boolean committed = false;
        if ( usingTransactions ) {
          TransactionInfo transactionInfo = batchWriteSet.getTransactionInfo();
          long batchNumFinished = transactionInfo.batchesFinished.incrementAndGet();
          timeToCommit = (batchNumFinished == getTransactionSize());
          if ( forceNewTransaction || timeToCommit ) {
            // this is the last batch in the transaction
            if ( transactionInfo.alive.get() == true ) {
              // if we're the only thread currently processing this transaction
              if ( transactionInfo.inProcess.get() <= 1 ) {
                // we're about to commit so let's restart transactionCounter
                host.transactionCounter.set(0);
                transactionInfo.transaction.commit();
                committed = true;
                sendSuccessToListeners(transactionInfo.batches);
              } else {
                // we chose not to commit because another thread is still processing,
                // so queue up this batchWriteSet
                transactionInfo.batches.add(batchWriteSet);
                // and queue up this commit
                host.unfinishedTransactions.add(transactionInfo);
                timeToCommit = false;
              }
            }
          } else {
            // this is *not* the last batch in the transaction
            // so queue up this batchWriteSet
            transactionInfo.batches.add(batchWriteSet);
          }
          transactionInfo.inProcess.decrementAndGet();
        } else {
          committed = true;
        }
        if ( committed ) {
          sendSuccessToListeners(batchWriteSet);
        }
    });
    batchWriteSet.onFailure( (throwable) -> {
      // reset the transactionCounter so the next write will start a new transaction
      host.transactionCounter.set(0);
      if ( usingTransactions ) {
        TransactionInfo transactionInfo = batchWriteSet.getTransactionInfo();
        transactionInfo.throwable.set(throwable);
        // if we're the only thread currently processing this transaction
        if ( transactionInfo.inProcess.get() <= 1 ) {
          try {
            logger.warn("Rolling back transaction because of throwable: {}", throwable.toString());
            transactionInfo.transaction.rollback();
          } catch(Throwable t2) {
            throwable.addSuppressed(t2);
            logger.warn("Failure to rollback transaction: {}", t2.toString());
          }
          sendThrowableToListeners(throwable, null, transactionInfo.batches);
        } else {
          host.unfinishedTransactions.add(transactionInfo);
        }
        transactionInfo.inProcess.decrementAndGet();
      }
      sendThrowableToListeners(throwable, "Error writing batch: {}", batchWriteSet);
    });
    return batchWriteSet;
  }

  @Override
  public WriteBatcher onBatchSuccess(WriteBatchListener listener) {
    successListeners.add(listener);
    return this;
  }
  @Override
  public WriteBatcher onBatchFailure(WriteFailureListener listener) {
    failureListeners.add(listener);
    return this;
  }

  @Override
  public void retry(WriteBatch batch) {
    boolean forceNewTransaction = true;
    BatchWriteSet writeSet = newBatchWriteSet(forceNewTransaction, batch.getJobBatchNumber());
    writeSet.onFailure(throwable -> {
      if ( throwable instanceof RuntimeException ) throw (RuntimeException) throwable;
      else throw new DataMovementException("Failed to retry batch", throwable);
    });
    writeSet.setItemsSoFar(itemsSoFar.get());
    for ( WriteEvent doc : batch.getItems() ) {
      writeSet.getWriteSet().add(doc.getTargetUri(), doc.getMetadata(), doc.getContent());
    }
    BatchWriter runnable = new BatchWriter(writeSet);
    runnable.run();
  }

  @Override
  public WriteBatchListener[]        getBatchSuccessListeners() {
    return successListeners.toArray(new WriteBatchListener[successListeners.size()]);
  }

  @Override
  public WriteFailureListener[] getBatchFailureListeners() {
    return failureListeners.toArray(new WriteFailureListener[failureListeners.size()]);
  }

  @Override
  public void setBatchSuccessListeners(WriteBatchListener... listeners) {
    requireNotInitialized();
    successListeners.clear();
    if ( listeners != null ) {
      for ( WriteBatchListener listener : listeners ) {
        successListeners.add(listener);
      }
    }
  }

  @Override
  public void setBatchFailureListeners(WriteFailureListener... listeners) {
    requireNotInitialized();
    failureListeners.clear();
    if ( listeners != null ) {
      for ( WriteFailureListener listener : listeners ) {
        failureListeners.add(listener);
      }
    }
  }

  @Override
  public void flushAsync() {
    flush(false);
  }

  @Override
  public void flushAndWait() {
    flush(true);
  }

  private void flush(boolean waitForCompletion) {
    requireInitialized();
    requireNotStopped();
    // drain any docs left in the queue
    List<DocumentToWrite> docs = new ArrayList<>();
    long recordInBatch = batchCounter.getAndSet(0);
    queue.drainTo(docs);
    logger.info("flushing {} queued docs", docs.size());
    Iterator<DocumentToWrite> iter = docs.iterator();
    boolean forceNewTransaction = true;
    for ( int i=0; iter.hasNext(); i++ ) {
      if ( isStopped() == true ) {
        logger.warn("Job is now stopped, preventing the flush of {} queued docs", docs.size() - i);
        return;
      }
      BatchWriteSet writeSet = newBatchWriteSet(forceNewTransaction);
      int j=0;
      for ( ; j < getBatchSize() && iter.hasNext(); j++ ) {
        DocumentToWrite doc = iter.next();
        writeSet.getWriteSet().add(doc.uri, doc.metadataHandle, doc.contentHandle);
      }
      writeSet.setItemsSoFar(itemsSoFar.addAndGet(j));
      threadPool.submit( new BatchWriter(writeSet) );
    }

    if ( waitForCompletion == true ) awaitCompletion();

    // commit any transactions remaining open
    if ( usingTransactions == true ) {
      Runnable cleanupTransactions = () -> {
        // first clean up old transactions
        cleanupUnfinishedTransactions();

        // now commit any current transactions
        for ( HostInfo host : hostInfos ) {
          TransactionInfo transactionInfo;
          while ( (transactionInfo = host.getTransactionInfoAndDrainPermits()) != null ) {
            TransactionInfo transactionInfoCopy = transactionInfo;
            completeTransaction(transactionInfoCopy);
          }
        }
      };
      if ( waitForCompletion == true ) {
        cleanupTransactions.run();
      } else {
        threadPool.submit( cleanupTransactions );
      }
    }
  }

  public boolean completeTransaction(TransactionInfo transactionInfo) {
    boolean completed = false;
    try {
      if ( transactionInfo.alive.get() == true && 
              transactionInfo.inProcess.get() <= 0 && 
              transactionInfo.written.get() == true ) {
        if ( transactionInfo.throwable.get() != null ) {
          transactionInfo.transaction.rollback();
          sendThrowableToListeners(transactionInfo.throwable.get(), "Failure during transaction: {}",
            transactionInfo.batches);
        } else {
          transactionInfo.transaction.commit();
          sendSuccessToListeners(transactionInfo.batches);
        }
        completed = true;
      }
    } catch (Throwable t) {
      transactionInfo.throwable.set(t);
      sendThrowableToListeners(t, "Failure to complete transaction: {}", transactionInfo.batches);
    }
    return completed;
  }

  private void sendSuccessToListeners(Collection<BatchWriteSet> batches) {
    for ( BatchWriteSet batch : batches ) {
      sendSuccessToListeners(batch);
    }
  }

  private void sendSuccessToListeners(BatchWriteSet batchWriteSet) {
    WriteBatch batch = batchWriteSet.getBatchOfWriteEvents();
    for ( WriteBatchListener successListener : successListeners ) {
      try {
        successListener.processEvent(batch);
      } catch (Throwable t) {
        logger.error("Exception thrown by an onBatchSuccess listener", t);
      }
    }
  }

  private void sendThrowableToListeners(Throwable t, String message, Collection<BatchWriteSet> batches) {
    for ( BatchWriteSet batchWriteSet : batches ) {
      sendThrowableToListeners(t, null, batchWriteSet);
    }
    if ( message != null ) logger.warn(message, t.toString());
  }

  private void sendThrowableToListeners(Throwable t, String message, BatchWriteSet batchWriteSet) {
    WriteBatch batch = batchWriteSet.getBatchOfWriteEvents();
    for ( WriteFailureListener failureListener : failureListeners ) {
      try {
        failureListener.processFailure(batch, t);
      } catch (Throwable t2) {
        logger.error("Exception thrown by an onBatchFailure listener", t2);
      }
    }
    if ( message != null ) logger.warn(message, t.toString());
  }

  public void start(JobTicket ticket) {
    jobTicket = ticket;
    initialize();
  }

  public void stop() {
    stopped.set(true);
    threadPool.shutdownNow();
    awaitCompletion();
  }

  @Override
  public boolean isStopped() {
    return threadPool != null && threadPool.isTerminated();
  }

  @Override
  public JobTicket getJobTicket() {
    requireInitialized();
    return jobTicket;
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return threadPool.awaitTermination(timeout, unit);
  }

  @Override
  public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
    return threadPool.awaitCompletion(timeout, unit);
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
  public WriteBatcher withJobName(String jobName) {
    requireNotInitialized();
    super.withJobName(jobName);
    return this;
  }

  @Override
  public WriteBatcher withBatchSize(int batchSize) {
    requireNotInitialized();
    super.withBatchSize(batchSize);
    return this;
  }

  @Override
  public WriteBatcher withThreadCount(int threadCount) {
    requireNotInitialized();
    super.withThreadCount(threadCount);
    return this;
  }

  public WriteBatcher withTransactionSize(int transactionSize) {
    requireNotInitialized();
    this.transactionSize = transactionSize;
    return this;
  }

  public int getTransactionSize() {
    return transactionSize;
  }

  @Override
  public WriteBatcher withTemporalCollection(String collection) {
    requireNotInitialized();
    this.temporalCollection = collection;
    return this;
  }

  @Override
  public String getTemporalCollection() {
    return temporalCollection;
  }

  @Override
  public WriteBatcher withTransform(ServerTransform transform) {
    requireNotInitialized();
    this.transform = transform;
    return this;
  }

  @Override
  public ServerTransform getTransform() {
    return transform;
  }

  @Override
  public synchronized WriteBatcher withForestConfig(ForestConfiguration forestConfig) {
    // get the list of hosts to use
    Forest[] forests = forestConfig.listForests();
    if ( forests.length == 0 ) {
      throw new IllegalStateException("WriteBatcher requires at least one writeable forest");
    }
    Map<String,Forest> hosts = new HashMap<>();
    for ( Forest forest : forests ) {
      if ( forest.getPreferredHost() == null ) {
        throw new IllegalStateException("Hostname must not be null for any forest");
      }
      hosts.put(forest.getPreferredHost(), forest);
    }
    Map<String,HostInfo> existingHostInfos = new HashMap<>();
    Map<String,HostInfo> removedHostInfos = new HashMap<>();
    if ( hostInfos != null ) {
      for ( HostInfo hostInfo : hostInfos ) {
        existingHostInfos.put(hostInfo.hostName, hostInfo);
        removedHostInfos.put(hostInfo.hostName, hostInfo);
      }
    }
    logger.info("(withForestConfig) Using {} hosts with forests for \"{}\"", hosts.keySet(), forests[0].getDatabaseName());
    // initialize a DatabaseClient for each host
    HostInfo[] newHostInfos = new HostInfo[hosts.size()];
    int i=0;
    for ( String host : hosts.keySet() ) {
      if ( existingHostInfos.get(host) != null ) {
        newHostInfos[i] = existingHostInfos.get(host);
        removedHostInfos.remove(host);
      } else {
        newHostInfos[i] = new HostInfo();
        newHostInfos[i].hostName = host;
        Forest forest = hosts.get(host);
        // this is a host-specific client (no DatabaseClient is actually forest-specific)
        newHostInfos[i].client = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
        logger.info("Adding DatabaseClient on port {} for host \"{}\" to the rotation",
          newHostInfos[i].client.getPort(), host);
      }
      i++;
    }
    this.forestConfig = forestConfig;
    this.hostInfos = newHostInfos;

    if ( removedHostInfos.size() > 0 ) {
      // since some hosts have been removed, let's remove from the queue any jobs that were targeting that host
      List<Runnable> tasks = new ArrayList<>();
      threadPool.getQueue().drainTo(tasks);
      for ( Runnable task : tasks ) {
        if ( task instanceof BatchWriter ) {
          BatchWriter writerTask = (BatchWriter) task;
          if ( removedHostInfos.containsKey(writerTask.writeSet.getClient().getHost()) ) {
            // this batch was targeting a host that's no longer on the list
            // if we re-add these docs they'll now be in batches that target acceptable hosts
            add(writerTask.writeSet.getBatchOfWriteEvents().getItems());
            // jump to the next task
            continue;
          }
        }
        // this task is still valid so add it back to the queue
        threadPool.submit(task);
      }
      for ( HostInfo removedHostInfo : removedHostInfos.values() ) {
        cleanupUnfinishedTransactions(removedHostInfo);
      }
    }
    return this;
  }

  @Override
  public ForestConfiguration getForestConfig() {
    return forestConfig;
  }

  public static class DocumentToWrite {
    public String uri;
    public DocumentMetadataWriteHandle metadataHandle;
    public AbstractWriteHandle contentHandle;

    public DocumentToWrite(String uri, DocumentMetadataWriteHandle metadata, AbstractWriteHandle content) {
      this.uri = uri;
      this.metadataHandle = metadata;
      this.contentHandle = content;
    }
  }

  public static class HostInfo {
    public String hostName;
    public DatabaseClient client;
    public AtomicLong transactionCounter = new AtomicLong(0);
    public ConcurrentLinkedDeque<TransactionInfo> transactionInfos = new ConcurrentLinkedDeque<>();
    public ConcurrentLinkedQueue<TransactionInfo> unfinishedTransactions = new ConcurrentLinkedQueue<>();

    private TransactionInfo getTransactionInfoAndDrainPermits() {
      TransactionInfo transactionInfo = transactionInfos.poll();
      if ( transactionInfo == null ) return null;
      // if any more batches can be written for this transaction then transactionPermits
      // is greater than zero and this transaction is available
      int permits = transactionInfo.transactionPermits.getAndSet(0);
      if ( permits > 0 ) {
        return transactionInfo;
      } else {
        // otherwise return null
        return null;
      }
    }

    private TransactionInfo getTransactionInfo() {
        // if any more batches can be written for this transaction then transactionPermits
        // can be acquired and this transaction is available
        // otherwise block until a new transaction is available with new permits
        // get one off the queue if available, if not then block until one is avialable
        TransactionInfo transactionInfo = transactionInfos.poll();
        if ( transactionInfo == null ) return null;
        // remove one permit
        int permits = transactionInfo.transactionPermits.decrementAndGet();
        // if there are permits left, push this back onto the queue
        if ( permits >= 0 ) {
          if ( permits > 0 ) {
            // there are more permits left, so push it back onto the front of the queue
            transactionInfos.addFirst(transactionInfo);
          } else {
            // this is the last permit, make sure this transaction gets completed
            unfinishedTransactions.add(transactionInfo);
          }
          return transactionInfo;
        } else {
          // somehow this transaction was on the queue with no permits left
          // make sure this transaction gets completed
          unfinishedTransactions.add(transactionInfo);
          // let's return a different transaction that has permits
          return getTransactionInfo();
        }
    }

    public void addTransactionInfo(TransactionInfo transactionInfo) {
      transactionInfos.add(transactionInfo);
    }

    public void releaseTransactionInfo(TransactionInfo toRelease) {
      toRelease.transactionPermits.set(0);
      transactionInfos.remove(toRelease);
      unfinishedTransactions.remove(toRelease);
    }
  }

  public static class TransactionInfo {
    private Transaction transaction;
    public AtomicBoolean alive = new AtomicBoolean(false);
    public AtomicBoolean written = new AtomicBoolean(false);
    public AtomicReference<Throwable> throwable = new AtomicReference<>();
    public AtomicLong inProcess = new AtomicLong(0);
    public AtomicLong batchesFinished = new AtomicLong(0);
    public AtomicBoolean queuedForCleanup = new AtomicBoolean(false);
    public ConcurrentLinkedQueue<BatchWriteSet> batches = new ConcurrentLinkedQueue<>();
    private AtomicInteger transactionPermits = new AtomicInteger(0);
  }


  private void cleanupUnfinishedTransactions() {
    for ( HostInfo host : hostInfos ) {
      cleanupUnfinishedTransactions(host);
    }
  }

  private void cleanupUnfinishedTransactions(HostInfo host) {
    Iterator<TransactionInfo> iterator = host.unfinishedTransactions.iterator();
    while ( iterator.hasNext() ) {
      TransactionInfo transactionInfo = iterator.next();
      if ( transactionInfo.alive.get() == false ) {
        iterator.remove();
      } else if ( transactionInfo.queuedForCleanup.get() == true ) {
        // skip this one, it's already queued
      } else {
        if ( transactionInfo.inProcess.get() <= 0 ) {
          if ( transactionInfo.written.get() == true ) {
            transactionInfo.queuedForCleanup.set(true);
            threadPool.submit( () -> {
              if ( completeTransaction(transactionInfo) ) {
                host.unfinishedTransactions.remove(transactionInfo);
              } else {
                // let's try again next cleanup
                transactionInfo.queuedForCleanup.set(false);
              }
            });
          } else {
            iterator.remove();
          }
        }
      }
    }
  }

  public TransactionInfo transactionOpener(HostInfo host, DatabaseClient client, int transactionSize) {
    TransactionInfo transactionInfo = new TransactionInfo();
    transactionInfo.transactionPermits.set(transactionSize - 1);
    Transaction realTransaction = client.openTransaction();
    logger.trace("opened transaction {}", realTransaction.getTransactionId());
    // wrapping Transaction so I can call releaseTransactionInfo when commit or rollback are called
    Transaction transaction = new Transaction() {
      @Override
      public void commit() {
        host.releaseTransactionInfo(transactionInfo);
        boolean alive = transactionInfo.alive.getAndSet(false);
        if ( alive == true ) {
          realTransaction.commit();
          logger.trace("committed transaction {}", realTransaction.getTransactionId());
        }
      }
      @Override
      public List<javax.ws.rs.core.NewCookie> getCookies() { return realTransaction.getCookies(); }
      @Override
      public String getHostId() { return realTransaction.getHostId(); }
      @Override
      public String getTransactionId() { return realTransaction.getTransactionId(); }
      @Override
      public <T extends StructureReadHandle> T readStatus(T handle) {
        return realTransaction.readStatus(handle);
      }
      @Override
      public void rollback() {
        host.releaseTransactionInfo(transactionInfo);
        boolean alive = transactionInfo.alive.getAndSet(false);
        if ( alive == true ) {
          realTransaction.rollback();
          logger.trace("rolled back transaction {}", realTransaction.getTransactionId());
        }
      }
    };
    transactionInfo.transaction = transaction;
    transactionInfo.alive.set(true);
    transactionInfo.inProcess.incrementAndGet();
    host.addTransactionInfo(transactionInfo);
    cleanupUnfinishedTransactions();
    return transactionInfo;
  }

  public static class BatchWriter implements Runnable {
    private BatchWriteSet writeSet;

    public BatchWriter(BatchWriteSet writeSet) {
      if ( writeSet.getWriteSet().size() == 0 ) {
        throw new IllegalStateException("Attempt to write an empty batch");
      }
      this.writeSet = writeSet;
    }

    @Override
    public void run() {
      try {
        Runnable onBeforeWrite = writeSet.getOnBeforeWrite();
        if ( onBeforeWrite != null ) {
          onBeforeWrite.run();
        }
        TransactionInfo transactionInfo = writeSet.getTransactionInfo();
        if ( transactionInfo == null || transactionInfo.alive.get() == true ) {
          Transaction transaction = null;
          if ( transactionInfo != null ) {
            transaction = transactionInfo.transaction;
            transactionInfo.written.set(true);
          }
          if ( writeSet.getTemporalCollection() == null ) {
            writeSet.getClient().newDocumentManager().write(
              writeSet.getWriteSet(), writeSet.getTransform(), transaction
            );
          } else {
            // to get access to the TemporalDocumentManager write overload we need to instantiate
            // a JSONDocumentManager or XMLDocumentManager, but we don't want to make assumptions about content
            // format, so we'll set the default content format to unknown
            XMLDocumentManager docMgr = writeSet.getClient().newXMLDocumentManager();
            docMgr.setContentFormat(Format.UNKNOWN);
            docMgr.write(
              writeSet.getWriteSet(), writeSet.getTransform(),
              transaction, writeSet.getTemporalCollection()
            );
          }
          logger.trace("sent batch {} to host \"{}\"", writeSet.getBatchNumber(), writeSet.getClient().getHost());
          closeAllHandles();
          Runnable onSuccess = writeSet.getOnSuccess();
          if ( onSuccess != null ) {
            onSuccess.run();
          }
        } else {
          throw new DataMovementException("Failed to write because transaction already underwent commit or rollback", null);
        }
      } catch (Throwable t) {
        logger.trace("failed batch sent to host \"{}\"", writeSet.getClient().getHost());
        Consumer<Throwable> onFailure = writeSet.getOnFailure();
        if ( onFailure != null ) {
          onFailure.accept(t);
        }
      }
    }

    private void closeAllHandles() throws Throwable {
      Throwable lastThrowable = null;
      for ( DocumentWriteOperation doc : writeSet.getWriteSet() ) {
        try {
          if ( doc.getContent() instanceof Closeable ) {
            ((Closeable) doc.getContent()).close();
          }
          if ( doc.getMetadata() instanceof Closeable ) {
            ((Closeable) doc.getMetadata()).close();
          }
        } catch (Throwable t) {
          logger.error("error calling close()", t);
          lastThrowable = t;
        }
      }
      if ( lastThrowable != null ) throw lastThrowable;
    }
  }

  public static class WriteThreadPoolExecutor extends ThreadPoolExecutor {
    private Object objectToNotifyFrom;
    private ConcurrentHashMap<Runnable,WrappedFuture<?>> futures = new ConcurrentHashMap<>();

    private static class FutureAwareQueue extends LinkedBlockingQueue<Runnable> {
      Map<Runnable,WrappedFuture<?>> futures;

      FutureAwareQueue(int capacity) {
        super(capacity);
      }

      void setFutures(Map<Runnable,WrappedFuture<?>> futures) {
        this.futures = futures;
      }

      @Override
      public int drainTo(Collection<? super Runnable> c) {
        List<Runnable> tasks = new ArrayList<>();
        int count = super.drainTo(tasks);
        // clear the associated futures
        for ( Runnable task : tasks ) {
logger.debug("DEBUG: [drainTo] task =[" + task  + "]");
logger.debug("DEBUG: [drainTo] task.getClass() =[" + task.getClass()  + "]");
logger.debug("DEBUG: [drainTo] futures.get(task) =[" + futures.get(task)  + "]");
          WrappedFuture<?> future = futures.get(task);
          if ( future == null || future.isStarted() == false ) {
            futures.remove(task);
          }
          c.add(task);
        }
        return count;
      }
    }

    // This wrapper allows us to call get() and wait for a future to complete
    // even if it was cancelled after it started. Without this wrapper, the
    // Future would throw CancellationException immediately when we call get()
    // rather than block until completion
    private static class WrappedFuture<V> implements Future<V> {
      private Future<V> future;
      private boolean isCancelled = false;
      private boolean isStarted = false;

      WrappedFuture(Future<V> future) {
        this.future = future;
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled = true;
        return true;
      }

      public V get() throws InterruptedException, ExecutionException {
logger.debug("DEBUG: [WrappedFuture.get] isCancelled =[" + isCancelled  + "]");
        if ( isCancelled == false ) {
          return future.get();
        } else {
logger.debug("DEBUG: [WrappedFuture.get] isStarted =[" + isStarted  + "]");
          if ( isStarted ) {
            // we're already started, so let's make that clear by waiting until we're finished before
            // we throw CancellationException
            future.get();
            throw new CancellationException();
          } else {
            throw new CancellationException();
          }
        }
      }

      public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException
      {
        return future.get(timeout, unit);
      }

      public boolean isCancelled() {
        return isCancelled;
      }

      public boolean isDone() {
        return future.isDone();
      }

      public void setStarted() {
        isStarted = true;
      }

      public boolean isStarted() {
        return isStarted;
      }
    }

    public WriteThreadPoolExecutor(int threadCount, Object objectToNotifyFrom) {
      super(threadCount, threadCount, 1, TimeUnit.MINUTES,
        new FutureAwareQueue(threadCount * 5), new ThreadPoolExecutor.CallerRunsPolicy());
      ((FutureAwareQueue) getQueue()).setFutures(futures);
      allowCoreThreadTimeOut(true);
      this.objectToNotifyFrom = objectToNotifyFrom;
    }

    protected void beforeExecute(Thread thread, Runnable task) {
      WrappedFuture<?> future = futures.get(task);
logger.debug("DEBUG: [beforeExecute] task =[" + task  + "]");
logger.debug("DEBUG: [beforeExecute] task.getClass() =[" + task.getClass()  + "]");
logger.debug("DEBUG: [beforeExecute] future =[" + future  + "]");
      if ( future != null ) future.setStarted();
      super.beforeExecute(thread, task);
    }

    protected void afterExecute(Runnable task, Throwable t) {
      super.afterExecute(task, t);
logger.debug("DEBUG: [afterExecute] task =[" + task  + "]");
logger.debug("DEBUG: [afterExecute] task.getClass() =[" + task.getClass()  + "]");
logger.debug("DEBUG: [afterExecute] futures.get(task) =[" + futures.get(task)  + "]");
      futures.remove(task);
      if ( t != null ) {
        logger.error("Task threw an Exception", t);
      }
    }

    @Override
    public Future<?> submit(Runnable task) {
      Future<?> future = super.submit(task);
      if ( future instanceof FutureTask<?> ) {
        // surprisingly, because of the current implementation of
        // ThreadPoolExecutor.beforeExecute and afterExecute and
        // LinkedBlockingQueue.drainTo, we need to key off the FutureTask
        futures.put((FutureTask<?>) future, new WrappedFuture(future));
      }
      return future;
    }

    public List<Runnable> shutdownNow() {
      List<Runnable> tasks = super.shutdownNow();
      Map<Runnable,WrappedFuture<?>> snapshot = new HashMap<>(futures);
      futures.clear();
      for ( Runnable task : snapshot.keySet() ) {
        WrappedFuture<?> future = snapshot.get(task);
        if ( future != null ) {
          future.cancel(true);
        }
      }
      return tasks;
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
      if ( unit == null ) throw new IllegalArgumentException("unit cannot be null");
      List<WrappedFuture<?>> snapshotOfFutures = new ArrayList<>();
      for ( WrappedFuture<?> future : futures.values() ) {
        if ( future != null ) snapshotOfFutures.add( future );
      }
      long duration = unit.convert(timeout, TimeUnit.MILLISECONDS);
      for ( Future<?> future : snapshotOfFutures ) {
        try {
          long startTime = System.currentTimeMillis();
logger.debug("DEBUG: [awaitCompletion] future.isCancelled() =[" + future.isCancelled()  + "]");
          if ( ! future.isCancelled() ) {
            try {
              future.get(duration, TimeUnit.MILLISECONDS);
            } catch(CancellationException ce) {
              if ( isTerminating() || isTerminated() || isShutdown() ) {
                // no problem, this future was cancelled legitimately when shutdownNow() was called
              } else {
                // there's a problem, this future was cancelled for an unknown reason
                throw ce;
              }
            }
          }
          /*
          if ( future.isCancelled() ) {
            duration = waitOnCancelledFuture(future, duration);
          } else {
            try {
              future.get(duration, TimeUnit.MILLISECONDS);
              duration -= System.currentTimeMillis() - startTime;
logger.debug("DEBUG: [awaitCompletion] duration =[" + duration  + "]");
            } catch(CancellationException ce) {
              if ( isTerminating() || isTerminated() || isShutdown() ) {
                logger.warn("CancellationException");
                // this future was cancelled legitimately when shutdownNow() was called
                // but we'd still like to wait for it to finish
                duration = waitOnCancelledFuture(future, duration);
              } else {
                // there's a problem, this future was cancelled for an unknown reason
                throw ce;
              }
            }
          }
          */
          duration -= System.currentTimeMillis() - startTime;
logger.debug("DEBUG: [WriteBatcherImpl] duration2 =[" + duration  + "]");
          if ( duration < 0 ) {
            // times up!  We didn't finish before timeout...
            return false;
          }
        } catch (TimeoutException e) {
          return false;
        } catch (InterruptedException e) {
          throw e;
        } catch (ExecutionException e) {
          logger.error("", e.getCause());
        } catch (Exception e) {
          logger.error("", e);
        }
      }
      return true;
    }

    private long waitOnCancelledFuture(Future future, long duration) {
      long startTime = System.currentTimeMillis();
      while (future.isDone() == false) {
logger.debug("DEBUG: [waitOnCancelledFuture] future.isDone() =[" + future.isDone()  + "]");
        duration -= System.currentTimeMillis() - startTime;
logger.debug("DEBUG: [waitOnCancelledFuture] duration =[" + duration  + "]");
        if ( duration < 0 ) {
          // times up!  We didn't finish before timeout...
          return duration;
        }
        // sleep up to 100 ms to avoid unnecessary CPU use while we wait
        try { Thread.sleep(duration % 100); } catch (InterruptedException e) {}
      }
logger.debug("DEBUG: [waitOnCancelledFuture] future.isDone() =[" + future.isDone()  + "]");
      return duration;
    }
  }
}
