/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawStructuredQueryDefinition provides access to a structured query
 * in a JSON or XML representation.
 */
public interface RawStructuredQueryDefinition extends RawQueryDefinition {
  /**
   * Specifies the handle for the JSON or XML representation
   * of a structured query and returns the query definition.
   * @param handle	the JSON or XML handle.
   * @return	the query definition.
   */
  RawStructuredQueryDefinition withHandle(StructureWriteHandle handle);

  /**
   * Returns the structured query definition as a serialized XML string.
   *
   * @return The serialized definition.
   */
  String serialize();

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
  RawStructuredQueryDefinition withCriteria(String criteria);
}
