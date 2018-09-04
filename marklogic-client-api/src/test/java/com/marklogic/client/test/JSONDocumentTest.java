/*
 * Copyright 2012-2018 MarkLogic Corporation
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

import com.marklogic.client.admin.ExtensionLibrariesManager;
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

  private static ExtensionLibrariesManager libsMgr = null;
  final static String metadata =
    "  {\"collections\":\n" +
    "    [\"collection1\",\n" +
    "    \"collection2\",\n" +
    "    \"collection4before\"],\n" +
    "   \"permissions\":[\n" +
    "     {\"role-name\":\"app-user\",\n" +
    "      \"capabilities\":[\"update\",\"read\"]}],\n" +
    "   \"properties\":[\n" +
    "    {\"first\":\"value one\",\n" +
    "     \"second\":2," +
    "     \"third\":3}],\n" +
    "  \"quality\":3, \n" +
    "  \"metadataValues\": {\"key1\" : \"value1\", \"key2\" : \"value2\" , \"number1\" : \"10\"}}\n";

  static final private Logger logger = LoggerFactory
    .getLogger(JSONDocumentTest.class);

  @BeforeClass
  public static void beforeClass() {
    Common.connectAdmin();
    // get a manager
    libsMgr = Common.adminClient
      .newServerConfigManager().newExtensionLibrariesManager();

    // write XQuery file to the modules database
    libsMgr.write("/ext/my-lib.xqy", new FileHandle(
      new File("src/test/resources/my-lib.xqy")).withFormat(Format.TEXT));

    Common.connect();
  }

  @AfterClass
  public static void afterClass() {
    libsMgr.delete("/ext/my-lib.xqy");
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
    sourceNode.put("numberKey3" , 31);
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
    patchBldr.insertFragment("$.[\"arrayKey\"]", Position.BEFORE, fragment);

    //patchBldr.delete("$.arrayKey.[*][?(@.string=\"3\")]");

    fragmentNode = mapper.createObjectNode();
    fragmentNode.put("appendedKey", "appended item");
    fragment = mapper.writeValueAsString(fragmentNode);
    patchBldr.insertFragment("$.[\"arrayKey\"]", Position.LAST_CHILD,
      Cardinality.ZERO_OR_ONE, fragment);
    patchBldr.replaceValue("$.booleanKey", true);
    patchBldr.replaceValue("$.numberKey2", 2);
    patchBldr.replaceValue("$.nullKey", null);
    patchBldr.library("http://marklogic.com/java-unit-test/my-lib",
      "/ext/my-lib.xqy");
    patchBldr.replaceApply("$.numberKey3",
      patchBldr.call().applyLibraryValues("getMin", 18, 21));

    DocumentPatchHandle patchHandle = patchBldr.pathLanguage(
      PathLanguage.JSONPATH).build();

    logger.debug("Patch1:" + patchHandle.toString());
    docMgr.patch(docId, patchHandle);

    ObjectNode expectedNode = mapper.createObjectNode();
    expectedNode.put("stringKey", "replaced value");
    expectedNode.put("numberKey", 9);
    ObjectNode replacedChildNode = mapper.createObjectNode();
    replacedChildNode.put("replacedChildKey", "replaced object value");
    ObjectNode childNode = mapper.createObjectNode();
    childNode.set("childObjectKey", replacedChildNode);
    expectedNode.set("objectKey", childNode);
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
    expectedNode.set("arrayKey", childArray);
    expectedNode.put("booleanKey", true);
    expectedNode.put("numberKey2", 2);
    expectedNode.putNull("nullKey");
    expectedNode.put("numberKey3" , 18);

    String docText = docMgr.read(docId, new StringHandle()).get();
    assertNotNull("Read null string for patched JSON content", docText);

    logger.debug("Before1:" + content);
    logger.debug("After1:"+docText);
    logger.debug("Expected1:" + mapper.writeValueAsString(expectedNode));

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

    logger.debug("Before2: " + content);
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
      .deleteCollection("collection1")
      .deleteProperty("first")
      .replacePropertyApply("second", patchBldr.call().add(3))
      .setQuality(4)
      .addMetadataValue("key3", "value3")
      .deleteMetadataValue("key2")
      .replaceMetadataValueApply("number1", patchBldr.call().add(5))
      .replaceMetadataValue("key1", "modifiedValue1")
      .build();

    docMgr.patch(docId, patchHandle);

    String metadata = docMgr.readMetadata(docId,
      new StringHandle().withFormat(Format.XML)).get();

    logger.debug("After2:" + metadata);
    assertTrue("Could not read document metadata after write default",
      metadata != null);

    assertTrue("Could not read document metadata after write default", metadata != null);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection' and string(.)='collection4after'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission' and string(*[local-name()='role-name'])='app-user']/*[local-name()='capability'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",metadata);
    assertXpathEvaluatesTo("5","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='second'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='4'])",metadata);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",metadata);
    assertXpathEvaluatesTo("value3", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key3\"]", metadata);
    assertXpathEvaluatesTo("modifiedValue1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", metadata);
    assertXpathEvaluatesTo("0", "count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key2\"])", metadata);
    assertXpathEvaluatesTo("15", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"number1\"]", metadata);

    docMgr.delete(docId);
  }

  @Test
  public void testXPathPatch() throws IOException {
    String docId = "/test/testWrite1.json";
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode sourceNode = makeContent(mapper);
    sourceNode.put("numberKey3", 31);
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
    patchBldr.insertFragment("/array-node('arrayKey')", Position.BEFORE,
      fragment);
    patchBldr.replaceValue("/booleanKey", true);
    patchBldr.replaceValue("/numberKey2", 2);
    patchBldr.replaceValue("/nullKey", null);
    patchBldr.library("http://marklogic.com/java-unit-test/my-lib",
      "/ext/my-lib.xqy");
    patchBldr.replaceApply("/numberKey3",
      patchBldr.call().applyLibraryValues("getMin", 18, 21));
    //patchBldr.replaceApply("/node()/arrayKey/node()[string(.) eq '3']",
    //		patchBldr.call().add(2));

    fragmentNode = mapper.createObjectNode();
    fragmentNode.put("appendedKey", "appended item");
    fragment = mapper.writeValueAsString(fragmentNode);
    patchBldr.insertFragment("/array-node('arrayKey')",
      Position.LAST_CHILD, Cardinality.ZERO_OR_ONE, fragment);

    DocumentPatchHandle patchHandle = patchBldr.build();

    logger.debug("Sending patch 3:" + patchBldr.build().toString());
    docMgr.patch(docId, patchHandle);

    ObjectNode expectedNode = mapper.createObjectNode();
    expectedNode.put("stringKey", "replaced value");
    expectedNode.put("numberKey",  9);
    ObjectNode replacedChildNode = mapper.createObjectNode();
    replacedChildNode.put("replacedChildKey", "replaced object value");
    ObjectNode childNode = mapper.createObjectNode();
    childNode.set("childObjectKey", replacedChildNode);
    expectedNode.set("objectKey", childNode);
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
    expectedNode.set("arrayKey", childArray);
    expectedNode.put("booleanKey", true);
    expectedNode.put("numberKey2", 2);
    expectedNode.putNull("nullKey");
    expectedNode.put("numberKey3", 18);

    String docText = docMgr.read(docId, new StringHandle()).get();

    assertNotNull("Read null string for patched JSON content", docText);
    JsonNode readNode = mapper.readTree(docText);

    logger.debug("Before3:" + content);
    logger.debug("After3:"+docText);
    logger.debug("Expected3:" + mapper.writeValueAsString(expectedNode));

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
      .replacePropertyValue("third", 17)
      .replacePropertyApply("second", patchBldr.call().add(3))
      .setQuality(4)
      .addMetadataValue("key3", "value3")
      .deleteMetadataValue("key2")
      .replaceMetadataValueApply("number1", patchBldr.call().add(5))
      .replaceMetadataValue("key1", "modifiedValue1")
      .build();

    String jsonMetadata = docMgr.readMetadata(docId,
      new StringHandle().withFormat(Format.JSON)).get();
    logger.debug("Before4: "+ jsonMetadata);
    logger.debug("Patch4: "+ patchHandle.toString());
    docMgr.patch(docId, patchHandle);

    String metadata = docMgr.readMetadata(docId,
      new StringHandle().withFormat(Format.XML)).get();
    jsonMetadata = docMgr.readMetadata(docId,
      new StringHandle().withFormat(Format.JSON)).get();

    logger.debug("After4: "+ jsonMetadata);


    assertTrue("Could not read document metadata after write default",
      metadata != null);

    assertTrue("Could not read document metadata after write default", metadata != null);
    assertXpathEvaluatesTo("4","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection' and string(.)='collection4after'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission' and string(*[local-name()='role-name'])='app-user']/*[local-name()='capability'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",metadata);
    assertXpathEvaluatesTo("5","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='second'])",metadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='4'])",metadata);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",metadata);
    assertXpathEvaluatesTo("value3", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key3\"]", metadata);
    assertXpathEvaluatesTo("modifiedValue1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", metadata);
    assertXpathEvaluatesTo("0", "count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key2\"])", metadata);
    assertXpathEvaluatesTo("15", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"number1\"]", metadata);
    //TODO: uncomment next line once we fix https://bugtrack.marklogic.com/29865
    //assertXpathEvaluatesTo("17","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='third'])",metadata);

    docMgr.delete(docId);
  }

  private ObjectNode makeContent(ObjectMapper mapper) throws IOException {
    ObjectNode sourceNode = mapper.createObjectNode();
    sourceNode.put("stringKey", "string value");
    sourceNode.put("numberKey", 7);
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("childObjectKey", "child object value");
    sourceNode.set("objectKey", childNode);
    ArrayNode childArray = mapper.createArrayNode();
    childArray.add("item value");
    childArray.add(3);
    childNode = mapper.createObjectNode();
    childNode.put("itemObjectKey", "item object value");
    childArray.add(childNode);
    sourceNode.set("arrayKey", childArray);
    sourceNode.put("booleanKey", false);
    sourceNode.put("numberKey2", 1);
    sourceNode.put("nullKey", 0);

    return sourceNode;
  }
}
