package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.XMLReadHandle;

public class XMLStreamReaderHandle
	implements XMLReadHandle<InputStream>, StructureReadHandle<InputStream>
{
	static final private Logger logger = LoggerFactory.getLogger(XMLStreamReaderHandle.class);

	public XMLStreamReaderHandle() {
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

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			logger.info("Parsing StAX stream from input stream");

			this.content = XMLInputFactory.newFactory().createXMLStreamReader(content);
		} catch (XMLStreamException e) {
			logger.error("Failed to parse StAX stream from input stream",e);
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			logger.error("Failed to parse StAX stream from input stream",e);
			throw new RuntimeException(e);
		}
	}
}