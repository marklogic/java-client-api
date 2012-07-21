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
import java.util.Properties;
import java.util.Set;

import org.htmlcleaner.ITagInfoProvider;
import org.htmlcleaner.TagInfo;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;

/**
 * HTMLCleanerHandleExample illustrates writing HTML content as
 * an indexable XHTML document using the HTMLCleanerHandle example
 * of a content handle extension.
 */
public class HTMLCleanerHandleExample {

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
		System.out.println("example: "+HTMLCleanerHandleExample.class.getName());

		String fileroot = "sentiment";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user, password, authType);

		// create a manager for documents of any format
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// read the example file
		InputStream docStream = HTMLCleanerHandleExample.class.getClassLoader().getResourceAsStream(
				"data"+File.separator+fileroot+".html");
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+fileroot+".xhtml";

		// create a handle for the content
		HTMLCleanerHandle writeHandle = new HTMLCleanerHandle();

		// configure the parser rules for this content
		ITagInfoProvider rules = writeHandle.getRulesProvider();
		TagInfo rule = rules.getTagInfo("p");
		Set pCloseTags = rule.getMustCloseTags();
		pCloseTags.add("h1");

		// set the handle to the parsed HTML content
		writeHandle.set(docStream, "UTF-8");

		// write the converted XHTML content
		docMgr.write(docId, writeHandle);

		// create a handle to receive the XHTML content
		StringHandle readHandle = new StringHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// delete the document
		docMgr.delete(docId);

		System.out.println("Wrote /example/"+fileroot+".xhtml using HTMLCleaner\n"+
				readHandle.get());

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream = HTMLCleanerHandleExample.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new IOException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
