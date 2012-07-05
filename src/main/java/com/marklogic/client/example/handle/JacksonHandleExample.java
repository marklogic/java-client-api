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
package com.marklogic.client.example.handle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.JSONDocumentManager;


/**
 * JacksonHandleExample illustrates writing and reading content as a JSON structure
 * using the JacksonHandle example of a content handle extension.
 */
public class JacksonHandleExample {

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

	public static void run(String host, int port, String user, String password, Authentication authType)
	throws IOException {
		System.out.println("example: "+JacksonHandleExample.class.getName());

		String filename = "flipper.json";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for JSON documents
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		// parse the example file into a JDOM structure
		InputStream docStream = JacksonHandleExample.class.getClassLoader().getResourceAsStream(
				"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		JsonNode writeDocument = new ObjectMapper().readValue(
				new InputStreamReader(docStream, "UTF-8"), JsonNode.class);

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle for the document
		JacksonHandle writeHandle = new JacksonHandle();
		writeHandle.set(writeDocument);

		// write the document
		docMgr.write(docId, writeHandle);

		// create a handle to receive the document content
		JacksonHandle readHandle = new JacksonHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		JsonNode readDocument = readHandle.get();
		String aRootField = null;
		Iterator<String> iterator = readDocument.fieldNames();
		while (iterator.hasNext()) {
			aRootField = iterator.next();
		}
		// delete the document
		docMgr.delete(docId);

		System.out.println("Wrote and read /example/"+filename+
				" content with a root field name of "+aRootField+" using Jackson");

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream = JacksonHandleExample.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
