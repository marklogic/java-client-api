package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

/**
 * An XML Stream Reader Handle represents XML content as an XML stream reader
 * for reading as a StAX pull stream.
 */
public class XMLStreamReaderHandle
	implements XMLReadHandle<InputStream>, StructureReadHandle<InputStream>
{
	static final private Logger logger = LoggerFactory.getLogger(XMLStreamReaderHandle.class);

	public XMLStreamReaderHandle() {
	}

	private DBResolver resolver;
	public DBResolver getResolver() {
		return resolver;
	}
	public void setResolver(DBResolver resolver) {
		this.resolver = resolver;
	}

	private XMLStreamReader content;
	public XMLStreamReader get() {
		return content;
	}

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("XMLStreamReaderHandle supports the XML format only");
	}

	private XMLInputFactory factory;
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
		try {
			logger.info("Parsing StAX stream from input stream");

			XMLInputFactory factory = getFactory();
			if (factory == null) {
				throw new RuntimeException("Failed to make StAX input factory");
			}

			if (resolver != null)
				factory.setXMLResolver(resolver);

			this.content = factory.createXMLStreamReader(content);
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX stream from input stream",e);
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			logger.error("Failed to parse StAX stream from input stream",e);
			throw new RuntimeException(e);
		}
	}
}