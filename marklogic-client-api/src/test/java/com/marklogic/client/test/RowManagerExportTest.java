package com.marklogic.client.test;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanTripleOption;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that export/import works for each accessor. This test is critical to ensure that each generated accessor
 * in PlanBuilderImpl is overridden in PlanBuilderSubImpl to ensure that the PlanBuilder is passed in; this avoids
 * null-pointer errors from the handleRegistry being null during export().
 * <p>
 * fromLiterals is not tested here because RowManagerTest already verifies export/import for it.
 */
public class RowManagerExportTest {

    private RowManager rowManager;
    private PlanBuilder op;

    @Before
    public void beforeClass() {
        Common.connect();
        rowManager = Common.client.newRowManager();
        op = rowManager.newPlanBuilder();
    }

    @Test
    public void fromDocUris() {
        // verifyExportedPlanReturnsSameRowCount(
        //         op.fromDocUris(op.cts.wordQuery("trumpet"), "")
        // );
    }

    // @Test
    // public void fromLexicons() {
    //     Map<String, CtsReferenceExpr> lexicons = new HashMap<>();
    //     lexicons.put("uri", op.cts.uriReference());
    //     lexicons.put("int", op.cts.elementReference(op.xs.QName("int")));

    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromLexicons(lexicons)
    //     );
    // }

    // @Test
    // public void fromParam() {
    //     DocumentMetadataHandle metadata = new DocumentMetadataHandle();
    //     DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
    //     writeSet.add("/fromParam/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
    //     writeSet.add("/fromParam/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));

    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromParam("myDocs", "", op.docColTypes()),
    //             plan -> plan.bindParam("myDocs", writeSet)
    //     );
    // }

    // @Test
    // public void fromSearch() {
    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromSearch(op.cts.jsonPropertyValueQuery("instrument", "trumpet"))
    //     );
    // }
    
    // @Test
    // public void fromSearchDocs() {
    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromSearchDocs(op.cts.wordQuery("trumpet"))
    //     );
    // }

    // Ignoring, as running these and then running a fromDocDescriptors or fromParam test is causing a segfault
//     @Test
//     public void fromSparql() {
//         String selectStmt = "PREFIX ad: <http://marklogicsparql.com/addressbook#> " +
//                 "SELECT ?firstName " +
//                 "WHERE {<#5555> ad:firstName ?firstName .}";

//         verifyExportedPlanReturnsSameRowCount(
//                 op.fromSparql(selectStmt, "sparql",
//                         op.sparqlOptions().withDeduplicated(false).withBase("http://marklogicsparql.com/id#")
//                 )
//         );
//     }

//     @Test
//     public void fromSql() {
//         verifyExportedPlanReturnsSameRowCount(
//                 op.fromSql("select * from opticUnitTest.musician")
//         );
//     }

    // @Test
    // public void fromTriples() {
    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromTriples(
    //                     op.pattern(op.col("subject"), op.prefixer("http://example.org/rowgraph").iri("p1"), op.col("object")),
    //                     null, (String) null, PlanTripleOption.DEDUPLICATED
    //             )
    //     );
    // }

    // @Test
    // public void fromView() {
    //     verifyExportedPlanReturnsSameRowCount(
    //             op.fromView("opticUnitTest", "musician"), null
    //     );
    // }

    private void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan) {
        verifyExportedPlanReturnsSameRowCount(plan, null);
    }

    private void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan,
                                                       Function<PlanBuilder.Plan, PlanBuilder.Plan> bindingFunction) {
        PlanBuilder.Plan planToExecute = bindingFunction != null ? bindingFunction.apply(plan) : plan;
        List<RowRecord> rowsFromPlan = rowManager
                .resultRows(planToExecute)
                .stream().collect(Collectors.toList());

        String exportedPlan = plan.exportAs(String.class);
        RawPlanDefinition rawPlan = rowManager.newRawPlanDefinition(new StringHandle(exportedPlan));
        PlanBuilder.Plan rawPlanToExecute = bindingFunction != null ? bindingFunction.apply(rawPlan) : rawPlan;

        List<RowRecord> rowsFromExportedPlan = rowManager
                .resultRows(rawPlanToExecute)
                .stream().collect(Collectors.toList());
        assertEquals("The row count from the exported list should match that of the rows from the original plan",
                rowsFromPlan.size(), rowsFromExportedPlan.size());

    }
}
