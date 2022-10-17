package com.marklogic.client.test.rows;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;

// Relevant bug - https://bugtrack.marklogic.com/57924 
public class RowManagerJoinDocColsTest extends AbstractRowManagerTest {

    // 4 musician documents are expected to be in this directory via mlDeploy
    private final static String MUSICIAN_DIRECTORY = "/optic/test/";

    @Test
    public void defaultColumns() {
        PlanBuilder.Plan plan = op
                .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
                .joinDocCols(op.docCols(), op.col("uri"))
                .orderBy(op.col("uri"));

        List<RowRecord> rows = rowManager.resultRows(plan).stream().collect(Collectors.toList());
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        assertEquals(0, firstRow.getInt("quality"));
        ObjectNode doc = firstRow.getContentAs("doc", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
    }

    @Test
    public void customColumns() {
        Map<String, String> columns = new HashMap<>();
        columns.put("doc", "doc2");
        columns.put("quality", "myQuality");

        PlanBuilder.Plan plan = op
                .fromDocUris(op.cts.directoryQuery(MUSICIAN_DIRECTORY))
                .joinDocCols(op.docCols(columns), op.col("uri"))
                .orderBy(op.col("uri"));

        List<RowRecord> rows = rowManager.resultRows(plan).stream().collect(Collectors.toList());
        assertEquals(4, rows.size());

        RowRecord firstRow = rows.get(0);
        assertEquals("/optic/test/musician1.json", firstRow.getString("uri"));
        ObjectNode doc = firstRow.getContentAs("doc2", ObjectNode.class);
        assertEquals("Louis", doc.get("musician").get("firstName").asText());
        assertEquals(0, firstRow.getInt("myQuality"));
    }
}
