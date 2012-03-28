package com.marklogic.client.config.search.impl;

import java.util.List;

import com.marklogic.client.config.search.WordConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Word;

public class WordConstraintImpl extends ConstraintImpl implements WordConstraint {


	private Word jaxbObject;

	public WordConstraintImpl(Constraint constraint, Word w) {
		super(constraint);
		jaxbObject = w;
		jaxbConstraint.getConstraint().add(jaxbObject);
	    indexReferenceImpl = new IndexReferenceImpl(jaxbObject.getElementOrAttributeOrFragmentScope());
	}

	public WordConstraintImpl(String name) {
		super(name);
		jaxbObject = new Word();
		jaxbConstraint.getConstraint().add(jaxbObject);
	    indexReferenceImpl = new IndexReferenceImpl(jaxbObject.getElementOrAttributeOrFragmentScope());
	}


	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrFragmentScope();
	}


}
