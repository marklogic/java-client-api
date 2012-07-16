package com.marklogic.client.config.support;

import com.marklogic.client.config.QueryOptions.QueryTuples;
import com.marklogic.client.config.QueryOptions.QueryValues;

public interface TupleSource {

	void build(QueryTuples tuples);
	void build(QueryValues values);
}