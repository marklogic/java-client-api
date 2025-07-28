package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These tests are not intended to be great examples of how to use patch operations, but
 * rather to provide some test coverage.
 */
@ExtendWith(RequiresML11.class)
public class OpticPatchTest extends AbstractOpticUpdateTest {

	private static final String JSON_URI = "/a.json";
	private static final String XML_URI = "/a.xml";
	private static final Map<String, String> XML_NAMESPACES = new HashMap<>();

	private DatabaseClient client;

	@BeforeEach
	void beforeEach() {
		client = Common.newClient();
		client.newJSONDocumentManager().write(JSON_URI, new DocumentMetadataHandle()
				.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
			, new JacksonHandle(new ObjectMapper().createObjectNode().putObject("parent").put("hello", "world"))
		);

		client.newXMLDocumentManager().write(XML_URI, new DocumentMetadataHandle()
				.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
			, new StringHandle("<parent xmlns='org:example'><child>1</child></parent>")
		);

		XML_NAMESPACES.put("oe", "org:example");
	}

	@Test
	void replaceValueWithSimpleString() {
		PlanBuilder.ModifyPlan plan = op.fromDocUris(op.cts.documentQuery(JSON_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/")
				.replaceValue("hello", op.xs.string("new value"))
			)
			.write();

		rowManager.withUpdate(true).execute(plan);

		// Read doc and verify it was patched correctly.
		JsonNode doc = client.newJSONDocumentManager().read(JSON_URI, new JacksonHandle()).get();
		assertEquals("new value", doc.get("hello").asText());
	}

	@Test
	void replaceValueWithJsonObject() {
		PlanBuilder.ModifyPlan plan = op.fromDocUris(op.cts.documentQuery(JSON_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/")
				.replaceValue("hello", op.jsonObject(op.prop("does-this", op.xs.string("work?"))))
			)
			.write();

		rowManager.withUpdate(true).execute(plan);

		JsonNode doc = client.newJSONDocumentManager().read(JSON_URI, new JacksonHandle()).get();
		assertEquals("work?", doc.get("hello").get("does-this").asText());
	}

	@Test
	void insertAfter() {
		String content = resultRows(op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/oe:parent", XML_NAMESPACES)
				.insertAfter("oe:child", op.xs.string("Hi"))
			)).get(0).getContent("doc", new StringHandle()).get();

		assertTrue(content.contains("<child>1</child>Hi</parent>"), "Unexpected content: " + content);
	}

	@Test
	void insertBefore() {
		String content = resultRows(op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/oe:parent", XML_NAMESPACES)
				.insertBefore("oe:child", op.xs.string("Hi"))
			)).get(0).getContent("doc", new StringHandle()).get();

		assertTrue(content.contains(">Hi<child>"), "Unexpected content: " + content);
	}

	@Test
	void insertChild() {
		String content = resultRows(op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/oe:parent", XML_NAMESPACES)
				.insertChild("oe:child", op.xs.string("Hi"))
			)).get(0).getContent("doc", new StringHandle()).get();

		assertTrue(content.contains("<child>1Hi</child>"), "Unexpected content: " + content);
	}

	@Test
	void replace() {
		String content = resultRows(op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/", XML_NAMESPACES)
				.replace("/oe:parent/oe:child/text()", op.xs.string("new text"))
			)).get(0).getContent("doc", new StringHandle()).get();

		assertTrue(content.contains("<child>new text</child>"), "Unexpected content: " + content);
	}

	@Test
	void replaceInsertChild() {
		String content = resultRows(op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/", XML_NAMESPACES)
				.replaceInsertChild("oe:parent", "oe:child", op.xs.string("new text"))
			)).get(0).getContent("doc", new StringHandle()).get();

		System.out.println(content);
		assertTrue(content.contains("<parent xmlns=\"org:example\">new text</parent>"),
			"Unexpected content: " + content);
	}

	@Test
	void remove() {
		PlanBuilder.ModifyPlan plan = op.fromDocUris(op.cts.documentQuery(XML_URI))
			.joinDocCols(null, op.col("uri"))
			.patch(op.col("doc"), op.patchBuilder("/", XML_NAMESPACES)
				.remove("/oe:parent/oe:child"))
			.write();

		rowManager.withUpdate(true).execute(plan);

		String content = client.newXMLDocumentManager().read(XML_URI, new StringHandle()).get();
		assertTrue(content.contains("<parent xmlns=\"org:example\"/>"),
			"The child element should have been removed, which verifies that the map of namespaces was " +
				"processed correctly; actual content: " + content);
	}
}
