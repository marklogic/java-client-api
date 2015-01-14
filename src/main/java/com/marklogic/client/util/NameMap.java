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
package com.marklogic.client.util;

import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * A NameMap defines the interface for maps where the key is
 * a QName (a name qualified by a URI for global uniqueness or
 * for association with a domain).
 * @param <V>	the type for the values stored in the map
 */
public interface NameMap<V> extends Map<QName,V> {
	/**
	 * Returns the namespace context (if any) that declares the
	 * namespace bindings used to convert prefixed string names
	 * into QNames.
	 * @return	the namespace context (if any)
	 */
	public NamespaceContext getNamespaceContext();
	/**
	 * Specifies a namespace context that declares the
	 * namespace bindings used to convert prefixed string names
	 * into QNames.
	 * @param context	the namespace context (if any)
	 */
	public void setNamespaceContext(NamespaceContext context);

	/**
	 * Whether the map contains the string name as a key.
	 * @param name	the key expressed as a string
	 * @return	true if the map contains a QName equivalent to the key
	 */
	public boolean containsKey(String name);

	/**
	 * Returns the value of the string name.
	 * @param name	the key expressed as a string
	 * @return	the value of the key or null if the key doesn't exist in the map
	 */
	public V get(String name);
	/**
	 * Returns the value of the QName, cast to the supplied type.
	 * @param name	the key
	 * @param as	the type for the value
	 * @return	the value cast to the type
	 */
	public <T> T get(QName name, Class<T> as);
	/**
	 * Returns the value of the string name, cast to the supplied type.
	 * @param name	the key expressed as a string
	 * @param as	the type for the value
	 * @return	the value cast to the type
	 */
	public <T> T get(String name, Class<T> as);

	/**
	 * Specifies the value of the string name.
	 * @param name	the key expressed as a string
	 * @param value	the value of the key
	 * @return	the previous value or null if the key did not have a value
	 */
	public V put(String name, V value);

	/**
	 * Removes the key-value pair from the map.
	 * @param name	the key expressed as a string
	 * @return	the previous value or null if the key did not have a value
	 */
	public V remove(String name);
}
