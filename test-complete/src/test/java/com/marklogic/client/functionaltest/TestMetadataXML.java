/*
 * Copyright 2014-2017 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;

public class TestMetadataXML extends BasicJavaClientREST {

  private static String dbName = "TestMetadataXMLDB";
  private static String[] fNames = { "TestMetadataXMLDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");

    configureRESTServer(dbName, fNames);
  }

  @Test
  public void testMetadataXMLCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testMetadataXMLCRUD");

    String filename = "Simple_ScanTe.png";
    String uri = "/write-bin-metadata/";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

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
    DatabaseClient client1 = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

    // write the doc
    writeDocumentUsingBytesHandle(client1, filename, uri, "Binary");

    // connect with another client to write metadata
    DatabaseClient client2 = getDatabaseClient("rest-reader", "x", Authentication.DIGEST);

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

    // assertEquals("Could write metadata with forbidden user",
    // expectedException, exception);

    boolean exceptionIsThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", exceptionIsThrown);

    // release the clients
    client1.release();
    client2.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
