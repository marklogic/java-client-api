package com.marklogic.client.config.search;

public interface GeoElementConstraint extends GeospatialConstraint, Indexable {

	public String getElementNs();
	public void setElementNs(String ns);
	public String getElementName();
	public void setElementName(String name);
	
}
