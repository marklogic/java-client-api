/*
 * Copyright 2014-2018 MarkLogic Corporation
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

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;

public class TestRawCombinedQueryGeo extends BasicJavaClientREST {
  private static String dbName = "TestRawCombinedQueryGeoDB";
  private static String[] fNames = { "TestRawCombinedQueryGeoDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesGeoConstraint(dbName);
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testRawCombinedQueryGeo() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testRawCombinedQueryGeo");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    loadGeoData();

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeo.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file); // bug 21107

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testRawCombinedQueryGeoJSON");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    loadGeoData();

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoJSON.json");

    String combinedQuery = convertFileToString(file);

    // create a handle for the search criteria
    StringHandle rawHandle = new StringHandle(combinedQuery);
    rawHandle.setFormat(Format.JSON);

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    StringHandle resultsHandle = new StringHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();

    System.out.println(resultDoc);

    assertTrue("total document returned is incorrect", resultDoc.contains("total=\"1\""));
    assertTrue("returned doc is incorrect", resultDoc.contains("uri=\"/geo-constraint/geo-constraint1.xml\""));
    assertTrue("matched text is incorrect", resultDoc.contains("karl_kara 12,5 12,5 12 5"));

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoBoxAndWordJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testRawCombinedQueryGeoBoxAndWordJSON");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    loadGeoData();

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoBoxAndWordJSON.json");

    String combinedQuery = convertFileToString(file);

    // create a handle for the search criteria
    StringHandle rawHandle = new StringHandle(combinedQuery);
    rawHandle.setFormat(Format.JSON);

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    StringHandle resultsHandle = new StringHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();

    System.out.println(resultDoc);

    assertTrue("total document returned is incorrect", resultDoc.contains("total=\"1\""));
    assertTrue("returned doc is incorrect", resultDoc.contains("uri=\"/geo-constraint/geo-constraint20.xml\""));

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoCircle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("testRawCombinedQueryGeoCircle");

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    loadGeoData();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoCircle.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file); // bug 21107

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("bill_kara 13,-5 13,-5 13 -5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("karl_gale 12,-6 12,-6 12 -6", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("testRawCombinedQueryGeoBox");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    loadGeoData();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoBox.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file); // bug 21107

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
    assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoBoxAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testRawCombinedQueryGeoBoxAndWord");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    loadGeoData();

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoBoxAndWord.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file); // bug 21107

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/geo-constraint/geo-constraint20.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRawCombinedQueryGeoPointAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testRawCombinedQueryGeoPointAndWord");

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (int i = 1; i <= 9; i++)
    {
      writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionGeoPointAndWord.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file); // bug 21107

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/geo-constraint/geo-constraint8.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
