package com.marklogic.client.config.search;

import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;


public interface SearchOptions {

	public abstract void writeTo(OutputStream os) throws JAXBException;

	public abstract String toString();

	public abstract List<SearchOption> getAll();

	public abstract boolean getReturnFacets();

	public abstract void setReturnFacets(boolean returnFacets);

	public abstract void add(SearchOption searchOption);

	public abstract boolean getReturnConstraints();

	public abstract void setReturnConstraints(boolean returnConstraints);

	public abstract boolean getReturnMetrics();

	public abstract void setReturnMetrics(boolean returnMetrics);

	public abstract boolean getReturnPlan();

	public abstract void setReturnPlan(boolean returnPlan);

	public abstract boolean getReturnQText();

	public abstract void setReturnQueryText(boolean returnQueryText);

	public abstract boolean getReturnResults();

	public abstract void setReturnResults(boolean returnResults);

	public abstract boolean getReturnSimilar();

	public abstract void setReturnSimilar(boolean returnSimilar);

	public abstract boolean getDebug();

	public abstract void setDebug(boolean debug);

	public abstract String getFragmentScope();

	public abstract void setFragmentScope(String fragmentScope);

	public abstract int getConcurrencyLevel();

	public abstract void setConcurrencyLevel(int concurrencyLevel);

	public abstract long getPageLength();

	public abstract void setPageLength(long pageLength);

	public abstract double getQualityWeight();

	public abstract void setQualityWeight(double qualityWeight);

	public List<SearchOption> getByClassName(@SuppressWarnings("rawtypes") Class clazz);

}