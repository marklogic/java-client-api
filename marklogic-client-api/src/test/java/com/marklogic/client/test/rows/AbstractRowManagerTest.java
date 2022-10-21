package com.marklogic.client.test.rows;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;

public abstract class AbstractRowManagerTest {

    private final static String XML_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    protected RowManager rowManager;
    protected PlanBuilder op;
    protected ObjectMapper mapper = new ObjectMapper();

    @Before
    public void beforeClass() {
        Common.connect();
        // Subclasses of this test are expected to only write URIs starting with /fromParam/, so delete all of them
        // before running the test to ensure a document doesn't already exist.
        Common.connectServerAdmin().newServerEval()
                .xquery("cts:uri-match('/fromParam/*') ! xdmp:document-delete(.)")
                .evalAs(String.class);

        rowManager = Common.client.newRowManager();
        op = rowManager.newPlanBuilder();
    }

    protected void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan) {
        verifyExportedPlanReturnsSameRowCount(plan, null);
    }

    protected final void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan,
                                                               Function<PlanBuilder.Plan, PlanBuilder.Plan> bindingFunction) {
        PlanBuilder.Plan planToExecute = bindingFunction != null ? bindingFunction.apply(plan) : plan;
        List<RowRecord> rowsFromPlan = resultRows(planToExecute);

        String exportedPlan = plan.exportAs(String.class);
        RawPlanDefinition rawPlan = rowManager.newRawPlanDefinition(new StringHandle(exportedPlan));
        PlanBuilder.Plan rawPlanToExecute = bindingFunction != null ? bindingFunction.apply(rawPlan) : rawPlan;

        List<RowRecord> rowsFromExportedPlan = resultRows(rawPlanToExecute);
        assertEquals("The row count from the exported list should match that of the rows from the original plan",
                rowsFromPlan.size(), rowsFromExportedPlan.size());
    }

    protected final void verifyJsonDoc(String uri, Consumer<ObjectNode> verifier) {
        verifier.accept((ObjectNode) Common.client.newJSONDocumentManager().read(uri, new JacksonHandle()).get());
    }

    protected final void verifyXmlDoc(String uri, Consumer<String> verifier) {
        String content = Common.client.newXMLDocumentManager().read(uri, new StringHandle().withFormat(Format.XML)).get();
        verifier.accept(content.replace(XML_PREAMBLE, ""));
    }

    protected final void verifyMetadata(String uri, Consumer<DocumentMetadataHandle> verifier) {
        verifier.accept(Common.client.newJSONDocumentManager().readMetadata(uri, new DocumentMetadataHandle()));
    }

    protected final String getRowContentWithoutXmlDeclaration(RowRecord row, String columnName) {
        String content = row.getContentAs(columnName, String.class);
        return content.replace(XML_PREAMBLE, "");
    }

    /**
     * Convenience method for executing a plan and getting the rows back as a list.
     * 
     * @param plan
     * @return
     */
    protected final List<RowRecord> resultRows(Plan plan) {
        return rowManager.resultRows(plan).stream().collect(Collectors.toList());
    }

    protected DocumentWriteOperation newWriteOp(String uri, JsonNode json) {
        return newWriteOp(uri, new JacksonHandle(json));
    }
    
    protected DocumentWriteOperation newWriteOp(String uri, AbstractWriteHandle content) {
        return new DocumentWriteOperationImpl(uri, new DocumentMetadataHandle(), content);
    }

    /**
     * Defines the required fields for the temporal axes configured for the test project.
     * 
     * @return
     */
    protected final ObjectNode newTemporalContent() {
        return mapper.createObjectNode()
                .put("system-start", "")
                .put("system-end", "")
                .put("valid-start", "2015-01-01T00:00:00")
                .put("valid-end", "2017-01-01T00:00:00");
    }
}
