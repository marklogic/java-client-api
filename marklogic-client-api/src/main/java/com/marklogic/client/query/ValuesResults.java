/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * ValuesResults represents one set of values from a values query.
 */
public interface ValuesResults {
  /**
   * Return the query definition for this set of results.
   * @return The query criteria.
   */
  ValuesDefinition getQueryCriteria();

  /**
   * Returns the name of the values.
   * @return The name.
   */
  String getName();

  /**
   * Returns the type of the values.
   *
   * <p>The type is an XSD type, for example "xs:boolean" or "xs:integer".</p>
   *
   * @return The type.
   */
  String getType();

  /**
   * Returns an array of the values.
   * @return The array of values.
   */
  CountedDistinctValue[] getValues();

  /**
   * Returns an array of the aggregates.
   * @return The array of aggrgates.
   */
  AggregateResult[] getAggregates();

  /**
   * Returns the aggregate result for the named aggregate function.
   * @param name The name of the aggregate function.
   * @return The corresponding aggregate, or null if no such aggregate exists.
   */
  AggregateResult getAggregate(String name);

  /**
   * Returns performance metrics about the query.
   * @return The metrics
   */
  ValuesMetrics getMetrics();
}

