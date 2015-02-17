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
package com.marklogic.client.io;

import java.io.UnsupportedEncodingException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BinaryReadHandle;
import com.marklogic.client.io.marker.BinaryWriteHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
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
	extends BaseHandle<byte[], byte[]>
	implements BufferableHandle, ContentHandle<byte[]>,
		BinaryReadHandle, BinaryWriteHandle,
		GenericReadHandle, GenericWriteHandle,
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	private byte[] content;

	/**
	 * Creates a factory to create a BytesHandle instance for a byte[] array.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ byte[].class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return byte[].class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new BytesHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public BytesHandle() {
		super();
   		setResendable(true);
	}
	/**
	 * Initializes the handle with a byte array for the content.
	 * @param content	the byte array
	 */
	public BytesHandle(byte[] content) {
		this();
		set(content);
	}

	/**
	 * Returns the byte array for the handle content.
	 * @return	the byte array
	 */
	@Override
	public byte[] get() {
		return content;
	}
	/**
	 * Assigns a byte array as the content.
	 * @param content	the byte array
	 */
	@Override
	public void set(byte[] content) {
		this.content = content;
	}
	/**
	 * Assigns a byte array as the content and returns the handle
	 * as a fluent convenience.
	 * @param content	the byte array
	 * @return	this handle
	 */
	public BytesHandle with(byte[] content) {
		set(content);
		return this;
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public BytesHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public BytesHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	@Override
	public void fromBuffer(byte[] buffer) {
		content = buffer;
	}
	@Override
	public byte[] toBuffer() {
		return content;
	}
	/**
	 * Returns a byte array as a string with the assumption
	 * that the bytes are encoded in UTF-8. If the bytes
	 * have a different encoding, instantiate a String
	 * directly instead of calling this method.
	 */
	@Override
	public String toString() {
		try {
			return new String(content,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	protected Class<byte[]> receiveAs() {
		return byte[].class;
	}
	protected void receiveContent(byte[] content) {
		this.content = content;
	}
	
	protected byte[] sendContent() {
		if (content == null || content.length == 0) {
			throw new IllegalStateException("No bytes to write");
		}

		return content;
	}
}
