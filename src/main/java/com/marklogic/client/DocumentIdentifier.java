package com.marklogic.client;

public class DocumentIdentifier {
	public DocumentIdentifier(String uri) {
		super();
		this.uri = uri;
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
	public void setByteLength(int length) {
    	byteLength = length;
    }

	private String mimetype;
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
}
