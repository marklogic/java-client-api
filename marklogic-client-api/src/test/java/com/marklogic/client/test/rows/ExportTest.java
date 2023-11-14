package com.marklogic.client.test.rows;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanTripleOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Verifies that export/import works for each accessor. This test is critical to ensure that each generated accessor
 * in PlanBuilderImpl is overridden in PlanBuilderSubImpl to ensure that the PlanBuilder is passed in; this avoids
 * null-pointer errors from the handleRegistry being null during export().
 * <p>
 * fromLiterals is not tested here because RowManagerTest already verifies export/import for it.
 */
public class ExportTest extends AbstractOpticUpdateTest {

    @Test
	@ExtendWith(RequiresML11.class)
    public void fromDocUris() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromDocUris(op.cts.wordQuery("trumpet"), "")
        );
    }

    @Test
    public void fromLexicons() {
        Map<String, CtsReferenceExpr> lexicons = new HashMap<>();
        lexicons.put("uri", op.cts.uriReference());
        lexicons.put("int", op.cts.elementReference(op.xs.QName("int")));

        verifyExportedPlanReturnsSameRowCount(
                op.fromLexicons(lexicons)
        );
    }

    @Test
	@ExtendWith(RequiresML11.class)
    public void fromParam() {
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        DocumentWriteSet writeSet = Common.client.newDocumentManager().newWriteSet();
        writeSet.add("/acme/doc1.xml", metadata, new StringHandle("<doc>1</doc>").withFormat(Format.XML));
        writeSet.add("/acme/doc2.xml", metadata, new StringHandle("<doc>2</doc>").withFormat(Format.XML));

        verifyExportedPlanReturnsSameRowCount(
                op.fromParam("myDocs", "", op.docColTypes()),
                plan -> plan.bindParam("myDocs", writeSet)
        );
    }

    @Test
    public void fromSearch() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromSearch(op.cts.jsonPropertyValueQuery("instrument", "trumpet"))
        );
    }

    @Test
    public void fromSearchDocs() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromSearchDocs(op.cts.wordQuery("trumpet"))
        );
    }

    @Test
    public void fromSparql() {
        String selectStmt = "PREFIX ad: <http://marklogicsparql.com/addressbook#> " +
                "SELECT ?firstName " +
                "WHERE {<#5555> ad:firstName ?firstName .}";

        verifyExportedPlanReturnsSameRowCount(
                op.fromSparql(selectStmt, "sparql",
                        op.sparqlOptions().withDeduplicated(false).withBase("http://marklogicsparql.com/id#")
                )
        );
    }

    @Test
    public void fromSql() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromSql("select * from opticUnitTest.musician_ml10")
        );
    }

    @Test
    public void fromTriples() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromTriples(
                        op.pattern(op.col("subject"), op.prefixer("http://example.org/rowgraph").iri("p1"), op.col("object")),
                        null, (String) null, PlanTripleOption.DEDUPLICATED
                )
        );
    }

    @Test
    public void fromView() {
        verifyExportedPlanReturnsSameRowCount(
                op.fromView("opticUnitTest", "musician_ml10"), null
        );
    }

}
