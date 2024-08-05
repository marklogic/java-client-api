/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import com.marklogic.client.util.EditableNamespaceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.*;

public class StructuredSearchTest {

	@BeforeAll
	public static void beforeClass() {
		Common.connect();
	}

	@Test
	public void testStructuredSearch() {
		QueryManager queryMgr = Common.client.newQueryManager();
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

		for (QueryDefinition t : new QueryDefinition[]{
			qb.term("leaf3"), qb.build(qb.value(qb.element("leaf"), "leaf3"))
		}) {
			SearchHandle results = queryMgr.search(t, new SearchHandle());
			assertNotNull(results);
			assertFalse(results.getMetrics().getTotalTime() == -1);

			MatchDocumentSummary[] summaries = results.getMatchResults();
			assertNotNull(summaries);
			assertEquals(1, summaries.length);
			for (MatchDocumentSummary summary : summaries) {
				MatchLocation[] locations = summary.getMatchLocations();
				assertEquals(1, locations.length);
				for (MatchLocation location : locations) {
					assertNotNull(location.getAllSnippetText());
				}
			}
		}
	}

	@Test
	public void testStructuredSearch1() {
		QueryManager queryMgr = Common.client.newQueryManager();
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

		for (QueryDefinition t : new QueryDefinition[]{
			qb.term("leaf3"), qb.build(qb.value(qb.element("leaf"), "leaf3"))
		}) {

			MatchDocumentSummary summary = queryMgr.findOne(t);
			assertNotNull(summary);

			GenericDocumentManager docMgr = Common.client.newDocumentManager();
			assertNotNull(docMgr.exists(summary.getUri()));
		}
	}

	@Test
	public void testStructuredSearch2() {
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
	public void testFailedSearch() {
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
			"<search xmlns=\"http://marklogic.com/appservices/search\">" +
				"<query>" +
				"<value-query>" +
				"<element ns=\"http://marklogic.com/xdmp/json\" name=\"firstKey\"/>" +
				"<text>first value</text>" +
				"</value-query>" +
				"</query>" +
				"<options>" +
				"<extract-metadata>" +
				"<qname elem-ns=\"http://marklogic.com/xdmp/json\" elem-name=\"subKey\"/>" +
				"</extract-metadata>" +
				"</options>" +
				"</search>";
		StringHandle rawHandle = new StringHandle(combined);

		RawCombinedQueryDefinition rawDef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

		SearchHandle sh = queryMgr.search(rawDef, new SearchHandle());

		MatchDocumentSummary[] summaries = sh.getMatchResults();
		assertNotNull(summaries);
		assertEquals(1, summaries.length);

		MatchDocumentSummary matchResult = summaries[0];
		Document metadata = matchResult.getMetadata();
		Element subKey = (Element)
			metadata.getElementsByTagNameNS("http://marklogic.com/xdmp/json", "subKey").item(0);
		assertEquals("string", subKey.getAttribute("type"));
		assertEquals("sub value", subKey.getTextContent());

		String docStr = Common.testDocumentToString(metadata);
		String handleStr = matchResult.getMetadata(new StringHandle()).get();
		assertXMLEqual("Different metadata for handle", docStr, handleStr);

		Document snippet = matchResult.getSnippets()[0];
		docStr = Common.testDocumentToString(snippet);
		handleStr = matchResult.getSnippetIterator(new StringHandle()).next().get();
		assertXMLEqual("Different snippet for handle", docStr, handleStr);
	}

	@Test
	void andNotQuery() {
		QueryManager queryManager = Common.client.newQueryManager();

		// Write the same doc in 2 different collections.
		XMLDocumentManager mgr = Common.client.newXMLDocumentManager();
		DocumentWriteSet writeSet = mgr.newWriteSet();
		writeSet.add("/1.xml", new DocumentMetadataHandle()
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
			.withCollections("test1"), new StringHandle("<hello>world</hello>"));
		writeSet.add("/2.xml", new DocumentMetadataHandle()
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
			.withCollections("test2"), new StringHandle("<hello>world</hello>"));
		mgr.write(writeSet);

		// Verify a structured query works
		StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();
		StructuredQueryDefinition structuredQuery = queryBuilder.and(
			queryBuilder.term("world"),
			queryBuilder.andNot(
				queryBuilder.collection("test1"),
				queryBuilder.collection("test2")
			)
		);

		long count = queryManager.search(structuredQuery, new SearchHandle()).getTotalResults();
		assertEquals(1, count);

		// Verify a combined query works.
		String combinedQuery = "<search xmlns=\"http://marklogic.com/appservices/search\">\n" +
			"  <query>\n" +
			"    <and-query>\n" +
			"      <term-query>\n" +
			"        <text>world</text>\n" +
			"      </term-query>\n" +
			"      <and-not-query>\n" +
			"        <positive-query>\n" +
			"          <collection-query>\n" +
			"            <uri>test1</uri>\n" +
			"          </collection-query>\n" +
			"        </positive-query>\n" +
			"        <negative-query>\n" +
			"          <collection-query>\n" +
			"            <uri>test2</uri>\n" +
			"          </collection-query>\n" +
			"        </negative-query>\n" +
			"      </and-not-query>\n" +
			"    </and-query>\n" +
			"  </query>\n" +
			"</search>";

		count = queryManager.search(
			queryManager.newRawCombinedQueryDefinition(new StringHandle(combinedQuery)),
			new SearchHandle()).getTotalResults();
		assertEquals(1, count);

		// Verify the combined query works with DMSDK.
		DataMovementManager dmm = Common.client.newDataMovementManager();
		AtomicInteger counter = new AtomicInteger();
		QueryBatcher qb = dmm.newQueryBatcher(
				queryManager.newRawCombinedQueryDefinition(new StringHandle(combinedQuery))
			)
			.onUrisReady(batch -> counter.getAndAdd(batch.getItems().length))
			.withThreadCount(1);
		dmm.startJob(qb);
		qb.awaitCompletion();
		dmm.stopJob(qb);
		assertEquals(1, counter.get());
	}
}
