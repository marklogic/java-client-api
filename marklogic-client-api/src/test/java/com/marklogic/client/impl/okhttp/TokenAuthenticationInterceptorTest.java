package com.marklogic.client.impl.okhttp;

import com.marklogic.client.ext.helper.LoggingObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Uses OkHttp's MockWebServer to completely mock a MarkLogic instance so that we can control what response codes are
 * returned and processed by TokenAuthenticationInterceptor.
 */
public class TokenAuthenticationInterceptorTest extends LoggingObject {

	private MockWebServer mockWebServer;
	private FakeTokenGenerator fakeTokenGenerator;
	private OkHttpClient okHttpClient;

	@BeforeEach
	void beforeEach() {
		mockWebServer = new MockWebServer();
		fakeTokenGenerator = new FakeTokenGenerator();

		MarkLogicCloudAuthenticationConfigurer.TokenAuthenticationInterceptor interceptor =
			new MarkLogicCloudAuthenticationConfigurer.TokenAuthenticationInterceptor(fakeTokenGenerator);
		assertEquals(1, fakeTokenGenerator.timesInvoked,
			"When the interceptor is created, it should immediately generate a token so that when multiple threads " +
				"are using the DatabaseClient, they will all use the same token.");

		okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
	}

	@Test
	void receive403() {
		enqueueResponseCodes(200, 200, 403, 200);

		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200,
			"If a 403 is received from the server, then the token should be renewed, and then the 200 should be " +
				"returned to the caller.");

		assertEquals(2, fakeTokenGenerator.timesInvoked,
			"A token should have been generated for the first request and then again when the 403 was received.");
	}

	@Test
	void receive401() {
		enqueueResponseCodes(200, 200, 401);

		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(200);
		verifyRequestReturnsResponseCode(401);

		assertEquals(1, fakeTokenGenerator.timesInvoked,
			"A token should have been generated for the first request, and the 401 should not have resulted in the " +
				"token being renewed; only a 403 should.");
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
		// second receives a 403 and then succeeds; and the third call succeeds.
		enqueueResponseCodes(200, 200, 403, 403, 200, 200, 200, 200);

		// Spawn two threads and wait for them to complete.
		ExecutorService service = Executors.newFixedThreadPool(2);
		Future f1 = service.submit(threadThatMakesThreeCalls);
		Future f2 = service.submit(threadThatMakesThreeCalls);
		f1.get();
		f2.get();

		assertEquals(2, fakeTokenGenerator.timesInvoked,
			"The fake token generator should have been invoked twice - once when the interceptor was created, and then " +
				"only one more time when the two threads received 403's at almost the exact same time. The interceptor " +
				"is expected to synchronize the call for generating a token such that only one thread will generate a " +
				"new token. The other token is expected to see that the token has changed and uses the new token " +
				"instead of generating a new token itself.");
	}

	/**
	 * Uses OkHttp's MockWebServer to enqueue mock responses with the given codes. This allows us to mock a 403 being
	 * returned to ensure that a new token is generated if necessary.
	 *
	 * @param codes
	 */
	private void enqueueResponseCodes(int... codes) {
		for (int code : codes) {
			mockWebServer.enqueue(new MockResponse().setResponseCode(code));
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
	private static class FakeTokenGenerator implements MarkLogicCloudAuthenticationConfigurer.TokenGenerator {
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
