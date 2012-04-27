package com.marklogic.client.configpojos;

import javax.xml.namespace.QName;

public class IndexReference {

	QName element;
	QName attribute;
	
	public QName getElement() {
		return element;
	}
	public IndexReference withElement(QName element) {
		this.element= element;
		return this;
	}

	public QName getAttribute() {
		return attribute;
	}

	public String getField() {
		// TODO Auto-generated method stub
		return null;
	}

}
