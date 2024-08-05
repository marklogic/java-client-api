/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.admin;

import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Resource Extensions Manager supports writing, reading, and deleting
 * a Resource Services extension as well as listing the installed
 * Resource Services extensions.  A Resource Services extension implements
 * server operations on a kind of database resource not supported
 * by default.
 */
public interface ResourceExtensionsManager {
  /**
   * Reads the list of resource service extensions installed on the server
   * in a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether to provide the list in a JSON or XML representation
   * @param as	the IO class for reading the list of resource service extensions
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the list of resource service extensions
   */
  <T> T listServicesAs(Format format, Class<T> as);
  /**
   * Reads the list of resource service extensions installed on the server
   * in a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether to provide the list in a JSON or XML representation
   * @param as	the IO class for reading the list of resource service extensions
   * @param refresh	whether to parse metadata from the extension source
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the list of resource service extensions
   */
  <T> T listServicesAs(Format format, Class<T> as, boolean refresh);
  /**
   * Reads the list of resource service extensions installed on the server.
   * @param listHandle	a handle on a JSON or XML representation of the list
   * @param <T> the type of StructureReadHandle to return
   * @return	the list handle
   */
  <T extends StructureReadHandle> T listServices(T listHandle);
  /**
   * Reads the list of resource service extensions installed on the server,
   * specifying whether to refresh the metadata about each extension by parsing
   * the extension source.
   * @param listHandle	a handle on a JSON or XML representation of the list
   * @param refresh	whether to parse metadata from the extension source
   * @param <T> the type of StructureReadHandle to return
   * @return	the list handle
   */
  <T extends StructureReadHandle> T listServices(T listHandle, boolean refresh);

  /**
   * Reads the XQuery implementation of the services for a resource
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param resourceName	the name of the resource
   * @param as	the IO class for reading the source code as text
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the source code for the service
   */
  <T> T readServicesAs(String resourceName, Class<T> as);

  /**
   * Reads the XQuery implementation of the services for a resource.
   * @param resourceName	the name of the resource
   * @param sourceHandle	a handle for reading the text of the XQuery implementation.
   * @param <T> the type of TextReadHandle to return
   * @return	the XQuery source code
   */
  <T extends TextReadHandle> T readServices(String resourceName, T sourceHandle);

  /**
   * Installs the services that implement a resource
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param resourceName	the name of the resource
   * @param source	an IO representation of the source code
   * @param metadata	the metadata about the resource services
   * @param methodParams	a declaration of the parameters for the services
   */
  void writeServicesAs(
    String resourceName, Object source, ExtensionMetadata metadata, MethodParameters... methodParams
  );

  /**
   * Installs the services that implement a resource.
   * @param resourceName	the name of the resource
   * @param sourceHandle	a handle on the source for the XQuery implementation
   * @param metadata	the metadata about the resource services
   * @param methodParams	a declaration of the parameters for the services
   */
  void writeServices(String resourceName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, MethodParameters... methodParams);

  /**
   * Uninstalls the services that implement a resource.
   * @param resourceName	the name of the resource
   */
  void deleteServices(String resourceName);

  /**
   * Starts debugging client requests. You can suspend and resume debugging output
   * using the methods of the logger.
   *
   * @param logger	the logger that receives debugging output
   */
  void startLogging(RequestLogger logger);
  /**
   *  Stops debugging client requests.
   */
  void stopLogging();

  /**
   * Method Parameters declare the parameters accepted
   * by the Resource Services extension.
   */
  public class MethodParameters extends RequestParameters {
    private MethodType method;

    /**
     * Declares the parameters for a method the provides services for a resource.
     * @param method	the method type
     */
    public MethodParameters(MethodType method) {
      super();
      this.method = method;
    }

    /**
     * Returns the method for the parameters.
     * @return	the method type
     */
    public MethodType getMethod() {
      return method;
    }

    /**
     * Returns the hash code for the method.
     */
    @Override
    public int hashCode() {
      return getMethod().hashCode();
    }
    /**
     * Returns whether the method declaration is the same.
     */
    @Override
    public boolean equals(Object other) {
      if (other == null)
        return false;
      if (!(other instanceof MethodParameters))
        return false;

      MethodParameters otherParam = (MethodParameters) other;
      if (!getMethod().equals(otherParam.getMethod()))
        return false;

      return super.equals(otherParam);
    }
  }
}
