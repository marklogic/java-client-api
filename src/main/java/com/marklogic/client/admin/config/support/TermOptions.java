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

import java.util.List;

/**
 * Marks classes and defined methods for options that
 * have term options and weights assigned to them.
 * Used indirectly in {@link com.marklogic.client.admin.config.QueryOptionsBuilder} to construct
 * {@link com.marklogic.client.admin.config.QueryOptions.QueryCustom} and {@link com.marklogic.client.admin.config.QueryOptions.QueryTerm}.
 */
public interface TermOptions {

	public List<String> getTermOptions();
	public void setTermOptions(List<String> termOptions);
	

	public Double getWeight();
	public void setWeight(Double weight);
}
