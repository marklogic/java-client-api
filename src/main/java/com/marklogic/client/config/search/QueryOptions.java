package com.marklogic.client.config.search;

import java.util.List;


public interface QueryOptions extends JAXBBackedQueryOption, StateOrTopLevel {
	
	public boolean getReturnFacets();

	public void setReturnFacets(boolean returnFacets);

	public void add(JAXBBackedQueryOption jAXBBackedQueryOption);

	public boolean getReturnConstraints();

	public void setReturnConstraints(boolean returnConstraints);

	public boolean getReturnMetrics();

	public void setReturnMetrics(boolean returnMetrics);

	public boolean getReturnPlan();

	public void setReturnPlan(boolean returnPlan);

	public boolean getReturnQText();

	public void setReturnQueryText(boolean returnQueryText);

	public boolean getReturnResults();

	public void setReturnResults(boolean returnResults);

	public boolean getReturnSimilar();

	public void setReturnSimilar(boolean returnSimilar);

	public String getFragmentScope();

	public void setFragmentScope(String fragmentScope);

	public int getConcurrencyLevel();

	public void setConcurrencyLevel(Integer concurrencyLevel);

	public List<String> getSearchOptions();

	public List<Constraint> getConstraints();

	public Term getTerm();

	public Grammar getGrammar();

	public List<Operator> getOperators();

	public TransformResults getTransformResults();

}