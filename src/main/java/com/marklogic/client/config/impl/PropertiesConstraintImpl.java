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

import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.PropertiesConstraint;
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.Properties;


public class PropertiesConstraintImpl extends ConstraintImpl<Properties> implements PropertiesConstraint {

	
	PropertiesConstraintImpl(Constraint constraint, Properties props) {
		super(constraint);
		jaxbObject = props;
	}
	
	public PropertiesConstraintImpl(String name) {
		super(name);
		jaxbObject = new Properties();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}


	@Override
	public void addQueryAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Element> getQueryAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on PropertiesConstraint.");
	}

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

}
