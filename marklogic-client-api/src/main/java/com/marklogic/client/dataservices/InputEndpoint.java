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
package com.marklogic.client.dataservices;

import java.io.InputStream;
import java.util.stream.Stream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.InputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * Provides an interface for calling an endpoint that takes input data structures.
 */
public interface InputEndpoint extends IOEndpoint {
	/**
     * Constructs an instance of the InputEndpoint interface.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @return  the InputEndpoint instance for calling the endpoint.
     */
	static InputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
		return new InputEndpointImpl(client, apiDecl);
	}
	/**
     * Makes one call to the endpoint for the instance
     * @param workUnit  the definition of a unit of work
     * @param input  the stream given to the endpoint as input.
     */
	void call(InputStream workUnit, Stream<InputStream> input);
	/**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the input endpoint
     */
	BulkInputCaller bulkCaller();

	/**
     * Provides an interface for completing a unit of work
     * by repeated calls to the input endpoint.
     */
	interface BulkInputCaller extends IOEndpoint.BulkIOEndpointCaller {
		/**
		 * Accepts the input for the input endpoint.
		 * @param input  the stream given to the endpoint as input.
		 */
		void accept(InputStream input);
	}
}
