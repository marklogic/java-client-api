package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="range")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Range  extends FacetableConstraintDefinition<Range>{

	@XmlElement(namespace=Options.SEARCH_NS, name="bucket")
	private List<Bucket> buckets;
	
	@XmlElement(namespace=Options.SEARCH_NS, name="computed-bucket")
	private List<ComputedBucket> computedBuckets;
	
	public Range() {
		buckets = new ArrayList<Bucket>();
		computedBuckets= new ArrayList<ComputedBucket>();
	}
	
	
	public List<Bucket> getBuckets() {
		return buckets;
	}


	public Range withBucket(String name, String label, String ge, String lt) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		buckets.add(bucket);
		return this;
	}

	public List<ComputedBucket> getComputedBuckets() {
		return computedBuckets;
	}


	public Range withBucket(ComputedBucket computedBucket) {
		this.computedBuckets.add(computedBucket);
		return this;
	}




	
	


}
