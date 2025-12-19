/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.auth.okhttp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.ProgressDataCloudException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Handles generating a token from the Progress Data Cloud token endpoint.
 *
 * @since 8.1.0
 */
public class TokenGenerator implements Supplier<String> {

	private final static Logger logger = LoggerFactory.getLogger(TokenGenerator.class);

	private final String host;
	private final TokenInputs tokenInputs;
	private final Supplier<OkHttpClient.Builder> okHttpClientBuilderSupplier;

	public TokenGenerator(String host, TokenInputs tokenInputs, Supplier<OkHttpClient.Builder> okHttpClientBuilderSupplier) {
		this.host = host;
		this.tokenInputs = tokenInputs;
		this.okHttpClientBuilderSupplier = okHttpClientBuilderSupplier;
	}

	public String get() {
		final Response tokenResponse = callTokenEndpoint();
		String token = getAccessTokenFromResponse(tokenResponse);
		if (logger.isDebugEnabled()) {
			logger.debug("Successfully obtained authentication token");
		}
		return token;
	}

	private Response callTokenEndpoint() {
		final HttpUrl tokenUrl = buildTokenUrl();
		OkHttpClient.Builder clientBuilder = okHttpClientBuilderSupplier.get();

		if (logger.isDebugEnabled()) {
			logger.debug("Calling token endpoint at: {}", tokenUrl);
		}

		final Call call = clientBuilder.build().newCall(
			new Request.Builder()
				.url(tokenUrl)
				.post(newFormBody())
				.build()
		);

		try {
			return call.execute();
		} catch (IOException e) {
			throw new ProgressDataCloudException(String.format("Unable to call token endpoint at %s; cause: %s",
				tokenUrl, e.getMessage()), e);
		}
	}

	protected final HttpUrl buildTokenUrl() {
		// For the near future, it's guaranteed that https and 443 will be required for connecting to Progress Data Cloud,
		// so providing the ability to customize this would be misleading.
		HttpUrl.Builder builder = new HttpUrl.Builder()
			.scheme("https")
			.host(host)
			.port(443)
			.build()
			.resolve(tokenInputs.getTokenEndpoint())
			.newBuilder();

		Integer duration = tokenInputs.getTokenDuration();
		return duration != null ?
			builder.addQueryParameter("duration", duration.toString()).build() :
			builder.build();
	}

	protected final FormBody newFormBody() {
		return new FormBody.Builder()
			.add("grant_type", tokenInputs.getGrantType())
			.add("key", tokenInputs.getApiKey())
			.build();
	}

	private String getAccessTokenFromResponse(Response response) {
		String responseBody = null;
		JsonNode payload;
		try {
			responseBody = response.body().string();
			payload = new ObjectMapper().readTree(responseBody);
		} catch (IOException ex) {
			throw new ProgressDataCloudException("Unable to get access token; response: " + responseBody, ex);
		}
		if (!payload.has("access_token")) {
			throw new ProgressDataCloudException("Unable to get access token; unexpected JSON response: " + payload);
		}
		return payload.get("access_token").asText();
	}
}
