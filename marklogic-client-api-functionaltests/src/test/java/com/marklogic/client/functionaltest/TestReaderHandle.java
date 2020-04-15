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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.ReaderHandle;

public class TestReaderHandle extends BasicJavaClientREST {
  private static String dbName = "WriteReaderHandleDB";
  private static String[] fNames = { "WriteReaderHandleDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("Running testXmlCRUD");

    String filename = "xml-original-test.xml";
    String uri = "/write-xml-readerhandle/";

    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write the doc
    writeDocumentReaderHandle(client, filename, uri, "XML");

    // read the document
    ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "XML");

    // access the document content
    Reader fileRead = readHandle.get();

    String readContent = convertReaderToString(fileRead);

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);

    // convert actual string to xml doc
    Document readDoc = convertStringToXMLDocument(readContent);

    assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // update the doc
    // acquire the content for update
    String updateFilename = "xml-updated-test.xml";
    updateDocumentReaderHandle(client, updateFilename, uri + filename, "XML");

    // read the document
    ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "XML");

    // access the document content
    Reader fileReadUpdate = updateHandle.get();

    String readContentUpdate = convertReaderToString(fileReadUpdate);

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
      readDocumentReaderHandle(client, uri + filename, "XML");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release the client
    client.release();
  }

  @Test
  public void testTextCRUD() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException
  {
    System.out.println("Running testTextCRUD");

    String filename = "text-original.txt";
    String uri = "/write-text-readerhandle/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write the doc
    writeDocumentReaderHandle(client, filename, uri, "Text");

    // read the document
    ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "Text");

    // access the document content
    Reader fileRead = readHandle.get();

    String expectedContent = "hello world, welcome to java API";

    String readContent = convertReaderToString(fileRead);

    assertEquals("Write Text document difference", expectedContent, readContent);

    // update the doc
    // acquire the content for update
    String updateFilename = "text-updated.txt";
    updateDocumentReaderHandle(client, updateFilename, uri + filename, "Text");

    // read the document
    ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "Text");

    // access the document content
    Reader fileReadUpdate = updateHandle.get();

    String readContentUpdate = convertReaderToString(fileReadUpdate);

    String expectedContentUpdate = "hello world, welcome to java API after new updates";

    assertEquals("Update Text document difference", expectedContentUpdate, readContentUpdate);

    // delete the document
    deleteDocument(client, uri + filename, "Text");

    // read the deleted document
    String exception = "";
    try
    {
      readDocumentReaderHandle(client, uri + filename, "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release the client
    client.release();
  }

  @Test
  public void testJsonCRUD() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException
  {
    System.out.println("Running testJsonCRUD");

    String filename = "json-original.json";
    String uri = "/write-json-readerhandle/";

    ObjectMapper mapper = new ObjectMapper();

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write the doc
    writeDocumentReaderHandle(client, filename, uri, "JSON");

    // read the document
    ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "JSON");

    // access the document content
    Reader fileRead = readHandle.get();
    JsonNode readContent = mapper.readTree(fileRead);

    // get expected contents
    JsonNode expectedContent = expectedJSONDocument(filename);

    assertTrue("Write JSON document difference", readContent.equals(expectedContent));

    // update the doc
    // acquire the content for update
    String updateFilename = "json-updated.json";
    updateDocumentReaderHandle(client, updateFilename, uri + filename, "JSON");

    // read the document
    ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "JSON");

    // access the document content
    Reader fileReadUpdate = updateHandle.get();
    JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

    // get expected contents
    JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

    assertTrue("Write JSON document difference", readContentUpdate.equals(expectedContentUpdate));

    // delete the document
    deleteDocument(client, uri + filename, "JSON");

    // read the deleted document
    String exception = "";
    try
    {
      readDocumentReaderHandle(client, uri + filename, "JSON");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "Could not read non-existent document";
    boolean documentIsDeleted = exception.contains(expectedException);
    assertTrue("Document is not deleted", documentIsDeleted);

    // release the client
    client.release();
  }

  // @Test Issue 225
  public void testBinaryCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    String filename = "Pandakarlino.jpg";
    String uri = "/write-bin-filehandle/";

    System.out.println("Running testBinaryCRUD");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());
    writeDocumentReaderHandle(client, filename, uri, "Binary");

    // read docs
    ReaderHandle contentHandle = readDocumentReaderHandle(client, uri + filename, "Binary");

    // get the contents
    Reader fileRead = contentHandle.get();

    // get the binary size
    long size = convertReaderToString(fileRead).length();
    // long expectedSize = 17031;

    boolean expectedSize;
    if (size >= 16800 && size <= 17200)
    {
      expectedSize = true;
      assertTrue("Binary size difference", expectedSize);
    }

    // update the doc
    // acquire the content for update
    String updateFilename = "mlfavicon.png";
    updateDocumentReaderHandle(client, updateFilename, uri + filename, "Binary");

    // read the document
    ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "Binary");

    // get the contents
    Reader fileReadUpdate = updateHandle.get();

    // get the binary size
    long sizeUpdate = convertReaderToString(fileReadUpdate).length();

    boolean expectedSizeUpdate;
    if (sizeUpdate >= 55000 && sizeUpdate <= 57000)
    {
      expectedSizeUpdate = true;
      assertTrue("Binary size difference", expectedSizeUpdate);
    }

    // delete the document
    deleteDocument(client, uri + filename, "Binary");

    // read the deleted document
    String exception = "";
    try
    {
      readDocumentReaderHandle(client, uri + filename, "Binary");
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-bin-filehandle/Pandakarlino.jpg";
    assertEquals("Document is not deleted", expectedException, exception);

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
