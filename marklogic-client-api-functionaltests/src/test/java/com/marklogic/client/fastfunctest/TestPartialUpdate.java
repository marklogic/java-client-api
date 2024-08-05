/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.*;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;




public class TestPartialUpdate extends AbstractFunctionalTest {

  private static DatabaseClient client;

  @BeforeAll
  public static void setUp() throws Exception {
    System.out.println("In setup");
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createUserRolesWithPrevilages("replaceRoleTest", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "replaceRoleTest", "rest-admin", "rest-writer", "rest-reader");
	  client = newClientAsUser("eval-user", "x");
  }

  @Test
  public void testPartialUpdateXML() throws IOException {
    System.out.println("Running testPartialUpdateXML");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    }

    String docId = "/partial-update/constraint1.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();

    patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<modified>2013-03-21</modified></root>"));

    // Check boolean replaces with XML documents.
    String xmlDocId = "/replaceBoolXml";

    String xmlStr1 = new String("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    String xmlStr2 = new String("<resources><screen name=\"screen_small\">true</screen><screen name=\"adjust_view_bounds\">false</screen></resources>");
    StringBuffer xmlStrbuf = new StringBuffer().append(xmlStr1).append(xmlStr2);

    StringHandle contentHandle = new StringHandle();
    contentHandle.set(xmlStrbuf.toString());

    // write the document content
    docMgr.write(xmlDocId, contentHandle);
    waitForPropertyPropagate();

    // Read it back to make sure the write worked.
    String contentXml = docMgr.read(xmlDocId, new StringHandle()).get();

    assertTrue( contentXml.contains(xmlStr2));

    DocumentPatchBuilder patchBldrBool = docMgr.newPatchBuilder();
    patchBldrBool.pathLanguage(PathLanguage.XPATH);

    // Flip the boolean values for both screen types
    patchBldrBool.replaceValue("/resources/screen[@name=\"screen_small\"]", false);
    patchBldrBool.replaceValue("/resources/screen[@name=\"adjust_view_bounds\"]", new Boolean(true));

    DocumentPatchHandle patchHandleBool = patchBldrBool.build();
    docMgr.patch(xmlDocId, patchHandleBool);
    waitForPropertyPropagate();

    String content1 = docMgr.read(xmlDocId, new StringHandle()).get();
    System.out.println(content1);
    String xmlStr2Mod = new String("<resources><screen name=\"screen_small\">false</screen><screen name=\"adjust_view_bounds\">true</screen></resources>");

