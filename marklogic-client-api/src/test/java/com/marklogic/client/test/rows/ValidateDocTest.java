/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;
import java.util.stream.Collectors;

import static com.marklogic.client.io.Format.XML;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequiresML11.class)
public class ValidateDocTest extends AbstractOpticUpdateTest {

	private Set<String> expectedUris = new HashSet<>();
	private DataMovementManager dataMovementManager;

	@BeforeEach
	public void moreSetup() {
		dataMovementManager = Common.client.newDataMovementManager();
	}

	@Test
	public void testXmlSchemaWithLaxMode() {
		WriteBatcher writeBatcher = dataMovementManager.newWriteBatcher();
		DocumentMetadataHandle meta = newDefaultMetadata();
		dataMovementManager.startJob(writeBatcher);
		final int uriCountToWrite = 10;
		for (int i = 0; i < uriCountToWrite; i++) {
			String uri = "/acme/" + i + ".xml";
			writeBatcher.addAs(uri, meta, new StringHandle("<Doc><key>" + i + "</key><Value>value" + i + "</Value></Doc>").withFormat(XML));
			expectedUris.add(uri);
		}
		writeBatcher.flushAndWait();
		dataMovementManager.stopJob(writeBatcher);

		PlanBuilder.Plan plan = op
			.fromDocUris(op.cts.directoryQuery("/acme/"))
			.joinDoc(op.col("doc"), op.col("uri"))
			.validateDoc(op.col("doc"),
				op.schemaDefinition("xmlSchema").withMode("lax")
			);
		XMLDocumentManager mgr = Common.client.newXMLDocumentManager();
		expectedUris.forEach(uri -> assertNotNull(mgr.exists(uri), "URI was not written: " + uri));

		List<RowRecord> rows = resultRows(plan);

		List<String> persistedUris = rows.stream().map(row -> {
			String uri = row.getString("uri");
			return uri;
		}).collect(Collectors.toList());
		assertEquals(uriCountToWrite, persistedUris.size());
		expectedUris.forEach(uri -> assertTrue(persistedUris.contains(uri), "persistedUris does not contain " + uri));
	}

	/**
	 * Shoehorning some test coverage of onError for the 11.2 server release into this test.
	 */
	@Test
	public void testXmlSchemaWithStrictMode() {
		String[][] triples = new String[][]{
			new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o1"},
			new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o2"},
			new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o3"}
		};
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

		String triplesXML =
			"<sem:triples xmlns:sem=\"http://marklogic.com/semantics\">\n" +
				String.join("\n", Arrays
					.stream(triples)
					.map(triple ->
						"<sem:triple>" +
							"<sem:subjectt>" + triple[0] + "</sem:subjectt>" +
							"<sem:predicate>" + triple[1] + "</sem:predicate>" +
							"<sem:object>" + triple[2] + "</sem:object>" +
							"</sem:triple>"
					)
					.toArray(size -> new String[size])) +
				"</sem:triples>";
		DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
		DocumentMetadataHandle metadata = newDefaultMetadata();
		for (int i = 0; i < 4; i++) {
			writeSet.add("/acme/" + i + ".xml", metadata, new StringHandle(triplesXML).withFormat(Format.XML));
		}
		docMgr.write(writeSet);

		PlanBuilder.ModifyPlan plan = op
			.fromDocUris(op.cts.directoryQuery("/acme/"))
			.joinDoc(op.col("doc"), op.col("uri"))
			.validateDoc(op.col("doc"), op.schemaDefinition("xmlSchema").withMode("strict"));

		// Verify that "continue" results in errors being returned.
		final String defaultErrorsColumn = "sys.errors";
		resultRows(plan.onError("continue")).forEach(row -> {
			JsonNode errors = row.getContent(defaultErrorsColumn, new JacksonHandle()).get();
			assertEquals("Validation error", errors.get("message").asText());
			assertTrue(errors.has("data"));
			String errorMessage = errors.get("data").get(0).asText();
			assertTrue(errorMessage.contains("Found sem:subjectt but expected"), "Unexpected error: "  + errorMessage);
		});

		// And verify a custom error column works.
		resultRows(plan.onError("continue", "myErrors")).forEach(row -> {
			JsonNode errors = row.getContent("myErrors", new JacksonHandle()).get();
			assertEquals("Validation error", errors.get("message").asText());
		});

		// "fail" should throw an error.
		FailedRequestException ex = assertThrows(FailedRequestException.class, () -> resultRows(plan.onError("fail")));
		assertTrue(ex.getMessage().contains("Found sem:subjectt but expected"), "Unexpected error: " + ex.getMessage());
	}

	@Test
	public void testWriteWithXmlSchemaWithStrictMode() {
		String[][] triples = new String[][]{
			new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o1"},
			new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o2"},
			new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o3"}
		};

		String triplesXML =
			"<sem:triples xmlns:sem=\"http://marklogic.com/semantics\">\n" +
				String.join("\n", (String[]) Arrays
					.stream(triples)
					.map(triple ->
						"<sem:triple>" +
							"<sem:subject>" + triple[0] + "</sem:subject>" +
							"<sem:predicate>" + triple[1] + "</sem:predicate>" +
							"<sem:object>" + triple[2] + "</sem:object>" +
							"</sem:triple>"
					)
					.toArray(size -> new String[size])) +
				"</sem:triples>";
		DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
		DocumentMetadataHandle metadata = newDefaultMetadata();
		for (int i = 0; i < 4; i++) {
			writeSet.add("/acme/" + i + ".xml", metadata, new StringHandle(triplesXML).withFormat(Format.XML));
		}

		PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes())
			.validateDoc(op.col("doc"),
				op.schemaDefinition("xmlSchema").withMode("strict"))
			.write();
		plan = plan.bindParam("myDocs", writeSet);

