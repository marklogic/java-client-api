/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Multi-Statement ACID Transactions")
public class TransactionsE2ETest extends AbstractClientTest {

	private DatabaseClient client;
	private JSONDocumentManager docMgr;
	private final ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		client = Common.connect();
		docMgr = client.newJSONDocumentManager();
	}

	@Test
	@DisplayName("ACID Rollback: Isolation and Rollback Verification")
	void testTransactionRollback() {
		String uri = "/e2e/tx/rollback-" + UUID.randomUUID() + ".json";
		Transaction tx = client.openTransaction("test-rollback-tx", 60);
		assertNotNull(tx.getTransactionId());

		try {
			// Write document within transaction
			ObjectNode data = mapper.createObjectNode()
				.put("txId", tx.getTransactionId())
				.put("status", "UNCOMMITTED");

			docMgr.write(uri, new JacksonHandle(data), tx);

			// Read within the same transaction (Read-Your-Own-Writes)
			JacksonHandle txReadHandle = new JacksonHandle();
			docMgr.read(uri, txReadHandle, tx);
			assertEquals("UNCOMMITTED", txReadHandle.get().get("status").asText());

			// Read outside the transaction (Isolation verification - should NOT exist)
			assertNull(docMgr.exists(uri), "Document must not exist outside uncommitted transaction");

			// Rollback transaction
			tx.rollback();
		} catch (Exception e) {
			tx.rollback();
			fail("Transaction failed unexpectedly: " + e.getMessage());
		}

		// Verify document does not exist after rollback
		assertNull(docMgr.exists(uri), "Document must not exist after rollback");
		assertThrows(ResourceNotFoundException.class, () -> docMgr.read(uri, new JacksonHandle()));
	}

	@Test
	@DisplayName("ACID Commit: Write within Transaction and Durability after Commit")
	void testTransactionCommit() {
		String uri = "/e2e/tx/commit-" + UUID.randomUUID() + ".json";
		Transaction tx = client.openTransaction("test-commit-tx", 60);

		try {
			ObjectNode data = mapper.createObjectNode()
				.put("amount", 999.99)
				.put("status", "PENDING_COMMIT");

			docMgr.write(uri, new JacksonHandle(data), tx);

			// Commit the transaction
			tx.commit();

			// Read after commit without transaction context
			JacksonHandle readHandle = new JacksonHandle();
			docMgr.read(uri, readHandle);
			JsonNode doc = readHandle.get();
			assertNotNull(doc);
			assertEquals(999.99, doc.get("amount").asDouble());
			assertEquals("PENDING_COMMIT", doc.get("status").asText());
		} finally {
			// Cleanup committed document
			try {
				docMgr.delete(uri);
			} catch (Exception ignored) {
			}
		}
	}

	@Test
	@DisplayName("Multiple Documents within Single Atomic Transaction")
	void testMultiDocumentAtomicTransaction() {
		String uri1 = "/e2e/tx/atomic1-" + UUID.randomUUID() + ".json";
		String uri2 = "/e2e/tx/atomic2-" + UUID.randomUUID() + ".json";

		Transaction tx = client.openTransaction();

		try {
			docMgr.write(uri1, new StringHandle("{\"account\": \"A\", \"balance\": 1000}").withFormat(Format.JSON), tx);
			docMgr.write(uri2, new StringHandle("{\"account\": \"B\", \"balance\": 2000}").withFormat(Format.JSON), tx);

			// Both visible in transaction
			assertNotNull(docMgr.exists(uri1, tx));
			assertNotNull(docMgr.exists(uri2, tx));

			// Neither visible outside transaction
			assertNull(docMgr.exists(uri1));
			assertNull(docMgr.exists(uri2));

			tx.commit();

			// Both visible outside transaction
			assertNotNull(docMgr.exists(uri1));
			assertNotNull(docMgr.exists(uri2));
		} finally {
			try {
				docMgr.delete(uri1);
				docMgr.delete(uri2);
			} catch (Exception ignored) {
			}
		}
	}
}
