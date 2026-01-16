/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple implementation of {@link NamespaceContext} backed by a Map of prefix to namespace URI mappings.
 * Used for XPath evaluation with namespace-qualified expressions.
 *
 * @since 8.1.0
 */
class SimpleNamespaceContext implements NamespaceContext {

	private final Map<String, String> prefixToNamespaceUri;

	SimpleNamespaceContext(Map<String, String> prefixToNamespaceUri) {
		this.prefixToNamespaceUri = prefixToNamespaceUri;
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return prefixToNamespaceUri.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		for (Map.Entry<String, String> entry : prefixToNamespaceUri.entrySet()) {
			if (entry.getValue().equals(namespaceURI)) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		return prefixToNamespaceUri.entrySet().stream()
			.filter(entry -> entry.getValue().equals(namespaceURI))
			.map(Map.Entry::getKey)
			.iterator();
	}
}
