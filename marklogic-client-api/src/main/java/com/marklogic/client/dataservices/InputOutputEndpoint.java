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
import com.marklogic.client.dataservices.impl.InputOutputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Provides an interface for calling an endpoint that takes input data structures and
 * returns output data structures.
 */
public interface InputOutputEndpoint {
    static InputOutputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
        return new InputOutputEndpointImpl(client, apiDecl);
    }

    /**
     * Makes one call to the endpoint for the instance
     * @param workUnit  the definition of a unit of work
     * @param input  the stream given to the endpoint as input.
     * @return  the response from the endpoint
     */
    Stream<InputStream> call(InputStream workUnit, Stream<InputStream> input);

    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the input-output endpoint
     */
    BulkInputOutputCaller bulkCaller();

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to the input-output endpoint.
     */
    interface BulkInputOutputCaller extends IOEndpoint.BulkIOEndpointCaller {
        /**
         * Specifies the function to call on receiving output from the endpoint.
         * @param outputConsumer a function for processing the endpoint output
         */
        void forEachOutput(Consumer<InputStream> outputConsumer);
        /**
         * Accepts the input for the input endpoint.
         * @param input  the stream given to the endpoint as input.
         */
        void accept(InputStream input);
    }
}
