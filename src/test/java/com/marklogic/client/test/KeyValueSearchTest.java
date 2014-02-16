/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;

public class KeyValueSearchTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();

		// setup namespaces to test kv with namespaces
		NamespacesManager nsMgr = Common.client.newServerConfigManager()
				.newNamespacesManager();

		nsMgr.updatePrefix("ns1", "http://marklogic.com/test-ns1");
		nsMgr.updatePrefix("ns2", "http://marklogic.com/test-ns2");

		Common.release();
		Common.connect();
	}

	@AfterClass
	public static void afterClass() {
		Common.release();
		Common.connectAdmin();
		NamespacesManager nsMgr = Common.client.newServerConfigManager()
				.newNamespacesManager();

		nsMgr.deleteAll();

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
		for (MatchDocumentSummary summary : summaries) {
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) {
				assertNotNull(location.getAllSnippetText());
			}
		}

		assertNotNull(summaries);
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

		qdef.put(queryMgr.newElementLocator(new QName("ns1:leaf")), "leaf3");
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

	@Test
	public void testJsonSearch() throws IOException {
		QueryManager queryMgr = Common.client.newQueryManager();
		KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

		qdef.put(queryMgr.newKeyLocator("firstKey"), "first value");
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
}
