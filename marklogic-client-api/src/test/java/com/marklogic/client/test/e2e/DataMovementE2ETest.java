/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;
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

	@Test
	@DisplayName("QueryBatcher: Super-Database Querying Across Sub-Database Forests")
	void testQueryBatcherWithSuperDatabase() {
		int docCount = 20;
		String superDbName = "e2e-super-db-" + UUID.randomUUID().toString().substring(0, 8);
		DatabaseClient evalClient = Common.newServerAdminClient();

		// 1. Seed documents into the sub-database (java-unittest)
		WriteBatcher wb = dmm.newWriteBatcher().withBatchSize(10).withThreadCount(2);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections(testCollection);
		for (int i = 0; i < docCount; i++) {
			String uri = String.format("/e2e/dmsdk/superdb-item-%d-%s.json", i, UUID.randomUUID());
			createdUris.add(uri);
			wb.add(uri, metadata, new JacksonHandle(mapper.createObjectNode().put("index", i)));
		}
		wb.flushAndWait();
		dmm.stopJob(wb);

		// 2. Create a temporary Super-Database (with 0 directly attached forests) and attach the current
		//    content database as its sub-database
		String subDbName = dmm.readForestConfig().listForests()[0].getDatabaseName();
		String createSuperDbXQuery =
			"import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy'; " +
			"declare variable $superDbName as xs:string external; " +
			"declare variable $subDbName as xs:string external; " +
			"let $config := admin:get-configuration() " +
			"let $config := admin:database-create($config, $superDbName, xdmp:database('Security'), xdmp:database('Schemas')) " +
			"let $_ := admin:save-configuration($config) " +
			"let $config := admin:get-configuration() " +
			"let $config := admin:database-attach-sub-database($config, xdmp:database($superDbName), xdmp:database($subDbName)) " +
			"return admin:save-configuration($config)";

		evalClient.newServerEval()
			.xquery(createSuperDbXQuery)
			.addVariable("superDbName", superDbName)
			.addVariable("subDbName", subDbName)
			.evalAs(String.class);

		try (DatabaseClient superDbClient = Common.newClientBuilder()
				.withDatabase(superDbName)
				.withUsername(Common.SERVER_ADMIN_USER)
				.withPassword(Common.SERVER_ADMIN_PASS)
				.build()) {
			DataMovementManager superDmm = superDbClient.newDataMovementManager();

			// Verify standard readForestConfig() on the Super-Database returns 0 forests
			assertEquals(0, superDmm.readForestConfig().listForests().length,
				"A Super-Database with no directly attached forests returns an empty ForestConfiguration by default");

			// 3. Build a composite ForestConfiguration from all sub-database(s)
			List<Forest> combinedSubDbForests = new ArrayList<>();
			for (String subDb : Collections.singletonList(subDbName)) {
				try (DatabaseClient subClient = Common.newClientBuilder().withDatabase(subDb).build()) {
					DataMovementManager subDmm = subClient.newDataMovementManager();
					Collections.addAll(combinedSubDbForests, subDmm.readForestConfig().listForests());
					subDmm.release();
				}
			}
			ForestConfiguration superForestConfig = () -> combinedSubDbForests.toArray(new Forest[0]);

			// When the Super-Database itself has 0 forests, point DataMovementServices at a sub-database client
			// so the initial /v1/internal/forestinfo query-preprocessing call succeeds before withForestConfig()
			// applies the composite sub-database ForestConfiguration to the Super-Database QueryBatcher.
			((DataMovementManagerImpl) superDmm).getDataMovementServices().setClient(client);

			StructuredQueryBuilder qb = superDbClient.newQueryManager().newStructuredQueryBuilder();
			StructuredQueryDefinition query = qb.collection(testCollection);

			Set<String> retrievedUris = Collections.synchronizedSet(new HashSet<>());
			QueryBatcher queryBatcher = superDmm.newQueryBatcher(query)
				.withForestConfig(superForestConfig)
				.withBatchSize(5)
				.withThreadCount(2)
				.onUrisReady(batch -> retrievedUris.addAll(Arrays.asList(batch.getItems())))
				.onQueryFailure(ex -> fail("Super-Database QueryBatcher failed: " + ex.getMessage()));

			superDmm.startJob(queryBatcher);
			queryBatcher.awaitCompletion();
			superDmm.stopJob(queryBatcher);
			superDmm.release();

			// 4. Verify all sub-database documents were discovered via the Super-Database QueryBatcher
			assertEquals(docCount, retrievedUris.size(),
				"Super-Database QueryBatcher configured with sub-database forests must retrieve all matching URIs");
			assertTrue(retrievedUris.containsAll(createdUris));
		} finally {
			String deleteSuperDbXQuery =
				"import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy'; " +
				"declare variable $subDbName as xs:string external; " +
				"for $dbId in xdmp:databases()[starts-with(xdmp:database-name(.), 'e2e-super-db-')] " +
				"let $config := admin:get-configuration() " +
				"let $config := admin:database-detach-sub-database($config, $dbId, xdmp:database($subDbName)) " +
				"let $_ := admin:save-configuration($config) " +
				"let $config := admin:get-configuration() " +
				"let $config := admin:database-delete($config, $dbId) " +
				"return admin:save-configuration($config)";
			evalClient.newServerEval()
				.xquery(deleteSuperDbXQuery)
				.addVariable("subDbName", subDbName)
				.evalAs(String.class);
			evalClient.release();
		}
	}
}
