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
 * A Bytes Handle represents document content as a byte array for reading or writing.
 * 
 * When writing JSON, text, or XML content, you should use a byte[] array only
 * if the bytes are encoded in UTF-8.  If the characters have a different encoding, use
 * a StringHandle and specify the correct character encoding for the bytes when
 * creating the String.
 */
public class BytesHandle
	implements
		BinaryReadHandle<byte[]>, BinaryWriteHandle<byte[]>,
		GenericReadHandle<byte[]>, GenericWriteHandle<byte[]>,
		JSONReadHandle<byte[]>, JSONWriteHandle<byte[]>, 
		TextReadHandle<byte[]>, TextWriteHandle<byte[]>,
		XMLReadHandle<byte[]>, XMLWriteHandle<byte[]>,
		StructureReadHandle<byte[]>, StructureWriteHandle<byte[]>
{
	private byte[] content;
	private Format format = Format.XML;

	public BytesHandle() {
		super();
	}
	public BytesHandle(byte[] content) {
		this();
		set(content);
	}

	public byte[] get() {
		return content;
	}
	public void set(byte[] content) {
		this.content = content;
	}
	public BytesHandle with(byte[] content) {
		set(content);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public BytesHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<byte[]> receiveAs() {
		return byte[].class;
	}
	public void receiveContent(byte[] content) {
		this.content = content;
	}
	public byte[] sendContent() {
		if (content == null || content.length == 0) {
			throw new IllegalStateException("No bytes to write");
		}

		return content;
	}
}
