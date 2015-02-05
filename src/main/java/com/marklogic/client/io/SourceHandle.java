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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A Source Handle represents XML content as a transform source for reading
 * or transforms a source into a result for writing.
 */
public class SourceHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle, ContentHandle<Source>,
	    XMLReadHandle, XMLWriteHandle, 
	    StructureReadHandle, StructureWriteHandle
{
	static final private Logger logger = LoggerFactory.getLogger(SourceHandle.class);

	private Transformer transformer;
	private Source      content;

	/**
	 * Creates a factory to create a SourceHandle instance for a Transformer Source.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ Source.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return Source.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new SourceHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public SourceHandle() {
		super();
		super.setFormat(Format.XML);
   		setResendable(false);
	}
	/**
	 * Initializes the handle with a transform source for the content.
	 * @param content	a transform source
	 */
	public SourceHandle(Source content) {
		this();
		set(content);
	}

	/**
	 * Returns a transformer for modifying the content.
	 * @return	the transformer
	 */
	public Transformer getTransformer() {
		return transformer;
	}
	/**
	 * Specifies a transformer for modifying the content.
	 * @param transformer	the transformer
	 */
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
    /**
	 * Specifies a transformer for modifying the content and returns the handle
	 * as a fluent convenience.
	 * @param transformer	the transformer
	 * @return	this handle
     */
	public SourceHandle withTransformer(Transformer transformer) {
		setTransformer(transformer);
		return this;
	}

	/**
	 * Returns the transform source that produces the content.
	 * @return	the transform source
	 */
	@Override
	public Source get() {
		return content;
	}
	/**
	 * Assigns a transform source that produces the content.
	 * @param content	the transform source
	 */
	@Override
	public void set(Source content) {
		this.content = content;
	}
    /**
	 * Assigns a transform source that produces the content and returns
	 * the handle as a fluent convenience.
	 * @param content	the transform source
	 * @return	this handle
     */
	public SourceHandle with(Source content) {
		set(content);
		return this;
	}

	/**
	 * Transforms the source for the content output to the result.  If
	 * the transformer is not specified, an identity transform sends
	 * the source to the result.  When writing, the result is stored
	 * in the database
	 * @param result	the receiver of the transform output
	 */
	public void transform(Result result) {
		if (logger.isInfoEnabled())
			logger.info("Transforming source into result");
		try {
			if (content == null) {
				throw new IllegalStateException("No source to transform");
			}

			Transformer transformer = null;
			if (this.transformer != null) {
				transformer = getTransformer();
			} else {
				if (logger.isWarnEnabled())
					logger.warn("No transformer, so using identity transform");
				transformer = TransformerFactory.newInstance().newTransformer();
			}

			transformer.transform(content, result);
		} catch (TransformerException e) {
			logger.error("Failed to transform source into result",e);
			throw new MarkLogicIOException(e);
		}
	}

	/**
	 * Restricts the format to XML.
	 */
	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException("SourceHandle supports the XML format only");
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public SourceHandle withMimetype(String mimetype) {
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
	 * Buffers the transform source and returns the buffer as a string.
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
			if (content == null) {
				this.content = null;
				return;
			}

			this.content = new StreamSource(new InputStreamReader(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}
	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No source to transform to result for writing");
		}

		return this;
	}
	public void write(OutputStream out) throws IOException {
		transform(new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
}
