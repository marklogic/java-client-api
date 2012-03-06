package com.marklogic.client.iml;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.Transaction;
import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;

class BinaryDocumentImpl
	extends AbstractDocumentImpl<BinaryReadHandle, BinaryWriteHandle>
	implements BinaryDocument
{
	BinaryDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}

	public <T extends BinaryReadHandle> T read(T handle, long start, long length, Metadata... categories) {
		// TODO Auto-generated method stub
		return handle;
	}

	public <T extends BinaryReadHandle> T read(T handle, long start, long length, Transaction transaction, Metadata... categories) {
		// TODO Auto-generated method stub
		return handle;
	}

	private MetadataExtraction metadataExtraction = MetadataExtraction.NONE;
	public MetadataExtraction getMetadataExtraction() {
		return metadataExtraction;
	}

	public void setMetadataExtraction(MetadataExtraction policy) {
		metadataExtraction = policy;	
	}
}
