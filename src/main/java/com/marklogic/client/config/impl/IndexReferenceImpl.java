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

import javax.xml.namespace.QName;

import com.marklogic.client.config.IndexReference;
import com.marklogic.client.config.search.jaxb.Attribute;
import com.marklogic.client.config.search.jaxb.Element;
import com.marklogic.client.config.search.jaxb.Field;

public class IndexReferenceImpl implements IndexReference {

	Attribute attribute;
	Element element;
	Field field;
	// Path path;
	private List<Object> jaxbList;

	public IndexReferenceImpl(List<Object> jaxbList) {
		this.jaxbList = jaxbList;
		for (Object o : jaxbList) {
			if (o instanceof Attribute) {
				attribute = (Attribute) o;
			} else if (o instanceof Element) {
				element = (Element) o;
			}
			else if (o instanceof Field) {
				field = (Field) o;
			}
			// TODO path range index
		}
	}

	@Override
	public QName getElement() {
		if (element == null) {
			return null;
		} else {
			return new QName(element.getNs(), element.getName());
		}
	}

	private void addAttributeQName(QName attributeQName) {
		attribute = new Attribute();
		attribute.setNs(attributeQName.getNamespaceURI());
		attribute.setName(attributeQName.getLocalPart());
		jaxbList.add(attribute);
	}

	private void addElementQName(QName elementQName) {
		element = new Element();
		element.setNs(elementQName.getNamespaceURI());
		element.setName(elementQName.getLocalPart());
		jaxbList.add(element);
	}

	@Override
	public void addField(String name) {
		field = new Field();
		field.setName(name);
		jaxbList.add(field);
	}

	@Override
	public QName getAttribute() {
		if (attribute == null) {
			return null;
		} else {
			return new QName(attribute.getNs(), attribute.getName());
		}
	}

	@Override
	public String getField() {
		if (field == null) {
			return null;
		} else {
			return field.getName();
		}
	}

	@Override
	public void addElementAttributeIndex(QName elementQName, QName attributeQName) {
		addAttributeQName(attributeQName);
		addElementQName(elementQName);
	}


	@Override
	public void addElementIndex(QName elementQName) {
		addElementQName(elementQName);
	}
	
	@Override
	public void addFieldIndex(String fieldName) {
		addField(fieldName);
	}

}
