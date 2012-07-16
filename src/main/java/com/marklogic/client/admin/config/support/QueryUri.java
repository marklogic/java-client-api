package com.marklogic.client.admin.config.support;

import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;

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