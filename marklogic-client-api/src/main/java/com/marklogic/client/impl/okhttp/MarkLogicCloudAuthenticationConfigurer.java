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
	private int port;

	public MarkLogicCloudAuthenticationConfigurer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, MarkLogicCloudAuthContext securityContext) {
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
		// Initial testing has shown that neither the OkHttp socket factory nor hostname verifier need to be configured
		// for the goal of invoking the token endpoint.

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
		return new HttpUrl.Builder()
			.scheme(securityContext.getSSLContext() != null ? "https" : "http")
			.host(host)
			.port(port)
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
