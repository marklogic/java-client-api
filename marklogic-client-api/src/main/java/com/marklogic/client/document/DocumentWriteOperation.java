/*
 * Copyright 2012-2019 MarkLogic Corporation
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

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import com.marklogic.client.MarkLogicInternalException;
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
    @Deprecated
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
   * @return the operation type which was set implicitly
   */
  OperationType getOperationType();

  // The uri for this document, whether set explicitly or received from the
  // server after a write with a DocumentDescriptor.
  /** The uri for this document if set explicitly by your call to
   * {@link DocumentWriteSet#add(String, AbstractWriteHandle) add(String, ...)}
   * @return the uri
   */
  String getUri();

  /** The handle with the metadata as set by your call to
   * {@link DocumentWriteSet#add(String, DocumentMetadataWriteHandle, AbstractWriteHandle) add} or
   * {@link DocumentWriteSet#add(DocumentDescriptor, DocumentMetadataWriteHandle, AbstractWriteHandle) add}.
   * @return the handle with the metadata
   */
  DocumentMetadataWriteHandle getMetadata();

  /** The handle with the content as set by your call to
   * {@link DocumentWriteSet#add(String, AbstractWriteHandle) add} or
   * {@link DocumentWriteSet#add(DocumentDescriptor, AbstractWriteHandle) add}.
   * @return the handle with the content
   */
  AbstractWriteHandle getContent();

  /**
   * The logical temporal document URI of the document as set by your call to
   * one of the 'add' methods which adds a document to a {@link DocumentWriteSet}
   * @return the logical temporal document URI
   */
  String getTemporalDocumentURI();
  
  /**
   * The from method prepares each content object for writing as a document including generating a URI by inserting a UUID.
   * @param content a subclass of AbstractWriteHandle
   * @param uriMaker DocumentUriMaker which internally accepts an AbstractWriteHandle and returns a String
   * @return a stream of DocumentWriteOperation to be written in the database.
   */
    public static Stream<DocumentWriteOperation> from(Stream<? extends AbstractWriteHandle> content,
            final DocumentUriMaker uriMaker) {
        if(content == null || uriMaker == null)
            throw new IllegalArgumentException("Content and/or Uri maker cannot be null");
        
        final class DocumentWriteOperationImpl implements DocumentWriteOperation {
            
            private AbstractWriteHandle content;
            private String uri;
            
            public DocumentWriteOperationImpl(AbstractWriteHandle content, String uri) {
                this.content = content;
                this.uri = uri;
            }


            @Override
            public OperationType getOperationType() {
                return null;
            }

            @Override
            public String getUri() {
                return uri;
            }

            @Override
            public DocumentMetadataWriteHandle getMetadata() {
                return null;
            }

            @Override
            public AbstractWriteHandle getContent() {
                return content;
            }

            @Override
            public String getTemporalDocumentURI() {
                return null;
            }

        }
        final class WrapperImpl {
            private DocumentUriMaker docUriMaker;
            WrapperImpl(DocumentUriMaker uriMaker){
                this.docUriMaker = uriMaker;
            }
            DocumentWriteOperation mapper(AbstractWriteHandle content) {
                String uri = docUriMaker.apply(content);
                if (uri == null)
                    throw new MarkLogicInternalException("Uri could not be created");
                return new DocumentWriteOperationImpl(content, uri);
            }

        }
        WrapperImpl wrapperImpl = new WrapperImpl(uriMaker);
        return content.map(wrapperImpl::mapper);

    }

    /**
     * The uriMaker method creates a uri for each document written in the database
     * @param format refers to the pattern passed.
     * @return DocumentUriMaker which contains the formatted uri for the new document.
     */
    public static DocumentUriMaker uriMaker(String format) throws IllegalArgumentException{

        if(format == null || format.length() == 0)
            throw new IllegalArgumentException("Format cannot be null or empty");
        
        final class FormatUriMaker {
            private String uriFormat;

            FormatUriMaker(String format) {
                this.uriFormat = format;
            }

            String makeUri(AbstractWriteHandle content) {
                return String.format(uriFormat, UUID.randomUUID());
            }
        }
        FormatUriMaker formatUriMaker = new FormatUriMaker(format);

        return formatUriMaker::makeUri;
    }

    @FunctionalInterface
    public interface DocumentUriMaker extends Function<AbstractWriteHandle, String> {
    }
}
