package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.Attributes;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.URIHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;

public class XMLDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	// TODO: test JAXB reader and writer

	@Test
	public void testReadWrite() throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, XMLStreamException {
		String uri = "/test/testWrite1.xml";
		DocumentIdentifier docId = new DocumentIdentifier(uri);

		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		root.setAttribute("xml:lang", "en");
		root.setAttribute("foo", "bar");
		root.appendChild(domDocument.createElement("child"));
		root.appendChild(domDocument.createTextNode("mixed"));
		domDocument.appendChild(root);

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new DOMHandle().on(domDocument));
		String domString = ((DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.getDOMImplementation()).createLSSerializer().writeToString(domDocument);
		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for XML content",docText);
		assertXMLEqual("Failed to read XML document as String", docText, domString);

		Document readDoc = docMgr.read(docId, new DOMHandle()).get();
		assertNotNull("Read null document for XML content",readDoc);
		assertXMLEqual("Failed to read XML document as DOM",domDocument,readDoc);
		DOMResult result = new DOMResult();
		docMgr.read(docId, new SourceHandle()).process(TransformerFactory.newInstance().newTransformer(), result);
		readDoc = (Document) result.getNode();
		assertNotNull("Read null document from transform on XML content",readDoc);
		assertXMLEqual("Failed to transform XML document with DOM",domDocument,readDoc);

		final HashMap<String,Integer> counter = new HashMap<String,Integer>(); 
		counter.put("elementCount",0);
		counter.put("attributeCount",0);
		DefaultHandler handler = new DefaultHandler() {
			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				counter.put("elementCount",counter.get("elementCount") + 1);
				if (attributes != null) {
					int elementAttributeCount = attributes.getLength();
					if (elementAttributeCount > 0)
						counter.put("attributeCount",counter.get("attributeCount") + elementAttributeCount);
				}
			}
		};
		docMgr.read(docId, new InputSourceHandle()).process(handler);
		assertTrue("Failed to process XML document with SAX",
				counter.get("elementCount") == 2 && counter.get("attributeCount") == 2);

		XMLStreamReader streamReader = docMgr.read(docId, new XMLStreamReaderHandle()).get();
		int elementCount = 0;
		int attributeCount = 0;
		while (streamReader.hasNext()) {
			if (streamReader.next() != XMLStreamReader.START_ELEMENT)
				continue;
			elementCount++;
			int elementAttributeCount = streamReader.getAttributeCount();
			if (elementAttributeCount > 0)
				attributeCount += elementAttributeCount;
		}
		streamReader.close();
		assertTrue("Failed to process XML document with StAX stream reader",
				elementCount == 2 && attributeCount == 2);

		XMLEventReader eventReader = docMgr.read(docId, new XMLEventReaderHandle()).get();
		elementCount = 0;
		attributeCount = 0;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (!event.isStartElement())
				continue;
			StartElement element = event.asStartElement();
			elementCount++;
			Iterator<Object> attributes = element.getAttributes();
			while (attributes.hasNext()) {
				attributes.next();
				attributeCount++;
			}
		}
		eventReader.close();
		assertTrue("Failed to process XML document with StAX event reader",
				elementCount == 2 && attributeCount == 2);
	}

	private static boolean testURIHandle = false;

	@Test
	public void testURIHandle() {
		if (!testURIHandle)
			return;

		String service =
"http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php?whichClient=NDFDgenByDayMultiZipCode&zipCodeList=94070&format=12+hourly&numDays=1";
		String uri2 = "/test/testWrite2.xml";
		DocumentIdentifier docId = new DocumentIdentifier(uri2);

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new URIHandle(service));
		String docText = docMgr.read(docId, new StringHandle()).get();
		assertNotNull("Read null string for URI with XML content",docText);
		assertTrue("Read empty string for URI with XML content",docText.length() > 0);
	}
}
