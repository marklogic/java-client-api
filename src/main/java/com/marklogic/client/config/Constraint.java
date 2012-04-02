package com.marklogic.client.config;

public interface Constraint extends JAXBBackedQueryOption, QueryAnnotatable {

	void setName(String name);

	String getName();

	
}