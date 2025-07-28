package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class AwaitCompletionTest {

	@Test
	void test() throws Exception {
		DataMovementManager dmm = Common.newClient().newDataMovementManager();
		AtomicBoolean listenerCompleted = new AtomicBoolean(false);
		WriteBatcher writeBatcher = dmm.newWriteBatcher().withBatchSize(1).onBatchSuccess(batch -> {
			try {
				// Intended to last longer than the duration passed to writeBacher.awaitCompletion.
				Thread.sleep(10000);
				listenerCompleted.set(true);
			} catch (InterruptedException e) {
				listenerCompleted.set(false);
			}
		});
		dmm.startJob(writeBatcher);

		writeBatcher.add("/0doesnt-matter.xml", new DocumentMetadataHandle()
				.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE),
			new StringHandle("<test/>"));
		writeBatcher.flushAsync();
		writeBatcher.awaitCompletion(2, TimeUnit.SECONDS);
		dmm.stopJob(writeBatcher);

		assertFalse(listenerCompleted.get(), "The batcher should have waited 2 seconds for the batch listener to " +
			"completed, which should not occur since the listener is sleeping for 10 seconds. This ensures that a bug " +
			"is fixed where the duration passed to 'awaitCompletion' was mishandled and always resulted in a " +
			"duration of 0 seconds, which means 'wait until all batches are completed'.");
	}
}
