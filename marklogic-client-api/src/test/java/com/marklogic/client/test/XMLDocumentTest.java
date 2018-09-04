/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.io.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.XMLDocumentManager.DocumentRepair;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import java.util.Map;

public class XMLDocumentTest {

  private static ExtensionLibrariesManager libsMgr = null;

  @BeforeClass
  public static void beforeClass() {
    Common.connectAdmin();
    // get a manager
    libsMgr = Common.adminClient
      .newServerConfigManager().newExtensionLibrariesManager();

    // write XQuery file to the modules database
    libsMgr.write("/ext/my-lib.xqy", new FileHandle(
      new File("src/test/resources/my-lib.xqy")).withFormat(Format.TEXT));

    Common.connect();
  }
  @AfterClass
  public static void afterClass() {
    libsMgr.delete("/ext/my-lib.xqy");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testReadWrite()
    throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException,
           TransformerFactoryConfigurationError, XMLStreamException
  {
    String docId = "/test/testWrite1.xml";

    Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = domDocument.createElement("root");
    root.setAttribute("xml:lang", "en");
    root.setAttribute("foo", "bar");
    root.appendChild(domDocument.createElement("child"));
    root.appendChild(domDocument.createTextNode("mixed"));
    domDocument.appendChild(root);

    String domString = Common.testDocumentToString(domDocument);

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write(docId, new DOMHandle().with(domDocument));

    String docText = docMgr.read(docId, new StringHandle()).get();
    assertNotNull("Read null string for XML content",docText);
    assertXMLEqual("Failed to read XML document as String",domString,docText);

    Document readDoc = docMgr.read(docId, new DOMHandle()).get();
    assertNotNull("Read null document for XML content",readDoc);
    assertXMLEqual("Failed to read XML document as DOM",Common.testDocumentToString(readDoc),domString);

    String docId2 = "/test/testWrite2.xml";

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    SourceHandle sourceHandle = new SourceHandle();
    sourceHandle.setTransformer(transformer);
    docMgr.write(docId2, docMgr.read(docId, sourceHandle));
    docText = docMgr.read(docId2, new StringHandle()).get();
    assertNotNull("Read null document for transform result",docText);
    assertXMLEqual("Transform result not equivalent to source",domString,docText);

    InputSourceHandle saxHandle = new InputSourceHandle();
    saxHandle.set(new InputSource(new StringReader(domString)));
    docMgr.write(docId, saxHandle);
    docText = docMgr.read(docId2, new StringHandle()).get();
    assertNotNull("Read null document for SAX writer",docText);
    assertXMLEqual("Failed to read XML document as DOM",domString,docText);

    final Map<String,Integer> counter = new HashMap<>();
    counter.put("elementCount",0);
    counter.put("attributeCount",0);
    DefaultHandler handler = new DefaultHandler() {
      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) {
        counter.put("elementCount",counter.get("elementCount") + 1);
        if (attributes != null) {
          int elementAttributeCount = attributes.getLength();
          if (elementAttributeCount > 0)
            counter.put("attributeCount",counter.get("attributeCount") + elementAttributeCount);
        }
      }
    };
    docMgr.read(docId, saxHandle).process(handler);
    assertTrue("Failed to process XML document with SAX",
      counter.get("elementCount") == 2 && counter.get("attributeCount") == 2);

    XMLStreamReaderHandle streamReaderHandle = docMgr.read(docId, new XMLStreamReaderHandle());
    XMLStreamReader streamReader = streamReaderHandle.get();
    int elementCount = 0;
    int attributeCount = 0;
    while (streamReader.hasNext()) {
      if (streamReader.next() != XMLStreamReader.START_ELEMENT)
        continue;
      elementCount++;
      int elementAttributeCount = streamReader.getAttributeCount();
      if (elementAttributeCount > 0)
        attributeCount += elementAttributeCount;
    }
    streamReaderHandle.close();
    assertTrue("Failed to process XML document with StAX stream reader",
      elementCount == 2 && attributeCount == 2);

    XMLEventReader eventReader = docMgr.read(docId, new XMLEventReaderHandle()).get();
    elementCount = 0;
    attributeCount = 0;
    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();
      if (!event.isStartElement())
        continue;
      StartElement element = event.asStartElement();
      elementCount++;
      Iterator<Attribute> attributes = element.getAttributes();
      while (attributes.hasNext()) {
        attributes.next();
        attributeCount++;
      }
    }
    eventReader.close();
    assertTrue("Failed to process XML document with StAX event reader",
      elementCount == 2 && attributeCount == 2);

    String truncatedDoc ="<root><poorlyFormed></root>";
    docMgr.setDocumentRepair(DocumentRepair.FULL);
    docMgr.write(docId, new StringHandle().with(truncatedDoc));

