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
    public void mjsTransformWithParamAndWrite() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        List<RowRecord> rows = resultRows(op
            .fromDocDescriptors(
                op.docDescriptor(newWriteOp("/acme/1.json", mapper.createObjectNode().put("doc", 1))),
                op.docDescriptor(newWriteOp("/acme/2.json", mapper.createObjectNode().put("doc", 2)))
            )
            .transformDoc(op.col("doc"),
                op.transformDef("/etc/optic/test/transformDoc-test.mjs")
                    .withParam("myParam", "my value"))
            .write()
        );

        // Verify returned rows first
        assertEquals(2, rows.size());
        ObjectNode content = rows.get(0).getContentAs("doc", ObjectNode.class);
        assertEquals("world", content.get("hello").asText());
        assertEquals("my value", content.get("yourParam").asText());
        assertEquals("The transform is expected to receive the incoming doc via the 'doc' param and then toss it into " +
            "the response under the key 'thedoc'", 1, content.get("theDoc").get("doc").asInt());
        content = rows.get(1).getContentAs("doc", ObjectNode.class);
        assertEquals("world", content.get("hello").asText());
        assertEquals("my value", content.get("yourParam").asText());
        assertEquals(2, content.get("theDoc").get("doc").asInt());

        // Verify persisted docs
        verifyJsonDoc("/acme/1.json", doc -> {
            assertEquals("world", doc.get("hello").asText());
            assertEquals("my value", doc.get("yourParam").asText());
            assertEquals(1, doc.get("theDoc").get("doc").asInt());
        });
        verifyJsonDoc("/acme/2.json", doc -> {
            assertEquals("world", doc.get("hello").asText());
            assertEquals("my value", doc.get("yourParam").asText());
            assertEquals(2, doc.get("theDoc").get("doc").asInt());
        });
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

    /**
     * See https://bugtrack.marklogic.com/57987
     */
    @Test
    public void multipleJsonDocsWithoutUpdateOperation() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ModifyPlan plan = op
            .fromDocUris("/optic/test/musician1.json", "/optic/test/musician2.json")
            .joinDoc(op.col("doc"), op.col("uri"))
            .transformDoc(op.col("doc"), op.transformDef("/etc/optic/test/transformDoc-test.mjs").withParam("myParam", "my value"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());
        System.out.println("ROWS: " + rows);

        ObjectNode transformedDoc = rows.get(0).getContentAs("doc", ObjectNode.class);
        assertEquals("world", transformedDoc.get("hello").asText());
        assertEquals("my value", transformedDoc.get("yourParam").asText());
        assertEquals("Armstrong", transformedDoc.get("theDoc").get("musician").get("lastName").asText());

        transformedDoc = rows.get(1).getContentAs("doc", ObjectNode.class);
        assertNotNull("Received erroneous null row: " + rows.get(1), transformedDoc);
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
