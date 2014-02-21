package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.admin.ExtensionLibraryDescriptor.Permission;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.util.RequestParameters;

public class ExtensionLibrariesManagerImpl
	extends AbstractLoggingManager
	implements ExtensionLibrariesManager
{
    private RESTServices          services;
	private HandleFactoryRegistry handleRegistry;
	
	public ExtensionLibrariesManagerImpl(RESTServices services) {
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
	public ExtensionLibraryDescriptor[] list()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return list("/ext/");
	}
	@Override
	public ExtensionLibraryDescriptor[] list(String directory)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		
		XMLEventReaderHandle handle = services.getResource(requestLogger, directory, null, new XMLEventReaderHandle());
		
		XMLEventReader reader = handle.get();
		List<ExtensionLibraryDescriptor> modules = new ArrayList<ExtensionLibraryDescriptor>();
		while (reader.hasNext()) {
				XMLEvent event;
				try {
					event = reader.nextEvent();
				if (event.isCharacters()) {
					String modulePath = event.asCharacters().getData();
					ExtensionLibraryDescriptor module = new ExtensionLibraryDescriptor();
					module.setPath(modulePath);
					modules.add(module);
				}
				} catch (XMLStreamException e) {
					throw new MarkLogicIOException(e);
				}
				
			}
		return modules.toArray(new ExtensionLibraryDescriptor[] {});
	}

	@Override
	public <T> T readAs(String modulePath, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		read(modulePath, handle);
		return handle.get();
	}
	@Override
	public <T> T read(ExtensionLibraryDescriptor modulesDescriptor, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
		read(modulesDescriptor, handle);
		return handle.get();
	}
	@Override
	public <T extends AbstractReadHandle> T read(String modulePath, T readHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return services.getResource(requestLogger, modulePath, null, readHandle);
	}
	@Override
	public <T extends AbstractReadHandle> T read(ExtensionLibraryDescriptor modulesDescriptor, T readHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		return read(modulesDescriptor.getPath(), readHandle);
	}

	@Override
	public void writeAs(String modulePath, Object content)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		write(modulePath, getContentHandle(content));
	}
	@Override
	public void writeAs(ExtensionLibraryDescriptor modulesDescriptor, Object content)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		write(modulesDescriptor, getContentHandle(content));
	}
	private AbstractWriteHandle getContentHandle(Object content) {
		if (content == null) {
			throw new IllegalArgumentException("no content to write");
		}

		Class<?> as = content.getClass();

		AbstractWriteHandle contentHandle = null;
		if (AbstractWriteHandle.class.isAssignableFrom(as)) {
			contentHandle = (AbstractWriteHandle) content;
		} else {
			ContentHandle<?> handle = getHandleRegistry().makeHandle(as);
			Utilities.setHandleContent(handle, content);
			contentHandle = handle;
		}

		return contentHandle;
	}

	@Override
	public void write(String modulePath, AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		services.putResource(requestLogger, modulePath, null, contentHandle, null);
	}
	@Override
	public void write(ExtensionLibraryDescriptor modulesDescriptor, AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {
		RequestParameters requestParams = new RequestParameters();
		for (Permission perm : modulesDescriptor.getPermissions()) {
			requestParams.add("perm:" + perm.getRoleName(), perm.getCapability());
		}
		services.putResource(requestLogger, modulesDescriptor.getPath(), requestParams, contentHandle, null);
	}

	@Override
	public void delete(String modulePath)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		services.deleteResource(requestLogger, modulePath, null, null);
	}
	@Override
	public void delete(ExtensionLibraryDescriptor modulesDescriptor)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		delete(modulesDescriptor.getPath());
	}
}
