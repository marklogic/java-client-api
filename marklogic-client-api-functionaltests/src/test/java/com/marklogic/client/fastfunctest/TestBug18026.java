/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class TestBug18026 extends AbstractFunctionalTest {

  private XpathEngine xpather;

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

    assertTrue( contentBefore.equals(contentAfter));

    // release client
    client.release();
  }

  @Test
  public void testBug19016() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBug19016");

    String[] filenames = { "bug19016.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)  {
      writeDocumentUsingInputStreamHandle(client, filename, "/bug19016/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("this\"is\"an%33odd string");

    String result = queryMgr.search(querydef, new StringHandle()).get();

    System.out.println(result);

    assertTrue( result.contains("<search:qtext>this\"is\"an%33odd string</search:qtext>"));

    // release client
    client.release();
  }
}
