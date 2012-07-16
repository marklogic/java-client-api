package com.marklogic.client.admin.config.support;

import com.marklogic.client.admin.config.QueryOptions.QueryExtractMetadata;

/*
 * Marker for qname|constraint-value|json-key
 */
public interface MetadataExtract {
	public void build(QueryExtractMetadata metadata);
}