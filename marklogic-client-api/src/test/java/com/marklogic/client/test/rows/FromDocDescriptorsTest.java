package com.marklogic.client.test.rows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In addition to testing fromDocDescriptors, this class contains tests to verify that default collections and
 * permissions are honored correctly.
 */
@ExtendWith(RequiresML11.class)
public class FromDocDescriptorsTest extends AbstractOpticUpdateTest {

    private final static String USER_WITH_DEFAULT_COLLECTIONS_AND_PERMISSIONS = "writer-default-collections-and-permissions";

    /**
     * Verify that default collections and permissions are honored when inserting docs, where one doc should use
     * the defaults and the other should not because the user explicitly specifies collections and permissions.
     */
    @Test
    public void insertDocsWithUserWithDefaultCollectionsAndPermissions() {
        final String firstUri = "/acme/doc1.json";
        final String secondUri = "/acme/doc2.json";

        // Create a couple documents to insert
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp(firstUri, null, mapper.createObjectNode().put("hello", "world")));
        writeSet.add(newWriteOp(secondUri, newDefaultMetadata().withCollections("custom1", "custom2"),
            mapper.createObjectNode().put("hello", "two")));

        Common.client = Common.newClientBuilder().withUsername(USER_WITH_DEFAULT_COLLECTIONS_AND_PERMISSIONS).build();
        Common.client.newRowManager().withUpdate(true).execute(op
            .fromDocDescriptors(op.docDescriptors(writeSet))
            .write());

