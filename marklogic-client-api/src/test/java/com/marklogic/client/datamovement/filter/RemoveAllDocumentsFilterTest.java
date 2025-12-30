/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoveAllDocumentsFilterTest extends AbstractClientTest {

	private static final DocumentMetadataHandle METADATA = new DocumentMetadataHandle()
		.withCollections("incremental-test")
		.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);

	AtomicInteger writtenCount = new AtomicInteger();

	@Test
	void filterRemovesAllDocuments() {
		new WriteBatcherTemplate(Common.newClient()).runWriteJob(
			writeBatcher -> writeBatcher
				.withDocumentWriteSetFilter(context -> context.getDatabaseClient().newDocumentManager().newWriteSet())
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length)),

			writeBatcher -> {
				for (int i = 1; i <= 10; i++) {
					writeBatcher.add("/incremental/test/doc-" + i + ".xml", METADATA, new StringHandle("<doc/>"));
				}
			}
		);

		assertEquals(0, writtenCount.get(), "No documents should have been written since the filter removed them all. " +
			"This test is verifying that no error will occur either when the filter doesn't return any documents.");
		assertCollectionSize("incremental-test", 0);
	}
}
