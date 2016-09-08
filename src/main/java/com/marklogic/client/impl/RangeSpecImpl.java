/*
 * Copyright 2012-2016 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.admin.config.support.IndexSpecImpl;
import com.marklogic.client.admin.config.support.RangeIndexed;
import com.marklogic.client.admin.config.support.RangeSpec;

public class RangeSpecImpl extends IndexSpecImpl implements RangeSpec {

	private String type;
	private String collation;

    @Override
	public void setType(String type) {
		this.type = type;
	}

    @Override
	public void setCollation(String collation) {
		this.collation = collation;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getCollation() {
		return collation;
	}

    @Override
	public void build(RangeIndexed indexable) {
		indexable.setType(getType());
	    indexable.setCollation(getCollation());	
	    super.build(indexable);
	}
}
