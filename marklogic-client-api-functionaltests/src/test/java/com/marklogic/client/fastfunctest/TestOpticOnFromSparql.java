/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class TestOpticOnFromSparql extends AbstractFunctionalTest {

  private String selectStmt;
  private RowManager rowMgr;
  private PlanBuilder p;
  private ModifyPlan plan1;
  private JacksonHandle jacksonHandle = new JacksonHandle();
  private JsonNode jsonBindingsNodes;
  private JsonNode node;

    @BeforeAll
    public static void setUp() throws Exception {
        removeFieldIndices();
        loadGraphToDB(client, "people.ttl", "/optic/sparql/test/people.ttl");
        loadGraphToDB(client, "companies_100.ttl", "/optic/sparql/test/companies.ttl");
    }

    @AfterAll
    public static void tearDown() {
        restoreFieldIndices();
    }

  @BeforeEach
  public void setUpBeforTest() {

	  rowMgr = client.newRowManager();
	  p = rowMgr.newPlanBuilder();
	  jacksonHandle.setMimetype("application/json");

  }


  /**
   * Write graph
   *
   * @param client
   * @param filename
   * @param uri
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */

  public static void loadGraphToDB(DatabaseClient client, String filename, String uri) throws IOException, ParserConfigurationException,
      SAXException
  {
    // create graph manager
	  GraphManager gmgr = client.newGraphManager();
	  gmgr.setDefaultMimetype(RDFMimeTypes.TURTLE);

      final String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
    File file = new File(datasource + filename);
    // create a handle on the content
    FileHandle handle = new FileHandle(file).withMimetype(RDFMimeTypes.TURTLE);
    handle.set(file);

    gmgr.setDefaultMimetype(RDFMimeTypes.TURTLE);
    gmgr.writeAs(
    		uri, handle);

    System.out.println("Write " + uri + " to database");
  }


  /*
   * 1) Test simple sparql select 2) Test simple sparql select with limit 3) Test simple sparql select with offsetLimit
   * 4) Test simple sparql select with offsetLimit 5) Test simple sparql select with offset 6) Test sparql select with multiple triple patterns
   * 7) Test sparql select with multiple triple patterns with condition 8) Test sparql select with order by
   * 9) Test sparql aggregate on count 10)
   */

  // Test 1
  @Test
  public void testSimpleSparqlSelect() throws Exception
  {
	  selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
	          "PREFIX ppl:  <http://people.org/> " +
	          "SELECT ?s ?o " +
	          "  WHERE { ?s foaf:knows ?o " +
	          "  }";
	  plan1 = p.fromSparql(selectStmt);
	    rowMgr.resultDoc(plan1, jacksonHandle);
	    jsonBindingsNodes = jacksonHandle.get().path("rows");
	    node = jsonBindingsNodes.path(0);
	    assertEquals( 22, jsonBindingsNodes.size());
	    assertEquals( "http://people.org/person1", node.path("s").path("value").asText());
  }

  //Test 2
  @Test
  public void testSimpleSparqlSelectWithLimit() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
	          "PREFIX ppl:  <http://people.org/> " +
	          "SELECT ?s ?o " +
	          "  WHERE { ?s foaf:knows ?o " +
	          "  }";
    plan1 = p.fromSparql(selectStmt).limit(10);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 10, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person1", node.path("s").path("value").asText());
  }

//Test 3
  @Test
  public void testSimpleSparqlSelectWithOffsetLimit_1() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
	          "PREFIX ppl:  <http://people.org/> " +
	          "SELECT ?s ?o " +
	          "  WHERE { ?s foaf:knows ?o " +
	          "  }";
    plan1 = p.fromSparql(selectStmt).offsetLimit(5, 15);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals(15, jsonBindingsNodes.size());
  }

//Test 4
  @Test
  public void testSimpleSparqlSelectWithOffsetLimit_2() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
	          "PREFIX ppl:  <http://people.org/> " +
	          "SELECT ?s ?o " +
	          "  WHERE { ?s foaf:knows ?o " +
	          "  }";
    plan1 = p.fromSparql(selectStmt).offsetLimit(11, 15);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals(11, jsonBindingsNodes.size());
  }

