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

import com.marklogic.client.AbstractDocumentManager;
import com.marklogic.client.BadRequestException;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Format;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ServerTransform;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
    extends AbstractLoggingManager
    implements AbstractDocumentManager<R, W>
{
	static final private Logger logger = LoggerFactory.getLogger(AbstractDocumentImpl.class);

	final private Set<Metadata> processedMetadata;

	private RESTServices    services;
	private Format          contentFormat;
	private ServerTransform readTransform;
	private ServerTransform writeTransform;
    private String          forestName;
	private MetadataUpdate  metadataUpdatePolicy;
	private boolean         versionMatched = false;

	AbstractDocumentImpl(RESTServices services, Format contentFormat) {
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
	public boolean exists(DocumentIdentifier docId) throws ForbiddenUserException, FailedRequestException {
		return exists(docId, null);
    }
	@Override
	public boolean exists(DocumentIdentifier docId, Transaction transaction) throws ForbiddenUserException, FailedRequestException {
		return services.head(requestLogger, docId, (transaction == null) ? null : transaction.getTransactionId());
	}

	@Override
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, null, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, transform, null, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, transform, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, null, transaction, getReadParams());
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, transform, transaction, getReadParams());
	}
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Reading document with null identifier");

		logger.info("Reading metadata and content for {}", docId.getUri());

		HandleAccessor.checkHandle(metadataHandle, "metadata");
		HandleAccessor.checkHandle(contentHandle,  "content");

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHandle != null) {
			Format metadataFormat = HandleAccessor.getFormat(metadataHandle);
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				HandleAccessor.setFormat(metadataHandle, Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				HandleAccessor.setFormat(contentHandle, contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHandle != null && contentHandle != null) {
			Object[] values = services.getDocument(
					requestLogger,
					docId, 
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					mergeTransformParameters(transform, extraParams),
					new String[]{metadataMimetype, contentMimetype},
					new Class[]{HandleAccessor.receiveAs(metadataHandle), HandleAccessor.receiveAs(contentHandle)}
					);
			HandleAccessor.receiveContent(metadataHandle, values[0]);
			HandleAccessor.receiveContent(contentHandle,  values[1]);
		} else if (metadataHandle != null) {
			HandleAccessor.receiveContent(
					metadataHandle,
					services.getDocument(
							requestLogger,
							docId,
							(transaction == null) ? null : transaction.getTransactionId(),
							metadata,
							mergeTransformParameters(transform, extraParams),
							metadataMimetype,
							HandleAccessor.receiveAs(metadataHandle)
							)
					);
		} else if (contentHandle != null) {
			HandleAccessor.receiveContent(
				contentHandle,
				services.getDocument(
						requestLogger,
						docId,
						(transaction == null) ? null : transaction.getTransactionId(),
						null,
						mergeTransformParameters(transform, extraParams),
						contentMimetype,
						HandleAccessor.receiveAs(contentHandle)
						)
				);
		}

		// TODO: after response, reset metadata and set flag

		return contentHandle;
	}

	@Override
	public void write(DocumentIdentifier docId, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadata, W contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadata, contentHandle, null, null, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadata, W contentHandle, ServerTransform transform)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadata, contentHandle, transform, null, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, transform, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadataHandle, contentHandle, null, transaction, getWriteParams());
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadataHandle, contentHandle, transform, transaction, getWriteParams());
	}
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform, Transaction transaction, RequestParameters extraParams)
	throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Writing document with null identifier");

		logger.info("Writing content for {}",docId.getUri());

		HandleAccessor.checkHandle(metadataHandle, "metadata");
		HandleAccessor.checkHandle(contentHandle,  "content");

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHandle != null) {
			Format metadataFormat = HandleAccessor.getFormat(metadataHandle);
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				HandleAccessor.setFormat(metadataHandle, Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				HandleAccessor.setFormat(contentHandle, contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHandle != null && contentHandle != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					mergeTransformParameters(transform, extraParams),
					new String[]{metadataMimetype, contentMimetype},
					new Object[] {HandleAccessor.sendContent(metadataHandle), HandleAccessor.sendContent(contentHandle)}
					);
		} else if (metadataHandle != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					mergeTransformParameters(transform, extraParams),
					metadataMimetype,
					HandleAccessor.sendContent(metadataHandle)
					);
		} else if (contentHandle != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					null,
					mergeTransformParameters(transform, extraParams),
					contentMimetype,
					HandleAccessor.sendContent(contentHandle)
					);
		}
	}

	@Override
	public void delete(DocumentIdentifier docId) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(docId, null);
	}
	@Override
	public void delete(DocumentIdentifier docId, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Deleting document with null identifier");

		logger.info("Deleting {}",docId.getUri());

		services.deleteDocument(requestLogger, docId, (transaction == null) ? null : transaction.getTransactionId(), null);
	}

	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readMetadata(docId, metadataHandle, null);
    }
	@Override
    public <T extends DocumentMetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		read(docId, metadataHandle, null, transaction);

		return metadataHandle;
    }

	@Override
    public void writeMetadata(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeMetadata(docId, metadataHandle, null);
    }
	@Override
    public void writeMetadata(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		write(docId, metadataHandle, null, transaction);
    }

	@Override
    public void writeDefaultMetadata(DocumentIdentifier docId) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		writeDefaultMetadata(docId, null);
    }
	@Override
    public void writeDefaultMetadata(DocumentIdentifier docId, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Resetting document metadata with null identifier");

		logger.info("Resetting metadata for {}",docId.getUri());

		services.deleteDocument(requestLogger, docId, (transaction == null) ? null : transaction.getTransactionId(), processedMetadata);
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
    public MetadataUpdate getMetadataUpdatePolicy() {
    	return metadataUpdatePolicy;
    }
	@Override
    public void SetMetadataUpdatePolicy(MetadataUpdate policy) {
    	metadataUpdatePolicy = policy;
    }

	@Override
	public boolean isVersionMatched() {
		return versionMatched;
	}
	@Override
	public void setVersionMatched(boolean match) {
		versionMatched = match;
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
