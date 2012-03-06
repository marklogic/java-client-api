package com.marklogic.client;

import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;

public interface BinaryDocument extends AbstractDocument<BinaryReadHandle, BinaryWriteHandle> {
	public enum MetadataExtraction {
		PROPERTIES, DOCUMENT, NONE;
	}

	// overloads for ranges
	public BinaryReadHandle read(BinaryReadHandle handle, long start, long length, Metadata... categories);
	public BinaryReadHandle read(BinaryReadHandle handle, long start, long length, Transaction transaction, Metadata... categories);

	public MetadataExtraction getMetadataExtraction();
	public void setMetadataExtraction(MetadataExtraction policy);
}
