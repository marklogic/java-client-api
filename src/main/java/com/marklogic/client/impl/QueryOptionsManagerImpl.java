/*
 * Copyright 2012-2013 MarkLogic Corporation
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

import com.marklogic.client.io.Format;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public class QueryOptionsManagerImpl extends AbstractLoggingManager implements
		QueryOptionsManager {

	final static private String QUERY_OPTIONS_BASE = "/config/query";
	private RESTServices services;

	public QueryOptionsManagerImpl(RESTServices services) {
		this.services = services;
	}

	@Override
	public void deleteOptions(String name) {
		services.deleteValue(null, QUERY_OPTIONS_BASE, name);
	}

	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends QueryOptionsReadHandle> T readOptions(String name,
			T queryOptionsHandle) {
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

	@SuppressWarnings("rawtypes")
	@Override
	public void writeOptions(String name,
			QueryOptionsWriteHandle queryOptionsHandle) {
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
}
