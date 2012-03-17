package com.marklogic.client.impl;

import com.marklogic.client.QueryManager;
import com.marklogic.client.config.search.MarkLogicIOException;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchMetrics;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.jaxb.Metrics;
import com.marklogic.client.config.search.jaxb.Response;
import com.marklogic.client.docio.XMLReadHandle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Date;

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
    public SearchResults search(QueryDefinition criteria) {
        try {
            jc = JAXBContext.newInstance("com.marklogic.client.config.search.jaxb");
            unmarshaller = jc.createUnmarshaller();
            m = jc.createMarshaller();
            
            InputStream resultXML = null;
            if (criteria instanceof StringQueryDefinition) {
                resultXML = services.stringSearch(InputStream.class, null, ((StringQueryDefinition) criteria).getCriteria(), null);
            } else {
                throw new UnsupportedOperationException("Unexpected search criteria: " + criteria.getClass().getName());
            }
            jaxbResponse = (Response) unmarshaller.unmarshal(resultXML);
        } catch (JAXBException e) {
            throw new MarkLogicIOException(
                    "Could not construct search results because of thrown JAXB Exception",
                    e);
        }

        SearchResults results = new SearchResultsImpl(criteria, jaxbResponse);
        return results;
    }

    @Override
    public <T extends XMLReadHandle> T searchAsXml(T handle, QueryDefinition criteria) {
        return null;
    }

    
    
}
