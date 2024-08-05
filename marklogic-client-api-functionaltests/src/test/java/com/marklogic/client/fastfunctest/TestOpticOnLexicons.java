/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.expression.PlanBuilder.PreparePlan;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanSystemColumn;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestOpticOnLexicons extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception
  {
    // Install the TDE templates
    // loadFileToDB(client, filename, docURI, collection, document format)
    loadFileToDB(schemasClient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemasClient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemasClient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });

    // Load XML data files.
    loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML", new String[] { "/optic/view/test" });
    loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML", new String[] { "/optic/player/triple/test" });
    loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML", new String[] { "/optic/team/triple/test" });
    loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML", new String[] { "/optic/other/player/triple/test" });
    loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML", new String[] { "/optic/lexicon/test" });

    // Load JSON data files.
    loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON", new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON", new String[] { "/optic/view/test" });

    loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[] { "/other/coll1", "/other/coll2" });
    loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[] { "/optic/lexicon/test" });

    loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[] { "/optic/lexicon/test" });
  }

  /*
   * Checks for Plan Builder's fromLexicon method. 1 plan1 uses strings as col
   * names, with date ordered and using intval 2 plan2 use colSeq() on select
   * method 3 plan3 use strings on select
   */
  @Test
  public void testfromLexicons()
  {
    System.out.println("In testfromLexicons method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    Map<String, CtsReferenceExpr> indexes = new HashMap<String, CtsReferenceExpr>();
    indexes.put("uri", p.cts.uriReference());
    indexes.put("city", p.cts.jsonPropertyReference("city"));
    indexes.put("popularity", p.cts.jsonPropertyReference("popularity"));
    indexes.put("date", p.cts.jsonPropertyReference("date"));
    indexes.put("distance", p.cts.jsonPropertyReference("distance"));
    indexes.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    // plan1 - Use strings as col names, with date ordered and using intval
    ExportablePlan plan1 = p.fromLexicons(indexes)
        .where(p.gt(p.col("popularity"), p.xs.intVal(2)))
        .orderBy(p.sortKeySeq(p.col("date")))
        .select(p.col("city"), p.col("popularity"), p.col("date"), p.col("distance"), p.col("point"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 4 == jsonBindingsNodes.size());
    // Verify first node.
    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();

    JsonNode jsonNameNode = null;

    jsonNameNode = nameNodesItr.next();
    // Verify result values are ordered by date. We will verify all elements
    // here.
    assertEquals("beijing", jsonNameNode.path("city").path("value").asText());
    assertEquals("5", jsonNameNode.path("popularity").path("value").asText());
    assertEquals("1981-11-09", jsonNameNode.path("date").path("value").asText());
    assertEquals("134.5", jsonNameNode.path("distance").path("value").asText());
    assertEquals(toWKT("39.900002,116.4"), jsonNameNode.path("point").path("value").asText());

    jsonNameNode = nameNodesItr.next();
    assertEquals("cape town", jsonNameNode.path("city").path("value").asText());
    assertEquals("3", jsonNameNode.path("popularity").path("value").asText());
    assertEquals("1999-04-22", jsonNameNode.path("date").path("value").asText());
    assertEquals("377.9", jsonNameNode.path("distance").path("value").asText());
    assertEquals(toWKT("-33.91,18.42"), jsonNameNode.path("point").path("value").asText());

    jsonNameNode = nameNodesItr.next();
    assertEquals("new york", jsonNameNode.path("city").path("value").asText());
    assertEquals("5", jsonNameNode.path("popularity").path("value").asText());
    assertEquals("2006-06-23", jsonNameNode.path("date").path("value").asText());
    assertEquals("23.3", jsonNameNode.path("distance").path("value").asText());
    assertEquals(toWKT("40.709999,-74.009995"), jsonNameNode.path("point").path("value").asText());

    jsonNameNode = nameNodesItr.next();
    assertEquals("london", jsonNameNode.path("city").path("value").asText());
    assertEquals("5", jsonNameNode.path("popularity").path("value").asText());
    assertEquals("2007-01-01", jsonNameNode.path("date").path("value").asText());
    assertEquals("50.4", jsonNameNode.path("distance").path("value").asText());
    assertEquals(toWKT("51.5,-0.12"), jsonNameNode.path("point").path("value").asText());

    System.out.println("Bindings after execution of Plan 1 is" + jsonBindingsNodes);

    // use colSeq() on select
    ExportablePlan plan2 = p.fromLexicons(indexes)
        .where(p.eq(p.col("popularity"), p.xs.intVal(5)))
        .orderBy(p.sortKeySeq(p.col("date")))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"));

    JacksonHandle jacksonHandle2 = new JacksonHandle();
    jacksonHandle2.setMimetype("application/json");

    rowMgr.resultDoc(plan2, jacksonHandle2);
    JsonNode jsonResults2 = jacksonHandle2.get();

    JsonNode jsonBindingsNodes2 = jsonResults2.path("rows");
    assertTrue( 3 == jsonBindingsNodes2.size());

    // use strings on select
    ExportablePlan plan3 = p.fromLexicons(indexes)
        .where(p.eq(p.col("popularity"), p.xs.intVal(5)))
        .orderBy(p.sortKeySeq(p.col("date")))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"));

    JacksonHandle jacksonHandle3 = new JacksonHandle();
    jacksonHandle3.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle3);
    JsonNode jsonResults3 = jacksonHandle3.get();

    JsonNode jsonBindingsNodes3 = jsonResults3.path("rows");
    assertTrue( 3 == jsonBindingsNodes3.size());
  }

  /*
   * Test join inner with joinInnerDoc
   */
  @Test
  public void testJoinInnerWithInnerDocfromLexicons()
  {
    System.out.println("In testJoinInnerWithInnerDocfromLexicons method");

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

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    // plan2
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");

    // plan3
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .joinDoc(p.col("doc"), p.col("uri2"))
        .orderBy(p.asc(p.col("date")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    StringHandle strHandle = new StringHandle();
    strHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, strHandle);

    JsonNode jsonInnerDocNodes = jsonResults.path("rows");
    assertTrue( 5 == jsonInnerDocNodes.size());
    // Verify first result
    assertEquals("1971-12-23", jsonInnerDocNodes.get(0).path("myCity.date").path("value").asText());
    assertEquals("/optic/lexicon/test/doc3.json", jsonInnerDocNodes.get(0).path("myCity.uri1").path("value").asText());
    assertEquals("12.9", jsonInnerDocNodes.get(0).path("myCity.distance").path("value").asText());
    assertEquals("new jersey", jsonInnerDocNodes.get(0).path("myCity.city").path("value").asText());
    assertEquals("2", jsonInnerDocNodes.get(0).path("myCity.popularity").path("value").asText());
    assertEquals(toWKT("40.720001,-74.07"), jsonInnerDocNodes.get(0).path("myCity.point").path("value").asText());

    assertEquals("/optic/lexicon/test/city3.json", jsonInnerDocNodes.get(0).path("myTeam.uri2").path("value").asText());
    assertEquals("new jersey", jsonInnerDocNodes.get(0).path("myTeam.cityName").path("value").asText());
    assertEquals("nets", jsonInnerDocNodes.get(0).path("myTeam.cityTeam").path("value").asText());

    assertEquals("new jersey", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityName").asText());
    assertEquals("3000000", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityPopulation").asText());
    assertEquals("nets", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityTeam").asText());

    assertEquals("1981-11-09", jsonInnerDocNodes.get(1).path("myCity.date").path("value").asText());
    assertEquals("1999-04-22", jsonInnerDocNodes.get(2).path("myCity.date").path("value").asText());
    assertEquals("2006-06-23", jsonInnerDocNodes.get(3).path("myCity.date").path("value").asText());

    // Verify last result, since records are ordered.
    assertEquals("2007-01-01", jsonInnerDocNodes.get(4).path("myCity.date").path("value").asText());
    assertEquals("/optic/lexicon/test/doc1.json", jsonInnerDocNodes.get(4).path("myCity.uri1").path("value").asText());
    assertEquals("50.4", jsonInnerDocNodes.get(4).path("myCity.distance").path("value").asText());
    assertEquals("london", jsonInnerDocNodes.get(4).path("myCity.city").path("value").asText());
    assertEquals("5", jsonInnerDocNodes.get(4).path("myCity.popularity").path("value").asText());
    assertEquals(toWKT("51.5,-0.12"), jsonInnerDocNodes.get(4).path("myCity.point").path("value").asText());

    assertEquals("/optic/lexicon/test/city1.json", jsonInnerDocNodes.get(4).path("myTeam.uri2").path("value").asText());
    assertEquals("london", jsonInnerDocNodes.get(4).path("myTeam.cityName").path("value").asText());
    assertEquals("arsenal", jsonInnerDocNodes.get(4).path("myTeam.cityTeam").path("value").asText());

    assertEquals("london", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityName").asText());
    assertEquals("2000000", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityPopulation").asText());
    assertEquals("arsenal", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityTeam").asText());

    // Validate RowRecord.
    // Validate the document content, Kind and MimeType.
    RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan3);
    Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
    RowRecord recordRow = recordRowItr.next();

    // Make sure that toString() does not blow up. The toString method is mostly
    // used for debugging.
    assertNotNull(recordRow.toString());
    String debugRR = recordRow.toString().trim();

    assertTrue( debugRR.contains("myCity.date:{kind: \"ATOMIC_VALUE\", type: \"xs:date\", value: \"1971-12-23\"}"));
    assertTrue( debugRR.contains("myCity.uri1:{kind: \"ATOMIC_VALUE\", type: \"xs:string\", value: \"/optic/lexicon/test/doc3.json\""));
    assertTrue( debugRR.contains("myCity.distance:{kind: \"ATOMIC_VALUE\", type: \"xs:double\", value: 12.9}"));
    assertTrue( debugRR.contains("myCity.point:{kind: \"ATOMIC_VALUE\", type: \"http://marklogic.com/cts#point\", value: \"" + toWKT("40.720001,-74.07") + "\"}"));

	  assertEquals("1971-12-23", recordRow.getString("myCity.date"));
	  assertEquals("/optic/lexicon/test/doc3.json", recordRow.getString("myCity.uri1"));
	  assertEquals(12.9, recordRow.getFloat("myCity.distance"), 0.1);
	  assertEquals("new jersey", recordRow.getString("myCity.city"));
    assertEquals(2, recordRow.getInt("myCity.popularity"));
    assertEquals(toWKT("40.720001,-74.07"), recordRow.getString("myCity.point"));

    // Use a handle different from Jackson.
    StringHandle strDocHandle = new StringHandle();
    recordRow.getContent("doc", strDocHandle);
    String docAsaString = strDocHandle.get();

    // Validate the document returned.
    assertTrue( docAsaString.contains("new jersey"));
    assertTrue( docAsaString.contains("cityName"));
    assertTrue( docAsaString.contains("3000000"));
    assertTrue( docAsaString.contains("cityPopulation"));
    assertTrue( docAsaString.contains("nets"));
    assertTrue( docAsaString.contains("cityTeam"));

    // Validate the format and Mime-type.
    assertTrue( recordRow.getContentFormat("doc") == Format.JSON);
    assertTrue( recordRow.getContentMimetype("doc").contains("application/json"));
  }

  /*
   * Test join inner with keymatch, viewCol, and date sort
   */
  @Test
  public void testJoinInnerKeymatchDateSort()
  {
    System.out.println("In testJoinInnerKeymatchDateSort method");

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

    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");

    ExportablePlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "new jersey", first.path("myCity.city").path("value").asText());
    assertEquals( "1971-12-23", first.path("myCity.date").path("value").asText());
    assertEquals( "new jersey", first.path("myTeam.cityName").path("value").asText());
    assertEquals( "nets", first.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "/optic/lexicon/test/city3.json", first.path("myTeam.uri2").path("value").asText());
    JsonNode five = jsonBindingsNodes.path(4);
    assertEquals( "london", five.path("myCity.city").path("value").asText());
    assertEquals( "2007-01-01", five.path("myCity.date").path("value").asText());
    assertEquals( "london", five.path("myTeam.cityName").path("value").asText());
    assertEquals( "arsenal", five.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "/optic/lexicon/test/city1.json", five.path("myTeam.uri2").path("value").asText());

    PlanColumn uriCol1 = p.col("uri1");
    PlanColumn cityCol = p.col("city");
    PlanColumn popCol = p.col("popularity");
    PlanColumn dateCol = p.col("date");
    PlanColumn distCol = p.col("distance");
    PlanColumn pointCol = p.col("point");
    PlanColumn uriCol2 = p.col("uri2");

    PlanColumn cityNameCol = p.col("cityName");
    PlanColumn cityTeamCol = p.col("cityTeam");
    // using element reference and viewname
    ModifyPlan outputNullVname = plan1.joinInner(plan2, p.on(p.viewCol(null, "city"), p.viewCol(null, "cityName")))
        .joinDoc(p.col("doc"), p.col("uri1"))
        .select(uriCol1, cityCol, popCol, dateCol, distCol, pointCol, p.as("nodes", p.xpath("doc", "//city")), uriCol2, cityNameCol, cityTeamCol)
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.desc("uri2"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputNullVname, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "cape town", node.path("myCity.city").path("value").asText());
    assertEquals( "377.9", node.path("myCity.distance").path("value").asText());
    assertEquals( "cape town", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "pirates", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "3", node.path("myCity.popularity").path("value").asText());

    node = jsonBindingsNodes.path(1);
    assertEquals( "beijing", node.path("myCity.city").path("value").asText());
    assertEquals( "134.5", node.path("myCity.distance").path("value").asText());
    assertEquals( "beijing", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "ducks", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    node = jsonBindingsNodes.path(4);
    assertEquals( "london", node.path("myCity.city").path("value").asText());
    assertEquals( "50.4", node.path("myCity.distance").path("value").asText());
    assertEquals( "london", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "arsenal", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());

    // TEST 4 - join inner with condition, joinInnerDoc and xpath

    ExportablePlan outputCondXpath = plan1.joinInner(plan2,
        p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName")),
        p.ne(p.col("popularity"), p.xs.intVal(3))
        )
        .joinDoc(p.col("doc"), p.col("uri1"))
        .select(uriCol1, cityCol, popCol, dateCol, distCol, pointCol, p.as("nodes", p.fn.number(p.xpath("doc", "//latLonPair/lat"))), uriCol2, cityNameCol, cityTeamCol)
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.desc(p.col("distance")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputCondXpath, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    node = jsonBindingsNodes.path(0);
    assertEquals( "beijing", node.path("myCity.city").path("value").asText());
    assertEquals( "134.5", node.path("myCity.distance").path("value").asText());
    assertEquals( "beijing", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "ducks", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("39.900002,116.4"), node.path("myCity.point").path("value").asText());
    assertEquals( "39.9", node.path("nodes").path("value").asText());

    node = jsonBindingsNodes.path(3);
    assertEquals( "new jersey", node.path("myCity.city").path("value").asText());
    assertEquals( "12.9", node.path("myCity.distance").path("value").asText());
    assertEquals( "new jersey", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "nets", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "2", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("40.720001,-74.07"), node.path("myCity.point").path("value").asText());
    assertEquals( "40.72", node.path("nodes").path("value").asText());

    // TEST 20 - join inner with joinInnerDoc and xpath
    ExportablePlan innerJoinInnerDocXPath = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .joinDoc(p.col("doc"), p.col("uri2"))
        .select(
            uriCol1, cityCol, popCol, dateCol, distCol, pointCol,
            p.viewCol("myCity", "__docId"),
            uriCol2, cityNameCol, cityTeamCol,
            p.viewCol("myTeam", "__docId"),
            p.as("nodes", p.xpath("doc", "/cityTeam"))
        )
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.asc(p.col("date")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(innerJoinInnerDocXPath, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");

    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());

    node = jsonBindingsNodes.path(0);
    assertEquals( "new jersey", node.path("myCity.city").path("value").asText());
    assertEquals( "12.9", node.path("myCity.distance").path("value").asText());
    assertEquals( "new jersey", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "nets", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "2", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("40.720001,-74.07"), node.path("myCity.point").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc3.json", node.path("myCity.uri1").path("value").asText());
    assertEquals( "/optic/lexicon/test/city3.json", node.path("myTeam.uri2").path("value").asText());

    node = jsonBindingsNodes.path(4);
    assertEquals( "london", node.path("myCity.city").path("value").asText());
    assertEquals( "50.4", node.path("myCity.distance").path("value").asText());
    assertEquals( "london", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "arsenal", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("51.5,-0.12"), node.path("myCity.point").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc1.json", node.path("myCity.uri1").path("value").asText());
    assertEquals( "/optic/lexicon/test/city1.json", node.path("myTeam.uri2").path("value").asText());
  }

  /*
   * Test prepare plan
   */
  @Test
  public void testPreparePlan()
  {
    System.out.println("In testPreparePlan method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    PlanColumn popCol = p.col("popularity");

    // prepare = 0
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    PreparePlan output1 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("city"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"))
        .prepare(0);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(output1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "beijing", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("39.900002,116.4"), first.path("myCity.point").path("value").asText());
    first = jsonBindingsNodes.path(3);
    assertEquals( "new york", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("40.709999,-74.009995"), first.path("myCity.point").path("value").asText());

    // prepare = 2
    PreparePlan output2 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("city"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"))
        .prepare(2);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(output2, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    first = jsonBindingsNodes.path(0);
    assertEquals( "beijing", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("39.900002,116.4"), first.path("myCity.point").path("value").asText());
    first = jsonBindingsNodes.path(3);
    assertEquals( "new york", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("40.709999,-74.009995"), first.path("myCity.point").path("value").asText());

    // prepare = 5
    PreparePlan output3 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("city"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"))
        .prepare(5);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(output3, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    first = jsonBindingsNodes.path(0);
    assertEquals( "beijing", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("39.900002,116.4"), first.path("myCity.point").path("value").asText());
    first = jsonBindingsNodes.path(3);
    assertEquals( "new york", first.path("myCity.city").path("value").asText());
    assertEquals( toWKT("40.709999,-74.009995"), first.path("myCity.point").path("value").asText());

    // prepare = -3
    PreparePlan output4 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("city"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"))
        .prepare(-3);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output4, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have XDMP-OPTION exceptions.
    assertTrue( str.toString().contains("XDMP-OPTION"));
    assertTrue( str.toString().contains("Invalid option \"optimize=-3\""));
  }

  /*
   * Test join inner with system col
   */
  @Test
  public void testJoinInnerWithSystemCol()
  {
    System.out.println("In testJoinInnerWithSystemCol method");

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

    PlanSystemColumn fragIdCol1 = p.fragmentIdCol("fragId1");
    PlanSystemColumn fragIdCol2 = p.fragmentIdCol("fragId2");

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", fragIdCol1);
    // plan2
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", fragIdCol2);

    // plan3
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);

    assertEquals( "new jersey", node.path("myCity.city").path("value").asText());
    assertEquals( "12.9", node.path("myCity.distance").path("value").asText());
    assertEquals( "new jersey", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "nets", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "2", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("40.720001,-74.07"), node.path("myCity.point").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc3.json", node.path("myCity.uri1").path("value").asText());
    assertEquals( "/optic/lexicon/test/city3.json", node.path("myTeam.uri2").path("value").asText());

    node = jsonBindingsNodes.path(4);
    assertEquals( "london", node.path("myCity.city").path("value").asText());
    assertEquals( "50.4", node.path("myCity.distance").path("value").asText());
    assertEquals( "london", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "arsenal", node.path("myTeam.cityTeam").path("value").asText());
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    assertEquals( toWKT("51.5,-0.12"), node.path("myCity.point").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc1.json", node.path("myCity.uri1").path("value").asText());
    assertEquals( "/optic/lexicon/test/city1.json", node.path("myTeam.uri2").path("value").asText());
  }

  /*
   * Test prepared plan and multiple order by and export.
   */
  @Test
  public void testPreparedPlanMultipleOrderBy()
  {
    System.out.println("In testPreparedPlanMultipleOrderBy method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanColumn popCol = p.col("popularity");

    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri1", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");

    PreparePlan preparedPlan = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.sortKeySeq(p.asc("popularity"), p.desc("date")))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"))
        .prepare(0);
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(preparedPlan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "cape town", node.path("myCity.city").path("value").asText());
    assertEquals( "3", node.path("myCity.popularity").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "5", node.path("myCity.popularity").path("value").asText());
    assertEquals( "1981-11-09", node.path("myCity.date").path("value").asText());

    StringHandle strHandle = new StringHandle();
    // Export the plan.
    String str = preparedPlan.export(strHandle).get();
    assertTrue( str.contains("\"fn\":\"from-lexicons\""));
    assertTrue( str.contains("\"fn\":\"prepare\""));
  }

  /*
   * conditional from join doc - TEST31
   */
  @Test
  public void testConditionalFromJoinDoc()
  {
    System.out.println("In testConditionalFromJoinDoc method");

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

    PlanSystemColumn fragIdCol1 = p.fragmentIdCol("fragId1");
    PlanSystemColumn fragIdCol2 = p.fragmentIdCol("fragId2");

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", fragIdCol1);
    // plan2
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", fragIdCol2);

    // plan
    ModifyPlan output = plan1.joinInner(plan2)
        .joinDoc(p.col("doc"), p.fragmentIdCol("fragId2"))
        .select(p.col("uri1"),
            p.col("city"),
            p.col("popularity"),
            p.col("date"),
            p.col("distance"),
            p.col("point"),
            p.as("nodes", p.xpath("doc", "//cityName")),
            p.col("uri2"),
            p.col("cityName"),
            p.col("cityTeam"))
        .where(p.eq(p.viewCol("myCity", "city"), p.sql.collatedString(p.col("nodes"), "http://marklogic.com/collation/")))
        .orderBy(p.col("uri1"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());

    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "london", node.path("myCity.city").path("value").asText());
    assertEquals( "london", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "arsenal", node.path("myTeam.cityTeam").path("value").asText());

    node = jsonBindingsNodes.path(4);
    assertEquals( "cape town", node.path("myCity.city").path("value").asText());
    assertEquals( "cape town", node.path("myTeam.cityName").path("value").asText());
    assertEquals( "pirates", node.path("myTeam.cityTeam").path("value").asText());
  }

  /*
   * TEST 28 - join doc uri with fragment id
   */
  @Test
  public void testJoinDocURI()
  {
    System.out.println("In testJoinDocURI method");

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

    ModifyPlan plan1 = p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"));
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));

    ExportablePlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .joinDocUri(p.col("doc"), p.fragmentIdCol("fragId1"))
        .orderBy(p.asc(p.col("date")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());

    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "new jersey", node.path("myCity.city").path("value").asText());
    assertEquals( "1971-12-23", node.path("myCity.date").path("value").asText());
    assertEquals( "12.9", node.path("myCity.distance").path("value").asText());

    node = jsonBindingsNodes.path(1);
    assertEquals( "beijing", node.path("myCity.city").path("value").asText());
    assertEquals( "1981-11-09", node.path("myCity.date").path("value").asText());
    assertEquals( "134.5", node.path("myCity.distance").path("value").asText());

    node = jsonBindingsNodes.path(4);
    assertEquals( "london", node.path("myCity.city").path("value").asText());
    assertEquals( "2007-01-01", node.path("myCity.date").path("value").asText());
    assertEquals( "50.4", node.path("myCity.distance").path("value").asText());
  }

  /*
   * Test Invalid range index- date
   */
  @Test
  public void testInvalidRangeIndexDate()
  {
    System.out.println("In testInvalidRangeIndexDate method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanColumn popCol = p.col("popularity");

    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    PreparePlan output = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("date_invalid"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: date_invalid"));
  }

  /*
   * Test Invalid refferance- city
   */
  @Test
  public void testInvalidRefferance()
  {
    System.out.println("In testInvalidRefferance method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanColumn popCol = p.col("popularity");

    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city_invalid"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    PreparePlan output = plan1.where(p.gt(popCol, p.xs.intVal(2)))
        .orderBy(p.asc("date_invalid"))
        .select(p.colSeq("city", "popularity", "date", "distance", "point"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue(
        str.toString().contains("XDMP-ELEMRIDXNOTFOUND: cts.jsonPropertyReference(\"city_invalid\") -- No  element range index for city_invalid collation"));
  }

  /*
   * Test Invalid refferance- qualifier
   */
  @Test
  public void testInvalidRefferanceQualifier()
  {
    System.out.println("In testInvalidRefferanceQualifier method");

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

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");
    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity_invalid", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: myCity_invalid.city"));
  }

  /*
   * Test Invalid viewCol
   */
  @Test
  public void testInvalidViewCol()
  {
    System.out.println("In testInvalidViewCol method");

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

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");
    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("invalid_view", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: invalid_view.city"));
  }

  /*
   * Test 1) invalid uri on join inner doc - TEST 10 2) null uri on join inner
   * doc - TEST 11 3) invalid doc on join inner doc
   */
  @Test
  public void testInvalidInnerDocElements()
  {
    System.out.println("In testInvalidInnerDocElements method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
    index1.put("uri", p.cts.uriReference());
    index1.put("city", p.cts.jsonPropertyReference("city"));
    index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
    index1.put("date", p.cts.jsonPropertyReference("date"));
    index1.put("distance", p.cts.jsonPropertyReference("distance"));
    index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");

    // invalid uri on join inner doc
    ModifyPlan outputInvalidURI = plan1.joinDoc(p.col("doc"), p.col("/foo/bar")).orderBy(p.asc("uri"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(outputInvalidURI, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: /foo/bar"));

    // null uri on join inner doc
    try {
      ModifyPlan outputNullURI = plan1.joinDoc(p.col("doc"), null).orderBy(p.asc("uri"));
      str = new StringBuilder();
      rowMgr.resultDoc(outputNullURI, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have java.lang.IllegalArgumentException exceptions.
    assertTrue( str.toString().contains("sourceCol parameter for joinDoc() cannot be null"));

    // invalid doc on join inner doc
    try {
      ModifyPlan outputNullURI = plan1.joinDoc(p.col("doc"), p.col("{foo: bar}")).orderBy(p.asc("uri"));
      str = new StringBuilder();
      rowMgr.resultDoc(outputNullURI, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: {foo: bar}"));
  }

  /*
   * Test Restricted xpath with unanamed nodes
   * SJS TEST 34
   */
  @Test
  public void testRestrictedXPathUnanamedNodes()
  {
    System.out.println("In testRestrictedXPathUnanamedNodes method");

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

    // plan1
    ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
    ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");

    PlanColumn uriCol1 = p.col("uri1");
    PlanColumn cityCol = p.col("city");
    PlanColumn popCol = p.col("popularity");
    PlanColumn dateCol = p.col("date");
    PlanColumn distCol = p.col("distance");
    PlanColumn pointCol = p.col("point");
    PlanColumn uriCol2 = p.col("uri2");

    PlanColumn cityNameCol = p.col("cityName");
    PlanColumn cityTeamCol = p.col("cityTeam");

    ModifyPlan UnnamedNodes = plan1.joinInner(plan2,
              p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName")),
              p.ne(p.col("popularity"), p.xs.intVal(3)))
            .joinDoc(p.col("doc"), p.col("uri1"))
            .select(uriCol1, cityCol, popCol, dateCol, distCol, pointCol, p.as("nodes", p.fn.number(p.xpath("doc", "/node('location')/object-node('latLonPair')/number-node()[1]"))), uriCol2, cityNameCol, cityTeamCol)
            .where(p.ge(p.col("nodes"), p.xs.intVal(0)))
            .orderBy(p.desc(p.col("distance")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(UnnamedNodes, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "london", first.path("myCity.city").path("value").asText());
    assertEquals( "51.5", first.path("nodes").path("value").asText());
    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals( "new jersey", third.path("myCity.city").path("value").asText());
    assertEquals( "40.72", third.path("nodes").path("value").asText());
  }

	/*
	 * Test Restricted xpath with predicate
	 * SJS TEST 35
	 */
	@Test
	public void testRestrictedXPathPredicate() {
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();

		Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));

		Map<String, CtsReferenceExpr> index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));

		PlanColumn cityCol = p.col("city");
		ModifyPlan plan = p.fromLexicons(index1, "myCity")
			.joinInner(
				p.fromLexicons(index2, "myTeam"),
				p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName"))
			)
			.joinDoc(p.col("doc"), p.col("uri1"))
			.select(cityCol,
				p.as(
					p.col("nodes"),
					p.xpath(p.col("doc"), p.xs.string("/description[fn:matches(., 'disc*')]"))
				)
			)
			.where(p.isDefined(p.col("nodes")));

		System.out.println("PLAN: " + plan.exportAs(ObjectNode.class).toPrettyString());
		JsonNode rows = rowMgr.resultDoc(plan, new JacksonHandle()).get().path("rows");
		assertEquals(1, rows.size(), "Expected only the London row since it's the only one with 'discoveries' in its description: " + rows.toPrettyString());
		JsonNode first = rows.path(0);
		assertEquals("london", first.path("myCity.city").path("value").asText());
		assertEquals("Two recent discoveries indicate probable very early settlements near the Thames", first.path("nodes").path("value").asText());
	}

  /*
   * Test Restricted xpath with predicate math:pow
   * SJS TEST 40
   */
  @Test
  public void testRestrictedXPathPredicateMath() {
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();

	  Map<String, CtsReferenceExpr> index1 = new HashMap<>();
	  index1.put("uri1", p.cts.uriReference());
	  index1.put("city", p.cts.jsonPropertyReference("city"));
	  index1.put("popularity", p.cts.jsonPropertyReference("popularity"));

	  Map<String, CtsReferenceExpr> index2 = new HashMap<>();
	  index2.put("uri2", p.cts.uriReference());
	  index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
	  index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

	  ModifyPlan plan = p.fromLexicons(index1, "myCity").joinInner(
			  p.fromLexicons(index2, "myTeam"),
			  p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName")),
			  p.ne(p.col("popularity"), p.xs.intVal(3))
		  )
		  .joinDoc(p.col("doc"), p.col("uri1"))
		  .select(
			  p.col("uri1"), p.col("city"), p.col("popularity"),
			  p.as(p.col("nodes"), p.xpath(p.col("doc"), p.xs.string("popularity[math:pow(., 2) eq 4]")))
		  )
		  .where(p.isDefined(p.col("nodes")));

	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan, jacksonHandle);
	  JsonNode rows = jacksonHandle.get().path("rows");
	  assertEquals(1, rows.size(), "Expected only the New Jersey row, which has a popularity of 2: " + rows.toPrettyString());
	  JsonNode first = rows.path(0);
	  assertEquals("new jersey", first.path("myCity.city").path("value").asText());
  }

}
