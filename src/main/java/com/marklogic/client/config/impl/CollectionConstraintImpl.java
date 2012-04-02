package com.marklogic.client.config.impl;

import java.util.List;

import com.marklogic.client.config.CollectionConstraint;
import com.marklogic.client.config.Facetable;
import com.marklogic.client.config.search.jaxb.Collection;
import com.marklogic.client.config.search.jaxb.Constraint;

public class CollectionConstraintImpl extends
		ConstraintImpl<com.marklogic.client.config.search.jaxb.Collection>
		implements Facetable, CollectionConstraint {

	CollectionConstraintImpl(Constraint constraint, Collection constraintSpec) {
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

}
