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

import com.marklogic.client.datamovement.impl.QueryBatchImpl;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Modifies documents in-place in the database by applying a {@link
 * com.marklogic.client.document.ServerTransform server-side transform}.
 * If the transform modifies documents to no longer match the query,
 * ApplyTransformListener should only be used when:</p>
 *
 * <ol>
 *   <li><a href="https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468">merge timestamp</a>
 *     is enabled and {@link QueryBatcher#withConsistentSnapshot} is called, or</li>
 *   <li>{@link DataMovementManager#newQueryBatcher(Iterator)
 *     newQueryBatcher(Iterator&lt;String&gt;)} is used to traverse a static data set</li>
 * </ol>
 *
 * <br>For example, given the REST transform myTransform.sjs:
 *
 * <pre>{@code
 *    function transform_function(context, params, content) {
 *      var document = content.toObject();
 *      document.someProperty = params.newValue;
 *      return document;
 *    };
 *    exports.transform = transform_function;
 *}</pre>
 *
 * installed in the server like so (using the MarkLogic Java Client API):
 *
 * <pre>{@code
 *    restAdminClient.newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
 *      "myTransform", new FileHandle(new File("myTransform.sjs")));
 *}</pre>
 *
 * you can run the transform on documents matching a query like so:
 *
 * <pre>{@code
 *    ServerTransform transform = new ServerTransform(transformName2)
 *      .addParameter("newValue", "some new value");
 *    ApplyTransformListener listener = new ApplyTransformListener()
 *      .withTransform(transform)
 *      .withApplyResult(ApplyResult.REPLACE);
 *    QueryBatcher batcher = moveMgr.newQueryBatcher(query)
 *      .onUrisReady(listener);
 *    JobTicket ticket = moveMgr.startJob( batcher );
 *    batcher.awaitCompletion();
 *    moveMgr.stopJob(ticket);
 *}</pre>
 *
 * <p>As with all the provided listeners, this listener will not meet the needs
 * of all applications but the
 * <a target="_blank" href="https://github.com/marklogic/java-client-api/blob/master/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/ApplyTransformListener.java">source code</a>
 * for it should serve as helpful sample code so you can write your own custom
 * listeners.</p>
 *
 * <p>In this listener, we initialize only the HostAvailabilityListener's
 * RetryListener and not NoResponseListener's RetryListener because if we get
 * empty responses when we try to apply a transform to the batch of URIs
 * retrieved from the server, we are not sure what happened in the server - if
 * the transform has been applied or it has not been applied. Retrying in those
 * scenarios would apply the transform twice if the transform has been already
 * applied and this is not desirable.</p>
 *
 * <p>In order to handle such scenarios where we get an empty response, it is
 * recommended to add a BatchFailureListener which would take care of apply
 * transform failures and retry only for those URIs for which the apply
 * transform has failed. If the transform is idempotent, we can just initialize
 * the RetryListener of the NoResponseListener by calling
 * NoResponseListener.initializeRetryListener(this) and add it to the
 * BatchFailureListeners similar to what we have in the other listeners.</p>
 */
