package com.marklogic.client;

import com.marklogic.client.abstractio.BinaryContentHandle;
import com.marklogic.client.abstractio.BinaryReadHandle;
import com.marklogic.client.abstractio.BinaryWriteHandle;

public interface BinaryDocument extends AbstractDocument<BinaryContentHandle, BinaryReadHandle, BinaryWriteHandle> {
	public enum MetadataExtraction {
		PROPERTIES, DOCUMENT, NONE;
	}

	// overloads for ranges
	public <T extends BinaryReadHandle> T read(Class<T> as, long start, long length, Metadata... categories);
	public <T extends BinaryReadHandle> T read(Class<T> as, long start, long length, Transaction transaction, Metadata... categories);

	public MetadataExtraction getMetadataExtraction();
	public void setMetadataExtraction(MetadataExtraction policy);
}
