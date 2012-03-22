package com.marklogic.client.config.search.impl;

import java.util.List;

import javax.xml.namespace.QName;

import com.marklogic.client.config.search.IndexReference;
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
