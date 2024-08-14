/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.MarkLogicVersion;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestOpticOnCtsQuery extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception {
      // Install the TDE templates
      // loadFileToDB(client, filename, docURI, collection, document format)
      loadFileToDB(schemasClient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[]{"http://marklogic.com/xdmp/tde"});
      loadFileToDB(schemasClient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});
      loadFileToDB(schemasClient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});
      loadFileToDB(schemasClient, "masterDetail4.tdej", "/optic/view/test/masterDetail4.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});

      // Load XML data files.
      loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML", new String[]{"/optic/view/test"});
      loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML", new String[]{"/optic/player/triple/test"});
      loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML", new String[]{"/optic/team/triple/test"});
      loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML", new String[]{"/optic/other/player/triple/test"});
      loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML", new String[]{"/optic/lexicon/test"});

      // Load JSON data files.
      loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON", new String[]{"/optic/view/test"});
      loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON", new String[]{"/optic/view/test"});
      loadFileToDB(client, "masterDetail4.json", "/optic/view/test/masterDetail4.json", "JSON", new String[]{"/optic/view/test"});
      loadFileToDB(client, "masterDetail5.json", "/optic/view/test/masterDetail5.json", "JSON", new String[]{"/optic/view/test"});

      loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[]{"/other/coll1", "/other/coll2"});
      loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[]{"/optic/lexicon/test"});

      loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[]{"/optic/lexicon/test"});
      loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[]{"/optic/lexicon/test"});
  }

  /*
   * Checks for Plan Builder's jsonPropertyWordQuery TEST 9 -
   * jsonPropertyWordQuery on fromViews. plan1 uses fromView plan2 use fromView
   */
  @Test
  public void testJsonPropertyWordQuery()
  {
    System.out.println("In testJsonPropertyWordQuery method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    // plan1 - fromView
    ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4", null, null)
        .where(p.cts.jsonPropertyWordQuery("name", "Detail 100"));
    // plan2 - fromView
    ModifyPlan plan2 = p.fromView("opticFunctionalTest4", "master4");

    ModifyPlan output = plan1.joinInner(plan2, p.on(p.schemaCol("opticFunctionalTest4", "detail4", "masterId"), p.schemaCol("opticFunctionalTest4", "master4", "id")))
        .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 3 == jsonBindingsNodes.size());
    assertEquals( "100", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
    assertEquals( "Master 100", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value")
        .asText());
    assertEquals( "Detail 200", jsonBindingsNodes.path(1).path("opticFunctionalTest4.detail4.name").path("value")
        .asText());
    assertEquals( "2016-04-02", jsonBindingsNodes.path(1).path("opticFunctionalTest4.master4.date").path("value")
        .asText());
    assertEquals( "72.9", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.amount").path("value").asText());
    assertEquals( "yellow", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.color").path("value").asText());
  }

  /*
   * Test join inner with joinInnerDoc - TEST 2 plan1 uses fromLexicon plan2 use
   * fromLexicons
   */
  @Test
  public void testjsonPropertyWordAndValueQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjsonPropertyWordAndValueQuery method");

    /*
     * // Create a new Plan. RowManager rowMgr = client.newRowManager();
     * PlanBuilder p = rowMgr.newPlanBuilder(); Map<String,
     * CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
     * index1.put("uri1", p.cts.uriReference()); index1.put("city",
     * p.cts.jsonPropertyReference("city")); index1.put("popularity",
     * p.cts.jsonPropertyReference("popularity")); index1.put("date",
     * p.cts.jsonPropertyReference("date")); index1.put("distance",
     * p.cts.jsonPropertyReference("distance")); index1.put("point",
     * p.cts.jsonPropertyReference("latLonPoint"));
     *
     * Map<String, CtsReferenceExpr>index2 = new HashMap<String,
     * CtsReferenceExpr>(); index2.put("uri2", p.cts.uriReference());
     * index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
     * index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam")); // TEST
     * 2 - jsonPropertyValueQuery on fromLexicons // plan1 - fromLexicons
     * ModifyPlan plan1 = p.fromLexicons(index1, "myCity",
     * p.fragmentIdCol("fragId1"), p.cts.jsonPropertyWordQuery("city", "new"));
     * // plan2 - fromLexicons ModifyPlan plan2 = p.fromLexicons(index2,
     * "myTeam", p.fragmentIdCol("fragId2"),
     * p.cts.jsonPropertyValueQuery("cityTeam", p.xs.string("yankee")));
     *
     * ModifyPlan output2 = plan1.joinInner(plan2)
     * .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
     * .orderBy(p.asc(p.col("date")));
     *
     * JacksonHandle jacksonHandle = new JacksonHandle();
     * jacksonHandle.setMimetype("application/json"); rowMgr.resultDoc(output2,
     * jacksonHandle); JsonNode jsonResults = jacksonHandle.get();
     *
     * JsonNode jsonBindingsNodes = jsonResults.path("rows");
     *
     * assertTrue(
     * "Number of Elements after plan execution is incorrect. Should be 1", 1 ==
     * jsonBindingsNodes.size());
     * assertEquals( "new york",
     * jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
     * assertEquals(
     * "40.709999,-74.009995",
     * jsonBindingsNodes.path(0).path("myCity.point").path("value").asText());
     * assertEquals(
     * "/optic/lexicon/test/city2.json",
     * jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());
     */
  }

  /*
   * Checks for jsonPropertyGeospatialQuery with circle on fromLexicons TEST 4 -
   * jsonPropertyWordQuery on fromLexicons with circle
   */
  @Test
  public void testJsonPropertyGeospatialQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {/*
    * System.out.println("In testJsonPropertyGeospatialQuery method");
    *
    * // Create a new Plan. RowManager rowMgr = client.newRowManager();
    * PlanBuilder p = rowMgr.newPlanBuilder(); Map<String,
    * CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
    * index1.put("uri1", p.cts.uriReference()); index1.put("city",
    * p.cts.jsonPropertyReference("city")); index1.put("popularity",
    * p.cts.jsonPropertyReference("popularity")); index1.put("date",
    * p.cts.jsonPropertyReference("date")); index1.put("distance",
    * p.cts.jsonPropertyReference("distance")); index1.put("point",
    * p.cts.jsonPropertyReference("latLonPoint"));
    *
    * Map<String, CtsReferenceExpr>index2 = new HashMap<String,
    * CtsReferenceExpr>(); index2.put("uri2", p.cts.uriReference());
    * index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
    * index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
    *
    * // plan1 - fromLexicons PlanSystemColumn fIdCol1 =
    * p.fragmentIdCol("fragId1"); PlanSystemColumn fIdCol2 =
    * p.fragmentIdCol("fragId2"); QualifiedPlan plan1 = p.fromLexicons(index1,
    * "myCity", fIdCol1, p.cts.jsonPropertyGeospatialQuery("latLonPoint",
    * p.cts.box(49.16, -13.41, 60.85, 1.76))); // plan2 - fromLexicons
    * QualifiedPlan plan2 = p.fromLexicons(index2, "myTeam", fIdCol2, null);
    *
    * ModifyPlan output = plan1.joinInner(plan2) .where(p.eq(p.viewCol("myCity",
    * "city"), p.col("cityName"))) .orderBy(p.asc(p.col("date")));
    *
    * JacksonHandle jacksonHandle = new JacksonHandle();
    * jacksonHandle.setMimetype("application/json");
    *
    * rowMgr.resultDoc(output, jacksonHandle); JsonNode jsonResults =
    * jacksonHandle.get();
    *
    * JsonNode jsonBindingsNodes = jsonResults.path("rows");
    * assertTrue("Number of Elements after plan execution is incorrect. Should be 1"
    * , 1 == jsonBindingsNodes.size());
    * assertEquals( "london",
    * jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
    * assertEquals(
    * "/optic/lexicon/test/doc1.json",
    * jsonBindingsNodes.path(0).path("myCity.uri1").path("value").asText());
    * assertEquals(
    * "/optic/lexicon/test/city1.json",
    * jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());
    */
  }

  /*
   * Test testWordQueryPropertyValueQueryFromViews plan1 uses fromView plan2 use
   * fromView
   */
  @Test
  public void testWordQueryPropertyValueQueryFromViews() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {/*
    * System.out.println("In testWordQueryPropertyValueQueryFromViews method");
    *
    * // Create a new Plan. RowManager rowMgr = client.newRowManager();
    * PlanBuilder p = rowMgr.newPlanBuilder(); PlanPrefixer bb =
    * p.prefixer("http://marklogic.com/baseball/players");
    *
    * // plan1 - fromView ModifyPlan plan1 = p.fromView("opticFunctionalTest4",
    * "detail4", null, null, p.cts.jsonPropertyValueQuery("id",
    * p.xs.string("600")) ); // plan2 - fromView ModifyPlan plan2 =
    * p.fromView("opticFunctionalTest4", "master4", null, null,
    * p.cts.wordQuery("Master 100") );
    *
    * ModifyPlan output = plan1.joinInner(plan2,
    * p.on(p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
    * p.schemaCol("opticFunctionalTest4", "master4", "id")))
    * .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));
    *
    * JacksonHandle jacksonHandle = new JacksonHandle();
    * jacksonHandle.setMimetype("application/json");
    *
    * rowMgr.resultDoc(output, jacksonHandle); JsonNode jsonResults =
    * jacksonHandle.get();
    *
    * JsonNode jsonBindingsNodes = jsonResults.path("rows");
    * assertTrue("Number of Elements after plan execution is incorrect. Should be 3"
    * , 3 == jsonBindingsNodes.size());
    * assertEquals(
    * "400",
    * jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path
    * ("value").asText());
    * assertEquals(
    * "Master 200",
    * jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name"
    * ).path("value").asText());
    * assertEquals(
    * "600",
    * jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path
    * ("value").asText());
    * assertEquals(
    * "Master 100",
    * jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name"
    * ).path("value").asText());
    */
  }

  /*
   * Checks for nearQuery on fromLexicons TEST 13
   */
  @Test
  public void testNearQueryFromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testNearQueryFromLexicons method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri1", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    Map<String, CtsReferenceExpr> index2 = new HashMap<String, CtsReferenceExpr>();
    index2.put("uri2", p.cts.uriReference());
    index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
    index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

    // plan1 - fromLexicons
    // Create an and query that is equivalent to array of queries

    CtsQuerySeqExpr andQuery = p.cts.andQuery(p.cts.wordQuery("near"), p.cts.wordQuery("Thames"));
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"))
        .where(p.cts.nearQuery(andQuery, p.xs.doubleVal(3)));
    // plan2 - fromLexicons
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));

    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		.orderBy(p.asc(p.col("date")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertEquals(1, jsonBindingsNodes.size(), "Unexpected result: " + jsonBindingsNodes.toPrettyString());
    assertEquals("london", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
    assertEquals("/optic/lexicon/test/doc1.json", jsonBindingsNodes.path(0).path("myCity.uri1").path("value").asText());
    assertEquals("/optic/lexicon/test/city1.json", jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());
  }

  /*
   * Checks for cts queries with options on fromLexicons TEST 14
   */
  @Test
  public void testCtsQueriesWithOptions()
  {
    System.out.println("In testCtsQueriesWithOptions method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri1", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    Map<String, CtsReferenceExpr> index2 = new HashMap<String, CtsReferenceExpr>();
    index2.put("uri2", p.cts.uriReference());
    index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
    index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

    // plan1 - fromLexicons
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"));
    // plan2 - fromLexicons
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));

    ModifyPlan output = plan1.where(p.cts.jsonPropertyWordQuery("city", "*k", "wildcarded", "case-sensitive"))
        .joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		.orderBy(p.asc(p.col("date")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    System.out.println("Results are " + jsonBindingsNodes.toString());

    assertEquals(1, jsonBindingsNodes.size(), "Number of Elements after plan execution is incorrect: " + jsonBindingsNodes.toPrettyString());
    assertEquals("new york", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
  }

  /*
   * Test jsonPropertyRangeQuery plan1 uses fromView plan2 use fromView
   */
  @Test
  public void testJsonPropertyRangeQueryFromViews() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testJsonPropertyRangeQueryFromViews method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    // plan1 - fromView
    ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4");

    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromView("opticFunctionalTest4", "master4");

    ModifyPlan output = plan1.where(p.cts.jsonPropertyRangeQuery(p.xs.string("id"), p.xs.string(">"), p.xs.intVal(300)))
        .joinInner(plan2, p.on(
            p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
            p.schemaCol("opticFunctionalTest4", "master4", "id")
            )
        )
        .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 3 == jsonBindingsNodes.size());
    assertEquals( "400", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
    assertEquals( "Master 200", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value")
        .asText());
    assertEquals( "600", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path("value").asText());
    assertEquals( "Master 100", jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name").path("value")
        .asText());
  }

  /*
   * Test export and import on more complex queries - TEST 18 plan1 uses
   * fromLexicon plan2 use fromLexicons
   */
  @Test
  public void testQNameExport()
  {
    System.out.println("In testQNameExport method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri1", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    Map<String, CtsReferenceExpr> index2 = new HashMap<String, CtsReferenceExpr>();
    index2.put("uri2", p.cts.uriReference());
    index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
    index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

    // export of the plan as String.

    ModifyPlan plan1 = p.fromLexicons(index1, "myCity",
        p.fragmentIdCol("fragId1"));
    // plan2 - fromLexicons
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));

    ModifyPlan output = plan1.where(p.cts.orQuery(p.cts.collectionQuery("/other/coll1"), p.cts.elementValueQuery("metro", "true")))
        .joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));
    StringHandle strHandle = new StringHandle();

    // Export the plan.
    String str = output.export(strHandle).get();
    System.out.println("Export of plan as String " + str);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    boolean isML11OrHigher = MarkLogicVersion.getMarkLogicVersion(client).getMajor() >= 11;
    assertTrue( 3 == jsonBindingsNodes.size());
    assertEquals( "beijing", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
    assertEquals( toWKT(isML11OrHigher, "39.900002,116.4"), jsonBindingsNodes.path(0).path("myCity.point").path("value").asText());
    assertEquals( "cape town", jsonBindingsNodes.path(1).path("myCity.city").path("value").asText());
    assertEquals( toWKT(isML11OrHigher, "-33.91,18.42"), jsonBindingsNodes.path(1).path("myCity.point").path("value").asText());
    assertEquals( "london", jsonBindingsNodes.path(2).path("myCity.city").path("value").asText());
    assertEquals( toWKT(isML11OrHigher, "51.5,-0.12"), jsonBindingsNodes.path(2).path("myCity.point").path("value").asText());

    // Verify exported string with QNAME - with random checks
    assertTrue( str.contains("\"fn\":\"from-lexicons\""));
    assertTrue(
        str.contains("\"fn\":\"fragment-id-col\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"fragId1\"]"));
	  assertTrue(str.contains("\"fn\":\"QName\", \"args\":[\"metro\"]"));
  }

  private String toWKT(boolean isML11OrHigher, String latLon) {
    if (isML11OrHigher) {
      String[] parts = latLon.split(",");
      return "POINT(" + parts[1] + " " + parts[0] + ")";
    }
    return latLon;
  }

  /*
   * Test cts queries with options and empty results on fromLexicons - TEST 15
   * and 16 plan1 uses fromLexicon plan2 use fromLexicons
   */
  @Test
  public void testEmptyAndInvalidResults()
  {
     System.out.println("In testEmptyAndInvalidResults method");

     // Create a new Plan. RowManager rowMgr = client.newRowManager();
     RowManager rowMgr = client.newRowManager();
     PlanBuilder p = rowMgr.newPlanBuilder();
     Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
     index1.put("uri1", p.cts.uriReference()); index1.put("city",
     p.cts.jsonPropertyReference("city")); index1.put("popularity",
     p.cts.jsonPropertyReference("popularity")); index1.put("date",
     p.cts.jsonPropertyReference("date")); index1.put("distance",
     p.cts.jsonPropertyReference("distance")); index1.put("point",
     p.cts.jsonPropertyReference("latLonPoint"));

     Map<String, CtsReferenceExpr>index2 = new HashMap<String,
     CtsReferenceExpr>(); index2.put("uri2", p.cts.uriReference());
     index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
     index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"));
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));

    ModifyPlan outputEmpty = plan1
             .where(p.cts.jsonPropertyWordQuery("city", "London", "case-sensitive"))
        .joinInner(plan2)
          .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
          .orderBy(p.asc(p.col("date")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
     rowMgr.resultDoc(outputEmpty, jacksonHandle);
     JsonNode node = null;
     StringBuilder strNull = new StringBuilder();
     int nSize = 0;
     try {
          node = jacksonHandle.get();
          nSize = node.size();
     } catch(Exception ex) {
        strNull.append(ex.getMessage());
        System.out.println("Exception message is " + strNull.toString());
     }
     // Should have NullPointerException.
    assertTrue( strNull.toString().contains("null"));
  }

  /*
   * Test multiple queries linearly. plan1 uses fromView plan2 use fromView
   */
  @Test
  public void testMultipleQuriesLinear()
  {
    System.out.println("In testMultipleQuriesLinear method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plan1 - fromView
    CtsQueryExpr andQuery1 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "400"),
        p.cts.jsonPropertyWordQuery("name", "Detail 400")
        );
    CtsQueryExpr andQuery2 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "500"),
        p.cts.jsonPropertyWordQuery("name", "Detail 500")
        );
    CtsQueryExpr andQuery3 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "600"),
        p.cts.jsonPropertyWordQuery("name", "Detail 600")
        );
    CtsQueryExpr orQuery = p.cts.orQuery(andQuery1, andQuery2, andQuery3);

    ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4");

    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromView("opticFunctionalTest4", "master4");

    ModifyPlan output = plan1.where(orQuery).joinInner(plan2, p.on(
        p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
        p.schemaCol("opticFunctionalTest4", "master4", "id")
        )
        )
        .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 3 == jsonBindingsNodes.size());
    assertEquals( "400", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
    assertEquals( "Master 200", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value")
        .asText());
    assertEquals( "600", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path("value").asText());
    assertEquals( "Master 100", jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name").path("value")
        .asText());
  }

  /*
   * Test multiple queries nested. plan1 uses fromView
   */
  @Test
  public void testMultipleQuriesNested()
  {
    System.out.println("In testMultipleQuriesNested method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plan1 - fromView
    CtsQueryExpr andQuery3 = p.cts.jsonPropertyWordQuery("id", "1");
    CtsQueryExpr andQuery4 = p.cts.jsonPropertyWordQuery("amount", "20.2");
    CtsQueryExpr andQuery7 = p.cts.jsonPropertyWordQuery("id", "4");
    CtsQueryExpr andQuery8 = p.cts.jsonPropertyWordQuery("name", "Detail 6");
    CtsQueryExpr andQuery5 = p.cts.jsonPropertyWordQuery("name", "Master 1");

    CtsQueryExpr andQuery6 = p.cts.jsonPropertyWordQuery("name", "Master 2");

    CtsQueryExpr andQuery1 = p.cts.andQuery(andQuery3, andQuery4);
    CtsQueryExpr andQuery2 = p.cts.andQuery(andQuery7, andQuery8);
    CtsQueryExpr andQuery9 = p.cts.andQuery(andQuery5, andQuery6);

    CtsQueryExpr orQuery = p.cts.orQuery(andQuery1, andQuery2);
    CtsQueryExpr andQuery10 = p.cts.andQuery(orQuery, andQuery9);

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail").where(andQuery10);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Make sure that nested and queries do not blow up the plan.
    assertTrue( 6 == jsonBindingsNodes.size());
  }

  /*
   * Sanity Checks for Plan Builder's fromSearch
   */
  @Test
  public void testfromSearchDocs() {
    System.out.println("In testfromSearchDocs method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanSystemColumn viewDocId = p.fragmentIdCol("planDocId");

    ModifyPlan output = p.fromSearchDocs(p.cts.wordQuery("Detail 400"))
                         .joinInner(
                                 p.fromView("opticFunctionalTest4", "detail4", "", viewDocId),
                                 p.on(p.fragmentIdCol("fragmentId"), viewDocId)
                                 )
                          .where (p.and(
                                  p.eq(p.col("color"),  p.xs.string("white")),
                                  p.eq(p.col("masterId"),  p.xs.intVal(100))
                          ));

    RowSet<RowRecord> rowSet = rowMgr.resultRows (output);
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    long rCount = 0;
    while (rowItr.hasNext()) {
      rCount++;
      record = rowItr.next();
      assertTrue( record.getInt("score") > 0);
      assertEquals(100, record.getInt("masterId"));
      assertEquals("white", record.getString("color"));
      assertEquals("Detail 600", record.getString("name"));
      assertEquals(600, record.getInt("id"));
      assertEquals("/optic/view/test/masterDetail5.json", record.getString("uri"));
    }
    if (rCount == 0) {
      fail("Could not traverse Iterator<RowRecord> in testfromSearch method");
    }
    else {
      assertEquals(1, rCount);
    }
  }

  /*
   * Sanity Checks for Plan Builder's fromSearch
   */
  @Test
  public void testfromSearchScores() {
    System.out.println("In testfromSearchScores method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master");

    ModifyPlan plan3 = plan1.joinFullOuter(plan2)
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.as("DetailId", p.schemaCol("opticFunctionalTest", "detail", "id")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.schemaCol("opticFunctionalTest", "master", "date"))));
    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");

    PlanBuilder.ExportablePlan output = p.fromSearch(p.cts.orQuery(
            p.cts.elementWordQuery("color", "blue"),
            p.cts.elementWordQuery("color", "green")))
            .joinInner(p.fromView("opticFunctionalTest", "detail", null, p.fragmentIdCol("viewDocId")
                        ),
                    p.on("fragmentId", "viewDocId")
            )
            .select()
            .orderBy(p.desc("amount"));

    RowSet<RowRecord> rowSet = rowMgr.resultRows (output);
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    JsonNode[] rowsToCheck =  new JsonNode[6];
    int rCount = 0;

    int score = 0;
    int[] masterid = new int[] {2, 1, 2, 1, 2, 1};
    String[] color = new String[] {"green", "green", "green", "blue", "blue", "blue"};
    String[] detname = new String[] {"Detail 6", "Detail 5", "Detail 4", "Detail 3", "Detail 2", "Detail 1"};
    double[] amount = new double[] {60.06, 50.05, 40.04, 30.03, 20.02, 10.01};
    int[] detailid = new int[] {6, 5, 4, 3, 2, 1};

    while (rowItr.hasNext()) {

      record = rowItr.next();
      score = record.getContainer("score").asInt();

      System.out.println("Results " + record.toString());
      assertTrue( score > 0);
      assertEquals(masterid[rCount], record.getInt("opticFunctionalTest.detail.masterId"));
      assertEquals(detailid[rCount], record.getInt("opticFunctionalTest.detail.id"));

      assertEquals(color[rCount], record.getString("opticFunctionalTest.detail.color"));
      assertEquals(detname[rCount], record.getString("opticFunctionalTest.detail.name"));
      assertEquals(amount[rCount], record.getDouble("opticFunctionalTest.detail.amount"), 0.0);

      rCount++;
    }
  }
}