//Test 5
  @Test
  public void testSimpleSparqlSelectWithOffset() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
	          "PREFIX ppl:  <http://people.org/> " +
	          "SELECT ?s ?o " +
	          "  WHERE { ?s foaf:knows ?o " +
	          "  }";
    plan1 = p.fromSparql(selectStmt).offset(15);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 7, jsonBindingsNodes.size());
  }

//Test 6
  @Test
  public void testSimpleSparqlSelectWithMultipleTriplePattern() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n" +
			"PREFIX ppl:  <http://people.org/>\r\n" +
			"          SELECT *\r\n" +
			"          WHERE {\r\n" +
			"            ?person foaf:name ?name .\r\n" +
			"            ?person <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type .\r\n" +
			"          }";
    plan1 = p.fromSparql(selectStmt).limit(5);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 5, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person1", node.path("person").path("value").asText());
    assertEquals( "Person 1", node.path("name").path("value").asText());
    assertEquals( "http://people.org/Person", node.path("type").path("value").asText());
  }

//Test 7
  @Test
  public void testSimpleSparqlSelectWithMultipleTriplePatternWithCondition() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n" +
			"PREFIX ppl:  <http://people.org/>\r\n" +
			"SELECT ?personA ?personB\r\n" +
			"WHERE {\r\n" +
			"?personB foaf:name 'Person 7' .\r\n" +
			"?personA foaf:knows ?personB\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person1", node.path("personA").path("value").asText());
    assertEquals( "http://people.org/person7", node.path("personB").path("value").asText());
  }

//Test 8
  @Test
  public void testSimpleSparqlSelectWithOrderBy() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n" +
			"PREFIX ppl:  <http://people.org/>\r\n" +
			"SELECT *\r\n" +
			"WHERE {\r\n" +
			"?person foaf:name ?name .\r\n" +
			"?person <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type .\r\n" +
			"}\r\n" +
			"ORDER BY ?name";
    plan1 = p.fromSparql(selectStmt).limit(15).offset(11);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person2", node.path("person").path("value").asText());
    assertEquals( "Person 2", node.path("name").path("value").asText());
    assertEquals( "http://people.org/Person", node.path("type").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "http://people.org/person4", node.path("person").path("value").asText());
    assertEquals( "Person 4", node.path("name").path("value").asText());
    assertEquals( "http://people.org/Person", node.path("type").path("value").asText());
  }

//Test 9
  @Test
  public void testSimpleSparqlSelectWithAggregateCount() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT (COUNT(?industry) AS ?total)\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:industry ?industry .\r\n" +
			"?company demov:industry 'Industrial Goods'\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "15", node.path("total").path("value").asText());
  }

//Test 9
  @Test
  public void testSimpleSparqlSelectWithAggregateDistinctCount() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT (COUNT( DISTINCT ?industry) AS ?total_industries)\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company demov:industry ?industry .\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "6", node.path("total_industries").path("value").asText());
  }

//Test 10
  @Test
  public void testSimpleSparqlSelectWithAggregateSum() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ( SUM (?sales) AS ?sum_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "19318588272", node.path("sum_sales").path("value").asText());
  }

//Test 11
  @Test
  public void testSimpleSparqlSelectWithAggregateSumWithBind() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT DISTINCT ?industry (SUM(?sales) as ?total_sales) ?country\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company demov:industry 'Other' .\r\n" +
			"?company vcard:hasAddress/vcard:country-name 'USA' .\r\n" +
			"BIND (vcard:hasAddress/vcard:country-name as ?country)\r\n" +
			"BIND (demov:industry as ?industry)\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "1647852766", node.path("total_sales").path("value").asText());
  }

