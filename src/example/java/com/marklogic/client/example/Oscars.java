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
package com.marklogic.client.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;

/**
 * The Oscars example illustrates creating query options that define constraints,
 * searching with criteria that uses constraints, and opening documents from 
 * search results.
 */
public class Oscars {
	static final private String OPTIONS_NAME = "oscars";

	public static void main(String[] args) throws IOException, ParserConfigurationException {
		Properties props = loadProperties();

		// connection parameters
		String         host     = props.getProperty("example.host");
		int            port     = Integer.parseInt(props.getProperty("oscars.port"));
		String         user     = "admin";
		String         password = "admin";
		Authentication authType = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		new Oscars().run(host, port, user, password, authType);
	}

	public void run(String host, int port, String user, String password, Authentication authType) {
		System.out.println("example: "+Oscars.class.getName());

		configure( host, port, user, password, authType );
		search(    host, port, user, password, authType );
	}

	public void configure(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newQueryOptionsManager();

		// create the query options
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
		builder.append("  <constraint name=\"year\">\n");
		builder.append("    <value>\n");
		builder.append("      <attribute ns=\"\" name=\"year\"/>\n");
		builder.append("      <element ns=\"http://marklogic.com/wikipedia\" name=\"oscar\"/>\n");
		builder.append("    </value>\n");
		builder.append("  </constraint>\n");
		builder.append("  <constraint name=\"award\">\n");
		builder.append("    <value>\n");
		builder.append("      <attribute ns=\"\" name=\"year\"/>\n");
		builder.append("      <element ns=\"http://marklogic.com/wikipedia\" name=\"award\"/>\n");
		builder.append("    </value>\n");
		builder.append("  </constraint>\n");
		builder.append("  <constraint name=\"winner\">\n");
		builder.append("    <value>\n");
		builder.append("      <attribute ns=\"\" name=\"winner\"/>\n");
		builder.append("      <element ns=\"http://marklogic.com/wikipedia\" name=\"award\"/>\n");
		builder.append("    </value>\n");
		builder.append("  </constraint>\n");
		builder.append("  <return-metrics>true</return-metrics>\n");
		builder.append("  <return-facets>true</return-facets>\n");
		builder.append("  <return-results>true</return-results>\n");
		builder.append("</options>\n");

		// initialize a handle with the query options
		StringHandle writeHandle = new StringHandle(builder.toString());

		// write the query options to the database
		optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

		// release the client
		client.release();
	}

	public void search(String host, int port, String user, String password, Authentication authType) {
		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition(OPTIONS_NAME);
		querydef.setCriteria("Lawrence Olivier");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(querydef, resultsHandle);

		// create a manager for the matched XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create a handle for the content of matched XML documents
		DOMHandle docHandle = new DOMHandle();

		// iterate over the matched documents
		System.out.println("Matched "+resultsHandle.getTotalResults()+" documents:\n");
		for (MatchDocumentSummary docSummary: resultsHandle.getMatchResults()) {
			// read a matched document into the handle
			docMgr.read(docSummary, docHandle);

			System.out.print(docSummary.getUri()+": ");

			// get the content of the matched document
			Document document = docHandle.get();

			Element root = document.getDocumentElement();
			NamedNodeMap attributes = root.getAttributes();
			for (int i=0; i < attributes.getLength(); i++) {
				Attr attribute = (Attr) attributes.item(i);

				String attName = attribute.getName();
				if (attName.startsWith("xmlns:"))
					continue;

				if (i > 0)
					System.out.print(",");

				System.out.print(" "+attName+"="+attribute.getNodeValue());
			}

			System.out.println();
		}

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream = Oscars.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
