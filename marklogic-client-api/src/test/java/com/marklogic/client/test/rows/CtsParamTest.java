/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML12;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RequiresML12.class)
class CtsParamTest extends AbstractClientTest {

	private RowManager rowManager;
	private PlanBuilder op;

	@BeforeEach
	void setup() {
		rowManager = Common.newClient().newRowManager();
		op = rowManager.newPlanBuilder();
	}

	@Test
	void wordQuery() {
		PlanBuilder.ModifyPlan plan = op.fromView("opticUnitTest", "musician", "")
			.where(op.cts.wordQuery(op.param("myName")));

		// Print the plan so it can be easily imported via qconsole to verify it's valid.
		JsonNode serializedPlan = plan.export(new JacksonHandle()).get();
		System.out.println("Plan: " + serializedPlan.toPrettyString());

		List<RowRecord> rows = rowManager.resultRows(plan.bindParam("myName", "Louis")).stream().toList();
		assertEquals(1, rows.size());
		assertEquals("Louis", rows.get(0).getString("firstName"));
	}
}
