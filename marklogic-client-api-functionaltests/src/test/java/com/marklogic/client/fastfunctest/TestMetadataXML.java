/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;


public class TestMetadataXML extends AbstractFunctionalTest {

  @Test
  public void testMetadataXMLCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMetadataXMLCRUD");

    String filename = "Simple_ScanTe.png";
    String uri = "/write-bin-metadata/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // WRITE
    // write the doc
    writeDocumentUsingBytesHandle(client, filename, uri, "Binary");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create doc id
    String docId = uri + filename;

    // write original metadata
    docMgr.writeMetadata(docId, writeMetadataHandle);

    // create handle to read metadata
    DOMHandle readMetadataHandle = new DOMHandle();

    // READ
    // read metadata
    docMgr.readMetadata(docId, readMetadataHandle);
    Document docReadMetadata = readMetadataHandle.get();

    assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
    assertXpathEvaluatesTo("coll2", "string(//*[local-name()='collection'][2])", docReadMetadata);
    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='Author'])", docReadMetadata);

    // UPDATE
    // get the update metadata
    Document docMetadataUpdate = getXMLMetadata("metadata-updated.xml");

    // create handle for metadata update
    DOMHandle writeMetadataHandleUpdate = new DOMHandle();
    writeMetadataHandleUpdate.set(docMetadataUpdate);

    // write updated metadata
    docMgr.writeMetadata(docId, writeMetadataHandleUpdate);

    // create handle to read updated metadata
    DOMHandle readMetadataHandleUpdate = new DOMHandle();

    // read updated metadata
    docMgr.readMetadata(docId, readMetadataHandleUpdate);
    Document docReadMetadataUpdate = readMetadataHandleUpdate.get();

    assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadataUpdate);
    assertXpathEvaluatesTo("coll3", "string(//*[local-name()='collection'][2])", docReadMetadataUpdate);
    assertXpathEvaluatesTo("23", "string(//*[local-name()='quality'])", docReadMetadataUpdate);
    assertXpathEvaluatesTo("Aries", "string(//*[local-name()='Author'])", docReadMetadataUpdate);

    // DELETE
    // write default metadata
    docMgr.writeDefaultMetadata(docId);

    // create handle to read deleted metadata
    DOMHandle readMetadataHandleDelete = new DOMHandle();

    // read deleted metadata
    docMgr.readMetadata(docId, readMetadataHandleDelete);
    Document docReadMetadataDelete = readMetadataHandleDelete.get();

    assertXpathEvaluatesTo("0", "string(//*[local-name()='quality'])", docReadMetadataDelete);

    // release the client
    client.release();
  }

  @Test
  public void testMetadataXMLNegative() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMetadataXMLNegative");

    String filename = "Simple_ScanTe.png";
    String uri = "/write-neg-metadata/";

    // connect the client
    DatabaseClient client1 = getDatabaseClient("rest-writer", "x", getConnType());

    // write the doc
    writeDocumentUsingBytesHandle(client1, filename, uri, "Binary");

    // connect with another client to write metadata
    DatabaseClient client2 = getDatabaseClient("rest-reader", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client2.newXMLDocumentManager();

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create doc id
    String docId = uri + filename;

    String expectedException = "You do not have permission to this method and URL";
    String exception = "";

    // write original metadata
    try
    {
      docMgr.writeMetadata(docId, writeMetadataHandle);
    } catch (Exception e) {
      exception = e.toString();
    }

    // assertEquals(
    // expectedException, exception);

    boolean exceptionIsThrown = exception.contains(expectedException);
    assertTrue( exceptionIsThrown);

    // release the clients
    client1.release();
    client2.release();
  }
}
