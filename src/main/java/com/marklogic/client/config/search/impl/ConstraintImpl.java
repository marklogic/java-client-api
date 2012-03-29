package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotatable;
import com.marklogic.client.config.search.Constraint;

public abstract class ConstraintImpl<T> extends AbstractQueryOption<T> implements
		Constraint, Annotatable {

	protected com.marklogic.client.config.search.jaxb.Constraint jaxbConstraint;

	public ConstraintImpl(String name) {
		jaxbConstraint = new com.marklogic.client.config.search.jaxb.Constraint();
		jaxbConstraint.setName(name);
	}

	public ConstraintImpl(
			com.marklogic.client.config.search.jaxb.Constraint constraint) {
		jaxbConstraint = constraint;
	}

	public Object asJaxbObject() {
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
	public void addAnnotation(Element annotation) {
		super.addAnnotation(jaxbConstraint, annotation);
	}

	@Override
	public List<Element> getAnnotations() {
		return super.getAnnotations(jaxbConstraint);
	}
}
