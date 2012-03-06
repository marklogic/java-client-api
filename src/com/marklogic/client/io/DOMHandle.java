package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.SAXException;

import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class DOMHandle
	implements XMLReadHandle<InputStream>, XMLWriteHandle<String>
{
	public DOMHandle() {
	}

	private Document content;
	public Document get() {
		return content;
	}
    public void set(Document content) {
    	this.content = content;
    }
    public DOMHandle on(Document content) {
    	set(content);
    	return this;
    }

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			this.content = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content);
		} catch (SAXException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
	public String sendContent() {
		try {
			return ((DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()).createLSSerializer().writeToString(content);
		} catch (DOMException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (LSException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			// TODO: log exception
			throw new RuntimeException(e);
		}
	}
}
