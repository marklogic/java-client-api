/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DeleteSearchTest {
  private static final String directory = "/delete/test/";
  private static final String filename = "testWrite1.xml";
  private static final String docId = directory + filename;
  private static DatabaseClient client = Common.connect();

  @BeforeAll
  public static void beforeClass() throws Exception {
    writeDoc();
  }

  public static void writeDoc() throws Exception {
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

    @SuppressWarnings("unused")
    String domString = ((DOMImplementationLS) factory.newDocumentBuilder()
      .getDOMImplementation()).createLSSerializer().writeToString(domDocument);

    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    docMgr.write(docId, new DOMHandle().with(domDocument));
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void test_A_Delete() {
    GenericDocumentManager docMgr = client.newDocumentManager();
    DocumentDescriptor desc = docMgr.exists(docId);
    assertNotNull(desc);
    assertEquals(desc.getUri(), docId);

    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition qdef = queryMgr.newDeleteDefinition();
    qdef.setDirectory(directory);

    queryMgr.delete(qdef);

    desc = docMgr.exists(docId);
    assertNull(desc);
  }

  @Test
  public void test_B_RuntimeDb() throws Exception {
    client = Common.newEvalClient("Documents");
    writeDoc();
    test_A_Delete();
  }
}
