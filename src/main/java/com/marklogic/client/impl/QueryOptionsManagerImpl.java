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
package com.marklogic.client.impl;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Format;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public class QueryOptionsManagerImpl extends AbstractLoggingManager implements
		QueryOptionsManager {

	private String QUERY_OPTIONS_BASE = "/config/query";
	private RESTServices services;

	public QueryOptionsManagerImpl(RESTServices services) {
		this.services = services;
	}

	@Override
	public void deleteOptions(String name) {
		services.deleteValue(null, QUERY_OPTIONS_BASE, name);
	}

	@Override
	public <T extends QueryOptionsReadHandle> T readOptions(String name,
			T queryOptionsHandle) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Cannot read options for null name");
		}

		BaseHandle queryOptionsBase = HandleAccessor.checkHandle(
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
		HandleAccessor.receiveContent(queryOptionsHandle, services.getValue(
				requestLogger, QUERY_OPTIONS_BASE, name, mimetype,
				HandleAccessor.receiveAs(queryOptionsHandle)));

		return queryOptionsHandle;
	}

	@Override
	public void writeOptions(String name,
			QueryOptionsWriteHandle queryOptionsHandle) {
		BaseHandle queryOptionsBase = HandleAccessor.checkHandle(
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

		services.putValue(requestLogger, QUERY_OPTIONS_BASE, name, mimetype,
				HandleAccessor.sendContent(queryOptionsHandle));
	}
}
