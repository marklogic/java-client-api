/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;



public class TestStructuredQuery extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testStructuredQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStructuredQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition t = qb.valueConstraint("id", "0026");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStructuredQueryJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStructuredQueryJSON");

    String[] filenames = { "constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json" };
    String queryOptionName = "valueConstraintWildCardOpt.json";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "JSON");
    }

    setJSONQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create value query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition t = qb.value(qb.jsonProperty("popularity"), "4");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));
    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/structured-query/constraint2.json", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);

    // create new word query def
    StructuredQueryDefinition t1 = qb.word(qb.jsonProperty("id"), "0012");
    // create handle
    DOMHandle resultsHandle1 = new DOMHandle();
    queryMgr.search(t1, resultsHandle1);

    // get the result
    Document resultDoc1 = resultsHandle1.get();
    System.out.println(convertXMLDocumentToString(resultDoc1));
    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc1);
    assertXpathEvaluatesTo("/structured-query/constraint2.json", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc1);

    // create new range word query def
    StructuredQueryDefinition t2 = qb.range(qb.jsonProperty("price"), "xs:integer", Operator.GE, "0.1");
    // create handle
    DOMHandle resultsHandle2 = new DOMHandle();
    queryMgr.search(t2, resultsHandle2);

    // get the result
    Document resultDoc2 = resultsHandle2.get();
    System.out.println(convertXMLDocumentToString(resultDoc2));
    assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc2);

    // release client
    client.release();
  }

  @Test
  public void testValueConstraintWildCard() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testValueConstraintWildCard");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition valueConstraintQuery1 = qb.valueConstraint("id", "00*2");
    StructuredQueryDefinition valueConstraintQuery2 = qb.valueConstraint("id", "0??6");
    StructuredQueryDefinition orFinalQuery = qb.or(valueConstraintQuery1, valueConstraintQuery2);

    SearchHandle results = queryMgr.search(orFinalQuery, new SearchHandle());

    assertEquals(2, results.getTotalResults());
    List<String> uris = new ArrayList<>();
    for (MatchDocumentSummary matchResult : results.getMatchResults()) {
      uris.add(matchResult.getUri());
    }
    assertTrue(uris.contains("/structured-query/constraint2.xml"));
    assertTrue(uris.contains("/structured-query/constraint5.xml"));

    // release client
    client.release();
  }

  @Test
  public void testAndNotQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testAndNotQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-andnot/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery1 = qb.term("Atlantic");
    StructuredQueryDefinition termQuery2 = qb.term("Monthly");
    StructuredQueryDefinition termQuery3 = qb.term("Bush");
    StructuredQueryDefinition andQuery = qb.and(termQuery1, termQuery2);
    StructuredQueryDefinition andNotFinalQuery = qb.andNot(andQuery, termQuery3);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(andNotFinalQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0113", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);

    // To test Git issue 908
    StructuredQueryDefinition termQuery11 = qb.term("Pacific");
    StructuredQueryDefinition termQuery21 = qb.term("Yearly");
    StructuredQueryDefinition termQuery31 = qb.term("DT");
    StructuredQueryDefinition andQuery1 = qb.and(termQuery11, termQuery21);
    StructuredQueryDefinition andNotFinalQuery1 = qb.andNot(andQuery1, termQuery31);
    SearchHandle results = null;
    StringBuilder searchHandleEx = new StringBuilder();
    try {
     results = queryMgr.search(andNotFinalQuery1, new SearchHandle());
    }
    catch (Exception ex) {
    	searchHandleEx.append(ex);
    }
    long res = results.getTotalResults();
    System.out.println("No query results available. Total Results should be zero: " + res);
    assertTrue( searchHandleEx.toString().isEmpty());
    assertEquals( 0, res);

    // release client
    client.release();
  }

  @Test
  public void testNearQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testNearQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-near/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery1 = qb.term("Bush");
    StructuredQueryDefinition termQuery2 = qb.term("Atlantic");
    StructuredQueryDefinition nearQuery = qb.near(6, 1.0, StructuredQueryBuilder.Ordering.UNORDERED, termQuery1, termQuery2);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(nearQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testDirectoryQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testDirectoryQuery");

    String[] filenames1 = { "constraint1.xml", "constraint2.xml", "constraint3.xml" };
    String[] filenames2 = { "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename1 : filenames1) {
      writeDocumentUsingInputStreamHandle(client, filename1, "/dir1/dir2/", "XML");
    }

    // write docs
    for (String filename2 : filenames2) {
      writeDocumentUsingInputStreamHandle(client, filename2, "/dir3/dir4/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery = qb.term("Memex");
    StructuredQueryDefinition dirQuery = qb.directory(true, "/dir3/");
    StructuredQueryDefinition andFinalQuery = qb.and(termQuery, dirQuery);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(andFinalQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testDocumentQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testDocumentQuery");

    String[] filenames1 = { "constraint1.xml", "constraint2.xml", "constraint3.xml" };
    String[] filenames2 = { "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename1 : filenames1) {
      writeDocumentUsingInputStreamHandle(client, filename1, "/dir1/dir2/", "XML");
    }

    // write docs
    for (String filename2 : filenames2) {
      writeDocumentUsingInputStreamHandle(client, filename2, "/dir3/dir4/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery = qb.term("Memex");
    StructuredQueryDefinition docQuery = qb.or(qb.document("/dir1/dir2/constraint2.xml"), qb.document("/dir3/dir4/constraint4.xml"), qb.document("/dir3/dir4/constraint5.xml"));
    StructuredQueryDefinition andFinalQuery = qb.and(termQuery, docQuery);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(andFinalQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testCollectionQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testCollectionQuery");

    String filename1 = "constraint1.xml";
    String filename2 = "constraint2.xml";
    String filename3 = "constraint3.xml";
    String filename4 = "constraint4.xml";
    String filename5 = "constraint5.xml";
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://test.com/set1");
    metadataHandle1.getCollections().addAll("http://test.com/set5");
    metadataHandle2.getCollections().addAll("http://test.com/set1");
    metadataHandle3.getCollections().addAll("http://test.com/set3");
    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
    metadataHandle5.getCollections().addAll("http://test.com/set1");
    metadataHandle5.getCollections().addAll("http://test.com/set5");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery = qb.term("Memex");
    StructuredQueryDefinition collQuery = qb.or(qb.collection("http://test.com/set1"), qb.collection("http://test.com/set3"));
    StructuredQueryDefinition andFinalQuery = qb.and(termQuery, collQuery);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(andFinalQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testContainerConstraintQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testContainerConstraintQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "containerConstraintOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-container/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition containerQuery = qb.containerConstraint("title-contain", qb.term("Bush"));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(containerQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  /*
   * Create a StructuredQueryDefinition (using StructuredQueryBuilder) and add a
   * string query by calling setCriteria and withCriteria Make sure a query
   * using those query definitions selects only documents that match both the
   * query definition and the string query
   *
   * Uses setCriteria. Uses valueConstraintWildCardOpt.xml options file QD and
   * string query (Memex) should return 1 URI in the response.
   */
  @Test
  public void testSetCriteriaOnStructdgQueryDef() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetCriteriaOnStructdgQueryDef");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition strutdDef = qb.valueConstraint("id", "0012");
    strutdDef.setCriteria("Memex");

    // create handle
    JacksonHandle strHandle = new JacksonHandle();
    JacksonHandle results = queryMgr.search(strutdDef, strHandle.withFormat(Format.JSON));

    JsonNode node = results.get();
    assertEquals( "1", node.path("total").asText());
    assertEquals( "/structured-query/constraint2.xml", node.path("results").get(0).path("uri").asText());

    // With multiple setCriteria - positive
    StructuredQueryDefinition strutdDefPos = qb.valueConstraint("id", "0012");
    strutdDefPos.setCriteria("Memex");
    strutdDefPos.setCriteria("described");

    // create handle
    JacksonHandle strHandlePos = new JacksonHandle();
    JacksonHandle resultsPos = queryMgr.search(strutdDefPos, strHandlePos.withFormat(Format.JSON));

    JsonNode nodePos = resultsPos.get();
    // Return 1 node - constraint2.xml
    assertEquals( "1", nodePos.path("total").asText());
    assertEquals( "/structured-query/constraint2.xml", nodePos.path("results").get(0).path("uri").asText());

    // With setCriteria AND - positive
    StructuredQueryDefinition strutdDefPosAnd = qb.valueConstraint("id", "0012");
    strutdDefPosAnd.setCriteria("Memex AND described");

    // create handle
    JacksonHandle strHandlePosAnd = new JacksonHandle();
    JacksonHandle resultsPosAnd = queryMgr.search(strutdDefPosAnd, strHandlePosAnd.withFormat(Format.JSON));

    JsonNode nodePosAnd = resultsPosAnd.get();
    // Return 1 node - constraint2.xml
    assertEquals( "1", nodePosAnd.path("total").asText());
    assertEquals( "/structured-query/constraint2.xml", nodePosAnd.path("results").get(0).path("uri").asText());
    assertEquals( "Memex AND described", strutdDefPosAnd.getCriteria());

    // With multiple setCriteria - negative
    StructuredQueryDefinition strutdDefNeg = qb.valueConstraint("id", "0012");
    strutdDefNeg.setCriteria("Memex");
    strutdDefNeg.setCriteria("Atlantic");

    // create handle
    JacksonHandle strHandleNeg = new JacksonHandle();
    JacksonHandle resultsNeg = queryMgr.search(strutdDefNeg, strHandleNeg.withFormat(Format.JSON));

    JsonNode nodeNeg = resultsNeg.get();
    // Return 0 nodes
    assertEquals( "0", nodeNeg.path("total").asText());
    // release client
    client.release();
  }

  /*
   * Create a StructuredQueryDefinition (using StructuredQueryBuilder) and add a
   * string query by calling setCriteria and withCriteria Make sure a query
   * using those query definitions selects only documents that match both the
   * query definition and the string query
   *
   * Uses withCriteria. Uses valueConstraintPopularityOpt.xml options file QD
   * and string query (Vannevar) should return 2 URIs in the response.
   * constraint1.xml and constraint4.xml
   */
  @Test
  public void testWithCriteriaOnStructdgQueryDef() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testWithCriteriaOnStructdgQueryDef");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintPopularityOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition strutdDef = (qb.valueConstraint("popularity", "5")).withCriteria("Vannevar");

    // create handle
    JacksonHandle strHandle = new JacksonHandle();
    JacksonHandle results = queryMgr.search(strutdDef, strHandle.withFormat(Format.JSON));

    JsonNode node = results.get();
    assertEquals( "2", node.path("total").asText());
    assertTrue( node.path("results").get(0).path("uri").asText().contains("/structured-query/constraint1.xml") ||
        node.path("results").get(1).path("uri").asText().contains("/structured-query/constraint1.xml"));
    assertTrue( node.path("results").get(0).path("uri").asText().contains("/structured-query/constraint4.xml") ||
        node.path("results").get(1).path("uri").asText().contains("/structured-query/constraint4.xml"));
    // With multiple withCriteria - positive
    StructuredQueryDefinition strutdDefPos = (qb.valueConstraint("popularity", "5")).withCriteria("Vannevar").withCriteria("Atlantic").withCriteria("intellectual");

    // create handle
    JacksonHandle strHandlePos = new JacksonHandle();
    JacksonHandle resultsPos = queryMgr.search(strutdDefPos, strHandlePos.withFormat(Format.JSON));

    JsonNode nodePos = resultsPos.get();
    // Return 2 nodes.
    assertEquals( "1", nodePos.path("total").asText());
    assertTrue( nodePos.path("results").get(0).path("uri").asText().contains("/structured-query/constraint4.xml"));

    // With multiple withCriteria - negative
    StructuredQueryDefinition strutdDefNeg = (qb.valueConstraint("popularity", "5")).withCriteria("Vannevar").withCriteria("England");

    // create handle
    JacksonHandle strHandleNeg = new JacksonHandle();
    JacksonHandle resultsNeg = queryMgr.search(strutdDefNeg, strHandleNeg.withFormat(Format.JSON));

    JsonNode nodeNeg = resultsNeg.get();
    // Return 0 nodes.
    assertEquals( "0", nodeNeg.path("total").asText());
    assertEquals( "England", strutdDefNeg.getCriteria());

    // create query def2 with both criteria methods and check fluent return

    StructuredQueryDefinition strutdDef2 = qb.valueConstraint("popularity", "5");
    strutdDef2.withCriteria("Vannevar").setCriteria("Bush");

    // create handle
    JacksonHandle strHandle2 = new JacksonHandle();
    JacksonHandle results2 = queryMgr.search(strutdDef2, strHandle2.withFormat(Format.JSON));

    JsonNode node2 = results2.get();
    // Returns 1 node. constraint1.xml
    assertEquals( "1", node2.path("total").asText());
    assertEquals( "/structured-query/constraint1.xml", node2.path("results").get(0).path("uri").asText());
    assertEquals( "Bush", strutdDef2.getCriteria());
    // release client
    client.release();
  }

  /*
   * Test to verify minimum distance parameter in near method. - Git issue 722
   * One word distance - q1 = The  q 2 = Atlantic :as in The Atlantic
   * Zero word distance - q1 = The  q 2 = Atlantic :as in The Atlantic
   * Reverse One word distance - q1 = Atlantic q2 = The :as in  The Atlantic
   * > 1 min distance  and max distance (2) is less than the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
   * > 1 min distance  and max distance (4) is equal to the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
   * < 1 min distance  and max distance (2) is less than the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
   * < 1 min distance  and max distance (4) is equal to the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
   * 0 min distance  and 0 max distance words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
   * Same queries One word distance - q1 = Atlantic  q2 = Atlantic :as in For 1945, the thoughts expressed in The Atlantic Monthly were groundbreaking.
   * Random queries One word distance - q1 = AAAA  q2 = BBBB
   * Near query on first word min distance = 1 q1 = Vannevar and q2 = wrote :as in Vannevar Bush wrote an article for The Atlantic Monthly
   * Near query on last word min distance = 0 q1 = Monthly and q2 (nothing)  :as in Vannevar Bush wrote an article for The Atlantic Monthly
   * Partial word min distance = 1  - q1 = Th  q 2 = lant :as in The Atlantic
   */

  @Test
  public void testNearQueryMinimumDistance() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testNearQueryMinimumDistance");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/min-dist/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();

    // create query def - One word distance - q1 = The  q 2 = Atlantic :as in The Atlantic
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition termQuery1 = qb.term("The");
    StructuredQueryDefinition termQuery2 = qb.term("Atlantic");

    StructuredQueryDefinition nearQuery1 = qb.near(1, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, termQuery1, termQuery2);
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(nearQuery1, resultsHandle);

    // get the Result
    JsonNode result = resultsHandle.get();
    System.out.println("nearQuery1 Results " + result.toString());

    assertTrue( result.path("total").asInt() == 2);

    String uri1 = result.path("results").get(0).path("uri").asText().trim();
    String uri2 = result.path("results").get(1).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint1.xml"));
    assertTrue( uri2.contains("/min-dist/constraint3.xml"));

    // create handle - Zero word distance - q1 = The  q 2 = Atlantic :as in The Atlantic
    StructuredQueryDefinition nearQuery2 = qb.near(0, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, termQuery1, termQuery2);

    // create handle
    JacksonHandle resultsHandle2 = new JacksonHandle();
    queryMgr.search(nearQuery2, resultsHandle2);

    // get the Result
    JsonNode result2 = resultsHandle2.get();
    System.out.println("nearQuery2 Results " + result2.toString());

    assertTrue( result2.path("total").asInt() == 2);
    uri1 = result2.path("results").get(0).path("uri").asText().trim();
    uri2 = result2.path("results").get(1).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint1.xml"));
    assertTrue( uri2.contains("/min-dist/constraint3.xml"));

    //Reverse One word distance - q1 = Atlantic q2 = The :as in  The Atlantic

    StructuredQueryDefinition RevtermQuery1 = qb.term("Atlantic");
    StructuredQueryDefinition RevtermQuery2 = qb.term("The");

    StructuredQueryDefinition RevnearQuery = qb.near(0, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, RevtermQuery1, RevtermQuery2);

    // create handle
    JacksonHandle RevResultsHandle = new JacksonHandle();
    queryMgr.search(RevnearQuery, RevResultsHandle);

    // get the Result should be 0
    JsonNode RevResult = RevResultsHandle.get();
    System.out.println("RevnearQuery Results " + RevResult.toString());

    assertTrue( RevResult.path("total").asInt() == 0);

    // > 1 min distance  and max distance(2) is less than the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
    StructuredQueryDefinition MaxtermQuery1 = qb.term("prominent");
    StructuredQueryDefinition MaxtermQuery2 = qb.term("intellectual");

    StructuredQueryDefinition maxnearQuery = qb.near(1, 2, 1.0, StructuredQueryBuilder.Ordering.ORDERED, MaxtermQuery1, MaxtermQuery2);

    // create handle
    JacksonHandle maxResultsHandle = new JacksonHandle();
    queryMgr.search(maxnearQuery, maxResultsHandle);

    // get the Result - Should be 0.
    JsonNode resultMax = maxResultsHandle.get();
    System.out.println("maxnearQuery Results " + resultMax.toString());

    assertTrue( resultMax.path("total").asInt() == 0);

    // > 1 min distance  and max distance (4) is equal to the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
    StructuredQueryDefinition maxnearQuery2 = qb.near(1, 4, 1.0, StructuredQueryBuilder.Ordering.ORDERED, MaxtermQuery1, MaxtermQuery2);
    // create handle
    JacksonHandle maxResultsHandle2 = new JacksonHandle();
    queryMgr.search(maxnearQuery2, maxResultsHandle2);

    // get the Result - Should be 0.
    JsonNode resultMax2 = maxResultsHandle2.get();
    System.out.println("maxnearQuery2 Results " + resultMax2.toString());

    assertTrue( resultMax2.path("total").asInt() == 1);
    uri1 = resultMax2.path("results").get(0).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint4.xml"));

    // < 1 min distance  and max distance (2) is less than the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
    StructuredQueryDefinition NegMinxnearQuery = qb.near(-1, 2, 1.0, StructuredQueryBuilder.Ordering.ORDERED, MaxtermQuery1, MaxtermQuery2);

    // create handle
    JacksonHandle NegMinxResultsHandle = new JacksonHandle();
    queryMgr.search(NegMinxnearQuery, NegMinxResultsHandle);

    // get the Result - Should be 0.
    JsonNode ResultNegMin = NegMinxResultsHandle.get();
    System.out.println("NegMinxnearQuery Results " + ResultNegMin.toString());

    assertTrue( ResultNegMin.path("total").asInt() == 0);

    // < 1 min distance  and max distance (4) is equal to the actual words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
    StructuredQueryDefinition NegMinxnearQuery2 = qb.near(-2, 4, 1.0, StructuredQueryBuilder.Ordering.ORDERED, MaxtermQuery1, MaxtermQuery2);
    // create handle
    JacksonHandle NegMinxResultsHandle2 = new JacksonHandle();
    queryMgr.search(NegMinxnearQuery2, NegMinxResultsHandle2);

    // get the Result - Should be 0.
    JsonNode resultNegMin2 = NegMinxResultsHandle2.get();
    System.out.println("NegMinxnearQuery2 Results " + resultNegMin2.toString());
    assertTrue( resultNegMin2.path("total").asInt() == 1);
    uri1 = resultNegMin2.path("results").get(0).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint4.xml"));

    // 0 min distance  and 0 max distance words in between - q1 = prominent and q2 = intellectual :as Vannevar served as a prominent policymaker and public intellectual
    StructuredQueryDefinition zeroMinxnearQuery = qb.near(0, 0, 1.0, StructuredQueryBuilder.Ordering.ORDERED, MaxtermQuery1, MaxtermQuery2);
    // create handle
    JacksonHandle zeroMinxResultsHandle = new JacksonHandle();
    queryMgr.search(zeroMinxnearQuery, zeroMinxResultsHandle);

    // get the Result - Should be 0.
    JsonNode ResultZero = zeroMinxResultsHandle.get();
    System.out.println("zeroMinxnearQuery Results " + ResultZero.toString());
    assertTrue( ResultZero.path("total").asInt() == 0);

    // Same queries One word distance - q1 = Atlantic  q 2 = Atlantic :as in For 1945, the thoughts expressed in The Atlantic Monthly were groundbreaking.

    StructuredQueryDefinition SamenearQuery = qb.near(0, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, termQuery2, termQuery2);

    // create handle
    JacksonHandle SameResultsHandle = new JacksonHandle();
    queryMgr.search(SamenearQuery, SameResultsHandle);

    // get the Result should be 0
    JsonNode SameResult = SameResultsHandle.get();
    System.out.println("SamenearQuery Results " + SameResult.toString());

    assertTrue( SameResult.path("total").asInt() == 2);
    uri1 = SameResult.path("results").get(0).path("uri").asText().trim();
    uri2 = SameResult.path("results").get(1).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint1.xml"));
    assertTrue( uri2.contains("/min-dist/constraint3.xml"));

    // Random queries One word distance - q1 = AAA  q 2 = BBB
    StructuredQueryDefinition randomQuery1 = qb.term("AAA");
    StructuredQueryDefinition randomQuery2 = qb.term("BBB");

    StructuredQueryDefinition RandomnearQuery = qb.near(1, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, randomQuery1, randomQuery2);

    // create handle
    JacksonHandle RandomResultsHandle = new JacksonHandle();
    queryMgr.search(RandomnearQuery, RandomResultsHandle);

    // get the Result should be 0
    JsonNode randomResult = RandomResultsHandle.get();
    System.out.println("RandomenearQuery Results " + randomResult.toString());
    assertTrue( randomResult.path("total").asInt() == 0);

    // Near query on first word min distance = 1 q1 = Vannevar and q2 = wrote :as in Vannevar Bush wrote an article for The Atlantic Monthly
    StructuredQueryDefinition firstQuery1 = qb.term("Vannevar");
    StructuredQueryDefinition firstQuery2 = qb.term("wrote");

    StructuredQueryDefinition firstnearQuery = qb.near(1, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, firstQuery1, firstQuery2);

    // create handle
    JacksonHandle firstResultsHandle = new JacksonHandle();
    queryMgr.search(firstnearQuery, firstResultsHandle);

    // get the Result should be 1
    JsonNode firstResult = firstResultsHandle.get();
    System.out.println("firstnearQuery Results " + firstResult.toString());
    assertTrue( firstResult.path("total").asInt() == 1);
    uri1 = firstResult.path("results").get(0).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint1.xml"));

    // Near query on last word min distance = 0 q1 = Monthly and q2 (nothing)  :as in Vannevar Bush wrote an article for The Atlantic Monthly
    StructuredQueryDefinition lastQuery1 = qb.term("Monthly");

    StructuredQueryDefinition lastnearQuery = qb.near(0, 0, 1.0, StructuredQueryBuilder.Ordering.ORDERED, lastQuery1);

    // create handle
    JacksonHandle lastResultsHandle = new JacksonHandle();
    queryMgr.search(lastnearQuery, lastResultsHandle);

    // get the Result should be 2
    JsonNode lastResult = lastResultsHandle.get();
    System.out.println("lastnearQuery Results " + lastResult.toString());
    assertTrue( lastResult.path("total").asInt() == 2);
    uri1 = lastResult.path("results").get(0).path("uri").asText().trim();
    uri2 = lastResult.path("results").get(1).path("uri").asText().trim();
    assertTrue( uri1.contains("/min-dist/constraint1.xml"));
    assertTrue( uri2.contains("/min-dist/constraint3.xml"));

    // Partial word min distance = 1  - q1 = Th  q 2 = lant :as in The Atlantic
    StructuredQueryDefinition PartialQuery1 = qb.term("Th");
    StructuredQueryDefinition PartialQuery2 = qb.term("lant");

    StructuredQueryDefinition PartialNearQuery = qb.near(1, 6, 1.0, StructuredQueryBuilder.Ordering.ORDERED, PartialQuery1, PartialQuery2);
    JacksonHandle PartialResultsHandle = new JacksonHandle();
    queryMgr.search(PartialNearQuery, PartialResultsHandle);

    // get the Result - Should be 0
    JsonNode resultpartial = PartialResultsHandle.get();
    System.out.println("PartialNearQuery Results " + resultpartial.toString());

    assertTrue( resultpartial.path("total").asInt() == 0);

    // release client
    client.release();
  }
}
