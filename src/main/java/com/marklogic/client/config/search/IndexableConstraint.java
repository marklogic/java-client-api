package com.marklogic.client.config.search;

import javax.xml.namespace.QName;


public class IndexableConstraint extends Constraint {

	public IndexableConstraint(String name) {
		super(name);
		index = new IndexReference();
	}
	
	protected IndexReference index;

	public void addElementAttributeIndex(QName elementQName, QName attributeQName) {
		index.addAttributeQName(attributeQName);
		index.addElementQName(elementQName);
	}

	public IndexReference getIndex() {
		return index;
	}

	
}
