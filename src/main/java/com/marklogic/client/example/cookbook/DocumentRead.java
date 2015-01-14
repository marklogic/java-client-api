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
package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;

/**
 * DocumentReader illustrates how to read the content of a database document.
 */
public class DocumentRead {
	public static void main(String[] args)
	throws IOException, XPathExpressionException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props)
	throws IOException, XPathExpressionException {
		System.out.println("example: "+DocumentRead.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		setUpExample(client);

		// use either shortcut or strong typed IO
		runShortcut(client);
		runStrongTyped(client);

		tearDownExample(client);

		// release the client
		client.release();
	}
	public static void runShortcut(DatabaseClient client) throws IOException {
		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/flipper.xml";

		// read the document content
		Document document = docMgr.readAs(docId, Document.class);

		// access the document content
		String rootName = document.getDocumentElement().getTagName();
		System.out.println("(Shortcut) Read "+docId+" content with the <"+rootName+"/> root element");
	}
	public static void runStrongTyped(DatabaseClient client)
	throws IOException, XPathExpressionException {
		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/flipper.xml";

		// create a handle to receive the document content
		DOMHandle handle = new DOMHandle();

		// read the document content
		docMgr.read(docId, handle);
		Document document = handle.get();

		// apply an XPath 1.0 expression to the document
		String productName = handle.evaluateXPath("string(/product/name)", String.class);

		// access the document content
		String rootName = document.getDocumentElement().getTagName();
		System.out.println("(Strong Typed) Read /example/"+docId+" content with the <"+rootName+"/> root element for the "+productName+" product");
	}

	// set up by writing document content for the example to read
	public static void setUpExample(DatabaseClient client) throws IOException {
		String filename = "flipper.xml";

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		String docId = "/example/"+filename;

		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		InputStreamHandle handle = new InputStreamHandle();
		handle.set(docStream);

		docMgr.write(docId, handle);
	}

	// clean up by deleting the document read by the example
	public static void tearDownExample(DatabaseClient client) {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		String docId = "/example/flipper.xml";

		docMgr.delete(docId);
	}
}
