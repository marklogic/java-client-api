package com.marklogic.client.admin.config.support;

import java.util.List;

import com.marklogic.client.admin.config.QueryOptions.Heatmap;

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
