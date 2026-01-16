/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplyInvalidExclusionsToIncrementalWriteTest extends AbstractIncrementalWriteTest {

	/**
	 * Verifies that an invalid JSON Pointer expression (missing leading slash) causes the build to fail
	 * immediately, allowing the user to fix the configuration before any documents are processed.
	 */
	@Test
	void invalidJsonPointerExpression() {
		IncrementalWriteFilter.Builder builder = IncrementalWriteFilter.newBuilder()
			.jsonExclusions("timestamp"); // Invalid - missing leading slash

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, builder::build);

		assertTrue(ex.getMessage().contains("Invalid JSON Pointer expression 'timestamp'"),
			"Error message should include the invalid expression. Actual: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("incremental write"),
			"Error message should mention incremental write context. Actual: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("must start with '/'"),
			"Error message should hint at the fix. Actual: " + ex.getMessage());
	}

	/**
	 * Verifies that an empty JSON Pointer expression is rejected since it would exclude the entire document,
	 * leaving nothing to hash.
	 */
	@Test
	void emptyJsonPointerExpression() {
		IncrementalWriteFilter.Builder builder = IncrementalWriteFilter.newBuilder()
			.jsonExclusions(""); // Invalid - would exclude entire document

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, builder::build);

		assertTrue(ex.getMessage().contains("Empty JSON Pointer expression"),
			"Error message should indicate empty expression. Actual: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("would exclude the entire document"),
			"Error message should explain why it's invalid. Actual: " + ex.getMessage());
	}

	/**
	 * Verifies that an invalid XPath expression causes the build to fail immediately,
	 * allowing the user to fix the configuration before any documents are processed.
	 */
	@Test
	void invalidXPathExpression() {
		IncrementalWriteFilter.Builder builder = IncrementalWriteFilter.newBuilder()
			.xmlExclusions("[[[invalid xpath"); // Invalid XPath syntax

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, builder::build);

		assertTrue(ex.getMessage().contains("Invalid XPath expression '[[[invalid xpath'"),
			"Error message should include the invalid expression. Actual: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("incremental write"),
			"Error message should mention incremental write context. Actual: " + ex.getMessage());
	}

}
