package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatchTest extends AbstractOpticUpdateTest {

	@Test
	void replaceValueWithSimpleString() {
		final String uri = "/a1.json";

		DatabaseClient client = Common.newClient();

		// Write a test document that we'll then patch.
		client.newJSONDocumentManager().write(uri, new DocumentMetadataHandle()
				.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
			, new JacksonHandle(new ObjectMapper().createObjectNode().putObject("parent").put("hello", "world"))
		);

		// Patch the document using replaceValue.
		RowManager rowManager = client.newRowManager();
		PlanBuilder op = rowManager.newPlanBuilder();
		PlanBuilder.ModifyPlan plan = op.fromDocUris(op.cts.documentQuery(uri))
			.joinDocCols(null, op.col("uri"))
			.select(op.col("uri"), op.col("doc"))
			.patch(op.col("doc"),
				op.patchBuilder("/").replaceValue("hello", op.xs.string("new value"))
			)
			.write();

		System.out.println(plan.exportAs(JsonNode.class).toPrettyString());
		rowManager.withUpdate(true).execute(plan);

		// Read doc and verify it was patched correctly.
		JsonNode doc = client.newJSONDocumentManager().read(uri, new JacksonHandle()).get();
		assertEquals("new value", doc.get("hello").asText());
	}
}
