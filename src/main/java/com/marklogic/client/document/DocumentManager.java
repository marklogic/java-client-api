/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.util.Set;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.io.Format;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;

/**
 * A Document Manager provides database operations on a document.
 * 
 * @param <R> the set of handles for reading the document content from the database
 * @param <W> the set of handles for writing the document content to the database
 */
public interface DocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    /**
     * The Metadata enumeration specifies the categories of metadata read from or written to the database.
     */
	public enum Metadata {
        /**
         * Specifies all document metadata categories.
         */
		ALL,
        /**
         * Specifies document collections.
         */
		COLLECTIONS,
        /**
         * Specifies document permissions.
         */
		PERMISSIONS,
        /**
         * Specifies document properties.
         */
		PROPERTIES,
        /**
         * Specifies document search quality.
         */
		QUALITY;
    }

	/**
	 * Creates a document descriptor for identifying the uri of a document,
	 * its format and mimetype, and its version.
	 * 
	 * @param uri	the identifier for the document
     * @return	a descriptor for the document
	 */
    public DocumentDescriptor newDescriptor(String uri);

	/**
	 * Creates a document uri template for assigning a uri to a document.
	 * @param extension	the identifier for the document
	 * @return	a template for the document uri
	 */
    public DocumentUriTemplate newDocumentUriTemplate(String extension);

    /**
     * Creates a builder for specifying changes to the metadata of a document.
     * @param pathFormat	whether the patch specifies metadata with JSON or XML paths
     * @return	the patch builder
     */
    public DocumentMetadataPatchBuilder newPatchBuilder(Format pathFormat);

    /**
     * Checks whether a document exists and gets its format and mimetype
     * 
     * To call exists(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId the URI identifier for the document
     * @return	a descriptor for the document
     */
    public DocumentDescriptor exists(String docId)
    	throws ForbiddenUserException, FailedRequestException;

    /**
     * Checks whether a document exists in an open transaction and gets its length and format
     * 
     * To call exists(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	a descriptor for the document
     */
    public DocumentDescriptor exists(String docId, Transaction transaction)
    	throws ForbiddenUserException, FailedRequestException;

    /**
     * Reads the document content from the database in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId the URI identifier for the document
     * @param as	the IO class for reading the content of the document
     * @return	an object of the IO class with the content of the document in the database
     */
    public <T> T readAs(String docId, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document content from the database in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId the URI identifier for the document
     * @param as	the IO class for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	an object of the IO class with the content of the document in the database
     */
    public <T> T readAs(String docId, Class<T> as, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document metadata and content from the database in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param as	the IO class for reading the content of the document
     * @return	an object of the IO class with the content of the document in the database
     */
    public <T> T readAs(String docId, DocumentMetadataReadHandle metadataHandle, Class<T> as)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document metadata and content from the database in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param as	the IO class for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	an object of the IO class with the content of the document in the database
     */
    public <T> T readAs(String docId, DocumentMetadataReadHandle metadataHandle, Class<T> as, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reads the document content from the database in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document content from the database as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document content from the database in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document content from the database as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reads the document metadata and content from the database in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from the database as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from the database in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from the database as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Reads the document content from an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, T contentHandle, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document content from an open database transaction  as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, T contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document content from an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document content from an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Reads the document metadata and content from an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads the document metadata and content from an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the content handle populated with the content of the document in the database
     */
    public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Reads from the database a list of documents matching the provided uris.  Allows
     * iteration across matching documents and metadata (only if setMetadataCategories 
     * has been called to request metadata).  To find out how many of your uris matched,
     * call the {@link DocumentPage#size() DocumentPage.size()} method.
     *
     * @param uris the database uris identifying documents to retrieve
     * @return the DocumentPage of matching documents and metadata
     */
    public DocumentPage read(String... uris);

    /**
     * Reads from the database a list of documents matching the provided uris.  Allows
     * iteration across matching documents and metadata (only if setMetadataCategories 
     * has been called to request metadata).  To find out how many of your uris matched,
     * call the {@link DocumentPage#size() DocumentPage.size()} method.
     *
     * @param transform the transform to be run on the server on each document (must already be installed)
     * @param uris the database uris identifying documents to retrieve
     * @return the DocumentPage of matching documents and metadata
     */
    public DocumentPage read(ServerTransform transform, String... uris);

    /**
     * Reads from the database a list of documents matching the provided uris.  Allows
     * iteration across matching documents and metadata (only if setMetadataCategories 
     * has been called to request metadata).  To find out how many of your uris matched,
     * call the {@link DocumentPage#size() DocumentPage.size()} method.
     *
     * @param transaction the transaction in which this read is participating
     * @param uris the database uris identifying documents to retrieve
     * @return the DocumentPage of matching documents and metadata
     */
    public DocumentPage read(Transaction transaction, String... uris);

    /**
     * Reads from the database a list of documents matching the provided uris.  Allows
     * iteration across matching documents and metadata (only if setMetadataCategories 
     * has been called to request metadata).  To find out how many of your uris matched,
     * call the {@link DocumentPage#size() DocumentPage.size()} method.
     *
     * @param transform the transform to be run on the server on each document (must already be installed)
     * @param transaction the transaction in which this read is participating
     * @param uris the database uris identifying documents to retrieve
     * @return the DocumentPage of matching documents and metadata
     */
    public DocumentPage read(ServerTransform transform, Transaction transaction, String... uris);

    public DocumentPage readMetadata(String... uris);

    public DocumentPage readMetadata(Transaction transaction, String... uris);

    public DocumentPage search(QueryDefinition querydef, long start);

    public DocumentPage search(QueryDefinition querydef, long start, Transaction transaction);

    public DocumentPage search(QueryDefinition querydef, long start, SearchReadHandle searchHandle);

    public DocumentPage search(QueryDefinition querydef, long start, SearchReadHandle searchHandle, Transaction transaction);

    public long getPageLength();

    public void setPageLength(long length);

    public Format getResponseFormat();

    public void setResponseFormat(Format format);

    public QueryView getSearchView();

    public void setSearchView(QueryView view);

    public DocumentWriteSet newWriteSet();

    public void write(DocumentWriteSet writeSet);

    public void write(DocumentWriteSet writeSet, ServerTransform transform);

    public void write(DocumentWriteSet writeSet, Transaction transaction);

    public void write(DocumentWriteSet writeSet, ServerTransform transform, Transaction transaction);

    /**
     * Writes the document content to the database from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param content	an IO representation of the document content
     */
    public void writeAs(String docId, Object content)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param content	an IO representation of the document content
     * @param transform	a server transform to modify the document content
     */
    public void writeAs(String docId, Object content, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param content	an IO representation of the document content
     */
    public void writeAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param content	an IO representation of the document content
     * @param transform	a server transform to modify the document content
     */
    public void writeAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document content to the database from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public void write(String docId, W contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     */
    public void write(String docId, W contentHandle, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public void write(DocumentDescriptor desc, W contentHandle)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to the database as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     */
    public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document metadata and content to the database from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public void write(String docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document metadata and content to the database as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     */
    public void write(String docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document metadata and content to the database from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     */
    public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document metadata and content to the database as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     */
    public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document content to an open database transaction from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(String docId, W contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to an open database transaction as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(String docId, W contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to an open database transaction from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(DocumentDescriptor desc, W contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document content to an open database transaction as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document metadata and content to an open database transaction from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(String docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Writes the document metadata and content to an open database transaction as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(String docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Writes the document metadata and content to an open database transaction from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Writes the document metadata and content to an open database transaction as transformed on the server.
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Deletes the document metadata and content from the database
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     */
    public void delete(String docId)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes the document metadata and content from an open database transaction
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void delete(String docId, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes the document metadata and content from the database
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     */
    public void delete(DocumentDescriptor desc)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes the document metadata and content from an open database transaction
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void delete(DocumentDescriptor desc, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
	 * Creates a database document with a uri assigned by the server from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
	 * @param template	the template for constructing the document uri
     * @param content	an IO representation of the document content
	 * @return	the database uri that identifies the created document
     */
    public DocumentDescriptor createAs(DocumentUriTemplate template, Object content)
		throws ForbiddenUserException, FailedRequestException;
    /**
	 * Creates a database document with a uri assigned by the server from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
	 * @param template	the template for constructing the document uri
     * @param content	an IO representation of the document content
     * @param transform	a server transform to modify the document content
	 * @return	the database uri that identifies the created document
     */
    public DocumentDescriptor createAs(DocumentUriTemplate template, Object content, ServerTransform transform)
		throws ForbiddenUserException, FailedRequestException;
    /**
	 * Creates a database document with a uri assigned by the server from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param content	an IO representation of the document content
	 * @return	the database uri that identifies the created document
     */
    public DocumentDescriptor createAs(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, Object content)
		throws ForbiddenUserException, FailedRequestException;
    /**
	 * Creates a database document with a uri assigned by the server from an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param content	an IO representation of the document content
     * @param transform	a server transform to modify the document content
	 * @return	the database uri that identifies the created document
     */
    public DocumentDescriptor createAs(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, Object content, ServerTransform transform)
		throws ForbiddenUserException, FailedRequestException;

    /**
	 * Creates a database document with a uri assigned by the server.
	 * @param template	the template for constructing the document uri
     * @param contentHandle	a handle for writing the content of the document
	 * @return	the database uri that identifies the created document
     */
    public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle)
    	throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with a uri assigned by the server as transformed on the server.
	 * @param template	the template for constructing the document uri
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle, ServerTransform transform)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with a uri assigned by the server in an open database transaction.
	 * @param template	the template for constructing the document uri
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with a uri assigned by the server in an open database transaction as transformed on the server.
	 * @param template	the template for constructing the document uri
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, W contentHandle, ServerTransform transform, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with metadata and content and a uri assigned by the server.
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, W contentHandle)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with metadata and content and a uri assigned by the server as transformed on the server.
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException;
	/**
	 * Creates a database document with metadata and content and a uri assigned by the server in an open database transaction as transformed on the server.
	 * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	a handle for writing the content of the document
     * @param transform	a server transform to modify the document content
     * @param transaction	a open transaction under which the document may have been created or deleted
	 * @return	the database uri that identifies the created document
	 */
    public DocumentDescriptor create(DocumentUriTemplate template, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException;

    /**
     * Modifies the metadata or content of a document within an open database transaction on the server.
     * Content can only be modified for JSON or XML documents.  You must use the setMetadataCategories()
     * method to specify whether the patch includes metadata.  An IO representation such as a string, 
     * input stream, or XML DOM specifies the patch as content.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param patch	an IO representation of the patch
     */
    public void patchAs(String docId, Object patch)
		throws ForbiddenUserException, FailedRequestException;

    /**
     * Modifies the metadata or content of a document within an open database transaction on the server.
     * Content can only be modified for JSON or XML documents.  When sending
     * a raw JSON or XML patch, you must use the setMetadataCategories()
     * method to specify whether the patch includes metadata.
     * 
     * To call patch(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param patch	a handle for writing the modification to the document metadata or content
     */
    public void patch(String docId, DocumentPatchHandle patch);
    /**
     * Modifies the metadata or content of a document within an open database transaction on the server.
     * Content can only be modified for JSON or XML documents.  When sending
     * a raw JSON or XML patch, you must use the setMetadataCategories()
     * method to specify whether the patch includes metadata.
     * 
     * To call patch(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param patch	a handle for writing the modification to the document metadata or content
     * @param transaction	a open transaction
     */
    public void patch(String docId, DocumentPatchHandle patch, Transaction transaction);
    /**
     * Modifies the metadata or content of a document.
     * Content can only be modified for JSON or XML documents.  When sending
     * a raw JSON or XML patch, you must use the setMetadataCategories()
     * method to specify whether the patch includes metadata.
     * 
     * To call patch(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param patch	a handle for writing the modification to the document metadata or content
     */
    public void patch(DocumentDescriptor desc, DocumentPatchHandle patch);
    /**
     * Modifies the metadata or content of a document within an open database transaction on the server.
     * Content can only be modified for JSON or XML documents.  When sending
     * a raw JSON or XML patch, you must use the setMetadataCategories()
     * method to specify whether the patch includes metadata.
     * 
     * To call patch(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param patch	a handle for writing the modification to the document metadata or content
     * @param transaction	a open transaction
     */
    public void patch(DocumentDescriptor desc, DocumentPatchHandle patch, Transaction transaction);

    /**
     * Reads the document metadata from the database in the representation provided by the handle
     * 
     * To call readMetadata(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @return	the metadata handle populated with the metadata for the document in the database
     */
    public <T extends DocumentMetadataReadHandle> T readMetadata(String docId, T metadataHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document metadata from an open database transaction in the representation provided by the handle
     * 
     * To call readMetadata(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the metadata handle populated with the metadata for the document in the database
     */
    public <T extends DocumentMetadataReadHandle> T readMetadata(String docId, T metadataHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document metadata to the database from the representation provided by the handle
     * 
     * To call writeMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     */
    public void writeMetadata(String docId, DocumentMetadataWriteHandle metadataHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document metadata to an open database transaction from the representation provided by the handle
     * 
     * To call writeMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void writeMetadata(String docId, DocumentMetadataWriteHandle metadataHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reverts the document metadata in the database to the defaults
     * 
     * To call writeDefaultMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     */
    public void writeDefaultMetadata(String docId)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reverts the document metadata in an open database transaction to the defaults
     * 
     * To call writeDefaultMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param transaction	a open transaction under which the document may have been created or deleted
     */
    public void writeDefaultMetadata(String docId, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Gets the format of the managed documents
     * 
     * @return	the format for documents supported by this document manager
     */
    public Format getContentFormat();

    /**
     * Returns the categories of metadata to read, write, or patch.
     * 
     * @return	the set of metadata categories
     */
    public Set<Metadata> getMetadataCategories();
    /**
     * Specifies the categories of metadata to read, write, or patch.
     * @param categories	the set of metadata categories
     */
    public void setMetadataCategories(Set<Metadata> categories);
    /**
     * Specifies the categories of metadata to read, write, or patch.
     * @param categories	the set of metadata categories
     */
    public void setMetadataCategories(Metadata... categories);
    /**
     * Clears the metadata categories.
     */
    public void clearMetadataCategories();

    /**
     * Returns the transform for read requests that don't specify a transform.
     * @return	the name of the transform
     */
    public ServerTransform getReadTransform();
    /**
     * Specifies a transform for read requests that don't specify a transform.
     * 
     * @param transform	the name of the transform
     */
    public void setReadTransform(ServerTransform transform);
 
    /**
     * Returns the transform for write requests that don't specify a transform.
     * @return	the name of the transform
     */
    public ServerTransform getWriteTransform();
    /**
     * Specifies a transform for write requests that don't specify a transform.
     * 
     * @param transform	the name of the transform
     */
    public void setWriteTransform(ServerTransform transform);

    /**
     * Returns the name of the forest that should store written documents.
     * @return	the name of the forest
     */
    public String getForestName();
    /**
     * Specifies the name of the forest that should store written documents. You
     * can leave this name null to let the server select a forest.
     * 
     * @param forestName	the name of the forest
     */
    public void setForestName(String forestName);

    /**
     * Starts debugging client requests. You can suspend and resume debugging output
     * using the methods of the logger.
     * 
     * @param logger	the logger that receives debugging output
     */
    public void startLogging(RequestLogger logger);
    /**
     *  Stops debugging client requests.
     */
    public void stopLogging();
}
