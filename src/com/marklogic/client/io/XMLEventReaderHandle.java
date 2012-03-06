package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.marklogic.client.docio.XMLReadHandle;

public class XMLEventReaderHandle implements XMLReadHandle<InputStream> {
	public XMLEventReaderHandle() {
	}

	private XMLEventReader content;
	public XMLEventReader get() {
		return content;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			this.content = XMLInputFactory.newFactory().createXMLEventReader(content);
		} catch (XMLStreamException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
}