/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadDocumentPageTest extends AbstractClientTest {

	/**
	 * Verifies that the jakarta.mail library, instead of javax.mail, can probably read the URI.
	 * See MLE-15748, which pertains to issues with javax.mail only allowing US-ASCII characters.
	 */
	@Test
	void uriWithNonUsAsciiCharacters() {
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
	void emptyTextDocument() {
		final String uri = "/sample/empty-file.txt";

		try (DatabaseClient client = Common.newClient()) {
			JSONDocumentManager documentManager = client.newJSONDocumentManager();
			StructuredQueryDefinition query = new StructuredQueryBuilder().document(uri);
			DocumentRecord documentRecord;
			try (DocumentPage documentPage = documentManager.search(query, 1)) {
				assertTrue(documentPage.hasNext(), "Expected a document in the page, but none was found.");
				documentRecord = documentPage.next();
			}
			String actualUri = documentRecord.getUri();
			assertEquals(uri, actualUri, "The URI of the empty document should match the one written.");

			IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> documentRecord.getContent(new BytesHandle()));
			assertEquals("No bytes to write", ex.getMessage(),
				"This assertion is documenting existing behavior, where an empty doc will result in an " +
					"exception being thrown when an attempt is made to retrieve its content. " +
					"This doesn't seem ideal - returning null seems preferable - but it's the " +
					"behavior that has likely always existed.");
		}
	}
}
