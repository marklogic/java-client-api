/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.io.*;
import com.marklogic.client.test.util.Referred;
import com.marklogic.client.test.util.Refers;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.*;

public class BufferableHandleTest {
  static private XpathEngine xpather;

  @BeforeAll
  public static void beforeClass() {
    Common.connect();

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalize(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

    Map<String,String> namespaces = new HashMap<>();
    namespaces.put("rapi", "http://marklogic.com/rest-api");
    namespaces.put("prop", "http://marklogic.com/xdmp/property");
    namespaces.put("xs",   "http://www.w3.org/2001/XMLSchema");
    namespaces.put("xsi",  "http://www.w3.org/2001/XMLSchema-instance");

    SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

    xpather = XMLUnit.newXpathEngine();
    xpather.setNamespaceContext(namespaceContext);
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testReadWrite()
    throws JAXBException, ParserConfigurationException, SAXException, IOException, XpathException
  {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);

    Document domDocument = factory.newDocumentBuilder().newDocument();
    Element root = domDocument.createElement("root");
    root.setAttribute("xml:lang", "en");
    root.setAttribute("foo", "bar");
    root.appendChild(domDocument.createElement("child"));
    root.appendChild(domDocument.createTextNode("mixed"));
    domDocument.appendChild(root);

    byte[] before    = null;
    byte[] after     = null;
    String beforeStr = null;

    DOMHandle domH = new DOMHandle().with(domDocument);
    before = domH.toBuffer();
    beforeStr = new String(before);

    BytesHandle bytesH = new BytesHandle();
    bytesH.fromBuffer(before);
    after = bytesH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("Bytes buffering",beforeStr,new String(after));

    domH.fromBuffer(before);
    after = domH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("DOM buffering",beforeStr,new String(after));

    InputSourceHandle inputSourceH = new InputSourceHandle();
    inputSourceH.fromBuffer(before);
    after = inputSourceH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("InputSource buffering",beforeStr,new String(after));

    InputStreamHandle inputStreamH = new InputStreamHandle();
    inputStreamH.fromBuffer(before);
    after = inputStreamH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("InputStream buffering",beforeStr,new String(after));

    ReaderHandle readerH = new ReaderHandle();
    readerH.fromBuffer(before);
    after = readerH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("Reader buffering",beforeStr,new String(after));

    SourceHandle sourceH = new SourceHandle();
    sourceH.fromBuffer(before);
    after = sourceH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("Source buffering",beforeStr,new String(after));

    StringHandle stringH = new StringHandle();
    stringH.fromBuffer(before);
    after = stringH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("String buffering",beforeStr,new String(after));

    XMLEventReaderHandle eventReaderH = new XMLEventReaderHandle();
    eventReaderH.fromBuffer(before);
    after = eventReaderH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("EventReader buffering",beforeStr,new String(after));

    XMLStreamReaderHandle streamReaderH = new XMLStreamReaderHandle();
    streamReaderH.fromBuffer(before);
    after = streamReaderH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("StreamReader buffering",beforeStr,new String(after));

    Refers refers = new Refers();
    refers.child = new Referred();

    JAXBContext jaxbContext = JAXBContext.newInstance(Refers.class);
    @SuppressWarnings({ "rawtypes", "unchecked" })
    JAXBHandle jaxbH = new JAXBHandle(jaxbContext).with(refers);

    before = jaxbH.toBuffer();
    beforeStr = new String(before);
    jaxbH.fromBuffer(before);
    after = jaxbH.toBuffer();
    assertNotNull(after);
    assertXMLEqual("JAXB buffering",beforeStr,new String(after));

    String metadataText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
      "<rapi:metadata uri=\"/test/testMetadataXML1.xml\" xsi:schemaLocation=\"http://marklogic.com/rest-api restapi.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:prop=\"http://marklogic.com/xdmp/property\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
        "<rapi:collections>"+
          "<rapi:collection>/document/collection1</rapi:collection>"+
          "<rapi:collection>/document/collection2</rapi:collection>"+
        "</rapi:collections>"+
        "<rapi:permissions>"+
          "<rapi:permission>"+
            "<rapi:role-name>app-user</rapi:role-name>"+
            "<rapi:capability>read</rapi:capability>"+
            "<rapi:capability>update</rapi:capability>"+
          "</rapi:permission>"+
        "</rapi:permissions>"+
        "<prop:properties>"+
          "<first xsi:type=\"xs:string\">value one</first>"+
          "<second xsi:type=\"xs:string\">2</second>"+
          "<third>"+
            "<third.first>value third one</third.first>"+
            "<third.second>3.2</third.second>"+
          "</third>"+
        "</prop:properties>"+
        "<rapi:quality>3</rapi:quality>"+
        "<rapi:metadata-values>"+
        "  <rapi:metadata-value key=\"key1\">value1</rapi:metadata-value>"+
        "  <rapi:metadata-value key=\"number1\">10</rapi:metadata-value>"+
        "</rapi:metadata-values>"+
      "</rapi:metadata>";

    DocumentMetadataHandle metadataH = new DocumentMetadataHandle();
    beforeStr = metadataText;
    before = metadataText.getBytes();

    metadataH.fromBuffer(before);
    after = metadataH.toBuffer();
    assertNotNull(after);
    String afterStr = new String(after);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:collections/rapi:collection[string(.) = '/document/collection1']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);

    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:collections/rapi:collection[string(.) = '/document/collection2']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:permissions/rapi:permission/rapi:role-name[string(.) = 'app-user']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:permissions/rapi:permission/rapi:capability[string(.) = 'read']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:permissions/rapi:permission/rapi:capability[string(.) = 'update']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/prop:properties/first[string(.) = 'value one']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/prop:properties/second[string(.) = '2']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/prop:properties/third/third.first[string(.) = 'value third one']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/prop:properties/third/third.second[string(.) = '3.2']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:metadata-values/rapi:metadata-value[@key = 'key1'][string(.) = 'value1']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:metadata-values/rapi:metadata-value[@key = 'number1'][string(.) = '10']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:metadata/rapi:quality[string(.) = '3']",
      XMLUnit.buildControlDocument(afterStr)
    ).getLength() == 1);
  }
}
