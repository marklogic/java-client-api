/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Data Movement SDK (DMSDK) Parallel Batching")
public class DataMovementE2ETest extends AbstractClientTest {

	private DatabaseClient client;
	private DataMovementManager dmm;
	private JSONDocumentManager docMgr;
	private final String testCollection = "e2e-dmsdk-" + UUID.randomUUID();
	private final List<String> createdUris = Collections.synchronizedList(new ArrayList<>());
	private final ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		client = Common.connect();
		dmm = client.newDataMovementManager();
		docMgr = client.newJSONDocumentManager();
	}

	@AfterEach
	void tearDown() {
		if (docMgr != null && !createdUris.isEmpty()) {
			docMgr.delete(createdUris.toArray(new String[0]));
		}
	}

	@Test
	@DisplayName("WriteBatcher: Multithreaded Bulk Insertion with Success Listeners and JobReport")
	void testWriteBatcher() {
		int totalDocs = 40;
		int batchSize = 10;
		int threadCount = 4;

		AtomicInteger successfulBatches = new AtomicInteger();
		AtomicInteger successfulDocs = new AtomicInteger();

		WriteBatcher writeBatcher = dmm.newWriteBatcher()
			.withBatchSize(batchSize)
			.withThreadCount(threadCount)
			.onBatchSuccess(batch -> {
				successfulBatches.incrementAndGet();
				successfulDocs.addAndGet(batch.getItems().length);
			})
			.onBatchFailure((batch, throwable) ->
				fail("WriteBatcher batch failed: " + throwable.getMessage())
			);

		com.marklogic.client.datamovement.JobTicket ticket = dmm.startJob(writeBatcher);

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(testCollection);

		for (int i = 0; i < totalDocs; i++) {
			String uri = String.format("/e2e/dmsdk/item-%d-%s.json", i, UUID.randomUUID());
			createdUris.add(uri);

			ObjectNode content = mapper.createObjectNode()
				.put("seq", i)
				.put("worker", "dmsdk-writer")
				.put("timestamp", System.currentTimeMillis());

			writeBatcher.add(uri, metadata, new JacksonHandle(content));
		}

		// Flush all pending items and wait for completion
		writeBatcher.flushAndWait();

		JobReport report = dmm.getJobReport(ticket);
		assertEquals(totalDocs, report.getSuccessEventsCount(), "JobReport must show all documents written");
		assertEquals(totalDocs, successfulDocs.get(), "Listener count must equal total written");
		assertTrue(successfulBatches.get() >= (totalDocs / batchSize));

		dmm.stopJob(ticket);

		// Verify random document exists in MarkLogic
		assertNotNull(docMgr.exists(createdUris.get(0)));
		assertNotNull(docMgr.exists(createdUris.get(totalDocs - 1)));
	}

	@Test
	@DisplayName("QueryBatcher: Parallel Querying and Forest Scanning with OnUrisReady Callback")
	void testQueryBatcher() {
		int docCount = 30;

		// 1. Seed data using WriteBatcher
		WriteBatcher wb = dmm.newWriteBatcher().withBatchSize(10).withThreadCount(2);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections(testCollection);

		for (int i = 0; i < docCount; i++) {
			String uri = String.format("/e2e/dmsdk/query-item-%d-%s.json", i, UUID.randomUUID());
			createdUris.add(uri);
			wb.add(uri, metadata, new JacksonHandle(mapper.createObjectNode().put("index", i)));
		}
		wb.flushAndWait();
		dmm.stopJob(wb);

		// 2. Query data using QueryBatcher
		StructuredQueryBuilder qb = client.newQueryManager().newStructuredQueryBuilder();
		StructuredQueryDefinition query = qb.collection(testCollection);

		Set<String> retrievedUris = Collections.synchronizedSet(new HashSet<>());
		AtomicInteger queryBatches = new AtomicInteger();

		QueryBatcher queryBatcher = dmm.newQueryBatcher(query)
			.withBatchSize(10)
			.withThreadCount(4)
			.onUrisReady(batch -> {
				queryBatches.incrementAndGet();
				retrievedUris.addAll(Arrays.asList(batch.getItems()));
			})
			.onQueryFailure(queryBatchException ->
				fail("QueryBatcher failed: " + queryBatchException.getMessage())
			);

		dmm.startJob(queryBatcher);
		queryBatcher.awaitCompletion();
		dmm.stopJob(queryBatcher);

		// 3. Verify all URIs were discovered across forests
		assertEquals(docCount, retrievedUris.size(), "QueryBatcher should find every seeded document");
		assertTrue(queryBatches.get() >= (docCount / 10));
		for (String uri : createdUris) {
			assertTrue(retrievedUris.contains(uri), "Retrieved URIs must include: " + uri);
		}
	}
}
