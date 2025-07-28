/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.admin;

import java.util.Map;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A Transform Extensions Manager supports writing, reading, and deleting
 * a Transform extension as well as listing the installed
 * Transform extensions.  A Transform extension implements conversion
 * of content on the server.  For instance, a Transform extension can
 * convert HTML documents to XHTML documents on write or XML documents
 * to HTML documents on read.
 */
public interface TransformExtensionsManager {
  /**
   * Reads the list of transform extensions installed on the server
   * in a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether to provide the list in a JSON or XML representation
   * @param as	the IO class for reading the list of transform extensions
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the list of transform extensions
   */
  <T> T listTransformsAs(Format format, Class<T> as);
  /**
   * Reads the list of transform extensions installed on the server
   * in a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether to provide the list in a JSON or XML representation
   * @param as	the IO class for reading the list of transform extensions
   * @param refresh	whether to parse metadata from the extension source
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the list of transform extensions
   */
  <T> T listTransformsAs(Format format, Class<T> as, boolean refresh);

  /**
   * Lists the installed transform extensions.
   * @param listHandle	a handle on a JSON or XML representation of the list
   * @param <T> the type of StructureReadHandle to return
   * @return	the list handle
   */
  <T extends StructureReadHandle> T listTransforms(T listHandle)
    throws ForbiddenUserException, FailedRequestException;
  /**
   * Lists the installed transform extensions, specifying whether to refresh
   * the metadata about each extension by parsing the extension source.
   * @param listHandle	a handle on a JSON or XML representation of the list
   * @param refresh	whether to parse metadata from the extension source
   * @param <T> the type of StructureReadHandle to return
   * @return	the list handle
   */
  <T extends StructureReadHandle> T listTransforms(T listHandle, boolean refresh)
    throws ForbiddenUserException, FailedRequestException;

  /**
   * Reads the source for a transform implemented in XSLT
   * in an XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param as	the IO class for reading the source code as XML
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the XSLT source code
   */
  <T> T readXSLTransformAs(String transformName, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads the source for a transform implemented in XSLT.
   * @param transformName	the name of the transform
   * @param sourceHandle	a handle for reading the text of the XSLT implementation.
   * @param <T> the type of XMLReadHandle to return
   * @return	the XSLT source code
   */
  <T extends XMLReadHandle> T readXSLTransform(String transformName, T sourceHandle)
    throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;

  /**
   * Reads the source for a transform implemented in XQuery
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param as	the IO class for reading the source code as text
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the XQuery source code
   */
  <T> T readXQueryTransformAs(String transformName, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads the source for a transform implemented in XQuery.
   * @param transformName	the name of the transform
   * @param sourceHandle	a handle for reading the text of the XQuery implementation.
   * @param <T> the type of TextReadHandle to return
   * @return	the XQuery source code
   */
  <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle)
    throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;

  /**
   * Reads the source for a transform implemented in Javascript
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param as	the IO class for reading the source code as text
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return	an object of the IO class with the Javascript source code
   */
  <T> T readJavascriptTransformAs(String transformName, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  /**
   * Reads the source for a transform implemented in Javascript.
   * @param transformName	the name of the transform
   * @param sourceHandle	a handle for reading the text of the Javascript implementation.
   * @param <T> the type of TextReadHandle to return
   * @return	the Javascript source code
   */
  <T extends TextReadHandle> T readJavascriptTransform(String transformName, T sourceHandle)
    throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;


  /**
   * Installs a transform implemented in XSL
   * in an XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param source	an IO representation of the source code
   */
  void writeXSLTransformAs(String transformName, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in XSL
   * in an XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param metadata	the metadata about the transform
   * @param source	an IO representation of the source code
   */
  void writeXSLTransformAs(String transformName, ExtensionMetadata metadata, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Installs a transform implemented in XSL.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the XSL implementation
   */
  void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in XSL.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the XSL implementation
   * @param metadata	the metadata about the transform
   */
  void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Installs a transform implemented in XQuery
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param source	an IO representation of the source code
   */
  void writeXQueryTransformAs(String transformName, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in XQuery
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param metadata	the metadata about the transform
   * @param source	an IO representation of the source code
   */
  void writeXQueryTransformAs(String transformName, ExtensionMetadata metadata, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Installs a transform implemented in XQuery.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the XQuery implementation
   */
  void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in XQuery.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the XQuery implementation
   * @param metadata	the metadata about the transform
   */
  void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Installs a transform implemented in XQuery
   * in a textual representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param source	an IO representation of the source code
   */

  void writeJavascriptTransformAs(String transformName, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in Javascript
   * in a textual representation provided as an object of an IO class.
   *
   *The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param transformName	the name of the transform
   * @param metadata	the metadata about the transform
   * @param source	an IO representation of the source code
   */
  void writeJavascriptTransformAs(String transformName, ExtensionMetadata metadata, Object source)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Installs a transform implemented in Javascript.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the Javascript implementation
   */
  void writeJavascriptTransform(String transformName, TextWriteHandle sourceHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  /**
   * Installs a transform implemented in Javascript.
   * @param transformName	the name of the resource
   * @param sourceHandle	a handle on the source for the Javascript implementation
   * @param metadata	the metadata about the transform
   */
  void writeJavascriptTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  /**
   * Uninstalls the transform.
   * @param transformName	the name of the transform
   */
  void deleteTransform(String transformName)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

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
}
