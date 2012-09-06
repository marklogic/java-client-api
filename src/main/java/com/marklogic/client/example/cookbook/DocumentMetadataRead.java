/*
 * Copyright 2012 MarkLogic Corporation
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

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.InputStreamHandle;

/**
 * DocumentMetadataReader illustrates how to read the metadata and content of a database document
 * in a single request.
 */
public class DocumentMetadataRead {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+DocumentMetadataRead.class.getName());

		String filename = "flipper.xml";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+filename;

		setUpExample(docMgr, docId, filename);

		// create a handle to receive the document metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// create a handle to receive the document content
		DOMHandle contentHandle = new DOMHandle();

		// read the document metadata and content
		docMgr.read(docId, metadataHandle, contentHandle);

		// access the document metadata
		DocumentCollections collections = metadataHandle.getCollections();

		// access the document content
		Document document = contentHandle.get();

		String collFirst = collections.toArray(new String[collections.size()])[0];
		String rootName = document.getDocumentElement().getTagName();
		System.out.println("Read /example/"+filename +
				" metadata and content in the '"+collFirst+"' collection with the <"+rootName+"/> root element");

		tearDownExample(docMgr, docId);

		// release the client
		client.release();
	}

	// set up by writing document metadata and content for the example to read
	public static void setUpExample(XMLDocumentManager docMgr, String docId, String filename) throws IOException {
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("products", "real-estate");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.setQuality(1);

		InputStreamHandle handle = new InputStreamHandle();
		handle.set(docStream);

		docMgr.write(docId, metadataHandle, handle);
	}

	// clean up by deleting the document read by the example
	public static void tearDownExample(XMLDocumentManager docMgr, String docId) {
		docMgr.delete(docId);
	}
}
