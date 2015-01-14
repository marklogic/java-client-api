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
package com.marklogic.client.admin.config.support;

import java.util.List;

import com.marklogic.client.admin.config.QueryOptions.Heatmap;

/**
 * Models an object that helps build heatmaps for use 
 * in {@link com.marklogic.client.admin.config.QueryOptionsBuilder} geospatial expressions.
 * Do not use directly.   Build using
 * {@link com.marklogic.client.admin.config.QueryOptionsBuilder}.heatmap()
 */
@SuppressWarnings("deprecation")
public class HeatmapAndOptions {

	private Heatmap heatmap;
	private List<String> facetOptions;
	
	public void setHeatmap(Heatmap heatmap) {
		this.heatmap = heatmap;
	}
	
	public Heatmap getHeatmap() {
		return heatmap;
	}

	public void setFacetOptions(List<String> facetOptions) {
		this.facetOptions = facetOptions;
	}
   
	public List<String> getFacetOptions() {
		return facetOptions;
	}
}
