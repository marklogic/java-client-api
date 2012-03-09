package com.marklogic.client.iml;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.AbstractDocument;
import com.marklogic.client.DocumentCollections;
import com.marklogic.client.DocumentPermissions;
import com.marklogic.client.DocumentProperties;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.AbstractDocument.Metadata;
import com.marklogic.client.AbstractDocument.MetadataUpdate;
import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocument<R, W>
{
	private RESTServices services;

	AbstractDocumentImpl(RESTServices services, String uri) {
		this.services = services;
		setUri(uri);
	}

    // select categories of metadata to read, write, or reset
	private Set<Metadata> processedMetadata;
    public Set<Metadata> getProcessedMetadata() {
    	return processedMetadata;
    }
    public void setProcessedMetadata(Set<Metadata> categories) {
    	this.processedMetadata = categories;    	
    }
    public void setProcessedMetadata(Metadata... categories) {
    	if (processedMetadata == null)
    		processedMetadata = Collections.emptySet();
    	else
    		processedMetadata.clear();
    	for (Metadata category: categories)
    		processedMetadata.add(category);
    }

	public boolean exists() {
		return exists(null);
    }

	public boolean exists(Transaction transaction) {
		services.head(uri, (transaction == null) ? null : transaction.getTransactionId());
		// TODO: length and mimetype
		return false;
	}

	public <T extends R> T read(T handle) {
		return read(handle, null);
	}

	public <T extends R> T read(T handle, Transaction transaction) {
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
		services.put(uri, mimetype, handle.sendContent(), processedMetadata,
				(transaction == null) ? null : transaction.getTransactionId());
	}

	public void delete() {
		delete(null);
	}
	public void delete(Transaction transaction) {
		services.delete(uri, (transaction == null) ? null : transaction.getTransactionId());
	}

    public void readMetadata() {
		readMetadata(null);
    }
    public void readMetadata(Transaction transaction) {
		// TODO Auto-generated method stub
    }

    public void writeMetadata() {
		writeMetadata(null);
    }
    public void writeMetadata(Transaction transaction) {
		// TODO Auto-generated method stub
    }

    public void resetMetadata() {
		resetMetadata(null);
    }
    public void resetMetadata(Transaction transaction) {
		// TODO Auto-generated method stub
    }

    public <T extends XMLReadHandle> T readMetadataAsXML(T handle) {
		// TODO Auto-generated method stub
		return handle;
    }
    public <T extends XMLReadHandle> T readMetadataAsXML(T handle, Transaction transaction) {
		// TODO Auto-generated method stub
		return handle;
    }
    public void writeMetadataAsXML(XMLWriteHandle handle) {
		// TODO Auto-generated method stub
    }
    public void writeMetadataAsXML(XMLWriteHandle handle, Transaction transaction) {
		// TODO Auto-generated method stub
    }

    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle) {
		// TODO Auto-generated method stub
		return handle;
    }
    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle, Transaction transaction) {
		// TODO Auto-generated method stub
		return handle;
    }
    public void writeMetadataAsJSON(JSONWriteHandle handle) {
		// TODO Auto-generated method stub
    }
    public void writeMetadataAsJSON(JSONWriteHandle handle, Transaction transaction) {
		// TODO Auto-generated method stub
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
