package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class FacetableConstraintDefinition<T extends FacetableConstraintDefinition<T>> extends ConstraintDefinition<T> {
	
	@XmlAttribute(name="facet")
	private boolean doFacets;
	
	@XmlElement(namespace=Options.SEARCH_NS,name="facet-option")
	private List<String> facetOptions;

	@XmlAttribute
	private QName type;

		
	public FacetableConstraintDefinition() {
		facetOptions = new ArrayList<String>();
	}
	/**
	 * Perform facets on this constraint.
	 * @param doFacets
	 * @return this object, for further fluent setters.
	 */
	public  T doFacets(boolean doFacets) {
		this.doFacets = doFacets;
		return (T) this;
	}
	/**
	 * @return true if this constraint is configured for facets.  False otherwise.
	 */
	public boolean getDoFacets() {
		return doFacets;
	}
	/**
	 * Add a facet option to this constraint type, returning this.
	 * @param facetOption
	 * @return this object, for further fluent setters.
	 */
	/* TODO: add facet option enum */
	public T withFacetOption(String facetOption) {
		this.facetOptions.add(facetOption);
		return (T) this;
	}

	/**
	 * get the list of facet options.
	 * @return
	 */
	public List<String> getFacetOptions() {
		return facetOptions;
	}
	

	public T withType(QName type) {
		this.type = type;
		return (T) this;
	}
}

