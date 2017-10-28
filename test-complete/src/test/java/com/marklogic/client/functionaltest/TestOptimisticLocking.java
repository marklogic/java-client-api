/*
 * Copyright 2014-2017 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.Policy;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.StringHandle;

public class TestOptimisticLocking extends BasicJavaClientREST {
  private static String dbName = "TestOptimisticLockingDB";
  private static String[] fNames = { "TestOptimisticLockingDB-1" };

  private static int restPort = 8011;

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @After
  public void testCleanUp() throws Exception {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testRequired() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testRequired");

    String filename = "xml-original.xml";
    String updateFilename = "xml-updated.xml";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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

    desc.setVersion(badVersion);

    String exception = "";
    String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /optimistic-locking/xml-original.xml doesn't match if-match: 1111";

    // CREATE
    // write document with bad version
    try
    {
      docMgr.write(desc, handle);
    } catch (FailedRequestException e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);
    System.out.println(exception);

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue("Wrong content", content.contains("<name>noodle</name>"));

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

    String updateException = "";
    String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /optimistic-locking/xml-original.xml has current version";

    try {
      docMgr.write(desc, updateHandle);
    } catch (FailedRequestException e) {
      updateException = e.toString();
    }
    System.out.println(updateException);
    boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
    assertTrue("Exception is not thrown", isUpdateExceptionThrown);

    // update with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);

    String updateUnknownException = "";
    String expectedUpdateUnknownException = "com.marklogic.client.FailedRequestException: Local message: Content version required to write document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /optimistic-locking/xml-original.xml";

    try {
      docMgr.write(desc, updateHandle);
    } catch (FailedRequestException e) {
      updateUnknownException = e.toString();
    }

    boolean isUpdateUnknownExceptionThrown = updateUnknownException.contains(expectedUpdateUnknownException);
    System.out.println(updateUnknownException);
    assertTrue("Exception is not thrown", isUpdateUnknownExceptionThrown);

    desc = docMgr.exists(docId);
    goodVersion = desc.getVersion();

    System.out.println("version before update: " + goodVersion);

    // update with good version
    desc.setVersion(goodVersion);
    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue("Wrong content", updateContent.contains("<name>fried noodle</name>"));

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);

    String deleteException = "";
    String expectedDeleteException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to delete document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /optimistic-locking/xml-original.xml has current version";

    try {
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      deleteException = e.toString();
    }

    boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
    System.out.println("Delete exception" + deleteException);
    assertTrue("Exception is not thrown", isDeleteExceptionThrown);

    // delete using unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);

    String deleteUnknownException = "";
    String expectedDeleteUnknownException = "com.marklogic.client.FailedRequestException: Local message: Content version required to delete document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /optimistic-locking/xml-original.xml";

    try {
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      deleteUnknownException = e.toString();
    }

    boolean isDeleteUnknownExceptionThrown = deleteUnknownException.contains(expectedDeleteUnknownException);
    System.out.println("Delete exception" + deleteUnknownException);
    assertTrue("Exception is not thrown", isDeleteUnknownExceptionThrown);

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
    assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);
    System.out.println("Delete exception" + verifyDeleteException);
    // release client
    client.release();

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(configMgr.getUpdatePolicy().toString());
  }

  @Test
  public void testOptionalWithUnknownVersion() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testOptionalWithUnknownVersion");

    String filename = "json-original.json";
    String updateFilename = "json-updated.json";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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

    String exception = "";
    String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    // CREATE
    // write document with bad version
    try {
      docMgr.write(desc, handle);
    } catch (FailedRequestException e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue("Wrong content", content.contains("John"));

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

    String updateException = "";
    String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    try {
      docMgr.write(desc, updateHandle);
    } catch (FailedRequestException e) {
      updateException = e.toString();
    }

    boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
    assertTrue("Exception is not thrown", isUpdateExceptionThrown);

    // update with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);

    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue("Wrong content", updateContent.contains("Aries"));

    unknownVersion = desc.getVersion();

    System.out.println("unknown version after update: " + unknownVersion);

    // read using matched version
    desc.setVersion(unknownVersion);
    StringHandle readMatchHandle = new StringHandle();
    docMgr.read(desc, readMatchHandle);
    String readMatchContent = readMatchHandle.get();
    assertTrue("Document does not return null", readMatchContent == null);

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);

    String deleteException = "";
    String expectedDeleteException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to delete document";

    try {
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      deleteException = e.toString();
    }

    boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
    assertTrue("Exception is not thrown", isDeleteExceptionThrown);

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
    assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);

    // release client
    client.release();

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(configMgr.getUpdatePolicy().toString());
  }

  @Test
  public void testOptionalWithGoodVersion() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testOptionalWithGoodVersion");

    String filename = "json-original.json";
    String updateFilename = "json-updated.json";
    String uri = "/optimistic-locking/";
    String docId = uri + filename;
    long badVersion = 1111;

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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

    String exception = "";
    String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    // CREATE
    // write document with bad version
    try {
      docMgr.write(desc, handle);
    } catch (FailedRequestException e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // write document with unknown version
    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, handle);

    StringHandle readHandle = new StringHandle();
    docMgr.read(desc, readHandle);
    String content = readHandle.get();
    assertTrue("Wrong content", content.contains("John"));

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

    String updateException = "";
    String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    try {
      docMgr.write(desc, updateHandle);
    } catch (FailedRequestException e) {
      updateException = e.toString();
    }

    boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
    assertTrue("Exception is not thrown", isUpdateExceptionThrown);

    // update with good version
    desc.setVersion(goodVersion);

    docMgr.write(desc, updateHandle);

    StringHandle updateReadHandle = new StringHandle();
    docMgr.read(desc, updateReadHandle);
    String updateContent = updateReadHandle.get();
    assertTrue("Wrong content", updateContent.contains("Aries"));

    goodVersion = desc.getVersion();

    System.out.println("good version after update: " + goodVersion);

    // read using matched version
    desc.setVersion(goodVersion);
    StringHandle readMatchHandle = new StringHandle();
    docMgr.read(desc, readMatchHandle);
    String readMatchContent = readMatchHandle.get();
    assertTrue("Document does not return null", readMatchContent == null);

    // DELETE
    // delete using bad version
    desc.setVersion(badVersion);

    String deleteException = "";
    String expectedDeleteException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to delete document";

    try {
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      deleteException = e.toString();
    }

    boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
    assertTrue("Exception is not thrown", isDeleteExceptionThrown);

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
    assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);

    // release client
    client.release();

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(configMgr.getUpdatePolicy().toString());
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
    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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
    assertTrue("Wrong content", content.contains("John"));

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
    assertTrue("Wrong content", updateContent.contains("Aries"));

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
    assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);

    // release client
    client.release();

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(configMgr.getUpdatePolicy().toString());
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // require content versions for updates and deletes
    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_OPTIONAL);

    // write the server configuration to the database
    configMgr.writeConfiguration();
    client.release();
    cleanupRESTServer(dbName, fNames);

  }
}
