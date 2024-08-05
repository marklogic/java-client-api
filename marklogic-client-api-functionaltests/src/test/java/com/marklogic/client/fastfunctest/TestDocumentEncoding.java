/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDocumentEncoding extends AbstractFunctionalTest
{
  @Test
  public void testEncoding() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, ParserConfigurationException
  {
    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    DOMImplementation impl = builder.getDOMImplementation();

    Document doc = impl.createDocument(null, null, null);
    Element e1 = doc.createElement("howto");
    doc.appendChild(e1);
    Element e2 = doc.createElement("java");
    e1.appendChild(e2);
    e2.setAttribute("url", "http://www.rgagnon.com/howto.html");
    Text text = doc.createTextNode("漢字");
    e2.appendChild(text);

    // transform the Document into a String
    Source domSource = new DOMSource(doc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING, "Cp1252");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);

    String xml = sw.toString();
    System.out.println(xml);

    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    StringHandle writeHandle = new StringHandle();
    writeHandle.set(xml);
    docMgr.write("/doc/foo.xml", writeHandle);

    System.out.println(docMgr.read("/doc/foo.xml", new StringHandle()).get());
    int length1 = docMgr.read("/doc/foo.xml", new BytesHandle()).get().length;
    System.out.println(length1);

    // ************************

    DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder2 = factory2.newDocumentBuilder();
    DOMImplementation impl2 = builder2.getDOMImplementation();

    Document doc2 = impl2.createDocument(null, null, null);
    Element x1 = doc2.createElement("howto");
    doc2.appendChild(x1);
    Element x2 = doc2.createElement("java");
    x1.appendChild(x2);
    x2.setAttribute("url", "http://www.rgagnon.com/howto.html");
    Text text2 = doc2.createTextNode("漢字");
    x2.appendChild(text2);

    Source domSource2 = new DOMSource(doc2);
    TransformerFactory tf2 = TransformerFactory.newInstance();
    Transformer transformer2 = tf2.newTransformer();
    transformer2.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer2.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer2.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
    java.io.StringWriter sw2 = new java.io.StringWriter();
    StreamResult sr2 = new StreamResult(sw2);
    transformer2.transform(domSource2, sr2);
    String xml2 = sw2.toString();

    System.out.println("*********** UTF-8 ************");
    System.out.println(xml2);

    StringHandle writeHandle2 = new StringHandle();
    writeHandle2.set(xml2);
    docMgr.write("/doc/bar.xml", writeHandle2);
    System.out.println(docMgr.read("/doc/bar.xml", new StringHandle()).get());
    int length2 = docMgr.read("/doc/bar.xml", new BytesHandle()).get().length;
    System.out.println(length2);

    assertEquals(length1, length2);

    // **************************

    client.release();
  }
}
