package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TransformDocTest extends AbstractOpticUpdateTest {

    @Test
    public void mjsTransformWithParam() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode rows = mapper.createArrayNode();
        rows.addObject().putObject("doc").put("some", "content");

        ModifyPlan plan = op
            .fromParam("myDocs", "", op.colTypes(op.colType("doc", "none")))
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.mjs")
                    .withParam("myParam", "my value"));

        List<RowRecord> results = resultRows(plan.bindParam("myDocs", new JacksonHandle(rows)));
        assertEquals(1, results.size());

        ObjectNode transformedDoc = results.get(0).getContentAs("doc", ObjectNode.class);
        assertEquals("world", transformedDoc.get("hello").asText());
        assertEquals("my value", transformedDoc.get("yourParam").asText());
        assertEquals(
            "The transform is expected to receive the incoming doc via the 'doc' param and then toss it into the " +
                "response under the key 'thedoc'",
            "content", transformedDoc.get("theDoc").get("some").asText());
    }

    @Test
    public void mjsTransformWithoutParam() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode rows = mapper.createArrayNode();
        rows.addObject().putObject("doc").put("some", "content");

        ModifyPlan plan = op
            .fromParam("myDocs", "", op.colTypes(op.colType("doc", "none")))
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.mjs").withKind("mjs"));

        List<RowRecord> results = resultRows(plan.bindParam("myDocs", new JacksonHandle(rows)));
        assertEquals(1, results.size());

        ObjectNode transformedDoc = results.get(0).getContentAs("doc", ObjectNode.class);
        assertEquals("world", transformedDoc.get("hello").asText());
        assertFalse("myParam was not specified, so yourParam should not exist", transformedDoc.has("yourParam"));
        assertEquals("content", transformedDoc.get("theDoc").get("some").asText());
    }

    @Test
    public void transformThrowsError() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ModifyPlan plan = op
            .fromDocUris("/optic/test/musician1.json")
            .joinDoc(op.col("doc"), op.col("uri"))
            .transformDoc(op.col("doc"), op.transformDef("/etc/optic/test/transformDoc-throwsError.mjs"));

        FailedRequestException ex = assertThrows(FailedRequestException.class, () -> rowManager.execute(plan));
        assertTrue("Unexpected message: " + ex.getMessage(), ex.getMessage().contains("throw Error(\"This is intentional\")"));
    }

    @Test
    @Ignore("See https://bugtrack.marklogic.com/57987")
    public void multipleJsonDocs() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ModifyPlan plan = op
            .fromDocUris("/optic/test/musician1.json", "/optic/test/musician2.json")
            .joinDoc(op.col("doc"), op.col("uri"))
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.mjs").withParam("myParam",
                    "my value"));

        List<RowRecord> results = resultRows(plan);
        assertEquals(2, results.size());

        ObjectNode transformedDoc = results.get(0).getContentAs("doc", ObjectNode.class);
        assertEquals("world", transformedDoc.get("hello").asText());
        assertEquals("my value", transformedDoc.get("yourParam").asText());
        assertEquals("Armstrong", transformedDoc.get("theDoc").get("musician").get("lastName").asText());

        transformedDoc = results.get(1).getContentAs("doc", ObjectNode.class);
        assertEquals("world", transformedDoc.get("hello").asText());
        assertEquals("my value", transformedDoc.get("yourParam").asText());
        assertEquals("Byron", transformedDoc.get("theDoc").get("musician").get("lastName").asText());
    }

    @Test
    @Ignore("See https://bugtrack.marklogic.com/57978#2; this works fine as a privileged user though")
    public void xsltTransformWithParam() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode rows = mapper.createArrayNode();
        rows.addObject().put("rowId", 1).put("doc", "doc1.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));

        ModifyPlan plan = op
            .fromParam("myDocs", "", op.colTypes(op.colType("rowId", "integer"), op.colType("doc", "none")))
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.xslt")
                    .withKind("xslt")
                    .withParam("myParam", "my value"));

        List<RowRecord> results = resultRows(
            plan.bindParam("myDocs", new JacksonHandle(rows), Collections.singletonMap("doc", attachments)));
        assertEquals(1, results.size());

        String xml = getRowContentWithoutXmlDeclaration(results.get(0), "doc");
        String message = "Unexpected XML doc: " + xml;
        // marklogic-junit would make this much easier/nicer once we change this project
        // to use JUnit 5
        assertTrue(message, xml.startsWith("<result>"));
        assertTrue(message, xml.contains("<doc>1</doc>"));
        assertTrue(message, xml.contains("<hello>world</hello>"));
        assertTrue(message, xml.contains("<yourParam>my value</yourParam"));
        assertTrue(message, xml.endsWith("</result>"));
    }

    @Test
    @Ignore("See https://bugtrack.marklogic.com/57978#2; this works fine as a privileged user though")
    public void xsltTransformWithoutParam() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode rows = mapper.createArrayNode();
        rows.addObject().put("doc", "doc1.xml");
        Map<String, AbstractWriteHandle> attachments = new HashMap<>();
        attachments.put("doc1.xml", new StringHandle("<doc>1</doc>").withFormat(Format.XML));

        ModifyPlan plan = op
            .fromParam("myDocs", "", op.colTypes(op.colType("doc", "none")))
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.xslt").withKind("xslt"));

        List<RowRecord> results = resultRows(
            plan.bindParam("myDocs", new JacksonHandle(rows), Collections.singletonMap("doc", attachments)));
        assertEquals(1, results.size());

        String xml = getRowContentWithoutXmlDeclaration(results.get(0), "doc");
        String message = "Unexpected XML doc: " + xml;
        assertTrue(message, xml.startsWith("<result>"));
        assertTrue(message, xml.contains("<hello>world</hello>"));
        assertTrue(message, xml.contains("<yourParam/>"));
        assertTrue(message, xml.endsWith("</result>"));
    }
}
