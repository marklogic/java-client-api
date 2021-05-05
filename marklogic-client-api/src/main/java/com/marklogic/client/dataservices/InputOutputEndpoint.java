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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.impl.InputOutputEndpointImpl;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.InputStream;

/**
 * Provides an interface for calling an endpoint that takes input data structures and
 * returns output data structures.
 */
@Deprecated
public interface InputOutputEndpoint extends InputOutputCaller<InputStream,InputStream> {
    /**
     * Constructs an instance of the InputOutputEndpoint interface.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @return  the InputOutputEndpoint instance for calling the endpoint.
     */
    @Deprecated
    static InputOutputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
        final class EndpointLocal extends InputOutputEndpointImpl<InputStream,InputStream>
                implements InputOutputEndpoint {
            private EndpointLocal(DatabaseClient client, JSONWriteHandle apiDecl) {
                super(client, apiDecl, false, new InputStreamHandle(), new InputStreamHandle());
            }
            public InputOutputEndpoint.BulkInputOutputCaller bulkCaller() {
                return new BulkLocal(this);
            }
            class BulkLocal extends InputOutputEndpointImpl.BulkInputOutputCallerImpl<InputStream,InputStream>
                    implements InputOutputEndpoint.BulkInputOutputCaller {
                private BulkLocal(EndpointLocal endpoint) {
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
     * @return  the endpoint state if produced by the endpoint followed by the response data from the endpoint
     */
    @Deprecated
    InputStream[] call(InputStream endpointState, SessionState session, InputStream workUnit, InputStream[] input);

    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the input-output endpoint
     */
    @Deprecated
    @Override
    BulkInputOutputCaller bulkCaller();

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to the input-output endpoint.
     */
    @Deprecated
    interface BulkInputOutputCaller extends InputOutputCaller.BulkInputOutputCaller<InputStream,InputStream> {
    }
}
