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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.InputStreamHandle;

public class TestInputStreamHandle extends BasicJavaClientREST {

  private static String dbName = "InputStreamHandleDB";
  private static String[] fNames = { "InputStreamHandleDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    String filename = "xml-original-test.xml";
    String uri = "/write-xml-inputstreamhandle/";

    System.out.println("Running testXmlCRUD");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // read docs
    InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");

    // get the contents
    InputStream fileRead = contentHandle.get();

    String readContent = convertInputStreamToString(fileRead);

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);

    // convert actual string to xml doc
    Document readDoc = convertStringToXMLDocument(readContent);

    assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // update the doc
    // acquire the content for update
    String updateFilename = "xml-updated-test.xml";
    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");

    // read the document
    InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");

    // get the contents
    InputStream fileReadUpdate = updateHandle.get();

    String readContentUpdate = convertInputStreamToString(fileReadUpdate);

    // get xml document for expected result
    Document expectedDocUpdate = expectedXMLDocument(updateFilename);

    // convert actual string to xml doc
    Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

    assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

    // delete the document
    deleteDocument(client, uri + filename, "XML");

    // read the deleted document
    String exception = "";
    try
    {
      readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release client
    client.release();
  }

  @Test
  public void testTextCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    String filename = "text-original.txt";
    String uri = "/write-text-inputstreamhandle/";

    System.out.println("Running testTextCRUD");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "Text");

    // read docs
    InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

    // get the contents
    InputStream fileRead = contentHandle.get();

    String readContent = convertInputStreamToString(fileRead);

    String expectedContent = "hello world, welcome to java API";

    assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());

    // update the doc
    // acquire the content for update
    String updateFilename = "text-updated.txt";
    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "Text");

    // read the document
    InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

    // get the contents
    InputStream fileReadUpdate = updateHandle.get();

    String readContentUpdate = convertInputStreamToString(fileReadUpdate);

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

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release client
    client.release();
  }

  @Test
  public void testJsonCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    String filename = "json-original.json";
    String uri = "/write-json-inputstreamhandle/";

    System.out.println("Running testJsonCRUD");

    ObjectMapper mapper = new ObjectMapper();

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "JSON");

    // read docs
    InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

    // get the contents
    InputStream fileRead = contentHandle.get();
    JsonNode readContent = mapper.readTree(fileRead);

    // get expected contents
    JsonNode expectedContent = expectedJSONDocument(filename);

    assertTrue("Write JSON document difference", readContent.equals(expectedContent));

    // update the doc
    // acquire the content for update
    String updateFilename = "json-updated.json";
    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "JSON");

    // read the document
    InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

    // get the contents
    InputStream fileReadUpdate = updateHandle.get();

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

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release client
    client.release();
  }

  @Test
  public void testBinaryCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    String filename = "Pandakarlino.jpg";
    String uri = "/write-bin-inputstreamhandle/";

    System.out.println("Running testBinaryCRUD");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "Binary");

    // read docs
    InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

    // get the contents
    InputStream fileRead = contentHandle.get();

    // get the binary size
    int size = getBinarySize(fileRead);
    int expectedSize = 34543;

    assertEquals("Binary size difference", expectedSize, size);

    // update the doc
    // acquire the content for update
    String updateFilename = "mlfavicon.png";
    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "Binary");

    // read the document
    InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

    // get the contents
    InputStream fileReadUpdate = updateHandle.get();

    // get the binary size
    int sizeUpdate = getBinarySize(fileReadUpdate);
    int expectedSizeUpdate = 3322;

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

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
