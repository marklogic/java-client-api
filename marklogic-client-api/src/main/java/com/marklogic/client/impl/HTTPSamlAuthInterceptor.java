/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.AuthorizerCallback;
import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.ExpiringSAMLAuth;
import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.RenewerCallback;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPSamlAuthInterceptor implements Interceptor {

    private String authorizationTokenValue;
    private AuthorizerCallback authorizer;
    private ExpiringSAMLAuth expiringSAMLAuth;
    private long threshold;
    private RenewerCallback renewer;
    private AtomicBoolean isCallbackExecuting;

    public HTTPSamlAuthInterceptor(String authToken) {
        this.authorizationTokenValue = authToken;
    }

    public HTTPSamlAuthInterceptor(AuthorizerCallback authorizer) {
        this.authorizer = authorizer;
    }

    public HTTPSamlAuthInterceptor(ExpiringSAMLAuth authorization, RenewerCallback renew) {
        expiringSAMLAuth = authorization;
        renewer = renew;
        isCallbackExecuting = new AtomicBoolean(false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (authorizer != null) {
            if(expiringSAMLAuth == null) {
                authorizeCallbackWrapper(null);
            } else if(threshold<=Instant.now().getEpochSecond()){
                authorizeCallbackWrapper(expiringSAMLAuth.getExpiry());
            }
        } else if (renewer != null && threshold <= Instant.now().getEpochSecond() && isCallbackExecuting.compareAndSet(false, true)) {
                RenewCallbackWrapper renewCallbackWrapper = new RenewCallbackWrapper(expiringSAMLAuth);
                Executors.defaultThreadFactory().newThread(renewCallbackWrapper).start();
        }
        String samlHeaderValue = RESTServices.AUTHORIZATION_TYPE_SAML + " " + RESTServices.AUTHORIZATION_PARAM_TOKEN
                + "=" + authorizationTokenValue;
        Request authenticatedRequest = request.newBuilder().header(RESTServices.HEADER_AUTHORIZATION, samlHeaderValue)
                .build();
        return chain.proceed(authenticatedRequest);
    }

    private synchronized void authorizeCallbackWrapper(Instant expiry) {

        if(expiry == null && expiringSAMLAuth != null) {
            return;
        }
        if(expiry!=null && expiry!=expiringSAMLAuth.getExpiry()) {
            return;
        }
        expiringSAMLAuth = authorizer.apply(expiringSAMLAuth);

        if(expiringSAMLAuth == null) {
            throw new IllegalArgumentException("SAML Authentication cannot be null");
        }

        if(expiringSAMLAuth.getAuthorizationToken() == null) {
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

        private ExpiringSAMLAuth expiringAuth;

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
