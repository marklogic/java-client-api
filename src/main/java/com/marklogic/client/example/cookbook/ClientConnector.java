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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.StringHandle;

/**
 * ClientConnector illustrates how to connect to a database.
 */
public class ClientConnector {

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

	public static void run(String host, int port, String user, String password, Authentication authType) {
		System.out.println("example: "+ClientConnector.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// make use of the client connection
		TextDocumentManager docMgr = client.newTextDocumentManager();
		String docId = "/example/text.txt";
		StringHandle handle = new StringHandle();
		handle.set("A simple text document");
		docMgr.write(docId, handle);

		System.out.println("Connected to "+host+":"+port+" as "+user);

		// clean up the written document
		docMgr.delete(docId);

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			ClientConnector.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
