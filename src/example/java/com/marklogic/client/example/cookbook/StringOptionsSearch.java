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
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.MatchSnippet;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;

/**
 * StringOptionsSearch illustrates searching for documents and iterating over results
 * with string criteria referencing a constraint defined by options.
 */
public class StringOptionsSearch {
	static final private String OPTIONS_NAME = "products";

	static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

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

	public static void run(String host, int port, String admin_user, String admin_password, String writer_user, String writer_password, Authentication authType) {
		System.out.println("example: "+StringOptionsSearch.class.getName());

		configure( host, port, admin_user,  admin_password,  authType );
		search(    host, port, writer_user, writer_password, authType );

		tearDownExample(host, port, admin_user, admin_password, authType);
	}

	public static void configure(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newQueryOptionsManager();

		// create the query options
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
		builder.append("  <constraint name=\"industry\">\n");
		builder.append("    <value>\n");
		builder.append("      <element ns=\"\" name=\"industry\"/>\n");
		builder.append("    </value>\n");
		builder.append("  </constraint>\n");
		builder.append("</options>\n");

		// initialize a handle with the query options
		StringHandle writeHandle = new StringHandle(builder.toString());

		// write the query options to the database
		optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

		// release the client
		client.release();
	}

	public static void search(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		setUpExample(client);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition(OPTIONS_NAME);
		querydef.setCriteria("neighborhood industry:\"Real Estate\"");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(querydef, resultsHandle);

		System.out.println("Matched "+resultsHandle.getTotalResults()+
				" documents with '"+querydef.getCriteria()+"'\n");

		// iterate over the result documents
		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		System.out.println("Listing "+docSummaries.length+" documents:\n");
		for (MatchDocumentSummary docSummary: docSummaries) {
			String uri = docSummary.getUri();
			int score = docSummary.getScore();

			// iterate over the match locations within a result document
			MatchLocation[] locations = docSummary.getMatchLocations();
			System.out.println("Matched "+locations.length+" locations in "+uri+" with "+score+" score:");
			for (MatchLocation location: locations) {

				// iterate over the snippets at a match location
				for (MatchSnippet snippet : location.getSnippets()) {
					boolean isHighlighted = snippet.isHighlighted();

					if (isHighlighted)
						System.out.print("[");
					System.out.print(snippet.getText());
					if (isHighlighted)
						System.out.print("]");
				}
				System.out.println();
			}
		}

		// release the client
		client.release();
	}

	// set up by writing the document content and options used in the example query
	public static void setUpExample(DatabaseClient client) {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		DocumentIdentifier docId = client.newDocId(null);

		InputStreamHandle contentHandle = new InputStreamHandle();

		for (String filename: filenames) {
			InputStream docStream = StringOptionsSearch.class.getClassLoader().getResourceAsStream(
					"data"+File.separator+filename);
			if (docStream == null)
				throw new RuntimeException("Could not read document example");

			docId.setUri("/example/"+filename);

			contentHandle.set(docStream);

			docMgr.write(docId, contentHandle);
		}
	}

	// clean up by deleting the documents and query options used in the example query
	public static void tearDownExample(
			String host, int port, String user, String password, Authentication authType) {
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		DocumentIdentifier docId = client.newDocId(null);

		for (String filename: filenames) {
			docId.setUri("/example/"+filename);

			docMgr.delete(docId);
		}

		QueryOptionsManager optionsMgr = client.newQueryOptionsManager();

		optionsMgr.deleteOptions(OPTIONS_NAME);

		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			StringOptionsSearch.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
