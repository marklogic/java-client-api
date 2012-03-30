package com.marklogic.client.config.search;

public interface GeoAttrPairConstraint extends GeospatialConstraint {

	public String getLatNs();
	public void setLatNs(String latNs);
	public String getLatName();
	public void setLatName(String latName);

	public String getLonNs();
	public void setLonNs(String LonNs);
	public String getLonName();
	public void setLonName(String LonName);
}