    assertTrue( content1.contains(xmlStr2Mod));
  }

  /*
   * Used to test Git issue # 94 along with uber-app server. use a bad user to
   * authenticate client. Should be throwing FailedRequestException Exception.
   * Message : Local message: write failed: Unauthorized. Server Message:
   * Unauthorized
   */
	@Test
  public void testJSONParserException()
  {
    System.out.println("Running testPartialUpdateJSON");

    String[] filenames = { "json-original.json" };

	DatabaseClient badClient = newClientAsUser("bad-eval-user", "x");

    // write docs
    for (String filename : filenames) {
      assertThrows(FailedRequestException.class, () -> writeDocumentUsingInputStreamHandle(badClient, filename, "/partial-update/", "JSON"));
    }
    badClient.release();
  }

  @Test
  public void testPartialUpdateJSON() throws IOException {
    System.out.println("Running testPartialUpdateJSON");

    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    ObjectMapper mapper = new ObjectMapper();

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    ObjectNode fragmentNode = mapper.createObjectNode();
    ObjectNode fragmentNode1 = mapper.createObjectNode();
    ObjectNode fragmentNode2 = mapper.createObjectNode();

    fragmentNode.put("insertedKey", 9);
    fragmentNode1.put("original", true);
    fragmentNode2.put("modified", false);

    String fragment = mapper.writeValueAsString(fragmentNode);
    String fragment1 = mapper.writeValueAsString(fragmentNode1);
    String fragment2 = mapper.writeValueAsString(fragmentNode2);

    String jsonpath = new String("$.employees[2]");
    patchBldr.insertFragment(jsonpath, Position.AFTER, fragment);
    patchBldr.insertFragment("$.employees[2]", Position.AFTER, fragment1);
    patchBldr.insertFragment("$.employees[0]", Position.AFTER, fragment2);

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("{\"insertedKey\":9}"));
    assertTrue( content.contains("{\"original\":true}"));
    assertTrue( content.contains("{\"modified\":false}"));

    // Test for replaceValue with booleans.
    DocumentPatchBuilder patchBldrBool = docMgr.newPatchBuilder();
    patchBldrBool.pathLanguage(PathLanguage.JSONPATH);

    // Replace original to false and modified to true.
    patchBldrBool.replaceValue("$.employees[5].original", false);
    patchBldrBool.replaceValue("$.employees[1].modified", new Boolean(true));

    DocumentPatchHandle patchHandleBool = patchBldrBool.build();
    docMgr.patch(docId, patchHandleBool);
    waitForPropertyPropagate();

    String content1 = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content1);
    // Make sure the inserted content is present.
    assertTrue( content1.contains("{\"original\":false}"));
    assertTrue( content1.contains("{\"modified\":true}"));
  }

  @Test
  public void testPartialUpdateContent() throws IOException
  {
    System.out.println("Running testPartialUpdateContent");

    String filename = "constraint1.xml";

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");

    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // //
    // Updating Content
    // //
    // Inserting Node
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(" Before Updating " + contentBefore);

    // Updating inserted Node
    DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
    DocumentPatchHandle xmlPatchForNode = xmlPatchBldr.replaceFragment("/root/modified", "<modified>2012-11-5</modified>").build();
    xmlDocMgr.patch(docId, xmlPatchForNode);
    waitForPropertyPropagate();
    String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Updating" + contentAfter);

    assertTrue( contentAfter.contains("<modified>2012-11-5</modified></root>"));

    // //
    // Updating Doc Element
    // //
    String contentBeforeElement = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBeforeElement);
    DocumentPatchHandle xmlPatchForElement = xmlPatchBldr.replaceValue("/root/popularity", 10).build();
    xmlDocMgr.patch(docId, xmlPatchForElement);
    waitForPropertyPropagate();
    contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Updating" + contentAfter);

    // Check
    assertTrue( contentAfter.contains("<popularity>10</popularity>"));

    // //
    // Updating Doc Attribute
    // //
    String contentBeforeAttribute = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBeforeAttribute);

    // Updating Attribute Value
    xmlPatchBldr.replaceValue("/root/*:price/@amt", 0.5);
    // xmlPatchBldr.replaceValue("/root/*:price/@xmlns","http://marklogic.com");
    DocumentPatchHandle xmlPatchForValue = xmlPatchBldr.build();
    xmlDocMgr.patch(docId, xmlPatchForValue);
    waitForPropertyPropagate();
    contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Updating" + contentAfter);
    // Check
    assertTrue( contentAfter.contains("<price amt=\"0.5\" xmlns=\"http://cloudbank.com\"/>"));

    // //
    // Updating Doc Namespace
    // //
    String contentBeforeNamespace = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBeforeNamespace);

    // Changing Element Value
    DocumentPatchHandle xmlPatch = xmlPatchBldr.replaceValue("/root/*:date", "2006-02-02").build();
    xmlDocMgr.patch(docId, xmlPatch);
    waitForPropertyPropagate();
    contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Updating" + contentAfter);
    // Check
    assertTrue( contentAfter.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2006-02-02</date>"));
  }

  @Test
  public void testPartialUpdateDeletePath() throws IOException
  {
    System.out.println("Running testPartialUpdateDeletePath");

    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();

    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);

    // Deleting Element Value
    DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
    // DocumentPatchHandle xmlPatch = xmlPatchBldr.replaceValue("/root/*:date",
    // "2006-02-02").build();
    DocumentPatchHandle xmlPatch = xmlPatchBldr.delete("/root/*:date").build();
    xmlDocMgr.patch(docId, xmlPatch);
    waitForPropertyPropagate();

    // Delete invalid Path
    try {
      xmlPatch = xmlPatchBldr.delete("InvalidPath").build();
      xmlDocMgr.patch(docId, xmlPatch);
      waitForPropertyPropagate();
    } catch (Exception e) {
      System.out.println(e.toString());
      assertTrue(e.toString().contains(" invalid path: //InvalidPath"));
    }
    String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Updating" + contentAfter);
    assertFalse( contentAfter.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
  }

  @Test
  public void testPartialUpdateFragments() throws Exception {
    System.out.println("Running testPartialUpdateFragments");

    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    waitForPropertyPropagate();
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);
    // Inserting Fragments with valid path
    DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/title", Position.BEFORE, "<start>Hi</start>\n  ");
    patchBldr.insertFragment("/root/id", Position.AFTER, "\n  <modified>2013-03-21</modified>");
    patchBldr.insertFragment("/root", Position.LAST_CHILD, "  <End>bye</End>\n");
    // Inserting Fragments with invalid path
    patchBldr.insertFragment("/root/someinvalidpath", Position.BEFORE, "<false>Entry</false>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    xmlDocMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<start>Hi</start>"));
    assertTrue( content.contains("<modified>2013-03-21</modified>"));
    assertTrue( content.contains("<End>bye</End>"));
    assertFalse( content.contains("<false>Entry</false>"));
  }

  @Test
  public void testPartialUpdateInsertFragments() throws Exception {
    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);
    // Replacing Fragments with valid path
    DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
    patchBldr.replaceFragment("/root/title", "<replaced>foo</replaced>");
    // Replacing Fragments with invalid path
    patchBldr.replaceFragment("/root/invalidpath", "<replaced>FalseEntry</replaced>");
    patchBldr.replaceInsertFragment("/root/nonexist", "/root", Position.LAST_CHILD, "  <foo>bar</foo>\n ");
    DocumentPatchHandle patchHandle = patchBldr.build();
    xmlDocMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<replaced>foo</replaced>"));
    assertFalse( content.contains("<replaced>FalseEntry</replaced>"));
    assertTrue( content.contains("<foo>bar</foo>"));
  }

  @Test
  public void testPartialUpdateInsertExistingFragments() throws Exception {
    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);
    // Replacing Fragments with valid path
    DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
    patchBldr.replaceInsertFragment("/root/title", "/root", Position.LAST_CHILD, "<foo>LastChild</foo>");
    patchBldr.replaceInsertFragment("/root/id", "/root", Position.BEFORE, "<foo>Before</foo>");
    patchBldr.replaceInsertFragment("/root/p", "/root", Position.AFTER, "<foo>After</foo>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    xmlDocMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue(content.contains("<foo>LastChild</foo>"));
    assertTrue( content.contains("<foo>Before</foo>"));
    assertTrue( content.contains("<foo>After</foo>"));
  }

  @Test
  public void testPartialUpdateReplaceApply() throws Exception {
    System.out.println("Running testPartialUpdateReplaceApply");
    DatabaseClient client = newClientAsUser("rest-admin", "x");
    ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();

    libsMgr.write("/ext/patch/custom-lib.xqy", new FileHandle(new File("src/test/java/com/marklogic/client/functionaltest/data/custom-lib.xqy")).withFormat(Format.TEXT));
    libsMgr.write("/ext/patch/qatests.sjs", new FileHandle(new File("src/test/java/com/marklogic/client/functionaltest/data/qatests.sjs")).withFormat(Format.TEXT));
    // write docs
    String filename = "constraint6.xml";
    String filename2 = "constraint6.json";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/partial-update/", "JSON");

    String docId = "/partial-update/constraint6.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);
    // Executing different operations on XML
    DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
    patchBldr.replaceApply("/root/add", patchBldr.call().add(10));
    patchBldr.replaceApply("/root/subtract", patchBldr.call().subtract(2));
    patchBldr.replaceApply("/root/multiply", patchBldr.call().multiply(2));
    patchBldr.replaceApply("/root/divide", patchBldr.call().divideBy(2));
    patchBldr.replaceApply("/root/concatenateAfter", patchBldr.call().concatenateAfter(" ML7"));
    patchBldr.replaceApply("/root/concatenateBetween", patchBldr.call().concatenateBetween("ML ", " 7"));
    patchBldr.replaceApply("/root/concatenateBefore", patchBldr.call().concatenateBefore("ML "));
    patchBldr.replaceApply("/root/substringAfter", patchBldr.call().substringAfter("Version"));
    patchBldr.replaceApply("/root/substringBefore", patchBldr.call().substringBefore("Version"));
    patchBldr.replaceApply("/root/replaceRegex", patchBldr.call().replaceRegex("[a-m]", "1"));
    patchBldr.replaceApply("/root/applyLibrary", patchBldr.call().applyLibraryFragments("underwrite", "<applyLibrary>API</applyLibrary>")).library(
        "http://marklogic.com/ext/patch/custom-lib", "/ext/patch/custom-lib.xqy");
    DocumentPatchHandle patchHandle = patchBldr.build();
    xmlDocMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("After Update" + content);
    // Check
    assertTrue( content.contains("<add>15</add>"));
    assertTrue( content.contains("<subtract>3</subtract>"));
    assertTrue( content.contains("<multiply>4</multiply>"));
    assertTrue( content.contains("<divide>10</divide>"));
    assertTrue( content.contains("<concatenateAfter>Hi ML7</concatenateAfter>"));
    assertTrue( content.contains("<concatenateBefore>ML 7</concatenateBefore>"));
    assertTrue( content.contains(" <substringAfter> 7</substringAfter>"));
    assertTrue( content.contains("<substringBefore>ML </substringBefore>"));
    assertTrue( content.contains("<concatenateBetween>ML Version 7</concatenateBetween>"));
    assertTrue( content.contains("<replaceRegex>C111nt</replaceRegex>"));
    assertTrue( content.contains("<applyLibrary>APIAPI</applyLibrary>"));

    String docId2 = "/partial-update/constraint6.json";

    JSONDocumentManager jdm = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldrSJS = jdm.newPatchBuilder();
    patchBldrSJS.pathLanguage(PathLanguage.JSONPATH);

    patchBldrSJS.library("", "/ext/patch/qatests.sjs");
    patchBldrSJS.replaceApply("root.divide",
    		patchBldrSJS.call().applyLibraryValues("Mymin", 18, 21));
    DocumentPatchHandle patchHandleSJS = patchBldrSJS.build();
    jdm.patch(docId2, patchHandleSJS);
    System.out.println(patchBldrSJS.build().toString());

    waitForPropertyPropagate();
    String content1 = xmlDocMgr.read(docId2, new StringHandle()).get();
    System.out.println("After Update on divide with fn() values " + content1);
    assertTrue( content1.contains("\"divide\":18"));

    // Work on the different element with different values and another patch update
    DocumentPatchBuilder patchBldrSJS1 = jdm.newPatchBuilder();
    patchBldrSJS1.pathLanguage(PathLanguage.JSONPATH);

    patchBldrSJS1.library("", "/ext/patch/qatests.sjs");
    patchBldrSJS1.replaceApply("root.add",
    		patchBldrSJS1.call().applyLibraryValues("Mymin", -12, 21));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode fragmentNode = mapper.createObjectNode();
    fragmentNode.put("modulo", 2);
    String fragment = mapper.writeValueAsString(fragmentNode);
    patchBldrSJS1.insertFragment("root.divide", Position.AFTER, fragment);

    DocumentPatchHandle patchHandleSJS1 = patchBldrSJS1.build();
    jdm.patch(docId2, patchHandleSJS1);
    System.out.println(patchBldrSJS1.build().toString());

    waitForPropertyPropagate();
    String content2 = xmlDocMgr.read(docId2, new StringHandle()).get();
    System.out.println("After Update on add with fn() values " + content2);
    assertTrue( content2.contains("\"add\":-12"));
    assertTrue( content2.contains("\"modulo\":2"));

    // Error condition checks
    DocumentPatchBuilder patchBldrSJSErr = jdm.newPatchBuilder();
    patchBldrSJSErr.pathLanguage(PathLanguage.JSONPATH);

    patchBldrSJSErr.library("", "/ext/patch/qatests.sjs");
    patchBldrSJSErr.replaceApply("root.add",
    		patchBldrSJSErr.call().applyLibraryValues("Mymin", new String("A"),  new String("A")));
    StringBuilder strErr = new StringBuilder();
    try {
    DocumentPatchHandle patchHandleSJSErr = patchBldrSJSErr.build();
    jdm.patch(docId2, patchHandleSJSErr);
    }
    catch (Exception ex) {
    	System.out.println(ex.getMessage());
    	strErr.append(ex.getMessage());
    }
    System.out.println(patchBldrSJSErr.build().toString());

    waitForPropertyPropagate();
    String content3 = xmlDocMgr.read(docId2, new StringHandle()).get();
    System.out.println("After Update on divide with fn() values " + content3);
    assertTrue( strErr.toString().isEmpty());

    libsMgr.delete("/ext/patch/custom-lib.xqy");
    libsMgr.delete("/ext/patch/qatests.sjs");
  }

  @Test
  public void testPartialUpdateCombination() throws Exception {
    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);

    DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
    DocumentPatchHandle xmlPatch = xmlPatchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").delete("/root/*:date")
        .replaceApply("/root/popularity", xmlPatchBldr.call().multiply(2)).build();
    xmlDocMgr.patch(docId, xmlPatch);
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(" After Updating " + content);
    // Check
    assertTrue( content.contains("<popularity>10</popularity>"));
    assertFalse( content.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
    assertTrue( content.contains("<modified>2012-11-5</modified>"));
  }

  @Test
  public void testPartialUpdateCombinationTransc() throws Exception {
    Transaction t = client.openTransaction("Transac");
    // write docs
    String filename = "constraint1.xml";
    // writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/",
    // "XML");
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    // t.commit();
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentBefore);
    // Transaction t1 = client.openTransaction();
    DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
    DocumentPatchHandle xmlPatch = xmlPatchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").delete("/root/*:date")
        .replaceApply("/root/popularity", xmlPatchBldr.call().multiply(2)).build();
    xmlDocMgr.patch(docId, xmlPatch, t);
    t.commit();
    waitForPropertyPropagate();
    String content = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(" After Updating " + content);

    // Check
    assertTrue( content.contains("<popularity>10</popularity>"));
    assertFalse( content.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
    assertTrue( content.contains("<modified>2012-11-5</modified>"));
  }

  @Test
  public void testPartialUpdateCombinationTranscRevert() throws Exception {
    // write docs
    String[] filenames = { "constraint1.xml", "constraint2.xml" };
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    }
    String docId1 = "/partial-update/constraint1.xml";
    String docId2 = "/partial-update/constraint2.xml";
    // Creating Manager
    XMLDocumentManager xmlDocMgr1 = client.newXMLDocumentManager();
    XMLDocumentManager xmlDocMgr2 = client.newXMLDocumentManager();
    String contentBefore1 = xmlDocMgr1.read(docId1, new StringHandle()).get();
    String contentBefore2 = xmlDocMgr2.read(docId2, new StringHandle()).get();
    System.out.println(" Before Updating Document 1 " + contentBefore1);
    System.out.println(" Before Updating Document 2 " + contentBefore2);

    DocumentPatchBuilder xmlPatchBldr1 = xmlDocMgr1.newPatchBuilder();
    DocumentPatchBuilder xmlPatchBldr2 = xmlDocMgr2.newPatchBuilder();

    DocumentPatchHandle xmlPatch1 = xmlPatchBldr1.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").build();
    DocumentPatchHandle xmlPatch2 = xmlPatchBldr2.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").build();

    Transaction t1 = client.openTransaction();
    xmlDocMgr1.patch(docId1, xmlPatch1, t1);
    t1.commit();
    waitForPropertyPropagate();
    String content1 = xmlDocMgr1.read(docId1, new StringHandle()).get();
    System.out.println(" After Updating Documant 1 : Transaction Commit" + content1);
    Transaction t2 = client.openTransaction();
    xmlDocMgr1.patch(docId2, xmlPatch2, t2);
    t2.rollback();
    waitForPropertyPropagate();

    String content2 = xmlDocMgr2.read(docId2, new StringHandle()).get();
    System.out.println(" After Updating Document 2 : Transaction Rollback" + content2);
  }

  /*
   * We have Git issue #199 that tracks multiple patch on same JSONPath index.
   * This test uses different path index. This test was modified to account for
   * the correct path index elements.
   */
  @Test
  public void testPartialUpdateCombinationJSON() throws Exception {
    System.out.println("Running testPartialUpdateCombinationJSON");
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    String[] filenames = { "json-original.json" };
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }
    String docId = "/partial-update/json-original.json";

    ObjectMapper mapper = new ObjectMapper();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);
    String content1 = docMgr.read(docId, new StringHandle()).get();

    System.out.println("Before" + content1);
    ObjectNode fragmentNode = mapper.createObjectNode();
    fragmentNode = mapper.createObjectNode();
    fragmentNode.put("insertedKey", 9);
    String fragment = mapper.writeValueAsString(fragmentNode);
    // Original - patchBldr.insertFragment("$.employees", Position.LAST_CHILD,
    // fragment).delete("$.employees[2]").replaceApply("$.employees[1].firstName",
    // patchBldr.call().concatenateAfter("Hi"));
    patchBldr.insertFragment("$.employees[0]", Position.AFTER, fragment).delete("$.employees[2]").replaceApply("$.employees[1].firstName", patchBldr.call().concatenateAfter("Hi"));
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println("After" + content);

    assertTrue( content.contains("{\"insertedKey\":9}"));
    assertTrue( content.contains("{\"firstName\":\"AnnHi\", \"lastName\":\"Smith\"}"));
    assertFalse( content.contains("{\"firstName\":\"Bob\", \"lastName\":\"Foo\"}"));
  }

  @Test
  public void testPartialUpdateMetadata() throws Exception {
    // write docs
    String filename = "constraint1.xml";
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    String contentMetadata = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" Before Updating " + contentMetadata);

    DocumentMetadataPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldr.addCollection("/document/collection3");
    patchBldr.addPermission("replaceRoleTest", Capability.READ);
    patchBldr.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandle = patchBldr.build();
    xmlDocMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String contentMetadata1 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadata1);

    // Check
    assertTrue( contentMetadata1.contains("<rapi:collection>/document/collection3</rapi:collection>"));
    assertTrue( contentMetadata1.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue( contentMetadata1.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));

    // //
    // replacing Metadata Values
    // //
    DocumentMetadataPatchBuilder patchBldrRep = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrRep.replaceCollection("/document/collection3", "/document/collection4");
    patchBldrRep.replacePermission("admin", Capability.UPDATE);
    patchBldrRep.replacePropertyValue("Hello", "Bye");
    DocumentPatchHandle patchHandleRep = patchBldrRep.build();
    xmlDocMgr.patch(docId, patchHandleRep);
    waitForPropertyPropagate();
    String contentMetadataRep = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Updating " + contentMetadataRep);

    // Check
    assertTrue( contentMetadataRep.contains("<rapi:collection>/document/collection4</rapi:collection>"));
    assertTrue( contentMetadata1.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue( contentMetadataRep.contains("<Hello xsi:type=\"xs:string\">Bye</Hello>"));

    // //
    // Deleting Metadata Values
    // //
    DocumentMetadataPatchBuilder patchBldrDel = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrDel.deleteCollection("/document/collection4");
    patchBldrDel.deletePermission("replaceRoleTest");
    patchBldrDel.deleteProperty("Hello");
    DocumentPatchHandle patchHandleDel = patchBldrDel.build();
    xmlDocMgr.patch(docId, patchHandleDel);
    waitForPropertyPropagate();
    String contentMetadataDel = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Deleting " + contentMetadataDel);

    // Check
    assertFalse( contentMetadataDel.contains("<rapi:collection>/document/collection4</rapi:collection>"));
    assertFalse( contentMetadataDel.contains("<rapi:role-name>admin</rapi:role-name>"));
    assertFalse( contentMetadataDel.contains("<Hello xsi:type=\"xs:string\">Bye</Hello>"));
  }

  @Test
  public void testPartialUpdateXMLDscriptor() throws IOException {
    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    }

    String docId = "/partial-update/constraint1.xml";
    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // Create Document Descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
    DocumentPatchHandle patchHandle = patchBldr.build();

    docMgr.patch(desc, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println("After" + content);

    assertTrue( content.contains("<modified>2013-03-21</modified></root>"));
  }

  @Test
  public void testPartialUpdateJSONDescriptor() throws IOException {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    ObjectMapper mapper = new ObjectMapper();
    String docId = "/partial-update/json-original.json";
    // create doc manager
    JSONDocumentManager docMgr = client.newJSONDocumentManager();

    // Create Document Descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);
    ObjectNode fragmentNode = mapper.createObjectNode();
    fragmentNode = mapper.createObjectNode();
    fragmentNode.put("insertedKey", 9);
    String fragment = mapper.writeValueAsString(fragmentNode);

    patchBldr.insertFragment("$.employees[2]", Position.AFTER, fragment);
    DocumentPatchHandle patchHandle = patchBldr.build();

    docMgr.patch(desc, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println("After" + content);

    assertTrue( content.contains("{\"insertedKey\":9}]"));
  }

  @Test
  public void testPartialUpdateXMLDscriptorTranc() throws IOException {
    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
    }

    String docId = "/partial-update/constraint1.xml";
    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // create template
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
    template.withDirectory(docId);

    DocumentDescriptor desc = docMgr.newDescriptor(template.getDirectory());
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    Transaction t = client.openTransaction("Tranc");
    docMgr.patch(desc, patchHandle, t);
    t.commit();
    waitForPropertyPropagate();
    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println("After" + content);

    assertTrue( content.contains("<modified>2013-03-21</modified></root>"));
  }

  @Test
  public void testPartialUpdateJSONDescriptorTranc() throws IOException {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }
    ObjectMapper mapper = new ObjectMapper();
    String docId = "/partial-update/json-original.json";
    // create doc manager
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    // create template
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("JSON");
    template.withDirectory(docId);
    // Create Document Descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(template.getDirectory());
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();

    ObjectNode fragmentNode = mapper.createObjectNode();
    fragmentNode = mapper.createObjectNode();
    fragmentNode.put("insertedKey", 9);
    String fragment = mapper.writeValueAsString(fragmentNode);
    patchBldr.pathLanguage(PathLanguage.JSONPATH);
    patchBldr.insertFragment("$.employees[2]", Position.AFTER, fragment);
    DocumentPatchHandle patchHandle = patchBldr.build();
    // Transaction t = client.openTransaction("Tranc");
    docMgr.patch(desc, patchHandle);// ,t);
    // t.commit();
    waitForPropertyPropagate();
    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println("After" + content);

    assertTrue( content.contains("{\"insertedKey\":9}]"));
  }

  @Test
  public void testPartialUpdateCardinality() throws IOException {
    String filename = "constraint1.xml";

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");

    String docId = "/partial-update/constraint1.xml";

    // Creating Manager
    XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // Inserting Node
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root", Position.LAST_CHILD, Cardinality.ONE, "<modified>2013-03-21</modified>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();
    String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println(" Content after Updating with Cardinality.ONE : " + contentBefore);
    assertTrue( contentBefore.contains("</modified></root>"));
    // Updating again
    DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
    DocumentPatchHandle xmlPatchForNode = xmlPatchBldr.insertFragment("/root/id", Position.BEFORE, Cardinality.ONE_OR_MORE, "<modified>1989-04-06</modified>").build();
    xmlDocMgr.patch(docId, xmlPatchForNode);
    waitForPropertyPropagate();
    String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("Content after Updating with Cardinality.ONE_OR_MORE" + contentAfter);
    assertTrue( contentAfter.contains("1989-04-06"));
    // Updating again
    DocumentPatchBuilder xmlPatchBldr1 = xmlDocMgr.newPatchBuilder();
    DocumentPatchHandle xmlPatchForNode1 = xmlPatchBldr1.insertFragment("/root/id", Position.AFTER, Cardinality.ZERO_OR_ONE, "<modified>2013-07-29</modified>").build();
    xmlDocMgr.patch(docId, xmlPatchForNode1);
    waitForPropertyPropagate();
    contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();

    System.out.println("Content after Updating with Cardinality.ZERO_OR_ONE" + contentAfter);
    assertTrue( contentAfter.contains("</id><modified>2013-07-29"));
  }

  /*
   * Purpose: This test is used to validate all of the patch builder functions
   * on a JSON document using JSONPath expressions.
   *
   * Function tested: replaceValue.
   */
  @Test
  public void testPartialUpdateReplaceValueJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    // Replace the third employee's first name. Change it to Jack. Issue #161 -
    // Using filters causes Bad Request Exceptions.
    patchBldr.replaceValue("$.employees[2].firstName", "Jack");

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"firstName\":\"Jack\", \"lastName\":\"Foo\"}]}";
    JSONAssert.assertEquals(exp, content, false);
  }

  /*
   * Purpose: This test is used to validate all of the patch builder functions
   * on a JSON document using JSONPath expressions.
   *
   * Functions tested : replaceFragment.
   */
  @Test
  public void testPartialUpdateReplaceFragmentJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    // Replace the third employee. Issue #161 - Using filters causes Bad Request
    // Exceptions.
    patchBldr.replaceFragment("$.employees[2]", "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}");

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}]}";
    JSONAssert.assertEquals(exp, content, false);
  }

  /*
   * Purpose: This test is used to validate all of the patch builder functions
   * on a JSON document using JSONPath expressions.
   *
   * Functions tested : replaceInsertFragment. An new fragment is inserted when
   * unknown index is used.
   */
  @Test
  public void testPartialUpdateReplaceInsertFragmentNewJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    // Mark an unknown location in argument 1, and then insert new node relative
    // to argument 2.
    patchBldr.replaceInsertFragment("$.employees[3]", "$.employees[0]", Position.BEFORE, "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}");

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}," +
        "{\"firstName\":\"Bob\", \"lastName\":\"Foo\"}]}";
    JSONAssert.assertEquals(exp, content, false);
  }

  /*
   * Purpose: This test is used to validate all of the patch builder functions
   * on a JSON document using JSONPath expressions.
   *
   * Functions tested : replaceInsertFragment. An existing fragment replaced
   * with another fragment.
   */
  @Test
  public void testPartialUpdateReplaceInsertFragmentExistingJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    // Replace the third employee. Issue #161 - Using filters causes Bad Request
    // Exceptions.
    patchBldr.replaceInsertFragment("$.employees[2]", "$.employees[2]", Position.LAST_CHILD, "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}");

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"firstName\":\"Albert\", \"lastName\":\"Einstein\"}]}";
    JSONAssert.assertEquals(exp, content, false);
  }

  /*
   * Purpose: This test is used to validate all of the patch builder functions
   * on a JSON document using JSONPath expressions.
   *
   * Function tested: delete.
   */
  @Test
  public void testPartialUpdateDeleteJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    // Delete the third employee's first name. Issue #161 - Using filters causes
    // Bad Request Exceptions.
    patchBldr.delete("$.employees[2].firstName", DocumentMetadataPatchBuilder.Cardinality.ZERO_OR_MORE);

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"lastName\":\"Foo\"}]}";
    JSONAssert.assertEquals(exp, content, false);
  }

  // Sanity test to make sure that restricted Xpath predicate functions can be used to patch documents.
  @Test
  public void testRestrictedXPath() {
      final String DIRECTORY = "/RXath/";
      final int BATCH_SIZE = 10;
      StringBuilder content1 = new StringBuilder();
      content1.append("{\"World\":[{\"CountyId\": \"0001\",");
      content1.append("\"Govt\": \"Presidential\",");
      content1.append("\"name\": \"USA\",");
      content1.append("\"Pop\": 328,");
      content1.append("\"Regions\":{\"Contiental\":[");
      content1.append("{ \"RegionId\": \"1001\", \"Direction\": \"NE\" },");
      content1.append("{ \"RegionId\": \"1002\", \"Direction\": \"SE\" },");
      content1.append("{ \"RegionId\": \"1003\", \"Direction\": \"NW\" },");
      content1.append("{ \"RegionId\": \"1004\", \"Direction\": \"SW\" }");
      content1.append("]}}]}");

      int count = 1;
      XMLDocumentManager docMgr = client.newXMLDocumentManager();
      // Write docs
      String docStr = content1.toString();
      DocumentWriteSet writeset1 = docMgr.newWriteSet();
      for (int i = 0; i < 11; i++) {
          writeset1.add(DIRECTORY + "World-01-" + i + ".json", new StringHandle(docStr));

          if (count % BATCH_SIZE == 0) {
              docMgr.write(writeset1);
              writeset1 = docMgr.newWriteSet();
          }
          count++;
      }
      if (count % BATCH_SIZE > 0) {
          docMgr.write(writeset1);
      }
      QueryManager queryMgr = client.newQueryManager();

      String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
      String tail = "</search:search>";
      // object-node - Number Node
      String qtext1 = "<search:qtext>1001</search:qtext>";
      String options1 ="<search:options>" +
                      "<search:extract-document-data selected=\"include\">" +
                      "<search:extract-path>/World//number-node()</search:extract-path>" +
                      "</search:extract-document-data>" +
                      "</search:options>";

      String combinedSearch = head + qtext1 + options1 + tail;
      RawCombinedQueryDefinition rawCombinedQueryDefinition =
              queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

      // create handle
      SearchHandle resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
      MatchDocumentSummary[] summaries = resSearchHandle.getMatchResults();
      for (MatchDocumentSummary summary : summaries) {
          ExtractedResult extracted = summary.getExtracted();
          if ( Format.JSON == summary.getFormat() ) {
              for (ExtractedItem item : extracted) {
                  String extractItem = item.getAs(String.class);
                  System.out.println("Extracted item from Number node element search " + extractItem);
                  assertTrue( extractItem.matches("\\{\"Pop\":328\\}"));
              }
          }
      }

      String docId = "/RXath/World-01-2.json";
      JSONDocumentManager JdocMgr = client.newJSONDocumentManager();
      DocumentPatchBuilder patchBldr = JdocMgr.newPatchBuilder();

      // Replace 328 in the population to be 500.
      patchBldr.pathLanguage(PathLanguage.XPATH);
      patchBldr.replaceValue("/World//number-node()", 500);

      DocumentPatchHandle patchHandle = patchBldr.build();
      docMgr.patch(docId, patchHandle);
      waitForPropertyPropagate();

      // Verify the results again. Poppulation should be 500 for second document
      String content = docMgr.read(docId, new StringHandle()).get();
      System.out.println("Patched Number node element is " + content);
      assertTrue( content.contains("\"Pop\":500"));
  }

  /*
   * Purpose: This test is used to validate Git issue 132. Apply a patch to
   * existing collections or permissions on a document using JSONPath
   * expressions.
   *
   * Functions tested : replaceInsertFragment. An new fragment is inserted when
   * unknown index is used.
   */
  @Test
  public void testMetaDataUpdateJSON() throws IOException, JSONException
  {
    String[] filenames = { "json-original.json" };

    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentMetadataPatchBuilder patchBldr = docMgr.newPatchBuilder(Format.JSON);

    // Adding the initial meta-data, since there are none.
    patchBldr.addCollection("JSONPatch1", "JSONPatch3");
    patchBldr.addPermission("test-eval", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.EXECUTE);

    DocumentMetadataPatchBuilder.PatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);
    waitForPropertyPropagate();

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);
    String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
        "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
        "{\"lastName\":\"Foo\"}]}";
    JSONAssert.assertEquals(exp, content, false);

    // Validate the changed meta-data.
    docMgr.readMetadata(docId, mhRead);

    // Collections
    DocumentCollections collections = mhRead.getCollections();
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("JSONPatch1"));
    assertTrue( actualCollections.contains("JSONPatch3"));

    // Construct a Patch From Raw JSON
    /*
     * This is the JSON Format of meta-data for a document: Used for debugging
     * and JSON Path estimation. { "collections" : [ string ], "permissions" : [
     * { "role-name" : string, "capabilities" : [ string ] } ], "properties" : {
     * property-name : property-value }, "quality" : integer }
     */

    /*
     * This is the format for INSERT patch. Refer to Guides. { "patch": [ {
     * "insert": { "context": "$.parent.child1", "position": "before",
     * "content": { "INSERT1": "INSERTED1" } }},
     */

    /*
     * This is the current meta-data in JSON format - For debugging purpose
     * {"collections":["JSONPatch1","JSONPatch3"],
     * "permissions":[{"role-name":"rest-writer",
     * "capabilities":["execute","read","update"]},{"role-name":"test-eval",
     * "capabilities":["execute","read"]},{"role-name":"rest-reader",
     * "capabilities":["read"]}], "properties":{},"quality":0}
     */

    // String str = new
    // String("{\"patch\": [{ \"insert\": {\"context\": \"collections\",\"position\": \"before\",\"content\": { \"shapes\":\"squares\" }}}]}");
  }

  @AfterAll
  public static void tearDown() throws Exception {
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
    deleteUserRole("replaceRoleTest");
  }
}
