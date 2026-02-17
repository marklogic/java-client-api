/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.*;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadJsonAsBinaryTest extends AbstractClientTest {

	@Test
	@Disabled("See MLE-27191 for description of the server bug")
	void test() {
		try (DatabaseClient client = Common.newClient()) {
			final String uri = "/a.json";
			writeJsonAsBinary(uri, client);

			GenericDocumentManager docManager = client.newDocumentManager();

			// A multipart request returns the correct document format.
			try (DocumentPage page = docManager.read(uri)) {
				DocumentRecord record = page.next();
				assertEquals(uri, record.getUri());
				assertEquals("application/json", record.getMimetype());
				assertEquals(Format.BINARY, record.getFormat());
			}

			// But a request for a single URI does not!
			try (InputStreamHandle handle = docManager.read(uri, new InputStreamHandle())) {
				assertEquals("application/json", handle.getMimetype());
				assertEquals(Format.BINARY, handle.getFormat(), "Unfortunately due to MLE-27191, the " +
					"format is JSON instead of binary. So this assertion will fail until that bug is fixed. " +
					"For a Java Client user, the workaround is to use the read method above and get a DocumentPage " +
					"back, as a multipart response seems to identify the correct headers.");
			}

		}
	}

	private void writeJsonAsBinary(String uri, DatabaseClient client) {
		JSONDocumentManager jsonDocManager = client.newJSONDocumentManager();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle()
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);

		jsonDocManager.setWriteTransform(new ServerTransform("toBinary"));
		jsonDocManager.write(uri, metadata, new JacksonHandle(new ObjectMapper().createObjectNode().put("a", 1)));
	}
}
