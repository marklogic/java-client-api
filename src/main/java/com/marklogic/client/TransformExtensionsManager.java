/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client;

import java.util.Map;

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
	 * Lists the installed transform extensions
	 * @param listHandle	a handle on a JSON or XML representation of the list
	 * @return	the list handle
	 */
	public <T extends StructureReadHandle> T listTransforms(T listHandle);

	/**
     * Reads the source for a transform implemented in XQuery.
     * @param transformName	the name of the transform
     * @param sourceHandle	a handle for reading the text of the XQuery implementation.
     * @return	the XQuery source code
	 */
	public <T extends TextReadHandle> T readXQueryTransform(String transformName, T sourceHandle);
	/**
     * Reads the source for a transform implemented in XSLT.
     * @param transformName	the name of the transform
     * @param sourceHandle	a handle for reading the text of the XSLT implementation.
     * @return	the XSLT source code
	 */
    public <T extends XMLReadHandle> T readXSLTransform(String transformName, T sourceHandle);

    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     */
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle);
    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     * @param metadata	the metadata about the transform
     */
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata);
    /**
     * Installs a transform implemented in XQuery.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XQuery implementation
     * @param metadata	the metadata about the transform
     * @param paramTypes	the names and XML Schema datatypes of the transform parameters
     */
    public void writeXQueryTransform(String transformName, TextWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes);

    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     */
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle);
    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     * @param metadata	the metadata about the transform
     */
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata);
    /**
     * Installs a transform implemented in XSL.
     * @param transformName	the name of the resource
     * @param sourceHandle	a handle on the source for the XSL implementation
     * @param metadata	the metadata about the transform
     * @param paramTypes	the names and XML Schema datatypes of the transform parameters
     */
    public void writeXSLTransform(String transformName, XMLWriteHandle sourceHandle, ExtensionMetadata metadata, Map<String,String> paramTypes);

    /**
     * Uninstalls the transform.
     * @param transformName	the name of the transform
     */
    public void deleteTransform(String transformName);

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
