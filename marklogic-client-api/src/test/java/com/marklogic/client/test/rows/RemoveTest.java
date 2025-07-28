package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequiresML11.class)
public class RemoveTest extends AbstractOpticUpdateTest {

    @Test
    public void removeTwoOfThreeDocs() {
        writeThreeXmlDocuments();

        rowManager.execute(op
            .fromDocUris("/acme/doc1.xml", "/acme/doc3.xml")
            .remove());
        verifyDocsDeleted();
    }

    @Test
    public void uriColumnSpecified() {
        writeThreeXmlDocuments();

        rowManager.execute(op
            .fromDocUris("/acme/doc1.xml", "/acme/doc3.xml")
            .remove(op.col("uri")));
        verifyDocsDeleted();
    }

    @Test
    public void multipleQualifiedUriColumns() {
        writeThreeXmlDocuments();

        ModifyPlan plan = op
            .fromDocUris(op.cts.documentQuery(op.xs.stringSeq("/acme/doc1.xml", "/acme/doc3.xml")), "view1")
            .joinLeftOuter(
                op.fromDocUris(op.cts.documentQuery(op.xs.stringSeq("/acme/doc1.xml", "/acme/doc3.xml")), "view2"),
                op.on(
                    op.viewCol("view1", "uri"),
                    op.viewCol("view2", "uri")
                )
            )
            .remove(op.viewCol("view1", "uri"));

        List<RowRecord> rows = resultRows(plan);
        assertEquals(2, rows.size());
        verifyDocsDeleted();
    }

    @Test
    public void fromParamWithCustomUriColumn() {
        writeThreeXmlDocuments();

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("myUri", "/acme/doc1.xml");
        paramValue.addObject().put("myUri", "/acme/doc3.xml");

        ModifyPlan plan = op
            .fromParam("bindingParam", "", op.colTypes(op.colType("myUri", "string")))
            .remove(op.col("myUri"));

        rowManager.execute(plan.bindParam("bindingParam", new JacksonHandle(paramValue), null));
        verifyDocsDeleted();
    }

    @Test
    public void fromParamWithQualifiedUriColumn() {
        writeThreeXmlDocuments();

        ArrayNode paramValue = mapper.createArrayNode();
        paramValue.addObject().put("uri", "/acme/doc1.xml");
        paramValue.addObject().put("uri", "/acme/doc3.xml");

        ModifyPlan plan = op
            .fromParam("bindingParam", "myQualifier", op.colTypes(op.colType("uri", "string")))
            .remove(op.viewCol("myQualifier", "uri"));

        rowManager.execute(plan.bindParam("bindingParam", new JacksonHandle(paramValue), null));
        verifyDocsDeleted();
    }

    @Test
    public void removeTemporal() {
        final String uri = "/acme/temporal-remove.json";
        final String temporalCollection = "temporal-collection";

        // Write the temporal doc
        rowManager.execute(op
            .fromDocDescriptors(op.docDescriptor(
                new DocumentWriteOperationImpl(uri, newDefaultMetadata(), new JacksonHandle(newTemporalContent()), temporalCollection)
            ))
            .write(op.docCols(null, op.xs.stringSeq("uri", "doc", "temporalCollection", "permissions"))));

        verifyMetadata(uri, metadata -> {
            assertTrue(metadata.getCollections().contains("latest"),
				"The document should be in the 'latest' collection if it was correctly inserted via " +
					"temporal.documentInsert");
            assertTrue(metadata.getCollections().contains(temporalCollection));
        });

        // Remove the temporal doc
        rowManager.execute(op
            .fromDocUris(uri)
            .bind(op.as(op.col("tempColl"), op.xs.string(temporalCollection)))
            .remove(op.col("tempColl"), op.col("uri")));

        verifyMetadata(uri, metadata -> {
            assertFalse(metadata.getCollections().contains("latest"),
				"The doc should still exist but no longer be in the 'latest' collection, as " +
					"temporal.documentDelete should have removed it from that collection");
            assertTrue(metadata.getCollections().contains(temporalCollection));
        });
    }

    private void writeThreeXmlDocuments() {
        DocumentMetadataHandle metadata = newDefaultMetadata();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet()
            .add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML))
            .add("/acme/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML))
            .add("/acme/doc3.xml", metadata, new StringHandle("<doc>3</doc>").withFormat(Format.XML));

        rowManager.execute(op
            .fromParam("myDocs", null, op.docColTypes())
            .write()
            .bindParam("myDocs", writeSet));
    }

    private void verifyDocsDeleted() {
        // Assumes that the test deleted doc1 and doc3
        GenericDocumentManager mgr = Common.client.newDocumentManager();
        assertNull(mgr.exists("/acme/doc1.xml"));
        assertNull(mgr.exists("/acme/doc3.xml"));
        assertNotNull(mgr.exists("/acme/doc2.xml"));
    }
}
