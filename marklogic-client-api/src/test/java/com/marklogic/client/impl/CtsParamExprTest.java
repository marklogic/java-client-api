/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.CtsParamExpr;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanSystemColumn;
import com.marklogic.client.type.ServerExpression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CtsParamExprTest {

  // ---------------------------------------------------------------------------
  // Server connection – shared across all integration tests in this class.
  // ---------------------------------------------------------------------------
  @BeforeAll
  static void connectToServer() {
    Common.connect();
  }

  @Test
  void exportsCtsParamInCollectionQuery() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.ModifyPlan employeesPlan = p
      .fromView("main", "employees")
      .select(p.col("EmployeeID"), p.col("FirstName"), p.col("LastName"))
      .where(p.cts.collectionQuery(p.cts.param("collection")));

    JacksonHandle handle = new JacksonHandle();
    employeesPlan.export(handle);
    ObjectNode exportNode = (ObjectNode) handle.get();

    assertEquals("op", exportNode.path("$optic").path("ns").asText());
    assertEquals("operators", exportNode.path("$optic").path("fn").asText());
    assertEquals("from-view", exportNode.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals("select", exportNode.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("where", exportNode.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals("collection-query", exportNode.path("$optic").path("args").get(2).path("args").get(0).path("fn").asText());
    assertEquals("param", exportNode.path("$optic").path("args").get(2).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals("cts", exportNode.path("$optic").path("args").get(2).path("args").get(0).path("args").get(0).path("ns").asText());
    assertEquals("collection", exportNode.path("$optic").path("args").get(2).path("args").get(0).path("args").get(0).path("args").get(0).path("args").get(0).asText());
  }

  @Test
  void rejectsOpParamInCtsNamespace() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> p.cts.collectionQuery(p.param("collection"))
    );

    assertEquals(
      "Cannot pass op:param() to cts:collection-query(). Use cts:param() for cts namespace expressions.",
      ex.getMessage()
    );
  }

  @Test
  void exportsCtsParamInJsonPropertyScopeQuery() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.ModifyPlan employeesPlan = p
      .fromView("main", "employees")
      .where(p.cts.jsonPropertyScopeQuery("myProperty", p.cts.param("inMyProperty")));

    JacksonHandle handle = new JacksonHandle();
    employeesPlan.export(handle);
    ObjectNode exportNode = (ObjectNode) handle.get();

    assertEquals("where", exportNode.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("json-property-scope-query", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("fn").asText());
    assertEquals("param", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("args").get(1).path("fn").asText());
    assertEquals("cts", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("args").get(1).path("ns").asText());
    assertEquals("inMyProperty", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("args").get(1).path("args").get(0).path("args").get(0).asText());
  }

  @Test
  void bindsCtsQueryByNameForCtsParamPlaceholder() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(p.cts.jsonPropertyScopeQuery("myProperty", p.cts.param("inMyProperty")))
      .bindParam("inMyProperty", p.cts.wordQuery("needle"));

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    ObjectNode exportNode = (ObjectNode) new com.fasterxml.jackson.databind.ObjectMapper().readTree(ast).path("$optic");

    ObjectNode queryNode = (ObjectNode) exportNode.path("args").get(1).path("args").get(0).path("args").get(1);
    assertEquals("cts", queryNode.path("ns").asText());
    assertEquals("word-query", queryNode.path("fn").asText());
  }

  @Test
  void bindsCtsQueryByCtsParamExpression() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();
    CtsParamExpr placeholder = p.cts.param("inMyProperty");

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(p.cts.jsonPropertyScopeQuery("myProperty", placeholder))
      .bindParam(placeholder, p.cts.trueQuery());

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    ObjectNode exportNode = (ObjectNode) new com.fasterxml.jackson.databind.ObjectMapper().readTree(ast).path("$optic");

    ObjectNode queryNode = (ObjectNode) exportNode.path("args").get(1).path("args").get(0).path("args").get(1);
    assertEquals("cts", queryNode.path("ns").asText());
    assertEquals("true-query", queryNode.path("fn").asText());
  }

  @Test
  void rejectsNonCtsParamExpressionForCtsQueryBinding() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    // The ClassCastException is thrown at the Java call-site cast, not inside bindParam.
    // This verifies that only CtsParamExpr instances (from cts.param()) satisfy the type
    // constraint — the JVM prevents any other CtsQueryExpr from being passed to this overload.
    ClassCastException ex = assertThrows(
      ClassCastException.class,
      () -> p.fromView("main", "employees").bindParam((CtsParamExpr) (ServerExpression) p.cts.trueQuery(), p.cts.wordQuery("needle"))
    );

    assertTrue(ex.getMessage().contains("CtsParamExpr"));
  }

  @Test
  void bindsMultipleCtsParamPlaceholders() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    // andQuery(CtsQueryExpr...) requires CtsQueryExpr args, but CtsParamExpr only extends
    // ServerExpression. Wrap each param in single-arg andQuery(ServerExpression) which
    // returns CtsQueryExpr, then compose via the varargs overload.
    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(p.cts.andQuery(
        p.cts.andQuery(p.cts.param("paramA")),
        p.cts.andQuery(p.cts.param("paramB"))
      ))
      .bindParam("paramA", p.cts.wordQuery("needleA"))
      .bindParam("paramB", p.cts.trueQuery());

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    // $optic.args[1] = where node; .args[0] = outer and-query; .args[0] = array of inner queries
    JsonNode outerAndQuery = new ObjectMapper().readTree(ast)
      .path("$optic").path("args").get(1).path("args").get(0);

    assertEquals("and-query", outerAndQuery.path("fn").asText());
    // Each inner and-query had its single cts:param arg replaced by the bound query
    assertEquals("word-query", outerAndQuery.path("args").get(0).get(0).path("args").get(0).path("fn").asText());
    assertEquals("true-query", outerAndQuery.path("args").get(0).get(1).path("args").get(0).path("fn").asText());
  }

  @Test
  void onlyTargetParamIsReplacedWhenMultipleExist() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(p.cts.andQuery(
        p.cts.andQuery(p.cts.param("paramA")),
        p.cts.andQuery(p.cts.param("paramB"))
      ))
      .bindParam("paramA", p.cts.wordQuery("needleA"));

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    JsonNode outerAndQuery = new ObjectMapper().readTree(ast)
      .path("$optic").path("args").get(1).path("args").get(0);

    // paramA was replaced with word-query
    assertEquals("word-query", outerAndQuery.path("args").get(0).get(0).path("args").get(0).path("fn").asText());
    // paramB remains as cts:param
    JsonNode unboundParam = outerAndQuery.path("args").get(0).get(1).path("args").get(0);
    assertEquals("param", unboundParam.path("fn").asText());
    assertEquals("cts", unboundParam.path("ns").asText());
    assertEquals("paramB", unboundParam.path("args").get(0).path("args").get(0).asText());
  }

  @Test
  void nullParamNameThrowsForCtsQueryBinding() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> p.fromView("main", "employees").bindParam((String) null, p.cts.wordQuery("needle"))
    );
    assertTrue(ex.getMessage().contains("paramName"));
  }

  @Test
  void nullQueryThrowsForCtsQueryBinding() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> p.fromView("main", "employees").bindParam("myParam", (CtsQueryExpr) null)
    );
    assertTrue(ex.getMessage().contains("query"));
  }

  // ---------------------------------------------------------------------------
  // Integration (roundtrip) tests – require a running MarkLogic instance.
  // ---------------------------------------------------------------------------

  /**
   * Verifies end-to-end that a {@code cts:param()} placeholder bound to a
   * {@link CtsQueryExpr} via {@code bindParam} is correctly substituted before
   * the plan is sent to MarkLogic and that the server returns the expected rows.
   *
   * <p>The plan mirrors the non-parameterized {@code testSearch} test in
   * {@code RowManagerTest}: {@code fromSearch()} joined to the
   * {@code opticUnitTest.musician_ml10} view, filtered to trumpet players and
   * ordered by last name. Binding {@code cts:jsonPropertyValueQuery} to the
   * placeholder must produce the same two rows (Armstrong, Davis).</p>
   */
  @Test
  void roundtripFromSearchWithCtsParamBinding() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn viewDocId = p.fragmentIdCol("viewDocId");
    CtsParamExpr searchParam = p.cts.param("searchQuery");

    PlanBuilder.Plan plan = p
      // CtsParamExpr extends ServerExpression, not CtsQueryExpr, so it cannot be passed
      // directly to fromSearch(CtsQueryExpr). Wrapping in andQuery(ServerExpression)
      // produces a CtsQueryExpr while keeping the cts:param node intact in the AST.
      .fromSearch(p.cts.andQuery(searchParam))
      .joinInner(
        p.fromView("opticUnitTest", "musician_ml10", "", viewDocId),
        p.on(p.fragmentIdCol("fragmentId"), viewDocId)
      )
      .orderBy(p.col("lastName"))
      .bindParam(searchParam, p.cts.jsonPropertyValueQuery("instrument", "trumpet"));

    String[] expectedLastName  = {"Armstrong", "Davis"};
    String[] expectedFirstName = {"Louis",     "Miles"};

    int rowNum = 0;
    for (RowRecord row : rowMgr.resultRows(plan)) {
      assertEquals(expectedLastName[rowNum],  row.getString("lastName"));
      assertEquals(expectedFirstName[rowNum], row.getString("firstName"));
      rowNum++;
    }
    assertEquals(2, rowNum);
  }

  // ---------------------------------------------------------------------------
  // op:param() in where() – unit tests
  // ---------------------------------------------------------------------------

  @Test
  void whereWithOpParamProducesCorrectAst() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.ModifyPlan plan = p
      .fromView("main", "employees")
      .where(p.param("query"));

    JacksonHandle handle = new JacksonHandle();
    plan.export(handle);
    ObjectNode exportNode = (ObjectNode) handle.get();

    // where() args[0] must be an op:param node
    ObjectNode whereArgs0 = (ObjectNode) exportNode.path("$optic").path("args").get(1).path("args").get(0);
    assertEquals("op",    whereArgs0.path("ns").asText(), "namespace should be op");
    assertEquals("param", whereArgs0.path("fn").asText(), "function should be param");
    assertEquals("query", whereArgs0.path("args").get(0).path("args").get(0).asText(), "param name");
  }

  @Test
  void bindsOpParamToQueryByString() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(p.param("query"))
      .bindParam("query", p.cts.wordQuery("needle"));

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    ObjectNode whereArgs0 = (ObjectNode) new ObjectMapper().readTree(ast)
      .path("$optic").path("args").get(1).path("args").get(0);

    // The op:param node must have been replaced by the bound word-query
    assertEquals("cts",        whereArgs0.path("ns").asText());
    assertEquals("word-query", whereArgs0.path("fn").asText());
  }

  @Test
  void bindsOpParamToQueryViaParamExpr() throws Exception {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();
    PlanParamExpr queryParam = p.param("query");

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      .where(queryParam)
      .bindParam(queryParam, p.cts.trueQuery());

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    ObjectNode whereArgs0 = (ObjectNode) new ObjectMapper().readTree(ast)
      .path("$optic").path("args").get(1).path("args").get(0);

    assertEquals("cts",        whereArgs0.path("ns").asText());
    assertEquals("true-query", whereArgs0.path("fn").asText());
  }

  @Test
  void rejectsNullParamExprForOpParamCtsBinding() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> p.fromView("main", "employees").bindParam((PlanParamExpr) null, p.cts.wordQuery("needle"))
    );
    assertTrue(ex.getMessage().contains("param"));
  }

  @Test
  void rejectsNullQueryForOpParamCtsBinding() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> p.fromView("main", "employees").bindParam(p.param("query"), (CtsQueryExpr) null)
    );
    assertTrue(ex.getMessage().contains("query"));
  }

  @Test
  void unboundOpParamIsPreservedInAst() {
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.ModifyPlan plan = p
      .fromView("main", "employees")
      .where(p.param("query"));

    // No bindParam call – op:param node must survive in the exported AST
    JacksonHandle handle = new JacksonHandle();
    plan.export(handle);
    ObjectNode whereArgs0 = (ObjectNode) handle.get()
      .path("$optic").path("args").get(1).path("args").get(0);

    assertEquals("op",    whereArgs0.path("ns").asText());
    assertEquals("param", whereArgs0.path("fn").asText());
  }

  @Test
  void stringKeyedBindingSubstitutesBothCtsParamAndOpParam() throws Exception {
    // A single bindParam(String, CtsQueryExpr) call must substitute ALL param nodes –
    // both cts:param and op:param – that share the given name in the same plan.
    PlanBuilderSubImpl p = new PlanBuilderSubImpl();

    PlanBuilder.Plan plan = p
      .fromView("main", "employees")
      // op:param("myParam") used directly in where()
      .where(p.param("myParam"))
      // cts:param("myParam") nested inside a cts expression in a second where()
      .where(p.cts.jsonPropertyScopeQuery("prop", p.cts.param("myParam")))
      .bindParam("myParam", p.cts.wordQuery("needle"));

    String ast = ((StringHandle) ((PlanBuilderBaseImpl.RequestPlan) plan).getHandle()).get();
    JsonNode root = new ObjectMapper().readTree(ast).path("$optic");

    // First where: op:param in where() must be replaced with word-query
    JsonNode firstWhereArg = root.path("args").get(1).path("args").get(0);
    assertEquals("cts",        firstWhereArg.path("ns").asText(), "op:param in where() must be substituted");
    assertEquals("word-query", firstWhereArg.path("fn").asText(), "op:param in where() must be substituted");

    // Second where: cts:param inside jsonPropertyScopeQuery must also be replaced with word-query
    JsonNode scopeQueryArg1 = root.path("args").get(2).path("args").get(0).path("args").get(1);
    assertEquals("cts",        scopeQueryArg1.path("ns").asText(), "cts:param in jsonPropertyScopeQuery must be substituted");
    assertEquals("word-query", scopeQueryArg1.path("fn").asText(), "cts:param in jsonPropertyScopeQuery must be substituted");
  }

  // ---------------------------------------------------------------------------
  // Integration (roundtrip) test – op:param in where()
  // ---------------------------------------------------------------------------

  /**
   * Verifies end-to-end that an {@code op:param()} placeholder used directly in
   * {@code where()} and bound to a {@link CtsQueryExpr} via {@code bindParam} is
   * substituted before the plan is sent to MarkLogic and that the server returns
   * the expected rows.
   *
   * <p>The plan queries the {@code opticUnitTest.musician_ml10} view, filters via
   * a {@code where(op.param())} bound to {@code cts:jsonPropertyValueQuery}, and
   * orders by last name. The expected result is Armstrong and Davis (trumpet
   * players), matching the behaviour verified by
   * {@link #roundtripFromSearchWithCtsParamBinding()}.</p>
   */
  @Test
  void roundtripWhereWithOpParamBinding() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanParamExpr queryParam = p.param("searchQuery");

    PlanBuilder.Plan plan = p
      .fromView("opticUnitTest", "musician_ml10")
      .where(queryParam)
      .orderBy(p.col("lastName"))
      .bindParam(queryParam, p.cts.jsonPropertyValueQuery("instrument", "trumpet"));

    String[] expectedLastName  = {"Armstrong", "Davis"};
    String[] expectedFirstName = {"Louis",     "Miles"};

    int rowNum = 0;
    for (RowRecord row : rowMgr.resultRows(plan)) {
      assertEquals(expectedLastName[rowNum],  row.getString("lastName"));
      assertEquals(expectedFirstName[rowNum], row.getString("firstName"));
      rowNum++;
    }
    assertEquals(2, rowNum);
  }

  @Test
  void roundtripWhereWithOpParamBindingInCtsOr() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    CtsParamExpr queryParam = p.cts.param("searchQuery");

    PlanBuilder.Plan plan = p
      .fromView("opticUnitTest", "musician_ml10")
      .where(
        p.cts.orQuery(
          p.cts.jsonPropertyValueQuery("instrument", "vocal"),
          queryParam
        )
      )
      .orderBy(p.col("lastName"))
      .bindParam(queryParam, p.cts.jsonPropertyValueQuery("instrument", "trumpet"));

    String[] expectedLastName  = {"Armstrong", "Davis"};
    String[] expectedFirstName = {"Louis",     "Miles"};

    int rowNum = 0;
    for (RowRecord row : rowMgr.resultRows(plan)) {
      assertEquals(expectedLastName[rowNum],  row.getString("lastName"));
      assertEquals(expectedFirstName[rowNum], row.getString("firstName"));
      rowNum++;
    }
    assertEquals(2, rowNum);
  }
}
