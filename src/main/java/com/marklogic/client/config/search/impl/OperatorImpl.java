package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Operator;
import com.marklogic.client.config.search.QueryOption;
import com.marklogic.client.config.search.State;

public class OperatorImpl extends AbstractQueryOption implements QueryOption, Operator {

    private com.marklogic.client.config.search.jaxb.Operator jaxbObject;
    
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

	@Override
	public void addAnnotation(Element annotation) {
		addAnnotation(this, annotation);
	}

	@Override
	public List<Element> getAnnotations() {
		return getAnnotations(this);
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
