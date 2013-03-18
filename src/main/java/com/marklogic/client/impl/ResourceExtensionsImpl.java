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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.io.Format;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;


@SuppressWarnings({"unchecked", "rawtypes"})
class ResourceExtensionsImpl
	extends AbstractLoggingManager
	implements ResourceExtensionsManager
{
	static final private Logger logger = LoggerFactory.getLogger(ResourceExtensionsImpl.class);

	private RESTServices services;

	ResourceExtensionsImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public <T extends StructureReadHandle> T listServices(T listHandle) {
		if (listHandle == null)
			throw new IllegalArgumentException("null handle for listing resource services");

		if (logger.isInfoEnabled())
			logger.info("Reading resource services list");

		HandleImplementation listBase = HandleAccessor.checkHandle(listHandle, "resource");

		Format listFormat = listBase.getFormat();
		if (!(Format.JSON == listFormat || Format.XML == listFormat))
			throw new IllegalArgumentException(
					"list handle for unsupported format: "+listFormat.getClass().getName());

		listBase.receiveContent(
				services.getValues(requestLogger, "config/resources", listFormat.getDefaultMimetype(), listBase.receiveAs())
				);

		return listHandle;
	}

	@Override
	public <T extends TextReadHandle> T readServices(String resourceName, T sourceHandle) {
		if (resourceName == null)
			throw new IllegalArgumentException("Reading resource services source with null name");

		if (logger.isInfoEnabled())
			logger.info("Reading resource services source for {}", resourceName);

		HandleImplementation sourceBase =
			HandleAccessor.checkHandle(sourceHandle, "resource");

		sourceBase.receiveContent(
				services.getValue(requestLogger, "config/resources", resourceName, true,
				"application/xquery", sourceBase.receiveAs())
				);

		return sourceHandle;
	}

	@Override
	public void writeServices(
		String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, MethodParameters... methodParams
	) {
		if (resourceName == null)
			throw new IllegalArgumentException("Writing resource services with null name");
		if (methodParams == null || methodParams.length == 0)
			throw new IllegalArgumentException("Writing resource services with no methods");

		if (logger.isInfoEnabled())
			logger.info("Writing resource services source for {}", resourceName);

		HandleImplementation sourceBase =
			HandleAccessor.checkHandle(sourceHandle, "resource");

		RequestParameters extraParams =
			(metadata != null) ? metadata.asParameters() : new RequestParameters();
			for (MethodParameters params : methodParams) {
				String method = params.getMethod().toString().toLowerCase();
				extraParams.add("method", method);
				String prefix = method+":";
				for (Map.Entry<String,List<String>> entry: params.entrySet()) {
					extraParams.put(prefix+entry.getKey(), entry.getValue());
				}
			}

		services.putValue(requestLogger, "config/resources", resourceName, extraParams,
				"application/xquery", sourceBase);
	}

	@Override
	public void deleteServices(String resourceName) {
		if (resourceName == null)
			throw new IllegalArgumentException("Deleting resource services with null name");

		if (logger.isInfoEnabled())
			logger.info("Deleting resource services for {}", resourceName);

		services.deleteValue(requestLogger, "config/resources", resourceName);
	}
}
