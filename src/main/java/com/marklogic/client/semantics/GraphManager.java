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

import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.Capability;

/**
 * A manager for CRUD operations on semantic graphs.
 */
// TODO: Are we ok without the generics?
public interface GraphManager {
// TODO: what is the value?
    public static String DEFAULT_GRAPH = "http...";
// TODO: what is the value?
    public static String MULTIPLE_GRAPHS = "http...";

    public Iterator<String> listGraphUris();

    // use read(DEFAULT_GRAPH, handle)
    // or read(null, handle)
    public <T extends TriplesReadHandle> T read(String uri, T handle);
    public <T extends TriplesReadHandle> T read(String uri, T handle, Transaction transaction);

    public <T> T readAs(String uri, Class<T> as);
    public <T> T readAs(String uri, Class<T> as, Transaction transaction);

    public GraphPermissions getPermissions(String uri);
    public GraphPermissions getPermissions(String uri, Transaction transaction);
    public void deletePermissions(String uri, GraphPermissions permissions);
    public void deletePermissions(String uri, GraphPermissions permissions, Transaction transaction);
    public void writePermissions(String uri, GraphPermissions permissions);
    public void writePermissions(String uri, GraphPermissions permissions, Transaction transaction);
    public void mergePermissions(String uri, GraphPermissions permissions);
    public void mergePermissions(String uri, GraphPermissions permissions, Transaction transcation);

    /** Create a GraphPermissions builder object with the specified role and capabilities.
     * @param role the name of the role receiving these capabilities
     * @param capabilities the capabilities (read, update, or execute) granted to this role
     * @return the new GraphPermissions object with these capabilities set
     */
    public GraphPermissions permission(String role, Capability... capabilities);

    public void merge(String uri, TriplesWriteHandle handle);
    public void merge(String uri, TriplesWriteHandle handle, Transaction transaction);
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions);
    public void merge(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction);

    public void mergeAs(String uri, Object graphData);
    public void mergeAs(String uri, Object graphData, Transaction transaction);
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions);
    public void mergeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction);

    public void write(String uri, TriplesWriteHandle handle);
    public void write(String uri, TriplesWriteHandle handle, Transaction transaction);
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions);
    public void write(String uri, TriplesWriteHandle handle, GraphPermissions permissions, Transaction transaction);

    public void writeAs(String uri, Object graphData);
    public void writeAs(String uri, Object graphData, Transaction transaction);
    public void writeAs(String uri, Object graphData, GraphPermissions permissions);
    public void writeAs(String uri, Object graphData, GraphPermissions permissions, Transaction transaction);

    public void delete(String uri);
    public void delete(String uri, Transaction transaction);

    public <T extends TriplesReadHandle> T things(String[] iris, T handle);
    public <T> T thingsAs(String[] iris, Class<T> as);

    // quads methods - no permissions transactions, or read
    public void mergeGraphs(QuadsWriteHandle handle);
    public void mergeGraphsAs(Object quadsData);
    public void replaceGraphs(QuadsWriteHandle handle);
    public void replaceGraphsAs(Object quadsData);
    // TODO: do we want this?
    public void deleteGraphs();
}

