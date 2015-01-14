/*
 * Copyright 2012-2015 MarkLogic Corporation
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
    public double[] getBox();
}
