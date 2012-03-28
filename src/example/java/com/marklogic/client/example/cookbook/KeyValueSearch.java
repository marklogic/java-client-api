package com.marklogic.client.example.cookbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.QueryManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.MatchSnippet;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;

/**
 * KeyValueSearch illustrates searching for documents and iterating over results
 * with simple pairs of element names and values.
 */
public class KeyValueSearch {
	static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

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
		// connect the client
		DatabaseClient client =
			DatabaseClientFactory.connect(host, port, user, password, authType);

		setUpExample(client);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition(null);
		querydef.put(queryMgr.newElementLocator(new QName("industry")), "Real Estate");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(querydef, resultsHandle);

		System.out.println("Matched "+resultsHandle.getTotalResults()+
				" documents with 'industry' value of 'Real Estate'\n");

		// iterate over the result documents
		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		System.out.println("Listing "+docSummaries.length+" documents:\n");
		for (MatchDocumentSummary docSummary: docSummaries) {

			// iterate over the match locations within a result document
			MatchLocation[] locations = docSummary.getMatchLocations();
			System.out.println("Matched "+locations.length+" locations in "+docSummary.getUri()+":");
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

		tearDownExample(client);

		// release the client
		client.release();
	}

	// set up by writing the document content and options used in the example query
	public static void setUpExample(DatabaseClient client) {
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		DocumentIdentifier docId = client.newDocId(null);

		InputStreamHandle contentHandle = new InputStreamHandle();

		for (String filename: filenames) {
			InputStream docStream = DocumentRead.class.getClassLoader().getResourceAsStream(
					"data"+File.separator+filename);
			if (docStream == null)
				throw new RuntimeException("Could not read document example");

			docId.setUri("/example/"+filename);

			contentHandle.set(docStream);

			docMgr.write(docId, contentHandle);
		}
	}

	// clean up by deleting the documents and options used in the example query
	public static void tearDownExample(DatabaseClient client) {
		GenericDocumentManager docMgr = client.newDocumentManager();

		DocumentIdentifier docId = client.newDocId(null);

		for (String filename: filenames) {
			docId.setUri("/example/"+filename);

			docMgr.delete(docId);
		}
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			KeyValueSearch.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
