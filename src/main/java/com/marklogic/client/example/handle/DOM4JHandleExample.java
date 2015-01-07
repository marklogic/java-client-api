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
package com.marklogic.client.example.handle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.extra.dom4j.DOM4JHandle;

/**
 * DOM4JHandleExample illustrates writing and reading content as a dom4j structure
 * using the dom4j extra library.  You must install the library first.
 */
public class DOM4JHandleExample {
	public static void main(String[] args) throws IOException, DocumentException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props)
	throws IOException, DocumentException {
		System.out.println("example: "+DOM4JHandleExample.class.getName());

		// use either shortcut or strong typed IO
		runShortcut(props);
		runStrongTyped(props);
	}
	public static void runShortcut(ExampleProperties props)
	throws IOException, DocumentException {
		String filename = "flipper.xml";

		// register the handle from the extra library
		DatabaseClientFactory.getHandleRegistry().register(
				DOM4JHandle.newFactory()
				);

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for documents of any format
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// parse the example file with dom4j
		SAXReader reader = new SAXReader();
		reader.setValidation(false);
		Document writeDocument =
			reader.read(new InputStreamReader(docStream, "UTF-8"));

		// write the document
		docMgr.writeAs(docId, writeDocument);

		// ... at some other time ...

		// read the document content
		Document readDocument = docMgr.readAs(docId, Document.class);

		String rootName = readDocument.getRootElement().getName();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Shortcut) Wrote and read /example/"+filename+
				" content with the <"+rootName+"/> root element using dom4j");

		// release the client
		client.release();
	}
	public static void runStrongTyped(ExampleProperties props)
	throws IOException, DocumentException {
		String filename = "flipper.xml";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for documents of any format
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle for the document
		DOM4JHandle writeHandle = new DOM4JHandle();

		// parse the example file with dom4j
		Document writeDocument = writeHandle.getReader().read(
				new InputStreamReader(docStream, "UTF-8"));
		writeHandle.set(writeDocument);

		// write the document
		docMgr.write(docId, writeHandle);

		// ... at some other time ...

		// create a handle to receive the document content
		DOM4JHandle readHandle = new DOM4JHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		Document readDocument = readHandle.get();

		String rootName = readDocument.getRootElement().getName();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Strong Typed) Wrote and read /example/"+filename+
				" content with the <"+rootName+"/> root element using dom4j");

		// release the client
		client.release();
	}
}
