/*
 * Copyright (c) 2019 MarkLogic Corporation
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
