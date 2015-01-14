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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A Reader Handle represents a character content as a reader
 * for reading to or writing from the database.
 * 
 * When finished with the reader, close the reader to release the resources.
 */
public class ReaderHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle, ContentHandle<Reader>,
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	final static private int BUFFER_SIZE = 8192;

    private Reader content;

	/**
	 * Creates a factory to create a ReaderHandle instance for a Reader.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ Reader.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return Reader.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new ReaderHandle() : null;
				return handle;
			}
		};
	}

	/**
     * Zero-argument constructor.
     */
    public ReaderHandle() {
    	super();
   		setResendable(false);
    }
	/**
	 * Initializes the handle with a character reader for the content.
	 * @param content	a character reader
	 */
    public ReaderHandle(Reader content) {
    	this();
    	set(content);
    }

    /**
	 * Returns a character reader for reading content.
	 * 
     * When finished with the reader, close the reader to release
     * the resource.
     * 
     * @return	the character reader
     */
	@Override
    public Reader get() {
    	return content;
    }
	/**
	 * Assigns an character reader as the content.
	 * @param content	a reader
	 */
	@Override
	public void set(Reader content) {
		this.content = content;
	}
    /**
	 * Assigns a character reader as the content and returns the handle
	 * as a fluent convenience.
	 * @param content	a reader
	 * @return	this handle
     */
	public ReaderHandle with(Reader content) {
		set(content);
		return this;
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public ReaderHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public ReaderHandle withMimetype(String mimetype) {
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
			write(buffer);

			byte[] b = buffer.toByteArray();
			fromBuffer(b);

			return b;
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	/**
	 * Buffers the character stream and returns the buffer as a string.
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
		try {
			this.content = new InputStreamReader(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}
	@Override
	protected ReaderHandle sendContent() {
		if (content == null) {
			throw new IllegalStateException("No character stream to write");
		}

		return this;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();

		CharBuffer charBuf = CharBuffer.allocate(BUFFER_SIZE);
		byte[] buf = new byte[BUFFER_SIZE * 2];
		ByteBuffer byteBuf = ByteBuffer.wrap(buf);

		while (content.read(charBuf) != -1) {
			encoder.reset();
			charBuf.flip();
			byteBuf.clear();
			CoderResult result = encoder.encode(charBuf, byteBuf, false);
			if (result.isError()) {
				throw new IOException("Failed during UTF-8 encoding - " + result.toString());
			}
			byteBuf.flip();
			out.write(buf, 0, byteBuf.limit());
			charBuf.clear();
		}

		out.flush();
	}
}
