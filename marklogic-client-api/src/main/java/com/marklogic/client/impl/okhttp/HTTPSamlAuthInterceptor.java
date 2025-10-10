/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */

package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.AuthorizerCallback;
import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.ExpiringSAMLAuth;
import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.RenewerCallback;
import com.marklogic.client.impl.RESTServices;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HTTPSamlAuthInterceptor implements Interceptor {

	private final AuthorizerCallback authorizer;
	private final RenewerCallback renewer;

	private String authorizationTokenValue;
	private ExpiringSAMLAuth expiringSAMLAuth;
	private long threshold;
	private AtomicBoolean isCallbackExecuting;

	public HTTPSamlAuthInterceptor(String authToken) {
		this.authorizationTokenValue = authToken;
		this.authorizer = null;
		this.renewer = null;
	}

	public HTTPSamlAuthInterceptor(AuthorizerCallback authorizer) {
		this.authorizer = authorizer;
		this.renewer = null;
	}

	public HTTPSamlAuthInterceptor(ExpiringSAMLAuth authorization, RenewerCallback renew) {
		expiringSAMLAuth = authorization;
		renewer = renew;
		isCallbackExecuting = new AtomicBoolean(false);
		this.authorizer = null;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		if (authorizer != null) {
			authorizeRequest();
		} else if (renewer != null && threshold <= Instant.now().getEpochSecond() && isCallbackExecuting.compareAndSet(false, true)) {
			startRenewalOfToken();
		}

		Request authenticatedRequest = chain.request().newBuilder()
			.header(RESTServices.HEADER_AUTHORIZATION, buildSamlHeader())
			.build();

		return chain.proceed(authenticatedRequest);
	}

	private synchronized void startRenewalOfToken() {
		RenewCallbackWrapper renewCallbackWrapper = new RenewCallbackWrapper(expiringSAMLAuth);
		Executors.defaultThreadFactory().newThread(renewCallbackWrapper).start();
	}

	private synchronized void authorizeRequest() {
		if (expiringSAMLAuth == null) {
			authorizeCallbackWrapper(null);
		} else if (threshold <= Instant.now().getEpochSecond()) {
			authorizeCallbackWrapper(expiringSAMLAuth.getExpiry());
		}
	}

	private synchronized String buildSamlHeader() {
		return String.format("%s %s=%s",
			RESTServices.AUTHORIZATION_TYPE_SAML,
			RESTServices.AUTHORIZATION_PARAM_TOKEN,
			this.authorizationTokenValue);
	}

	private synchronized void authorizeCallbackWrapper(Instant expiry) {

		if (expiry == null && expiringSAMLAuth != null) {
			return;
		}
		if (expiry != null && expiry != expiringSAMLAuth.getExpiry()) {
			return;
		}
		expiringSAMLAuth = authorizer.apply(expiringSAMLAuth);

		if (expiringSAMLAuth == null) {
			throw new IllegalArgumentException("SAML Authentication cannot be null");
		}

		if (expiringSAMLAuth.getAuthorizationToken() == null) {
			throw new IllegalArgumentException("SAML Authentication token cannot be null");
		}
		authorizationTokenValue = expiringSAMLAuth.getAuthorizationToken();
		setThreshold(expiringSAMLAuth.getExpiry());
	}

	private void setThreshold(Instant instant) {
		if (instant == null) {
			throw new IllegalArgumentException("SAML authentication does not have expiry value.");
		}
		if (instant.isBefore(Instant.now())) {
			throw new IllegalArgumentException("SAML authentication token has expired.");
		}
		long current = Instant.now().getEpochSecond();
		threshold = current + ((instant.getEpochSecond() - current) / 2);
	}

	private class RenewCallbackWrapper implements Runnable {

		private final ExpiringSAMLAuth expiringAuth;

		public RenewCallbackWrapper(ExpiringSAMLAuth expiringSamlAuth) {
			this.expiringAuth = expiringSamlAuth;
		}

		@Override
		public void run() {
			try {
				Instant newInstant = renewer.apply(expiringAuth);
				setThreshold(newInstant);
			} finally {
				isCallbackExecuting.set(false);
			}
		}
	}
}