        // Verify first doc inherits the default collections and permissions
		// 2024-05-16 This started failing in the 15th due to the existence of the "collections" and "permissions"
		// columns having null values. The server treats that as "Don't assign any collections/permissions" and thus
		// the user's default ones are not applied.
        verifyJsonDoc(firstUri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(firstUri, metadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
            assertPermissionExists(perms, "qconsole-user", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "qconsole-user", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(1, perms.size());
            DocumentMetadataHandle.DocumentCollections colls = metadata.getCollections();
            assertTrue(colls.contains("default1"));
            assertTrue(colls.contains("default2"));
            assertEquals(2, colls.size());
        });

        // Verify second doc uses the custom collections and permissions
        verifyJsonDoc(secondUri, doc -> assertEquals("two", doc.get("hello").asText()));
        verifyMetadata(secondUri, metadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(2, perms.size());
            DocumentMetadataHandle.DocumentCollections colls = metadata.getCollections();
            assertTrue(colls.contains("custom1"));
            assertTrue(colls.contains("custom2"));
            assertEquals(2, colls.size());
        });
    }

    @Test
    public void updateOnlyDocAsUserWithNoDefaults() {
        // Default test user does not have any default collections or permissions, so no need to switch to
        // a different user
        verifyOnlyDocCanBeUpdatedWithoutLosingAnyMetadata();
    }

    @Test
    public void updateOnlyDocWithUserWithDefaultCollectionsAndPermissions() {
        // Set up client as user with default collections and permissions
        Common.client = Common.newClientBuilder().withUsername(USER_WITH_DEFAULT_COLLECTIONS_AND_PERMISSIONS).build();
        rowManager = Common.client.newRowManager().withUpdate(true);
        op = rowManager.newPlanBuilder();

        verifyOnlyDocCanBeUpdatedWithoutLosingAnyMetadata();
    }

    /**
     * Verifies that the doc is not modified when only updating metadata, and that any non-modified metadata is not
     * updated either.
     */
    @Test
    public void updateOnlyCollection() {
        final String uri = writeDocWithAllMetadata();

        // Update the collections on the doc
        List<RowRecord> rows = resultRows(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, new DocumentMetadataHandle().withCollections("custom2"), null)))
            .write(op.docCols(null, op.xs.stringSeq("uri", "collections"))));

        assertEquals(1, rows.size());
        RowRecord row = rows.get(0);
        assertEquals(uri, row.getString("uri"));
        assertEquals(0, rows.get(0).getInt("quality"),
			"Quality still exists as a column because there's no way for fromDocDescriptors to know if the " +
				"user intentionally set quality to zero or not; however, the assertions below verify that 'quality' is " +
				"not passed to the 'write' col because the original quality still exists");
        assertTrue(row.containsKey("collections"));
        assertEquals(3, row.size(),
			"Row is expected to contain uri, collections, and quality: " + row);

        // Verify only collections were updated
        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = docMetadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(10, docMetadata.getQuality());
            assertEquals(1, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("custom2"));
            assertEquals(1, docMetadata.getMetadataValues().size());
            assertEquals("value1", docMetadata.getMetadataValues().get("key1"));
        });
    }

    @Test
    public void updateOnlyMetadata() {
        final String uri = writeDocWithAllMetadata();

        // Update only metadata
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, new DocumentMetadataHandle().withMetadataValue("key2", "value2"), null)))
            .write(op.docCols(null, op.xs.stringSeq("uri", "metadata"))));

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = docMetadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(10, docMetadata.getQuality());
            assertEquals(1, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("custom1"));
            assertEquals(1, docMetadata.getMetadataValues().size());
            assertEquals("value2", docMetadata.getMetadataValues().get("key2"));
        });
    }

    @Test
    public void updateOnlyPermissions() {
        final String uri = writeDocWithAllMetadata();

        // Update only permissions
        DocumentMetadataHandle metadata = new DocumentMetadataHandle()
            .withPermission("rest-extension-user", DocumentMetadataHandle.Capability.READ)
            .withPermission("rest-reader", DocumentMetadataHandle.Capability.UPDATE);
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, metadata, null)))
            .write(op.docCols(null, op.xs.stringSeq("uri", "permissions"))));

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = docMetadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.UPDATE);
            assertPermissionExists(perms, "rest-extension-user", DocumentMetadataHandle.Capability.READ);
            assertEquals(10, docMetadata.getQuality());
            assertEquals(1, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("custom1"));
            assertEquals(1, docMetadata.getMetadataValues().size());
            assertEquals("value1", docMetadata.getMetadataValues().get("key1"));
        });
    }

    @Test
    public void updateOnlyQuality() {
        final String uri = writeDocWithAllMetadata();

        // Update only quality
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(
                new DocumentWriteOperationImpl(uri, new DocumentMetadataHandle().withQuality(17), null)))
            .write(op.docCols(null, op.xs.stringSeq("uri", "quality"))));

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = docMetadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(17, docMetadata.getQuality());
            assertEquals(1, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("custom1"));
            assertEquals(1, docMetadata.getMetadataValues().size());
            assertEquals("value1", docMetadata.getMetadataValues().get("key1"));
        });
    }

    private String writeDocWithAllMetadata() {
        final String uri = "/acme/doc1.json";
        DocumentMetadataHandle metadata = newDefaultMetadata().withCollections("custom1");
        metadata.setQuality(10);
        metadata.getMetadataValues().add("key1", "value1");
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, metadata, mapper.createObjectNode().put("hello", "world"))))
            .write());
        return uri;
    }

    /**
     * Verifies that individual DocumentWriteOperations can be passed in without having to construct a
     * DocumentWriteSet first.
     */
    @Test
    public void writeIndividualDocDescriptors() {
        final String firstUri = "/acme/doc1.json";
        final String secondUri = "/acme/doc2.json";
        ObjectNode doc1 = mapper.createObjectNode().put("hello", "doc1");
        ObjectNode doc2 = mapper.createObjectNode().put("hello", "doc2");

        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(
            op.docDescriptor(newWriteOp(firstUri, doc1)),
            op.docDescriptor(newWriteOp(secondUri, doc2))
        );
        verifyExportedPlanReturnsSameRowCount(plan);

        rowManager.execute(plan.write());
        verifyJsonDoc(firstUri, doc -> assertEquals("doc1", doc.get("hello").asText()));
        verifyJsonDoc(secondUri, doc -> assertEquals("doc2", doc.get("hello").asText()));

        // Verify no metadata "snuck" in unexpectedly
        Stream.of(firstUri, secondUri).forEach(uri -> verifyMetadata(uri, metadata -> {
            assertEquals(0, metadata.getQuality());
            assertEquals(0, metadata.getCollections().size());
            assertEquals(0, metadata.getMetadataValues().size());
            // Verify the perms used by the test exist
            DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
        }));
    }

    @Test
    public void documentWriteSetWithQualifier() {
        final String uri = "/acme/doc1.json";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(newWriteOp(uri, mapper.createObjectNode().put("hello", "world")));

        final String qualifier = "myQualifier";
        PlanBuilder.AccessPlan plan = op.fromDocDescriptors(op.docDescriptors(writeSet), op.xs.string(qualifier));

        verifyExportedPlanReturnsSameRowCount(plan);

        List<RowRecord> rows = resultRows(plan.write(op.docCols(qualifier)));
        assertEquals("/acme/doc1.json", rows.get(0).getString("myQualifier.uri"));

        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
    }

    @Test
    public void writingNonJsonContentShouldFail() {
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", new DocumentMetadataHandle(),
            new StringHandle("<doc>1</doc>").withFormat(Format.XML));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> op.docDescriptors(writeSet),
			"Only JSON content is supported for the 5.6.0 release and 11.0 release of MarkLogic");
        assertEquals(
            "Only JSON content can be used with fromDocDescriptors; non-JSON content found for document with URI: /acme/doc1.xml",
            ex.getMessage(),
			"Unexpected exception: " + ex.getMessage());
    }

    @Test
    public void testExport() {
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

    private void verifyOnlyDocCanBeUpdatedWithoutLosingAnyMetadata() {
        // Create a doc with custom collections and permissions
        final String uri = "/acme/doc1.json";
        final ObjectNode initialContent = mapper.createObjectNode().put("hello", "world");
        DocumentMetadataHandle initialMetadata = new DocumentMetadataHandle();
        initialMetadata.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ);
        initialMetadata.getPermissions().add("test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
        initialMetadata.getCollections().addAll("custom1", "custom2");
        initialMetadata.setQuality(2);
        initialMetadata.getMetadataValues().put("key1", "value1");
        initialMetadata.getMetadataValues().put("key2", "value2");

        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, initialMetadata, initialContent)))
            .write());

        // Now update only the doc
        ModifyPlan updatePlan = op
            .fromDocDescriptors(op.docDescriptor(newWriteOp(uri, null, mapper.createObjectNode().put("hello", "modified!"))))
            .write(op.docCols(null, op.xs.stringSeq("uri", "doc")));

        List<RowRecord> rows = resultRows(updatePlan);
        assertEquals(1, rows.size());
        assertEquals(uri, rows.get(0).getString("uri"));
        assertEquals("modified!", rows.get(0).getContentAs("doc", ObjectNode.class).get("hello").asText());
        assertEquals(2, rows.get(0).size(),
			"Should only have 'uri' and 'doc' since that's all that the user specified");

        // Verify only the doc was updated
        verifyJsonDoc(uri, doc -> assertEquals("modified!", doc.get("hello").asText()));
        verifyMetadata(uri, docMetadata -> {
            DocumentMetadataHandle.DocumentPermissions perms = docMetadata.getPermissions();
            assertEquals(2, perms.size());
            assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
            assertPermissionExists(perms, "test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            assertEquals(2, docMetadata.getQuality());
            assertEquals(2, docMetadata.getCollections().size());
            assertTrue(docMetadata.getCollections().contains("custom1"));
            assertTrue(docMetadata.getCollections().contains("custom2"));
            assertEquals("value1", docMetadata.getMetadataValues().get("key1"));
            assertEquals("value2", docMetadata.getMetadataValues().get("key2"));
        });
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
