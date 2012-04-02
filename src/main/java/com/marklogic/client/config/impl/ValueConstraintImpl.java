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

import com.marklogic.client.config.ValueConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Value;


public class ValueConstraintImpl extends ConstraintImpl<Value> implements ValueConstraint {

	
	ValueConstraintImpl(Constraint constraint, Value value) {
		super(constraint);
		jaxbObject = value;
	    indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}

	public ValueConstraintImpl(String name) {
		super(name);
		jaxbObject = new Value();
		jaxbConstraint.getConstraint().add(jaxbObject);
	    indexReferenceImpl = new IndexReferenceImpl(getJAXBChildren());
	}


	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getElementOrAttributeOrFragmentScope();
	}


	@Override
	public Double getWeight() {
		return JAXBHelper.getOneSimpleByElementName(this ,"weight");
	}
	
	@Override
	public void setWeight(Double weight) {
		JAXBHelper.setOneSimpleByElementName(this, "weight", weight);
	}


}
