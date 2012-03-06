package com.marklogic.client.iml;

import com.marklogic.client.AbstractDocument;
import com.marklogic.client.DocumentCollections;
import com.marklogic.client.DocumentPermissions;
import com.marklogic.client.DocumentProperties;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.AbstractDocument.Metadata;
import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;

abstract class AbstractDocumentImpl<R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocument<R, W>
{
	private RESTServices services;

	AbstractDocumentImpl(RESTServices services, String uri) {
		this.services = services;
		setUri(uri);
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public <T extends R> T read(T handle, Metadata... categories) {
		/* TODO:
		   check that uri exists
		   after response, reset metadata and set flag
		 */
		handle.receiveContent(services.get(handle.receiveAs(), uri, getMimetype(), categories));
		return handle;
	}

	public <T extends R> T read(T handle, Transaction transaction, Metadata... categories) {
		// TODO Auto-generated method stub
		return handle;
	}

	public void write(W handle) {
		// TODO Auto-generated method stub
	}
	public void write(W handle, Transaction transaction) {
		// TODO Auto-generated method stub
	}

	public void delete() {
		// TODO Auto-generated method stub
	}
	public void delete(Transaction transaction) {
		// TODO Auto-generated method stub
	}

    public void readMetadata(Metadata... categories) {
		// TODO Auto-generated method stub
    }
    public void readMetadata(Transaction transaction, Metadata... categories) {
		// TODO Auto-generated method stub
    }

    public void writeMetadata() {
		// TODO Auto-generated method stub
    }
    public void writeMetadata(Transaction transaction) {
		// TODO Auto-generated method stub
    }

	private String uri;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
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
