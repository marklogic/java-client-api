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

/** A reflection of the write operations queued by calls to add,
 * {@link DocumentWriteSet#add add}, {@link DocumentWriteSet#addDefault addDefault}, or
 * {@link DocumentWriteSet#disableDefault disableDefault}.
 */
public interface DocumentWriteOperation {
    public enum OperationType {
        /** This write operation (REST API mime part) sets the defaults for the 
         * rest of the request.
         * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_56498">
         *     REST API Guide -&gt; Constructing a Metadata Part</a>
         */
        METADATA_DEFAULT,
        /** This write operation (REST API mime part) clears the defaults for the
         * rest of the request.  While this removes defaults set previously on the
         * request, this does not completely restore server-side defaults.  For
         * more information see the 
         * <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_54554">
         * REST API Guide -&gt; Example: Reverting to System Default Metadata</a>
         */
        DISABLE_METADATA_DEFAULT,
        /** This write operation (REST API mime part) creates or overwrites
         * one document and/or document metadata.
         * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_33756">
         *     REST API Guide -&gt; Constructing a Content Part</a>
         * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_56498">
         *     REST API Guide -&gt; Constructing a Metadata Part</a>
         * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk#id_89876">
         *     REST API Guide -&gt; Understanding When Metadata is Preserved or Replaced</a>
         */
        DOCUMENT_WRITE
    };

    /** Returns the {@link DocumentWriteOperation.OperationType} set implicitly by your call to
     * {@link DocumentWriteSet#add add}, {@link DocumentWriteSet#addDefault addDefault}, or
     * {@link DocumentWriteSet#disableDefault disableDefault}.
     */
    public OperationType getOperationType();

    // The uri for this document, whether set explicitly or received from the
    // server after a write with a DocumentDescriptor.
    /** The uri for this document if set explicitly by your call to
     * {@link DocumentWriteSet#add(String, AbstractWriteHandle) add(String, ...)}
     */
    public String getUri();

    /** The handle with the metadata as set by your call to
     * {@link DocumentWriteSet#add(String, DocumentMetadataWriteHandle, AbstractWriteHandle) add} or
     * {@link DocumentWriteSet#add(DocumentDescriptor, DocumentMetadataWriteHandle, AbstractWriteHandle) add}.
     */
    public DocumentMetadataWriteHandle getMetadata();

    /** The handle with the content as set by your call to
     * {@link DocumentWriteSet#add(String, AbstractWriteHandle) add} or
     * {@link DocumentWriteSet#add(DocumentDescriptor, AbstractWriteHandle) add}.
     */
    public AbstractWriteHandle getContent();
}
