/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A FacetResult represents a single facet as returned by a search.
 */
public interface FacetResult {
  /**
   * Returns the facet name.
   * @return The facet name.
   */
  String getName();

  /**
   * Returns an array of the facet values.
   * @return The facet values.
   */
  FacetValue[] getFacetValues();
}
