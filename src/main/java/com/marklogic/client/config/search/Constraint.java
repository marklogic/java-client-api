package com.marklogic.client.config.search;

public interface Constraint extends JAXBBackedQueryOption, Annotatable {

	void setName(String name);

	String getName();

	
}