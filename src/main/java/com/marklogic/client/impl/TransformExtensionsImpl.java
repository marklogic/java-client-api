/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.io.Format;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;


@SuppressWarnings({"unchecked", "rawtypes"})
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
	public <T extends StructureReadHandle> T listTransforms(T listHandle)
	throws ForbiddenUserException, FailedRequestException {
		return listTransforms(listHandle, true);
	}
	@Override
	public <T extends StructureReadHandle> T listTransforms(T listHandle, boolean refresh)
	throws ForbiddenUserException, FailedRequestException {
		if (listHandle == null)
			throw new IllegalArgumentException("Reading transform list with null handle");

		if (logger.isInfoEnabled())
			logger.info("Reading transform list");

		HandleImplementation listBase = HandleAccessor.checkHandle(listHandle, "transform");

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
				services.getValues(requestLogger, "config/transforms", extraParams,
						listFormat.getDefaultMimetype(), listBase.receiveAs())
				);

		return listHandle;
	}

	@Override
	public <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readTransform(transformName, "application/xquery", sourceHandle);
	}
	@Override
	public <T extends XMLReadHandle> T readXSLTransform(String transformName,T sourceHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readTransform(transformName, "application/xslt+xml", sourceHandle);
	}
	private <T extends AbstractReadHandle> T readTransform(
		String transformName, String sourceMimetype, T sourceHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (transformName == null)
			throw new IllegalArgumentException("Reading transform with null name");

		if (logger.isInfoEnabled())
			logger.info("Reading transform source for {}", transformName);

		HandleImplementation sourceBase =
			HandleAccessor.checkHandle(sourceHandle, "transform");

		sourceBase.receiveContent(
				services.getValue(requestLogger, "config/transforms", transformName,
						true, sourceMimetype, sourceBase.receiveAs())
				);

		return sourceHandle;
	}

	@Override
	public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXQueryTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXQueryTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXQueryTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeTransform(transformName, "application/xquery", sourceHandle, metadata, paramTypes);
	}
	@Override
	public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXSLTransform(transformName, sourceHandle, null, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXSLTransform(transformName, sourceHandle, metadata, null);
	}
	@Override
	public void writeXSLTransform(
		String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeTransform(transformName, "application/xslt+xml", sourceHandle, metadata, paramTypes);
	}
	private void writeTransform(
		String transformName, String sourceMimetype, AbstractWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String, String> paramTypes)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		if (transformName == null)
			throw new IllegalArgumentException("Writing transform with null name");
		if (sourceHandle == null)
			throw new IllegalArgumentException("Writing transform source with null handle");

		if (logger.isInfoEnabled())
			logger.info("Writing transform source for {}", transformName);

		HandleImplementation sourceBase = HandleAccessor.checkHandle(sourceHandle, "transform");

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

		services.putValue(requestLogger, "config/transforms", transformName, extraParams,
				sourceMimetype, sourceBase);
	}

	@Override
	public void deleteTransform(String transformName)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (transformName == null)
			throw new IllegalArgumentException("Deleting transform with null name");

		if (logger.isInfoEnabled())
			logger.info("Deleting transform for {}", transformName);

		services.deleteValue(requestLogger, "config/transforms", transformName);
	}
}
