/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClientFactory.MarkLogicCloudAuthContext;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class MarkLogicCloudAuthenticationConfigurer implements AuthenticationConfigurer<MarkLogicCloudAuthContext> {

	private String host;

	MarkLogicCloudAuthenticationConfigurer(String host) {
		this.host = host;
	}

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, MarkLogicCloudAuthContext securityContext) {
		final String apiKey = securityContext.getApiKey();
		if (apiKey == null || apiKey.trim().length() < 1) {
			throw new IllegalArgumentException("No API key provided");
		}
		TokenGenerator tokenGenerator = new DefaultTokenGenerator(this.host, securityContext);
		clientBuilder.addInterceptor(new TokenAuthenticationInterceptor(tokenGenerator));
	}

	/**
	 * Exists solely for mocking in unit tests.
	 */
	public interface TokenGenerator {
		String generateToken();
	}

	/**
	 * Knows how to call the "/token" endpoint in MarkLogic Cloud to generate a new token based on the
	 * user-provided API key.
	 */
	static class DefaultTokenGenerator implements TokenGenerator {

		private final static Logger logger = LoggerFactory.getLogger(DefaultTokenGenerator.class);
		private String host;
		private MarkLogicCloudAuthContext securityContext;

		public DefaultTokenGenerator(String host, MarkLogicCloudAuthContext securityContext) {
			this.host = host;
			this.securityContext = securityContext;
		}

		public String generateToken() {
			final Response tokenResponse = callTokenEndpoint();
			String token = getAccessTokenFromResponse(tokenResponse);
			if (logger.isInfoEnabled()) {
				logger.info("Successfully obtained authentication token");
			}
			return token;
		}

		private Response callTokenEndpoint() {
			final HttpUrl tokenUrl = buildTokenUrl();
			OkHttpClient.Builder clientBuilder = OkHttpUtil.newClientBuilder();
			// Current assumption is that the SSL config provided for connecting to MarkLogic should also be applicable
			// for connecting to MarkLogic Cloud's "/token" endpoint.
			OkHttpUtil.configureSocketFactory(clientBuilder, securityContext.getSSLContext(), securityContext.getTrustManager());
			OkHttpUtil.configureHostnameVerifier(clientBuilder, securityContext.getSSLHostnameVerifier());

			if (logger.isInfoEnabled()) {
				logger.info("Calling token endpoint at: " + tokenUrl);
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
				throw new RuntimeException(String.format("Unable to call token endpoint at %s; cause: %s",
					tokenUrl, e.getMessage(), e));
			}
		}

		protected HttpUrl buildTokenUrl() {
			// For the near future, it's guaranteed that https and 443 will be required for connecting to MarkLogic Cloud,
			// so providing the ability to customize this would be misleading.
			HttpUrl.Builder builder = new HttpUrl.Builder()
				.scheme("https")
				.host(host)
				.port(443)
				.build()
				.resolve(securityContext.getTokenEndpoint()).newBuilder();

			Integer duration = securityContext.getTokenDuration();
			return duration != null ?
				builder.addQueryParameter("duration", duration.toString()).build() :
				builder.build();
		}

		protected FormBody newFormBody() {
			return new FormBody.Builder()
				.add("grant_type", securityContext.getGrantType())
				.add("key", securityContext.getApiKey())
				.build();
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

	/**
	 * OkHttp interceptor that handles adding a token to an HTTP request and renewing it when necessary.
	 */
	static class TokenAuthenticationInterceptor implements Interceptor {

		private final static Logger logger = LoggerFactory.getLogger(TokenAuthenticationInterceptor.class);

		private TokenGenerator tokenGenerator;
		private String token;

		public TokenAuthenticationInterceptor(TokenGenerator tokenGenerator) {
			this.tokenGenerator = tokenGenerator;
			this.token = tokenGenerator.generateToken();
		}

		@Override
		public Response intercept(Chain chain) throws IOException {
			Response response = chain.proceed(addTokenToRequest(chain));
			if (response.code() == 401) {
				logger.info("Received 401; will generate new token if necessary and retry request");
				response.close();
				final String currentToken = this.token;
				generateNewTokenIfNecessary(currentToken);
				response = chain.proceed(addTokenToRequest(chain));
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
				this.token = tokenGenerator.generateToken();
			} else if (logger.isDebugEnabled()) {
				logger.debug("This instance's token has already been updated, presumably by another thread");
			}
		}

		private Request addTokenToRequest(Chain chain) {
			return chain.request().newBuilder()
				.header("Authorization", "Bearer " + token)
				.build();
		}
	}
}
