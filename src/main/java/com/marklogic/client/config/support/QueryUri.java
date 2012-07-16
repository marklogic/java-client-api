package com.marklogic.client.config.support;

import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.QueryOptions.QueryTuples;
import com.marklogic.client.config.QueryOptions.QueryValues;

public enum QueryUri implements TupleSource {

	YES;

	@Override
	public void build(QueryValues values) {
		values.setUri();
	}
	
	public String toString() {
		return "";
	}

	@Override
	public void build(QueryTuples tuples) {
		tuples.setUri();
}


}