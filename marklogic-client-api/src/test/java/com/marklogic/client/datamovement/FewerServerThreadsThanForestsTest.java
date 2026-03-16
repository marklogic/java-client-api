/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FewerServerThreadsThanForestsTest extends AbstractClientTest {

	@Test
	void test() {
		DatabaseClient client = Common.newClient();
		final int forestCount = client.newDataMovementManager().readForestConfig().listForests().length;
		if (forestCount < 2) {
			logger.info("This test requires multiple forests so that the server thread count can be set to the " +
				"number of forests minus one; skipping test");
			return;
		}

		adjustServerThreads(forestCount - 1);
		try {
			DataMovementManager dmm = client.newDataMovementManager();
			AtomicInteger uriCount = new AtomicInteger();
			QueryBatcher queryBatcher = dmm.newQueryBatcher(client.newQueryManager().newStructuredQueryBuilder().collection("/optic/test"))
				.withThreadCount(1)
				.onUrisReady(batch -> uriCount.addAndGet(batch.getItems().length));
			dmm.startJob(queryBatcher);
			queryBatcher.awaitCompletion();
			dmm.stopJob(queryBatcher);

			assertEquals(4, uriCount.get(), "Verifies that the 4 test documents were found, and more importantly, " +
				"that the new default maxDocToUriBatchRatio of 1 was applied correctly when the number of " +
				"server threads is less than the number of forests. This is for bug 1872 in GitHub. Prior to this " +
				"fix, the maxDocToUriBatchRatio of -1 returned by the server caused an error when the " +
				"LinkedBlockingQueue was constructed with a negative capacity.");
		} finally {
			// We can safely use this number because we know the test-app doesn't change this.
			final int defaultServerThreadCount = 32;
			adjustServerThreads(defaultServerThreadCount);
		}
	}

	private void adjustServerThreads(final int threads) {
		logger.info("Adjusting server threads to {}", threads);
		Common.newAdminManager().invokeActionRequiringRestart(() -> {
			ManageClient manageClient = Common.newManageClient();
			ObjectNode payload = Common.newServerPayload().put("threads", threads);
			new ServerManager(manageClient).save(payload.toString());
			return true;
		});
	}
}
