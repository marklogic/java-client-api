/*
 * Copyright (c) 2021 MarkLogic Corporation
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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.impl.HandleProvider;
import com.marklogic.client.dataservices.impl.InputEndpointImpl;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * Provides an interface for calling an endpoint that takes input data structures.
 */
@Deprecated
public interface InputEndpoint extends InputCaller<InputStream> {
	/**
     * Constructs an instance of the InputEndpoint interface.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @return  the InputEndpoint instance for calling the endpoint.
     */
	@Deprecated
	static InputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
		final class EndpointLocal<O> extends InputEndpointImpl<InputStream,O>
				implements InputEndpoint {
			private EndpointLocal(DatabaseClient client, JSONWriteHandle apiDecl) {
				super(client, apiDecl, new HandleProvider.ContentHandleProvider<>(new InputStreamHandle(), null));
			}
			public InputEndpoint.BulkInputCaller bulkCaller() {
				return new BulkLocal(this);
			}
			class BulkLocal extends InputEndpointImpl.BulkInputCallerImpl<InputStream,O>
					implements InputEndpoint.BulkInputCaller {
				private BulkLocal(EndpointLocal<O> endpoint) {
					super(endpoint);
				}
			}
		}
		return new EndpointLocal(client, apiDecl);
	}

	/**
	 * Makes one call to the endpoint for the instance
	 * @param endpointState  the current mutable state of the endpoint (which must be null if not accepted by the endpoint)
	 * @param session  the identifier for the server cache of the endpoint (which must be null if not accepted by the endpoint)
	 * @param workUnit  the definition of a unit of work (which must be null if not accepted by the endpoint)
	 * @param input  the request data sent to the endpoint
	 * @return the endpoint state for the next call, if returned by the endpoint, or null
	 */
	@Deprecated
	InputStream call(InputStream endpointState, SessionState session, InputStream workUnit, InputStream[] input);

	/**
	 * Constructs an instance of a bulk caller, which completes
	 * a unit of work by repeated calls to the endpoint.
	 * @return  the bulk caller for the input endpoint
	 */
	@Override
	@Deprecated
	BulkInputCaller bulkCaller();

	/**
     * Provides an interface for completing a unit of work
     * by repeated calls to the input endpoint.
     */
	@Deprecated
	interface BulkInputCaller extends InputCaller.BulkInputCaller<InputStream> {
	}
}
