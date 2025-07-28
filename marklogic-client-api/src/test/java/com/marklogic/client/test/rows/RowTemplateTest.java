/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.marklogic.client.row.RowTemplate;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RequiresML11.class)
class RowTemplateTest extends AbstractOpticUpdateTest {

	private RowTemplate rowTemplate;

	@BeforeEach
	void beforeEach() {
		rowTemplate = new RowTemplate(Common.newClient());
	}

	@Test
	void simpleQuery() {
		List<String> lastNames = rowTemplate.query(
			op -> op.fromView("opticUnitTest", "musician").orderBy(op.asc("lastName")),
			rowSet -> rowSet.stream().map(row -> row.getString("lastName"))
				.collect(Collectors.toList())
		);

		assertEquals(4, lastNames.size());
		assertEquals("Armstrong", lastNames.get(0));
		assertEquals("Byron", lastNames.get(1));
		assertEquals("Coltrane", lastNames.get(2));
		assertEquals("Davis", lastNames.get(3));
	}

	@Test
	void updateAndReturnData() {
		List<String> newUris = rowTemplate.update(
			op -> op.fromDocUris(op.cts.collectionQuery("zipcode"))
				.joinDocCols(op.docCols(), op.col("uri"))
				.bind(op.as("uri", op.fn.concat(op.xs.string("/acme"), op.col("uri"))))
				.write(),
			rowSet -> rowSet.stream().map(row -> row.getString("uri"))
				.collect(Collectors.toList())
		);

		assertEquals(2, newUris.size(), "Should have 2 new URIs, one for each existing doc in the zipcodes collection.");
		verifyJsonDoc("/acme/optic/zipcodes/zip1.json", doc ->
			assertEquals(22201, doc.get("zipcode").get("zip").asInt())
		);
		verifyJsonDoc("/acme/optic/zipcodes/zip2.json", doc ->
			assertEquals(22202, doc.get("zipcode").get("zip").asInt())
		);
	}

	@Test
	void updateAndDontReturnData() {
		rowTemplate.update(
			op -> op.fromDocUris(op.cts.collectionQuery("zipcode"))
				.joinDocCols(op.docCols(), op.col("uri"))
				.bind(op.as("uri", op.fn.concat(op.xs.string("/acme"), op.col("uri"))))
				.write()
		);

		verifyJsonDoc("/acme/optic/zipcodes/zip1.json", doc ->
			assertEquals(22201, doc.get("zipcode").get("zip").asInt())
		);
		verifyJsonDoc("/acme/optic/zipcodes/zip2.json", doc ->
			assertEquals(22202, doc.get("zipcode").get("zip").asInt())
		);
	}
}
