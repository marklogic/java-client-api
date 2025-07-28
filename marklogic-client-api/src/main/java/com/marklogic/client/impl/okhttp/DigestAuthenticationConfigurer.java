/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.marklogic.client.DatabaseClientFactory;
import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DigestAuthenticationConfigurer implements AuthenticationConfigurer<DatabaseClientFactory.DigestAuthContext> {

	@Override
	public void configureAuthentication(OkHttpClient.Builder clientBuilder, DatabaseClientFactory.DigestAuthContext securityContext) {
		String user = securityContext.getUser();
		String password = securityContext.getPassword();
		if (user == null)
			throw new IllegalArgumentException("No user provided");
		if (password == null)
			throw new IllegalArgumentException("No password provided");

		final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
		CachingAuthenticator authenticator = new DigestAuthenticator(new Credentials(user, password));
		clientBuilder.authenticator(new CachingAuthenticatorDecorator(authenticator, authCache));
		clientBuilder.addInterceptor(new AuthenticationCacheInterceptor(authCache));
	}
}
