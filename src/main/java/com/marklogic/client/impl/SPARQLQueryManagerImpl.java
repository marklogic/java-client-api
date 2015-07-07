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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.marklogic.client.Transaction;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.SPARQLReadHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import com.marklogic.client.semantics.SPARQLTuple;
import com.marklogic.client.semantics.SPARQLTupleResults;
import com.marklogic.client.util.RequestParameters;

public class SPARQLQueryManagerImpl extends AbstractLoggingManager implements SPARQLQueryManager {
    private RESTServices services;

    public SPARQLQueryManagerImpl(RESTServices services) {
        super();
        this.services = services;
    }

    @Override
    public SPARQLQueryDefinition newQueryDefinition(String sparql) {
        return new SPARQLQueryDefinitionImpl(sparql);
    }

    @Override
    public <T extends SPARQLReadHandle> T executeQuery(
            SPARQLQueryDefinition qdef, T handle) {
        return executeQueryImpl(qdef, handle, null, false);
    }

    @Override
    public <T extends SPARQLReadHandle> T executeQuery(
            SPARQLQueryDefinition qdef, T handle, Transaction tx) {
        return executeQueryImpl(qdef, handle, tx, false);
    }

    private <T extends AbstractReadHandle> T executeQueryImpl(
            SPARQLQueryDefinition qdef, T handle, Transaction tx, boolean isUpdate) {
        return executeQueryImpl(qdef, handle, -1, -1, tx, isUpdate);
   }

    private <T extends AbstractReadHandle> T executeQueryImpl(
            SPARQLQueryDefinition qdef, T handle, long start, long pageLength, Transaction tx, boolean isUpdate) {
        if ( qdef == null )   throw new IllegalArgumentException("qdef cannot be null");
        if ( handle == null ) throw new IllegalArgumentException("handle cannot be null");
        return services.executeSparql(requestLogger, qdef, handle, start, pageLength, tx, isUpdate);
    }

    @Override
    public SPARQLTupleResults executeSelect(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        SPARQLTupleResults output = new SPARQLTupleResultsImpl().withFormat(Format.JSON); 
        return executeQueryImpl(qdef, output, null, false);
    }

    @Override
    public SPARQLTupleResults executeSelect(SPARQLQueryDefinition qdef,
            long start, long pageLength) {
        return executeSelect(qdef, start, pageLength, null);
    }

    @Override
    public SPARQLTupleResults executeSelect(SPARQLQueryDefinition qdef,
            long start, long pageLength, Transaction tx) {
        // TODO Auto-generated method stub
        SPARQLTupleResults output = new SPARQLTupleResultsImpl().withFormat(Format.JSON); 
        return executeQueryImpl(qdef, output, start, pageLength, tx, false);
    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, null, false);
    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, tx, false);
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, null, false);
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, tx, false);
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        return Boolean.valueOf(
            executeQueryImpl(qdef, new StringHandle().withFormat(Format.JSON), null, false).get()
        );
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        return Boolean.valueOf(
            executeQueryImpl(qdef, new StringHandle().withFormat(Format.JSON), tx, false).get()
        );
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), null, true);
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), tx, true);
    }

    @Override
    public GraphPermissions permission(String role, Capability... capabilities) {
        // TODO Auto-generated method stub
        return new GraphPermissionsImpl(role, capabilities);
    }
}
