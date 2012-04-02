package com.marklogic.client.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.JAXBBackedQueryOption;
import com.marklogic.client.config.Operator;
import com.marklogic.client.config.State;

public class OperatorImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Operator > implements JAXBBackedQueryOption, Operator {

    
	OperatorImpl(com.marklogic.client.config.search.jaxb.Operator ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJAXB() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getStateOrAnnotation();
	}

	@Override
	public String getName() {
		return jaxbObject.getName();
	}

	@Override
	public void setName(String name) {
		jaxbObject.setName(name);
	}


	public List<State> getStates() {
		return JAXBHelper.getByClassName(this, com.marklogic.client.config.State.class);
	}	
}
