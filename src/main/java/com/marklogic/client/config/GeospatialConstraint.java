package com.marklogic.client.config;

import java.util.List;

public interface GeospatialConstraint extends Constraint {

	public Heatmap getHeatmap();
	public List<String> getGeoOptions();
	public void setHeatmap(Heatmap heatmap);
	public void setGeoOptions(List<String> geoOptions);

	public String getParentNs();
	public void setParentNs(String parentNs);
	public String getParentName();
	public void setParentName(String parentNs);
	
}
