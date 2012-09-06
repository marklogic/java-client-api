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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

/**
 * DocumentReadTransform installs a server transform for converting XML documents
 * with a known vocabulary to HTML documents for presentation.
 */
public class DocumentReadTransform {
	static final private String TRANSFORM_NAME = "xml2html";

	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	// install the transform and then read a transformed document 
	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+DocumentReadTransform.class.getName());

		installTransform(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		readDocument(props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		tearDownExample(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);
	}

	public static void installTransform(String host, int port, String user, String password, Authentication authType) throws IOException {
		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				host, port, user, password, authType);

		// create a manager for transform extensions
		TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

		// specify metadata about the transform extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("XML-TO-HTML XSLT Transform");
		metadata.setDescription("This plugin transforms an XML document with a known vocabulary to HTML");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		// acquire the transform source code
		InputStream transStream = Util.openStream(
			"scripts"+File.separator+TRANSFORM_NAME+".xsl");
		if (transStream == null)
			throw new IOException("Could not read example transform");

		// create a handle on the transform source code
		InputStreamHandle handle = new InputStreamHandle();
		handle.set(transStream);

		// write the transform extension to the database
		transMgr.writeXSLTransform(TRANSFORM_NAME, handle, metadata);

		System.out.println("Installed the "+TRANSFORM_NAME+" transform");

		// release the client
		client.release();
	}

	public static void readDocument(String host, int port, String user, String password, Authentication authType) throws IOException {
		String filename = "flipper.xml";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				host, port, user, password, authType);

		// create an identifier for the document
		String docId = "/example/"+filename;

		setUpExample(client, docId, filename);

		// create a manager for text documents
		TextDocumentManager docMgr = client.newTextDocumentManager();

		// create a handle on the content
		StringHandle readHandle = new StringHandle();

		// specify the mime type for the content
		readHandle.setMimetype("text/html");

		// specify the transform
		ServerTransform transform = new ServerTransform(TRANSFORM_NAME);

		// read the XML content as HTML by transforming in the database
		docMgr.read(docId, readHandle, transform);

		System.out.println("Read "+docId+" with transform");
		System.out.println(readHandle.get());

		// release the client
		client.release();
	}

	// set up by writing document content for the example to read
	public static void setUpExample(DatabaseClient client, String docId, String filename) throws IOException {
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		InputStreamHandle handle = new InputStreamHandle();
		handle.set(docStream);

		docMgr.write(docId, handle);
	}

	// clean up by deleting the read document and the example transform
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(
				host, port, user, password, authType);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		docMgr.delete("/example/flipper.xml");

		TransformExtensionsManager transMgr =
			client.newServerConfigManager().newTransformExtensionsManager();

		transMgr.deleteTransform(TRANSFORM_NAME);

		client.release();
	}
}
