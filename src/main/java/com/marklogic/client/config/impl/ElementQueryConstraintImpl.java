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
import com.marklogic.client.config.search.jaxb.Constraint;
import com.marklogic.client.config.search.jaxb.ElementQuery;

public class ElementQueryConstraintImpl extends ConstraintImpl<ElementQuery> {

	
	public ElementQueryConstraintImpl(String name) {
		super(name);
		jaxbObject = new ElementQuery();
		jaxbConstraint.getConstraint().add(jaxbObject);
	}

    ElementQueryConstraintImpl(Constraint constraint,
			com.marklogic.client.config.search.jaxb.ElementQuery elementQuery) {
		super(constraint);
		this.jaxbObject = elementQuery;
		jaxbConstraint.getConstraint().add(jaxbObject);

	}

	/**
     * Gets the value of the ns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNs() {
        return jaxbObject.getNs();
    }

    /**
     * Sets the value of the ns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNs(String ns) {
        jaxbObject.setNs(ns);
    }


    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return jaxbObject.getName();
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String name) {
        jaxbObject.setName(name);
    }

	@Override
	public List<Object> getJAXBChildren() {
		return null;
	}

	@Override
	public void addQueryAnnotation(Element annotation) {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}

	@Override
	public List<Element> getQueryAnnotations() {
		throw new MarkLogicBindingException("Annotations not allowed on CollectionConstraint.");
	}



}
