/*
 * Copyright 2012-2014 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.io.Format;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

public class JSONDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite() throws IOException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for JSON content",docText);
		JsonNode readNode = mapper.readTree(docText);
		assertTrue("Failed to read JSON document as String", sourceNode.equals(readNode));
		
		BytesHandle bytesHandle = new BytesHandle();
		docMgr.read(docId, bytesHandle);
		readNode = mapper.readTree(bytesHandle.get());
		assertTrue("JSON document mismatch reading bytes", sourceNode.equals(readNode));

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		docMgr.read(docId, inputStreamHandle);
		readNode = mapper.readTree(inputStreamHandle.get());
		assertTrue("JSON document mismatch reading input stream", sourceNode.equals(readNode));

		Reader reader = docMgr.read(docId, new ReaderHandle()).get();
		readNode = mapper.readTree(reader);
		assertTrue("JSON document mismatch with reader", sourceNode.equals(readNode));

		File file = docMgr.read(docId, new FileHandle()).get();
		readNode = mapper.readTree(file);
		assertTrue("JSON document mismatch with file", sourceNode.equals(readNode));

		docMgr.write(docId,
				new StringHandle().with(GenericDocumentTest.metadata).withFormat(Format.XML),
				new StringHandle().with(content));
		docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for JSON content",docText);
		readNode = mapper.readTree(docText);
		assertTrue("Failed to read JSON document as String", sourceNode.equals(readNode));

		String lang = "fr-CA";
		docMgr.setLanguage(lang);
		docMgr.write(docId, new StringHandle().with(content));

		XMLDocumentManager xmlMgr = Common.client.newXMLDocumentManager();
		Document document = xmlMgr.read(docId, new DOMHandle()).get();
		assertEquals("Failed to set language attribute on JSON", lang,
				document.getDocumentElement().getAttributeNS(XMLConstants.XML_NS_URI, "lang"));
	}

	@Test
	public void testPatch() throws IOException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();

		patchBldr.replaceValue("$.stringKey", Cardinality.ONE, "replaced value");

		patchBldr.delete("$.numberKey");

		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode.put("replacedChildKey","replaced object value");
		String fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.replaceFragment("$.objectKey.childObjectKey", fragment);

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey",9);
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("$.arrayKey", Position.BEFORE, fragment);

		patchBldr.replaceApply("$.arrayKey.[*][?(@=\"3\")]", patchBldr.call().add(2));

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("appendedKey","appended item");
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment(
				"$.arrayKey", Position.LAST_CHILD, Cardinality.ZERO_OR_ONE, fragment
				);

		DocumentPatchHandle patchHandle = patchBldr.build();

		docMgr.patch(docId, patchHandle);

		ObjectNode expectedNode = mapper.createObjectNode();
		expectedNode.put("stringKey", "replaced value");
		ObjectNode childNode = mapper.createObjectNode();
		childNode.put("replacedChildKey", "replaced object value");
		expectedNode.put("objectKey", childNode);
		expectedNode.put("insertedKey", 9);
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("item value");
		childArray.add(5);
		childNode = mapper.createObjectNode();
		childNode.put("itemObjectKey", "item object value");
		childArray.add(childNode);
		childNode = mapper.createObjectNode();
		childNode.put("appendedKey", "appended item");
		childArray.add(childNode);
		expectedNode.put("arrayKey", childArray);

		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for patched JSON content",docText);
		JsonNode readNode = mapper.readTree(docText);
		assertTrue("Patched JSON document without expected result", expectedNode.equals(readNode));

		docMgr.delete(docId);
	}

	private ObjectNode makeContent(ObjectMapper mapper) throws IOException {
		ObjectNode sourceNode = mapper.createObjectNode();
		sourceNode.put("stringKey", "string value");
		sourceNode.put("numberKey", 7);
		ObjectNode childNode = mapper.createObjectNode();
		childNode.put("childObjectKey", "child object value");
		sourceNode.put("objectKey", childNode);
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("item value");
		childArray.add(3);
		childNode = mapper.createObjectNode();
		childNode.put("itemObjectKey", "item object value");
		childArray.add(childNode);
		sourceNode.put("arrayKey", childArray);

		return sourceNode;
	}
}
