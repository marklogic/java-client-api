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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SourceHandle;

public class TestSourceHandle extends BasicJavaClientREST {

  private static String dbName = "SourceHandleDB";
  private static String[] fNames = { "SourceHandleDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException
  {
    String filename = "xml-original-test.xml";
    String uri = "/write-xml-sourcehandle/";

    System.out.println("Running testXmlCRUD");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // read docs
    SourceHandle contentHandle = readDocumentUsingSourceHandle(client, uri + filename, "XML");

    // get the contents
    Source fileRead = contentHandle.get();

    String readContent = convertSourceToString(fileRead);

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
    SourceHandle updateHandle = readDocumentUsingSourceHandle(client, uri + filename, "XML");

    // get the contents
    Source fileReadUpdate = updateHandle.get();

    String readContentUpdate = convertSourceToString(fileReadUpdate);

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
    contentHandle.close();
    updateHandle.close();
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}