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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;

public class TestBug18026 extends BasicJavaClientREST {

  private static String dbName = "Bug18026DB";
  private static String[] fNames = { "Bug18026DB-1" };

  private XpathEngine xpather;

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testBug18026() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    String filename = "xml-original.xml";
    String uri = "/write-buffer/";
    byte[] before = null;
    byte[] after = null;
    String strBefore = null;
    String strAfter = null;

    System.out.println("Running testBug18026");

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalize(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("rapi", "http://marklogic.com/rest-api");
    namespaces.put("prop", "http://marklogic.com/xdmp/property");
    namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
    namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

    xpather = XMLUnit.newXpathEngine();
    xpather.setNamespaceContext(namespaceContext);

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    Document readDoc = expectedXMLDocument(filename);

    DOMHandle dHandle = new DOMHandle();
    dHandle.set(readDoc);
    before = dHandle.toBuffer();
    strBefore = new String(before);
    System.out.println("Before: " + strBefore);

    // write doc
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    String docId = uri + filename;
    docMgr.write(docId, dHandle);

    // read doc
    docMgr.read(docId, dHandle);
    dHandle.get();

    after = dHandle.toBuffer();
    strAfter = new String(after);
    System.out.println("After: " + strAfter);

    assertXMLEqual("Buffer is not the same", strBefore, strAfter);

    // release client
    client.release();
  }

  @Test
  public void testBug18026WithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    String filename = "json-original.json";
    String uri = "/write-buffer/";
    byte[] before = null;
    byte[] after = null;
    String strBefore = null;
    String strAfter = null;

    System.out.println("Running testBug18026WithJson");

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalize(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("rapi", "http://marklogic.com/rest-api");
    namespaces.put("prop", "http://marklogic.com/xdmp/property");
    namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
    namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

    xpather = XMLUnit.newXpathEngine();
    xpather.setNamespaceContext(namespaceContext);

    ObjectMapper mapper = new ObjectMapper();

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    InputStreamHandle isHandle = new InputStreamHandle();
    isHandle.set(inputStream);
    before = isHandle.toBuffer();
    strBefore = new String(before);
    System.out.println("Before: " + strBefore);

    JsonNode contentBefore = mapper.readValue(strBefore, JsonNode.class);

    // write doc
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    String docId = uri + filename;
    docMgr.write(docId, isHandle);

    // read doc
    docMgr.read(docId, isHandle);
    isHandle.get();

    after = isHandle.toBuffer();
    strAfter = new String(after);
    System.out.println("After: " + strAfter);

    JsonNode contentAfter = mapper.readValue(strAfter, JsonNode.class);

    assertTrue("Buffered JSON document difference", contentBefore.equals(contentAfter));

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
