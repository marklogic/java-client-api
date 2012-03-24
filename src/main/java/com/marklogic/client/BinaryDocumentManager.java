package com.marklogic.client;

import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

/**
 * A Binary Document Manager provides database operations on binary documents.
 */
public interface BinaryDocumentManager extends AbstractDocumentManager<BinaryReadHandle, BinaryWriteHandle> {
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
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, T contentHandle, long start, long length);
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
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length);
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
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, T contentHandle, long start, long length, Transaction transaction);
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
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction);

	public MetadataExtraction getMetadataExtraction();
	public void setMetadataExtraction(MetadataExtraction policy);
}
