/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.impl.OutputStreamTee;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.util.RequestLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class RequestLoggerTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testCopyTee() throws IOException {
    String expectedString = "first line\nsecond line\n";

    ByteArrayOutputStream out = null;
    RequestLogger logger = null;
    String outString = null;

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);

    StringReader mainReader = new StringReader(expectedString);
    Reader copyReader = logger.copyContent(mainReader);
    String copyString = Common.readerToString(copyReader);
    assertEquals( expectedString, copyString);
    outString = new String(out.toByteArray());
    assertEquals( expectedString, outString);

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);

    ByteArrayInputStream mainInputStream =
      new ByteArrayInputStream(expectedString.getBytes());
    InputStream copyInputStream = logger.copyContent(mainInputStream);
    byte[] copyBytes = Common.streamToBytes(copyInputStream);
    assertEquals( expectedString, new String(copyBytes));
    outString = new String(out.toByteArray());
    assertEquals( expectedString, outString);

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);

    ByteArrayOutputStream mainOutputStream = new ByteArrayOutputStream();
    OutputStream tee = new OutputStreamTee(mainOutputStream, out, Long.MAX_VALUE);
    tee.write(expectedString.getBytes());
    byte[] mainBytes = mainOutputStream.toByteArray();
    assertEquals( expectedString, new String(mainBytes));
    outString = new String(out.toByteArray());
    assertEquals( expectedString, outString);
    tee.close();
  }

  @Test
  public void testWriteReadLog() throws IOException, ParserConfigurationException {
    String docId = "/test/testWrite1.xml";

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    DocumentBuilder documentBldr = factory.newDocumentBuilder();
    Document domDocument = documentBldr.newDocument();
    Element root = domDocument.createElement("root");
    root.setAttribute("xml:lang", "en");
    root.setAttribute("foo", "bar");
    root.appendChild(domDocument.createElement("child"));
    root.appendChild(domDocument.createTextNode("mixed"));
    domDocument.appendChild(root);

    String domString = ((DOMImplementationLS) documentBldr
      .getDOMImplementation()).createLSSerializer().writeToString(domDocument)
      .replaceFirst("^<\\?xml(\\s+version=\"[^\"]*\"|\\s+encoding=\"[^\"]*\")*\\s*\\?>\\s*", "");

    ByteArrayOutputStream out = null;
    RequestLogger logger = null;
    String outString = null;

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    docMgr.startLogging(logger);

    docMgr.write(docId, new DOMHandle().with(domDocument));
    outString = new String(out.toByteArray());
    assertTrue( outString.contains(domString));

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    docMgr.startLogging(logger);

    String docText = docMgr.read(docId, new StringHandle()).get();
    outString = new String(out.toByteArray());
    assertTrue( outString.contains(docText));

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    docMgr.startLogging(logger);

    docMgr.exists(docId);
    outString = new String(out.toByteArray());
    assertTrue(  outString != null);
    if (outString != null)
      assertTrue( outString.length() > 0);

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    docMgr.startLogging(logger);

    docMgr.delete(docId);
    outString = new String(out.toByteArray());
    assertTrue( outString != null && outString.length() > 0);

  }

  @Test
  public void testSearchLog() {
    QueryManager qMgr = Common.client.newQueryManager();
    ByteArrayOutputStream out = null;
    RequestLogger logger = null;
    String outString = null;

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    qMgr.startLogging(logger);

    QueryDefinition querydef = qMgr.newStringDefinition();

    qMgr.search(querydef, new SearchHandle());
    outString = new String(out.toByteArray());
    assertTrue( outString != null && outString.length() > 0);

    out = new ByteArrayOutputStream();
    logger = Common.client.newLogger(out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);
    qMgr.startLogging(logger);

    DeleteQueryDefinition deleteDef = qMgr.newDeleteDefinition();
    deleteDef.setCollections("x");

    qMgr.delete(deleteDef);
    outString = new String(out.toByteArray());
    assertTrue( outString != null && outString.length() > 0);


  }

}
