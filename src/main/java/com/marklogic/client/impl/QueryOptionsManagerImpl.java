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

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public class QueryOptionsManagerImpl
	extends AbstractLoggingManager
	implements QueryOptionsManager
{
	final static private String QUERY_OPTIONS_BASE = "/config/query";
    private RESTServices          services;
	private HandleFactoryRegistry handleRegistry;

	public QueryOptionsManagerImpl(RESTServices services) {
		this.services = services;
	}

	HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

	@Override
	public void deleteOptions(String name)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		services.deleteValue(null, QUERY_OPTIONS_BASE, name);
	}

	@Override
	public <T> T readOptionsAs(String name, Format format, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!QueryOptionsReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read resource service source as "+as.getName()
					);
		}

		Utilities.setHandleStructuredFormat(handle, format);

		readOptions(name, (QueryOptionsReadHandle) handle);

		return handle.get();
	}
	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends QueryOptionsReadHandle> T readOptions(String name, T queryOptionsHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (name == null) {
			throw new IllegalArgumentException(
					"Cannot read options for null name");
		}

		HandleImplementation queryOptionsBase = HandleAccessor.checkHandle(
				queryOptionsHandle, "query options");

		Format queryOptionsFormat = queryOptionsBase.getFormat();
		switch (queryOptionsFormat) {
		case UNKNOWN:
			queryOptionsFormat = Format.XML;
			break;
		case JSON:
		case XML:
			break;
		default:
			throw new UnsupportedOperationException(
					"Only JSON and XML query options are possible.");
		}

		String mimetype = queryOptionsFormat.getDefaultMimetype();
		queryOptionsBase.receiveContent(services.getValue(
				requestLogger, QUERY_OPTIONS_BASE, name, false, mimetype,
				queryOptionsBase.receiveAs()));

		return queryOptionsHandle;
	}

	@Override
	public void writeOptionsAs(String name, Format format, Object queryOptions)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		if (queryOptions == null) {
			throw new IllegalArgumentException("no options to write");
		}

		Class<?> as = queryOptions.getClass();

		QueryOptionsWriteHandle queryOptionsHandle = null;
		if (QueryOptionsWriteHandle.class.isAssignableFrom(as)) {
			queryOptionsHandle = (QueryOptionsWriteHandle) queryOptions;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			if (!QueryOptionsWriteHandle.class.isAssignableFrom(handle.getClass())) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used to write query options as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, queryOptions);
			Utilities.setHandleStructuredFormat(handle, format);
			queryOptionsHandle = (QueryOptionsWriteHandle) handle;
		}

		writeOptions(name, queryOptionsHandle);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public void writeOptions(String name, QueryOptionsWriteHandle queryOptionsHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		HandleImplementation queryOptionsBase = HandleAccessor.checkHandle(
				queryOptionsHandle, "query options");

		if (queryOptionsBase == null)
			throw new IllegalArgumentException("Could not write null options: "
					+ name);

		Format queryOptionsFormat = queryOptionsBase.getFormat();
		switch (queryOptionsFormat) {
		case UNKNOWN:
			queryOptionsFormat = Format.XML;
			break;
		case JSON:
		case XML:
			break;
		default:
			throw new UnsupportedOperationException(
					"Only JSON and XML query options are possible.");
		}

		String mimetype = queryOptionsFormat.getDefaultMimetype();

		services.putValue(requestLogger, QUERY_OPTIONS_BASE, name, mimetype, queryOptionsBase);
	}

	@Override
    public <T> T optionsListAs(Format format, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!QueryOptionsListReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to list query options as "+as.getName()
					);
		}

		Utilities.setHandleStructuredFormat(handle, format);

		optionsList((QueryOptionsListReadHandle) handle);

		return handle.get();
    }
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle)
	throws ForbiddenUserException, FailedRequestException {
		HandleImplementation optionsBase = HandleAccessor.checkHandle(optionsHandle, "optionslist");
		
		Format optionsFormat = optionsBase.getFormat();
		switch(optionsFormat) {
		case UNKNOWN:
			optionsFormat = Format.XML;
			break;
		case JSON:
		case XML:
			break;
		default:
			throw new UnsupportedOperationException("Only XML and JSON options list results are possible.");
		}

		String mimetype = optionsFormat.getDefaultMimetype();

		optionsBase.receiveContent(services.optionsList(optionsBase.receiveAs(), mimetype, null));
		return optionsHandle;
	}
}
