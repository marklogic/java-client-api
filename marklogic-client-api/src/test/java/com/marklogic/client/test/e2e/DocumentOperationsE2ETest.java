/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.io.*;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Comprehensive Document Operations across All Formats and Modes")
public class DocumentOperationsE2ETest extends AbstractClientTest {

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	@DisplayName("JSON Document Full Lifecycle: Write, Read, Metadata, Patch, Delete")
	void testJsonDocumentLifecycle() throws Exception {
		DatabaseClient client = Common.connect();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		String uri = "/e2e/test-" + UUID.randomUUID() + ".json";

		// 1. Create JSON document with metadata
		ObjectNode json = mapper.createObjectNode()
			.put("title", "E2E Test Document")
			.put("status", "ACTIVE")
			.put("counter", 100);

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().addAll("e2e-collection", "json-docs");
		metadata.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ);
		metadata.getProperties().put("author", "rsingh");
		metadata.setQuality(5);

		docMgr.write(uri, metadata, new JacksonHandle(json));

		// 2. Read content back and verify
		JacksonHandle readHandle = new JacksonHandle();
		docMgr.read(uri, readHandle);
		JsonNode readNode = readHandle.get();
		assertEquals("E2E Test Document", readNode.get("title").asText());
		assertEquals("ACTIVE", readNode.get("status").asText());
		assertEquals(100, readNode.get("counter").asInt());

		// 3. Read metadata back and verify
		DocumentMetadataHandle readMeta = new DocumentMetadataHandle();
		docMgr.readMetadata(uri, readMeta);
		assertTrue(readMeta.getCollections().contains("e2e-collection"));
		assertTrue(readMeta.getCollections().contains("json-docs"));
		assertEquals("rsingh", readMeta.getProperties().get("author"));
		assertEquals(5, readMeta.getQuality());

		// 4. Apply JSON partial update (Patch) using JSONPath
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder()
			.pathLanguage(DocumentPatchBuilder.PathLanguage.JSONPATH);
		patchBldr.replaceValue("$.status", "COMPLETED");
		patchBldr.insertFragment("$.counter", Position.AFTER, "{\"verified\": true}");
		docMgr.patch(uri, patchBldr.build());

		// 5. Verify patch result
		JsonNode patchedNode = docMgr.read(uri, new JacksonHandle()).get();
		assertEquals("COMPLETED", patchedNode.get("status").asText());
		assertTrue(patchedNode.get("verified").asBoolean());
		assertEquals(100, patchedNode.get("counter").asInt());

