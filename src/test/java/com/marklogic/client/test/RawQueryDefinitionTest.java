/*
 * Copyright 2013 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class RawQueryDefinitionTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
		queryMgr = Common.client.newQueryManager();
	}

	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@BeforeClass
	public static void setupTestOptions() throws FileNotFoundException {
		Common.connectAdmin();

		QueryOptionsManager queryOptionsManager = Common.client.newServerConfigManager().newQueryOptionsManager();
		File options = new File("src/test/resources/alerting-options.xml");
		queryOptionsManager.writeOptions("alerts", new FileHandle(options));
		Common.client.newServerConfigManager().setServerRequestLogging(true);
		Common.release();
		Common.connect();
		
		// write three files for alert tests.
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write("/alert/first.xml", new FileHandle(new File("src/test/resources/alertFirst.xml")));
		docMgr.write("/alert/second.xml", new FileHandle(new File("src/test/resources/alertSecond.xml")));
		docMgr.write("/alert/third.xml", new FileHandle(new File("src/test/resources/alertThird.xml")));
    }
	
	private static QueryManager queryMgr;

	String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
	String tail = "</search:search>";

	String qtext1 = "<search:qtext>false</search:qtext>";
	String qtext2 = "<search:qtext>favorited:true</search:qtext>";
	String qtext3 = "<search:qtext>leaf3</search:qtext>";

	StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);

	String structuredString = qb.valueConstraint("name", "one").serialize();

	String optionsString = "<search:options >"
			+ "<search:constraint name=\"favorited\">" + "<search:value>"
			+ "<search:element name=\"favorited\" ns=\"\"/>"
			+ "</search:value>" + "</search:constraint>" + "</search:options>";

	private void check(StructureWriteHandle handle, String optionsName) {
		RawCombinedQueryDefinition rawCombinedQueryDefinition;

		if (optionsName == null) {
			rawCombinedQueryDefinition =
				queryMgr.newRawCombinedQueryDefinition(handle);
		} else {
			rawCombinedQueryDefinition =
				queryMgr.newRawCombinedQueryDefinition(handle, optionsName);
		}
		SearchHandle results;
		results = queryMgr.search(rawCombinedQueryDefinition,
				new SearchHandle());

		check(results);
	}
	private void check(SearchHandle results) {
		assertNotNull(results);

		assertFalse(results.getMetrics().getTotalTime() == -1);

		MatchDocumentSummary[] summaries = results.getMatchResults();
		assertNotNull(summaries);
		assertTrue(summaries.length > 0);
		for (MatchDocumentSummary summary : summaries) {
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) {
				assertNotNull(location.getAllSnippetText());
			}
		}
	}
	private void check(StructureWriteHandle handle) {
		check(handle, null);
	}

	@Test
	public void testCombinedSearches() throws IOException {
		// Structured Query, No Options
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
		StructuredQueryDefinition t = qb.term("leaf3");
		check(new StringHandle(head + t.serialize() + tail));

		// String query no options
		String str = head + qtext1 + tail;
		check(new StringHandle(str).withMimetype("application/xml"));

		// String query plus options
		str = head + qtext2 + optionsString + tail;
		check(new StringHandle(str));

		// Structured query plus options
		str = head + t.serialize() + optionsString + tail;
		check(new StringHandle(str));

		// Structured query plus options
		str = head + t.serialize() + optionsString + tail;
		check(new StringHandle(str), "alerts");

		// All three
		str = head + qtext3 + t.serialize() + optionsString + tail;
		System.out.println(str);
		check(new StringHandle(str));
	}

	@Test
	public void testFailedSearch() throws IOException {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.term(
				"criteriaThatShouldNotMatchAnyDocument");

		RawCombinedQueryDefinition queryDef =
				queryMgr.newRawCombinedQueryDefinition(
						new StringHandle(head + t.serialize() + tail)
						.withMimetype("application/xml")
						);

		SearchHandle results = queryMgr.search(queryDef, new SearchHandle());
        assertNotNull(results);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertTrue(summaries == null || summaries.length == 0);
	}
}
