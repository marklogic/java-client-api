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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extra.xom.XOMHandle;
import com.marklogic.client.test.Common;

public class XOMHandleTest {
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
		String docId = "/example/xom-test.xml";

		// create a manager for XML database documents
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

		// create a XOM document
		Element root = new Element("root");
		root.addAttribute(new Attribute("foo", "bar"));
		root.appendChild(new Element("child"));
		root.appendChild("mixed");
		Document writeDocument = new Document(root);

		// create a handle for the XOM document
		XOMHandle writeHandle = new XOMHandle(writeDocument);

		// write the XOM document to the database
		docMgr.write(docId, writeHandle);

		// create a handle to receive the document content as a XOM document
		XOMHandle readHandle = new XOMHandle();

		// read the document content from the database as a XOM document
		docMgr.read(docId, readHandle);

		// access the document content
		Document readDocument = readHandle.get();
		assertNotNull("Wrote null XOM document", readDocument);
		assertXMLEqual("XOM documents not equal", 
				writeDocument.toXML(), readDocument.toXML());

		// delete the document
		docMgr.delete(docId);
	}
}
