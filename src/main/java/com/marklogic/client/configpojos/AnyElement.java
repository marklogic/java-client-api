/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;

import org.w3c.dom.Element;


/**
 * Wraps any element, for those places in the Search API schema where any
 * XML element may be used.
 * @see com.marklogic.client.configpojos.Grammar
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AnyElement  {

	@XmlAnyElement
	private Element element;
	
	public AnyElement() {
	}
	
	public AnyElement(Element ctsQuery) {
		element=ctsQuery;
	}


	public Element getValue() {
		return element;
	}
	
}