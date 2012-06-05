package com.marklogic.client.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.TransformExtensionsManager;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

class TransformExtensionsImpl
	extends AbstractLoggingManager
	implements TransformExtensionsManager
{
	static final private Logger logger = LoggerFactory.getLogger(TransformExtensionsImpl.class);

	private RESTServices services;

	TransformExtensionsImpl(RESTServices services) {
		super();
		this.services = services;
	}

	@Override
	public <T extends StructureReadHandle> T listTransforms(T listHandle) {
		if (listHandle == null)
			throw new IllegalArgumentException("Reading transform list with null handle");

		logger.info("Reading transform list");

		BaseHandle listBase = HandleAccessor.checkHandle(listHandle, "transform");

		Format listFormat = listBase.getFormat();
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
		writeXQueryTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata
	) {
		writeXQueryTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		writeTransform(transformName, "application/xquery", sourceHandle, metadata, paramTypes);
	}
	@Override
	public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle) {
		writeXSLTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata
	) {
		writeXSLTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		writeTransform(transformName, "application/xslt+xml", sourceHandle, metadata, paramTypes);
	}
	private void writeTransform(
		String transformName, String sourceMimetype, AbstractWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes
	) {
		if (transformName == null)
			throw new IllegalArgumentException("Writing transform with null name");
		if (sourceHandle == null)
			throw new IllegalArgumentException("Writing transform source with null handle");

		logger.info("Writing transform source for {}", transformName);

		BaseHandle sourceBase = HandleAccessor.checkHandle(sourceHandle, "transform");

		Format sourceFormat = sourceBase.getFormat();
		if ("application/xquery".equals(sourceMimetype)) {
			if (Format.TEXT != sourceFormat)
				sourceBase.setFormat(Format.TEXT);
		} else if ("application/xslt+xml".equals(sourceMimetype)) {
			if (Format.XML != sourceFormat)
				sourceBase.setFormat(Format.XML);
		} else {
			throw new MarkLogicInternalException(
				"Unsupported mimetype for source: "+sourceMimetype);
		}
		if (!sourceMimetype.equals(sourceBase.getMimetype()))
			sourceBase.setMimetype(sourceMimetype);

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
