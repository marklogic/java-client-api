package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.Format;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceExtensionsManager;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

public class ResourceExtensionsImpl
	extends AbstractLoggingManager
	implements ResourceExtensionsManager
{
	static final private Logger logger = LoggerFactory.getLogger(ResourceExtensionsImpl.class);

	private RESTServices services;

	public ResourceExtensionsImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public <T extends StructureReadHandle> T listServices(T listHandle) {
		logger.info("Reading resource services list");

		HandleAccessor.checkHandle(listHandle, "resource");

		Format listFormat = HandleAccessor.getFormat(listHandle);
		if (!(Format.JSON == listFormat || Format.XML == listFormat))
			throw new IllegalArgumentException(
					"list handle for unsupported format: "+listFormat.getClass().getName());

		HandleAccessor.receiveContent(
				listHandle,
				services.getValues(requestLogger, "config/resources", listFormat.getDefaultMimetype(), HandleAccessor.receiveAs(listHandle))
				);

		return listHandle;
	}

	@Override
	public <T extends TextReadHandle> T readServices(String resourceName, T sourceHandle) {
		if (resourceName == null)
			throw new IllegalArgumentException("Reading resource services source with null name");

		logger.info("Reading resource services source for {}", resourceName);

		HandleAccessor.checkHandle(sourceHandle, "resource");

		HandleAccessor.receiveContent(
				sourceHandle,
				services.getValue(requestLogger, "config/resources", resourceName, "application/xquery", HandleAccessor.receiveAs(sourceHandle))
				);

		return sourceHandle;
	}

	@Override
	public void writeServices(String resourceName, TextWriteHandle sourceHandle) {
		writeServices(resourceName, sourceHandle, null, (MethodParameters[]) null);
	}
	@Override
	public void writeServices(
		String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata
	) {
		writeServices(resourceName, sourceHandle, metadata, (MethodParameters[]) null);
	}
	@Override
	public void writeServices(
		String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, MethodParameters... methodParams
	) {
		if (resourceName == null)
			throw new IllegalArgumentException("Writing resource services with null name");

		logger.info("Writing resource services source for {}", resourceName);

		HandleAccessor.checkHandle(sourceHandle, "resource");

		RequestParameters extraParams = (metadata != null) ? metadata.asParameters() : null;
		if (methodParams != null) {
			if (extraParams == null)
				extraParams = new RequestParameters();
			for (MethodParameters params : methodParams) {
				String method = params.getMethod().toString().toLowerCase();
				extraParams.add("method", method);
				String prefix = method+":";
				for (Map.Entry<String,List<String>> entry: params.entrySet()) {
					extraParams.put(prefix+entry.getKey(), entry.getValue());
				}
			}
		}

		services.putValue(requestLogger, "config/resources", resourceName, extraParams, "application/xquery", HandleAccessor.sendContent(sourceHandle));
	}

	@Override
	public void deleteServices(String resourceName) {
		if (resourceName == null)
			throw new IllegalArgumentException("Deleting resource services with null name");

		logger.info("Deleting resource services for {}", resourceName);

		services.deleteValue(requestLogger, "config/resources", resourceName);
	}
}
