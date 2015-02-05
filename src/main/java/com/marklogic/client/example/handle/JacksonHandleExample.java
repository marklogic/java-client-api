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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.JacksonHandle;

/**
 * JacksonHandleExample illustrates writing and reading content as a JSON structure
 * using the Jackson extra library.  You must install the library first.
 */
public class JacksonHandleExample {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+JacksonHandleExample.class.getName());

		// use either shortcut or strong typed IO
		runShortcut(props);
		runStrongTyped(props);
	}
	public static void runShortcut(ExampleProperties props) throws IOException {
		String filename = "flipper.json";

		// register the handle from the extra library
		DatabaseClientFactory.getHandleRegistry().register(
				JacksonHandle.newFactory()
				);

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for JSON documents
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// parse the example file with Jackson
		JsonNode writeDocument = new ObjectMapper().readValue(
				new InputStreamReader(docStream, "UTF-8"), JsonNode.class);

		// write the document
		docMgr.writeAs(docId, writeDocument);

		// ... at some other time ...

		// read the document content
		JsonNode readDocument = docMgr.readAs(docId, JsonNode.class);

		// access the document content
		String aRootField = readDocument.fieldNames().next();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Shortcut) Wrote and read /example/"+filename+
				" content with a root field name of "+aRootField+" using Jackson");

		// release the client
		client.release();
	}
	public static void runStrongTyped(ExampleProperties props) throws IOException {
		String filename = "flipper.json";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for JSON documents
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle for the document
		JacksonHandle writeHandle = new JacksonHandle();

		// parse the example file with Jackson
		JsonNode writeDocument = writeHandle.getMapper().readValue(
				new InputStreamReader(docStream, "UTF-8"), JsonNode.class);
		writeHandle.set(writeDocument);

		// write the document
		docMgr.write(docId, writeHandle);

		// ... at some other time ...

		// create a handle to receive the document content
		JacksonHandle readHandle = new JacksonHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		JsonNode readDocument = readHandle.get();
		String aRootField = readDocument.fieldNames().next();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Strong Typed) Wrote and read /example/"+filename+
				" content with a root field name of "+aRootField+" using Jackson");

		// release the client
		client.release();
	}
}
