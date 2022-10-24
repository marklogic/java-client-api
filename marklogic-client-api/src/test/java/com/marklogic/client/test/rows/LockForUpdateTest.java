package com.marklogic.client.test.rows;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;

public class LockForUpdateTest extends AbstractOpticUpdateTest {

    @Test
    public void basicTest() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final String uri = "/acme/doc1.json";

        // Write a document
        rowManager.execute(op.fromDocDescriptors(
                op.docDescriptor(newWriteOp(uri, new JacksonHandle(mapper.createObjectNode().put("hello", "world")))))
                .write());
        verifyJsonDoc(uri, doc -> assertEquals("world", doc.get("hello").asText()));

        // Verify it can be locked; we don't have any effective way to verify that it
        // was locked as we'd need to cause
        // the plan invocation to "sleep" for awhile, and then use a second thread to
        // inspect the status of the
        // documents. So we assume that the return of a row for the URI is a measure of
        // success.
        List<RowRecord> rows = resultRows(op.fromDocUris(uri).lockForUpdate());
        assertEquals(1, rows.size());

        // Verify it can be updated - i.e. the lock was released in the previous call
        rowManager.execute(op.fromDocDescriptors(
                op.docDescriptor(newWriteOp(uri, new JacksonHandle(mapper.createObjectNode().put("hello", "modified")))))
                .write());
        verifyJsonDoc(uri, doc -> assertEquals("modified", doc.get("hello").asText()));
    }

    @Test
    public void uriColumnSpecified() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        List<RowRecord> rows = resultRows(op
                .fromDocUris("/optic/test/musician1.json")
                .lockForUpdate(op.col("uri")));
        assertEquals(1, rows.size());
    }

    @Test
    public void fromParamWithCustomUriColumn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("myUri", "/optic/test/musician1.json");

        List<RowRecord> rows = resultRows(op
                .fromParam("bindingParam", "", op.colTypes(op.colType("myUri", "string")))
                .lockForUpdate(op.col("myUri"))
                .bindParam("bindingParam", new JacksonHandle(paramValue), null));
        assertEquals(1, rows.size());
    }

    @Test
    public void fromParamWithQualifiedUriColumn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("myUri", "/optic/test/musician1.json");

        List<RowRecord> rows = resultRows(op
                .fromParam("bindingParam", "myQualifier", op.colTypes(op.colType("myUri", "string")))
                .lockForUpdate(op.viewCol("myQualifier", "myUri"))
                .bindParam("bindingParam", new JacksonHandle(paramValue), null));
        assertEquals(1, rows.size());
    }
}
