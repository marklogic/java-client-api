package com.marklogic.client.config;

import java.util.List;

import org.w3c.dom.Element;

public interface Grammar extends JAXBBackedQueryOption {

	String getQuotation();

	Element getImplicit();

	List<Joiner> getJoiners();
	List<Starter> getStarters();
	
	
	
}
