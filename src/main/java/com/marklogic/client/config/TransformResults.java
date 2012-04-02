package com.marklogic.client.config;

import java.util.List;

import com.marklogic.client.ElementLocator;

public interface TransformResults extends JAXBBackedQueryOption {

	public enum Apply { SNIPPET, RAW };
	
	public String getApply();
	public void setApply(String apply);
	public FunctionRef getTransformFunction();
	public void setTransformFunction(FunctionRef function);
	
	public void setPerMatchTokens(long perMatchTokens);
	public long getPerMatchTokens();

	public void setMaxMatches(long maxMatches);
	public long getMaxMatches();
	
	public void setMaxSnippetChars(long maxSnippetChars);
	public long getMaxSnippetChars();
	
	public List<ElementLocator> getPreferredElements();
	public void setPreferredElements(List<ElementLocator> elements);
	
}