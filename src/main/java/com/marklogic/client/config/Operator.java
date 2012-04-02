package com.marklogic.client.config;

import java.util.List;

public interface Operator extends JAXBBackedQueryOption, QueryAnnotatable {

	public String getName();
	public void setName(String name);
	
	public List<State> getStates();
	
	

}
