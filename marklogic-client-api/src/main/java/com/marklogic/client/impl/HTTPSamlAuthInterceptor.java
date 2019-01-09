package com.marklogic.client.impl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPSamlAuthInterceptor implements Interceptor{

	  String authorizationTokenValue;

	  HTTPSamlAuthInterceptor(String authToken) {
	    this.authorizationTokenValue = authToken;
	  }

	  @Override
	  public Response intercept(Chain chain) throws IOException {
	    Request request = chain.request();
	    String samlHeaderValue = RESTServices.AUTHORIZATION_TYPE_SAML+ " "+ RESTServices.AUTHORIZATION_PARAM_TOKEN + "=" + authorizationTokenValue;
	    Request authenticatedRequest = request.newBuilder().header("Authorization", samlHeaderValue).build();
	    return chain.proceed(authenticatedRequest);
	  }

}