		// 6. Delete document and assert ResourceNotFoundException
		docMgr.delete(uri);
		assertThrows(ResourceNotFoundException.class, () -> docMgr.read(uri, new JacksonHandle()));
	}

	@Test
	@DisplayName("XML Document Full Lifecycle: Write, Read, DOMHandle, and Metadata")
	void testXmlDocumentLifecycle() {
		DatabaseClient client = Common.connect();
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		String uri = "/e2e/test-" + UUID.randomUUID() + ".xml";

		String xmlContent = """
			<order id="ord-999">
				<customer>Alice Corp</customer>
				<total>450.75</total>
			</order>
			""";

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().addAll("e2e-xml", "orders");

		docMgr.write(uri, metadata, new StringHandle(xmlContent).withFormat(Format.XML));

		// Read via DOMHandle
		DOMHandle domHandle = new DOMHandle();
		docMgr.read(uri, domHandle);
		Document doc = domHandle.get();
		assertEquals("order", doc.getDocumentElement().getNodeName());
		assertEquals("ord-999", doc.getDocumentElement().getAttribute("id"));
		assertEquals("Alice Corp", doc.getElementsByTagName("customer").item(0).getTextContent());

		// Cleanup
		docMgr.delete(uri);
		assertFalse(docMgr.exists(uri) != null);
	}

	@Test
	@DisplayName("Text and Binary Documents with Streaming Handles")
	void testTextAndBinaryDocuments() throws Exception {
		DatabaseClient client = Common.connect();
		TextDocumentManager textMgr = client.newTextDocumentManager();
		BinaryDocumentManager binaryMgr = client.newBinaryDocumentManager();

		// Text Document
		String textUri = "/e2e/test-" + UUID.randomUUID() + ".txt";
		String rawText = "Hello MarkLogic E2E Streaming Text " + System.currentTimeMillis();
		textMgr.write(textUri, new StringHandle(rawText));
		assertEquals(rawText, textMgr.read(textUri, new StringHandle()).get());
		textMgr.delete(textUri);

		// Binary Document with raw bytes and InputStream
		String binUri = "/e2e/test-" + UUID.randomUUID() + ".bin";
		byte[] originalBytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC, (byte) 0xDE, (byte) 0xF0};
		binaryMgr.write(binUri, new BytesHandle(originalBytes));

		BytesHandle readBytesHandle = new BytesHandle();
		binaryMgr.read(binUri, readBytesHandle);
		assertArrayEquals(originalBytes, readBytesHandle.get());

		// Stream read verification
		InputStreamHandle isHandle = new InputStreamHandle();
		binaryMgr.read(binUri, isHandle);
		try (InputStream is = isHandle.get()) {
			byte[] streamedBytes = is.readAllBytes();
			assertArrayEquals(originalBytes, streamedBytes);
		}
		binaryMgr.delete(binUri);
	}

	@Test
	@DisplayName("Bulk Document Operations: Bulk WriteSet and Bulk DocumentPage Read")
	void testBulkWriteAndRead() {
		DatabaseClient client = Common.connect();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		int docCount = 20;
		String[] uris = new String[docCount];
		DocumentWriteSet writeSet = docMgr.newWriteSet();

		for (int i = 0; i < docCount; i++) {
			uris[i] = "/e2e/bulk/" + i + "-" + UUID.randomUUID() + ".json";
			String content = String.format("{\"index\": %d, \"tag\": \"e2e-bulk\"}", i);
			writeSet.add(uris[i], new StringHandle(content).withFormat(Format.JSON));
		}

		// Perform atomic bulk write
		docMgr.write(writeSet);

		// Read back using bulk read
		DocumentPage page = docMgr.read(uris);
		assertEquals(docCount, page.size());
		int readCount = 0;
		while (page.hasNext()) {
			DocumentRecord record = page.next();
			assertNotNull(record.getUri());
			assertEquals(Format.JSON, record.getFormat());
			String json = record.getContent(new StringHandle()).get();
			assertTrue(json.contains("e2e-bulk"));
			readCount++;
		}
		assertEquals(docCount, readCount);

		// Bulk delete
		docMgr.delete(uris);
	}

	@Test
	@DisplayName("Optimistic Concurrency Control: Version Matching and 412 Precondition Failed")
	void testOptimisticConcurrencyControl() throws Exception {
		com.marklogic.client.admin.ServerConfigurationManager serverConfig =
			Common.connectRestAdmin().newServerConfigManager();
		serverConfig.readConfiguration();
		com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy origPolicy =
			serverConfig.getUpdatePolicy();
		String uri = "/e2e/occ-" + UUID.randomUUID() + ".json";
		try {
			serverConfig.setUpdatePolicy(com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy.VERSION_REQUIRED);
			serverConfig.writeConfiguration();
			Common.propertyWait();

			DatabaseClient client = Common.connect();
			JSONDocumentManager docMgr = client.newJSONDocumentManager();

			// Initial write
			StringHandle content1 = new StringHandle("{\"version\": 1}").withFormat(Format.JSON);
			docMgr.write(uri, content1);

			// Read with descriptor to capture version/ETag
			DocumentDescriptor desc = docMgr.newDescriptor(uri);
			docMgr.read(desc, new StringHandle());
			long version = desc.getVersion();
			assertTrue(version > 0, "Effective version/ETag must be positive");

			// Successful conditional update with matching version
			DocumentDescriptor matchingDesc = docMgr.newDescriptor(uri);
			matchingDesc.setVersion(version);
			StringHandle content2 = new StringHandle("{\"version\": 2}").withFormat(Format.JSON);
			docMgr.write(matchingDesc, content2);

			// Attempt update with stale version; must fail with FailedRequestException (412 Precondition Failed)
			DocumentDescriptor staleDesc = docMgr.newDescriptor(uri);
			staleDesc.setVersion(version);
			StringHandle content3 = new StringHandle("{\"version\": 3}").withFormat(Format.JSON);
			FailedRequestException ex = assertThrows(FailedRequestException.class, () -> docMgr.write(staleDesc, content3));
			assertEquals(412, ex.getServerStatusCode(), "Stale update must fail with 412 Precondition Failed");

		} finally {
			serverConfig.setUpdatePolicy(origPolicy);
			serverConfig.writeConfiguration();
			Common.propertyWait();
			try {
				Common.connect().newJSONDocumentManager().delete(uri);
			} catch (Exception ignored) {
			}
		}
	}
}
