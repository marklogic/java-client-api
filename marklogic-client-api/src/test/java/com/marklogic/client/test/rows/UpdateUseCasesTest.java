package com.marklogic.client.test.rows;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanSystemColumn;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Intended to capture interesting use cases, particularly those called out in the functional spec.
 */
public class UpdateUseCasesTest extends AbstractOpticUpdateTest {

    // TODO Try out existsJoin

    /**
     * Fails with ML error message of:
     * <p>
     * Status 500: SQL-MISMATCH: plan.inspectCols(plan.sparql("ExternalTable[ bindingName:docDescriptorsPlanBinding-3f3d2c0a-8794-401e-9176-0149120bbe17
     * columns: .input.doc,.input.permissions,.input.quality,.input.uri ]  ( { __docid <http://marklogic.com/lexicon> uri . })
     * AS \"persisted\" FILTER (input.uri eq persisted.uri)")) --  Found 6 binding(s)  and 4 static type(s)
     */
    @Test
    @Ignore("Cannot get notExistsJoin to work; only result() works")
    public void notExistsJoin() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        // Insert doc1
        DocumentWriteOperation insertDoc1 = newWriteOp("/acme/doc1.json",
            new JacksonHandle(mapper.createObjectNode().put("hello", "world")));
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(insertDoc1))
            .write());

        // Define an update for doc1 and an insert for doc2
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentWriteOperation updateDoc1 = newWriteOp("/acme/doc1.json",
            mapper.createObjectNode().put("hello", "MODIFIED"));
        DocumentWriteOperation insertDoc2 = newWriteOp("/acme/doc2.json",
            mapper.createObjectNode().put("hello", "world2"));
        writeSet.add(updateDoc1);
        writeSet.add(insertDoc2);

        // Define a plan that will exclude updateDoc1 via notExistsJoin against the URI lexicon
        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet), "input")
            .notExistsJoin(
                op.fromLexicons(Collections.singletonMap("uri", op.cts.uriReference()), "persisted"),
                op.on(op.viewCol("input", "uri"), op.viewCol("persisted", "uri")));

        // Note that result() works before write() is added, with only doc2 returned and the qualifier used correctly
        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size());
        assertEquals("/acme/doc2.json", rows.get(0).getString("input.uri"));

        // But once write() is added, it fails, regardless of whether it's write() or write(docCols)
        rowManager.execute(plan.write(op.docCols("input")));
    }

    @Test
    public void dedupDescriptorsAndWrite() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final String duplicateUri = "/acme/a1.json";

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp(duplicateUri, mapper.createObjectNode().put("value", 1)));
        writeSet.add(newWriteOp(duplicateUri, mapper.createObjectNode().put("value", "this should be dropped")));
        writeSet.add(newWriteOp("/acme/a2.json", mapper.createObjectNode().put("value", 2)));

        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet))
            .groupBy(
                // The column to group by
                op.col("uri"),
                // The columns to retain in the result
                op.aggregateSeq(op.col("doc"), op.col("permissions")));

        rowManager.execute(plan.write());

        verifyJsonDoc(duplicateUri, doc -> assertEquals(
            "The first writeOp with the duplicate URI should have been retained via groupBy and written",
            1, doc.get("value").asInt()));
        verifyJsonDoc("/acme/a2.json", doc -> assertEquals(2, doc.get("value").asInt()));
    }

    @Test
    public void wrapContentInEnvelope() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp("/acme/1.json", mapper.createObjectNode().put("value", 1)));
        writeSet.add(newWriteOp("/acme/2.json", mapper.createObjectNode().put("value", 2)));

        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet))
            .bind(op.as(
                op.col("doc"),
                op.jsonObject(
                    op.prop("header", op.jsonObject(op.prop("user", op.xdmp.getCurrentUser()))),
                    op.prop("body", op.col("doc")))));

        rowManager.execute(plan.write());

        verifyJsonDoc("/acme/1.json", doc -> {
            assertEquals("writer-no-default-permissions", doc.get("header").get("user").asText());
            assertEquals(1, doc.get("body").get("value").asInt());
        });

        verifyJsonDoc("/acme/2.json", doc -> {
            assertEquals("writer-no-default-permissions", doc.get("header").get("user").asText());
            assertEquals(2, doc.get("body").get("value").asInt());
        });
    }

    @Test
    public void writeNewDocsFromView() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanSystemColumn idColumn = op.fragmentIdCol("fragmentId");

        ModifyPlan plan = op.fromView("opticUnitTest", "musician", null, idColumn)
            .where(op.sqlCondition("dob > '1901-01-01'"))
            .orderBy(op.asc(op.col("lastName")))
            .offset(2)
            .limit(2)
            .joinDoc(op.col("doc"), idColumn)
            .bind(op.as("uri", op.fn.concat(
                op.xs.string("/acme/"),
                op.fn.string(op.col("lastName")),
                op.xs.string(".json"))
            ))
            .bind(op.as("permissions", op.jsonArray(
                op.permission("rest-reader", "read"),
                op.permission("rest-extension-user", "update")
            )))
            .lockForUpdate()
            .transformDoc(
                op.col("doc"),
                op.transformDefinition("/etc/optic/test/transformDoc-test.mjs")
                    .withParam("myParam", "my value"))
            .write();

        List<RowRecord> rows = resultRows(plan);

        assertEquals(2, rows.size());
        assertEquals("/acme/Coltrane.json", rows.get(0).getString("uri"));
        assertEquals("/acme/Davis.json", rows.get(1).getString("uri"));

        verifyJsonDoc("/acme/Coltrane.json", doc -> {
            assertEquals("This is added by the transform", "world", doc.get("hello").asText());
            assertEquals("John", doc.get("theDoc").get("musician").get("firstName").asText());
        });

        verifyMetadata("/acme/Coltrane.json", metadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
            assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("rest-reader").iterator().next());
            assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("rest-extension-user").iterator().next());
        });
    }

    @Test
    public void writeThreeDocsAndAuditLogDoc() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentMetadataHandle metadata = newDefaultMetadata().withCollections("audited-doc");
        writeSet.add(newWriteOp("/acme/1.json", metadata, mapper.createObjectNode().put("value", 1)));
        writeSet.add(newWriteOp("/acme/2.json", metadata, mapper.createObjectNode().put("value", 2)));
        writeSet.add(newWriteOp("/acme/3.json", metadata, mapper.createObjectNode().put("value", 3)));

        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet), "input")
            .write(op.docCols("input"))
            .groupBy(null, op.arrayAggregate(op.col("uris"), op.viewCol("input", "uri")))
            .select(
                op.as("uri", op.fn.concat(op.xs.string("/acme/audit-"), op.xdmp.random(), op.xs.string(".log"))),
                op.as("doc", op.jsonObject(
                    op.prop("uris", op.col("uris")),
                    op.prop("timestamp", op.fn.currentDateTime())
                )),
                op.as("collections", op.xs.string("test-auditLog")),
                op.as("permissions", op.jsonArray(
                    op.permission("rest-reader", "read"),
                    op.permission("test-rest-writer", "update")
                ))
            )
            .write();

        List<RowRecord> rows = resultRows(plan);
        assertEquals("Should contain 1 row for the audit log doc since that was the result of the last write", 1, rows.size());

        // Verify the 3 audited docs
        assertEquals(3, getCollectionSize("audited-doc"));
        verifyJsonDoc("/acme/1.json", doc -> assertEquals(1, doc.get("value").asInt()));
        verifyJsonDoc("/acme/2.json", doc -> assertEquals(2, doc.get("value").asInt()));
        verifyJsonDoc("/acme/3.json", doc -> assertEquals(3, doc.get("value").asInt()));

        // Verify the audit doc
        List<String> auditUris = getUrisInCollection("test-auditLog");
        assertEquals(1, auditUris.size());
        verifyJsonDoc(auditUris.get(0), doc -> {
            assertTrue(doc.has("timestamp"));
            List<String> uris = new ArrayList<>();
            doc.get("uris").forEach(node -> uris.add(node.asText()));
            assertEquals(3, uris.size());
            assertTrue(uris.contains("/acme/1.json"));
            assertTrue(uris.contains("/acme/2.json"));
            assertTrue(uris.contains("/acme/3.json"));
        });

        // Verify audit metadata
        verifyMetadata(auditUris.get(0), auditMetadata -> {
            assertEquals(1, auditMetadata.getCollections().size());
            assertEquals("test-auditLog", auditMetadata.getCollections().iterator().next());
            DocumentMetadataHandle.DocumentPermissions perms = auditMetadata.getPermissions();
            assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("rest-reader").iterator().next());
            assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("test-rest-writer").iterator().next());
        });
    }
}
