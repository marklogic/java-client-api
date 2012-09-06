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
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

/**
 * DocumentFormats illustrates working with documents in multiple or unknown formats.
 */
public class DocumentFormats {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+DocumentFormats.class.getName());

		// a list of files with the format of each file
		String[][] fileEntries = {
				{"mlfavicon.png",   "binary"},
				{"siccodes.json",   "JSON"},
				{"producturis.txt", "text"},
				{"flipper.xml",     "XML"}
				};

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// iterate over the files
		for (String[] fileEntry: fileEntries) {
			String filename = fileEntry[0];
			String format   = fileEntry[1];  // used only for the log message

			writeReadDeleteDocument(client, filename, format);
		}

		// release the client
		client.release();
	}

	// write, read, and delete the document
	public static void writeReadDeleteDocument(DatabaseClient client, String filename, String format) throws IOException {
		// create a manager for documents of any format
		GenericDocumentManager docMgr = client.newDocumentManager();

		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle for the document
		InputStreamHandle writeHandle = new InputStreamHandle();
		writeHandle.set(docStream);

		// write the document
		docMgr.write(docId, writeHandle);

		// create a handle to receive the document content
		BytesHandle readHandle = new BytesHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		byte[] document = readHandle.get();

		// ... do something with the document content ...

		// delete the document
		docMgr.delete(docId);

		System.out.println("Wrote, read, and deleted /example/"+filename+
				" content with "+document.length+" bytes in the "+format+" format");
	}
}
