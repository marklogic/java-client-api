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
package com.marklogic.client.test.extra;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extra.dom4j.DOM4JHandle;
import com.marklogic.client.test.Common;

public class DOM4JHandleTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite() throws SAXException, IOException {
		// create an identifier for the database document
		String docId = "/example/jdom-test.xml";

		// create a manager for XML database documents
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

		DocumentFactory factory = new DocumentFactory();

		// create a dom4j document
		Document writeDocument = factory.createDocument();
		Element root = factory.createElement("root");
		root.attributeValue("foo", "bar");
		root.add(factory.createElement("child"));
		root.addText("mixed");
		writeDocument.setRootElement(root);

		// create a handle for the dom4j document
		DOM4JHandle writeHandle = new DOM4JHandle(writeDocument);

		// write the document to the database
		docMgr.write(docId, writeHandle);

		// create a handle to receive the database content as a dom4j document
		DOM4JHandle readHandle = new DOM4JHandle();

		// read the document content from the database as a dom4j document
		docMgr.read(docId, readHandle);

		// access the document content
		Document readDocument = readHandle.get();
		assertNotNull("Wrote null dom4j document", readDocument);
		assertXMLEqual("dom4j document not equal",
				writeDocument.asXML(), readDocument.asXML());

		// delete the document
		docMgr.delete(docId);
	}
}
