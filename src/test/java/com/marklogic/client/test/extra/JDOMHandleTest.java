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

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.test.Common;

public class JDOMHandleTest {
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

		// create a JDOM document
		Document writeDocument = new Document();
		Element root = new Element("root");
		root.setAttribute("foo", "bar");
		root.addContent(new Element("child"));
		root.addContent("mixed");
		writeDocument.setRootElement(root);

		// create a handle for the JDOM document
		JDOMHandle writeHandle = new JDOMHandle(writeDocument);

		// write the JDOM document to the database
		docMgr.write(docId, writeHandle);

		// create a handle to receive the database content as a JDOM document
		JDOMHandle readHandle = new JDOMHandle();

		// read the document content from the database as a JDOM document
		docMgr.read(docId, readHandle);

		// access the document content
		Document readDocument = readHandle.get();
		assertNotNull("Wrote null JDOM document", readDocument);
		assertXMLEqual("JDOM document not equal", 
				writeHandle.toString(), readHandle.toString());

		// delete the document
		docMgr.delete(docId);
	}
}
