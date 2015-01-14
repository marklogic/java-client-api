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
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;

public class KeyValueSearchTest {
	@BeforeClass
	public static void beforeClass()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		Common.connect();
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");

	}

	@AfterClass
	public static void afterClass()
	throws ForbiddenUserException, FailedRequestException {
		Common.release();

	}

	@Test
	public void testKVSearch() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newElementLocator(new QName("leaf")), "leaf3");
		SearchHandle results = queryMgr.search(qdef, new SearchHandle());
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

	@Test(expected=FailedRequestException.class)
	public void testKVSearchBadNamespacePrefix() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newElementLocator(new QName("badprefix:leaf")),
				"leaf3");
		@SuppressWarnings("unused")
		SearchHandle results = queryMgr.search(qdef, new SearchHandle());
		fail("Test should have thrown a Failed Request with bad prefix");
	}
	
	@Test(expected=FailedRequestException.class)
	public void testKVSearchInadequateQName() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newElementLocator(new QName("http://marklogic.com/test-namespace-with-no-prefix", "leaf")),
				"leaf3");
		@SuppressWarnings("unused")
		SearchHandle results = queryMgr.search(qdef, new SearchHandle());
		fail("Test should have thrown a Failed Request with bad prefix");
	}

	@Test
	public void testKVSearchGoodNamespacePrefix() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newElementLocator(new QName("json:thirdKey")), "3");
		SearchHandle results = queryMgr.search(qdef, new SearchHandle());
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

	@Test
	public void testJsonSearch() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newKeyLocator("firstKey"), "first value");
		SearchHandle results = queryMgr.search(qdef, new SearchHandle());
		assertNotNull(results);
		assertFalse(results.getMetrics().getTotalTime() == -1);

		MatchDocumentSummary[] summaries = results.getMatchResults();
		assertNotNull(summaries);
		for (MatchDocumentSummary summary : summaries) {
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) {
				assertNotNull(location.getAllSnippetText());
			}
		}
	}
}