//Test 12
  @Test
  public void testSimpleSparqlSelectWithAggregateGroupBy() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?industry (SUM (?sales) AS ?sum_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company demov:industry ?industry\r\n" +
			"}\r\n" +
			"GROUP BY ?industry\r\n" +
			"ORDER BY ?sum_sales";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 6, jsonBindingsNodes.size());
    assertEquals( "Retail/Wholesale", node.path("industry").path("value").asText());
    assertEquals( "1255159206", node.path("sum_sales").path("value").asText());
    node = jsonBindingsNodes.path(5);
    assertEquals( "Healthcare/Life Sciences", node.path("industry").path("value").asText());
    assertEquals( "6141852782", node.path("sum_sales").path("value").asText());
  }

//Test 13
  @Test
  public void testSimpleSparqlSelectWithAggregateGroupByWithMin() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?country (MIN (?sales) AS ?min_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company vcard:hasAddress [ vcard:country-name ?country ]\r\n" +
			"}\r\n" +
			"GROUP BY ?country\r\n" +
			"ORDER BY ASC( ?min_sales ) ?country";
    plan1 = p.fromSparql(selectStmt);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "China", node.path("country").path("value").asText());
    assertEquals( "8", node.path("min_sales").path("value").asText());
    node = jsonBindingsNodes.path(7);
    assertEquals( "USA", node.path("country").path("value").asText());
    assertEquals( "10000000", node.path("min_sales").path("value").asText());
  }

//Test 14
  @Test
  public void testSimpleSparqlSelectWithAggregateGroupByWithQualifier() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?industry (SUM (?sales) AS ?sum_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company demov:industry ?industry\r\n" +
			"}\r\n" +
			"GROUP BY ?industry\r\n" +
			"ORDER BY ?sum_sales";
    plan1 = p.fromSparql(selectStmt, "MySPARQL");
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 6, jsonBindingsNodes.size());
    assertEquals( "Retail/Wholesale", node.path("MySPARQL.industry").path("value").asText());
    assertEquals( "1255159206", node.path("MySPARQL.sum_sales").path("value").asText());
    node = jsonBindingsNodes.path(5);
    assertEquals( "Healthcare/Life Sciences", node.path("MySPARQL.industry").path("value").asText());
    assertEquals( "6141852782", node.path("MySPARQL.sum_sales").path("value").asText());
  }

/*
//Test 15
  @Test
  public void testSimpleSparqlSelectWithAggregateGroupByWithKeyVaue() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?country (MIN (?sales) AS ?min_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company vcard:hasAddress [ vcard:country-name ?country ]\r\n" +
			"}\r\n" +
			"GROUP BY ?country\r\n" +
			"ORDER BY ASC( ?min_sales ) ?country";
    plan1 = p.fromSparql(selectStmt, "qualifier");
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "China", node.path("country").path("value").asText());
    assertEquals( "8", node.path("min_sales").path("value").asText());
    node = jsonBindingsNodes.path(7);
    assertEquals( "USA", node.path("country").path("value").asText());
    assertEquals( "10000000", node.path("min_sales").path("value").asText());
  }
 */

//Test 16
  @Test
  public void testSimpleSparqlSelectWithAggregateGroupByWithNullQualifier() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?country (MIN (?sales) AS ?min_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company vcard:hasAddress [ vcard:country-name ?country ]\r\n" +
			"}\r\n" +
			"GROUP BY ?country\r\n" +
			"ORDER BY ASC( ?min_sales ) ?country";
    plan1 = p.fromSparql(selectStmt, null);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "China", node.path("country").path("value").asText());
    assertEquals( "8", node.path("min_sales").path("value").asText());
    node = jsonBindingsNodes.path(7);
    assertEquals( "USA", node.path("country").path("value").asText());
    assertEquals( "10000000", node.path("min_sales").path("value").asText());
  }

//Test 17
  @Test
  public void testSimpleSparqlSelectWithOpticOperations() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n" +
			"PREFIX ppl:  <http://people.org/>\r\n" +
			"SELECT ?s ?o\r\n" +
			"WHERE { ?s foaf:knows ?o }";
    plan1 = p.fromSparql(selectStmt).orderBy(p.desc("o"));
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 22, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person9", node.path("o").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "http://people.org/person8", node.path("o").path("value").asText());
    node = jsonBindingsNodes.path(21);
    assertEquals( "http://people.org/person10", node.path("o").path("value").asText());
  }

