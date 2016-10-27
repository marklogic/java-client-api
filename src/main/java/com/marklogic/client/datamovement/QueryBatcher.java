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
package com.marklogic.client.datamovement;

import java.util.concurrent.TimeUnit;

/**
 * To facilitate long-running read, update, and delete use cases, coordinates
 * threads to process batches of uris matching a query or coming
 * from an Iterator.  Each batch of uris matching a query will come from a
 * single forest.  The host for that forest is the target of the DatabaseClient
 * provided to the listener's processEvent method.  The query is performed
 * directly on each forest associated with the database for the DatabaseClient
 * provided to DataMovementManager.  The end goal of each job is determined by
 * the listeners registered with onUrisReady.  The data set from which batches
 * are made and on which processing is performed is determined by the
 * {@link DataMovementManager#newQueryBatcher(QueryDefinition) query} or
 * {@link DataMovementManager#newQueryBatcher(Iterator) Iterator} used to
 * construct this instance.
 *
 * While the most custom use cases will be addressed by custom listeners, the
 * common use cases are addressed by provided listeners, including
 * {@link com.marklogic.client.datamovement.ApplyTransformListener},
 * {@link com.marklogic.client.datamovement.DeleteListener},
 * {@link com.marklogic.client.datamovement.ExportListener}, and
 * {@link com.marklogic.client.datamovement.ExportToWriterListener}.  The provided
 * listeners are used by adding an instance via onUrisReady like so:
 *
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withConsistentSnapshot()
 *         .onUrisReady( new DeleteListener() )
 *         .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *
 * Custom listeners will generally use the [MarkLogic Java Client API][] to
 * manipulate the documents for the uris in each batch.
 *
 * QueryBatcher is designed to be highly scalable and performant.  To
 * accommodate the largest result sets, QueryBatcher paginates through
 * matches rather than loading matches into memory.  To prevent queueing too
 * many tasks when running a query, QueryBatcher only adds another task
 * when one completes the query and is about to send the matching uris to the
 * onUrisReady listeners.
 *
 * For pagination to succeed, you must not modify the result set during pagination. This means you must
 *
 * 1. perform a read-only operation, or
 * 2. make sure modifications do not modify the result set by deleting matches or modifying them to no longer match, or
 * 3. set a [merge timestamp][] and use {@link #withConsistentSnapshot}, or
 * 4. use {@link DataMovementManager#newQueryBatcher(Iterator) Iterator} instead of a {@link DataMovementManager#newQueryBatcher(QueryDefinition) query}.
 *
 * [MarkLogic Java Client API]: http://docs.marklogic.com/guide/java
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 *
 * Sample usage using withConsistentSnapshot():
 *
 *     QueryDefinition query = new StructuredQueryBuilder().collection("myCollection");
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withBatchSize(1000)
 *         .withThreadCount(20)
 *         .withConsistentSnapshot()
 *         .onUrisReady((client, batch) -&gt; {
 *             for ( String uri : batch.getItems() ) {
 *                 if ( uri.endsWith(".txt") ) {
 *                     client.newDocumentManager().delete(uri);
 *                 }
 *             }
 *         })
 *         .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *
 * Example of queueing uris in memory instead of using withConsistentSnapshot():
 *
 *     ArrayList&lt;String&gt; uris = new ArrayList&lt;&gt;();
 *     QueryBatcher getUris = dataMovementManager.newQueryBatcher(query)
 *       .withBatchSize(5000)
 *       .onUrisReady( (client, batch) -&gt; uris.addAll(Arrays.asList(batch.getItems())) )
 *       .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket getUrisTicket = dataMovementManager.startJob(getUris);
 *     getUris.awaitCompletion();
 *     dataMovementManager.stopJob(getUrisTicket);
 *
 *     // now we have the uris, let's step through them
 *     QueryBatcher performDelete = moveMgr.newQueryBatcher(uris.iterator())
 *       .onUrisReady(new DeleteListener())
 *       .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(performDelete);
 *     performDelete.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
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
   * Add a listener to run each time there is an Exception retrieving a batch
   * of uris or running a listener registered with onUrisReady.
   * @param listener the action which has to be done when the query fails
   * @return this instance for method chaining
   */
  QueryBatcher onQueryFailure(QueryFailureListener listener);

  /**
   * Retry in the same thread to query a batch that failed. This method will
   * throw an Exception if it fails again, so it can be wrapped in a try-catch
   * block.
   * @param queryEvent the information about the batch that failed
   */
  public void retry(QueryEvent queryEvent);

  /**
   * Get the array of QueryBatchListener instances registered via
   * onBatchSuccess.
   *
   * @return the QueryBatchListener instances this batcher
   *   is using
   */
  QueryBatchListener[] getQuerySuccessListeners();

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
   * via onBatchSuccess and replace them with the provided listeners.
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
   * Specifies that matching uris should be retrieved as they were when this
   * QueryBatcher job started.  This enables a point-in-time query so that
   * the set of uri matches is as it was at that point in time.  This requires
   * that the server be configured to allow such queries by setting the [merge
   * timestamp][] to a timestamp before the job starts or a sufficiently large
   * negative value.  This should only be used when the QueryBatcher is
   * constructed with a {@link
   * DataMovementManager#newQueryBatcher(QueryDefinition) query}, not with
   * an {@link DataMovementManager#newQueryBatcher(Iterator) Iterator}.
   * This is required when performing a delete of documents matching the query
   * or any modification (including ApplyTransformListener) of matching
   * documents which would cause them to no longer match the query (otherwise
   * pagination through the result set would fail because pages shift as
   * documents are deleted or modfied to no longer match the query).
   *
   * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
   *
   * @return this instance for method chaining
   */
  QueryBatcher withConsistentSnapshot();

  /**
   * Sets the job name.  Eventually, this may become useful for seeing named
   * jobs in ops director.
   *
   * @return this instance for method chaining
   */
  @Override
  public QueryBatcher withJobName(String jobName);

  /**
   * Sets the number of uris to retrieve per batch.  Since uris are small
   * relative to full documents, this number should be much higher than the
   * batch size for WriteBatcher.  The default batch size is 1000.
   *
   * @return this instance for method chaining
   */
  @Override
  public QueryBatcher withBatchSize(int batchSize);

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
   * Blocks until the job is has finished stopping.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout argument
   *
   * @return true if the job terminated without timing out, false if we hit the time limit
   * @throws InterruptedException if interrupted while waiting
   */
  boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * true if the job is terminated (last batch was finished or {@link
   * DataMovementManager#stopJob DataMovementManager.stopJob} was called),
   * false otherwise
   *
   * @return true if the job is terminated (last batch was finished or {@link
   * DataMovementManager#stopJob DataMovementManager.stopJob} was called), false otherwise
   */
  boolean isStopped();
}
