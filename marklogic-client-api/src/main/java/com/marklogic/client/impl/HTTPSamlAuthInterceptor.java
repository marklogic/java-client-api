/*
 * Copyright 2019 MarkLogic Corporation
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
            if ((expiringSAMLAuth == null || threshold <= Instant.now().getEpochSecond())) {
                authorizeCallback();
            }
        } else if (renewer != null && threshold <= Instant.now().getEpochSecond()) {
                RenewCallback renewExpiry = new RenewCallback(expiringSAMLAuth);
                if (isCallbackExecuting.compareAndSet(false, true))
                    Executors.defaultThreadFactory().newThread(renewExpiry).start();

        }
        String samlHeaderValue = RESTServices.AUTHORIZATION_TYPE_SAML + " " + RESTServices.AUTHORIZATION_PARAM_TOKEN
                + "=" + authorizationTokenValue;
        Request authenticatedRequest = request.newBuilder().header(RESTServices.HEADER_AUTHORIZATION, samlHeaderValue)
                .build();
        return chain.proceed(authenticatedRequest);
    }

    private synchronized void authorizeCallback() {

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

    private class RenewCallback implements Runnable {
        
        private ExpiringSAMLAuth expiringAuth;
        
        public RenewCallback(ExpiringSAMLAuth expiringSamlAuth) {
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
