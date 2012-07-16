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

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.QueryOptions.Facets;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;

public class StringSearchTest {
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
        String optionsName = "facets";

        // Get back facets...
        QueryOptionsBuilder builder = new QueryOptionsBuilder();
        QueryOptionsHandle options = new QueryOptionsHandle()
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
        						
        QueryRange range = options.getConstraint("decade").getSource();
        assertEquals(range.getElement(), new QName(
                "http://marklogic.com/wikipedia", "nominee"));
        assertEquals(range.getAttribute(), new QName("year"));

        QueryTransformResults tresults = builder.emptySnippets();
        options.withTransformResults(tresults);

        QueryOptionsManager queryOptionsMgr =
        	Common.client.newServerConfigManager().newQueryOptionsManager();

        logger.error(options.toXMLString());

        queryOptionsMgr.writeOptions(optionsName, options);

        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("Peck");

        /*
        StringHandle handle = queryMgr.search(qdef, new StringHandle());
        System.err.println(handle.get());
        */

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);
        assertFalse(results.getMetrics().getTotalTime() == -1);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        for (MatchDocumentSummary summary : summaries) {
            MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                assertNotNull(location.getAllSnippetText());
            }
        }

        assertNotNull(summaries);
    }

/*
    @Test
    public void testStringSearch2() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(null);
        qdef.setCriteria("leaf3");

        SearchHandle handle = new SearchHandle();
        handle.setForceDOM(true);
        handle = queryMgr.search(qdef, handle);

        assertNotNull(handle);
    }

    @Test
    public void testStringStringSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(null);
        qdef.setCriteria("leaf3");

        StringHandle handle = new StringHandle();
        handle = queryMgr.search(qdef, handle);

        assertNotNull(handle);
        assertNotNull(handle.get());
    }
*/
}
