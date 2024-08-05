/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.ExecEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * Provides an interface for calling an endpoint that doesn't take
 * input data structures or return output data structures.
 *
 * @see <a href="https://github.com/marklogic/java-client-api/wiki/Data-Services-for-IO#exec-endpoints">Exec endpoints</a>
 */
public interface ExecCaller extends IOEndpoint {
    /**
     * Constructs an instance of the ExecCaller interface
     * for calling the specified endpoint.
     * @param client  the database client to use for making calls
     * @param apiDecl  the JSON api declaration specifying how to call the endpoint
     * @return  the ExecCaller instance for calling the endpoint
     */
    static ExecCaller on(DatabaseClient client, JSONWriteHandle apiDecl) {
        return new ExecEndpointImpl(client, apiDecl);
    }

    /**
     * Makes one call to an endpoint that doesn't take endpoint constants, endpoint state, or a session.
     */
    void call();
    /**
     * Makes one call to the endpoint for the instance and sets the endpoint state in the Call Context.
     * @param callContext the context consisting of the optional endpointConstants, endpointState, and session
     */
    void call(CallContext callContext);

    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint.
     * @return  the bulk caller for the endpoint
     */
    BulkExecCaller bulkCaller();
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in the current thread.
     * @param callContext the context consisting of the optional endpointConstants, endpointState, and session
     * @return  the bulk caller for the endpoint
     */
    BulkExecCaller bulkCaller(CallContext callContext);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
     * @param callContexts array of callContexts
     * @return  the bulk caller for the endpoint
     */
    BulkExecCaller bulkCaller(CallContext[] callContexts);
    /**
     * Constructs an instance of a bulk caller, which completes
     * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
     * @param callContexts array of callContexts
     * @param threadCount the number of threads
     * @return  the bulk caller for the endpoint
     */
    BulkExecCaller bulkCaller(CallContext[] callContexts, int threadCount);

    /**
     * Provides an interface for completing a unit of work
     * by repeated calls to an endpoint that doesn't take input
     * data structure or return output data structures.
     *
     * Call awaitCompletion() to start making calls.
     *
     * @see <a href="https://github.com/marklogic/java-client-api/wiki/Bulk-Data-Services">Bulk Data Services</a>
     */
    interface BulkExecCaller extends BulkIOEndpointCaller {
        void setErrorListener(ErrorListener errorListener);

        interface ErrorListener {
            ErrorDisposition processError(
                    int retryCount, Throwable throwable, CallContext callContext);
        }
    }
}
