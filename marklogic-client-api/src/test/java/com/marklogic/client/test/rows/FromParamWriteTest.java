package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FromParamWriteTest extends AbstractOpticUpdateTest {

    @Test
    public void jsonDocumentWithAllMetadata() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

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
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes()).write();

        DocumentMetadataHandle metadata = newDefaultMetadata();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/acme/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        verifyXmlDoc("/acme/doc1.xml", "<doc>1</doc>");
        verifyXmlDoc("/acme/doc2.xml", "<doc>2</doc>");
    }

    /**
     * This test is used to document the fact that mixed content cannot be written yet.
     */
    @Test
    public void jsonAndXmlDocumentsInDocumentWriteSet() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

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
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

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

        verifyXmlDoc("/acme/doc1.xml", "<doc>1</doc>");
        verifyXmlDoc("/acme/doc2.xml", "<doc>2</doc>");
    }

    @Test
    @Ignore("See https://bugtrack.marklogic.com/57895")
    public void temporalTest() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        String contents = "<test>" +
            "<system-start>2014-08-10T00:00:00Z</system-start>" +
            "<system-end>2014-08-20T00:00:00Z</system-end>" +
            "<valid-start>2014-08-15T00:00:00Z</valid-start>" +
            "<valid-end>2014-08-17T00:00:01Z</valid-end>" +
            "</test>";

        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes()).write();

        final String temporalCollection = "temporal-collection";
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1-temporal.xml", metadata, new StringHandle(contents).withFormat(Format.XML), temporalCollection);
        writeSet.add("/acme/doc2-temporal.xml", metadata, new StringHandle(contents).withFormat(Format.XML), temporalCollection);
        plan = plan.bindParam("myDocs", writeSet);

        rowManager.resultRows(plan);

        verifyXmlDoc("/acme/doc1-temporal.xml", contents);
        verifyXmlDoc("/acme/doc2-temporal.xml", contents);

        XMLDocumentManager mgr = Common.client.newXMLDocumentManager();
        metadata = mgr.readMetadata("/acme/doc1-temporal.xml", new DocumentMetadataHandle());
        assertTrue("The document should be in the 'latest' collection if it was correctly inserted via " +
            "temporal.documentInsert", metadata.getCollections().contains("latest"));
        assertTrue(metadata.getCollections().contains(temporalCollection));
    }

    private void verifyXmlDoc(String uri, String expectedContent) {
        StringHandle doc = Common.client.newXMLDocumentManager().read(uri, new StringHandle());
        assertEquals(Format.XML, doc.getFormat());
        assertTrue("Unexpected content: " + doc.get(), doc.get().contains(expectedContent));
    }
}
