/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.junit5.AbstractMarkLogicTest;
import org.junit.jupiter.api.AfterEach;

/**
 * Intended to be the base class for all future client API tests, as it properly prepares the database by deleting
 * documents from previous test runs that were not created as part of deploying the test app.
 */
public abstract class AbstractClientTest extends AbstractMarkLogicTest {

	@Override
	protected final DatabaseClient getDatabaseClient() {
		return Common.newServerAdminClient();
	}

	@Override
	protected final String getJavascriptForDeletingDocumentsBeforeTestRuns() {
		return """
			declareUpdate();
			cts.uris('', [], cts.notQuery(cts.collectionQuery(['test-data', 'temporal-collection'])))
				.toArray().forEach(item => xdmp.documentDelete(item))
			""";
	}

	@AfterEach
	void releaseClient() {
		if (Common.client != null) {
			Common.client.release();
		}
	}
}
