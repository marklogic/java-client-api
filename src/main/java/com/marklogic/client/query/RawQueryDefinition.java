package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

public interface RawQueryDefinition extends QueryDefinition {
	
	public StructureWriteHandle getHandle();

	public void setHandle(StructureWriteHandle handle);

	public RawQueryDefinition withHandle(StructureWriteHandle handle);

}
