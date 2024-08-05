/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawQueryDefinition provides access to a query
 * in a JSON or XML representation.
 */
public interface RawQueryDefinition extends QueryDefinition {
  /**
   * Returns the handle for the JSON or XML representation of the query.
   * @return	the JSON or XML handle.
   */
  StructureWriteHandle getHandle();

  /**
   * Specifies the handle for the JSON or XML representation of the query.
   * @param handle	the JSON or XML handle.
   */
  void setHandle(StructureWriteHandle handle);
}
