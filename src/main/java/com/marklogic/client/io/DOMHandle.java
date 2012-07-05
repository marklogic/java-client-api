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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSResourceResolver;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A DOM Handle represents XML content as a DOM document for reading or writing.
 */
public class DOMHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

	private LSResourceResolver     resolver;
	private Document               content;
	private DocumentBuilderFactory factory;

	public DOMHandle() {
		super();
		super.setFormat(Format.XML);
	}
	public DOMHandle(Document content) {
		this();
		set(content);
	}

	public LSResourceResolver getResolver() {
		return resolver;
	}
	public void setResolver(LSResourceResolver resolver) {
		this.resolver = resolver;
	}

	public Document get() {
		return content;
	}
    public void set(Document content) {
    	this.content = content;
    }
    public DOMHandle with(Document content) {
    	set(content);
    	return this;
    }

    @Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException("DOMHandle supports the XML format only");
	}
	public DOMHandle withMimetype(String mimetype) {
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

			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DocumentBuilderFactory getFactory() throws ParserConfigurationException {
		if (factory == null)
			factory = makeDocumentBuilderFactory();
		return factory;
	}
	public void setFactory(DocumentBuilderFactory factory) {
		this.factory = factory;
	}
	protected DocumentBuilderFactory makeDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		// TODO: XInclude

		return factory;
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	protected void receiveContent(InputStream content) {
		if (content == null) {
			this.content = null;
			return;
		}

		try {
			if (logger.isInfoEnabled())
				logger.info("Parsing DOM document from input stream");

			DocumentBuilderFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make DOM document builder factory");
			}

			DOMImplementationLS domImpl = (DOMImplementationLS) factory.newDocumentBuilder().getDOMImplementation();

			LSParser parser = domImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			if (resolver != null) {
				parser.getDomConfig().setParameter("resource-resolver", resolver);
			}

			LSInput domInput = domImpl.createLSInput();
			domInput.setEncoding("UTF-8");
			domInput.setByteStream(content);

			this.content = parser.parse(domInput);
			content.close();
		} catch (IOException e) {
			logger.error("Failed to parse DOM document from input stream",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse DOM document from input stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}

		return this;
	}
	public void write(OutputStream out) throws IOException {
		try {
			if (logger.isInfoEnabled())
				logger.info("Serializing DOM document to output stream");

			DocumentBuilderFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make DOM document builder factory");
			}

			DOMImplementationLS domImpl = (DOMImplementationLS) factory.newDocumentBuilder().getDOMImplementation();
			LSOutput domOutput = domImpl.createLSOutput();
			domOutput.setEncoding("UTF-8");
			domOutput.setByteStream(out);
			domImpl.createLSSerializer().write(content, domOutput);
		} catch (DOMException e) {
			logger.error("Failed to serialize DOM document to output stream",e);
			throw new MarkLogicInternalException(e);
		} catch (LSException e) {
			logger.error("Failed to serialize DOM document to output stream",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to serialize DOM document to output stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
}
