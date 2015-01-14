/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.test.util.Referred;
import com.marklogic.client.test.util.Refers;

public class BufferableHandleTest {
	static private XpathEngine xpather;

	@BeforeClass
	public static void beforeClass() {
		Common.connect();

		XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalize(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

        HashMap<String,String> namespaces = new HashMap<String, String>();
        namespaces.put("rapi", "http://marklogic.com/rest-api");
        namespaces.put("prop", "http://marklogic.com/xdmp/property");
        namespaces.put("xs",   "http://www.w3.org/2001/XMLSchema");
        namespaces.put("xsi",  "http://www.w3.org/2001/XMLSchema-instance");

        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

        xpather = XMLUnit.newXpathEngine();
        xpather.setNamespaceContext(namespaceContext);
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite()
	throws JAXBException, ParserConfigurationException, SAXException, IOException, XpathException {
		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		root.setAttribute("xml:lang", "en");
		root.setAttribute("foo", "bar");
		root.appendChild(domDocument.createElement("child"));
		root.appendChild(domDocument.createTextNode("mixed"));
		domDocument.appendChild(root);

		byte[] before    = null;
		byte[] after     = null;
		String beforeStr = null;

		DOMHandle domH = new DOMHandle().with(domDocument);
		before = domH.toBuffer();
		beforeStr = new String(before);

		BytesHandle bytesH = new BytesHandle();
		bytesH.fromBuffer(before);
		after = bytesH.toBuffer();
		assertNotNull("Bytes after",after);
		assertXMLEqual("Bytes buffering",beforeStr,new String(after));

		domH.fromBuffer(before);
		after = domH.toBuffer();
		assertNotNull("DOM  after",after);
		assertXMLEqual("DOM buffering",beforeStr,new String(after));
		
		InputSourceHandle inputSourceH = new InputSourceHandle();
		inputSourceH.fromBuffer(before);
		after = inputSourceH.toBuffer();
		assertNotNull("InputSource after",after);
		assertXMLEqual("InputSource buffering",beforeStr,new String(after));

		InputStreamHandle inputStreamH = new InputStreamHandle();
		inputStreamH.fromBuffer(before);
		after = inputStreamH.toBuffer();
		assertNotNull("InputStream after",after);
		assertXMLEqual("InputStream buffering",beforeStr,new String(after));

		ReaderHandle readerH = new ReaderHandle();
		readerH.fromBuffer(before);
		after = readerH.toBuffer();
		assertNotNull("Reader after",after);
		assertXMLEqual("Reader buffering",beforeStr,new String(after));

		SourceHandle sourceH = new SourceHandle();
		sourceH.fromBuffer(before);
		after = sourceH.toBuffer();
		assertNotNull("Source after",after);
		assertXMLEqual("Source buffering",beforeStr,new String(after));

		StringHandle stringH = new StringHandle();
		stringH.fromBuffer(before);
		after = stringH.toBuffer();
		assertNotNull("String after",after);
		assertXMLEqual("String buffering",beforeStr,new String(after));

		XMLEventReaderHandle eventReaderH = new XMLEventReaderHandle();
		eventReaderH.fromBuffer(before);
		after = eventReaderH.toBuffer();
		assertNotNull("EventReader after",after);
		assertXMLEqual("EventReader buffering",beforeStr,new String(after));

		XMLStreamReaderHandle streamReaderH = new XMLStreamReaderHandle();
		streamReaderH.fromBuffer(before);
		after = streamReaderH.toBuffer();
		assertNotNull("StreamReader after",after);
		assertXMLEqual("StreamReader buffering",beforeStr,new String(after));

		Refers refers = new Refers();
		refers.child = new Referred();

		JAXBContext jaxbContext = JAXBContext.newInstance(Refers.class);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JAXBHandle  jaxbH       = new JAXBHandle(jaxbContext).with(refers);

		before = jaxbH.toBuffer();
		beforeStr = new String(before);
		jaxbH.fromBuffer(before);
		after = jaxbH.toBuffer();
		assertNotNull("JAXB after",after);
		assertXMLEqual("JAXB buffering",beforeStr,new String(after));

		String metadataText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<rapi:metadata uri=\"/test/testMetadataXML1.xml\" xsi:schemaLocation=\"http://marklogic.com/rest-api/database dbmeta.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:prop=\"http://marklogic.com/xdmp/property\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
		  "<rapi:collections>"+
		    "<rapi:collection>/document/collection1</rapi:collection>"+
		    "<rapi:collection>/document/collection2</rapi:collection>"+
		  "</rapi:collections>"+
		  "<rapi:permissions>"+
		    "<rapi:permission>"+
		      "<rapi:role-name>app-user</rapi:role-name>"+
		      "<rapi:capability>read</rapi:capability>"+
		      "<rapi:capability>update</rapi:capability>"+
		    "</rapi:permission>"+
		  "</rapi:permissions>"+
		  "<prop:properties>"+
		    "<first xsi:type=\"xs:string\">value one</first>"+
		    "<second xsi:type=\"xs:string\">2</second>"+
		    "<third>"+
		      "<third.first>value third one</third.first>"+
		      "<third.second>3.2</third.second>"+
		    "</third>"+
		  "</prop:properties>"+
		  "<rapi:quality>3</rapi:quality>"+
		"</rapi:metadata>";

    	DocumentMetadataHandle metadataH = new DocumentMetadataHandle();
		beforeStr = metadataText;
		before = metadataText.getBytes();

		metadataH.fromBuffer(before);
		after = metadataH.toBuffer();
		assertNotNull("DocumentMetadata after",after);
		String afterStr = new String(after);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:collections/rapi:collection[string(.) = '/document/collection1']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);

		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:collections/rapi:collection[string(.) = '/document/collection2']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:permissions/rapi:permission/rapi:role-name[string(.) = 'app-user']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:permissions/rapi:permission/rapi:capability[string(.) = 'read']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:permissions/rapi:permission/rapi:capability[string(.) = 'update']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/prop:properties/first[string(.) = 'value one']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/prop:properties/second[string(.) = '2']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/prop:properties/third/third.first[string(.) = 'value third one']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/prop:properties/third/third.second[string(.) = '3.2']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
		assertTrue("", xpather.getMatchingNodes(
				"/rapi:metadata/rapi:quality[string(.) = '3']",
				XMLUnit.buildControlDocument(afterStr)
				).getLength() == 1);
	}
}
