package com.marklogic.client;

import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;
import com.marklogic.client.io.MetadataHandle;

public interface BinaryDocumentManager extends AbstractDocumentManager<BinaryReadHandle, BinaryWriteHandle> {
	public enum MetadataExtraction {
		PROPERTIES, DOCUMENT, NONE;
	}

	// overloads for ranges
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, T contentHandle, long start, long length);
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, MetadataHandle metadataHandle, T contentHandle, long start, long length);
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, T contentHandle, long start, long length, Transaction transaction);
	public <T extends BinaryReadHandle> T read(DocumentIdentifier docId, MetadataHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction);

	public MetadataExtraction getMetadataExtraction();
	public void setMetadataExtraction(MetadataExtraction policy);
}
