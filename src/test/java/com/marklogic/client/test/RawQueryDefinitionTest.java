/*
 * Copyright 2013-2014 MarkLogic Corporation
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
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.ValuesDefinition;

public class RawQueryDefinitionTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
		queryMgr = Common.client.newQueryManager();
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
	}

	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@BeforeClass
	public static void setupTestOptions()
	throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
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

	String optionsString = "<search:options >"
			+ "<search:constraint name=\"favorited\">" + "<search:value>"
			+ "<search:element name=\"favorited\" ns=\"\"/>"
			+ "</search:value>" + "</search:constraint>"
			+ "<search:search-option>relevance-trace</search:search-option>"
			+ "</search:options>";

	String lexiconOptions = "<options xmlns=\"http://marklogic.com/appservices/search\">"
			+ "<values name=\"grandchild\">"
			+ "<range type=\"xs:string\">"
			+ "<element ns=\"\" name=\"grandchild\"/>"
			+ "</range>"
			+ "<values-option>limit=2</values-option>"
			+ "</values>"
			+ "<tuples name=\"co\">"
			+ "<range type=\"xs:double\">"
			+ "<element ns=\"\" name=\"double\"/>"
			+ "</range>"
			+ "<range type=\"xs:int\">"
			+ "<element ns=\"\" name=\"int\"/>"
			+ "</range>"
			+ "</tuples>"
			+ "<tuples name=\"n-way\">"
			+ "<range type=\"xs:double\">"
			+ "<element ns=\"\" name=\"double\"/>"
			+ "</range>"
			+ "<range type=\"xs:int\">"
			+ "<element ns=\"\" name=\"int\"/>"
			+ "</range>"
			+ "<range type=\"xs:string\">"
			+ "<element ns=\"\" name=\"string\"/>"
			+ "</range>"
			+ "<values-option>ascending</values-option>"
			+ "</tuples>"
			+ "<return-metrics>true</return-metrics>"
			+ "<return-values>true</return-values>" + "</options>";

	private void check(StructureWriteHandle handle, String optionsName) {
		RawCombinedQueryDefinition rawCombinedQueryDefinition;

		if (optionsName == null) {
			rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(handle);
		} else {
			rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(handle, optionsName);
		}
		// StringHandle stringResults = null;
		// stringResults = queryMgr.search(rawCombinedQueryDefinition,
		// new StringHandle());
		// System.out.println(stringResults.get());

		SearchHandle results;
		results = queryMgr.search(rawCombinedQueryDefinition,
				new SearchHandle());

		checkResults(results);
	}

	private void checkResults(SearchHandle results) {
		assertNotNull(results);

		assertFalse(results.getMetrics().getTotalTime() == -1);

		MatchDocumentSummary[] summaries = results.getMatchResults();
		assertNotNull(summaries);
		assertTrue(summaries.length > 0);
		for (MatchDocumentSummary summary : summaries) {
			assertTrue("Mime type of document",
					summary.getMimeType().matches("(application|text)/xml"));
			assertEquals("Format of document", Format.XML, summary.getFormat());
			Document relevanceTrace = summary.getRelevanceInfo();
			if (relevanceTrace != null) {
				assertEquals(relevanceTrace.getDocumentElement().getLocalName(),"relevance-info");
			}
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
		check(new StringHandle(str));
	}

	@Test
	public void testFailedSearch() throws IOException {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.term("criteriaThatShouldNotMatchAnyDocument");

		RawCombinedQueryDefinition queryDef = queryMgr.newRawCombinedQueryDefinition(
				new StringHandle(head + t.serialize() + tail)
				.withMimetype("application/xml")
				);

		SearchHandle results = queryMgr.search(queryDef, new SearchHandle());
		assertNotNull(results);

		MatchDocumentSummary[] summaries = results.getMatchResults();
		assertTrue(summaries == null || summaries.length == 0);

		StringHandle criteria = new StringHandle().withFormat(Format.XML);
		criteria.set("<q:query xmlns:q='" + RawQueryByExampleDefinition.QBE_NS
				+ "'>"
				+ "<q:word>criteriaThatShouldNotMatchAnyDocument</q:word>"
				+ "</q:query>");

		RawQueryByExampleDefinition qbe = queryMgr.newRawQueryByExampleDefinition(criteria);

		results = queryMgr.search(qbe, new SearchHandle());
		assertNotNull(results);

		summaries = results.getMatchResults();
		assertTrue(summaries == null || summaries.length == 0);
	}

	@Test
	public void testByExampleSearch() throws IOException, SAXException, XpathException {
		StringHandle criteria = new StringHandle().withFormat(Format.XML);
		criteria.set("<q:query xmlns:q='" + RawQueryByExampleDefinition.QBE_NS
				+ "'>" + "<favorited>true</favorited>" + "</q:query>");

		RawQueryByExampleDefinition qbe = queryMgr
				.newRawQueryByExampleDefinition(criteria);

		SearchHandle results = queryMgr.search(qbe, new SearchHandle());

		checkResults(results);

		String output = queryMgr.validate(qbe, new StringHandle()).get();
		assertNotNull("Empty XML validation", output);
		assertXMLEqual("Failed to validate QBE", output,
				"<q:valid-query xmlns:q=\"http://marklogic.com/appservices/querybyexample\"/>");

		output = queryMgr.convert(qbe, new StringHandle()).get();
		assertNotNull("Empty XML conversion", output);
		assertXpathEvaluatesTo(
				"favorited",
				"string(/*[local-name()='search']/*[local-name()='query']/*[local-name()='value-query']/*[local-name()='element']/@name)",
				output);
		assertXpathEvaluatesTo(
				"true",
				"string(/*[local-name()='search']/*[local-name()='query']/*[local-name()='value-query']/*[local-name()='text'])",
				output);

		criteria.withFormat(Format.JSON).set(
						"{"+
						"\"$format\":\"xml\","+
						"\"$query\":{\"favorited\":\"true\"}"+
						"}"
						);
		output = queryMgr.search(qbe, new StringHandle().withFormat(Format.JSON)).get();
		assertNotNull("Empty JSON output", output);
		assertTrue("Output without a match",
				output.contains("\"results\":[{\"index\":1,"));
	}

	@Test
	public void testValues() {
		String str = head + lexiconOptions + tail;
		RawCombinedQueryDefinition rawCombinedQueryDefinition;
		
		rawCombinedQueryDefinition = queryMgr
				.newRawCombinedQueryDefinition(new StringHandle(str).withMimetype("application/xml"));
		
		StringHandle stringResults = null;
		ValuesDefinition vdef = queryMgr.newValuesDefinition("grandchild");
		
		vdef.setQueryDefinition(rawCombinedQueryDefinition);
		
		stringResults = queryMgr.tuples(vdef, new StringHandle());
		System.out.println(stringResults.get());
		
		ValuesHandle valuesResults = queryMgr.values(vdef,
				new ValuesHandle());
		
		assertFalse(valuesResults.getMetrics().getTotalTime() == -1);

		CountedDistinctValue[] values = valuesResults.getValues();
		assertNotNull(values);
	}

	@Test
	public void testTuples() {
		String str = head + lexiconOptions + tail;
		RawCombinedQueryDefinition rawCombinedQueryDefinition;
		
		rawCombinedQueryDefinition = queryMgr
				.newRawCombinedQueryDefinition(new StringHandle(str).withMimetype("application/xml"));
		
		StringHandle stringResults = null;
		ValuesDefinition vdef = queryMgr.newValuesDefinition("n-way");
		
		vdef.setQueryDefinition(rawCombinedQueryDefinition);
		
		stringResults = queryMgr.tuples(vdef, new StringHandle());
		System.out.println(stringResults.get());
		
		TuplesHandle tuplesResults = queryMgr.tuples(vdef,
				new TuplesHandle());
		Tuple[] tuples = tuplesResults.getTuples();
		assertNotNull(tuples);
	}
}
