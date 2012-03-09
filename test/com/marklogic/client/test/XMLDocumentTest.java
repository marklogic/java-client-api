package com.marklogic.client.test;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.marklogic.client.XMLDocument;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class XMLDocumentTest {
	@Test
	public void testReadT() {
		XMLDocument doc = Common.client.newXMLDocument("/sample/first.xml");
		DOMHandle domHandle = new DOMHandle();
		Document domDocument = doc.read(domHandle).get();
		assertTrue("Failed to read DOM document", domDocument != null && domDocument.getDocumentElement() != null);
	}

/*
	@Test
	public void testReadTTransaction() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testWriteW() throws ParserConfigurationException {
		String uri = "/test/testWrite1.xml";
		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		root.appendChild(domDocument.createElement("child"));
		root.appendChild(domDocument.createTextNode("mixed"));
		root.setAttribute("foo", "bar");
		domDocument.appendChild(root);
		XMLDocument doc = Common.client.newXMLDocument(uri);
		doc.write(new DOMHandle().on(domDocument));
		String docText = doc.read(new StringHandle()).get();
		assertTrue("Failed to write DOM document", docText != null && docText.length() > 0);
	}

/*
	@Test
	public void testWriteWTransaction() {
		fail("Not yet implemented");
	}
 */
}
