package com.marklogic.client;

/**
 * A Document Identifier identifies a document for database read, write, and delete operations.
 */
public class DocumentIdentifier {
	private String uri;
	private int    byteLength = 0;
	private String mimetype;

	public DocumentIdentifier(String uri) {
		super();
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
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

	public int getByteLength() {
    	return byteLength;
    }
	public void setByteLength(int length) {
    	byteLength = length;
    }
}
