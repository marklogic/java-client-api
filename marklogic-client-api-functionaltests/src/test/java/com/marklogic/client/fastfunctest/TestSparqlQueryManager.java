/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.semantics.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;



public class TestSparqlQueryManager extends AbstractFunctionalTest {

  private static DatabaseClient writeclient;
  private static DatabaseClient readclient;


  private static String newline;
  private static String customGraph;
  private static String datecustomGraph;
  private static String inferenceGraph;
  private static String multibyteGraphName;
  private static String enlocaleGraphName;
  private static String zhlocaleGraphName;
  private static String datasource;
  private static String mbSearchStr = "凌";

  /*
   * Used in testExecuteAskInTransactions method to test transactions This
   * returns false.
   */
  static class ExecuteAskSecondThreadFalse extends Thread {
    private boolean bCompleted = false;

    public void setbCompleted(boolean bCompleted) {
      this.bCompleted = bCompleted;
    }

    @Override
    public void run() {
      Transaction t2 = writeclient.openTransaction();
      try {
        System.out.println("In ExecuteAskSecondThreadFalse run method");

        SPARQLQueryManager sparqlQmgrTh = writeclient.newSPARQLQueryManager();
        StringBuffer sparqlQueryTh = new StringBuffer();
        sparqlQueryTh.append("ASK FROM <rdfxml> where { <http://example.org/kennedy/person1> <http://purl.org/dc/elements/1.1/title>  \"Person\'s title\"@en }");

        SPARQLQueryDefinition qdefTh = sparqlQmgrTh.newQueryDefinition(sparqlQueryTh.toString());

        // Verify result in t2 transaction.
        boolean bAskInTransT2 = sparqlQmgrTh.executeAsk(qdefTh, t2);
        System.out.println("Method ExecuteAskSecondThreadFalse Run result is " + bAskInTransT2);
        assertFalse( bAskInTransT2);
        setbCompleted(true);

      } catch (ForbiddenUserException e) {
        e.printStackTrace();
      } catch (FailedRequestException e) {
        e.printStackTrace();
      } finally {
        if (t2 != null) {
          t2.rollback();
          t2 = null;
        }
      }
    }
  }

  /*
   * Used in testExecuteAskInTransactions method to test transactions Sleeps for
   * 5 seconds, so that main thread commits the record This returns true.
   */
  static class ExecuteAskSecondThreadTrue extends Thread {
    private boolean bCompleted = false;

    public boolean isbCompleted() {
      return bCompleted;
    }

    public void setbCompleted(boolean bCompleted) {
      this.bCompleted = bCompleted;
    }

    @Override
    public void run() {
      Transaction t2 = writeclient.openTransaction("WaitingTransaction");
      try {
        System.out.println("In ExecuteAskSecondThreadTrue run method");

        SPARQLQueryManager sparqlQmgrTh = writeclient.newSPARQLQueryManager();
        StringBuffer sparqlQueryTh = new StringBuffer();
        sparqlQueryTh.append("ASK FROM <rdfxml> where { <http://example.org/kennedy/person1> <http://purl.org/dc/elements/1.1/title>  \"Person\'s title\"@en }");

        SPARQLQueryDefinition qdefTh = sparqlQmgrTh.newQueryDefinition(sparqlQueryTh.toString());

        // Verify result in t2 transaction.
        try {
          // sleep for 5 seconds for main thread to finish commit.
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // By this time the other transaction should (could have?) have
        // commited.
        boolean bAskInTransT2 = sparqlQmgrTh.executeAsk(qdefTh, t2);
        System.out.println("Method ExecuteAskSecondThreadTrue Run result is " + bAskInTransT2);
        assertTrue( bAskInTransT2);
        setbCompleted(true);

      } catch (ForbiddenUserException e) {
        e.printStackTrace();
      } catch (FailedRequestException e) {
        e.printStackTrace();
      }
      if (t2 != null) {
        t2.rollback();
        t2 = null;
      }
    }
  }

  @BeforeAll
  public static void setUp() throws Exception
  {
    System.out.println("In SPARQL Query Manager Test setup");

    newline = System.getProperty("line.separator");
    customGraph = "TestCustomGraph";
    datecustomGraph = "TestDateCustomGraph";
    enlocaleGraphName = "englishLocale";
    inferenceGraph = "TestInferenceGraph";
    multibyteGraphName = new String("万里长城");

    datasource = "src/test/java/com/marklogic/client/functionaltest/data/semantics/";

    // localGraphName will have the value of beijing in Chinese which is 北京
    zhlocaleGraphName = new String("北京");

    writeclient = getDatabaseClient("rest-writer", "x", getConnType());
    readclient = getDatabaseClient("rest-reader", "x", getConnType());
    client = getDatabaseClient("rest-admin", "x", getConnType());

    // Build up custom data.
    StringBuffer sparqldata = new StringBuffer().append("prefix ad: <http://marklogicsparql.com/addressbook#>");
    sparqldata.append(newline);
    sparqldata.append("prefix id:  <http://marklogicsparql.com/id#>");
    sparqldata.append(newline);
    sparqldata.append("id:1111 ad:firstName \"John\" .");
    sparqldata.append("id:1111 ad:lastName  \"Snelson\" .");
    sparqldata.append("id:1111 ad:homeTel   \"(111) 111-1111\" .");
    sparqldata.append("id:1111 ad:email     \"John.Snelson@marklogic.com\" .");

    sparqldata.append(newline);
    sparqldata.append("id:2222 ad:firstName \"Micah\" .");
    sparqldata.append("id:2222 ad:lastName  \"Dubinko\" .");
    sparqldata.append("id:2222 ad:homeTel   \"(222) 222-2222\" .");
    sparqldata.append("id:2222 ad:email     \"Micah.Dubinko@marklogic.com\" .");

    sparqldata.append(newline);
    sparqldata.append("id:3333 ad:firstName \"Fei\" .");
    sparqldata.append("id:3333 ad:lastName  \"Ling\" .");
    sparqldata.append("id:3333 ad:email     \"FeiLing@yahoo.com\" .");
    sparqldata.append("id:3333 ad:email     \"Fei.Ling@marklogic.com\" .");

    sparqldata.append(newline);
    sparqldata.append("id:4444 ad:firstName \"Ling\" .");
    sparqldata.append("id:4444 ad:lastName  \"Ling\" .");
    sparqldata.append("id:4444 ad:email     \"lingling@yahoo.com\" .");
    sparqldata.append("id:4444 ad:email     \"Ling.Ling@marklogic.com\" .");

    sparqldata.append(newline);
    sparqldata.append("id:5555 ad:firstName \"Fei\" .");
    sparqldata.append("id:5555 ad:lastName  \"Xiang\" .");
    sparqldata.append("id:5555 ad:email     \"FeiXiang@yahoo.comm\" .");
    sparqldata.append("id:5555 ad:email     \"Fei.Xiang@marklogic.comm\" .");

    sparqldata.append("id:6666 ad:firstName \"Lei\" .");
    sparqldata.append("id:6666 ad:lastName  \"Pei\" .");
    sparqldata.append("id:6666 ad:homeTel   \"(666) 666-6666\" .");
    sparqldata.append("id:6666 ad:email     \"Lei.Pei@gmail.com\" .");

    sparqldata.append("id:7777 ad:firstName \"Meng\" .");
    sparqldata.append("id:7777 ad:lastName  \"Chen\" .");
    sparqldata.append("id:7777 ad:homeTel   \"(777) 777-7777\" .");
    sparqldata.append("id:7777 ad:email     \"Meng.Chen@gmail.com\" .");

    sparqldata.append("id:8888 ad:firstName \"Lihan\" .");
    sparqldata.append("id:8888 ad:lastName  \"Wang\" .");
    sparqldata.append("id:8888 ad:email     \"lihanwang@yahoo.com\" .");
    sparqldata.append("id:8888 ad:email     \"Lihan.Wang@gmail.com\" .");
    writeSPARQLDataFromString(sparqldata.toString(), customGraph);

    // Write the triples into a graph name with Multi-byte characters.
    writeSPARQLDataFromString(sparqldata.toString(), multibyteGraphName);

    // Insert the required Graphs and triples for most of all the tests.

    // NTRIPLES "application/n-triples"
    writeSPARQLDocumentUsingFileHandle(writeclient, datasource,
        "foaf1.nt", "http://marklogic.com/qatests/ntriples/", RDFMimeTypes.NTRIPLES);
    // TURTLE "text/turtle"
    // Pass null for uri so that this triple gets into default graph.
    writeSPARQLDocumentUsingFileHandle(writeclient, datasource,
        "livesIn.ttl", null, RDFMimeTypes.TURTLE);
    // N3 "text/n3"
    writeSPARQLDocumentUsingFileHandle(writeclient, datasource,
        "geo-states.n3", "http://marklogic.com/qatests/n3/", RDFMimeTypes.N3);

    // RDFXML "application/rdf+xml"
    writeSPARQLDocumentUsingFileHandle(writeclient, datasource,
        "rdfxml1.rdf", "rdfxml", RDFMimeTypes.RDFXML);
    // RDFJSON "application/rdf+json"
    writeSPARQLDocumentUsingFileHandle(writeclient, datasource,
        "rdfjson.json", "rdfjson", RDFMimeTypes.RDFJSON);

    // Build custom data for Ruleset and Inference tests
    writeNTriplesFromFile("inference.ttl", inferenceGraph);
    writeNTriplesFromFile("englishlocale.ttl", enlocaleGraphName);
    /*
     * Build custom data for Locale test. From Google translation We have Ling
     * in Chinese as 凌 We have Fei in Chinese as 飞
     *
     * We will have these triples in a graph called 北京 (beijing) in Chinese.
     */
    writeNTriplesFromFile("chineselocale.ttl", zhlocaleGraphName);

    // Build up custom data.
    StringBuffer sparqldatedata = new StringBuffer().append("prefix ad: <http://marklogicsparql.com/addressbook#>");
    sparqldatedata.append(newline);
    sparqldatedata.append("prefix xs: <http://www.w3.org/2001/XMLSchema#>");
    sparqldatedata.append(newline);
    sparqldatedata.append("prefix bb: <http://marklogic.com/baseball/players#>");
    sparqldatedata.append(newline);

    sparqldatedata.append("bb:6 bb:playerid \"6\"^^xs:integer .");
    sparqldatedata.append("bb:6 bb:lastname \"Abad\" .");
    sparqldatedata.append("bb:6 bb:firstname \"Fernando\" .");
    sparqldatedata.append("bb:6 bb:position  \"pitcher\" .");
    sparqldatedata.append("bb:6 bb:number  \"56\" .");
    sparqldatedata.append("bb:6 bb:team   \"Athletics\" .");
    sparqldatedata.append("bb:6 bb:throws  \"left\" .");
    sparqldatedata.append("bb:6 bb:bats   \"left\" .");
    sparqldatedata.append("bb:6 bb:weight  \"220\" .");
    sparqldatedata.append("bb:6 bb:birthdate \"1985-12-17\"^^xs:date .");
    sparqldatedata.append(newline);

    sparqldatedata.append("bb:7 bb:playerid \"7\"^^xs:integer .");
    sparqldatedata.append("bb:7 bb:lastname  \"Chavez\" .");
    sparqldatedata.append("bb:7 bb:firstname \"Jesse\" .");
    sparqldatedata.append("bb:7 bb:position  \"pitcher\" .");
    sparqldatedata.append("bb:7 bb:number  \"60\" .");
    sparqldatedata.append("bb:7 bb:team   \"Athletics\" .");
    sparqldatedata.append("bb:7 bb:throws  \"right\" .");
    sparqldatedata.append("bb:7 bb:bats   \"right\" .");
    sparqldatedata.append("bb:7 bb:weight  \"160\" .");
    sparqldatedata.append("bb:7 bb:birthdate \"1983-08-21\"^^xs:date .");

    writeSPARQLDataFromString(sparqldatedata.toString(), datecustomGraph);
  }

