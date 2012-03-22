package com.marklogic.client.config.search.impl;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.marklogic.client.config.search.Annotation;
import com.marklogic.client.config.search.Facetable;
import com.marklogic.client.config.search.RangeConstraint;
import com.marklogic.client.config.search.jaxb.Bucket;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Range;

/**
 * POJO to wrap and represent a search API constraint of type 'range'.
 * 
 * @author cgreer
 * 
 */
public class RangeConstraintImpl extends ConstraintImpl implements Facetable,
		RangeConstraint {

	private Range jaxbObject;

	public RangeConstraintImpl(Constraint constraint, Range range) {
		super(constraint);
		jaxbObject = range;
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public RangeConstraintImpl(String name) {
		super(name);
		jaxbObject = new Range();
		jaxbConstraint.getConstraint().add(jaxbObject);
		indexReferenceImpl = new IndexReferenceImpl(
				this.jaxbObject.getElementOrAttributeOrFragmentScope());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.marklogic.client.config.search.impl.RangeConstraintI#addBucket(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void addBucket(String name, String label) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(bucket);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.marklogic.client.config.search.impl.RangeConstraintI#addBucket(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addBucket(String name, String label, String ge, String lt) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(bucket);
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
		JAXBElement<String> facetOptionElement = new JAXBElement<String>(
				new QName("http://marklogic.com/appservices/search",
						"facet-option"), String.class, facetOption);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(
				facetOptionElement);

	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrFragmentScope();
	}

	@Override
	public void addAnnotation(Element annotation) {
		super.addAnnotation(this, annotation);
	}

	@Override
	public List<Element> getAnnotations() {
		return super.getAnnotations(this);
	}

}
