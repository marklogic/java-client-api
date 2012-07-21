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
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;

/**
 * DocumentWriter illustrates how to write content to a database document.
 */
public class DocumentWrite {

	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for writer user
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, writer_user, writer_password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType) throws IOException {
		System.out.println("example: "+DocumentWrite.class.getName());

		String filename = "flipper.xml";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// acquire the content
		InputStream docStream = DocumentWrite.class.getClassLoader().getResourceAsStream(
			"data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle on the content
		InputStreamHandle handle = new InputStreamHandle();
		handle.set(docStream);

		// write the document content
		docMgr.write(docId, handle);

		System.out.println("Wrote /example/"+filename+" content");

		tearDownExample(docMgr, docId);

		// release the client
		client.release();
	}

	// clean up by deleting the document that the example wrote
	public static void tearDownExample(XMLDocumentManager docMgr, String docId) {
		docMgr.delete(docId);
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			DocumentWrite.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new IOException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
