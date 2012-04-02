package com.marklogic.client.config.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.Constraint;
import com.marklogic.client.config.QueryAnnotatable;

public abstract class ConstraintImpl<T> extends AbstractQueryOption<T> implements
		Constraint, QueryAnnotatable {

	protected com.marklogic.client.config.search.jaxb.Constraint jaxbConstraint;

	public ConstraintImpl(String name) {
		jaxbConstraint = new com.marklogic.client.config.search.jaxb.Constraint();
		jaxbConstraint.setName(name);
	}

	protected ConstraintImpl(
			com.marklogic.client.config.search.jaxb.Constraint constraint) {
		jaxbConstraint = constraint;
	}

	public Object asJAXB() {
		return jaxbConstraint;
	}

	@Override
	public String getName() {
		return jaxbConstraint.getName();
	}
	
	@Override
	public void setName(String name) {
		jaxbConstraint.setName(name);
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}


	@Override
	public void addQueryAnnotation(Element annotation) {
		super.addAnnotation(jaxbConstraint, annotation);
	}

	@Override
	public List<Element> getQueryAnnotations() {
		return super.getAnnotations(jaxbConstraint);
	}
}
