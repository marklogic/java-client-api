package com.marklogic.client.config.search;

import java.util.List;

import javax.xml.namespace.QName;


public interface RangeConstraint extends JAXBBackedQueryOption, Constraint, Facetable, Indexable {

	/**
	 * add a bucket to a range constraint to support facets.  This method adds a bucket with no upper or lower bounds.
	 * @param name Name of the bucket.
	 * @param label Label for the bucket to be used in text.
	 */
	public void addBucket(String name, String label);

	/**
	 * add a bucket to a range constraint to support facets.  This methods adds a bucket with lower and upper bounds defined.
	 * @param name Name of the bucket element.
	 * @param label Label for the bucket to be used in text.
	 * @param ge The lower bound of the bucket.  Stands for "greater than or equal to"
	 * @param lt The upper bound of the bucket.  Stands for "less than"
	 */
	public void addBucket(String name, String label, String ge,
			String lt);
	
	public List<Bucket> getBuckets();  // TODO refactor jaxb class out of interface.

	public void setType(QName string);
	public QName getType();

}