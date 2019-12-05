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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Provides an interface for calling an endpoint that returns output data structures.
 */
public interface OutputEndpoint extends IOEndpoint {
    /**
     * Constructs an instance of the OutputEndpoint interface.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @return  the OutputEndpoint instance for calling the endpoint.
     */
    static OutputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
        return new OutputEndpointImpl(client, apiDecl);
    }

    /**
     * Makes one call to the endpoint for the instance
     * @param endpointState  the current mutable state of the endpoint (which must be null if not accepted by the endpoint)
     * @param session  the identifier for the server cache of the endpoint (which must be null if not accepted by the endpoint)
     * @param workUnit  the definition of a unit of work (which must be null if not accepted by the endpoint)
     * @return  the endpoint state if produced by the endpoint followed by the response data from the endpoint
     */
    InputStream[] call(InputStream endpointState, SessionState session, InputStream workUnit);

    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the output endpoint
     */
    BulkOutputCaller bulkCaller();

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to the output endpoint.
     */
    interface BulkOutputCaller extends IOEndpoint.BulkIOEndpointCaller {
        /**
         * Specifies the function to call on receiving output from the endpoint.
         * @param listener a function for processing the endpoint output
         */
        void setOutputListener(Consumer<InputStream> listener);
        /**
         * Provides synchronous access to output.
         * @return the response from the endpoint.
         */
        InputStream[] next();
    }
}
