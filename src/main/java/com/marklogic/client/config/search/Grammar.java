package com.marklogic.client.config.search;

import java.util.List;

import org.w3c.dom.Element;

public interface Grammar {

	String getQuotation();

	Element getImplicit();

	List<Joiner> getJoiners();
	List<Starter> getStarters();
	
	
	
}
