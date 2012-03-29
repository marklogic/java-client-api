package com.marklogic.client.config.search.impl;

import java.util.List;

import com.marklogic.client.config.search.ValueConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Value;


public class ValueConstraintImpl extends ConstraintImpl<Value> implements ValueConstraint {

	
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
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrFragmentScope();
	}


	@Override
	public double getWeight() {
		return JAXBHelper.getOneSimpleByElementName(this ,"weight");
	}
	
	@Override
	public void setWeight(double weight) {
		JAXBHelper.setOneSimpleByElementName(this, "weight", weight);
	}


}
