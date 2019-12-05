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

import com.marklogic.client.SessionState;
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
     * the unit of work to be done by the endpoint.
     * @return  whether the endpoint takes a work unit
     */
    boolean allowsWorkUnit();
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
         * Gets the current snapshot of the mutable endpoint state.
         * @return  the data structure with the endpoint state
         */
        InputStream getEndpointState();
        /**
         * Initializes the endpoint state, typically prior to the first call.
         * @param endpointState the data structure for the endpoint state as a byte[] array
         */
        void setEndpointState(byte[] endpointState);
        /**
         * Initializes the endpoint state, typically prior to the first call.
         * @param endpointState the data structure for the endpoint state as an InputStream
         */
        void setEndpointState(InputStream endpointState);
        /**
         * Initializes the endpoint state, typically prior to the first call.
         * @param endpointState the data structure for the endpoint state as a bufferable handle
         */
        void setEndpointState(BufferableHandle endpointState);

        /**
         * Gets the definition for the unit of  work to be done by the endpoint.
         * @return  the data structure for the unit of work
         */
        InputStream getWorkUnit();
        /**
         * Initializes the defintion of the work unit prior to the first call.
         * @param workUnit the data structure for the work unit as a byte[] array
         */
        void setWorkUnit(byte[] workUnit);
        /**
         * Initializes the defintion of the work unit prior to the first call.
         * @param workUnit the data structure for the work unit as an InputStream
         */
        void setWorkUnit(InputStream workUnit);
        /**
         * Initializes the defintion of the work unit prior to the first call.
         * @param workUnit the data structure for the work unit as a bufferable handle
         */
        void setWorkUnit(BufferableHandle workUnit);

        /**
         * Waits for the bulk calling to complete, first starting the calls if appropriate.
         */
        void awaitCompletion();

        /**
         * Interrupts the iterative bulk calls prior to completion.
         */
        void interrupt();
    }
}
