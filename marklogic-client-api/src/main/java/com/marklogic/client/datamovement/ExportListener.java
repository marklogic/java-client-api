/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.document.*;
import com.marklogic.client.impl.GenericDocumentImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * <p>Reads document contents (and optionally metadata) for each batch, then sends
 * each document to any listeners registered with {@link #onDocumentReady
 * onDocumentReady} for further processing or writing to any target supported
 * by Java.  Supports reading partial documents via transforms.  Supports
 * exporting all documents at a consistent point-in-time using
 * withConsistentSnapshot.</p>
 *
 * For example:
 *
 * <pre>{@code
 *     QueryBatcher exportBatcher = moveMgr.newQueryBatcher(query)
 *         .withConsistentSnapshot()
 *         .onUrisReady(
 *           new ExportListener()
 *             .withConsistentSnapshot()
 *             .onDocumentReady(doc -> {
 *               logger.debug("Contents=[{}]", doc.getContentAs(String.class));
 *             })
 *         )
 *         .onQueryFailure(exception -> exception.printStackTrace());
 *
 *     JobTicket ticket = moveMgr.startJob(exportBatcher);
 *     exportBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 *}</pre>
 *
 * <p>By default only document contents are retrieved.  If you would also like
 * metadata, make sure to call {@link #withMetadataCategory withMetadataCategory}
 * to configure which categories of metadata you desire.</p>
 *
 * <p>As with all the provided listeners, this listener will not meet the needs
 * of all applications but the
 * <a target="_blank" href="https://github.com/marklogic/java-client-api/blob/master/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/ExportListener.java">source code</a>
 * for it should serve as helpful sample code so you can write your own custom
 * listeners.</p>
 */
public class ExportListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(ExportListener.class);
  private ServerTransform transform;
  private QueryManager.QueryView view;
  private Set<DocumentManager.Metadata> categories = new HashSet<>();
  private Format nonDocumentFormat;
  private List<Consumer<DocumentRecord>> documentListeners = new ArrayList<>();
  private Consumer<DocumentPage> documentPageListener;
  private boolean consistentSnapshot = false;
  private List<BatchFailureListener<QueryBatch>> queryBatchFailureListeners = new ArrayList<>();

  public ExportListener() {
    logger.debug("new ExportListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  protected DocumentPage getDocs(QueryBatch batch) {
    if (batch.getClient() == null) {
      throw new IllegalStateException("null DatabaseClient");
    }
    GenericDocumentManager docMgr = batch.getClient().newDocumentManager();
    if ( view              != null ) docMgr.setSearchView(view);
    if ( categories        != null ) docMgr.setMetadataCategories(categories);
    if ( nonDocumentFormat != null ) docMgr.setNonDocumentFormat(nonDocumentFormat);
    if ( consistentSnapshot == true ) {
      return ((GenericDocumentImpl) docMgr).read( batch.getServerTimestamp(), transform, batch.getItems() );
    } else {
      return docMgr.read( transform, batch.getItems() );
    }
  }

  /**
   * This implementation of initializeListener adds this instance of
   * ExportListener to the two RetryListener's in this QueryBatcher so they
   * will retry any batches that fail during the read request.
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
   * This is the method QueryBatcher calls for ExportListener to do its
   * thing.  You should not need to call it.
   *
   * @param batch the batch of uris and some metadata about the current status of the job
   */
  @Override
  public void processEvent(QueryBatch batch) {
    try ( DocumentPage docs = getDocs(batch) ) {
		if (documentPageListener != null) {
			documentPageListener.accept(docs);
		} else {
			while ( docs.hasNext() ) {
				for ( Consumer<DocumentRecord> listener : documentListeners) {
					try {
						listener.accept(docs.next());
					} catch (Throwable t) {
						logger.error("Exception thrown by an onDocumentReady listener", t);
					}
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
	  if (this.documentPageListener != null) {
		  throw new IllegalStateException("Cannot call onDocumentReady if a listener has already been set via onDocumentPageReady");
	  }
	  documentListeners.add(listener);
	  return this;
  }

	/**
	 * Sets a listener to process a page of retrieved documents. Useful for when documents should be written to an
	 * external system where it's more efficient to make batched writes to that system. Note that {@code close()} does
	 * need to be invoked on the {@code DocumentPage}; this class will handle that.
	 *
	 * @param listener the code which will process each page of documents
	 * @return this instance for method chaining
	 * @see Consumer
	 * @see DocumentPage
	 * @since 6.2.0
	 */
	public ExportListener onDocumentPageReady(Consumer<DocumentPage> listener) {
		if (this.documentListeners != null && !this.documentListeners.isEmpty()) {
			throw new IllegalStateException("Cannot call onDocumentPageReady if a listener has already been added via onDocumentReady");
		}
		this.documentPageListener = listener;
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
  public ExportListener onFailure(BatchFailureListener<QueryBatch> listener) {
    queryBatchFailureListeners.add(listener);
    return this;
  }

  protected List<BatchFailureListener<QueryBatch>> getBatchFailureListeners() {
    return queryBatchFailureListeners;
  }
}
