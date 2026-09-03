/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.example.extension;

import com.marklogic.client.example.extension.OpenCSVBatcherExample;
import com.marklogic.client.io.DOMHandle;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenCSVBatcherTest {

	@Test
	void testMain() throws Exception {
		// This is a simple smoke test to ensure that the main method runs without exceptions.
		OpenCSVBatcherExample.main(new String[0]);
	}

	@Test
	void documentBuilderShouldRejectDoctype() throws Exception {
		// Verifies that the DocumentBuilder produced by DOMHandle.getFactory()
		// — the same factory now used by OpenCSVBatcher.write() — rejects DOCTYPE declarations,
		// confirming that XXE / DTD processing is disabled (CWE-611).
		DocumentBuilder builder = new DOMHandle().getFactory().newDocumentBuilder();
		String xmlWithDoctype =
			"<?xml version=\"1.0\"?>" +
			"<!DOCTYPE foo []>" +
			"<foo/>";

		assertThrows(SAXException.class, () ->
			builder.parse(new ByteArrayInputStream(xmlWithDoctype.getBytes(StandardCharsets.UTF_8)))
		);
	}
}
