package com.marklogic.client.test.rows;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanParamExpr;

/**
 * Tests various scenarios involving the {@code fromParam} accessor and the need to bind a content handle as a parameter
 * to the plan.
 */
public class FromParamTest extends AbstractOpticUpdateTest {

    @Test
    public void fromParamWithSimpleJsonArray() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        // Specify the columns that describe the rows that will be passed in
        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.colTypes(
                op.colType("lastName", "string"),
                op.colType("firstName", "string")
        ));

        // Build the rows to bind to the plan
        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("lastName", "Smith").put("firstName", "Jane");
        array.addObject().put("lastName", "Jones").put("firstName", "Jack");
        plan = plan.bindParam("myDocs", new JacksonHandle(array), null);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());
        assertEquals("Jane", rows.get(0).getString("firstName"));
        assertEquals("Smith", rows.get(0).getString("lastName"));
        assertEquals("Jack", rows.get(1).getString("firstName"));
        assertEquals("Jones", rows.get(1).getString("lastName"));
    }

    @Test
    public void fromParamWithXmlAttachments() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("bindingParam", "", op.colTypes(
                op.colType("rowId", "integer"),
                op.colType("doc")
        ));

        final PlanParamExpr param = op.param("bindingParam");

        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParam(param, new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("<doc>1</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("<doc>2</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));
    }

    @Test
    public void fromParamWithBinaryAttachments() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("bindingParam", "", op.colTypes(
                op.colType("rowId", "integer"),
                op.colType("doc")
        ));

        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.bin");
        array.addObject().put("rowId", 2).put("doc", "doc2.bin");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.bin", new BytesHandle("<doc>1</doc>".getBytes()).withFormat(Format.BINARY));
        attachments.put("doc2.bin", new BytesHandle("<doc>2</doc>".getBytes()).withFormat(Format.BINARY));
        plan = plan.bindParam("bindingParam", new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("<doc>1</doc>", row.getContentAs("doc", String.class));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("<doc>2</doc>", row.getContentAs("doc", String.class));
    }

    @Test
    public void fromParamWithTextAttachments() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("bindingParam", "", op.colTypes(
                op.colType("rowId", "integer"),
                op.colType("doc")
        ));

        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.txt");
        array.addObject().put("rowId", 2).put("doc", "doc2.txt");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.txt", new StringHandle("doc1-text").withFormat(Format.TEXT));
        attachments.put("doc2.txt", new StringHandle("doc2-text").withFormat(Format.TEXT));
        plan = plan.bindParam("bindingParam", new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("doc1-text", row.getContentAs("doc", String.class));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("doc2-text", row.getContentAs("doc", String.class));
    }

    /**
     * This tests ensures that the bindParam(param, AbstractWriteHandle) methods in RawPlanImpl work correctly.
     * Those methods are currently duplicated between RowPlanImpl and PlanSubImpl because the two classes do not have
     * a common parent class. So we need at least one test that covers the RawPlanImpl methods, which is this test.
     */
    @Test
    public void fromParamWithRawPlan() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        // The raw plan is the serialized representation of the plan in fromParamWithTextAttachments
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
                "                            \"bindingParam\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"ns\": \"xs\",\n" +
                "                        \"fn\": \"string\",\n" +
                "                        \"args\": [\n" +
                "                            \"\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    [\n" +
                "                        {\n" +
                "                            \"column\": \"rowId\",\n" +
                "                            \"type\": \"integer\",\n" +
                "                            \"nullable\": true\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"column\": \"doc\",\n" +
                "                            \"type\": \"none\",\n" +
                "                            \"nullable\": true\n" +
                "                        }\n" +
                "                    ]\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}").withFormat(Format.JSON));

        final PlanParamExpr param = op.param("bindingParam");

        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        PlanBuilder.Plan plan = rawPlan.bindParam(param, new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("<doc>1</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("<doc>2</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));
    }

    /**
     * Verifies that a user can have multiple columns that are associated with attachments.
     */
    @Test
    public void fromParamWithMultipleAttachmentColumns() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("bindingParam", "", op.colTypes(
                op.colType("rowId", "integer"),
                op.colType("doc"),
                op.colType("otherDoc")
        ));

        final PlanParamExpr param = op.param("bindingParam");

        ArrayNode array = mapper.createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml").put("otherDoc", "otherDoc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml").put("otherDoc", "otherDoc2.xml");
        Map<String, Map<String, AbstractWriteHandle>> columnAttachments = new HashMap<>();
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        columnAttachments.put("doc", attachments);
        attachments = new HashMap<>();
        attachments.put("otherDoc1.xml", new StringHandle("<otherDoc>1</otherDoc>").withFormat(Format.XML));
        attachments.put("otherDoc2.xml", new StringHandle("<otherDoc>2</otherDoc>").withFormat(Format.XML));
        columnAttachments.put("otherDoc", attachments);
        plan = plan.bindParam(param, new JacksonHandle(array), columnAttachments);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("<doc>1</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));
        assertEquals("<otherDoc>1</otherDoc>", getRowContentWithoutXmlDeclaration(row, "otherDoc"));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("<doc>2</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));
        assertEquals("<otherDoc>2</otherDoc>", getRowContentWithoutXmlDeclaration(row, "otherDoc"));
    }

    @Test
    public void xmlDocumentWriteSetSingleDocBug57894() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.Plan plan = op.fromParam("myDocs", "", op.docColTypes());

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        plan = plan.bindParam("myDocs", writeSet);

        List<RowRecord> rows = resultRows(plan);
        assertEquals(1, rows.size());
        RowRecord row = rows.get(0);
        assertEquals("/acme/doc1.xml", row.getString("uri"));
        assertEquals("<doc>1</doc>", getRowContentWithoutXmlDeclaration(row, "doc"));
    }
}
