/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extensions;

import java.util.Iterator;

import com.marklogic.client.io.Format;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

/**
 * A ResourceServices object supports calling the services for a resource.
 * The Resource Services extension must have been installed on the server
 * previously, which can be done with
 * {@link com.marklogic.client.admin.ResourceExtensionsManager}.
 * A {@link ResourceManager} object
 * receives a ResourceServices object when it is initialized by the
 * {@link com.marklogic.client.DatabaseClient}.init() method.
 */
public interface ResourceServices {
  /**
   * Returns the name of the resource.
   * @return	the resource name
   */
  String getResourceName();

  /**
   * Reads resource content by calling a GET service.
   * @param params	the parameters for the call
   * @param output	a handle on the content returned by the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle
   */
  <R extends AbstractReadHandle> R get(RequestParameters params, R output);
  /**
   * Reads resource content by calling a GET service.
   * @param params	the parameters for the call
   * @param transaction	the transaction for reading content
   * @param output	a handle on the content returned by the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle
   */
  <R extends AbstractReadHandle> R get(RequestParameters params, Transaction transaction, R output);

  /**
   * Reads multiple resource content by calling a GET service.
   * @param params	the parameters for the call
   * @return	an iterator over the requested content
   */
  ServiceResultIterator get(RequestParameters params);
  /**
   * Reads multiple resource content by calling a GET service.
   * @param params	the parameters for the call
   * @param transaction	the transaction for reading content
   * @return	an iterator over the requested content
   */
  ServiceResultIterator get(RequestParameters params, Transaction transaction);

  /**
   * Writes content by calling a PUT service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, R output);
  /**
   * Writes content by calling a PUT service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @param transaction	the transaction for writing content
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R put(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
  /**
   * Writes multiple content by calling a PUT service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, R output);
  /**
   * Writes multiple content by calling a PUT service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param transaction	the transaction for writing content
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R put(RequestParameters params, W[] input, Transaction transaction, R output);

  /**
   * Applies content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, R output);
  /**
   * Applies content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @param transaction	the transaction for applying content
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R post(RequestParameters params, AbstractWriteHandle input, Transaction transaction, R output);
  /**
   * Applies content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, R output);
  /**
   * Applies content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param transaction	the transaction for applying content
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R post(RequestParameters params, W[] input, Transaction transaction, R output);

  /**
   * Applies multiple content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @return	an iterator over the requested content
   */
  ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input);
  /**
   * Applies multiple content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	the content passed with the call
   * @param transaction	the transaction for applying content
   * @return	an iterator over the requested content
   */
  ServiceResultIterator post(RequestParameters params, AbstractWriteHandle input, Transaction transaction);
  /**
   * Applies multiple content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	an iterator over the requested content
   */
  <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input);
  /**
   * Applies multiple content by calling a POST service.
   * @param params	the parameters for the call
   * @param input	an array of content passed with the call
   * @param transaction	the transaction for applying content
   * @param <W> the type of AbstractWriteHandle's with content to send
   * @return	an iterator over the requested content
   */
  <W extends AbstractWriteHandle> ServiceResultIterator post(RequestParameters params, W[] input, Transaction transaction);

  /**
   * Deletes content by calling a DELETE service.
   * @param params	the parameters for the call
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R delete(RequestParameters params, R output);
  /**
   * Deletes content by calling a DELETE service.
   * @param params	the parameters for the call
   * @param transaction	the transaction for applying content
   * @param output	a handle on the content response from the call
   * @param <R> the type of AbstractReadHandle to return
   * @return	the content handle for the response
   */
  <R extends AbstractReadHandle> R delete(RequestParameters params, Transaction transaction, R output);

  /**
   * Starts debugging client requests. You can suspend and resume debugging output
   * using the methods of the logger.
   *
   * @param logger	the logger that receives debugging output
   */
  void startLogging(RequestLogger logger);
  /**
   * Returns the logger for debugging client requests.
   * @return	the request logger
   */
  RequestLogger getRequestLogger();
  /**
   *  Stops debugging client requests.
   */
  void stopLogging();

  /**
   * ServiceResult provides one content response from a service.
   */
  interface ServiceResult {
    /**
     * Returns the format of the content.
     * @return	the content format
     */
    abstract Format getFormat();
    /**
     * Returns the mimetype of the content.
     * @return	the content mimetype
     */
    abstract String getMimetype();
    /**
     * Returns the length of the content in bytes.
     * @return	the byte length
     */
    abstract long   getLength();
    /**
     * Returns a handle on the content.
     * @param handle	the content handle
     * @param <R> the type of AbstractReadHandle to return
     * @return	the content handle
     */
    <R extends AbstractReadHandle> R getContent(R handle);
  }
  /**
   * ServiceResultIterator provides an iterator over content responses
   * from the server.
   */
  interface ServiceResultIterator extends Iterator<ServiceResult> {
    /**
     * Closes the iterator when no longer needed.
     */
    void close();
  }
}
