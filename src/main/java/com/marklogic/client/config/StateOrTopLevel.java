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

import org.w3c.dom.Element;

/**
 * Methods that the state element and the top-level options element share.
 * @author cgreer
 *
 */
interface StateOrTopLevel extends JAXBBackedQueryOption, QueryAnnotatable {

	public Element getAdditionalQuery();
	public Boolean getDebug();
	public List<Long> getForests();
	public Long getPageLength();
	public Double getQualityWeight();
	public String getSearchableExpression();
	public List<String> getSearchOptions();
	public List<SortOrder> getSortOrders();
	public TransformResults getTransformResults();
	
	public void setAdditionalQuery(Element ctsQuery);
	public void setDebug(Boolean debug);
	public void setForest(Long forest);
	public void setForests(List<Long> forests);
	public void setPageLength(Long pageLength);
	public void setQualityWeight(Double qualityWeight);
	public void setSearchableExpression(String searchableExpression);
	public void setSearchOptions(List<String> searchOptions);
	public void setTransformResults(TransformResults transformResults);

}
