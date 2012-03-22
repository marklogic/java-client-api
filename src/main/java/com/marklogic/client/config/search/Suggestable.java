package com.marklogic.client.config.search;

import java.util.List;


public interface Suggestable  {

	public void addSuggestionOption(String option);
	public List<String> getSuggestionOptions();
	
}
