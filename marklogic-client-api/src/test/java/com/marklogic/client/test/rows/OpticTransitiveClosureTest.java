/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */

package com.marklogic.client.test.rows;

import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowTemplate;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanTransitiveClosureOptions;
import com.marklogic.client.type.PlanTripleOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the transitiveClosure Optic function introduced in MarkLogic 12.
 * This test class verifies the transitive closure operation over graph-like structures,
 * identifying all reachable node pairs from a given start node to an end node through
 * one or more intermediate steps. The tests use a parent-child relationship graph
 * loaded from transClosureTripleSet.xml using mlDeploy during setup.
 */
public class OpticTransitiveClosureTest {

    @BeforeAll
    public static void setUp() {
        Common.connect();
    }

    /**
     * Simple full transitive closure without options.
     * Expects 21 rows with person and ancestor columns.
     */
    @Test
    void testSimplePatternFullTransitiveClosure() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> op.fromTriples(
			op.pattern(
				op.col("person"),
				op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
				op.col("ancestor")
			),
			null,
			(String) null,
			PlanTripleOption.DEDUPLICATED
		)
		.transitiveClosure(op.col("person"), op.col("ancestor"))
		.orderBy(op.sortKeySeq(op.asc("ancestor"), op.asc("person"))),
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(21, rowList.size(), "Expected 21 rows for full transitive closure");

                // Verify first row has required columns
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");

