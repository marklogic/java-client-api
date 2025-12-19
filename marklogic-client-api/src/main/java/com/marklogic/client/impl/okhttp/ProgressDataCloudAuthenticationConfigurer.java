/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import com.progress.pdc.auth.okhttp.TokenGenerator;
import com.progress.pdc.auth.okhttp.TokenAuthenticationInterceptor;
import okhttp3.OkHttpClient;

import java.util.function.Supplier;

record ProgressDataCloudAuthenticationConfigurer(
	String host) implements AuthenticationConfigurer<DatabaseClientFactory.ProgressDataCloudAuthContext> {

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.ProgressDataCloudAuthContext securityContext) {
		final String apiKey = securityContext.getApiKey();
		if (apiKey == null || apiKey.trim().isEmpty()) {
			throw new IllegalArgumentException("No API key provided");
		}

		Supplier<OkHttpClient.Builder> okHttpClientBuilderSupplier = () -> {
			OkHttpClient.Builder tokenClientBuilder = OkHttpUtil.newClientBuilder();
			// Current assumption is that the SSL config provided for connecting to MarkLogic should also be applicable
			// for connecting to Progress Data Cloud's "/token" endpoint.
			OkHttpUtil.configureSocketFactory(tokenClientBuilder, securityContext.getSSLContext(), securityContext.getTrustManager());
			OkHttpUtil.configureHostnameVerifier(tokenClientBuilder, securityContext.getSSLHostnameVerifier());
			return tokenClientBuilder;
		};

		Supplier<String> tokenGenerator = new TokenGenerator(this.host, securityContext, okHttpClientBuilderSupplier);
		clientBuilder.addInterceptor(new TokenAuthenticationInterceptor(tokenGenerator));
	}
}
