package com.marklogic.client.test.rows;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.test.Common;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The actual usage of trace label has to be manually verified by inspecting logs. And there's not a reliable way to
 * verify that a particular valid value for "optimize" had the intended effect. So this test ensures that valid values
 * don't throw errors, and invalid values do not work.
 */
public class TraceLabelAndOptimizeTest extends AbstractOpticUpdateTest {

    @Test
    public void validTraceLabelAndOptimize() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        rowManager.setOptimize(1);
        rowManager.setTraceLabel("test");

        List<RowRecord> rows = resultRows(op
            .fromDocUris(op.cts.directoryQuery("/optic/test/")));
        assertEquals(4, rows.size());
    }

    @Test
    public void optimizeLessThanZeroWithExecute() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        rowManager.setOptimize(-1);
        FailedRequestException ex = assertThrows(
            FailedRequestException.class,
            () -> rowManager.execute(op.fromDocUris(op.cts.documentQuery("/doesnt-matter")))
        );
        assertTrue(
            "Unexpected message: " + ex.getMessage(),
            ex.getMessage().contains("optimize can only be a positive integer")
        );
    }

    @Test
    public void optimizeLessThanZeroWithResultRows() {
        if (!Common.markLogicIsVersion11OrHigher()) {
            return;
        }

        rowManager.setOptimize(-1);
        FailedRequestException ex = assertThrows(
            FailedRequestException.class,
            () -> resultRows(op.fromDocUris(op.cts.documentQuery("/doesnt-matter")))
        );
        assertTrue(
            "Unexpected message: " + ex.getMessage(),
            ex.getMessage().contains("optimize can only be a positive integer")
        );
    }
}
