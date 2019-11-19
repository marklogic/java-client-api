/*
 * Copyright 2015-2019 MarkLogic Corporation
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Sends a Java API bulk {@link com.marklogic.client.document.DocumentManager#delete(String...) delete}
 * request for all the documents from each batch.  Because it deletes
 * documents, it should only be used when:</p>
 *
 * <ol>
 *   <li><a href="https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468">merge timestamp</a>
 *     is enabled and {@link QueryBatcher#withConsistentSnapshot} is called, or</li>
 *   <li>{@link DataMovementManager#newQueryBatcher(Iterator)
 *     newQueryBatcher(Iterator&lt;String&gt;)} is used to traverse a static data set</li>
 * </ol>
 *
 * <br>When merge timestamp is enabled, pass a DeleteListener instance to
 * QueryBatcher onUrisReady like so:
 *
 * <pre>{@code
 *     QueryBatcher deleteBatcher = moveMgr.newQueryBatcher(query)
 *       .onUrisReady(new DeleteListener())
 *       .withConsistentSnapshot();
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *}</pre>
 *
 * With Iterator&lt;String&gt;, pass a DeleteListener instance to
 * QueryBatcher onUrisReady like so:
 *
 * <pre>{@code
 *     QueryBatcher deleteBatcher = moveMgr.newQueryBatcher(query)
 *       .onUrisReady(new DeleteListener());
 *     JobTicket ticket = moveMgr.startJob(deleteBatcher);
 *     deleteBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *}</pre>
 *
 * <p>As with all the provided listeners, this listener will not meet the needs
 * of all applications but the
 * <a target="_blank" href="https://github.com/marklogic/java-client-api/blob/develop/src/main/java/com/marklogic/client/datamovement/DeleteListener.java">source code</a>
 * for it should serve as helpful sample code so you can write your own custom
 * listeners.</p>
*/
public class DeleteListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(DeleteListener.class);
  private List<BatchFailureListener<Batch<String>>> failureListeners = new ArrayList<>();
  private List<BatchFailureListener<QueryBatch>> queryBatchFailureListeners = new ArrayList<>();

  public DeleteListener() {
    logger.debug("new DeleteListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  /**
   * This implementation of initializeListener adds this instance of
   * DeleteListener to the two RetryListener's in this QueryBatcher so they
   * will retry any batches that fail during the delete request.
   */
  @Override
  public void initializeListener(QueryBatcher queryBatcher) {
    HostAvailabilityListener hostAvailabilityListener = HostAvailabilityListener.getInstance(queryBatcher);
    if ( hostAvailabilityListener != null ) {
      BatchFailureListener<QueryBatch> retryListener = hostAvailabilityListener.initializeRetryListener(this);
      if ( retryListener != null )  onFailure(retryListener);
    }
    NoResponseListener noResponseListener = NoResponseListener.getInstance(queryBatcher);
    if ( noResponseListener != null ) {
      BatchFailureListener<QueryBatch> noResponseRetryListener = noResponseListener.initializeRetryListener(this);
      if ( noResponseRetryListener != null )  onFailure(noResponseRetryListener);
    }
  }

  /**
   * The standard BatchListener action called by QueryBatcher.
   */
  @Override
  public void processEvent(QueryBatch batch) {
    try {
      if (batch.getClient() == null) {
        throw new IllegalStateException("null DatabaseClient");
      }
      batch.getClient().newDocumentManager().delete( batch.getItems() );
    } catch (Throwable t) {
      for ( BatchFailureListener<Batch<String>> listener : failureListeners ) {
        try {
          listener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onBatchFailure listener", t2);
        }
      }
      for ( BatchFailureListener<QueryBatch> queryBatchFailureListener : queryBatchFailureListeners ) {
        try {
          queryBatchFailureListener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onFailure listener", t2);
        }
      }
    }
  }

  /**
   * When a batch fails or a callback throws an Exception, run this listener
   * code.  Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a failure occurs
   *
   * @return this instance for method chaining
   * @deprecated  use {@link #onFailure(BatchFailureListener)}
   */
  @Deprecated
  public DeleteListener onBatchFailure(BatchFailureListener<Batch<String>> listener) {
    failureListeners.add(listener);
    return this;
  }

  /**
   * When a batch fails or a callback throws an Exception, run this listener
   * code.  Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a failure occurs
   *
   * @return this instance for method chaining
   */
  public DeleteListener onFailure(BatchFailureListener<QueryBatch> listener) {
    queryBatchFailureListeners.add(listener);
    return this;
  }
}
