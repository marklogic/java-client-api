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
package com.marklogic.client.io;

import java.io.File;

import com.marklogic.client.Format;
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
	implements
		BinaryReadHandle<File>, BinaryWriteHandle<File>,
		GenericReadHandle<File>, GenericWriteHandle<File>,
		JSONReadHandle<File>, JSONWriteHandle<File>, 
		TextReadHandle<File>, TextWriteHandle<File>,
		XMLReadHandle<File>, XMLWriteHandle<File>,
		StructureReadHandle<File>, StructureWriteHandle<File>
{
	private File   content;
	private Format format = Format.XML;

	public FileHandle() {
		super();
	}
	public FileHandle(File content) {
		this();
		set(content);
	}

	public File get() {
		return content;
	}
	public void set(File content) {
		this.content = content;
	}
	public FileHandle with(File content) {
		set(content);
    	return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public FileHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<File> receiveAs() {
		return File.class;
	}
	public void receiveContent(File content) {
		this.content = content;
	}
	public File sendContent() {
		if (content == null) {
			throw new IllegalStateException("No file to write");
		}

		return content;
	}
}
