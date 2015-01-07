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
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether to provide the list in a JSON or XML representation
     * @param as	the IO class for reading the list of transform extensions
	 * @return	an object of the IO class with the list of transform extensions
     */
    public <T> T listTransformsAs(Format format, Class<T> as);
    /**
	 * Reads the list of transform extensions installed on the server
	 * in a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether to provide the list in a JSON or XML representation
     * @param as	the IO class for reading the list of transform extensions
     * @param refresh	whether to parse metadata from the extension source
	 * @return	an object of the IO class with the list of transform extensions
     */
    public <T> T listTransformsAs(Format format, Class<T> as, boolean refresh);

	/**
	 * Lists the installed transform extensions.
	 * @param listHandle	a handle on a JSON or XML representation of the list
	 * @return	the list handle
	 */
	public <T extends StructureReadHandle> T listTransforms(T listHandle)
	throws ForbiddenUserException, FailedRequestException;
	/**
	 * Lists the installed transform extensions, specifying whether to refresh
	 * the metadata about each extension by parsing the extension source.
	 * @param listHandle	a handle on a JSON or XML representation of the list
     * @param refresh	whether to parse metadata from the extension source
	 * @return	the list handle
	 */
	public <T extends StructureReadHandle> T listTransforms(T listHandle, boolean refresh)
		throws ForbiddenUserException, FailedRequestException;

	/**
     * Reads the source for a transform implemented in XSLT
	 * in an XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param as	the IO class for reading the source code as XML
     * @return	an object of the IO class with the XSLT source code
	 */
	public <T> T readXSLTransformAs(String transformName, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
     * Reads the source for a transform implemented in XSLT.
     * @param transformName	the name of the transform
     * @param sourceHandle	a handle for reading the text of the XSLT implementation.
     * @return	the XSLT source code
	 */
    public <T extends XMLReadHandle> T readXSLTransform(String transformName, T sourceHandle)
    	throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;

    /**
     * Reads the source for a transform implemented in XQuery
	 * in a textual representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param as	the IO class for reading the source code as text
     * @return	an object of the IO class with the XQuery source code
     */
	public <T> T readXQueryTransformAs(String transformName, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

	/**
     * Reads the source for a transform implemented in XQuery.
     * @param transformName	the name of the transform
     * @param sourceHandle	a handle for reading the text of the XQuery implementation.
     * @return	the XQuery source code
	 */
	public <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle)
    	throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;
	
  /**
   * Reads the source for a transform implemented in Javascript
   * in a textual representation provided as an object of an IO class.
   * 
   * The IO class must have been registered before creating the database client.
   * By default, standard Java IO classes for document content are registered.
   * 
   * @param transformName	the name of the transform
   * @param as	the IO class for reading the source code as text
   * @return	an object of the IO class with the Javascript source code
   */
public <T> T readJavascriptTransformAs(String transformName, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

/**
   * Reads the source for a transform implemented in Javascript.
   * @param transformName	the name of the transform
   * @param sourceHandle	a handle for reading the text of the Javascript implementation.
   * @return	the Javascript source code
 */
public <T extends TextReadHandle> T readJavascriptTransform(String transformName, T sourceHandle)
  	throws FailedRequestException, ResourceNotFoundException, ForbiddenUserException;
	
	
	/**
     * Installs a transform implemented in XSL
	 * in an XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param source	an IO representation of the source code
	 */
	public void writeXSLTransformAs(String transformName, Object source)
		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
	/**
     * Installs a transform implemented in XSL
	 * in an XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param metadata	the metadata about the transform
     * @param source	an IO representation of the source code
	 */
	public void writeXSLTransformAs(String transformName, ExtensionMetadata metadata, Object source)
		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     */
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     * @param metadata	the metadata about the transform
     */
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     * @param metadata	the metadata about the transform
     * @param paramTypes	the names and XML Schema datatypes of the transform parameters
     */
    @Deprecated
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

	/**
     * Installs a transform implemented in XQuery
	 * in a textual representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param source	an IO representation of the source code
	 */
	public void writeXQueryTransformAs(String transformName, Object source)
		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
	/**
     * Installs a transform implemented in XQuery
	 * in a textual representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param transformName	the name of the transform
     * @param metadata	the metadata about the transform
     * @param source	an IO representation of the source code
	 */
	public void writeXQueryTransformAs(String transformName, ExtensionMetadata metadata, Object source)
		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     */
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     * @param metadata	the metadata about the transform
     */
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     * @param metadata	the metadata about the transform
     * @param paramTypes	the names and XML Schema datatypes of the transform parameters
     */
    @Deprecated
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

  	/**
       * Installs a transform implemented in XQuery
  	 * in a textual representation provided as an object of an IO class.
       * 
       * The IO class must have been registered before creating the database client.
       * By default, standard Java IO classes for document content are registered.
       * 
       * @param transformName	the name of the transform
       * @param source	an IO representation of the source code
  	 */
    
  	public void writeJavascriptTransformAs(String transformName, Object source)
  		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  	/**
       * Installs a transform implemented in Javascript
   	   * in a textual representation provided as an object of an IO class.
       * 
       * The IO class must have been registered before creating the database client.
       * By default, standard Java IO classes for document content are registered.
       * 
       * @param transformName	the name of the transform
       * @param metadata	the metadata about the transform
       * @param source	an IO representation of the source code
  	 */
  	public void writeJavascriptTransformAs(String transformName, ExtensionMetadata metadata, Object source)
  		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

    /**
     * Installs a transform implemented in Javascript.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the Javascript implementation
     */
    public void writeJavascriptTransform(String transformName, TextWriteHandle sourceHandle)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Installs a transform implemented in Javascript.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the Javascript implementation
     * @param metadata	the metadata about the transform
     */
    public void writeJavascriptTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;

    /**
     * Uninstalls the transform.
     * @param transformName	the name of the transform
     */
    public void deleteTransform(String transformName)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Starts debugging client requests. You can suspend and resume debugging output
     * using the methods of the logger.
     * 
     * @param logger	the logger that receives debugging output
     */
    public void startLogging(RequestLogger logger);
    /**
     *  Stops debugging client requests.
     */
    public void stopLogging();
}
