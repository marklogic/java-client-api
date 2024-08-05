/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.admin;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

/**
 * A QueryOptionsManager support database operations on QueryOptionsHandle instances.
 *
 */
public interface QueryOptionsManager {
  /**
   * Retrieves the list of available named query options in a JSON or
   * XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether to provide the list in a JSON or XML representation
   * @param as	the IO class for reading the list of options
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the option names
   */
  <T> T optionsListAs(Format format, Class<T> as)
    throws ForbiddenUserException, FailedRequestException;
  /**
   * Retrieves the list of available named query options in a JSON or
   * XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, standard Java IO classes for document content are registered.
   *
   * @param listHandle a handle for reading the list of name options
   * @param <T> the type of QueryOptionsListReadHandle to return
   * @return the handle populated with the names
   */
  <T extends QueryOptionsListReadHandle> T optionsList(T listHandle)
    throws ForbiddenUserException, FailedRequestException;

  /**
   * Fetch a query options configuration from the REST Server by name.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param name the name of options configuration stored on MarkLogic REST instance.
   * @param format	whether to provide the options in a JSON or XML representation
   * @param as	the IO class for reading the query options
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return an object of the IO class with the query options
   */
  <T> T readOptionsAs(String name, Format format, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  /**
   * Fetch a query options configuration from the REST Server by name.
   * <p>
   * Use a QueryOptionsHandle object for access to the configuration with Java.
   *
   * @param name the name of options configuration stored on MarkLogic REST instance.
   * @param queryOptionsHandle an object into which to fetch the query options.
   * @param <T> the type of QueryOptionsListReadHandle to return
   * @return an object holding the query configurations
   */
  <T extends QueryOptionsReadHandle> T readOptions(String name, T queryOptionsHandle)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Write a named QueryOptions configuration to the REST server in a JSON or
   * XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param name name given to the QueryOptions for use in runtime queries
   * @param format	whether the options are provided in a JSON or XML representation
   * @param queryOptions	an IO representation of the JSON or XML query options
   */
  void writeOptionsAs(String name, Format format, Object queryOptions)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Write a named QueryOptions configuration to the REST server.
   * @param name name given to the QueryOptions for use in runtime queries
   * @param queryOptionsHandle an object able to serialize a QueryOptions configuration
   */
  void writeOptions(String name, QueryOptionsWriteHandle queryOptionsHandle)
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException;

  /**
   * Remove a search configuration from the REST server.
   * @param name name of query options to remove from the REST server.
   */
  void deleteOptions(String name)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
}
