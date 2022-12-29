package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequiresML11.class)
public class FromParamWriteTest extends AbstractOpticUpdateTest {

    @Test
    public void jsonDocumentWithAllMetadata() {
        PlanBuilder.Plan plan = op
            .fromParam("myDocs", "", op.docColTypes())
            .write();

        DocumentMetadataHandle metadata = newDefaultMetadata()
            .withQuality(2)
            .withMetadataValue("meta1", "value1")
            .withMetadataValue("meta2", "value2")
            .withCollections("common-coll", "other-coll-1");

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.json", metadata,
            new JacksonHandle(new ObjectMapper().createObjectNode().put("value", 1)));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size());

        verifyMetadata("/acme/doc1.json", docMetadata -> {
            assertEquals(2, docMetadata.getQuality());
            assertEquals(DocumentMetadataHandle.Capability.READ, docMetadata.getPermissions().get("rest-reader").iterator().next());
            assertEquals(DocumentMetadataHandle.Capability.UPDATE, docMetadata.getPermissions().get("test-rest-writer").iterator().next());
            assertTrue(docMetadata.getCollections().contains("common-coll"));
            assertTrue(docMetadata.getCollections().contains("other-coll-1"));
            assertEquals("value1", docMetadata.getMetadataValues().get("meta1"));
            assertEquals("value2", docMetadata.getMetadataValues().get("meta2"));
        });
    }

    @Test
    public void xmlDocumentWriteSet() {
        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes()).write();

        DocumentMetadataHandle metadata = newDefaultMetadata();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/acme/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        verifyXmlDocContent("/acme/doc1.xml", "<doc>1</doc>");
        verifyXmlDocContent("/acme/doc2.xml", "<doc>2</doc>");
    }

    /**
     * This test is used to document the fact that mixed content cannot be written yet.
     */
    @Test
    public void jsonAndXmlDocumentsInDocumentWriteSet() {
        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes()).write();

        DocumentMetadataHandle metadata = newDefaultMetadata();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>"));
        writeSet.add("/acme/doc2.json", metadata, new StringHandle("{\"doc\":2}").withFormat(Format.JSON));
        PlanBuilder.Plan finalPlan = plan.bindParam("myDocs", writeSet);

        // The reason why this fails has changed a few times, so this test no longer asserts on the message, but
        // rather just that an exception occurs
        Exception ex = assertThrows(Exception.class, () -> rowManager.execute(finalPlan));
        System.out.println(ex.getMessage());
    }

    /**
     * Verifies that the path through RawPlanImpl supports binding content params.
     */
    @Test
    public void rawPlanAndDocumentWriteSet() {
        RawPlanDefinition rawPlan = rowManager.newRawPlanDefinition(new StringHandle("{\n" +
            "    \"$optic\": {\n" +
            "        \"ns\": \"op\",\n" +
            "        \"fn\": \"operators\",\n" +
            "        \"args\": [\n" +
            "            {\n" +
            "                \"ns\": \"op\",\n" +
            "                \"fn\": \"from-param\",\n" +
            "                \"args\": [\n" +
            "                    {\n" +
            "                        \"ns\": \"xs\",\n" +
            "                        \"fn\": \"string\",\n" +
            "                        \"args\": [\n" +
            "                            \"myDocs\"\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": \"xs\",\n" +
            "                        \"fn\": \"string\",\n" +
            "                        \"args\": [\n" +
            "                            \"\"\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": \"op\",\n" +
            "                        \"fn\": \"doc-col-types\",\n" +
            "                        \"args\": []\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            {\n" +
            "                \"ns\": \"op\",\n" +
            "                \"fn\": \"write\",\n" +
            "                \"args\": []\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}"));

        DocumentMetadataHandle metadata = newDefaultMetadata();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/acme/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        PlanBuilder.Plan plan = rawPlan.bindParam("myDocs", writeSet);

        rowManager.execute(plan);

        verifyXmlDocContent("/acme/doc1.xml", "<doc>1</doc>");
        verifyXmlDocContent("/acme/doc2.xml", "<doc>2</doc>");
    }

    @Test
    public void temporalWrite() {
        final String uri = "/acme/doc1-temporal.json";
        final String temporalCollection = "temporal-collection";
        ObjectNode temporalContent = newTemporalContent();

        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(
                new DocumentWriteOperationImpl(uri, newDefaultMetadata(), new JacksonHandle(temporalContent), temporalCollection)
            ))
            .write(op.docCols(null, op.xs.stringSeq("uri", "doc", "temporalCollection", "permissions"))));

        verifyJsonDoc(uri, doc -> {
            String systemEnd = doc.get("system-end").asText();
            assertTrue(systemEnd.startsWith("9999"), "system-end should have been populated by ML: " + doc);
            assertTrue(
                StringUtils.isNotEmpty(doc.get("system-start").asText()),
				"system-start should have been populated by ML: " + doc);
        });

        verifyMetadata(uri, metadata -> {
            assertTrue(metadata.getCollections().contains("latest"),
				"The document should be in the 'latest' collection if it was correctly inserted via temporal.documentInsert");
            assertTrue(metadata.getCollections().contains(temporalCollection));
        });

        // Update the doc to ensure that a new version is created
        temporalContent.put("hello", "world");
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(
                new DocumentWriteOperationImpl(uri, null, new JacksonHandle(temporalContent), temporalCollection)
            ))
            .write(op.docCols(null, op.xs.stringSeq("uri", "doc", "temporalCollection"))));

        // Verify doc and that we now have 2 versions
        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));
        List<RowRecord> rows = resultRows(op.fromDocUris(op.cts.collectionQuery(temporalCollection)));
        assertEquals(2, rows.size(), "Should have 2 versions in the temporal collection");
    }

    @Test
    public void invalidTemporalWrite() {
        final String temporalCollection = "temporal-collection";
        assertThrows(
            // The error message is not of interest, as it's controlled by temporal.documentInsert and not by Optic.
            // Just need to verify that this fails as expected.
            FailedRequestException.class,
            () -> rowManager.execute(op
                .fromDocDescriptors(op.docDescriptor(
                    new DocumentWriteOperationImpl("/acme/this-should-fail.json", newDefaultMetadata(),
                        new JacksonHandle(mapper.createObjectNode().put("this is", "missing temporal fields")), temporalCollection
                    )
                ))
                .write())
        );
    }

    private void verifyXmlDocContent(String uri, String expectedContent) {
        verifyXmlDoc(uri, content -> {
            assertTrue(content.contains(expectedContent), "Unexpected content: " + content);
        });
    }
}
