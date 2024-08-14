/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.ContentNoVersionException;
import com.marklogic.client.ContentWrongVersionException;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;


public class TestOptimisticLocking extends AbstractFunctionalTest {


  @AfterAll
  public static void tearDown() throws Exception
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // require content versions for updates and deletes
    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_OPTIONAL);

    // write the server configuration to the database
    configMgr.writeConfiguration();
  }

  @Test
  public void testRequired() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testRequired");

    String filename = "xml-original.xml";
    String updateFilename = "xml-updated.xml";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for the server configuration
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // require content versions for updates and deletes
    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    System.out.println("set optimistic locking to required");

    // create document manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create document descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);
	  AtomicReference<DocumentDescriptor> descRef = new AtomicReference<>(desc);

    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.write(descRef.get(), handle));

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue( content.contains("<name>noodle</name>"));

    // get the good version
    long goodVersion = desc.getVersion();

    System.out.println("version before create: " + goodVersion);

    // UPDATE
    File updateFile = new File("src/test/java/com/marklogic/client/functionaltest/data/" + updateFilename);

    // create a handle on the content
    FileHandle updateHandle = new FileHandle(updateFile);
    updateHandle.set(updateFile);

    // update with bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.write(descRef.get(), updateHandle));

    // update with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
	assertThrows(ContentNoVersionException.class, () -> docMgr.write(descRef.get(), updateHandle));

    desc = docMgr.exists(docId);
	descRef.set(desc);
    goodVersion = desc.getVersion();

    System.out.println("version before update: " + goodVersion);

    // update with good version
    desc.setVersion(goodVersion);
    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue( updateContent.contains("<name>fried noodle</name>"));

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.delete(descRef.get()));

    // delete using unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
	assertThrows(ContentNoVersionException.class, () -> docMgr.delete(descRef.get()));

    // delete using good version
    desc = docMgr.exists(docId);
    goodVersion = desc.getVersion();

    System.out.println("version before delete: " + goodVersion);

    docMgr.delete(desc);

    String verifyDeleteException = "";
    String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /optimistic-locking/xml-original.xml";

    StringHandle deleteHandle = new StringHandle();
    try {
      docMgr.read(desc, deleteHandle);
    } catch (ResourceNotFoundException e) {
      verifyDeleteException = e.toString();
    }

    boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
    assertTrue( isVerifyDeleteExceptionThrown);
    System.out.println("Delete exception" + verifyDeleteException);
  }

  @Test
  public void testOptionalWithUnknownVersion() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOptionalWithUnknownVersion");

    String filename = "json-original.json";
    String updateFilename = "json-updated.json";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for the server configuration
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_OPTIONAL);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    System.out.println("set optimistic locking to optional");

    // create document manager
    JSONDocumentManager docMgr = client.newJSONDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create document descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.write(desc, handle));

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue( content.contains("John"));

    // get the unknown version
    long unknownVersion = desc.getVersion();

    System.out.println("unknown version after create: " + unknownVersion);

    // UPDATE
    File updateFile = new File("src/test/java/com/marklogic/client/functionaltest/data/" + updateFilename);

    // create a handle on the content
    FileHandle updateHandle = new FileHandle(updateFile);
    updateHandle.set(updateFile);

    // update with bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.write(desc, updateHandle));

    // update with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);

    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue( updateContent.contains("Aries"));

    unknownVersion = desc.getVersion();

    System.out.println("unknown version after update: " + unknownVersion);

    // read using matched version
    desc.setVersion(unknownVersion);
    StringHandle readMatchHandle = new StringHandle();
    docMgr.read(desc, readMatchHandle);
    String readMatchContent = readMatchHandle.get();
    assertNull( readMatchContent);

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.delete(desc));

    // delete using unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.delete(desc);

    unknownVersion = desc.getVersion();

    System.out.println("unknown version after delete: " + unknownVersion);

    String verifyDeleteException = "";
    String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";

    StringHandle deleteHandle = new StringHandle();
    try {
      docMgr.read(desc, deleteHandle);
    } catch (ResourceNotFoundException e) {
      verifyDeleteException = e.toString();
    }

    boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
    assertTrue( isVerifyDeleteExceptionThrown);
  }

  @Test
  public void testOptionalWithGoodVersion() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOptionalWithGoodVersion");

    String filename = "json-original.json";
    String updateFilename = "json-updated.json";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for the server configuration
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_OPTIONAL);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    System.out.println("set optimistic locking to optional");

    // create document manager
    JSONDocumentManager docMgr = client.newJSONDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create document descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    // CREATE
    // write document with bad version
	  desc.setVersion(badVersion);
	  assertThrows(ContentWrongVersionException.class, () -> docMgr.write(desc, handle));

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue( content.contains("John"));

    // get the good version
    long goodVersion = desc.getVersion();

    System.out.println("good version after create: " + goodVersion);

    // UPDATE
    File updateFile = new File("src/test/java/com/marklogic/client/functionaltest/data/" + updateFilename);

    // create a handle on the content
    FileHandle updateHandle = new FileHandle(updateFile);
    updateHandle.set(updateFile);

    // update with bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.write(desc, updateHandle));

    // update with good version
    desc.setVersion(goodVersion);

    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue( updateContent.contains("Aries"));

    goodVersion = desc.getVersion();

    System.out.println("good version after update: " + goodVersion);

    // read using matched version
    desc.setVersion(goodVersion);
    StringHandle readMatchHandle = new StringHandle();
    docMgr.read(desc, readMatchHandle);
    String readMatchContent = readMatchHandle.get();
    assertNull( readMatchContent);

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);
	assertThrows(ContentWrongVersionException.class, () -> docMgr.delete(desc));

    // delete using good version
    desc.setVersion(goodVersion);
    docMgr.delete(desc);

    goodVersion = desc.getVersion();

    System.out.println("unknown version after delete: " + goodVersion);

    String verifyDeleteException = "";
    String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";

    StringHandle deleteHandle = new StringHandle();
    try {
      docMgr.read(desc, deleteHandle);
    } catch (ResourceNotFoundException e) {
      verifyDeleteException = e.toString();
    }

    boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
    assertTrue( isVerifyDeleteExceptionThrown);
  }

  @Test
  public void testNone() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testNone");

    String filename = "json-original.json";
    String updateFilename = "json-updated.json";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for the server configuration
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // use Policy.NONE
    configMgr.setUpdatePolicy(UpdatePolicy.MERGE_METADATA);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    System.out.println("set optimistic locking to none");

    // create document manager
    JSONDocumentManager docMgr = client.newJSONDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create document descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    desc.setVersion(badVersion);

    // CREATE
    // write document with bad version

    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue( content.contains("John"));

    // UPDATE
    File updateFile = new File("src/test/java/com/marklogic/client/functionaltest/data/" + updateFilename);

    // create a handle on the content
    FileHandle updateHandle = new FileHandle(updateFile);
    updateHandle.set(updateFile);

    // update with bad version
    desc.setVersion(badVersion);

    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue( updateContent.contains("Aries"));

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);

    docMgr.delete(desc);

    String verifyDeleteException = "";
    String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";

    StringHandle deleteHandle = new StringHandle();
    try {
      docMgr.read(desc, deleteHandle);
    } catch (ResourceNotFoundException e) {
      verifyDeleteException = e.toString();
    }

    boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
    assertTrue( isVerifyDeleteExceptionThrown);
  }

  @Test
  public void testAfterAndBeforeQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
	  System.out.println("Running testAfterAndBeforeQuery");

	  String[] filenames1 = { "constraint1.xml", "constraint2.xml" };
	  String[] filenames2 = { "constraint3.xml" };
	  String[] filenames3 = { "constraint4.xml" };
	  String[] filenames4 = { "constraint5.xml" };

	  String URLprefix = "/structured-query-andnot/";

	  // connect the client
	  DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

	  // create a manager for the server configuration
	  ServerConfigurationManager configMgr = client.newServerConfigManager();

	  // read the server configuration from the database
	  configMgr.readConfiguration();

	  // require content versions for updates and deletes
	  // use Policy.OPTIONAL to allow but not require versions
	  configMgr.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);
	  configMgr.setQueryOptionValidation(true);

	  // write the server configuration to the database
	  configMgr.writeConfiguration();

	  // write docs
	  for (String filename : filenames1) {
		  writeDocumentUsingInputStreamHandle(client, filename, URLprefix, "XML");
	  }
      // TODO This used to have very long delays in it, like 30 to 45s. Seems to work reliably with a delay of 1s,
      // though not certain yet as I don't fully understand what this test is doing yet.
      waitFor(1000);
	  for (String filename : filenames2) {
		  writeDocumentUsingInputStreamHandle(client, filename, URLprefix, "XML");
	  }
      waitFor(1000);
	  for (String filename : filenames3) {
		  writeDocumentUsingInputStreamHandle(client, filename, URLprefix, "XML");
	  }
      waitFor(1000);

	// write docs
	  for (String filename : filenames4) {
		  writeDocumentUsingInputStreamHandle(client, filename, URLprefix, "XML");
	  }
	  // create document manager
	  XMLDocumentManager docMgr = client.newXMLDocumentManager();
	  QueryManager queryMgr = client.newQueryManager();

	  DocumentDescriptor desc1 = docMgr.exists(URLprefix + "constraint1.xml");
	  long constraint1 = desc1.getVersion();

	  DocumentDescriptor desc3 = docMgr.exists(URLprefix + "constraint3.xml");
	  long constraint3 = desc3.getVersion();

	  DocumentDescriptor desc4 = docMgr.exists(URLprefix + "constraint4.xml");
	  long constraint4 = desc4.getVersion();

	  DocumentDescriptor desc5 = docMgr.exists(URLprefix + "constraint5.xml");
	  long constraint5 = desc5.getVersion();

	  System.out.println("TimeStamp for constraint1.xml is : " + constraint1);
	  System.out.println("TimeStamp for constraint3.xml is : " + constraint3);
	  System.out.println("TimeStamp for constraint4.xml is : " + constraint4);
	  System.out.println("TimeStamp for constraint5.xml is : " + constraint5);

	  StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
	  StructuredQueryDefinition qd = null, qd2 = null, qd3 = null, qd4 = null;
	  try {
		  qd = qb.afterQuery(0L);
	  }
	  catch (Exception ex) {
		  String aftQueryExMsg = ex.getMessage();
		  assertTrue(
				  aftQueryExMsg.contains("timestamp cannot be zero") );
	  }
	  try {
		  qd = qb.beforeQuery(0L);
	  }
	  catch (Exception ex) {
		  String aftQueryExMsg = ex.getMessage();
		  assertTrue(
				  aftQueryExMsg.contains("timestamp cannot be zero") );
	  }
	  // Search with actual constarint1 time-stamp value
	  try {
		  qd = qb.afterQuery(constraint3 - 10000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("afterQuery with constraint1 timestamp failed");
	  }
	  // create handle
	  JacksonHandle resultsHandle = new JacksonHandle();
	  queryMgr.search(qd, resultsHandle);
	  JsonNode jsRes = resultsHandle.get();
	  System.out.println("Total from results is : " + jsRes.get("total").asText());
	  assertEquals( "3", jsRes.get("total").asText());
	  jsRes = null;
	  resultsHandle = null;

	  Instant inst = Instant.ofEpochMilli(constraint1);
	  Instant inst2 = inst.minus(1L, ChronoUnit.DAYS);

	  QueryManager queryMgr2 = client.newQueryManager();
	  StructuredQueryBuilder qb2 = queryMgr2.newStructuredQueryBuilder();
	  // Search with constarint1 time-stamp value minus 1 day.
	  try {
		  qd2 = qb2.beforeQuery(inst2.getEpochSecond() * 1000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("resultsHandleBef2 with constraint1 timestamp failed");
	  }
	  JacksonHandle resultsHandleBef2 = new JacksonHandle();
	  queryMgr2.search(qd2, resultsHandleBef2);
	  JsonNode jsRes2 = resultsHandleBef2.get();
	  System.out.println("Total from results is on the day before: " + jsRes2.get("total").asText());
	  // Zero documents should be available for yesterday
	  assertEquals( "0", jsRes2.get("total").asText());

	  inst = Instant.ofEpochMilli(constraint3);
	  Instant inst3 = inst.plus(1L, ChronoUnit.MINUTES);

	  System.out.println("inst3 " + inst3.toString());

	  QueryManager queryMgr3 = client.newQueryManager();
	  StructuredQueryBuilder qb3 = queryMgr3.newStructuredQueryBuilder();
	  // Search with constarint3 time-stamp value plus 1 minute.
	  try {
		  qd3 = qb3.afterQuery(inst3.getEpochSecond() * 1000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("afterQuery with constraint3 timestamp failed");
	  }
	  JacksonHandle resultsHandleBef3 = new JacksonHandle();
	  queryMgr3.search(qd3, resultsHandleBef3);
	  JsonNode jsRes3 = resultsHandleBef3.get();
	  System.out.println("Total from results is : " + jsRes3.get("total").asText());
	  // Two documents should be available for the time
	  assertEquals( "2", jsRes3.get("total").asText());

	  QueryManager queryMgr4 = client.newQueryManager();
	  StructuredQueryBuilder qb4 = queryMgr4.newStructuredQueryBuilder();
	  // Search with constarint3 time-stamp value plus 1 minute.
	  try {
		  qd4 = qb4.beforeQuery(inst3.getEpochSecond() * 1000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("beforeQuery with constraint3 timestamp failed");
	  }
	  JacksonHandle resultsHandleBef4 = new JacksonHandle();
	  queryMgr3.search(qd4, resultsHandleBef4);
	  JsonNode jsRes4 = resultsHandleBef4.get();
	  System.out.println("Total from results is : " + jsRes4.get("total").asText());
	  // Three documents should be available for the time
	  assertEquals( "3", jsRes4.get("total").asText());

	  // Test for meta data changes. First make sure after constraint5 timestamp + 1 there are no docs. Should be zero.
	  inst = Instant.ofEpochMilli(constraint5);
	  Instant inst5 = inst.plus(1L, ChronoUnit.MINUTES);
	  try {
		  qd4 = qb4.afterQuery(inst5.getEpochSecond() * 1000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("afterQuery with constraint5 timestamp failed");
	  }
	  resultsHandleBef4 = new JacksonHandle();
	  queryMgr3.search(qd4, resultsHandleBef4);
	  jsRes4 = resultsHandleBef4.get();
	  System.out.println("Total from results is : " + jsRes4.get("total").asText());
	  // Zero documents should be available for the time
	  assertEquals( "0", jsRes4.get("total").asText());
	  //Now update meta data for contraint5.xml file and see if update is reflected.
	  DocumentMetadataPatchBuilder patchBldr = docMgr.newPatchBuilder(Format.JSON);
	  // Adding the initial meta-data, since there are none.
      patchBldr.addCollection("XMLPatch1", "XMLPatch2");
      DocumentMetadataPatchBuilder.PatchHandle patchHandle = patchBldr.build();
      docMgr.patch(URLprefix + "constraint5.xml", patchHandle);
      waitForPropertyPropagate();
      try {
		  qd4 = qb4.afterQuery(inst5.getEpochSecond() * 1000);
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("afterQuery with constraint5 timestamp failed");
	  }
      resultsHandleBef4 = new JacksonHandle();
      queryMgr3.search(qd4, resultsHandleBef4);
      jsRes4 = resultsHandleBef4.get();
      System.out.println("Total from results after meta-data update is : " + jsRes4.get("total").asText());
      assertEquals( "1", jsRes4.get("total").asText());
}

}
