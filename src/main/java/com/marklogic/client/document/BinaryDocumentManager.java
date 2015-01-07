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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

/**
 * A Binary Document Manager provides database operations on binary documents.
 */
public interface BinaryDocumentManager extends DocumentManager<BinaryReadHandle, BinaryWriteHandle> {
	/**
	 * The MetadataExtraction enumeration identifies whether properties are extracted from binary documents
	 * to metadata properties on the binary document, to a separate XHTML document, or not at all.
	 */
	public enum MetadataExtraction {
		/**
		 * Specifies extraction of metadata to the document properties.
		 */
		PROPERTIES,
		/**
		 * Specifies extraction of metadata to a separate companion XHTML document.
		 */
		DOCUMENT,
		/**
		 * Specifies no extraction of metadata.
		 */
		NONE;
	}

    /**
     * Reads a range of bytes from the content of a binary database document in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
     * @param as	the IO class for reading the range of bytes
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	an object of the IO class with the range of bytes
	 */
	public <T> T readAs(String docId, Class<T> as, long start, long length)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads a range of bytes from the content of a binary database document in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param docId	the URI identifier for the document
	 * @param metadataHandle	a handle for reading the metadata of the document
     * @param as	the IO class for reading the range of bytes
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	an object of the IO class with the range of bytes
	 */
	public <T> T readAs(String docId, DocumentMetadataReadHandle metadataHandle, Class<T> as, long start, long length)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Reads a range of bytes from the content of a binary database document in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Reads a range of bytes from the content of a binary database document as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads a range of bytes from the content of a binary database document in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
    /**
     * Reads a range of bytes from the content of a binary database document as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads metadata and a range of bytes from the content of a binary database document in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary database document as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary database document in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary database document as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads a range of bytes from the content of a binary document for an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads a range of bytes from the content of a binary document for an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads a range of bytes from the content of a binary document for an open database transaction in the representation provided by the handle
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads a range of bytes from the content of a binary document for an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
     * Reads metadata and a range of bytes from the content of a binary document for an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary document for an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(String docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary document for an open database transaction in the representations provided by the handles
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	/**
     * Reads metadata and a range of bytes from the content of a binary document for an open database transaction as transformed on the server.
     * 
     * To call read(), an application must authenticate as rest-reader, rest-writer, or rest-admin.
     * 
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for reading the metadata of the document
     * @param contentHandle	a handle for reading the content of the document
     * @param transform	a server transform to modify the document content
     * @param start	the zero-based index of the first byte in the range
     * @param length	the number of bytes in the range
     * @param transaction	a open transaction under which the document may have been created or deleted
     * @return	the range of bytes
     */
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

	/**
	 * Returns the metadata extraction policy.
	 * @return	the policy for extracting metadata
	 */
	public MetadataExtraction getMetadataExtraction();
	/**
	 * Specifies the metadata extraction policy for binary documents.
	 * @param policy	the policy for extracting metadata
	 */
	public void setMetadataExtraction(MetadataExtraction policy);
}
