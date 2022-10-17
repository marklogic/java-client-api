package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowRecord;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RowManagerFromParamWriteTest extends AbstractRowManagerTest {

    @Test
    public void jsonDocumentWithAllMetadata() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op
                .fromParam("myDocs", "", op.docColTypes())
                .write();

        DocumentMetadataHandle metadata = new DocumentMetadataHandle()
                .withQuality(2)
//                // TODO Metadata and permissions not supported by server yet
//                .withMetadataValue("meta1", "value1")
//                .withMetadataValue("meta2", "value2")
                .withPermission("rest-reader", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE)
                .withCollections("common-coll", "other-coll-1");

        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/fromParam/doc1.json", metadata,
                new JacksonHandle(new ObjectMapper().createObjectNode().put("value", 1)));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = rowManager.resultRows(plan).stream().collect(Collectors.toList());
        assertEquals(1, rows.size());

        verifyMetadata("/fromParam/doc1.json", docMetadata -> {
            assertEquals(2, docMetadata.getQuality());
            assertTrue(docMetadata.getCollections().contains("common-coll"));
            assertTrue(docMetadata.getCollections().contains("other-coll-1"));
        });
    }

    @Test
    public void xmlDocumentWriteSet() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes()).write();

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/fromParam/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/fromParam/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = rowManager.resultRows(plan).stream().collect(Collectors.toList());
        assertEquals(2, rows.size());

        verifyXmlDoc("/fromParam/doc1.xml", "<doc>1</doc>");
        verifyXmlDoc("/fromParam/doc2.xml", "<doc>2</doc>");
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

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/fromParam/doc1.xml", metadata, new StringHandle("<doc>1</doc>"));
        writeSet.add("/fromParam/doc2.json", metadata, new StringHandle("{\"doc\":2}").withFormat(Format.JSON));
        PlanBuilder.Plan finalPlan = plan.bindParam("myDocs", writeSet);

        // Example of expected error message:
        // Local message: failed to apply resource at rows: Bad Request. Server Message: XDMP-ARGTYPE: xdmp.documentInsert("/fromParam/doc2.json",
        // Sequence(), {collections:Sequence(), permissions:[{roleId:"7089338530631756591", capability:"read"},
        // {roleId:"7089338530631756591", capability:"update"}], metadata:[], ...}) -- arg2 is not of type Node
        FailedRequestException ex = assertThrows(FailedRequestException.class, () -> rowManager.execute(finalPlan));
        assertTrue("Unexpected error: " + ex.getMessage() + "; the write should have failed because JSON and XML " +
                "documents can't yet be written together", ex.getMessage().contains("arg2 is not of type Node"));
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

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/fromParam/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/fromParam/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        PlanBuilder.Plan plan = rawPlan.bindParam("myDocs", writeSet);

        rowManager.execute(plan);

        verifyXmlDoc("/fromParam/doc1.xml", "<doc>1</doc>");
        verifyXmlDoc("/fromParam/doc2.xml", "<doc>2</doc>");
    }

    private void verifyXmlDoc(String uri, String expectedContent) {
        StringHandle doc = Common.client.newXMLDocumentManager().read(uri, new StringHandle());
        assertEquals(Format.XML, doc.getFormat());
        assertTrue("Unexpected content: " + doc.get(), doc.get().contains(expectedContent));
    }
}
