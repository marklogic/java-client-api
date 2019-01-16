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
import java.util.function.Function;

import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.ExpiringSAMLAuth;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPSamlAuthInterceptor implements Interceptor{

	  private String authorizationTokenValue;
	  private Function<ExpiringSAMLAuth, ExpiringSAMLAuth> authorizer;
	  private ExpiringSAMLAuth expiringSAMLAuth;
	  private long threshold;

	  HTTPSamlAuthInterceptor(String authToken) {
	    this.authorizationTokenValue = authToken;
	  }
	  HTTPSamlAuthInterceptor(Function<ExpiringSAMLAuth, ExpiringSAMLAuth> authorizer) {
		this.authorizer = authorizer;
	  }

	  @Override
	  public Response intercept(Chain chain) throws IOException {
	    Request request = chain.request();
	    if(expiringSAMLAuth==null) {
	    	authorize(null);
	    } else if(threshold<=Instant.now().getEpochSecond()){
	    	authorize(expiringSAMLAuth.getExpiry());
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
		  expiringSAMLAuth = authorizer.apply(expiringSAMLAuth);
		  if(expiringSAMLAuth==null) {
			  throw new IllegalArgumentException("SAML Authentication cannot be null");
		  }
		  
		  if(expiringSAMLAuth.getAuthorizationToken() == null) {
			  throw new IllegalArgumentException("SAML Authentication token cannot be null");
		  }
		  authorizationTokenValue = expiringSAMLAuth.getAuthorizationToken();
		  
		  if(expiringSAMLAuth.getExpiry() == null) {
			  throw new IllegalArgumentException("SAML authentication does not have expiry value.");
		  }
		  if(expiringSAMLAuth.getExpiry().isBefore(Instant.now())) {
			  throw new IllegalArgumentException("SAML authentication token has expired.");
		  }
		  long current = Instant.now().getEpochSecond();
		  threshold = current+ (expiringSAMLAuth.getExpiry().getEpochSecond() - current)/2;
	  }
}
