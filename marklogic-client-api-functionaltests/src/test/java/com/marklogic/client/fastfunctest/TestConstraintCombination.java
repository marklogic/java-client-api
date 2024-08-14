/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

public class TestConstraintCombination extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
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
    querydef.setCriteria("intitle:BBQ AND flavor:smoky AND heat:moderate AND contributor:AuntSally AND (summary:Southern AND summary:classic)");

    System.out.println(queryMgr.search(querydef, new JacksonHandle()).get().toPrettyString());

    SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    assertEquals(1, results.getTotalResults());
    assertEquals("/combination-constraint/bbq1.xml", results.getMatchResults()[0].getUri());
    assertEquals("Moderate (500 - 2500)", results.getFacetResult("heat").getFacetValues()[0].getLabel());

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
}
