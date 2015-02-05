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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * An XML Event Reader Handle represents XML content as an XML event reader
 * for reading as a series of StAX events.
 * 
 * When finished with the event reader, close the event reader to release
 * the response.
 */
public class XMLEventReaderHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle, ContentHandle<XMLEventReader>,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	static final private Logger logger = LoggerFactory.getLogger(XMLEventReaderHandle.class);

	private XMLResolver     resolver;
	private XMLEventReader  content;
	private XMLInputFactory factory;

	/**
	 * Creates a factory to create an XMLEventReaderHandle instance for a StAX event reader.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ XMLEventReader.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return XMLEventReader.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new XMLEventReaderHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public XMLEventReaderHandle() {
		super();
		super.setFormat(Format.XML);
   		setResendable(false);
	}
	/**
	 * Initializes the handle with a StAX event reader for the content.
	 * @param content	a StAX event reader
	 */
	public XMLEventReaderHandle(XMLEventReader content) {
		this();
		set(content);
	}

	/**
	 * Returns the resolver for resolving references while parsing
	 * the event reader source.
	 * @return	the resolver
	 */
	public XMLResolver getResolver() {
		return resolver;
	}
	/**
	 * Specifies the resolver for resolving references while parsing
	 * the event reader source.
	 * @param resolver	the reference resolver
	 */
	public void setResolver(XMLResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Returns an XML Event Reader for for reading a resource from the database
	 * as a series of StAX events.
	 * 
     * When finished with the event reader, close the event reader to release
     * the response.
	 * 
	 * @return	the XML event reader
	 */
	@Override
	public XMLEventReader get() {
		return content;
	}
	/**
	 * Assigns the event reader for the content.
	 * @param content	a StAX event reader
	 */
	@Override
	public void set(XMLEventReader content) {
		this.content = content;
	}
    /**
	 * Assigns an event reader for the content and returns the handle
	 * as a fluent convenience.
	 * @param content	a StAX event reader
	 * @return	this handle
     */
	public XMLEventReaderHandle with(XMLEventReader content) {
		set(content);
		return this;
	}

	/**
	 * Restricts the format to XML.
	 */
	public void setFormat(Format format) {
		if (format != Format.XML)
			throw new IllegalArgumentException("XMLEventReaderHandle supports the XML format only");
	}
	/**
	 * Specifies the mime type of the content and returns the handle
	 * as a fluent convenience.
	 * @param mimetype	the mime type of the content
	 * @return	this handle
	 */
	public XMLEventReaderHandle withMimetype(String mimetype) {
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
	 * Buffers the StAX event source and returns the buffer
	 * as an XML string.
	 */
	@Override
	public String toString() {
		try {
			return new String(toBuffer(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	/**
	 * Returns the factory for parsing StAX events.
	 * @return	the StAX factory
	 */
	public XMLInputFactory getFactory() {
		if (factory == null)
			factory = makeXMLInputFactory();
		return factory;
	}
	/**
	 * Specifies the factory for parsing StAX events.
	 * @param factory	the StAX factory
	 */
	public void setFactory(XMLInputFactory factory) {
		this.factory = factory;
	}
	protected XMLInputFactory makeXMLInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty("javax.xml.stream.isNamespaceAware", true);
		factory.setProperty("javax.xml.stream.isValidating",     false);

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
				logger.info("Parsing StAX events from input stream");

			XMLInputFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make StAX input factory");
			}

			if (resolver != null)
				factory.setXMLResolver(resolver);

			this.content = factory.createXMLEventReader(content, "UTF-8");
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new MarkLogicInternalException(e);
		} catch (FactoryConfigurationError e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No input source to write");
		}

		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newFactory();
			XMLEventWriter   writer  = factory.createXMLEventWriter(out, "UTF-8");
			
			writer.add(content);
			writer.flush();
			writer.close();

			content.close();
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
}
