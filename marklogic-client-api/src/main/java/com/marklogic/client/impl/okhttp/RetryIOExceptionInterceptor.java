/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Experimental interceptor added in 8.0.0 for retrying requests that fail due to connection issues. These issues are
 * not handled by the application-level retry support in OkHttpServices, which only handles retries based on certain
 * HTTP status codes. The main limitation of this approach is that it cannot retry a request that has a one-shot body,
 * such as a streaming body. But for requests that don't have one-shot bodies, this interceptor can be helpful for
 * retrying requests that fail due to temporary network issues or MarkLogic restarts.
 */
public class RetryIOExceptionInterceptor implements Interceptor {

	private final static Logger logger = org.slf4j.LoggerFactory.getLogger(RetryIOExceptionInterceptor.class);

	private final int maxRetries;
	private final long initialDelayMs;
	private final double backoffMultiplier;
	private final long maxDelayMs;

	public RetryIOExceptionInterceptor(int maxRetries, long initialDelayMs, double backoffMultiplier, long maxDelayMs) {
		this.maxRetries = maxRetries;
		this.initialDelayMs = initialDelayMs;
		this.backoffMultiplier = backoffMultiplier;
		this.maxDelayMs = maxDelayMs;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();

		if (request.body() instanceof RetryableRequestBody body && !body.isRetryable()) {
			return chain.proceed(request);
		}

		for (int attempt = 0; attempt <= maxRetries; attempt++) {
			try {
				return chain.proceed(request);
			} catch (IOException e) {
				if (attempt == maxRetries || !isRetryableIOException(e)) {
					logger.warn("Not retryable: {}; {}", e.getClass(), e.getMessage());
					throw e;
				}

				long delay = calculateDelay(attempt);
				logger.warn("Request to {} failed (attempt {}/{}): {}. Retrying in {}ms",
					request.url(), attempt + 1, maxRetries, e.getMessage(), delay);

				sleep(delay);
			}
		}

		// This should never be reached due to loop logic, but is required for compilation.
		throw new IllegalStateException("Unexpected end of retry loop");
	}

	private boolean isRetryableIOException(IOException e) {
		return e instanceof ConnectException ||
			e instanceof SocketTimeoutException ||
			e instanceof UnknownHostException ||
			(e.getMessage() != null && (
				e.getMessage().contains("Failed to connect") ||
					e.getMessage().contains("unexpected end of stream") ||
					e.getMessage().contains("Connection reset") ||
					e.getMessage().contains("Read timed out") ||
					e.getMessage().contains("Broken pipe")
			));
	}

	private long calculateDelay(int attempt) {
		long delay = (long) (initialDelayMs * Math.pow(backoffMultiplier, attempt));
		return Math.min(delay, maxDelayMs);
	}

	private void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
			logger.warn("Ignoring InterruptedException while sleeping for retry delay: {}", ie.getMessage());
		}
	}
}
