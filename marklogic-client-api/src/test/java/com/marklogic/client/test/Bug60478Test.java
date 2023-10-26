package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import org.junit.jupiter.api.Test;

public class Bug60478Test {

	@Test
	void test() {
		// Creates a client using "rest-evaluator" as the user, who has a password of "x".
		DatabaseClient client = Common.newEvalClient();

		// Overwrites the /sample.xml document.
		client.newServerEval().modulePath("/overwrite-sample-doc.xqy").evalAs(String.class);

		/**
		 * Run a query that:
		 *
		 * 1. Modifies an unrelated document in the database, forcing an update transaction.
		 * 2. Runs a simple Optic query.
		 * 3. Runs a simple CTS search query.
		 *
		 * If the Optic query is left in, then a read lock is obtained on /sample.xml - though it's not touched in
		 * this call to MarkLogic.
		 *
		 * If the Optic query is commented out and only the CTS search query is run, a read lock is not obtained on
		 * /sample.xml .
		 *
		 * If this is instead run using the "admin" user, or if the "rest-evaluator" user is given a role of "admin",
		 * then a read lock is not obtained on /sample.xml.
		 *
		 * Additionally, in some cases, read locks will be returned on a couple dozen other documents inexplicably, as
		 * they are never accessed by any code.
		 */
		String query = "xquery version '1.0-ml'; " +
			"import module namespace op = 'http://marklogic.com/optic' at '/MarkLogic/optic.xqy'; " +
			"let $_ := xdmp:node-replace(fn:doc('/sample2/suggestion.xml'), fn:doc('/sample2/suggestion.xml')) " +
			"let $result := op:from-view('opticUnitTest', 'musician') => op:result() " +
			"let $docs := cts:search(/, cts:collection-query('/optic/music'))" +
			"return xdmp:transaction-locks()";

		System.out.println(query);
		String output = client.newServerEval().xquery(query).evalAs(String.class);
		System.out.println("Output: " + output);
	}
}
