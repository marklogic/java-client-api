package com.marklogic.client.iml;

import com.marklogic.client.AbstractDocument;
import com.marklogic.client.DocumentCollections;
import com.marklogic.client.DocumentPermissions;
import com.marklogic.client.DocumentProperties;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.abstractio.AbstractContentHandle;
import com.marklogic.client.abstractio.AbstractReadHandle;
import com.marklogic.client.abstractio.AbstractWriteHandle;
import com.marklogic.client.iml.io.BytesHandleImpl;
import com.marklogic.client.iml.io.InputStreamHandleImpl;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

abstract class AbstractDocumentImpl<N extends AbstractContentHandle, R extends AbstractReadHandle, W extends AbstractWriteHandle>
	implements AbstractDocument<N, R, W>
{
	private RESTServices services;

	AbstractDocumentImpl(RESTServices services, String uri) {
		this.services = services;
		setUri(uri);
	}

	public <T extends N> T newHandle(Class<T> as) {
		return makeHandle(as);
	}
	private <T> T makeHandle(Class<T> as) {
		if (as == BytesHandle.class)
			return (T) new BytesHandleImpl();
		if (as == InputStreamHandle.class)
			return (T) new InputStreamHandleImpl();
		try {
			return (T) as.newInstance();
		} catch (InstantiationException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public <T extends R> T read(Class<T> handleAs, Metadata... categories) {
		/* TODO:
		   check that uri exists
		   after response, reset metadata and set flag
		 */
		T handle = makeHandle(handleAs);
		handle.set(services.get(handle.handles(), uri, getMimetype(), categories));
		return handle;
	}

	public <T extends R> T read(Class<T> handleAs, Transaction transaction, Metadata... categories) {
		// TODO Auto-generated method stub
		return null;
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
