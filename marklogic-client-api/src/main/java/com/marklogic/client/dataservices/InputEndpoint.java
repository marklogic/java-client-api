package com.marklogic.client.dataservices;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.InputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

public interface InputEndpoint {
	
	public interface BulkInputCaller{
		
	}
	static InputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
		return new InputEndpointImpl(client, apiDecl);
	}
}
