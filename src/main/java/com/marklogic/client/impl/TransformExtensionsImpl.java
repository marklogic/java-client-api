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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
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
import com.marklogic.client.io.marker.ContentHandle;
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

    private RESTServices          services;
	private HandleFactoryRegistry handleRegistry;

	TransformExtensionsImpl(RESTServices services) {
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
    public <T> T listTransformsAs(Format format, Class<T> as) {
		return listTransformsAs(format, as, true);
    }
    @Override
    public <T> T listTransformsAs(Format format, Class<T> as, boolean refresh) {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!StructureReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to list transforms as "+as.getName()
					);
		}

		Utilities.setHandleStructuredFormat(handle, format);

		listTransforms((StructureReadHandle) handle, refresh);

		return handle.get();
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
	public <T> T readXQueryTransformAs(String transformName, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!TextReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read transform source as "+as.getName()
					);
		}

		readXQueryTransform(transformName, (TextReadHandle) handle);

		return handle.get();
	}
	@Override
	public <T> T readXSLTransformAs(String transformName, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!XMLReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read transform source as "+as.getName()
					);
		}

		readXSLTransform(transformName, (XMLReadHandle) handle);

		return handle.get();
	}
	
	@Override
	public <T> T readJavascriptTransformAs(String transformName, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		if (!TextReadHandle.class.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException(
					"Handle "+handle.getClass().getName()+
					" cannot be used to read transform source as "+as.getName()
					);
		}

		readJavascriptTransform(transformName, (TextReadHandle) handle);

		return handle.get();
	}

	@Override
	public <T extends TextReadHandle> T readJavascriptTransform(String transformName, T sourceHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return readTransform(transformName, "application/javascript", sourceHandle);
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
	public void writeXQueryTransformAs(String transformName, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXQueryTransformAs(transformName, null, source);
	}
	@Override
	public void writeXQueryTransformAs(String transformName, ExtensionMetadata metadata, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
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
						" cannot be used to write transform source as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, source);
			sourceHandle = (TextWriteHandle) handle;
		}

		writeXQueryTransform(transformName, sourceHandle, metadata);
	}
	@Override
	public void writeXSLTransformAs(String transformName, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeXSLTransformAs(transformName, null, source);
	}
	@Override
	public void writeXSLTransformAs(String transformName, ExtensionMetadata metadata, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeTextHandleTransformAs(transformName, metadata, source, "xquery");
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
	public void writeJavascriptTransformAs(String transformName, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeJavascriptTransformAs(transformName, null, source);
	}
	@Override
	public void writeJavascriptTransformAs(String transformName, ExtensionMetadata metadata, Object source)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {		
		writeTextHandleTransformAs(transformName, metadata, source, "javascript");
	}
	
	// This method used by writeJavascriptTransformAs and writeXQueryTransformAs
	private void writeTextHandleTransformAs(String transformName, ExtensionMetadata metadata, Object source, String transformType)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
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
						" cannot be used to write transform source as "+as.getName()
						);
			}
			Utilities.setHandleContent(handle, source);
			sourceHandle = (TextWriteHandle) handle;
		}

		if (transformType.equals("javascript")) {
		  writeJavascriptTransform(transformName, sourceHandle, metadata);
		}
		else if (transformType.equals("xquery")) {
		  writeXQueryTransform(transformName, sourceHandle, metadata);			
		}
	}
	
	@Override
	public void writeJavascriptTransform(String transformName, TextWriteHandle sourceHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeTransform(transformName, "application/javascript", sourceHandle, null, null);
	}
	@Override
	public void writeJavascriptTransform(
		String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		writeTransform(transformName, "application/javascript", sourceHandle, metadata, null);
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
		} else if ("application/javascript".equals(sourceMimetype)) {
			if (Format.JSON != sourceFormat)
				sourceBase.setFormat(Format.JSON);
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
