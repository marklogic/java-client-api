/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A SearchMetrics object represents the query metrics returned by a search.
 */
public interface ValuesMetrics {
  /**
   * Returns the query resolution time in milliseconds.
   * @return The query resolution time.
   */
  long getValuesResolutionTime();

  /**
   * Returns the facet resolution time in milliseconds.
   * @return The facet resolution time.
   */
  long getAggregateResolutionTime();

  /**
   * Returns the total time taken by the query in milliseconds.
   * @return The total time.
   */
  long getTotalTime();
}
