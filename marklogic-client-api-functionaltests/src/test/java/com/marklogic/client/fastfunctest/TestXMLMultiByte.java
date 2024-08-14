/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestXMLMultiByte extends AbstractFunctionalTest {

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

    assertEquals( readDoc.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue());

    assertEquals( readDoc.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue());

    assertEquals( readDoc.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue(),
        expectedDoc.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue());

    assertEquals( readDoc.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue(),
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

    assertEquals( readDocUpdate.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("yen").item(0).getFirstChild().getNodeValue());

    assertEquals( readDocUpdate.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("haba").item(0).getFirstChild().getNodeValue());

    assertEquals( readDocUpdate.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("chinese").item(0).getFirstChild().getNodeValue());

    assertEquals( readDocUpdate.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("trademark").item(0).getFirstChild().getNodeValue());

    assertEquals( readDocUpdate.getElementsByTagName("kanji").item(0).getFirstChild().getNodeValue(),
        expectedDocUpdate.getElementsByTagName("kanji").item(0).getFirstChild().getNodeValue());

    // delete the document
    deleteDocument(client, uri + filename, "XML");

    // release client
    client.release();
  }
}
