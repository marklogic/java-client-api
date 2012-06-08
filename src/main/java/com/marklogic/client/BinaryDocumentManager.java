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

import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

/**
 * A Binary Document Manager provides database operations on binary documents.
 */
public interface BinaryDocumentManager extends DocumentManager<BinaryReadHandle, BinaryWriteHandle> {
	public enum MetadataExtraction {
		PROPERTIES, DOCUMENT, NONE;
	}

    /**
     * Reads a range of bytes from the content of a binary database document in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @param start
     * @param length
     * @return
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads metadata and a range of bytes from the content of a binary database document in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @param start
     * @param length
     * @return
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads a range of bytes from the content of a binary document for an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @param start
     * @param length
     * @param transaction
     * @return
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads metadata and a range of bytes from the content of a binary document for an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @param start
     * @param length
     * @param transaction
     * @return
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	public MetadataExtraction getMetadataExtraction();
	public void setMetadataExtraction(MetadataExtraction policy);
}
