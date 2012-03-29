package com.marklogic.client.config.search;

import java.util.List;


public interface QueryOptions extends JAXBBackedQueryOption, StateOrTopLevel {
	
	public Boolean getReturnFacets();

	public void setReturnFacets(Boolean returnFacets);

	public void add(JAXBBackedQueryOption jAXBBackedQueryOption);

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

	public List<Constraint> getConstraints();

	public Term getTerm();

	public Grammar getGrammar();

	public List<Operator> getOperators();

	public TransformResults getTransformResults();

}