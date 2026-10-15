/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Comprehensive Search and Query Execution")
public class SearchAndQueryE2ETest extends AbstractClientTest {

	private DatabaseClient client;
	private QueryManager queryMgr;
	private JSONDocumentManager docMgr;
	private final String testCollection = "e2e-search-" + UUID.randomUUID();
	private final Set<String> createdUris = new HashSet<>();
	private final ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		client = Common.connect();
		queryMgr = client.newQueryManager();
		docMgr = client.newJSONDocumentManager();

		// Seed 10 sample test documents across 2 categories
		for (int i = 1; i <= 10; i++) {
			String uri = String.format("/e2e/search/doc-%d-%s.json", i, UUID.randomUUID());
			createdUris.add(uri);

			ObjectNode doc = mapper.createObjectNode()
				.put("id", i)
				.put("category", (i % 2 == 0) ? "hardware" : "software")
				.put("name", (i % 2 == 0) ? "Server Rack " + i : "Enterprise Software " + i)
				.put("active", i > 3)
				.put("rating", 3.5 + (i * 0.1));

			com.marklogic.client.io.DocumentMetadataHandle meta = new com.marklogic.client.io.DocumentMetadataHandle();
			meta.getCollections().addAll(testCollection, (i % 2 == 0) ? "cat-hardware" : "cat-software");
			docMgr.write(uri, meta, new JacksonHandle(doc));
		}
	}

	@AfterEach
	void tearDown() {
		if (docMgr != null && !createdUris.isEmpty()) {
			docMgr.delete(createdUris.toArray(new String[0]));
		}
	}

	@Test
	@DisplayName("Structured Query: Term, Collection, Boolean And/Or, and Negation")
	void testStructuredQueryBuilder() {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

		// 1. Search by collection
		StructuredQueryDefinition colQuery = qb.collection(testCollection);
		SearchHandle colResults = queryMgr.search(colQuery, new SearchHandle());
		assertEquals(10, colResults.getTotalResults(), "Total results in test collection should be 10");

		// 2. Search with AND condition (Collection AND Category Hardware)
		StructuredQueryDefinition andQuery = qb.and(
			qb.collection(testCollection),
			qb.collection("cat-hardware")
		);
		SearchHandle andResults = queryMgr.search(andQuery, new SearchHandle());
		assertEquals(5, andResults.getTotalResults(), "Hardware category should have exactly 5 documents");

		// 3. Search with NOT condition (Test collection NOT cat-hardware)
		StructuredQueryDefinition notQuery = qb.and(
			qb.collection(testCollection),
			qb.not(qb.collection("cat-hardware"))
		);
		SearchHandle notResults = queryMgr.search(notQuery, new SearchHandle());
		assertEquals(5, notResults.getTotalResults(), "Non-hardware category should have exactly 5 documents");

		// 4. Term query matching text within JSON
		StructuredQueryDefinition termQuery = qb.and(
			qb.collection(testCollection),
			qb.term("Enterprise")
		);
		SearchHandle termResults = queryMgr.search(termQuery, new SearchHandle());
		assertEquals(5, termResults.getTotalResults(), "5 software documents contain 'Enterprise'");
	}

	@Test
	@DisplayName("String Query: Criteria Matching and Match Summaries")
	void testStringQuery() {
		StringQueryDefinition stringQuery = queryMgr.newStringDefinition();
		stringQuery.setCriteria("Enterprise");
		stringQuery.setCollections(testCollection);

		SearchHandle results = queryMgr.search(stringQuery, new SearchHandle());
		assertTrue(results.getTotalResults() >= 1, "Should match Enterprise software documents");

		MatchDocumentSummary[] summaries = results.getMatchResults();
		assertNotNull(summaries);
		assertTrue(summaries.length > 0);

		for (MatchDocumentSummary summary : summaries) {
			assertNotNull(summary.getUri());
			assertTrue(summary.getScore() > 0 || summary.getConfidence() >= 0);
		}
	}

	@Test
	@DisplayName("Pagination and Slicing: Page Length, Offset, and Total Results")
	void testPaginationAndSlicing() {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = qb.collection(testCollection);

		queryMgr.setPageLength(3);

		// Page 1 (items 1 to 3)
		SearchHandle page1 = queryMgr.search(query, new SearchHandle(), 1);
		assertEquals(10, page1.getTotalResults());
		assertEquals(3, page1.getMatchResults().length);
		assertEquals(1, page1.getStart());

		// Page 2 (items 4 to 6)
		SearchHandle page2 = queryMgr.search(query, new SearchHandle(), 4);
		assertEquals(3, page2.getMatchResults().length);
		assertEquals(4, page2.getStart());

		// Page 4 (items 10 to 10 - partial last page)
		SearchHandle page4 = queryMgr.search(query, new SearchHandle(), 10);
		assertEquals(1, page4.getMatchResults().length);
		assertEquals(10, page4.getStart());
	}

	@Test
	@DisplayName("Document Page Streaming Search: Reading Document Content Directly via Query")
	void testDocumentPageSearch() {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = qb.and(
			qb.collection(testCollection),
			qb.collection("cat-software")
		);

		DocumentPage docPage = docMgr.search(query, 1);
		assertEquals(5, docPage.getTotalSize());

		int count = 0;
		while (docPage.hasNext()) {
			DocumentRecord record = docPage.next();
			assertEquals(Format.JSON, record.getFormat());
			String content = record.getContent(new StringHandle()).get();
			assertTrue(content.contains("Enterprise Software"));
			count++;
		}
		assertEquals(5, count);
	}

	@Test
	@DisplayName("Delete by Search: Removing All Documents Matching Query")
	void testDeleteBySearch() {
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = qb.and(
			qb.collection(testCollection),
			qb.collection("cat-hardware")
		);

		// Verify 5 hardware documents before deletion
		SearchHandle before = queryMgr.search(query, new SearchHandle());
		assertEquals(5, before.getTotalResults());

		// Execute delete by query using DeleteQueryDefinition
		DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
		deleteDef.setCollections("cat-hardware");
		queryMgr.delete(deleteDef);

		// Verify hardware documents are gone
		SearchHandle after = queryMgr.search(query, new SearchHandle());
		assertEquals(0, after.getTotalResults(), "Hardware documents should be deleted");

		// Software documents must still exist
		StructuredQueryDefinition softQuery = qb.and(
			qb.collection(testCollection),
			qb.collection("cat-software")
		);
		assertEquals(5, queryMgr.search(softQuery, new SearchHandle()).getTotalResults());
	}
}
