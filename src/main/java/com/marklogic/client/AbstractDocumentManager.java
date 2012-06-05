/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client;

import java.util.Set;

import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

/**
 * A Document Manager provides database operations on a document.
 * 
 * @param <R> the set of handles for reading the document content from the database
 * @param <W> the set of handles for writing the document content to the database
 */
public interface AbstractDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    /**
     * The Metadata enumeration specifies the categories of metadata read from or written to the database.
     */
	public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY;
    }

	// whether all permissions and propertiesOption are replaced or only named permissions and propertiesOption
    public enum MetadataUpdate {
        REPLACE_ALL, REPLACE_NAMED;
    }

    /**
     * Checks whether a document exists and gets its length and format
     * 
     * To call exists(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId
     * @return
     */
    public DocumentDescriptor exists(String docId)
    	throws ForbiddenUserException, FailedRequestException;
    /**
     * Checks whether a document exists in an open transaction and gets its length and format
     * 
     * To call exists(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId
     * @param transaction
     * @return
     */
    public DocumentDescriptor exists(String docId, Transaction transaction)
    	throws ForbiddenUserException, FailedRequestException;
 
    /**
     * Reads the document content from the database in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @return
     */
    public <T extends R> T read(String docId, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public <T extends R> T read(String docId, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Reads the document metadata and content from the database in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @return
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Reads the document content from an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @param transaction
     * @return
     */
    public <T extends R> T read(String docId, T contentHandle, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public <T extends R> T read(String docId, T contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Reads the document metadata and content from an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @param transaction
     * @return
     */
    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public <T extends R> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Writes the document content to the database from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param contentHandle
     */
    public void write(String docId, W contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public void write(String docId, W contentHandle, ServerTransform transform)
		throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Writes the document metadata and content to the database from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param metadata
     * @param contentHandle
     */
    public void write(String docId, DocumentMetadataWriteHandle metadata, W contentHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public void write(String docId, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Writes the document content to an open database transaction from the representation provided by the handle
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param contentHandle
     * @param transaction
     */
    public void write(String docId, W contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public void write(String docId, W contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Writes the document metadata and content to an open database transaction from the representations provided by the handles
     * 
     * To call write(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param metadata
     * @param contentHandle
     * @param transaction
     */
    public void write(String docId, DocumentMetadataWriteHandle metadata, W contentHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    public void write(String docId, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException;

    /**
     * Deletes the document metadata and content from the database
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     */
    public void delete(String docId)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes the document metadata and content from an open database transaction
     * 
     * To call delete(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param transaction
     */
    public void delete(String docId, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reads the document metadata from the database in the representation provided by the handle
     * 
     * To call readMetadata(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @return
     */
    public <T extends DocumentMetadataReadHandle> T readMetadata(String docId, T metadataHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads the document metadata from an open database transaction in the representation provided by the handle
     * 
     * To call readMetadata(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param transaction
     * @return
     */
    public <T extends DocumentMetadataReadHandle> T readMetadata(String docId, T metadataHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Writes the document metadata to the database from the representation provided by the handle
     * 
     * To call writeMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param metadataHandle
     */
    public void writeMetadata(String docId, DocumentMetadataWriteHandle metadataHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Writes the document metadata to an open database transaction from the representation provided by the handle
     * 
     * To call writeMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param metadataHandle
     * @param transaction
     */
    public void writeMetadata(String docId, DocumentMetadataWriteHandle metadataHandle, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reverts the document metadata in the database to the defaults
     * 
     * To call writeDefaultMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     */
    public void writeDefaultMetadata(String docId)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reverts the document metadata in an open database transaction to the defaults
     * 
     * To call writeDefaultMetadata(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @param docId
     * @param transaction
     */
    public void writeDefaultMetadata(String docId, Transaction transaction)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Gets the format of the managed documents
     * 
     * @return
     */
    public Format getContentFormat();

    /**
     * Returns the categories of metadata to read or write
     * 
     * @return
     */
    public Set<Metadata> getMetadataCategories();
    /**
     * Specifies the categories of metadata to read or write
     * 
     * @param categories
     */
    public void setMetadataCategories(Set<Metadata> categories);
    /**
     * Specifies the categories of metadata to read or write
     * 
     * @param categories
     */
    public void setMetadataCategories(Metadata... categories);

    public ServerTransform getReadTransform();
    public void setReadTransform(ServerTransform transform);
 
    public ServerTransform getWriteTransform();
    public void setWriteTransform(ServerTransform transform);
 
    public String getForestName();
    public void setForestName(String forestName);

    public MetadataUpdate getMetadataUpdatePolicy();
    public void SetMetadataUpdatePolicy(MetadataUpdate policy);

    // optimistic locking
    public boolean isVersionMatched();
    public void setVersionMatched(boolean match);
 
    // for debugging client requests
    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
