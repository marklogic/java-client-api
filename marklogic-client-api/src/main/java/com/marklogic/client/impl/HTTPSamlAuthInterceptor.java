package com.marklogic.client.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Function;

import com.marklogic.client.DatabaseClientFactory.SAMLAuthContext.ExpiringSAMLAuth;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An HTTP Request interceptor that modifies the request headers to enable
 * 'SAML' authentication. It compares the current instant with the expiry time of the existing token and 
 * resets the token if the current time is greater than or equal to half of the expiry time.
 *
 */
public class HTTPSamlAuthInterceptor implements Interceptor{

	  private String authorizationTokenValue;
	  private Function<ExpiringSAMLAuth, ExpiringSAMLAuth> authorizer;
	  private ExpiringSAMLAuth expiringSAMLAuth;
	  private Instant threshold;

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
	    } else if(threshold.isBefore(Instant.now())){
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
		  threshold = Instant.now().plusSeconds((expiringSAMLAuth.getExpiry().getEpochSecond())/2);
	  }
}
