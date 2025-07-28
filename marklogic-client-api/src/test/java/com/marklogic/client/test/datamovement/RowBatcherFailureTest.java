package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.RowBatcher;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RowBatcherFailureTest {

	@Test
	void invalidQuery() {
		DatabaseClient client = Common.newClient();
		DataMovementManager dmm = client.newDataMovementManager();

		RowManager rowManager = client.newRowManager();
		PlanBuilder op = rowManager.newPlanBuilder();
		PlanBuilder.ModifyPlan plan = op
			.fromView("opticUnitTest", "musician_ml10")
			.where(op.eq(op.col("dob"), op.xs.string("this is not a valid date")));

		List<JsonNode> returnedRows = new ArrayList<>();
		List<Throwable> batchFailures = new ArrayList<>();

		RowBatcher rowBatcher = dmm.newRowBatcher(new JacksonHandle())
			.withBatchView(plan)
			.withBatchSize(Integer.MAX_VALUE) // guarantees a single batch
			.onSuccess(batch -> returnedRows.add(batch.getRowsDoc()))
			.onFailure(((batch, throwable) -> batchFailures.add(throwable)));

		dmm.startJob(rowBatcher);
		rowBatcher.awaitCompletion();
		dmm.stopJob(rowBatcher);

		assertEquals(0, returnedRows.size(), "The query is invalid, so no rows should have been captured by the " +
			"success listener");

		assertEquals(1, batchFailures.size(), "The query is invalid, so the failure listener should have been invoked " +
			"once. Somewhat surprisingly, this doesn't cause a failure when the RowBatcher is created. This is due to " +
			"the 'estimate' query in internal/viewinfo only querying on the schema + view, thereby ignoring every other " +
			"part of the query. So the invalid query won't be detected until the first call is made to MarkLogic to " +
			"get rows back.");
		Throwable failure = batchFailures.get(0);
		assertTrue(failure instanceof FailedRequestException);
		FailedRequestException ex = (FailedRequestException) failure;
		assertTrue(ex.getMessage().contains("Invalid cast: \"this is not a valid date\" cast as xs:date"),
			"Unexpected error message: " + ex.getMessage() + "; should have failed because the date is not valid");
	}
}
