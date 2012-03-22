package com.marklogic.client.config.search;

import javax.xml.namespace.QName;


public interface Indexable {

	public IndexReference getIndex();

	public void addFieldIndex(String fieldName);

	public void addElementIndex(QName elementQName);

	public void addElementAttributeIndex(QName elementQName, QName attributeQName);

}
