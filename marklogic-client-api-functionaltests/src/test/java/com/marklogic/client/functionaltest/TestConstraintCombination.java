/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestConstraintCombination extends BasicJavaClientREST {

  private static String dbName = "ConstraintCombinationDB";
  private static String[] fNames = { "ConstraintCombinationDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    addRangeElementAttributeIndex(dbName, "dateTime", "http://example.com", "entry", "", "date");
    addRangeElementIndex(dbName, "int", "http://example.com", "scoville");
    addRangeElementIndex(dbName, "decimal", "http://example.com", "rating");
    addField(dbName, "bbqtext");
    includeElementField(dbName, "bbqtext", "http://example.com", "title");
    includeElementField(dbName, "bbqtext", "http://example.com", "abstract");
    enableCollectionLexicon(dbName);
    enableTrailingWildcardSearches(dbName);
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testConstraintCombination() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testConstraintCombination");

    String filename1 = "bbq1.xml";
    String filename2 = "bbq2.xml";
    String filename3 = "bbq3.xml";
    String filename4 = "bbq4.xml";
    String filename5 = "bbq5.xml";

    String queryOptionName = "constraintCombinationOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("intitle:BBQ AND flavor:smok* AND heat:moderate AND contributor:AuntSally AND (summary:Southern AND summary:classic)");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/combination-constraint/bbq1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    assertXpathEvaluatesTo("1", "string(//*[local-name()='facet']//*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
    assertXpathEvaluatesTo("Moderate (500 - 2500)", "string(//*[local-name()='facet']//*[local-name()='facet-value'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testConstraintCombinationWordAndCollection() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("Running testConstraintCombinationWordAndCollection");

    String filename1 = "bbq1.xml";
    String filename2 = "bbq2.xml";
    String filename3 = "bbq3.xml";
    String filename4 = "bbq4.xml";
    String filename5 = "bbq5.xml";

    String queryOptionName = "constraintCombinationOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("intitle:pigs contributor:BigTex");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/combination-constraint/bbq4.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testConstraintCombinationFieldAndRange() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("testConstraintCombinationFieldAndRange");

    String filename1 = "bbq1.xml";
    String filename2 = "bbq2.xml";
    String filename3 = "bbq3.xml";
    String filename4 = "bbq4.xml";
    String filename5 = "bbq5.xml";

    String queryOptionName = "constraintCombinationOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("summary:Louisiana AND summary:sweet heat:moderate");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/combination-constraint/bbq3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

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
