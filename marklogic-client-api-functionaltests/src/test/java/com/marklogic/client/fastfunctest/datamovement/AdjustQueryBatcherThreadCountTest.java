package com.marklogic.client.fastfunctest.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdjustQueryBatcherThreadCountTest extends AbstractFunctionalTest {

	@Test
	void increaseThreadCount() {
		List<String> uris = writeJsonDocs(20);
		Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());
		AtomicInteger uriCount = new AtomicInteger();

		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher qb = dmm.newQueryBatcher(uris.iterator())
			.withThreadCount(1)
			.withBatchSize(1)
			.onUrisReady(batch -> {
				waitFor(50);
				threadNames.add(Thread.currentThread().getName());
				uriCount.addAndGet(batch.getItems().length);
			});

		dmm.startJob(qb);
		waitFor(100);
		qb.withThreadCount(3);
		qb.awaitCompletion();
		dmm.stopJob(qb);

		assertEquals(20, uriCount.get());
		assertEquals(3, threadNames.size(), "3 threads should have processed all the batches, as the thread count " +
			"was increased from 1 to 3 100ms into the job.");
	}

	@Test
	void reduceThreadCount() {
		List<String> uris = writeJsonDocs(20);
		List<String> threadNames = Collections.synchronizedList(new ArrayList<>());

		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher qb = dmm.newQueryBatcher(uris.iterator())
			.withThreadCount(4)
			.withBatchSize(1)
			.onUrisReady(batch -> {
				waitFor(50);
				threadNames.add(Thread.currentThread().getName());
			});

		dmm.startJob(qb);
		waitFor(100);
		qb.withThreadCount(2);
		qb.awaitCompletion();
		dmm.stopJob(qb);

		assertEquals(20, threadNames.size(), "With 20 docs and a batch size of 1, the onUrisReady listener should " +
			"have been called 20 times and thus captured 20 names.");

		Set<String> lastEightThreadNames = new HashSet<>(threadNames.subList(12, 19));
		assertEquals(2, lastEightThreadNames.size(), "Since the thread count was reduced from 4 to 2 100ms into the " +
			"job, then we can assume that 8 batches of size 1 were processed by 4 threads during those first 100ms, " +
			"as there's a 50ms pause in the onUrisReady listener. The thread count would have been reduced to 2. In " +
			"theory, the last 12 batches should have only been processed by those 2 threads. But just to be safe, " +
			"we verify that the last 8 batches were only processed by 2 threads in case it took the thread pool a " +
			"little bit of time to switch from 4 to 2 threads.");
	}

	@Test
	void setThreadCountToOneAndThenHigher() {
		List<String> uris = writeJsonDocs(20);
		AtomicInteger uriCount = new AtomicInteger();

		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher qb = dmm.newQueryBatcher(uris.iterator())
			.withThreadCount(4)
			.withBatchSize(1)
			.onUrisReady(batch -> {
				waitFor(50);
				uriCount.addAndGet(batch.getItems().length);
			});

		dmm.startJob(qb);
		waitFor(100);
		qb.withThreadCount(1);
		qb.withThreadCount(8);
		qb.awaitCompletion();
		dmm.stopJob(qb);

		assertEquals(20, uriCount.get(), "The purpose of this test is to verify that if the thread count is set to 1, " +
			"the thread pool doesn't stop or throw an error. It may pause execution (for reasons that aren't known), " +
			"but testing has shown that increasing it to a value greater than 1 will cause execution to resume if it " +
			"was in fact paused.");
	}

	@Test
	void setThreadCountToZero() {
		List<String> uris = writeJsonDocs(20);
		AtomicInteger uriCount = new AtomicInteger();

		DataMovementManager dmm = client.newDataMovementManager();
		QueryBatcher qb = dmm.newQueryBatcher(uris.iterator())
			.withThreadCount(4)
			.withBatchSize(1)
			.onUrisReady(batch -> {
				waitFor(50);
				uriCount.addAndGet(batch.getItems().length);
			});

		dmm.startJob(qb);
		waitFor(100);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> qb.withThreadCount(0));
		assertEquals("threadCount must be 1 or greater", ex.getMessage());
		assertEquals(4, qb.getThreadCount(), "The thread count should not have been adjusted since the input was invalid");

		qb.awaitCompletion();
		dmm.stopJob(qb);

		assertEquals(20, uriCount.get(), "All 20 URIs should still have been retrieved");
	}
}
