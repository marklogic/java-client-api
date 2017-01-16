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

/**
 * Sends a Java API bulk {@link com.marklogic.client.document.DocumentManager#delete(String...) delete}
 * request for all the documents from each batch.  Because it deletes
 * documents, it should only be used when:
 *
 * 1. [merge timestamp][] is enabled and
 * {@link QueryBatcher#withConsistentSnapshot} is called, or
 * 2. {@link DataMovementManager#newQueryBatcher(Iterator)
 * newQueryBatcher(Iterator&lt;String&gt;)} is used to traverse a static data set
 *
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 *
 * When merge timestamp is enabled, pass a DeleteListener instance to
 * QueryBatcher onUrisReady like so:
 *
 *     QueryBatcher deleteBatcher = moveMgr.newQueryBatcher(query)
 *       .onUrisReady(new DeleteListener())
 *       .withConsistentSnapshot();
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *
 * With Iterator<String>, pass a DeleteListener instance to
 * QueryBatcher onUrisReady like so:
 *
 *     QueryBatcher deleteBatcher = moveMgr.newQueryBatcher(query)
 *       .onUrisReady(new DeleteListener());
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *
 * As with all the provided listeners, this listener will not meet the needs of
 * all applications but the [source code][] for it should serve as helpful sample
 * code so you can write your own custom listeners.
 *
 * [source code]: https://github.com/marklogic/java-client-api/blob/develop/src/main/java/com/marklogic/client/datamovement/DeleteListener.java
 */
public class DeleteListener implements QueryBatchListener {
  /**
   * The standard BatchListener action called by QueryBatcher.
   */
  @Override
  public void processEvent(QueryBatch batch) {
    batch.getClient().newDocumentManager().delete( batch.getItems() );
  }
}
