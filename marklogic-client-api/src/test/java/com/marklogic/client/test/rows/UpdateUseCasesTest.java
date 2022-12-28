package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.example.cookbook.OpticUpdateExample;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanSystemColumn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Intended to capture interesting use cases, particularly those called out in the functional spec.
 */
public class UpdateUseCasesTest extends AbstractOpticUpdateTest {

    /**
     * Use case: Given a set of doc descriptors, add a collection to each document with a URI matching one of the
     * descriptors. Any descriptor that has a URI for a document that no longer exists will be ignored.
     */
    @Test
    public void updateAndIgnoreNonExistentURIs() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        // First, write a document that can then be updated
        final String firstUri = "/acme/1.json";
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(
                newWriteOp(firstUri, newDefaultMetadata().withCollections("test1"), mapper.createObjectNode().put("hello", "world"))
            ))
            .write());
        verifyJsonDoc(firstUri, doc -> assertEquals("world", doc.get("hello").asText()));

        // Now submit two documents for updating, but ignore the one that doesn't match an existing URI
        final String missingUri = "/acme/doesnt-exist.json";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp(firstUri, mapper.createObjectNode().put("hello", "modified")));
        writeSet.add(newWriteOp(missingUri, mapper.createObjectNode().put("should be", "ignored")));

        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet), "input")
            .existsJoin(
                op.fromDocUris(op.cts.documentQuery(op.xs.stringSeq(firstUri, missingUri)), "lexicon"),
                op.on(op.viewCol("input", "uri"), op.viewCol("lexicon", "uri"))
            )
            .select(op.viewCol("input", "uri"), op.viewCol("input", "doc"))
            .write(op.docCols(op.xs.string("input"), op.xs.stringSeq("uri", "doc")));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size());

        verifyJsonDoc(firstUri, doc -> assertEquals("modified", doc.get("hello").asText()));
        verifyMetadata(firstUri, metadata -> {
            assertTrue(
                metadata.getCollections().contains("test1"));
        });
        assertNull(Common.client.newDocumentManager().exists(missingUri));
    }

    @Test
    public void notExistsJoin() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        // Insert doc1
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("hello", "world"))))
            .write());

        // Define an update for doc1 and an insert for doc2
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        DocumentWriteOperation updateDoc1 = newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("hello", "MODIFIED"));
        DocumentWriteOperation insertDoc2 = newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("hello", "world2"));
        writeSet.add(updateDoc1);
        writeSet.add(insertDoc2);

        // Define a plan that will exclude updateDoc1 via notExistsJoin against the URI lexicon
        ModifyPlan plan = op
            .fromDocDescriptors(op.docDescriptors(writeSet))
            .notExistsJoin(
                op.fromLexicons(Collections.singletonMap("existingUri", op.cts.uriReference())),
                op.on(op.col("uri"), op.col("existingUri"))
            )
            .write();

        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size(),
			"Only doc2 should be returned; doc1 should have been filtered out by the notExistsJoin " +
				"since it already exists");
        assertEquals("/acme/doc2.json", rows.get(0).getString("uri"));

        // doc1 should not have been modified since it was filtered out from the plan
        verifyJsonDoc("/acme/doc1.json", doc -> assertEquals("world", doc.get("hello").asText()));
        verifyJsonDoc("/acme/doc2.json", doc -> assertEquals("world2", doc.get("hello").asText()));
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
                op.aggregateSeq(op.col("doc"), op.col("permissions"))
            )
            .write();

        rowManager.execute(plan);

        verifyJsonDoc(duplicateUri, doc -> assertEquals(1, doc.get("value").asInt(),
			"The first writeOp with the duplicate URI should have been retained via groupBy and written"));
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
                    op.prop("body", op.col("doc"))
                )
            ));

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
            .offset(op.param("offset"))
            .limit(2)
            .joinDoc(op.col("doc"), idColumn)
            .bind(op.as("uri", op.fn.concat(
                op.xs.string("/acme/"),
                op.fn.string(op.col("lastName")),
                op.xs.string(".json"))
            ))
            .bind(op.as("permissions", op.param("perms")))
            .lockForUpdate()
            .transformDoc(
                op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.mjs")
                    .withParam("myParam", "my value"))
            .write();

        // Example of using Jackson to define permissions and then binding them to the plan
        ArrayNode permissions = mapper.createArrayNode();
        permissions.addObject().put("roleName", "rest-reader").put("capability", "read");
        permissions.addObject().put("roleName", "rest-extension-user").put("capability", "update");

        List<RowRecord> rows = resultRows(plan
            .bindParam("offset", 2)
            .bindParam("perms", new JacksonHandle(permissions))
        );

        assertEquals(2, rows.size());
        assertEquals("/acme/Coltrane.json", rows.get(0).getString("uri"));
        assertEquals("/acme/Davis.json", rows.get(1).getString("uri"));

        verifyJsonDoc("/acme/Coltrane.json", doc -> {
            assertEquals("world", doc.get("hello").asText(), "This is added by the transform");
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
            // Results in a single row with a single column of "uris"
            .groupBy(null, op.arrayAggregate(op.col("uris"), op.viewCol("input", "uri")))
            // Construct the descriptor columns to use when writing
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
        assertEquals(1, rows.size(),
			"Should contain 1 row for the audit log doc since that was the result of the last write");

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

    /**
     * Use case: we have a set of rows (perhaps from a CSV, or Kafka, etc), and we want to insert new documents for
     * them, but also join in some reference data from an existing view as part of the new documents. In this use case,
     * we're denormalizing "city" into the new documents based on the zip code in each document.
     * <p>
     * This is now reusing OpticUpdateExample so that that class can act as a cookbook example.
     */
    @Test
    public void writeWithReferenceDataFromViewJoinedIn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        List<RowRecord> rows = OpticUpdateExample.runPlanToWriteDocuments(rowManager).stream().collect(Collectors.toList());
        assertEquals(2, rows.size());

        verifyJsonDoc("/acme/person/Smith.json", doc -> {
            assertEquals("Smith", doc.get("lastName").asText());
            assertEquals("Jane", doc.get("firstName").asText());
            assertEquals(22201, doc.get("zipCode").asInt());
            assertEquals("Arlington", doc.get("cityOfResidence").asText());
        });

        verifyJsonDoc("/acme/person/Jones.json", doc -> {
            assertEquals("Jones", doc.get("lastName").asText());
            assertEquals("John", doc.get("firstName").asText());
            assertTrue(doc.get("zipCode") instanceof NullNode);
            assertTrue(doc.get("cityOfResidence") instanceof NullNode);
        });
    }
}