    docMgr.setDocumentRepair(DocumentRepair.NONE);
    boolean threwException = false;
    try {
      docMgr.write(docId, new StringHandle().with(truncatedDoc));
    } catch(RuntimeException ex) {
      threwException = true;
    }
    assertTrue("Expected failure on truncated XML document with no repair", threwException);
  }

  @Test
  public void testValidate()
    throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException,
           TransformerFactoryConfigurationError, XMLStreamException
  {
    String docId = "/test/testWrite1.xml";

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.setDocumentRepair(DocumentRepair.NONE);

    String doc = "<?xml version='1.0' encoding='UTF-8'?>" +
      "<root foo='bar'><child/>mixed</root>";

    InputSourceHandle saxHandle = new InputSourceHandle();

    // throw exceptions for parse errors
    saxHandle.setErrorHandler(new InputSourceHandle.DraconianErrorHandler());

    String validSchema =
      "<?xml version='1.0' encoding='UTF-8'?>" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' " +
          "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
          "xsi:schemaLocation='http://www.w3.org/2001/XMLSchema XMLSchema.xsd'>" +
        "<xs:element name='root'>" +
          "<xs:complexType mixed='true'>" +
            "<xs:choice minOccurs='0' maxOccurs='unbounded'>" +
              "<xs:element name='child'/>" +
            "</xs:choice>" +
            "<xs:attribute name='foo' type='xs:string' use='optional'/>" +
          "</xs:complexType>" +
        "</xs:element>" +
      "</xs:schema>";

    Schema schema = factory.newSchema(new StreamSource(new StringReader(validSchema)));

    saxHandle.setDefaultWriteSchema(schema);

    if (docMgr.exists(docId) != null) {
      docMgr.delete(docId);
    }

    docMgr.write(docId, saxHandle.with(new InputSource(new StringReader(doc))));

    DocumentDescriptor docDesc = docMgr.exists(docId);
    assertTrue("Write failed with valid SAX", docDesc != null);

    docMgr.delete(docId);

    String invalidSchema =
      "<?xml version='1.0' encoding='UTF-8'?>" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' " +
          "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
          "xsi:schemaLocation='http://www.w3.org/2001/XMLSchema XMLSchema.xsd'>" +
        "<xs:element name='root'>" +
          "<xs:complexType>" +
            "<xs:attribute name='foo' type='xs:string' use='optional'/>" +
          "</xs:complexType>" +
        "</xs:element>" +
      "</xs:schema>";

    schema = factory.newSchema(new StreamSource(new StringReader(invalidSchema)));

    saxHandle.setDefaultWriteSchema(schema);

    boolean threwException = false;
    try {
      docMgr.write(docId, saxHandle.with(new InputSource(new StringReader(doc))));
    } catch(RuntimeException ex) {
      threwException = true;
    }
    assertTrue("Expected failure for invalid SAX", threwException);

    // if the error occurs in the root element, the server writes an empty document
    docDesc = docMgr.exists(docId);
    if (docDesc != null) {
      docMgr.delete(docId);
    }
  }

  @Test
  public void testStAXWrite() throws XMLStreamException, SAXException, IOException {
    String docId = "/test/testWrite1.xml";

    String docIn =
      "<?xml version='1.0'?>" +
      "<def:default" +
      " xmlns:def='http://marklogic.com/example/ns/default'" +
      " xmlns:sp='http://marklogic.com/example/ns/specified'" +
      " xmlns:un='http://marklogic.com/example/ns/unspecified'" +
      ">" +
      "<sp:specified>first value</sp:specified>" +
      "<un:unspecified>second value</un:unspecified>" +
      "</def:default>";

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    XMLStreamReaderHandle streamHandle = new XMLStreamReaderHandle();
    streamHandle.set(
      streamHandle.getFactory().createXMLStreamReader(
        new StringReader(docIn)
      )
    );

    docMgr.write(docId, streamHandle);

    String docOut = docMgr.read(docId, new StringHandle()).get();
    assertNotNull("Wrote null document for StAX stream", docOut);
    assertXMLEqual("Failed to write StAX stream", docIn, docOut);

    XMLEventReaderHandle eventHandle = new XMLEventReaderHandle();
    eventHandle.set(
      eventHandle.getFactory().createXMLEventReader(
        new StringReader(docIn)
      )
    );

    docMgr.write(docId, eventHandle);

    docOut = docMgr.read(docId, new StringHandle()).get();
    assertNotNull("Wrote null document for StAX events", docOut);
    assertXMLEqual("Failed to write StAX events", docIn, docOut);
  }

  @Test
  public void testPatch() throws Exception {
    String docId = "/test/testWrite1.xml";

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment(
      "/root/firstChild/firstChildOfFirstChild", Position.BEFORE, Cardinality.ONE_OR_MORE,
      "<newFirstChildOfFirstChild/>"
    );
    patchBldr.insertFragment(
      "/root/firstChild", Position.LAST_CHILD, "<lastChildOfFirstChild/>"
    );
    patchBldr.replaceFragment("/root/secondChild", "<replacedSecondChild/>");
    patchBldr.replaceValue("/root/thirdChild", "new value");
    patchBldr.delete("fourthChild");
    patchBldr.replaceApply("fifthChild", Cardinality.ONE, patchBldr.call().multiply(3));

    patchBldr.library("http://marklogic.com/java-unit-test/my-lib",
      "/ext/my-lib.xqy");
    patchBldr.replaceApply("/root/sixthChild",
      patchBldr.call().applyLibraryValues("getMin", 18, 21));

    DocumentPatchHandle patchHandle = patchBldr.build();

    for (int i=0; i < 2; i++) {
      Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element root    = domDocument.createElement("root");
      Element firstChild = domDocument.createElement("firstChild");
      Element firstChildOfFirstChild = domDocument.createElement("firstChildOfFirstChild");
      firstChild.appendChild(firstChildOfFirstChild);
      root.appendChild(firstChild);
      Element secondChild = domDocument.createElement("secondChild");
      root.appendChild(secondChild);
      Element thirdChild = domDocument.createElement("thirdChild");
      thirdChild.setTextContent("old value");
      root.appendChild(thirdChild);
      Element fourthChild = domDocument.createElement("fourthChild");
      root.appendChild(fourthChild);
      Element fifthChild = domDocument.createElement("fifthChild");
      fifthChild.setTextContent("5");
      root.appendChild(fifthChild);
      Element sixthChild = domDocument.createElement("sixthChild");
      sixthChild.setTextContent("31");
      root.appendChild(sixthChild);
      domDocument.appendChild(root);

      docMgr.write(docId, new DOMHandle().with(domDocument));

      String patchType = null;
      switch (i) {
        case 0:
          patchType = "built";
          docMgr.patch(docId, patchHandle);
          break;
        case 1:
          patchType = "raw";
          docMgr.clearMetadataCategories();
          docMgr.patch(docId, new StringHandle(patchHandle.toString()));
          docMgr.setMetadataCategories(Metadata.ALL);
          break;
        default:
          throw new Exception("unknown patch loop value");
      }

      firstChild.insertBefore(
        domDocument.createElement("newFirstChildOfFirstChild"), firstChildOfFirstChild
      );
      firstChild.appendChild(domDocument.createElement("lastChildOfFirstChild"));
      root.replaceChild(domDocument.createElement("replacedSecondChild"), secondChild);
      thirdChild.setTextContent("new value");
      fifthChild.setTextContent("15");
      sixthChild.setTextContent("18");
      root.removeChild(fourthChild);

      String expected = Common.testDocumentToString(domDocument);

      String actual   = docMgr.read(docId, new StringHandle()).get();
      assertNotNull("Read null string for "+patchType+" patched XML content",actual);
      assertXMLEqual("Unexpected result for "+patchType+" patched XML document",expected,actual);
    }

    String before =
      "<r:root xmlns:r=\"root.org\">" +
      "<t:target xmlns:t=\"target.org\">";
    String after =
      "</t:target>" +
      "</r:root>";

    EditableNamespaceContext namespaces = new EditableNamespaceContext();
    namespaces.put("r", "root.org");
    namespaces.put("t", "target.org");
    namespaces.put("n", "new.org");

    patchBldr = docMgr.newPatchBuilder();
    patchBldr.setNamespaces(namespaces);
    String inserted = "<n:new xmlns:n=\"new.org\"/>";
    patchBldr.insertFragment(
      "/r:root/t:target", Position.LAST_CHILD, inserted
    );
    patchHandle = patchBldr.build();

    String expected = before+inserted+after;

    for (int i=0; i < 2; i++) {
      docMgr.write(docId, new StringHandle(before+after));

      String patchType = null;
      switch (i) {
        case 0:
          patchType = "built";
          docMgr.patch(docId, patchHandle);
          break;
        case 1:
          patchType = "raw";
          docMgr.patch(docId, new StringHandle(patchHandle.toString()));
          break;
        default:
          throw new Exception("unknown namespaced patch loop value");
      }

      String actual = docMgr.read(docId, new StringHandle()).get();
      assertXMLEqual("Unexpected result for "+patchType+" patched namespaced XML document",expected,actual);
    }

    docMgr.delete(docId);
  }
}
