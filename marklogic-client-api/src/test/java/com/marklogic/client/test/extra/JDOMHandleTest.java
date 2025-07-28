/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.extra;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.test.Common;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JDOMHandleTest {
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

    // create a JDOM document
    Document writeDocument = new Document();
    Element root = new Element("root");
    root.setAttribute("foo", "bar");
    root.addContent(new Element("child"));
    root.addContent("mixed");
    writeDocument.setRootElement(root);

    // create a handle for the JDOM document
    JDOMHandle writeHandle = new JDOMHandle(writeDocument);

    // write the JDOM document to the database
    docMgr.write(docId, writeHandle);

    // create a handle to receive the database content as a JDOM document
    JDOMHandle readHandle = new JDOMHandle();

    // read the document content from the database as a JDOM document
    docMgr.read(docId, readHandle);

    // access the document content
    Document readDocument = readHandle.get();
    assertNotNull( readDocument);
    assertXMLEqual("JDOM document not equal",
      writeHandle.toString(), readHandle.toString());

    // delete the document
    docMgr.delete(docId);
  }
}
