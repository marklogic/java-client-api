/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.config;

import java.util.List;

import javax.xml.namespace.QName;


public interface RangeConstraint extends BoundQueryOption, Constraint, Facetable, Indexable {

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
	
	public void setAnchorAsFunction(String name, FunctionRef function);
	public FunctionRef getAnchorFunction();
	
	public List<ComputedBucket> getComputedBuckets();
	public void setComputedBuckets(List<ComputedBucket> buckets);
	

}