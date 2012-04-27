/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.config;

import java.util.List;


public interface QueryOptions extends BoundQueryOption, StateOrTopLevel {
	
	public Boolean getReturnFacets();

	public void setReturnFacets(Boolean returnFacets);

	public Boolean getReturnConstraints();

	public void setReturnConstraints(Boolean returnConstraints);

	public Boolean getReturnMetrics();

	public void setReturnMetrics(Boolean returnMetrics);

	public Boolean getReturnPlan();

	public void setReturnPlan(Boolean returnPlan);

	public Boolean getReturnQText();

	public void setReturnQueryText(Boolean returnQueryText);

	public Boolean getReturnResults();

	public void setReturnResults(Boolean returnResults);

	public Boolean getReturnSimilar();

	public void setReturnSimilar(Boolean returnSimilar);

	public String getFragmentScope();

	public void setFragmentScope(String fragmentScope);

	public Integer getConcurrencyLevel();

	public void setConcurrencyLevel(Integer concurrencyLevel);

	public List<String> getSearchOptions();

	public List<com.marklogic.client.configpojos.Constraint> getConstraints();

	public Term getTerm();

	public Grammar getGrammar();

	public List<Operator> getOperators();

	public TransformResults getTransformResults();

}