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
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.ServerTransform;
import com.marklogic.client.TransformExtensionsManager;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

/**
 * DocumentWriteTransform installs a server transform for converting HTML documents
 * to XHTML documents so HTML documents can be written to the database, indexed,
 * and easily modified.
 */
public class DocumentWriteTransform {
	static final private String TRANSFORM_NAME = "html2xhtml";

	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for writer and admin users
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		String         admin_user      = props.getProperty("example.admin_user");
		String         admin_password  = props.getProperty("example.admin_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, admin_user, admin_password, writer_user, writer_password, authType);
	}

	// install the transform and then write a transformed document 
	public static void run(String host, int port, String admin_user, String admin_password, String writer_user, String writer_password, Authentication authType) {
		System.out.println("example: "+DocumentWriteTransform.class.getName());

		installTransform( host, port, admin_user,  admin_password,  authType );

		writeDocument(    host, port, writer_user, writer_password, authType );

		tearDownExample(host, port, admin_user, admin_password, authType);
	}

	public static void installTransform(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for transform extensions
		TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

		// specify metadata about the transform extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("HTML-TO-XHTML XQuery Transform");
		metadata.setDescription("This plugin adds transforms an HTML document to XHTML");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		// acquire the transform source code
		InputStream transStream = DocumentWrite.class.getClassLoader().getResourceAsStream(
			"scripts"+File.separator+TRANSFORM_NAME+".xqy");
		if (transStream == null)
			throw new RuntimeException("Could not read example transform");

		// create a handle on the transform source code
		InputStreamHandle handle = new InputStreamHandle(transStream);
		handle.set(transStream);

		// write the transform extension to the database
		transMgr.writeXQueryTransform(TRANSFORM_NAME, handle, metadata);

		System.out.println("Installed the "+TRANSFORM_NAME+" transform");

		// release the client
		client.release();
	}

	public static void writeDocument(String host, int port, String user, String password, Authentication authType) {
		String filename = "sentiment.html";

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// acquire the content
		InputStream docStream = DocumentWrite.class.getClassLoader().getResourceAsStream(
			"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		// create a manager for writing text documents
		TextDocumentManager writeMgr = client.newTextDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle on the content
		InputStreamHandle writeHandle = new InputStreamHandle(docStream);
		writeHandle.set(docStream);

		// specify the mime type for the content
		writeHandle.setMimetype("text/html");

		// specify the transform and its parameters
		ServerTransform transform = new ServerTransform(TRANSFORM_NAME);
		transform.put("drop-font-tags",              "yes");
		transform.put("drop-proprietary-attributes", "yes");
		transform.put("enclose-block-text",          "yes");
		transform.put("enclose-text",                "yes");
		transform.put("logical-emphasis",            "yes");

		// write the HTML content as XHTML by transforming in the database
		writeMgr.write(docId, writeHandle, transform);

		System.out.println("Wrote "+docId+" with transform");

		// read back the transformed XHTML document
		XMLDocumentManager readMgr = client.newXMLDocumentManager();

		StringHandle readHandle = new StringHandle();
		readMgr.read(docId, readHandle);

		System.out.println(readHandle.get());

		// release the client
		client.release();
	}

	// clean up by deleting the written document and the example transform
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		TextDocumentManager docMgr = client.newTextDocumentManager();

		docMgr.delete("/example/sentiment.html");

		TransformExtensionsManager transMgr =
			client.newServerConfigManager().newTransformExtensionsManager();

		transMgr.deleteTransform(TRANSFORM_NAME);

		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWriteTransform.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
