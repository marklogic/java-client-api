package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.admin.ExtensionLibraryDescriptor.Permission;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.util.RequestParameters;

public class ExtensionLibrariesManagerImpl extends AbstractLoggingManager implements ExtensionLibrariesManager {

	private RESTServices services;
	
	public ExtensionLibrariesManagerImpl(RESTServices services) {
		this.services = services;
	}
	@Override
	public ExtensionLibraryDescriptor[] list() {
		return list("/ext/");
	}

	@Override
	public ExtensionLibraryDescriptor[] list(String directory) {
		
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
	public <T extends AbstractReadHandle> T read(String modulePath, T readHandle) {
		return services.getResource(requestLogger, modulePath, null, readHandle);
	}

	@Override
	public <T extends AbstractReadHandle> T read(
			ExtensionLibraryDescriptor modulesDescriptor, T readHandle) {
		return read(modulesDescriptor.getPath(), readHandle);
	}

	@Override
	public void write(String modulePath, AbstractWriteHandle contentHandle) {
		services.putResource(requestLogger, modulePath, null, contentHandle, null);
	}

	@Override
	public void write(ExtensionLibraryDescriptor modulesDescriptor,
			AbstractWriteHandle contentHandle) {
		RequestParameters requestParams = new RequestParameters();
		for (Permission perm : modulesDescriptor.getPermissions()) {
			requestParams.add("perm:" + perm.getRoleName(), perm.getCapability());
		}
		services.putResource(requestLogger, modulesDescriptor.getPath(), requestParams, contentHandle, null);
	}

	@Override
	public void delete(String modulePath) {
		services.deleteResource(requestLogger, modulePath, null, null);
	}

	@Override
	public void delete(ExtensionLibraryDescriptor modulesDescriptor) {
		delete(modulesDescriptor.getPath());
	}

}
