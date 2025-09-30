/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

class GetDocumentTest {

	@Test
	void jsonDocument() {
		try (DatabaseClient client = Common.newClient()) {
			var doc = client.newJSONDocumentManager().read("/optic/test/musician1.json", new StringHandle()).get();
			System.out.println(doc);
		}
	}
}
