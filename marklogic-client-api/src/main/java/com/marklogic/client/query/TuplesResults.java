/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * TupleResults represent the values returned by a tuple values query.
 */
public interface TuplesResults {
  /**
   * Returns the query used to locate these tuples.
   * @return The query definition.
   */
  ValuesDefinition getQueryCriteria();

  /**
   * Returns the name of the tuples.
   * @return The name.
   */
  String getName();

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
   * Returns an array of Tuples.
   * @return The array of tuples.
   */
  Tuple[] getTuples();

  /**
   * Returns performance metrics about the query.
   * @return The metrics
   */
  ValuesMetrics getMetrics();
}
