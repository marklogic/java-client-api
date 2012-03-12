package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.xml.sax.SAXException;

import com.marklogic.client.XMLDocumentBuffer;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class XMLDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	// TODO: test Source reader, SAX handler, StAX stream reader, StAX event reader, JAXB reader and writer 

	@Test
	public void testReadWrite() throws ParserConfigurationException, SAXException, IOException {
		String uri = "/test/testWrite1.xml";
		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		root.setAttribute("xml:lang", "en");
		root.setAttribute("foo", "bar");
		root.appendChild(domDocument.createElement("child"));
		root.appendChild(domDocument.createTextNode("mixed"));
		domDocument.appendChild(root);
		XMLDocumentBuffer doc = Common.client.newXMLDocumentBuffer(uri);
		doc.write(new DOMHandle().on(domDocument));
		String domString = ((DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.getDOMImplementation()).createLSSerializer().writeToString(domDocument);
		String docText = doc.read(new StringHandle()).get();
		assertNotNull("Read null string for XML content",docText);
		assertXMLEqual("Failed to read XML document as String", docText, domString);
		Document readDoc = doc.read(new DOMHandle()).get();
		assertNotNull("Read null document for XML content",readDoc);
		assertXMLEqual("Failed to read XML document as DOM",domDocument,readDoc);
	}
}
