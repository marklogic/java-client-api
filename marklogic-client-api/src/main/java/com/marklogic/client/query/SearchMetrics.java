/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A SearchMetrics object represents the query metrics returned by a search.
 */
public interface SearchMetrics {
  /**
   * Returns the query resolution time in milliseconds.
   * @return The query resolution time.
   */
  long getQueryResolutionTime();

  /**
   * Returns the facet resolution time in milliseconds.
   * @return The facet resolution time.
   */
  long getFacetResolutionTime();

  /**
   * Returns the snippet resolution time in milliseconds.
   * @return The snippet resolution time.
   */
  long getSnippetResolutionTime();

  /**
   * Returns the extract resolution time in milliseconds.
   * @return The extract resolution time.
   */
  long getExtractResolutionTime();

  /**
   * Returns the total time taken by the query in milliseconds.
   * @return The total time.
   */
  long getTotalTime();
}
