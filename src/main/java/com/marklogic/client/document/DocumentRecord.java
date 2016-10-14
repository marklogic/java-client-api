/*
 * Copyright 2012-2016 MarkLogic Corporation
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
package com.marklogic.client.document;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

/** Each DocumentRecord represents one document, its uri, format, mime-type
 *  and possibly its metadata (collections, properties, quality, and permissions).
 *  Whether metadata is included depends on whether it was requested in the call
 *  sent to the server. For example, to request collections metadata:
 *  <pre>{@code
 *JSONDocumentManager docMgr = databaseClient.newJSONDocumentManager();
 *docMgr.setNonDocumentFormat(Format.XML);
 *docMgr.setMetadataCategories(Metadata.COLLECTIONS);
 *DocumentPage documents = docMgr.read("doc1.json", "doc2.json");
 *try {
 *    for ( DocumentRecord record : documents ) {
 *        String uri = record.getUri();
 *        JacksonHandle content = record.getContent(new JacksonHandle());
 *        DocumentMetadataHandle metadata = record.getMetadata(new DocumentMetadataHandle());
 *        DocumentCollections collections = metadata.getCollections();
 *        // ... do something ...
 *    }
 *} finally {
 *    documents.close();
 *}
 *  }</pre>
 */
public interface DocumentRecord {
    /** Returns the uri (unique identifier) of the document in the server
     * @return the uri
     */
    public String getUri();

    /** Returns the format of the document in the server
     * @return the format of the document in the server
     */
    public Format getFormat();

    /** Returns the mime-type ("Content-Type" header) of the document as specified by
     * the server (uses the server's mime-type mapping for file extensions)
     *
     * @return the mime-type
     */
    public String getMimetype();

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
    public <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle);
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
    public <T> T getMetadataAs(Class<T> as);

    /**
     * Given a handle, populates the handle with the document contents directly from
     * the server (or the transformed contents if a ServerTransform was used). Use
     * a handle that is appropriate for the {@link #getFormat format} of this document.
     *
     * @param contentHandle the handle to populate with the contents
     * @param <T> the type of AbstractReadHandle to return
     * @return a handle populated with the document contents
     */
    public <T extends AbstractReadHandle> T getContent(T contentHandle);
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
    public <T> T getContentAs(Class<T> as);
}
