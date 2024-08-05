/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.util.Set;

/**
 * Builds a set of {@link DocumentWriteOperation DocumentWriteOperations} to be sent
 * to the server through the REST API as a bulk write request.  Instantiate with {@link
 * DocumentManager#newWriteSet()}.
 * @see DocumentManager#write(DocumentWriteSet)
 * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_54649">
 *     REST API Guide -&gt; Writing Multiple Documents</a>
 */
public interface DocumentWriteSet extends Set<DocumentWriteOperation> {
  /** Sets the default metadata for this write set for all documents added after this call
   * @param metadataHandle the handle containing the metatdata to use as defaults
   * @return this instance (for method chaining)
   */
  DocumentWriteSet addDefault(DocumentMetadataWriteHandle metadataHandle);

  /** Removes the default metadata for this write set for all documents added after this call
   * @return this instance (for method chaining)
   */
  DocumentWriteSet disableDefault();

  /**
   * Adds to this write set a document with the given docId (server uri)
   * and contents provided by the handle
   * @param docId	the URI identifier for the document
   * @param contentHandle	a handle for writing the content of the document
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle);

  /**
   * Adds to this write set a document with the given docId (server uri)
   * and contents.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param docId	the URI identifier for the document
   * @param content	an IO representation of the document content
   * @return this instance (for method chaining)
   */
  DocumentWriteSet addAs(String docId, Object content);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * metadata, and contents provided by the handle
   * @param docId	the URI identifier for the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	a handle for writing the content of the document
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * metadata, and contents.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param docId	the URI identifier for the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param content	an IO representation of the document content
   * @return this instance (for method chaining)
   */
  DocumentWriteSet addAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content);

  /**
   * Adds to this write set a document with the given uri template, and
   * contents provided by the handle
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param contentHandle	a handle for writing the content of the document
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle);

  /**
   * Adds to this write set a document with the given uri template, metadata,
   * and contents provided by the handle
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	a handle for writing the content of the document
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * temporalDocumentURI and contents provided by the handle
   * @param docId the version URI identifier for the document
   * @param contentHandle a handle for writing the content of the document
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle, String temporalDocumentURI);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * temporalDocumentURI and contents.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param docId the version URI identifier for the document
   * @param content   an IO representation of the document content
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet addAs(String docId, Object content, String temporalDocumentURI);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * metadata, temporalDocumentURI and contents provided by the handle
   * @param docId the version URI identifier for the document
   * @param metadataHandle    a handle for writing the metadata of the document
   * @param contentHandle a handle for writing the content of the document
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle, String temporalDocumentURI);

  /**
   * Adds to this write set a document with the given docId (server uri),
   * metadata, temporalDocumentURI and contents.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param docId the version URI identifier for the document
   * @param metadataHandle    a handle for writing the metadata of the document
   * @param content   an IO representation of the document content
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet addAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content, String temporalDocumentURI);

  /**
   * Adds to this write set a document with the given uri template, temporalDocumentURI and
   * contents provided by the handle
   * @param desc  a descriptor for the version URI identifier, format, and mimetype of the document
   * @param contentHandle a handle for writing the content of the document
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle, String temporalDocumentURI);

  /**
   * Adds to this write set a document with the given uri template, metadata, temporalDocumentURI
   * and contents provided by the handle
   * @param desc  a descriptor for the version URI identifier, format, and mimetype of the document
   * @param metadataHandle    a handle for writing the metadata of the document
   * @param contentHandle a handle for writing the content of the document
   * @param temporalDocumentURI the logical temporal document collection URI
   * @return this instance (for method chaining)
   */
  DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle, String temporalDocumentURI);
}

