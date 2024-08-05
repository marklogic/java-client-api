/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.pojo.PojoQueryDefinition;

/**
 * A StringQueryDefinition represents the criteria associated with a simple string query.
 */
public interface StringQueryDefinition
  extends QueryDefinition, ValueQueryDefinition, PojoQueryDefinition
{
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
  StringQueryDefinition withCriteria(String criteria);
}
