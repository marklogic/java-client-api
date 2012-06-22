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
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * An Input Source Handle represents XML content as an input source for reading or writing.
 * When reading, the XML may be processed by a SAX content handler.
 */
public class InputSourceHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender,
	    XMLReadHandle, XMLWriteHandle,
	    StructureReadHandle, StructureWriteHandle
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
	public void set(InputSource content) {
    	this.content = content;
    }
	public InputSourceHandle with(InputSource content) {
    	set(content);
    	return this;
    }

	public void process(ContentHandler handler) {
		try {
			if (logger.isInfoEnabled())
				logger.info("Processing input source with SAX content handler");

			XMLReader reader = makeReader();

			reader.setContentHandler(handler);

			reader.parse(content);
		} catch (SAXException e) {
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
	public InputSourceHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
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
	protected XMLReader makeReader() {
		try {
			SAXParserFactory factory = getFactory();
			if (factory == null) {
				throw new MarkLogicInternalException("Failed to make SAX parser factory");
			}

			XMLReader reader = factory.newSAXParser().getXMLReader();
			if (resolver != null)
				reader.setEntityResolver(resolver);

			return reader;
		} catch (SAXException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new MarkLogicInternalException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new MarkLogicInternalException(e);
		}
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
			TransformerFactory.newInstance().newTransformer().transform(
					new SAXSource(makeReader(), content),
					new StreamResult(out)
					);
		} catch (TransformerException e) {
			logger.error("Failed to transform input source into result",e);
			throw new MarkLogicIOException(e);
		}
	}
}
