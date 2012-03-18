package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

/**
 * An Input Source Handle represents XML content as an input source for reading,
 * potentially with processing by a SAX content handler.
 */
public class InputSourceHandle
	implements XMLReadHandle<InputStream>, StructureReadHandle<InputStream>
{
	static final private Logger logger = LoggerFactory.getLogger(InputSourceHandle.class);

	public InputSourceHandle() {
	}

	private DBResolver resolver;
	public DBResolver getResolver() {
		return resolver;
	}
	public void setResolver(DBResolver resolver) {
		this.resolver = resolver;
	}

	private InputSource content;
	public InputSource get() {
    	return content;
    }
	public void process(ContentHandler handler) {
		try {
			logger.info("Processing input source with SAX content handler");

			SAXParserFactory factory = getFactory();
			if (factory == null) {
				throw new RuntimeException("Failed to make SAX parser factory");
			}

			XMLReader reader = factory.newSAXParser().getXMLReader();
			if (resolver != null)
				reader.setEntityResolver(resolver);

			reader.setContentHandler(handler);

			reader.parse(content);
		} catch (SAXException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to process input source with SAX content handler",e);
			throw new RuntimeException(e);
		}
	}

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("InputSourceHandle supports the XML format only");
	}

	private SAXParserFactory factory;
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

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		if (content == null) {
			this.content = null;
			return;
		}

		this.content = new InputSource(content);
	}
}
