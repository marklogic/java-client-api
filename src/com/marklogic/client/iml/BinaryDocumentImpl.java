package com.marklogic.client.iml;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.Transaction;
import com.marklogic.client.abstractio.BinaryContentHandle;
import com.marklogic.client.abstractio.BinaryReadHandle;
import com.marklogic.client.abstractio.BinaryWriteHandle;

class BinaryDocumentImpl
	extends AbstractDocumentImpl<BinaryContentHandle, BinaryReadHandle, BinaryWriteHandle>
	implements BinaryDocument
{
	BinaryDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
	}

	public <T extends BinaryReadHandle> T read(Class<T> as, long start, long length, Metadata... categories) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends BinaryReadHandle> T read(Class<T> as, long start, long length, Transaction transaction, Metadata... categories) {
		// TODO Auto-generated method stub
		return null;
	}

	private MetadataExtraction metadataExtraction = MetadataExtraction.NONE;
	public MetadataExtraction getMetadataExtraction() {
		return metadataExtraction;
	}

	public void setMetadataExtraction(MetadataExtraction policy) {
		metadataExtraction = policy;	
	}
}
