package com.marklogic.client.config.search;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Methods that the state element and the top-level options element share.
 * @author cgreer
 *
 */
interface StateOrTopLevel extends Annotatable {

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
