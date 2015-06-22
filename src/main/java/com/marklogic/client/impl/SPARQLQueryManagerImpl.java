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

import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.SPARQLReadHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import com.marklogic.client.semantics.SPARQLTuplesResult;

public class SPARQLQueryManagerImpl implements SPARQLQueryManager {

    @Override
    public SPARQLQueryDefinition newQueryDefinition(String sparql) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends SPARQLReadHandle> T executeQuery(
            SPARQLQueryDefinition qdef, T handle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends SPARQLReadHandle> T executeQuery(
            SPARQLQueryDefinition qdef, T handle, Transaction tx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SPARQLTuplesResult executeSelect(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SPARQLTuplesResult executeSelect(SPARQLQueryDefinition qdef,
            long start, long pageLength, Transaction tx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends TriplesReadHandle> T executeConstruct(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends TriplesReadHandle> T executeDescribe(
            SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx) {
        // TODO Auto-generated method stub
        
    }
}

