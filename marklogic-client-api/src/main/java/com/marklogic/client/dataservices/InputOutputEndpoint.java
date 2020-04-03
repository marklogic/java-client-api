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
import com.marklogic.client.dataservices.impl.InputOutputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Provides an interface for calling an endpoint that takes input data structures and
 * returns output data structures.
 */
public interface InputOutputEndpoint extends IOEndpoint {
    static InputOutputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
        return new InputOutputEndpointImpl(client, apiDecl);
    }

    /**
     * Makes one call to an endpoint that doesn't take an endpoint state, session, or work unit.
     * @param input  the request data sent to the endpoint
     * @return  the response data from the endpoint
     */
    InputStream[] call(InputStream[] input);
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
    BulkInputOutputCaller bulkCaller();

    /**
     * Makes one call to an endpoint that doesn't take an endpoint state, session, or work unit.
     * @param callContext  the collection of endpointState, sessionState and workUnit
     * @param input  the request data sent to the endpoint
     * @return  the response data from the endpoint
     */
    InputStream[] call(CallContext callContext, InputStream[] input);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @param callContext  the collection of endpointState, sessionState and workUnit
     * @return  the bulk caller for the input-output endpoint
     */
    BulkInputOutputCaller bulkCaller(CallContext callContext);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @param callContexts  the collection of callContexts
     * @return  the bulk caller for the input-output endpoint
     */
    BulkInputOutputCaller bulkCaller(CallContext[] callContexts);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @param callContexts  the collection of callContexts
     * @param threadCount the number of threads
     * @return  the bulk caller for the input-output endpoint
     */
    BulkInputOutputCaller bulkCaller(CallContext[] callContexts, int threadCount);

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to the input-output endpoint.
     */
    interface BulkInputOutputCaller extends IOEndpoint.BulkIOEndpointCaller {
        /**
         * Specifies the function to call on receiving output from the endpoint.
         * @param listener a function for processing the endpoint output
         */
        void setOutputListener(Consumer<InputStream> listener);
        /**
         * Accepts an input item for the endpoint.  Items are queued
         * and submitted to the endpoint in batches.
         * @param input  one input item
         */
        void accept(InputStream input);
        /**
         * Accepts multiple input items for the endpoint.  Items are queued
         * and submitted to the endpoint in batches.
         * @param input  multiple input items.
         */
        void acceptAll(InputStream[] input);

        void setErrorListener(ErrorListener errorListener);

        interface ErrorListener {
            IOEndpoint.BulkIOEndpointCaller.ErrorDisposition processError(
                    int retryCount, Throwable throwable, CallContext callContext, InputStream[] input
            );
        }
    }
}
