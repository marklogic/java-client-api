/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

public class TestOpticOnTriples extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception
  {
    removeFieldIndices();
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

  @AfterAll
  public static void tearDown() {
    restoreFieldIndices();
  }

  @Test
  public void testfromTriples()
  {
    System.out.println("In testfromTriples method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanTriplePatternSeq patSeq = p.patternSeq(p.pattern(p.col("id"), p.sem.iri("http://marklogic.com/baseball/players/age"), p.col("age")));
    ExportablePlan plan1 = p.fromTriples(patSeq ,"players", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.col("age"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Verify first node.
    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    JsonNode jsonNameNode = null;
    if (nameNodesItr.hasNext()) {
      jsonNameNode = nameNodesItr.next();
      // Verify result 1's values.
      assertEquals( "19", jsonNameNode.path("players.age").path("value").asText());
      // Verify the last node's age value
      assertEquals( "34", jsonBindingsNodes.get(7).path("players.age").path("value").asText());
    }
    else {
      fail("Could not traverse the Eight Triplesin testfromTriples method");
    }
  }

  @Test
  public void testPrefixerfromTriples()
  {
    System.out.println("In testPrefixerfromTriples method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer rowGraph = p.prefixer("http://marklogic.com/baseball/players");

    PlanTriplePatternSeq patSeq = p.patternSeq(
            p.pattern(p.col("id"), rowGraph.iri("age"), p.col("age")));

    ExportablePlan plan1 = p.fromTriples(patSeq, "players", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.col("age"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Verify first node.
    Iterator<JsonNode> nameNodesItr = jsonBindingsNodes.elements();
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    JsonNode jsonNameNode = null;
    if (nameNodesItr.hasNext()) {
      jsonNameNode = nameNodesItr.next();
      // Verify result 1's values.
      assertEquals( "19", jsonNameNode.path("players.age").path("value").asText());
      // Verify the last node's age value
      assertEquals( "34", jsonBindingsNodes.get(7).path("players.age").path("value").asText());
    }
    else {
      fail("Could not traverse the Eight Triplesin testfromTriples method");
    }
  }

  /*
   * This test checks access with select aliased columns.
   *
   * Should return 8 results.
   */
  @Test
  public void testAccessWithSelectAlias()
  {
    System.out.println("In testAccessWithSelectAlias method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer rowGraph = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");
    PlanColumn nameCol = p.col("name");
    PlanColumn posCol = p.col("position");
    PlanTriplePatternSeq patSeq = p.patternSeq(
        p.pattern(idCol, rowGraph.iri("age"), ageCol),
        p.pattern(idCol, rowGraph.iri("name"), nameCol),
        p.pattern(idCol, rowGraph.iri("position"), posCol));

    ModifyPlan plan1 = p.fromTriples(patSeq,
        (String) null,
        (String) null,
        PlanTripleOption.DEDUPLICATED)
        .orderBy(p.desc(ageCol))
        .select(
            p.as("PlayerName", nameCol),
            p.as("PlayerPosition", posCol)
        );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "Aoki Yamada", first.path("PlayerName").path("value").asText());
    assertEquals( "First Base", first.path("PlayerPosition").path("value").asText());

    JsonNode eight = jsonBindingsNodes.path(7);
    assertEquals( "Pedro Barrozo", eight.path("PlayerName").path("value").asText());
    assertEquals( "Midfielder", eight.path("PlayerPosition").path("value").asText());
  }

  /*
   * This test checks join inner with condition.
   *
   * Should return 2 results.
   */
  @Test
  public void testJoinInnerWithCondition()
  {
    System.out.println("In testJoinInnerWithCondition method");
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
        );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "Josh Ream", first.path("PlayerName").path("value").asText());
    assertEquals( "29", first.path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", first.path("TeamName").path("value").asText());
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "John Doe", second.path("PlayerName").path("value").asText());
    assertEquals( "31", second.path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", second.path("TeamName").path("value").asText());
  }

  /*
   * This test checks union with where distinct.
   *
   * Should return 13 results.
   */
  @Test
  public void testUnionWithWhereDistinct()
  {
    System.out.println("In testUnionWithWhereDistinct method");

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
    ModifyPlan player_plan = p.fromTriples(
        p.pattern(playerIdCol, players.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, players.iri("name"), playerNameCol),
        p.pattern(playerIdCol, players.iri("team"), playerTeamCol)
        );
    ModifyPlan team_plan = p.fromTriples(
        p.pattern(teamIdCol, team.iri("name"), teamNameCol),
        p.pattern(teamIdCol, team.iri("city"), teamCityCol)
        );
    ModifyPlan output = player_plan.union(team_plan)
        .whereDistinct();
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 13 nodes returned.
    assertEquals( 13, jsonBindingsNodes.size());
  }

  /*
   * This test checks group by: 1) avg. Should return 4 results. Avg - 29 2)
   * max. Should return 4 results. Max - 34
   */
  @Test
  public void testGroupBys()
  {
    System.out.println("In testGroupBys method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer players = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer team = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");
    ModifyPlan player_plan = p.fromTriples(
        p.pattern(playerIdCol, players.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, players.iri("name"), playerNameCol),
        p.pattern(playerIdCol, players.iri("team"), playerTeamCol)
        );
    ModifyPlan team_plan = p.fromTriples(
        p.pattern(teamIdCol, team.iri("name"), teamNameCol),
        p.pattern(teamIdCol, team.iri("city"), teamCityCol)
        );
    // Group by avg
    ModifyPlan outputAvg = player_plan.joinInner(team_plan)
        .groupBy(teamNameCol, p.avg(p.col("AverageAge"), playerAgeCol))
        .orderBy(p.asc(p.col("AverageAge")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputAvg, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode fourth = jsonBindingsNodes.path(3);
    assertEquals( "Giants", fourth.path("team_name").path("value").asText());
    assertEquals( "29", fourth.path("AverageAge").path("value").asText());

    // Group by max.
    ModifyPlan outputMax = player_plan.joinInner(team_plan)
        .groupBy(teamNameCol, p.max(p.col("MaxAge"), playerAgeCol))
        .orderBy(p.desc(p.col("MaxAge")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputMax, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "Padres", first.path("team_name").path("value").asText());
    assertEquals( "34", first.path("MaxAge").path("value").asText());
  }

  /*
   * This test checks group by: 1) Count. Should return 4 results. Returns
   * Giants for Node 1 and for Node4 Athletics 2) Min on decimals. Should return
   * 4 results. Min - 25.45 3) Sum on all. Should return 1 result. Sum 350.4
   */
  @Test
  public void testGroupByCountAndSum()
  {
    System.out.println("In testGroupByCountAndSum method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerEffCol = p.col("player_eff");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanTriplePatternSeq playerSeq = p.patternSeq(
    		p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
            p.pattern(playerIdCol, bb.iri("team"), playerTeamCol),
            p.pattern(playerIdCol, bb.iri("eff"), playerEffCol)
            );
		ModifyPlan player_plan = p.fromTriples(playerSeq);
    ModifyPlan team_plan = p.fromTriples(
        p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    // group by Count
    ModifyPlan outputCnt = player_plan.joinInner(team_plan)
        .groupBy(teamNameCol, p.count(p.col("CountPlayer"), playerIdCol))
        .orderBy(p.desc(p.col("CountPlayer")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputCnt, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "Mariners", first.path("team_name").path("value").asText());
    assertEquals( "17", first.path("CountPlayer").path("value").asText());
    first = jsonBindingsNodes.path(3);
    assertEquals( "Athletics", first.path("team_name").path("value").asText());
    assertEquals( "1", first.path("CountPlayer").path("value").asText());

    // group by min on decimals
    ModifyPlan outputMin = player_plan.joinInner(team_plan)
        .groupBy(teamNameCol, p.min(p.col("MinEff"), playerEffCol))
        .orderBy(p.desc(p.col("MinEff")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputMin, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode third = jsonBindingsNodes.path(3);
    assertEquals( "Giants", third.path("team_name").path("value").asText());
    assertEquals( "25.45", third.path("MinEff").path("value").asText());

    // group by sum on all
    PlanTriplePatternSeq playerSumSeq = p.patternSeq(
    p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
    p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
    p.pattern(playerIdCol, bb.iri("team"), playerTeamCol),
    p.pattern(playerIdCol, bb.iri("eff"), playerEffCol));

    PlanTriplePatternSeq team_planSumSeq = p.patternSeq(
    p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
    p.pattern(teamIdCol, tm.iri("city"), teamCityCol));

	ModifyPlan player_planSum = p.fromTriples(playerSumSeq/* , "players", null, PlanTripleOption.DEDUPLICATED */);
    ModifyPlan team_planSum = p.fromTriples(team_planSumSeq);

    ModifyPlan outputSum = player_planSum.joinInner(team_planSum)
        .groupBy(null, p.sum(p.col("SumAll"), playerEffCol))
		.orderBy(p.desc(p.col("SumAll")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputSum, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    assertEquals(1, jsonBindingsNodes.size(),
		"Node not returned from testGroupByCountAndSum method: " + jsonBindingsNodes.toPrettyString());
    third = jsonBindingsNodes.path(0);
    assertEquals( "843.75", third.path("SumAll").path("value").asText());
  }

  /*
   * This test checks join inner with graph iri and options.
   */
  @Test
  public void testJoinInnerWithGraphIRI()
  {
    System.out.println("In testJoinInnerWithGraphIRI method");
    // 'TEST 13 - join inner with graph iri'
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanSystemColumn graphCol = p.graphCol("graphUri");

    PlanTriplePatternSeq patSeq = p.patternSeq(p.pattern(playerIdCol, bb.iri("age"), playerAgeCol, graphCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );
    ModifyPlan player_plan = p.fromTriples(patSeq, null, "/optic/player/triple/test", PlanTripleOption.DEDUPLICATED);
    ModifyPlan team_plan = p.fromTriples(
        p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );

    ModifyPlan output =
        player_plan.joinInner(team_plan)
            .where(p.eq(teamNameCol, p.xs.string("Giants")))
            .orderBy(p.asc(playerAgeCol))
            .select(
                p.as("PlayerName", playerNameCol),
                p.as("PlayerAge", playerAgeCol),
                p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol)),
                p.as("GraphName", graphCol)
            );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "Juan Leone", jsonResults.get(0).path("PlayerName").path("value").asText());
    assertEquals( "27", jsonResults.get(0).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(0).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(0).path("GraphName").path("value").asText());

    assertEquals( "Josh Ream", jsonResults.get(1).path("PlayerName").path("value").asText());
    assertEquals( "29", jsonResults.get(1).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(1).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(1).path("GraphName").path("value").asText());

    assertEquals( "John Doe", jsonResults.get(2).path("PlayerName").path("value").asText());
    assertEquals( "31", jsonResults.get(2).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(2).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(2).path("GraphName").path("value").asText());
  }

  /*
   * TEST 16 - join inner with array of graph iris Test options, p.sem.store()
   * and p.sem.rulesetStore()
   */
  @Test
  public void testJoinInnerWithSemStore()
  {
    System.out.println("In testJoinInnerWithSemStore method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");

    PlanSystemColumn playerGraphCol = p.graphCol("graphUri");

    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(p.pattern(playerIdCol, bb.iri("age"), playerAgeCol, playerGraphCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );
    SemStoreExpr storeExpr = p.sem.store("any");
    ModifyPlan player_plan = p.fromTriples(patPlayerSeq, (String) null, (String) null,
        PlanTripleOption.DEDUPLICATED)
        .where(storeExpr);

    PlanTriplePatternSeq patTeamSeq = p.patternSeq(p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    ModifyPlan team_plan = p.fromTriples(patTeamSeq,
        (String) null,
        null,
        PlanTripleOption.DEDUPLICATED
        );

    ModifyPlan output = player_plan.joinInner(team_plan)
        .where(p.eq(teamNameCol, p.xs.string("Giants")))
        .orderBy(p.asc(playerAgeCol))
        .select(
            p.as("PlayerName", playerNameCol),
            p.as("PlayerAge", playerAgeCol),
            p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol)),
            p.as("PlayerGraph", playerGraphCol));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "Juan Leone", jsonResults.get(0).path("PlayerName").path("value").asText());
    assertEquals( "27", jsonResults.get(0).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(0).path("TeamName").path("value").asText());

    assertEquals( "Josh Ream", jsonResults.get(1).path("PlayerName").path("value").asText());
    assertEquals( "29", jsonResults.get(1).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(1).path("TeamName").path("value").asText());

    assertEquals( "John Doe", jsonResults.get(2).path("PlayerName").path("value").asText());
    assertEquals( "31", jsonResults.get(2).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(2).path("TeamName").path("value").asText());

    // Verify overloaded fromTriples() with graphIRI
    ModifyPlan player_plan1 = p.fromTriples(patPlayerSeq, (String) null, "/optic/player/triple/test", PlanTripleOption.DEDUPLICATED);
    ModifyPlan output1 = player_plan.joinInner(team_plan)
        .where(p.eq(teamNameCol, p.xs.string("Giants")))
        .orderBy(p.asc(playerAgeCol))
        .select(
            p.as("PlayerName", playerNameCol),
            p.as("PlayerAge", playerAgeCol),
            p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol)),
            p.as("PlayerGraph", playerGraphCol));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output1, jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "Juan Leone", jsonResults.get(0).path("PlayerName").path("value").asText());
    assertEquals( "27", jsonResults.get(0).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(0).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(0).path("PlayerGraph").path("value").asText());

    assertEquals( "Josh Ream", jsonResults.get(1).path("PlayerName").path("value").asText());
    assertEquals( "29", jsonResults.get(1).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(1).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(1).path("PlayerGraph").path("value").asText());

    assertEquals( "John Doe", jsonResults.get(2).path("PlayerName").path("value").asText());
    assertEquals( "31", jsonResults.get(2).path("PlayerAge").path("value").asText());
    assertEquals( "San Francisco Giants", jsonResults.get(2).path("TeamName").path("value").asText());
    assertEquals( "/optic/player/triple/test", jsonResults.get(2).path("PlayerGraph").path("value").asText());
  }

  /*
   * This test checks access with qualifier.
   */
  @Test
  public void testAccessWithQualifier()
  {
    System.out.println("In testAccessWithQualifier method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");
    PlanColumn nameCol = p.col("name");
    PlanColumn teamCol = p.col("team");
    PlanTriplePatternSeq patSeq = p.patternSeq(p.pattern(idCol, bb.iri("age"), ageCol),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("team"), teamCol));
    ModifyPlan output = p.fromTriples(patSeq, "myPlayer", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.desc(ageCol));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    JsonNode nodeVal = jsonBindingsNodes.path(0);
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "http://marklogic.com/baseball/id#006", nodeVal.path("myPlayer.id").path("value").asText());
    assertEquals( "34", nodeVal.path("myPlayer.age").path("value").asText());
    assertEquals( "Aoki Yamada", nodeVal.path("myPlayer.name").path("value").asText());
    assertEquals( "http://marklogic.com/mlb/team/id/003", nodeVal.path("myPlayer.team").path("value").asText());
    nodeVal = jsonBindingsNodes.path(7);
    assertEquals( "http://marklogic.com/baseball/id#005", nodeVal.path("myPlayer.id").path("value").asText());
    assertEquals( "19", nodeVal.path("myPlayer.age").path("value").asText());
    assertEquals( "Pedro Barrozo", nodeVal.path("myPlayer.name").path("value").asText());
    assertEquals( "http://marklogic.com/mlb/team/id/002", nodeVal.path("myPlayer.team").path("value").asText());

    // access with qualifier with where and order by
    ModifyPlan output1 = p.fromTriples(patSeq, "myPlayer")
        .where(p.le(p.viewCol("myPlayer", "age"), p.xs.intVal(25)))
        .orderBy(p.desc(p.viewCol("myPlayer", "name")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output1, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    nodeVal = jsonBindingsNodes.path(0);

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    assertEquals( "Pedro Barrozo", nodeVal.path("myPlayer.name").path("value").asText());
    assertEquals( "19", nodeVal.path("myPlayer.age").path("value").asText());

    nodeVal = jsonBindingsNodes.path(2);
    assertEquals( "Bob Brian", nodeVal.path("myPlayer.name").path("value").asText());
    assertEquals( "23", nodeVal.path("myPlayer.age").path("value").asText());

    // access with qualifier and no subject
    ModifyPlan outputNoSubject = p.fromTriples(p.pattern(null, bb.iri("age"), ageCol), "myPlayer", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.desc(p.viewCol("myPlayer", "age")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputNoSubject, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    nodeVal = jsonBindingsNodes.path(0);

    // Should have 7 nodes returned.
    assertEquals( 7, jsonBindingsNodes.size());
    assertEquals( "34", nodeVal.path("myPlayer.age").path("value").asText());
    nodeVal = jsonBindingsNodes.path(6);
    assertEquals( "19", nodeVal.path("myPlayer.age").path("value").asText());

    // access with qualifier and no object
    ModifyPlan outputNoObject = p.fromTriples(p.pattern(idCol, bb.iri("age"), null), "myPlayer", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.asc(p.viewCol("myPlayer", "id")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputNoObject, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    assertEquals( 8, jsonBindingsNodes.size());

    nodeVal = jsonBindingsNodes.path(0);
    assertEquals( "http://marklogic.com/baseball/id#001", nodeVal.path("myPlayer.id").path("value").asText());
    nodeVal = jsonBindingsNodes.path(7);
    assertEquals( "http://marklogic.com/baseball/id#008", nodeVal.path("myPlayer.id").path("value").asText());

    // access with qualifier and fragment id column

    PlanSystemColumn fragIdCol = p.fragmentIdCol("fragId");
    PlanTriplePatternSeq patSeq1 = p.patternSeq(p.pattern(idCol, bb.iri("age"), ageCol, fragIdCol),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("team"), teamCol));

    ModifyPlan outputFragId =
        p.fromTriples(patSeq1, "myPlayer", null, PlanTripleOption.DEDUPLICATED)
            .where(p.le(p.viewCol("myPlayer", "age"), p.xs.intVal(25)))
            .orderBy(p.desc(p.viewCol("myPlayer", "name")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputFragId, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    // Should return 3 nodes.
    assertEquals( 3, jsonBindingsNodes.size());

    assertEquals( "Pedro Barrozo", jsonBindingsNodes.get(0).path("myPlayer.name").path("value").asText());
    assertEquals( "19", jsonBindingsNodes.get(0).path("myPlayer.age").path("value").asText());

    assertEquals( "Pat Crenshaw", jsonBindingsNodes.get(1).path("myPlayer.name").path("value").asText());
    assertEquals( "25", jsonBindingsNodes.get(1).path("myPlayer.age").path("value").asText());

    assertEquals( "Bob Brian", jsonBindingsNodes.get(2).path("myPlayer.name").path("value").asText());
    assertEquals( "23", jsonBindingsNodes.get(2).path("myPlayer.age").path("value").asText());
  }

  /*
   * This test checks access with iri predicate.
   */
  @Test
  public void testSemIRI()
  {
    System.out.println("In testSemIRI method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");

    PlanTriplePatternSeq patSeq = p.patternSeq(p.pattern(
        idCol,
        p.sem.iri("http://marklogic.com/baseball/players/age"),
        ageCol)
        );
    ModifyPlan output = p.fromTriples(patSeq, "myPlayer", null, PlanTripleOption.DEDUPLICATED)
        .orderBy(p.asc(p.viewCol("myPlayer", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    JsonNode nodeVal = jsonBindingsNodes.path(0);
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "31", nodeVal.path("myPlayer.age").path("value").asText());
    nodeVal = jsonBindingsNodes.path(7);
    assertEquals( "27", nodeVal.path("myPlayer.age").path("value").asText());

    // access with iri subject
    ModifyPlan output1 = p.fromTriples(p.pattern(p.sem.iri("http://marklogic.com/baseball/id#001"), bb.iri("age"), ageCol), "myPlayer");
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output1, jacksonHandle);
    jsonResults = jacksonHandle.get();
    jsonBindingsNodes = jsonResults.path("rows");
    nodeVal = jsonBindingsNodes.path(0);
    // Should have 1 nodes returned.
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "31", nodeVal.path("myPlayer.age").path("value").asText());
  }

  /*
   * This test checks join left outer with condition and multiple keymatch.
   */
  @Test
  public void testJoinLeftWithMultipleKeyMatch()
  {
    System.out.println("In testJoinLeftWithMultipleKeyMatch method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/other/bball/players#");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("team_id");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );
    ModifyPlan player_plan = p.fromTriples(patPlayerSeq, "myPlayer");

    PlanTriplePatternSeq patTeamSeq = p.patternSeq(p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    ModifyPlan team_plan = p.fromTriples(patTeamSeq, "myTeam");

    ModifyPlan output = player_plan.joinLeftOuter(team_plan, p.on(playerTeamCol, teamIdCol))
        .where(p.and(p.gt(playerAgeCol, p.xs.intVal(20)), p.isDefined(teamNameCol)));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 1 nodes returned.
    assertEquals( 1, jsonBindingsNodes.size());
  }

  /*
   * This test checks join inner outer with whereDistinct. Returns 8 results
   */
  @Test
  public void testJoinWhereDistinct()
  {
    System.out.println("In testJoinWhereDistinct method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    ModifyPlan player_plan = p.fromTriples(
        p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );

    ModifyPlan team_plan = p.fromTriples(
        p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    ModifyPlan output = player_plan.joinInner(team_plan)
        .whereDistinct()
        .orderBy(p.asc(playerAgeCol))
        .select(
            p.as("PlayerName", playerNameCol),
            p.as("PlayerAge", playerAgeCol),
            p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol))
        )
        .offsetLimit(0, 10);
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
  }

  /*
   * This test checks value processing functions. Returns 8 results
   */
  @Test
  public void testProcessingFunctions()
  {
    System.out.println("In testProcessingFunctions method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn playerEffCol = p.col("player_eff");
    PlanColumn playerDobCol = p.col("player_dob");
    PlanColumn teamIdCol = p.col("player_team");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    ModifyPlan player_plan =
        p.fromTriples(
            p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
            p.pattern(playerIdCol, bb.iri("team"), playerTeamCol),
            p.pattern(playerIdCol, bb.iri("dob"), playerDobCol),
            p.pattern(playerIdCol, bb.iri("eff"), playerEffCol)
            );

    ModifyPlan team_plan =
        p.fromTriples(
            p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
            p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
            );
    ModifyPlan output =
        player_plan.joinInner(team_plan)
            .whereDistinct()
            .select(
                p.as("name", p.fn.lowerCase(playerNameCol)),
                p.as("nameLength", p.fn.stringLength(playerNameCol)),
                p.as("firstname", p.fn.substringBefore(playerNameCol, p.xs.string(" "))),
                p.as("lastname", p.fn.substringAfter(playerNameCol, p.xs.string(" "))),
                p.as("year", p.fn.yearFromDate(playerDobCol)),
                p.as("month", p.fn.monthFromDate(playerDobCol)),
                p.as("day", p.fn.dayFromDate(playerDobCol)),
                p.as("log", p.math.log(playerEffCol))
            )
            .orderBy(p.sortKeySeq(p.col("lastname")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    JsonNode nodeVal = jsonBindingsNodes.path(0);
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());

    assertEquals( "pedro barrozo", nodeVal.path("name").path("value").asText());
    assertEquals( "13", nodeVal.path("nameLength").path("value").asText());
    assertEquals( "Pedro", nodeVal.path("firstname").path("value").asText());
    assertEquals( "1991", nodeVal.path("year").path("value").asText());
    assertEquals( "12", nodeVal.path("month").path("value").asText());
    assertEquals( "9", nodeVal.path("day").path("value").asText());
    assertEquals( "3.72930136861285", nodeVal.path("log").path("value").asText());
    nodeVal = jsonBindingsNodes.path(7);
    assertEquals( "aoki yamada", nodeVal.path("name").path("value").asText());
    assertEquals( "11", nodeVal.path("nameLength").path("value").asText());
    assertEquals( "Aoki", nodeVal.path("firstname").path("value").asText());
    assertEquals( "1987", nodeVal.path("year").path("value").asText());
    assertEquals( "3", nodeVal.path("month").path("value").asText());
    assertEquals( "15", nodeVal.path("day").path("value").asText());
    assertEquals( "4.01096295328305", nodeVal.path("log").path("value").asText());
  }

  /*
   * This test checks export plan. Returns StringHandle instance with plan
   * contents.
   */
  @Test
  public void testExportPlan()
  {
    System.out.println("In testExportPlan method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("team_id");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );
    ModifyPlan player_plan = p.fromTriples(patPlayerSeq, "myPlayer");

    PlanTriplePatternSeq patTeamSeq = p.patternSeq(p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    ModifyPlan team_plan = p.fromTriples(patTeamSeq, "myTeam");

    // Export as StringHandle
    StringHandle strHandle = new StringHandle();
    ExportablePlan exportedPlan = player_plan.joinLeftOuter(team_plan, p.on(playerTeamCol, teamIdCol))
        .where(p.and(p.gt(playerAgeCol, p.xs.intVal(20)), p.isDefined(teamNameCol)));
    exportedPlan.export(strHandle);
    // Verify the handle contents - Some of the plan fields.
    String str = strHandle.get();
    assertTrue( str.contains("\"fn\":\"from-triples\""));
    assertTrue( str.contains("\"fn\":\"join-left-outer\""));
    assertTrue(str.contains("\"args\":[\"player_id\"]"));
    assertTrue( str.contains("\"args\":[\"http://marklogic.com/baseball/players/age\"]"));
    assertTrue( str.contains("\"args\":[\"http://marklogic.com/baseball/players/team\"]"));
    assertTrue( str.contains("\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"20\"]"));
    assertTrue( str.contains("\"fn\":\"is-defined\""));
  }

  /*
   * Test multiple left join on different prefixers
   */
  @Test
  public void testMultipleLeftJoins()
  {
    System.out.println("In testMultipleLeftJoins method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanPrefixer bb = p.prefixer("http://marklogic.com/other/bball/players#");
    PlanPrefixer org = p.prefixer("http://marklogic.com/baseball/players/");
    PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("team_id");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(
        p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
        );
    PlanTriplePatternSeq patTeamSeq = p.patternSeq(
        p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
        p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
        );
    PlanTriplePatternSeq patOrgSeq = p.patternSeq(
        p.pattern(p.col("org_id"), org.iri("age"), p.col("org_age")),
        p.pattern(p.col("org_id"), org.iri("name"), p.col("org_name")),
        p.pattern(p.col("org_id"), org.iri("team"), p.col("org_team"))
        );

    ModifyPlan player_plan = p.fromTriples(patPlayerSeq, "myPlayer", null, PlanTripleOption.DEDUPLICATED);
    ModifyPlan team_plan = p.fromTriples(patTeamSeq, "myTeam", null, PlanTripleOption.DEDUPLICATED);
    ModifyPlan org_plan = p.fromTriples(patOrgSeq, "myOriginal", null, PlanTripleOption.DEDUPLICATED);

    ModifyPlan output = org_plan.joinLeftOuter(team_plan, p.on(p.col("org_team"), teamIdCol))
        .joinLeftOuter(player_plan, p.on(p.viewCol("myOriginal", "org_team"), p.viewCol("myPlayer", "player_team")))
        .orderBy(p.desc(p.viewCol("myOriginal", "org_age")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals("Aoki Yamada", jsonBindingsNodes.path(0).path("myOriginal.org_name").path("value").asText());
    assertEquals("Padres", jsonBindingsNodes.path(0).path("myTeam.team_name").path("value").asText());
    assertEquals("Phil Green", jsonBindingsNodes.path(0).path("myPlayer.player_name").path("value").asText());

    assertEquals("John Doe", jsonBindingsNodes.path(1).path("myOriginal.org_name").path("value").asText());
    assertEquals("Giants", jsonBindingsNodes.path(1).path("myTeam.team_name").path("value").asText());
    assertTrue(jsonBindingsNodes.path(1).path("myPlayer.player_name").asText().isEmpty());

    assertEquals("Pat Crenshaw", jsonBindingsNodes.path(5).path("myOriginal.org_name").path("value").asText());
    assertEquals("Mariners", jsonBindingsNodes.path(5).path("myTeam.team_name").path("value").asText());
    assertEquals("Seattle", jsonBindingsNodes.path(5).path("myTeam.team_city").path("value").asText());

    assertEquals("23", jsonBindingsNodes.path(6).path("myOriginal.org_age").path("value").asText());

    assertEquals("Pedro Barrozo", jsonBindingsNodes.path(7).path("myOriginal.org_name").path("value").asText());
  }

  // Negative Cases

  /*
   * This test checks additional parameter.
   *
   * Should return exceptions.
   */
  @Test
  public void testInvalidViewCol()
  {
    System.out.println("In testInvalidViewCol method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players/");
    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");
    PlanColumn nameCol = p.col("name");
    PlanColumn teamCol = p.col("team");
    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(p.pattern(idCol, bb.iri("age"), ageCol),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("team"), teamCol)
        );
    ModifyPlan output = p.fromTriples(patPlayerSeq, "myPlayer")
        .where(p.le(p.viewCol("myPlayer_Invalid", "age"), p.xs.intVal(25)))
        .orderBy(p.desc(p.viewCol("myPlayer", "name")));
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
    assertTrue( str.toString().contains("Column not found"));
    assertTrue( str.toString().contains("myPlayer_Invalid.age"));
  }

  /*
   * This test checks triples with invaid qualifier.
   *
   * Should return exception.
   */
  @Test
  public void testInvalidQualifier()
  {
    System.out.println("In testInvalidQualifier method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players/");
    PlanColumn nameCol = p.col("name");
    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");
    PlanColumn teamCol = p.col("team");

    PlanTriplePatternSeq patPlayerSeq = p.patternSeq(p.pattern(idCol, bb.iri("age"), ageCol),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("team"), teamCol)
        );
    ModifyPlan output = p.fromTriples(patPlayerSeq, "myPlayer", null, PlanTripleOption.DEDUPLICATED)
        .where(p.ge(p.viewCol("myPlayer_Invalid", "age"), p.xs.intVal(25)))
        .orderBy(p.desc(p.viewCol("myPlayer", "name")));

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
    assertTrue( str.toString().contains("Column not found: myPlayer_Invalid.age"));
  }

  /*
   * This test checks null value in avg function.
   *
   * Should return exception.
   */
  @Test
  public void testNullAvgFunction()
  {
    System.out.println("In testNullAvgFunction method");
    StringBuilder str = new StringBuilder();
    try {
      // Create a new Plan.
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();

      PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players/");
      PlanPrefixer tm = p.prefixer("http://marklogic.com/mlb/team/");

      PlanColumn playerAgeCol = p.col("player_age");
      PlanColumn playerIdCol = p.col("player_id");
      PlanColumn playerNameCol = p.col("player_name");
      PlanColumn playerTeamCol = p.col("player_team");
      PlanColumn teamIdCol = p.col("player_team");
      PlanColumn teamNameCol = p.col("team_name");
      PlanColumn teamCityCol = p.col("team_city");

      ModifyPlan player_plan = p.fromTriples(
          p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
          p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
          p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
          );
      ModifyPlan team_plan = p.fromTriples(
          p.pattern(teamIdCol, tm.iri("name"), teamNameCol),
          p.pattern(teamIdCol, tm.iri("city"), teamCityCol)
          );
      ModifyPlan output = player_plan.joinInner(team_plan)
          .groupBy(teamNameCol, p.avg("AverageAge", null))
          .orderBy(p.asc(p.col("AverageAge")));
      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is : " + str.toString());
    }
    assertTrue( str.toString().contains("column parameter for avg() cannot be null"));
  }

  /*
   * This test checks bindParam on triples' subject and object.
   */
  @Test
  public void testFromTriplesWithbindParam()
  {
    System.out.println("In testFromTriplesWithbindParam method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanParamExpr ageParam = p.param("player_age");

    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");

    ModifyPlan player_plan = p.fromTriples(p.pattern(playerIdCol, bb.iri("age"), ageParam),
        p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
        p.pattern(playerIdCol, bb.iri("team"), playerTeamCol));
    Plan player_plan1 = player_plan.bindParam(ageParam, p.xs.intVal(27));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(player_plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 1 nodes returned.
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "Juan Leone", jsonBindingsNodes.path(0).path("player_name").path("value").asText());
    assertEquals( "http://marklogic.com/mlb/team/id/001", jsonBindingsNodes.path(0).path("player_team").path("value").asText());

    // Verify bind value with different types, values.
    Plan player_plan2 = player_plan.bindParam(ageParam, p.xs.intVal(0));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(player_plan2, jacksonHandle);
    jsonResults = jacksonHandle.get();

    assertTrue( jsonResults == null);

    // Should not throw an exception, but return null results
    Plan player_plan3 = player_plan.bindParam(ageParam, p.xs.intVal(-1));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(player_plan3, jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null nodes returned.
    assertTrue( jsonResults == null);

    // Should not throw an exception, but return null results
    Plan player_plan4 = player_plan.bindParam(ageParam, p.xs.string("abcd"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(player_plan4, jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null nodes returned.
    assertTrue( jsonResults == null);
  }

  /*
   * This test checks union instead of join over pattern value permutations.
   */
  @Test
  public void testPatternValuePermutations()
  {
    System.out.println("In testPatternValuePermutations method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer bb = p.prefixer("http://marklogic.com/baseball/players");

    PlanColumn ageCol = p.col("age");
    PlanColumn idCol = p.col("id");
    PlanColumn nameCol = p.col("name");

    PlanColumn posCol = p.col("position");

    PlanTriplePositionSeq subjectSeq = p.subjectSeq(idCol);
    PlanTriplePositionSeq predicateSeq = p.predicateSeq(bb.iri("/age"));

    PlanTriplePositionSeq objectSeq = p.objectSeq(p.xs.intVal(23), p.xs.intVal(19));

    PlanTriplePatternSeq patSeq = p.patternSeq(p.pattern(subjectSeq, predicateSeq, objectSeq),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("position"), posCol),
        p.pattern(p.col("id"), p.sem.iri("http://marklogic.com/baseball/players/age"), p.col("age")));
    ModifyPlan output = p.fromTriples(patSeq, "myPlayer")
        .orderBy(p.desc(ageCol))
        .select(
            p.as("PlayerName", nameCol),
            p.as("PlayerPosition", posCol),
            p.as("PlayerAge", ageCol)
        );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    JsonNode nodeVal = jsonBindingsNodes.path(0);
    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    assertEquals( "Bob Brian", nodeVal.path("PlayerName").path("value").asText());
    assertEquals( "23", nodeVal.path("PlayerAge").path("value").asText());
    assertEquals( "Outfielder", nodeVal.path("PlayerPosition").path("value").asText());

    nodeVal = jsonBindingsNodes.path(1);
    assertEquals( "Pedro Barrozo", nodeVal.path("PlayerName").path("value").asText());
    assertEquals( "19", nodeVal.path("PlayerAge").path("value").asText());
    assertEquals( "Midfielder", nodeVal.path("PlayerPosition").path("value").asText());

    // Test for verifying that value permutations do not appear in the result
    // rows. They should not be part of results.
    // In the above plan we explicitly made a pattern to recover the ages.
    PlanTriplePatternSeq patSeq1 = p.patternSeq(p.pattern(subjectSeq, predicateSeq, objectSeq));
    ModifyPlan output1 = p.fromTriples(patSeq1, "myPlayer");
    JacksonHandle jacksonHandle1 = new JacksonHandle();
    jacksonHandle1.setMimetype("application/json");

    rowMgr.resultDoc(output1, jacksonHandle1);
    JsonNode jsonResults1 = jacksonHandle1.get();
    JsonNode jsonBindingsNodes1 = jsonResults1.path("rows");
    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes1.size());
    assertTrue(
        jsonBindingsNodes1.path(0).get("myPlayer.id").get("value").asText().contains("http://marklogic.com/baseball/id#005") ||
            jsonBindingsNodes1.path(0).get("myPlayer.id").get("value").asText().contains("http://marklogic.com/baseball/id#002"));
    assertTrue(
        jsonBindingsNodes1.path(1).get("myPlayer.id").get("value").asText().contains("http://marklogic.com/baseball/id#005") ||
            jsonBindingsNodes1.path(1).get("myPlayer.id").get("value").asText().contains("http://marklogic.com/baseball/id#002"));
    // Negative cases
    PlanTriplePositionSeq objectSeqNeg = p.objectSeq(p.xs.intVal(123), p.xs.intVal(100));

    PlanTriplePatternSeq patSeqNeg = p.patternSeq(p.pattern(subjectSeq, predicateSeq, objectSeqNeg),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("position"), posCol),
        p.pattern(p.col("id"), p.sem.iri("http://marklogic.com/baseball/players/age"), p.col("age")));
    ModifyPlan outputNeg = p.fromTriples(patSeqNeg, "myPlayer")
        .orderBy(p.desc(ageCol))
        .select(
            p.as("PlayerName", nameCol),
            p.as("PlayerPosition", posCol),
            p.as("PlayerAge", ageCol)
        );
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputNeg, jacksonHandle);
    JsonNode jsonResultsNeg = jacksonHandle.get();

    // Should have 0 nodes returned.
    assertTrue( jsonResultsNeg == null);

    PlanTriplePositionSeq objectSeqNeg1 = p.objectSeq(p.xs.intVal(23), p.xs.intVal(19), p.xs.intVal(-1));

    PlanTriplePatternSeq patSeqNeg1 = p.patternSeq(p.pattern(subjectSeq, predicateSeq, objectSeqNeg1),
        p.pattern(idCol, bb.iri("name"), nameCol),
        p.pattern(idCol, bb.iri("position"), posCol),
        p.pattern(p.col("id"), p.sem.iri("http://marklogic.com/baseball/players/age"), p.col("age")));
    ModifyPlan outputNeg1 = p.fromTriples(patSeqNeg1, "myPlayer")
        .orderBy(p.desc(ageCol))
        .select(
            p.as("PlayerName", nameCol),
            p.as("PlayerPosition", posCol),
            p.as("PlayerAge", ageCol)
        );
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputNeg1, jacksonHandle);
    JsonNode jsonResultsNeg1 = jacksonHandle.get();
    JsonNode jsonBindingsNodesNeg1 = jsonResultsNeg1.path("rows");
    JsonNode nodeValNeg1 = jsonBindingsNodesNeg1.path(0);
    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodesNeg1.size());
    assertEquals( "Bob Brian", nodeValNeg1.path("PlayerName").path("value").asText());
    assertEquals( "23", nodeValNeg1.path("PlayerAge").path("value").asText());
    assertEquals( "Outfielder", nodeValNeg1.path("PlayerPosition").path("value").asText());

    nodeValNeg1 = jsonBindingsNodesNeg1.path(1);
    assertEquals( "Pedro Barrozo", nodeValNeg1.path("PlayerName").path("value").asText());
    assertEquals( "19", nodeValNeg1.path("PlayerAge").path("value").asText());
    assertEquals( "Midfielder", nodeValNeg1.path("PlayerPosition").path("value").asText());
  }
}
