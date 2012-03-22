package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.ValueConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Value;


public class ValueConstraintImpl extends ConstraintImpl implements ValueConstraint {

	
	private Value jaxbObject;

	public ValueConstraintImpl(Constraint constraint, Value value) {
		super(constraint);
		jaxbObject = value;
	    indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public ValueConstraintImpl(String name) {
		super(name);
		jaxbObject = new Value();
		jaxbConstraint.getConstraint().add(jaxbObject);
	    indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	@Override
	public void addTermOption(String termOption) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrFragmentScope();
	}

	@Override
	public void addAnnotation(Element annotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Element> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}


}
