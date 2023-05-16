package com.marklogic.client.test.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intent of this test is to document how each patch operation performs on both XML and JSON in small, hopefully
 * easy-to-understand tests. There seems to be good coverage already of the patch feature, but the tests are fairly
 * large and more difficult to understand.
 */
public class PatchDocumentTest {

	XMLDocumentManager xmlManager;
	DocumentPatchBuilder xmlPatchBuilder;
	JSONDocumentManager jsonManager;
	DocumentPatchBuilder jsonPatchBuilder;

	final String xmlUri = "/acme/1.xml";
	final String jsonUri = "/acme/2.json";

	@BeforeEach
	void insertDocuments() {
		DatabaseClient client = Common.newClient();
		xmlManager = client.newXMLDocumentManager();
		jsonManager = client.newJSONDocumentManager();
		xmlPatchBuilder = xmlManager.newPatchBuilder();
		jsonPatchBuilder = jsonManager.newPatchBuilder();

		final DocumentMetadataHandle metadata = new DocumentMetadataHandle()
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
			.withPermission("rest-writer", DocumentMetadataHandle.Capability.UPDATE);

		String xmlContent = "<root>\n" +
			"    <color>red</color>\n" +
			"    <color>blue</color>\n" +
			"</root>";

		String jsonContent = "{\n" +
			"    \"color\": [\n" +
			"        \"red\",\n" +
			"        \"blue\"\n" +
			"    ],\n" +
			"    \"numbers\": [\n" +
			"        {\"number\": 1},\n" +
			"        {\"number\": 2}\n" +
			"    ]\n" +
			"}";

		xmlManager.write(xmlUri, metadata, new StringHandle(xmlContent).withFormat(Format.XML));
		jsonManager.write(jsonUri, metadata, new StringHandle(jsonContent).withFormat(Format.XML));
	}

