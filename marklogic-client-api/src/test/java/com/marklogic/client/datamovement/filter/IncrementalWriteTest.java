/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.*;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.*;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncrementalWriteTest extends AbstractIncrementalWriteTest {

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

	@Test
	void noRangeIndexForField() {
		filter = IncrementalWriteFilter.newBuilder()
			.hashKeyName("non-existent-field")
			.build();

		writeTenDocuments();

		assertNotNull(batchFailure.get());
		String message = batchFailure.get().getMessage();
		assertTrue(message.contains("Unable to query for existing incremental write hashes") && message.contains("XDMP-FIELDRIDXNOTFOUND"),
			"When the user tries to use the incremental write feature without the required range index, we should " +
				"fail with a helpful error message. Actual message: " + message);
	}

	@Test
	void noRangeIndexForFieldWithEval() {
		filter = IncrementalWriteFilter.newBuilder()
			.hashKeyName("non-existent-field")
			.useEvalQuery(true)
			.build();

		writeTenDocuments();

		assertNotNull(batchFailure.get());
		String message = batchFailure.get().getMessage();
		assertTrue(message.contains("Unable to query for existing incremental write hashes") && message.contains("XDMP-FIELDRIDXNOTFOUND"),
			"When the user tries to use the incremental write feature without the required range index, we should " +
				"fail with a helpful error message. Actual message: " + message);
	}

	@Test
	void customTimestampKeyName() {
		filter = IncrementalWriteFilter.newBuilder()
			.hashKeyName("myWriteHash")
			.timestampKeyName("myTimestamp")
			.build();

		writeTenDocuments();

		DocumentMetadataHandle metadata = Common.client.newDocumentManager().readMetadata("/incremental/test/doc-1.xml",
			new DocumentMetadataHandle());

		assertNotNull(metadata.getMetadataValues().get("myWriteHash"));
		assertNotNull(metadata.getMetadataValues().get("myTimestamp"));
		assertFalse(metadata.getMetadataValues().containsKey("incrementalWriteHash"));
		assertFalse(metadata.getMetadataValues().containsKey("incrementalWriteTimestamp"));
	}

	/**
	 * The thought for this test is that if the user passes null in (which could happen via our Spark connector),
	 * they're breaking the feature. So don't let them do that - ignore null and use the default values.
	 */
	@Test
	void nullIsIgnoredForKeyNames() {
		filter = IncrementalWriteFilter.newBuilder()
			.hashKeyName(null)
			.timestampKeyName(null)
			.build();

		writeTenDocuments();

		DocumentMetadataHandle metadata = Common.client.newDocumentManager().readMetadata("/incremental/test/doc-1.xml",
			new DocumentMetadataHandle());

		assertNotNull(metadata.getMetadataValues().get("incrementalWriteHash"));
		assertNotNull(metadata.getMetadataValues().get("incrementalWriteTimestamp"));
	}

	@Test
	void textDocument() {
		final DocumentWriteOperation writeOp = new DocumentWriteOperationImpl("/incremental/test/doc.txt", METADATA,
			new StringHandle("Hello world"));

		docs.add(writeOp);
		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write the same text document again
		docs = new ArrayList<>();
		docs.add(writeOp);
		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(1, skippedCount.get(), "This is a sanity check to verify that text files work as expected. " +
			"Exclusions can't yet be specified for them since we only support JSON Pointer and XPath so far. It may " +
			"be worth supporting regex-based exclusions for text files in the future.");
	}

	@Test
	void binaryDocument() {
		byte[] binaryContent = "Binary content example".getBytes();
		final DocumentWriteOperation writeOp = new DocumentWriteOperationImpl("/incremental/test/doc.bin", METADATA,
			new BytesHandle(binaryContent).withFormat(Format.BINARY));

		docs.add(writeOp);
		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write the same binary document again
		docs = new ArrayList<>();
		docs.add(writeOp);
		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(1, skippedCount.get(), "Another sanity check to make sure that binary documents work as " +
			"expected. Exclusions cannot be specified for them.");
	}

	private void verifyIncrementalWriteWorks() {
		writeTenDocuments();
		verifyDocumentsHasHashInMetadataKey();
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
		docs = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			// Consistent URIs are required for incremental writes to work.
			String uri = "/incremental/test/doc-" + i + ".xml";
			String content = "<doc>This is document number " + i + "</doc>";
			docs.add(new DocumentWriteOperationImpl(uri, METADATA, new StringHandle(content)));
		}
		writeDocs(docs);
	}

	private void verifyDocumentsHasHashInMetadataKey() {
		GenericDocumentManager mgr = Common.client.newDocumentManager();
		mgr.setMetadataCategories(DocumentManager.Metadata.METADATAVALUES);
		DocumentPage page = mgr.search(Common.client.newQueryManager().newStructuredQueryBuilder().collection("incremental-test"), 1);
		while (page.hasNext()) {
			DocumentRecord doc = page.next();
			DocumentMetadataHandle metadata = doc.getMetadata(new DocumentMetadataHandle());

			String hash = metadata.getMetadataValues().get("incrementalWriteHash");
			try {
				// Can use Java's support for parsing unsigned longs in base 16 to verify the hash is valid.
				Long.parseUnsignedLong(hash, 16);
			} catch (NumberFormatException e) {
				fail("Document " + doc.getUri() + " has an invalid incrementalWriteHash value: " + hash);
			}

			String timestamp = metadata.getMetadataValues().get("incrementalWriteTimestamp");
			assertNotNull(timestamp, "Document " + doc.getUri() + " should have an incrementalWriteTimestamp value.");
		}
	}

	private void modifyFiveDocuments() {
		docs = new ArrayList<>();
		for (int i = 6; i <= 10; i++) {
			String uri = "/incremental/test/doc-" + i + ".xml";
			String content = "<doc>This is modified content</doc>";
			docs.add(new DocumentWriteOperationImpl(uri, METADATA, new StringHandle(content)));
		}
		writeDocs(docs);
	}
}
