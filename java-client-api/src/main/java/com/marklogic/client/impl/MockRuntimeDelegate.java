/*
 * Copyright 2012-2017 MarkLogic Corporation
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

import okhttp3.Cookie;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/** We just need this no-implmentation place-holder so that we can instantiate NewCookie
 *  without needing Jersey on the classpath.
 */
public class MockRuntimeDelegate extends RuntimeDelegate {
  @Override
  public UriBuilder createUriBuilder() {
    return null;
  }

  @Override
  public Response.ResponseBuilder createResponseBuilder() {
    return null;
  }

  @Override
  public Variant.VariantListBuilder createVariantListBuilder() {
    return null;
  }

  @Override
  public <T> T createEndpoint(Application application, Class<T> aClass)
    throws IllegalArgumentException, UnsupportedOperationException
  {
    return null;
  }

  @Override
  public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> aClass) throws IllegalArgumentException {
    return null;
  }

  @Override
  public Link.Builder createLinkBuilder() {
    return null;
  }
}
