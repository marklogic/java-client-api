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

import org.w3c.dom.Element;

import com.marklogic.client.config.Constraint;
import com.marklogic.client.config.QueryAnnotatable;

public abstract class ConstraintImpl<T> extends AbstractQueryOption<T> implements
		Constraint, QueryAnnotatable {

	protected com.marklogic.client.config.search.jaxb.Constraint jaxbConstraint;

	public ConstraintImpl(String name) {
		jaxbConstraint = new com.marklogic.client.config.search.jaxb.Constraint();
		jaxbConstraint.setName(name);
	}

	protected ConstraintImpl(
			com.marklogic.client.config.search.jaxb.Constraint constraint) {
		jaxbConstraint = constraint;
	}

	public Object asJAXB() {
		return jaxbConstraint;
	}

	@Override
	public String getName() {
		return jaxbConstraint.getName();
	}
	
	@Override
	public void setName(String name) {
		jaxbConstraint.setName(name);
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}


	@Override
	public void addQueryAnnotation(Element annotation) {
		super.addAnnotation(jaxbConstraint, annotation);
	}

	@Override
	public List<Element> getQueryAnnotations() {
		return super.getAnnotations(jaxbConstraint);
	}
}
