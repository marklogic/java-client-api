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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;

public class StructuredSearchTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStructuredSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

        for (QueryDefinition t:new QueryDefinition[]{
            qb.term("leaf3"), qb.build(qb.value(qb.element("leaf"), "leaf3"))
        }) {
        	SearchHandle results = queryMgr.search(t, new SearchHandle());
        	assertNotNull(results);
        	assertFalse(results.getMetrics().getTotalTime() == -1);

        	MatchDocumentSummary[] summaries = results.getMatchResults();
        	assertNotNull(summaries);
    		assertEquals("expected 1 result", 1, summaries.length);
        	for (MatchDocumentSummary summary : summaries) {
        		MatchLocation[] locations = summary.getMatchLocations();
        		assertEquals("expected 1 match location", 1, locations.length);
        		for (MatchLocation location : locations) {
        			assertNotNull(location.getAllSnippetText());
        		}
        	}
        }
    }

    @Test
    public void testStructuredSearch1() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

        for (QueryDefinition t:new QueryDefinition[]{
                qb.term("leaf3"), qb.build(qb.value(qb.element("leaf"), "leaf3"))
            }) {

        	MatchDocumentSummary summary = queryMgr.findOne(t);
        	assertNotNull(summary);

        	GenericDocumentManager docMgr = Common.client.newDocumentManager();
       		assertNotNull("Document exists", docMgr.exists(summary.getUri()));
        }
    }

    @Test
    public void testStructuredSearch2() throws IOException {
    	QueryManager queryMgr = Common.client.newQueryManager();
        
    	EditableNamespaceContext namespaces = new EditableNamespaceContext(); 
    	namespaces.put("x", "root.org");
    	namespaces.put("y", "target.org");
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(namespaces);
    	
    	StructuredQueryDefinition qdef = qb.geospatial(
    			qb.geoPath(qb.pathIndex("/x:geo/y:path")),
    			qb.box(1, 2, 3, 4));
    	
    	SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    	assertNotNull(results);

    	MatchDocumentSummary[] summaries = results.getMatchResults();
    	assertTrue(summaries == null || summaries.length == 0);
    }

    
    @Test
    public void testFailedSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
        StructuredQueryDefinition qdef = qb.term(
        		"criteriaThatShouldNotMatchAnyDocument");

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries == null || summaries.length == 0);
    }

    @Test
    public void testJSON() {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
        StructuredQueryDefinition t = qb.term("leaf3");

        // create a handle for the search results
        StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

        // run the search
        queryMgr.search(t, resultsHandle);

        assertEquals("{", resultsHandle.get().substring(0, 1)); // It's JSON, right?
    }

    @Test
    public void testExtractMetadata() throws SAXException, IOException {
        QueryManager queryMgr = Common.client.newQueryManager();

        String combined =
			"<search xmlns=\"http://marklogic.com/appservices/search\">"+
			"<query>"+
			"<value-query>"+
			"<element ns=\"http://marklogic.com/xdmp/json\" name=\"firstKey\"/>"+
			"<text>first value</text>"+
			"</value-query>"+
			"</query>"+
			"<options>"+
			"<extract-metadata>"+
			"<qname elem-ns=\"http://marklogic.com/xdmp/json\" elem-name=\"subKey\"/>"+
			"</extract-metadata>"+
			"</options>"+
			"</search>";
		StringHandle rawHandle = new StringHandle(combined);

		RawCombinedQueryDefinition rawDef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

		SearchHandle sh = queryMgr.search(rawDef, new SearchHandle());

		MatchDocumentSummary[] summaries = sh.getMatchResults();
    	assertNotNull(summaries);
		assertEquals("expected 1 result", 1, summaries.length);

		MatchDocumentSummary matchResult = summaries[0];
		Document metadata = matchResult.getMetadata();
		Element subKey = (Element)
		metadata.getElementsByTagNameNS("http://marklogic.com/xdmp/json", "subKey").item(0);
		assertEquals("string", subKey.getAttribute("type"));
		assertEquals("sub value", subKey.getTextContent());

		String docStr    = Common.testDocumentToString(metadata);
		String handleStr = matchResult.getMetadata(new StringHandle()).get();
		assertXMLEqual("Different metadata for handle", docStr, handleStr);

		Document snippet = matchResult.getSnippets()[0];
		docStr    = Common.testDocumentToString(snippet);
		handleStr = matchResult.getSnippetIterator(new StringHandle()).next().get();
		assertXMLEqual("Different snippet for handle", docStr, handleStr);
    }
}
