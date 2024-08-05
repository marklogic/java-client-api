/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanPrefixer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class TestOpticEnhancements extends AbstractFunctionalTest {

  private static Map<String, Object>[] literals1 = new HashMap[10];
  private static Map<String, Object>[] literals2 = new HashMap[4];
  private static Map<String, Object>[] storeInformation = new HashMap[4];
  private static Map<String, Object>[] internetSales = new HashMap[4];

  @BeforeAll
  public static void setUp() throws Exception {
    removeFieldIndices();
  // Install the TDE templates into schemadbName DB
  // loadFileToDB(client, filename, docURI, collection, document format)
  loadFileToDB(schemasClient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[]{"http://marklogic.com/xdmp/tde"});
  loadFileToDB(schemasClient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});
  loadFileToDB(schemasClient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});

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

  loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[]{"/other/coll1", "/other/coll2"});
  loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[]{"/optic/lexicon/test"});

  loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[]{"/optic/lexicon/test"});

    Map<String, Object> row = new HashMap<>();

    row.put("rowId", 1);
    row.put("colorId", 1);
    row.put("desc", "ball");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId", 2);
    row.put("desc", "square");
    literals1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId", 1);
    row.put("desc", "box");
    literals1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId", 1);
    row.put("desc", "hoop");
    literals1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId", 5);
    row.put("desc", "circle");
    literals1[4] = row;

    row = new HashMap<>();
    row.put("rowId", 6);
    row.put("colorId", 3);
    row.put("desc", "hOop");
    literals1[5] = row;

    row = new HashMap<>();
    row.put("rowId", 7);
    row.put("colorId", 2);
    row.put("desc", "hooP");
    literals1[6] = row;

    row = new HashMap<>();
    row.put("rowId", 8);
    row.put("colorId", 4);
    row.put("desc", "Mainframe");
    literals1[7] = row;

    row = new HashMap<>();
    row.put("rowId", 9);
    row.put("colorId", 5);
    row.put("desc", "JavaScript");
    literals1[8] = row;

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

    // Fill up storeInformation Map
    row = new HashMap<>();
    row.put("storeName", "Los Angeles");
    row.put("sales", 1500);
    row.put("txnDate", "Jan-05-1999");
    storeInformation[0] = row;

    row = new HashMap<>();
    row.put("storeName", "San Diego");
    row.put("sales", 250);
    row.put("txnDate", "Jan-07-1999");
    storeInformation[1] = row;

    row = new HashMap<>();
    row.put("storeName", "Los Angeles");
    row.put("sales", 300);
    row.put("txnDate", "Jan-08-1999");
    storeInformation[2] = row;

    row = new HashMap<>();
    row.put("storeName", "Boston");
    row.put("sales", 700);
    row.put("txnDate", "Jan-08-1999");
    storeInformation[3] = row;

    // Fill up internetSales Map
    row = new HashMap<>();
    row.put("txnDate", "Jan-07-1999");
    row.put("sales", 250);
    row.put("sales", 1500);
    internetSales[0] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-10-1999");
    row.put("sales", 535);
    row.put("storeName", "San Diego");
    internetSales[1] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-11-1999");
    row.put("sales", 320);
    row.put("storeName", "Los Angeles");
    internetSales[2] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-12-1999");
    row.put("sales", 750);
    row.put("storeName", "Boston");
    internetSales[3] = row;
  }

  @AfterAll
  public static void teardown() {
    restoreFieldIndices();
  }

  @Test
    public void testRedactRegexJoinInner()
    {
      System.out.println("In testRedactRegexJoinInner method");

      // Create a new Plan.
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();

      Map<String,Object> hoops = new HashMap<>();
      hoops.put("pattern",     "(h)([A-Z])(op)");
      hoops.put("replacement", "h=$2=");
      Map<String,Object> mf = new HashMap<>();
      mf.put("pattern", "(Main)([a-z])(rame)");
      mf.put("replacement", "$3Obsolate$2$1");
      Map<String,Object> blackColor = new HashMap<>();
      blackColor.put("pattern",     "bl(ac)k");
      blackColor.put("replacement", "AC");
      Map<String,Object> redColor = new HashMap<>();
      redColor.put("pattern",     "red");
      redColor.put("replacement", "RED");

      // plans from literals
      ModifyPlan plan1 = p.fromLiterals(literals1);
      ModifyPlan plan2 = p.fromLiterals(literals2);
      ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")))
          .bind(p.rdt.redactRegex(p.col("desc"), hoops))
          .bind(p.rdt.redactRegex(p.col("desc"), mf))
          .bind(p.rdt.redactRegex(p.col("colorDesc"), blackColor))
          .bind(p.rdt.redactRegex(p.col("colorDesc"), redColor));
;
      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(output, jacksonHandle);
      JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
      //System.out.println(jsonBindingsNodes);
      // Should have 7 nodes returned.
      assertEquals( 7, jsonBindingsNodes.size());
      JsonNode node = jsonBindingsNodes.path(0);
      assertEquals( "1", node.path("rowId").path("value").asText());
      assertEquals( "ball", node.path("desc").path("value").asText());
      assertEquals( "RED", node.path("colorDesc").path("value").asText());
      node = jsonBindingsNodes.path(3);
      assertEquals( "4", node.path("rowId").path("value").asText());
      assertEquals( "hoop", node.path("desc").path("value").asText());
      assertEquals( "RED", node.path("colorDesc").path("value").asText());
      node = jsonBindingsNodes.path(4);
      assertEquals( "h=O=", node.path("desc").path("value").asText());
      node = jsonBindingsNodes.path(6);
      assertEquals( "rameObsolatefMain", node.path("desc").path("value").asText());
    }

  @Test
  public void testRedactDatetime() {
    System.out.println("In testRedactDatetime method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, Object>[] literals1 = new HashMap[2];
    Map<String, Object> row = new HashMap<>();

    row.put("id", 1);
    row.put("name", "Master 1");
    row.put("date", "2021-12-31T23:59:59");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("id", 2);
    row.put("name", "Master 2");
    row.put("date", "2020-02-29T00:00:59");
    literals1[1] = row;

    Map<String,Object> dateOpts = new HashMap<>();
    dateOpts.put("level",   "parsed");
    dateOpts.put("picture", "[M01]/[D01]/[Y0001]");
    dateOpts.put("format",  "Month=[M01] Day=[D01]/xx [H01]:[m01]:[s01]");

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.orderBy(p.asc(p.col("id"))).bind(p.rdt.redactDatetime(p.col("date"), dateOpts));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);
    assertEquals( 2, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("id").path("value").asText());
    assertEquals( "Month=05 Day=12/xx 00:00:00", jsonBindingsNodes.path(0).path("date").path("value").asText());
    assertEquals( "2", jsonBindingsNodes.path(1).path("id").path("value").asText());
    assertEquals( "Month=04 Day=02/xx 00:00:00", jsonBindingsNodes.path(1).path("date").path("value").asText());
  }

  @Test
  public void testMaskDeterministic() {
    System.out.println("In testMaskDeterministic method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, Object> detailOpts = new HashMap<>();
    detailOpts.put("character", "lowerCase");
    detailOpts.put("maxLength", 10);

    Map<String, Object> masterOpts = new HashMap<>();
    masterOpts.put("character", "mixedCaseNumeric");
    masterOpts.put("maxLength", 33);

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.schemaCol("opticFunctionalTest", "detail", "masterId")
                    )
            )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.schemaCol("opticFunctionalTest", "detail", "amount"),
                    p.schemaCol("opticFunctionalTest", "detail", "color")
            )
            .orderBy(p.asc(p.col("Amount")))
            .bind(p.rdt.maskDeterministic(p.col("DetailName"), detailOpts))
            .bind(p.rdt.maskDeterministic(p.col("MasterName"), masterOpts));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);

    Pattern patternMaster = Pattern.compile("[a-z][A-Z][0-9]");
    Pattern patternDetail = Pattern.compile("[a-z]");

    assertEquals( 6, jsonBindingsNodes.size());

    String rowOneMasterName = jsonBindingsNodes.path(0).path("MasterName").path("value").asText();
    String rowOneDetailName = jsonBindingsNodes.path(0).path("DetailName").path("value").asText();
    assertEquals( 33, rowOneMasterName.length());
    assertTrue( patternMaster.matcher(rowOneMasterName).find());
    assertEquals( 10, rowOneDetailName.length());
    assertTrue( patternDetail.matcher(rowOneDetailName).find());

    assertEquals( "10.01", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }

  @Test
  public void testMaskRandom() {
    System.out.println("In testMaskRandom method");

    Map<String, Object> hoops = new HashMap<>();
    hoops.put("character", "mixedCase");
    hoops.put("maxLength", 10);

    Map<String, Object> colorOpts = new HashMap<>();
    colorOpts.put("character", "lowerCase");
    colorOpts.put("maxLength", 20);
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    JacksonHandle jacksonHandle = new JacksonHandle();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);
    ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")))
            .bind(p.rdt.maskRandom(p.col("desc"), hoops))
            .bind(p.rdt.maskRandom(p.col("colorId"), colorOpts));
    rowMgr.resultDoc(output, jacksonHandle);

    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);

    Pattern patternDesc = Pattern.compile("[a-z][A-Z]");
    Pattern patternColorId = Pattern.compile("[a-z]");

    // Should have 7 nodes returned.
    assertEquals( 7, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    String rowOneDescName = jsonBindingsNodes.path(0).path("desc").path("value").asText();
    assertTrue( patternDesc.matcher(rowOneDescName).find());

    node = jsonBindingsNodes.path(3);
    String rowFourcolorId = jsonBindingsNodes.path(0).path("colorId").path("value").asText();
    assertTrue( patternColorId.matcher(rowFourcolorId).find());
  }

  @Test
  public void testRedactNumber()
  {
    System.out.println("In testRedactNumber method");
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer players = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer team = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("team_id");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    Map<String, Object> numbOpts = new HashMap<>();
    numbOpts.put("character", "mixedCase");
    numbOpts.put("min", 1000);
    numbOpts.put("max", 100000);
    numbOpts.put("format", "000,000");

    Map<String, Object> alphaOpts = new HashMap<>();
    alphaOpts.put("character", "lowerCase");
    alphaOpts.put("maxLength", 20);

    ModifyPlan player_plan = p.fromTriples(
            p.pattern(playerIdCol, players.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, players.iri("name"), playerNameCol),
            p.pattern(playerIdCol, players.iri("team"), playerTeamCol)
    );
    ModifyPlan team_plan = p.fromTriples(
            p.pattern(teamIdCol, team.iri("name"), teamNameCol),
            p.pattern(teamIdCol, team.iri("city"), teamCityCol)
    );
    ModifyPlan output = player_plan.joinInner(team_plan,
            p.on(playerTeamCol, teamIdCol),
            p.and(
                    p.gt(playerAgeCol, p.xs.intVal(27)), p.eq(teamNameCol, p.xs.string("Giants")))
    )
            .orderBy(p.asc(playerAgeCol))
            .select(
                    p.as("PlayerName", playerNameCol),
                    p.as("PlayerAge", playerAgeCol),
                    p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol))
            )
            .bind(p.rdt.redactNumber(p.col("PlayerAge"), numbOpts))
            .bind(p.rdt.maskDeterministic(p.col("TeamName"), alphaOpts)
		  );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    System.out.println(jsonBindingsNodes);

    Pattern patternName = Pattern.compile("[a-z]");
    Pattern patternAge = Pattern.compile("(\\d)+,(\\d)+");

    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);

    String rowOneTeamName = jsonBindingsNodes.path(0).path("TeamName").path("value").asText();
    String rowOnePlayerAge = jsonBindingsNodes.path(0).path("PlayerAge").path("value").asText();

    assertEquals( "Josh Ream", first.path("PlayerName").path("value").asText());
    assertTrue(  patternName.matcher(rowOneTeamName).find());
    assertTrue( patternAge.matcher(rowOnePlayerAge).find());

    JsonNode second = jsonBindingsNodes.path(1);
    String rowTwoTeamName = jsonBindingsNodes.path(1).path("TeamName").path("value").asText();
    String rowTwoPlayerAge = jsonBindingsNodes.path(1).path("PlayerAge").path("value").asText();

    assertEquals( "John Doe", second.path("PlayerName").path("value").asText());
    assertTrue(  patternName.matcher(rowTwoTeamName).find());
    assertTrue( patternAge.matcher(rowTwoPlayerAge).find());
  }
}
