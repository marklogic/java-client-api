/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.test.Common;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the retry logic in OkHttpServices using a custom interceptor that simulates server errors. This is not a
 * well-documented feature but users are currently able to discover it via the codebase.
 */
class RetryOn50XResponseTest {

	/**
	 * Custom interceptor that returns 502 Bad Gateway responses and counts how many times it's invoked.
	 */
	private static class BadGatewayInterceptor implements Interceptor {

		private final AtomicInteger invocationCount = new AtomicInteger(0);
		private final int failureCount;

		/**
		 * @param failureCount Number of times to return 502 before allowing the request through
		 */
		public BadGatewayInterceptor(int failureCount) {
			this.failureCount = failureCount;
		}

		@Override
		public Response intercept(Chain chain) throws IOException {
			int count = invocationCount.incrementAndGet();
			Request request = chain.request();

			// Fail the first N requests
			if (count <= failureCount) {
				return new Response.Builder()
					.request(request)
					.protocol(Protocol.HTTP_1_1)
					.code(502)
					.message("Bad Gateway")
					.body(okhttp3.ResponseBody.create("Simulated 502 error", null))
					.build();
			}

			// After N failures, let the request through
			return chain.proceed(request);
		}

		public int getInvocationCount() {
			return invocationCount.get();
		}

		public void reset() {
			invocationCount.set(0);
		}
	}

	@BeforeEach
	void setUp() {
		// Configure very short retry delays for testing
		System.setProperty(RESTServices.MAX_DELAY_PROP, "3");
		System.setProperty(RESTServices.MIN_RETRY_PROP, "2");

		// "Touch" the Common class to trigger the static block that removes existing configurators on
		// DatabaseClientFactory.
		Common.newServerPayload();
	}

	@AfterEach
	void tearDown() {
		DatabaseClientFactory.removeConfigurators();
		System.clearProperty(RESTServices.MAX_DELAY_PROP);
		System.clearProperty(RESTServices.MIN_RETRY_PROP);
	}

	@Test
	void testRetryWith502Responses() {
		// Create an interceptor that will fail 2 times, then succeed
		BadGatewayInterceptor interceptor = new BadGatewayInterceptor(2);

		DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) builder ->
			builder.addInterceptor(interceptor));

		try (DatabaseClient client = Common.newClient()) {
			client.checkConnection();
			assertEquals(3, interceptor.getInvocationCount(),
				"Expected 3 invocations: 2 failures followed by 1 success");
		}
	}

	@Test
	void testRetryExceedsMaxAttempts() {
		// Create an interceptor that will fail 10 times (more than minRetry)
		BadGatewayInterceptor interceptor = new BadGatewayInterceptor(10);

		DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) builder ->
			builder.addInterceptor(interceptor));

		try (DatabaseClient client = Common.newClient()) {
			assertThrows(FailedRequestException.class, () -> {
				client.checkConnection();
			}, "Expected FailedRequestException after exhausting retries");

			assertTrue(interceptor.getInvocationCount() >= 3,
				"Expected at least 3 retry attempts, but got " + interceptor.getInvocationCount());
		}
	}

	@Test
	void testRetryCountIncreases() {
		// Test that retry attempts increase as expected
		BadGatewayInterceptor interceptor = new BadGatewayInterceptor(1);

		DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) builder ->
			builder.addInterceptor(interceptor));

		try (DatabaseClient client = Common.newClient()) {
			// First request: fails once, then succeeds
			client.checkConnection();
			assertEquals(2, interceptor.getInvocationCount(), "Expected 2 invocations for first request");

			// Reset and try again
			interceptor.reset();
			client.checkConnection();
			assertEquals(2, interceptor.getInvocationCount(), "Expected 2 invocations for second request");
		}
	}

	@Test
	void testNoRetryOnSuccessfulRequest() {
		// Interceptor that never fails
		BadGatewayInterceptor interceptor = new BadGatewayInterceptor(0);

		DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) builder ->
			builder.addInterceptor(interceptor));

		try (DatabaseClient client = Common.newClient()) {
			client.checkConnection();
			assertEquals(1, interceptor.getInvocationCount(),
				"Expected exactly 1 invocation when request succeeds immediately");
		}
	}
}
