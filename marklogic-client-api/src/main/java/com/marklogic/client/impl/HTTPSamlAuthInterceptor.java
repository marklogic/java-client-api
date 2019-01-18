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

public class HTTPSamlAuthInterceptor implements Interceptor, Runnable {

	  private String authorizationTokenValue;
	  private AuthorizerCallback authorizer;
	  private static ExpiringSAMLAuth expiringSAMLAuth;
	  private static long threshold;
	  private static RenewerCallback renewer;
	  static AtomicBoolean isCallbackExecuting = new AtomicBoolean(false);

	  HTTPSamlAuthInterceptor(String authToken) {
	    this.authorizationTokenValue = authToken;
	  }
	  HTTPSamlAuthInterceptor(AuthorizerCallback authorizer) {
		this.authorizer = authorizer;
	  }
	  public HTTPSamlAuthInterceptor(ExpiringSAMLAuth authorization, RenewerCallback renew) {
	    expiringSAMLAuth = authorization;
        renewer = renew;
    }

	  @Override
	  public Response intercept(Chain chain) throws IOException {
	    Request request = chain.request();
	    if(authorizer!=null) {
	        if(expiringSAMLAuth==null) {
	            authorize(null);
	        } else if(threshold<=Instant.now().getEpochSecond()){
	            authorize(expiringSAMLAuth.getExpiry());
	                    
	        }
	    }
	    else if(renewer!=null) {
	        if(threshold<=Instant.now().getEpochSecond()){
	            HTTPSamlAuthInterceptor httpSamlAuthInterceptor = new HTTPSamlAuthInterceptor(expiringSAMLAuth, renewer);
	            if(isCallbackExecuting.compareAndSet(false, true))
	                Executors.defaultThreadFactory().newThread(httpSamlAuthInterceptor).start();
	            
	        }
	    }
	    String samlHeaderValue = RESTServices.AUTHORIZATION_TYPE_SAML+ " "+ RESTServices.AUTHORIZATION_PARAM_TOKEN + "=" + authorizationTokenValue;
	    Request authenticatedRequest = request.newBuilder().header(RESTServices.HEADER_AUTHORIZATION, samlHeaderValue).build();
	    return chain.proceed(authenticatedRequest);
	  }
	  
	  private synchronized void authorize(Instant expiry) {
		  if(expiry == null && expiringSAMLAuth != null) {
			  return;
		  }
		  if(expiry!=null && expiry!=expiringSAMLAuth.getExpiry()) {
			  return;
		  }
		  checkAuthorizationExpiry(expiry);
		  expiringSAMLAuth = authorizer.apply(expiringSAMLAuth);
		  if(expiringSAMLAuth==null) {
			  throw new IllegalArgumentException("SAML Authentication cannot be null");
		  }
		  
		  if(expiringSAMLAuth.getAuthorizationToken() == null) {
			  throw new IllegalArgumentException("SAML Authentication token cannot be null");
		  }
		  authorizationTokenValue = expiringSAMLAuth.getAuthorizationToken();
		  
		  
		  if(isCallbackExecuting.compareAndSet(false, true))
		      threshold = expiry.getEpochSecond();
		  
	  }
	  
	  private static void checkAuthorizationExpiry(Instant instant) {
	      if(instant == null) {
              throw new IllegalArgumentException("SAML authentication does not have expiry value.");
          }
          if(instant.isBefore(Instant.now())) {
              throw new IllegalArgumentException("SAML authentication token has expired.");
          }
	  }
	  static void renew(ExpiringSAMLAuth expiringSamlAuth) {
	      try {
	      Instant newInstant = renewer.apply(expiringSamlAuth);
	      
	      checkAuthorizationExpiry(newInstant);
	      long current = Instant.now().getEpochSecond();
	      threshold = current+ ((newInstant.getEpochSecond() - current)/2);
	      } finally {
            isCallbackExecuting = new AtomicBoolean(false);
        }
	  }
    @Override
    public void run() {
        renew(expiringSAMLAuth);
        
    }
}
