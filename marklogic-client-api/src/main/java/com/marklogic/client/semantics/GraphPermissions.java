/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

import java.util.Map;
import java.util.Set;

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
  GraphPermissions permission(String role, Capability... capabilities);
}
