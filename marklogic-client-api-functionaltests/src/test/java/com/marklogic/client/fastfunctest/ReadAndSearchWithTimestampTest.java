package com.marklogic.client.fastfunctest;

import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Added in 6.4.1; the 3 methods being tested had been commented out with a comment indicating that the server APIs
 * were not fully fleshed out, but those APIs - specifically, how the REST API receives a server timestamp - have long
 * been present in the REST API.
 */
public class ReadAndSearchWithTimestampTest extends AbstractFunctionalTest {

	@Test
	void test() {
		final String collection = "timestamp-test";
		final String[] uris = writeJsonDocs(2, collection).toArray(new String[]{});

		final QueryManager queryManager = client.newQueryManager();
		final GenericDocumentManager docManager = client.newDocumentManager();
		final StructuredQueryDefinition collectionQuery = queryManager.newStructuredQueryBuilder().collection(collection);

		SearchHandle searchResults = queryManager.search(collectionQuery, new SearchHandle());
		assertEquals(2, searchResults.getTotalResults());
		final long serverTimestamp = searchResults.getServerTimestamp();
		logger.info("Server timestamp: " + serverTimestamp);

		// Write additional docs to the same collection and verify they are not returned by subsequent point-in-time
		// queries. Note that the first 2 URIs are the same as the ones originally written.
		writeJsonDocs(5, collection);

		// Verify each of the exposed methods in 6.4.1
		assertEquals(2, docManager.read(serverTimestamp, uris).size());
		assertEquals(2, docManager.read(serverTimestamp, null, uris).size());
		assertEquals(2, docManager.search(collectionQuery, 1, serverTimestamp).size());

		assertEquals(5, docManager.search(collectionQuery, 1).size(),
			"A query without a timestamp should return all of the documents in the collection.");
	}
}
