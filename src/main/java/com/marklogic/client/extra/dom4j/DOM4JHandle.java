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
package com.marklogic.client.extra.dom4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A DOM4JHandle represents XML content as a dom4j document for reading or writing.
 * You must install the dom4j library to use this class.
 */
public class DOM4JHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle, ContentHandle<Document>,
    	XMLReadHandle, XMLWriteHandle,
    	StructureReadHandle, StructureWriteHandle
{
	private SAXReader    reader;
	private OutputFormat outputFormat;
	private Document     content;

	/**
	 * Creates a factory to create a DOM4JHandle instance for a dom4j document.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ Document.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return Document.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new DOM4JHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public DOM4JHandle() {
		super();
		super.setFormat(Format.XML);
   		setResendable(true);
	}
	/**
	 * Provides a handle on XML content as a dom4j document structure.
	 * @param content	the XML document.
	 */
	public DOM4JHandle(Document content) {
		this();
    	set(content);
	}

	/**
	 * Returns the dom4j reader for XML content.
	 * @return	the dom4j reader.
	 */
	public SAXReader getReader() {
		if (reader == null)
			reader = makeReader();

		return reader;
	}
	/**
	 * Specifies a dom4j reader for XML content.
	 * @param reader	the dom4j reader.
	 */
	public void setReader(SAXReader reader) {
		this.reader = reader;
	}
	protected SAXReader makeReader() {
		SAXReader reader = new SAXReader();
		reader.setValidation(false);
		return reader;
	}

	/**
	 * Returns the dom4j output format for serializing XML content.
	 * @return	the output format.
	 */
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}
	/**
	 * Specifies the dom4j output format for serializing XML content.
	 * @param outputFormat	the output format.
	 */
	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * Returns the XML document structure.
	 * @return	the XML document.
	 */
	@Override
	public Document get() {
		return content;
	}
	/**
	 * Assigns an XML document structure as the content.
	 * @param content	the XML document.
	 */
    @Override
	public void set(Document content) {
    	this.content = content;
    }
	/**
	 * Assigns an XML document structure as the content and returns the handle.
	 * @param content	the XML document.
	 * @return	the handle on the XML document.
	 */
    public DOM4JHandle with(Document content) {
    	set(content);
    	return this;
    }

	/**
	 * Restricts the format to XML.
	 */
	@Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException("JDOMHandle supports the XML format only");
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

			return buffer.toByteArray();
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}

	/**
	 * Returns the XML document as a string.
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
		if (content == null)
			return;

		try {
			this.content = getReader().read(
					new InputStreamReader(content, "UTF-8")
					);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		} catch (DocumentException e) {
			throw new MarkLogicIOException(e);
		} finally {
			try {
				content.close();
			} catch (IOException e) {
				// ignore.
			}
		}

	}

	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}

		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		OutputFormat outputFormat = getOutputFormat();
		if (outputFormat != null) {
			new XMLWriter(writer, outputFormat).write(content);
		} else {
			new XMLWriter(writer).write(content);
		}
		writer.flush();
	}
}
