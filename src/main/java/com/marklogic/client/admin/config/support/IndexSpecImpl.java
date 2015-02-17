/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.QueryOptions.Field;
import com.marklogic.client.admin.config.QueryOptions.JsonKey;
import com.marklogic.client.admin.config.QueryOptions.MarkLogicQName;
import com.marklogic.client.admin.config.QueryOptions.PathIndex;

/**
 * Implements accessors to index or index-like configurations.
 * Used only in {@link com.marklogic.client.admin.config.QueryOptionsBuilder} expressions.
 */
@SuppressWarnings("deprecation")
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
