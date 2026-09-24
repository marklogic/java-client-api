/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.impl.okhttp.RetryIOExceptionInterceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingOutputImplTest {

	@Test
	void multipartBodyWithStreamingPartIsNotRetried() {
		StreamingOutputImpl streamingBody = new StreamingOutputImpl(
			outputStream -> outputStream.write("content".getBytes(StandardCharsets.UTF_8)),
			null,
			MediaType.get("application/xml")
		);
		MultipartBody multipartBody = new MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("body", null, streamingBody)
			.build();
		assertTrue(multipartBody.isOneShot());

		AtomicInteger attempts = new AtomicInteger();
		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor(new RetryIOExceptionInterceptor(3, 0, 1, 0))
			.addInterceptor(chain -> {
				attempts.incrementAndGet();
				throw new IOException("unexpected end of stream");
			})
			.build();
		Request request = new Request.Builder()
			.url("http://localhost/")
			.post(multipartBody)
			.build();

		assertThrows(IOException.class, () -> client.newCall(request).execute());
		assertEquals(1, attempts.get());
	}

	@Test
	void multipartBodyWithInputStreamPartIsNotRetried() {
		InputStream inputStream = new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8));
		OkHttpServices.ObjectRequestBody inputStreamBody =
			new OkHttpServices.ObjectRequestBody(inputStream, MediaType.get("application/xml"));
		MultipartBody multipartBody = new MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("body", null, inputStreamBody)
			.build();
		assertTrue(multipartBody.isOneShot(),
			"A multipart body containing an InputStream-backed part must be one-shot so the retry interceptor does " +
				"not replay an already-consumed stream.");

		AtomicInteger attempts = new AtomicInteger();
		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor(new RetryIOExceptionInterceptor(3, 0, 1, 0))
			.addInterceptor(chain -> {
				attempts.incrementAndGet();
				throw new IOException("unexpected end of stream");
			})
			.build();
		Request request = new Request.Builder()
			.url("http://localhost/")
			.post(multipartBody)
			.build();

		assertThrows(IOException.class, () -> client.newCall(request).execute());
		assertEquals(1, attempts.get());
	}
}
