/*
 * Copyright 2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.dataservices.impl;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

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
		if (apiDeclaration.has("$bulk") && apiDeclaration.get("$bulk").isObject()
				&& apiDeclaration.get("$bulk").has("inputBatchSize")
				&& apiDeclaration.get("$bulk").get("inputBatchSize").isInt()) {
			this.batchSize = apiDeclaration.get("$bulk").get("inputBatchSize").asInt();
		} else
			this.batchSize = DEFAULT_BATCH_SIZE;
	}

	private InputCallerImpl getCaller() {
		return this.caller;
	}

	private int getBatchSize() {
		return this.batchSize;
	}

	@Override
	public BulkInputCaller bulkCaller() {
		// TODO Auto-generated method stub
		return null;
	}
	
	final static class BulkInputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl implements InputEndpoint.BulkInputCaller {
		
		private InputEndpointImpl endpoint;
		private int batchSize;
		private LinkedBlockingQueue<InputStream> queue;
		
		private BulkInputCallerImpl(InputEndpointImpl endpoint, int batchSize) {
			super(endpoint);
            this.endpoint = endpoint;
            this.batchSize = batchSize;
        }
		
		private InputEndpointImpl getEndpoint() {
			return endpoint;
		}
		
		private int getBatchSize() {
			return batchSize;
		}

		@Override
		public void accept(InputStream input) {
			// TODO Auto-generated method stub
		}

		@Override
		public void awaitCompletion() {
			// TODO Auto-generated method stub
		}
	}
}
