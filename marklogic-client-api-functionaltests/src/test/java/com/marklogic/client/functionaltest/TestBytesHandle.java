/*
 * Copyright 2014-2018 MarkLogic Corporation
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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.BytesHandle;

import org.junit.*;

public class TestBytesHandle extends BasicJavaClientREST {

  private static String dbName = "BytesHandleDB";
  private static String[] fNames = { "BytesHandleDB-1" };

  // Additional port to test for Uber port
  private static int uberPort = 8000;
  private static String appServerHostname = null;

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
    appServerHostname = getRestAppServerHostName();
  }

  @Test
  public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {

    String filename = "xml-original-test.xml";
    String uri = "/write-xml-domhandle/";
    System.out.println("Running testXmlCRUD");
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);

    // connect the client
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingBytesHandle(client, filename, uri, null, "XML");

    // read docs
    BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "XML");

    // get the contents

    byte[] readDoc1 = (byte[]) contentHandle.get();
    String readDoc2 = new String(readDoc1);
    Document readDoc = convertStringToXMLDocument(readDoc2);

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);
    assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // Update the Doc
    // acquire the content for update
    String updateFilename = "xml-updated-test.xml";
    updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "XML");

    // read the document
    BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "XML");
    byte[] readDocUpdateInBytes = updateHandle.get();
    String readDocUpdateInString = new String(readDocUpdateInBytes);

    // convert actual string to xml doc
    Document readDocUpdate = convertStringToXMLDocument(readDocUpdateInString);

    // get xml document for expected result
    Document expectedDocUpdate = expectedXMLDocument(updateFilename);
    assertXMLEqual("Write XML Difference", expectedDocUpdate, readDocUpdate);

    // delete the document
    deleteDocument(client, uri + filename, "XML");

    // read the deleted document
    String exception = "";
    try
    {
      readDocumentUsingBytesHandle(client, uri + filename, "XML");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-xml-domhandle/xml-original-test.xml";
    assertEquals("Document is not deleted", expectedException, exception);

    // assertFalse("Document is not deleted", isDocumentExist(client, uri +
    // filename, "XML"));

    // release client
    client.release();

  }

  @Test
  public void testTextCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
    String filename = "text-original.txt";
    String uri = "/write-text-Byteshandle/";
    System.out.println("Runing test TextCRUD");

    // connect the client
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingBytesHandle(client, filename, uri, "Text");

    // read docs
    BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "Text");

    // get the contents
    byte[] fileRead = (byte[]) contentHandle.get();
    // String readContent = contentHandle.get().toString();
    String readContent = new String(fileRead);
    String expectedContent = "hello world, welcome to java API";
    assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());

    // UPDATE the doc
    // acquire the content for update
    // String updateFilename = "text-updated.txt";
    String updateFilename = "text-updated.txt";
    updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "Text");

    // read the document
    BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "Text");

    // get the contents
    byte[] fileReadUpdate = updateHandle.get();
    String readContentUpdate = new String(fileReadUpdate);
    String expectedContentUpdate = "hello world, welcome to java API after new updates";

    assertEquals("Write Text difference", expectedContentUpdate.trim(), readContentUpdate.toString().trim());

    // delete the document
    deleteDocument(client, uri + filename, "Text");

    // read the deleted document
    // assertFalse("Document is not deleted", isDocumentExist(client, uri +
    // filename, "Text"));

    String exception = "";
    try
    {
      readDocumentUsingInputStreamHandle(client, uri + filename, "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-text-Byteshandle/text-original.txt";
    assertEquals("Document is not deleted", expectedException, exception);

    // release client
    client.release();

  }

  @Test
  public void testJsonCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
    String filename = "json-original.json";
    String uri = "/write-json-Byteshandle/";
    System.out.println("Running testJsonCRUD");

    ObjectMapper mapper = new ObjectMapper();

    // connect the client
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingBytesHandle(client, filename, uri, "JSON");

    // read docs
    BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "JSON");

    // get the contents
    byte[] fileRead = contentHandle.get();
    JsonNode readContent = mapper.readTree(fileRead);

    // get expected contents
    JsonNode expectedContent = expectedJSONDocument(filename);

    assertTrue("Write JSON document difference", readContent.equals(expectedContent));

    // update the doc
    // acquire the content for update
    String updateFilename = "json-updated.json";
    updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "JSON");

    // read the document
    BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "JSON");

    // get the contents
    byte[] fileReadUpdate = updateHandle.get();
    JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

    // get expected contents
    JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);
    assertTrue("Write JSON document difference", readContentUpdate.equals(expectedContentUpdate));

    // delete the document
    deleteDocument(client, uri + filename, "JSON");

    // read the deleted document
    // assertFalse("Document is not deleted", isDocumentExist(client, uri +
    // filename, "JSON"));

    String exception = "";
    try
    {
      readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-json-Byteshandle/json-original.json";
    assertEquals("Document is not deleted", expectedException, exception);

    // release client
    client.release();
  }

  @Test
  public void testBinaryCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
    String filename = "Pandakarlino.jpg";
    String uri = "/write-bin-Bytehandle/";
    System.out.println("Running testBinaryCRUD");

    // connect the client
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingBytesHandle(client, filename, uri, "Binary");

    // read docs
    BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "Binary");

    // get the contents
    byte[] fileRead = contentHandle.get();

    // get the binary size
    long size = getBinarySizeFromByte(fileRead);
    long expectedSize = 34543;

    assertEquals("Binary size difference", expectedSize, size);

    // update the doc
    // acquire the content for update
    String updateFilename = "mlfavicon.png";
    updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "Binary");

    // read the document
    BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "Binary");

    // get the contents
    byte[] fileReadUpdate = updateHandle.get();

    // get the binary size
    long sizeUpdate = getBinarySizeFromByte(fileReadUpdate);
    // long expectedSizeUpdate = 3290;
    long expectedSizeUpdate = 3322;
    assertEquals("Binary size difference", expectedSizeUpdate, sizeUpdate);

    // delete the document
    deleteDocument(client, uri + filename, "Binary");

    // read the deleted document
    // assertFalse("Document is not deleted", isDocumentExist(client, uri +
    // filename, "Binary"));

    String exception = "";
    try
    {
      readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-bin-Bytehandle/Pandakarlino.jpg";
    assertEquals("Document is not deleted", expectedException, exception);

    // release client
    client.release();

  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");

  }

}
