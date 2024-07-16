package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.TextNode;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.client.test.junit5.RequiresMLElevenDotOne;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains tests for the following methods that were exposed for MarkLogic 11.1:
 * joinDocAndUri, documentRootQuery, documentFragmentQuery, documentPermissionQuery, and the string constructors for
 * cts.point and cts.polygon.
 */
@ExtendWith(RequiresMLElevenDotOne.class)
public class NewOpticMethodsInElevenDotOneTest extends AbstractOpticUpdateTest {

	@Test
	void pointString() {
		List<RowRecord> rows = resultRows(op
			.fromView("opticUnitTest", "musician")
			.where(op.eq(op.col("point"), op.cts.point("1,2")))
		);

		assertEquals(1, rows.size());
		assertEquals("Armstrong", rows.get(0).getString("lastName"));
		assertEquals("1,2", rows.get(0).getString("point"));
	}

	@Test
	void polygonString() {
		List<RowRecord> rows = resultRows(op
			.fromView("opticUnitTest", "musician")
			.where(op.eq(op.col("polygon"), op.cts.polygon("1,2 3,4 5,6 1,2")))
		);

		assertEquals(1, rows.size());
		assertEquals("Armstrong", rows.get(0).getString("lastName"));
		assertEquals("1,2 3,4 5,6 1,2", rows.get(0).getString("polygon"));
	}

	@Test
	void joinDocAndUri() {
		List<RowRecord> rows = resultRows(op
			.fromView("opticUnitTest", "musician", null, op.fragmentIdCol("fragmentId"))
			.joinDocAndUri(op.col("doc"), op.col("uri"), op.fragmentIdCol("fragmentId"))
		);

		assertEquals(4, rows.size());
		rows.forEach(row -> {
			assertNotNull(row.get("doc"), "doc should have been added via joinDocAndUri");
			assertNotNull(row.get("uri"), "uri shoudl have been added via joinDocAndUri");
		});
	}

	@Test
	// Fixed via MLE-55
	@ExtendWith(RequiresML12.class)
	void documentRootQuery() {
		List<RowRecord> rows = resultRows(op
			.fromDocUris(op.cts.documentRootQuery("suggest"))
		);

		assertEquals(2, rows.size());
		assertEquals("/sample/suggestion.xml", ((TextNode) rows.get(0).get("uri")).asText());
		assertEquals("/sample2/suggestion.xml", ((TextNode) rows.get(1).get("uri")).asText());
	}

	@Test
	// Fixed via MLE-55
	@ExtendWith(RequiresML12.class)
	void documentFormatQuery() {
		List<RowRecord> rows = resultRows(op
			.fromDocUris(op.cts.andQuery(
				op.cts.documentFormatQuery("text"),
				op.cts.collectionQuery("document-format-query-test")
			))
		);

		assertEquals(1, rows.size());
		assertEquals("/sample/second.txt", ((TextNode) rows.get(0).get("uri")).asText());
	}

	@Test
	// Fixed via MLE-55
	@ExtendWith(RequiresML12.class)
	void documentPermissionQuery() {
		List<RowRecord> rows = resultRows(op
			.fromDocUris(op.cts.documentPermissionQuery("rest-reader", "read"))
		);

		assertTrue(!rows.isEmpty(), "Expecting dozens of documents to have a rest-reader/read permission, " +
			"just want to make sure the query is processed successfully by the server.");
	}
}
