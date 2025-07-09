/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.*;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadDocumentPageTest {

	@Test
	void test() {
		Common.deleteUrisWithPattern("/aaa-page/*");

		final String uri = "/aaa-page/太田佳伸のＸＭＬファイル.xml";
		DocumentRecord documentRecord;
		try (DatabaseClient client = Common.newClient()) {
			client.newXMLDocumentManager().write(uri, Common.newDefaultMetadata(),
				new StringHandle("<test>太田佳伸のＸＭＬファイル</test>"));

			try (DocumentPage page = client.newXMLDocumentManager().read(uri)) {
				assertTrue(page.hasNext());
				documentRecord = page.next();
			}
		}
		assertEquals(uri, documentRecord.getUri());
	}

	@Test
	@Disabled("Disabling for now because this seems to be a server bug.")
	void testEmptyDocWithNoExtension() {
		final String collection = "empty-binary-test";

		try (DatabaseClient client = Common.newClient()) {
			writeEmptyDocWithNoFileExtension(client, collection);

			JSONDocumentManager documentManager = client.newJSONDocumentManager();
			StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
			DocumentRecord documentRecord;
			try (DocumentPage documentPage = documentManager.search(query, 1)) {
				assertTrue(documentPage.hasNext(), "Expected a document in the page, but none was found.");
				documentRecord = documentPage.next();
			}
			String uri = documentRecord.getUri();
			assertEquals("/test/empty", uri, "The URI of the empty document should match the one written.");
		}
	}

	protected void writeEmptyDocWithNoFileExtension(DatabaseClient client, String... collections) {
		DocumentMetadataHandle metadata = new DocumentMetadataHandle()
			.withCollections(collections)
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);
		// This needs to be a JSON document manager because the empty document is written without a format.
		JSONDocumentManager mgr = client.newJSONDocumentManager();
		DocumentWriteSet set = mgr.newWriteSet();
		BytesHandle emptyBytesHandle = new BytesHandle(new byte[0]);
		String uri = "/test/empty";
		set.add(uri, metadata, emptyBytesHandle);
		mgr.write(set);
	}
}
