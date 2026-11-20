/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.ReaderHandle;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import mockwebserver3.SocketEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uses a mock server to verify how requests with non-resendable content (e.g. a Reader) are sent. A real MarkLogic
 * instance can drop a pooled connection right after a response, such as a compressed rows response. If a one-shot
 * request is the next one on that connection, it fails and cannot be resent, because its stream has been consumed.
 */
class StreamingPartRequestTest {

	private static final String PAYLOAD = "{\"a\":1}";

	private MockWebServer mockWebServer;
	private final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
	private final AtomicBoolean dropConnectionOnce = new AtomicBoolean();
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
	void streamingRequestSucceedsWhenServerDropsThePooledConnection() {
		// The server answers the first call, then drops the connection when it answers the next request on it.
		serve(request -> dropConnectionOnce.compareAndSet(true, false) ?
			new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build() :
			new MockResponse.Builder().code(200).build());

		callWithStreamingPart();
		dropConnectionOnce.set(true);
		callWithStreamingPart();

		List<RecordedRequest> posts = requests.stream().filter(r -> "POST".equals(r.getMethod())).toList();
		assertEquals(2, posts.size(), "Each call should have sent its POST exactly once");
		assertEquals(1, countOccurrences(posts.get(1).getBody().utf8(), PAYLOAD),
			"The body must arrive once and intact: " + posts.get(1).getBody().utf8());
	}

	@Test
	void documentWriteWithReaderSucceedsWhenServerDropsThePooledConnection() {
		serve(request -> dropConnectionOnce.compareAndSet(true, false) ?
			new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build() :
			new MockResponse.Builder().code("HEAD".equals(request.getMethod()) ? 200 : 204).build());

		client.newJSONDocumentManager().write("/a.json", new ReaderHandle(new StringReader(PAYLOAD)));
		dropConnectionOnce.set(true);
		client.newJSONDocumentManager().write("/b.json", new ReaderHandle(new StringReader(PAYLOAD)));

		List<RecordedRequest> writes = requests.stream().filter(r -> !"HEAD".equals(r.getMethod())).toList();
		assertEquals(2, writes.size());
		assertEquals(PAYLOAD, writes.get(1).getBody().utf8());
	}

	@Test
	void failedStreamingRequestIsNotResent() {
		serve(request -> "POST".equals(request.getMethod()) ?
			new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build() :
			new MockResponse.Builder().code(200).build());

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, this::callWithStreamingPart);

		assertFalse(ex.getMessage().contains("Stream closed"),
			"The stream cannot be resent, so the original failure should be reported; message: " + ex.getMessage());
		assertEquals(1, requests.stream().filter(r -> "POST".equals(r.getMethod())).count(),
			"The streaming request must not be sent a second time");
	}

	@Test
	void failedStreamingRequestIsNotResentWithDigestAuthentication() {
		client.release();
		client = DatabaseClientFactory.newClient("localhost", mockWebServer.getPort(),
			new DatabaseClientFactory.DigestAuthContext("user", "password"));
		serve(request -> {
			if (request.getHeaders().get("Authorization") == null) {
				return new MockResponse.Builder().code(401)
					.addHeader("WWW-Authenticate", "Digest realm=\"public\", qop=\"auth\", nonce=\"abc\", opaque=\"xyz\"")
					.build();
			}
			return "POST".equals(request.getMethod()) ?
				new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build() :
				new MockResponse.Builder().code(200).build();
		});

		assertThrows(MarkLogicIOException.class, this::callWithStreamingPart);

		assertEquals(1, requests.stream()
				.filter(r -> "POST".equals(r.getMethod()) && r.getHeaders().get("Authorization") != null).count(),
			"The authenticated streaming request must not be sent a second time");
	}

	private void serve(Function<RecordedRequest, MockResponse> responder) {
		mockWebServer.setDispatcher(new Dispatcher() {
			@Override
			public MockResponse dispatch(RecordedRequest request) {
				requests.add(request);
				return responder.apply(request);
			}
		});
	}

	private void callWithStreamingPart() {
		BaseProxy.request("/ds/custom-content/", "importContent.xqy", BaseProxy.ParameterValuesKind.MULTIPLE_NODES)
			.on(client)
			.withSession()
			.withParams(BaseProxy.documentParam("content", false,
				Stream.of(new StringReader(PAYLOAD)).map(BaseProxy.JsonDocumentType::fromReader)))
			.responseNone();
	}

	private static int countOccurrences(String text, String token) {
		int count = 0;
		for (int i = text.indexOf(token); i >= 0; i = text.indexOf(token, i + token.length())) {
			count++;
		}
		return count;
	}
}
