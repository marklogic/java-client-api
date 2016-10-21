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

import com.marklogic.client.DatabaseClient;

/**
 * Sends a Java API bulk {@link com.marklogic.client.document.DocumentManager#delete(String...) delete}
 * request for all the documents from each batch.  Because it deletes
 * documents, it should only be used when:
 *
 * 1. [merge timestamp][] is enabled and
 * {@link QueryHostBatcher#withConsistentSnapshot} is called, or
 * 2. {@link DataMovementManager#newQueryHostBatcher(Iterator)
 * newQueryHostBatcher(Iterator&lt;String&gt;)} is used to traverse a static data set
 *
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 *
 * When merge timestamp is enabled, pass a DeleteListener instance to
 * QueryHostBatcher onUrisReady like so:
 *
 *     QueryHostBatcher deleteBatcher = moveMgr.newQueryHostBatcher(query)
 *       .onUrisReady(new DeleteListener())
 *       .withConsistentSnapshot();
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *
 * With Iterator&lt;String&gt;, pass a DeleteListener instance to
 * QueryHostBatcher onUrisReady like so:
 *
 *     QueryHostBatcher deleteBatcher = moveMgr.newQueryHostBatcher(query)
 *       .onUrisReady(new DeleteListener());
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 */
public class DeleteListener implements QueryBatchListener {
  /**
   * The standard BatchListener action called by QueryHostBatcher.
   */
  @Override
  public void processEvent(DatabaseClient client, QueryBatch batch) {
    client.newDocumentManager().delete( batch.getItems() );
  }
}
