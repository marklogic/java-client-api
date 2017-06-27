/*
 * Copyright 2015-2017 MarkLogic Corporation
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
package com.marklogic.client.datamovement;

import java.util.concurrent.TimeUnit;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

/**
 * To facilitate long-running write jobs, batches documents added by many
 * external threads and coordinates internal threads to send the batches
 * round-robin to all appropriate hosts in the cluster.  Appropriate hosts are
 * those containing a forest associated with the database for the
 * DatabaseClient provided to DataMovementManager.  Many external threads
 * (threads not managed by WriteBatcher) can concurrently add documents by
 * calling WriteBatcher {@link #add add} or {@link #addAs addAs}.  Each
 * time enough documents are added to make a batch, the batch is added to an
 * internal queue where the first available internal thread will pick it up and
 * write it to the server.  Since batches are not written until they are full,
 * you should always call {@link #flushAsync} or {@link #flushAndWait} when no
 * more documents will be written to ensure that any partial batch is written.
 *
 * Sample Usage:
 *
 *     WriteBatcher whb = dataMovementManager.newWriteBatcher()
 *         .withBatchSize(100)
 *         .withThreadCount(20)
 *         .onBatchSuccess(batch -> {
 *             logger.debug("batch # {}, so far: {}", batch.getJobBatchNumber(), batch.getJobWritesSoFar());
 *         })
 *         .onBatchFailure((batch,throwable) -> throwable.printStackTrace() );
 *     JobTicket ticket = dataMovementManager.startJob(whb);
 *     whb.add  ("doc1.txt", new StringHandle("doc1 contents"));
 *     whb.addAs("doc2.txt", "doc2 contents");
 *
 *     whb.flushAndWait(); // send the two docs even though they're not a full batch
 *     dataMovementManager.stopJob(ticket);
 *
 * Note: All Closeable content or metadata handles passed to {@link #add add}
 * methods will be closed as soon as possible (after the batch is written).
 * This is to avoid IO resource leakage.  This differs from the normal usage of
 * the Java Client API because WriteBatcher is asynchronous so there's no
 * easy way to know which handles have finished writing and can therefore be
 * closed.  So to save confusion we close all handles for you.  If you have a
 * resource that must be closed after a batch is written, but is not closed by
 * your handle, override the close method of any Closeable handle and close
 * your resource there.
 */
public interface WriteBatcher extends Batcher {
  /**
   * Add a document to be batched then written to the server when a batch is full
   * or {@link #flushAsync} or {@link #flushAndWait} is called.
   *
   * #####See Also:
   *   [the Java Guide](http://docs.marklogic.com/guide/java/document-operations) for more on using handles
   *
   * @param uri the document uri
   * @param contentHandle the document contents
   * @return WriteBatcher the batcher containing the documents added
   */
  WriteBatcher add(String uri, AbstractWriteHandle contentHandle);

  /**
   * Add a document to be batched then written to the server when a batch is full
   * or {@link #flushAsync} or {@link #flushAndWait} is called.
   *
   * #####See Also:
   *   [IO Shortcut in MarkLogic Java Client API](http://www.marklogic.com/blog/io-shortcut-marklogic-java-client-api/)
   *   for more on using the *As shortcut methods
   *
   * @param uri the document uri
   * @param content the document contents
   * @return WriteBatcher the batcher containing the documents added
   */
  WriteBatcher addAs(String uri, Object content);

  /**
   * Add a document to be batched then written to the server when a batch is full
   * or {@link #flushAsync} or {@link #flushAndWait} is called.
   *
   * #####See Also:
   *   [the Java Guide](http://docs.marklogic.com/guide/java/document-operations) for more on using handles
   *
   * @param uri the document uri
   * @param metadataHandle the metadata (collection, permissions, metdata values, properties, quality)
   * @param contentHandle the document contents
   * @return WriteBatcher the batcher containing the documents added
   */
  WriteBatcher add(String uri, DocumentMetadataWriteHandle metadataHandle,
                   AbstractWriteHandle contentHandle);

  /**
   * Add a document to be batched then written to the server when a batch is full
   * or {@link #flushAsync} or {@link #flushAndWait} is called.
   *
   * #####See Also:
   *   [IO Shortcut in MarkLogic Java Client API](http://www.marklogic.com/blog/io-shortcut-marklogic-java-client-api/)
   *   for more on using the *As shortcut methods
   *
   * @param uri the document uri
   * @param metadataHandle the metadata (collection, permissions, metdata values, properties, quality)
   * @param content the document contents
   * @return WriteBatcher the batcher containing the documents added
   */
  WriteBatcher addAs(String uri, DocumentMetadataWriteHandle metadataHandle,
                     Object content);

  /**
   * Add docs in the form of WriteEvents.  This is a convenience method for re-adding
   * documents from failed batches.
   *
   * @param docs the batch of WriteEvents where each WriteEvent represents one document
   * @return WriteBatcher the batcher containing the documents added
   */
  WriteBatcher add(WriteEvent... docs);

  /**
   * Add a listener to run each time a batch is successfully written.
   * @param listener the action which has to be done when the batch gets written
   *        successfully
   * @return this instance for method chaining
   */
  WriteBatcher onBatchSuccess(WriteBatchListener listener);

