package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


@XmlAccessorType(XmlAccessType.FIELD)
public class QueryAnnotation  {

	@XmlAnyElement
	private List<Element> annotations;

	public QueryAnnotation() {
		annotations = new ArrayList<Element>();
	}
	
	public void set(Element value) {
		this.annotations.add(value);
	}

	public Element get(int i) {
		return annotations.get(i);
	}

	
}

