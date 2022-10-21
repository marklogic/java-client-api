package com.marklogic.client.test.rows;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;

public class RowManagerRemoveTest extends AbstractRowManagerTest {

    @Test
    public void removeTwoOfThreeDocs() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        writeThreeXmlDocuments();

        rowManager.execute(op
                .fromDocUris("/fromParam/doc1.xml", "/fromParam/doc3.xml")
                .remove());
        verifyDocsDeleted();
    }

    @Test
    public void uriColumnSpecified() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        writeThreeXmlDocuments();

        rowManager.execute(op
                .fromDocUris("/fromParam/doc1.xml", "/fromParam/doc3.xml")
                .remove(op.col("uri")));
        verifyDocsDeleted();
    }

    @Test
    public void fromParamWithCustomUriColumn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        writeThreeXmlDocuments();

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("myUri", "/fromParam/doc1.xml");
        paramValue.addObject().put("myUri", "/fromParam/doc3.xml");

        ModifyPlan plan = op
                .fromParam("bindingParam", "", op.colTypes(op.colType("myUri", "string")))
                .remove(op.col("myUri"));

        rowManager.execute(plan.bindParam("bindingParam", new JacksonHandle(paramValue), null));
        verifyDocsDeleted();
    }

    @Test
    public void fromParamWithQualifiedUriColumn() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        writeThreeXmlDocuments();

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("uri", "/fromParam/doc1.xml");
        paramValue.addObject().put("uri", "/fromParam/doc3.xml");

        ModifyPlan plan = op
                .fromParam("bindingParam", "myQualifier", op.colTypes(op.colType("uri", "string")))
                .remove(op.viewCol("myQualifier", "uri"));

        rowManager.execute(plan.bindParam("bindingParam", new JacksonHandle(paramValue), null));
        verifyDocsDeleted();
    }

    @Test
    public void removeTemporal() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final String uri = "/fromParam/temporal-remove.json";
        final String temporalCollection = "temporal-collection";
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add(uri, new DocumentMetadataHandle(), new JacksonHandle(newTemporalContent()), temporalCollection);

        // Write the temporal doc
        rowManager.resultRows(op
                .fromDocDescriptors(op.docDescriptors(writeSet))
                .write(op.docCols(new String[] { "uri", "doc", "temporalCollection", "permissions" })));

        verifyMetadata(uri, metadata -> {
            assertTrue("The document should be in the 'latest' collection if it was correctly inserted via " +
                    "temporal.documentInsert", metadata.getCollections().contains("latest"));
            assertTrue(metadata.getCollections().contains(temporalCollection));
        });

        // Remove the temporal doc
        rowManager.execute(op
                .fromDocUris(uri)
                .remove(temporalCollection, op.col("uri")));

        verifyMetadata(uri, metadata -> {
            assertFalse("The doc should still exist but no longer be in the 'latest' collection, as " +
                    "temporal.documentDelete should have removed it from that collection",
                    metadata.getCollections().contains("latest"));
            assertTrue(metadata.getCollections().contains(temporalCollection));
        });
    }

    private void writeThreeXmlDocuments() {
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet()
                .add("/fromParam/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML))
                .add("/fromParam/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML))
                .add("/fromParam/doc3.xml", metadata, new StringHandle("<doc>3</doc>").withFormat(Format.XML));

        rowManager.execute(op
                .fromParam("myDocs", null, op.docColTypes())
                .write()
                .bindParam("myDocs", writeSet));
    }

    private void verifyDocsDeleted() {
        // Assumes that the test deleted doc1 and doc3
        GenericDocumentManager mgr = Common.client.newDocumentManager();
        assertNull(mgr.exists("/fromParam/doc1.xml"));
        assertNull(mgr.exists("/fromParam/doc3.xml"));
        assertNotNull(mgr.exists("/fromParam/doc2.xml"));
    }
}
