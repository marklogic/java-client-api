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
}
