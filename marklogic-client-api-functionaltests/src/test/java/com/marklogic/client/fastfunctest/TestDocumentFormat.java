/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;




public class TestDocumentFormat extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception {
    // Create a user with minimal privs and test doc exists in a transaction.
    createRESTUser("userInTrans", "x", "rest-writer");
  }

  @Test
  public void testExistsInTransMinPrivs() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testExistsInTransMinPrivs");
    DatabaseClient client1 = null;
    try {
      String filename = "json-original.json";
      String uri1 = "/DocExistsInTransMinimalPriv/";

      // user with minimal privs.
      client1 = getDatabaseClient("userInTrans", "x", getConnType());

      // create doc manager
      DocumentManager docMgr1 = client1.newDocumentManager();
      Transaction t1 = client1.openTransaction();

      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

      // create a handle on the content
      FileHandle handle = new FileHandle(file);
      handle.set(file);

      handle.setFormat(Format.JSON);

      // create docIds
      String docId1 = uri1 + filename;
      docMgr1.write(docId1, handle);
      String expectedUri1 = uri1 + filename;

      String docUri1 = docMgr1.exists(expectedUri1, t1).getUri();
      assertEquals( expectedUri1, docUri1);
      t1.rollback();
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // release the clients
      if (client1 != null)
        client1.release();
    }
  }

  @Test
  public void testExistsInTransWithPrivs() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testExistsInTransWithPrivs");
    DatabaseClient client2 = null;
    try {
      String filename = "json-original.json";
      String uri2 = "/DocExistsTransWithPrivs/";

      // user with privs.
      client2 = getDatabaseClient("rest-writer", "x", getConnType());

      // create doc manager
      DocumentManager docMgr2 = client2.newDocumentManager();
      Transaction t2 = client2.openTransaction();

      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

      // create a handle on the content
      FileHandle handle = new FileHandle(file);
      handle.set(file);

      handle.setFormat(Format.JSON);

      // create docIds
      String docId2 = uri2 + filename;
      docMgr2.write(docId2, handle);

      String expectedUri2 = uri2 + filename;
      String docUri2 = docMgr2.exists(expectedUri2, t2).getUri();
      assertEquals( expectedUri2, docUri2);
      t2.rollback();
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // release the clients
      if (client2 != null)
        client2.release();
    }
  }

  @Test
  public void testXMLFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/xml-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager<GenericReadHandle, GenericWriteHandle> docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/json-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/bin-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnXML");

    String filename = "flipper.xml";
    String uri = "/txt-format-xml-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/json-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/xml-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/bin-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnJSON");

    String filename = "json-original.json";
    String uri = "/txt-format-json-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/xml-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }
    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/json-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnBinary() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnBinary");

    String filename = "Pandakarlino.jpg";
    String uri = "/bin-format-bin-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testTextFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testTextFormatOnText");

    String filename = "text-original.txt";
    String uri = "/txt-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.TEXT);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testXMLFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testXMLFormatOnText");

    String filename = "text-original.txt";
    String uri = "/xml-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.XML);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testJSONFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testJSONFormatOnText");

    String filename = "text-original.txt";
    String uri = "/json-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.JSON);

    // create docId
    String docId = uri + filename;

    String exception = "";
    String expectedException = "";

    try {
      docMgr.write(docId, handle);
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @Test
  public void testBinaryFormatOnText() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testBinaryFormatOnText");

    String filename = "text-original.txt";
    String uri = "/bin-format-txt-file/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    handle.setFormat(Format.BINARY);

    // create docId
    String docId = uri + filename;

    docMgr.write(docId, handle);

    String expectedUri = uri + filename;
    String docUri = docMgr.exists(expectedUri).getUri();
    assertEquals( expectedUri, docUri);

    // release the client
    client.release();
  }

  @Test
  public void testNegativeJSONFormatWithDOMHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("Running testNegativeJSONFormatWithDOMHandle");

    String filename = "xml-original.xml";
    String uri = "/negative-format/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    DocumentManager docMgr = client.newDocumentManager();

    Document readDoc = expectedXMLDocument(filename);

    // create a handle on the content
    DOMHandle handle = new DOMHandle();
    handle.set(readDoc);

    String exception = "";
    String expectedException = "java.lang.IllegalArgumentException: DOMHandle supports the XML format only";

    try {
      handle.setFormat(Format.JSON);
    } catch (IllegalArgumentException e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);
    assertTrue( isExceptionThrown);

    // release the client
    client.release();
  }

  @AfterAll
  public static void tearDown() throws Exception
  {
    deleteRESTUser("userInTrans");
  }
}
