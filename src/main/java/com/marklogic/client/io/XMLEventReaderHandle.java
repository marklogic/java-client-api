package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.XMLReadHandle;

public class XMLEventReaderHandle
	implements XMLReadHandle<InputStream>, StructureReadHandle<InputStream>
{
	static final private Logger logger = LoggerFactory.getLogger(XMLEventReaderHandle.class);

	public XMLEventReaderHandle() {
	}

	private XMLEventReader content;
	public XMLEventReader get() {
		return content;
	}

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("XMLEventReaderHandle supports the XML format only");
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			logger.info("Parsing StAX events from input stream");

			this.content = XMLInputFactory.newFactory().createXMLEventReader(content);
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			logger.error("Failed to parse StAX events from input stream",e);
			throw new RuntimeException(e);
		}
	}
}