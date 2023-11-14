package com.marklogic.client.fastfunctest.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetMaxBatchesTest extends AbstractFunctionalTest {

	@Disabled("Getting this test in place to verify that setMaxBatches does not work when used with a query.")
	@Test
	void testWithQuery() {
		writeJsonDocs(50, "max-batches-test");

		DataMovementManager dmm = client.newDataMovementManager();
		AtomicInteger uriCount = new AtomicInteger();
		QueryBatcher queryBatcher = dmm
			.newQueryBatcher(client.newQueryManager().newStructuredQueryBuilder().collection("max-batches-test"))
			.withThreadCount(1)
			.withBatchSize(10)
			.onUrisReady(batch -> uriCount.addAndGet(batch.getItems().length));
		queryBatcher.setMaxBatches(2);
		dmm.startJob(queryBatcher);
		queryBatcher.awaitCompletion();
		dmm.stopJob(queryBatcher);

		assertEquals(20, uriCount.get(), "Because the batch size is 10 and we asked for 2 batches back, we only " +
			"expect 20 URIs back. But through 6.2.2 (and probably going back much further), all URIs are returned. " +
			"Modifying the thread count and batch size do not appear to affect this at all.");
	}

	/**
	 * This verifies that setMaxBatches works with an iterator. The feature appears to have been introduced in 5.1.0,
	 * so since then, it's only ever worked for an iterator. It does not work when an actual query is involved.
	 */
	@Test
	void iteratorTest() {
		List<String> results = new ArrayList<>();

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
