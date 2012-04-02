package com.marklogic.client.config.impl;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.marklogic.client.config.Anchor;
import com.marklogic.client.config.Bucket;
import com.marklogic.client.config.ComputedBucket;
import com.marklogic.client.config.Facetable;
import com.marklogic.client.config.FunctionRef;
import com.marklogic.client.config.RangeConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Range;

/**
 * POJO to wrap and represent a search API constraint of type 'range'.
 * 
 * @author cgreer
 * 
 */
public class RangeConstraintImpl extends ConstraintImpl<Range> implements Facetable,
		RangeConstraint {


	
	RangeConstraintImpl(Constraint constraint, Range range) {
		super(constraint);
		jaxbObject = range;
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public RangeConstraintImpl(String name) {
		super(name);
		jaxbObject = new Range();
		jaxbConstraint.getConstraint().add(jaxbObject);
		indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	@Override
	public void addBucket(String name, String label) {
		com.marklogic.client.config.search.jaxb.Bucket bucket = new com.marklogic.client.config.search.jaxb.Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		jaxbObject.getElementOrAttributeOrFragmentScope().add(bucket);
	}

	@Override
	public void addBucket(String name, String label, String ge, String lt) {
		com.marklogic.client.config.search.jaxb.Bucket bucket = new com.marklogic.client.config.search.jaxb.Bucket();
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
	public void setType(QName type) {
		jaxbObject.setType(type);
	}

	@Override
	public QName getType() {
		return jaxbObject.getType();
	}

	@Override
	public List<Bucket> getBuckets() {
		return JAXBHelper.getByClassName(this, Bucket.class);
	}

	
	@Override
	public void setAnchorAsFunction(String name, FunctionRef function) {
		Anchor newAnchor = new AnchorImpl(name, function);
		JAXBHelper.setOneByClassName(this, newAnchor);
	}
	
	public FunctionRef getAnchorFunction() {
		Anchor anchor = JAXBHelper.getOneByClassName(this, Anchor.class);
		
		FunctionRef function = new FunctionRefImpl(new QName("dummy"));
		function.setApply(anchor.getApply());
		function.setAt(anchor.getAt());
		function.setNs(anchor.getNs());
		return function;
	}


	@Override
	public List<ComputedBucket> getComputedBuckets() {
		return JAXBHelper.getByClassName(this, ComputedBucket.class);
	}

	@Override
	public void setComputedBuckets(List<ComputedBucket> buckets) {
		JAXBHelper.setByClassName(this, buckets, ComputedBucket.class);
	}

}
