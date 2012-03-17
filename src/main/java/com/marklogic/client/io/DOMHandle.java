package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.SAXException;

import com.marklogic.client.Format;
import com.marklogic.client.docio.StructureReadHandle;
import com.marklogic.client.docio.StructureWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

/**
 * A DOM Handle represents XML content as a DOM document for reading or writing.
 */
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

	private DBResolver resolver;
	public DBResolver getResolver() {
		return resolver;
	}
	public void setResolver(DBResolver resolver) {
		this.resolver = resolver;
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

	public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("DOMHandle supports the XML format only");
	}

	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	public void receiveContent(InputStream content) {
		try {
			logger.info("Parsing DOM document from input stream");

			DocumentBuilderFactory factory = makeDocumentBuilderFactory();
			if (factory == null) {
				throw new RuntimeException("Failed to make DOM document builder factory");
			}

			DocumentBuilder builder = factory.newDocumentBuilder();
			if (resolver != null)
				builder.setEntityResolver(resolver);

			this.content = builder.parse(content);
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

	protected DocumentBuilderFactory makeDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		// TODO: XInclude

		return factory;
	}
}
