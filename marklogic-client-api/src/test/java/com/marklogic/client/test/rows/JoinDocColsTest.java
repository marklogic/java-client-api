package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import com.marklogic.client.type.PlanColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RequiresML11.class)
public class JoinDocColsTest extends AbstractOpticUpdateTest {

    // 4 musician documents are expected to be in this directory via mlDeploy
    private final static String MUSICIAN_DIRECTORY = "/optic/test/";

    @Test
    public void defaultColumnsNoQualifier() {
        PlanBuilder.Plan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
            .joinDocCols(null, op.col("uri"))
            .orderBy(op.col("uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        assertEquals(0, firstRow.getInt("quality"));
        assertTrue(firstRow.containsKey("metadata"));
        assertTrue(firstRow.containsKey("permissions"));
        assertTrue(firstRow.containsKey("collections"));
        ObjectNode doc = firstRow.getContentAs("doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
    }

    @Test
    public void defaultColumnsWithTargetQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
            .joinDocCols(op.docCols("target"), op.col("uri"))
            .orderBy(op.viewCol("target", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        assertEquals("/optic/test/musician1.json", firstRow.getString("target.uri"));
        assertEquals(0, firstRow.getInt("target.quality"));
        assertTrue(firstRow.containsKey("target.metadata"));
        assertTrue(firstRow.containsKey("target.permissions"));
        assertTrue(firstRow.containsKey("target.collections"));
        ObjectNode doc = firstRow.getContentAs("target.doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
    }

    @Test
    public void defaultColumnsWithSourceQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY), "source")
            .joinDocCols(op.docCols(), op.viewCol("source", "uri"))
            .orderBy(op.viewCol("source", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("source.uri"));
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        assertEquals(0, firstRow.getInt("quality"));
        assertTrue(firstRow.containsKey("metadata"));
        assertTrue(firstRow.containsKey("permissions"));
        assertTrue(firstRow.containsKey("collections"));
        ObjectNode doc = firstRow.getContentAs("doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(7, firstRow.size());
    }

    @Test
    public void defaultColumnsWithSourceAndTargetQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY), "source")
            .joinDocCols(op.docCols("target"), op.viewCol("source", "uri"))
            .orderBy(op.viewCol("source", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("source.uri"));
        assertEquals("/optic/test/musician1.json", firstRow.getString("target.uri"));
        assertEquals(0, firstRow.getInt("target.quality"));
        assertTrue(firstRow.containsKey("target.metadata"));
        assertTrue(firstRow.containsKey("target.permissions"));
        assertTrue(firstRow.containsKey("target.collections"));
        ObjectNode doc = firstRow.getContentAs("target.doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(7, firstRow.size());
    }

    @Test
    public void columnSubsetNoQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
            .joinDocCols(op.docCols(null, op.xs.stringSeq("uri", "doc")), op.col("uri"))
            .orderBy(op.col("uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        ObjectNode doc = firstRow.getContentAs("doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(2, firstRow.size());
    }

    @Test
    public void columnSubsetWithTargetQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
            .joinDocCols(op.docCols(op.xs.string("target"), op.xs.stringSeq("uri", "doc")), op.col("uri"))
            .orderBy(op.viewCol("target", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        assertEquals("/optic/test/musician1.json", firstRow.getString("target.uri"));
        ObjectNode doc = firstRow.getContentAs("target.doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(3, firstRow.size());
    }

    @Test
    public void columnSubsetWithSourceAndTargetQualifier() {
        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY), "source")
            .joinDocCols(op.docCols(op.xs.string("target"), op.xs.stringSeq("uri", "doc")), op.viewCol("source", "uri"))
            .orderBy(op.viewCol("target", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("source.uri"));
        assertEquals("/optic/test/musician1.json", firstRow.getString("target.uri"));
        ObjectNode doc = firstRow.getContentAs("target.doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(3, firstRow.size());
    }

    @Test
    public void customColumns() {
        Map<String, PlanColumn> columns = new HashMap<>();
        columns.put("doc", op.col("doc2"));
        columns.put("quality", op.col("myQuality"));

        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
            .joinDocCols(op.docCols(columns), op.col("uri"))
            .orderBy(op.col("uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        ObjectNode doc = firstRow.getContentAs("doc2", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(0, firstRow.getInt("myQuality"));
        assertEquals(3, firstRow.size());
    }
}
