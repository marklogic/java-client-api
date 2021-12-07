/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import com.marklogic.client.Transaction;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;

public class TestDocumentFormat extends BasicJavaClientREST {

  private static String dbName = "TestDocumentFormatDB";
  private static String[] fNames = { "TestDocumentFormat-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    // Create a user with minimal privs and test doc exists in a transaction.
    createRESTUser("userInTrans", "x", "rest-writer");
  }

  @Test
  public void testXMLFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager<GenericReadHandle, GenericWriteHandle> docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/json-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try
    {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/bin-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/txt-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/json-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/xml-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/bin-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/txt-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/xml-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try
    {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/json-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try
    {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try
    {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnText");

    String filename = "text-original.txt";
    String uri = "/txt-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnText");

    String filename = "text-original.txt";
    String uri = "/xml-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnText");

    String filename = "text-original.txt";
    String uri = "/json-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try
    {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnText");

    String filename = "text-original.txt";
    String uri = "/bin-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals("URI is not found", expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testNegativeJSONFormatWithDOMHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("Running testNegativeJSONFormatWithDOMHandle");

    String filename = "xml-original.xml";
    String uri = "/negative-format/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    Document readDoc = expectedXMLDocument(filename);

    // File file = new
    // File("src/test/java/com/marklogic/client/functionaltest/data/" +
    // filename);

    // create a handle on the content
    DOMHandle handle = new DOMHandle();
    handle.set(readDoc);

    String exception = "";
    String expectedException = "java.lang.IllegalArgumentException: DOMHandle supports the XML format only";

    try
    {
      handle.setFormat(Format.JSON);
    } catch (IllegalArgumentException e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue("Wrong exception", isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testDocExistsWithTransaction() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testDocExistsWithTransaction");

    String filename = "json-original.json";
    String uri1 = "/DocExistsInTransMinimalPriv/";
    String uri2 = "/DocExistsTransWithPrivs/";

    // user with minimal privs.
    DatabaseClient client1 = getDatabaseClient("userInTrans", "x", getConnType());
    // user with privs.
    DatabaseClient client2 = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr1 = client1.newDocumentManager();
    DocumentManager docMgr2 = client2.newDocumentManager();
    Transaction t1 = client1.openTransaction();
    Transaction t2 = client2.openTransaction();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docIds
    String docId1 = uri1 + filename;
    String docId2 = uri2 + filename;
    docMgr1.write(docId1, handle);
    docMgr2.write(docId2, handle);

    String expectedUri1 = uri1 + filename;
    String expectedUri2 = uri2 + filename;
    String docUri1 = docMgr1.exists(expectedUri1, t1).getUri();
    String docUri2 = docMgr2.exists(expectedUri2, t2).getUri();
    assertEquals("URI is not found", expectedUri1, docUri1);
    assertEquals("URI is not found", expectedUri2, docUri2);
    // release the clients
    client1.release();
    client2.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    //Delete user userInTrans
    deleteRESTUser("userInTrans");
    cleanupRESTServer(dbName, fNames);
  }
}
