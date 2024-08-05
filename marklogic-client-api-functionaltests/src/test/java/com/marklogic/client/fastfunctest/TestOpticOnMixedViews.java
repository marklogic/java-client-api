/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;




/* The tests here are for sanity checks when we have plans from different sources
 * such as fromLexicons and fromtriples.
 */

public class TestOpticOnMixedViews extends AbstractFunctionalTest {

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
   * Checks for Plan Builder's fromLexicon and fromLiterals method. plan1 uses
   * fromLexicon plan2 use fromLiterals
   */
  @Test
  public void testfromLexiconsAndLiterals()
  {
    System.out.println("In testfromLexiconsAndLiterals method");

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

    Map<String, Object>[] literals1 = new HashMap[5];
    Map<String, Object> row = new HashMap<>();
    row.put("rowId", 1);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("popularity", 2);
    row.put("desc", "item");
    literals1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("popularity", 5);
    row.put("desc", "item");
    literals1[4] = row;

    // plan1 - fromLexicons
    ModifyPlan plan1 = p.fromLexicons(indexes, "myCity");
    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.joinInner(plan2).offsetLimit(0, 3).orderBy(p.viewCol("myCity", "city"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 3 == jsonBindingsNodes.size());
  }

  /*
   * Test join inner with joinInnerDoc pan1 uses fromLexicon plan2 use
   * fromLiterals
   */
  @Test
  public void testJoinInnerWithInnerDocMixed()
  {
    System.out.println("In testJoinInnerWithInnerDocMixed method");

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

    Map<String, Object>[] literals1 = new HashMap[5];
    Map<String, Object> row = new HashMap<>();
    row.put("rowId", 1);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("popularity", 2);
    row.put("desc", "item");
    literals1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("popularity", 1);
    row.put("desc", "item");
    literals1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("popularity", 5);
    row.put("desc", "item");
    literals1[4] = row;

    // plan1 - fromLexicons
    ModifyPlan plan1 = p.fromLexicons(indexes, "myCity");
    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.joinInner(plan2)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(p.colSeq("city", "uri", "rowId", "doc"))
        .orderBy(p.sortKeySeq(p.col("rowId"), p.col("city")))
        .offsetLimit(0, 5);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 5 == jsonBindingsNodes.size());
  }

  /*
   * Test join inner between view and lexicon pan1 uses fromView plan2 use
   * fromLexicons
   */
  @Test
  public void testJoinfromViewfronLexicons()
  {
    System.out.println("In testJoinInnerWithInnerDocMixed method");

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

    // plan1 - fromView
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    // plan2 - fromLexicons
    ModifyPlan plan2 = p.fromLexicons(indexes, "myCity");

    ModifyPlan output = plan1.joinInner(plan2).offsetLimit(0, 2);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 2 == jsonBindingsNodes.size());
  }

  /*
   * Test join between triples and literals pan1 uses fromTriples plan2 use
   * fromLiterals
   */
  @Test
  public void testJoinfromTriplesfromLiterals()
  {
    System.out.println("In testJoinfromTriplesfromLiterals method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");

    Map<String, Object>[] literals2 = new HashMap[5];
    Map<String, Object> row = new HashMap<>();
    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    literals2[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    literals2[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    literals2[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    literals2[3] = row;

    PlanTriplePatternSeq playerSeq = p.patternSeq(
    		p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
            p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
            );

    // plan1 - fromTriples
    ModifyPlan plan1 = p.fromTriples(playerSeq, "",  null, PlanTripleOption.DEDUPLICATED);
    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinLeftOuter(plan2).where
        (
            p.and
                (
                    p.eq(playerNameCol, p.xs.string("Matt Rose")),
                    p.or(p.gt(p.col("colorId"), p.xs.intVal(3)), p.lt(p.col("colorId"), p.xs.intVal(2)))
                )
        )
        .orderBy(p.col("colorId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    assertTrue( 2 == jsonBindingsNodes.size());
    assertEquals("Matt Rose", jsonBindingsNodes.path(0).path("player_name").path("value").asText());
    assertEquals( "red", jsonBindingsNodes.path(0).path("colorDesc").path("value").asText());
    assertEquals("Matt Rose", jsonBindingsNodes.path(1).path("player_name").path("value").asText());
    assertEquals( "yellow", jsonBindingsNodes.path(1).path("colorDesc").path("value").asText());
  }

  /*
   * Test basic Constructors plan1 uses fromTriples plan2 use fromLiterals
   */
  @Test
  public void testConstructor()
  {
    System.out.println("In testJoinfromTriplesfromLiterals method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");

    Map<String, Object>[] literals2 = new HashMap[5];
    Map<String, Object> row = new HashMap<>();
    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    literals2[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    literals2[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    literals2[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    literals2[3] = row;
    PlanTriplePatternSeq playerSeq = p.patternSeq(
    		p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
            p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
            );
    // plan1 - fromTriples
    ModifyPlan plan1 = p.fromTriples(playerSeq,  "",  null, PlanTripleOption.DEDUPLICATED);
    // plan2 - fromLiterals
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinLeftOuter(plan2).where
        (
            p.and
                (
                    p.eq(playerNameCol, p.xs.string("Matt Rose")),
                    p.or(p.gt(p.col("colorId"), p.xs.intVal(3)), p.lt(p.col("colorId"), p.xs.intVal(2)))
                )
        )
        .orderBy(p.col("colorId"))
        .select(p.as("myResults",
            p.jsonDocument(p.jsonObject(p.prop("myRows", p.jsonArray(p.jsonString(p.col("colorId")))))))
        );

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();

    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    assertTrue( 2 == jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("myResults").path("value").path("myRows").path(0).asText());
    assertEquals( "4", jsonBindingsNodes.path(1).path("myResults").path("value").path("myRows").path(0).asText());
  }
}
