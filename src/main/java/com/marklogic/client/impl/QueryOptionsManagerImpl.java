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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public class QueryOptionsManagerImpl extends AbstractLoggingManager implements
		QueryOptionsManager {

	static final private Logger logger = LoggerFactory
			.getLogger(AbstractDocumentImpl.class);

	private String QUERY_OPTIONS_BASE = "/config/query";
	private RESTServices services;

	public QueryOptionsManagerImpl(RESTServices services) {
		this.services = services;
	}

	@Override
	public void deleteOptions(String name) {
		services.deleteValue(null, QUERY_OPTIONS_BASE, name);
	}

	/*
	 * @Override public QueryOptions newOptions() {
	 * 
	 * QueryOptionsHandle handle = new QueryOptionsHandle(); return handle; }
	 */

	@Override
	public <T extends QueryOptionsReadHandle> T readOptions(String name,
			T searchOptionsHandle) {
		if (name == null)
			throw new IllegalArgumentException(
					"Cannot read options for null name");

		searchOptionsHandle.receiveContent(services.getValue(requestLogger,
				QUERY_OPTIONS_BASE, name, searchOptionsHandle.getFormat().getDefaultMimetype(),
				searchOptionsHandle.receiveAs()));
		return searchOptionsHandle;
	}

	@Override
	public void writeOptions(String name,
			QueryOptionsWriteHandle queryOptionsHandle) {
		services.putValue(requestLogger, QUERY_OPTIONS_BASE, name,
				queryOptionsHandle.getFormat().getDefaultMimetype(), queryOptionsHandle.sendContent());
	}
}