                return null;
            });
    }

    /**
     * Transitive closure with minLength=2 (grandparents and up).
     * This excludes direct parent-child relationships.
     * Expects 12 rows.
     */
    @Test
    void testTransitiveClosureWithMinLength() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> {
                // Create options with minLength=2
                PlanTransitiveClosureOptions options = op.transitiveClosureOptions()
                    .withMinLength(2);

                return op.fromTriples(
                    op.pattern(
                        op.col("person"),
                        op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
                        op.col("ancestor")
                    ),
						null,
                    (String) null,
                    PlanTripleOption.DEDUPLICATED
                )
                .transitiveClosure(op.col("person"), op.col("ancestor"), options);
            },
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                // 2 steps or more excludes direct parent-child relationships
                assertEquals(12, rowList.size(), "Expected 12 rows with minLength=2 (grandparents and up)");

                // Verify columns exist
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");

                return null;
            });
    }

    /**
     * Transitive closure with minLength=2 and maxLength=2 (grandparents only).
     * Expects 6 rows.
     */
    @Test
    void testTransitiveClosureWithMinAndMaxLength() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> {
                // Create options with minLength=2 and maxLength=2
                PlanTransitiveClosureOptions options = op.transitiveClosureOptions()
                    .withMinLength(2)
                    .withMaxLength(2);

                return op.fromTriples(
                    op.pattern(
                        op.col("person"),
                        op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
                        op.col("ancestor")
                    ),
                    null,
                    "http://test.optic.tc#",
                    PlanTripleOption.DEDUPLICATED
                )
                .transitiveClosure(op.col("person"), op.col("ancestor"), options);
            },
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                // 2 steps only is grandparent relationships only
                assertEquals(6, rowList.size(), "Expected 6 rows with minLength=2 and maxLength=2 (grandparents only)");

                // Verify columns exist
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");

                return null;
            });
    }

    /**
     * Transitive closure with column renamed using op.as().
     * Uses "parent" column and renames it to "ancestor".
     * Expects 21 rows.
     */
    @Test
    void testTransitiveClosureWithColumnRename() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> op.fromTriples(
			op.pattern(
				op.col("person"),
				op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
				op.col("parent")
			),
			null,
			(String) null,
			PlanTripleOption.DEDUPLICATED
		)
		.transitiveClosure(op.col("person"), op.as("ancestor", op.col("parent"))),
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(21, rowList.size(), "Expected 21 rows with renamed column");

                // Verify renamed column exists
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist (renamed from parent)");

                return null;
            });
    }

    /**
     * Transitive closure with joins to get labels.
     * Joins with label triples to get human-readable names.
     * Expects 21 rows with person, ancestor, person_name, and ancestor_name columns.
     */
    @Test
    void testTransitiveClosureWithJoinsForLabels() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> {
                var labelIri = op.sem.iri("http://test.optic.tc#label");
                return op.fromTriples(
                    op.pattern(
                        op.col("person"),
                        op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
                        op.col("ancestor")
                    )
                                , (String)null,
                                "http://test.optic.tc#",
                                PlanTripleOption.DEDUPLICATED
                )
                .transitiveClosure(op.col("person"), op.col("ancestor"))
                .joinLeftOuter(
                    op.fromTriples(
                        op.pattern(op.col("person"), labelIri, op.col("person_name")),
                        null,
                        "http://test.optic.tc#",
                        PlanTripleOption.DEDUPLICATED
                    )
                )
                .joinLeftOuter(
                    op.fromTriples(
                        op.pattern(op.col("ancestor"), labelIri, op.col("ancestor_name")),
                        null,
                        "http://test.optic.tc#",
                        PlanTripleOption.DEDUPLICATED
                    )
                );
            },
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(21, rowList.size(), "Expected 21 rows with joined labels");

                // Verify all columns exist
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");
                assertNotNull(firstRow.get("person_name"), "person_name column should exist");
                assertNotNull(firstRow.get("ancestor_name"), "ancestor_name column should exist");

                return null;
            });
    }

    /**
     * Test 6: Transitive closure using string column names instead of column expressions.
     * This tests the convenience overload that accepts String parameters.
     * Expects 21 rows.
     */
    @Test
    void testTransitiveClosureWithStringColumnNames() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> op.fromTriples(
				op.pattern(
					op.col("person"),
					op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
					op.col("ancestor")
				),
				null,
				(String) null,
				PlanTripleOption.DEDUPLICATED
		)
		.transitiveClosure("person", "ancestor")
		.orderBy(op.sortKeySeq(op.asc("ancestor"), op.asc("person"))),
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(21, rowList.size(), "Expected 21 rows using string column names");

                // Verify columns exist
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");

                return null;
            });
    }

    /**
     * Test 7: Transitive closure with string column names and options.
     * Expects 12 rows with minLength=2.
     */
    @Test
    void testTransitiveClosureWithStringNamesAndOptions() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> op.fromTriples(
			op.pattern(
				op.col("person"),
				op.sem.iri("http://marklogic.com/transitiveClosure/parent"),
				op.col("ancestor")
			),
			null,
			(String) null,
			PlanTripleOption.DEDUPLICATED
		)
		.transitiveClosure("person", "ancestor", op.transitiveClosureOptions().withMinLength(2)),
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(12, rowList.size(), "Expected 12 rows with string names and minLength=2");

                return null;
            });
    }

    /**
     * Test 8: Transitive closure starting from a SPARQL query with minLength=2
     * Expects 21 rows. This is harder to do in SPARQL directly, so we do the closure in Optic.
     */
    @Test
    void testTransitiveClosureFromSparql() {
        if (Common.getMarkLogicVersion().getMajor() < 12) {
            return;
        }

        new RowTemplate(Common.client).query(op -> op.fromSparql(
			"SELECT ?person ?ancestor WHERE { ?person <http://marklogic.com/transitiveClosure/parent> ?ancestor }"
		)
		.transitiveClosure(op.col("person"), op.col("ancestor"), op.transitiveClosureOptions().withMinLength(2))
		.orderBy(op.sortKeySeq(op.asc("ancestor"), op.asc("person"))),
            rows -> {
                List<RowRecord> rowList = new ArrayList<>();
                rows.forEach(rowList::add);

                assertEquals(12, rowList.size(), "Expected 12 rows for transitive closure from SPARQL with minLength=2 (grandparents and farther)");

                // Verify first row has required columns
                RowRecord firstRow = rowList.get(0);
                assertNotNull(firstRow.get("person"), "person column should exist");
                assertNotNull(firstRow.get("ancestor"), "ancestor column should exist");

                return null;
            });
    }
}
