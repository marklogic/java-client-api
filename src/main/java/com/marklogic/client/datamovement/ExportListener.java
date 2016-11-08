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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import java.util.List;
import java.util.Set;

/**
 * Reads document contents and metadata for each batch, then sends each
 * document to any listeners registered with {@link #onDocumentReady
 * onDocumentReady} for further processing or writing to any target supported
 * by Java.  Supports reading partial documents via transforms.  Supports
 * exporting all documents at a consistent point-in-time using
 * withConsistentSnapshot.
 *
 * For example:
 *
 *     QueryBatcher exportBatcher = moveMgr.newQueryBatcher(query)
 *         .withConsistentSnapshot()
 *         .onUrisReady(
 *           new ExportListener()
 *               .withConsistentSnapshot()
 *               .onDocumentReady(doc -&gt; {
 *                 logger.debug("Contents=[{}]", doc.getContentAs(String.class));
 *               })
 *         )
 *         .onQueryFailure(exception -&gt; exception.printStackTrace());
 *
 *     JobTicket ticket = moveMgr.startJob(exportBatcher);
 *     exportBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 */
public class ExportListener implements QueryBatchListener {
  private ServerTransform transform;
  private QueryManager.QueryView view;
  private Set<DocumentManager.Metadata> categories = new HashSet<>();
  private Format nonDocumentFormat;
  private List<Consumer<DocumentRecord>> exportListeners = new ArrayList<>();
  private boolean consistentSnapshot = false;

  public ExportListener() {
  }

  protected DocumentPage getDocs(QueryBatch batch) {
    GenericDocumentManager docMgr = batch.getClient().newDocumentManager();
    if ( view              != null ) docMgr.setSearchView(view);
    if ( categories        != null ) docMgr.setMetadataCategories(categories);
    if ( nonDocumentFormat != null ) docMgr.setNonDocumentFormat(nonDocumentFormat);
    if ( consistentSnapshot == true ) {
      return docMgr.read( batch.getServerTimestamp(), transform, batch.getItems() );
    } else {
      return docMgr.read( transform, batch.getItems() );
    }
  }

  /**
   * This is the method QueryBatcher calls for ExportListener to do its
   * thing.  You should not need to call it.
   *
   * @param batch the batch of uris and some metadata about the current status of the job
   */
  @Override
  public void processEvent(QueryBatch batch) {
    DocumentPage docs = getDocs(batch);
    while ( docs.hasNext() ) {
      for ( Consumer<DocumentRecord> listener : exportListeners ) {
        listener.accept(docs.next());
      }
    }
  }

  /**
   * Specifies that documents should be retrieved as they were when this QueryBatcher job started.
   * This enables a point-in-time export so that all documents are as they were at that point in time.
   * This requires that the server be configured to allow such queries by setting the
   * [merge timestamp](https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468) to a timestamp
   * before the job starts or a sufficiently large negative value.  This should
   * only be used when the QueryBatcher is constructed with a {@link
   * DataMovementManager#newQueryBatcher(StructuredQueryDefinition) query}, not with
   * an {@link DataMovementManager#newQueryBatcher(Iterator) Iterator}.
   *
   * @return this instance for method chaining
   *
   * @see QueryBatcher#withConsistentSnapshot
   */
  public ExportListener withConsistentSnapshot() {
    consistentSnapshot = true;
    return this;
  }

  /**
   * Adds a metadata category to retrieve and make available from {@link
   * DocumentRecord#getMetadata DocumentRecord.getMetadata} to listeners
   * registered with onDocumentReady.  To specify the format for the metdata,
   * call {@link #withNonDocumentFormat withNonDocumentFormat}.
   *
   * @param category the metadata category to retrieve
   * @return this instance for method chaining
   *
   * @see DocumentManager#setMetadataCategories DocumentManager.setMetadataCategories
   */
  public ExportListener withMetadataCategory(DocumentManager.Metadata category) {
    this.categories.add(category);
    return this;
  }

  /**
   * Sets the format for metadata available from {@link DocumentRecord#getMetadata
   * DocumentRecord.getMetadata} to listeners registered with
   * onDocumentReady (assuming {@link #withMetadataCategory
   * withMetadataCategory} has been called to request specific metdata).  If
   * setNonDocumentFormat is not called, the server default format will be
   * used.
   *
   * @param nonDocumentFormat the format in which retrieve the metadata
   * @return this instance for method chaining
   *
   * @see DocumentManager#setNonDocumentFormat(Format) DocumentManager.setNonDocumentFormat
   */
  public ExportListener withNonDocumentFormat(Format nonDocumentFormat) {
    this.nonDocumentFormat = nonDocumentFormat;
    return this;
  }

  /* TODO: test to see if QueryView is really necessary
  public ExportListener withSearchView(QueryManager.QueryView view) {
    this.view = view;
    return this;
  }
  */

  /**
   * Sets the server tranform to modify the document contents.
   *
   * @param transform the name of the transform already installed in the REST server
   * @return this instance for method chaining
   */
  public ExportListener withTransform(ServerTransform transform) {
    this.transform = transform;
    return this;
  }

  /**
   * Adds a listener to process each retrieved document, which is the way users
   * of ExportListener can provide custom code to export documents.  This
   * custom code could write the document or a portion of the document to the
   * file system, a REST service, or any target supported by Java.  If further
   * information is required about the document beyond what DocumentRecord can
   * provide, register a listener with {@link QueryBatcher#onUrisReady
   * QueryBatcher.onUrisReady} instead.
   *
   * @param listener the code which will process each document
   * @return this instance for method chaining
   *
   * @see Consumer
   * @see DocumentRecord
   */
  public ExportListener onDocumentReady(Consumer<DocumentRecord> listener) {
    exportListeners.add(listener);
    return this;
  }
}
