/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphPermissions;

public class GraphPermissionsImpl extends HashMap<String, Set<Capability>> implements GraphPermissions {
  @Override
  public GraphPermissions permission(String role, Capability... capabilities) {
    if ( capabilities == null ) throw new IllegalArgumentException("capabilities cannot be null");
    if ( this.get(role) == null ) {
      this.put(role, new HashSet<>(Arrays.asList(capabilities)) );
    } else {
      this.get(role).addAll(Arrays.asList(capabilities));
    }
    return this;
  }
}
