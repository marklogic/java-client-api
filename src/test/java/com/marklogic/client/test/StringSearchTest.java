/*
 * Copyright 2012 MarkLogic Corporation
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

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions.QueryRange;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;

public class StringSearchTest {
	@SuppressWarnings("unused")
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStringSearch() throws IOException, ParserConfigurationException, SAXException {
        String optionsName = writeOptions();

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("grandchild1 OR grandchild4");

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);
        assertFalse(results.getMetrics().getTotalTime() == -1);

        FacetResult[] facets = results.getFacetResults();
        assertNotNull(facets);
        assertTrue(facets.length > 0);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries.length > 0);
        for (MatchDocumentSummary summary : summaries) {
        	MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                assertNotNull(location.getAllSnippetText());
            }
        }

        assertNotNull(summaries);
    }

    @Test
    public void testStringSearch2() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();

        // "em-1"
        StringQueryDefinition qdef = queryMgr.newStringDefinition();
        qdef.setCriteria("10");

        SearchHandle handle = new SearchHandle();
        handle = queryMgr.search(qdef, handle);

        assertNotNull(handle);
    }

    @Test
    public void testStringSearch3() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        // "metatest-1"
        StringQueryDefinition qdef = queryMgr.newStringDefinition();
        qdef.setCriteria("10");

        SearchHandle handle = new SearchHandle();
        handle = queryMgr.search(qdef, handle);

/* UNUSED
        String[] facetNames = handle.getFacetNames();
        FacetResult[] facetResults = handle.getFacetResults();
        MatchDocumentSummary[] matchDocSum = handle.getMatchResults();
        SearchMetrics metrics = handle.getMetrics();
        Document plan = handle.getPlan();
        QueryDefinition def = handle.getQueryCriteria();
        SearchHandle.Report[] reports = handle.getReports();
        SearchHandle.Warning[] warnings = handle.getWarnings();
        long total = handle.getTotalResults();
 */

        assertNotNull(handle);
    }

    @Test
    public void testStringSearch4() throws IOException {
        String optionsName = writeOptions();

        QueryManager queryMgr = Common.client.newQueryManager();

        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("grandchild1 OR grandchild4");

        queryMgr.setView(QueryView.FACETS);
        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        FacetResult[] facets = results.getFacetResults();
        assertNotNull(facets);
        assertTrue(facets.length > 0);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries == null || summaries.length == 0);

        queryMgr.setView(QueryView.RESULTS);
        results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        facets = results.getFacetResults();
        assertTrue(facets == null || facets.length == 0);

        summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertTrue(summaries.length > 0);
    }

    private String writeOptions() {
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
/*
        .withConstraints(
        		builder.constraint("decade", 
        				builder.range(
        						builder.elementAttributeRangeIndex(
        								new QName("http://marklogic.com/wikipedia", "nominee"),
                						new QName("year"), 
                						builder.rangeType(new QName("xs:gYear"))),
                						Facets.FACETED, FragmentScope.DOCUMENTS,
                						builder.buckets(
		        						builder.bucket("2000s", "2000s", null, null),
		        		                builder.bucket("1990s", "1990s", "1990", "2000"),
		        		                builder.bucket("1980s", "1980s", "1980", "1990"),
		        		                builder.bucket("1970s", "1970s", "1970", "1980"),
		        		                builder.bucket("1960s", "1960s", "1960", "1970"),
		        		                builder.bucket("1950s", "1950s", "1950", "1960"),
		        		                builder.bucket("1940s", "1940s", "1940", "1950"),
		        		                builder.bucket("1930s", "1930s", "1930", "1940"),
		        		                builder.bucket("1920s", "1920s", "1920", "1930")),
        							"limit=10")));
 */

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
