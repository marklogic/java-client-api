/*
 * Copyright (c) 2022 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl.okhttp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClientFactory.MarkLogicCloudAuthContext;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MarkLogicCloudAuthenticationConfigurer implements AuthenticationConfigurer<MarkLogicCloudAuthContext> {

	private final static Logger logger = LoggerFactory.getLogger(MarkLogicCloudAuthenticationConfigurer.class);

	private String host;

	public MarkLogicCloudAuthenticationConfigurer(String host) {
		this.host = host;
	}

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, MarkLogicCloudAuthContext securityContext) {
		final String apiKey = securityContext.getKey();
		if (apiKey == null || apiKey.trim().length() < 1) {
			throw new IllegalArgumentException("No API key provided");
		}

		final Response response = callTokenEndpoint(securityContext);
		final String accessToken = getAccessTokenFromResponse(response);
		if (logger.isInfoEnabled()) {
			logger.info("Successfully obtained authentication token");
		}
		clientBuilder
			.addInterceptor(chain -> {
				Request authenticatedRequest = chain.request().newBuilder()
					.header("Authorization", "Bearer " + accessToken)
					.build();
				return chain.proceed(authenticatedRequest);
			});
	}

	private Response callTokenEndpoint(MarkLogicCloudAuthContext securityContext) {
		final HttpUrl tokenUrl = buildTokenUrl(securityContext);
		OkHttpClient.Builder clientBuilder = OkHttpUtil.newClientBuilder();
		// Current assumption is that the SSL config provided for connecting to MarkLogic should also be applicable
		// for connecting to MarkLogic Cloud's "/token" endpoint.
		OkHttpUtil.configureSocketFactory(clientBuilder, securityContext.getSSLContext(), securityContext.getTrustManager());
		OkHttpUtil.configureHostnameVerifier(clientBuilder, securityContext.getSSLHostnameVerifier());

		if (logger.isInfoEnabled()) {
			logger.info("Calling token endpoint at: " + tokenUrl);
		}

		final Call call = clientBuilder
			.build()
			.newCall(new Request.Builder()
				.url(tokenUrl)
				.post(newFormBody(securityContext))
				.build()
			);

		try {
			return call.execute();
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to call token endpoint at %s; cause: %s",
				tokenUrl, e.getMessage(), e));
		}
	}

	protected HttpUrl buildTokenUrl(MarkLogicCloudAuthContext securityContext) {
		// For the near future, it's guaranteed that https and 443 will be required for connecting to MarkLogic Cloud,
		// so providing the ability to customize this would be misleading.
		return new HttpUrl.Builder()
			.scheme("https")
			.host(host)
			.port(443)
			.build()
			.resolve(securityContext.getTokenEndpoint()).newBuilder().build();
	}

	protected FormBody newFormBody(MarkLogicCloudAuthContext securityContext) {
		return new FormBody.Builder()
			.add("grant_type", securityContext.getGrantType())
			.add("key", securityContext.getKey()).build();
	}

	private String getAccessTokenFromResponse(Response response) {
		String responseBody = null;
		JsonNode payload;
		try {
			responseBody = response.body().string();
			payload = new ObjectMapper().readTree(responseBody);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to get access token; response: " + responseBody, ex);
		}
		if (!payload.has("access_token")) {
			throw new RuntimeException("Unable to get access token; unexpected JSON response: " + payload);
		}
		return payload.get("access_token").asText();
	}
}
