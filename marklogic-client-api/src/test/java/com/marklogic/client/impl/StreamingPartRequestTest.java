/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRetryException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.extra.okhttpclient.RemoveAcceptEncodingConfigurator;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uses a mock server to verify how requests with non-resendable content (e.g. a Reader) are sent. MarkLogic answers a
 * Row Manager request with a chunked, gzipped response that says "Connection: Keep-Alive", yet closes the connection
 * after it. If a one-shot request is the next one on that connection, it fails and cannot be resent.
 */
class StreamingPartRequestTest {

	private static final String PAYLOAD = "{\"a\":1}";
	private static final String ROWS_BOUNDARY = "rows-boundary";

	private MockWebServer mockWebServer;
	private final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
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
	void streamingRequestAfterRowsRequestSucceeds() throws IOException {
		serve(request -> isRowsRequest(request) ? rowsResponseThatClosesConnectionLikeMarkLogic(request) :
			new MockResponse.Builder().code(200).build());

		queryRows();
		callWithStreamingPart();

		List<RecordedRequest> posts = requests.stream()
			.filter(r -> "POST".equals(r.getMethod()) && !isRowsRequest(r)).toList();
		assertEquals(1, posts.size(), "The streaming request should have been sent exactly once");
		assertEquals(1, countOccurrences(posts.get(0).getBody().utf8(), PAYLOAD),
			"The body must arrive once and intact: " + posts.get(0).getBody().utf8());
		assertEquals(0, requests.stream().filter(r -> "HEAD".equals(r.getMethod())).count(),
			"No ping should be sent before a one-shot request when not using digest authentication");

		RecordedRequest rows = requests.get(0);
		assertEquals("close", rows.getHeaders().get("Connection"),
			"The client should ask to close the connection that MarkLogic would close anyway");
		assertNotEquals(rows.getConnectionIndex(), posts.get(0).getConnectionIndex(),
			"The streaming request should not reuse the connection from the rows request");
	}

	@Test
	void rowsRequestKeepsConnectionWhenGzipIsDisabled() throws IOException {
		DatabaseClientFactory.addConfigurator(new RemoveAcceptEncodingConfigurator());
		try {
			client.release();
			client = DatabaseClientFactory.newClient("localhost", mockWebServer.getPort(),
				new DatabaseClientFactory.BasicAuthContext("user", "password"));
		} finally {
			DatabaseClientFactory.removeConfigurators();
		}
		serve(request -> isRowsRequest(request) ? rowsResponseThatClosesConnectionLikeMarkLogic(request) :
			new MockResponse.Builder().code(200).build());

		queryRows();
		callWithStreamingPart();

		RecordedRequest rows = requests.get(0);
		assertNotEquals("close", rows.getHeaders().get("Connection"),
			"Without gzip, MarkLogic keeps the connection open, so the client should not ask to close it");
		assertEquals(rows.getConnectionIndex(), requests.get(1).getConnectionIndex(),
			"The streaming request should reuse the connection from the rows request");
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
	void digestAuthenticationPingsOnlyBeforeFirstStreamingRequest() {
		useDigestClient();
		serve(request -> request.getHeaders().get("Authorization") == null ?
			digestChallenge() : new MockResponse.Builder().code(200).build());

		callWithStreamingPart();
		callWithStreamingPart();

		List<String> methods = requests.stream().map(RecordedRequest::getMethod).toList();
		int firstPost = methods.indexOf("POST");
		assertTrue(methods.subList(0, firstPost).contains("HEAD"), "The first one-shot request should be preceded by a ping: " + methods);
		assertFalse(methods.subList(firstPost, methods.size()).contains("HEAD"), "Only the first one-shot request needs a ping: " + methods);
		assertEquals(2, methods.stream().filter("POST"::equals).count(), "Each body should be sent once: " + methods);
	}

	@Test
	void failedStreamingRequestIsNotResentWithDigestAuthentication() {
		useDigestClient();
		serve(request -> {
			if (request.getHeaders().get("Authorization") == null) {
				return digestChallenge();
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

	@Test
	void streamingRequestIsNotSentWhenPingsFindServerUnavailable() {
		useDigestClient();
		serve(request -> {
			if (request.getHeaders().get("Authorization") == null) {
				return digestChallenge();
			}
			return new MockResponse.Builder().code("HEAD".equals(request.getMethod()) ? 503 : 200).build();
		});

		assertThrows(FailedRetryException.class, this::callWithStreamingPart);

		assertEquals(0, requests.stream().filter(r -> "POST".equals(r.getMethod())).count(),
			"Content that can only be sent once must not be sent while the server is unavailable");
	}

	private void useDigestClient() {
		client.release();
		client = DatabaseClientFactory.newClient("localhost", mockWebServer.getPort(),
			new DatabaseClientFactory.DigestAuthContext("user", "password"));
	}

	private static MockResponse digestChallenge() {
		return new MockResponse.Builder().code(401)
			.addHeader("WWW-Authenticate", "Digest realm=\"public\", qop=\"auth\", nonce=\"abc\", opaque=\"xyz\"")
			.build();
	}

	private static boolean isRowsRequest(RecordedRequest request) {
		return request.getUrl().encodedPath().endsWith("/v1/rows");
	}

	/**
	 * Mimics MarkLogic: a response to "TE: trailers" with "Accept-Encoding: gzip" is chunked and gzipped, and unless
	 * the client asked for "Connection: close", it says "Keep-Alive" yet the server closes the connection after it.
	 */
	private static MockResponse rowsResponseThatClosesConnectionLikeMarkLogic(RecordedRequest request) {
		String body = "--" + ROWS_BOUNDARY + "\r\n" +
			"Content-Type: application/json\r\n\r\n" +
			"{\"columns\":[{\"name\":\"a\"}]}\r\n" +
			"--" + ROWS_BOUNDARY + "--\r\n";
		MockResponse.Builder builder = new MockResponse.Builder().code(200)
			.addHeader("Content-Type", "multipart/mixed; boundary=" + ROWS_BOUNDARY)
			.body(body);
		boolean chunkedAndGzipped = "trailers".equals(request.getHeaders().get("TE")) &&
			"gzip".equals(request.getHeaders().get("Accept-Encoding"));
		if (chunkedAndGzipped && !"close".equalsIgnoreCase(request.getHeaders().get("Connection"))) {
			builder.addHeader("Connection", "Keep-Alive").onResponseEnd(new SocketEffect.CloseSocket());
		}
		return builder.build();
	}

	private void queryRows() throws IOException {
		RowManager rowManager = client.newRowManager();
		try (RowSet<RowRecord> rows = rowManager.resultRows(rowManager.newPlanBuilder().fromView("schema", "view"))) {
			rows.forEach(row -> {
			});
		}
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
