package com.marklogic.client.test.rows;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.CtsReferenceExpr;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Written for bug 58069; see that description for more information.
 */
public class JoinDocTest extends AbstractOpticUpdateTest {

    @Test
    public void propertiesFragmentsShouldNotBeReturned() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery("/acme/"))
            .joinDoc(op.col("doc"), op.col("uri"));

        verifyPropertiesFragmentsAreNotReturned(plan);
    }

    /**
     * Same as propertiesFragmentsShouldNotBeReturned, but uses fromLexicons so it can run against ML 10.
     *
     * 2022-12-12 This is now running only on ML 11, as it's consistently failing on ML 10. We have a fix slated for
     * 11.x, and it's not clear yet if it'll be backported to ML 10.
     */
    @Test
    public void propertiesFragmentShouldNotBeReturnedByFromLexicons() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        Map<String, CtsReferenceExpr> lexicons = new HashMap<>();
        lexicons.put("uri", op.cts.uriReference());

        PlanBuilder.ModifyPlan plan = op.fromLexicons(lexicons, "", op.fragmentIdCol("fragmentId"))
            .where(op.cts.directoryQuery("/acme/"))
            .joinDoc(op.col("doc"), op.col("uri"));

        verifyPropertiesFragmentsAreNotReturned(plan);
    }

    private void verifyPropertiesFragmentsAreNotReturned(PlanBuilder.ModifyPlan plan) {
        final int docCount = 50;
        writeDocs(docCount);

        List<RowRecord> rows = resultRows(plan);
        System.out.println(rows);
        assertEquals(docCount, rows.size(),
			"If the actual count is double the expected count, then joinDoc is erroneously pulling back " +
				"properties fragments. These exist because the test database has the 'last modified' flag on by default, " +
				"resulting the creation of a properties fragment for each URI. This error is very intermittent though " +
				"and we do not have a reliable way to reproduce it. Once it happens, it will happen reliably for awhile, " +
				"regardless of the number of URIs being returned by fromDocUris.");
    }

    private void writeDocs(int docCount) {
        JSONDocumentManager mgr = Common.client.newJSONDocumentManager();
        DocumentWriteSet writeSet = mgr.newWriteSet();
        for (int i = 1; i <= docCount; i++) {
            writeSet.add(newWriteOp("/acme/" + i + ".json", mapper.createObjectNode().put("hello", "world")));
        }
        mgr.write(writeSet);
    }
}
