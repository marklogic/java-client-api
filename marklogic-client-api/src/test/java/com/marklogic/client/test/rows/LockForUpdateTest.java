package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequiresML11.class)
public class LockForUpdateTest extends AbstractOpticUpdateTest {

	@Test
	public void basicTest() {
		final String uri = "/acme/doc1.json";

		// Write a document
		rowManager.execute(op.fromDocDescriptors(
				op.docDescriptor(newWriteOp(uri, new JacksonHandle(mapper.createObjectNode().put("hello", "world")))))
			.write());
		verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));

		// Construct a plan that will lock the URI and update its collection
		PlanBuilder.ModifyPlan plan = op
			.fromDocDescriptors(
				op.docDescriptor(newWriteOp(uri, new DocumentMetadataHandle().withCollections("optic1"), null))
			)
			.lockForUpdate()
			.write(op.docCols(null, op.xs.stringSeq("uri", "collections")));

		// Run an eval that locks the URI and sleeps for 2 seconds, which will block the plan run below
		new Thread(() -> {
			Common.newServerAdminClient().newServerEval()
				.javascript(String.format("declareUpdate(); " +
					"xdmp.lockForUpdate('%s'); " +
					"xdmp.sleep(2000); " +
					"xdmp.documentSetCollections('%s', ['eval1']);", uri, uri))
				.evalAs(String.class);
		}).start();

		// Immediately run a plan that updates the collections as well; this should be blocked while the eval thread
		// above completes
		long start = System.currentTimeMillis();
		rowManager.execute(plan);
		long duration = System.currentTimeMillis() - start;
		System.out.println("DUR: " + duration);

		assertTrue(duration > 1500,
			"Because the eval call slept for 2 seconds, the duration of the plan execution should be at least " +
				"1500ms, which is much longer than normal; it may not be at least 2 seconds due to the small delay in " +
				"the Java layer of executing the plan; duration: " + duration);

		// Verify that the collections were set based on the plan, which should have run second
		verifyMetadata(uri, metadata -> {
			DocumentMetadataHandle.DocumentCollections colls = metadata.getCollections();
			assertEquals(1, colls.size());
			assertEquals("optic1", colls.iterator().next());
		});
	}

	@Test
	void wrongEndpoint() {
		rowManager.withUpdate(false);
		assertThrows(
			FailedRequestException.class,
			() -> rowManager.execute(op.fromDocUris("/optic/test/musician1.json").lockForUpdate()),
			"Hoping to update this assertion to verify that the server message tells the user to hit v1/rows/update " +
				"instead; right now, it's mentioning using declareUpdate() which isn't applicable to a REST API user."
		);
	}

	@Test
	public void uriColumnSpecified() {
		List<RowRecord> rows = resultRows(op
			.fromDocUris("/optic/test/musician1.json")
			.lockForUpdate(op.col("uri")));
		assertEquals(1, rows.size());
	}

	@Test
	public void fromParamWithCustomUriColumn() {
		ArrayNode paramValue = mapper.createArrayNode();
		paramValue.addObject().put("myUri", "/optic/test/musician1.json");

		List<RowRecord> rows = resultRows(op
			.fromParam("bindingParam", "", op.colTypes(op.colType("myUri", "string")))
			.lockForUpdate(op.col("myUri"))
			.bindParam("bindingParam", new JacksonHandle(paramValue), null));
		assertEquals(1, rows.size());
	}

	@Test
	public void fromParamWithQualifiedUriColumn() {
		ArrayNode paramValue = mapper.createArrayNode();
		paramValue.addObject().put("myUri", "/optic/test/musician1.json");

		List<RowRecord> rows = resultRows(op
			.fromParam("bindingParam", "myQualifier", op.colTypes(op.colType("myUri", "string")))
			.lockForUpdate(op.viewCol("myQualifier", "myUri"))
			.bindParam("bindingParam", new JacksonHandle(paramValue), null));
		assertEquals(1, rows.size());
	}
}
