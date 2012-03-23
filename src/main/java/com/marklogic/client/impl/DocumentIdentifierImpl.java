package com.marklogic.client.impl;

import com.marklogic.client.DocumentIdentifier;

public class DocumentIdentifierImpl implements DocumentIdentifier {
	private String uri;
	private int    byteLength = 0;
	private String mimetype;

	public DocumentIdentifierImpl(String uri) {
		super();
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public DocumentIdentifierImpl withUri(String uri) {
		setUri(uri);
		return this;
	}

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public DocumentIdentifierImpl withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	public int getByteLength() {
    	return byteLength;
    }
	public void setByteLength(int length) {
    	byteLength = length;
    }
}