	@Test
	void xmlInsertAfterEachSize() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder
			.insertFragment("/root/color", DocumentPatchBuilder.Position.AFTER, "<hello>world</hello>")
		);

		final String message = "Unexpected XML: " + doc.getPrettyXml();
		assertTrue(doc.elementExists("/root/color[. = 'red']/following-sibling::hello[. = 'world']"), message);
		assertTrue(doc.elementExists("/root/color[. = 'blue']/following-sibling::hello[. = 'world']"), message);
		assertEquals(2, doc.getElements("//hello").size());
	}

	@Test
	void xmlInsertAfterSecondSize() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder
			.insertFragment("/root/color[2]", DocumentPatchBuilder.Position.AFTER, "<hello>world</hello>")
		);

		final String message = "Unexpected XML: " + doc.getPrettyXml();
		assertTrue(doc.elementExists("/root/color[. = 'blue']/following-sibling::hello[. = 'world']"), message);
		assertEquals(1, doc.getElements("//hello").size());
	}

	@Test
	void jsonInsertAfterEachSize() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder
			.insertFragment("/color", DocumentPatchBuilder.Position.AFTER, "{\"hello\":\"world\"}"));

		ArrayNode colors = (ArrayNode) doc.get("color");
		assertEquals(4, colors.size());
		assertEquals("red", colors.get(0).asText());
		assertEquals("world", colors.get(1).get("hello").asText());
		assertEquals("blue", colors.get(2).asText());
		assertEquals("world", colors.get(3).get("hello").asText());
	}

	@Test
	void jsonInsertAfterSecondSize() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder
			.insertFragment("/color[2]", DocumentPatchBuilder.Position.AFTER, "{\"hello\":\"world\"}"));

		ArrayNode colors = (ArrayNode) doc.get("color");
		assertEquals(3, colors.size());
		assertEquals("red", colors.get(0).asText());
		assertEquals("blue", colors.get(1).asText());
		assertEquals("world", colors.get(2).get("hello").asText());
	}

	@Test
	void xmlInsertColorLastChild() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder
			.insertFragment("/root/color[2]", DocumentPatchBuilder.Position.LAST_CHILD, "<hello>world</hello>")
		);

		doc.prettyPrint();
		assertEquals("red", doc.getElementValue("/root/color[1]"));
		assertEquals("blue", doc.getElementValue("/root/color[2]"),
			"The 'hello' fragment is added under /root/color[2], but it still retains 'blue' as a text node.");
		assertEquals("world", doc.getElementValue("/root/color[2]/hello"));
	}

	@Test
	void jsonInsertColorLastChild() {
		FailedRequestException ex = assertThrows(FailedRequestException.class,
			() -> patchJsonDoc(jsonPatchBuilder
				.insertFragment("/color", DocumentPatchBuilder.Position.LAST_CHILD, "{\"hello\":\"world\"}")));

		assertTrue(ex.getMessage().contains("cannot insert last child for text node: /color"),
			"A fragment cannot be inserted as a child of 'color' since the 'color' array only contains simple values " +
				"and not objects; unexpected error: " + ex.getMessage());
	}

	@Test
	void jsonInsertNumberLastChild() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder
			.insertFragment("/numbers", DocumentPatchBuilder.Position.LAST_CHILD, "{\"hello\":\"world\"}"));

		assertEquals(2, doc.get("numbers").size());

		assertEquals(1, doc.get("numbers").get(0).get("number").asInt());
		assertEquals("world", doc.get("numbers").get(0).get("hello").asText(),
			"A fragment can be inserted as a last child of each item in the 'numbers' array since each item is an " +
				"object.");

		assertEquals(2, doc.get("numbers").get(1).get("number").asInt());
		assertEquals("world", doc.get("numbers").get(1).get("hello").asText());
	}

	@Test
	void xmlReplaceEachColorFragment() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder.replaceFragment("/root/color", "<shape>circle</shape>"));

		assertEquals(0, doc.getElements("//color").size());
		assertEquals(2, doc.getElements("/root/shape").size());
		assertEquals("circle", doc.getElementValue("/root/shape[1]"));
		assertEquals("circle", doc.getElementValue("/root/shape[2]"));
	}

	@Test
	void xmlReplaceEachColorValue() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder.replaceValue("/root/color", "green"));

		assertEquals(2, doc.getElements("/root/color").size());
		assertEquals("green", doc.getElementValue("/root/color[1]"));
		assertEquals("green", doc.getElementValue("/root/color[2]"));
	}

	@Test
	void jsonReplaceEachColorFragment() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder.replaceFragment("/color", "{\"hello\":\"world\"}"));

		assertEquals(2, doc.get("color").size());
		assertEquals("world", doc.get("color").get(0).get("hello").asText());
		assertEquals("world", doc.get("color").get(1).get("hello").asText());
	}

	@Test
	void jsonReplaceEachColorValue() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder.replaceValue("/color", "replaced"));

		assertEquals(2, doc.get("color").size());
		assertEquals("replaced", doc.get("color").get(0).asText());
		assertEquals("replaced", doc.get("color").get(1).asText());
	}

	@Test
	void xmlReplaceInsertColor() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder
			.replaceInsertFragment("/root/color[3]", "/root/color[2]", DocumentPatchBuilder.Position.AFTER, "<hello>world</hello>"));

		assertEquals(2, doc.getElements("/root/color").size());
		assertEquals(1, doc.getElements("/root/hello").size());
		assertEquals("world", doc.getElementValue("/root/hello"));
	}

	@Test
	void jsonReplaceInsertColor() {
		// The names of the arguments are "selectPath" (replace the content here if it exists) and
		// "contextPath" (insert the content if the selectPath does not exist). "replacePath" and "insertPath" may
		// be more intuitive names.
		JsonNode doc = patchJsonDoc(jsonPatchBuilder
			.replaceInsertFragment("/color[3]", "/color[2]", DocumentPatchBuilder.Position.AFTER, "{\"hello\":\"world\"}"));

		assertEquals(3, doc.get("color").size());
		assertEquals("red", doc.get("color").get(0).asText());
		assertEquals("blue", doc.get("color").get(1).asText());
		assertEquals("world", doc.get("color").get(2).get("hello").asText(),
			"Since /color[3] doesn't exist, the second argument - the context - is used to determine where the " +
				"fragment should be inserted. Note that only fragments can be inserted, not values.");
	}

	@Test
	void xmlDeleteColors() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder.delete("/root/color"));

		assertEquals(0, doc.getElements("//color").size());
	}

	@Test
	void xmlDeleteFirstColor() {
		Fragment doc = patchXmlDoc(xmlPatchBuilder.delete("/root/color[1]"));

		assertEquals(1, doc.getElements("/root/color").size());
	}

	@Test
	void jsonDeleteColors() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder.delete("/color"));

		assertTrue(doc.has("color"), "The values in the 'color' array are deleted, but the key still exists.");
		assertEquals(0, doc.get("color").size());
	}

	@Test
	void jsonDeleteFirstSize() {
		JsonNode doc = patchJsonDoc(jsonPatchBuilder.delete("/color[1]"));

		assertEquals(1, doc.get("color").size());
		assertEquals("blue", doc.get("color").get(0).asText());
	}

	/**
	 * Using the Fragment class from ml-app-deployer as it's a convenient JDOM2-based wrapper around XML with some
	 * nice methods for verifying an XML document with XPath.
	 *
	 * @param patchBuilder
	 * @return
	 */
	private Fragment patchXmlDoc(DocumentPatchBuilder patchBuilder) {
		DocumentMetadataPatchBuilder.PatchHandle patch = patchBuilder.build();
		System.out.println("PATCH: " + patch.toString());
		xmlManager.patch(xmlUri, patch);
		return new Fragment(xmlManager.read(xmlUri, new StringHandle()).get());
	}

	private JsonNode patchJsonDoc(DocumentPatchBuilder patchBuilder) {
		DocumentMetadataPatchBuilder.PatchHandle patch = patchBuilder.build();
		System.out.println("PATCH: " + patch.toString());
		jsonManager.patch(jsonUri, patch);
		return jsonManager.read(jsonUri, new JacksonHandle()).get();
	}
}
