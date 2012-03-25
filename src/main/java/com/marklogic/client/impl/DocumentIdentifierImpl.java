package com.marklogic.client.impl;

import com.marklogic.client.DocumentIdentifier;

public class DocumentIdentifierImpl implements DocumentIdentifier {
	private String uri;
	private long   byteLength = UNKNOWN_LENGTH;
	private String mimetype;

	public DocumentIdentifierImpl(String uri) {
		super();
		setUri(uri);
	}

	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
		if (byteLength != UNKNOWN_LENGTH)
			byteLength = UNKNOWN_LENGTH;
	}
	public DocumentIdentifier withUri(String uri) {
		setUri(uri);
		return this;
	}

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public DocumentIdentifier withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	public long getByteLength() {
    	return byteLength;
    }
	public void setByteLength(long length) {
    	byteLength = length;
    }
}
