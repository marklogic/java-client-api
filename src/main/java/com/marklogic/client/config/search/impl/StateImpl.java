package com.marklogic.client.config.search.impl;

import com.marklogic.client.config.search.State;

public class StateImpl implements State {

	com.marklogic.client.config.search.jaxb.State jaxbObject;
	
	public StateImpl(com.marklogic.client.config.search.jaxb.State o) {
		jaxbObject= o;
	}

}
