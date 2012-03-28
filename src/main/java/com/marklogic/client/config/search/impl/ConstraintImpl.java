package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotate;
import com.marklogic.client.config.search.Annotation;
import com.marklogic.client.config.search.Constraint;

public abstract class ConstraintImpl extends AbstractQueryOption implements
		Constraint, Annotate {

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

	public void addAnnotation(Element annotation) {
		jaxbConstraint.getConstraint().add(annotation);
	}

	public List<Element> getAnnotations() {
		List<Element> l = new ArrayList<Element>();
		List<Object> children = jaxbConstraint.getConstraint();
		for (Object o : children) {
			if (o instanceof com.marklogic.client.config.search.jaxb.Annotation) {
				Annotation a = (Annotation) new AnnotationImpl(
						(com.marklogic.client.config.search.jaxb.Annotation) o);
				l.addAll(a.getContent());
			}
		}
		return l;
	}

}
