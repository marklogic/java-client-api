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
