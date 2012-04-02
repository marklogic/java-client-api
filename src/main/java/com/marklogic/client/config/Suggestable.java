package com.marklogic.client.config;

import java.util.List;


public interface Suggestable  {

	public void addSuggestionOption(String option);
	public List<String> getSuggestionOptions();
	
}
