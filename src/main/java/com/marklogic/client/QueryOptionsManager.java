package com.marklogic.client;

import com.marklogic.client.config.search.SearchOptions;

public interface QueryOptionsManager {
    public SearchOptions readOptions(String uri);
    public void writeOptions(String uri, SearchOptions options);
    public void deleteOptions(String uri);
}
