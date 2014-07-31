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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

public class JSONDocumentTest {

	final static String metadata = 
			  "  {\"collections\":\n"
			+ "    [\"collection1\",\n" 
			+ "    \"collection2\",\n"
			+ "    \"collection4before\"],\n" 
			+ "   \"permissions\":[\n"
			+ "     {\"role-name\":\"app-user\",\n"
			+ "      \"capabilities\":[\"update\",\"read\"]}],\n"
			+ "   \"properties\":[\n" 
			+ "    {\"first\":\"value one\",\n"
			+ "     \"second\":2}]," 
			+ "  \"quality\":3}\n";

	static final private Logger logger = LoggerFactory
			.getLogger(JSONDocumentTest.class);

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
		assertNotNull("Read null string for JSON content", docText);
		JsonNode readNode = mapper.readTree(docText);
		assertTrue("Failed to read JSON document as String",
				sourceNode.equals(readNode));

		BytesHandle bytesHandle = new BytesHandle();
		docMgr.read(docId, bytesHandle);
		readNode = mapper.readTree(bytesHandle.get());
		assertTrue("JSON document mismatch reading bytes",
				sourceNode.equals(readNode));

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		docMgr.read(docId, inputStreamHandle);
		readNode = mapper.readTree(inputStreamHandle.get());
		assertTrue("JSON document mismatch reading input stream",
				sourceNode.equals(readNode));

		Reader reader = docMgr.read(docId, new ReaderHandle()).get();
		readNode = mapper.readTree(reader);
		assertTrue("JSON document mismatch with reader",
				sourceNode.equals(readNode));

