/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The server bug that the interceptor works around is specific to HTTP/1.1, so the interceptor must leave other
 * connections alone. How a DatabaseClient behaves on HTTP/1.1 is covered by StreamingPartRequestTest.
 */
class ChunkedGzipConnectionCloseInterceptorTest {

	@Test
	void http11ConnectionIsNotReused() throws Exception {
		List<RecordedRequest> requests = sendTwoRowsRequests(Protocol.HTTP_1_1);

		assertEquals("close", requests.get(0).getHeaders().get("Connection"));
		assertNotEquals(requests.get(0).getConnectionIndex(), requests.get(1).getConnectionIndex());
	}

	@Test
	void http2ConnectionIsReused() throws Exception {
		List<RecordedRequest> requests = sendTwoRowsRequests(Protocol.H2_PRIOR_KNOWLEDGE);

		assertEquals(requests.get(0).getConnectionIndex(), requests.get(1).getConnectionIndex(),
			"An HTTP/2 connection is not affected by the server bug, so it should stay in the pool");
	}

	private static List<RecordedRequest> sendTwoRowsRequests(Protocol protocol) throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.setProtocols(List.of(protocol));
			server.enqueue(new MockResponse.Builder().code(200).body("{}").build());
			server.enqueue(new MockResponse.Builder().code(200).body("{}").build());
			server.start();

			OkHttpClient client = new OkHttpClient.Builder()
				.protocols(List.of(protocol))
				.addNetworkInterceptor(new ChunkedGzipConnectionCloseInterceptor())
				.build();
			try {
				for (int i = 0; i < 2; i++) {
					// OkHttp adds "Accept-Encoding: gzip" itself
					Request request = new Request.Builder().url(server.url("/v1/rows"))
						.header("TE", "trailers")
						.post(RequestBody.create("{}", MediaType.parse("application/json")))
						.build();
					try (Response response = client.newCall(request).execute()) {
						assertEquals(200, response.code());
						response.body().string();
					}
				}
				return List.of(server.takeRequest(), server.takeRequest());
			} finally {
				client.connectionPool().evictAll();
			}
		}
	}
}
