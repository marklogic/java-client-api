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
 * A Content Descriptor identifies the properties of some content.
 */
public interface ContentDescriptor {
	/**
	 * Indicates that the length of the content is not known.
	 */
	final static public long UNKNOWN_LENGTH = -1;

	/**
	 * Returns the format of the content.
	 * @return	the content format
	 */
	public Format getFormat();
	/**
	 * Specifies the format of the content as binary, JSON, text, or XML.
	 * @param format	the format of the content
	 */
	public void setFormat(Format format);

	/**
	 * Returns the mimetype of the content.
	 * @return	the content mimetype
	 */
	public String getMimetype();
	/**
	 * Specifies the mimetype of the content such as application/json,
	 * text/plain, or application/xml.
	 * @param mimetype	the content mimetype
	 */
	public void setMimetype(String mimetype);

	/** 
	 * Returns the length of the content in bytes.  The byte length can be
	 * larger than the character length if the content contains multi-byte
	 * characters.
	 * @return	the content length in bytes
	 */
	public long getByteLength();
	/**
	 * Specifies the length of the content in bytes or the UNKNOWN_LENGTH
	 * constant if the length of the content is not known.
	 * @param length	the content length in bytes
	 */
	public void setByteLength(long length);
}
