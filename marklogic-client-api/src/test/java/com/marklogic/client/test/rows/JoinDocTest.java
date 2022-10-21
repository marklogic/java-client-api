package com.marklogic.client.test.rows;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JoinDocTest extends AbstractOpticUpdateTest {

    @Test
    public void propertiesFragmentsShouldNotBeReturned() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        final int docCount = 50;

        JSONDocumentManager mgr = Common.client.newJSONDocumentManager();
        DocumentWriteSet writeSet = mgr.newWriteSet();
        for (int i = 1; i <= docCount; i++) {
            writeSet.add(newWriteOp("/acme/" + i + ".json", mapper.createObjectNode().put("hello", "world")));
        }
        mgr.write(writeSet);

        PlanBuilder.ModifyPlan plan = op
            .fromDocUris(op.cts.directoryQuery("/acme/"))
            .joinDoc(op.col("doc"), op.col("uri"));

        List<RowRecord> rows = resultRows(plan);
        System.out.println(rows);
        assertEquals("If the actual count is double the expected count, then joinDoc is erroneously pulling back " +
            "properties fragments. These exist because the test database has the 'last modified' flag on by default, " +
            "resulting the creation of a properties fragment for each URI. This error is very intermittent though " +
            "and we do not have a reliable way to reproduce it. Once it happens, it will happen reliably for awhile, " +
            "regardless of the number of URIs being returned by fromDocUris.", docCount, rows.size());
    }
}
