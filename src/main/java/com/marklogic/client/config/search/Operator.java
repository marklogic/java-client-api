package com.marklogic.client.config.search;

import java.util.List;

public interface Operator extends Annotate {

	public String getName();
	public void setName(String name);
	
	public List<State> getStates();
	
	

}
