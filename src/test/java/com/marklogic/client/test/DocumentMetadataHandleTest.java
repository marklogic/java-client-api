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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.StringHandle;

public class DocumentMetadataHandleTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWriteMetadata() throws SAXException, IOException, XpathException, ParserConfigurationException {
		String docId   = "/test/testMetadataXML1.xml";
		String content = "<?xml version='1.0' encoding='UTF-8'?>\n"+
			"<root mode='mixed' xml:lang='en'>\n"+
			"<child mode='basic'>value</child>\n"+
			"A simple XML document\n"+
			"</root>\n";

		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new StringHandle().with(content));

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

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		Document document = factory.newDocumentBuilder().newDocument();
		Element third = document.createElement("third");
		Element child = document.createElement("third.first");
		child.setTextContent("value third one");
		third.appendChild(child);
		child = document.createElement("third.second");
		child.setTextContent("3.2");
		third.appendChild(child);
		NodeList thirdChildren = third.getChildNodes();

		DocumentMetadataHandle metaWriteHandle = new DocumentMetadataHandle();
		metaWriteHandle.getCollections().addAll("/document/collection1", "/document/collection2");
		metaWriteHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metaWriteHandle.getProperties().put("first", "value one");
		metaWriteHandle.getProperties().put("second", 2);
		metaWriteHandle.getProperties().put("third", thirdChildren);
		metaWriteHandle.setQuality(3);

		docMgr.setMetadataCategories(Metadata.ALL);

		for (int pass=0; pass < 2; pass++) {
			if (pass==0) {
				docMgr.writeMetadata(docId, new StringHandle().with(metadataText));
			} else if (pass==1) {
				docMgr.writeMetadata(docId, metaWriteHandle);
				StringHandle xmlStringHandle = new StringHandle();
				String stringMetadata = docMgr.readMetadata(docId, xmlStringHandle).get();
				assertTrue("Could not get document metadata as an XML String", stringMetadata != null && stringMetadata.length() > 0);
			} else {
				assertTrue("Test case error", false);
			}

			DocumentMetadataHandle metaReadHandle = docMgr.readMetadata(docId, new DocumentMetadataHandle());
			assertTrue("Could not get document metadata as a structure", metaReadHandle != null);
			DocumentCollections collections = metaReadHandle.getCollections();
			assertEquals("Collection with wrong size", 2, collections.size());
			assertTrue("Collection with wrong values", collections.contains("/document/collection1") && collections.contains("/document/collection2"));
			DocumentPermissions permissions = metaReadHandle.getPermissions();
			// rest-reader and rest-writer expected
			assertEquals("Permissions with wrong size", 3, permissions.size());
			assertTrue("Permissions without key", permissions.containsKey("app-user"));
			assertEquals("Permission key with wrong value size", 2, permissions.get("app-user").size());
			assertTrue("Permission key with wrong values", permissions.get("app-user").contains(Capability.READ) && permissions.get("app-user").contains(Capability.UPDATE));
			DocumentProperties properties = metaReadHandle.getProperties();
			assertTrue("Properties without first property", properties.containsKey("first"));
			assertTrue("Properties without second property", properties.containsKey("second"));
			assertTrue("Properties without third property", properties.containsKey("third"));
			assertEquals("First property with wrong value", "value one", properties.get("first"));
			if (pass==0) {
				assertEquals("Second property with wrong value", String.valueOf(2), properties.get("second"));
			} else if (pass==1) {
				assertEquals("Second property with wrong value", 2, properties.get("second"));
			}
			Object thirdValue = properties.get("third");
			assertTrue("Third property with wrong class for value", thirdValue instanceof NodeList);
			NodeList thirdNodes = (NodeList) thirdValue;
			ArrayList<Element> thirdElements = new ArrayList<Element>(); 
			for (int i=0; i < thirdNodes.getLength(); i++) {
				Node thirdNode = thirdNodes.item(i);
				if (thirdNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				thirdElements.add((Element) thirdNode);
			}
			assertEquals("Third property with wrong number of child nodes", thirdChildren.getLength(), thirdElements.size());
			for (int i=0; i < thirdChildren.getLength(); i++) {
				Node    expectedNode = thirdChildren.item(i);
				Element actualNode   = thirdElements.get(i);
				assertEquals("Third property with wrong child name "+i,  expectedNode.getNodeName(),  actualNode.getNodeName());
				assertEquals("Third property with wrong child value "+i, expectedNode.getNodeValue(), actualNode.getNodeValue());
			}
			assertEquals("Wrong quality", 3, metaReadHandle.getQuality());
		}
	}
}
