package com.marklogic.client.admin.config.support;

import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;

public interface TupleSource {

	void build(QueryTuples tuples);
	void build(QueryValues values);
}