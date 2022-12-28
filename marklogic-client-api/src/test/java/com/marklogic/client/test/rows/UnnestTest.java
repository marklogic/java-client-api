package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test demonstrates the primary use case for unnest, which is, for a given row, to create N rows based on a column in
 * that row containing an array with N values.
 */
public class UnnestTest extends AbstractOpticUpdateTest {

    private final static String SINGLE_NAME_COLUMN = "teamMemberName";

    /**
     * Inserts a test document for testing with the unnestSchema/unnestView view.
     */
    @BeforeEach
    public void insertTestDocument() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ObjectNode doc = mapper.createObjectNode();
        ArrayNode office = doc.putArray("office");
        Stream.of("Engineering:Cindy,Alice", "Sales:Bob", "Marketing: ").forEach(value -> {
            String[] tokens = value.split(":");
            ObjectNode obj = office.addObject();
            obj.put("department", tokens[0]);
            obj.put("teamMembers", StringUtils.hasText(tokens[1]) ? tokens[1] : null);
        });

        resultRows(op.fromDocDescriptors(op.docDescriptor(newWriteOp("/acme/office1.json", doc))).write());
    }

    @Test
    public void unnestInner() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
            .bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
            .unnestInner("teamMemberNameArray", SINGLE_NAME_COLUMN)
            .orderBy(op.col(SINGLE_NAME_COLUMN));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(3, rows.size());
        assertEquals("Alice", rows.get(0).getString(SINGLE_NAME_COLUMN));
        assertEquals("Bob", rows.get(1).getString(SINGLE_NAME_COLUMN));
        assertEquals("Cindy", rows.get(2).getString(SINGLE_NAME_COLUMN));
    }

    @Test
    public void unnestInnerWithOrdinality() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
            .bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
            .unnestInner("teamMemberNameArray", SINGLE_NAME_COLUMN, "index")
            .orderBy(op.col(SINGLE_NAME_COLUMN));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(3, rows.size());
        assertEquals("Alice", rows.get(0).getString(SINGLE_NAME_COLUMN));
        assertEquals(2, rows.get(0).getInt("index"),
			"The ordinality column is expected to capture the index of the value in the array that it came from, " +
				"where the index is 1-based, not 0-based");
        assertEquals("Bob", rows.get(1).getString(SINGLE_NAME_COLUMN));
        assertEquals(1, rows.get(1).getInt("index"));
        assertEquals("Cindy", rows.get(2).getString(SINGLE_NAME_COLUMN));
        assertEquals(1, rows.get(2).getInt("index"));
    }

    @Test
    public void unnestLeftOuter() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
            .bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
            .unnestLeftOuter("teamMemberNameArray", SINGLE_NAME_COLUMN)
            .orderBy(op.col(SINGLE_NAME_COLUMN));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());
        assertEquals("Alice", rows.get(0).getString(SINGLE_NAME_COLUMN));
        assertEquals("Bob", rows.get(1).getString(SINGLE_NAME_COLUMN));
        assertEquals("Cindy", rows.get(2).getString(SINGLE_NAME_COLUMN));
        assertNull(rows.get(3).get(SINGLE_NAME_COLUMN));
    }

    @Test
    public void unnestLeftOuterWithOrdinality() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.ModifyPlan plan = op.fromView("unnestSchema", "unnestView")
            .bind(op.as("teamMemberNameArray", op.fn.tokenize(op.col("teamMembers"), op.xs.string(","))))
            .unnestLeftOuter("teamMemberNameArray", SINGLE_NAME_COLUMN, "myIndex")
            .orderBy(op.col(SINGLE_NAME_COLUMN));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());
        assertEquals("Alice", rows.get(0).getString(SINGLE_NAME_COLUMN));
        assertEquals(2, rows.get(0).getInt("myIndex"));
        assertEquals("Bob", rows.get(1).getString(SINGLE_NAME_COLUMN));
        assertEquals(1, rows.get(1).getInt("myIndex"));
        assertEquals("Cindy", rows.get(2).getString(SINGLE_NAME_COLUMN));
        assertEquals(1, rows.get(2).getInt("myIndex"));
        assertNull(rows.get(3).get(SINGLE_NAME_COLUMN));
        assertNull(rows.get(3).get("myIndex"));
    }
}
