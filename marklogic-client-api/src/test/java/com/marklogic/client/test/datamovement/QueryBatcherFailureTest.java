package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Captures what may be surprising behavior of onQueryFailure on QueryBatcher - i.e. it does not capture exceptions
 * from the initial query failing, nor does it capture exceptions from an onUrisReady listener failing.
 * <p>
 * Based on code analysis of QueryBatcherImpl, it seems that the only time an onQueryFailure listener will fail is
 * due to some kind of programming error or a very unexpected error when assembling a batch of URIs based on the
 * already-queried-for list of URIs. For example, QueryBatcherTest has a "testBadIteratorAndThrowException" test that
 * uses a bad implementation of Iterator to throw an exception. That seems to adequately capture the very remote chance
 * that onQueryFailure would ever receive an exception - i.e. we rarely expect an Iterator to fail.
 * <p>
 * Thus, it's best to think of "onQueryFailure" as = a very unexpected failure that occurs while assembling
 * already-queried-for batches and handing them off to a QueryBatchListener.
 * <p>
 * Note that onQueryFailure is not invoked if the queryMgr.uris call in QueryBatcherImpl fails - i.e. when the call
 * is made to query for URIs for a batch before the batch is processed. That may be a bug - that seems like when it's
 * supposed to be invoked. You can verify this by forcing an exception to be thrown right after the URIs are retrieved.
 */
public class QueryBatcherFailureTest {

    @Test
    public void invalidQuery() {
        final List<String> failureMessages = new ArrayList<>();

        DatabaseClient client = Common.connect();

        FailedRequestException ex = assertThrows(FailedRequestException.class, () ->
            client.newDataMovementManager().newQueryBatcher(
                client.newQueryManager().newStructuredQueryBuilder().directory(0, "/invalid/path")
            ).onQueryFailure(failure -> failureMessages.add(failure.getMessage()))
        );

        assertTrue(ex.getMessage().contains("Directory URI path must end with '/'"),
			"Unexpected message: " + ex.getMessage()
        );

        assertEquals(0, failureMessages.size(),
			"Despite the name, 'onQueryFailure' does not capture exceptions based on the query failing. If the original " +
				"query fails, then an exception will be immediately thrown by 'newQueryBatcher' - which is good! " +
				"A user can try/catch that and act accordingly. But an onQueryFailure listener is not invoked. ");
    }

    @Test
    public void batchProcessingFails() {
        final List<String> failureMessages = new ArrayList<>();
        final List<String> processedItems = new ArrayList<>();

        DatabaseClient client = Common.connect();
        DataMovementManager dmm = client.newDataMovementManager();
        QueryBatcher qb = dmm.newQueryBatcher(Arrays.asList("item1", "item2").iterator())
            .withThreadCount(1)
            .withBatchSize(1)
            .onUrisReady(batch -> {
                if ("item2".equals(batch.getItems()[0])) {
                    throw new RuntimeException("item2 explodes!");
                }
                processedItems.add(batch.getItems()[0]);
            })
            .onQueryFailure(failure -> failureMessages.add(failure.getMessage()));

        dmm.startJob(qb);
        qb.awaitCompletion();
        dmm.stopJob(qb);

        assertEquals(1, processedItems.size());
        assertEquals("item1", processedItems.get(0));

        assertEquals(0, failureMessages.size(),
			"An onQueryFailure listener does not receive errors from an onUrisReady listener. Instead, " +
				"QueryBatcherImpl simply logs these, such that they are effectively swallowed. It's up to the user " +
				"implementing a QueryBatchListener to provide a better error-handling mechanism.");
    }
}
