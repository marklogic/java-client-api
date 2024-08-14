/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
  ExtensionLibraryDescriptor[] list()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  /**
   * Lists all of the library files in one directory (infinite depth) on the server.
   * @param directory The directory to list.
   * @return An array of ExtensionLibraryDescriptor objects.
   */
  ExtensionLibraryDescriptor[] list(String directory)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads the contents of a library asset as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param libraryPath the path to the library
   * @param as	the IO class for reading the library asset
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the library asset
   */
  <T> T readAs(String libraryPath, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  /**
   * Reads the contents of a library asset as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param  libraryDescriptor a descriptor that locates the library
   * @param as	the IO class for reading the library asset
   * @param <T> the type of AbstractReadHandle to return
   * @return	an object of the IO class with the library asset
   */
  <T> T read(ExtensionLibraryDescriptor libraryDescriptor, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads the contents of a library asset into a handle.
   * @param libraryPath the path to the library
   * @param readHandle a handle for reading the contents of the file
   * @param <T> the type of AbstractReadHandle to return
   * @return the handle for the library asset
   */
  <T extends AbstractReadHandle> T read(String libraryPath, T readHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  /**
   * Reads the contents of a library asset into a handle.
   * @param  libraryDescriptor a descriptor that locates the library.
   * @param readHandle A handle for reading the contents of the file.
   * @param <T> the type of AbstractReadHandle to return
   * @return The handle.
   */
  <T extends AbstractReadHandle> T read(ExtensionLibraryDescriptor libraryDescriptor, T readHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Writes the contents of a handle to the provided path on the REST server
   * as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param libraryPath The path at which to install the library.
   * @param content	an IO representation of the library asset
   */
  void writeAs(String libraryPath, Object content)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Writes the contents of a handle to the provided path on the REST server
   * as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param libraryDescriptor The descriptory which locates where to install the library.
   * @param content	an IO representation of the library asset
   */
  void writeAs(ExtensionLibraryDescriptor libraryDescriptor, Object content)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Writes the contents of a handle to the provided path on the REST server.
   * @param libraryPath The path at which to install the library.
   * @param contentHandle The handle containing the contents of the library.
   */
  void write(String libraryPath, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Writes the contents of a handle to the provided path on the REST server.
   * @param libraryDescriptor The descriptory which locates where to install the library.
   * @param contentHandle The handle containing the contents of the library.
   */
  void write(ExtensionLibraryDescriptor libraryDescriptor, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Removes a library asset from the server.
   * @param libraryPath The path to the library to delete.
   */
  void delete(String libraryPath)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  /**
   * Removes a library asset from the server.
   * @param libraryDescriptor A descriptor locating the library to delete.
   */
  void delete(ExtensionLibraryDescriptor libraryDescriptor)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
}
