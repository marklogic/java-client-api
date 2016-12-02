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
package com.marklogic.client.datamovement;

import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.datamovement.impl.QueryBatchImpl;

import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modifies documents in-place in the database by applying a {@link
 * com.marklogic.client.document.ServerTransform server-side transform}.
 * If the transform modifies documents to no longer match the query,
 * ApplyTransformListener should only be used when:
 *
 * 1. [merge timestamp][] is enabled and
 * {@link QueryBatcher#withConsistentSnapshot} is called, or
 * 2. {@link DataMovementManager#newQueryBatcher(Iterator)
 * newQueryBatcher(Iterator&lt;String&gt;)} is used to traverse a static data set
 *
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 *
 * For example, given the REST transform myTransform.sjs:
 *
 *     function transform_function(context, params, content) {
 *       var document = content.toObject();
 *       document.someProperty = params.newValue;
 *       return document;
 *     };
 *     exports.transform = transform_function;
 *
 * installed in the server like so (using the MarkLogic Java Client API):
 *
 *     restAdminClient.newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
 *       "myTransform", new FileHandle(new File("myTransform.sjs")));
 *
 * you can run the transform on documents matching a query like so:
 *
 *     ServerTransform transform = new ServerTransform(transformName2)
 *         .addParameter("newValue", "some new value");
 *     ApplyTransformListener listener = new ApplyTransformListener()
 *       .withTransform(transform)
 *       .withApplyResult(ApplyResult.REPLACE);
 *     QueryBatcher batcher = moveMgr.newQueryBatcher(query)
 *         .onUrisReady(listener);
 *     JobTicket ticket = moveMgr.startJob( batcher );
 *     batcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 */
public class ApplyTransformListener implements QueryBatchListener {
  private ServerTransform transform;
  private ApplyResult applyResult = ApplyResult.REPLACE;
  private List<QueryBatchListener> successListeners = new ArrayList<>();
  private List<QueryBatchListener> skippedListeners = new ArrayList<>();
  private List<BatchFailureListener<Batch<String>>> failureListeners = new ArrayList<>();

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
        listener.processEvent(processedBatch);
      }

      List<String> skippedRequestUris = new ArrayList<>(Arrays.asList(batch.getItems()));
      skippedRequestUris.removeAll( responseUris );
      if ( skippedRequestUris.size() > 0 ) {
        QueryBatchImpl skippedBatch = processedBatch
          .withItems( skippedRequestUris.toArray(new String[0]) );
        for ( QueryBatchListener listener : skippedListeners ) {
          listener.processEvent(skippedBatch);
        }
      }
    } catch (Throwable t) {
      for ( BatchFailureListener<Batch<String>> listener : failureListeners ) {
        listener.processFailure(batch, t);
      }
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
  public ApplyTransformListener onBatchFailure(BatchFailureListener<Batch<String>> listener) {
    failureListeners.add(listener);
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
