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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.util.RequestParameters;

@SuppressWarnings({"unchecked", "rawtypes"})
class ResourceExtensionsImpl
	extends AbstractLoggingManager
	implements ResourceExtensionsManager
{
	static final private Logger logger = LoggerFactory.getLogger(ResourceExtensionsImpl.class);

    private RESTServices          services;
	private HandleFactoryRegistry handleRegistry;

	ResourceExtensionsImpl(RESTServices services) {
		super();
		this.services = services;
	}

	HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
		this.handleRegistry = handleRegistry;
	}

    @Override
    public <T> T listServicesAs(Format format, Class<T> as) {
		return listServicesAs(format, as, true);
    }
    @Override
    public <T> T listServicesAs(Format format, Class<T> as, boolean refresh) {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!StructureReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to list resource services as "+as.getName()
					);
		}

		Utilities.setHandleStructuredFormat(handle, format);

		listServices((StructureReadHandle) handle, refresh);

		return handle.get();
    }

    @Override
	public <T extends StructureReadHandle> T listServices(T listHandle) {
		return listServices(listHandle, true);
	}
	@Override
	public <T extends StructureReadHandle> T listServices(T listHandle, boolean refresh) {
		if (listHandle == null)
			throw new IllegalArgumentException("null handle for listing resource services");

		if (logger.isInfoEnabled())
			logger.info("Reading resource services list");

		HandleImplementation listBase = HandleAccessor.checkHandle(listHandle, "resource");

		Format listFormat = listBase.getFormat();
		if (!(Format.JSON == listFormat || Format.XML == listFormat))
			throw new IllegalArgumentException(
					"list handle for unsupported format: "+listFormat.getClass().getName());

		RequestParameters extraParams = null;
		if (!refresh) {
			extraParams = new RequestParameters();
			extraParams.put("refresh", "false");
		}

		listBase.receiveContent(
				services.getValues(requestLogger, "config/resources", extraParams,
						listFormat.getDefaultMimetype(), listBase.receiveAs())
				);

		return listHandle;
	}

	@Override
	public <T> T readServicesAs(String resourceName, Class<T> as) {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!TextReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read resource service source as "+as.getName()
					);
		}

		readServices(resourceName, (TextReadHandle) handle);

		return handle.get();
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
				sourceBase.getMimetype(), sourceBase.receiveAs())
				);

		return sourceHandle;
	}

	@Override
	public void writeServicesAs(
		String resourceName, Object source, ExtensionMetadata metadata, MethodParameters... methodParams
	) {
		if (source == null) {
			throw new IllegalArgumentException("no source to write");
		}

		Class<?> as = source.getClass();

		TextWriteHandle sourceHandle = null;
		if (TextWriteHandle.class.isAssignableFrom(as)) {
			sourceHandle = (TextWriteHandle) source;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			if (!TextWriteHandle.class.isAssignableFrom(handle.getClass())) {
				throw new IllegalArgumentException(
						"Handle "+handle.getClass().getName()+
						" cannot be used to write resource service source as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, source);
			sourceHandle = (TextWriteHandle) handle;
		}

		writeServices(resourceName, sourceHandle, metadata, methodParams);
	}
	@Override
	public void writeServices(
		String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, MethodParameters... methodParams
	) {
		if (resourceName == null)
			throw new IllegalArgumentException("Writing resource services with null name");

		if (logger.isInfoEnabled())
			logger.info("Writing resource services source for {}", resourceName);

		HandleImplementation sourceBase =
			HandleAccessor.checkHandle(sourceHandle, "resource");

		RequestParameters extraParams =
			(metadata != null) ? metadata.asParameters() : new RequestParameters();
		if (methodParams != null) {
			for (MethodParameters params : methodParams) {
				String method = params.getMethod().toString().toLowerCase();
				extraParams.add("method", method);
				String prefix = method+":";
				for (Map.Entry<String,List<String>> entry: params.entrySet()) {
					extraParams.put(prefix+entry.getKey(), entry.getValue());
				}
			}
		}
		String contentType = null;
		if ( metadata == null ) {
		} else if ( metadata.getScriptLanguage() == null ) {
			throw new IllegalArgumentException("scriptLanguage cannot be null");
		} else if ( metadata.getScriptLanguage() == ScriptLanguage.JAVASCRIPT ) {
			contentType = "application/vnd.marklogic-javascript";
		} else if ( metadata.getScriptLanguage() == ScriptLanguage.XQUERY ) {
			contentType = "application/xquery";
		} 

		services.putValue(requestLogger, "config/resources", resourceName, extraParams,
				contentType, sourceBase);
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
