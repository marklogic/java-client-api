/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.HandleProvider;
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

import java.util.function.Consumer;

/**
 * Provides an interface for calling an endpoint that returns output data structures.
 *
 * @param <O> The representation for document output
 * @see <a href="https://github.com/marklogic/java-client-api/wiki/Data-Services-for-IO#output-endpoints">Output endpoints</a>
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
        return new OutputEndpointImpl(client, apiDecl, new HandleProvider.ContentHandleProvider<>(null, outputHandle));
    }

    /**
     * Constructs an instance of the OutputCaller interface.
     * This factory is useful primarily for parameters or return values of the anyDocument type.
     * @param client the database client to use for making calls
     * @param apiDecl the JSON api declaration specifying how to call the endpoint
     * @param outputHandle the handles that provides the output content (such as BytesHandle)
     * @param <OC> the content type of the output handle
     * @param <OR> the type for the data received by the output handle
     * @param <O> the output handle
     * @return the InputOutputCaller instance for calling the endpoint.
     */
    static <OC,OR,O extends BufferableContentHandle<OC,OR>> OutputCaller<O> onHandles(
            DatabaseClient client, JSONWriteHandle apiDecl, O outputHandle
    ) {
        return new OutputEndpointImpl(client, apiDecl, new HandleProvider.DirectHandleProvider<>(null, outputHandle));
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
     *
     * @param <O> The representation for document output
     * @see <a href="https://github.com/marklogic/java-client-api/wiki/Bulk-Data-Services">Bulk Data Services</a>
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
