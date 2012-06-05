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

import java.io.InputStream;

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
 * An Input Stream Handle represents a resource as an input stream for reading or writing.
 * 
 * When finished with the input stream, close the input stream to release
 * the response.
 * 
 * When writing JSON, text, or XML content, you should use an InputStream only
 * if the stream is encoded in UTF-8.  If the characters have a different encoding, use
 * a ReaderHandle and specify the correct character encoding for the stream when
 * creating the Reader.
 */
public class InputStreamHandle
	extends BaseHandle<InputStream, InputStream>
	implements
		BinaryReadHandle, BinaryWriteHandle,
		GenericReadHandle, GenericWriteHandle,
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	private InputStream content;

	public InputStreamHandle() {
		super();
	}
	public InputStreamHandle(InputStream content) {
		this();
		set(content);
	}

	/**
	 * Returns an input stream for a resource read from the database.
	 * 
     * When finished with the input stream, close the input stream to release
     * the response.
	 * 
	 * @return
	 */
	public InputStream get() {
		return content;
	}
	public void set(InputStream content) {
		this.content = content;
	}
	public InputStreamHandle with(InputStream content) {
		set(content);
		return this;
	}

	public InputStreamHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	public InputStreamHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	protected void receiveContent(InputStream content) {
		this.content = content;
	}
	@Override
	protected InputStream sendContent() {
		if (content == null) {
			throw new IllegalStateException("No stream to write");
		}

		return content;
	}
}
