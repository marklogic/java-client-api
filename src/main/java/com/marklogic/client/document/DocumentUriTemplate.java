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
package com.marklogic.client.document;

import com.marklogic.client.io.Format;

/**
 * A DocumentUriTemplate specifies how the server should construct
 * a name for a document.
 */
public interface DocumentUriTemplate extends ContentDescriptor {
    /**
     * Returns the directory that should prefix the document uri.
     * @return	the directory.
     */
	public String getDirectory();
    /**
     * Specifies the directory that should prefix the document uri.
     * @param directory	the directory.
     */
	public void setDirectory(String directory);
    /**
     * Specifies the directory that should prefix the document uri
     * and returns the template object.
     * @param directory	the directory.
     * @return	the template object.
     */
	public DocumentUriTemplate withDirectory(String directory);
    /**
     * Returns the extension that should suffix the document uri.
     * @return	the extension.
     */
    public String getExtension();
    /**
     * Specifies the extension that should suffix the document uri.
     * The extension should not start with the period separator.
     * @param extension	the extension.
     */
	public void setExtension(String extension);
	/**
	 * Specifies the format of the document
     * and returns the template object.
	 * @param format	the format.
     * @return	the template object.
	 */
	public DocumentUriTemplate withFormat(Format format);
}
