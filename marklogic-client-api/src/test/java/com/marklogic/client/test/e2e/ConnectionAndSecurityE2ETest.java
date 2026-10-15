/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Database Client Builder, Connection Modes, and Security Roles")
public class ConnectionAndSecurityE2ETest extends AbstractClientTest {

	@Test
	@DisplayName("DatabaseClientBuilder: Fluent Configuration with Digest Auth and Direct Connection")
	void testDatabaseClientBuilder() {
		DatabaseClient customClient = new DatabaseClientBuilder()
			.withHost(Common.HOST)
			.withPort(Common.PORT)
			.withAuthType(DatabaseClientFactory.DigestAuthContext.class.getSimpleName().toUpperCase().replace("AUTHCONTEXT", ""))
			.withUsername(Common.USER)
			.withPassword(Common.PASS)
			.withConnectionType(DatabaseClient.ConnectionType.DIRECT)
			.build();

		try {
			assertNotNull(customClient);
			JSONDocumentManager docMgr = customClient.newJSONDocumentManager();
			String uri = "/e2e/conn/builder-" + UUID.randomUUID() + ".json";
			String content = "{\"builder\": \"test\", \"direct\": true}";

			docMgr.write(uri, new StringHandle(content).withFormat(Format.JSON));
			String readContent = docMgr.read(uri, new StringHandle()).get();
			assertTrue(readContent.contains("\"builder\":\"test\"") || readContent.contains("\"builder\": \"test\""));
			assertTrue(readContent.contains("\"direct\":true") || readContent.contains("\"direct\": true"));
			docMgr.delete(uri);
		} finally {
			customClient.release();
		}
	}

	@Test
	@DisplayName("Gateway Connection Type: Host Resolution Bypass for Single Load Balancer Gateway")
	void testGatewayConnectionType() {
		DatabaseClient gatewayClient = new DatabaseClientBuilder()
			.withHost(Common.HOST)
			.withPort(Common.PORT)
			.withAuthType("DIGEST")
			.withUsername(Common.USER)
			.withPassword(Common.PASS)
			.withConnectionType(DatabaseClient.ConnectionType.GATEWAY)
			.build();

		try {
			assertEquals(DatabaseClient.ConnectionType.GATEWAY, gatewayClient.getConnectionType());
			JSONDocumentManager docMgr = gatewayClient.newJSONDocumentManager();
			String uri = "/e2e/conn/gw-" + UUID.randomUUID() + ".json";

			docMgr.write(uri, new StringHandle("{\"gateway\": true}").withFormat(Format.JSON));
			assertNotNull(docMgr.exists(uri));
			docMgr.delete(uri);
		} finally {
			gatewayClient.release();
		}
	}

	@Test
	@DisplayName("Security RBAC: Read-Only User Allowed Read but Forbidden from Write")
	void testRoleBasedAccessControl() {
		DatabaseClient writerClient = Common.newClient();
		DatabaseClient readerClient = Common.connectReadOnly();

		String uri = "/e2e/conn/rbac-" + UUID.randomUUID() + ".json";

		try {
			// 1. Writer creates document
			writerClient.newJSONDocumentManager().write(
				uri,
				new StringHandle("{\"confidential\": false}").withFormat(Format.JSON)
			);

			// 2. Reader successfully reads document
			String content = readerClient.newJSONDocumentManager().read(uri, new StringHandle()).get();
			assertTrue(content.contains("confidential"));

			// 3. Reader attempts write -> Must fail with ForbiddenUserException (403)
			ForbiddenUserException ex = assertThrows(ForbiddenUserException.class, () ->
				readerClient.newJSONDocumentManager().write(
					uri,
					new StringHandle("{\"hacked\": true}").withFormat(Format.JSON)
				)
			);
			assertNotNull(ex.getMessage());
		} finally {
			try {
				writerClient.newJSONDocumentManager().delete(uri);
			} catch (Exception ignored) {
			}
			writerClient.release();
			readerClient.release();
		}
	}

	@Test
	@DisplayName("Client Release Lifecycle: Idempotent Release and State Invalidation")
	void testClientReleaseLifecycle() {
		DatabaseClient client = Common.newClient();
		assertNotNull(client);

		// Perform operation
		String uri = "/e2e/conn/release-" + UUID.randomUUID() + ".json";
		client.newJSONDocumentManager().write(uri, new StringHandle("{}").withFormat(Format.JSON));
		client.newJSONDocumentManager().delete(uri);

		// Release client
		client.release();

		// Calling release again should be safe and idempotent
		assertDoesNotThrow(client::release);
	}
}
