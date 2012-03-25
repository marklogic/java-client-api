package com.marklogic.client;

/**
 * A Document Identifier identifies a document for database read, write, and delete operations.
 */
public interface DocumentIdentifier {
	final static public long UNKNOWN_LENGTH = -1;

	public String getUri();
	public void setUri(String uri);
	public DocumentIdentifier withUri(String uri);

	public String getMimetype();
	public void setMimetype(String mimetype);
	public DocumentIdentifier withMimetype(String mimetype);

	public long getByteLength();
	public void setByteLength(long length);
}
