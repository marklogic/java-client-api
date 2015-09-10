/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.semantics;

import java.util.Iterator;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.Capability;

/**
 * A manager for CRUD operations on semantic graphs.
 */
public interface GraphManager {
    public static String DEFAULT_GRAPH = "com.marklogic.client.semantics.GraphManager.DEFAULT_GRAPH";

    public Iterator<String> listGraphUris();

    // use read(DEFAULT_GRAPH, handle)
    // or read(null, handle)
    public <T extends TriplesReadHandle> T read(String uri, T handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public <T extends TriplesReadHandle> T read(String uri, T handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public <T> T readAs(String uri, Class<T> as)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public <T> T readAs(String uri, Class<T> as, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public GraphPermissions getPermissions(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public GraphPermissions getPermissions(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deletePermissions(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deletePermissions(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void writePermissions(String uri, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void writePermissions(String uri, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergePermissions(String uri, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergePermissions(String uri, GraphPermissions permissions, Transaction transcation)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** Create a GraphPermissions builder object with the specified role and capabilities.
     * @param role the name of the role receiving these capabilities
     * @param capabilities the capabilities (read, update, or execute) granted to this role
     * @return the new GraphPermissions object with these capabilities set
     */
    public GraphPermissions permission(String role, Capability... capabilities);

    public void merge(String uri, TriplesWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void merge(String uri, TriplesWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public void mergeAs(String uri, Object graphData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeAs(String uri, Object graphData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public void write(String uri, TriplesWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void write(String uri, TriplesWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public void writeAs(String uri, Object graphData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void writeAs(String uri, Object graphData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void writeAs(String uri, Object graphData, GraphPermissions permissions)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void writeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public void delete(String uri)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void delete(String uri, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public <T extends TriplesReadHandle> T things(T handle, String... iris)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public <T> T thingsAs(Class<T> as, String... iris)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    // quads methods - no permissions or read
    public void mergeGraphs(QuadsWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeGraphs(QuadsWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeGraphsAs(Object quadsData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void mergeGraphsAs(Object quadsData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void replaceGraphs(QuadsWriteHandle handle)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void replaceGraphs(QuadsWriteHandle handle, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void replaceGraphsAs(Object quadsData)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void replaceGraphsAs(Object quadsData, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deleteGraphs()
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deleteGraphs(Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public String getDefaultMimetype();
    public void setDefaultMimetype(String mimetype);
}

