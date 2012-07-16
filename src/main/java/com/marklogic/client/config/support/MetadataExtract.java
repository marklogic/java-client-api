package com.marklogic.client.config.support;

import com.marklogic.client.config.QueryOptions.QueryExtractMetadata;

/*
 * Marker for qname|constraint-value|json-key
 */
public interface MetadataExtract {
	public void build(QueryExtractMetadata metadata);
}