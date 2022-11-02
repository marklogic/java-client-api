package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LockForUpdateTest extends AbstractOpticUpdateTest {

    @Test
    public void basicTest() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

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

        assertTrue("Because the eval call slept for 2 seconds, the duration of the plan execution should be at least " +
            "1500ms, which is much longer than normal; it may not be at least 2 seconds due to the small delay in " +
            "the Java layer of executing the plan; duration: " + duration, duration > 1500);

        // Verify that the collections were set based on the plan, which should have run second
        verifyMetadata(uri, metadata -> {
            DocumentMetadataHandle.DocumentCollections colls = metadata.getCollections();
            assertEquals(1, colls.size());
            assertEquals("optic1", colls.iterator().next());
        });
    }

    @Test
    public void uriColumnSpecified() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        List<RowRecord> rows = resultRows(op
            .fromDocUris("/optic/test/musician1.json")
            .lockForUpdate(op.col("uri")));
        assertEquals(1, rows.size());
    }

    @Test
    public void fromParamWithCustomUriColumn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

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
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("myUri", "/optic/test/musician1.json");

        List<RowRecord> rows = resultRows(op
            .fromParam("bindingParam", "myQualifier", op.colTypes(op.colType("myUri", "string")))
            .lockForUpdate(op.viewCol("myQualifier", "myUri"))
            .bindParam("bindingParam", new JacksonHandle(paramValue), null));
        assertEquals(1, rows.size());
    }
}
