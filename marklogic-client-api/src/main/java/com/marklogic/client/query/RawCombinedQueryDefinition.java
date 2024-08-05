/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawCombinedQueryDefinition provides access to a combined query
 * in a JSON or XML representation.
 */
public interface RawCombinedQueryDefinition extends RawQueryDefinition, ValueQueryDefinition {
  /**
   * Specifies the handle for the JSON or XML representation
   * of a combined query and returns the query definition.
   * @param handle	the JSON or XML handle.
   * @return	the query definition.
   */
  RawCombinedQueryDefinition withHandle(StructureWriteHandle handle);
}
