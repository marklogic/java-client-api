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
package com.marklogic.client.impl;

import com.marklogic.client.util.NameMap;

import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * NameMapBase provides a base class for maps where the key is
 * a QName (a name qualified by a URI for global uniqueness or
 * for association with a domain).
 * @param <V>
 */
@SuppressWarnings("serial")
public class NameMapBase<V>
    extends HashMap<QName,V>
    implements NameMap<V>
{
	private NamespaceContext context;

	/**
	 * Zero-argument constructor.
	 */
	public NameMapBase() {
		super();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return context;
	}
	@Override
	public void setNamespaceContext(NamespaceContext context) {
		this.context = context;
	}

	@Override
	public boolean containsKey(String name) {
		return super.containsKey(makeQName(name));
	}

	@Override
	public V get(String name) {
		return super.get(makeQName(name));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(QName name, Class<T> as) {
		Object value = get(name);
		if (value == null)
			return null;
		if (as.isAssignableFrom(value.getClass()))
			return (T) value;
		throw new IllegalArgumentException("Cannot get value of "+value.getClass().getName()+" as "+as.getName());
	}
	@Override
	public <T> T get(String name, Class<T> as) {
		return get(makeQName(name), as);
	}

	@Override
	public V put(String name, V value) {
		return put(makeQName(name), value);
	}

	@Override
	public V remove(String name) {
		return super.remove(makeQName(name));
	}

	protected QName makeQName(String name) {
		if (name == null) return null;

		if (name.contains(":")) {
			if (context == null)
				throw new IllegalStateException("No namespace context for resolving key with prefix: "+name);
			String[] parts = name.split(":", 2);
			String prefix = parts[0];
			if (prefix == null)
				throw new IllegalArgumentException("Empty prefix in key: "+name);
			String localPart = parts[1];
			if (localPart == null)
				throw new IllegalArgumentException("Empty local part in key: "+name);
			String uri = context.getNamespaceURI(prefix);
			if (uri == null || XMLConstants.NULL_NS_URI.equals(uri))
				throw new IllegalStateException("No namespace uri defined in context for prefix "+prefix+" of key: "+name);
			return new QName(uri,localPart, prefix);
		} else if (context != null) {
			String uri = context.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
			if (uri != null && !XMLConstants.NULL_NS_URI.equals(uri))
				return new QName(uri,name);
		}

		return new QName(name);
	}
}
