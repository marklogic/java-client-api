/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncrementalWriteTest extends AbstractClientTest {

	private static final DocumentMetadataHandle METADATA = new DocumentMetadataHandle()
		.withCollections("incremental-test")
		.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);

	@Test
	void test() {
		AtomicInteger writtenCount = new AtomicInteger();

		try (DatabaseClient client = Common.newClient()) {
			WriteBatcherTemplate template = new WriteBatcherTemplate(client);

			template.runWriteJob(writeBatcher -> writeBatcher
					.withThreadCount(1)
					.withBatchSize(10)
					.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length)),

				writeBatcher -> {
					for (int i = 1; i <= 20; i++) {
						String uri = "/incremental/test/doc-" + i + ".xml";
						String content = "<doc><docNum>" + i + "</docNum><text>This is document number " + i + "</text></doc>";
						writeBatcher.add(uri, METADATA, new StringHandle(content));
					}
				}
			);
		}

		assertEquals(20, writtenCount.get());
	}

	// Experimenting with a template that gets rid of some annoying DMSDK boilerplate.
	private record WriteBatcherTemplate(DatabaseClient databaseClient) {

		public void runWriteJob(Consumer<WriteBatcher> writeBatcherConfigurer, Consumer<WriteBatcher> writeBatcherUser) {
			try (DataMovementManager dmm = databaseClient.newDataMovementManager()) {
				WriteBatcher writeBatcher = dmm.newWriteBatcher();
				writeBatcherConfigurer.accept(writeBatcher);

				dmm.startJob(writeBatcher);

				writeBatcherUser.accept(writeBatcher);
				writeBatcher.awaitCompletion();

				dmm.stopJob(writeBatcher);
			}
		}
	}
}
