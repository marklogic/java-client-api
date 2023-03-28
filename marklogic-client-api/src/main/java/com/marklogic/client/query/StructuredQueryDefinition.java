/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.pojo.PojoQueryDefinition;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A StructuredQueryDefinition represents a structured query.
 *
 * Instances of this interface are produced by StructuredQueryBuilder.
 */
public interface StructuredQueryDefinition
  extends QueryDefinition, ValueQueryDefinition, PojoQueryDefinition
{
  /**
   * Returns the structured query definition as a serialized XML string.
   *
   * @return The serialized definition.
   */
  String serialize();

	void serialize(XMLStreamWriter serializer) throws XMLStreamException;

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
  StructuredQueryDefinition withCriteria(String criteria);
}
