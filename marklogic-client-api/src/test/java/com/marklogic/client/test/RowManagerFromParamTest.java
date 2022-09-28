package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import org.junit.BeforeClass;
import org.junit.Test;

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
/*
    @Test
    public void fromParamWithSimpleJsonArray() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        RowManager rowMgr = Common.client.newRowManager();
        PlanBuilder planBuilder = rowMgr.newPlanBuilder();

        PlanBuilder.AccessPlan plan = planBuilder.fromParam("myDocs", "", planBuilder.colTypes(
                planBuilder.colType("lastName", "string"),
                planBuilder.colType("firstName", "string", "", "", null)
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("lastName", "Smith").put("firstName", "Jane");
        array.addObject().put("lastName", "Jones").put("firstName", "Jack");
        plan.bindParam("myDocs", new JacksonHandle(array));

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

        PlanBuilder.AccessPlan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.xml");
        array.addObject().put("rowId", 2).put("doc", "doc2.xml");
        plan = plan.bindParam("bindingParam", new JacksonHandle(array));

        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        attachments.put("doc2.xml", new StringHandle("<doc>2</doc>").withFormat(Format.XML));
        plan = plan.bindParamAttachments("bindingParam", "doc", attachments);

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

        PlanBuilder.AccessPlan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.bin");
        array.addObject().put("rowId", 2).put("doc", "doc2.bin");
        plan = plan.bindParam("bindingParam", new JacksonHandle(array));

        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.bin", new BytesHandle("<doc>1</doc>".getBytes()).withFormat(Format.BINARY));
        attachments.put("doc2.bin", new BytesHandle("<doc>2</doc>".getBytes()).withFormat(Format.BINARY));
        plan = plan.bindParamAttachments("bindingParam", "doc", attachments);

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

        PlanBuilder.AccessPlan plan = planBuilder.fromParam("bindingParam", "", planBuilder.colTypes(
                planBuilder.colType("rowId", "integer"),
                planBuilder.colType("doc", "none")
        ));

        ArrayNode array = new ObjectMapper().createArrayNode();
        array.addObject().put("rowId", 1).put("doc", "doc1.txt");
        array.addObject().put("rowId", 2).put("doc", "doc2.txt");
        plan = plan.bindParam("bindingParam", new JacksonHandle(array));

        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.txt", new StringHandle("doc1-text").withFormat(Format.TEXT));
        attachments.put("doc2.txt", new StringHandle("doc2-text").withFormat(Format.TEXT));
        plan = plan.bindParamAttachments("bindingParam", "doc", attachments);

        List<RowRecord> rows = rowMgr.resultRows(plan).stream().collect(Collectors.toList());
        assertEquals(2, rows.size());

        RowRecord row = rows.get(0);
        assertEquals(1, row.getInt("rowId"));
        assertEquals("doc1-text", row.getContentAs("doc", String.class));

        row = rows.get(1);
        assertEquals(2, row.getInt("rowId"));
        assertEquals("doc2-text", row.getContentAs("doc", String.class));
    }*/

    private String getRowContentWithoutXmlDeclaration(RowRecord row, String columnName) {
        String content = row.getContentAs(columnName, String.class);
        return content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "");
    }
}
