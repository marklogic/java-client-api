package com.marklogic.client.config.search.impl;

import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.CollectionConstraint;
import com.marklogic.client.config.search.Facetable;
import com.marklogic.client.config.search.MarkLogicBindingException;
import com.marklogic.client.config.search.jaxb.Collection;
import com.marklogic.client.config.search.jaxb.Constraint;


public class CollectionConstraintImpl extends ConstraintImpl implements Facetable, CollectionConstraint {

	com.marklogic.client.config.search.jaxb.Collection jaxbObject;
	

	
	public CollectionConstraintImpl(Constraint constraint,
			Collection constraintSpec) {
		super(constraint);
		jaxbObject = constraintSpec;
	}
	
	public CollectionConstraintImpl(String name) {
		super(name);
		jaxbObject = new com.marklogic.client.config.search.jaxb.Collection();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}


	@Override
	public void setDoFacets(boolean doFacets) {
		jaxbObject.setFacet(doFacets);
	}

	@Override
	public boolean getDoFacets() {
		return jaxbObject.isSetFacet();
	}

	@Override
	public void addFacetOption(String facetOption) {
		jaxbObject.getFacetOption().add(facetOption);
	}
	
	@Override
	public void setPrefix(String prefix) {
		jaxbObject.setPrefix(prefix);
	}
	
	public String getPrefix() {
		return jaxbObject.getPrefix();
	}


	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}


	@Override
	public void addAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}

	@Override
	public List<Element> getAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}


}
