/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.fastfunctest.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetMaxBatchesTest extends AbstractFunctionalTest {

	/**
	 * This verifies that setMaxBatches works with an iterator. The feature appears to have been introduced in 5.1.0,
	 * so since then, it's only ever worked for an iterator. It does not work when an actual query is involved.
	 */
	@Test
	void iteratorTest() {
		List<String> results = Collections.synchronizedList(new ArrayList<>());

		List<String> input = new ArrayList<>();
		for (int i = 1; i <= 100; i++) {
			input.add(i + "");
		}

		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher queryBatcher = dmm
			.newQueryBatcher(input.iterator())
			.withThreadCount(4)
			.withBatchSize(10)
			.onUrisReady(batch -> results.addAll(Arrays.asList(batch.getItems())));

		queryBatcher.setMaxBatches(2);
		dmm.startJob(queryBatcher);
		queryBatcher.awaitCompletion();
		dmm.stopJob(queryBatcher);

		assertEquals(20, results.size());
	}
}
