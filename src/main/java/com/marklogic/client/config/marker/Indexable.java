package com.marklogic.client.config.marker;

import javax.xml.namespace.QName;

import com.marklogic.client.config.QueryOptionsBuilder.Attribute;
import com.marklogic.client.config.QueryOptionsBuilder.Element;
import com.marklogic.client.config.QueryOptionsBuilder.Field;

public interface Indexable {

	/**
	 * Add a reference to an element to this ConstraintBase
	 */
	public void setElement(Element element);

	public void setAttribute(Attribute attribute);

	public QName getAttribute();

	public QName getElement();

	public String getFieldName();

	public void setField(Field field);
}
