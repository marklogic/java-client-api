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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

/**
 * An Input Source Handle represents XML content as an input source for reading,
 * potentially with processing by a SAX content handler.
 */
public class InputSourceHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements XMLReadHandle, StructureReadHandle
{
	static final private Logger logger = LoggerFactory.getLogger(InputSourceHandle.class);

	private EntityResolver   resolver;
	private InputSource      content;
	private SAXParserFactory factory;

	public InputSourceHandle() {
		super();
		super.setFormat(Format.XML);
	}

	public EntityResolver getResolver() {
		return resolver;
	}
	public void setResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}

	public InputSource get() {
    	return content;
    }
	public void process(ContentHandler handler) {
		try {
			logger.info("Processing input source with SAX content handler");

			SAXParserFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make SAX parser factory");
			}

			XMLReader reader = factory.newSAXParser().getXMLReader();
			if (resolver != null)
				reader.setEntityResolver(resolver);

			reader.setContentHandler(handler);

			reader.parse(content);
		} catch (SAXException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new MarkLogicInternalException(e);
		} catch (IOException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new MarkLogicInternalException(e);
		}
	}

	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("InputSourceHandle supports the XML format only");
	}
	public InputSourceHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public SAXParserFactory getFactory() throws SAXException, ParserConfigurationException {
		if (factory == null)
			factory = makeSAXParserFactory();
		return factory;
	}
	public void setFactory(SAXParserFactory factory) {
		this.factory = factory;
	}
	protected SAXParserFactory makeSAXParserFactory() throws SAXException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
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

		this.content = new InputSource(content);
	}
}
