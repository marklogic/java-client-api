/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicIOException;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import mockwebserver3.SocketEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uses a mock server to verify how a Data Services call with a non-resendable streaming part (e.g. a Reader) is sent.
 * A real MarkLogic instance can close a pooled connection after a response, such as a compressed rows response. The
 * next streaming request would then fail on the dead connection, and OkHttp would try to resend the already-consumed
 * stream, resulting in a misleading "Stream closed" error.
 */
class StreamingPartRequestTest {

	private MockWebServer mockWebServer;
	private DatabaseClient client;

	@BeforeEach
	void beforeEach() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		client = DatabaseClientFactory.newClient("localhost", mockWebServer.getPort(),
			new DatabaseClientFactory.BasicAuthContext("user", "password"));
	}

	@AfterEach
	void afterEach() {
		client.release();
		mockWebServer.close();
	}

	@Test
	void serverIsPingedBeforeEveryStreamingRequest() throws Exception {
		for (int i = 0; i < 2; i++) {
			mockWebServer.enqueue(new MockResponse.Builder().code(200).build());
			mockWebServer.enqueue(new MockResponse.Builder().code(200).build());
		}

		callWithStreamingPart();
		callWithStreamingPart();

		assertEquals("HEAD", mockWebServer.takeRequest().getMethod());
		assertEquals("POST", mockWebServer.takeRequest().getMethod());
		assertEquals("HEAD", mockWebServer.takeRequest().getMethod(),
			"The ping must not be limited to the first request, as a connection used by an earlier request may " +
				"have been closed by the server.");
		RecordedRequest secondPost = mockWebServer.takeRequest();
		assertEquals("POST", secondPost.getMethod());
		assertTrue(secondPost.getBody().utf8().contains("{\"a\":1}"));
	}

	@Test
	void failedStreamingRequestIsNotResent() {
		mockWebServer.enqueue(new MockResponse.Builder().code(200).build());
		mockWebServer.enqueue(new MockResponse.Builder()
			.onResponseStart(new SocketEffect.CloseSocket())
			.build());
		// Would be consumed if the streaming request were resent.
		mockWebServer.enqueue(new MockResponse.Builder().code(200).build());

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, this::callWithStreamingPart);

		assertFalse(ex.getMessage().contains("Stream closed"),
			"The stream cannot be resent, so the original failure should be reported instead of an attempt to " +
				"read the consumed stream; message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("unexpected end of stream"), "Unexpected message: " + ex.getMessage());
		assertEquals(2, mockWebServer.getRequestCount());
	}

	private void callWithStreamingPart() {
		BaseProxy.request("/ds/custom-content/", "importContent.xqy", BaseProxy.ParameterValuesKind.MULTIPLE_NODES)
			.on(client)
			.withSession()
			.withParams(BaseProxy.documentParam("content", false,
				Stream.of(new StringReader("{\"a\":1}")).map(BaseProxy.JsonDocumentType::fromReader)))
			.responseNone();
	}
}
