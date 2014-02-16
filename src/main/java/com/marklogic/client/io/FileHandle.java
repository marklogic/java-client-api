/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.io;

import java.io.File;

import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A File Handle represents document content as a file for reading or writing.
 * 
 * When you read a database document into a file handle, the API creates a temporary
 * file.  You can then open the file or move it with the File.renameTo() method.
 * 
 * When writing JSON, text, or XML content, you should use a File only
 * if the file is encoded in UTF-8.  If the characters have a different encoding, use
 * a ReaderHandle and specify the correct character encoding for the file when
 * creating the Reader.
 */
public class FileHandle
	extends BaseHandle<File, File>
	implements
		BinaryReadHandle, BinaryWriteHandle,
		GenericReadHandle, GenericWriteHandle,
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	private File content;

	/**
	 * Zero-argument constructor.
	 */
	public FileHandle() {
		super();
   		setResendable(true);
	}
	/**
	 * Initializes the handle with a file containing the content.
	 * @param content	the file
	 */
	public FileHandle(File content) {
		this();
		set(content);
	}

	/**
	 * Returns the file for the handle content.
	 * @return	the file
	 */
	public File get() {
		return content;
	}
	/**
	 * Assigns a file as the content.
	 * @param content	the file
	 */
	public void set(File content) {
		this.content = content;
	}
	/**
	 * Assigns a file as the content and returns the handle
	 * as a fluent convenience.
	 * @param content	the file
	 * @return	this handle
	 */
	public FileHandle with(File content) {
		set(content);
    	return this;
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public FileHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public FileHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	@Override
	protected Class<File> receiveAs() {
		return File.class;
	}
	@Override
	protected void receiveContent(File content) {
		this.content = content;
	}
	@Override
	protected File sendContent() {
		if (content == null) {
			throw new IllegalStateException("No file to write");
		}

		return content;
	}
}