  /*
   * This test checks a simple SPARQL query results from named graph. The
   * database should contain triples from foaf1.nt.
   *
   * The query should be returning two results ordered by name.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testNamedExecuteSelectQuery()
  {
    System.out.println("In SPARQL Query Manager Test testNamedExecuteSelectQuery method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();
    StringBuffer sparqlQuery = new StringBuffer().append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQuery.append("SELECT ?name from <http://marklogic.com/qatests/ntriples/foaf1.nt>");
    sparqlQuery.append("WHERE { ?alum foaf:schoolHomepage <http://www.ucsb.edu/> .");
    sparqlQuery.append("?alum foaf:knows ?person .");
    sparqlQuery.append("?person foaf:name ?name }");
    sparqlQuery.append("ORDER BY ?name");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    JsonNode jsonResults = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");

    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());

    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();
    JsonNode jsonNameNode = null;
    if (nameNodesItr.hasNext()) {
      jsonNameNode = nameNodesItr.next();
      // Verify result 1's values.
      assertEquals( "Karen Schouten", jsonNameNode.path("name").path("value").asText());
      assertEquals( "literal", jsonNameNode.path("name").path("type").asText());
    }

    if (nameNodesItr.hasNext()) {
      // Verify result 2's values.
      jsonNameNode = nameNodesItr.next();
      assertEquals( "Nick Aster", jsonNameNode.path("name").path("value").asText());
      assertEquals( "literal", jsonNameNode.path("name").path("type").asText());
    }
  }

  /*
   * This test checks a simple SPARQL query results from default graph. The
   * database should contain triples from livesIn.ttl.
   *
   * The query should be returning three results ordered by name.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testDefaultExecuteSelectQuery()
  {
    System.out.println("In SPARQL Query Manager Test testDefaultExecuteSelectQuery method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();
    StringBuffer sparqlQuery = new StringBuffer().append("select ?name ?lives ?city from <http://marklogic.com/semantics#default-graph>{ ?name ?lives ?city } ORDER BY ?name");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
    JsonNode jsonResults = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());

    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();
    JsonNode jsonNameNode = null;
    if (nameNodesItr.hasNext()) {
      jsonNameNode = nameNodesItr.next();
      // Verify result 1 values.
      assertEquals( "http://example.org/ml/people/Jack_Smith", jsonNameNode.path("name").path("value").asText());
      assertEquals( "livesIn", jsonNameNode.path("lives").path("value").asText());
      assertEquals( "Glasgow", jsonNameNode.path("city").path("value").asText());
    }
    if (nameNodesItr.hasNext()) {
      // Verify result 2 values.
      jsonNameNode = nameNodesItr.next();
      assertEquals( "http://example.org/ml/people/Jane_Smith", jsonNameNode.path("name").path("value").asText());
      assertEquals( "livesIn", jsonNameNode.path("lives").path("value").asText());
      assertEquals( "London", jsonNameNode.path("city").path("value").asText());
    }
    if (nameNodesItr.hasNext()) {
      // Verify result 3 values.
      jsonNameNode = nameNodesItr.next();
      assertEquals( "http://example.org/ml/people/John_Smith", jsonNameNode.path("name").path("value").asText());
      assertEquals( "livesIn", jsonNameNode.path("lives").path("value").asText());
      assertEquals( "London", jsonNameNode.path("city").path("value").asText());
    }
  }

  /*
   * This test checks a simple SPARQL query results from named graph in a
   * transaction. The database should contain triples from geo-states.n3.
   *
   * The query should be returning one result.
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testExecuteQueryInTransaction()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteQueryInTransaction method");
    Transaction t = writeclient.openTransaction();
    try {
      SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

      StringBuffer sparqlQuery = new StringBuffer().append("PREFIX usgovt: <tag:govshare.info,2005:rdf/usgovt/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix dc: <http://purl.org/dc/elements/1.1/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix census: <tag:govshare.info,2005:rdf/census/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix census: <tag:govshare.info,2005:rdf/census/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("SELECT ?stateCode ?population ?statename from <http://marklogic.com/qatests/n3/geo-states.n3> ");
      sparqlQuery.append("WHERE { ?stateCode usgovt:censusStateCode \"63\" .");
      sparqlQuery.append("?stateCode census:population ?population .");
      sparqlQuery.append("?stateCode dc:title ?statename . }");

      SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
      // Select in a transaction.
      String jsonStrResults = sparqlQmgr.executeSelect(qdef, new StringHandle(), t).get();
      ObjectMapper mapper = new ObjectMapper();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr = mapper.readTree(jsonStrResults);
      System.out.println(jsonStrResults);
      JsonNode jsonBindingsNodes = jsonNodesFromStr.path("results").path("bindings");
      // Should have 1 node returned.
      assertEquals( 1, jsonBindingsNodes.size());

      // Verify results.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/al", jsonBindingsNodes.get(0).path("stateCode").path("value").asText());
      assertEquals( "4447100", jsonBindingsNodes.get(0).path("population").path("value").asText());
      assertEquals( "Alabama", jsonBindingsNodes.get(0).path("statename").path("value").asText());
      // Handle the transaction.
      t.commit();
      t = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally
    {
      if (t != null)
      {
        t.rollback();
        t = null;
      }
    }
  }

  /*
   * This test checks a simple SPARQL query pagination from named graph in a
   * transaction. The database should contain triples from geo-states.n3.
   *
   * The query should be returning states where population is greater than 5
   * million ordered by population count ascending.
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testPaginationInTransaction()
  {
	  if (markLogicVersion.getMajor() >= 12) {
		  // Disabled until MLE-12708 is fixed.
		  return;
	  }
    System.out.println("In SPARQL Query Manager Test testPaginationInTransaction method");
    Transaction t = writeclient.openTransaction();
    try {
      SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

      StringBuffer sparqlQuery = new StringBuffer().append("PREFIX usgovt: <tag:govshare.info,2005:rdf/usgovt/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix dc: <http://purl.org/dc/elements/1.1/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix census: <tag:govshare.info,2005:rdf/census/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("prefix census: <tag:govshare.info,2005:rdf/census/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("SELECT ?stateCode ?population ?statename from <http://marklogic.com/qatests/n3/geo-states.n3> ");
      sparqlQuery.append("WHERE { ?stateCode census:population ?population . ");
      sparqlQuery.append("?stateCode dc:title ?statename . ");
      sparqlQuery.append("filter (?population > 5000000) }");
      sparqlQuery.append(" ORDER by (?population)");

      SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
      // Select in a transaction with start = 1 and page length = 1.
      sparqlQmgr.setPageLength(1);
      String jsonStrResults = sparqlQmgr.executeSelect(qdef, new StringHandle(), 1, t).get();
      ObjectMapper mapper = new ObjectMapper();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr = mapper.readTree(jsonStrResults);
      System.out.println(jsonStrResults);
      JsonNode jsonBindingsNodes = jsonNodesFromStr.path("results").path("bindings");
      // Should have 1 node returned. Details of State - Arizona.
      assertEquals( 1, jsonBindingsNodes.size());
      System.out.println("testPaginationInTransaction query 1 result size is " + jsonBindingsNodes.size());

      // Verify results.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/az", jsonBindingsNodes.get(0).path("stateCode").path("value").asText());
      assertEquals( "5130632", jsonBindingsNodes.get(0).path("population").path("value").asText());
      assertEquals( "Arizona", jsonBindingsNodes.get(0).path("statename").path("value").asText());

      // Select in a transaction with start = 2 and page length = 1.
      /*
       * Order of states returned are : AZ, MD, WS, MS, TN, WA, IN, MA, VR, NC,
       * GA, NJ, MI, OH, PA, IL..... We have set page length = 1 and start from
       * result 2. Should skip Arizona and return only Maryland
       */
      sparqlQmgr.setPageLength(1);
      String jsonStrResults2 = sparqlQmgr.executeSelect(qdef, new StringHandle(), 2, t).get();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr2 = mapper.readTree(jsonStrResults2);
      System.out.println(jsonStrResults2);
      JsonNode jsonBindingsNodes2 = jsonNodesFromStr2.path("results").path("bindings");

      assertEquals( 1, jsonBindingsNodes2.size());
      System.out.println("testPaginationInTransaction query 2 result size is " + jsonBindingsNodes2.size());

      // Verify results - Details of State - Maryland.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/md", jsonBindingsNodes2.get(0).path("stateCode").path("value").asText());
      assertEquals( "5296486", jsonBindingsNodes2.get(0).path("population").path("value").asText());
      assertEquals( "Maryland", jsonBindingsNodes2.get(0).path("statename").path("value").asText());

      // Select in a transaction with start = 2 and page length = 3.
      /*
       * Order of states returned are : AZ, MD, WS, MS, TN, WA, IN, MA, VR, NC,
       * GA, NJ, MI, OH, PA, IL..... We have set page length = 3 and start from
       * result 2. Should skip Arizona and return Maryland, Wisconsin, Missouri
       */
      sparqlQmgr.setPageLength(3);
      String jsonStrResults3 = sparqlQmgr.executeSelect(qdef, new StringHandle(), 2, t).get();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr3 = mapper.readTree(jsonStrResults3);
      System.out.println(jsonStrResults3);

      assertEquals( 3, jsonNodesFromStr3.path("results").path("bindings").size());
      System.out.println("testPaginationInTransaction query 3 result is " + jsonNodesFromStr3.path("results").path("bindings").size());

      Iterator<JsonNode> jsonBindingsNodesItr3 = jsonNodesFromStr3.path("results").path("bindings").elements();
      JsonNode jsonItrNode1 = jsonBindingsNodesItr3.next();
      // Verify results - Details of State - Maryland.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/md", jsonItrNode1.path("stateCode").path("value").asText());
      assertEquals( "5296486", jsonItrNode1.path("population").path("value").asText());
      assertEquals( "Maryland", jsonItrNode1.path("statename").path("value").asText());

      JsonNode jsonItrNode2 = jsonBindingsNodesItr3.next();
      // Verify results - Details of State - Wisconsin.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/wi", jsonItrNode2.path("stateCode").path("value").asText());
      assertEquals( "5363675", jsonItrNode2.path("population").path("value").asText());
      assertEquals( "Wisconsin", jsonItrNode2.path("statename").path("value").asText());

      JsonNode jsonItrNode3 = jsonBindingsNodesItr3.next();
      // Verify results - Details of State - Wisconsin.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us/mo", jsonItrNode3.path("stateCode").path("value").asText());
      assertEquals( "5595211", jsonItrNode3.path("population").path("value").asText());
      assertEquals( "Missouri", jsonItrNode3.path("statename").path("value").asText());

      assertEquals( 3, sparqlQmgr.getPageLength());
      // Verify clear page length.
      sparqlQmgr.clearPageLength();
      assertEquals( -1, sparqlQmgr.getPageLength());
      // Test negative cases.

      // Select in a transaction with (java index) start = 21 and page length =
      // 2. Out of results' bounds
      sparqlQmgr.setPageLength(2);
      String jsonStrResultsNeg1 = sparqlQmgr.executeSelect(qdef, new StringHandle(), 21, t).get();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStrNeg1 = mapper.readTree(jsonStrResultsNeg1);
      System.out.println(jsonStrResultsNeg1);
      JsonNode jsonBindingsNodesNeg1 = jsonNodesFromStrNeg1.path("results").path("bindings");
      // Should have 1 node returned. Details of United States.
      assertEquals( 1, jsonBindingsNodesNeg1.size());
      System.out.println("testPaginationInTransaction negative query 1 result size is " + jsonBindingsNodesNeg1.size());
      // Verify results - Details of United States.
      assertEquals( "http://www.rdfabout.com/rdf/usgov/geo/us", jsonBindingsNodesNeg1.get(0).path("stateCode").path("value").asText());
      assertEquals( "281421906", jsonBindingsNodesNeg1.get(0).path("population").path("value").asText());
      assertEquals( "United States", jsonBindingsNodesNeg1.get(0).path("statename").path("value").asText());

      // Select in a transaction with (java index) start = 100 and page length =
      // 100.
      sparqlQmgr.setPageLength(100);
      String jsonStrResultsNeg2 = sparqlQmgr.executeSelect(qdef, new StringHandle(), 100, t).get();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStrNeg2 = mapper.readTree(jsonStrResultsNeg2);
      System.out.println(jsonStrResultsNeg2);
      JsonNode jsonBindingsNodesNeg2 = jsonNodesFromStrNeg2.path("results").path("bindings");
      // Should have 0 nodes returned.
      assertEquals( 0, jsonBindingsNodesNeg2.size());
      System.out.println("testPaginationInTransaction negative query 2 result size is " + jsonBindingsNodesNeg2.size());
      // Pass negative values.
      String expectedException = "IllegalArgumentException";
      String exception = "";
      try {
        // Select in a transaction with (java index) start = -1 and page length
        // = -1.
        sparqlQmgr.setPageLength(-1);
        String jsonStrResultsNeg3 = sparqlQmgr.executeSelect(qdef, new StringHandle(), -1, t).get();
      } catch (Exception e) {
        exception = e.toString();
      }
      System.out.println("Exception thrown from testQueryBindingsNullString is \n" + exception);
      assertTrue( exception.contains(expectedException));

