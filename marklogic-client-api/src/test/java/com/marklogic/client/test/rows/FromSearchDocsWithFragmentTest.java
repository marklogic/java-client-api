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
 * {@code op.fromSearchDocs()}. All tests require MarkLogic 12.1 or higher because the
 * {@code fragment} option was introduced in MarkLogic 12.1.
 *
 * <p>Note: {@code op.fromSearchDocs()} returns rows with {@code uri} and {@code doc}
 * columns directly — URI assertions are possible for all fragment types without requiring
 * a separate {@code joinDocUri()} call.
 *
 * @see FromSearchWithFragmentTest for equivalent tests using {@code op.fromSearch()}.
 */
@ExtendWith(RequiresML12Dot1.class)
class FromSearchDocsWithFragmentTest extends AbstractFromSearchFragmentTest {

	/**
	 * Test case: Verifies the default {@code fromSearchDocs} behavior when no {@code fragment} option
	 * is specified. On MarkLogic 12.1, the default is to search document fragments only. "duck"
	 * appears in the document content of range-prop-3.json ({@code {"word":"duck"}}), so exactly
	 * one row is returned with {@code uri} equal to range-prop-3.json. Properties and lock fragments
	 * are not searched by default.
	 */
	@Test
	void fromSearchDocsDefaultFragment() {
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(
				op.cts.wordQuery("duck"),
				null,
				null
			)
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-3.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: With persistent locks on the 3 test documents, use {@code fromSearchDocs} with the
	 * {@link PlanSearchOptions.Fragment#LOCKS} option to find a document via its lock holder text.
	 * "rose" appears only in the lock holder for range-prop-1.json ("dog rose"), not in any document
	 * content, so exactly one row is returned and resolves to range-prop-1.json, proving the search
	 * targeted lock fragments.
	 */
	@Test
	void fromSearchDocsWithLocksFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.LOCKS);
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(
				op.cts.locksFragmentQuery(op.cts.wordQuery("rose")),
				null,
				options
			)
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-1.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: "prop2value" appears only in the property of range-prop-2.json, proving the search
	 * is scoped to properties fragments. Exactly one row is returned and resolves to range-prop-2.json.
	 */
	@Test
	void fromSearchDocsWithPropertiesFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.PROPERTIES);
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(
				op.cts.propertiesFragmentQuery(op.cts.wordQuery("prop2value")),
				null,
				options
			)
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-2.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: "duck" appears only in the document content of range-prop-3.json ({@code {"word":"duck"}}),
	 * not in any lock holder or property. With {@link PlanSearchOptions.Fragment#DOCUMENT}, exactly
	 * one document fragment matches and resolves to range-prop-3.json via the {@code uri} column
	 * returned directly by {@code fromSearchDocs}.
	 */
	@Test
	void fromSearchDocsWithDocumentFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.DOCUMENT);
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(
				op.cts.wordQuery("duck"),
				null,
				options
			)
		);
		assertEquals(1, rows.size());
		assertEquals("range-prop-3.json", rows.get(0).getString("uri"));
	}

	/**
	 * Test case: "opticfragmentpropvalue" appears only in the properties fragment of each test
	 * document (not in document content or lock holders). With {@link PlanSearchOptions.Fragment#ANY},
	 * all 3 properties fragments match. Unlike {@code fromSearch()}, {@code fromSearchDocs()} with
	 * {@code ANY} returns multiple rows per matched document (one per fragment type present on that
	 * document). The test asserts that all 3 test document URIs appear among the results.
	 */
	@Test
	void fromSearchDocsWithAnyFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.ANY);
		List<RowRecord> rows = resultRows(
			op.fromSearchDocs(
				op.cts.propertiesFragmentQuery(op.cts.wordQuery("opticfragmentpropvalue")),
				null,
				options
			)
		);
		// fromSearchDocs with ANY returns one row per fragment type per matched document (3 docs × 3 types = 9 rows)
		assertEquals(9, rows.size(), "ANY fragment should return 3 rows per document (document + properties + locks)");
		assertEquals(EXPECTED_URIS, rows.stream().map(r -> r.getString("uri")).distinct().sorted().collect(Collectors.toList()));
	}

	/**
	 * Test case: Verifies that {@code explain()} serialises a plan that uses {@code fromSearchDocs}
	 * with the {@code fragment} option. Calls {@code explain()} on a plan that searches lock fragments
	 * for the word "dog" (matches only range-prop-1.json whose lock holder is "dog rose"). Verifies
	 * that {@code explain()} returns a non-null JSON node, confirming the plan with the
	 * {@code fragment} option can be serialised without error.
	 */
	@Test
	void explainFromSearchDocsWithLocksFragment() {
		PlanSearchOptions options = op.searchOptions().withFragment(PlanSearchOptions.Fragment.LOCKS);

		JacksonHandle explainHandle = rowManager.explain(
			op.fromSearchDocs(
				op.cts.locksFragmentQuery(op.cts.wordQuery("dog")),
				null,
				options
			)
			 .orderBy(op.col("uri"))
			 .select(op.col("uri"), op.col("doc")),
			new JacksonHandle()
		);
		assertNotNull(explainHandle.get(), "explain() must return a non-null plan JSON node");
	}
}
