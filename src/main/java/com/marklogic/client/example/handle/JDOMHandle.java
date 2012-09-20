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
package com.marklogic.client.example.handle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;

import com.marklogic.client.io.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A JDOM Handle represents XML content as a JDOM document for reading or writing.
 */
public class JDOMHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle,
    	XMLReadHandle, XMLWriteHandle,
    	StructureReadHandle, StructureWriteHandle
{
	private Document     content;
	private SAXBuilder   builder;
	private XMLOutputter outputter;

	public JDOMHandle() {
		super();
		super.setFormat(Format.XML);
   		setResendable(true);
	}
	public JDOMHandle(Document content) {
		this();
    	set(content);
	}

	public SAXBuilder getBuilder() {
		if (builder == null)
			builder = makeBuilder();
		return builder;
	}
	public void setBuilder(SAXBuilder builder) {
		this.builder = builder;
	}
	protected SAXBuilder makeBuilder() {
		return new SAXBuilder(XMLReaders.NONVALIDATING);
	}

	public XMLOutputter getOutputter() {
		if (outputter == null)
			outputter = makeOutputter();
		return outputter;
	}
	public void setOutputter(XMLOutputter outputter) {
		this.outputter = outputter;
	}
	protected XMLOutputter makeOutputter() {
		return new XMLOutputter();
	}

	public Document get() {
		return content;
	}
    public void set(Document content) {
    	this.content = content;
    }
    public JDOMHandle with(Document content) {
    	set(content);
    	return this;
    }

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
			this.content = getBuilder().build(
					new InputStreamReader(content, "UTF-8")
					);
		} catch (JDOMException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
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
		getOutputter().output(
				content,
				new OutputStreamWriter(out, "UTF-8")
				);
	}

}
