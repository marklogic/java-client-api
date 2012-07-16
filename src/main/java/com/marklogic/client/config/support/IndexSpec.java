package com.marklogic.client.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.config.QueryOptions.PathIndex;

/**
 * Supports building query configurations by encapsulating common index methods in one place.
 */
public interface IndexSpec {
	
	public QName getAttribute();

	public String getField();

	public QName getElement();

	public String getJsonKey();

	public PathIndex getPathIndex();
	
	public void build(Indexed indexable);
	
	public void setElement(QName qname);
	
	public void setAttribute(QName qname);
	
	public void setField(String fieldName);
	
	public void setJsonKey(String jsonKey);
	
	public void setPathIndex(PathIndex path);
	
}