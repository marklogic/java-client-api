package com.marklogic.client.config.search;

import javax.xml.namespace.QName;

public interface IndexReference {

	public QName getElement();

	public void addField(String name);

	public QName getAttribute();

	public String getField();

	public void addElementAttributeIndex(QName elementQName,
			QName attributeQName);

	public void addElementIndex(QName elementQName);

	public void addFieldIndex(String fieldName);

}