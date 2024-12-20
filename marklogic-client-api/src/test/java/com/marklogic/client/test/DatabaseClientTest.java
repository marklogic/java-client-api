/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClient.ConnectionResult;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.*;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.util.RequestLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {

	@BeforeAll
	public static void beforeClass() {
		Common.connect();
		Common.connectRestAdmin();
	}

	@AfterAll
	public static void afterClass() {
	}

	@Test
	void tryWithResource() {
		try (DatabaseClient client = Common.newClient()) {
			ConnectionResult result = client.checkConnection();
			assertTrue(result.isConnected(), "This test is ensuring that a DatabaseClient, as of 7.1.0, can " +
				"be used in a try-with-resources block without any error being thrown. We don't have a way of " +
				"verifying that release() is actually called on the client though.");
		}
	}

	@Test
	public void testNewDocument() {
		GenericDocumentManager doc = Common.client.newDocumentManager();
		assertNotNull(doc);
	}

	@Test
	public void testNewBinaryDocument() {
		BinaryDocumentManager doc = Common.client.newBinaryDocumentManager();
		assertNotNull(doc);
	}

	@Test
	public void testNewJSONDocument() {
		JSONDocumentManager doc = Common.client.newJSONDocumentManager();
		assertNotNull(doc);
	}

	@Test
	public void testNewTextDocument() {
		TextDocumentManager doc = Common.client.newTextDocumentManager();
		assertNotNull(doc);
	}

	@Test
	public void testNewXMLDocument() {
		XMLDocumentManager doc = Common.client.newXMLDocumentManager();
		assertNotNull(doc);
	}

	@Test
	public void testNewLogger() {
		RequestLogger logger = Common.client.newLogger(System.out);
		assertNotNull(logger);
	}

	@Test
	public void testNewQueryManager() {
		QueryManager mgr = Common.client.newQueryManager();
		assertNotNull(mgr);
	}

	@Test
	public void testNewRuleManager() {
		RuleManager mgr = Common.client.newRuleManager();
		assertNotNull(mgr);
	}

	@Test
	public void testNewPojoRepository() {
		PojoRepository<City, Integer> mgr = Common.client.newPojoRepository(City.class, Integer.class);
		assertNotNull(mgr);
	}

	@Test
	public void testNewServerEvaluationCall() {
		ServerEvaluationCall mgr = Common.client.newServerEval();
		assertNotNull(mgr);
	}

	@Test
	public void testNewQueryOptionsManager() {
		QueryOptionsManager mgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
		assertNotNull(mgr);
	}

	@Test
	public void testGetClientImplementationObject() {
		Object impl = Common.client.getClientImplementation();
		assertNotNull(impl);
		assertTrue(impl instanceof okhttp3.OkHttpClient);
	}

	@Test
	public void testCheckConnectionWithValidUser() {
		ConnectionResult connResult = Common.newClient().checkConnection();
		assertTrue(connResult.isConnected());
	}

	@Test
	public void testCheckConnectionWithInvalidUser() {
		ConnectionResult connResult = Common.newClientBuilder().withUsername("invalid").withPassword("invalid").build().checkConnection();
		assertFalse(connResult.isConnected());
		assertTrue(connResult.getStatusCode() == 401);
		assertTrue(connResult.getErrorMessage().equalsIgnoreCase("Unauthorized"));
	}

}
