/*
 * Copyright (c) 2023 MarkLogic Corporation
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

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.marklogic.client.DatabaseClientFactory;
import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DigestAuthenticationConfigurer implements AuthenticationConfigurer<DatabaseClientFactory.DigestAuthContext> {

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
