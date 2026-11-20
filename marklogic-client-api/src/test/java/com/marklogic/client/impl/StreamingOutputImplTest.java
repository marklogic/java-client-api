/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.impl.okhttp.RetryIOExceptionInterceptor;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the request body built for a handle reports {@code isOneShot()} according to whether the handle's
 * content can be written more than once, and that OkHttp-based retry honours that.
 */
class StreamingOutputImplTest {

	static Stream<Object[]> handlesAndOneShotFlags() {
		return Stream.of(
			new Object[]{"StringHandle", new StringHandle("<a/>"), false},
			new Object[]{"JacksonHandle", new JacksonHandle(new ObjectMapper().createObjectNode()), false},
			new Object[]{"BytesHandle", new BytesHandle("<a/>".getBytes(StandardCharsets.UTF_8)), false},
			new Object[]{"FileHandle", new FileHandle(new File("pom.xml")), false},
			new Object[]{"ReaderHandle", new ReaderHandle(new StringReader("<a/>")), true},
			new Object[]{"InputStreamHandle",
				new InputStreamHandle(new ByteArrayInputStream("<a/>".getBytes(StandardCharsets.UTF_8))), true}
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("handlesAndOneShotFlags")
	void oneShotMatchesWhetherTheHandleCanBeResent(String name, AbstractWriteHandle handle, boolean expectedOneShot) {
		assertEquals(expectedOneShot, OkHttpServices.makeRequestBodyForContent(handle).isOneShot());
	}

	@Test
	void multipartWithAnyOneShotPartIsOneShot() {
		MultipartBody body = multipart(
			OkHttpServices.makeRequestBodyForContent(new StringHandle("<a/>")),
			OkHttpServices.makeRequestBodyForContent(new ReaderHandle(new StringReader("<b/>")))
		);
		assertTrue(body.isOneShot());
	}

	@Test
	void multipartWithOnlyResendablePartsIsNotOneShot() {
		MultipartBody body = multipart(
			OkHttpServices.makeRequestBodyForContent(new StringHandle("<a/>")),
			OkHttpServices.makeRequestBodyForContent(new JacksonHandle(new ObjectMapper().createObjectNode()))
		);
		assertFalse(body.isOneShot());
	}

	@Test
	void oneShotMultipartIsNotRetried() {
		MultipartBody body = multipart(
			OkHttpServices.makeRequestBodyForContent(new ReaderHandle(new StringReader("<a/>")))
		);
		CountingFailure failure = new CountingFailure(Integer.MAX_VALUE);

		assertThrows(IOException.class, () -> client(failure).newCall(post(body)).execute());
		assertEquals(1, failure.attempts);
	}

	@Test
	void resendableMultipartIsRetried() throws IOException {
		MultipartBody body = multipart(
			OkHttpServices.makeRequestBodyForContent(new StringHandle("<a/>"))
		);
		CountingFailure failure = new CountingFailure(1);

		try (Response response = client(failure).newCall(post(body)).execute()) {
			assertEquals(200, response.code());
		}
		assertEquals(2, failure.attempts);
	}

	private static MultipartBody multipart(RequestBody... parts) {
		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		for (int i = 0; i < parts.length; i++) {
			builder.addFormDataPart("part" + i, null, parts[i]);
		}
		return builder.build();
	}

	private static Request post(RequestBody body) {
		return new Request.Builder().url("http://localhost/").post(body).build();
	}

	private static OkHttpClient client(CountingFailure failure) {
		return new OkHttpClient.Builder()
			.addInterceptor(new RetryIOExceptionInterceptor(3, 0, 1, 0))
			.addInterceptor(failure)
			.build();
	}

	/**
	 * Fails the first {@code failuresBeforeSuccess} attempts with an IOException, then returns a 200.
	 */
	private static class CountingFailure implements okhttp3.Interceptor {
		private final int failuresBeforeSuccess;
		int attempts;

		CountingFailure(int failuresBeforeSuccess) {
			this.failuresBeforeSuccess = failuresBeforeSuccess;
		}

		@Override
		public Response intercept(Chain chain) throws IOException {
			if (++attempts <= failuresBeforeSuccess) {
				throw new IOException("unexpected end of stream");
			}
			return new Response.Builder()
				.request(chain.request())
				.protocol(Protocol.HTTP_1_1)
				.code(200)
				.message("OK")
				.body(ResponseBody.create("", null))
				.build();
		}
	}
}
