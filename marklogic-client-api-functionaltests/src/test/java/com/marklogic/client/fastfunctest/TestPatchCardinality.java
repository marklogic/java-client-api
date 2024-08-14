/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

public class TestPatchCardinality extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testOneCardinalityNegative() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOneCardinalityNegative");

    String[] filenames = { "cardinal1.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal1.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();

    String exception = "";
    try
    {
      docMgr.patch(docId, patchHandle);
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
      exception = e.getMessage();
    }

    String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal1.xml: invalid cardinality of 5 nodes for: /root/foo";

    assertTrue( exception.contains(expectedException));

    // release client
    client.release();
  }

  @Test
  public void testOneCardinalityPositve() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOneCardinalityPositive");

    String[] filenames = { "cardinal2.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal2.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<bar>added</bar>"));

    // release client
    client.release();
  }

  @Test
  public void testOneOrMoreCardinalityPositve() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOneOrMoreCardinalityPositive");

    String[] filenames = { "cardinal1.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal1.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE_OR_MORE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<foo>one</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>two</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>three</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>four</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>five</foo><bar>added</bar>"));

    // release client
    client.release();
  }

  @Test
  public void testOneOrMoreCardinalityNegative() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testOneOrMoreCardinalityNegative");

    String[] filenames = { "cardinal3.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal3.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE_OR_MORE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();

    String exception = "";
    try
    {
      docMgr.patch(docId, patchHandle);
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
      exception = e.getMessage();
    }

    String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal3.xml: invalid cardinality of 0 nodes for: /root/foo";

    assertTrue( exception.contains(expectedException));

    // release client
    client.release();
  }

  @Test
  public void testZeroOrOneCardinalityNegative() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testZeroOrOneCardinalityNegative");

    String[] filenames = { "cardinal1.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal1.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();

    String exception = "";
    try
    {
      docMgr.patch(docId, patchHandle);
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
      exception = e.getMessage();
    }

    String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal1.xml: invalid cardinality of 5 nodes for: /root/foo";

    assertTrue( exception.contains(expectedException));

    // release client
    client.release();
  }

  @Test
  public void testZeroOrOneCardinalityPositive() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testZeroOrOneCardinalityPositive");

    String[] filenames = { "cardinal2.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal2.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<foo>one</foo><bar>added</bar>"));

    // release client
    client.release();
  }

  @Test
  public void testZeroOrOneCardinalityPositiveWithZero() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testZeroOrOneCardinalityPositiveWithZero");

    String[] filenames = { "cardinal3.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal3.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertFalse(content.contains("<baz>one</baz><bar>added</bar>"));

    // release client
    client.release();
  }

  @Test
  public void testZeroOrMoreCardinality() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testZeroOrMoreCardinality");

    String[] filenames = { "cardinal1.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
    }

    String docId = "/cardinal/cardinal1.xml";
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_MORE, "<bar>added</bar>");
    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue( content.contains("<foo>one</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>two</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>three</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>four</foo><bar>added</bar>"));
    assertTrue( content.contains("<foo>five</foo><bar>added</bar>"));

    // release client
    client.release();
  }

  @Test
  public void testBug23843() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testBug23843");

    String[] filenames = { "cardinal1.xml", "cardinal4.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");

      String docId = "";

      XMLDocumentManager docMgr = client.newXMLDocumentManager();

      DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
      if (filename == "cardinal1.xml") {
        patchBldr.insertFragment("/root", Position.LAST_CHILD, Cardinality.ONE, "<bar>added</bar>");
      }
      else if (filename == "cardinal4.xml") {
        patchBldr.insertFragment("/root", Position.LAST_CHILD, "<bar>added</bar>");
      }
      DocumentPatchHandle patchHandle = patchBldr.build();
      String RawPatch = patchHandle.toString();
      System.out.println("Before" + RawPatch);

      if (filename == "cardinal1.xml") {
        try
        {
          docId = "/cardinal/cardinal1.xml";
          docMgr.patch(docId, patchHandle);

          String actual = docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString();
          System.out.println("Actual" + actual);

          assertXpathEvaluatesTo("4", "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission'])", actual);
          assertXpathEvaluatesTo("1",
              "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='rest-reader'])", actual);
          assertXpathEvaluatesTo("1",
              "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='rest-writer'])", actual);
          assertXpathEvaluatesTo("1", "count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='0'])", actual);
        } catch (Exception e)
        {
          System.out.println(e.getMessage());
        }
      }
      else if (filename == "cardinal4.xml") {
        try
        {
          docId = "/cardinal/cardinal4.xml";
          docMgr.clearMetadataCategories();
          docMgr.patch(docId, new StringHandle(patchHandle.toString()));
          docMgr.setMetadataCategories(Metadata.ALL);

          String actual = docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString();
          System.out.println("Actual" + actual);

          assertXpathEvaluatesTo("4", "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission'])", actual);
          assertXpathEvaluatesTo("1",
              "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='rest-reader'])", actual);
          assertXpathEvaluatesTo("1",
              "count(/*[local-name()='metadata']/*[local-name()='permissions']/*[local-name()='permission']/*[local-name()='role-name' and string(.)='rest-writer'])", actual);
          assertXpathEvaluatesTo("1", "count(/*[local-name()='metadata']/*[local-name()='quality' and string(.)='0'])", actual);
        } catch (Exception e)
        {
          System.out.println(e.getMessage());
        }
      }

      String actual = docMgr.read(docId, new StringHandle()).get();

      System.out.println("Actual : " + actual);
    }

    // release client
    client.release();
  }
}
