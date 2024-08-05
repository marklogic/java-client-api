/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestServerAssignedDocumentURI extends AbstractFunctionalTest {

  @Test
  public void testCreate() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testCreate");

    String filename = "flipper.xml";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create template
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
    template.withDirectory("/mytest/create/");

    // get the file
    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create doc
    DocumentDescriptor desc = docMgr.create(template, handle);

    // get the uri
    String docId = desc.getUri();
    System.out.println(docId);

    String content = docMgr.read(desc, new StringHandle()).get();
    System.out.println(content);

    assertTrue( content.contains("Flipper"));

    // release the client
    client.release();
  }

  @Test
  public void testCreateMultibyte() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testCreateMultibyte");

    String filename = "flipper.xml";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create template
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
    template.withDirectory("/里/里/");

    // get the file
    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);
    String content = null;
    try {
      // create doc
      DocumentDescriptor desc = docMgr.create(template, handle);

      // get the uri
      String docId = desc.getUri();
      System.out.println(docId);

      content = docMgr.read(desc, new StringHandle()).get();
      System.out.println(content);

      assertTrue( content.contains("Flipper"));
    } catch (ResourceNotFoundException e) {
      System.out.println("Because of Special Characters in uri, it is throwing exception");
    }
    // release the client
    client.release();
  }

  @Test
  public void testCreateInvalidURI() throws KeyManagementException, NoSuchAlgorithmException, IOException // should
                                                                                                          // be
                                                                                                          // failed
  {
    System.out.println("Running testCreateInvalidURI");

    String filename = "flipper.xml";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create template
    try {
      DocumentUriTemplate template = docMgr.newDocumentUriTemplate("/");
      template.withDirectory("/mytest/create/");

      // get the file
      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

      // create a handle on the content
      FileHandle handle = new FileHandle(file);
      handle.set(file);

      // create doc
      DocumentDescriptor desc = docMgr.create(template, handle);

      // get the uri
      String docId = desc.getUri();
      System.out.println(docId);
    } catch (IllegalArgumentException i) {
      i.printStackTrace();
      System.out.println("Expected Output");
    }
    // release the client
    client.release();
  }

  @Test
  public void testCreateInvalidDirectory() throws KeyManagementException, NoSuchAlgorithmException, IOException // Should
                                                                                                                // be
                                                                                                                // failed
  {
    System.out.println("Running testCreateInvalidDirectory");

    String filename = "flipper.xml";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    try {
      // create template
      DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
      template.withDirectory("/:?#[]@/");

      // get the file
      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

      // create a handle on the content
      FileHandle handle = new FileHandle(file);
      handle.set(file);

      // create doc
      DocumentDescriptor desc = docMgr.create(template, handle);

      // get the uri
      String docId = desc.getUri();
      System.out.println(docId);
    } catch (IllegalArgumentException i) {
      i.printStackTrace();
      assertTrue( i.toString().contains("Directory is not valid: /:?#[]@/"));
    }
    // release the client
    client.release();
  }

  @Test
  public void testCreateWithTransformerTxMetadata() throws KeyManagementException, NoSuchAlgorithmException, TransformerException, ParserConfigurationException, SAXException,
      IOException
  {
    System.out.println("Running testCreateWithTransformerTxMetadata");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // get the doc
    Source source = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee.xml");

    // get the xsl
    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/employee-stylesheet.xsl");

    String xsl = convertFileToString(file);

    // create transform
    TransformExtensionsManager extensionMgr = client.newServerConfigManager().newTransformExtensionsManager();
    extensionMgr.writeXSLTransform("somename", new StringHandle().with(xsl));
    ServerTransform transform = new ServerTransform("somename");

    // release rest-admin client
    client.release();

    // connect the rest-writer client
    DatabaseClient client1 = getDatabaseClient("rest-writer", "x", getConnType());

    // create a doc manager
    XMLDocumentManager docMgr = client1.newXMLDocumentManager();

    // create template
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
    template.withDirectory("/mytest/create/transformer/");

    // create a handle on the content
    SourceHandle handle = new SourceHandle();
    handle.set(source);

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create transaction
    Transaction transaction = client1.openTransaction();

    // create doc
    DocumentDescriptor desc = docMgr.create(template, writeMetadataHandle, handle, transform, transaction);

    // get the uri
    String docId = desc.getUri();
    System.out.println(docId);

    System.out.println("Before commit:");

    String exception = "";
    try
    {
      String content = docMgr.read(desc, new StringHandle()).get();
      System.out.println(content);
    } catch (Exception e)

    {
      System.out.println(e);
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT";
    assertTrue( exception.contains(expectedException));

    System.out.println("After commit:");
    transaction.commit();

    String content1 = docMgr.read(desc, new StringHandle()).get();
    System.out.println(content1);

    assertTrue( content1.contains("firstname"));

    // read metadata
    String metadataContent = docMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(metadataContent);

    assertTrue( metadataContent.contains("<Author>MarkLogic</Author>"));
    handle.close();

    // release client
    client1.release();
  }
}
