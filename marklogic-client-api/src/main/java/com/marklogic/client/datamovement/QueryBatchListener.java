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
