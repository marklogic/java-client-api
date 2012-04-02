package com.marklogic.client.config;

public interface SuggestionSource extends QueryAnnotatable, Indexable, Suggestable {

	public void useWordLexicon(String collation);
	public void useWordLexicon(String collation, String fragmentScope);
	public void useWordLexicon();
	
}