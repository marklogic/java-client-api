/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.extra;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extra.dom4j.DOM4JHandle;
import com.marklogic.client.test.Common;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DOM4JHandleTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testReadWrite() throws SAXException, IOException {
    // create an identifier for the database document
    String docId = "/example/jdom-test.xml";

    // create a manager for XML database documents
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    DocumentFactory factory = new DocumentFactory();

    // create a dom4j document
    Document writeDocument = factory.createDocument();
    Element root = factory.createElement("root");
    root.attributeValue("foo", "bar");
    root.add(factory.createElement("child"));
    root.addText("mixed");
    writeDocument.setRootElement(root);

    // create a handle for the dom4j document
    DOM4JHandle writeHandle = new DOM4JHandle(writeDocument);

    // write the document to the database
    docMgr.write(docId, writeHandle);

    // create a handle to receive the database content as a dom4j document
    DOM4JHandle readHandle = new DOM4JHandle();

    // read the document content from the database as a dom4j document
    docMgr.read(docId, readHandle);

    // access the document content
    Document readDocument = readHandle.get();
    assertNotNull( readDocument);
    assertXMLEqual("dom4j document not equal",
      writeDocument.asXML(), readDocument.asXML());

    // delete the document
    docMgr.delete(docId);
  }
}
