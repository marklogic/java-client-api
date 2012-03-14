package com.marklogic.client.config.search;

import javax.xml.namespace.QName;

import com.marklogic.client.config.search.jaxb.Attribute;
import com.marklogic.client.config.search.jaxb.Element;
import com.marklogic.client.config.search.jaxb.Field;

public class IndexReference {

	Attribute attribute;
	Element element;
	Field field;
	//Path path;
	
	
	public QName getElement() {
		return new QName(element.getNs(), element.getName());
	}
	
	public void addAttributeQName(QName attributeQName) {
		attribute = new Attribute();
		attribute.setNs(attributeQName.getNamespaceURI());
		attribute.setName(attributeQName.getLocalPart());
	}
	
	public void addElementQName(QName elementQName) {
		element = new Element();
		element.setNs(elementQName.getNamespaceURI());
		element.setName(elementQName.getLocalPart());
	}
	
	public void addField(String name) {
		field = new Field();
		field.setName(name);
	}

	public QName getAttribute() {
		return new QName(attribute.getNs(), attribute.getName());		
	}
	
	public String getField() {
		return field.getName();
	}
}



