/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.DatabaseClient;

/**
 * <p>To facilitate long-running read, update, and delete use cases, coordinates
 * threads to process batches of uris matching a query or coming
 * from an Iterator.  Each batch of uris matching a query will come from a
 * single forest.  The host for that forest is the target of the DatabaseClient
 * provided to the listener's processEvent method.  The query is performed
 * directly on each forest associated with the database for the DatabaseClient
 * provided to DataMovementManager.  The end goal of each job is determined by
 * the listeners registered with onUrisReady.  The data set from which batches
 * are made and on which processing is performed is determined by the
 * {@link DataMovementManager#newQueryBatcher(StructuredQueryDefinition) query} or
 * {@link DataMovementManager#newQueryBatcher(Iterator) Iterator} used to
 * construct this instance.</p>
 *
 * While the most custom use cases will be addressed by custom listeners, the
 * common use cases are addressed by provided listeners, including
 * {@link com.marklogic.client.datamovement.ApplyTransformListener},
 * {@link com.marklogic.client.datamovement.DeleteListener},
 * {@link com.marklogic.client.datamovement.ExportListener}, and
 * {@link com.marklogic.client.datamovement.ExportToWriterListener}.  The provided
 * listeners are used by adding an instance via onUrisReady like so:
 *
 * <pre>{@code
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withConsistentSnapshot()
 *         .onUrisReady( new DeleteListener() )
 *         .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 * <p>Custom listeners will generally use the [MarkLogic Java Client API][] to
 * manipulate the documents for the uris in each batch.</p>
 *
 * <p>QueryBatcher is designed to be highly scalable and performant.  To
 * accommodate the largest result sets, QueryBatcher paginates through
 * matches rather than loading matches into memory.  To prevent queueing too
 * many tasks when running a query, QueryBatcher only adds another task
 * when one completes the query and is about to send the matching uris to the
 * onUrisReady listeners.</p>
 *
 * <p>For pagination to succeed, you must not modify the result set during
 * pagination. This means you must</p>
 *
 * <ol>
 *   <li>perform a read-only operation, or
 *   <li>make sure modifications do not modify the result set by deleting matches
 *    or modifying them to no longer match, or
 *   <li>set a
 *     <a href="https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468">merge timestamp</a>
 *     and use {@link #withConsistentSnapshot}, or
 *   <li>use {@link DataMovementManager#newQueryBatcher(Iterator) Iterator}
 *    instead of a {@link
 *    DataMovementManager#newQueryBatcher(StructuredQueryDefinition) query}.
 * </ol>
 *
 * <p>Sample usage using withConsistentSnapshot():</p>
 *
 * <pre>{@code
 *     QueryDefinition query = new StructuredQueryBuilder().collection("myCollection");
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withBatchSize(1000)
 *         .withThreadCount(20)
 *         .withConsistentSnapshot()
 *         .onUrisReady(batch -> {
 *             for ( String uri : batch.getItems() ) {
 *                 if ( uri.endsWith(".txt") ) {
 *                     client.newDocumentManager().delete(uri);
 *                 }
 *             }
 *         })
 *         .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 * Example of queueing uris in memory instead of using withConsistentSnapshot():
 *
 * <pre>{@code
 *     ArrayList<String> uris = Collections.synchronizedList(new ArrayList<>());
 *     QueryBatcher getUris = dataMovementManager.newQueryBatcher(query)
 *       .withBatchSize(5000)
 *       .onUrisReady( batch -> uris.addAll(Arrays.asList(batch.getItems())) )
 *       .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket getUrisTicket = dataMovementManager.startJob(getUris);
 *     getUris.awaitCompletion();
 *     dataMovementManager.stopJob(getUrisTicket);
 *
 *     // now we have the uris, let's step through them
 *     QueryBatcher performDelete = moveMgr.newQueryBatcher(uris.iterator())
 *       .onUrisReady(new DeleteListener())
 *       .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(performDelete);
 *     performDelete.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 * To queue uris to disk (if not enough memory is available) see {@link UrisToWriterListener}.
 */
public interface QueryBatcher extends Batcher {
  /**
   * Add a listener to run each time a batch of uris is ready.
   * @param listener the action which has to be done when uris are ready
   * @return this instance for method chaining
   */
  QueryBatcher onUrisReady(QueryBatchListener listener);

  /**
   * <p>Add a listener to run each time there is an exception retrieving a batch
   * of uris.</p>
   *
   * <p>These listeners will not run when an exception is thrown by a listener
   * registered with onUrisReady.  To learn more, please see
   * <a href="package-summary.html#errs">Handling Exceptions in Listeners</a></p>
   *
   * @param listener the code to run when a failure occurs
   * @return this instance for method chaining
   */
  QueryBatcher onQueryFailure(QueryFailureListener listener);

