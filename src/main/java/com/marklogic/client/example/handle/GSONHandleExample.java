/*
 * Copyright 2015 MarkLogic Corporation
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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.extra.gson.GSONHandle;

/**
 * GSONHandleExample illustrates writing and reading content as a JSON structure
 * using the GSON extra library.  You must install the library first.
 */
public class GSONHandleExample {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+GSONHandleExample.class.getName());

		// use either shortcut or strong typed IO
		runShortcut(props);
		runStrongTyped(props);
	}
	public static void runShortcut(ExampleProperties props) throws IOException {
		String filename = "flipper.json";

		// register the handle from the extra library
		DatabaseClientFactory.getHandleRegistry().register(
				GSONHandle.newFactory()
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

		// parse the example file with GSON
		JsonElement writeDocument = new JsonParser().parse(
				new InputStreamReader(docStream, "UTF-8"));

		// write the document
		docMgr.writeAs(docId, writeDocument);

		// ... at some other time ...

		// read the document content
		JsonElement readDocument = docMgr.readAs(docId, JsonElement.class);

		// access the document content
		String aRootField =
			readDocument.getAsJsonObject().entrySet().iterator().next().getKey();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Shortcut) Wrote and read /example/"+filename+
				" content with a root field name of "+aRootField+" using GSON");

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
		GSONHandle writeHandle = new GSONHandle();

		// parse the example file with GSON
		JsonElement writeDocument = writeHandle.getParser().parse(
				new InputStreamReader(docStream, "UTF-8"));
		writeHandle.set(writeDocument);

		// write the document
		docMgr.write(docId, writeHandle);

		// ... at some other time ...

		// create a handle to receive the document content
		GSONHandle readHandle = new GSONHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		JsonElement readDocument = readHandle.get();
		String aRootField =
			readDocument.getAsJsonObject().entrySet().iterator().next().getKey();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Strong Typed) Wrote and read /example/"+filename+
				" content with a root field name of "+aRootField+" using GSON");

		// release the client
		client.release();
	}
}
