package com.marklogic.client.config.marker;

import com.marklogic.client.config.QueryOptionsBuilder.QueryOptions;

/**
 * Marks the top-level components of a QueryOptions
 * @author cgreer
 *
 */
public interface QueryOptionsItem {

	public void build(QueryOptions options);
}
