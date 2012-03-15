package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.SAXException;

import com.marklogic.client.docio.StructureFormat;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.StructureWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class DOMHandle
	implements
		XMLReadHandle<InputStream>, XMLWriteHandle<String>,
		StructureReadHandle<InputStream>, StructureWriteHandle<String>
{
	static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

	public DOMHandle() {
		super();
	}
	public DOMHandle(Document content) {
		this();
		set(content);
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

	public StructureFormat getFormat() {
		return StructureFormat.XML;
	}
	public void setFormat(StructureFormat format) {
		if (format != StructureFormat.XML)
			new RuntimeException("DOMHandle supports the XML format only");
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			logger.info("Parsing DOM document from input stream");

			this.content = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content);
		} catch (SAXException e) {
			logger.error("Failed to parse DOM document from input stream",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Failed to parse DOM document from input stream",e);
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to parse DOM document from input stream",e);
			throw new RuntimeException(e);
		}
	}
	public String sendContent() {
		try {
			logger.info("Serializing DOM document as String");

			return ((DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()).createLSSerializer().writeToString(content);
		} catch (DOMException e) {
			logger.error("Failed to serialize DOM document as String",e);
			throw new RuntimeException(e);
		} catch (LSException e) {
			logger.error("Failed to serialize DOM document as String",e);
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			logger.error("Failed to serialize DOM document as String",e);
			throw new RuntimeException(e);
		}
	}
}
