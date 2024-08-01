/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.Transaction;
import com.marklogic.client.extensions.ResourceServices;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;

class ResourceServicesImpl
  extends AbstractLoggingManager
  implements ResourceServices
{
  private String       resourceName;
  private RESTServices services;

  ResourceServicesImpl(RESTServices services, String resourceName) {
    super();
    this.services     = services;
    this.resourceName = resourceName;
  }

  @Override
  public String getResourceName() {
    return resourceName;
  }
  private String getResourcePath() {
    return "resources/"+getResourceName();
  }

  @Override
  public <R extends AbstractReadHandle> R get(RequestParameters params, R output) {
    return get(params, null, output);
  }
  @Override
  public <R extends AbstractReadHandle> R get(RequestParameters params, Transaction transaction, R output) {
    return services.getResource(requestLogger, getResourcePath(),
      transaction, prepareParams(params), output);
  }
  @Override
  public ServiceResultIterator get(RequestParameters params) {
    return get(params, (Transaction)null);
  }
  @Override
  public ServiceResultIterator get(RequestParameters params, Transaction transaction) {
    return services.getIteratedResource(requestLogger, getResourcePath(), transaction, prepareParams(params));
  }

  @Override
  public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output) {
    return put(params, input, null, output);
  }
  @Override
  public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
    return services.putResource(requestLogger, getResourcePath(),
      transaction, prepareParams(params), input, output);
  }
  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output) {
    return put(params, input, null, output);
  }
  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output) {
    return services.putResource(requestLogger, getResourcePath(),
      transaction, prepareParams(params), input, output);
  }

  @Override
  public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output) {
    return post(params, input, null, output);
  }
  @Override
  public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
    return services.postResource(
      requestLogger, getResourcePath(), transaction, prepareParams(params), input, output);
  }
  @Override
  public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input) {
    return post(params, input, (Transaction)null);
  }
  @Override
  public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, Transaction transaction) {
    return services.postIteratedResource(
      requestLogger, getResourcePath(), transaction, prepareParams(params), input);
  }
  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output) {
    return post(params, input, null, output);
  }
  @Override
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output) {
    return services.postResource(
      requestLogger, getResourcePath(), transaction, prepareParams(params), input, output);
  }
  @Override
  public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input) {
    return post(params, input, (Transaction) null);
  }
  @Override
  public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, Transaction transaction) {
    return services.postIteratedResource(
      requestLogger, getResourcePath(), transaction, prepareParams(params), input);
  }

  @Override
  public <R extends AbstractReadHandle> R delete(RequestParameters params, R output) {
    return delete(params, null, output);
  }
  @Override
  public <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output) {
    return services.deleteResource(requestLogger,
      getResourcePath(), transaction, prepareParams(params), output);
  }

  @Override
  public RequestLogger getRequestLogger() {
    return requestLogger;
  }

  private RequestParameters prepareParams(RequestParameters params) {
    return params != null ? params.copy("rs") : null;
  }
}
