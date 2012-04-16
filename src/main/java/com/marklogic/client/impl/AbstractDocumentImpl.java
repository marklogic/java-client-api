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
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager;
import com.marklogic.client.BadRequestException;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Format;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.HandleHelper;
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

	private RESTServices       services;
	private Format             contentFormat;
	private String             readTransformName;
    private Map<String,String> readTransformParams;
	private String             writeTransformName;
    private Map<String,String> writeTransformParams;
    private String             forestName;
	private MetadataUpdate     metadataUpdatePolicy;
	private boolean            versionMatched = false;

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
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, null);
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, null);
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, null, contentHandle, transaction);
	}
	@Override
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		return read(docId, metadataHandle, contentHandle, transaction, getReadParams());
	}
	public <T extends R> T read(DocumentIdentifier docId, DocumentMetadataReadHandle metadataHandle, T contentHandle, Transaction transaction, Map<String,String> extraParams) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		logger.info("Reading metadata and content for {}", docId.getUri());

		if (!HandleHelper.isHandle(metadataHandle)) 
			throw new IllegalArgumentException(
					"metadata handle does not extend BaseHandle: "+metadataHandle.getClass().getName());
		HandleHelper metadataHand = HandleHelper.newHelper(metadataHandle);

		if (!HandleHelper.isHandle(contentHandle)) 
			throw new IllegalArgumentException(
					"content handle does not extend BaseHandle: "+contentHandle.getClass().getName());
		HandleHelper contentHand = HandleHelper.newHelper(contentHandle);

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHand != null) {
			Format metadataFormat = metadataHand.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataHand.setFormat(Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHand != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentHand.setFormat(contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHand != null && contentHand != null) {
			Object[] values = services.getDocument(
					requestLogger,
					docId, 
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					new String[]{metadataMimetype, contentMimetype},
					new Class[]{metadataHand.receiveAs(), contentHand.receiveAs()}
					);
			metadataHand.receiveContent(values[0]);
			contentHand.receiveContent(values[1]);
		} else if (metadataHand != null) {
			metadataHand.receiveContent(
					services.getDocument(
							requestLogger,
							docId,
							(transaction == null) ? null : transaction.getTransactionId(),
							metadata,
							extraParams,
							metadataMimetype,
							metadataHand.receiveAs()
							)
					);
		} else if (contentHand != null) {
			contentHand.receiveContent(
				services.getDocument(
						requestLogger,
						docId,
						(transaction == null) ? null : transaction.getTransactionId(),
						null,
						extraParams,
						contentMimetype,
						contentHand.receiveAs()
						)
				);
		}

		// TODO: after response, reset metadata and set flag

		HandleHelper.release(metadataHand);
		HandleHelper.release(contentHand);

		return contentHandle;
	}

	@Override
	public void write(DocumentIdentifier docId, W contentHandle) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, null);
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadata, W contentHandle) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadata, contentHandle, null);
	}
	@Override
	public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, null, contentHandle, transaction);
	}
	@Override
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		write(docId, metadataHandle, contentHandle, transaction, getWriteParams());
	}
	public void write(DocumentIdentifier docId, DocumentMetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction, Map<String,String> extraParams) throws ResourceNotFoundException, ForbiddenUserException, BadRequestException, FailedRequestException {
		logger.info("Writing content for {}",docId.getUri());

		if (!HandleHelper.isHandle(metadataHandle)) 
			throw new IllegalArgumentException(
					"metadata handle does not extend BaseHandle: "+metadataHandle.getClass().getName());
		HandleHelper metadataHand = HandleHelper.newHelper(metadataHandle);

		if (!HandleHelper.isHandle(contentHandle)) 
			throw new IllegalArgumentException(
					"content handle does not extend BaseHandle: "+contentHandle.getClass().getName());
		HandleHelper contentHand = HandleHelper.newHelper(contentHandle);

		String metadataMimetype = null;
		Set<Metadata> metadata = null;
		if (metadataHand != null) {
			Format metadataFormat = metadataHand.getFormat();
			if (metadataFormat == null || (metadataFormat != Format.JSON && metadataFormat != Format.XML)) {
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
				metadataHand.setFormat(Format.XML);
				metadataFormat = Format.XML;
			}

			metadataMimetype = metadataFormat.getDefaultMimetype();

			metadata = processedMetadata;
		}

		String contentMimetype = null;
		if (contentHand != null) {
			contentMimetype = docId.getMimetype();
			if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentHand.setFormat(contentFormat);
				if (contentMimetype == null)
					contentMimetype = contentFormat.getDefaultMimetype();
			}
		}

		if (metadataHand != null && contentHand != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					new String[]{metadataMimetype, contentMimetype},
					new Object[] {metadataHand.sendContent(), contentHand.sendContent()}
					);
		} else if (metadataHand != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					metadata,
					extraParams,
					metadataMimetype,
					metadataHand.sendContent()
					);
		} else if (contentHand != null) {
			services.putDocument(
					requestLogger,
					docId,
					(transaction == null) ? null : transaction.getTransactionId(),
					null,
					extraParams,
					contentMimetype,
					contentHand.sendContent()
					);
		}

		HandleHelper.release(metadataHand);
		HandleHelper.release(contentHand);
	}

	@Override
	public void delete(DocumentIdentifier docId) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(docId, null);
	}
	@Override
	public void delete(DocumentIdentifier docId, Transaction transaction) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
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
		logger.info("Resetting metadata for {}",docId.getUri());

		services.deleteDocument(requestLogger, docId, (transaction == null) ? null : transaction.getTransactionId(), processedMetadata);
    }

	@Override
    public String getReadTransformName() {
    	return readTransformName;
    }
	@Override
    public void setReadTransformName(String name) {
    	this.readTransformName = name;
    }

	@Override
    public Map<String,String> getReadTransformParameters() {
    	return readTransformParams;
    }
	@Override
    public void setReadTransformParameters(Map<String,String> parameters) {
    	this.readTransformParams = parameters;
    }
 
	@Override
    public String getWriteTransformName() {
    	return writeTransformName;
    }
	@Override
    public void setWriteTransformName(String name) {
    	this.writeTransformName = name;
    }

	@Override
    public Map<String,String> getWriteTransformParameters() {
    	return writeTransformParams;
    }
	@Override
    public void setWriteTransformParameters(Map<String,String> parameters) {
    	this.writeTransformParams = parameters;
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

	// hooks for extension
	protected Map<String,String> getReadParams() {
		return null;
	}
	protected Map<String,String> getWriteParams() {
		return null;
	}
}
