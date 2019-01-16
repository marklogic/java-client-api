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
package com.marklogic.client.datamovement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * <p>Reports on progress as batches are processed by sending an instance of the nested ProgressUpdate interface to
 * instances of java.util.function.Consumer.</p>
 *
 * As an example, one approach would be to log the progress as a string:
 *
 * <pre>{@code
 *      QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
 *         .withConsistentSnapshot()
 *         .onUrisReady(
 *           new ProgressListener()
 *             .onProgressUpdate(progressUpdate -> {
 *               System.out.println(progressUpdate.getProgressAsString());
 *             })
 *         );
 *
 *     JobTicket ticket = moveMgr.startJob(queryBatcher);
 *     queryBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 * }</pre>
 *
 * The method {@link #withTotalResults(long) withTotalResults} can be used to inform the listener of the total number
 * of expected results; this would likely have been determined by first using a QueryManager with a page length of zero
 * to determine the number of results for a query. Given the total results, each ProgressUpdate object will be aware of
 * how close to complete the QueryBatcher is.
 */
public class ProgressListener implements QueryBatchListener {

	private static Logger logger = LoggerFactory.getLogger(ProgressListener.class);

	private List<Consumer<ProgressUpdate>> consumers = new ArrayList<>();
	private AtomicLong resultsSoFar = new AtomicLong(0);
	private long startTime;
	private long totalResults;

	public ProgressListener() {
	}

	/**
	 * Use this constructor for when the total number of results isn't known ahead of time.
	 *
	 * @param consumers
	 */
	public ProgressListener(Consumer<ProgressUpdate>... consumers) {
		this(0, consumers);
	}

	/**
	 * Use this constructor for when the total number of results is known ahead of time. The ProgressUpdate objects that
	 * are emitted will have more interesting information as a result.
	 *
	 * @param consumers
	 * @param totalResults
	 */
	public ProgressListener(long totalResults, Consumer<ProgressUpdate>... consumers) {
		this.totalResults = totalResults;
		for (Consumer<ProgressUpdate> consumer : consumers) {
			this.consumers.add(consumer);
		}
	}

	public ProgressListener withTotalResults(long totalResults) {
		this.totalResults = totalResults;
		return this;
	}

	public ProgressListener onProgressUpdate(Consumer<ProgressUpdate> consumer) {
		this.consumers.add(consumer);
		return this;
	}

	/**
	 * Initializes the start time so that each ProgressUpdate knows how long it occurred after the job was started.
	 *
	 * @param queryBatcher
	 */
	@Override
	public void initializeListener(QueryBatcher queryBatcher) {
		startTime = System.currentTimeMillis();
	}

	/**
	 * Batches arrive in random order, so a ProgressUpdate is created and sent to each Consumer only if the
	 * value of "getJobResultsSoFar" on the QueryBatch exceeds the number of results seen so far.
	 * <p>
	 * For example, if there are 2 batches, and batch 2 is processed first by this listener followed by batch 1, a
	 * ProgressUpdate is only created when batch 2 is processed.
	 *
	 * @param batch
	 */
	@Override
	public void processEvent(QueryBatch batch) {
		// resultsSoFar is an AtomicLong so it can be safely updated across many threads
		final long jobResultsSoFar = batch.getJobResultsSoFar();
		final long newResultsSoFar = this.resultsSoFar.updateAndGet(operand ->
			jobResultsSoFar > operand ? jobResultsSoFar : operand
		);
		boolean resultsSoFarWasUpdated = jobResultsSoFar == newResultsSoFar;

		if (resultsSoFarWasUpdated && consumers != null) {
			double timeSoFar = ((double) System.currentTimeMillis() - startTime) / 1000;

			/**
			 * The initial totalResults may have been incorrectly set to a value lower than jobResultsSoFar; if this
			 * occurs, use jobResultsSoFar as the value passed to the ProgressUpdate object. totalResults is not
			 * updated though in case there's a need to know what the initial value was.
			 */
			long totalForThisUpdate = jobResultsSoFar > this.totalResults && this.totalResults > 0 ? jobResultsSoFar : this.totalResults;

			ProgressUpdate progressUpdate = newProgressUpdate(batch, startTime, totalForThisUpdate, timeSoFar);

			for (Consumer<ProgressUpdate> consumer : consumers) {
				invokeConsumer(consumer, progressUpdate);
			}
		}
	}

	/**
	 * A subclass can override this to provide a different implementation of ProgressUpdate.
	 *
	 * @param batch
	 * @param startTime
	 * @param totalForThisUpdate
	 * @param timeSoFar
	 * @return
	 */
	protected ProgressUpdate newProgressUpdate(QueryBatch batch, long startTime, long totalForThisUpdate, double timeSoFar) {
		return new SimpleProgressUpdate(batch, startTime, totalForThisUpdate, timeSoFar);
	}

	/**
	 * Protected so that a subclass can override how a consumer is invoked, particularly how an exception is handled.
	 *
	 * @param consumer
	 * @param progressUpdate
	 */
	protected void invokeConsumer(Consumer<ProgressUpdate> consumer, ProgressUpdate progressUpdate) {
		try {
			consumer.accept(progressUpdate);
		} catch (Throwable t) {
			logger.error("Exception thrown by a Consumer<ProgressUpdate> consumer: " + consumer + "; progressUpdate: " + progressUpdate, t);
		}
	}

	/**
	 * Captures data of interest for a progress update.
	 */
	public interface ProgressUpdate {

		String getProgressAsString();

		boolean isComplete();

		QueryBatch getQueryBatch();

		long getStartTime();

		long getTotalResults();

		double getTimeSoFarInSeconds();
	}

	/**
	 * Simple implementation of ProgressUpdate; only real thing of interest in here is how it generates the progress
	 * as a string for display purposes.
	 */
	public static class SimpleProgressUpdate implements ProgressUpdate {

		private QueryBatch queryBatch;
		private long startTime;
		private long totalResults;
		private double timeSoFarInSeconds;

		public SimpleProgressUpdate(QueryBatch queryBatch, long startTime, long totalResults, double timeSoFarInSeconds) {
			this.queryBatch = queryBatch;
			this.startTime = startTime;
			this.timeSoFarInSeconds = timeSoFarInSeconds;
			this.totalResults = totalResults;
		}

		@Override
		public String getProgressAsString() {
			String text;
			if (totalResults > 0) {
				text = String.format("Progress: %d of %d; time %fs", queryBatch.getJobResultsSoFar(), totalResults, timeSoFarInSeconds);
			}
			else {
				text = String.format("Progress: %d results so far; time %fs", queryBatch.getJobResultsSoFar(), timeSoFarInSeconds);
			}

			if (timeSoFarInSeconds > 0) {
				double rate = queryBatch.getJobResultsSoFar() / timeSoFarInSeconds;
				BigDecimal bd = new BigDecimal(rate);
				rate = bd.round(new MathContext(5)).doubleValue();
				return text + "; " + rate + " records/s";
			}

			return text;
		}

		@Override
		public boolean isComplete() {
			return totalResults > 0 ? queryBatch.getJobResultsSoFar() >= totalResults : false;
		}

		@Override
		public QueryBatch getQueryBatch() {
			return queryBatch;
		}

		@Override
		public long getStartTime() {
			return startTime;
		}

		@Override
		public long getTotalResults() {
			return totalResults;
		}

		@Override
		public double getTimeSoFarInSeconds() {
			return timeSoFarInSeconds;
		}
	}
}
