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

import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.configpojos.Constraint;
import com.marklogic.client.configpojos.Range;
import com.marklogic.client.configpojos.TransformResults;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class StringSearchTest {
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
        QueryOptionsHandle options = new QueryOptionsHandle();
        options.withReturnFacets(true);
        Range range = new Range().inside(new Constraint("decade"));
        range.doFacets(true);
        options.withConstraintDefinition(range);

        assertEquals("Wrong name returned from range object", "decade",
                range.getConstraintName());

        range.withElement("http://marklogic.com/wikipedia", "nominee")
                .withAttribute("year").withType(new QName("xs:gYear"));

        assertEquals(range.getElement(), new QName(
                "http://marklogic.com/wikipedia", "nominee"));
        assertEquals(range.getAttribute(), new QName("year"));

        range.withType(new QName("xs:gYear"));

        range.withBucket("2000s", "2000s", null, null)
                .withBucket("1990s", "1990s", "1990", "2000")
                .withBucket("1980s", "1980s", "1980", "1990")
                .withBucket("1970s", "1970s", "1970", "1980")
                .withBucket("1960s", "1960s", "1960", "1970")
                .withBucket("1950s", "1950s", "1950", "1960")
                .withBucket("1940s", "1940s", "1940", "1950")
                .withBucket("1930s", "1930s", "1930", "1940")
                .withBucket("1920s", "1920s", "1920", "1930")
                .withFacetOption("limit=10");

        TransformResults tresults = new TransformResults();
        tresults.setApply("raw");
        options.withTransformResults(tresults);

        QueryOptionsManager queryOptionsMgr = Common.client.newQueryOptionsManager();

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
