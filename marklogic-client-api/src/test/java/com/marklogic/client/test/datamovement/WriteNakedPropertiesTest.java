package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteNakedPropertiesTest {

	@BeforeEach
	void setup() {
		Common.newRestAdminClient().newXMLDocumentManager().delete("/naked.xml");
	}

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
