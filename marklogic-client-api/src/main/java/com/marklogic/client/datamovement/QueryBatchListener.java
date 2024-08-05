/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * Runs processEvent on each batch as it is ready during a QueryBatcher
 * job.
 *
 * @see #processEvent processEvent
 */
public interface QueryBatchListener extends BatchListener<QueryBatch> {
  /**
   * <p>The method called by QueryBatcher or WriteBatcher to run your
   * custom code on this batch.  You usually implement this as a lambda expression.</p>
   *
   * For example, see the lambda expression passed to onUrisReady:
   *
   * <pre>{@code
   *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
   *         .withBatchSize(1000, 20)
   *         .onUrisReady(batch -> {
   *             for ( String uri : batch.getItems() ) {
   *                 if ( uri.endsWith(".txt") ) {
   *                     batch.getClient().newDocumentManager().delete(uri);
   *                 }
   *             }
   *         })
   *         .onQueryFailure(queryBatchException -> queryBatchException.printStackTrace());
   *     JobTicket ticket = dataMovementManager.startJob(qhb);
   *     qhb.awaitCompletion();
   *     dataMovementManager.stopJob(ticket);
   *}</pre>
   *
   * @param batch the batch of uris and some metadata about the current status of the job
   */
  void processEvent(QueryBatch batch);

  /**
   * This default method should be implemented by custom listeners that should
   * be retried in case of failover.
   *
   * @param queryBatcher the QueryBatcher which will call this Listener
   */
  default void initializeListener(QueryBatcher queryBatcher) {}
}
