package com.marklogic.client.config;

import java.util.List;


public interface Facetable {

	public void setDoFacets(boolean doFacets);
	public boolean getDoFacets();
	public void addFacetOption(String facetOption);
	public List<String> getFacetOptions();
	public void setFacetOptions(List<String> facetOptions);
	
}