  /**
   * <p>Add a listener to run when the Query job is completed i.e. when all the
   * document URIs are retrieved and the associated listeners are completed</p>
   *
   * @param listener the code to run when the Query job is completed
   * @return this instance for method chaining
   */
  QueryBatcher onJobCompletion(QueryBatcherListener listener);
  /**
   * Retry in the same thread to query a batch that failed. This method will
   * throw an Exception if it fails again, so it can be wrapped in a try-catch
   * block.
   * @param queryEvent the information about the batch that failed
   */
  public void retry(QueryEvent queryEvent);

  /**
   * Get the array of QueryBatchListener instances registered via
   * onUrisReady.
   *
   * @return the QueryBatchListener instances this batcher
   *   is using
   */
  QueryBatchListener[] getUrisReadyListeners();

  /**
   * Get the array of QueryBatcherListener instances registered via
   * onJobCompletion.
   *
   * @return the QueryBatcherListener instances this batcher is using
   */
  QueryBatcherListener[] getQueryJobCompletionListeners();
  /**
   * Get the array of QueryFailureListener instances
   * registered via onBatchFailure including the HostAvailabilityListener
   * registered by default.
   *
   * @return the QueryFailureListener instances this
   *   batcher is using
   */
  QueryFailureListener[] getQueryFailureListeners();

  /**
   * Remove any existing QueryBatchListener instances registered
   * via onUrisReady and replace them with the provided listeners.
   *
   * @param listeners the QueryBatchListener instances this
   *   batcher should use
   */
  void setUrisReadyListeners(QueryBatchListener... listeners);

  /**
   * Remove any existing QueryFailureListener instances
   * registered via onBatchFailure including the HostAvailabilityListener
   * registered by default and replace them with the provided listeners.
   *
   * @param listeners the QueryFailureListener instances this
   *   batcher should use
   */
  void setQueryFailureListeners(QueryFailureListener... listeners);

  /**
   * Remove any existing QueryBatcherListener instances registered via
   * onJobCompletion and replace them with the provided listeners.
   *
   * @param listeners the QueryBatcherListener instances this batcher should use
   */
  void setQueryJobCompletionListeners(QueryBatcherListener... listeners);
  /**
   * Specifies that matching uris should be retrieved as they were when this
   * QueryBatcher job started.  This enables a point-in-time query so that
   * the set of uri matches is as it was at that point in time.  This requires
   * that the server be configured to allow such queries by setting the [merge
   * timestamp][] to a timestamp before the job starts or a sufficiently large
   * negative value.  This should only be used when the QueryBatcher is
   * constructed with a {@link
   * DataMovementManager#newQueryBatcher(StructuredQueryDefinition) query}, not with
   * an {@link DataMovementManager#newQueryBatcher(Iterator) Iterator}.
   * This is required when performing a delete of documents matching the query
   * or any modification (including ApplyTransformListener) of matching
   * documents which would cause them to no longer match the query (otherwise
   * pagination through the result set would fail because pages shift as
   * documents are deleted or modfied to no longer match the query).
   *
   * @return this instance for method chaining
   */
  QueryBatcher withConsistentSnapshot();

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
  QueryBatcher withForestConfig(ForestConfiguration forestConfig);

  /**
   * Sets the job name.  Eventually, this may become useful for seeing named
   * jobs in ops director.
   *
   * @return this instance for method chaining
   */
  @Override
  public QueryBatcher withJobName(String jobName);

  /**
   * Sets the unique id of the job to help with managing multiple concurrent jobs and
   * start the job with the specified job id.
   *
   * @param jobId the unique id you would like to assign to this job
   * @return this instance (for method chaining)
   */
  QueryBatcher withJobId(String jobId);

  /**
   * Sets the number of documents processed in a batch.
   * @param docBatchSize the number of documents processed in a batch
   * @return this instance for method chaining
   */
  @Override
  public QueryBatcher withBatchSize(int docBatchSize);

  /**
   * Sets the number of documents processed in a batch and the ratio of the document processing batch to
   * the document uri collection batch. For example, if docBatchSize is 100 and docToUriBatchRatio is 5,
   * the document processing batch size is 100 and the document URI collection batch is 500.
   * @param docBatchSize the number of documents processed in a batch
   * @param docToUriBatchRatio the ratio of the document processing batch to the document uri collection batch. The
   *                           docToUriBatchRatio should ordinarily be larger than 1 because URIs are small relative to
   *                           full documents and because collecting URIs from indexes is ordinarily faster than
   *                           processing documents.
   * @return this instance for method chaining
   */
  public QueryBatcher withBatchSize(int docBatchSize, int docToUriBatchRatio);

