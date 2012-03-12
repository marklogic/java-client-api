package com.marklogic.client.iml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentBuffer;
import com.marklogic.client.DocumentCollections;
import com.marklogic.client.DocumentPermissions;
import com.marklogic.client.DocumentProperties;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocumentBuffer<R, W>
{
	static final private Logger logger = LoggerFactory.getLogger(AbstractDocumentImpl.class);

	private RESTServices services;

	AbstractDocumentImpl(RESTServices services, String uri) {
		this.services = services;
		setUri(uri);
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

	public boolean exists() {
		return exists(null);
    }

	public boolean exists(Transaction transaction) {
		logger.info("Checking existence of {}",uri);

		Map<String,List<String>> headers = services.head(uri, (transaction == null) ? null : transaction.getTransactionId());
		if (headers == null)
			return false;

		List<String> values = null;
		if (headers.containsKey("Content-Type")) {
			values = headers.get("Content-Type");
			if (values != null) {
				String type = values.get(0);
				mimetype = type.contains(";") ? type.substring(0, type.indexOf(";")) : type;
			}
		}
		if (headers.containsKey("Content-Length")) {
			values = headers.get("Content-Length");
			if (values != null) {
				byteLength = Integer.valueOf(values.get(0));
			}
		}

		return true;
	}

	public <T extends R> T read(T handle) {
		return read(handle, null);
	}

	public <T extends R> T read(T handle, Transaction transaction) {
		logger.info("Reading content for {}",uri);

		// TODO: after response, reset metadata and set flag
		handle.receiveContent(
				services.get(handle.receiveAs(), uri, mimetype, processedMetadata,
						(transaction == null) ? null : transaction.getTransactionId())
				);
		return handle;
	}

	public void write(W handle) {
		write(handle, null);
	}
	public void write(W handle, Transaction transaction) {
		logger.info("Writing content for {}",uri);

		services.put(uri, mimetype, handle.sendContent(), processedMetadata,
				(transaction == null) ? null : transaction.getTransactionId());
	}

	public void delete() {
		delete(null);
	}
	public void delete(Transaction transaction) {
		logger.info("Deleting {}",uri);

		services.delete(uri, (transaction == null) ? null : transaction.getTransactionId());
	}

    public void readMetadata() {
		readMetadata(null);
    }
    public void readMetadata(Transaction transaction) {
		logger.info("Reading metadata for {}",uri);

		// TODO Auto-generated method stub
    }

    public void writeMetadata() {
		writeMetadata(null);
    }
    public void writeMetadata(Transaction transaction) {
		logger.info("Writing metadata for {}",uri);

		// TODO Auto-generated method stub
    }

    public void writeDefaultMetadata() {
		writeDefaultMetadata(null);
    }
    public void writeDefaultMetadata(Transaction transaction) {
		logger.info("Resetting metadata for {}",uri);

		// TODO Auto-generated method stub
    }

    public <T extends XMLReadHandle> T readMetadataAsXML(T handle) {
		return readMetadataAsXML(handle, null);
    }
    public <T extends XMLReadHandle> T readMetadataAsXML(T handle, Transaction transaction) {
		logger.info("Reading metadata as XML for {}",uri);

		handle.receiveContent(
				services.get(handle.receiveAs(), uri, mimetype, processedMetadata,
						(transaction == null) ? null : transaction.getTransactionId())
				);
		return handle;
    }

    public void writeMetadataAsXML(XMLWriteHandle handle) {
    	writeMetadataAsXML(handle, null);
    }
    public void writeMetadataAsXML(XMLWriteHandle handle, Transaction transaction) {
		logger.info("Writing metadata as XML for {}",uri);

		services.put(uri, mimetype, handle.sendContent(), processedMetadata,
				(transaction == null) ? null : transaction.getTransactionId());
    }

    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle) {
		return readMetadataAsJSON(handle, null);
    }
    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle, Transaction transaction) {
		logger.info("Reading metadata as JSON for {}",uri);

		handle.receiveContent(
				services.get(handle.receiveAs(), uri, mimetype, processedMetadata,
						(transaction == null) ? null : transaction.getTransactionId())
				);
		return handle;
    }
    public void writeMetadataAsJSON(JSONWriteHandle handle) {
    	writeMetadataAsJSON(handle, null);
    }
    public void writeMetadataAsJSON(JSONWriteHandle handle, Transaction transaction) {
		logger.info("Writing metadata as JSON for {}",uri);

		services.put(uri, mimetype, handle.sendContent(), processedMetadata,
				(transaction == null) ? null : transaction.getTransactionId());
    }

    private String uri;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	private int byteLength = 0;
	public int getByteLength() {
    	return byteLength;
    }

	private String mimetype;
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public DocumentCollections getCollections() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setCollections(DocumentCollections collections) {
		// TODO Auto-generated method stub
	}
    public void setCollections(String... collections) {
		// TODO Auto-generated method stub
	}

	public DocumentPermissions getPermissions() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setPermissions(DocumentPermissions permissions) {
		// TODO Auto-generated method stub
	}

	public DocumentProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setProperties(DocumentProperties properties) {
		// TODO Auto-generated method stub
	}

	private int quality = 0;
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
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
