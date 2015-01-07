/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.admin.ExtensionMetadata;
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
				prepareParams(params, transaction), output);
	}
	@Override
	public ServiceResultIterator get(RequestParameters params, String... outputMimetypes) {
		return get(params, null, outputMimetypes);
	}
	@Override
	public ServiceResultIterator get(RequestParameters params, Transaction transaction, String... outputMimetypes) {
		return services.getIteratedResource(requestLogger, getResourcePath(), 
				prepareParams(params, transaction), outputMimetypes);
	}

	@Override
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output) {
		return put(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
		return services.putResource(requestLogger, getResourcePath(), 
				prepareParams(params, transaction), input, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output) {
		return put(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output) {
		return services.putResource(requestLogger, getResourcePath(),
				prepareParams(params, transaction), input, output);
	}

	@Override
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output) {
		return services.postResource(
				requestLogger, getResourcePath(), prepareParams(params, transaction), input, output);
	}
	@Override
	public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, String... outputMimetypes) {
		return post(params, input, null, outputMimetypes);
	}
	@Override
	public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, String... outputMimetypes) {
		return services.postIteratedResource(
				requestLogger, getResourcePath(), prepareParams(params, transaction), input, outputMimetypes);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output) {
		return post(params, input, null, output);
	}
	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output) {
		return services.postResource(
				requestLogger, getResourcePath(), prepareParams(params, transaction), input, output);
	}
	@Override
	public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, String... outputMimetypes) {
		return post(params, input, null, outputMimetypes);
	}
	@Override
	public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, Transaction transaction, String... outputMimetypes) {
		return services.postIteratedResource(
				requestLogger, getResourcePath(), prepareParams(params, transaction), input, outputMimetypes);
	}

	@Override
	public <R extends AbstractReadHandle> R delete(RequestParameters params, R output) {
		return delete(params, null, output);
	}
	@Override
	public <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output) {
		return services.deleteResource(requestLogger,
				getResourcePath(), prepareParams(params, transaction), output);
	}

	@Override
    public RequestLogger getRequestLogger() {
    	return requestLogger;
    }

	private RequestParameters prepareParams(RequestParameters params, Transaction transaction) {
		if (params == null && transaction == null)
			return null;
		if (transaction == null)
			return params.copy("rs");

		RequestParameters requestParams =
			(params != null) ? params.copy("rs") : new RequestParameters();
		requestParams.add("txid", transaction.getTransactionId());

		return requestParams;
	}
}
