package com.marklogic.client.dataservices.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.dataservices.impl.IOEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class InputEndpointImpl extends IOEndpointImpl implements InputEndpoint {
	private static int DEFAULT_BATCH_SIZE = 100;
	private InputCallerImpl caller;
	private int batchSize;
	
	public InputEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
		this(client, new InputCallerImpl(apiDecl));
	}
	
	private InputEndpointImpl(DatabaseClient client, InputCallerImpl caller) {
		super(client, caller);
		this.caller = caller;
		
		JsonNode apiDeclaration = caller.getApiDeclaration();
		if(apiDeclaration.has("$bulk") && apiDeclaration.get("$bulk").isObject() && 
				apiDeclaration.get("$bulk").has("inputBatchSize")) {
			this.batchSize = apiDeclaration.get("$bulk").get("inputBatchSize").asInt();
		}
		else
			this.batchSize = DEFAULT_BATCH_SIZE;
	}
	
	private InputCallerImpl getCaller() {
        return this.caller;
    }
	
	private int getBatchSize() {
		return this.batchSize;
	}
	

}
