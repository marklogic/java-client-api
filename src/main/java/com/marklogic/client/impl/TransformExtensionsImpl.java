package com.marklogic.client.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.Format;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.TransformExtensionsManager;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

public class TransformExtensionsImpl
	extends AbstractLoggingManager
	implements TransformExtensionsManager
{
	static final private Logger logger = LoggerFactory.getLogger(TransformExtensionsImpl.class);

	private RESTServices services;

	public TransformExtensionsImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public <T extends StructureReadHandle> T listTransforms(T listHandle) {
		logger.info("Reading transform list");

		HandleAccessor.checkHandle(listHandle, "transform");

		Format listFormat = HandleAccessor.getFormat(listHandle);
		if (!(Format.JSON == listFormat || Format.XML == listFormat))
			throw new IllegalArgumentException(
					"list handle for unsupported format: "+listFormat.getClass().getName());

		HandleAccessor.receiveContent(
				listHandle,
				services.getValues(requestLogger, "config/transforms", listFormat.getDefaultMimetype(), HandleAccessor.receiveAs(listHandle))
				);

		return listHandle;
	}

	@Override
	public <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle) {
		return readTransform(transformName, "application/xquery", sourceHandle);
	}
	@Override
	public <T extends XMLReadHandle> T readXSLTransform(String transformName,T sourceHandle) {
		return readTransform(transformName, "application/xslt+xml", sourceHandle);
	}
	private <T extends AbstractReadHandle> T readTransform(
		String transformName, String sourceMimetype, T sourceHandle
	) {
		if (transformName == null)
			throw new IllegalArgumentException("Reading transform with null name");

		logger.info("Reading transform source for {}", transformName);

		HandleAccessor.checkHandle(sourceHandle, "transform");

		HandleAccessor.receiveContent(
				sourceHandle,
				services.getValue(requestLogger, "config/transforms", transformName, sourceMimetype, HandleAccessor.receiveAs(sourceHandle))
				);

		return sourceHandle;
	}

	@Override
	public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle) {
		writeTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata
	) {
		writeTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		writeTransform(transformName, sourceHandle, metadata, paramTypes);
	}
	@Override
	public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle) {
		writeTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata
	) {
		writeTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		writeTransform(transformName, sourceHandle, metadata, paramTypes);
	}
	private void writeTransform(
		String transformName, AbstractWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		if (transformName == null)
			throw new IllegalArgumentException("Writing transform with null name");

		logger.info("Writing transform source for {}", transformName);

		HandleAccessor.checkHandle(sourceHandle, "transform");

		Format sourceFormat = HandleAccessor.getFormat(sourceHandle);
		String sourceMimetype = null;
		if (sourceFormat.TEXT == sourceFormat) {
			sourceMimetype = "application/xquery";
		} else if (sourceFormat.XML == sourceFormat) {
			sourceMimetype = "application/xslt+xml";
		} else {
			throw new IllegalArgumentException(
				"source handle for unsupported format: "+sourceFormat.getClass().getName());
		}

		RequestParameters extraParams = (metadata != null) ? metadata.asParameters() : null;
		if (paramTypes != null) {
			if (extraParams == null)
				extraParams = new RequestParameters();
			for (Map.Entry<String,String> entry: paramTypes.entrySet()) {
				extraParams.put("trans:"+entry.getKey(), entry.getValue());
			}
		}

		services.putValue(requestLogger, "config/transforms", transformName, extraParams, sourceMimetype, HandleAccessor.sendContent(sourceHandle));
	}

	@Override
	public void deleteTransform(String transformName) {
		if (transformName == null)
			throw new IllegalArgumentException("Deleting transform with null name");

		logger.info("Deleting transform for {}", transformName);

		services.deleteValue(requestLogger, "config/transforms", transformName);
	}
}
