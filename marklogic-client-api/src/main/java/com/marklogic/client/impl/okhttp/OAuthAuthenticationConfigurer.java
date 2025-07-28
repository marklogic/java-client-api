package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @since 6.6.0
 */
class OAuthAuthenticationConfigurer implements AuthenticationConfigurer<DatabaseClientFactory.OAuthContext> {

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.OAuthContext authContext) {
		clientBuilder.addInterceptor(chain -> {
			Request authenticatedRequest = makeAuthenticatedRequest(chain.request(), authContext);
			return chain.proceed(authenticatedRequest);
		});
	}

	Request makeAuthenticatedRequest(Request request, DatabaseClientFactory.OAuthContext authContext) {
		String authValue = String.format("Bearer %s", authContext.getToken());
		return request.newBuilder().header("Authorization", authValue).build();
	}
}

