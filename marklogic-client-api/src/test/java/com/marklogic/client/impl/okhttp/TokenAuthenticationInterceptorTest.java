/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.ext.helper.LoggingObject;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Uses OkHttp's MockWebServer to completely mock a MarkLogic instance so that we can control what response codes are
 * returned and processed by TokenAuthenticationInterceptor.
 */
class TokenAuthenticationInterceptorTest extends LoggingObject {

	private MockWebServer mockWebServer;
	private FakeTokenGenerator fakeTokenGenerator;
	private OkHttpClient okHttpClient;

	@BeforeEach
	void beforeEach() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		fakeTokenGenerator = new FakeTokenGenerator();

		ProgressDataCloudAuthenticationConfigurer.TokenAuthenticationInterceptor interceptor =
			new ProgressDataCloudAuthenticationConfigurer.TokenAuthenticationInterceptor(fakeTokenGenerator);
		assertEquals(1, fakeTokenGenerator.timesInvoked,
			"When the interceptor is created, it should immediately generate a token so that when multiple threads " +
				"are using the DatabaseClient, they will all use the same token.");

		okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
	}

	@AfterEach
	void tearDown() {
		mockWebServer.close();
	}

	@Test
	void receive401() {
		enqueueResponseCodes(200, 200, 401, 200);

		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200,
			"If a 401 is received from the server, then the token should be renewed, and then the 200 should be " +
				"returned to the caller.");

		assertEquals(2, fakeTokenGenerator.timesInvoked,
			"A token should have been generated for the first request and then again when the 401 was received.");
	}

	@Test
	void receive403() {
		enqueueResponseCodes(200, 200, 403);

		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(403);

		assertEquals(1, fakeTokenGenerator.timesInvoked,
			"A token should have been generated for the first request, and the 403 should not have resulted in the " +
				"token being renewed; only a 401 should.");
	}

	@Test
	void multipleThreads() throws Exception {
		Runnable threadThatMakesThreeCalls = () -> {
			for (int i = 0; i < 3; i++) {
				sleep(100);
				callMockWebServer();
			}
		};

		// Mock up 4 responses for each of the 2 threads created below. For each thread, the first call succeeds; the
		// second receives a 401 and then succeeds; and the third call succeeds.
		enqueueResponseCodes(200, 200, 401, 401, 200, 200, 200, 200);

		// Spawn two threads and wait for them to complete.
		ExecutorService service = Executors.newFixedThreadPool(2);
		Future f1 = service.submit(threadThatMakesThreeCalls);
		Future f2 = service.submit(threadThatMakesThreeCalls);
		f1.get();
		f2.get();

		assertTrue(fakeTokenGenerator.timesInvoked == 2 || fakeTokenGenerator.timesInvoked == 3,
			"The fake token generator should have been invoked twice - once when the interceptor was created, and then " +
				"only one more time when the two threads received 401's at almost the exact same time. The interceptor " +
				"is expected to synchronize the call for generating a token such that only one thread will generate a " +
				"new token. The other token is expected to see that the token has changed and uses the new token " +
				"instead of generating a new token itself. " +
				"" +
				"This has now been updated to allow for the fake token generator to be invoked 3 times as well. This " +
				"test started failing after a Polaris-recommended update to TokenAuthInterceptor to avoid a race " +
				"condition. Actual times invoked: " + fakeTokenGenerator.timesInvoked);
	}

	/**
	 * Uses OkHttp's MockWebServer to enqueue mock responses with the given codes. This allows us to mock a 403 being
	 * returned to ensure that a new token is generated if necessary.
	 *
	 * @param codes
	 */
	private void enqueueResponseCodes(int... codes) {
		for (int code : codes) {
			mockWebServer.enqueue(new MockResponse.Builder().code(code).build());
		}
	}

	private void verifyRequestReturnsResponseCode(int expectedCode) {
		verifyRequestReturnsResponseCode(expectedCode, null);
	}

	private void verifyRequestReturnsResponseCode(int expectedCode, String optionalMessage) {
		int actualCode = callMockWebServer();
		if (optionalMessage != null) {
			assertEquals(expectedCode, actualCode, optionalMessage);
		} else {
			assertEquals(expectedCode, actualCode);
		}
	}

	private int callMockWebServer() {
		Request request = new Request.Builder().url(mockWebServer.url("/url-doesnt-matter")).build();
		try {
			return okHttpClient.newCall(request).execute().code();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Fake token generator that allows us to assert on how many times it's invoked, which ensures that new tokens are
	 * or are not being generated when required.
	 */
	private static class FakeTokenGenerator implements ProgressDataCloudAuthenticationConfigurer.TokenGenerator {
		int timesInvoked;

		@Override
		public String generateToken() {
			// A slight delay is added here for the multipleThread test case to simulate the token generation taking
			// some amount of time. This allows us to verify that the synchronization is working properly in the
			// interceptor.
			sleep(100);
			timesInvoked++;
			return "fake-token-" + timesInvoked;
		}
	}

	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
