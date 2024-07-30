/*
 * Copyright © 2024 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadDocumentPageTest {

	@Test
	void test() {
		Common.deleteUrisWithPattern("/aaa-page/*");

		final String uri = "/aaa-page/太田佳伸のＸＭＬファイル.xml";
		DatabaseClient client = Common.newClient();
		client.newXMLDocumentManager().write(uri, Common.newDefaultMetadata(),
			new StringHandle("<test>太田佳伸のＸＭＬファイル</test>"));

		DocumentPage page = client.newXMLDocumentManager().read(uri);
		assertTrue(page.hasNext());
		DocumentRecord record = page.next();
		assertEquals(uri, record.getUri());
	}
}
