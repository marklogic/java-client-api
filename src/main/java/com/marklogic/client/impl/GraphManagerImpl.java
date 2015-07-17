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
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;

public class GraphManagerImpl<R extends TriplesReadHandle, W extends TriplesWriteHandle>
    extends AbstractLoggingManager
    implements GraphManager
{
    private RESTServices services;
    private HandleFactoryRegistry handleRegistry;
    private String defaultMimetype;

    public GraphManagerImpl(RESTServices services, HandleFactoryRegistry handleRegistry) {
        super();
        this.services = services;
        this.handleRegistry = handleRegistry;
    }

    @Override
    public Iterator listGraphUris() {
        final int bufferSize = 10;
        final String uriString = services.getGraphUris(requestLogger, new StringHandle()).get();
        String[] uris = uriString.split("\n");
        return Arrays.asList(uris).iterator();
    }

    @Override
    public TriplesReadHandle read(String uri, TriplesReadHandle handle) {
        return read(uri, handle, null);
    }

    @Override
    public TriplesReadHandle read(String uri, TriplesReadHandle handle,
            Transaction transaction) {
        HandleImplementation baseHandle = HandleAccessor.as(handle);
        String mimetype = baseHandle.getMimetype();
        if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
        services.readGraph(requestLogger, uri, handle, transaction);
        baseHandle.setMimetype(mimetype);
        return handle;
    }

    @Override
    public <T> T readAs(String uri, Class<T> clazz) {
        return readAs(uri, clazz, null);
    }

    @Override
    public <T> T readAs(String uri, Class<T> as,
            Transaction transaction) {
        ContentHandle<T> handle = handleRegistry.makeHandle(as);

        if ( ! (handle instanceof TriplesReadHandle) ) {
            throw new IllegalArgumentException("Second arg \"as\" " +
                "is registered by " + handle.getClass() + " which is not a " +
                "TriplesReadHandle so it cannot read content from GraphManager");
        }
        @SuppressWarnings("unchecked")
        TriplesReadHandle triplesHandle = (TriplesReadHandle) handle;
        if (null == read(uri, triplesHandle, transaction)) {
            return null;
        }

        return handle.get();
    }

    @Override
    public GraphPermissions getPermissions(String uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GraphPermissions getPermissions(String uri, Transaction transaction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePermissions(String uri, GraphPermissions permissions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deletePermissions(String uri, GraphPermissions permissions,
            Transaction transaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writePermissions(String uri, GraphPermissions permissions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writePermissions(String uri, GraphPermissions permissions,
            Transaction transaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mergePermissions(String uri, GraphPermissions permissions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mergePermissions(String uri, GraphPermissions permissions,
            Transaction transcation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void merge(String uri, TriplesWriteHandle handle) {
        merge(uri, handle, null, null);
    }

    @Override
    public void merge(String uri, TriplesWriteHandle handle,
            Transaction transaction) {
        merge(uri, handle, null, transaction);
    }

    @Override
    public void merge(String uri, TriplesWriteHandle handle,
            GraphPermissions permissions) {
        merge(uri, handle, permissions, null);
    }

    @Override
    public void merge(String uri, TriplesWriteHandle handle,
            GraphPermissions permissions, Transaction transaction) {
        HandleImplementation baseHandle = HandleAccessor.as(handle);
        String mimetype = baseHandle.getMimetype();
        if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
        services.mergeGraph(requestLogger, uri, handle, permissions, transaction);
        baseHandle.setMimetype(mimetype);
    }

    @Override
    public void mergeAs(String uri, Object graphData) {
        mergeAs(uri, graphData, null, null);
    }

    @Override
    public void mergeAs(String uri, Object graphData, Transaction transaction) {
        mergeAs(uri, graphData, null, transaction);
    }

    @Override
    public void mergeAs(String uri, Object graphData,
            GraphPermissions permissions) {
        mergeAs(uri, graphData, permissions, null);
    }

    @Override
    public void mergeAs(String uri, Object graphData,
            GraphPermissions permissions, Transaction transaction) {
        merge(uri, populateTriplesHandle(graphData), permissions, transaction);
    }

    @Override
    public void write(String uri, TriplesWriteHandle handle) {
        write(uri, handle, null, null);
    }

    @Override
    public void write(String uri, TriplesWriteHandle handle,
            Transaction transaction) {
        write(uri, handle, null, transaction);
    }

    @Override
    public void write(String uri, TriplesWriteHandle handle,
            GraphPermissions permissions) {
        write(uri, handle, permissions, null);
    }

    @Override
    public void write(String uri, TriplesWriteHandle handle,
            GraphPermissions permissions, Transaction transaction) {
        HandleImplementation baseHandle = HandleAccessor.as(handle);
        String mimetype = baseHandle.getMimetype();
        if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
        services.writeGraph(requestLogger, uri, handle, permissions, transaction);
        baseHandle.setMimetype(mimetype);
    }

    @Override
    public void writeAs(String uri, Object graphData) {
        writeAs(uri, graphData, null, null);
    }

    @Override
    public void writeAs(String uri, Object graphData, Transaction transaction) {
        writeAs(uri, graphData, null, transaction);
    }

    @Override
    public void writeAs(String uri, Object graphData,
            GraphPermissions permissions) {
        writeAs(uri, graphData, permissions, null);
    }

    @Override
    public void writeAs(String uri, Object graphData,
            GraphPermissions permissions, Transaction transaction) {
        write(uri, populateTriplesHandle(graphData), permissions, transaction);
    }

    private AbstractWriteHandle populateHandle(Object graphData) {
        if (graphData == null) {
            throw new IllegalArgumentException("no graphData to write");
        }

        Class<?> as = graphData.getClass();

        if (AbstractWriteHandle.class.isAssignableFrom(as)) {
            return (AbstractWriteHandle) graphData;
        } else {
            ContentHandle<?> handle = handleRegistry.makeHandle(as);
            if ( ! (handle instanceof TriplesReadHandle) ) {
                throw new IllegalArgumentException("Arg \"graphData\" " +
                        "is handled by " + handle.getClass() + " which is not a " +
                        "TriplesReadHandle so it cannot write using GraphManager");
            }
            Utilities.setHandleContent(handle, graphData);
            return handle;
        }
    }
    private QuadsWriteHandle populateQuadsHandle(Object graphData) {
        return (QuadsWriteHandle) populateHandle(graphData);
    }
    private TriplesWriteHandle populateTriplesHandle(Object graphData) {
        return (TriplesWriteHandle) populateHandle(graphData);
    }

    @Override
    public void delete(String uri) {
        services.deleteGraph(requestLogger, uri, null);
    }

    @Override
    public void delete(String uri, Transaction transaction) {
        services.deleteGraph(requestLogger, uri, transaction);
    }

    @Override
    public TriplesReadHandle things(String[] iris, TriplesReadHandle handle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object thingsAs(String[] iris, Class clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void mergeGraphs(QuadsWriteHandle handle) {
        HandleImplementation baseHandle = HandleAccessor.as(handle);
        String mimetype = baseHandle.getMimetype();
        if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
        services.mergeGraphs(requestLogger, handle);
        baseHandle.setMimetype(mimetype);
    }

    @Override
    public void mergeGraphsAs(Object quadsData) {
        mergeGraphs( populateQuadsHandle(quadsData) );
    }

    @Override
    public void replaceGraphs(QuadsWriteHandle handle) {
        HandleImplementation baseHandle = HandleAccessor.as(handle);
        String mimetype = baseHandle.getMimetype();
        if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
        services.writeGraphs(requestLogger, handle);
        baseHandle.setMimetype(mimetype);
    }

    @Override
    public void replaceGraphsAs(Object quadsData) {
        replaceGraphs( populateQuadsHandle(quadsData) );
    }

    @Override
    public void deleteGraphs() {
        services.deleteGraphs(requestLogger);
    }

    @Override
    public GraphPermissions permission(String role, Capability... capabilities) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDefaultMimetype() {
        return defaultMimetype;
    }

    public void setDefaultMimetype(String mimetype) {
        this.defaultMimetype = mimetype;
    }
}
