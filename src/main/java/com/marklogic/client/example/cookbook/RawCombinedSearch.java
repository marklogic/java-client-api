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
package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.MatchSnippet;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;

/**
 * RawCombinedSearch illustrates searching for documents and iterating over results
 * by passing structured criteria and query options in a single request.
 */
public class RawCombinedSearch {
	static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+RawCombinedSearch.class.getName());

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		setUpExample(client);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// specify the query and options for the search criteria
		// in raw XML (raw JSON is also supported)
		String rawSearch = new StringBuilder()
    	    .append("<search:search ")
    	    .append(    "xmlns:search='http://marklogic.com/appservices/search'>")
    	    .append("<search:query>")
  	        .append(    "<search:term-query>")
   	        .append(        "<search:text>neighborhoods</search:text>")
   	        .append(    "</search:term-query>")
   	        .append(    "<search:value-constraint-query>")
   	        .append(        "<search:constraint-name>industry</search:constraint-name>")
   	        .append(        "<search:text>Real Estate</search:text>")
   	        .append(    "</search:value-constraint-query>")
	        .append("</search:query>")
    	    .append("<search:options>")
            .append(    "<search:constraint name='industry'>")
    	    .append(        "<search:value>")
    		.append(            "<search:element name='industry' ns=''/>")
    	    .append(        "</search:value>")
            .append(    "</search:constraint>")
            .append("</search:options>")
            .append("</search:search>")
            .toString();

        // create a search definition for the search criteria
        RawCombinedQueryDefinition querydef
                = queryMgr.newRawCombinedQueryDefinitionAs(Format.XML, rawSearch);

        // create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(querydef, resultsHandle);

		System.out.println("(Strong Typed) Matched "+resultsHandle.getTotalResults()+
				" documents with structured query\n");

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
	}

	// set up by writing the document content used in the example query
	public static void setUpExample(DatabaseClient client) throws IOException {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		InputStreamHandle contentHandle = new InputStreamHandle();

		for (String filename: filenames) {
			InputStream docStream = Util.openStream("data"+File.separator+filename);
			if (docStream == null)
				throw new IOException("Could not read document example");

			contentHandle.set(docStream);

			docMgr.write("/example/"+filename, contentHandle);
		}
	}

	// clean up by deleting the documents used in the example query
	public static void tearDownExample(DatabaseClient client) {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		for (String filename: filenames) {
			docMgr.delete("/example/"+filename);
		}
	}
}
