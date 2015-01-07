/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.util.Set;

/**
 * Builds a set of {@link DocumentWriteOperation DocumentWriteOperations} to be sent
 * to the server through the REST API as a bulk write request.
 * @see DocumentManager#write(DocumentWriteSet)
 * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_54649">
 *     REST API Guide -&gt; Writing Multiple Documents</a>
 */
public interface DocumentWriteSet extends Set<DocumentWriteOperation> {
    /** Sets the default metadata for this write set for all documents added after this call */
    public DocumentWriteSet addDefault(DocumentMetadataWriteHandle metadataHandle);

    /** Removes the default metadata for this write set for all documents added after this call */
    public DocumentWriteSet disableDefault();

    /**
     * Adds to this write set a document with the given docId (server uri)
     * and contents provided by the handle
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle);

    /**
     * Adds to this write set a document with the given docId (server uri),
     * metadata, and contents provided by the handle
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle);

    /**
     * Adds to this write set a document with the given uri template, and
     * contents provided by the handle
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle);

    /**
     * Adds to this write set a document with the given uri template, metadata,
     * and contents provided by the handle
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle);
}

