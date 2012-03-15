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
	public <T extends R> T read(DocumentIdentifier docId, MetadataHandle metadataHandle, T contentHandle) {
		return read(docId, metadataHandle, contentHandle, null);
	}
	public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction) {
		return read(docId, null, contentHandle, transaction);
	}
	public <T extends R> T read(DocumentIdentifier docId, MetadataHandle metadataHandle, T contentHandle, Transaction transaction) {
		return read(docId, "application/xml", metadataHandle, contentHandle, transaction);
	}
	<T extends R> T read(DocumentIdentifier docId, String metadataMimetype, AbstractReadHandle metadataHandle, T contentHandle, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Reading metadata and content for {}",uri);

		String contentMimetype = docId.getMimetype();
		if (contentMimetype == null && defaultMimetype != null)
			contentMimetype = defaultMimetype;

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
	public void write(DocumentIdentifier docId, MetadataHandle metadata, W contentHandle) {
		write(docId, metadata, contentHandle, null);
	}
	public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction) {
		write(docId, null, contentHandle, transaction);
	}
	public void write(DocumentIdentifier docId, MetadataHandle metadataHandle, W contentHandle, Transaction transaction) {
		write(docId, "application/xml", metadataHandle, contentHandle, transaction);
	}
	void write(DocumentIdentifier docId, String metadataMimetype, AbstractWriteHandle metadataHandle, W contentHandle, Transaction transaction) {
		String uri = docId.getUri();
		logger.info("Writing content for {}",uri);

		String mimetype = docId.getMimetype();
		if (mimetype == null && defaultMimetype != null)
			mimetype = defaultMimetype;

		if (metadataHandle != null && contentHandle != null) {
			// TODO: multipart
			;
		} else if (metadataHandle != null) {
			Set<Metadata> metadata = processedMetadata;
			if (processedMetadata == null || processedMetadata.contains(Metadata.NONE)) {
				metadata = new HashSet<Metadata>();
				metadata.add(Metadata.ALL);
			}
			services.put(uri, mimetype, metadataHandle.sendContent(), metadata,
					(transaction == null) ? null : transaction.getTransactionId());
		} else if (contentHandle != null) {
			services.put(uri, mimetype, contentHandle.sendContent(), processedMetadata,
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

    public MetadataHandle readMetadata(DocumentIdentifier docId, MetadataHandle metadataHandle) {
		return readMetadata(docId, metadataHandle, null);
    }
    public MetadataHandle readMetadata(DocumentIdentifier docId, MetadataHandle metadataHandle, Transaction transaction) {
		read(docId, metadataHandle, null, transaction);

		return metadataHandle;
    }

    public void writeMetadata(DocumentIdentifier docId, MetadataHandle metadataHandle) {
		writeMetadata(docId, metadataHandle, null);
    }
    public void writeMetadata(DocumentIdentifier docId, MetadataHandle metadataHandle, Transaction transaction) {
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

    public <T extends XMLReadHandle> T readMetadataAsXML(DocumentIdentifier docId, T metadataHandle) {
    	return readMetadataAsXML(docId, metadataHandle, null);
    }
    public <T extends XMLReadHandle> T readMetadataAsXML(DocumentIdentifier docId, T metadataHandle, Transaction transaction) {
		read(docId, "application/xml", metadataHandle, null, transaction);
    	return metadataHandle;
    }

    public void writeMetadataAsXML(DocumentIdentifier docId, XMLWriteHandle metadataHandle) {
    	writeMetadataAsXML(docId, metadataHandle, null);
    }
    public void writeMetadataAsXML(DocumentIdentifier docId, XMLWriteHandle metadataHandle, Transaction transaction) {
		write(docId, "application/xml", metadataHandle, null, transaction);
    }

    public <T extends JSONReadHandle> T readMetadataAsJSON(DocumentIdentifier docId, T metadataHandle) {
    	return readMetadataAsJSON(docId, metadataHandle, null);
    }
    public <T extends JSONReadHandle> T readMetadataAsJSON(DocumentIdentifier docId, T metadataHandle, Transaction transaction) {
		read(docId, "application/json", metadataHandle, null, transaction);
    	return metadataHandle;
    }
    public void writeMetadataAsJSON(DocumentIdentifier docId, JSONWriteHandle metadataHandle) {
    	writeMetadataAsJSON(docId, metadataHandle, null);
    }
    public void writeMetadataAsJSON(DocumentIdentifier docId, JSONWriteHandle metadataHandle, Transaction transaction) {
		write(docId, "application/json", metadataHandle, null, transaction);
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
