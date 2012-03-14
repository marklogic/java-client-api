package com.marklogic.client.config.search;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.marklogic.client.config.search.jaxb.Bucket;
import com.marklogic.client.config.search.jaxb.Range;

/**
 * POJO to wrap and represent a search API constraint of type 'range'.
 * 
 * @author cgreer
 * 
 */
public class RangeConstraint extends IndexableConstraint implements
		SearchOption {

	private Range jaxbObject;

	public RangeConstraint(String name, Range range) {
		super(name);
		jaxbObject = new Range();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}

	public RangeConstraint(String name) {
		this(name, new Range());
	}

	public void addBucket(String name, String label) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(bucket);
	}

	public void addBucket(String name, String label, String ge, String lt) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(bucket);
	}

	public void addFacetOption(String facetOption) {
		JAXBElement<String> facetOptionElement = new JAXBElement<String>(
				new QName("http://marklogic.com/appservices/search", "facet-option"),
				String.class, facetOption);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(
				facetOptionElement);
	}

}