/*
//Test 18
  @Test
  public void testSimpleSparqlSelectWithOpticWhere() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?country (MIN (?sales) AS ?min_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company vcard:hasAddress [ vcard:country-name ?country ]\r\n" +
			"}\r\n" +
			"GROUP BY ?country\r\n" +
			"ORDER BY ASC( ?min_sales ) ?country";
    plan1 = p.fromSparql(selectStmt).where(p.eq(p.col("min_sales"), p.)));
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 22, jsonBindingsNodes.size());
    assertEquals( "http://people.org/person9", node.path("o").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "http://people.org/person8", node.path("o").path("value").asText());
    node = jsonBindingsNodes.path(21);
    assertEquals( "http://people.org/person10", node.path("o").path("value").asText());
  }
*/

//Test 19
  @Test
  public void testSimpleSparqlSelectWithOpticSQLAndQualifier() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?industry (SUM (?sales) AS ?sum_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company demov:industry ?industry\r\n" +
			"}\r\n" +
			"GROUP BY ?industry\r\n" +
			"ORDER BY ?sum_sales";
    plan1 = p.fromSparql(selectStmt, "MySPARQL").where(p.sqlCondition("MySPARQL.industry LIKE" + "\'Re%\'")).select(p.viewCol("MySPARQL", "industry"), p.as("mySales", p.viewCol("MySPARQL", "sum_sales")));
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "Retail/Wholesale", node.path("MySPARQL.industry").path("value").asText());
    assertEquals( "1255159206", node.path("mySales").path("value").asText());
  }

/*
//Test 20
  @Test
  public void testSimpleSparqlSelectWithOpticAndQualifier() throws Exception
  {
	selectStmt = "PREFIX demov: <http://demo/verb#>\r\n" +
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\r\n" +
			"SELECT ?industry (SUM (?sales) AS ?sum_sales )\r\n" +
			"FROM </optic/sparql/test/companies.ttl>\r\n" +
			"WHERE {\r\n" +
			"?company a vcard:Organization .\r\n" +
			"?company demov:sales ?sales .\r\n" +
			"?company demov:industry ?industry\r\n" +
			"}\r\n" +
			"GROUP BY ?industry\r\n" +
			"ORDER BY ?sum_sales";
    plan1 = p.fromSparql(selectStmt, "MySPARQL").select(p.viewCol("MySPARQL", "industry"), p.as("doubleSales", p.viewCol("MySPARQL", "sum_sales")),
    		p.as("discount", p.viewCol("MySPARQL", "sum_sales")));
//    p.divide(p.viewCol("MySPARQL", "sum_sales"), 10);
//    p.multiply(p.viewCol("MySPARQL", "sum_sales"), 2);
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 6, jsonBindingsNodes.size());
    assertEquals( "Retail/Wholesale", node.path("MySPARQL.industry").path("value").asText());
    assertEquals( "2510318412", node.path("doubleSales").path("value").asText());
    assertEquals( "125515920.6", node.path("discount").path("value").asText());
  }
*/

 /*
//Test 21
  @Test
  public void testSimpleSparqlSelectWithLiteralPlaceholder() throws Exception
  {
	selectStmt = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n" +
			"PREFIX ppl:  <http://people.org/>\r\n" +
			"SELECT ?personA ?personB\r\n" +
			"WHERE {\r\n" +
			"?personB foaf:name @placeholderName .\r\n" +
			"?personA foaf:knows ?personB\r\n" +
			"}";
    plan1 = p.fromSparql(selectStmt, null, "");
    rowMgr.resultDoc(plan1, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(0);
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "Retail/Wholesale", node.path("MySPARQL.industry").path("value").asText());
    assertEquals( "1255159206", node.path("mySales").path("value").asText());
  }
*/
}
