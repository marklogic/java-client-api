/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.io.Format;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

class BinaryDocumentImpl
	extends DocumentManagerImpl<BinaryReadHandle, BinaryWriteHandle>
	implements BinaryDocumentManager
{
	static final private Logger logger = LoggerFactory.getLogger(BinaryDocumentImpl.class);

	private MetadataExtraction metadataExtraction = MetadataExtraction.NONE;

	BinaryDocumentImpl(RESTServices services) {
		super(services, Format.BINARY);
	}

	// shortcut readers
	@Override
	public <T> T readAs(String uri, Class<T> as, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readAs(uri, null, as, start, length);
	}
	@Override
	public <T> T readAs(String uri, DocumentMetadataReadHandle metadataHandle, Class<T> as, long start, long length)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);

		if (null == read(uri, metadataHandle, castAbstractReadHandle(as, handle), start, length)) {
			return null;
		}

		return handle.get();
	}

	// strongly typed readers
	@Override
	public <T extends BinaryReadHandle> T read(String uri, T contentHandle, long start, long length) {
		return read(uri, null, contentHandle, null, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, T contentHandle, ServerTransform transform, long start, long length) {
		return read(uri, null, contentHandle, transform, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length) {
		return read(uri, metadataHandle, contentHandle, null, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length) {
		return read(uri, metadataHandle, contentHandle, transform, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, T contentHandle, long start, long length, Transaction transaction) {
		return read(uri, null, contentHandle, null, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction) {
		return read(uri, null, contentHandle, transform, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction) {
		return read(uri, metadataHandle, contentHandle, null, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction) {
		return read(new DocumentDescriptorImpl(uri, true), metadataHandle, contentHandle, transform, start, length, transaction);
	}

	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length) {
		return read(desc, null, contentHandle, null, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length) {
		return read(desc, null, contentHandle, transform, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length) {
		return read(desc, metadataHandle, contentHandle, null, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length) {
		return read(desc, metadataHandle, contentHandle, transform, start, length, null);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, long start, long length, Transaction transaction) {
		return read(desc, null, contentHandle, null, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction) {
		return read(desc, null, contentHandle, transform, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, long start, long length, Transaction transaction) {
		return read(desc, metadataHandle, contentHandle, null, start, length, transaction);
	}
	@Override
	public <T extends BinaryReadHandle> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, long start, long length, Transaction transaction) {
		if (logger.isInfoEnabled())
			logger.info("Reading range of binary content for {}",desc.getUri());

		RequestParameters extraParams = new RequestParameters();
		if (length > 0)
			extraParams.put("range", "bytes="+start+"-"+(start + length - 1));
		else
			extraParams.put("range", "bytes="+String.valueOf(start));

		return read(desc, metadataHandle, contentHandle, transform, transaction, extraParams);
	}

	public MetadataExtraction getMetadataExtraction() {
		return metadataExtraction;
	}
	public void setMetadataExtraction(MetadataExtraction policy) {
		metadataExtraction = policy;	
	}

	protected RequestParameters getWriteParams() {
		if (metadataExtraction == null || metadataExtraction == MetadataExtraction.NONE)
			return null;

		RequestParameters params = new RequestParameters();
		if (metadataExtraction == MetadataExtraction.PROPERTIES)
			params.put("extract", "properties");
		else if (metadataExtraction == MetadataExtraction.DOCUMENT)
			params.put("extract", "document");
		else
			throw new MarkLogicInternalException("Internal error - unknown metadata extraction policy: "+metadataExtraction.name());

		return params;
	}
}
