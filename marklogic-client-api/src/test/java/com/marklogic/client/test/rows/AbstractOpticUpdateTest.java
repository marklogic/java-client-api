package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public abstract class AbstractOpticUpdateTest {

    protected RowManager rowManager;
    protected PlanBuilder op;
    protected ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        // Subclasses of this test are expected to only write URIs starting with /acme/ (which is used so that test
        // URIs show up near the top when exploring the database in qconsole), so delete all of them before running the
        // test to ensure a document doesn't already exist.
        Common.connectServerAdmin().newServerEval()
                .xquery("cts:uri-match('/acme/*') ! xdmp:document-delete(.)")
                .evalAs(String.class);

        Common.client = Common.newClientAsUser("writer-no-default-permissions");
        rowManager = Common.client.newRowManager();
        op = rowManager.newPlanBuilder();
    }

    @After
    public void teardown() {
        // Reset back to the default client that non-OpticUpdate tests expect
        Common.client = Common.newClient();
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

    protected final void verifyMetadata(String uri, Consumer<DocumentMetadataHandle> verifier) {
        verifier.accept(Common.client.newJSONDocumentManager().readMetadata(uri, new DocumentMetadataHandle()));
    }

    protected final String getRowContentWithoutXmlDeclaration(RowRecord row, String columnName) {
        String content = row.getContentAs(columnName, String.class);
        return content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "");
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

    protected DocumentWriteOperation newWriteOp(String uri, DocumentMetadataHandle metadata, JsonNode json) {
        return new DocumentWriteOperationImpl(uri, metadata, new JacksonHandle(json));
    }

    protected DocumentWriteOperation newWriteOp(String uri, AbstractWriteHandle content) {
        return new DocumentWriteOperationImpl(uri, newDefaultMetadata(), content);
    }

    /**
     * Convenience method for constructing metadata with a default set of permissions that the test user - "rest-writer"
     * - can both read and update.
     *
     * @return
     */
    protected DocumentMetadataHandle newDefaultMetadata() {
        return new DocumentMetadataHandle()
                .withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
                .withPermission("test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
    }

    protected int getCollectionSize(String collection) {
        String result = Common.newEvalClient().newServerEval()
            .xquery(String.format("xdmp:estimate(fn:collection('%s'))", collection))
            .evalAs(String.class);
        return Integer.parseInt(result);
    }

    protected List<String> getUrisInCollection(String collection) {
        List<String> uris = new ArrayList<>();
        Common.newEvalClient().newServerEval()
            .xquery(String.format("cts:uris((), (), cts:collection-query('%s'))", collection))
            .eval()
            .forEachRemaining(result -> uris.add(result.getString()));
        return uris;
    }
}
