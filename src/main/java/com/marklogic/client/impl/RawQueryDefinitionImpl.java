package com.marklogic.client.impl;

import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.RawQueryDefinition;

public class RawQueryDefinitionImpl extends AbstractQueryDefinition implements
		RawQueryDefinition {

	private StructureWriteHandle handle;
	
	
	@Override
	public StructureWriteHandle getHandle() {
		return handle;
	}

	@Override
	public void setHandle(StructureWriteHandle handle) {
		this.handle = handle;
	}

	@Override
	public RawQueryDefinition withHandle(StructureWriteHandle handle) {
		this.handle = handle;
		return this;
	}

}
