package com.marklogic.client.iml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.Transaction;
import com.marklogic.client.docio.BinaryReadHandle;
import com.marklogic.client.docio.BinaryWriteHandle;

class BinaryDocumentImpl
	extends AbstractDocumentImpl<BinaryReadHandle, BinaryWriteHandle>
	implements BinaryDocument
{
	static final private Logger logger = LoggerFactory.getLogger(BinaryDocumentImpl.class);

	BinaryDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
		setMimetype("application/x-unknown-content-type");
	}

	public <T extends BinaryReadHandle> T read(T handle, long start, long length, Metadata... categories) {
		return read(handle, start, length, null, categories);
	}
	public <T extends BinaryReadHandle> T read(T handle, long start, long length, Transaction transaction, Metadata... categories) {
		logger.info("Reading range of binary content for {}",getUri());

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
