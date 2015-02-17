/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.admin.config.support;

import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;

/**
 * A special marker for indicating that the source of
 * tuples or values is the URI lexicon.
 */
@SuppressWarnings("deprecation")
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
