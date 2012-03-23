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