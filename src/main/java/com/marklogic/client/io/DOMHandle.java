package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.xml.sax.SAXException;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.OutputStreamSender;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A DOM Handle represents XML content as a DOM document for reading or writing.
 */
public class DOMHandle
	implements OutputStreamSender,
		XMLReadHandle<InputStream>, XMLWriteHandle<OutputStreamSender>,
		StructureReadHandle<InputStream>, StructureWriteHandle<OutputStreamSender>
{
	static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

	private DBResolver             resolver;
	private Document               content;
	private DocumentBuilderFactory factory;

	public DOMHandle() {
		super();
	}
	public DOMHandle(Document content) {
		this();
		set(content);
	}

	public DBResolver getResolver() {
		return resolver;
	}
	public void setResolver(DBResolver resolver) {
		this.resolver = resolver;
	}

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

	public DocumentBuilderFactory getFactory() throws ParserConfigurationException {
		if (factory == null)
			factory = makeDocumentBuilderFactory();
		return factory;
	}
	public void setFactory(DocumentBuilderFactory factory) {
		this.factory = factory;
	}
	protected DocumentBuilderFactory makeDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		// TODO: XInclude

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
			logger.info("Parsing DOM document from input stream");

			DocumentBuilderFactory factory = getFactory();
			if (factory == null) {
				throw new RuntimeException("Failed to make DOM document builder factory");
			}

			DocumentBuilder builder = factory.newDocumentBuilder();
			if (resolver != null)
				builder.setEntityResolver(resolver);

			this.content = builder.parse(content);
			content.close();
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
	public OutputStreamSender sendContent() {
		return this;
	}
	public void write(OutputStream out) throws IOException {
		try {
			logger.info("Serializing DOM document to output stream");

			if (content == null) {
				throw new RuntimeException("No document to write");
			}

			DocumentBuilderFactory factory = getFactory();
			if (factory == null) {
				throw new RuntimeException("Failed to make DOM document builder factory");
			}

			DOMImplementationLS domImpl = (DOMImplementationLS) factory.newDocumentBuilder().getDOMImplementation();
			LSOutput domOutput = domImpl.createLSOutput();
			domOutput.setByteStream(out);
			domImpl.createLSSerializer().write(content, domOutput);
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
