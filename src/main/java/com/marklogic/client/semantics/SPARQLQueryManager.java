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

import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.SPARQLResultsReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.Capability;

public interface SPARQLQueryManager  {
    public SPARQLQueryDefinition newQueryDefinition();
    public SPARQLQueryDefinition newQueryDefinition(String sparql);
    public SPARQLQueryDefinition newQueryDefinition(TextWriteHandle sparql);

    public <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle);
    public <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, Transaction tx);
    public <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, long start);
    public <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, long start, Transaction tx);
    public long getPageLength();
    public void setPageLength(long pageLength);

    public <T extends TriplesReadHandle> T executeConstruct(SPARQLQueryDefinition qdef, T triplesReadHandle);
    public <T extends TriplesReadHandle> T executeConstruct(SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx);
    public <T extends TriplesReadHandle> T executeDescribe(SPARQLQueryDefinition qdef, T triplesReadHandle);
    public <T extends TriplesReadHandle> T executeDescribe(SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx);
    public Boolean executeAsk(SPARQLQueryDefinition qdef);
    public Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx);
    public void executeUpdate(SPARQLQueryDefinition qdef);
    public void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx);

    /** Create a GraphPermissions builder object with the specified role and capabilities.
     * @param role the name of the role receiving these capabilities
     * @param capabilities the capabilities (read, update, or execute) granted to this role
     * @return the new GraphPermissions object with these capabilities set
     */
    public GraphPermissions permission(String role, Capability... capabilities);
}
