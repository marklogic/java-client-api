package com.marklogic.client.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import com.burgstaller.okhttp.digest.Credentials;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An HTTP Request interceptor that modifies the request headers to enable
 * 'Basic' authentication. It takes in the credentials for authentication and
 * appends it to the 'Authorization' request headers for 'Basic' authentication.
 *
 */
public class HTTPBasicAuthInterceptor implements Interceptor {

  String authValue;

  HTTPBasicAuthInterceptor(Credentials credentials) {
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
