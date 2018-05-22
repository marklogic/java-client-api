/*
 * Copyright 2015-2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class ProgressListenerTest extends Assert {

	private ProgressListener listener;
	private TestConsumer consumer = new TestConsumer();

	@Test
	public void batchesInOrder() throws Exception {
		listener = new ProgressListener(4, consumer);
		listener.initializeListener(null);
		// Ensure that the total amount of time will be greater than zero for when it's tested below
		Thread.sleep(100);

		listener.processEvent(new FakeQueryBatch(2));
		assertFalse(consumer.lastProgressUpdate.isComplete());

		listener.processEvent(new FakeQueryBatch(4));
		assertTrue(consumer.lastProgressUpdate.isComplete());

		assertTrue(consumer.texts.get(0).startsWith("Progress: 2 of 4; time "));
		assertTrue(consumer.texts.get(0).contains("records/s"));
		assertTrue(consumer.texts.get(1).startsWith("Progress: 4 of 4; time "));
		assertTrue(consumer.texts.get(1).contains("records/s"));
		assertEquals(2, consumer.texts.size());

		// Verify the values on the ProgressUpdate are correct
		assertEquals(4, consumer.lastProgressUpdate.getQueryBatch().getJobResultsSoFar());
		assertEquals(4, consumer.lastProgressUpdate.getTotalResults());
		assertTrue(consumer.lastProgressUpdate.getStartTime() > 0);
		assertTrue(consumer.lastProgressUpdate.getTimeSoFarInSeconds() > 0);
	}

	/**
	 * Just verifies that the class can be configured via the chaining methods instead of the constructor.
	 */
	@Test
	public void configuredViaMethodsInsteadOfConstructor() {
		listener = new ProgressListener().withTotalResults(4).onProgressUpdate(consumer);
		listener.initializeListener(null);
		listener.processEvent(new FakeQueryBatch(2));
		listener.processEvent(new FakeQueryBatch(4));

		assertTrue(consumer.texts.get(0).startsWith("Progress: 2 of 4; time "));
		assertTrue(consumer.texts.get(1).startsWith("Progress: 4 of 4; time "));
		assertEquals(2, consumer.texts.size());
	}

	@Test
	public void batchesOutOfOrder() {
		listener = new ProgressListener(4, consumer);
		listener.initializeListener(null);
		listener.processEvent(new FakeQueryBatch(4));
		listener.processEvent(new FakeQueryBatch(2));

		assertTrue(consumer.texts.get(0).startsWith("Progress: 4 of 4"));
		assertEquals("The second batch is ignored because its jobResultsSoFar value is less than what the listener has seen so far",
			1, consumer.texts.size());
	}

	@Test
	public void noTotalResults() {
		listener = new ProgressListener(consumer);
		listener.initializeListener(null);
		listener.processEvent(new FakeQueryBatch(2));
		listener.processEvent(new FakeQueryBatch(4));

		System.out.println(consumer.texts);
		assertTrue(consumer.texts.get(0).startsWith("Progress: 2 results so far; time "));
		assertTrue(consumer.texts.get(1).startsWith("Progress: 4 results so far; time "));
		assertEquals(2, consumer.texts.size());
	}

	@Test
	public void multipleConsumersAndOneThrowsExceptions() {
		listener = new ProgressListener(new ExceptionThrowingConsumer(), consumer);
		listener.initializeListener(null);
		listener.processEvent(new FakeQueryBatch(2));
		listener.processEvent(new FakeQueryBatch(4));

		assertEquals("Verifying that the TestConsumer still got updates, even though the first consumer kept throwing exceptions",
			2, consumer.texts.size());
	}

	@Test
	public void initialTotalResultsIsTooLow() {
		listener = new ProgressListener(3, consumer);
		listener.initializeListener(null);
		listener.processEvent(new FakeQueryBatch(2));
		listener.processEvent(new FakeQueryBatch(4));

		assertEquals(2, consumer.texts.size());
		assertTrue("On the first batch, the listener doesn't yet know that the total results is too low",
			consumer.texts.get(0).startsWith("Progress: 2 of 3; time "));
		assertTrue("But on the second batch, the listener should realize that the initial total results value was incorrect and adjust it",
			consumer.texts.get(1).startsWith("Progress: 4 of 4; time "));
	}
}

class TestConsumer implements Consumer<ProgressListener.ProgressUpdate> {

	public List<String> texts = new ArrayList<>();
	public ProgressListener.ProgressUpdate lastProgressUpdate;

	@Override
	public void accept(ProgressListener.ProgressUpdate progressUpdate) {
		lastProgressUpdate = progressUpdate;
		texts.add(progressUpdate.getProgressAsString());
	}
}

class ExceptionThrowingConsumer implements Consumer<ProgressListener.ProgressUpdate> {

	@Override
	public void accept(ProgressListener.ProgressUpdate progressUpdate) {
		throw new UnsupportedOperationException("I'm throwing an error to make sure that the listener catches it");
	}
}

class FakeQueryBatch implements QueryBatch {

	private long jobResultsSoFar;

	public FakeQueryBatch(long jobResultsSoFar) {
		this.jobResultsSoFar = jobResultsSoFar;
	}

	@Override
	public long getServerTimestamp() {
		return 0;
	}

	@Override
	public String[] getItems() {
		return new String[0];
	}

	@Override
	public Calendar getTimestamp() {
		return null;
	}

	@Override
	public QueryBatcher getBatcher() {
		return null;
	}

	@Override
	public DatabaseClient getClient() {
		return null;
	}

	@Override
	public long getJobBatchNumber() {
		return 0;
	}

	@Override
	public long getJobResultsSoFar() {
		return jobResultsSoFar;
	}

	@Override
	public long getForestBatchNumber() {
		return 0;
	}

	@Override
	public long getForestResultsSoFar() {
		return 0;
	}

	@Override
	public Forest getForest() {
		return null;
	}

	@Override
	public JobTicket getJobTicket() {
		return null;
	}
}