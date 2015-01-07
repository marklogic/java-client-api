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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	implements BufferableHandle, ContentHandle<InputStream>,
		BinaryReadHandle, BinaryWriteHandle,
		GenericReadHandle, GenericWriteHandle,
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	private InputStream content;

	final static private int BUFFER_SIZE = 8192;

	/**
	 * Creates a factory to create an InputStreamHandle instance for an input stream.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ InputStream.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return InputStream.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new InputStreamHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public InputStreamHandle() {
		super();
   		setResendable(false);
	}
	/**
	 * Initializes the handle with an input stream for the content.
	 * @param content	an input stream
	 */
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
	 * @return	the input stream
	 */
	@Override
	public InputStream get() {
		return content;
	}
	/**
	 * Assigns an input stream as the content.
	 * @param content	an input stream
	 */
	@Override
	public void set(InputStream content) {
		this.content = content;
	}
    /**
	 * Assigns an input stream as the content and returns the handle
	 * as a fluent convenience.
	 * @param content	an input stream
	 * @return	this handle
     */
	public InputStreamHandle with(InputStream content) {
		set(content);
		return this;
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public InputStreamHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public InputStreamHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	@Override
	public void fromBuffer(byte[] buffer) {
		if (buffer == null || buffer.length == 0)
			content = null;
		else
			receiveContent(new ByteArrayInputStream(buffer));
	}
	@Override
	public byte[] toBuffer() {
		try {
			if (content == null)
				return null;

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			byte[] b = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = content.read(b)) != -1) {
				buffer.write(b, 0, len);
			}
			content.close();

			b = buffer.toByteArray();
			fromBuffer(b);

			return b;
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	/**
	 * Buffers the input stream and returns the buffer as a string
	 * with the assumption that the stream is encoded in UTF-8. If
	 * the stream has a different encoding, use InputStreamReader
	 * instead of calling this method.
	 */
	@Override
	public String toString() {
		try {
			return new String(toBuffer(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
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
