/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncrementalWriteTest extends AbstractClientTest {

	private static final DocumentMetadataHandle METADATA = new DocumentMetadataHandle()
		.withCollections("incremental-test")
		.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);

	AtomicInteger writtenCount = new AtomicInteger();
	AtomicInteger skippedCount = new AtomicInteger();
	ObjectMapper objectMapper = new ObjectMapper();

	IncrementalWriteFilter filter;

	@BeforeEach
	void setup() {
		// Need a user with eval privileges so that the eval filter can be tested.
		Common.client = Common.newEvalClient();
	}

	@Test
	void opticFilter() {
		filter = IncrementalWriteFilter.newBuilder()
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		runTest();
	}

	@Test
	void evalFilter() {
		filter = IncrementalWriteFilter.newBuilder()
			.useEvalQuery(true)
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		runTest();
	}

	@Test
	void filterRemovesAllDocuments() {
		new WriteBatcherTemplate(Common.client).runWriteJob(
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

	@Test
	void jsonKeysOutOfOrder() {
		filter = IncrementalWriteFilter.newBuilder()
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		List<DocumentWriteOperation> docs = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("number", i);
			doc.put("text", "hello");
			docs.add(new DocumentWriteOperationImpl("/incremental/test/doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(10, writtenCount.get());
		assertEquals(0, skippedCount.get());

		docs = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("text", "hello");
			doc.put("number", i);
			docs.add(new DocumentWriteOperationImpl("/incremental/test/doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(10, writtenCount.get());
		assertEquals(10, skippedCount.get(), "Since JSON canonicalization is enabled by default, the documents " +
			"should be recognized as unchanged even though their keys are in a different order.");
	}

	@Test
	void jsonKeysOutOfOrderWithNoCanonicalization() {
		filter = IncrementalWriteFilter.newBuilder()
			.canonicalizeJson(false)
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		List<DocumentWriteOperation> docs = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("number", i);
			doc.put("text", "hello");
			docs.add(new DocumentWriteOperationImpl("/incremental/test/doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(10, writtenCount.get());
		assertEquals(0, skippedCount.get());

		docs = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("text", "hello");
			doc.put("number", i);
			docs.add(new DocumentWriteOperationImpl("/incremental/test/doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(20, writtenCount.get(), "Since JSON canonicalization is disabled, all documents should be " +
			"written again since their keys are in a different order.");
		assertEquals(0, skippedCount.get());
	}

	private void runTest() {
		writeTenDocuments();
		assertEquals(10, writtenCount.get());
		assertEquals(0, skippedCount.get(), "No docs should have been skipped on the first write.");

		writeTenDocuments();
		assertEquals(10, skippedCount.get(), "All docs should have been skipped since their content hasn't changed.");
		assertEquals(10, writtenCount.get(), "The count of written should still be 10 since all docs should have been skipped on the second write.");

		modifyFiveDocuments();
		assertEquals(10, skippedCount.get());
		assertEquals(15, writtenCount.get(), "5 documents should have been modified, with their hashes being updated.");

		writeTenDocuments();
		assertEquals(15, skippedCount.get(), "The 5 unmodified documents should have been skipped.");
		assertEquals(20, writtenCount.get(), "The 5 modified documents should have been overwritten since their content changed.");
	}

	private void writeTenDocuments() {
		new WriteBatcherTemplate(Common.client).runWriteJob(writeBatcher -> writeBatcher
				.withThreadCount(1).withBatchSize(5)
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length))
				.withDocumentWriteSetFilter(filter),

			writeBatcher -> {
				for (int i = 1; i <= 10; i++) {
					// Consistent URIs are required for incremental writes to work.
					String uri = "/incremental/test/doc-" + i + ".xml";
					String content = "<doc>This is document number " + i + "</doc>";
					writeBatcher.add(uri, METADATA, new StringHandle(content));
				}
			}
		);
	}

	private void modifyFiveDocuments() {
		new WriteBatcherTemplate(Common.client).runWriteJob(writeBatcher -> writeBatcher
				.withThreadCount(1).withBatchSize(5)
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length))
				.withDocumentWriteSetFilter(filter),

			writeBatcher -> {
				for (int i = 6; i <= 10; i++) {
					String uri = "/incremental/test/doc-" + i + ".xml";
					String content = "<doc>This is modified content</doc>";
					writeBatcher.add(uri, METADATA, new StringHandle(content));
				}
			}
		);
	}

	private void writeDocs(List<DocumentWriteOperation> docs) {
		new WriteBatcherTemplate(Common.client).runWriteJob(
			writeBatcher -> writeBatcher
				.withDocumentWriteSetFilter(filter)
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length)),

			writeBatcher -> docs.forEach(writeBatcher::add)
		);
	}

	// Experimenting with a template that gets rid of some annoying DMSDK boilerplate.
	private record WriteBatcherTemplate(DatabaseClient databaseClient) {

		public void runWriteJob(Consumer<WriteBatcher> writeBatcherConfigurer, Consumer<WriteBatcher> writeBatcherUser) {
			try (DataMovementManager dmm = databaseClient.newDataMovementManager()) {
				WriteBatcher writeBatcher = dmm.newWriteBatcher();
				writeBatcherConfigurer.accept(writeBatcher);

				dmm.startJob(writeBatcher);
				writeBatcherUser.accept(writeBatcher);
				writeBatcher.flushAndWait();
				writeBatcher.awaitCompletion();
				dmm.stopJob(writeBatcher);
			}
		}
	}
}
