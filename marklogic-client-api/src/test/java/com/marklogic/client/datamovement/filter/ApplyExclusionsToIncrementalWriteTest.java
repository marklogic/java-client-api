/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplyExclusionsToIncrementalWriteTest extends AbstractIncrementalWriteTest {

	@Test
	void jsonExclusions() {
		filter = IncrementalWriteFilter.newBuilder()
			.jsonExclusions("/timestamp", "/metadata/lastModified")
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		// Write initial documents with three keys
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("id", i);
			doc.put("name", "Document " + i);
			doc.put("timestamp", "2025-01-01T10:00:00Z");
			doc.putObject("metadata")
				.put("lastModified", "2025-01-01T10:00:00Z")
				.put("author", "Test User");
			docs.add(new DocumentWriteOperationImpl("/incremental/test/json-doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(5, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write again with different values for excluded fields - should be skipped
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("id", i);
			doc.put("name", "Document " + i);
			doc.put("timestamp", "2026-01-02T15:30:00Z"); // Changed
			doc.putObject("metadata")
				.put("lastModified", "2026-01-02T15:30:00Z") // Changed
				.put("author", "Test User");
			docs.add(new DocumentWriteOperationImpl("/incremental/test/json-doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(5, writtenCount.get(), "Documents should be skipped since only excluded fields changed");
		assertEquals(5, skippedCount.get());

		// Write again with actual content change - should NOT be skipped
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			ObjectNode doc = objectMapper.createObjectNode();
			doc.put("id", i);
			doc.put("name", "Modified Document " + i); // Changed
			doc.put("timestamp", "2026-01-02T16:00:00Z");
			doc.putObject("metadata")
				.put("lastModified", "2026-01-02T16:00:00Z")
				.put("author", "Test User");
			docs.add(new DocumentWriteOperationImpl("/incremental/test/json-doc-" + i + ".json", METADATA, new JacksonHandle(doc)));
		}

		writeDocs(docs);
		assertEquals(10, writtenCount.get(), "Documents should be written since non-excluded content changed");
		assertEquals(5, skippedCount.get(), "Skip count should remain at 5");
	}

	@Test
	void xmlExclusions() {
		filter = IncrementalWriteFilter.newBuilder()
			.xmlExclusions("//timestamp", "//metadata/lastModified")
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		// Write initial documents
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String xml = "<doc>" +
				"<id>" + i + "</id>" +
				"<name>Document " + i + "</name>" +
				"<timestamp>2025-01-01T10:00:00Z</timestamp>" +
				"<metadata>" +
				"<author>Test User</author>" +
				"<lastModified>2025-01-01T10:00:00Z</lastModified>" +
				"</metadata>" +
				"</doc>";
			docs.add(new DocumentWriteOperationImpl("/incremental/test/xml-doc-" + i + ".xml", METADATA, new StringHandle(xml).withFormat(Format.XML)));
		}

		writeDocs(docs);
		assertEquals(5, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write again with different values for excluded fields - should be skipped
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String xml = "<doc>" +
				"<id>" + i + "</id>" +
				"<name>Document " + i + "</name>" +
				"<timestamp>2026-01-02T15:30:00Z</timestamp>" + // Changed
				"<metadata>" +
				"<author>Test User</author>" +
				"<lastModified>2026-01-02T15:30:00Z</lastModified>" + // Changed
				"</metadata>" +
				"</doc>";
			docs.add(new DocumentWriteOperationImpl("/incremental/test/xml-doc-" + i + ".xml", METADATA, new StringHandle(xml).withFormat(Format.XML)));
		}

		writeDocs(docs);
		assertEquals(5, writtenCount.get(), "Documents should be skipped since only excluded fields changed");
		assertEquals(5, skippedCount.get());

		// Write again with actual content change - should NOT be skipped
		docs = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String xml = "<doc>" +
				"<id>" + i + "</id>" +
				"<name>Modified Document " + i + "</name>" + // Changed
				"<timestamp>2026-01-02T16:00:00Z</timestamp>" +
				"<metadata>" +
				"<author>Test User</author>" +
				"<lastModified>2026-01-02T16:00:00Z</lastModified>" +
				"</metadata>" +
				"</doc>";
			docs.add(new DocumentWriteOperationImpl("/incremental/test/xml-doc-" + i + ".xml", METADATA, new StringHandle(xml).withFormat(Format.XML)));
		}

		writeDocs(docs);
		assertEquals(10, writtenCount.get(), "Documents should be written since non-excluded content changed");
		assertEquals(5, skippedCount.get(), "Skip count should remain at 5");
	}

	/**
	 * Verifies that JSON Pointer exclusions are only applied to JSON documents and are ignored for XML documents.
	 * The XML document should use its full content for hashing since no XML exclusions are configured.
	 */
	@Test
	void jsonExclusionsIgnoredForXmlDocuments() {
		filter = IncrementalWriteFilter.newBuilder()
			.jsonExclusions("/timestamp")
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		// Write one JSON doc and one XML doc
		docs = new ArrayList<>();
		ObjectNode jsonDoc = objectMapper.createObjectNode();
		jsonDoc.put("id", 1);
		jsonDoc.put("timestamp", "2025-01-01T10:00:00Z");
		docs.add(new DocumentWriteOperationImpl("/incremental/test/mixed-doc.json", METADATA, new JacksonHandle(jsonDoc)));

		String xmlDoc = "<doc><id>1</id><timestamp>2025-01-01T10:00:00Z</timestamp></doc>";
		docs.add(new DocumentWriteOperationImpl("/incremental/test/mixed-doc.xml", METADATA, new StringHandle(xmlDoc).withFormat(Format.XML)));

		writeDocs(docs);
		assertEquals(2, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write again with different timestamp values
		docs = new ArrayList<>();
		jsonDoc = objectMapper.createObjectNode();
		jsonDoc.put("id", 1);
		jsonDoc.put("timestamp", "2026-01-02T15:30:00Z"); // Changed
		docs.add(new DocumentWriteOperationImpl("/incremental/test/mixed-doc.json", METADATA, new JacksonHandle(jsonDoc)));

		xmlDoc = "<doc><id>1</id><timestamp>2026-01-02T15:30:00Z</timestamp></doc>"; // Changed
		docs.add(new DocumentWriteOperationImpl("/incremental/test/mixed-doc.xml", METADATA, new StringHandle(xmlDoc).withFormat(Format.XML)));

		writeDocs(docs);
		assertEquals(3, writtenCount.get(), "XML doc should be written since its timestamp changed and no XML exclusions are configured");
		assertEquals(1, skippedCount.get(), "JSON doc should be skipped since only the excluded timestamp field changed");
	}

	/**
	 * Verifies that when canonicalizeJson is false, documents with logically identical content
	 * but different key ordering will produce different hashes, causing a write to occur.
	 */
	@Test
	void jsonNotCanonicalizedCausesDifferentHashForReorderedKeys() {
		filter = IncrementalWriteFilter.newBuilder()
			.canonicalizeJson(false)
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		// Write initial document with keys in a specific order
		docs = new ArrayList<>();
		String json1 = "{\"name\":\"Test\",\"id\":1,\"value\":100}";
		docs.add(new DocumentWriteOperationImpl("/incremental/test/non-canonical.json", METADATA,
			new StringHandle(json1).withFormat(Format.JSON)));

		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write again with same logical content but different key order
		docs = new ArrayList<>();
		String json2 = "{\"id\":1,\"value\":100,\"name\":\"Test\"}";
		docs.add(new DocumentWriteOperationImpl("/incremental/test/non-canonical.json", METADATA,
			new StringHandle(json2).withFormat(Format.JSON)));

		writeDocs(docs);
		assertEquals(2, writtenCount.get(), "Document should be written because key order differs and JSON is not canonicalized");
		assertEquals(0, skippedCount.get(), "No documents should be skipped");
	}

	/**
	 * Verifies that with the default canonicalizeJson(true), documents with logically identical content
	 * but different key ordering will produce the same hash, causing the document to be skipped.
	 */
	@Test
	void jsonCanonicalizedProducesSameHashForReorderedKeys() {
		filter = IncrementalWriteFilter.newBuilder()
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();

		// Write initial document with keys in a specific order
		docs = new ArrayList<>();
		String json1 = "{\"name\":\"Test\",\"id\":1,\"value\":100}";
		docs.add(new DocumentWriteOperationImpl("/incremental/test/canonical.json", METADATA,
			new StringHandle(json1).withFormat(Format.JSON)));

		writeDocs(docs);
		assertEquals(1, writtenCount.get());
		assertEquals(0, skippedCount.get());

		// Write again with same logical content but different key order
		docs = new ArrayList<>();
		String json2 = "{\"id\":1,\"value\":100,\"name\":\"Test\"}";
		docs.add(new DocumentWriteOperationImpl("/incremental/test/canonical.json", METADATA,
			new StringHandle(json2).withFormat(Format.JSON)));

		writeDocs(docs);
		assertEquals(1, writtenCount.get(), "Document should be skipped because canonicalized JSON produces the same hash");
		assertEquals(1, skippedCount.get(), "One document should be skipped");
	}
}
