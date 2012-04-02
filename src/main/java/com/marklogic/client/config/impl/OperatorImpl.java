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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.marklogic.client.config.JAXBBackedQueryOption;
import com.marklogic.client.config.Operator;
import com.marklogic.client.config.State;

public class OperatorImpl extends AbstractQueryOption<com.marklogic.client.config.search.jaxb.Operator > implements JAXBBackedQueryOption, Operator {

    
	OperatorImpl(com.marklogic.client.config.search.jaxb.Operator ot) {
		jaxbObject = ot;
	}

	@Override
	public Object asJAXB() {
		return jaxbObject;
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbObject.getStateOrAnnotation();
	}

	@Override
	public String getName() {
		return jaxbObject.getName();
	}

	@Override
	public void setName(String name) {
		jaxbObject.setName(name);
	}


	public List<State> getStates() {
		return JAXBHelper.getByClassName(this, com.marklogic.client.config.State.class);
	}	
}
