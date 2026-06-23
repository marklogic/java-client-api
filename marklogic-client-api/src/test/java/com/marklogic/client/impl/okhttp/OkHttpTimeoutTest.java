/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.impl.OkHttpServices;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies that read and write timeouts configured via {@link OkHttpServices.ConnectionConfig} are applied to the
 * underlying {@link OkHttpClient}.
 */
class OkHttpTimeoutTest {

	private MockWebServer mockWebServer;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterEach
	void tearDown() {
		mockWebServer.close();
	}

	/**
	 * Verifies that a configured read timeout actually fires when the server stalls before sending
	 * any response. This is an end-to-end behavioral test: if the timeout were not wired to the
	 * OkHttpClient, the call would block indefinitely rather than throwing a SocketTimeoutException.
	 */
	@Test
	void readTimeoutFiresWhenServerStalls() throws Exception {
		// Use a very short read timeout (100 ms) to keep the test fast.
		DatabaseClientFactory.DigestAuthContext securityContext =
			new DatabaseClientFactory.DigestAuthContext("user", "password");
		OkHttpServices.ConnectionConfig config = new OkHttpServices.ConnectionConfig(
			mockWebServer.getHostName(), mockWebServer.getPort(), null, null,
			securityContext, Collections.emptyList(), 100L, 0L);

		OkHttpServices services = new OkHttpServices(config);

		OkHttpClient okHttpClient = services.getClientImplementation();

		// Server stalls for 5 seconds before sending headers. The read timeout is 100 ms, so the
		// test completes in ~100 ms when passing. The 50× margin eliminates the risk of a GC pause
		// causing MockWebServer to respond before the timeout fires.
		mockWebServer.enqueue(new MockResponse.Builder()
			.headersDelay(5_000, TimeUnit.MILLISECONDS)
			.code(200)
			.body("ok")
			.build());

		Request request = new Request.Builder()
			.url(mockWebServer.url("/"))
			.build();

		// The read timeout must fire, producing a SocketTimeoutException (subtype of IOException).
		assertThrows(SocketTimeoutException.class,
			() -> okHttpClient.newCall(request).execute().close(),
			"Expected a SocketTimeoutException when the server does not respond within the configured timeout");
	}

	@Test
	void readAndWriteTimeoutsAreApplied() throws Exception {
		DatabaseClientFactory.DigestAuthContext securityContext =
			new DatabaseClientFactory.DigestAuthContext("user", "password");

		OkHttpServices.ConnectionConfig config = new OkHttpServices.ConnectionConfig(
			mockWebServer.getHostName(), mockWebServer.getPort(), null, null,
			securityContext, Collections.emptyList(),
			TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(2));

		OkHttpServices services = new OkHttpServices(config);

		OkHttpClient okHttpClient = services.getClientImplementation();

		assertEquals(30_000, okHttpClient.readTimeoutMillis(),
			"Read timeout should be 30 seconds expressed in milliseconds");
		assertEquals(120_000, okHttpClient.writeTimeoutMillis(),
			"Write timeout should be 2 minutes expressed in milliseconds");
	}

	@Test
	void defaultTimeoutsRemainZero() throws Exception {
		DatabaseClientFactory.DigestAuthContext securityContext =
			new DatabaseClientFactory.DigestAuthContext("user", "password");

		OkHttpServices.ConnectionConfig config = new OkHttpServices.ConnectionConfig(
			mockWebServer.getHostName(), mockWebServer.getPort(), null, null,
			securityContext, Collections.emptyList(), 0L, 0L);

		OkHttpServices services = new OkHttpServices(config);

		OkHttpClient okHttpClient = services.getClientImplementation();

		assertEquals(0, okHttpClient.readTimeoutMillis(),
			"Default read timeout should be 0 (no timeout), preserving existing behaviour");
		assertEquals(0, okHttpClient.writeTimeoutMillis(),
			"Default write timeout should be 0 (no timeout), preserving existing behaviour");
	}
}
