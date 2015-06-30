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
        return executeQueryImpl(qdef, handle, null);
    }

    @Override
    public <T extends SPARQLReadHandle> T executeQuery(
            SPARQLQueryDefinition qdef, T handle, Transaction tx) {
        return executeQueryImpl(qdef, handle, tx);
    }

    private <T extends AbstractReadHandle> T executeQueryImpl(
            SPARQLQueryDefinition qdef, T handle, Transaction tx) {


        if ( qdef == null )   throw new IllegalArgumentException("qdef cannot be null");
        if ( handle == null ) throw new IllegalArgumentException("handle cannot be null");
        if ( qdef instanceof SPARQLQueryDefinitionImpl ) {
            try {
                RequestParameters params = new RequestParameters();
                // TODO: this should be in JerseyServices
                StringBuffer sb = new StringBuffer();
                String sparql = ((SPARQLQueryDefinitionImpl) qdef).getSparql();
                    sb.append("query=" + URLEncoder.encode(sparql, "UTF-8"));
                SPARQLBindings bindings = qdef.getBindings();
                for ( String bindingName : bindings.keySet() ) {
                    String paramName = "bind:" + bindingName;
                    String typeOrLang = "";
                    for ( SPARQLBinding binding : bindings.get(bindingName) ) {
                        if ( binding.getType() != null && ! "".equals(binding.getType()) ) {
                            typeOrLang = ":" + binding.getType();
                        } else if ( binding.getLanguageTag() != null ) {
                            typeOrLang = "@" + binding.getLanguageTag().toLanguageTag();
                        }
                        String value = URLEncoder.encode(binding.getValue(), "UTF-8");
                        params.add(paramName + typeOrLang, value);
                    }
                }
                String formUrlEncodedPayload = sb.toString();
                StringHandle input = new StringHandle(formUrlEncodedPayload)
                    .withMimetype("application/x-www-form-urlencoded");

                // TODO: do we want this default?
                HandleImplementation baseHandle = HandleAccessor.checkHandle(handle, "graphs/sparql");
                if ( baseHandle.getFormat() == Format.JSON ) {
                    baseHandle.setMimetype("application/sparql-results+json");
                }
                return services.postResource(requestLogger, "/graphs/sparql", tx, params, input, handle);
            } catch (UnsupportedEncodingException e) {}
        }
        throw new IllegalStateException("Unknown type of SPARQLQueryDefinition: " +
            qdef.getClass().getName());
    }

    @Override
    public SPARQLTupleResults executeSelect(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        SPARQLTupleResults output = new SPARQLTupleResultsImpl().withFormat(Format.JSON); 
        return executeQueryImpl(qdef, output, null);
    }

    @Override
    public SPARQLTupleResults executeSelect(SPARQLQueryDefinition qdef,
            long start, long pageLength, Transaction tx) {
        // TODO Auto-generated method stub
        SPARQLTupleResults output = new SPARQLTupleResultsImpl().withFormat(Format.JSON); 
        return executeQueryImpl(qdef, output, tx);    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, null);
    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, tx);
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, null);
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return executeQueryImpl(qdef, triplesReadHandle, tx);
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        return Boolean.valueOf(
            executeQuery(qdef, new StringHandle().withFormat(Format.JSON), null).get()
        );
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        return Boolean.valueOf(
            executeQueryImpl(qdef, new StringHandle().withFormat(Format.JSON), tx).get()
        );
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), null);
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), tx);
    }

    @Override
    public GraphPermissions permission(String role, Capability... capabilities) {
        // TODO Auto-generated method stub
        return new GraphPermissionsImpl(role, capabilities);
    }
}
