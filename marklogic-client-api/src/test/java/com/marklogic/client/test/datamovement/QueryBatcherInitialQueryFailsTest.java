package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class QueryBatcherInitialQueryFailsTest {

	@Test
	void jobStopsWhenQueryIsInvalid() {
		Common.connect();

		StructuredQueryBuilder queryBuilder = Common.client.newQueryManager().newStructuredQueryBuilder();
		StructuredQueryDefinition invalidQuery = queryBuilder.range(
			queryBuilder.pathIndex("doesnt-work"),
			"xs:date", StructuredQueryBuilder.Operator.GT, "2007-01-01"
		);

		AtomicBoolean successListenerInvoked = new AtomicBoolean(false);
		AtomicBoolean failureListenerInvoked = new AtomicBoolean(false);

		DataMovementManager dataMovementManager = Common.client.newDataMovementManager();
		QueryBatcher queryBatcher = dataMovementManager.newQueryBatcher(invalidQuery)
			.onUrisReady(batch -> successListenerInvoked.set(true))
			.onQueryFailure(failure -> failureListenerInvoked.set(true));

		dataMovementManager.startJob(queryBatcher);
		queryBatcher.awaitCompletion();
		dataMovementManager.stopJob(queryBatcher);

		assertFalse(successListenerInvoked.get(),
			"The success listener should not have been invoked since the initial query was failed; additionally, " +
				"getting to this point in the test verifies that the job stopped successfully, which prior to this " +
				"test being written would not occur due to a bug");

		assertFalse(failureListenerInvoked.get(),
			"The failure listener should not have been invoked either; see QueryBatcherFailureTest for an explanation " +
				"as to what a failure listener actually captures (it does not capture failures from an invalid query)");
	}

}
