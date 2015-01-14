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
 * A Document Descriptor describes a database document.
 */
public interface DocumentDescriptor extends ContentDescriptor {
	/**
	 * Indicates that the version of the database document is not known.
	 */
	static final public long UNKNOWN_VERSION = -1;

	/**
	 * Returns the URI identifier for the database document.
	 * @return	the document URI
	 */
	public String getUri();
	/**
	 * Specifies the URI identifier for a database document.
	 * @param uri	the document URI
	 */
	public void setUri(String uri);

	/**
	 * Specifies the format for a database document and
	 * returns the descriptor object
	 * @param format	the document format
	 * @return	the descriptor object
	 */
	public DocumentDescriptor withFormat(Format format);

	/**
	 * Returns the version for the database document.  Each update
	 * creates a new version of a document.  Version numbering can be
	 * used to refresh a client document cache or for optimistic locking.
	 * Use {@link com.marklogic.client.admin.ServerConfigurationManager}
	 * to enable versioning on content.
	 * @return	the document version number
	 */
	public long getVersion();
	/**
	 * Specifies the document version.  Checking the existence
	 * of a document or reading a document specifies the document version
	 * if you have enabled versioning on content.
	 * @param version	the document version number
	 */
	public void setVersion(long version);
}
