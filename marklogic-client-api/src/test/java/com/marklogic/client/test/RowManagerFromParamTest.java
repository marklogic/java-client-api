package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.type.PlanParamExpr;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Tests various scenarios involving the {@code fromParam} accessor and the need to bind a content handle as a parameter
 * to the plan.
 */
public class RowManagerFromParamTest {

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @Test
    public void fromParamWithSimpleJsonArray() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        // Specify the columns that describe the rows that will be passed in
        PlanBuilder.Plan plan = planBuilder.fromParam("myDocs", "", planBuilder.colTypes(
                planBuilder.colType("lastName", "string"),
                planBuilder.colType("firstName", "string", "", "", null)
        ));

        // Build the rows to bind to the plan
        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("lastName", "Smith").put("firstName", "Jane");
        array.addObject().put("lastName", "Jones").put("firstName", "Jack");
        plan = plan.bindParam("myDocs", new JacksonHandle(array), null);

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        PlanBuilder.Plan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        final PlanParamExpr param = planBuilder.param("bindingParam");

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParam(param, new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        PlanBuilder.Plan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.bin");
        array.addObject().put("rowId", 2).put("doc", "doc2.bin");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.bin", new BytesHandle("<doc>1</doc>".getBytes()).withFormat(Format.BINARY));
        attachments.put("doc2.bin", new BytesHandle("<doc>2</doc>".getBytes()).withFormat(Format.BINARY));
        plan = plan.bindParam("bindingParam", new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        PlanBuilder.Plan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.txt");
        array.addObject().put("rowId", 2).put("doc", "doc2.txt");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.txt", new StringHandle("doc1-text").withFormat(Format.TEXT));
        attachments.put("doc2.txt", new StringHandle("doc2-text").withFormat(Format.TEXT));
        plan = plan.bindParam("bindingParam", new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        // The raw plan is the serialized representation of the plan in fromParamWithTextAttachments
        RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(new StringHandle("{\n" +
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
                "                            \"schema\": \"\",\n" +
                "                            \"view\": \"\",\n" +
                "                            \"column\": \"rowId\",\n" +
                "                            \"type\": \"integer\",\n" +
                "                            \"nullable\": true\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"schema\": \"\",\n" +
                "                            \"view\": \"\",\n" +
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

        final PlanParamExpr param = planBuilder.param("bindingParam");

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        PlanBuilder.Plan plan = rawPlan.bindParam(param, new JacksonHandle(array), Collections.singletonMap("doc", attachments));

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        PlanBuilder.Plan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none"),
                planBuilder.colType("otherDoc", "none")
        ));

        final PlanParamExpr param = planBuilder.param("bindingParam");

        ArrayNode array = new ObjectMapper().createArrayNode();
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

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
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

    private String getRowContentWithoutXmlDeclaration(RowRecord row, String columnName) {
        String content = row.getContentAs(columnName, String.class);
        return content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "");
    }
}
