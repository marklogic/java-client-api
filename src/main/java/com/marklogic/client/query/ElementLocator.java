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
package com.marklogic.client.query;

import javax.xml.namespace.QName;

/**
 * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
 * <br><br>
 *
 * An Element Locator specifies an element or element and attribute
 * containing a value as part of a KeyValueQueryDefinition.
 * The element is required.  The attribute is optional.
 */
@Deprecated
public interface ElementLocator extends ValueLocator {
    /**
     * Returns the name of the element containing the attribute
     * or value.
     * @return	the element name
     */
	public QName getElement();
	/**
	 * Specifies the name of the element containing the attribute
     * (if also specified) or value. The element may have namespace.
	 * @param qname	the element name
	 */
    public void setElement(QName qname);
    /**
     * Returns the name of the attribute (if specified).
     * @return	the attribute name
     */
    public QName getAttribute();
    /**
     * Specifies the name of the attribute containing the value.  The
     * element name must also be specified.
     * @param qname	the attribute name
     */
    public void setAttribute(QName qname);
}
