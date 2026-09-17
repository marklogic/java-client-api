/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncrementalWriteSourceUriKeyNameTest extends AbstractIncrementalWriteTest {

	private static final String SOURCE_URI_KEY_NAME = "incrementalWriteSourceUri";
	private static final String SOURCE_URI = "/incremental/test/source-uri-doc.xml";
	private static final String RELOCATED_URI = "/incremental/test/relocated-doc.xml";

	@Override
	@BeforeEach
	void setup() {
		super.setup();
		filter = IncrementalWriteFilter.newBuilder()
			.sourceUriKeyName(SOURCE_URI_KEY_NAME)
			.onDocumentsSkipped(docs -> skippedCount.addAndGet(docs.length))
			.build();
	}

	@Test
	void stampsSourceUriMetadataAutomatically() {
		writeDoc(SOURCE_URI, "<doc>original content</doc>");

		DocumentMetadataHandle metadata = Common.client.newDocumentManager().readMetadata(SOURCE_URI,
			new DocumentMetadataHandle());
		assertEquals(SOURCE_URI, metadata.getMetadataValues().get(SOURCE_URI_KEY_NAME),
			"The filter should automatically stamp the configured metadata key with the document's own URI.");
	}

	@Test
	void matchesAfterExternalRelocation() {
		// Initial write via the filter.
		writeDoc(SOURCE_URI, "<doc>original content</doc>");
		assertEquals(1, writtenCount.get());
		assertEquals(0, skippedCount.get());

		relocateDocument();

		// Same source URI, unchanged content - should be recognized as unchanged even though the document
		// now physically lives at a different URI, because the lookup matches on the source-URI field value.
		writeDoc(SOURCE_URI, "<doc>original content</doc>");
		assertEquals(1, writtenCount.get(), "No new write should have occurred since the document was skipped.");
		assertEquals(1, skippedCount.get());

		assertNull(Common.client.newDocumentManager().exists(SOURCE_URI),
			"Nothing should have been written back to the source URI since the write was skipped.");
		assertNotNull(Common.client.newDocumentManager().exists(RELOCATED_URI),
			"The relocated document should still be the only copy of this content.");
	}

	@Test
	void changedContentIsWrittenAfterExternalRelocation() {
		writeDoc(SOURCE_URI, "<doc>original content</doc>");
		relocateDocument();

		// Content has changed, so the write should go through - to the source URI, not the relocated one.
		writeDoc(SOURCE_URI, "<doc>modified content</doc>");
		assertEquals(2, writtenCount.get());
		assertEquals(0, skippedCount.get());

		String newContent = Common.client.newTextDocumentManager().read(SOURCE_URI, new StringHandle()).get();
		assertNotNull(newContent);
		assertTrue(newContent.contains("<doc>modified content</doc>"));

		DocumentMetadataHandle metadata = Common.client.newDocumentManager().readMetadata(SOURCE_URI,
			new DocumentMetadataHandle());
		assertEquals(SOURCE_URI, metadata.getMetadataValues().get(SOURCE_URI_KEY_NAME),
			"The rewritten document should have the source-URI metadata value stamped again.");
	}

	private void writeDoc(String uri, String content) {
		List<DocumentWriteOperation> ops = new ArrayList<>();
		ops.add(new DocumentWriteOperationImpl(uri, METADATA, new StringHandle(content)));
		writeDocs(ops);
	}

	/**
	 * Simulates a process outside of this filter relocating the document to a new URI while preserving its
	 * metadata (in particular, the source-URI metadata value), then removing the original.
	 */
	private void relocateDocument() {
		DocumentMetadataHandle metadata = Common.client.newDocumentManager().readMetadata(SOURCE_URI,
			new DocumentMetadataHandle());
		String content = Common.client.newTextDocumentManager().read(SOURCE_URI, new StringHandle()).get();

		Common.client.newTextDocumentManager().write(RELOCATED_URI, metadata, new StringHandle(content));
		Common.client.newDocumentManager().delete(SOURCE_URI);
	}
}