      // Handle the transaction.
      t.commit();
      t = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (t != null) {
        t.rollback();
        t = null;
      }
    }
  }

  /*
   * This test checks a SPARQL construct query results. The database should
   * contain triples from foaf1.nt file. The data format in this file is XML.
   * Results are returned in a sequence of sem:triple values as triples in
   * memory.
   *
   * The query should be returning one result.
   *
   * Uses StringHandle (TriplesReadHandle)
   */
  @Test
  public void testExecuteConstruct()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteConstruct method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();

    StringBuffer sparqlQuery = new StringBuffer().append(" PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQuery.append(newline);
    sparqlQuery.append("CONSTRUCT {<http://www.ucsb.edu/random-alum> foaf:knows ?alum }");
    sparqlQuery.append(newline);
    sparqlQuery.append("where");
    sparqlQuery.append(newline);
    sparqlQuery.append("{ ?alum foaf:schoolHomepage <http://www.ucsb.edu/> }");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
    // Execute Construct query
    String[] jsonResults = sparqlQmgr.executeConstruct(qdef, new StringHandle()).get().split(" ");

    // Account for the dot at the end of the triple. Hence size is 4.
    assertEquals( 4, jsonResults.length);
    assertEquals( "<http://www.ucsb.edu/random-alum>", jsonResults[0]);
    assertEquals( "<http://xmlns.com/foaf/0.1/knows>", jsonResults[1]);
    assertEquals( "<1bfbfb8:ff2d706919:-7fa9>", jsonResults[2]);
  }

  /*
   * This test checks a SPARQL CONSTRUCT query results. The database should
   * contain triples from foaf1.nt file. The data format in this file is XML.
   * Results are returned in a sequence of sem:triple values as triples in
   * memory.
   *
   * The query should be returning one result. This test also verifies Base URI
   * and Git Issue 356, 358.
   *
   * Uses JacksonHandle and DOMHandle
   */
  @Test
  public void testBaseUri()
  {
    System.out.println("In SPARQL Query Manager Test testBaseUri method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer sparqlQuery = new StringBuffer().append(" PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQuery.append(newline);
    sparqlQuery.append("CONSTRUCT { <qatest1> <qatest2> <qatest3> }");
    sparqlQuery.append(newline);
    sparqlQuery.append("WHERE { ?s ?p ?o . } LIMIT 1");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());

    // Base URI set.
    qdef.setBaseUri("http://qa.marklogic.com/functional/tests/one/");

    // Verifying Git Issue 356 also, below with JacksonHandle.
    JacksonHandle jh = new JacksonHandle();
    JsonNode jsonResults = sparqlQmgr.executeConstruct(qdef, jh).get();
    String s = jsonResults.fieldNames().next();
    String p = jsonResults.get(s).fieldNames().next();
    JsonNode o = jsonResults.get(s).get(p);

    // Verify the mimetype of the handle.
    assertEquals( RDFMimeTypes.RDFJSON, jh.getMimetype());
    assertEquals( "http://qa.marklogic.com/functional/tests/one/qatest1", s);
    assertEquals( "http://qa.marklogic.com/functional/tests/one/qatest2", p);
    assertEquals( "http://qa.marklogic.com/functional/tests/one/qatest3", o.path(0).path("value").asText());

    // Verify if defaulting to RDFJSON when XMLHandle is used. Git Issue 356.
    DOMHandle handle = new DOMHandle();
    Document xmlDoc = sparqlQmgr.executeConstruct(qdef, handle).get();
    Node description = xmlDoc.getFirstChild().getFirstChild();
    NamedNodeMap attrs = description.getFirstChild().getAttributes();

    String oXML = attrs.getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource").getTextContent();

    assertEquals( RDFMimeTypes.RDFXML, handle.getMimetype());
    assertEquals( "http://qa.marklogic.com/functional/tests/one/qatest1", description.getAttributes().item(0)
        .getTextContent());
    assertEquals( "qatest2", description.getFirstChild().getNodeName());
    assertEquals( "http://qa.marklogic.com/functional/tests/one/qatest3", oXML);
  }

  /*
   * This test checks a SPARQL CONSTRUCT query results. The database should
   * contain triples from foaf1.nt file. The data format in this file is XML.
   * Results are returned in a sequence of sem:triple values as triples in
   * memory.
   *
   * The query should be returning one result.
   *
   * Uses StringHandle (TriplesReadHandle)
   */
  @Test
  public void testExecuteConstructInTransaction()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteConstructInTransaction method");
    Transaction t = writeclient.openTransaction();

    try {
      SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

      StringBuffer sparqlQuery = new StringBuffer().append(" PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
      sparqlQuery.append(newline);
      sparqlQuery.append("CONSTRUCT {<http://www.ucsb.edu/random-alum> foaf:knows ?alum }");
      sparqlQuery.append(newline);
      sparqlQuery.append("where");
      sparqlQuery.append(newline);
      sparqlQuery.append("{ ?alum foaf:schoolHomepage <http://www.ucsb.edu/> }");

      SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
      // Execute Construct query with RDFMimeTypes.NTRIPLES
      String[] jsonResults = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES), t).get().split(" ");

      // Account for the dot at the end of the triple. Hence size is 4.
      assertEquals( 4, jsonResults.length);
      assertEquals( "<http://www.ucsb.edu/random-alum>", jsonResults[0]);
      assertEquals( "<http://xmlns.com/foaf/0.1/knows>", jsonResults[1]);
      assertEquals( "<1bfbfb8:ff2d706919:-7fa9>", jsonResults[2]);

      // Tests for additional MIME Type.

      ObjectMapper mapper = new ObjectMapper();

      String strRDFJSON = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.RDFJSON), t).get().toString();
      System.out.println("\n RDFJSON format is " + strRDFJSON);

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr = mapper.readTree(strRDFJSON);
      String strValue = jsonNodesFromStr.path("http://www.ucsb.edu/random-alum").path("http://xmlns.com/foaf/0.1/knows").get(0).path("value").asText();
      assertEquals( "1bfbfb8:ff2d706919:-7fa9", strValue);

      String strRDFXML = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.RDFXML), t).get().toString();

      System.out.println("\n RDFXML Format is " + strRDFXML);
      assertTrue( strRDFXML.contains("rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""));
      assertTrue( strRDFXML.contains("rdf:Description rdf:about=\"http://www.ucsb.edu/random-alum\""));
      assertTrue( strRDFXML.contains("knows rdf:resource=\"1bfbfb8:ff2d706919:-7fa9\""));

      String strTRIPLEXML = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.TRIPLEXML), t).get().toString();
      System.out.println("\n TRIPLEXML format is " + strTRIPLEXML);
      assertTrue( strTRIPLEXML.contains("<sem:subject>http://www.ucsb.edu/random-alum</sem:subject>"));
      assertTrue( strTRIPLEXML.contains("<sem:predicate>http://xmlns.com/foaf/0.1/knows</sem:predicate>"));
      assertTrue( strTRIPLEXML.contains("<sem:object>1bfbfb8:ff2d706919:-7fa9</sem:object>"));

      String strTURTLE = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.TURTLE), t).get().toString();
      System.out.println("\n TURTLE format is " + strTURTLE);
      assertTrue( strTURTLE.contains("<http://www.ucsb.edu/random-alum>"));
      assertTrue( strTURTLE.contains("foaf:knows"));
      assertTrue( strTURTLE.contains("<1bfbfb8:ff2d706919:-7fa9>"));

      // String strN3 = sparqlQmgr.executeConstruct(qdef, new
      // StringHandle().withMimetype(RDFMimeTypes.N3), t).get().toString();
      // System.out.println("\n N3 format is " + strN3);

      String strNTriples = sparqlQmgr.executeConstruct(qdef, new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES), t).get().toString();
      System.out.println("\n NTRIPLES format is " + strNTriples);
      assertTrue( strNTriples.contains("<http://www.ucsb.edu/random-alum>"));
      assertTrue( strNTriples.contains("<http://xmlns.com/foaf/0.1/knows>"));
      assertTrue( strNTriples.contains("<1bfbfb8:ff2d706919:-7fa9>"));

      // Handle the transaction.
      t.commit();
      t = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (t != null) {
        t.rollback();
        t = null;
      }
    }
  }

  /*
   * This test checks a simple SPARQL DESCRIBE results from named graph. The
   * database should contain triples from rdfjson.json. Verifies Git Issue 356,
   * 358
   *
   * The DESCRIBE query should be returning one result. Result includes all
   * triples which have the IRI as a subject
   *
   * Uses Stringhandle and JacksonHandle.
   */
  @Test
  public void testExecuteDescribeFromRDFJSON()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteDescribeFromRDFJSON method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();
    StringBuffer sparqlQuery = new StringBuffer().append("DESCRIBE <http://example.org/about>");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
    String jsonResults = sparqlQmgr.executeDescribe(qdef, new StringHandle()).get();
    // Verify result 1 value.
    System.out.println(jsonResults);
    String[] resultString = jsonResults.split(" ");
    assertEquals( "<http://example.org/about>", resultString[0]);
    assertEquals( "<http://purl.org/dc/elements/1.1/title>", resultString[1]);
    String objectStr = resultString[2] + " " + resultString[3];
    assertEquals( "\"Anna's Homepage\"", objectStr);

    // Verifying default mime types- Using JacksonHandle
    JacksonHandle jh = new JacksonHandle();
    JsonNode jsonNodes = sparqlQmgr.executeDescribe(qdef, jh).get();
    JsonNode jsonNodeValue = jsonNodes.path("http://example.org/about").path("http://purl.org/dc/elements/1.1/title").get(0);

    // Verify Mime type on the handle and value.
    System.out.println("testQueryOnMultibyeGraphName query result size is " + jsonNodeValue.get("value"));
    assertEquals(RDFMimeTypes.RDFJSON, jh.getMimetype());
    assertEquals( "Anna\'s Homepage", jsonNodeValue.get("value").asText());

    // Verifying default mime types- Using DOMHandle
    DOMHandle dh = new DOMHandle();
    Document xmlDoc = sparqlQmgr.executeDescribe(qdef, dh).get();

    Node description = xmlDoc.getFirstChild().getFirstChild();
    NamedNodeMap attrs = description.getFirstChild().getAttributes();
    // attrs NodeMap should have two nodes.
    assertEquals( 2, attrs.getLength());

    if (attrs.item(0).getNodeName().equalsIgnoreCase("rdf:datatype"))
    {
      assertEquals( "http://www.w3.org/2001/XMLSchema#string", attrs.item(0).getNodeValue());
      assertEquals( "http://purl.org/dc/elements/1.1/", attrs.item(1).getNodeValue());
    }
    else if (attrs.item(0).getNodeName().equalsIgnoreCase("xmlns"))
    {
      assertEquals( "http://www.w3.org/2001/XMLSchema#string", attrs.item(1).getNodeValue());
      assertEquals( "http://purl.org/dc/elements/1.1/", attrs.item(0).getNodeValue());
    }
    String value = description.getFirstChild().getFirstChild().getNodeValue();
    assertEquals( "Anna\'s Homepage", value);
  }

  /*
   * This test checks a simple SPARQL DESCRIBE results from named graph in
   * transaction. The database should contain triples from geo-states.n3.
   *
   * The DESCRIBE query should be returning result. Result includes all triples
   * which have the IRI as a subject.
   *
   * Uses Stringhandle
   */
  @Test
  public void testExecuteDescribeInTransaction()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteDescribeInTransaction method");
    Transaction t = writeclient.openTransaction();

    try {
      SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

      StringBuffer sparqlQuery = new StringBuffer().append("DESCRIBE <http://www.rdfabout.com/rdf/usgov/geo/us/wi>");

      SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
      String jsonResults = sparqlQmgr.executeDescribe(qdef, new StringHandle(), t).get();
      // Split the string containing series of triples on the last characters
      // i.e a space and dot.

      // Verify result 1 value.
      System.out.println(jsonResults);
      assertEquals( 13, jsonResults.split(" \\.").length);

      // Negative test cases.
      // Have Invalid URI
      StringBuffer sparqlInvalidQuery = new StringBuffer().append("DESCRIBE <http://www.rdfabout.com/rdf/usgov/geo/us/blahblah>");

      SPARQLQueryDefinition qdefInvalid = sparqlQmgr.newQueryDefinition(sparqlInvalidQuery.toString());
      String jsonInvalidResults = sparqlQmgr.executeDescribe(qdefInvalid, new StringHandle(), t).get();
      ObjectMapper mapper = new ObjectMapper();

      // Parsing results using JsonNode.
      JsonNode jsonNodesFromStr = mapper.readTree(jsonInvalidResults);
      JsonNode jsonBindingsNodes = jsonNodesFromStr.path("results").path("bindings");

      // Verify result 0 elements exists.
      System.out.println("DESCRIBE with Invalid URI size is : " + jsonBindingsNodes.size());
      assertEquals( 0, jsonBindingsNodes.size());

      // Have missing enclosing
      StringBuffer sparqlInvalidQuery1 = new StringBuffer().append("DESCRIBE http://www.rdfabout.com/rdf/usgov/geo/us/wi");
      String expectedException = "FailedRequestException";
      String exception = "";
      SPARQLQueryDefinition qdefInvalid1 = sparqlQmgr.newQueryDefinition(sparqlInvalidQuery1.toString());
      try {
        sparqlQmgr.executeDescribe(qdefInvalid1, new StringHandle(), t).get();
      } catch (Exception e) {
        exception = e.toString();
      }
      System.out.println("Exception thrown from testExecuteDescribeInTransaction is \n" + exception);
      assertTrue(
          exception.contains(expectedException));

      // Handle the transaction.
      t.commit();
      t = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (t != null) {
        t.rollback();
        t = null;
      }
    }
  }

  /*
   * This test checks a simple SPARQL ASK results from named graph. The database
   * should contain triples from foaf1.nt file. The data format in this file is
   * XML.
   *
   * The ASK query should be returning result in boolean. Expected value True
   *
   * Uses Stringhandle
   */
  @Test
  public void testExecuteAsk()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteAsk method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();

    StringBuffer sparqlQueryTrue = new StringBuffer().append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQueryTrue.append(newline);
    sparqlQueryTrue.append("ASK { ?alum foaf:schoolHomepage <http://www.ucsb.edu/> }");
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQueryTrue.toString());
    boolean bAskTrue = sparqlQmgr.executeAsk(qdef);

    // Verify result 1 value.
    System.out.println("Checking for true value in testexecuteAsk method : " + bAskTrue);
    assertTrue( bAskTrue);

    StringBuffer sparqlQueryfalse = new StringBuffer().append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQueryfalse.append(newline);
    sparqlQueryfalse.append("ASK { ?alum foaf:schoolHomepage <http://www.blahblah.edu/> }");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(sparqlQueryfalse.toString());
    boolean bAskFalse = sparqlQmgr.executeAsk(qdef1);

    // Verify result 1 value.
    System.out.println("Checking for false value in testExecuteAsk method : " + bAskFalse);
    assertFalse( bAskFalse);

    // Negative test case - An empty ASK returns true.
    StringBuffer sparqlQueryEmpty = new StringBuffer().append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlQueryEmpty.append(newline);
    sparqlQueryEmpty.append("ASK { }");
    SPARQLQueryDefinition qdefEmpty = sparqlQmgr.newQueryDefinition(sparqlQueryEmpty.toString());
    boolean bAskEmpty = sparqlQmgr.executeAsk(qdefEmpty);

    // Verify result 1 value.
    System.out.println("Checking for true value in testExecuteAsk method with empty ASK : " + bAskEmpty);
    assertTrue( bAskEmpty);
  }

  /*
   * This test checks a simple SPARQL ASK results from named graph in a
   * transaction. The database contains triples from rdfxml1.rdf file. The data
   * format in this file is RDFXML.
   *
   * The ASK query should be returning result in boolean.
   */
  @Test
  public void testExecuteAskInTransactions()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteAskInTransactions method");
    Transaction t1 = null;
    Transaction tAfterRollback = null;
    Transaction tAnother = null;
    try {

      SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
      t1 = writeclient.openTransaction();
      StringBuffer sparqlQuery = new StringBuffer();
      sparqlQuery.append("ASK FROM <rdfxml> where { <http://example.org/kennedy/person1> <http://purl.org/dc/elements/1.1/title>  \"Person\'s title\"@en }");

      SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());
      boolean bAskNoWrite = sparqlQmgr.executeAsk(qdef, t1);

      // Verify result.
      System.out.println(bAskNoWrite);
      assertFalse( bAskNoWrite);

      // RDFXML "application/rdf+xml". Get the content into FileHandle
      File file = new File(datasource + "rdfxml1.rdf");
      FileHandle filehandle = new FileHandle();
      filehandle.set(file);

      // Create Graph manager
      GraphManager sparqlGmgr = writeclient.newGraphManager();

      // Write the triples in the doc into named graph.
      sparqlGmgr.write("rdfxml", filehandle.withMimetype(RDFMimeTypes.RDFXML), t1);

      // Verify result in t1 transaction.
      boolean bAskInTransT1 = sparqlQmgr.executeAsk(qdef, t1);
      System.out.println(bAskInTransT1);
      assertTrue( bAskInTransT1);

      // Thread1 should be blocked due to ML Server holding a write lock on the
      // record and returns false.
      ExecuteAskSecondThreadFalse thread1 = new ExecuteAskSecondThreadFalse();
      thread1.start();
      // Handle the transactions.
      t1.rollback();
      t1 = null;

      boolean bAskTransRolledback = sparqlQmgr.executeAsk(qdef);
      System.out.println(bAskTransRolledback);
      assertFalse( bAskTransRolledback);

      // After rollback. Open another transaction and verify ASK on that
      // transaction.
      tAfterRollback = writeclient.openTransaction();
      // Write the triples in the doc into either named graph.
      sparqlGmgr.write("rdfxml", filehandle.withMimetype(RDFMimeTypes.RDFXML), tAfterRollback);

      // Thread2 should be blocked and sleeping, while main thread commits.
      // Thread 2 returns true.
      ExecuteAskSecondThreadTrue thread2 = new ExecuteAskSecondThreadTrue();
      thread2.start();

      tAfterRollback.commit();
      tAfterRollback = null;
      int nCount = 0;
      // Now wait for thread2 to complete. To do - Producer/Consumer model
      // helps?.
      while (!thread2.isbCompleted()) {
        try {
          // Sleep main thread for 1 second and check for 10 times.
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (nCount++ > 10)
          break;
      }

      boolean bAskAfterCommit = sparqlQmgr.executeAsk(qdef);
      System.out.println(bAskAfterCommit);
      assertTrue( bAskAfterCommit);

      // After commit. Open another transaction and verify ASK on that
      // transaction.
      tAnother = writeclient.openTransaction();
      // Verify result.
      boolean bAskInAnotherTrans = sparqlQmgr.executeAsk(qdef, tAnother);
      System.out.println(bAskInAnotherTrans);
      assertTrue( bAskInAnotherTrans);

      // Handle the transaction.
      tAnother.commit();
      tAnother = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (t1 != null) {
        t1.rollback();
        t1 = null;
      }
      if (tAnother != null) {
        tAnother.rollback();
        tAnother = null;
      }
      if (tAfterRollback != null) {
        tAfterRollback.rollback();
        tAfterRollback = null;
      }
    }
  }

  /*
   * This test checks if Exceptions are throw when qdef is null. The database
   * should contain triples from geo-states.n3.
   *
   * Expected Result : IllegalArgumentException Exception thrown
   *
   * Uses JacksonHandle
   */
  @Test
  public void testExecuteSelectQueryNullQDEF()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteSelectQueryNullQDEF method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();

    String expectedException = "IllegalArgumentException";
    String exception = "";
    try {
      sparqlQmgr.executeSelect(null, new JacksonHandle()).get();
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println("Exception thrown from testExecuteSelectQueryNullQDEF is \n" + exception);
    assertTrue( exception.contains(expectedException));
  }

  /*
   * This test checks if Exceptions are throw when qdef is empty. The database
   * should contain triples from geo-states.n3.
   *
   * Expected Result : FailedRequestException Exception thrown
   *
   * Uses JacksonHandle
   */
  @Test
  public void testExecuteEmptySelectQuery()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteEmptySelectQuery method");
    SPARQLQueryManager sparqlQmgr = readclient.newSPARQLQueryManager();
    StringBuffer sparqlQuery = new StringBuffer().append("");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());

    String expectedException = "FailedRequestException";
    String exception = "";
    try {
      sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println("Exception thrown from testExecuteEmptySelectQuery is \n" + exception);
    assertTrue( exception.contains(expectedException));
  }

  /*
   * This test verifies query clauses with OPTIONAL and FILTER keywords on a
   * graph name with multi-byte characters. The database should contain triples
   * from multibyteGraphName graph.
   *
   * Expected Result : 1 solution contaiining person with id 4444
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testQueryOnMultibyeGraphName() throws Exception
  {
    System.out.println("In SPARQL Query Manager Test testQueryOnMultibyeGraphName method");
    // Form a query

    StringBuffer sparqlQuery = new StringBuffer().append("prefix ad: <http://marklogicsparql.com/addressbook#>");
    sparqlQuery.append(newline);
    sparqlQuery.append("prefix id:  <http://marklogicsparql.com/id#>");
    sparqlQuery.append(newline);
    sparqlQuery.append("SELECT DISTINCT ?person FROM <");
    sparqlQuery.append(multibyteGraphName);
    sparqlQuery.append(">");
    sparqlQuery.append(newline);
    sparqlQuery.append("WHERE { ?person ad:firstName ?firstname ;");
    sparqlQuery.append(newline);
    sparqlQuery.append(" ad:lastName ?lastname . ");
    sparqlQuery.append("OPTIONAL {?person ad:homeTel ?phonenumber .} ");
    sparqlQuery.append("FILTER (?firstname = \"Ling\")");
    sparqlQuery.append("}");

    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());

    String jsonStrResults = sparqlQmgr.executeSelect(qdef, new StringHandle()).get();
    ObjectMapper mapper = new ObjectMapper();

    // Parsing results using JsonNode.
    JsonNode jsonNodesFromStr = mapper.readTree(jsonStrResults);
    System.out.println(jsonStrResults);
    JsonNode jsonBindingsNodes = jsonNodesFromStr.path("results").path("bindings");
    // Should have 1 node returned.
    System.out.println("testQueryOnMultibyeGraphName query result size is " + jsonBindingsNodes.size());
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.get(0).path("person").path("value").asText());
  }

  /*
   * This test verifies query clauses with OPTIONAL and FILTER keywords. The
   * database should contain triples from testcustom graph.
   *
   * Expected Result : 1 solution contaiining person with id 4444
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testQueryClauseOptionalFilter() throws Exception
  {
    System.out.println("In SPARQL Query Manager Test testQueryClauseOptionalFilter method");
    // Form a query

    StringBuffer sparqlQuery = new StringBuffer().append("prefix ad: <http://marklogicsparql.com/addressbook#>");
    sparqlQuery.append(newline);
    sparqlQuery.append("prefix id:  <http://marklogicsparql.com/id#>");
    sparqlQuery.append(newline);
    sparqlQuery.append("SELECT DISTINCT ?person FROM <");
    sparqlQuery.append(customGraph);
    sparqlQuery.append(">");
    sparqlQuery.append(newline);
    sparqlQuery.append("WHERE { ?person ad:firstName ?firstname ;");
    sparqlQuery.append(newline);
    sparqlQuery.append(" ad:lastName ?lastname . ");
    sparqlQuery.append("OPTIONAL {?person ad:homeTel ?phonenumber .} ");
    sparqlQuery.append("FILTER (?firstname = \"Ling\")");
    sparqlQuery.append("}");

    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlQuery.toString());

    String jsonStrResults = sparqlQmgr.executeSelect(qdef, new StringHandle()).get();
    ObjectMapper mapper = new ObjectMapper();

    // Parsing results using JsonNode.
    JsonNode jsonNodesFromStr = mapper.readTree(jsonStrResults);
    System.out.println(jsonStrResults);
    JsonNode jsonBindingsNodes = jsonNodesFromStr.path("results").path("bindings");
    // Should have 1 node returned.
    System.out.println("testQueryClauseOptionalFilter query result size is " + jsonBindingsNodes.size());
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.get(0).path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on string. The database
   * should contain triples from TestCustomeGraph.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsOnString()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingString method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    SPARQLBindings bindings = qdef1.getBindings();
    bindings.bind("firstname", "Ling", RDFTypes.STRING);
    qdef1.setBindings(bindings);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("firstname"));

    assertEquals( 1, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings").get(0);

    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on string. Verify with
   * withBindings method The database should contain triples from
   * TestCustomeGraph.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testQueryWithBindingsOnString()
  {
    System.out.println("In SPARQL Query Manager Test testQueryWithBindingsOnString method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    qdef1.withBinding("firstname", "Ling", RDFTypes.STRING);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("firstname"));

    assertEquals( 1, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings").get(0);

    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on date. Verify with
   * withBindings method The database should contain triples from
   * TestDateCustomGraph. Verifies Git Issue 378. Uses JacksonHandle
   */
  @Test
  public void testQueryWithBindingsOnDate()
  {
    System.out.println("In SPARQL Query Manager Test testQueryWithBindingsOnDate method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX bb: <http://marklogic.com/baseball/players#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("prefix xs: <http://www.w3.org/2001/XMLSchema#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?birthdate");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(datecustomGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person bb:firstname ?firstname ;");
    queryStr.append(newline);
    queryStr.append("bb:lastname ?lastname;");
    queryStr.append(newline);
    queryStr.append("bb:birthdate ?birthdate.");
    queryStr.append(newline);
    queryStr.append("FILTER( ?birthdate=?bdate)");
    queryStr.append("}");

    System.out.println(queryStr.toString());
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    qdef1.withBinding("bdate", "1983-08-21", RDFTypes.DATE);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("bdate"));

    assertEquals( 1, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings").get(0);

    assertEquals("http://marklogic.com/baseball/players#7", jsonBindingsNodes.path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on string. The database
   * should contain triples from englishLocale graph.
   *
   * Uses JacksonHandle. Locale language used is en.
   */
  @Test
  public void testQueryBindingsOnStringWithENLocale()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsOnStringWithENLocale method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(enlocaleGraphName);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    SPARQLBindings bindings = qdef1.getBindings();

    // Set up the String variable binding with locale
    Locale enUSLocale = new Locale.Builder().setLanguage("en").build();

    bindings.bind("firstname", "Ling", enUSLocale);
    qdef1.setBindings(bindings);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("firstname"));

    assertEquals( 1, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings").get(0);

    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on string with Locale.
   * Verify with withBindings method The database should contain triples from
   * zhlocaleGraphName variable's value.
   *
   * We have Ling in Chinese as 凌 We have Fei in Chinese as 飞
   *
   * We will have these triples in a graph called 北京 (beijing) in Chinese.
   * Uses JacksonHandle
   */
  @Test
  public void testQueryWithBindingsOnMultiByteString()
  {
    System.out.println("In SPARQL Query Manager Test testQueryWithBindingsOnMultiByteString method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(zhlocaleGraphName);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding with locale
    Locale cnLocale = new Locale.Builder().setLanguage("zh").build();
    qdef1.withBinding("firstname", mbSearchStr, cnLocale);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("firstname"));

    assertEquals( 1, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings").get(0);

    assertEquals("http://marklogicsparql.com/id#4444", jsonBindingsNodes.path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on integer. The database
   * should contain triples from TestCustomeGraph.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsOnInteger()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsOnInteger method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname ?lastname ?cost");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("FILTER (?cost < 10)");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    SPARQLBindings bindings = qdef1.getBindings();
    bindings.bind("cost", "8", RDFTypes.INTEGER);
    qdef1.setBindings(bindings);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("cost"));
    assertEquals( 8, jsonStrResults.path("results").path("bindings").size());
  }

  /*
   * This test verifies ASK query definition with bindings on multiple string
   * values. The database should contain triples from TestCustomeGraph.
   *
   * Expected return : true Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsAskOnStrings()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsAskOnStrings method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("ASK");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("FILTER (?firstname = \"Ling\")");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    SPARQLBindings bindings = qdef1.getBindings();
    bindings.bind("firstname", "John", RDFTypes.STRING);
    qdef1.setBindings(bindings);
    bindings.bind("firstname", "Ling", RDFTypes.STRING);
    qdef1.setBindings(bindings);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);
    // Verify the bindings.
    assertEquals( 1, qdef1.getBindings().entrySet().size());
    assertEquals( "true", jsonStrResults.path("boolean").asText());
  }

  /*
   * This test verifies query definition with bindings on string variable with
   * null value. The database should contain triples from TestCustomeGraph.
   *
   * Expected result : IllegalArgumentException Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsNullString()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsNullString method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding
    SPARQLBindings bindings = qdef1.getBindings();

    String expectedException = "IllegalArgumentException";
    String exception = "";
    try {
      bindings.bind("firstname", null, RDFTypes.STRING);
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println("Exception thrown from testQueryBindingsNullString is \n" + exception);
    assertTrue( exception.contains(expectedException));
  }

  /*
   * This test verifies query definition with bindings on string. The database
   * should contain triples from TestCustomeGraph.
   *
   * Expected result : SPARQL considers that there is no binding and all results
   * are returned. Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsDifferentVariableName()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsDifferentVariableName method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    queryStr.append(newline);
    queryStr.append("ORDER BY ?firstname ?lastname");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding with wrong name
    SPARQLBindings bindings = qdef1.getBindings();
    bindings.bind("blahblah", "Ling", RDFTypes.STRING);
    qdef1.setBindings(bindings);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println(jsonStrResults);

    // Verify the bindings.
    assertTrue( qdef1.getBindings().containsKey("blahblah"));

    assertEquals( 8, jsonStrResults.path("results").path("bindings").size());
    JsonNode jsonBindingsNodes = jsonStrResults.path("results").path("bindings");
    // The results are ordered.
    assertEquals("http://marklogicsparql.com/id#3333", jsonBindingsNodes.get(0).path("person").path("value").asText());
    assertEquals("http://marklogicsparql.com/id#5555", jsonBindingsNodes.get(1).path("person").path("value").asText());
    assertEquals("http://marklogicsparql.com/id#1111", jsonBindingsNodes.get(2).path("person").path("value").asText());
    assertEquals( "http://marklogicsparql.com/id#6666", jsonBindingsNodes.get(3).path("person").path("value").asText());
    assertEquals( "http://marklogicsparql.com/id#8888", jsonBindingsNodes.get(4).path("person").path("value").asText());
    assertEquals( "http://marklogicsparql.com/id#4444", jsonBindingsNodes.get(5).path("person").path("value").asText());
    assertEquals( "http://marklogicsparql.com/id#7777", jsonBindingsNodes.get(6).path("person").path("value").asText());
    assertEquals( "http://marklogicsparql.com/id#2222", jsonBindingsNodes.get(7).path("person").path("value").asText());
  }

  /*
   * This test verifies query definition with bindings on SPARQL Update query
   * with integer data type. The database should contain triples from
   * TestCustomeGraph.
   *
   * Expected result : Id should have 30 in query results returned. Uses
   * JacksonHandle
   */
  @Test
  public void testSparqlUpdateInsertDataBinding()
  {
    System.out.println("In SPARQL Query Manager Test testSparqlUpdateInsertDataBinding method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryInsStr = new StringBuffer();

    queryInsStr.append("prefix df: <http://marklogic.com/default-data>");
    queryInsStr.append(newline);
    queryInsStr.append("prefix su-api: <http://marklogic.com/SparqlUpdate-API#>");
    queryInsStr.append(newline);
    queryInsStr.append("prefix bb: <http://marklogic.com/baseball/players#>");
    queryInsStr.append(newline);
    queryInsStr.append("prefix xs: <http://www.w3.org/2001/XMLSchema#>");
    queryInsStr.append(newline);
    queryInsStr.append("INSERT DATA");
    queryInsStr.append("{");
    queryInsStr.append(newline);
    queryInsStr.append("GRAPH <BindingsGraph>");
    queryInsStr.append(newline);
    queryInsStr.append("{");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:playerid	\"XX\" .");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:lastname		\"LASTNAME\" .");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:firstname	\"FIRSTNAME\" .");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:position		\"POSITION\" .");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:number		\"NUMBER\" .");
    queryInsStr.append(newline);
    queryInsStr.append("#bb:XX	bb:team			\"Athletics\" .");
    queryInsStr.append(newline);
    queryInsStr.append("bb:35	bb:playerid	?playerid .");
    queryInsStr.append(newline);
    queryInsStr.append("}");
    queryInsStr.append(newline);
    queryInsStr.append("}");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(queryInsStr.toString());

    // Set up the integer variable binding
    SPARQLBindings bindings = qdef.getBindings();
    bindings.bind("playerid", "30", RDFTypes.INTEGER);
    qdef.setBindings(bindings);

    sparqlQmgr.executeUpdate(qdef);

    // Query the graph and make sure we have 30 in the id

    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition().withSparql("select ?s ?p ?o from <BindingsGraph> where {?s ?p ?o. }");
    JsonNode jsonBindingsNodes = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get().path("results").path("bindings").get(0);

    // Should have 1 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    assertEquals( "http://marklogic.com/baseball/players#35", jsonBindingsNodes.path("s").path("value").asText());
    assertEquals( "http://marklogic.com/baseball/players#playerid", jsonBindingsNodes.path("p").path("value").asText());
    assertEquals( "30", jsonBindingsNodes.path("o").path("value").asText());

    // Verify BaseURI - Insert triples with valid URI
    SPARQLQueryDefinition qdef2 = sparqlQmgr.newQueryDefinition(queryInsStr.toString());
    // Set up the integer variable binding
    SPARQLBindings bindings2 = qdef2.getBindings();
    bindings2.bind("playerid", "30", RDFTypes.INTEGER);
    qdef2.setBindings(bindings2);
    // Set the base URI. This gets concatented to the relative URI
    // (BindingsGraph).
    String baseuri2 = "http://qa.marklogic.com/qdef2/";
    qdef2.setBaseUri(baseuri2);

    sparqlQmgr.executeUpdate(qdef2);
    String q21 = "select ?s ?p ?o from <" + baseuri2 + "BindingsGraph> where {?s ?p ?o. }";
    SPARQLQueryDefinition qdefValid = sparqlQmgr.newQueryDefinition().withSparql(q21);
    JsonNode jsonBindingsNodes21 = sparqlQmgr.executeSelect(qdefValid, new JacksonHandle()).get().path("results").path("bindings").get(0);

    // Should have 1 nodes returned.
    assertEquals( 3, jsonBindingsNodes21.size());
    assertEquals( "http://marklogic.com/baseball/players#35", jsonBindingsNodes21.path("s").path("value").asText());
    assertEquals( "http://marklogic.com/baseball/players#playerid", jsonBindingsNodes21.path("p").path("value").asText());
    assertEquals( "30", jsonBindingsNodes21.path("o").path("value").asText());

    // Verify with base URI set to null;
    SPARQLQueryDefinition qdefNull = sparqlQmgr.newQueryDefinition(queryInsStr.toString());
    // Set up the integer variable binding
    SPARQLBindings bindingsNull = qdefNull.getBindings();
    bindingsNull.bind("playerid", "30", RDFTypes.INTEGER);
    qdefNull.setBindings(bindingsNull);
    // Set the base URI. This gets concatented to the relative URI
    // (BindingsGraph).
    String baseuriNull = null;
    qdefNull.setBaseUri(baseuriNull);

    sparqlQmgr.executeUpdate(qdefNull);
    String qNull = "select ?s ?p ?o from <" + baseuriNull + "BindingsGraph> where {?s ?p ?o. }";
    SPARQLQueryDefinition qdefNullExeSel = sparqlQmgr.newQueryDefinition().withSparql(qNull);
    JsonNode jsonBindingsNodesNull = sparqlQmgr.executeSelect(qdefNullExeSel, new JacksonHandle()).get().path("results").path("bindings").get(0);

    // Zero nodes returned.
    assertNull( jsonBindingsNodesNull);

    // Verify with base URI set to empty;
    SPARQLQueryDefinition qdefEmpty = sparqlQmgr.newQueryDefinition(queryInsStr.toString());

    // Set up the integer variable binding
    SPARQLBindings bindingsEmpty = qdefEmpty.getBindings();
    bindingsEmpty.bind("playerid", "30", RDFTypes.INTEGER);
    qdefEmpty.setBindings(bindingsEmpty);

    // Set the base URI. This gets concatented to the relative URI
    // (BindingsGraph).
    String baseuriEmpty = "";
    qdefEmpty.setBaseUri(baseuriEmpty);
    String expectedException = "FailedRequestException";
    String exception = "";

    try {
      sparqlQmgr.executeUpdate(qdefEmpty);

    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println(exception);
    assertTrue( exception.contains(expectedException));

    String multibyteName = new String("万里长城");
    // Verify BaseURI - Insert triples with valid base URI containing MB string

    SPARQLQueryDefinition qdefMB = sparqlQmgr.newQueryDefinition(queryInsStr.toString());
    // Set up the integer variable binding
    SPARQLBindings bindingsMB = qdefMB.getBindings();
    bindingsMB.bind("playerid", "30", RDFTypes.INTEGER);
    qdefMB.setBindings(bindingsMB);
    // Set the base URI. This gets concatented to the relative URI
    // (BindingsGraph).
    String baseuriMB = "http://qa.marklogic.com/qdef2/" + multibyteName + "/";
    qdefMB.setBaseUri(baseuriMB);

    sparqlQmgr.executeUpdate(qdefMB);
    String qMB = "select ?s ?p ?o from <" + baseuriMB + "BindingsGraph> where {?s ?p ?o. }";
    SPARQLQueryDefinition qdefValidMB = sparqlQmgr.newQueryDefinition().withSparql(qMB);
    JsonNode jsonBindingsNodesMB = sparqlQmgr.executeSelect(qdefValidMB, new JacksonHandle()).get().path("results").path("bindings").get(0);

    // Should have 1 nodes returned.
    assertEquals( 3, jsonBindingsNodesMB.size());
    assertEquals( "http://marklogic.com/baseball/players#35", jsonBindingsNodesMB.path("s").path("value").asText());
    assertEquals( "http://marklogic.com/baseball/players#playerid", jsonBindingsNodesMB.path("p").path("value").asText());
    assertEquals( "30", jsonBindingsNodesMB.path("o").path("value").asText());

  }

  /*
   * This test verifies query definition bindings on SPARQL UPDATE command. The
   * test inserts a graph with triples and then verifies the binding value.
   *
   * Expected result : IllegalArgumentException and Invalid parameter: Bind
   * variable type parameter requires XSD type message Uses JacksonHandle
   */
  @Test
  public void testQueryBindingsIncorrectDataType()
  {
    System.out.println("In SPARQL Query Manager Test testQueryBindingsIncorrectDataType method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?firstname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("}");
    queryStr.append(newline);
    queryStr.append("ORDER BY ?firstname ?lastname");
    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString());

    // Set up the String variable binding with wrong data type.
    SPARQLBindings bindings = qdef1.getBindings();
    bindings.bind("firstname", "Ling", RDFTypes.DATETIME);
    qdef1.setBindings(bindings);

    String expectedException = "FailedRequestException";
    String exception = "";
    try {
      JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println("Exception thrown from testQueryBindingsIncorrectDataType is \n" + exception);
    assertTrue( exception.contains(expectedException));
    assertTrue( exception.contains("Invalid cast: \"Ling\" cast as xs:dateTime"));
  }

  /*
   * This test verifies sparql update CREATE GRAPH, INSERT DATA and also
   * validates SPARQL EXISTS. The test creates an empty graph first, INSERTS
   * DATA, checks for EXISTS and then tries create it again with SILENT option
   * enabled and disabled.
   *
   * Expected Result : When SILENT disabled error reported, if same graph is
   * created again. When SILENT enabled no error reported, if same graph is
   * created again.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testExecuteUpdateCreateSilent()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteUpdateCreateSilent method");
    // Form a query
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer sparqlCreateGraphQuery = new StringBuffer().append("CREATE GRAPH <DOMICLE>;");
    sparqlCreateGraphQuery.append(newline);

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqlCreateGraphQuery.toString());
    // Create an empty graph.
    sparqlQmgr.executeUpdate(qdef);
    qdef = null;

    // Insert triple into graph using INSERT DATA
    StringBuffer sparqlInsertData = new StringBuffer().append("PREFIX : <http://example.org/>");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlInsertData.append("INSERT DATA { GRAPH <DOMICLE> { ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":alice  rdf:type   foaf:Person .");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":alice  foaf:name  \"Alice\" . ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":bob    rdf:type   foaf:Person . ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("} } ");

    qdef = sparqlQmgr.newQueryDefinition(sparqlInsertData.toString());
    // Insert Data into the empty graph.
    sparqlQmgr.executeUpdate(qdef);
    // Wait for index
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    qdef = null;

    // Check for EXIST.
    StringBuffer sparqlExists = new StringBuffer();
    sparqlExists.append("PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
    sparqlExists.append(newline);
    sparqlExists.append("PREFIX  foaf:   <http://xmlns.com/foaf/0.1/>");
    sparqlExists.append(newline);
    sparqlExists.append("SELECT ?person ");
    sparqlExists.append(newline);
    sparqlExists.append("FROM <DOMICLE>");
    sparqlExists.append(newline);
    sparqlExists.append("WHERE");
    sparqlExists.append(newline);
    sparqlExists.append("{");
    sparqlExists.append("?person rdf:type  foaf:Person .");
    sparqlExists.append(newline);
    sparqlExists.append("FILTER EXISTS { ?person foaf:name ?name }");
    sparqlExists.append(newline);
    sparqlExists.append("}");
    qdef = sparqlQmgr.newQueryDefinition(sparqlExists.toString());

    // Perform Exists query.
    JsonNode jsonResults = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");

    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodes.size());
    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();
    JsonNode jsonPersonNode = null;
    if (nameNodesItr.hasNext()) {
      jsonPersonNode = nameNodesItr.next();
      assertEquals( "http://example.org/alice", jsonPersonNode.path("person").path("value").asText());
    }
    qdef = null;

    // Test to see if FailedRequestException is thrown.
    qdef = sparqlQmgr.newQueryDefinition(sparqlCreateGraphQuery.toString());

    String expectedException = "FailedRequestException";
    String exception = "";
    try {
      // Create same graph.
      sparqlQmgr.executeUpdate(qdef);
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println(exception);
    assertTrue( exception.contains(expectedException));

    qdef = null;

    // Create the same graph again silently. Should not expect exception.
    StringBuffer sparqlCreateGraphSilentQuery = new StringBuffer().append("CREATE SILENT GRAPH <DOMICLE>;");
    sparqlCreateGraphQuery.append(newline);

    qdef = sparqlQmgr.newQueryDefinition(sparqlCreateGraphSilentQuery.toString());
    String expectedSilentException = "";
    String exceptionSilent = "";
    try {
      // Create same graph.
      sparqlQmgr.executeUpdate(qdef);
    } catch (Exception e) {
      exceptionSilent = e.toString();
    }

    assertTrue( exceptionSilent.equals(expectedSilentException));
    qdef = null;

    // Verify the EXISTS again. Should have one solution returned.
    qdef = sparqlQmgr.newQueryDefinition(sparqlExists.toString());

    // Perform Exists query again
    JsonNode jsonResultsSil = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    JsonNode jsonBindingsNodesSilent = jsonResultsSil.path("results").path("bindings");

    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodesSilent.size());

    Iterator<JsonNode> nameNodesSilentItr = jsonBindingsNodesSilent.elements();
    JsonNode jsonPersonNodeSilent = null;
    if (nameNodesSilentItr.hasNext()) {
      jsonPersonNodeSilent = nameNodesSilentItr.next();
      assertEquals( "http://example.org/alice", jsonPersonNodeSilent.path("person").path("value").asText());
    }
  }

  /*
   * This test verifies sparql update DROP GRAPH. The test tries to DROP an
   * non-existent graph first, CREATES a NAMED GRAPH, INSERTS DATA, and then
   * tries drop it. DROP with SILENT option is tested.
   *
   * Expected Result : When SILENT disabled error reported, if non existent
   * graph is dropped. When SILENT enabled no error reported, if non existent
   * graph is dropped.
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testExecuteUpdateDropSilent()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteUpdateDropSilent method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer sparqldropQuery = new StringBuffer().append("DROP GRAPH <TOBEDROPPED>;");
    sparqldropQuery.append(newline);

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(sparqldropQuery.toString());

    String expectedException = "FailedRequestException";
    String exception = "";
    try {
      // Drop the non existent graph.
      sparqlQmgr.executeUpdate(qdef);
    } catch (Exception e) {
      exception = e.toString();
    }
    System.out.println(exception);
    assertTrue( exception.contains(expectedException));

    qdef = null;

    // Test to drop a named graph.
    StringBuffer sparqlCreateQuery = new StringBuffer().append("CREATE GRAPH <TOBEDROPPED>;");
    sparqlCreateQuery.append(newline);

    qdef = sparqlQmgr.newQueryDefinition(sparqlCreateQuery.toString());
    // Create an empty graph.
    sparqlQmgr.executeUpdate(qdef);

    // Drop the named graph. Should not throw an exception.
    qdef = null;
    qdef = sparqlQmgr.newQueryDefinition(sparqldropQuery.toString());
    String expectedNamedException = "";
    String exceptionNamed = "";
    try {
      // Drop the graph.
      sparqlQmgr.executeUpdate(qdef);
    } catch (Exception e) {
      exception = e.toString();
    }
    assertTrue( exceptionNamed.equals(expectedNamedException));
  }

  /*
   * This test verifies read and write of Triples within transactions using
   * executeUpdate method.
   *
   * Insert data ina transaction using write client. Read using read client
   * outside - No results should be returned. Read using write client within
   * transaction (transaction object passed in) - Should have results. Read
   * using write client without transaction object in executeUpdate - No results
   * returned. Rollback. Read using write client - No results returned. Open a
   * new transaction and Commit the data. Read using read client and write
   * client. - Both returns data. Uses JacksonHandle.
   */
  @Test
  public void testExecuteUpdateInTransactions()
  {
    System.out.println("In SPARQL Query Manager Test testExecuteUpdateInTransactions method");
    Transaction tWrite = null;

    SPARQLQueryManager sparqlReadQmgr = readclient.newSPARQLQueryManager();
    SPARQLQueryManager sparqlWriteQmgr = writeclient.newSPARQLQueryManager();

    // Insert data into Graph in a transaction.

    SPARQLQueryDefinition qdefWrite = sparqlWriteQmgr.newQueryDefinition();
    qdefWrite.setSparql("INSERT DATA { GRAPH <TransactionTest> { <Bob> <LivesIn> <London> . } }");
    try {
      tWrite = writeclient.openTransaction();
      sparqlWriteQmgr.executeUpdate(qdefWrite, tWrite);

      // Try to do a read outside and before committing on tWrite. Should not
      // see the results.
      SPARQLQueryDefinition qdefRead = sparqlReadQmgr.newQueryDefinition("select ?s ?p ?o from <TransactionTest> where { ?s ?p ?o }");

      JsonNode jsonResults = sparqlReadQmgr.executeSelect(qdefRead, new JacksonHandle()).get();
      JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 0 nodes returned.
      assertEquals( 0, jsonBindingsNodes.size());
      jsonResults = null;
      jsonBindingsNodes = null;

      // Try to do a read with transaction and before committing on tWrite.
      // Should see the results.
      SPARQLQueryDefinition qdefFromWriteMgr = sparqlWriteQmgr.newQueryDefinition("select ?s ?p ?o from <TransactionTest> where { ?s ?p ?o }");
      jsonResults = sparqlWriteQmgr.executeSelect(qdefFromWriteMgr, new JacksonHandle(), tWrite).get();
      jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 1 nodes returned.
      assertEquals( 1, jsonBindingsNodes.size());
      jsonResults = null;
      jsonBindingsNodes = null;

      // Try to do a read with no transaction using Write User. Should not see
      // the results.
      jsonResults = sparqlWriteQmgr.executeSelect(qdefFromWriteMgr, new JacksonHandle()).get();
      jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 0 nodes returned.
      assertEquals( 0, jsonBindingsNodes.size());
      jsonResults = null;
      jsonBindingsNodes = null;

      // Verify if Graph without triples does not Exists on rollback.
      tWrite.rollback();
      tWrite = null;
      // Try to do a read with no transaction using Write User. Should not see
      // the results.
      jsonResults = sparqlWriteQmgr.executeSelect(qdefFromWriteMgr, new JacksonHandle()).get();
      jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 0 nodes returned.
      assertEquals( 0, jsonBindingsNodes.size());
      jsonResults = null;
      jsonBindingsNodes = null;
      tWrite = writeclient.openTransaction();

      // Write the triple and commit transaction.
      sparqlWriteQmgr.executeUpdate(qdefWrite, tWrite);
      tWrite.commit();

      // Read using both clients. Both should return results.

      // Using read client.
      jsonResults = sparqlReadQmgr.executeSelect(qdefRead, new JacksonHandle()).get();
      jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 1 nodes returned.
      assertEquals( 1, jsonBindingsNodes.size());
      assertEquals( "Bob", jsonBindingsNodes.get(0).path("s").path("value").asText());
      assertEquals( "LivesIn", jsonBindingsNodes.get(0).path("p").path("value").asText());
      assertEquals( "London", jsonBindingsNodes.get(0).path("o").path("value").asText());
      jsonResults = null;
      jsonBindingsNodes = null;

      // Using write client.

      jsonResults = sparqlWriteQmgr.executeSelect(qdefFromWriteMgr, new JacksonHandle()).get();
      jsonBindingsNodes = jsonResults.path("results").path("bindings");

      // Should have 1 nodes returned.
      assertEquals( 1, jsonBindingsNodes.size());
      assertEquals( "Bob", jsonBindingsNodes.get(0).path("s").path("value").asText());
      assertEquals( "LivesIn", jsonBindingsNodes.get(0).path("p").path("value").asText());
      assertEquals( "London", jsonBindingsNodes.get(0).path("o").path("value").asText());
      tWrite = null;

      // Execute a write with Read Client to validate the error message.
      SPARQLQueryDefinition qdefWriteError = sparqlReadQmgr.newQueryDefinition();
      qdefWriteError.setSparql("INSERT DATA { GRAPH <TransactionTestErr> { <Bob> <LivesIn> <London> . } }");
      String expectedException = "ForbiddenUserException";
      String exception = "";
      String localMessage = "Local message: User is not allowed to apply resource at /graphs/sparql.";

      try {
        sparqlReadQmgr.executeUpdate(qdefWriteError);
      } catch (Exception e) {
        exception = e.toString();
      }
      System.out.println(exception);
      assertTrue( exception.contains(expectedException));
      assertTrue( exception.contains(localMessage));
    } catch (Exception ex) {
      tWrite = null;
      throw ex;
    } finally {
      if (tWrite != null)
        tWrite.rollback();
      tWrite = null;
    }
  }

  /*
   * This test verifies set and get methods on SPARQLQueryDefinition class. Do a
   * COPY GRAPH and verify if values set on the original and copied Graph
   *
   * Uses StringHandle (XMLReadHandle)
   */
  @Test
  public void testSetGetMethodsOnQdefWithCopy() throws Exception
  {
    System.out.println("In SPARQL Query Manager Test testSetGetMethodsOnQdefWithCopy method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition();

    // Create graph and COPY
    StringBuffer sparqlCreateQuery = new StringBuffer().append("CREATE GRAPH <OriginalGraph>;");
    sparqlCreateQuery.append(newline);

    // Test SPARQLQueryDefinition class' set methods.
    qdef.setSparql(sparqlCreateQuery.toString());
    qdef.setCollections("my-collections1");
    qdef.setCollections("my-collections2");
    qdef.setDirectory("my-Directory");

    createUserRolesWithPrevilages("sem-query-role");
    createRESTUser("sem-query-user", "x", "sem-query-role", "rest-writer");
    DatabaseClient semQueryclient = getDatabaseClient("sem-query-user", "x", getConnType());

    GraphManager graphManagerPerm = semQueryclient.newGraphManager();
    GraphPermissions graphPermissions = graphManagerPerm.permission("sem-query-role", Capability.UPDATE, Capability.EXECUTE);

    // Set the Permissions on SPARQLQueryDefinition
    qdef.setUpdatePermissions(graphPermissions);
    // Create original graph
    sparqlQmgr.executeUpdate(qdef);

    // Verify SPARQLQueryDefinition get methods.
    for (String collections : qdef.getCollections())
      assertTrue( collections.contains("my-collections1") || collections.contains("my-collections2"));
    assertEquals( "my-Directory", qdef.getDirectory());
    assertNull( qdef.getIncludeDefaultRulesets());
    assertTrue( qdef.getBindings().isEmpty());
    assertNull( qdef.getOptionsName());

    // Now read the graph back using GraphManager and Check Permissions.
    StringHandle graphStr = graphManagerPerm.read("OriginalGraph", new StringHandle());

    GraphPermissions readBackPermissions = graphManagerPerm.getPermissions("OriginalGraph");
    Set<Entry<String, Set<Capability>>> setPermissions = readBackPermissions.entrySet();
    Iterator<Entry<String, Set<Capability>>> itr = setPermissions.iterator();
    String stringPermissions = "size:" + setPermissions.size() + "|";
    ;
    while (itr.hasNext())
    {
      Map.Entry mePermissions = (Map.Entry) itr.next();
      stringPermissions = stringPermissions + mePermissions.getKey() + ":" + mePermissions.getValue() + "|";
    }
    System.out.println("Returned permissions from OriginalGraph : " + stringPermissions);

    assertTrue( stringPermissions.contains("size:5"));
    assertTrue( stringPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( stringPermissions.contains("harmonized-reader:[READ]"));
    assertTrue( stringPermissions.contains("rest-reader:[READ]"));
    assertTrue( stringPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue( stringPermissions.contains("sem-query-role:"));
    // sem-query-role:[UPDATE, EXECUTE] --> Order of UPDATE, EXECUTE not
    // certain. Split on role, then pipe char and replace trailing ]
    // Better way?
    String capab = stringPermissions.split("sem-query-role:\\[")[1].split("\\|")[0].replace("]", "");
    assertTrue( capab.contains("UPDATE, EXECUTE") || capab.contains("EXECUTE, UPDATE"));

    // Insert data into OriginalGraph.
    StringBuffer sparqlInsertData = new StringBuffer().append("PREFIX : <http://example.org/>");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
    sparqlInsertData.append("INSERT DATA { GRAPH <OriginalGraph> { ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":alice  rdf:type   foaf:Person .");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":alice  foaf:name  \"Alice\" . ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append(":bob    rdf:type   foaf:Person . ");
    sparqlInsertData.append(newline);
    sparqlInsertData.append("} } ");

    qdef = sparqlQmgr.newQueryDefinition(sparqlInsertData.toString());
    qdef.setDirectory("TestMarkLogic");
    // Insert Data into the empty graph.
    sparqlQmgr.executeUpdate(qdef);

    qdef = null;

    // Copy the graph and verify if set methods worked on Original and Copied
    // graphs.
    // Create graph and COPY
    StringHandle strWriteHandle = new StringHandle();
    qdef = sparqlQmgr.newQueryDefinition();
    strWriteHandle.with("COPY <OriginalGraph> TO <CopiedGraph>");
    qdef.setSparql(strWriteHandle);
    sparqlQmgr.executeUpdate(qdef);

    // Read the copied Graph
    GraphManager sparqlGmgr = readclient.newGraphManager();
    String strHandle = sparqlGmgr.read("CopiedGraph", new StringHandle()).get();
    System.out.println(strHandle);

    // Verify the permissions
    GraphPermissions graphPermission = sparqlGmgr.getPermissions("CopiedGraph");

    Set<Entry<String, Set<Capability>>> setPermissionsCopy = graphPermission.entrySet();
    Iterator<Entry<String, Set<Capability>>> iPermissionsCopy = setPermissionsCopy.iterator();
    String stringPermissionsCopy = "size:" + graphPermission.size() + "|";
    while (iPermissionsCopy.hasNext())
    {
      Map.Entry mePermissionsCopy = (Map.Entry) iPermissionsCopy.next();
      stringPermissionsCopy = stringPermissionsCopy + mePermissionsCopy.getKey() + ":" + mePermissionsCopy.getValue() + "|";
    }

    System.out.println("Returned permissions from Copy graph is : " + stringPermissionsCopy);

    assertTrue( stringPermissions.contains("size:5"));
    assertTrue( stringPermissionsCopy.contains("harmonized-updater:[UPDATE]"));
    assertTrue( stringPermissionsCopy.contains("harmonized-reader:[READ]"));
    assertTrue( stringPermissionsCopy.contains("rest-reader:[READ]"));
    assertTrue( stringPermissionsCopy.contains("rest-writer:[UPDATE]"));

    // Better way?
    String capabCpy = stringPermissionsCopy.split("sem-query-role:\\[")[1].split("\\|")[0].replace("]", "");
    assertTrue( capabCpy.contains("UPDATE, EXECUTE") || capabCpy.contains("EXECUTE, UPDATE"));

    // Validate the meta data through DocumentMetadataHandle also.
    TextDocumentManager docMgr = readclient.newTextDocumentManager();
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();
    DocumentPage page = docMgr.read("CopiedGraph");

    assertTrue(page.size() == 1);

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      System.out.println(rec.getUri());
      docMgr.readMetadata(rec.getUri(), mhRead);
      // get metadata values
      DocumentProperties properties = mhRead.getProperties();
      DocumentPermissions permissions = mhRead.getPermissions();
      DocumentCollections collections = mhRead.getCollections();

      // Properties - None.
      assertTrue( properties.size() == 0);

      // Permissions
      String actualPermissions = getDocumentPermissionsString(permissions);
      System.out.println("Returned permissions from DocumentMetadataHandle : " + actualPermissions);

      assertTrue( actualPermissions.contains("size:5"));
      assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
      assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
      assertTrue( actualPermissions.contains("rest-reader:[READ]"));
      assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
      // Better way?
      String capabAct = actualPermissions.split("sem-query-role:\\[")[1].split("\\|")[0].replace("]", "");
      assertTrue( capabAct.contains("UPDATE, EXECUTE") || capabAct.contains("EXECUTE, UPDATE"));

      // Collections
      String actualCollections = getDocumentCollectionsString(collections);
      System.out.println("Returned collections: " + actualCollections);

      assertTrue( actualCollections.contains("size:2"));
      assertTrue( actualCollections.contains("http://marklogic.com/semantics#graphs"));
      assertTrue( actualCollections.contains("CopiedGraph"));
    }
    // Release resources.
    deleteRESTUser("sem-query-user");
    deleteUserRole("sem-query-role");
    semQueryclient.release();
  }

  /*
   * This test verifies MarkLogic Java API support for Inference and Ruleset.
   *
   * No default ruleset, no inference triples Add one ruleset, verify inference
   * triples Add two rulesets, verify inference triples
   */
  @Test
  public void testInferenceAndRuleSet()
  {
    System.out.println("In SPARQL Query Manager Test testInferenceAndRuleSet method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition();

    // Create graph and COPY
    StringBuffer sparqlInferQuery = new StringBuffer();
    sparqlInferQuery.append("SELECT *");
    sparqlInferQuery.append(newline);
    sparqlInferQuery.append("FROM <");
    sparqlInferQuery.append(inferenceGraph);
    sparqlInferQuery.append(">");
    sparqlInferQuery.append(newline);
    sparqlInferQuery.append("WHERE ");
    sparqlInferQuery.append("{ ?s ?p ?o . }");
    sparqlInferQuery.append(newline);

    qdef.setSparql(sparqlInferQuery.toString());
    qdef.setIncludeDefaultRulesets(false);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    JsonNode jsonResults18 = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    JsonNode jsonBindingsNodes18 = jsonResults18.path("results").path("bindings");

    // Should have 18 nodes returned. No inference triples returned.
    assertEquals( 18, jsonBindingsNodes18.size());
    qdef = null;
    jacksonHandle = null;

    // Add subClassOf.rules as default ruleset
    qdef = sparqlQmgr.newQueryDefinition();
    qdef.setRulesets(SPARQLRuleset.SUBCLASS_OF);
    qdef.setIncludeDefaultRulesets(true);
    qdef.setSparql(sparqlInferQuery.toString());
    assertEquals( 1, qdef.getRulesets().length);
    SPARQLRuleset[] rulesets = qdef.getRulesets();
    assertEquals( "subClassOf.rules", rulesets[0].getName());

    // Execute with default Rulesets enabled. - We need to get Inference triples
    // now.
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    JsonNode jsonResultsSubClass = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    // Should have 31 nodes returned.
    assertEquals( 31, jsonResultsSubClass.path("results").path("bindings").size());

    // Enable two rulesets
    qdef = null;
    jacksonHandle = null;
    rulesets = null;

    // Add subClassOf.rules and subPropertyOf.rules as default ruleset
    qdef = sparqlQmgr.newQueryDefinition();
    qdef.setRulesets(SPARQLRuleset.SUBCLASS_OF, SPARQLRuleset.SUBPROPERTY_OF);
    qdef.setIncludeDefaultRulesets(true);
    qdef.setSparql(sparqlInferQuery.toString());
    assertEquals( 2, qdef.getRulesets().length);
    // Have an ordered collection.
    Collection<SPARQLRuleset> list = Arrays.asList(qdef.getRulesets());
    // Iterate over the list two times. Items more or less, would have asserted
    // by now.
    Iterator<SPARQLRuleset> itr = list.iterator();
    assertEquals( "subClassOf.rules", itr.next().getName());
    assertEquals( "subPropertyOf.rules", itr.next().getName());
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    JsonNode jsonResultsTwoRules = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    // Should have 44 nodes returned.
    assertEquals( 44, jsonResultsTwoRules.path("results").path("bindings").size());
  }

  /*
   * This test verifies setConstrainingQueryDefinition method on
   * SPARQLQueryDefinition class.
   *
   * Uses JacksonHandle
   */
  @Test
  public void testConstrainingQuery()
  {
    System.out.println("In SPARQL Query Manager Test testConstrainingQuery method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?fn ?lastname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("BIND (?firstname as ?fn)");
    queryStr.append("}");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());
    QueryManager queryMgr = writeclient.newQueryManager();

    // Set up the String variable binding.
    SPARQLBindings bindings = qdef.getBindings();

    bindings.bind("firstname", "Lei", RDFTypes.STRING);
    qdef.setBindings(bindings);

    // create query def.
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("Meng AND 8888");

    qdef.setConstrainingQueryDefinition(querydef);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    System.out.println(jsonStrResults);
    JsonNode jsonBindings = jsonStrResults.path("results").path("bindings").get(0);

    // Verify the results.
    assertEquals( "http://marklogicsparql.com/id#6666", jsonBindings.path("person").path("value").asText());
    assertEquals( "Lei", jsonBindings.path("fn").path("value").asText());
    assertEquals( "Pei", jsonBindings.path("lastname").path("value").asText());
  }

  /*
   * This negative test verifies setConstrainingQueryDefinition method on
   * SPARQLQueryDefinition class with null. Pass a null to
   * setConstrainingQueryDefinition Pass invalid data as criteria Uses
   * JacksonHandle
   */
  @Test
  public void testConstrainingQueryNull()
  {
    System.out.println("In SPARQL Query Manager Test testConstrainingQueryNull method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?fn ?lastname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append("BIND (?firstname as ?fn)");
    queryStr.append("}");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());
    QueryManager queryMgr = writeclient.newQueryManager();
    // create query def.
    StringQueryDefinition strquerydef = queryMgr.newStringDefinition();
    strquerydef.setCriteria("Foo AND bar");
    // Set up the String variable binding.
    SPARQLBindings bindings = qdef.getBindings();

    bindings.bind("firstname", "Lei", RDFTypes.STRING);
    qdef.setBindings(bindings);
    qdef.setConstrainingQueryDefinition(null);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    System.out.println("Null in ConstrainingQueryDefinition \n");
    System.out.println(jsonStrResults);
    JsonNode jsonBindings = jsonStrResults.path("results").path("bindings");

    // Verify the results. Returns results.
    assertEquals( 1, jsonBindings.size());
    assertEquals( "http://marklogicsparql.com/id#6666", jsonBindings.get(0).path("person").path("value").asText());
    assertEquals( "Lei", jsonBindings.get(0).path("fn").path("value").asText());
    assertEquals( "Pei", jsonBindings.get(0).path("lastname").path("value").asText());

    SPARQLQueryDefinition qdef1 = sparqlQmgr.newQueryDefinition(queryStr.toString()).withConstrainingQuery(strquerydef);
    // Parsing results using JsonNode.
    JsonNode jsonStrResults1 = sparqlQmgr.executeSelect(qdef1, new JacksonHandle()).get();
    System.out.println("Invalid Data \n");
    System.out.println(jsonStrResults1);
    JsonNode jsonBindings1 = jsonStrResults1.path("results").path("bindings");

    // Verify the results. Returns No results.
    assertEquals( 0, jsonBindings1.size());
  }

  /*
   * This test verifies SPARQL query with cts:contains
   *
   * Uses JacksonHandle
   */
  @Test
  public void testSparqlQueryCtsContains()
  {
    System.out.println("In SPARQL Query Manager Test testSparqlQueryCtsContains method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX ad: <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("PREFIX d:  <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("PREFIX cts: <http://marklogic.com/cts#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?person ?fn ?lastname");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?person ad:firstName ?firstname ;");
    queryStr.append(newline);
    queryStr.append("ad:lastName ?lastname.");
    queryStr.append(newline);
    queryStr.append("OPTIONAL {?person ad:homeTel ?phonenumber .}");
    queryStr.append(newline);
    queryStr.append(" FILTER cts:contains(?firstname, cts:or-query((\"Ling\", \"Lei\")))");
    queryStr.append("}");
    queryStr.append("ORDER BY ?firstname");

    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());
    JsonNode jsonStrResults = sparqlQmgr.executeSelect(qdef, new JacksonHandle()).get();
    System.out.println(jsonStrResults);
    JsonNode jsonBindings1 = jsonStrResults.path("results").path("bindings").get(0);
    JsonNode jsonBindings2 = jsonStrResults.path("results").path("bindings").get(1);

    // Verify the results.
    assertEquals( "http://marklogicsparql.com/id#6666", jsonBindings1.path("person").path("value").asText());
    assertEquals( "Pei", jsonBindings1.path("lastname").path("value").asText());

    assertEquals( "http://marklogicsparql.com/id#4444", jsonBindings2.path("person").path("value").asText());
    assertEquals( "Ling", jsonBindings2.path("lastname").path("value").asText());
  }

  /*
   * This test verifies MarkLogic Sparql Negation.
   */
  @Test
  public void testSparqlNegation()  {
    System.out.println("In SPARQL Query Manager Test testSparqlNegation method");
    SPARQLQueryManager sparqlQmgr = writeclient.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlQmgr.newQueryDefinition();

    StringBuffer queryStr = new StringBuffer();
    queryStr.append("PREFIX  id:    <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("PREFIX  add:   <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?name");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?id add:lastName  ?name .");
    queryStr.append(newline);
    queryStr.append("FILTER NOT EXISTS { ?id add:homeTel ?num }");
    queryStr.append(newline);
    queryStr.append("}");
    queryStr.append(newline);
    queryStr.append("ORDER BY ?name");
    qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    JsonNode jsonResults = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();
    JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "Ling", jsonBindingsNodes.get(0).path("name").path("value").asText());
    assertEquals( "Ling", jsonBindingsNodes.get(1).path("name").path("value").asText());
    assertEquals( "Wang", jsonBindingsNodes.get(2).path("name").path("value").asText());
    assertEquals( "Xiang", jsonBindingsNodes.get(3).path("name").path("value").asText());

    qdef = null;
    jacksonHandle = null;
    jsonResults = null;
    jsonBindingsNodes = null;
    queryStr = null;

    queryStr = new StringBuffer();
    queryStr.append("PREFIX  id:    <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("PREFIX  add:   <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?name");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?id add:lastName  ?name .");
    queryStr.append(newline);
    queryStr.append("?id add:firstName  ?name .");
    queryStr.append(newline);
    queryStr.append("MINUS { ?id add:homeTel ?num }");
    queryStr.append(newline);
    queryStr.append("}");
    queryStr.append(newline);
    queryStr.append("ORDER BY ?name");
    qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    jsonResults = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    jsonBindingsNodes = jsonResults.path("results").path("bindings");
    // Should have 1 nodes returned.
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "Ling", jsonBindingsNodes.get(0).path("name").path("value").asText());

    qdef = null;
    jacksonHandle = null;
    jsonResults = null;
    jsonBindingsNodes = null;
    queryStr = null;

    queryStr = new StringBuffer();
    queryStr.append("PREFIX  id:    <http://marklogicsparql.com/id#>");
    queryStr.append(newline);
    queryStr.append("PREFIX  add:   <http://marklogicsparql.com/addressbook#>");
    queryStr.append(newline);
    queryStr.append("SELECT ?name");
    queryStr.append(newline);
    queryStr.append("FROM <");
    queryStr.append(customGraph);
    queryStr.append(">");
    queryStr.append(newline);
    queryStr.append("WHERE");
    queryStr.append(newline);
    queryStr.append("{");
    queryStr.append(newline);
    queryStr.append("?id add:firstName  ?name .");
    queryStr.append(newline);
    queryStr.append("FILTER EXISTS { ?id add:homeTel ?num }");
    queryStr.append(newline);
    queryStr.append("}");
    queryStr.append(newline);
    queryStr.append("ORDER BY ?name");
    qdef = sparqlQmgr.newQueryDefinition(queryStr.toString());

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    jsonResults = sparqlQmgr.executeSelect(qdef, jacksonHandle).get();

    jsonBindingsNodes = jsonResults.path("results").path("bindings");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "John", jsonBindingsNodes.get(0).path("name").path("value").asText());
    assertEquals( "Lei", jsonBindingsNodes.get(1).path("name").path("value").asText());
    assertEquals( "Meng", jsonBindingsNodes.get(2).path("name").path("value").asText());
    assertEquals( "Micah", jsonBindingsNodes.get(3).path("name").path("value").asText());
  }

  /*
   * Write a N-TRIPLES format custom data contained in a file to the database.
   * Graph Name is : testlocaleGraph
   */
  public static void writeNTriplesFromFile(String filename, String graphName) throws KeyManagementException, NoSuchAlgorithmException, Exception {
    File file = new File(datasource + filename);
    FileHandle filehandle = new FileHandle();
    GraphManager sparqlGmgr = writeclient.newGraphManager();
    filehandle.set(file);
    sparqlGmgr.write(graphName, filehandle.withMimetype(RDFMimeTypes.TURTLE));
  }

  /*
   * Write a TURTLE format custom data contained in a string to the database.
   * Graph Name is : graphName
   */

  public static void writeSPARQLDataFromString(String content, String graphName) throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    // Create Grapgh manager
    GraphManager sparqlGmgr = writeclient.newGraphManager();
    StringHandle handle = new StringHandle();
    handle.set(content.toString());
    // write the triples in the doc into named graph
    sparqlGmgr.write(graphName, handle.withMimetype(RDFMimeTypes.TURTLE));
    // Wait for index. Need a better way to handle this.
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      System.out.print("Exception from writeSPARQLDataFromString " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Write document using FileHandle
   *
   * @param client
   * @param directoryPath
   * @param filename
   * @param uri
   * @param sparqlMIMEType
   * @throws IOException
   */
  public static void writeSPARQLDocumentUsingFileHandle(DatabaseClient client, String directoryPath, String filename, String uri, String sparqlMIMEType)
      throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    // Get the content to file
    File file = new File(directoryPath + filename);
    String docId = null;

    FileHandle filehandle = new FileHandle();
    filehandle.set(file);

    // Create Graph manager
    GraphManager sparqlGmgr = client.newGraphManager();
    if (uri != null)
      docId = uri + filename;

    // Write the triples in the doc into either named graph or into the default
    // graph (when uri is null)
    sparqlGmgr.write(docId, filehandle.withMimetype(sparqlMIMEType));
    if (uri != null)
      System.out.println("Loaded triples in " + filename
          + " into the database with named graph " + docId);
    else
      System.out.println("Loaded triples in " + filename
          + " into the database with default graph");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception
  {
    writeclient.release();
    readclient.release();
    client.release();
  }
}
