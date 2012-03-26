package com.marklogic.client.impl;

import com.marklogic.client.ElementLocator;
import com.marklogic.client.Format;
import com.marklogic.client.KeyLocator;
import com.marklogic.client.QueryManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.StructuredQueryBuilder;
import com.marklogic.client.config.search.StructuredQueryDefinition;
import com.marklogic.client.config.search.jaxb.Response;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryManagerImpl implements QueryManager {
    protected JAXBContext jc = null;
    protected Unmarshaller unmarshaller = null;
    protected Marshaller m = null;
    private Response jaxbResponse = null;
    private RESTServices services = null;

    public QueryManagerImpl(RESTServices services) {
        this.services = services;
    }
    
    public StringQueryDefinition newStringCriteria(String optionsName) {
        return new StringQueryDefinitionImpl(optionsName);
    }

    @Override
    public KeyValueQueryDefinition newKeyValueCriteria(String optionsName) {
        return new KeyValueQueryDefinitionImpl(optionsName);
    }

    public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName) {
        return new StructuredQueryBuilder(optionsName);
    }
    
    @Override
    public ElementLocator newElementLocator(QName element) {
        return new ElementLocatorImpl(element);
    }

    @Override
    public ElementLocator newElementLocator(QName element, QName attribute) {
        return new ElementLocatorImpl(element, attribute);
    }

    @Override
    public KeyLocator newKeyLocator(String key) {
        return new KeyLocatorImpl(key);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle) {
        return search(querydef, searchHandle, 1, null);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start) {
        return search(querydef, searchHandle, start, null);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, Transaction transaction) {
        return search(querydef, searchHandle, 1, transaction);
    }

    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start, Transaction transaction) {
        if (searchHandle instanceof SearchHandle) {
            ((SearchHandle) searchHandle).setQueryCriteria(querydef);
        }
        String mimetype = null;
        if (searchHandle.getFormat() == Format.XML) {
            mimetype = "application/xml";            
        } else if (searchHandle.getFormat() == Format.JSON) {
            mimetype = "application/json";
        } else {
            throw new UnsupportedOperationException("Only XML and JSON search results are possible.");
        }

        String tid = transaction == null ? null : transaction.getTransactionId();
        searchHandle.receiveContent(services.search(searchHandle.receiveAs(), querydef, mimetype, start, tid));
        return searchHandle;
    }

    @Override
    public MatchDocumentSummary findOne(QueryDefinition querydef) {
        SearchHandle results = search(querydef, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        if (summaries.length > 0) {
            return summaries[0];
        } else {
            return null;
        }
    }

    @Override
    public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction) {
        SearchHandle results = search(querydef, new SearchHandle(), transaction);
        MatchDocumentSummary[] summaries = results.getMatchResults();
        if (summaries.length > 0) {
            return summaries[0];
        } else {
            return null;
        }
    }
}
