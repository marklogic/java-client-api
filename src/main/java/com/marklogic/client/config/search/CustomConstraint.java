package com.marklogic.client.config.search;

public interface CustomConstraint extends Constraint, Facetable, TermOptions {

	public void setParse(FunctionRef function);
	public FunctionRef getParse();
	public void setStartFacet(FunctionRef function);
	public FunctionRef getStartFacet();
	public void setFinishFacet(FunctionRef function);
	public FunctionRef getFinishFacet();
	
}