/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A FacetHeatmapValue is a facet value returned by a geospatial query heatmap.
 */
public interface FacetHeatmapValue extends FacetValue {
  /**
   * Returns the box associated with the heatmap as an array.
   *
   * The latitude/longitude values in the array represent the South, West, North, and East
   * corners of the box, respectively.
   *
   * @return The box values.
   */
  double[] getBox();
}
