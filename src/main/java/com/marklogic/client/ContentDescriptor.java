package com.marklogic.client;

public interface ContentDescriptor {
	final static public long UNKNOWN_LENGTH = -1;

	public Format getFormat();
	public void setFormat(Format format);

	public String getMimetype();
	public void setMimetype(String mimetype);

	public long getByteLength();
	public void setByteLength(long length);
}
