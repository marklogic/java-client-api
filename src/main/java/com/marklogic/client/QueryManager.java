package com.marklogic.client;

import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.io.marker.XMLReadHandle;

import javax.xml.namespace.QName;

public interface QueryManager {
    // constants that can be used as values for pagination
    static final public long DEFAULT_PAGE_LENGTH = 10;
    static final public long START = 1;

    public StringQueryDefinition newStringCriteria(String optionsUri);
    public KeyValueQueryDefinition newKeyValueCriteria(String optionsUri);

    public ElementLocator newElementLocator(QName element);
    public ElementLocator newElementLocator(QName element, QName attribute);
    public KeyLocator newKeyLocator(String key);

    public SearchResults search(QueryDefinition criteria);

    public <T extends XMLReadHandle> T searchAsXml(T handle, QueryDefinition criteria);
}
