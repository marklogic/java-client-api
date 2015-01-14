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

import com.marklogic.client.admin.config.QueryOptions.PathIndex;

/**
 * Defines methods for accessing index (or indexable) configuration elements.  
 * Indexes in MarkLogic may be built on element names, path expressions, fields, and json keys.
 * This interface simply defines shared methods among those MarkLogic entities that 
 * back term sources and range sources.
 */
@SuppressWarnings("deprecation")
public interface IndexSpec  {
	
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
