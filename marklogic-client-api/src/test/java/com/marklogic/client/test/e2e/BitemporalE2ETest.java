/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Bitemporal Document Lifecycle and Axis Management")
public class BitemporalE2ETest extends AbstractClientTest {

	private static final String TEMPORAL_COLLECTION = "temporal-collection";
	private DatabaseClient client;
	private XMLDocumentManager docMgr;
	private String docId;

	@BeforeEach
	void setUp() {
		client = Common.connect();
		docMgr = client.newXMLDocumentManager();
		docId = "/e2e/temporal/policy-" + UUID.randomUUID() + ".xml";
	}

	@AfterEach
	void tearDown() {
		try (DatabaseClient adminClient = Common.newServerAdminClient()) {
			QueryManager qm = adminClient.newQueryManager();
			qm.setPageLength(100);
			QueryDefinition qd = qm.newStringDefinition();
			qd.setCollections(TEMPORAL_COLLECTION);
			SearchHandle handle = qm.search(qd, new SearchHandle());
			for (MatchDocumentSummary doc : handle.getMatchResults()) {
				if (!(TEMPORAL_COLLECTION + ".lsqt").equals(doc.getUri()) && doc.getUri().contains("/e2e/temporal/")) {
					try {
						adminClient.newXMLDocumentManager().delete(doc.getUri());
					} catch (Exception ignored) {
					}
				}
			}
		}
	}

	@Test
	@DisplayName("Bitemporal Insert, Update, and System Timestamp Assignment")
	void testTemporalInsertAndUpdate() {
		String initialContent = """
			<policy>
				<id>POL-1001</id>
				<coverage>500000</coverage>
				<system-start></system-start>
				<system-end></system-end>
				<valid-start>2024-01-01T00:00:00Z</valid-start>
				<valid-end>2025-01-01T00:00:00Z</valid-end>
			</policy>
			""";

		// 1. Insert temporal document
		docMgr.write(docId, null, new StringHandle(initialContent).withFormat(Format.XML), null, null, TEMPORAL_COLLECTION);

		// 2. Read back and verify system timestamps were populated by server
		String doc = docMgr.read(docId, new StringHandle()).get();
		assertNotNull(doc);
		assertTrue(doc.contains("<coverage>500000</coverage>"));
		assertFalse(doc.contains("<system-start></system-start>"), "MarkLogic must populate non-empty system-start");

		// 3. Update temporal document for new validity period
		String updatedContent = """
			<policy>
				<id>POL-1001</id>
				<coverage>750000</coverage>
				<system-start></system-start>
				<system-end></system-end>
				<valid-start>2025-01-01T00:00:00Z</valid-start>
				<valid-end>2026-01-01T00:00:00Z</valid-end>
			</policy>
			""";

		docMgr.write(docId, null, new StringHandle(updatedContent).withFormat(Format.XML), null, null, TEMPORAL_COLLECTION);

		String updatedDoc = docMgr.read(docId, new StringHandle()).get();
		assertTrue(updatedDoc.contains("<coverage>750000</coverage>"));

		// 4. Temporal delete
		docMgr.delete(docId, null, TEMPORAL_COLLECTION);
	}
}
