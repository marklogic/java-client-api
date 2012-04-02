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

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

/**
 * An XML Event Reader Handle represents XML content as an XML event reader
 * for reading as a series of StAX events.
 * 
 * When finished with the event reader, close the event reader to release
 * the response.
 */
public class XMLEventReaderHandle
	implements XMLReadHandle<InputStream>, StructureReadHandle<InputStream>
{
	static final private Logger logger = LoggerFactory.getLogger(XMLEventReaderHandle.class);

	private DBResolver      resolver;
	private XMLEventReader  content;
	private XMLInputFactory factory;

	public XMLEventReaderHandle() {
	}

	public DBResolver getResolver() {
		return resolver;
	}
	public void setResolver(DBResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Returns an XML Event Reader for for reading a resource from the database
	 * as a series of StAX events.
	 * 
     * When finished with the event reader, close the event reader to release
     * the response.
	 * 
	 * @return
	 */
	public XMLEventReader get() {
		return content;
	}

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("XMLEventReaderHandle supports the XML format only");
	}
	public XMLEventReaderHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public XMLInputFactory getFactory() {
		if (factory == null)
			factory = makeXMLInputFactory();
		return factory;
	}
	public void setFactory(XMLInputFactory factory) {
		this.factory = factory;
	}
	protected XMLInputFactory makeXMLInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty("javax.xml.stream.isNamespaceAware", new Boolean(true));
		factory.setProperty("javax.xml.stream.isValidating",     new Boolean(false));

		return factory;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		if (content == null) {
			this.content = null;
			return;
		}

		try {
			logger.info("Parsing StAX events from input stream");

			XMLInputFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make StAX input factory");
			}

			if (resolver != null)
				factory.setXMLResolver(resolver);

			this.content = factory.createXMLEventReader(content);
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new MarkLogicInternalException(e);
		} catch (FactoryConfigurationError e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new MarkLogicInternalException(e);
		}
	}
}