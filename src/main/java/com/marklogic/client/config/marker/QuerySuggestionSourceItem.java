package com.marklogic.client.config.marker;

import com.marklogic.client.config.QueryOptionsBuilder.QuerySuggestionSource;

public interface QuerySuggestionSourceItem {

	void build(QuerySuggestionSource suggestionSource);

}
