/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

/**
 * Workaround for a MarkLogic server bug. When a request has both "TE: trailers" and "Accept-Encoding: gzip", MarkLogic
 * sends a chunked, gzipped response marked "Connection: Keep-Alive", but then closes the connection. OkHttp keeps that
 * dead connection in its pool, so the next request on it fails, and a request with a one-shot body (e.g. a Reader)
 * cannot be resent. With "Connection: close" on the request, OkHttp does not return the connection to its pool, and
 * MarkLogic also answers "Connection: close".
 * <p>
 * This must be a network interceptor so that it sees the headers that are actually sent: OkHttp adds
 * "Accept-Encoding: gzip" after application interceptors run, and a client configurator may remove it.
 * <p>
 * Only HTTP/1.1 connections are affected. On HTTP/2 (e.g. to a load balancer in front of MarkLogic) the header is not
 * sent, yet OkHttp would still stop reusing a healthy connection because of it.
 * <p>
 * TODO Remove this once the MarkLogic server bug is fixed (HTTPRequest::finishResponse closes every compressed
 * connection, even after sendKeepAliveHeader) and the oldest MarkLogic version supported by this client has the fix.
 */
public class ChunkedGzipConnectionCloseInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		if (isHttp11(chain.connection()) && headerContains(request, "TE", "trailers") && headerContains(request, "Accept-Encoding", "gzip")) {
			request = request.newBuilder().header("Connection", "close").build();
		}
		return chain.proceed(request);
	}

	private static boolean isHttp11(Connection connection) {
		return connection != null && connection.protocol() == Protocol.HTTP_1_1;
	}

	private static boolean headerContains(Request request, String name, String token) {
		String value = request.header(name);
		return value != null && value.toLowerCase(Locale.ROOT).contains(token);
	}
}
