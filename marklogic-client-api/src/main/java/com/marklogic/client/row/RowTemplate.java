/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.expression.PlanBuilder;

import java.io.IOException;
import java.util.function.Function;

/**
 * Simplifies common use cases for the {@link RowManager} API. Allows for Optic queries to be constructed with less
 * boilerplate code, and for results to be processed without worrying about closing the row set.
 *
 * @since 7.2.0
 */
public class RowTemplate {

	private final DatabaseClient databaseClient;

	public RowTemplate(DatabaseClient client) {
		this.databaseClient = client;
	}

	/**
	 * Execute a query defined by the given function for building an Optic plan. The function is passed an instance of
	 * {@link PlanBuilder} which is commonly named "op". The {@link RowSet} returned by executing the query is then
	 * passed to the consumer function, which is expected to process the rows and return a result. This method
	 * will handle closing the row set, such that the consumer does not need to worry about it.
	 *
	 * @param planFunction   function for building the Optic plan
	 * @param rowSetConsumer function for processing the rows in the row set
	 * @param <T>            the type of the result returned by the consumer function
	 * @return the result of the consumer function
	 */
	public <T> T query(Function<PlanBuilder, PlanBuilder.Plan> planFunction, Function<RowSet<RowRecord>, T> rowSetConsumer) {
		final RowManager rowManager = databaseClient.newRowManager();
		final PlanBuilder.Plan plan = planFunction.apply(rowManager.newPlanBuilder());
		return resultRows(rowManager, plan, rowSetConsumer);
	}

	/**
	 * Execute an update defined by the given function for building an Optic plan. The plan is expected to update one
	 * or more documents in the database without any data being returned.
	 *
	 * @param planFunction function for building the Optic plan
	 */
	public void update(Function<PlanBuilder, PlanBuilder.Plan> planFunction) {
		update(planFunction, null);
	}

	/**
	 * Execute an update defined by the given function for building an Optic plan. The plan is expected to update one
	 * or more documents in the database. The consumer function can then return a result based on the rows in the row
	 * set.
	 *
	 * @param planFunction   function for building the Optic plan
	 * @param rowSetConsumer function for processing the rows in the row set
	 * @param <T>            the type of the result returned by the consumer function
	 * @return the result of the consumer function
	 */
	public <T> T update(Function<PlanBuilder, PlanBuilder.Plan> planFunction, Function<RowSet<RowRecord>, T> rowSetConsumer) {
		final RowManager rowManager = databaseClient.newRowManager().withUpdate(true);
		final PlanBuilder.Plan plan = planFunction.apply(rowManager.newPlanBuilder());
		if (rowSetConsumer == null) {
			rowManager.execute(plan);
			return null;
		} else {
			return resultRows(rowManager, plan, rowSetConsumer);
		}
	}

	private <T> T resultRows(RowManager rowManager, PlanBuilder.Plan plan, Function<RowSet<RowRecord>, T> rowSetConsumer) {
		try (RowSet<RowRecord> rows = rowManager.resultRows(plan)) {
			return rowSetConsumer.apply(rows);
		} catch (IOException ex) {
			throw new MarkLogicIOException(
				String.format("Unable to close row set; cause: %s", ex.getMessage()), ex);
		}
	}

}
