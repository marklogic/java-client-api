/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.burgstaller.okhttp.digest.Credentials;
import com.marklogic.client.DatabaseClientFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.Charset;

class BasicAuthenticationConfigurer implements AuthenticationConfigurer<DatabaseClientFactory.BasicAuthContext> {

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.BasicAuthContext securityContext) {
		String user = securityContext.getUser();
		String password = securityContext.getPassword();
		if (user == null)
			throw new IllegalArgumentException("No user provided");
		if (password == null)
			throw new IllegalArgumentException("No password provided");

		clientBuilder.addInterceptor(new BasicAuthInterceptor(new Credentials(user, password)));
	}
}

class BasicAuthInterceptor implements Interceptor {

	private String authValue;

	BasicAuthInterceptor(Credentials credentials) {
		this.authValue = okhttp3.Credentials.basic(credentials.getUserName(), credentials.getPassword(),
			Charset.forName("UTF-8"));
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Request authenticatedRequest = request.newBuilder().header("Authorization", authValue).build();
		return chain.proceed(authenticatedRequest);
	}

}
