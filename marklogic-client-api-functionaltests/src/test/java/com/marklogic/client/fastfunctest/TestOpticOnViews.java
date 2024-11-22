/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.AccessPlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;



public class TestOpticOnViews extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception {
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
  }

  /*
   * This test checks a simple Schema and View ordered by id.
   *
   * The query should be returning 6 results ordered by id. Test asserts only on
   * the first node results. Uses JacksonHandle
   */
  @Test
  public void testnamedSchemaAndView() {
    System.out.println("In testnamedSchemaAndView method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());

    // Verify result 1's values.
    assertEquals( "xs:integer", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.id").path("type")
            .asText());
    assertEquals( "1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.id").path("value").asText());

    assertEquals( "xs:string", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.name").path("type")
            .asText());
    assertEquals( "Detail 1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.name").path("value")
            .asText());

    assertEquals( "xs:integer",
            jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.masterId").path("type").asText());
    assertEquals( "1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.masterId").path("value")
            .asText());

    assertEquals( "xs:double", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("type")
            .asText());
    assertEquals( "10.01", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("value")
            .asText());

    assertEquals( "xs:string", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.color").path("type")
            .asText());
    assertEquals( "blue", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    // Verify only the name value of other nodes in the array results.

    assertEquals( "Detail 2", jsonBindingsNodes.path(1).path("opticFunctionalTest.detail.name").path("value")
            .asText());
    assertEquals( "Detail 3", jsonBindingsNodes.path(2).path("opticFunctionalTest.detail.name").path("value")
            .asText());
    assertEquals( "Detail 4", jsonBindingsNodes.path(3).path("opticFunctionalTest.detail.name").path("value")
            .asText());
    assertEquals( "Detail 5", jsonBindingsNodes.path(4).path("opticFunctionalTest.detail.name").path("value")
            .asText());
    assertEquals( "Detail 6", jsonBindingsNodes.path(5).path("opticFunctionalTest.detail.name").path("value")
            .asText());
  }

  /*
   * This test checks a simple Schema and view with a qualifier ordered by id.
   *
   * The query should be returning 6 results ordered by id. Test asserts only on
   * the first node results. Uses JacksonHandle
   */
  @Test
  public void testnamedSchemaViewWithQualifier() {
    System.out.println("In testnamedSchemaViewWithQualifier method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    AccessPlan plan = p.fromView("opticFunctionalTest", "detail", "MarkLogicQAQualifier");

    plan.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.masterId"),
            p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.color"),
            p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.amount")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
  }

  /*
   * This test checks group by with a view. Should return 3 items. Tested
   * arrayAggregate, union, groupby methods Uses schemaCol for Columns selection
   */
  @Test
  public void testgroupBy() {
    System.out.println("In testgroupBy method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.union(plan2)
            .select(p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .groupBy(p.col("MasterName"), p.arrayAggregate("arrayDetail", "DetailName"))
            .orderBy(p.desc(p.col("MasterName")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    assertEquals( "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
    assertEquals( 0, jsonBindingsNodes.get(0).path("arrayDetail").path("value").size());
    assertEquals( "Master 1", jsonBindingsNodes.get(1).path("MasterName").path("value").asText());
    assertEquals( 0, jsonBindingsNodes.get(1).path("arrayDetail").path("value").size());
    assertEquals( 6, jsonBindingsNodes.get(2).path("arrayDetail").path("value").size());
    // Verify arrayDetail array
    assertEquals( "Detail 1", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(0).asText());
    assertEquals( "Detail 2", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(1).asText());
    assertEquals( "Detail 6", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(5).asText());
  }

  /*
   * This test checks join inner with keymatch. Should return 6 items.
   */
  @Test
  public void testjoinInnerKeyMatch() {
    System.out.println("In testjoinInnerKeyMatch method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals( "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "2", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());
  }

  /*
   * This test checks join inner with keymatch with select. Should return 6
   * items.
   */
  @Test
  public void testjoinInnerKeyMatchWithSelect() {
    System.out.println("In testjoinInnerKeyMatchWithSelect method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.desc(p.col("DetailName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "Master 2", first.path("MasterName").path("value").asText());
    assertEquals( "Detail 6", first.path("DetailName").path("value").asText());
    assertEquals( "60.06", first.path("opticFunctionalTest.detail.amount").path("value").asText());
    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals( "Master 1", sixth.path("MasterName").path("value").asText());
    assertEquals( "Detail 1", sixth.path("DetailName").path("value").asText());
    assertEquals( "blue", sixth.path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks join inner with keymatch with select. Should return 2
   * items.
   */
  @Test
  public void testjoinInnerGroupBy() {
    System.out.println("In testjoinInnerGroupBy method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .groupBy(p.schemaCol("opticFunctionalTest", "master", "name"), p.sum(p.col("DetailSum"), p.col("amount")))
            .orderBy(p.desc(p.col("DetailSum")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "Master 2", first.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "120.12", first.path("DetailSum").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "Master 1", second.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "90.09", second.path("DetailSum").path("value").asText());
  }

  /*
   * This test checks join left outer with select. Should return 2 items.
   */
  @Test
  public void testjoinLeftOuterWithSelect() {
    System.out.println("In testjoinLeftOuterWithSelect method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinLeftOuter(plan2)
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.col("MasterName"))));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 12 nodes returned.
    assertEquals( 12, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "Master 2", first.path("MasterName").path("value").asText());
    assertEquals( "Detail 6", first.path("DetailName").path("value").asText());

    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "Master 1", second.path("MasterName").path("value").asText());
    assertEquals( "Detail 6", second.path("DetailName").path("value").asText());

    // Verify twelveth node.
    JsonNode twelve = jsonBindingsNodes.path(11);
    assertEquals( "Master 1", twelve.path("MasterName").path("value").asText());
    assertEquals( "Detail 1", twelve.path("DetailName").path("value").asText());
  }

  /*
   * This test checks join cross product. Should return 12 items.
   */
  @Test
  public void testjoinCrossProduct() {
    System.out.println("In testjoinCrossProduct method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinCrossProduct(plan2)
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.desc(p.col("DetailName")))
            .orderBy(p.asc(p.col("MasterName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 12 nodes returned.
    assertEquals( 12, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "Master 1", first.path("MasterName").path("value").asText());
    assertEquals( "Detail 6", first.path("DetailName").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "Master 1", second.path("MasterName").path("value").asText());
    assertEquals( "Detail 5", second.path("DetailName").path("value").asText());
    // Verify second node.
    JsonNode twelve = jsonBindingsNodes.path(11);
    assertEquals( "Master 2", twelve.path("MasterName").path("value").asText());
    assertEquals( "Detail 1", twelve.path("DetailName").path("value").asText());
  }

  /*
   * This test checks inner join with accessor plan and on. Verifies
   * joinInner(), on(), offset() orderBy() desc(),limits(), RowSet and RowRecord
   * iterator with a view Should return 3 items.
   */
  @Test
  public void testjoinInnerAccessorPlanAndOn() {
    System.out.println("In testjoinInnerAccessorPlanAndOn method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");

    ModifyPlan plan3 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.intVal(3)))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "1", first.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals( "5", first.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 5", first.path("myDetail.name").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "2", second.path("myMaster.id").path("value").asText());
    assertEquals( "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals( "4", second.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 4", second.path("myDetail.name").path("value").asText());
    // Verify third node.
    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals( "1", third.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals( "3", third.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 3", third.path("myDetail.name").path("value").asText());

    // Verify RowSet and RowRecord.
    RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
    String[] colNames = rowSet.getColumnNames();
    Arrays.sort(colNames);

    String[] exptdColumnNames = {"myMaster.id", "myMaster.name", "myDetail.id", "myDetail.name"};
    Arrays.sort(exptdColumnNames);
    // Verify if all columns are available.
    assertTrue(Arrays.equals(colNames, exptdColumnNames));

    // Verify RowRecords using Iterator
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    if (rowItr.hasNext()) {
      record = rowItr.next();
      assertEquals( 1, record.getInt("myMaster.id"));
      assertEquals( 5, record.getInt("myDetail.id"));
      assertEquals( "Detail 5", record.getString("myDetail.name"));
      assertEquals( "Master 1", record.getString("myMaster.name"));

      XsStringVal str = record.getValueAs("myMaster.name", XsStringVal.class);
      assertEquals( "Master 1", str.getString());
    } else {
      fail("Could not traverse Iterator<RowRecord> in testjoinInnerOffsetAndLimit method");
    }
  }

  /*
   * This test checks join inner with null schema. Should return 3 nodes
   */
  @Test
  public void testjoinInnerWithNullSchema() {
    System.out.println("In testjoinInnerWithNullSchema method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView(null, "detail3");
    ModifyPlan plan2 = p.fromView(null, "master3");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol(null, "master3", "id"),
                            p.schemaCol(null, "detail3", "masterId")
                    )
            )
            .orderBy(p.asc(p.schemaCol(null, "detail3", "id")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 node3 returned.
    assertEquals( 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "7", first.path("opticFunctionalTest3.detail3.id").path("value").asText());
    assertEquals( "2016-03-01", first.path("opticFunctionalTest3.master3.date").path("value").asText());

    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "Detail 8", second.path("opticFunctionalTest3.detail3.name").path("value").asText());
    assertEquals( "89.36", second.path("opticFunctionalTest3.detail3.amount").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals( "Detail 11", third.path("opticFunctionalTest3.detail3.name").path("value").asText());
    assertEquals( "green", third.path("opticFunctionalTest3.detail3.color").path("value").asText());
  }

  /*
   * This test checks when we export plan. Should return 1 item.
   */
  @Test
  public void testExportPlanWithPlanCol() throws Exception {
    System.out.println("In testExportPlanWithPlanCol method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    JacksonHandle exportHandle = new JacksonHandle();

    AccessPlan plan1 = p.fromView("opticFunctionalTest", "detail");
    AccessPlan plan2 = p.fromView("opticFunctionalTest", "master");

    PlanColumn masterIdCol1 = plan1.col("masterId");
    PlanColumn masterIdCol2 = plan2.col("id");
    PlanColumn idCol1 = plan2.col("id");
    PlanColumn idCol2 = plan1.col("id");
    PlanColumn detailNameCol = plan1.col("name");

    ModifyPlan exportedPlan = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.on(idCol1, idCol2))
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);
    ModifyPlan exportedPlan2 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.on(idCol1, idCol2))
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(exportedPlan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "Master 1", first.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "2015-12-01", first.path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "10.01", first.path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "blue", first.path("opticFunctionalTest.detail.color").path("value").asText());

    // Export the Plan to a handle.
    exportedPlan.export(exportHandle);
    JsonNode exportNode = exportHandle.get();
    // verify parts of the Exported Plan String.
    assertEquals( "from-view", exportNode.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals( "join-inner", exportNode.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals( "from-view", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals( "order-by", exportNode.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals( "offset-limit", exportNode.path("$optic").path("args").get(3).path("fn").asText());

    // ExportAs the Plan to a handle.
    String strJackHandleAs = exportedPlan.exportAs(String.class);
    JsonNode JsonNodeAs = exportedPlan2.exportAs(JsonNode.class);

    // verify parts of the Exported Plan String.
    ObjectMapper mapper = new ObjectMapper();
    JsonNode exportedAs = mapper.readTree(strJackHandleAs);
    assertEquals( "from-view", exportedAs.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals( "join-inner", exportedAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals( "from-view", exportedAs.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals( "order-by", exportedAs.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals( "offset-limit", exportedAs.path("$optic").path("args").get(3).path("fn").asText());

    // Verify with exportAs to JsonNode
    assertEquals( "from-view", JsonNodeAs.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals( "join-inner", JsonNodeAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals( "from-view", JsonNodeAs.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals( "order-by", JsonNodeAs.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals( "offset-limit", JsonNodeAs.path("$optic").path("args").get(3).path("fn").asText());

    // Export a plan with error / incorrect column
    exportHandle = new JacksonHandle();
    exportedPlan.export(exportHandle);
    JsonNode exportNodedAA = exportHandle.get();

    assertEquals( "from-view", exportNodedAA.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals( "join-inner", exportedAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals( "order-by", exportedAs.path("$optic").path("args").get(2).path("fn").asText());
  }

  /*
   * This test checks offset with positive value, negative value and zero.
   * Should return 3 items, null, 6 items and exception
   */
  @Test
  public void testoffsetVales() {
    System.out.println("In testoffsetVales method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(p.eq(masterIdCol1, masterIdCol2))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offset(1)
            .limit(3);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 node returned.
    assertEquals( 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "1", first.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals( "5", first.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 5", first.path("myDetail.name").path("value").asText());
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "2", second.path("myMaster.id").path("value").asText());
    assertEquals( "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals( "4", second.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 4", second.path("myDetail.name").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals( "1", third.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals( "3", third.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 3", third.path("myDetail.name").path("value").asText());

    // offset with out of bound value
    ModifyPlan plan4 = plan1.joinInner(plan2)
            .where(
                    p.eq(masterIdCol1, masterIdCol2)
            )
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offset(10);
    JacksonHandle jacksonHandle10 = new JacksonHandle();
    jacksonHandle10.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandle10);
    JsonNode jsonResults10 = jacksonHandle10.get();

    // Should be null.
    assertNull( jsonResults10);

    // offset with 0 bound value
    ModifyPlan plan5 = plan1.joinInner(plan2)
            .where(p.eq(masterIdCol1, masterIdCol2))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offset(0);
    JacksonHandle jacksonHandleZOffset = new JacksonHandle();
    jacksonHandleZOffset.setMimetype("application/json");

    rowMgr.resultDoc(plan5, jacksonHandleZOffset);
    JsonNode jsonResultsZOffset = jacksonHandleZOffset.get();
    JsonNode jsonBindingsNodesZOffset = jsonResultsZOffset.path("rows");
    // Should have 6 node returned.
    assertEquals( 6, jsonBindingsNodesZOffset.size());

    // offset with negative bound value
    StringBuilder str = new StringBuilder();
    try {
      ModifyPlan plan6 = plan1.joinInner(plan2)
              .where(p.eq(masterIdCol1, masterIdCol2))
              .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
              .orderBy(p.desc(detailNameCol))
              .offset(-2);
      JacksonHandle jacksonHandleNegOffset = new JacksonHandle();
      jacksonHandleNegOffset.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandleNegOffset);
      JsonNode jsonResultsNegOffset = jacksonHandleNegOffset.get();
    } catch (Exception ex) {
      str.append(ex.getMessage());
    }
    assertTrue( str.toString().contains("Invalid arguments: offset must be a non-negative number: -2"));
  }

  /*
   * This test checks limit with positive value, negative value and zero. Should
   * return 3 items, null, 6 items and exception.
   */
  @Test
  public void testlimitValues() {
    System.out.println("In testlimitVales method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(masterIdCol1, masterIdCol2)
            )
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offset(1)
            .limit(3);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 node returned.
    assertEquals( 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals( "1", first.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals( "5", first.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 5", first.path("myDetail.name").path("value").asText());
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals( "2", second.path("myMaster.id").path("value").asText());
    assertEquals( "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals( "4", second.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 4", second.path("myDetail.name").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals( "1", third.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals( "3", third.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 3", third.path("myDetail.name").path("value").asText());

    // Limit with large value
    ModifyPlan plan4 = plan1.joinInner(plan2)
            .where(
                    p.eq(masterIdCol1, masterIdCol2)
            )
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .limit(10000);

    JacksonHandle jacksonHandleLarge = new JacksonHandle();
    jacksonHandleLarge.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleLarge);
    JsonNode jsonResultsLarge = jacksonHandleLarge.get();
    JsonNode jsonBindingsNodesLarge = jsonResultsLarge.path("rows");
    // Should have 6 node returned.
    assertEquals( 6, jsonBindingsNodesLarge.size());

    // Limit with 0 value
    StringBuilder strZ = new StringBuilder();
    try {
      // Throw exception
      ModifyPlan plan5 = plan1.joinInner(plan2)
              .where(
                      p.eq(masterIdCol1, masterIdCol2)
              )
              .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
              .orderBy(p.desc(detailNameCol))
              .limit(0);

      JacksonHandle jacksonHandleZ = new JacksonHandle();
      jacksonHandleZ.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandleZ);
      JsonNode jsonResultsZ = jacksonHandleLarge.get();
      JsonNode jsonBindingsNodesZ = jsonResultsZ.path("rows");
    } catch (Exception ex) {
      strZ.append(ex.getMessage());
    }
    assertTrue( strZ.toString().contains("Invalid arguments: limit must be a positive number: 0"));

    // Limit with negative value
    StringBuilder strNeg = new StringBuilder();
    try {
      // Throw exception
      ModifyPlan plan6 = plan1.joinInner(plan2)
              .where(p.eq(masterIdCol1, masterIdCol2))
              .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
              .orderBy(p.desc(detailNameCol))
              .limit(-2);

      JacksonHandle jacksonHandleNeg = new JacksonHandle();
      jacksonHandleNeg.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandleNeg);
      JsonNode jsonResultsNeg = jacksonHandleNeg.get();
      JsonNode jsonBindingsNodesNeg = jsonResultsNeg.path("rows");
    } catch (Exception ex) {
      strNeg.append(ex.getMessage());
    }
    assertTrue( strNeg.toString().contains("Invalid arguments: limit must be a positive number: -2"));
  }

  /*
   * This test checks joinInner() with where disctinct Should return 3 items.
   * Uses schemaCol and viewCol for Columns selection
   */
  @Test
  public void testjoinInnerWhereDisctinct() {
    System.out.println("In testjoinInnerWhereDisctinct method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "master", "id")))
            .select(p.col("color"))
            .whereDistinct()
            .orderBy(p.desc(p.col("color")));
    // Using Jackson to traverse the list.
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    assertEquals( "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "blue", jsonBindingsNodes.get(1).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks joinLeftOuter with a view. Should return 12 items. Uses
   * schemaCol and viewCol for Columns selection Processing Each Row As a
   * Separate JSON
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testJoinLeftOuter() {
    System.out.println("In testJoinLeftOuter method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinLeftOuter(plan2)
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.schemaCol("opticFunctionalTest", "master", "date"))));

    RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
    // Verify RowRecords using Iterator
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    if (rowItr.hasNext()) {
      record = rowItr.next();
      assertEquals( "2015-12-02", record.getString("opticFunctionalTest.master.date"));

      assertEquals( "Master 2", record.getString("MasterName"));
      assertEquals( "Detail 6", record.getString("DetailName"));
      assertEquals( "green", record.getString("color"));
      assertEquals(60.06, record.getDouble("opticFunctionalTest.detail.amount"), 0.00);
    } else {
      fail("Could not traverse Iterator<RowRecord> in testJoinLeftOuter method");
    }

    // Using Jackson to traverse the list.
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 12 nodes returned.
    assertEquals( 12, jsonBindingsNodes.size());
    // Verify first node
    assertEquals( "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
    assertEquals( "2015-12-02", jsonBindingsNodes.get(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "Detail 6", jsonBindingsNodes.get(0).path("DetailName").path("value").asText());
    assertEquals(60.06, jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals( "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());

    // Verify twelveth node
    assertEquals( "Master 1", jsonBindingsNodes.get(11).path("MasterName").path("value").asText());
    assertEquals( "2015-12-01", jsonBindingsNodes.get(11).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "Detail 1", jsonBindingsNodes.get(11).path("DetailName").path("value").asText());
    assertEquals(10.01, jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals( "blue", jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  @Test
  public void testJoinFullOuter() {
    System.out.println("In testJoinFullOuter method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinFullOuter(plan2)
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.schemaCol("opticFunctionalTest", "master", "date"))));

    RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
    // Verify RowRecords using Iterator
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    if (rowItr.hasNext()) {
      record = rowItr.next();
      assertEquals( "2015-12-02", record.getString("opticFunctionalTest.master.date"));

      assertEquals( "Master 2", record.getString("MasterName"));
      assertEquals( "Detail 6", record.getString("DetailName"));
      assertEquals( "green", record.getString("color"));
      assertEquals(60.06, record.getDouble("opticFunctionalTest.detail.amount"), 0.00);
    } else {
      fail("Could not traverse Iterator<RowRecord> in testJoinFullOuter method");
    }

    // Using Jackson to traverse the list.
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 12 nodes returned.
    assertEquals( 12, jsonBindingsNodes.size());
    // Verify first node
    assertEquals( "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
    assertEquals( "2015-12-02", jsonBindingsNodes.get(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "Detail 6", jsonBindingsNodes.get(0).path("DetailName").path("value").asText());
    assertEquals(60.06, jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals( "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());

    // Verify twelveth node
    assertEquals( "Master 1", jsonBindingsNodes.get(11).path("MasterName").path("value").asText());
    assertEquals( "2015-12-01", jsonBindingsNodes.get(11).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "Detail 1", jsonBindingsNodes.get(11).path("DetailName").path("value").asText());
    assertEquals(10.01, jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals( "blue", jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks union and except with a view. Should return 8 items. Uses
   * schemaCol for Columns selection
   */
  @Test
  public void testUnion() {
    System.out.println("In testUnion method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 =
            p.fromView("opticFunctionalTest", "master");
    ModifyPlan plan2 =
            p.fromView("opticFunctionalTest2", "master");
    ModifyPlan plan3 =
            p.fromView("opticFunctionalTest2", "detail");
    ModifyPlan plan4 =
            p.fromView("opticFunctionalTest", "detail");
    ModifyPlan output =
            plan3.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "detail", "id")))
                    .union(plan4.select(p.as("unionId", p.col("id"))))
                    .except(
                            plan1.select(p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")))
                                    .union(plan2.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))))
                    )
                    .orderBy(p.col("unionId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 8 nodes returned.
    assertEquals( 8, jsonBindingsNodes.size());
    assertEquals( "5", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
    assertEquals( "6", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
    assertEquals( "7", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
    assertEquals( "8", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
    assertEquals( "9", jsonBindingsNodes.get(4).path("unionId").path("value").asText());

    assertEquals( "12", jsonBindingsNodes.get(7).path("unionId").path("value").asText());
  }

  /*
   * This test checks intersect on different schemas with a view. Should return
   * 4 items. Uses schemaCol for Columns selection Methods used - intersect
   */
  @Test
  public void testIntersectDiffSchemas() {
    System.out.println("In testIntersectDiffSchemas method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "master");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest2", "master");
    ModifyPlan plan3 = p.fromView("opticFunctionalTest", "detail");

    ModifyPlan plan4 = plan1.select(p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")))
            .union(plan2.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))))
            .intersect(
                    plan3.select(p.as("unionId", p.col("id")))
            )
            .orderBy(p.col("unionId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
    assertEquals( "2", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
    assertEquals( "3", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
    assertEquals( "4", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
  }

  /*
   * This test checks arithmetic operations with a view. Should return 6 items.
   * Uses schemaCol for Columns selection Math fns - add, subtract, modulo,
   * divide, multiply
   */
  @Test
  public void testArithmeticOperations() {
    System.out.println("In testArithmeticOperations method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                    p.col("masterId"))
            )
            .select(p.as("added", p.add(p.col("amount"), p.col("masterId"))),
                    p.as("substracted", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
                    p.as("modulo", p.modulo(p.col("amount"), p.viewCol("master", "id"))),
                    p.as("invSubstract", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
                    p.as("divided", p.divide(p.col("amount"), p.multiply(p.col("amount"), p.viewCol("detail", "id"))))
            )
            .orderBy(p.asc("substracted"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify first node
    assertEquals(11.01, jsonBindingsNodes.get(0).path("added").path("value").asDouble(), 0.00d);
    assertEquals(9.01, jsonBindingsNodes.get(0).path("substracted").path("value").asDouble(), 0.00d);
    assertEquals(0.00999999999999979, jsonBindingsNodes.get(0).path("modulo").path("value").asDouble(), 0.00d);
    assertEquals(1, jsonBindingsNodes.get(0).path("divided").path("value").asDouble(), 0.00d);
    // Verify sixth node
    assertEquals(62.06, jsonBindingsNodes.get(5).path("added").path("value").asDouble(), 0.00d);
    assertEquals(58.06, jsonBindingsNodes.get(5).path("substracted").path("value").asDouble(), 0.00d);
    assertEquals(0.0600000000000023, jsonBindingsNodes.get(5).path("modulo").path("value").asDouble(), 0.00d);
    assertEquals(0.166666666666667, jsonBindingsNodes.get(5).path("divided").path("value").asDouble(), 0.00d);
  }

  /*
   * This test checks MarkLogic buit-in function with a view. Should return 3
   * items. Uses schemaCol for Columns selection Math fns - median
   */
  @Test
  public void testBuiltinFuncs() {
    System.out.println("In testBuiltinFuncs method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(p.gt(
                    p.col("amount"), p.math.median(p.xs.doubleSeq(10.0, 40.0, 50.0, 30.0, 60.0, 0.0, 100.0))
                    )
            )
            .select(p.as("myAmount", p.viewCol("detail", "amount")))
            .whereDistinct()
            .orderBy(p.asc("myAmount"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    // Verify nodes
    assertEquals(40.04, jsonBindingsNodes.get(0).path("myAmount").path("value").asDouble(), 0.00d);
    assertEquals(50.05, jsonBindingsNodes.get(1).path("myAmount").path("value").asDouble(), 0.00d);
    assertEquals(60.06, jsonBindingsNodes.get(2).path("myAmount").path("value").asDouble(), 0.00d);
  }

  /*
   * This test checks inner join with accessor plan and on, diff data types.
   * Should return 3 items or exceptions.
   */
  @Test
  public void testjoinInnerWithDataTypes() {
    System.out.println("In testjoinInnerWithDataTypes method");
    StringBuilder str = new StringBuilder();

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    // Pass a double
    ModifyPlan plan3 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.doubleVal(3.0)))
            .where(p.eq(masterIdCol1, masterIdCol2))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());

    // Pass a String
    ModifyPlan plan4 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.string("3")))
            .where(p.eq(masterIdCol1, masterIdCol2))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);

    JacksonHandle jacksonHandleStr = new JacksonHandle();
    jacksonHandleStr.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleStr);
    JsonNode jsonResultsStr = jacksonHandleStr.get();
    // Should have 3 nodes returned.
    assertEquals( 3, jsonResultsStr.path("rows").size());

    // Pass as a date
    try {
      ModifyPlan plan5 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.date("3")))
              .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
              .orderBy(p.desc(detailNameCol))
              .offsetLimit(1, 100);

      JacksonHandle jacksonHandleDate = new JacksonHandle();
      jacksonHandleDate.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandleDate);
      JsonNode jsonResultsDate = jacksonHandleDate.get();
    } catch (Exception ex) {
      str.append(ex.toString());
    }
    assertTrue( str.toString().contains("java.lang.IllegalArgumentException: 3"));

    // Pass as a decimal

    ModifyPlan plan6 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.decimal(3.0)))
            .where(p.eq(masterIdCol1, masterIdCol2))
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offsetLimit(1, 100);

    JacksonHandle jacksonHandleDecimal = new JacksonHandle();
    jacksonHandleDecimal.setMimetype("application/json");

    rowMgr.resultDoc(plan6, jacksonHandleDecimal);
    JsonNode jsonResultsDecimal = jacksonHandleDecimal.get();
    JsonNode jsonBindingsNodesDecimal = jsonResultsDecimal.path("rows");
    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodesDecimal.size());
  }

  /*
   * This test checks plan builder with invalid Schema and view names.
   */
  @Test
  public void testinValidNamedSchemaView() {
    System.out.println("In testinValidNamedSchemaView method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // Verify for invalid schema name
    AccessPlan planInvalidSchema = p.fromView("opticFunctionalTestInvalid", "detail", "MarkLogicQAQualifier");
    planInvalidSchema.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionSch = null;
    try {
      rowMgr.resultDoc(planInvalidSchema, jacksonHandle);
    } catch (Exception ex) {
      exceptionSch = ex.getMessage();
      System.out.println("Exception message is " + exceptionSch.toString());
    }
    assertTrue(
            exceptionSch.contains("SQL-TABLENOTFOUND:") &&
                    exceptionSch.contains("Unknown table: Table 'opticFunctionalTestInvalid.detail' not found"));
    /* Original assert
    assertTrue(
            exceptionSch.contains("SQL-TABLENOTFOUND: plan.view(\"opticFunctionalTestInvalid\", \"detail\", null, \"MarkLogicQAQualifier\")") &&
                    exceptionSch.contains("Unknown table: Table 'opticFunctionalTestInvalid.detail' not found"));
    */
    // Verify for invalid view name
    AccessPlan planInvalidView = p.fromView("opticFunctionalTest", "detailInvalid", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionVw = null;
    try {
      rowMgr.resultDoc(planInvalidView, jacksonHandle);
    } catch (Exception ex) {
      exceptionVw = ex.getMessage();
      System.out.println("Exception message is " + exceptionVw.toString());
    }
    /* Original assert
    assertTrue(
            exceptionVw.contains("SQL-TABLENOTFOUND: plan.view(\"opticFunctionalTest\", \"detailInvalid\", null, \"MarkLogicQAQualifier\")") &&
                    exceptionVw.contains("Unknown table: Table 'opticFunctionalTest.detailInvalid' not found"));
     */
    assertTrue(
            exceptionVw.contains("SQL-TABLENOTFOUND") &&
                    exceptionVw.contains("Unknown table: Table 'opticFunctionalTest.detailInvalid' not found"));

    // Verify for empty view name
    AccessPlan planEmptyView = p.fromView("opticFunctionalTest", "", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionNoView = null;
    try {
      rowMgr.resultDoc(planEmptyView, jacksonHandle);
    } catch (Exception ex) {
      exceptionNoView = ex.getMessage();
      System.out.println("Exception message is " + exceptionNoView.toString());
    }
    assertTrue( exceptionNoView.contains("OPTIC-INVALARGS") &&
            exceptionNoView.contains("Invalid arguments: cannot specify fromView() without view name"));

    // Verify for empty Schma name
    AccessPlan planEmptySch = p.fromView("", "detail", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionNoySch = null;
    try {
      rowMgr.resultDoc(planEmptySch, jacksonHandle);
    } catch (Exception ex) {
      exceptionNoySch = ex.getMessage();
      System.out.println("Exception message is " + exceptionNoySch.toString());
    }
    assertTrue( exceptionNoySch.contains("OPTIC-INVALARGS") &&
            exceptionNoySch.contains("Invalid arguments: cannot specify fromView() with invalid schema name"));
  }

  /*
   * This test checks select ambiguous columns. Should return exceptions.
   */
  @Test
  public void testselectAmbiguousColumns() {
    System.out.println("In testselectAmbiguousColumns method");
    StringBuilder str = new StringBuilder();
    try {
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();
      ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail");
      ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master");
      ModifyPlan plan3 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.col("id")))
              .select(p.colSeq("id"));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-AMBCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-AMBCOLUMN"));
    assertTrue( str.toString().contains("Ambiguous column reference: found opticFunctionalTest.master.id and opticFunctionalTest.detail.id"));
  }

  /*
   * This test checks invalid schema name, view name and column name on
   * schemaCol. Should return exceptions.
   */
  @Test
  public void testinValidSchemaViewColOnSchemaCol() {
    System.out.println("In testinValidSchemaViewColOnSchemaCol method");
    StringBuilder strSchema = new StringBuilder();
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master");
    try {
      // Invalid schema name on schemaCol
      ModifyPlan plan3 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.schemaCol("opticFunctionalTest_invalid", "detail", "id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      strSchema.append(ex.getMessage());
      System.out.println("Exception message is " + strSchema.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( strSchema.toString().contains("SQL-NOCOLUMN"));
    assertTrue( strSchema.toString().contains("Column not found: opticFunctionalTest_invalid.detail.id"));

    // Invalid View name on schemaCol
    StringBuilder strView = new StringBuilder();
    try {
      ModifyPlan plan4 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail_invalid", "id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan4, jacksonHandle);
    } catch (Exception ex) {
      strView.append(ex.getMessage());
      System.out.println("Exception message is " + strView.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( strView.toString().contains("SQL-NOCOLUMN"));
    assertTrue( strView.toString().contains("Column not found: opticFunctionalTest.detail_invalid.id"));

    // Invalid Column name on schemaCol
    StringBuilder strCol = new StringBuilder();
    try {
      ModifyPlan plan5 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id_invalid")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandle);
    } catch (Exception ex) {
      strCol.append(ex.getMessage());
      System.out.println("Exception message is " + strCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( strCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue( strCol.toString().contains("Column not found: opticFunctionalTest.detail.id_invalid"));

    // Invalid column in where
    StringBuilder strWhereCol = new StringBuilder();
    try {
      ModifyPlan plan6 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id_invalid"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.col("id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandle);
    } catch (Exception ex) {
      strWhereCol.append(ex.getMessage());
      System.out.println("Exception message is " + strWhereCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( strWhereCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue( strWhereCol.toString().contains("Column not found: opticFunctionalTest.master.id_invalid"));

    // Invalid column in viewCol
    StringBuilder strViewCol = new StringBuilder();
    try {
      ModifyPlan plan7 = plan1.joinInner(plan2)
              .where(
                      p.eq(
                              p.schemaCol("opticFunctionalTest", "master", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.viewCol("detail_invalid", "id")));
      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");
      rowMgr.resultDoc(plan7, jacksonHandle);
    } catch (Exception ex) {
      strViewCol.append(ex.getMessage());
      System.out.println("Exception message is " + strViewCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( strViewCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue( strViewCol.toString().contains("Column not found: detail_invalid.id"));
  }

  /*
   * This test checks different number of columns. 1) intersect with different
   * number of columns 2) except with different number of columns Should return
   * exceptions.
   */
  @Test
  public void testDifferentColumns() {
    System.out.println("In testDifferentColumns method");
    StringBuilder strIntersect = new StringBuilder();
    JacksonHandle jacksonHandle = null;

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    // intersect with different number of columns
    try {
      ModifyPlan plan3 = plan1.select(p.schemaCol("opticFunctionalTest", "master", "id"))
              .intersect(
                      plan2.select(
                              p.schemaCol("opticFunctionalTest", "detail", "id"),
                              p.schemaCol("opticFunctionalTest", "master", "masterId")
                      )
              )
              .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));

      jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      strIntersect.append(ex.getMessage());
      System.out.println("Exception message is " + strIntersect.toString());
    }
    JsonNode jsonBindingsNodes = jacksonHandle.get();
    assertTrue( jsonBindingsNodes == null);
    // except with different number of columns
    StringBuilder strExcept = new StringBuilder();
    try {
      ModifyPlan plan4 = plan1.select(
              p.schemaCol("opticFunctionalTest", "master", "id")
      )
              .except(
                      plan2.select(
                              p.col("id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.col("id")));
      jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan4, jacksonHandle);
    } catch (Exception ex) {
      strExcept.append(ex.getMessage());
      System.out.println("Exception message is " + strExcept.toString());
    }
    jsonBindingsNodes = jacksonHandle.get();
    assertEquals( 2, jsonBindingsNodes.size());
  }

  /*
   * Test explain plan on / with 1) Valid plan 2) Use of StringHandle 3) Invalid
   * plan
   */
  @Test
  public void testExplainPlan() {
    System.out.println("In testExplainPlan method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
            .orderBy(p.col("id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color"),
                    fIdCol1,
                    fIdCol2
            )
            .orderBy(p.desc(p.col("DetailName")));
    JsonNode explainNode = rowMgr.explain(output, new JacksonHandle()).get();
    // Making sure explain() does not blow up for a valid plan.
    assertEquals( explainNode.path("node").asText(), "plan");
    if (isML11OrHigher) {
      assertEquals( explainNode.path("expr").path("columns").get(0).path("name").asText(), "MasterName");
    } else {
      assertEquals( explainNode.path("expr").path("columns").get(0).path("column").asText(), "DetailName");
    }
    // Invalid string - Use txt instead of json or xml
    String explainNodetxt = rowMgr.explain(output, new StringHandle()).get();
    System.out.println(explainNodetxt);
    assertTrue( explainNodetxt.contains("\"node\":\"plan\""));
    // Invalid Plan
    ModifyPlan plan3 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan4 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    // intersect with different number of columns
    JsonNode explainNodeInv = null;
    try {
      ModifyPlan outputInv = plan3.select(p.schemaCol("opticFunctionalTest", "master", "id"))
              .intersect(
                      plan4.select(
                              p.schemaCol("opticFunctionalTest", "detail", "id"),
                              p.col("masterId")
                      )
              )
              .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));

      explainNodeInv = rowMgr.explain(outputInv, new JacksonHandle()).get();
      assertEquals( explainNodeInv.path("node").asText(), "plan");
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      fail("Explain of Invalid plan has Exceptions");
    }
  }

  /*
   * Test on fromViews when fragment id is used
   */
  @Test
  public void testFragmentId() {
    System.out.println("In testFragmentId method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan1.joinInner(plan2).where(
            p.eq(
                    p.schemaCol("opticFunctionalTest", "master", "id"),
                    p.col("masterId")
            )
    )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color"),
                    fIdCol1,
                    fIdCol2
            )
            .orderBy(p.desc(p.col("DetailName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    JsonNode node = jsonBindingsNodes.get(0);

    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify nodes
    assertEquals( "Master 2", node.path("MasterName").path("value").asText());
    assertEquals( "Detail 6", node.path("DetailName").path("value").asText());
    assertEquals( "60.06", node.path("opticFunctionalTest.detail.amount").path("value").asText());
    node = jsonBindingsNodes.get(5);
    assertEquals( "Master 1", node.path("MasterName").path("value").asText());
    assertEquals( "Detail 1", node.path("DetailName").path("value").asText());
    assertEquals( "10.01", node.path("opticFunctionalTest.detail.amount").path("value").asText());
  }

  /*
   * This test checks the bind params on a where clause; params as PlanParamExpr. Should return 6 items.
   */
  @Test
  public void testjoinInnerWithBind() {
    System.out.println("In testjoinInnerWithBind method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    PlanParamExpr idParam = p.param("ID");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"), idParam)
            )
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3.bindParam(idParam, p.xs.intVal(1)), jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals( "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify with negative value.
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3.bindParam(idParam, -1), jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null returned.
    assertTrue( jsonResults == null);

    // Verify with double value.
    PlanParamExpr amtParam = p.param("AMT");
    ModifyPlan planAmt = p.fromView("opticFunctionalTest", "detail")
            .where(p.gt(p.col("amount"), amtParam)
            )
            .orderBy(p.col("id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planAmt.bindParam(amtParam, 10.1), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 5 rows returned.
    assertEquals( 5, jsonResults.size());

    assertEquals( "20.02", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "30.03", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "40.04", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "50.05", jsonResults.path(3).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "60.06", jsonResults.path(4).path("opticFunctionalTest.detail.amount").path("value").asText());

    // verify for Strings.
    PlanParamExpr detNameParam = p.param("DETAILNAME");
    ModifyPlan planStringBind = p.fromView("opticFunctionalTest", "detail")
            .where(p.eq(p.col("name"), detNameParam)
            )
            .orderBy(p.col("id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planStringBind.bindParam(detNameParam, p.xs.string("Detail 6")), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    assertEquals( 1, jsonResults.size());
    assertEquals( "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());

    // Verify with different types in multiple places.
    ModifyPlan planMultiBind = p.fromView("opticFunctionalTest", "detail")
            .where(p.and(p.eq(p.col("name"), detNameParam),
                    p.eq(p.col("id"), idParam)
                    )
            )
            .orderBy(p.col("id"));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planMultiBind.bindParam(detNameParam, p.xs.string("Detail 6")).bindParam("ID", 6), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 1 node returned.
    assertEquals( 1, jsonResults.size());
    assertEquals( "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }

  /*
   * This test checks the bind params on a where clause; params as String. Should return 6 items.
   */
  @Test
  public void testjoinInnerWithBindParamsAsString() {
    System.out.println("In testjoinInnerWithBindParamsAsString method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    PlanParamExpr idParam = p.param("ID");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"), idParam)
            )
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3.bindParam("ID", 1), jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals( "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals( "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify with negative value.
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3.bindParam("ID", -1), jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null returned.
    assertTrue( jsonResults == null);

    // Verify with double value.
    PlanParamExpr amtParam = p.param("AMT");
    ModifyPlan planAmt = p.fromView("opticFunctionalTest", "detail")
            .where(p.gt(p.col("amount"), amtParam)
            )
            .orderBy(p.col("id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planAmt.bindParam("AMT", 10.1), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 5 rows returned.
    assertEquals( 5, jsonResults.size());

    assertEquals( "20.02", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "30.03", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "40.04", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "50.05", jsonResults.path(3).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "60.06", jsonResults.path(4).path("opticFunctionalTest.detail.amount").path("value").asText());

    // verify for Strings.
    PlanParamExpr detNameParam = p.param("DETAILNAME");
    ModifyPlan planStringBind = p.fromView("opticFunctionalTest", "detail")
            .where(p.eq(p.col("name"), detNameParam)
            )
            .orderBy(p.col("id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planStringBind.bindParam(detNameParam, p.xs.string("Detail 6")), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    assertEquals( 1, jsonResults.size());
    assertEquals( "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());

    // Verify with different types in multiple places.
    ModifyPlan planMultiBind = p.fromView("opticFunctionalTest", "detail")
            .where(p.and(p.eq(p.col("name"), detNameParam),
                    p.eq(p.col("id"), idParam)
                    )
            )
            .orderBy(p.col("id"));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planMultiBind.bindParam(detNameParam, p.xs.string("Detail 6")).bindParam("ID", 6), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 1 node returned.
    assertEquals( 1, jsonResults.size());
    assertEquals( "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }


  // Test row serialization as Object. Other tests handle array and Iterator
  @Test
  public void testRowRecordAsObject() throws Exception {
    System.out.println("In testRowRecordAsObject method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(masterIdCol1, masterIdCol2)
            )
            .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
            .orderBy(p.desc(detailNameCol))
            .offset(1)
            .limit(1);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    RowSet<JsonNode> jsonResults = rowMgr.resultRowsAs(plan3, JsonNode.class);
    ArrayList<String> actual = new ArrayList<String>();
    ArrayList<String> expected = new ArrayList<String>();

    String[] colNames = jsonResults.getColumnNames();
    // Verify column names available in the object.
    for (String cols : colNames)
      actual.add(cols);
    Collections.sort(actual);

    expected.add("myMaster.id");
    expected.add("myMaster.name");
    expected.add("myDetail.id");
    expected.add("myDetail.name");
    Collections.sort(expected);

    assertTrue( expected.equals(actual));

    Iterator<JsonNode> jsonRowItr = jsonResults.iterator();
    JsonNode first = null;
    if (jsonRowItr.hasNext()) {
      first = jsonRowItr.next();
      System.out.println("Row iterated using JsonNode.class" + first.toString());
    } else {
      fail("No JsonNodes available when JsonNode.class used");
    }
    assertEquals( "1", first.path("myMaster.id").path("value").asText());
    assertEquals( "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals( "5", first.path("myDetail.id").path("value").asText());
    assertEquals( "Detail 5", first.path("myDetail.name").path("value").asText());

    jsonResults.close();

    RowSet<Document> xmlResults = rowMgr.resultRowsAs(plan3, Document.class);

    ArrayList<String> actualXmlCol = new ArrayList<String>();
    String[] colNamesXML = xmlResults.getColumnNames();

    // Verify column names available in the object.
    for (String cols : colNamesXML)
      actualXmlCol.add(cols);
    Collections.sort(actualXmlCol);
    assertTrue( expected.equals(actualXmlCol));

    Iterator<Document> xmlRowItr = xmlResults.iterator();
    DOMHandle firstXML = new DOMHandle();
    Document firstXMLDoc = null;
    if (xmlRowItr.hasNext()) {
      firstXMLDoc = xmlRowItr.next();
      String rowContents = firstXML.with(firstXMLDoc).toString();
      System.out.println("Row iterated using Document.class" + rowContents);
      assertTrue( rowContents.contains("<t:cell name=\"myMaster.id\" type=\"xs:integer\">1</t:cell>"));
      assertTrue( rowContents.contains("<t:cell name=\"myMaster.name\" type=\"xs:string\">Master 1</t:cell>"));
      assertTrue( rowContents.contains("<t:cell name=\"myDetail.id\" type=\"xs:integer\">5</t:cell>"));
      assertTrue( rowContents.contains("<t:cell name=\"myDetail.name\" type=\"xs:string\">Detail 5</t:cell>"));
    } else {
      fail("No JsonNodes available when Document.class used");
    }
  }

  //fromsql TEST 1 - join inner with keymatch
  @Test
  public void testFromSqljoinInnerWithkeymatch() {
    System.out.println("In testFromSqljoinInnerWithkeymatch method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromSql("SELECT * from opticFunctionalTest.detail");

    ModifyPlan plan2 = p.fromSql("SELECT * from opticFunctionalTest.master");

    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    assertEquals( 1, jsonResults.path(0).path("opticFunctionalTest.detail.id").path("value").asInt());
    assertEquals( 1, jsonResults.path(0).path("opticFunctionalTest.master.id").path("value").asInt());
    assertEquals( 1, jsonResults.path(0).path("opticFunctionalTest.detail.masterId").path("value").asInt());
    assertEquals( "Detail 1", jsonResults.path(0).path("opticFunctionalTest.detail.name").path("value").asText());
    assertEquals( 6, jsonResults.path(5).path("opticFunctionalTest.detail.id").path("value").asInt());
    assertEquals( 2, jsonResults.path(5).path("opticFunctionalTest.master.id").path("value").asInt());
    assertEquals( 2, jsonResults.path(5).path("opticFunctionalTest.detail.masterId").path("value").asInt());
    assertEquals( "Detail 6", jsonResults.path(5).path("opticFunctionalTest.detail.name").path("value").asText());
  }

  //fromsql TEST 2 - join inner with keymatch and select
  @Test
  public void testFromSqljoinInnerWithkeymatchSelect() {
    System.out.println("In testFromSqljoinInnerWithkeymatchSelect method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromSql("SELECT opticFunctionalTest.master.name AS MasterName, opticFunctionalTest.master.date, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.amount,  opticFunctionalTest.detail.color" +
            " FROM opticFunctionalTest.detail" +
            " INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId" +
            " ORDER BY DetailName DESC");

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    assertEquals( "Master 2", jsonResults.path(0).path("MasterName").path("value").asText());
    assertEquals( "Detail 6", jsonResults.path(0).path("DetailName").path("value").asText());
    assertEquals( "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "Master 1", jsonResults.path(5).path("MasterName").path("value").asText());
    assertEquals( "Detail 1", jsonResults.path(5).path("DetailName").path("value").asText());
    assertEquals( "blue", jsonResults.path(5).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  //fromsql TEST 4 - sql group by
  @Test
  public void testFromSqlGroupBy() {
    System.out.println("In testFromSqlGroupBy method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromSql("SELECT opticFunctionalTest.master.name, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.color, SUM(amount) AS DetailSum " +
            "          FROM opticFunctionalTest.detail " +
            "          INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId " +
            "          GROUP BY opticFunctionalTest.master.name " +
            "          ORDER BY DetailSum DESC");

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 2 nodes returned.
    assertEquals( 2, jsonResults.size());
    assertEquals( "Master 2", jsonResults.path(0).path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "120.12", jsonResults.path(0).path("DetailSum").path("value").asText());
    assertEquals( "Master 1", jsonResults.path(1).path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "90.09", jsonResults.path(1).path("DetailSum").path("value").asText());
  }

  //fromsql TEST 8 - select with empty string qualifier and as
  @Test
  public void testFromSqlSelectEmptyAs() {
    System.out.println("In testFromSqlSelectEmptyAs method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanExprColSeq colSeqSel = p.colSeq(
            p.schemaCol("opticFunctionalTest", "detail", "id"),
            p.schemaCol("opticFunctionalTest", "detail", "color"),
            p.as("masterName", p.schemaCol("opticFunctionalTest", "master", "name"))
    );

    ModifyPlan plan1 = p.fromSql("select * FROM opticFunctionalTest.detail INNER JOIN opticFunctionalTest.master WHERE opticFunctionalTest.detail.masterId = opticFunctionalTest.master.id")
            .select(colSeqSel)
            .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    assertEquals( "1", jsonResults.path(0).path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "blue", jsonResults.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "Master 1", jsonResults.path(0).path("masterName").path("value").asText());
    assertEquals( "6", jsonResults.path(5).path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "green", jsonResults.path(5).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "Master 2", jsonResults.path(5).path("masterName").path("value").asText());
  }

  //fromsql TEST 12 - arithmetic operations
  @Test
  public void testFromSqlArithmetic() {
    System.out.println("In testFromSqlArithmetic method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromSql("SELECT "
            + " (opticFunctionalTest.detail.amount + opticFunctionalTest.detail.masterId) AS added, "
            + " (opticFunctionalTest.detail.amount - opticFunctionalTest.master.id) AS substracted, "
            + " (opticFunctionalTest.detail.amount % opticFunctionalTest.master.id) AS modulo, "
            + " (opticFunctionalTest.detail.amount / (opticFunctionalTest.detail.amount * opticFunctionalTest.detail.id)) AS divided "
            + " FROM opticFunctionalTest.detail INNER JOIN opticFunctionalTest.master WHERE opticFunctionalTest.detail.masterId = opticFunctionalTest.master.id"
    )
            .orderBy(p.asc("substracted"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    assertEquals( "11.01", jsonResults.path(0).path("added").path("value").asText());
    assertEquals( "9.01", jsonResults.path(0).path("substracted").path("value").asText());
    assertEquals( "0.00999999999999979", jsonResults.path(0).path("modulo").path("value").asText());
    assertEquals( "1", jsonResults.path(0).path("divided").path("value").asText());
    assertEquals( "62.06", jsonResults.path(5).path("added").path("value").asText());
    assertEquals( "58.06", jsonResults.path(5).path("substracted").path("value").asText());
    assertEquals( "0.0600000000000023", jsonResults.path(5).path("modulo").path("value").asText());
    assertEquals( "0.166666666666667", jsonResults.path(5).path("divided").path("value").asText());
  }

  //fromsql TEST 19 - sql between with sql condition
  @Test
  public void testFromSqlBetweenAndSqlCondition() {
    System.out.println("In testFromSqlBetweenAndSqlCondition method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanExprColSeq colSeqSel = p.colSeq(
            p.col("id"),
            p.col("name"),
            p.col("masterId"),
            p.col("amount"),
            p.col("color")
    );

    ModifyPlan plan1 = p.fromSql("SELECT * FROM opticFunctionalTest.detail WHERE opticFunctionalTest.detail.amount BETWEEN 10 AND 40", "NewDetail")
            .where(p.sqlCondition("name <> 'Detail 1'"))
            .select(colSeqSel)
            .orderBy(p.asc("id"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 2 nodes returned.
    assertEquals( 2, jsonResults.size());
    assertEquals( "20.02", jsonResults.path(0).path("NewDetail.amount").path("value").asText());
    assertEquals( "30.03", jsonResults.path(1).path("NewDetail.amount").path("value").asText());
  }


  //fromsql TEST 27 - union with select, orderby, limit, and offset
  @Test
  public void testFromSqlUnionSelectOrderbyLimitOffset() {
	  if (isML12OrHigher) {
		  logger.info("Skipping as this fails intermittently on MarkLogic 12 for unknown reasons. Consistently " +
			  "passes locally.");
		  return;
	  }

	  RowManager rowManager = client.newRowManager();
	  PlanBuilder op = rowManager.newPlanBuilder();

	  ModifyPlan plan1 = op.fromSql(
			  // Had to adjust this for MarkLogic 12 to fully qualify the 'name' column in each 'ORDER BY'.
			  "SELECT opticFunctionalTest.detail.id, opticFunctionalTest.detail.name FROM opticFunctionalTest.detail " +
				  "ORDER BY opticFunctionalTest.detail.name "
				  + " UNION "
				  + " SELECT opticFunctionalTest.master.id, opticFunctionalTest.master.name FROM opticFunctionalTest.master " +
				  "ORDER BY opticFunctionalTest.master.name"
		  )
		  .orderBy(op.desc("id"))
		  .limit(3)
		  .offset(1)
		  .select(op.as("myName", op.col("name")));

	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  JsonNode result = rowManager.resultDoc(plan1, new JacksonHandle()).get();
	  assertNotNull(result, "result is unexpectedly null.");
	  JsonNode rows = result.path("rows");

	  assertEquals(2, rows.size());
	  assertEquals("Detail 5", rows.path(0).path("myName").path("value").asText());
	  assertEquals("Detail 4", rows.path(1).path("myName").path("value").asText());
  }


  //fromsql TEST 28 - union with qualifier and offsetLimit
  @Test
  public void testFromSqlUnionOffsetLimit() {
    System.out.println("In testFromSqlUnionOffsetLimit method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromSql(
            "SELECT opticFunctionalTest.detail.id, opticFunctionalTest.detail.name FROM opticFunctionalTest.detail ORDER BY name "
                    + " UNION "
                    + " SELECT opticFunctionalTest.master.id, opticFunctionalTest.master.name FROM opticFunctionalTest.master ORDER BY name",
            "myPlan"
    )
            .orderBy(p.desc("id"))
            .offsetLimit(1, 3);
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan1, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows");
    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "Detail 5", jsonResults.path(0).path("myPlan.name").path("value").asText());
    assertEquals( "Detail 4", jsonResults.path(1).path("myPlan.name").path("value").asText());
    assertEquals( "Detail 3", jsonResults.path(2).path("myPlan.name").path("value").asText());
  }

  // Tests for Query DSL

  /*
   * This test checks group by with a view. Should return 3 items. Tested
   * arrayAggregate, union, groupby methods Uses schemaCol for Columns selection
   *
   * Similar to testFromSqljoinInnerWithkeymatch
   */
  @Test
  public void testQueryDSLjoinInnerWithkeymatch() {
    System.out.println("In testQueryDSLjoinInnerWithkeymatch method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    StringBuilder strbldr = new StringBuilder();
    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");
    strbldr.append(".joinInner(");
    strbldr.append("op.fromView('opticFunctionalTest', 'master').orderBy(op.schemaCol('opticFunctionalTest', 'master' , 'id'))");
    strbldr.append(")");
    strbldr.append(".where(op.eq(op.schemaCol('opticFunctionalTest', 'master' , 'id'), op.schemaCol('opticFunctionalTest', 'detail', 'masterId')))");
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");
    String exeSQLStr = strbldr.toString();
    System.out.println(exeSQLStr);
    StringHandle strHdl = new StringHandle(exeSQLStr);

    RawQueryDSLPlan planDSL = rowMgr.newRawQueryDSLPlan(strHdl);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSL, jacksonHandle);

    JsonNode jsonResults = jacksonHandle.get().get("rows");
    System.out.println("Results are : " + jsonResults);

    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    assertEquals( "1", jsonResults.path(0).path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals( "1", jsonResults.path(0).path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals( "1", jsonResults.path(0).path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals( "Detail 1", jsonResults.path(0).path("opticFunctionalTest.detail.name").path("value").asText());
    assertEquals( "60.06", jsonResults.path(5).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "green", jsonResults.path(5).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "Master 2", jsonResults.path(5).path("opticFunctionalTest.master.name").path("value").asText());
  }

  /*
   * This test checks JS operators and types returned from query execution.
   * Types used are long, double, string and boolean.
   *
   */
  @Test
  public void testQueryDSLArithmeticOperators() {
    System.out.println("In testQueryDSLArithmeticOperators method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    StringBuilder strbldr = new StringBuilder();
    strbldr.append("op.fromView('opticFunctionalTest', 'detail')");

    strbldr.append(".select([");

    // Check for long data types (arithmetic)
    strbldr.append("op.as('addLong', op.schemaCol('opticFunctionalTest', 'detail', 'id') + 1), ");
    strbldr.append("op.as('subtractLong', op.schemaCol('opticFunctionalTest', 'detail', 'id') - 1), ");

    // Check for SJS String fns, chaining
    strbldr.append("op.as('nameJSLen', fn.stringLength(op.schemaCol('opticFunctionalTest', 'detail', 'name'))), ");
    strbldr.append("op.as('nameJSConcat', fn.upperCase(fn.replace(op.schemaCol('opticFunctionalTest', 'detail', 'name'), 'tail', 'Head'))), ");

    // Check for double data types (arithmetic)
    strbldr.append("op.as('add', op.schemaCol('opticFunctionalTest', 'detail', 'amount') + 1), ");
    strbldr.append("op.as('multiple', op.schemaCol('opticFunctionalTest', 'detail', 'amount') * 2), ");
    strbldr.append("op.as('subtract', op.schemaCol('opticFunctionalTest', 'detail', 'amount') - 1),");
    strbldr.append("op.as('divide', op.schemaCol('opticFunctionalTest', 'detail', 'amount') / 3), ");
    strbldr.append("op.as('equality', op.schemaCol('opticFunctionalTest', 'detail', 'amount') == op.schemaCol('opticFunctionalTest', 'detail', 'amount')), ");
    strbldr.append("op.as('inequality', op.schemaCol('opticFunctionalTest', 'detail', 'amount') != op.schemaCol('opticFunctionalTest', 'detail', 'amount')), ");
    // Verify literals and teritary op
    strbldr.append("op.as('numeralLiteral', 3 == 3 ? 4 : 6), ");
    strbldr.append("op.as('stringLiteral', 'abc' == 'abcd' ? 'MarkLogic' : 'Oracle') ");

    strbldr.append("])");
    strbldr.append(".orderBy('addLong');");

    String exeSQLStr = strbldr.toString();
    System.out.println(exeSQLStr);
    StringHandle strHdl = new StringHandle(exeSQLStr);

    RawQueryDSLPlan planDSL = rowMgr.newRawQueryDSLPlan(strHdl);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSL, jacksonHandle);

    JsonNode jsonResults = jacksonHandle.get().get("rows");
    System.out.println("Results are : " + jsonResults);

    // Should have 6 nodes returned.
    assertEquals( 6, jsonResults.size());
    // Verify values for row 1
    assertEquals( "2", jsonResults.path(0).path("addLong").path("value").asText());
    assertEquals( "0", jsonResults.path(0).path("subtractLong").path("value").asText());
    assertEquals( "8", jsonResults.path(0).path("nameJSLen").path("value").asText());
    assertEquals( "DEHEAD 1", jsonResults.path(0).path("nameJSConcat").path("value").asText());
    assertEquals( "11.01", jsonResults.path(0).path("add").path("value").asText());
    assertEquals( "20.02", jsonResults.path(0).path("multiple").path("value").asText());
    assertEquals( "9.01", jsonResults.path(0).path("subtract").path("value").asText());
    assertEquals( "3.33666666666667", jsonResults.path(0).path("divide").path("value").asText());
    assertEquals( "true", jsonResults.path(0).path("equality").path("value").asText());
    assertEquals( "false", jsonResults.path(0).path("inequality").path("value").asText());

    assertEquals( "4", jsonResults.path(0).path("numeralLiteral").path("value").asText());
    assertEquals( "Oracle", jsonResults.path(0).path("stringLiteral").path("value").asText());

    // Verify data types for row 6
    assertEquals( "xs:integer", jsonResults.path(5).path("addLong").path("type").asText());
    assertEquals( "xs:integer", jsonResults.path(5).path("subtractLong").path("type").asText());
    assertEquals( "xs:integer", jsonResults.path(5).path("nameJSLen").path("type").asText());
    assertEquals( "xs:string", jsonResults.path(5).path("nameJSConcat").path("type").asText());
    assertEquals( "xs:double", jsonResults.path(5).path("add").path("type").asText());
    assertEquals( "xs:double", jsonResults.path(5).path("multiple").path("type").asText());
    assertEquals( "xs:double", jsonResults.path(5).path("subtract").path("type").asText());
    assertEquals( "xs:double", jsonResults.path(5).path("divide").path("type").asText());
    assertEquals( "xs:boolean", jsonResults.path(5).path("equality").path("type").asText());
    assertEquals( "xs:boolean", jsonResults.path(5).path("inequality").path("type").asText());

    assertEquals( "xs:integer", jsonResults.path(0).path("numeralLiteral").path("type").asText());
    assertEquals( "xs:string", jsonResults.path(0).path("stringLiteral").path("type").asText());
  }

  /*
   * This test checks dateTime types with fromSQL in DSL format
   */
  @Test
  public void testQueryDSLFromViewDateType() {
    System.out.println("In testQueryDSLFromViewDateType method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    StringBuilder strbldr = new StringBuilder();
    StringBuilder strbldrWhereValidDate = new StringBuilder();
    StringBuilder strbldrWhereInValidDate = new StringBuilder();

    String exeSQLStr = "op.fromSQL(`SELECT opticFunctionalTest.master.name AS MasterName, opticFunctionalTest.master.date, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.amount,  opticFunctionalTest.detail.color" +
          " FROM opticFunctionalTest.detail"  +
          " INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId" +
          " WHERE opticFunctionalTest.master.date = '2015-12-01'" +
          " and opticFunctionalTest.detail.color = 'blue'" +
          " ORDER BY DetailName DESC`);";

    StringHandle strHdl = new StringHandle(exeSQLStr);
    System.out.println(exeSQLStr);

    RawQueryDSLPlan planDSL = rowMgr.newRawQueryDSLPlan(strHdl);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSL, jacksonHandle);

    JsonNode jsonResults = jacksonHandle.get().get("rows");
    System.out.println("Results are : " + jsonResults);
    // Should have 2 nodes returned.
    assertEquals( 2, jsonResults.size());
    assertEquals( "Master 1", jsonResults.path(0).path("MasterName").path("value").asText());
    assertEquals( "2015-12-01", jsonResults.path(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "blue", jsonResults.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "Master 1", jsonResults.path(1).path("MasterName").path("value").asText());
    assertEquals( "2015-12-01", jsonResults.path(1).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "blue", jsonResults.path(1).path("opticFunctionalTest.detail.color").path("value").asText());

    strbldrWhereValidDate.append(".where(op.and(op.gt(op.schemaCol('opticFunctionalTest', 'master', 'date'), xs.date('2015-12-01'))," +
            " op.eq(op.schemaCol('opticFunctionalTest', 'detail', 'color'), 'blue')))");

    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");
    strbldr.append(".joinInner(");
    strbldr.append("op.fromView('opticFunctionalTest', 'master').orderBy(op.schemaCol('opticFunctionalTest', 'master', 'id'))");
    strbldr.append(")");
    strbldr.append(strbldrWhereValidDate.toString());
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");

    String exeSQLStrValDate = strbldr.toString() ;

    strHdl = new StringHandle(exeSQLStrValDate);
    System.out.println("Valid Date SQL is " +  exeSQLStrValDate);

    RawQueryDSLPlan planDSLFrmVw = rowMgr.newRawQueryDSLPlan(strHdl);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSLFrmVw, jacksonHandle);
    jsonResults = jacksonHandle.get().get("rows");
    System.out.println("Results from Valid date in View are : " + jsonResults);

    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "10.01", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "2015-12-02", jsonResults.path(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "blue", jsonResults.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "20.02", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "30.03", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "Detail 3", jsonResults.path(2).path("opticFunctionalTest.detail.name").path("value").asText());

    // Invalid date - XDMP-CAST Exception
    strbldr = new StringBuilder();
    strbldrWhereInValidDate.append(".where( op.ge(op.schemaCol('opticFunctionalTest', 'master' , 'date'), xs.date('2-12-2015')))");

    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");
    strbldr.append(".joinInner(");
    strbldr.append("op.fromView('opticFunctionalTest', 'master').orderBy(op.schemaCol('opticFunctionalTest', 'master' , 'id'))");
    strbldr.append(")");
    strbldr.append(strbldrWhereInValidDate.toString());
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");

    String exeSQLStrInValDate = strbldr.toString() ;

    strHdl = new StringHandle(exeSQLStrInValDate);
    System.out.println("Invalid Date SQL is " + exeSQLStrInValDate);

    RawQueryDSLPlan planDSLFrmVwInvDate = rowMgr.newRawQueryDSLPlan(strHdl);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    try {
      rowMgr.resultDoc(planDSLFrmVwInvDate, jacksonHandle);
    }
    catch (Exception ex) {
      assert(ex.toString().contains("XDMP-CAST"));
    }

  }

  /*
   * This test checks group by, order by with fromSQL in DSL format
   * Similar to join inner with keymatch of fromSQL SJS test
   */
  @Test
  public void testQueryDSLFromSQL() {
    System.out.println("In testQueryDSLFromSQL method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    String strbldr = "op.fromSQL('SELECT opticFunctionalTest.master.name, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.color, SUM(amount) AS DetailSum " +
            "          FROM opticFunctionalTest.detail " +
            "          INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId " +
            "          GROUP BY opticFunctionalTest.master.name " +
            "          ORDER BY DetailSum DESC');";

    System.out.println(strbldr);
    StringHandle strHdl = new StringHandle(strbldr);

    RawQueryDSLPlan planDSL = rowMgr.newRawQueryDSLPlan(strHdl);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSL, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows");
    System.out.println("Results are : " + jsonResults);

    // Should have 2 nodes returned.
    assertEquals( 2, jsonResults.size());
    assertEquals( "Master 2", jsonResults.path(0).path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "120.12", jsonResults.path(0).path("DetailSum").path("value").asText());
    assertEquals( "Master 1", jsonResults.path(1).path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals( "90.09", jsonResults.path(1).path("DetailSum").path("value").asText());
  }

  /*
   * This test checks binding params.
   * Types used are long, double, string and boolean.
   *
   */
  @Test
  public void testQueryDSLBindParams() {
    System.out.println("In testQueryDSLBindParams method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    StringBuilder strbldr = new StringBuilder();

    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");
    strbldr.append(".joinInner(");
    strbldr.append("op.fromView('opticFunctionalTest', 'master').orderBy(op.schemaCol('opticFunctionalTest', 'master', 'id'))");
    strbldr.append(")");
    strbldr.append(".where(op.and(op.gt(op.schemaCol('opticFunctionalTest', 'master', 'date'), xs.date(op.param('dateParam')) )," +
            " op.eq(op.schemaCol('opticFunctionalTest', 'detail', 'color'), op.param('colorParam'))))");
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");
    // op.param('dateParam')
    String exeSQLStr = strbldr.toString();

    System.out.println(exeSQLStr);
    StringHandle strHdl = new StringHandle(exeSQLStr);

    PlanBuilder.Plan planDSL = rowMgr.newRawQueryDSLPlan(strHdl)
            .bindParam("dateParam", "2015-12-01").bindParam("colorParam", "blue");

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(planDSL, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows");
    System.out.println("Results are : " + jsonResults);

    // Should have 3 nodes returned.
    assertEquals( 3, jsonResults.size());
    assertEquals( "10.01", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "2015-12-02", jsonResults.path(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals( "blue", jsonResults.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals( "20.02", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "30.03", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals( "Detail 3", jsonResults.path(2).path("opticFunctionalTest.detail.name").path("value").asText());

    // Invalid value in bind
    PlanBuilder.Plan planDSL1 = rowMgr.newRawQueryDSLPlan(strHdl)
            .bindParam("dateParam", "2015-00-01").bindParam("colorParam", "blue");

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder castEx = new StringBuilder();
    try {
      rowMgr.resultDoc(planDSL1, jacksonHandle);
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
      castEx.append(ex.getMessage());
    }

    // ""null"" value in bind
    String clrVal = "";
    PlanBuilder.Plan planDSL2 = rowMgr.newRawQueryDSLPlan(strHdl)
            .bindParam("dateParam", "2015-12-01").bindParam("colorParam", clrVal);

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    String res = null;

      rowMgr.resultDoc(planDSL2, jacksonHandle);
      res = jacksonHandle.toString();

      System.out.println("Result handle is null when bind value is empty");
      System.out.println(res);

      assertTrue(castEx.toString().contains("Invalid cast: \"2015-00-01\" cast as xs:date"));
      assertNull(res);
  }

  /*
   * This test checks that JavaScript fns are not allowed
   *
   */
  @Test
  public void testQueryDSLJavaScriptFn() {
    System.out.println("In testQueryDSLJavaScriptFn method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    StringBuilder strbldr = new StringBuilder();

    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");

    strbldr.append(".where(op.and(op.gt(op.schemaCol('opticFunctionalTest', 'detail', 'date'), xs.date(op.param('dateParam')) )," +
            " op.eq(op.schemaCol('opticFunctionalTest', 'detail', 'color'), 'blue'.toUpperCase())))");
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");
    // op.param('dateParam')
    String exeSQLStr = strbldr.toString();

    System.out.println(exeSQLStr);
    StringHandle strHdl = new StringHandle(exeSQLStr);

    PlanBuilder.Plan planDSL = rowMgr.newRawQueryDSLPlan(strHdl)
            .bindParam("dateParam", "2015-12-01").bindParam("colorParam", "blue");

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder jsStrEx = new StringBuilder();
    try {
      rowMgr.resultDoc(planDSL, jacksonHandle);
    }
    catch (Exception ex) {
      jsStrEx.append(ex.getMessage());
      System.out.println(jsStrEx.toString());
    }
    assertTrue(jsStrEx.toString().contains("cannot build chained call for function \"toUpperCase\""));

    // Namespace check
    strbldr = new StringBuilder();
    strbldr.append("op.fromView('opticFunctionalTest', 'detail').orderBy(op.schemaCol('opticFunctionalTest', 'detail', 'id'))");

    strbldr.append(".where(op.and(op.gt(op.schemaCol('opticFunctionalTest', 'detail', 'date'), xdmp.restart() )," +
            " op.eq(op.schemaCol('opticFunctionalTest', 'detail', 'color'), 'blue'.toUpperCase())))");
    strbldr.append(".orderBy(op.asc(op.schemaCol('opticFunctionalTest', 'detail', 'id')));");
    // op.param('dateParam')
    String exeSQLStrNs = strbldr.toString();

    System.out.println(exeSQLStrNs);
    strHdl = new StringHandle(exeSQLStrNs);

    PlanBuilder.Plan planDSLNs = rowMgr.newRawQueryDSLPlan(strHdl)
            .bindParam("dateParam", "2015-12-01").bindParam("colorParam", "blue");

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder jsStrNsEx = new StringBuilder();
    try {
      rowMgr.resultDoc(planDSLNs, jacksonHandle);
    }
    catch (Exception ex) {
      jsStrNsEx.append(ex.getMessage());
      System.out.println(jsStrNsEx.toString());
    }
    assertTrue(jsStrNsEx.toString().contains("cannot build call for unknown variable or namespace \"xdmp\" with function \"restart\""));
  }

  // Similar to testgroupBy
  @Test
  public void testgroupByUnion() {
    System.out.println("In testgroupByUnion method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.union(plan2)
            .select(
                    p.col("amount"),
                    p.col("color")
            )
            .groupByUnion(p.groupSeq(p.col("color"), p.col("amount")),
                    p.aggregateSeq(
                            p.count("ColorCount", "color"),
                            p.avg("AverageAmout", "amount")
                    )
            )
            .orderBy(p.desc(p.col("ColorCount")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    System.out.println("Results are : " + jsonBindingsNodes);
    // Should have 10 array nodes returned.
    assertEquals( 10, jsonBindingsNodes.size());
    assertEquals( "blue", jsonBindingsNodes.get(0).path("color").path("value").asText());
    assertEquals( 3, jsonBindingsNodes.get(0).path("ColorCount").path("value").asInt());
    assertEquals( "20.02", jsonBindingsNodes.get(0).path("AverageAmout").path("value").asText());

    assertEquals( "green", jsonBindingsNodes.get(1).path("color").path("value").asText());
    assertEquals( 3, jsonBindingsNodes.get(1).path("ColorCount").path("value").asInt());
    assertEquals( "50.05", jsonBindingsNodes.get(1).path("AverageAmout").path("value").asText());
    // Assert for null in both grouping columns. We will have one row with nulls

    assertEquals( "null", jsonBindingsNodes.get(9).path("color").path("type").asText());
    assertEquals( 0, jsonBindingsNodes.get(9).path("ColorCount").path("value").asInt());
    assertEquals( "null", jsonBindingsNodes.get(9).path("AverageAmout").path("type").asText());
    // Use date in aggregate
    ModifyPlan plan4 = plan1.union(plan2)
            .select(p.as("MasterDate", p.schemaCol("opticFunctionalTest", "master", "date")),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .groupByUnion(p.groupSeq(p.col("color"), p.col("MasterDate")),
                    p.aggregateSeq(p.count("DateCount", "MasterDate")))
            .orderBy(p.desc(p.col("MasterDate")));

    JacksonHandle jacksonHandleDate = new JacksonHandle();
    jacksonHandleDate.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleDate);
    JsonNode jsonResultsDate = jacksonHandleDate.get();
    JsonNode jsonBindingsNodesDate = jsonResultsDate.path("rows");
    System.out.println("Results from date grouping are : " + jsonBindingsNodesDate);

    assertEquals( 6, jsonBindingsNodesDate.size());
    assertEquals( "2015-12-02", jsonBindingsNodesDate.get(0).path("MasterDate").path("value").asText());
    assertEquals( 1, jsonBindingsNodesDate.get(0).path("DateCount").path("value").asInt());
    assertEquals( "null", jsonBindingsNodesDate.get(0).path("color").path("type").asText());

    assertEquals( "2015-12-01", jsonBindingsNodesDate.get(1).path("MasterDate").path("value").asText());
    assertEquals( 1, jsonBindingsNodesDate.get(1).path("DateCount").path("value").asInt());
    assertEquals( "null", jsonBindingsNodesDate.get(1).path("color").path("type").asText());

    // Note that this plan5 is ued to verify if non-alphabetic col key name have any issues. Refer to BT56490
    ModifyPlan plan5 = plan1.union(plan2)
            .select(p.as("MasterDate", p.schemaCol("opticFunctionalTest", "master", "date")),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.as("DetailId", p.schemaCol("opticFunctionalTest", "detail", "masterId")),
                    p.col("amount"),
                    p.col("color")
            )
            .groupByUnion(p.groupSeq(p.col("DetailId"), p.col("MasterDate"), p.col("color")),
                    p.aggregateSeq(p.count("DateCount", "MasterDate")))
            .orderBy(p.desc(p.col("MasterDate")));

    JacksonHandle jacksonHandleColOrder = new JacksonHandle();
    jacksonHandleColOrder.setMimetype("application/json");

    rowMgr.resultDoc(plan5, jacksonHandleColOrder);
    JsonNode jsonResultsColOrd = jacksonHandleColOrder.get();
    JsonNode jsonBindingsNodesColOrd = jsonResultsColOrd.path("rows");
    System.out.println("Results from Column in non alphbetic column name order are : " + jsonBindingsNodesColOrd);

    assertEquals( 9, jsonBindingsNodesColOrd.size());
    assertEquals( "2015-12-02", jsonBindingsNodesColOrd.get(0).path("MasterDate").path("value").asText());
    assertEquals( 1, jsonBindingsNodesColOrd.get(0).path("DateCount").path("value").asInt());
    assertEquals( "null", jsonBindingsNodesColOrd.get(7).path("color").path("type").asText());
    assertEquals( "blue", jsonBindingsNodesColOrd.get(6).path("color").path("value").asText());

  }

  // Same as testgroupsByUnion with facade.
  @Test
  public void testgroupByArrays() {
    System.out.println("In testgroupByArrays method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan detailPlan = p.fromView("opticFunctionalTest", "detail");
    ModifyPlan masterPlan = p.fromView("opticFunctionalTest", "master");

    ModifyPlan plan3 = detailPlan
        .joinInner(masterPlan)
        .where(p.eq(
            p.schemaCol("opticFunctionalTest", "master", "id"),
            p.schemaCol("opticFunctionalTest", "detail", "masterId")
        ))
        .groupToArrays(
            p.namedGroupSeq(
                p.namedGroup("DetColor", p.col("color")),
                p.namedGroup("Amt", p.schemaCol("opticFunctionalTest", "detail", "amount"))
            ),
            p.aggregateSeq(p.sum("sum", "amount"), p.count("CountofColors", "color"))
        );

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    System.out.println("Results are : " + jsonBindingsNodes);
    // Should be returning as one array containing sub arrays for each namedgroup
    assertEquals( 1, jsonBindingsNodes.size());

    JsonNode jsonDetColorNodes = jsonBindingsNodes.get(0).get("DetColor").get("value");
    assertEquals( 2, jsonDetColorNodes.size());

    // Order of the aggregated rows is not consistent
    JsonNode firstRow = jsonDetColorNodes.get(0);
    JsonNode secondRow = jsonDetColorNodes.get(1);
    boolean firstRowIsBlue = firstRow.get("color").asText().equals("blue");
    JsonNode blueRow =  firstRowIsBlue ? firstRow : secondRow;
    JsonNode greenRow = firstRowIsBlue ? secondRow : firstRow;

    assertEquals( "blue", blueRow.path("color").asText());
    assertEquals( "60.06", blueRow.path("sum").asText());
    assertEquals( "3", blueRow.path("CountofColors").asText());

    assertEquals( "green", greenRow.path("color").asText());
    assertEquals( "150.15", greenRow.path("sum").asText());
    assertEquals( "3", greenRow.path("CountofColors").asText());

    JsonNode jsonAmtNodes = jsonBindingsNodes.get(0).get("Amt").get("value");
    assertEquals( 6, jsonAmtNodes.size());

    // Verify without aggregate param
    ModifyPlan plan4 =
            detailPlan.joinInner(masterPlan)
                    .where(
                            p.eq(
                                    p.schemaCol("opticFunctionalTest", "master" , "id"),
                                    p.schemaCol("opticFunctionalTest", "detail", "masterId")
                            )
                    )
                    .groupToArrays(p.namedGroupSeq(
                            p.namedGroup("DetColor", p.col("color")),
                            p.namedGroup("Amt", p.schemaCol("opticFunctionalTest", "detail" , "amount"))
                            )
                    );

    JacksonHandle jacksonHandleNoAggCol = new JacksonHandle();
    jacksonHandleNoAggCol.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleNoAggCol);
    JsonNode jsonResultsNoAgg = jacksonHandleNoAggCol.get();
    JsonNode jsonBindingsNodesNoAgg = jsonResultsNoAgg.path("rows");
    System.out.println("Results are : " + jsonBindingsNodesNoAgg);

  }

  @Test
  public void testFacetByWithBindParams() {
    System.out.println("In testFacetByWithBindParams method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    PlanParamExpr idParam = p.param("ID");
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"), idParam)
            )
            .facetBy(p.colSeq("color")
            );
            //.orderBy(p.asc(p.col("color")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3.bindParam("ID", 1), jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get().path("rows").get(0).path("group0").path("value");

    JsonNode jsonBindingsNodes1 = jsonResults.get(0);
    System.out.println("First Results are : " + jsonBindingsNodes1);
    assertEquals( 3, jsonBindingsNodes1.get("count").asInt());
    assertEquals( "blue", jsonBindingsNodes1.get("color").asText());

    // Verify next facet value
    JsonNode jsonBindingsNodes2 = jsonResults.get(1);
    System.out.println("Second Results are : " + jsonBindingsNodes2);
    assertEquals( 3, jsonBindingsNodes2.get("count").asInt());
    assertEquals( "green", jsonBindingsNodes2.get("color").asText());

    // Verify two parameter facetBy method
    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");
    ModifyPlan plan11 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
            .orderBy(p.col("id"));
    ModifyPlan plan21 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan11.joinInner(plan21).where(
            p.eq(
                    p.schemaCol("opticFunctionalTest", "master", "id"),
                    p.col("masterId")
            )
    )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color"),
                    fIdCol1,
                    fIdCol2
            ).facetBy(p.colSeq("color", "amount"), "DetailName");
            //.orderBy(p.desc(p.col("DetailName")));
    JacksonHandle jacksonHandleFrg = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandleFrg);
    JsonNode jsonResultsFrag = jacksonHandleFrg.get();
    //System.out.println("Results are " + jsonResultsFrag);

    JsonNode jsonColumnsResults = jsonResultsFrag.get("columns");
    assertEquals( "group0", jsonColumnsResults.get(0).get("name").asText());
    assertEquals( "group1", jsonColumnsResults.get(1).get("name").asText());
  }

  // Testing columnInfo - similar to testgroupBy

  @Test
  public void testColumnInfo() {
    System.out.println("In testColumnInfo method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.union(plan2)
            .select(p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color")
            )
            .orderBy(p.desc(p.col("MasterName")));

    assertColumnInfosExist(rowMgr.columnInfoAs(plan3, String.class),
        new ColumnInfo("MasterName", "string").withNullable(true),
        new ColumnInfo("opticFunctionalTest", "master", "date", "date").withNullable(true),
        new ColumnInfo("DetailName", "string").withNullable(true),
        new ColumnInfo("opticFunctionalTest", "detail", "amount", "double").withNullable(true),
        new ColumnInfo("opticFunctionalTest", "detail", "color", "string").withNullable(true)
    );
  }

  // Test for fragment Id types. Similar to testExplainPlan
  @Test
  public void testColInfoWithSysCols() {
    System.out.println("In testColInfoWithSysCols method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
            .orderBy(p.col("id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan1.joinInner(plan2).where(
                    p.eq(
                            p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.col("masterId")
                    )
            )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.col("amount"),
                    p.col("color"),
                    fIdCol1,
                    fIdCol2
            )
            .orderBy(p.desc(p.col("DetailName")));

    final String expectedFragmentValue = isML11OrHigher ? "fragmentId": "fraghint";

    assertColumnInfosExist(rowMgr.columnInfoAs(output, String.class),
        new ColumnInfo("MasterName", "string"),
        new ColumnInfo("opticFunctionalTest", "master", "date", "date"),
        new ColumnInfo("DetailName", "string"),
        new ColumnInfo("opticFunctionalTest", "detail", "amount", "double"),
        new ColumnInfo("opticFunctionalTest", "detail", "color", "string"),
		// Per DBQ-123, since the fragment IDs are being selected, hidden should now be false
        new ColumnInfo("opticFunctionalTest", "detail", "fragIdCol1", expectedFragmentValue, false),
        new ColumnInfo("opticFunctionalTest", "master", "fragIdCol2", expectedFragmentValue, false)
    );
  }
}
