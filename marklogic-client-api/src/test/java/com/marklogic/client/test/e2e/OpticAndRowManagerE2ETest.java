/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.test.AbstractClientTest;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E Test: Optic API and Relational Row Operations")
public class OpticAndRowManagerE2ETest extends AbstractClientTest {

	private DatabaseClient client;
	private RowManager rowMgr;
	private PlanBuilder p;

	@BeforeEach
	void setUp() {
		client = Common.connect();
		rowMgr = client.newRowManager();
		p = rowMgr.newPlanBuilder();
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Optic Pipeline on Literals: Filter, Compute Column, Sort, and Iterate")
	void testOpticLiteralsPipeline() {
		List<Map<String, Object>> rows = new ArrayList<>();
		rows.add(Map.of("id", 1, "department", "Engineering", "salary", 120000));
		rows.add(Map.of("id", 2, "department", "Engineering", "salary", 95000));
		rows.add(Map.of("id", 3, "department", "Marketing", "salary", 80000));
		rows.add(Map.of("id", 4, "department", "Engineering", "salary", 140000));
		rows.add(Map.of("id", 5, "department", "Sales", "salary", 70000));

		ModifyPlan plan = p.fromLiterals(rows.toArray(new Map[0]))
			.where(p.eq(p.col("department"), p.xs.string("Engineering")))
			.select(
				p.col("id"),
				p.col("department"),
				p.col("salary"),
				p.as("bonus", p.multiply(p.col("salary"), p.xs.doubleVal(0.15)))
			)
			.where(p.gt(p.col("salary"), p.xs.intVal(100000)))
			.orderBy(p.desc(p.col("salary")));

		RowSet<RowRecord> resultRows = rowMgr.resultRows(plan);
		List<RowRecord> list = new ArrayList<>();
		resultRows.forEach(list::add);

		assertEquals(2, list.size(), "Should match 2 high-earning Engineers");
		assertEquals(140000, list.get(0).getInt("salary"));
		assertEquals(21000.0, list.get(0).getDouble("bonus"), 0.01);
		assertEquals(120000, list.get(1).getInt("salary"));
		assertEquals(18000.0, list.get(1).getDouble("bonus"), 0.01);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Optic Aggregations: GroupBy with Count and Average")
	void testOpticAggregationAndGroupBy() {
		List<Map<String, Object>> rows = new ArrayList<>();
		rows.add(Map.of("category", "Books", "price", 25));
		rows.add(Map.of("category", "Books", "price", 35));
		rows.add(Map.of("category", "Electronics", "price", 200));
		rows.add(Map.of("category", "Electronics", "price", 400));
		rows.add(Map.of("category", "Electronics", "price", 600));

		ModifyPlan plan = p.fromLiterals(rows.toArray(new Map[0]))
			.groupBy(
				p.col("category"),
				p.aggregateSeq(
					p.count("itemCount", "price"),
					p.avg("avgPrice", "price")
				)
			)
			.orderBy(p.asc(p.col("category")));

		RowSet<RowRecord> resultRows = rowMgr.resultRows(plan);
		Map<String, Integer> counts = new HashMap<>();
		Map<String, Double> averages = new HashMap<>();

		for (RowRecord row : resultRows) {
			String cat = row.getString("category");
			counts.put(cat, row.getInt("itemCount"));
			averages.put(cat, row.getDouble("avgPrice"));
		}

		assertEquals(2, counts.get("Books"));
		assertEquals(30.0, averages.get("Books"), 0.01);
		assertEquals(3, counts.get("Electronics"));
		assertEquals(400.0, averages.get("Electronics"), 0.01);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Optic Join: Inner Join between Two Literal Datasets")
	void testOpticJoin() {
		List<Map<String, Object>> customers = List.of(
			Map.of("custId", 101, "customerName", "Alice"),
			Map.of("custId", 102, "customerName", "Bob")
		);

		List<Map<String, Object>> orders = List.of(
			Map.of("orderId", 1, "custId", 101, "amount", 500),
			Map.of("orderId", 2, "custId", 101, "amount", 250),
			Map.of("orderId", 3, "custId", 102, "amount", 700)
		);

		ModifyPlan customerPlan = p.fromLiterals(customers.toArray(new Map[0]));
		ModifyPlan orderPlan = p.fromLiterals(orders.toArray(new Map[0]));

		ModifyPlan joined = customerPlan.joinInner(
			orderPlan,
			p.on("custId", "custId")
		).select(
			p.col("customerName"),
			p.col("orderId"),
			p.col("amount")
		).orderBy(p.asc(p.col("orderId")));

		RowSet<RowRecord> resultRows = rowMgr.resultRows(joined);
		List<RowRecord> list = new ArrayList<>();
		resultRows.forEach(list::add);

		assertEquals(3, list.size());
		assertEquals("Alice", list.get(0).getString("customerName"));
		assertEquals(500, list.get(0).getInt("amount"));
		assertEquals("Alice", list.get(1).getString("customerName"));
		assertEquals(250, list.get(1).getInt("amount"));
		assertEquals("Bob", list.get(2).getString("customerName"));
		assertEquals(700, list.get(2).getInt("amount"));
	}

	@Test
	@DisplayName("Optic Database View Query: Querying TDE View 'opticUnitTest.musician'")
	void testOpticViewQuery() {
		ModifyPlan plan = p.fromView("opticUnitTest", "musician")
			.select(p.col("firstName"), p.col("lastName"), p.col("dob"))
			.orderBy(p.asc(p.col("lastName")))
			.limit(10);

		RowSet<RowRecord> resultRows = rowMgr.resultRows(plan);
		int rowCount = 0;
		for (RowRecord row : resultRows) {
			assertNotNull(row.getString("lastName"));
			assertNotNull(row.getString("firstName"));
			rowCount++;
		}
		assertTrue(rowCount > 0, "TDE view opticUnitTest.musician should contain sample musicians");
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Optic Output Formats: JSON Handle Execution and Serialization")
	void testOpticJsonOutput() {
		List<Map<String, Object>> rows = List.of(
			Map.of("ticker", "MLOG", "price", 150.5),
			Map.of("ticker", "GOOG", "price", 185.0)
		);

		ModifyPlan plan = p.fromLiterals(rows.toArray(new Map[0]))
			.orderBy(p.desc(p.col("price")));

		JacksonHandle handle = new JacksonHandle();
		rowMgr.resultDoc(plan, handle);

		JsonNode root = handle.get();
		assertNotNull(root);
		assertTrue(root.isArray() || root.has("rows"));
	}
}
