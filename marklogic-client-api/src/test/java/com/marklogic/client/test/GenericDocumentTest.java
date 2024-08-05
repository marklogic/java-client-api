/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import static org.custommonkey.xmlunit.XMLAssert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GenericDocumentTest {
  static private Random seed;

  @BeforeAll
  public static void beforeClass() {
    seed = new Random();
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
    seed = null;
  }

  @Test
  public void testExists() {
    String docId = "/test/testExists1.txt";

    TextDocumentManager docMgr = Common.client.newTextDocumentManager();

    // guarantee that a previous run didn't leave the document
    docMgr.delete(docId);

    assertTrue( docMgr.exists(docId)==null);
    docMgr.write(docId,new StringHandle().with("A simple text document"));
    assertTrue("Existent document doesn't appear to exist", docMgr.exists(docId)!=null);
    docMgr.delete(docId);
  }

  @Test
  public void testDelete() {
    String docId = "/test/testDelete1.txt";

    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    docMgr.write(docId, new StringHandle().with("A simple text document"));
    String text = docMgr.read(docId, new StringHandle()).get();
    assertTrue( text != null && text.length() > 0);
    docMgr.delete(docId);
    text = null;
    boolean hadException = false;
    try {
      text = docMgr.read(docId, new StringHandle()).get();
    } catch (Exception ex) {
      hadException = true;
    }
    assertTrue( text == null && hadException);
  }

  final static String content = "<?xml version='1.0' encoding='UTF-8'?>\n"+
    "<root mode='mixed' xml:lang='en'>\n"+
    "<child mode='basic'>value</child>\n"+
    "A simple XML document\n"+
    "</root>\n";

  final static String metadata =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
      "<rapi:metadata uri=\"/test/testMetadataXML1.xml\" xsi:schemaLocation=\"http://marklogic.com/rest-api restapi.xsd\" xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
      "  <rapi:collections>\n"+
      "    <rapi:collection>/document/collection1</rapi:collection>\n"+
      "    <rapi:collection>/document/collection2</rapi:collection>\n"+
      "    <rapi:collection>/document/collection4before</rapi:collection>\n"+
      "  </rapi:collections>\n"+
      "  <rapi:permissions>\n"+
      "    <rapi:permission>\n"+
      "      <rapi:role-name>app-user</rapi:role-name>\n"+
      "      <rapi:capability>update</rapi:capability>\n"+
      "      <rapi:capability>read</rapi:capability>\n"+
      "    </rapi:permission>\n"+
      "  </rapi:permissions>\n"+
      "  <prop:properties xmlns:prop=\"http://marklogic.com/xdmp/property\">\n"+
      "    <first>value one</first>\n"+
      "    <second>2</second>\n"+
      "  </prop:properties>\n"+
      "  <rapi:quality>3</rapi:quality>\n"+
      "  <rapi:metadata-values>"+
      "    <rapi:metadata-value key=\"key1\">value1</rapi:metadata-value>"+
      "    <rapi:metadata-value key=\"key2\">value2</rapi:metadata-value>"+
      "    <rapi:metadata-value key=\"number1\">10</rapi:metadata-value>"+
      "  </rapi:metadata-values>"+
      "</rapi:metadata>\n";

  final static String patchedMetadata =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
      "<rapi:metadata uri=\"/test/testMetadataXML1.xml\" xsi:schemaLocation=\"http://marklogic.com/rest-api restapi.xsd\" xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
      "  <rapi:collections>\n"+
      "    <rapi:collection>/document/collection1</rapi:collection>\n"+
      "    <rapi:collection>/document/collection2</rapi:collection>\n"+
      "    <rapi:collection>/document/collection3</rapi:collection>\n"+
      "  </rapi:collections>\n"+
      "  <rapi:permissions>\n"+
      "    <rapi:permission>\n"+
      "      <rapi:role-name>app-user</rapi:role-name>\n"+
      "      <rapi:capability>update</rapi:capability>\n"+
      "    </rapi:permission>\n"+
      "  </rapi:permissions>\n"+
      "  <prop:properties xmlns:prop=\"http://marklogic.com/xdmp/property\">\n"+
      "    <second>2</second>\n"+
      "  </prop:properties>\n"+
      "  <rapi:quality>4</rapi:quality>\n"+
      "</rapi:metadata>\n";

  @Test
  public void testReadWriteMetadata() throws SAXException, IOException, XpathException {
    String docId = "/test/testMetadataXML1.xml";

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write(docId, new StringHandle().with(content));

    docMgr.setMetadataCategories(Metadata.ALL);
    docMgr.writeMetadata(docId, new StringHandle().with(metadata));

    StringHandle xmlStringHandle = new StringHandle();
    String stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
    assertTrue( stringMetadata != null && stringMetadata.length() > 0);

    Document domMetadata = docMgr.readMetadata(docId, new DOMHandle()).get();
    assertTrue( domMetadata != null);

    StringHandle jsonStringHandle = new StringHandle();
    jsonStringHandle.setFormat(Format.JSON);
    stringMetadata = docMgr.readMetadata(docId, jsonStringHandle).get();
    assertTrue( stringMetadata != null && stringMetadata.length() > 0);

    String docText = docMgr.read(docId, xmlStringHandle, new StringHandle()).get();
    stringMetadata = xmlStringHandle.get();
    assertXMLEqual("Failed to read document content in single request",content,docText);
    assertTrue( stringMetadata != null);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
    assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",stringMetadata);
    assertXpathEvaluatesTo("value1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", stringMetadata);

    docId = "/test/testMetadataXML2.xml";
    docMgr.write(docId, new StringHandle().with(metadata), new StringHandle().with(content));
    docText = docMgr.read(docId, xmlStringHandle, new StringHandle()).get();
    stringMetadata = xmlStringHandle.get();
    assertXMLEqual("Failed to write document content in single request",content,docText);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
    assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",stringMetadata);
    assertXpathEvaluatesTo("value1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", stringMetadata);

    docMgr.writeDefaultMetadata(docId);
    stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
    assertTrue( stringMetadata != null);
    assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
    assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
    assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
    assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
    assertXpathEvaluatesTo("0","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",stringMetadata);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testUrisWithSpaces() {
    DocumentManager docMgr = Common.client.newDocumentManager();

    String[] testUris = new String[] { "/a", "/a%20b", "/a+b+c", "/a%isa#vrybig*andStrangeUr-d/x", "/χρυσαφὶ.html", "/фальшивый" };
    for (String testUri : testUris) {
      String contents = "<a>" + testUri + "</a>";
      docMgr.write(testUri, new StringHandle(contents));
      StringHandle result = new StringHandle();
      docMgr.read(testUri, result);
      assertEquals(contents, result.get());
      docMgr.delete(testUri);
      try {
        docMgr.read(testUri, result);
        fail("Document was not deleted");
      } catch (ResourceNotFoundException e) {
        //pass   404 after delete successful
      }

    }

    String[] urisWithSpaces = new String[] { "/a b", "/uri with spaces" };
    for (String testUri : urisWithSpaces) {
      try {
        StringHandle result = new StringHandle();   // should be able to read these, with 404
        docMgr.read(testUri, result);
      } catch (ResourceNotFoundException e) {
        //pass
      }
      try {
        String contents = "<a>" + testUri + "</a>";
        docMgr.write(testUri, new StringHandle(contents));
        fail("Server accepted URI with a space in it");
      } catch (FailedRequestException e) {
        // pass   cannot write to uris with spaces.
      }
    }
    for (String testUri : urisWithSpaces) {
      docMgr.delete(testUri);		// can delete with 204.
    }
  }


  @Test
  public void testCommit() throws XpathException {
    String docId1 = "/test/testExists1.txt";
    String docId2 = "/test/testExists2.txt";

    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    docMgr.write(docId1,new StringHandle().with("A simple text document"));

    String transactionName = "java-client-" + seed.nextLong();

    Transaction transaction = Common.client.openTransaction(transactionName);
    StringHandle docHandle = docMgr.read(docId1, new StringHandle(), transaction);
    docMgr.write(docId2, docHandle, transaction);
    docMgr.delete(docId1, transaction);

    Document status = transaction.readStatus(new DOMHandle()).get();
    assertXpathExists("//*[local-name() = 'transaction-name' and "+
      "string(.) = '"+transactionName+"']", status);

    transaction.commit();

    assertTrue(        docMgr.exists(docId1)==null);
    assertTrue("Document 2 doesn't exist", docMgr.exists(docId2)!=null);

    docMgr.delete(docId2);
  }

  @Test
  public void testRollback() {
    String docId1 = "/test/testExists1.txt";
    String docId2 = "/test/testExists2.txt";

    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    docMgr.write(docId1,new StringHandle().with("A simple text document"));

    Transaction transaction = Common.client.openTransaction();
    StringHandle docHandle = docMgr.read(docId1, new StringHandle(), transaction);
    docMgr.write(docId2, docHandle, transaction);
    docMgr.delete(docId1, transaction);
    transaction.rollback();

    assertTrue("Document 1 doesn't exist", docMgr.exists(docId1)!=null);
    assertTrue(        docMgr.exists(docId2)==null);

    docMgr.delete(docId1);
  }

  @Test
  public void testMultiple() throws XpathException {
    int docMax        = 3;
    int collectionMax = 2;

    String[] docIds = new String[docMax];
    for (int i=1; i <= docMax; i++) {
      docIds[i - 1] = "/test/testMulti"+i+".txt";
    }

    String[] collections = new String[collectionMax];
    for (int i=1; i <= collectionMax; i++) {
      collections[i - 1] = "/document/collection"+i;
    }

    DocumentMetadataHandle metaWriteHandle = new DocumentMetadataHandle();
    metaWriteHandle.getCollections().addAll(collections);

    Transaction transaction = Common.client.openTransaction();

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.setMetadataCategories(Metadata.COLLECTIONS);

    for (String docId: docIds) {
      docMgr.write(
        docId,
        metaWriteHandle,
        new StringHandle().with("<document>"+docId+"</document>"),
        transaction);
    }

    transaction.commit();

    for (String docId: docIds) {
      assertTrue("Document doesn't exist "+docId, docMgr.exists(docId)!=null);

      DocumentMetadataHandle metaReadHandle = docMgr.readMetadata(docId, new DocumentMetadataHandle());
      assertTrue( metaReadHandle != null);

      DocumentCollections readCollections = metaReadHandle.getCollections();
      assertEquals( collectionMax, readCollections.size());
    }

    docMgr.setMetadataCategories(Metadata.COLLECTIONS);
    docMgr.writeDefaultMetadata(docIds);
    for (String docId: docIds) {
      DocumentMetadataHandle metaReadHandle = docMgr.readMetadata(docId, new DocumentMetadataHandle());

      DocumentCollections readCollections = metaReadHandle.getCollections();
      assertEquals( 0, readCollections.size());
    }

    for (String docId: docIds) {
      docMgr.delete(docId);
    }
  }

  @Test
  public void testCreate() throws SAXException, IOException, XpathException {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    DocumentUriTemplate template =
      docMgr.newDocumentUriTemplate("xml").withDirectory("/test/testcreate/");

    DocumentDescriptor desc = docMgr.create(template, new StringHandle().with(content));
    String docId = desc.getUri();
    assertTrue(
      docId != null && docId.length() > 0);

    String docText = docMgr.read(desc, new StringHandle()).get();
    assertXMLEqual("Failed to read content for created document", content, docText);

    docMgr.delete(desc);

    docMgr.setMetadataCategories(Metadata.ALL);
    desc = docMgr.create(
      template,
      new StringHandle().with(metadata).withFormat(Format.XML),
      new StringHandle().with(content));
    docId = desc.getUri();
    assertTrue(
      docId != null && docId.length() > 0);

    String stringMetadata = docMgr.readMetadata(
      docId,
      new StringHandle().withFormat(Format.XML)
    ).get();
    assertTrue(
      stringMetadata != null && stringMetadata.length() > 0);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='app-user'])",stringMetadata);
    assertXpathEvaluatesTo("2","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",stringMetadata);
    assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='3'])",stringMetadata);
    assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",stringMetadata);
    assertXpathEvaluatesTo("value1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", stringMetadata);

    docMgr.delete(docId);
  }

  @Test
  public void testPatch() throws IOException, XpathException, SAXException {
    String docId = "/test/testMetadataXML1.xml";

    GenericDocumentManager docMgr = Common.client.newDocumentManager();
    docMgr.write(
      docId,
      new BytesHandle(content.getBytes(Charset.forName("UTF-8")))
        .withFormat(Format.XML)
    );

    docMgr.setMetadataCategories(Metadata.ALL);

    for (Format format: new Format[]{Format.XML, Format.JSON}) {
      // init or reinit
      docMgr.writeMetadata(docId, new StringHandle().with(metadata));

      DocumentMetadataPatchBuilder patchBldr = docMgr.newPatchBuilder(format);
      DocumentPatchHandle patchHandle = patchBldr
        .addCollection("/document/collection3")
        .replaceCollection("/document/collection4before", "/document/collection4after")
        .replacePermission("app-user", Capability.UPDATE)
        .deleteProperty("first")
        .replacePropertyApply("second", patchBldr.call().add(3))
        .setQuality(4)
        .addMetadataValue("key3", "value3")
        .deleteMetadataValue("key2")
        .replaceMetadataValueApply("number1", patchBldr.call().add(5))
        .replaceMetadataValue("key1", "modifiedValue1")
        .build();

      docMgr.patch(docId, patchHandle);

      String metadata = docMgr.readMetadata(docId, new StringHandle().withFormat(Format.XML)).get();

      assertTrue( metadata != null);
      assertXpathEvaluatesTo("4","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection'])",metadata);
      assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='collections']/*[local-name()='collection' and string(.)='/document/collection4after'])",metadata);
      assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission' and string(*[local-name()='role-name'])='app-user']/*[local-name()='capability'])",metadata);
      assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='first' or local-name()='second'])",metadata);
      assertXpathEvaluatesTo("5","string(/*[local-name()='metadata']/*[local-name()='properties']/*[local-name()='second'])",metadata);
      assertXpathEvaluatesTo("1","count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='4'])",metadata);
      assertXpathEvaluatesTo("3","count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'])",metadata);
      assertXpathEvaluatesTo("value3", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key3\"]", metadata);
      assertXpathEvaluatesTo("modifiedValue1", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key1\"]", metadata);
      assertXpathEvaluatesTo("0", "count(/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"key2\"])", metadata);
      assertXpathEvaluatesTo("15", "/*[local-name()='metadata']/*[local-name()='metadata-values']/*[local-name()='metadata-value'][@key=\"number1\"]", metadata);
    }

    docMgr.delete(docId);
  }


}

