/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.junit5.RequiresML12Dot1;
import com.marklogic.client.type.PlanSearchOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link PlanSearchOptions.Fragment} option added by MLE-28334, using
 * {@code op.fromSearch()}. All tests require MarkLogic 12.1 or higher because the
 * {@code fragment} option was introduced in MarkLogic 12.1.
 *
 * <p>Note: {@code op.fromSearch()} returns rows with {@code fragmentId} and {@code score}
 * columns only — there is no {@code uri} column. {@code joinDocUri()} can resolve document
 * fragment IDs to URIs, but does not support lock or properties fragment IDs on MarkLogic 12.1.
 *
 * @see FromSearchDocsWithFragmentTest for equivalent tests using {@code op.fromSearchDocs()}.
 */
@ExtendWith(RequiresML12Dot1.class)
class FromSearchWithFragmentTest extends AbstractFromSearchFragmentTest {

	/**
	 * Test case: Verifies the default {@code fromSearch} behavior when no {@code fragment} option
	 * is specified. On MarkLogic 12.1, the default is to search document fragments only. "duck"
	 * appears in the document content of range-prop-3.json ({@code {"word":"duck"}}), so exactly
	 * one row is returned. Properties and lock fragments are not searched by default.
	 */
	@Test
	void fromSearchDefaultFragment() {
		List<RowRecord> rows = resultRows(
			op.fromSearch(
				op.cts.wordQuery("duck"),
				null, null, null
			).joinDocUri(op.col("uri"), op.fragmentIdCol("fragmentId"))
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-3.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: With persistent locks on the 3 test documents, use {@code fromSearch} with the
	 * {@link PlanSearchOptions.Fragment#LOCKS} option to find a document via its lock holder text.
	 * "rose" appears only in the lock holder for range-prop-1.json ("dog rose"), not in any document
	 * content, so exactly one row is returned, proving the search targeted lock fragments.
	 * {@code fromSearch} returns only {@code fragmentId} and {@code score} columns; URI resolution
	 * via {@code joinDocUri} is not supported for lock fragment IDs on MarkLogic 12.1.
	 */
	@Test
	void fromSearchWithLocksFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.LOCKS);
		List<RowRecord> rows = resultRows(
			op.fromSearch(
				op.cts.locksFragmentQuery(op.cts.wordQuery("rose")),
				null, null, options
			)
		);
		assertEquals(1, rows.size());
		assertNotNull(rows.get(0).get("score"),
			"score column must be present in fromSearch() results");
	}

	/**
	 * Test case: "prop2value" appears only in the property of range-prop-2.json, proving the search
	 * is scoped to properties fragments. Exactly one row is returned. {@code fromSearch} returns
	 * only {@code fragmentId} and {@code score} columns; URI resolution via {@code joinDocUri} is
	 * not supported for properties fragment IDs on MarkLogic 12.1.
	 */
	@Test
	void fromSearchWithPropertiesFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.PROPERTIES);
		List<RowRecord> rows = resultRows(
			op.fromSearch(
				op.cts.propertiesFragmentQuery(op.cts.wordQuery("prop2value")),
				null, null, options
			)
		);
		assertEquals(1, rows.size());
		assertNotNull(rows.get(0).get("score"),
			"score column must be present in fromSearch() results");
	}

	/**
	 * Test case: "duck" appears only in the document content of range-prop-3.json ({@code {"word":"duck"}}),
	 * not in any lock holder or property. With {@link PlanSearchOptions.Fragment#DOCUMENT}, exactly
	 * one document fragment matches and resolves to range-prop-3.json via {@code joinDocUri}.
	 */
	@Test
	void fromSearchWithDocumentFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.DOCUMENT);
		List<RowRecord> rows = resultRows(
			op.fromSearch(
				op.cts.wordQuery("duck"),
				null, null, options
			).joinDocUri(op.col("uri"), op.fragmentIdCol("fragmentId"))
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-3.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: "opticfragmentpropvalue" appears only in the properties fragment of each test
	 * document (not in document content or lock holders). With {@link PlanSearchOptions.Fragment#ANY},
	 * all 3 properties fragments match, returning 3 rows with all 3 document URIs.
	 */
	@Test
	void fromSearchWithAnyFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.ANY);
		List<RowRecord> rows = resultRows(
			op.fromSearch(
				op.cts.propertiesFragmentQuery(op.cts.wordQuery("opticfragmentpropvalue")),
				null, null, options
			).joinDocUri(op.col("uri"), op.fragmentIdCol("fragmentId"))
		);
		assertEquals(3, rows.size());
		assertEquals(EXPECTED_URIS, rows.stream().map(r -> r.getString("uri")).sorted().collect(Collectors.toList()));
	}

	/**
	 * Test case: Verifies that {@code explain()} serialises a plan that uses the {@code fragment} option.
	 * Calls {@code explain()} on a plan that searches lock fragments for the word "dog"
	 * (matches only range-prop-1.json whose lock holder is "dog rose") and joins to retrieve
	 * lock document content via {@code joinDocAndUri}. Verifies that {@code explain()} returns
	 * a non-null JSON node, confirming the plan with the {@code fragment} option can be
	 * serialised without error.
	 */
	@Test
	void explainFromSearchWithLocksFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.LOCKS);

		JacksonHandle explainHandle = rowManager.explain(
			op.fromSearch(
				op.cts.locksFragmentQuery(op.cts.wordQuery("dog")),
				null, null, options
			).joinDocAndUri(op.col("doc"), op.col("uri"), op.fragmentIdCol("fragmentId"))
			 .orderBy(op.col("uri"))
			 .select(op.col("uri"), op.col("doc")),
			new JacksonHandle()
		);
		assertNotNull(explainHandle.get(), "explain() must return a non-null plan JSON node");
	}
}