		File file = docMgr.read(docId, new FileHandle()).get();
		readNode = mapper.readTree(file);
		assertTrue("JSON document mismatch with file",
				sourceNode.equals(readNode));
	}

	@Test
	public void testJsonPathPatch() throws IOException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);
		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder().pathLanguage(
				PathLanguage.JSONPATH);

		patchBldr
				.replaceValue("$.stringKey", Cardinality.ONE, "replaced value");

		patchBldr.replaceApply("$.numberKey", patchBldr.call().add(2));

		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode.put("replacedChildKey", "replaced object value");
		String fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.replaceFragment("$.objectKey.childObjectKey", fragment);

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("$.arrayKey", Position.BEFORE, fragment);

		// patchBldr.delete("$.arrayKey.[*][?(@.string=\"3\")]");

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("appendedKey", "appended item");
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("$.arrayKey", Position.LAST_CHILD,
				Cardinality.ZERO_OR_ONE, fragment);

		DocumentPatchHandle patchHandle = patchBldr.pathLanguage(
				PathLanguage.JSONPATH).build();

		logger.debug("Patch:" + patchHandle.toString());
		docMgr.patch(docId, patchHandle);

		ObjectNode expectedNode = mapper.createObjectNode();
		expectedNode.put("stringKey", "replaced value");
		expectedNode.put("numberKey", 9);
		ObjectNode childNode = mapper.createObjectNode();
		childNode.put("replacedChildKey", "replaced object value");
		expectedNode.put("objectKey", childNode);
		expectedNode.put("insertedKey", 9);
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("item value");
		childArray.add(3);
		childNode = mapper.createObjectNode();
		childNode.put("itemObjectKey", "item object value");
		childArray.add(childNode);
		childNode = mapper.createObjectNode();
		childNode.put("appendedKey", "appended item");
		childArray.add(childNode);
		expectedNode.put("arrayKey", childArray);

		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for patched JSON content", docText);
		
		logger.debug("Before:" + content);
		logger.debug("After:"+docText);
		logger.debug("Expected:" + mapper.writeValueAsString(expectedNode));
		
		JsonNode readNode = mapper.readTree(docText);
		assertTrue("Patched JSON document without expected result",
				expectedNode.equals(readNode));

	}
	
	@Test
	public void testJSONPathMetadata() throws IOException, XpathException, SAXException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		
		// add metadata patch here. This will be failing now.
		docMgr.write(docId,
				new BytesHandle(content.getBytes(Charset.forName("UTF-8")))
						.withFormat(Format.JSON));

		docMgr.setMetadataCategories(Metadata.ALL);

		docMgr.writeMetadata(docId, new StringHandle().with(metadata).withFormat(Format.JSON));

		DocumentPatchBuilder patchBldr =  docMgr.newPatchBuilder().pathLanguage(
				PathLanguage.JSONPATH);

		DocumentPatchHandle patchHandle = patchBldr
				.pathLanguage(PathLanguage.JSONPATH)
				.addCollection("collection3")
				.replaceCollection("collection4before",
						"collection4after")
				.replacePermission("app-user", Capability.UPDATE)
				.deleteProperty("first")
				.replacePropertyApply("second", patchBldr.call().add(3))
				.setQuality(4).build();

		docMgr.patch(docId, patchHandle);

		String metadata = docMgr.readMetadata(docId,
				new StringHandle().withFormat(Format.XML)).get();

		assertTrue("Could not read document metadata after write default",
				metadata != null);

		assertTrue("Could not read document metadata after write default", metadata != null);
		assertXpathEvaluatesTo("4","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection' and string(.)='collection4after'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission' and string(*[local-name()='role-name'])='app-user']/*[local-name()='capability'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",metadata);
		assertXpathEvaluatesTo("5","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='second'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='4'])",metadata);

		docMgr.delete(docId);
	}

	@Test
	public void testXPathPatch() throws IOException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();

		patchBldr.replaceValue("/stringKey", Cardinality.ONE, "replaced value");

		patchBldr.replaceApply("/numberKey", patchBldr.call().add(2));

		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode.put("replacedChildKey", "replaced object value");
		String fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.replaceFragment("/objectKey/childObjectKey", fragment);

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("/node()/node('arrayKey')", Position.BEFORE,
				fragment);

		//patchBldr.replaceApply("/node()/arrayKey/node()[string(.) eq '3']",
		//		patchBldr.call().add(2));

		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("appendedKey", "appended item");
		fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("/node()/node('arrayKey')",
				Position.LAST_CHILD, Cardinality.ZERO_OR_ONE, fragment);

		DocumentPatchHandle patchHandle = patchBldr.build();

		logger.debug("Sending patch " + patchBldr.build().toString());
		docMgr.patch(docId, patchHandle);

		ObjectNode expectedNode = mapper.createObjectNode();
		expectedNode.put("stringKey", "replaced value");
		expectedNode.put("numberKey",  9);
		ObjectNode childNode = mapper.createObjectNode();
		childNode.put("replacedChildKey", "replaced object value");
		expectedNode.put("objectKey", childNode);
		expectedNode.put("insertedKey", 9);
		ArrayNode childArray = mapper.createArrayNode();
		childArray.add("item value");
		childArray.add(3);
		childNode = mapper.createObjectNode();
		childNode.put("itemObjectKey", "item object value");
		childArray.add(childNode);
		childNode = mapper.createObjectNode();
		childNode.put("appendedKey", "appended item");
		childArray.add(childNode);
		expectedNode.put("arrayKey", childArray);

		String docText = docMgr.read(docId, new StringHandle()).get();
		
		assertNotNull("Read null string for patched JSON content", docText);
		JsonNode readNode = mapper.readTree(docText);
		

		logger.debug("Before:" + content);
		logger.debug("After:"+docText);
		logger.debug("Expected:" + mapper.writeValueAsString(expectedNode));
		
		
		assertTrue("Patched JSON document without expected result",
				expectedNode.equals(readNode));

		docMgr.delete(docId);
	}
	
	@Test
	public void testXPathJsonMetadata() throws IOException, XpathException, SAXException {
		String docId = "/test/testWrite1.json";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode sourceNode = makeContent(mapper);
		String content = mapper.writeValueAsString(sourceNode);

		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
		
		// add metadata patch here. This will be failing now.
		docMgr.write(docId,
				new BytesHandle(content.getBytes(Charset.forName("UTF-8")))
						.withFormat(Format.JSON));

		docMgr.setMetadataCategories(Metadata.ALL);

		docMgr.writeMetadata(docId, new StringHandle().with(metadata).withFormat(Format.JSON));

		DocumentPatchBuilder patchBldr =  docMgr.newPatchBuilder().pathLanguage(
				PathLanguage.JSONPATH);

		DocumentPatchHandle patchHandle = patchBldr
				.pathLanguage(PathLanguage.XPATH)
				.addCollection("collection3")
				.replaceCollection("collection4before",
						"collection4after")
				.replacePermission("app-user", Capability.UPDATE)
				.deleteProperty("first")
				.replacePropertyApply("second", patchBldr.call().add(3))
				.setQuality(4).build();

		logger.debug("Patch: "+ patchHandle.toString());
		logger.debug("Before: "+ content);
		docMgr.patch(docId, patchHandle);

		String metadata = docMgr.readMetadata(docId,
				new StringHandle().withFormat(Format.XML)).get();
		String jsonMetadata = docMgr.readMetadata(docId,
				new StringHandle().withFormat(Format.JSON)).get();

		logger.debug("After: "+ jsonMetadata);

		
		assertTrue("Could not read document metadata after write default",
				metadata != null);

		assertTrue("Could not read document metadata after write default", metadata != null);
		assertXpathEvaluatesTo("4","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection' and string(.)='collection4after'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission' and string(*[local-name()='role-name'])='app-user']/*[local-name()='capability'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",metadata);
		assertXpathEvaluatesTo("5","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='second'])",metadata);
		assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='4'])",metadata);

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
