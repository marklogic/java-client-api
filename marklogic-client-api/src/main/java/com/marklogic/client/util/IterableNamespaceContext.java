/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.util;

import java.util.Collection;

import javax.xml.namespace.NamespaceContext;

/**
 * InterableNamespaceContext extends NamespaceContext to support
 * introspection of unknown namespace bindings.
 */
public interface IterableNamespaceContext extends NamespaceContext {
  /**
   * Returns all bound prefixes.
   * @return	the set of prefixes
   */
  Collection<String> getAllPrefixes();
}
