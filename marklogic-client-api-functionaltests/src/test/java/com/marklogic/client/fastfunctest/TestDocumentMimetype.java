/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.FileHandle;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TestDocumentMimetype extends AbstractFunctionalTest {

  @Test
  public void testMatchedMimetypeOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMatchedMimetypeOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/xml");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document format
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "XML";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnknownMimetypeOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnknownMimetypeOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/x-excel");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "XML";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnmatchedMimetypeOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnmatchedMimetypeOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("image/svg+xml");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "XML";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnsupportedMimetypeOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnsupportedMimetypeOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/vnd.nokia.configuration-message");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "XML";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testMatchedMimetypeOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMatchedMimetypeOnJSON");

    String filename = "json-original.json";
    String uri = "/json-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/json");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document format
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "JSON";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnknownMimetypeOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnknownMimetypeOnJSON");

    String filename = "json-original.json";
    String uri = "/json-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("image/jpeg");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "JSON";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnmatchedMimetypeOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnmatchedMimetypeOnJSON");

    String filename = "json-original.json";
    String uri = "/json-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("text/html");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "JSON";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnsupportedMimetypeOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnsupportedMimetypeOnJSON");

    String filename = "json-original.json";
    String uri = "/json-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/vnd.nokia.configuration-message");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "JSON";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testMatchedMimetypeOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMatchedMimetypeOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("image/jpeg");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document format
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "BINARY";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnknownMimetypeOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnknownMimetypeOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/rtf");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "BINARY";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnmatchedMimetypeOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnmatchedMimetypeOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("text/rtf");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "BINARY";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnsupportedMimetypeOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnsupportedMimetypeOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/vnd.nokia.configuration-message");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "BINARY";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testMatchedMimetypeOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMatchedMimetypeOnText");

    String filename = "text-original.txt";
    String uri = "/txt-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("text/plain");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document format
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "TEXT";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnknownMimetypeOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnknownMimetypeOnText");

    String filename = "text-original.txt";
    String uri = "/txt-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/rtf");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "TEXT";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnmatchedMimetypeOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnmatchedMimetypeOnText");

    String filename = "text-original.txt";
    String uri = "/txt-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("image/jpeg");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "TEXT";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }

  @Test
  public void testUnsupportedMimetypeOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testUnsupportedMimetypeOnText");

    String filename = "text-original.txt";
    String uri = "/txt-mimetype/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setMimetype("application/vnd.nokia.configuration-message");

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // read document mimetype
    docMgr.read(docId, handle);
    String format = handle.getFormat().name();
    String expectedFormat = "TEXT";

    assertEquals( expectedFormat, format);

    // release the client
    client.release();
  }
}