  /**
   * Returns docToUriBatchRatio set to the QueryBatcher
   * @return docToUriBatchRatio
   */
  public int getDocToUriBatchRatio();

  /**
   * Returns defaultDocBatchSize, which is calculated according to server status
   * @return defaultDocBatchSize
   */
  public int getDefaultDocBatchSize();

  /**
   * Returns maxUriBatchSize, which is calculated according to server status
   * @return maxUriBatchSize
   */
  public int getMaxUriBatchSize();

  /**
   * Returns maxDocToUriBatchRatio, which is calculated according to server status
   * @return maxDocToUriBatchRatio
   */
  public int getMaxDocToUriBatchRatio();

  /**
   * Sets the number of threads added to the internal thread pool for this
   * instance to use for retrieving or processing batches of uris.  For queries
   * these threads both retrieve and process batches.  For queries one batch
   * per forest is queued immediately, then subsequent batches per forest are
   * only queued after each previous batch is retrieved.  This means more
   * threads than the number of forests is likely to be beneficial only when
   * time is spent in the listeners registered with onUrisReady, for example if
   * ApplyTransformListener, DeleteListener, ExportListener, or
   * ExportToWriterListener are used since each of these makes additional
   * requests to the server. For Iterators, the main thread (the one calling
   * {@link DataMovementManager#startJob startJob}) is used to queue all
   * batches--so startJob will not return until all iteration is complete and
   * all batches are queued.  For Iterators this thread count is the number of
   * threads used for processing the queued batches (running processEvent on
   * the listeners regiested with onUrisReady).
   *
   * As of the 6.2.0 release, this can now be adjusted after the batcher has been started. The underlying Java
   * {@code ThreadPoolExecutor} will have both its core and max pool sizes set to the given thread count. Use caution
   * when reducing this to a value of 1 while the batcher is running; in some cases, the underlying
   * {@code ThreadPoolExecutor} may halt execution of any tasks. Execution can be resumed by increasing the thread count
   * to a value of 2 or higher.
   *
   * @return this instance for method chaining
   */
  @Override
  public QueryBatcher withThreadCount(int threadCount);

  /**
   * Blocks until the job is complete.
   *
   * @return true if the job completed without InterruptedException, false if
   *         InterruptedException was thrown while waiting
   */
  boolean awaitCompletion();

  /**
   * Blocks until the job is complete.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout argument
   *
   * @return true if the job completed without timing out, false if we hit the time limit
   * @throws InterruptedException if interrupted while waiting
   */
  boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * true if the job is terminated (last batch was finished or {@link
   * DataMovementManager#stopJob DataMovementManager.stopJob} was called),
   * false otherwise
   *
   * @return true if the job is terminated (last batch was finished or {@link
   * DataMovementManager#stopJob DataMovementManager.stopJob} was called), false otherwise
   */
  boolean isStopped();

  /**
   * After the job has been started, returns the JobTicket generated when the
   * job was started.
   *
   * @return the JobTicket generated when this job was started
   *
   * @throws IllegalStateException if this job has not yet been started
   */
  JobTicket getJobTicket();

  /**
   * Retries processing the listener to the batch of URIs, when the batch has
   * been successfully retrieved from the server but applying the listener
   * on the batch failed.
   *
   * @param batch  the QueryBatch for which we need to process the listener
   * @param queryBatchListener  the QueryBatchListener which needs to be applied
   */
  void retryListener(QueryBatch batch, QueryBatchListener queryBatchListener);

  /**
   * Retry in the same thread to query a batch that failed. If it fails again,
   * all the failure listeners associated with the batcher using onQueryFailure
   * method would be processed.
   *
   * Note : Use this method with caution as there is a possibility of infinite
   * loops. If a batch fails and one of the failure listeners calls this method
   * to retry with failure listeners and if the batch again fails, this would go
   * on as an infinite loop until the batch succeeds.
   *
   * @param queryEvent the information about the batch that failed
   */
  void retryWithFailureListeners(QueryEvent queryEvent);

  /**
   * Sets the limit for the maximum number of batches that can be collected.
   *
   * @param maxBatches is the value of the limit.
   */
  void setMaxBatches(long maxBatches);

  /**
   * Caps the query at the current batch.
   */
  void setMaxBatches();

  /**
   * Returns the maximum number of Batches for the current job.
   *
   * @return the maximum number of Batches that can be collected.
   */
  long getMaxBatches();

  /**
   * If {@code withConsistentSnapshot} was used before starting the job, will return the MarkLogic server timestamp
   * associated with the snapshot. Returns null otherwise.
   *
   * @return the timestamp or null
   */
  Long getServerTimestamp();
}
