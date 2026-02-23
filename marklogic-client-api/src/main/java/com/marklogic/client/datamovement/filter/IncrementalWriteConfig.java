/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.document.DocumentWriteOperation;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Configuration for incremental write filtering.
 *
 * @since 8.1.0
 */
public class IncrementalWriteConfig {

	private final String hashKeyName;
	private final String timestampKeyName;
	private final boolean canonicalizeJson;
	private final Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer;
	private final String[] jsonExclusions;
	private final String[] xmlExclusions;
	private final Map<String, String> xmlNamespaces;
	private final String schemaName;
	private final String viewName;

	public IncrementalWriteConfig(String hashKeyName, String timestampKeyName, boolean canonicalizeJson,
								  Consumer<DocumentWriteOperation[]> skippedDocumentsConsumer,
								  String[] jsonExclusions, String[] xmlExclusions, Map<String, String> xmlNamespaces,
								  String schemaName, String viewName) {
		this.hashKeyName = hashKeyName;
		this.timestampKeyName = timestampKeyName;
		this.canonicalizeJson = canonicalizeJson;
		this.skippedDocumentsConsumer = skippedDocumentsConsumer;
		this.jsonExclusions = jsonExclusions;
		this.xmlExclusions = xmlExclusions;
		this.xmlNamespaces = xmlNamespaces != null ? Collections.unmodifiableMap(xmlNamespaces) : null;
		this.schemaName = schemaName;
		this.viewName = viewName;
	}

	public String getHashKeyName() {
		return hashKeyName;
	}

	public String getTimestampKeyName() {
		return timestampKeyName;
	}

	public boolean isCanonicalizeJson() {
		return canonicalizeJson;
	}

	public Consumer<DocumentWriteOperation[]> getSkippedDocumentsConsumer() {
		return skippedDocumentsConsumer;
	}

	public String[] getJsonExclusions() {
		return jsonExclusions;
	}

	public String[] getXmlExclusions() {
		return xmlExclusions;
	}

	public Map<String, String> getXmlNamespaces() {
		return xmlNamespaces != null ? xmlNamespaces : Collections.emptyMap();
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getViewName() {
		return viewName;
	}
}
