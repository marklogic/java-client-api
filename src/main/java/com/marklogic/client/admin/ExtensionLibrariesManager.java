package com.marklogic.client.admin;

import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

public interface ExtensionLibrariesManager {

	/**
	 * Lists all of the library files that are installed on the server.
	 * @return An array of ExtensionLibraryDescriptor objects.
	 */
	public ExtensionLibraryDescriptor[] list();
	
	/**
	 * Lists all of the library files in one directory (infinite depth) on the server.
	 * @param directory The directory to list.
	 * @return An array of ExtensionLibraryDescriptor objects.
	 */
	public ExtensionLibraryDescriptor[] list(String directory);

	/**
	 * Reads the contents of a library asset into a handle.
	 * @param libraryPath The path to the library.
	 * @param readHandle A handle for reading the contents of the file.
	 * @return The handle.
	 */
	public <T extends AbstractReadHandle> T read(String libraryPath, T readHandle);

	/**
	 * Reads the contents of a library asset into a handle.
	 * @param  libraryDescriptor a descriptor that locates the library.
	 * @param readHandle A handle for reading the contents of the file.
	 * @return The handle.
	 */
	public <T extends AbstractReadHandle> T read(
			ExtensionLibraryDescriptor libraryDescriptor, T readHandle);

	/**
	 * Writes the contents of a handle to the provided path on the REST server.
	 * @param libraryPath The path at which to install the library.
	 * @param contentHandle The handle containing the contents of the library.
	 */
	public void write(String libraryPath, AbstractWriteHandle contentHandle);

	/**
	 * Writes the contents of a handle to the provided path on the REST server.
	 * @param libraryDescriptor The descriptory which locates where to install the library.
	 * @param contentHandle The handle containing the contents of the library.
	 */
	public void write(ExtensionLibraryDescriptor libraryDescriptor,
			AbstractWriteHandle contentHandle);

	/**
	 * Removes a library asset from the server.
	 * @param libraryPath The path to the library to delete.
	 */
	public void delete(String libraryPath);

	/**
	 * Removes a library asset from the server.
	 * @param modulesDescriptor A descriptor locating the library to delete.
	 */
	public void delete(ExtensionLibraryDescriptor modulesDescriptor);
}
