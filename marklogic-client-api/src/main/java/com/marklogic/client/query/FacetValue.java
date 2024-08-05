/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A FacetValue represents a single value returned in a set of facet results.
 */
public interface FacetValue {
  /**
   * Returns the name of the facet value.
   * @return The name.
   */
  String getName();

  /**
   * Returns the count of items for that facet value.
   * @return The count
   */
  long   getCount();

  /**
   * Returns the label associated with that facet value.
   * @return The label.
   */
  String getLabel();
}
