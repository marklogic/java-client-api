package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Operator;
import com.marklogic.client.config.search.JAXBBackedQueryOption;
import com.marklogic.client.config.search.State;

public class OperatorImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Operator > implements JAXBBackedQueryOption, Operator {

    
	public OperatorImpl(com.marklogic.client.config.search.jaxb.Operator ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJaxbObject() {
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
		List<State> l = new ArrayList<State>();
		List<Object> children = getJAXBChildren();
		for (Object o : children ) {
			if (o instanceof com.marklogic.client.config.search.jaxb.State) {
				l.add((State) new StateImpl((com.marklogic.client.config.search.jaxb.State) o));
			}
		}
		return l;
	}
}
