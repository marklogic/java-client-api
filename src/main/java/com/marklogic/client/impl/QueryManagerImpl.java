package com.marklogic.client.impl;

import com.marklogic.client.ElementLocator;
import com.marklogic.client.KeyLocator;
import com.marklogic.client.QueryManager;
import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.jaxb.Response;
import com.marklogic.client.io.SearchHandle;
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
    
    public StringQueryDefinition newStringCriteria(String optionsUri) {
        return new StringQueryDefinitionImpl(optionsUri);
    }

    @Override
    public KeyValueQueryDefinition newKeyValueCriteria(String optionsUri) {
        return new KeyValueQueryDefinitionImpl(optionsUri);
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
    public <T extends StructureReadHandle> T search(T searchHandle, QueryDefinition querydef) {
        return search(searchHandle, querydef, 1, null);
    }

    @Override
    public <T extends StructureReadHandle> T search(T searchHandle, QueryDefinition querydef, long start) {
        return search(searchHandle, querydef, start, null);
    }

    @Override
    public <T extends StructureReadHandle> T search(T searchHandle, QueryDefinition querydef, String transactionId) {
        return search(searchHandle, querydef, 1, transactionId);
    }

    public <T extends StructureReadHandle> T search(T searchHandle, QueryDefinition querydef, long start, String transactionId) {
        if (searchHandle instanceof SearchHandle) {
            ((SearchHandle) searchHandle).setQueryCriteria(querydef);
        }
        searchHandle.receiveContent(services.search(searchHandle.receiveAs(), querydef, start, transactionId));
        return searchHandle;
    }
}
