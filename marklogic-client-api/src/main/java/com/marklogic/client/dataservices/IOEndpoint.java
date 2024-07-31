/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.SessionState;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.marker.BufferableHandle;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Base interface providing the methods common to all endpoints.
 */
public interface IOEndpoint {
    /**
     * Identifies the path of the endpoint in the modules database on the server.
     * @return the path
     */
    String getEndpointPath();

    /**
     * Identifies whether the endpoint accepts and returns a data structure
     * that provides a snapshot of the mutable state of the endpoint.
     * @return  whether the endpoint takes a state
     */
    boolean allowsEndpointState();
    /**
     * Identifies whether the endpoint accepts a session identifier that
     * the endpoint can use to access a session cache on the server.
     * @return  whether the endpoint takes a session
     */
    boolean allowsSession();
    /**
     * Identifies whether the endpoint accepts a data structure that defines
     * constant input to provide to the endpoint.
     * @return  whether the endpoint takes endpoint constants
     */
    boolean allowsEndpointConstants();
    /**
     * Identifies whether the endpoint accepts one or more input data structures
     * to work on.
     * @return  whether the endpoint takes input data structures
     */
    boolean allowsInput();

    /**
     * Generates an identifier for an endpoint to use when accessing a session cache
     * on the server. The identifier can be reused for multiple calls.
     * @return  a new identifier for a session cache on the server
     */
    SessionState newSessionState();

    /**
     * Base interface providing the methods common to all bulk endpoint callers.
     */
    interface BulkIOEndpointCaller {
        /**
         * Waits for the bulk calling to complete, first starting the calls if appropriate.
         */
        void awaitCompletion();

        /**
         * Interrupts the iterative bulk calls prior to completion.
         */
        void interrupt();

        /**
         * Indicates the disposition of an error.
         */
        enum ErrorDisposition {
            RETRY, SKIP_CALL, STOP_ALL_CALLS;
        }
    }
    /**
     * Constructs a context for the calls that specifying the optional endpoint constants,
     * endpoint state, and session.
     * @return  the context for calls
     */
    CallContext newCallContext();

    /**
     * Provides the optional endpoint constants, endpoint state, and session as a context
     * for calls to the server.
     */
    interface CallContext {
        /**
         * Gets the current snapshot of the mutable state of the endpoint.
         * @return  the data structure with the endpoint state
         */
        BytesHandle getEndpointState();
        /**
         * Initializes the stateful properties of the endpoint prior to the first call to the endpoint.
         * The endpoint returns the state as the first value in the response.
         * The caller resends the modified state on the next call to the endpoint.
         * @param endpointState the data structure for the endpoint state in a representation supported by a bufferable content handle
         * @return the callContext
         */
        CallContext withEndpointState(BufferableHandle endpointState);
        /**
         * Initializes the stateful properties of the endpoint prior to the first call to the endpoint.
         * The endpoint returns the state as the first value in the response.
         * The caller resends the modified state on the next call to the endpoint.
         * @param endpointState the data structure for the endpoint state in a representation supported by a bufferable content handle
         * @return the callContext
         */
        CallContext withEndpointStateAs(Object endpointState);

        /**
         * Gets the definition for constant inputs to the endpoint.
         * @return  the data structure for the constants
         */
        BytesHandle getEndpointConstants();
        /**
         * Sets the constant inputs prior to the first call to the endpoint.
         * Examples of such constants might include the query when paging
         * over a result set or the provenance when ingesting records.
         * @param constants the data structure in a representation supported by a bufferable content handle
         * @return the callContext
         */
        CallContext withEndpointConstants(BufferableHandle constants);
        /**
         * Sets the constant inputs prior to the first call to the endpoint.
         * Examples of such constants might include the query when paging
         * over a result set or the provenance when ingesting records.
         * @param constants the data structure in a representation supported by a bufferable content handle
         * @return  the callContext
         */
        CallContext withEndpointConstantsAs(Object constants);

        /**
         * Returns an identifier for an endpoint to use when accessing a session cache on the server.
         * @return the endpoint identifier
         */
        SessionState getSessionState();
        /**
         * Sets an identifier for an endpoint to use when accessing a session cache on the server.
         * @param sessionState the identifier for the server cache of the endpoint
         * @return the callContext
         */
        CallContext withSessionState(SessionState sessionState);
    }
}