  /**
   * Add a listener to run each time there is an exception writing a batch.
   *
   * These listeners will not run when an exception is thrown by a listener
   * registered with onBatchSuccess.  To learn more, please see
   * [Handling Exceptions in Listeners](package-summary.html#errs)
   *
   * @param listener the code to run when a failure occurs
   * @return this instance for method chaining
   */
  WriteBatcher onBatchFailure(WriteFailureListener listener);

  /**
   * Retry in the same thread to send a batch that failed. This method will
   * throw an Exception if it fails again, so it can be wrapped in a try-catch
   * block.
   * @param queryEvent the information about the batch that failed
   */
  public void retry(WriteBatch queryEvent);

  /*
  public WriteBatcher withTransactionSize(int transactionSize);
  public int getTransactionSize();
  */

  /**
   * Get the array of WriteBatchListener instances registered via
   * onBatchSuccess.
   *
   * @return the WriteBatchListener instances this batcher
   *   is using
   */
  WriteBatchListener[] getBatchSuccessListeners();

  /**
   * Get the array of WriteFailureListener instances
   * registered via onBatchFailure including the HostAvailabilityListener
   * registered by default.
   *
   * @return the WriteFailureListener instances this batcher
   *   is using
   */
  WriteFailureListener[] getBatchFailureListeners();

  /**
   * Remove any existing WriteBatchListener instances registered
   * via onBatchSuccess and replace them with the provided listeners.
   *
   * @param listeners the WriteBatchListener instances this
   *   batcher should use
   */
  void setBatchSuccessListeners(WriteBatchListener... listeners);

  /**
   * Remove any existing WriteFailureListener instances
   * registered via onBatchFailure including the HostAvailabilityListener
   * registered by default and replace them with the provided listeners.
   *
   * @param listeners the WriteFailureListener instances this
   *   batcher should use
   */
  void setBatchFailureListeners(WriteFailureListener... listeners);

  /**
   * The temporal collection to use for a temporal document insert
   *
   * @param collection The temporal collection to use for a temporal document insert
   *
   * @return this instance for method chaining
   */
  WriteBatcher withTemporalCollection(String collection);

  /**
   * The temporal collection configured for temporal document inserts
   *
   * @return The temporal collection configured for temporal document inserts
   */
  String getTemporalCollection();

  /**
   * The ServerTransform to modify each document from each batch before it is
   * written to the database.
   *
   * @param transform The ServerTransform to run on each document from each batch.
   *
   * @return this instance for method chaining
   */
  WriteBatcher withTransform(ServerTransform transform);
  ServerTransform getTransform();

  /**
   * If the server forest configuration changes mid-job, it can be re-fetched
   * with {@link DataMovementManager#readForestConfig} then set via
   * withForestConfig.
   *
   * @param forestConfig the updated ForestConfiguration
   *
   * @return this instance for method chaining
   */
  @Override
  WriteBatcher withForestConfig(ForestConfiguration forestConfig);

  /**
   * Sets the job name.  Eventually, this may become useful for seeing named
   * jobs in ops director.
   *
   * @return this instance for method chaining
   */
  @Override
  WriteBatcher withJobName(String jobName);

  /**
   * Sets the unique id of the job to help with managing multiple concurrent jobs and
   * start the job with the specified job id.
   *
   * @param jobId the unique id you would like to assign to this job
   * @return this instance (for method chaining)
   */
  WriteBatcher withJobId(String jobId);

  /**
   * Sets the number of documents to send per batch. Since documents are large
   * relative to uris, this number should be much lower than the batch size for
   * QueryBatcher. The default batch size is 100.
   *
   * @return this instance for method chaining
   */
  @Override
  WriteBatcher withBatchSize(int batchSize);

  /**
   * Sets the number of threads added to the internal thread pool for this
   * instance to use for writing or reporting on batches of uris.  Each time
   * enough documents are added to fill a batch, a batch is created and a task
   * is queued to write the batch.  As a thread becomes available it grabs a
   * task from the queue and performs the task (usually writing the batch to
   * the server then reporting on the batch to listeners registered with
   * onBatchSuccess and onBatchFailure).  By default the number of threads is
   * the number of hosts containing applicable forests.  More threads should
   * accommodate more throughput.
   *
   * @return this instance for method chaining
   */
  @Override
  WriteBatcher withThreadCount(int threadCount);

  /** Create a batch from any unbatched documents and write that batch
   * asynchronously.
   */
  void flushAsync();

  /** Create a batch from any unbatched documents and write that batch, then
   * wait for all batches to complete (the same as awaitCompletion().
   */
  void flushAndWait();

  /**
   * Blocks until the job has finished or cancelled all queued tasks.
   *
   * @return true if the queue completed without InterruptedException, false if
   *         we hit the time limit or InterruptedException was thrown while waiting
   */
  boolean awaitCompletion();

  /**
   * Blocks until the job has finished or cancelled all queued tasks.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout argument
   *
   * @return true if the queue completed without timing out, false if we hit the time limit
   * @throws InterruptedException if interrupted while waiting
   */
  boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * After the job has been started, returns the JobTicket generated when the
   * job was started.
   *
   * @return the JobTicket generated when this job was started
   *
   * @throws IllegalStateException if this job has not yet been started
   */
  JobTicket getJobTicket();
}
