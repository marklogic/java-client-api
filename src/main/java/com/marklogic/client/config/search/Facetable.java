package com.marklogic.client.config.search;


public interface Facetable {

	public void setDoFacets(boolean doFacets);
	public boolean getDoFacets();
	public void addFacetOption(String facetOption);
	
}
