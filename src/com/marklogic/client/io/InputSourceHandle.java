package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.marklogic.client.docio.XMLReadHandle;

public class InputSourceHandle implements XMLReadHandle<InputStream> {
	public InputSourceHandle() {
	}

	private InputSource content;
	public InputSource get() {
    	return content;
    }
	public void process(ContentHandler handler) {
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(handler);
			reader.parse(content);
		} catch (SAXException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		this.content = new InputSource(content);
	}
}
