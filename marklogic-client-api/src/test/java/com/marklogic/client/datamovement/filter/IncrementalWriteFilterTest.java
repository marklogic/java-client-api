/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests that make no connection to MarkLogic.
 */
class IncrementalWriteFilterTest {

	/**
	 * Verifies that when a hash is added, a new metadata object is created so that a doc-specific hash field can be
	 * added without affecting any other document that might be sharing the same metadata object.
	 */
	@Test
	void addHashToMetadata() {
		DocumentMetadataHandle metadata = new DocumentMetadataHandle()
			.withCollections("c1")
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
			.withQuality(2)
			.withProperty("prop1", "value1")
			.withMetadataValue("meta1", "value1");

		DocumentWriteOperation doc1 = new DocumentWriteOperationImpl("/1.xml", metadata, new StringHandle("<doc1/>"));
		DocumentWriteOperation doc2 = new DocumentWriteOperationImpl("/2.xml", metadata, new StringHandle("<doc2/>"));

		doc2 = IncrementalWriteFilter.addHashToMetadata(doc2, "theField", "abc123");

		assertEquals(metadata, doc1.getMetadata(), "doc1 should still have the original metadata object");

		DocumentMetadataHandle metadata2 = (DocumentMetadataHandle) doc2.getMetadata();
		assertEquals("c1", metadata2.getCollections().iterator().next(), "collection should be preserved");
		assertEquals(DocumentMetadataHandle.Capability.READ, metadata2.getPermissions().get("rest-reader").iterator().next(), "permission should be preserved");
		assertEquals(2, metadata2.getQuality(), "quality should be preserved");
		assertEquals("value1", metadata2.getProperties().get("prop1"), "property should be preserved");

		assertEquals("value1", metadata2.getMetadataValues().get("meta1"), "metadata value should be preserved");
		assertEquals("abc123", metadata2.getMetadataValues().get("theField"), "hash field should be added");
	}
}
