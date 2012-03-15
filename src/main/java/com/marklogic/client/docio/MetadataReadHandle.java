package com.marklogic.client.docio;

public interface MetadataReadHandle<C> extends AbstractReadHandle<C> {
	public StructureFormat getFormat();
	public void setFormat(StructureFormat format);
}
