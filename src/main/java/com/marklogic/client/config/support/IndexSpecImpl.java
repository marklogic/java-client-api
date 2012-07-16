package com.marklogic.client.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.config.QueryOptions.Field;
import com.marklogic.client.config.QueryOptions.JsonKey;
import com.marklogic.client.config.QueryOptions.MarkLogicQName;
import com.marklogic.client.config.QueryOptions.PathIndex;


public class IndexSpecImpl implements TermSpec {
	public QName getAttribute() {
		return attribute;
	}
	public void setAttribute(QName attribute) {
		this.attribute = attribute;
	}
	public javax.xml.namespace.QName getElement() {
		return element;
	}
	public void setElement(QName element) {
		this.element = element;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public PathIndex getPathIndex() {
		return pathIndex;
	}
	public void setPathIndex(PathIndex pathIndex) {
		this.pathIndex = pathIndex;
	}
	public String getJsonKey() {
		return jsonKey;
	}
	public void setJsonKey(String jsonKey) {
		this.jsonKey = jsonKey;
	}
	private QName attribute;
	private QName element;
	private String field;
	private PathIndex pathIndex;
	private String jsonKey;
	
	@Override
	public void build(Indexed indexable) {
		if (getAttribute() != null) {
			indexable.setAttribute(new MarkLogicQName(getAttribute().getNamespaceURI(), getAttribute().getLocalPart()));
		}
		if (getElement() != null) {
			indexable.setElement(new MarkLogicQName(getElement().getNamespaceURI(), getElement().getLocalPart()));
		}
		if (getField() != null) {
			indexable.setField(new Field(getField()));
		}
		if (getJsonKey() != null) {
			indexable.setJsonKey(new JsonKey(getJsonKey()));
		}
		if (getPathIndex() != null) {
			indexable.setPath(getPathIndex());	
		}
	}
		

}
