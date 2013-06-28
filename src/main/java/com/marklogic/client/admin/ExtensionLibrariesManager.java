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
package com.marklogic.client.admin;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * ExtensionLibrariesManager provides methods to read, list and update server-side XQuery modules
 * that reside in the REST instanance's modules database.  It can be used for any assets or code 
 * that an application needs to store on the server as part of the server-side logic.
 *
 */
public interface ExtensionLibrariesManager {

	/**
	 * Lists all of the library files that are installed on the server.
	 * @return An array of ExtensionLibraryDescriptor objects.
	 */
	public ExtensionLibraryDescriptor[] list()
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	
	/**
	 * Lists all of the library files in one directory (infinite depth) on the server.
	 * @param directory The directory to list.
	 * @return An array of ExtensionLibraryDescriptor objects.
	 */
	public ExtensionLibraryDescriptor[] list(String directory)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
	 * Reads the contents of a library asset into a handle.
	 * @param libraryPath The path to the library.
	 * @param readHandle A handle for reading the contents of the file.
	 * @return The handle.
	 */
	public <T extends AbstractReadHandle> T read(String libraryPath, T readHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
	 * Reads the contents of a library asset into a handle.
	 * @param  libraryDescriptor a descriptor that locates the library.
	 * @param readHandle A handle for reading the contents of the file.
	 * @return The handle.
	 */
	public <T extends AbstractReadHandle> T read(
			ExtensionLibraryDescriptor libraryDescriptor, T readHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
	 * Writes the contents of a handle to the provided path on the REST server.
	 * @param libraryPath The path at which to install the library.
	 * @param contentHandle The handle containing the contents of the library.
	 */
	public void write(String libraryPath, AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

	/**
	 * Writes the contents of a handle to the provided path on the REST server.
	 * @param libraryDescriptor The descriptory which locates where to install the library.
	 * @param contentHandle The handle containing the contents of the library.
	 */
	public void write(ExtensionLibraryDescriptor libraryDescriptor, AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

	/**
	 * Removes a library asset from the server.
	 * @param libraryPath The path to the library to delete.
	 */
	public void delete(String libraryPath)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
	 * Removes a library asset from the server.
	 * @param modulesDescriptor A descriptor locating the library to delete.
	 */
	public void delete(ExtensionLibraryDescriptor modulesDescriptor)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
}
