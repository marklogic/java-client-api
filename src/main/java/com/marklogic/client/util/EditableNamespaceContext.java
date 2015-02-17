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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

/**
 * EditableNamespaceContext provides access to namespace bindings of prefixes and URIs and
 * can act as a NamespaceContext.
 */
public class EditableNamespaceContext
    implements IterableNamespaceContext, Map<String, String>
{
	// key is prefix, value is namespace URI
	private HashMap<String, String> bindings = new HashMap<String, String>();

	/**
	 * Constructs a NamespaceContext.
	 */
	public EditableNamespaceContext() {
		super();
	}

	/**
	 * Returns the namespace URI bound to the empty prefix.
	 * @return	the namespace URI
	 */
	public String getDefaultNamespaceURI() {
    	return bindings.get(XMLConstants.DEFAULT_NS_PREFIX);
    }
	/**
	 * Specifies the namespace URI bound to the empty prefix.
	 * @param namespaceURI	the namespace URI
	 */
    public void setDefaultNamespaceURI(String namespaceURI) {
    	if (namespaceURI == null)
			throw new IllegalArgumentException("Cannot set default prefix to null namespace URI");

   		bindings.put(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
    }

    /**
     * Returns the URI for a namespace binding.
     */
    @Override
    public String getNamespaceURI(String prefix) {
    	// per javax.xml.namespace.NamespaceContext doc
    	if (prefix == null)
			throw new IllegalArgumentException("Cannot get namespace URI for null prefix");
    	if (XMLConstants.XML_NS_PREFIX.equals(prefix))
    		return XMLConstants.XML_NS_URI;
    	if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix))
    		return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

    	String namespaceURI = bindings.get(prefix);

    	// per javax.xml.namespace.NamespaceContext doc
    	return (namespaceURI != null) ? namespaceURI : XMLConstants.NULL_NS_URI;
    }
    /**
     * Specifies a binding between a prefix and a namespace URI.
     * @param prefix	the prefix for the binding
     * @param namespaceURI	the URI for the namespace
     */
    public void setNamespaceURI(String prefix, String namespaceURI) {
    	if (prefix == null)
			throw new IllegalArgumentException("Cannot bind null prefix");
    	if (namespaceURI == null)
			throw new IllegalArgumentException("Cannot set prefix to null namespace URI");

    	// no need to store
    	if (XMLConstants.XML_NS_PREFIX.equals(prefix) || XMLConstants.XMLNS_ATTRIBUTE.equals(prefix))
    		return;

    	bindings.put(prefix, namespaceURI);
    }

    /**
     * Returns all bound prefixes.
     * @return	the set of prefixes
     */
    public Collection<String> getAllPrefixes() {
        return bindings.keySet();
    }

    /**
     * Returns the prefix for a namespace binding.
     */
    @Override
    public String getPrefix(String namespaceURI) {
    	// per javax.xml.namespace.NamespaceContext doc
    	if (namespaceURI == null)
			throw new IllegalArgumentException("Cannot find prefix for null namespace URI");
    	if (XMLConstants.XML_NS_URI.equals(namespaceURI))
    		return XMLConstants.XML_NS_PREFIX;
    	if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
    		return XMLConstants.XMLNS_ATTRIBUTE;

    	for (Map.Entry<String, String> entry: bindings.entrySet()) {
    		if (namespaceURI.equals(entry.getValue()))
    			return entry.getKey();
    	}

    	return null;
    }
    /**
     * Returns all prefixes with a namespace binding.
     */
    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
    	// per javax.xml.namespace.NamespaceContext doc
    	if (namespaceURI == null)
			throw new IllegalArgumentException("Cannot find prefix for null namespace URI");

    	List<String> list = new ArrayList<String>();
    	if (XMLConstants.XML_NS_URI.equals(namespaceURI))
    		list.add(XMLConstants.XML_NS_PREFIX);
    	else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
    		list.add(XMLConstants.XMLNS_ATTRIBUTE);
    	else
    		for (Map.Entry<String, String> entry: bindings.entrySet()) {
    			if (namespaceURI.equals(entry.getValue()))
    				list.add(entry.getKey());
    		}

    	return Collections.unmodifiableList(list).iterator();
    }

    /**
     * Returns the number of namespace bindings.
     */
    @Override
	public int size() {
		return bindings.size();
	}
    /**
     * Returns whether any namespace bindings exists.
     */
	@Override
	public boolean isEmpty() {
		return bindings.isEmpty();
	}
	/**
	 * Checks whether a prefix has a binding.
	 */
	@Override
	public boolean containsKey(Object key) {
		return bindings.containsKey(key);
	}
	/**
	 * Checks whether a namespace URI has a binding.
	 */
	@Override
	public boolean containsValue(Object value) {
		return bindings.containsValue(value);
	}
	/**
	 * Returns the URI for a prefix.
	 */
	@Override
	public String get(Object key) {
		return bindings.get(key);
	}
	/**
	 * Adds a namespace binding between a prefix and a URI.
	 */
	@Override
	public String put(String key, String value) {
		return bindings.put(key, value);
	}
	/**
	 * Removes the namespace binding for a prefix.
	 */
	@Override
	public String remove(Object key) {
		return bindings.remove(key);
	}
	/**
	 * Adds all namespace bindings from a map.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		bindings.putAll(m);
	}
	/**
	 * Removes all namespace bindings.
	 */
	@Override
	public void clear() {
		bindings.clear();
	}
	/**
	 * Gets the set of prefixes from the namespace bindings.
	 */
	@Override
	public Set<String> keySet() {
		return bindings.keySet();
	}
	/**
	 * Gets the list of URIs from the namespace bindings.
	 */
	@Override
	public Collection<String> values() {
		return bindings.values();
	}
	/**
	 * Gets the namespace bindings.
	 */
	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return bindings.entrySet();
	}
}
