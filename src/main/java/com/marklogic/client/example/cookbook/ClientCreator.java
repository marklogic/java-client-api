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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;

/**
 * ClientCreator illustrates how to create a database client.
 */
public class ClientCreator {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) {
		System.out.println("example: "+ClientCreator.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
			props.host, props.port, props.writerUser, props.writerPassword,
			props.authType);

		// make use of the client connection
		TextDocumentManager docMgr = client.newTextDocumentManager();
		String docId = "/example/text.txt";
		StringHandle handle = new StringHandle();
		handle.set("A simple text document");
		docMgr.write(docId, handle);

		System.out.println(
			"Connected to "+props.host+":"+props.port+" as "+props.writerUser);

		// clean up the written document
		docMgr.delete(docId);

		// release the client
		client.release();
	}
}
