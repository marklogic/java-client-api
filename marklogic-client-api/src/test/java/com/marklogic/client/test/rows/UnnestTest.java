package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test demonstrates the primary use case for unnest, which is, for a given row, to create N rows based on a column in
 * that row containing an array with N values.
 */
@ExtendWith(RequiresML11.class)
public class UnnestTest extends AbstractOpticUpdateTest {

	private final static String TEAM_MEMBER_NAME_COLUMN = "teamMemberName";

	/**
	 * Inserts a test document for testing with the unnestSchema/unnestView view.
	 */
	@BeforeEach
	public void insertTestDocument() {
		ObjectNode doc = mapper.createObjectNode();
		ArrayNode office = doc.putArray("office");
		Stream.of("Engineering:Cindy,Alice,Dan", "Sales:Bob", "Marketing: ").forEach(value -> {
			String[] tokens = value.split(":");
			ObjectNode obj = office.addObject();
			obj.put("department", tokens[0]);
			obj.put("teamMembers", StringUtils.hasText(tokens[1]) ? tokens[1] : null);
		});

		resultRows(op.fromDocDescriptors(op.docDescriptor(newWriteOp("/acme/office1.json", doc))).write());
	}

	@Test
	public void unnestInner() {
		PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
			.bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
			.unnestInner("teamMemberNameArray", TEAM_MEMBER_NAME_COLUMN)
			.orderBy(op.col(TEAM_MEMBER_NAME_COLUMN))
			.select(op.col("department"), op.col(TEAM_MEMBER_NAME_COLUMN));

		List<RowRecord> rows = resultRows(plan);
		assertEquals(4, rows.size(), "The 3 incoming rows should result in 4 rows due to the unnestInner operation " +
			"creating a row for each team member name, and there are 4 team member names");
		assertEquals("Alice", rows.get(0).getString(TEAM_MEMBER_NAME_COLUMN), "Alice should be first since the rows " +
			"are ordered by team member name");
		assertEquals("Bob", rows.get(1).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals("Cindy", rows.get(2).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals("Dan", rows.get(3).getString(TEAM_MEMBER_NAME_COLUMN));
	}

	@Test
	public void unnestInnerWithOrdinality() {
		PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
			.bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
			.unnestInner("teamMemberNameArray", TEAM_MEMBER_NAME_COLUMN, "index")
			.orderBy(op.col(TEAM_MEMBER_NAME_COLUMN));

		List<RowRecord> rows = resultRows(plan);
		System.out.println(rows);
		assertEquals(4, rows.size());
		assertEquals("Alice", rows.get(0).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(2, rows.get(0).getInt("index"),
			"The ordinality column is expected to capture the index of the value in the array that it came from, " +
				"where the index is 1-based, not 0-based");
		assertEquals("Bob", rows.get(1).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(1, rows.get(1).getInt("index"));
		assertEquals("Cindy", rows.get(2).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(1, rows.get(2).getInt("index"));
		assertEquals("Dan", rows.get(3).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(3, rows.get(3).getInt("index"));
	}

	@Test
	public void unnestLeftOuter() {
		PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
			.bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
			.unnestLeftOuter("teamMemberNameArray", TEAM_MEMBER_NAME_COLUMN)
			.orderBy(op.col(TEAM_MEMBER_NAME_COLUMN));

		List<RowRecord> rows = resultRows(plan);
		assertEquals(5, rows.size());
		assertEquals("Alice", rows.get(0).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals("Bob", rows.get(1).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals("Cindy", rows.get(2).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals("Dan", rows.get(3).getString(TEAM_MEMBER_NAME_COLUMN));
		assertNull(rows.get(4).get(TEAM_MEMBER_NAME_COLUMN), "unnestLeftOuter should include rows where the array is null, " +
			"and thus the Marketing row should be retained (whereas unnestInner discards it)");
		assertEquals("Marketing", rows.get(4).getString("department"));
	}

	@Test
	public void unnestLeftOuterWithOrdinality() {
		PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
			.bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
			.unnestLeftOuter("teamMemberNameArray", TEAM_MEMBER_NAME_COLUMN, "myIndex")
			.orderBy(op.col(TEAM_MEMBER_NAME_COLUMN));

		List<RowRecord> rows = resultRows(plan);
		assertEquals(5, rows.size());
		assertEquals("Alice", rows.get(0).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(2, rows.get(0).getInt("myIndex"));
		assertEquals("Bob", rows.get(1).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(1, rows.get(1).getInt("myIndex"));
		assertEquals("Cindy", rows.get(2).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(1, rows.get(2).getInt("myIndex"));
		assertEquals("Dan", rows.get(3).getString(TEAM_MEMBER_NAME_COLUMN));
		assertEquals(3, rows.get(3).getInt("myIndex"));
		assertNull(rows.get(4).get(TEAM_MEMBER_NAME_COLUMN));
		assertNull(rows.get(4).get("myIndex"));
	}
}
