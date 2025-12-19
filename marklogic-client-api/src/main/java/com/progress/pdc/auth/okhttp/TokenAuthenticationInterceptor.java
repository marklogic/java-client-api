/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.auth.okhttp;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * An OkHttp interceptor that adds a bearer token to requests, and handles 401 responses by generating
 * a new token and retrying the request.
 *
 * @since 8.1.0
 */
public class TokenAuthenticationInterceptor implements Interceptor {

	private final static Logger logger = LoggerFactory.getLogger(TokenAuthenticationInterceptor.class);

	private final Supplier<String> tokenGenerator;
	private String token;

	public TokenAuthenticationInterceptor(Supplier<String> tokenGenerator) {
		this.tokenGenerator = tokenGenerator;
		this.token = tokenGenerator.get();
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request.Builder builder = chain.request().newBuilder();
		builder = addTokenToRequest(builder);
		Response response = chain.proceed(builder.build());
		if (response.code() == 401) {
			logger.info("Received 401; will generate new token if necessary and retry request");
			response.close();
			final String currentToken = this.token;
			generateNewTokenIfNecessary(currentToken);

			builder = chain.request().newBuilder();
			builder = addTokenToRequest(builder);
			response = chain.proceed(builder.build());
		}
		return response;
	}

	/**
	 * In the case of N threads using the same DatabaseClient - e.g. when using DMSDK - all of them
	 * may make a request at the same time and get a 401 back. Functionally, it should be fine if all
	 * make their own requests to renew the token, with the last thread being the one whose token
	 * value is retained on this class. But to simplify matters, this block is synchronized so only one
	 * thread can be in here. And if that thread finds that this.token is different from currentToken,
	 * then some other thread already renewed the token - so this thread doesn't need to do anything and
	 * can just try again.
	 *
	 * @param currentToken the value of this instance's token right before calling this method; in the event that
	 *                     another thread using this instance got here first, then this value will differ from the
	 *                     instance's token field
	 */
	private synchronized void generateNewTokenIfNecessary(String currentToken) {
		if (currentToken.equals(this.token)) {
			logger.info("Generating new token based on receiving 401");
			this.token = tokenGenerator.get();
		} else if (logger.isDebugEnabled()) {
			logger.debug("This instance's token has already been updated, presumably by another thread");
		}
	}

	private synchronized Request.Builder addTokenToRequest(Request.Builder builder) {
		return builder.header("Authorization", String.format("Bearer %s", this.token));
	}

}
