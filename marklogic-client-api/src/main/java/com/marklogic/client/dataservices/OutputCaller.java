/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.util.function.Consumer;

/**
 * Provides an interface for calling an endpoint that returns output data structures.
 */
public interface OutputCaller<O> extends IOEndpoint {
    /**
     * Constructs an instance of the OutputCaller interface.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @param outputHandle the handle for the representation of the output content (such as BytesHandle)
     * @param <O>  the output content representation (such as byte[])
     * @return  the OutputCaller instance for calling the endpoint.
     */
    static <O> OutputCaller<O> on(
            DatabaseClient client, JSONWriteHandle apiDecl, BufferableContentHandle<O,?> outputHandle
    ) {
        return new OutputEndpointImpl(client, apiDecl, outputHandle);
    }

    /**
     * Makes one call to an endpoint that doesn't take endpoint constants, endpoint state, or a session.
     * @return  the response data from the endpoint
     */
    O[] call();
    /**
     * Makes one call to the endpoint for the instance and sets the endpoint state in the Call Context.
     * @param callContext the context consisting of the optional endpointConstants, endpointState, and session
     * @return  the response data from the endpoint
     */
    O[] call(CallContext callContext);

    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the output endpoint
     */
    BulkOutputCaller<O> bulkCaller();
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in current thread.
     * @param callContext the context consisting of the optional endpointConstants, endpointState, and session
     * @return  the bulk caller for the output endpoint
     */
    BulkOutputCaller<O> bulkCaller(CallContext callContext);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
     * @param callContexts the collection of callContexts
     * @return  the bulk caller for the output endpoint
     */
    BulkOutputCaller<O> bulkCaller(CallContext[] callContexts);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
     * @param callContexts the collection of callContexts
     * @param threadCount the number of threads
     * @return  the bulk caller for the output endpoint
     */
    BulkOutputCaller<O> bulkCaller(CallContext[] callContexts, int threadCount);

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to the output endpoint.
     */
    interface BulkOutputCaller<O> extends BulkIOEndpointCaller {
        /**
         * Specifies the function to call on receiving output from the endpoint.
         * @param listener a function for processing the endpoint output
         */
        void setOutputListener(Consumer<O> listener);
        /**
         * Provides synchronous access to output. Used only while executing calls in the current thread instead of
         * worker threads and when an output listener is not used.
         * @return the response from the endpoint.
         */
        O[] next();

        void setErrorListener(ErrorListener errorListener);

        interface ErrorListener {
            ErrorDisposition processError(
                    int retryCount, Throwable throwable, CallContext callContext);
        }
    }
}
