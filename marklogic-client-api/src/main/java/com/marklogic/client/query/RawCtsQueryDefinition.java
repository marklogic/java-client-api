/*
 * Copyright 2013-2018 MarkLogic Corporation
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
package com.marklogic.client.query;

import com.marklogic.client.io.marker.CtsQueryWriteHandle;

/**
 * A RawCtsQueryDefinition allows you to create a query with a serialized cts
 * query in a JSON or XML representation.
 */
public interface RawCtsQueryDefinition extends QueryDefinition, ValueQueryDefinition {
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
   * Specifies the handle for the JSON or XML representation of the query.
   * @param handle	the JSON or XML handle.
   */
  void setHandle(CtsQueryWriteHandle handle);

  /**
   * Specifies the handle for the JSON or XML representation
   * of a combined query and returns the query definition.
   * @param handle	the JSON or XML handle.
   * @return	the query definition.
   */
  RawCtsQueryDefinition withHandle(CtsQueryWriteHandle handle);
}
