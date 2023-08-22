package com.marklogic.client.fastfunctest.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Getting this test in place to verify that setMaxBatches does not appear to work yet.")
public class SetMaxBatchesTest extends AbstractFunctionalTest {

	@Test
	void test() {
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
}
