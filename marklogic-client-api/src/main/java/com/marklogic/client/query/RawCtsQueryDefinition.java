/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawCtsQueryDefinition allows you to create a query with a serialized cts
 * query in a JSON or XML representation.
 */
public interface RawCtsQueryDefinition extends RawQueryDefinition, ValueQueryDefinition {
  /**
   * Returns the query criteria, that is the query string.
   * @return The query string.
   */
  String getCriteria();

  /**
   * Sets the query criteria as a query string.
   * @param criteria The query string.
   */
  void setCriteria(String criteria);

  /**
   * Sets the query criteria as a query string and returns the query
   * definition as a fluent convenience.
   * @param criteria The query string.
   * @return	This query definition.
   */
  RawCtsQueryDefinition withCriteria(String criteria);

  /**
   * Returns the handle for the JSON or XML representation of the query.
   * @return	the JSON or XML handle.
   */
  CtsQueryWriteHandle getHandle();

  /**
   * Specifies the handle for the JSON or XML representation
   * of a combined query and returns the query definition.
   * @param handle	the JSON or XML handle.
   * @return	the query definition.
   */
  RawCtsQueryDefinition withHandle(StructureWriteHandle handle);
}
