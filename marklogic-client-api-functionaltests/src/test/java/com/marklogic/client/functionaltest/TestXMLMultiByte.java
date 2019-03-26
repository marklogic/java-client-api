/*
 * Copyright 2014-2019 MarkLogic Corporation
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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;

public class TestXMLMultiByte extends BasicJavaClientREST {

  private static String dbName = "XMLMultiByteDB";
  private static String[] fNames = { "XMLMultiByteDB-1" };

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testXmlMultibyte() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException
  {
    String filename = "multibyte-original.xml";
    String uri = "/write-xml-multibyte/";

    System.out.println("Running testXmlMultibyte");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // read docs
    DOMHandle contentHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");

    // get the contents
    Document readDoc = contentHandle.get();

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);

    assertEquals("Incorrect yen value", readDoc.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect haba value", readDoc.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect chinese value", readDoc.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect trademark value", readDoc.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue());

    // update the doc
    // acquire the content for update
    String updateFilename = "multibyte-updated.xml";
    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");

    // read the document
    DOMHandle updateHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");

    // get the contents
    Document readDocUpdate = updateHandle.get();

    // get xml document for expected result
    Document expectedDocUpdate = expectedXMLDocument(updateFilename);

    assertEquals("Incorrect yen value", readDocUpdate.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect haba value", readDocUpdate.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect chinese value", readDocUpdate.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect trademark value", readDocUpdate.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue());

    assertEquals("Incorrect kanji value", readDocUpdate.getElementsByTagName("kanji").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("kanji").item(0).getFirstChild().getNodeValue());

    // delete the document
    deleteDocument(client, uri + filename, "XML");

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
