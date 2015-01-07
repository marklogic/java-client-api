/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions.QueryRange;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.FacetValue;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;

@SuppressWarnings("deprecation")
public class StringSearchTest {
	@SuppressWarnings("unused")
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStringSearch()
    throws IOException, ParserConfigurationException, SAXException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
        String optionsName = writeOptions();

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("grandchild1 OR grandchild4");
        qdef.setDirectory("/sample/");

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);
        assertFalse(results.getMetrics().getTotalTime() == -1);

        FacetResult[] facets = results.getFacetResults();
        assertNotNull(facets);
        assertEquals("expected 1 facet", 1, facets.length);
        FacetValue[] facetVals = facets[0].getFacetValues();
        assertEquals("expected 6 facet values", 6, facetVals.length);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals("expected 2 results", 2, summaries.length);
    }

    @Test
    public void testStringSearch2() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition();
        qdef.setCriteria("10");
        qdef.setDirectory("/sample/");

        SearchHandle handle = new SearchHandle();
        handle = queryMgr.search(qdef, handle);
        assertNotNull(handle);

		MatchDocumentSummary[] summaries = handle.getMatchResults();
		assertNotNull(summaries);
		assertEquals("expected 2 results", 2, summaries.length);

		for (MatchDocumentSummary summary : summaries) {
        	MatchLocation[] locations = summary.getMatchLocations();
    		assertEquals("expected 1 match location", 1, locations.length);
			for (MatchLocation location : locations) {
				assertNotNull(location.getAllSnippetText());
			}
        }
    }

    @Test
    public void testStringSearch4()
    throws IOException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
        String optionsName = writeOptions();

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("grandchild1 OR grandchild4");
        qdef.setDirectory("/sample/");

        queryMgr.setView(QueryView.FACETS);
        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        FacetResult[] facets = results.getFacetResults();
        assertNotNull(facets);
        assertEquals("expected 1 facet", 1, facets.length);
        FacetValue[] facetVals = facets[0].getFacetValues();
        assertEquals("expected 6 facet values", 6, facetVals.length);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries == null || summaries.length == 0);

        queryMgr.setView(QueryView.RESULTS);
        results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        facets = results.getFacetResults();
        assertTrue(facets == null || facets.length == 0);

        summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals("expected 2 results", 2, summaries.length);
    }

    @Test
    public void testFailedSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        queryMgr.setView(QueryView.RESULTS);

        StringQueryDefinition qdef = queryMgr.newStringDefinition();
        qdef.setCriteria("criteriaThatShouldNotMatchAnyDocument");
        qdef.setDirectory("/sample/");

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries == null || summaries.length == 0);
    }

    @Test
    public void testJSON()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
        String optionsName = writeOptions();

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("grandchild1 OR grandchild4");
        qdef.setDirectory("/sample/");

        queryMgr.setView(QueryView.FACETS);

        // create a handle for the search results
        StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

        // run the search
        queryMgr.search(qdef, resultsHandle);

        assertEquals("{", resultsHandle.get().substring(0, 1)); // It's JSON, right?
    }

    private String writeOptions()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
        String optionsName = "facets";

        // Get back facets...
        QueryOptionsBuilder builder = new QueryOptionsBuilder();
		QueryRange grandchildRange = builder.range(
				builder.elementRangeIndex(
						new QName("grandchild"),
						builder.stringRangeType("http://marklogic.com/collation/")
						));
		grandchildRange.setDoFacets(true);
		QueryOptionsHandle options = new QueryOptionsHandle().withConstraints(
        		builder.constraint("grandchild",grandchildRange)
        		);

        QueryRange range = options.getConstraint("grandchild").getSource();
        assertEquals(range.getElement(), new QName("grandchild"));

        QueryTransformResults tresults = builder.emptySnippets();
        options.withTransformResults(tresults);

        QueryOptionsManager queryOptionsMgr =
        	Common.client.newServerConfigManager().newQueryOptionsManager();

        queryOptionsMgr.writeOptions(optionsName, options);

        return optionsName;
    }
}
