package com.marklogic.client.docio;

public interface MetadataWriteHandle<C> extends AbstractWriteHandle<C> {
	public StructureFormat getFormat();
	public void setFormat(StructureFormat format);
}
