package com.marklogic.client.config.search;

public interface SuggestionSource extends Annotate, Indexable, Suggestable {

	public void useWordLexicon(String collation);
	public void useWordLexicon(String collation, String fragmentScope);
	public void useWordLexicon();
	
}