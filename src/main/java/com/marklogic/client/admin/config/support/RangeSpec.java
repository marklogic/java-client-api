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


/**
 * Marks classes and defines methods for configuring a
 * range index description.
 * Used only in {@link com.marklogic.client.admin.config.QueryOptionsBuilder}.*RangeIndex(...) expressions.
 */
public interface RangeSpec extends IndexSpec {
	
	public void setType(String type);

	public void setCollation(String collation);

	public String getType();

	public String getCollation();
	
	public void build(RangeIndexed rangeIndexed);
	

}
