/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client;

import java.util.Iterator;

import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * A ResourceServices object supports calling the services for a resource.
 * The Resource Services extension must have been installed on the server
 * previously, which can be done with 
 * {@link com.marklogic.client.ResourceExtensionsManager}.
 * A {@link com.marklogic.client.ResourceManager} object
 * receives a ResourceServices object when it is initialized by the
 * {@link com.marklogic.client.DatabaseClient}.init() method.
 */
public interface ResourceServices {
	public String getResourceName();

	public <R extends AbstractReadHandle> R get(RequestParameters params, R output);
	public <R extends AbstractReadHandle> R get(RequestParameters params, Transaction transaction, R output);

	public ServiceResultIterator get(RequestParameters params, String... mimetypes);
	public ServiceResultIterator get(RequestParameters params, Transaction transaction, String... mimetypes);

	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output);
	public <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output);

	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output);
	public <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output);
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output);

	public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, String... mimetypes);
	public ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, String... mimetypes);
	public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, String... mimetypes);
	public <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, Transaction transaction, String... mimetypes);

	public <R extends AbstractReadHandle> R delete(RequestParameters params, R output);
	public <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output);

	// for debugging client requests
    public void startLogging(RequestLogger logger);
    public RequestLogger getRequestLogger();
    public void stopLogging();

    public interface ServiceResult {
		public abstract Format getFormat();
		public abstract String getMimetype();
		public abstract long   getLength();
		public <R extends AbstractReadHandle> R getContent(R handle);
	}
	public interface ServiceResultIterator extends Iterator<ServiceResult> {
		public void close();
	}
}
