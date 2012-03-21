package com.marklogic.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * EditableNamespaceContext provides access to namespace bindings of prefixes and URIs and
 * can act as a NamespaceContext.
 */
public class EditableNamespaceContext
    implements NamespaceContext, Map<String, String>
{
	// key is prefix, value is namespace URI
	private HashMap<String, String> bindings = new HashMap<String, String>();

	public EditableNamespaceContext() {
		super();
	}

	public String getDefaultNamespaceURI() {
    	return bindings.get(XMLConstants.DEFAULT_NS_PREFIX);
    }
    public void setDefaultNamespaceURI(String namespaceURI) {
    	if (namespaceURI == null)
			throw new IllegalArgumentException("Cannot set default prefix to null namespace URI");

   		bindings.put(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
    }

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

    @Override
	public int size() {
		return bindings.size();
	}
	@Override
	public boolean isEmpty() {
		return bindings.isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return bindings.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return bindings.containsValue(value);
	}
	@Override
	public String get(Object key) {
		return bindings.get(key);
	}
	@Override
	public String put(String key, String value) {
		return bindings.put(key, value);
	}
	@Override
	public String remove(Object key) {
		return bindings.remove(key);
	}
	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		bindings.putAll(m);
	}
	@Override
	public void clear() {
		bindings.clear();
	}
	@Override
	public Set<String> keySet() {
		return bindings.keySet();
	}
	@Override
	public Collection<String> values() {
		return bindings.values();
	}
	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return bindings.entrySet();
	}
}
