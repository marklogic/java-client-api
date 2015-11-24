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

import java.util.Map;
import java.util.Set;

import com.marklogic.client.semantics.Capability;

/**
 * <p>A Map of permissions for a graph, where the keys are roles and the values
 * are the Set of capabilities available to that role (one of READ, UPDATE, or
 * EXECUTE).  See usage examples in javadocs for {@link GraphManager} and
 * {@link SPARQLQueryManager}.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics Developer's Guide</a>
 */
public interface GraphPermissions extends Map<String, Set<Capability>> {
    /** Add the specified role and capabilities to this GraphPermissions object.
     *
     * @param role the name of the role receiving these capabilities
     * @param capabilities the capabilities (READ, UPDATE, or EXECUTE) granted to this role
     *
     * @return the new GraphPermissions object with these permissions added
     */
    public GraphPermissions permission(String role, Capability... capabilities);
}
