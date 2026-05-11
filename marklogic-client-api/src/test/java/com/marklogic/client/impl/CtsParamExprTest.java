/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CtsParamExprTest {

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
}
