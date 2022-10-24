package com.marklogic.client.test.rows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FromDocDescriptorsTest extends AbstractOpticUpdateTest {

    @Test
    public void writeWithAllMetadata() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentMetadataHandle metadata = newDefaultMetadata();
        metadata.getCollections().addAll("docDescriptors1", "docDescriptors2");
        metadata.setQuality(2);

        final String uri = "/acme/doc1.json";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        ObjectNode content = mapper.createObjectNode().put("hello", "world");
        writeSet.add(uri, metadata, new JacksonHandle(content));

        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(op.docDescriptors(writeSet));
        verifyExportedPlanReturnsSameRowCount(plan);

        rowManager.execute(plan.write());

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            assertEquals(DocumentMetadataHandle.Capability.READ, docMetadata.getPermissions().get("rest-reader").iterator().next());
            assertEquals(DocumentMetadataHandle.Capability.UPDATE, docMetadata.getPermissions().get("test-rest-writer").iterator().next());
            assertEquals(2, docMetadata.getQuality());
            assertEquals(2, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("docDescriptors1"));
            assertTrue(docMetadata.getCollections().contains("docDescriptors2"));
        });

        // Now update only the collections
        metadata = new DocumentMetadataHandle().withCollections("updatedColl1", "updatedColl2");
        ModifyPlan updatePlan = op
                .fromDocDescriptors(op.docDescriptor(new DocumentWriteOperationImpl(uri, metadata, null)))
                .write(op.docCols(new String[] { "uri", "collections" }));
        rowManager.execute(updatePlan);

        // Verify only collections were updated
        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            assertEquals(DocumentMetadataHandle.Capability.READ, docMetadata.getPermissions().get("rest-reader").iterator().next());
            assertEquals(DocumentMetadataHandle.Capability.UPDATE, docMetadata.getPermissions().get("test-rest-writer").iterator().next());
            assertEquals(2, docMetadata.getQuality());
            assertEquals(2, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("updatedColl1"));
            assertTrue(docMetadata.getCollections().contains("updatedColl2"));
        });
    }

    @Test
    public void jsonDocumentWithUserWithDefaultPermissions() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final String uri = "/acme/doc1.json";

        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(op.docDescriptor(
                new DocumentWriteOperationImpl(uri, new DocumentMetadataHandle(),
                        new JacksonHandle(mapper.createObjectNode().put("hello", "world")))
        ));

        // Use a new RowManager with a user that has default permissions
        DatabaseClient originalClient = Common.client;
        try {
            Common.client = Common.newClientAsUser("rest-writer", null);
            rowManager = Common.client.newRowManager();
            rowManager.execute(plan.write());

            verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
            verifyMetadata(uri, docMetadata -> {
                // Since this user has the "rest-writer" role, we expect there to be default doc permissions
                assertEquals(DocumentMetadataHandle.Capability.READ, docMetadata.getPermissions().get("harmonized-reader").iterator().next());
                assertEquals(DocumentMetadataHandle.Capability.UPDATE, docMetadata.getPermissions().get("harmonized-updater").iterator().next());
                assertEquals(DocumentMetadataHandle.Capability.READ, docMetadata.getPermissions().get("rest-reader").iterator().next());
                assertEquals(DocumentMetadataHandle.Capability.UPDATE, docMetadata.getPermissions().get("rest-writer").iterator().next());
            });
        } finally {
            Common.client = originalClient;
        }
    }


    @Test
    public void writeIndividualDocDescriptors() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentMetadataHandle metadata = newDefaultMetadata();
        ObjectNode doc1 = mapper.createObjectNode().put("hello", "doc1");
        ObjectNode doc2 = mapper.createObjectNode().put("hello", "doc2");

        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(
                op.docDescriptor(
                        new DocumentWriteOperationImpl("/acme/doc1.json", metadata, new JacksonHandle(doc1))),
                op.docDescriptor(
                        new DocumentWriteOperationImpl("/acme/doc2.json", metadata, new JacksonHandle(doc2))));
        verifyExportedPlanReturnsSameRowCount(plan);

        rowManager.execute(plan.write());
        verifyJsonDoc("/acme/doc1.json", doc -> assertEquals("doc1", doc.get("hello").asText()));
        verifyJsonDoc("/acme/doc2.json", doc -> assertEquals("doc2", doc.get("hello").asText()));
    }

    @Test
    public void documentWriteSetWithQualifier() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final String uri = "/acme/doc1.json";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        ObjectNode content = mapper.createObjectNode().put("hello", "world");
        writeSet.add(uri, newDefaultMetadata(), new JacksonHandle(content));

        final String qualifier = "myQualifier";
        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(op.docDescriptors(writeSet), qualifier);

        verifyExportedPlanReturnsSameRowCount(plan);

        List<RowRecord> rows = resultRows(plan.write(op.docCols(qualifier)));
        assertEquals("/acme/doc1.json", rows.get(0).getString("myQualifier.uri"));

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
    }

    @Test
    public void writingNonJsonContentShouldFail() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", new DocumentMetadataHandle(),
                new StringHandle("<doc>1</doc>").withFormat(Format.XML));

        IllegalArgumentException ex = assertThrows(
                "Only JSON content is supported for the 5.6.0 release and 11.0 release of MarkLogic",
                IllegalArgumentException.class,
                () -> op.docDescriptors(writeSet));
        assertEquals("Unexpected exception: " + ex.getMessage(),
                "Only JSON content can be used with fromDocDescriptors; non-JSON content found for document with URI: /acme/doc1.xml",
                ex.getMessage());
    }

    @Test
    public void testExport() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp("/acme/doc1.json", mapper.createObjectNode().put("hello", "doc1")));
        writeSet.add(newWriteOp("/acme/doc2.json", mapper.createObjectNode().put("hello", "doc2")));

        PlanBuilder.AccessPlan accessPlan = op.fromDocDescriptors(op.docDescriptors(writeSet));
        ObjectNode plan = exportPlan(accessPlan);
        JsonNode docDescriptorArgs = plan.get("$optic").get("args").get(0).get("args").get(0);
        assertEquals("doc1", docDescriptorArgs.get(0).get("doc").get("hello").asText());
        assertEquals("doc2", docDescriptorArgs.get(1).get("doc").get("hello").asText());

        verifyExportedPlanReturnsSameRowCount(accessPlan);
    }

    private ObjectNode exportPlan(PlanBuilder.ExportablePlan plan) {
        String json = plan.exportAs(JsonNode.class).toPrettyString();
        try {
            return (ObjectNode) mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