public class ApplyTransformListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(ApplyTransformListener.class);
  private ServerTransform transform;
  private ApplyResult applyResult = ApplyResult.REPLACE;
  private List<QueryBatchListener> successListeners = new ArrayList<>();
  private List<QueryBatchListener> skippedListeners = new ArrayList<>();
  private List<BatchFailureListener<QueryBatch>> queryBatchFailureListeners = new ArrayList<>();

  public ApplyTransformListener() {
    logger.debug("new ApplyTransformListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  /**
   * This implementation of initializeListener adds this instance of
   * ApplyTransformListener to the two RetryListener's in this QueryBatcher so
   * they will retry any batches that fail during the apply-transform request.
   */
  @Override
  public void initializeListener(QueryBatcher queryBatcher) {
    HostAvailabilityListener hostAvailabilityListener = HostAvailabilityListener.getInstance(queryBatcher);
    if ( hostAvailabilityListener != null ) {
      BatchFailureListener<QueryBatch> retryListener = hostAvailabilityListener.initializeRetryListener(this);
      if( retryListener != null )  onFailure(retryListener);
    }
  }

  /**
   * The standard BatchListener action called by QueryBatcher.
   */
  public void processEvent(QueryBatch batch) {
    if ( ! (batch.getClient() instanceof DatabaseClientImpl) ) {
      throw new IllegalStateException("DatabaseClient must be instanceof DatabaseClientImpl");
    }
    StringHandle uris = new StringHandle(String.join("\n", batch.getItems()))
      .withMimetype("text/uri-list");
    RESTServices services = ((DatabaseClientImpl) batch.getClient()).getServices();
    try {
      RequestParameters params = new RequestParameters();
      if ( transform != null ) transform.merge(params);
      params.add("result", applyResult.toString().toLowerCase());
      List<String> responseUris = new BufferedReader(
        services.postResource(null, "internal/apply-transform", null, params, uris, new ReaderHandle()).get()
      ).lines().collect(Collectors.toList());
      QueryBatchImpl processedBatch = new QueryBatchImpl()
        .withClient( batch.getClient() )
        .withItems( responseUris.toArray(new String[responseUris.size()]) )
        .withTimestamp( batch.getTimestamp() )
        .withJobBatchNumber( batch.getJobBatchNumber() )
        .withJobResultsSoFar( batch.getJobResultsSoFar() )
        .withForestBatchNumber( batch.getForestBatchNumber() )
        .withForestResultsSoFar( batch.getForestResultsSoFar() )
        .withForest( batch.getForest() )
        .withServerTimestamp( batch.getServerTimestamp() )
        .withJobTicket( batch.getJobTicket() );
      for ( QueryBatchListener listener : successListeners ) {
        try {
          listener.processEvent(processedBatch);
        } catch (Throwable t) {
          logger.error("Exception thrown by an onSuccess listener", t);
        }
      }

      List<String> skippedRequestUris = new ArrayList<>(Arrays.asList(batch.getItems()));
      skippedRequestUris.removeAll( responseUris );
      if ( skippedRequestUris.size() > 0 ) {
        QueryBatchImpl skippedBatch = processedBatch
          .withItems( skippedRequestUris.toArray(new String[0]) );
        for ( QueryBatchListener listener : skippedListeners ) {
          try {
            listener.processEvent(skippedBatch);
          } catch (Throwable t) {
            logger.error("Exception thrown by an onSkipped listener", t);
          }
        }
      }
    } catch (Throwable t) {
      for ( BatchFailureListener<QueryBatch> queryBatchFailureListener : queryBatchFailureListeners ) {
        try {
          queryBatchFailureListener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onFailure listener", t2);
        }
      }
      logger.warn("Error: {} in batch with urs ({})", t.toString(),
        Arrays.asList(batch.getItems()));
    }
  }

  /**
   * When a batch has been successfully transformed, run this listener code.
   * Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a batch is successfully transformed
   *
   * @return this instance for method chaining
   */
  public ApplyTransformListener onSuccess(QueryBatchListener listener) {
    successListeners.add(listener);
    return this;
  }

  /**
   * When documents were not found and therefore not transformed, run this
   * listener code.  Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when documents were not transformed
   *
   * @return this instance for method chaining
   */
  public ApplyTransformListener onSkipped(QueryBatchListener listener) {
    skippedListeners.add(listener);
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
  public ApplyTransformListener onFailure(BatchFailureListener<QueryBatch> listener) {
    queryBatchFailureListeners.add(listener);
    return this;
  }

  /**
   * The ServerTransform to run on each document from each batch.
   *
   * @param transform the ServerTransform to run on each document from each batch
   *
   * @return this instance for method chaining
   */
  public ApplyTransformListener withTransform(ServerTransform transform) {
    this.transform = transform;
    return this;
  }

  /**
   * Whether to {@link ApplyResult#REPLACE REPLACE} each document with the result of the transform, or run
   * the transform with each document as input, but {@link ApplyResult#IGNORE IGNORE} the result.
   *
   * @param applyResult the behavior required after each transform is run
   *
   * @return this instance for method chaining
   */
  public ApplyTransformListener withApplyResult(ApplyResult applyResult) {
    this.applyResult = applyResult;
    return this;

  }

  /**
   * Either {@link #REPLACE} each document with the result of the transform, or run
   * the transform with each document as input, but {@link #IGNORE} the result.
   */
  public enum ApplyResult {
    /** (Default) Overwrites documents with the value returned by the transform, just
     * like REST write transforms. This is the default behavior.
     */
    REPLACE,

    /** Run the transform on each document, but ignore the value returned by
     * the transform because the transform will do any necessary database
     * modifications or other processing.  For example, a transform might call
     * out to an external REST service or perhaps write multiple additional
     * documents.
     */
    IGNORE
  };
}
