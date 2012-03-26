package com.marklogic.client;

import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.StructuredQueryBuilder;
import com.marklogic.client.config.search.StructuredQueryDefinition;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;

import javax.xml.namespace.QName;

public interface QueryManager {
    // constants that can be used as values for pagination
    static final public long DEFAULT_PAGE_LENGTH = 10;
    static final public long START = 1;

    public StringQueryDefinition newStringCriteria(String optionsName);
    public KeyValueQueryDefinition newKeyValueCriteria(String optionsName);
    public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName);

    public ElementLocator newElementLocator(QName element);
    public ElementLocator newElementLocator(QName element, QName attribute);
    public KeyLocator newKeyLocator(String key);

    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, Transaction transaction);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start, Transaction transaction);

    public MatchDocumentSummary findOne(QueryDefinition querydef);
    public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction);
}
