/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractIncrementalWriteTest extends AbstractClientTest {

	static final DocumentMetadataHandle METADATA = new DocumentMetadataHandle()
		.withCollections("incremental-test")
		.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);

	AtomicInteger writtenCount = new AtomicInteger();
	AtomicInteger skippedCount = new AtomicInteger();
	AtomicReference<Throwable> batchFailure = new AtomicReference<>();
	ObjectMapper objectMapper = new ObjectMapper();

	List<DocumentWriteOperation> docs = new ArrayList<>();
	IncrementalWriteFilter filter;

	@BeforeEach
	void setup() {
		// Need a user with eval privileges so that the eval filter can be tested.
		Common.client = Common.newEvalClient();

		// Default filter implementation, should be suitable for most tests.
		filter = IncrementalWriteFilter.newBuilder()
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.timestampKeyName("incrementalWriteTimestamp")
			.build();
	}

	final void writeDocs(List<DocumentWriteOperation> docs) {
		new WriteBatcherTemplate(Common.client).runWriteJob(
			writeBatcher -> writeBatcher
				.withDocumentWriteSetFilter(filter)
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length))
				.onBatchFailure((batch, failure) -> batchFailure.set(failure)),

			writeBatcher -> docs.forEach(writeBatcher::add)
		);
	}
}
