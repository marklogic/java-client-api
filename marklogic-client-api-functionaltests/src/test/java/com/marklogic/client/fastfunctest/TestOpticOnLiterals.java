/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.PlanAggregateColSeq;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprColSeq;
import com.marklogic.client.type.PlanValueOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestOpticOnLiterals extends AbstractFunctionalTest {

  private static Map<String, Object>[] literals1 = new HashMap[5];
  private static Map<String, Object>[] literals2 = new HashMap[4];
  private static Map<String, Object>[] storeInformation = new HashMap[4];
  private static Map<String, Object>[] internetSales = new HashMap[4];

  @BeforeAll
  public static void setUp() throws Exception
  {
    // Install the TDE templates
    // loadFileToDB(client, filename, docURI, collection, document format)
    loadFileToDB(schemasClient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemasClient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemasClient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemasClient, "masterDetail4.tdej", "/optic/view/test/masterDetail4.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });

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
    loadFileToDB(client, "masterDetail4.json", "/optic/view/test/masterDetail4.json", "JSON", new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail5.json", "/optic/view/test/masterDetail5.json", "JSON", new String[] { "/optic/view/test" });

    loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[] { "/other/coll1", "/other/coll2" });
    loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[] { "/optic/lexicon/test" });

    loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[] { "/optic/lexicon/test" });

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

  /*
   * 1) Test join inner 2) Test join inner with qualifier 3) Test join and
   * multiple order by 4) Test multple inner joins
   */
  @Test
  public void testJoinInner() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testJoinInner method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);
    ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned.
    assertEquals(4, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "1", node.path("rowId").path("value").asText());
    assertEquals( "ball", node.path("desc").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "4", node.path("rowId").path("value").asText());
    assertEquals( "hoop", node.path("desc").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());

    // Join inner with qualifier
    ModifyPlan plan3 = p.fromLiterals(literals1, "table1");
    ModifyPlan plan4 = p.fromLiterals(literals2, "table2");
    ModifyPlan outputQualifier = plan3.joinInner(plan4)
        .where(p.eq(p.viewCol("table1", "colorId"), p.viewCol("table2", "colorId")))
        .orderBy(p.desc(p.viewCol("table1", "rowId")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputQualifier, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    node = jsonBindingsNodes.path(3);
    assertEquals( "1", node.path("table1.rowId").path("value").asText());
    assertEquals( "ball", node.path("table1.desc").path("value").asText());
    assertEquals( "red", node.path("table2.colorDesc").path("value").asText());
    node = jsonBindingsNodes.path(0);
    assertEquals( "4", node.path("table1.rowId").path("value").asText());
    assertEquals( "hoop", node.path("table1.desc").path("value").asText());
    assertEquals( "red", node.path("table2.colorDesc").path("value").asText());

    // Test join and multiple order by
    ModifyPlan plan5 = p.fromLiterals(literals1, "myItem");
    ModifyPlan plan6 = p.fromLiterals(literals2, "myColor");

    PlanColumn descCol = p.viewCol("myItem", "desc");

    PlanColumn itemColorIdCol = p.viewCol("myItem", "colorId");
    PlanColumn colorIdCol = p.viewCol("myColor", "colorId");
    ModifyPlan outputMultiOrder = plan5.joinInner(plan6, p.on(itemColorIdCol, colorIdCol))
        .select(descCol, colorIdCol)
        .orderBy(p.sortKeySeq(colorIdCol, descCol));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputMultiOrder, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    node = jsonBindingsNodes.path(0);
    assertEquals( "ball", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "box", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    node = jsonBindingsNodes.path(2);
    assertEquals( "hoop", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "square", node.path("myItem.desc").path("value").asText());
    assertEquals( "2", node.path("myColor.colorId").path("value").asText());

    // Test multple inner joins
    Map<String, Object>[] literals3 = new HashMap[4];
    Map<String, Object> row = new HashMap<>();

    row = new HashMap<>();
    row.put("color", "red");
    row.put("ref", "rose");
    literals3[0] = row;
    row = new HashMap<>();
    row.put("color", "blue");
    row.put("ref", "water");
    literals3[1] = row;
    row = new HashMap<>();
    row.put("color", "black");
    row.put("ref", "bag");
    literals3[2] = row;
    row = new HashMap<>();
    row.put("color", "yellow");
    row.put("ref", "moon");
    literals3[3] = row;

    ModifyPlan plan7 = p.fromLiterals(literals3, "myRef");

    PlanColumn colorDescCol = p.viewCol("myColor", "colorDesc");
    PlanColumn refColorCol = p.viewCol("myRef", "color");
    PlanColumn refCol = p.viewCol("myRef", "ref");

    ModifyPlan outputMultiJoin = plan5.joinInner(plan6, p.on(itemColorIdCol, colorIdCol))
        .joinInner(plan7, p.on(colorDescCol, refColorCol))
        .select(descCol, colorIdCol, refCol)
        .orderBy(p.sortKeySeq(colorIdCol, descCol));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputMultiJoin, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    node = jsonBindingsNodes.path(0);
    assertEquals( "ball", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    assertEquals( "rose", node.path("myRef.ref").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "box", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    assertEquals( "rose", node.path("myRef.ref").path("value").asText());
    node = jsonBindingsNodes.path(2);
    assertEquals( "hoop", node.path("myItem.desc").path("value").asText());
    assertEquals( "1", node.path("myColor.colorId").path("value").asText());
    assertEquals( "rose", node.path("myRef.ref").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "square", node.path("myItem.desc").path("value").asText());
    assertEquals( "2", node.path("myColor.colorId").path("value").asText());
    assertEquals( "water", node.path("myRef.ref").path("value").asText());
  }

  /*
   * Test join left outer
   */
  @Test
  public void testJoinLeftOuter() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testJoinLeftOuter method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinLeftOuter(plan2)
        .orderBy(p.col("rowId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 5 nodes returned.
    assertEquals( 5, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "1", node.path("rowId").path("value").asText());
    assertEquals( "ball", node.path("desc").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());
    node = jsonBindingsNodes.path(3);
    assertEquals( "4", node.path("rowId").path("value").asText());
    assertEquals( "hoop", node.path("desc").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());
    node = jsonBindingsNodes.path(4);
    assertEquals( "5", node.path("rowId").path("value").asText());
    assertEquals( "circle", node.path("desc").path("value").asText());
    assertFalse(node.path("colorDesc").isNull());
    // To verify issue 1055.
    FileHandle fh = new FileHandle();
    rowMgr.resultDoc(output, fh);
    File file = fh.get();
    String fileContents = convertFileToString(file);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode fromFile = mapper.readTree(file).path("rows");
    System.out.println(fileContents);

    assertEquals( 5, fromFile.size());
    JsonNode nodeFile = fromFile.path(0);
    assertEquals( "1", nodeFile.path("rowId").path("value").asText());
    assertEquals( "ball", nodeFile.path("desc").path("value").asText());
    assertEquals( "red", nodeFile.path("colorDesc").path("value").asText());
  }

  /*
   * Test group-by max
   */
  @Test
  public void testGroupByMax() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testGroupByMax method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.groupBy(p.col("colorId"), p.max("new_color_id", "colorId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "1", node.path("colorId").path("value").asText());
    assertEquals("1", node.path("new_color_id").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "2", node.path("colorId").path("value").asText());
    assertEquals("2", node.path("new_color_id").path("value").asText());
    node = jsonBindingsNodes.path(2);
    assertEquals( "5", node.path("colorId").path("value").asText());
    assertEquals("5", node.path("new_color_id").path("value").asText());
  }

  /*
   * Test where limit and select Test limit with 0 length
   */
  @Test
  public void testOffsetAndLimit() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testOffsetAndLimit method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
        .offsetLimit(1, 3)
        .select(p.colSeq("rowId", "desc", "colorId", "colorDesc"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "3", node.path("rowId").path("value").asText());
    assertEquals( "box", node.path("desc").path("value").asText());
    assertEquals( "1", node.path("colorId").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "4", node.path("rowId").path("value").asText());
    assertEquals( "hoop", node.path("desc").path("value").asText());
    assertEquals( "1", node.path("colorId").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());

    // limit with 0 length
    ModifyPlan outputLimit = plan1.joinInner(plan2)
        .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
        .limit(0)
        .select(p.colSeq("rowId", "desc", "colorId", "colorDesc"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(outputLimit, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have OPTIC-INVALARGS exceptions.
    assertTrue( str.toString().contains("OPTIC-INVALARGS"));
    assertTrue( str.toString().contains("Invalid arguments: limit must be a positive number: 0"));
  }

  /*
   * Test arithmetic expression
   */
  @Test
  public void testArithmeticExpression() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testArithmeticExpression method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output =
        plan1.joinInner(plan2)
            .select(
                p.col("rowId"),
                p.col("desc"),
                p.col("colorId"),
                p.col("colorDesc"),
                p.as("added", p.add(p.col("rowId"), p.col("colorId"), p.xs.intVal(10))),
                p.as("subtracted", p.subtract(p.col("colorId"), p.col("rowId"))),
                p.as("negSubtracted", p.subtract(p.col("colorId"), p.col("rowId"))),
                p.as("divided", p.divide(p.col("colorId"), p.col("rowId"))),
                p.as("multiplied", p.multiply(p.col("colorId"), p.col("rowId"), p.xs.floatVal(0.6f))),
                p.as("colDefined", p.isDefined(p.col("added"))),
                p.as("colNotDefined", p.isDefined(p.col("negSubtracted"))),
                p.as("colNotDefinedNegate", p.not(p.isDefined(p.col("negSubtracted")))),
                p.as("caseExpr",
                    p.caseExpr(
                        p.when(p.eq(p.col("rowId"), p.xs.intVal(1)), p.xs.string("foo")),
                        p.when(p.eq(p.col("rowId"), p.xs.intVal(2)), p.xs.string("baz")),
                        p.when(p.eq(p.col("rowId"), p.xs.intVal(3)), p.xs.string("rat")),
                        p.elseExpr(p.xs.string("bar"))
                        )
                    )
            )
            .orderBy(p.sortKeySeq(p.col("rowId")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);

    assertEquals( "1", node.path("rowId").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());

    assertEquals( "12", node.path("added").path("value").asText());
    assertEquals( "0", node.path("subtracted").path("value").asText());
    assertEquals( "1", node.path("divided").path("value").asText());
    assertEquals( "0.600000023841858", node.path("multiplied").path("value").asText());
    assertEquals( "foo", node.path("caseExpr").path("value").asText());

    node = jsonBindingsNodes.path(1);
    assertEquals( "baz", node.path("caseExpr").path("value").asText());

    node = jsonBindingsNodes.path(2);
    assertEquals( "rat", node.path("caseExpr").path("value").asText());

    node = jsonBindingsNodes.path(3);
    assertEquals( "4", node.path("rowId").path("value").asText());
    assertEquals( "red", node.path("colorDesc").path("value").asText());

    assertEquals( "15", node.path("added").path("value").asText());
    assertEquals( "-3", node.path("subtracted").path("value").asText());
    assertEquals( "0.25", node.path("divided").path("value").asText());
    assertEquals( "2.400000095367432", node.path("multiplied").path("value").asText());
    assertEquals( "bar", node.path("caseExpr").path("value").asText());
  }

  /*
   * Test join inner doc on json and xml documents
   */
  @Test
  public void testJoinInnerDocOnJson()
  {
    System.out.println("In testJoinInnerDocOnJson method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, Object>[] literals3 = new HashMap[4];
    Map<String, Object> row = new HashMap<>();
    row.put("id", 1);
    row.put("val", 2);
    row.put("uri", "/optic/lexicon/test/doc1.json");
    literals3[0] = row;

    row = new HashMap<>();
    row.put("id", 2);
    row.put("val", 4);
    row.put("uri", "/optic/test/not/a/real/doc.nada");
    literals3[1] = row;

    row = new HashMap<>();
    row.put("id", 3);
    row.put("val", 6);
    row.put("uri", "/optic/lexicon/test/doc3.json");
    literals3[2] = row;

    row = new HashMap<>();
    row.put("id", 4);
    row.put("val", 8);
    row.put("uri", "/optic/lexicon/test/doc4.xml");
    literals3[3] = row;

    // TEST 16 - join inner doc with xpath traversing down and multiple values
    // on json
    ModifyPlan output = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.xpath("doc", "/location/latLonPair/(lat|long)")))
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.asc("id"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 2 nodes returned.
    assertEquals( 2, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( "1", node.path("id").path("value").asText());
    assertEquals( "2", node.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc1.json", node.path("uri").path("value").asText());
    node = jsonBindingsNodes.path(1);
    assertEquals( "3", node.path("id").path("value").asText());
    assertEquals( "6", node.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc3.json", node.path("uri").path("value").asText());

    // Verify TEST 17 - join inner doc with xpath accessing attribute on xml
    ModifyPlan output17 = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.xpath("doc", "/doc/distance/@direction")))
        .where(p.isDefined(p.col("nodes")))
		.orderBy(p.asc("id"));

    System.out.println(output17.exportAs(JsonNode.class).toPrettyString());

    JacksonHandle jacksonHandle17 = new JacksonHandle();
    jacksonHandle17.setMimetype("application/json");
    rowMgr.resultDoc(output17, jacksonHandle17);
    JsonNode jsonBindingsNodes17 = jacksonHandle17.get().path("rows");

    assertEquals(
        1, jsonBindingsNodes17.size(),
		"One node not returned from testJoinInnerDocOnJson method: " + jsonBindingsNodes17.toPrettyString());

    JsonNode node17 = jsonBindingsNodes17.path(0);
    assertEquals( "4", node17.path("id").path("value").asText());
    assertEquals( "8", node17.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc4.xml", node17.path("uri").path("value").asText());
    assertEquals( "east", node17.path("nodes").path("value").asText());

    // Verify TEST 18 - join inner doc with xpath traversing down and multiple
    // values on xml
    ModifyPlan output18 = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.xpath("doc", "/doc/location/latLonPair/(lat|long)/text()")))
        .where(p.isDefined(p.col("nodes")))
		.orderBy(p.asc("id"));

    JacksonHandle jacksonHandle18 = new JacksonHandle();
    jacksonHandle18.setMimetype("application/json");
    rowMgr.resultDoc(output18, jacksonHandle18);
    JsonNode jsonBindingsNodes18 = jacksonHandle18.get().path("rows");

    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodes18.size());
    JsonNode node18 = jsonBindingsNodes18.path(0);
    assertEquals( "4", node18.path("id").path("value").asText());
    assertEquals( "8", node18.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc4.xml", node18.path("uri").path("value").asText());
    assertEquals( "39.90", node18.path("nodes").path("value").get(0).asText());
    assertEquals( "116.40", node18.path("nodes").path("value").get(1).asText());

    // Verify TEST 19 - join inner doc with traversing up xpath on json
    ModifyPlan output19 = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.fn.string(p.xpath("doc", "//city"))))
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.asc("id"));
    JacksonHandle jacksonHandle19 = new JacksonHandle();
    jacksonHandle19.setMimetype("application/json");
    rowMgr.resultDoc(output19, jacksonHandle19);
    JsonNode jsonBindingsNodes19 = jacksonHandle19.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes19.size());
    JsonNode node19 = jsonBindingsNodes19.path(0);
    assertEquals( "1", node19.path("id").path("value").asText());
    assertEquals( "2", node19.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc1.json", node19.path("uri").path("value").asText());
    assertEquals( "london", node19.path("nodes").path("value").asText());

    node19 = jsonBindingsNodes19.path(1);
    assertEquals( "3", node19.path("id").path("value").asText());
    assertEquals( "6", node19.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc3.json", node19.path("uri").path("value").asText());
    assertEquals( "new jersey", node19.path("nodes").path("value").asText());

    node19 = jsonBindingsNodes19.path(2);
    assertEquals( "4", node19.path("id").path("value").asText());
    assertEquals( "8", node19.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc4.xml", node19.path("uri").path("value").asText());
    assertEquals( "beijing", node19.path("nodes").path("value").asText());

    // Verify TEST 15 - join inner doc with xpath accessing attribute on xml
    ModifyPlan output15 = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.xpath("doc", "/doc/city")))
        .where(p.isDefined(p.col("nodes")))
		.orderBy(p.asc("id"));
    JacksonHandle jacksonHandle15 = new JacksonHandle();
    jacksonHandle15.setMimetype("application/json");
    rowMgr.resultDoc(output15, jacksonHandle15);
    JsonNode jsonBindingsNodes15 = jacksonHandle15.get().path("rows");

    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodes15.size());
    JsonNode node20 = jsonBindingsNodes15.path(0);
    assertEquals( "4", node20.path("id").path("value").asText());
    assertEquals( "8", node20.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc4.xml", node20.path("uri").path("value").asText());
    assertEquals( "<city>beijing</city>", node20.path("nodes").path("value").asText());

    // Verify TEST 21 - join inner doc with traversing deep xpath on json and
    // xml
    ModifyPlan output21 = p.fromLiterals(literals3)
        .joinDoc(p.col("doc"), p.col("uri"))
        .select(
            p.col("id"),
            p.col("val"),
            p.col("uri"),
            p.as("nodes", p.fn.number(p.xpath("doc", "//lat"))))
        .where(p.isDefined(p.col("nodes")))
        .orderBy(p.asc("id"));
    JacksonHandle jacksonHandle21 = new JacksonHandle();
    jacksonHandle21.setMimetype("application/json");
    rowMgr.resultDoc(output21, jacksonHandle21);
    JsonNode jsonBindingsNodes21 = jacksonHandle21.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes21.size());
    JsonNode node21 = jsonBindingsNodes21.path(0);
    assertEquals( "1", node21.path("id").path("value").asText());
    assertEquals( "2", node21.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc1.json", node21.path("uri").path("value").asText());
    assertEquals( "51.5", node21.path("nodes").path("value").asText());
    node21 = jsonBindingsNodes21.path(1);
    assertEquals( "3", node21.path("id").path("value").asText());
    assertEquals( "6", node21.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc3.json", node21.path("uri").path("value").asText());
    assertEquals( "40.72", node21.path("nodes").path("value").asText());
    node21 = jsonBindingsNodes21.path(2);
    assertEquals( "4", node21.path("id").path("value").asText());
    assertEquals( "8", node21.path("val").path("value").asText());
    assertEquals( "/optic/lexicon/test/doc4.xml", node21.path("uri").path("value").asText());
    assertEquals( "39.9", node21.path("nodes").path("value").asText());
  }

  /*
   * Test group-by without aggregate
   */
  @Test
  public void testGroupbyWithoutAggregate() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testGroupbyWithoutAggregate method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.groupBy(p.col("colorId")).orderBy(p.col("colorId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("colorId").path("value").asText());
    assertEquals( "2", jsonBindingsNodes.path(1).path("colorId").path("value").asText());
    assertEquals( "5", jsonBindingsNodes.path(2).path("colorId").path("value").asText());
  }

  /*
   * Test union with whereDistinct
   */
  @Test
  public void testUnionWithWhereDistinct() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testUnionWithWhereDistinct method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.union(plan2).whereDistinct().orderBy(p.sortKeySeq(p.col("rowId"), p.col("colorId")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 9 nodes returned.
    assertEquals( 9, jsonBindingsNodes.size());

    assertEquals( "1", jsonBindingsNodes.path(0).path("colorId").path("value").asText());
    assertEquals( "null", jsonBindingsNodes.path(0).path("colorDesc").path("value").asText());
    assertEquals( "2", jsonBindingsNodes.path(1).path("colorId").path("value").asText());
    assertEquals( "null", jsonBindingsNodes.path(1).path("colorDesc").path("value").asText());
    assertEquals( "null", jsonBindingsNodes.path(5).path("desc").path("value").asText());
    assertEquals( "null", jsonBindingsNodes.path(5).path("rowId").path("value").asText());
    assertEquals( "null", jsonBindingsNodes.path(8).path("desc").path("value").asText());
    assertEquals( "4", jsonBindingsNodes.path(8).path("colorId").path("value").asText());
  }

  /*
   * Test intersect
   */
  @Test
  public void testIntersect() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testIntersect method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(storeInformation);
    ModifyPlan plan2 = p.fromLiterals(internetSales);

    ModifyPlan output = plan1.select(p.col("txnDate"))
        .intersect(plan2.select(p.col("txnDate")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 1 node returned.
    assertEquals( 1, jsonBindingsNodes.size());
    assertEquals( "Jan-07-1999", jsonBindingsNodes.path(0).path("txnDate").path("value").asText());
  }

  /*
   * Test except
   */
  @Test
  public void testExcept() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testExcept method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(storeInformation);
    ModifyPlan plan2 = p.fromLiterals(internetSales);

    ModifyPlan output = plan1.select(p.colSeq("txnDate"))
        .except(plan2.select(p.colSeq("txnDate")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 3 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
    assertEquals( "Jan-05-1999", jsonBindingsNodes.path(0).path("txnDate").path("value").asText());
    assertEquals( "Jan-08-1999", jsonBindingsNodes.path(1).path("txnDate").path("value").asText());
    assertEquals( "Jan-08-1999", jsonBindingsNodes.path(2).path("txnDate").path("value").asText());
  }

  /*
   * Test arrayAggregate, sequenceAggregate and aggregate with distinct and
   * duplicate options
   */
  @Test
  public void testAggregateSeq() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testAggregateSeq method");

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    // Create a new Map for literals.
    Map<String, Object> row = new HashMap<>();
    Map<String, Object>[] fooLiteral = new HashMap[6];
    Map<String, Object>[] purpleLiteral = new HashMap[6];

    Map<String, Object>[] ballLiteral = new HashMap[6];

    row.put("rowId", 1);
    row.put("colorId", 1);
    row.put("desc", "ball");
    fooLiteral[0] = row;

    row = new HashMap<>();
    row.put("rowId", 1);
    row.put("colorId", 3);
    row.put("desc", "foo");
    fooLiteral[1] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId", 2);
    row.put("desc", "square");
    fooLiteral[2] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId", 1);
    row.put("desc", "box");
    fooLiteral[3] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId", 1);
    row.put("desc", "hoop");
    fooLiteral[4] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId", 5);
    row.put("desc", "circle");
    fooLiteral[5] = row;

    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    purpleLiteral[0] = row;
    // Add another red color
    purpleLiteral[1] = row;

    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "purple");
    purpleLiteral[2] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    purpleLiteral[3] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    purpleLiteral[4] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    purpleLiteral[5] = row;

    row = new HashMap<>();
    row.put("rowId", 1);
    row.put("colorId", 1);
    row.put("desc", "ball");
    ballLiteral[0] = row;
    row = new HashMap<>();
    row.put("rowId", 1);
    row.put("colorId", 3);
    row.put("desc", "ball");
    ballLiteral[1] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId", 2);
    row.put("desc", "square");
    ballLiteral[2] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId", 1);
    row.put("desc", "box");
    ballLiteral[3] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId", 1);
    row.put("desc", "hoop");
    ballLiteral[4] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId", 5);
    row.put("desc", "circle");
    ballLiteral[5] = row;

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(fooLiteral, "myItem");
    ModifyPlan plan2 = p.fromLiterals(purpleLiteral, "myColor");

    PlanColumn rowIdExp = p.viewCol("myItem", "rowId");
    PlanColumn itemColorIdCol = p.viewCol("myItem", "colorId");
    PlanColumn colorIdCol = p.viewCol("myColor", "colorId");

    ModifyPlan outputAgg = plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol))
        .groupBy(rowIdExp, p.arrayAggregate("colorIdArray", "colorDesc"))
        .orderBy(p.sortKeySeq(p.col("rowId")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputAgg, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("myItem.rowId").path("value").asText());
    assertEquals( 4, jsonBindingsNodes.path(0).path("colorIdArray").path("value").size());
    assertEquals( "blue", jsonBindingsNodes.path(1).path("colorIdArray").path("value").path(0).asText());

    // sequenceAggregate

    ModifyPlan outputSeqAgg = plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol))
        .groupBy(rowIdExp, p.sequenceAggregate("colorIdArray", "colorDesc"))
        .orderBy(p.sortKeySeq(p.col("rowId")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputSeqAgg, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("myItem.rowId").path("value").asText());
    assertEquals( 4, jsonBindingsNodes.path(0).path("colorIdArray").path("value").size());
    assertEquals( "blue", jsonBindingsNodes.path(1).path("colorIdArray").path("value").asText());

    // TEST 37 - aggregate with distinct option
    // plans from literals
    ModifyPlan plan3 = p.fromLiterals(ballLiteral);
    ModifyPlan plan4 = p.fromLiterals(literals2);

    // Verify DISTINCT
    PlanAggregateColSeq aggColSeq = p.aggregateSeq(p.count("descAgg", "desc", PlanValueOption.DISTINCT));
    PlanExprColSeq colSeq = p.colSeq("rowId");

    ModifyPlan outputCountDist = plan3.joinInner(plan4)
        .groupBy(colSeq, aggColSeq)
        .orderBy(p.sortKeySeq(p.col("rowId")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputCountDist, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("rowId").path("value").asText());
    assertEquals( 1, jsonBindingsNodes.path(0).path("descAgg").path("value").asInt());

    assertEquals( "4", jsonBindingsNodes.path(3).path("rowId").path("value").asText());
    assertEquals( 1, jsonBindingsNodes.path(3).path("descAgg").path("value").asInt());

    // aggregate with duplicate option
    PlanAggregateColSeq aggColSeqDup = p.aggregateSeq(p.count("descAgg", "desc", PlanValueOption.DUPLICATE));
    PlanExprColSeq colSeqDup = p.colSeq("rowId");

    ModifyPlan outputCountDup = plan3.joinInner(plan4)
        .groupBy(colSeqDup, aggColSeqDup)
        .orderBy(p.sortKeySeq(p.col("rowId")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(outputCountDup, jacksonHandle);
    JsonNode jsonBindingsNodesDup = jacksonHandle.get().path("rows");

    // Should have 4 nodes returned. Duplicate values are included.
    assertEquals( 4, jsonBindingsNodesDup.size());
    assertEquals( "1", jsonBindingsNodesDup.path(0).path("rowId").path("value").asText());
    assertEquals( 2, jsonBindingsNodesDup.path(0).path("descAgg").path("value").asInt());

    assertEquals( "4", jsonBindingsNodesDup.path(3).path("rowId").path("value").asText());
    assertEquals( 1, jsonBindingsNodesDup.path(3).path("descAgg").path("value").asInt());
  }

  /*
   * Test date time builtin functions
   */
  @Disabled
  public void testBuiltInFns()
  {
    System.out.println("In testBuiltInFns method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1, "myItem");
    ModifyPlan plan2 = p.fromLiterals(literals2, "myColor");

    PlanColumn descCol = p.viewCol("myItem", "desc");
    PlanColumn itemColorIdCol = p.viewCol("myItem", "colorId");
    PlanColumn colorIdCol = p.viewCol("myColor", "colorId");

    /*
     * XsDateExpr curDate = p.fn.currentDate(); XsTimeExpr curTime =
     * p.fn.currentTime(); SqlGenericDateTimeExpr sqlcurTime =
     * (SqlGenericDateTimeExpr) curTime; double sqlHr = p.sql.hours(sqlcurTime);
     *
     * PlanColumn log10 = p.math.log10(sqlHr); ModifyPlan output =
     * plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol)) .select(
     * p.as("currentDate", p.fn.currentDate()), p.as("curDate", curDate),
     * p.as("currentTime", p.fn.currentTime()), p.as("curTime", curTime),
     * p.as("upperCase", p.fn.upperCase(p.col("desc"))), p.as("semNum",
     * p.sem.isLiteral(p.col("desc"))), p.as("sqlHours",
     * p.sql.hours(sqlcurTime)), p.as("sqlHr", sqlHr), p.as("mathLog10",
     * p.math.log10(p.col("sqlHours"))), p.as("log10", log10) )
     * .orderBy(p.col("upperCase"));
     */
    // TODO Wait until Erik H comes back on this to discuss the recent changes
    // to SQL types.

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    // rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 2 nodes returned.
    assertEquals( 3, jsonBindingsNodes.size());
  }

  /*
   * Test invalid rowdef
   */
  @Test
  public void testInvalidDefinitions() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testInvalidDefinitions method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
        .select(p.colSeq("rowIdRef", "desc", "colorId", "colorDesc"));

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
    assertTrue( str.toString().contains("Column not found: rowIdRef"));

    // invalid viewCol
    ModifyPlan outputExtCol = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("invalid_view", "colorId"), p.xs.intVal(1)))
        .offsetLimit(1, 3);
    str = new StringBuilder();
    try {
      rowMgr.resultDoc(outputExtCol, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: invalid_view.colorId"));
  }

  /*
   * Test offsetLimit with negative offset
   */
  @Test
  public void testNegativeOffset() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testNegativeOffset method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);

    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
        .offsetLimit(-1, 1)
        .select(p.colSeq("rowId", "desc", "colorId", "colorDesc"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have OPTIC-INVALARGS exceptions.
    assertTrue( str.toString().contains("OPTIC-INVALARGS"));
    assertTrue( str.toString().contains("Invalid arguments: offset must be a non-negative number: -1"));
  }

  /*
   * Test invalid qualifier
   */
  @Test
  public void testInvalidQualifier() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testInvalidQualifier method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1, "table1");
    ModifyPlan plan2 = p.fromLiterals(literals2, "table2");

    ModifyPlan output = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("table1_Invalid", "colorId"), p.viewCol("table2", "colorId")))
        .orderBy(p.desc(p.viewCol("table1", "rowId")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    StringBuilder str = new StringBuilder();
    try {
      rowMgr.resultDoc(output, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have OPTIC-INVALARGS exceptions.
    assertTrue( str.toString().contains("SQL-NOCOLUMN"));
    assertTrue( str.toString().contains("Column not found: table1_Invalid.colorId"));
  }

  /*
   * Test Map function Uses JS function colorIdMapper installed in the REST
   * server module database.
   */
  @Test
  public void testMapFunction() {
    System.out.println("In testMapFunction method");
    Map<String, Object>[] literalsM1 = new HashMap[5];
    Map<String, Object>[] literalsM2 = new HashMap[4];

    Map<String, Object> row = new HashMap<>();
    row.put("rowId", 1);
    row.put("colorId_shape", 1);
    row.put("desc", "ball");
    literalsM1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId_shape", 2);
    row.put("desc", "square");
    literalsM1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId_shape", 1);
    row.put("desc", "box");
    literalsM1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId_shape", 1);
    row.put("desc", "hoop");
    literalsM1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId_shape", 5);
    row.put("desc", "circle");
    literalsM1[4] = row;

    // Assemble literalsM2
    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    literalsM2[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    literalsM2[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    literalsM2[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    literalsM2[3] = row;

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literalsM1);
    ModifyPlan plan2 = p.fromLiterals(literalsM2);

    ExportablePlan output = plan1.joinInner(plan2, p.on(p.col("colorId_shape"), p.col("colorId")))
        .select(
            p.as("rowId", p.col("rowId")),
            p.as("description", p.col("desc")),
            p.as("myColorId", p.col("colorId"))

        )
        .orderBy(p.asc("rowId"))
        .map(p.resolveFunction(p.xs.QName("colorIdMapper"), "/etc/optic/opticMappingFunctions.sjs"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 node returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("rowId").path("value").asText());
    assertEquals( "RED ROBIN", jsonBindingsNodes.path(0).path("myColorId").path("value").asText());
    assertEquals( "ball", jsonBindingsNodes.path(0).path("description").path("value").asText());

    assertEquals( "2", jsonBindingsNodes.path(1).path("rowId").path("value").asText());
    assertEquals( "BLUE JAY", jsonBindingsNodes.path(1).path("myColorId").path("value").asText());
    assertEquals( "square", jsonBindingsNodes.path(1).path("description").path("value").asText());

    assertEquals( "4", jsonBindingsNodes.path(3).path("rowId").path("value").asText());
    assertEquals( "RED ROBIN", jsonBindingsNodes.path(3).path("myColorId").path("value").asText());
    assertEquals( "hoop", jsonBindingsNodes.path(3).path("description").path("value").asText());
  }

  /*
   * Test Reduce function Uses JS function fibReducer installed in the REST
   * server module database.
   */
  @Test
  public void testReduceFunction() {
    System.out.println("In testReduceFunction method");
    Map<String, Object>[] literalsM1 = new HashMap[5];
    Map<String, Object>[] literalsM2 = new HashMap[4];

    Map<String, Object> row = new HashMap<>();
    row.put("rowId", 1);
    row.put("colorId_shape", 1);
    row.put("desc", "ball");
    literalsM1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId_shape", 2);
    row.put("desc", "square");
    literalsM1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId_shape", 1);
    row.put("desc", "box");
    literalsM1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId_shape", 1);
    row.put("desc", "hoop");
    literalsM1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId_shape", 5);
    row.put("desc", "circle");
    literalsM1[4] = row;

    // Assemble literalsM2
    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    literalsM2[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    literalsM2[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    literalsM2[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    literalsM2[3] = row;

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literalsM1);
    ModifyPlan plan2 = p.fromLiterals(literalsM2);

    ExportablePlan output = plan1.joinInner(plan2, p.on(p.col("colorId_shape"), p.col("colorId")))
        .select(
            p.as("myRowId", p.col("rowId"))
        )
        .orderBy(p.asc("myRowId"))
        .reduce(p.resolveFunction(p.xs.QName("fibReducer"), "/etc/optic/opticMappingFunctions.sjs"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

    // Should have 4 node returned.
    assertEquals( 4, jsonBindingsNodes.size());
    assertEquals( "1", jsonBindingsNodes.path(0).path("myRowId").path("value").asText());
    assertEquals( "0", jsonBindingsNodes.path(0).path("i").path("value").asText());
    assertEquals( "0", jsonBindingsNodes.path(0).path("fib").path("value").asText());

    assertEquals( "2", jsonBindingsNodes.path(1).path("myRowId").path("value").asText());
    assertEquals( "1", jsonBindingsNodes.path(1).path("i").path("value").asText());
    assertEquals( "1", jsonBindingsNodes.path(1).path("fib").path("value").asText());

    assertEquals( "4", jsonBindingsNodes.path(3).path("myRowId").path("value").asText());
    assertEquals( "3", jsonBindingsNodes.path(3).path("i").path("value").asText());
    assertEquals( "2", jsonBindingsNodes.path(3).path("fib").path("value").asText());
  }

  /*
   * Test existJoin with literals
   */
  @Test
  public void testExistJoin() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
    System.out.println("In testExistJoin method");

    Map<String, Object>[] colLiteral = new HashMap[7];
    Map<String, Object>[] invLiteral = new HashMap[6];

    Map<String, Object> row = new HashMap<>();

    row.put("rowId", 1);
    row.put("colorId", 1);
    row.put("desc", "ball");
    row.put("colorDesc", "red");
    invLiteral[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId", 2);
    row.put("desc", "square");
    row.put("colorDesc", "blue");
    invLiteral[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId", 1);
    row.put("desc", "box");
    row.put("colorDesc", "red");
    invLiteral[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId", 3);
    row.put("desc", "hoop");
    row.put("colorDesc", "black");
    invLiteral[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId", 4);
    row.put("desc", "circle");
    row.put("colorDesc", "yellow");
    invLiteral[4] = row;

    row = new HashMap<>();
    row.put("rowId", 6);
    row.put("colorId", 5);
    row.put("desc", "car");
    row.put("colorDesc", "YELLOW");
    invLiteral[5] = row;

    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    colLiteral[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    colLiteral[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    colLiteral[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    colLiteral[3] = row;

    row = new HashMap<>();
    row.put("colorId", 5);
    row.put("colorDesc", "YELLOW");
    colLiteral[4] = row;

    row = new HashMap<>();
    row.put("colorId", 6);
    row.put("colorDesc", "Yellow");
    colLiteral[5] = row;

    row = new HashMap<>();
    row.put("colorId", 7);
    row.put("colorDesc", "yellowish");
    colLiteral[6] = row;

    ArrayList<String> exptdExists = new ArrayList<String>();
    exptdExists.add("red");
    exptdExists.add("black");
    exptdExists.add("blue");
    exptdExists.add("yellow");
    exptdExists.add("YELLOW");
    Collections.sort(exptdExists);

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(colLiteral, "table1");
    ModifyPlan plan2 = p.fromLiterals(invLiteral, "table2");

    ModifyPlan existsOutput = plan1.existsJoin(plan2, p.on(p.viewCol("table1", "colorDesc"), p.viewCol("table2", "colorDesc")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(existsOutput, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    ArrayList<String> existColorList = new ArrayList<String>();
    existColorList.add(jsonBindingsNodes.path(0).path("table1.colorDesc").path("value").asText());
    existColorList.add(jsonBindingsNodes.path(1).path("table1.colorDesc").path("value").asText());
    existColorList.add(jsonBindingsNodes.path(2).path("table1.colorDesc").path("value").asText());
    existColorList.add(jsonBindingsNodes.path(3).path("table1.colorDesc").path("value").asText());
    existColorList.add(jsonBindingsNodes.path(4).path("table1.colorDesc").path("value").asText());

    Collections.sort(existColorList);

    assertTrue( existColorList.equals(exptdExists));

    // existJoin with literals - without on parameters
    ModifyPlan existsNoOptionsOutput = plan2.existsJoin(plan1).orderBy(p.desc(p.col("desc")));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(existsNoOptionsOutput, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");
    assertEquals( "square", jsonBindingsNodes.path(0).path("table2.desc").path("value").asText());
    assertEquals( "circle", jsonBindingsNodes.path(2).path("table2.desc").path("value").asText());
    assertEquals( "ball", jsonBindingsNodes.path(5).path("table2.desc").path("value").asText());

    // not exists join
    ModifyPlan notExistsOutput = plan1.notExistsJoin(plan2, p.on(p.viewCol("table1", "colorDesc"), p.viewCol("table2", "colorDesc")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(notExistsOutput, jacksonHandle);
    jsonBindingsNodes = jacksonHandle.get().path("rows");

    ArrayList<String> exptdNotExists = new ArrayList<String>();
    exptdNotExists.add("Yellow");
    exptdNotExists.add("yellowish");
    Collections.sort(exptdNotExists);

    ArrayList<String> notExistColorList = new ArrayList<String>();
    notExistColorList.add(jsonBindingsNodes.path(0).path("table1.colorDesc").path("value").asText());
    notExistColorList.add(jsonBindingsNodes.path(1).path("table1.colorDesc").path("value").asText());

    Collections.sort(notExistColorList);
    assertTrue( notExistColorList.equals(exptdNotExists));
  }

  /*
   * Test facetBy and bucketGroups
   */
  @Test
  public void testFacetByWithBucketGroups() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
    System.out.println("In testFacetByWithBucketGroups method");

    Map<String, Object>[] invLiteral = new HashMap[17];
    Map<String, Object>[] colLiteral = new HashMap[6];
    Map<String, Object> row = new HashMap<>();

    row = new HashMap<>();
    row.put("rowId", 1); row.put("colorId", 1); row.put("desc", "ball");
    invLiteral[0] = row;
    row = new HashMap<>();
    row.put("rowId", 2); row.put("colorId", 2); row.put("desc", "square");
    invLiteral[1] = row;
    row = new HashMap<>();
    row.put("rowId", 3); row.put("colorId", 1); row.put("desc", "box");
    invLiteral[2] = row;
    row = new HashMap<>();
    row.put("rowId", 4); row.put("colorId", 1); row.put("desc", "hoop");
    invLiteral[3] = row;
    row = new HashMap<>();
    row.put("rowId", 5); row.put("colorId", 5); row.put("desc", "circle");
    invLiteral[4] = row;
    row = new HashMap<>();
    row.put("rowId", 6); row.put("colorId", 1); row.put("desc", "key");
    invLiteral[5] = row;
    row = new HashMap<>();
    row.put("rowId", 7); row.put("colorId", 1); row.put("desc", "cup");
    invLiteral[6] = row;
    row = new HashMap<>();
    row.put("rowId", 8); row.put("colorId", 1); row.put("desc", "bike");
    invLiteral[7] = row;
    row = new HashMap<>();
    row.put("rowId", 9); row.put("colorId", 3); row.put("desc", "car");
    invLiteral[8] = row;
    row = new HashMap<>();
    row.put("rowId", 10); row.put("colorId", 3); row.put("desc", "cart");
    invLiteral[9] = row;
    row = new HashMap<>();
    row.put("rowId", 11); row.put("colorId", 4); row.put("desc", "cupholder");
    invLiteral[10] = row;
    row = new HashMap<>();
    row.put("rowId", 12); row.put("colorId", 2); row.put("desc", "scooter");
    invLiteral[11] = row;
    row = new HashMap<>();
    row.put("rowId", 13); row.put("colorId", 5); row.put("desc", "hat");
    invLiteral[12] = row;
    row = new HashMap<>();
    row.put("rowId", 14); row.put("colorId", 3); row.put("desc", "booster");
    invLiteral[13] = row;
    row = new HashMap<>();
    row.put("rowId", 15); row.put("colorId", 3); row.put("desc", "knapsack");
    invLiteral[14] = row;
    row = new HashMap<>();
    row.put("rowId", 16); row.put("colorId", 1); row.put("desc", "hovercraft");
    invLiteral[15] = row;
    row = new HashMap<>();
    row.put("rowId", 17); row.put("colorId", 6); row.put("desc", "dice");
    invLiteral[16] = row;

    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    colLiteral[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    colLiteral[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    colLiteral[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    colLiteral[3] = row;

    row = new HashMap<>();
    row.put("colorId", 5);
    row.put("colorDesc", "white");
    colLiteral[4] = row;

    row = new HashMap<>();
    row.put("colorId", 6);
    row.put("colorDesc", "grey");
    colLiteral[5] = row;
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(invLiteral, "table1");
    ModifyPlan plan2 = p.fromLiterals(colLiteral, "table2");
    ModifyPlan output1 = plan1.joinInner(plan2)
            .where(p.eq(p.viewCol("table1","colorId"), p.viewCol("table2","colorId")))
            .facetBy(p.namedGroupSeq(p.bucketGroup(p.xs.string("colorBucket"), p.viewCol("table1","colorId"), p.xs.integerSeq(2, 4) )));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output1, jacksonHandle);
    JsonNode colorBucketNodes = jacksonHandle.get().path("rows");
    assertEquals( "7", colorBucketNodes.path(0).path("colorBucket").path("value").path(0).path("count").asText());
    assertEquals( "6", colorBucketNodes.path(0).path("colorBucket").path("value").path(1).path("count").asText());
    assertEquals( "4", colorBucketNodes.path(0).path("colorBucket").path("value").path(2).path("count").asText());

    // outside range
    ModifyPlan output2 = plan1.joinInner(plan2)
            .where(p.eq(p.viewCol("table1","colorId"), p.viewCol("table2","colorId")))
            .facetBy(p.namedGroupSeq(p.bucketGroup(p.xs.string("colorBucket"), p.viewCol("table1","colorId"), p.xs.integerSeq(-1, 0) )));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output2, jacksonHandle);
    JsonNode colorBucketNodesNoRange = jacksonHandle.get().path("rows");
    assertEquals( "17", colorBucketNodesNoRange.path(0).path("colorBucket").path("value").path(0).path("count").asText());

    // string range
    ModifyPlan output3 = plan1.joinInner(plan2)
            .where(p.eq(p.viewCol("table1","colorId"), p.viewCol("table2","colorId")))
            .facetBy(p.namedGroupSeq(p.bucketGroup(p.xs.string("descBucket"), p.col("desc"), p.xs.stringSeq("ball", "dice"))));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output3, jacksonHandle);
    JsonNode descBucketNodesStringRange = jacksonHandle.get().path("rows");
    assertEquals( "9", descBucketNodesStringRange.path(0).path("descBucket").path("value").path(0).path("count").asText());
    assertEquals( "8", descBucketNodesStringRange.path(0).path("descBucket").path("value").path(1).path("count").asText());

    //bucket group negative test
    ModifyPlan output4 = plan1.joinInner(plan2)
            .where(p.eq(p.viewCol("table1","colorId"), p.viewCol("table2","colorId")))
            .facetBy(p.namedGroupSeq(p.bucketGroup(p.xs.string("colorBucket"), p.viewCol("table1","colorId"), p.xs.integerSeq(1, 0) )));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    try {
      rowMgr.resultDoc(output4, jacksonHandle);
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("failed to apply resource at rows: Internal Server Error"));
    }
  }

  @Test
  public void testLiteralsWithColumnInfo() {
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);
    ModifyPlan output = plan1.joinInner(plan2); //.orderBy(p.asc(p.col("rowId")));

    String colInfo = rowMgr.columnInfoAs(output, String.class);
    System.out.println(colInfo);

    String expectedType = isML11OrHigher ? "string": "unknown";

    assertColumnInfosExist(colInfo,
        new ColumnInfo("desc", expectedType),
        new ColumnInfo("colorDesc", expectedType));

    expectedType = isML11OrHigher ? "integer": "unknown";
    assertColumnInfosExist(colInfo,
        new ColumnInfo("colorId", expectedType),
        new ColumnInfo("rowId", expectedType));
  }
}
