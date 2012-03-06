package com.marklogic.client.io;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.marklogic.client.docio.XMLReadHandle;

public class XMLStreamReaderHandle implements XMLReadHandle<InputStream> {
	public XMLStreamReaderHandle() {
	}

	private XMLStreamReader content;
	public XMLStreamReader get() {
		return content;
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			this.content = XMLInputFactory.newFactory().createXMLStreamReader(content);
		} catch (XMLStreamException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
}