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

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DocumentDescriptor;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.ServerConfigurationManager.Policy;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;

/**
 * Optimistic Locking creates a document only when the document doesn't exist and
 * updates or deletes a document only when the document hasn't changed.
 */
public class OptimisticLocking {
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
		System.out.println("example: "+OptimisticLocking.class.getName());

		requireOptimisticLocking( host, port, admin_user,  admin_password,  authType );

		modifyDatabase(           host, port, writer_user, writer_password, authType );

		tearDownExample(host, port, admin_user, admin_password, authType);
	}

	public static void requireOptimisticLocking(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// require content versions for updates and deletes
		// use Policy.OPTIONAL to allow but not require versions
		configMgr.setContentVersionRequests(Policy.REQUIRED);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("enabled optimistic locking");

		// release the client
		client.release();
	}

	public static void modifyDatabase(String host, int port, String user, String password, Authentication authType) {
		String filename = "flipper.xml";

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// acquire the content
		InputStream docStream = DocumentWrite.class.getClassLoader().getResourceAsStream(
			"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		// create a manager for writing XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+filename;

		// provide a handle for the initial content of the document
		InputStreamHandle createHandle = new InputStreamHandle(docStream);
		createHandle.set(docStream);

		// write a new document without a version (and so, without a descriptor)
		// if the document exists, write() throws an exception
		docMgr.write(docId, createHandle);

		// create a descriptor for versions of the document
		DocumentDescriptor desc = docMgr.newDescriptor(docId);

		// provide a handle for updating the content of the document
		DOMHandle updateHandle = new DOMHandle();

		// read the document, capturing the initial version with the descriptor
		docMgr.read(desc, updateHandle);

		System.out.println("created "+docId+" as version "+desc.getVersion());

		// modify the document
		Document document = updateHandle.get();
		document.getDocumentElement().setAttribute("modified", "true");

		// update the document, specifying the current version with the descriptor
		// if the document changed after reading, write() throws an exception
		docMgr.write(desc, updateHandle);

		// get the updated version without getting the content
		desc = docMgr.exists(docId);

		System.out.println("updated "+docId+" as version "+desc.getVersion());

		// delete the document, specifying the current version with the descriptor
		// if the document changed after exists(), delete() throws an exception
		docMgr.delete(desc);

		// release the client
		client.release();
	}

	// clean up by resetting the server configuration
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		ServerConfigurationManager configMgr = client.newServerConfigManager();

		configMgr.readConfiguration();
		configMgr.setContentVersionRequests(Policy.NONE);
		configMgr.writeConfiguration();

		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			OptimisticLocking.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