		List<RowRecord> rows = resultRows(plan);
		assertEquals(4, rows.size());
	}

	@Test
	public void testUsingFromParamAndSchematron() {
		DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
		DocumentMetadataHandle metadata = newDefaultMetadata();

		writeSet.add("/acme/doc3.xml", metadata, new StringHandle("<user id=\"003\">\n" +
			"  <name>George</name>\n" +
			"  <gender>Male</gender>\n" +
			"  <age>15</age>\n" +
			"  <score total=\"90\">\n" +
			"    <test-1>50</test-1>\n" +
			"    <test-2>40</test-2>\n" +
			"  </score>  \n" +
			"  <result>pass</result> \n" +
			"</user>").withFormat(Format.XML));

		PlanBuilder.ModifyPlan plan = op.fromParam("myDocs", "", op.docColTypes())
			.validateDoc(op.col("doc"), op.schemaDefinition("schematron").withSchemaUri("/validateDoc/schematron.sch"))
			.write();

		System.out.println("PLAN: " + plan.exportAs(ObjectNode.class).toPrettyString());

		List<RowRecord> rows = resultRows(plan.bindParam("myDocs", writeSet));
		assertEquals(1, rows.size());
		DocumentManager docMgr = Common.client.newDocumentManager();
		assertNotNull(docMgr.exists("/acme/doc3.xml"));
	}

	@Test
	public void testWithJsonSchema() {
		PlanBuilder.ModifyPlan plan = op
			.fromDocDescriptors(
				op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total", 2))),
				op.docDescriptor(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("count", 2).put("total", 3)))
			)
			.validateDoc(op.col("doc"),
				op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/jsonSchema.json"));

		verifyExportedPlanReturnsSameRowCount(plan);
		Iterator<RowRecord> rows = rowManager.resultRows(plan).iterator();
		while (rows.hasNext()) {
			RowRecord str = rows.next();
			String uri = str.getString("uri");
			expectedUris.add(uri);
		}
		assertTrue(expectedUris.size() == 2);
		rowManager.execute(plan.write());
		DocumentManager docMgr = Common.client.newDocumentManager();
		assertTrue(docMgr.exists("/acme/doc1.json") != null);
		assertTrue(docMgr.exists("/acme/doc2.json") != null);
		assertTrue(
			docMgr.read("/acme/doc1.json").next().getContent(new StringHandle()).toString().equals("{\"count\":1, \"total\":2}"));
		assertTrue(
			docMgr.read("/acme/doc2.json").next().getContent(new StringHandle()).toString().equals("{\"count\":2, \"total\":3}"));

		assertTrue(expectedUris.contains("/acme/doc1.json"));
		assertTrue(expectedUris.contains("/acme/doc2.json"));
	}

	@Test
	public void testWithJsonSchemaAndInvalidJsons() {
		PlanBuilder.ModifyPlan plan = op
			.fromDocDescriptors(
				op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total", 2))),
				op.docDescriptor(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("count", -1).put("total", 3))),
				op.docDescriptor(newWriteOp("/acme/doc3.json", mapper.createObjectNode().put("count", 2).put("total", 13))),
				op.docDescriptor(newWriteOp("/acme/doc4.json", mapper.createObjectNode().put("count", -2).put("total", 23))),
				op.docDescriptor(newWriteOp("/acme/doc5.json", mapper.createObjectNode().put("count", 3).put("total", 33))),
				op.docDescriptor(newWriteOp("/acme/doc6.json", mapper.createObjectNode().put("count", 4).put("total", 34))),
				op.docDescriptor(newWriteOp("/acme/doc7.json", mapper.createObjectNode().put("count", -5).put("total", 38)))
			)
			.validateDoc(op.col("doc"),
				op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/jsonSchema.json"));

		FailedRequestException ex = assertThrows(FailedRequestException.class, () -> verifyExportedPlanReturnsSameRowCount(plan));
		assertTrue(ex.getMessage().contains("XDMP-JSVALIDATEINVMINMAX"), "Unexpected message: " + ex.getMessage());
	}

	@Test
	public void testWithNonExistingSchema() {
		PlanBuilder.ModifyPlan plan = op
			.fromDocDescriptors(
				op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("count", 1).put("total", 2)))
			).validateDoc(op.col("doc"),
				op.schemaDefinition("jsonSchema").withSchemaUri("/validateDoc/Non-Existing.json")
			).write();

		FailedRequestException ex = assertThrows(FailedRequestException.class, () -> rowManager.execute(plan));
		assertTrue(ex.getMessage().contains("XDMP-JSVALIDATEBADSCHEMA: Invalid schema"),
			"Unexpected error: " + ex.getMessage());
	}
}
