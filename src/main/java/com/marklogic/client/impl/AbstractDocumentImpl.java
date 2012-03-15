package com.marklogic.client.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.MetadataReadHandle;
import com.marklogic.client.docio.MetadataWriteHandle;
import com.marklogic.client.docio.StructureFormat;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;
import com.marklogic.client.io.MetadataHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocumentManager<R, W>
{
	static final private Logger logger = LoggerFactory.getLogger(AbstractDocumentImpl.class);

	private RESTServices services;
	private String defaultMimetype;

	AbstractDocumentImpl(RESTServices services) {
		this.services = services;
	}
	AbstractDocumentImpl(RESTServices services, String defaultMimetype) {
		this(services);
		this.defaultMimetype = defaultMimetype;
	}

    // select categories of metadata to read, write, or reset
	private Set<Metadata> processedMetadata;
    public Set<Metadata> getMetadataCategories() {
    	return processedMetadata;
    }
    public void setMetadataCategories(Set<Metadata> categories) {
    	this.processedMetadata = categories;    	
    }
    public void setMetadataCategories(Metadata... categories) {
    	if (processedMetadata == null)
    		processedMetadata = new HashSet<Metadata>();
    	else
    		processedMetadata.clear();
    	for (Metadata category: categories)
    		processedMetadata.add(category);
    }

	public boolean exists(DocumentIdentifier docId) {
		return exists(docId, null);
    }
	public boolean exists(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Checking existence of {}",uri);

		Map<String,List<String>> headers = services.head(uri, (transaction == null) ? null : transaction.getTransactionId());
		if (headers == null)
			return false;

		List<String> values = null;
		if (headers.containsKey("Content-Type")) {
			values = headers.get("Content-Type");
			if (values != null) {
				String type = values.get(0);
				docId.setMimetype(
						type.contains(";") ? type.substring(0, type.indexOf(";")) : type
						);
			}
		}
		if (headers.containsKey("Content-Length")) {
			values = headers.get("Content-Length");
			if (values != null) {
				docId.setByteLength(
						Integer.valueOf(values.get(0))
						);
			}
		}

		return true;
	}

	public <T extends R> T read(DocumentIdentifier docId, T contentHandle) {
		return read(docId, null, contentHandle, null);
	}
	public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle) {
		return read(docId, metadataHandle, contentHandle, null);
	}
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction) {
		return read(docId, null, contentHandle, transaction);
	}
	public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Reading metadata and content for {}",uri);

		String metadataMimetype = null;
		if (metadataHandle != null) {
			StructureFormat metadataFormat = metadataHandle.getFormat();
			if (metadataFormat == StructureFormat.JSON) {
				metadataMimetype = "application/json";
			} else if (metadataFormat == StructureFormat.XML) {
				metadataMimetype = "application/xml";
			} else {
				logger.warn("Unknown metadata format {}",metadataFormat.name());
				metadataMimetype = "application/xml";
			}
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentMimetype == null && defaultMimetype != null)
				contentMimetype = defaultMimetype;
		}

		if (metadataHandle != null && contentHandle != null) {
			// TODO: multipart
			;
		} else if (metadataHandle != null) {
			Set<Metadata> metadata = processedMetadata;
			if (processedMetadata == null || processedMetadata.contains(Metadata.NONE)) {
				metadata = new HashSet<Metadata>();
				metadata.add(Metadata.ALL);
			}
			metadataHandle.receiveContent(
					services.get(metadataHandle.receiveAs(), uri, metadataMimetype, metadata,
							(transaction == null) ? null : transaction.getTransactionId())
					);
		} else if (contentHandle != null) {
			contentHandle.receiveContent(
				services.get(contentHandle.receiveAs(), uri, contentMimetype, processedMetadata,
						(transaction == null) ? null : transaction.getTransactionId())
				);
		}

		// TODO: after response, reset metadata and set flag

		return contentHandle;
	}

	public void write(DocumentIdentifier docId, W contentHandle) {
		write(docId, null, contentHandle, null);
	}
	public void write(DocumentIdentifier docId, MetadataWriteHandle metadata, W contentHandle) {
		write(docId, metadata, contentHandle, null);
	}
	public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction) {
		write(docId, null, contentHandle, transaction);
	}
	public void write(DocumentIdentifier docId, MetadataWriteHandle metadataHandle, W contentHandle, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Writing content for {}",uri);

		String metadataMimetype = null;
		if (metadataHandle != null) {
			StructureFormat metadataFormat = metadataHandle.getFormat();
			if (metadataFormat == StructureFormat.JSON) {
				metadataMimetype = "application/json";
			} else if (metadataFormat == StructureFormat.XML) {
				metadataMimetype = "application/xml";
			} else {
				logger.warn("Unknown metadata format {}",metadataFormat.name());
				metadataMimetype = "application/xml";
			}
		}

		String contentMimetype = null;
		if (contentHandle != null) {
			contentMimetype = docId.getMimetype();
			if (contentMimetype == null && defaultMimetype != null)
				contentMimetype = defaultMimetype;
		}

		if (metadataHandle != null && contentHandle != null) {
			// TODO: multipart
			;
		} else if (metadataHandle != null) {
			Set<Metadata> metadata = processedMetadata;
			if (processedMetadata == null || processedMetadata.contains(Metadata.NONE)) {
				metadata = new HashSet<Metadata>();
				metadata.add(Metadata.ALL);
			}
			services.put(uri, metadataMimetype, metadataHandle.sendContent(), metadata,
					(transaction == null) ? null : transaction.getTransactionId());
		} else if (contentHandle != null) {
			services.put(uri, contentMimetype, contentHandle.sendContent(), processedMetadata,
				(transaction == null) ? null : transaction.getTransactionId());
		}
	}

	public void delete(DocumentIdentifier docId) {
		delete(docId, null);
	}
	public void delete(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Deleting {}",uri);

		services.delete(uri, (transaction == null) ? null : transaction.getTransactionId());
	}

    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle) {
		return readMetadata(docId, metadataHandle, null);
    }
    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle, Transaction transaction) {
		read(docId, metadataHandle, null, transaction);

		return metadataHandle;
    }

    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle) {
		writeMetadata(docId, metadataHandle, null);
    }
    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle, Transaction transaction) {
		write(docId, metadataHandle, null, transaction);
    }

    public void writeDefaultMetadata(DocumentIdentifier docId) {
		writeDefaultMetadata(docId, null);
    }
    public void writeDefaultMetadata(DocumentIdentifier docId, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Resetting metadata for {}",uri);

		// TODO Auto-generated method stub
    }

	private String readTransformName;
    public String getReadTransformName() {
    	return readTransformName;
    }
    public void setReadTransformName(String name) {
    	this.readTransformName = name;
    }

    private Map<String,String> readTransformParams;
    public Map<String,String> getReadTransformParameters() {
    	return readTransformParams;
    }
    public void setReadTransformParameters(Map<String,String> parameters) {
    	this.readTransformParams = parameters;
    }
 
	private String writeTransformName;
    public String getWriteTransformName() {
    	return writeTransformName;
    }
    public void setWriteTransformName(String name) {
    	this.writeTransformName = name;
    }

    private Map<String,String> writeTransformParams;
    public Map<String,String> getWriteTransformParameters() {
    	return writeTransformParams;
    }
    public void setWriteTransformParameters(Map<String,String> parameters) {
    	this.writeTransformParams = parameters;
    }
 
    private String forestName;
    public String getForestName() {
    	return forestName;
    }
    public void setForestName(String forestName) {
    	this.forestName = forestName;
    }

	private MetadataUpdate metadataUpdatePolicy;
    public MetadataUpdate getMetadataUpdatePolicy() {
    	return metadataUpdatePolicy;
    }
    public void SetMetadataUpdatePolicy(MetadataUpdate policy) {
    	metadataUpdatePolicy = policy;
    }

	private boolean versionMatched = false;
	public boolean isVersionMatched() {
		return versionMatched;
	}
	public void setVersionMatched(boolean match) {
		versionMatched = match;
	}

	public void startLogging(RequestLogger logger) {
		// TODO Auto-generated method stub
	}
	public void stopLogging() {
		// TODO Auto-generated method stub
	}

}
