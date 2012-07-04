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
package com.marklogic.client.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DocumentDescriptor;
import com.marklogic.client.DocumentManager;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Format;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ServerTransform;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

abstract class DocumentManagerImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
    extends AbstractLoggingManager
    implements DocumentManager<R, W>
{
	static final private Logger logger = LoggerFactory.getLogger(DocumentManagerImpl.class);

	final private Set<Metadata> processedMetadata;

	private RESTServices    services;
	private Format          contentFormat;
	private ServerTransform readTransform;
	private ServerTransform writeTransform;
    private String          forestName;

	DocumentManagerImpl(RESTServices services, Format contentFormat) {
		super();
		this.services = services;
		this.contentFormat = contentFormat;
	}

	RESTServices getServices() {
		return services;
	}
	void setServices(RESTServices services) {
		this.services = services;
	}

	@Override
    public Format getContentFormat() {
    	return contentFormat;
    }

    // select categories of metadata to read, write, or reset
	{
		HashSet<Metadata> metadata = new HashSet<Metadata>();
		metadata.add(Metadata.ALL);
		processedMetadata = metadata;
	}
	@Override
    public Set<Metadata> getMetadataCategories() {
    	return processedMetadata;
    }
	@Override
    public void setMetadataCategories(Set<Metadata> categories) {
		processedMetadata.clear();
		processedMetadata.addAll(categories);
    }
	@Override
    public void setMetadataCategories(Metadata... categories) {
   		processedMetadata.clear();
    	for (Metadata category: categories)
    		processedMetadata.add(category);
    }

	@Override
	public DocumentDescriptor exists(String uri) throws ForbiddenUserException, FailedRequestException {
		return exists(uri, null);
    }
	@Override
	public DocumentDescriptor exists(String uri, Transaction transaction) throws ForbiddenUserException, FailedRequestException {
		return services.head(requestLogger, uri, (transaction == null) ? null : transaction.getTransactionId());
	}

	@Override
	public <T extends R> T read(String uri, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, null, contentHandle, transform, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(uri, metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

    public <T extends R> T read(String uri, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(new DocumentDescriptorImpl(uri, true), metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

	@Override
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, null, null, getReadParams());
	}

	@Override
    public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, null, contentHandle, transform, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		return read(desc, metadataHandle, contentHandle, transform, transaction, getReadParams());
	}

    public <T extends R> T read(DocumentDescriptor desc, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Reading document with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Reading metadata and content for {}", desc.getUri());

		if (metadataHandle != null) {
			HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
			Format metadataFormat = metadataBase.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				if (logger.isWarnEnabled())
					logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataBase.setFormat(Format.XML);
			}
		}

		checkContentFormat(contentHandle);

		boolean wasModified = services.getDocument(
				requestLogger,
				desc, 
				(transaction != null) ? transaction.getTransactionId() : null,
				(metadataHandle != null) ? processedMetadata : null,
				mergeTransformParameters(
						(transform != null) ? transform : getReadTransform(),
						extraParams
						),
				metadataHandle,
				contentHandle
				);

		// TODO: after response, reset metadata and set flag

		return wasModified ? contentHandle : null;
	}

	@Override
	public void write(String uri, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadata, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadata, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadata, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(uri, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	public void write(String uri, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(new DocumentDescriptorImpl(uri, true), metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	@Override
	public void write(DocumentDescriptor desc, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadata, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadata, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadata, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		write(desc, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}

	public void write(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Writing document with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Writing content for {}",desc.getUri());

		if (metadataHandle != null) {
			HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
			Format metadataFormat = metadataBase.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				if (logger.isWarnEnabled())
					logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataBase.setFormat(Format.XML);
			}
		}

		checkContentFormat(contentHandle);

		services.putDocument(
				requestLogger,
				desc,
				(transaction == null) ? null : transaction.getTransactionId(),
				(metadataHandle != null) ? processedMetadata : null,
				mergeTransformParameters(
						(transform != null) ? transform : getWriteTransform(),
						extraParams
						),
				metadataHandle,
				contentHandle
				);
	}

	@Override
	public void delete(String uri) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(uri, null);
	}
	@Override
	public void delete(String uri, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(new DocumentDescriptorImpl(uri, true), transaction);
	}
	@Override
    public void delete(DocumentDescriptor desc) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(desc, null);
    }
	@Override
    public void delete(DocumentDescriptor desc, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (desc == null)
			throw new IllegalArgumentException("Deleting document with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Deleting {}",desc.getUri());

		services.deleteDocument(requestLogger, desc, (transaction == null) ? null : transaction.getTransactionId(), null);
    }

	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(String uri, T metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readMetadata(uri, metadataHandle, null);
    }
	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(String uri, T metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		read(uri, metadataHandle, null, transaction);

		return metadataHandle;
    }

	@Override
    public void writeMetadata(String uri, DocumentMetadataWriteHandle metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeMetadata(uri, metadataHandle, null);
    }
	@Override
    public void writeMetadata(String uri, DocumentMetadataWriteHandle metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		write(uri, metadataHandle, null, transaction);
    }

	@Override
    public void writeDefaultMetadata(String uri) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeDefaultMetadata(uri, null);
    }
	@Override
    public void writeDefaultMetadata(String uri, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (uri == null)
			throw new IllegalArgumentException("Resetting document metadata with null identifier");

		if (logger.isInfoEnabled())
			logger.info("Resetting metadata for {}",uri);

		services.deleteDocument(requestLogger, new DocumentDescriptorImpl(uri, true), (transaction == null) ? null : transaction.getTransactionId(), processedMetadata);
    }

	@Override
    public ServerTransform getReadTransform() {
    	return readTransform;
    }
	@Override
    public void setReadTransform(ServerTransform transform) {
    	this.readTransform = transform;
    }

	@Override
    public ServerTransform getWriteTransform() {
    	return writeTransform;
    }
	@Override
    public void setWriteTransform(ServerTransform transform) {
    	this.writeTransform = transform;
    }

	@Override
    public String getForestName() {
    	return forestName;
    }
	@Override
    public void setForestName(String forestName) {
    	this.forestName = forestName;
    }

	@Override
	public DocumentDescriptor newDescriptor(String uri) {
		return new DocumentDescriptorImpl(uri, false);
	}

	private void checkContentFormat(Object contentHandle) {
		checkContentFormat(HandleAccessor.checkHandle(contentHandle, "content"));
	}
	private void checkContentFormat(HandleImplementation contentBase) {
		if (contentBase == null)
			return;

		if (contentFormat != null && contentFormat != Format.UNKNOWN) {
			Format currFormat = contentBase.getFormat();
			if (currFormat != contentFormat) {
				contentBase.setFormat(contentFormat);
				if (currFormat != Format.UNKNOWN)
					contentBase.setMimetype(contentFormat.getDefaultMimetype());
			}
		}
	}
	protected RequestParameters mergeTransformParameters(ServerTransform transform, RequestParameters extraParams) {
		if (transform == null)
			return extraParams;

		return transform.merge(extraParams);
	}

	// hooks for extension
	protected RequestParameters getReadParams() {
		return null;
	}
	protected RequestParameters getWriteParams() {
		return null;
	}
}
