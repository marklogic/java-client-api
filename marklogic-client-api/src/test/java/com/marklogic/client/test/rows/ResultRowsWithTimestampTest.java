package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultRowsWithTimestampTest extends AbstractOpticUpdateTest {

	private final static String NEW_MUSICIAN_URI = "/test/newMusician.json";

	private final static String NEW_MUSICIAN_JSON = "{\n" +
		"  \"musician\": {\n" +
		"    \"lastName\": \"Smith\",\n" +
		"    \"firstName\": \"Jane\",\n" +
		"    \"dob\": \"1901-08-04\"\n" +
		"  }\n" +
		"}";

	@AfterEach
	void deleteNewMusician() {
		Common.client.newJSONDocumentManager().delete(NEW_MUSICIAN_URI);
	}

	@Test
	void testResultRowsWithPointInTimeQueryTimestamp() {
		rowManager.withUpdate(false);

		final RawQueryDSLPlan plan = rowManager.newRawQueryDSLPlan(new StringHandle("op.fromView('opticUnitTest', 'musician_ml10')"));

		JacksonHandle result = new JacksonHandle();
		JsonNode doc = rowManager.resultDoc(plan, result).get();
		assertEquals(4, doc.get("rows").size(), "Expecting the 4 musicians loaded by test-app to exist");

		final long serverTimestamp = result.getServerTimestamp();
		assertTrue(serverTimestamp > 0, "Unexpected timestamp: " + serverTimestamp);

		// Insert a new musician, which will bump up the server timestamp
		Common.client.newJSONDocumentManager().write(NEW_MUSICIAN_URI, newDefaultMetadata(), new StringHandle(NEW_MUSICIAN_JSON));

		doc = rowManager.resultDoc(plan, new JacksonHandle()).get();
		assertEquals(5, doc.get("rows").size(), "Should now get 5 musician rows due to the 5th row being added by " +
			"inserting the new musician doc");

		// Now verify a point-in-time query works
		result = new JacksonHandle();
		result.setPointInTimeQueryTimestamp(serverTimestamp);
		doc = rowManager.resultDoc(plan, result).get();
		assertEquals(4, doc.get("rows").size(), "Only 4 rows should be returned since the query should have been " +
			"run at a server timestamp prior to the newMusician doc being inserted.");

		// And verify point-in-time works when using resultRows too
		result = new JacksonHandle();
		result.setPointInTimeQueryTimestamp(serverTimestamp);
		Iterator<JacksonHandle> rows = rowManager.resultRows(plan, result).iterator();
		int count = 0;
		while (rows.hasNext()) {
			rows.next();
			count++;
		}
		assertEquals(4, count, "resultRows should honor the point-in-time timestamp, just like resultDoc does");
	}
}
