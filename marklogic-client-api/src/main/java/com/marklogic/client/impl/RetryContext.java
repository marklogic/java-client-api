/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.FailedRetryException;
import org.slf4j.Logger;

import java.util.Set;

/**
 * Helper class to consolidate retry logic across multiple HTTP operations.
 * Tracks retry state, calculates delays, handles sleeping, and logs retry attempts.
 */
class RetryContext {
	private final Logger logger;
	private final Set<Integer> retryableStatusCodes;
	private final Runnable onMaxRetriesCallback;

	private int retry = 0;
	private final long startTime = System.currentTimeMillis();
	private int nextDelay = 0;

	/**
	 * @param logger                Logger for debug output
	 * @param retryableStatusCodes  Set of HTTP status codes that trigger retries
	 * @param onMaxRetriesCallback  Callback to invoke when max retries is exceeded (e.g., to reset first request flag)
	 */
	RetryContext(Logger logger, Set<Integer> retryableStatusCodes, Runnable onMaxRetriesCallback) {
		this.logger = logger;
		this.retryableStatusCodes = retryableStatusCodes;
		this.onMaxRetriesCallback = onMaxRetriesCallback;
	}

	boolean shouldContinueRetrying(int minAttempts, int maxDelay) {
		return retry < minAttempts || (System.currentTimeMillis() - startTime) < maxDelay;
	}

	void sleepIfNeeded() throws InterruptedException {
		if (nextDelay > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retrying request after {} ms delay (attempt {})", nextDelay, retry);
			}
			Thread.sleep(nextDelay);
		}
	}

	void calculateNextDelay(int retryAfter, int calculatedDelay) {
		nextDelay = Math.max(retryAfter, calculatedDelay);
		if (logger.isDebugEnabled()) {
			logger.debug("Calculated next retry delay: {} ms (retryAfter: {}, calculatedDelay: {})",
				nextDelay, retryAfter, calculatedDelay);
		}
	}

	void throwIfMaxRetriesExceeded(int status) {
		if (retryableStatusCodes.contains(status)) {
			if (onMaxRetriesCallback != null) {
				onMaxRetriesCallback.run();
			}
			throw new FailedRetryException(
				"Service unavailable and maximum retry period elapsed: " +
					((System.currentTimeMillis() - startTime) / 1000) +
					" seconds after " + retry + " retries");
		}
	}

	void incrementRetry() {
		retry++;
	}

	int getRetry() {
		return retry;
	}
}
