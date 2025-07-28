/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

/** Each DocumentRecord represents one document, its uri, format, mime-type
 *  and possibly its metadata (collections, properties, quality, and permissions).
 *  Whether metadata is included depends on whether it was requested in the call
 *  sent to the server. For example, to request collections metadata:
 *
 *     JSONDocumentManager docMgr = databaseClient.newJSONDocumentManager();
 *     docMgr.setNonDocumentFormat(Format.XML);
 *     docMgr.setMetadataCategories(Metadata.COLLECTIONS);
 *     // make sure to use a try-with-resources block or call documents.close()
 *     try ( DocumentPage documents = docMgr.read("doc1.json", "doc2.json") ) {
 *       for ( DocumentRecord record : documents ) {
 *         String uri = record.getUri();
 *         JacksonHandle content = record.getContent(new JacksonHandle());
 *         DocumentMetadataHandle metadata = record.getMetadata(new DocumentMetadataHandle());
 *         DocumentCollections collections = metadata.getCollections();
 *         // ... do something ...
 *       }
 *     }
 */
public interface DocumentRecord {
  /** Returns the uri (unique identifier) of the document in the server
   * @return the uri
   */
  String getUri();

  /**
   * Returns a descriptor for the document content including the document version
   * if enabled on the server.
   * @return the descriptor
   */
  DocumentDescriptor getDescriptor();

  /** Returns the format of the document in the server
   * @return the format of the document in the server
   */
  Format getFormat();

  /** Returns the mime-type ("Content-Type" header) of the document as specified by
   * the server (uses the server's mime-type mapping for file extensions)
   *
   * @return the mime-type
   */
  String getMimetype();

  /**
   * Returns the length of the document content.
   * @return the content length
   */
  long getLength();

  /**
   * Given a handle, populates the handle with the structured metadata directly from
   * the REST API. Depending on the nonDocumentFormat set on the DocumentManager,
   * this will be XML or JSON format. If the nonDocumentFormat is XML, you can use
   * DocumentMetadataHandle which offers convenient metadata access methods.
   * @see <a href="http://docs.marklogic.com/guide/rest-dev/documents#id_63117">
   *     REST API Dev Guide -&gt; Working with Metadata</a>
   *
   * @param metadataHandle the handle to populate with the metadata
   * @param <T> the type of DocumentMetadataReadHandle to return
   * @return a handle populated with the document metadata
   */
  <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle);
  /**
   * Reads the metadata from the multipart response into the representation
   * specified by the IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param as the Class which a handle should instantiate, populate and return
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return the document metadata as the Class type requested
   */
  <T> T getMetadataAs(Class<T> as);

  /**
   * Given a handle, populates the handle with the document contents directly from
   * the server (or the transformed contents if a ServerTransform was used). Use
   * a handle that is appropriate for the {@link #getFormat format} of this document.
   *
   * @param contentHandle the handle to populate with the contents
   * @param <T> the type of AbstractReadHandle to return
   * @return a handle populated with the document contents
   */
  <T extends AbstractReadHandle> T getContent(T contentHandle);
  /**
   * Reads the document content from the multipart response into the representation
   * specified by the IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param as the Class which a handle should instantiate, populate and return
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return the document contents as the Class type requested
   */
  <T> T getContentAs(Class<T> as);
}
