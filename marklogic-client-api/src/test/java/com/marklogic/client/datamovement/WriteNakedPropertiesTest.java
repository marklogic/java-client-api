/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WriteNakedPropertiesTest extends AbstractClientTest {

	@Test
	void test() {
		DatabaseClient client = Common.newClient();
		DataMovementManager dmm = client.newDataMovementManager();
		WriteBatcher writeBatcher = dmm.newWriteBatcher();
		dmm.startJob(writeBatcher);

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getProperties().put(new QName("org:example", "hello"), "world");
		writeBatcher.add("/naked.xml", metadata, null);
		writeBatcher.flushAndWait();
		dmm.stopJob(writeBatcher);

		DatabaseClient evalClient = Common.newEvalClient();
		String properties = evalClient.newServerEval()
			.xquery("xdmp:document-properties('/naked.xml')").evalAs(String.class);
		assertTrue(properties.contains("world"), "Should be able to read the 'naked' properties fragment, " +
			"which verifies that it was written correctly, even with the content handle being null.");

		String output = evalClient.newServerEval().xquery("fn:doc-available('/naked.xml')").evalAs(String.class);
		assertEquals("false", output, "No document exists, only a properties fragment.");
	}
}
