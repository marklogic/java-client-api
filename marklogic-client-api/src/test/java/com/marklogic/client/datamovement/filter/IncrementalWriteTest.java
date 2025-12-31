/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class IncrementalWriteTest extends AbstractClientTest {

	private static final DocumentMetadataHandle METADATA = new DocumentMetadataHandle()
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
			.build();
	}

	@Test
	void opticFilter() {
		verifyIncrementalWriteWorks();
	}

	@Test
	void evalFilter() {
		filter = IncrementalWriteFilter.newBuilder()
			.useEvalQuery(true)
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		verifyIncrementalWriteWorks();
	}

	@Test
	void jsonKeysOutOfOrder() {
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

	@Test
	void invalidJsonWithNoFormat() {
		docs.add(new DocumentWriteOperationImpl("/not-json.txt", METADATA, new StringHandle("{\"not actually json")));
		writeDocs(docs);

		assertEquals(1, writtenCount.get(), "When the format is not specified and the content looks like JSON " +
			"because it starts with a '{', the JSON canonicalization should fail and log a warning. The " +
			"document should still be written with a hash generated based on the text in the document.");

		assertNull(batchFailure.get(), "No failure should have been thrown since the format on the content is " +
			"not JSON, and thus the content should be hashed as text.");
	}

	@Test
	void invalidJsonWithFormat() {
		docs.add(new DocumentWriteOperationImpl("/not.json", METADATA, new StringHandle("not actually json").withFormat(Format.JSON)));
		writeDocs(docs);

		assertNotNull(batchFailure.get(), "A failure should have been thrown by the server since the content is not " +
			"JSON. But the failure to canonicalize should still be logged, as the user will be far more interested " +
			"in the error from the server.");

		String message = batchFailure.get().getMessage();
		assertTrue(message.contains("failed to apply resource at documents"),
			"Expecting the server to throw an error. Actual message: " + message);
	}

	private void verifyIncrementalWriteWorks() {
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
				.onBatchSuccess(batch -> writtenCount.addAndGet(batch.getItems().length))
				.onBatchFailure((batch, failure) -> batchFailure.set(failure)),

			writeBatcher -> docs.forEach(writeBatcher::add)
		);
	}
}
