/*
 * Copyright 2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.row;

import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.RowReadHandle;

/* STATUS

   DONE:
   + PlanBuilder - fromView(), fromTriples(), fromLexicon(), fromLiterals(), expressions
   + PlanBuilder - placeholder parameter binding
   + PlanBuilder - cts.query
   + RowManager.newPlanBuilder()
   + RowManager.resultDoc()
   + RowManager.resultDocAs()
   + RowManager.resultRows()
   + RowManager - placeholder parameters
   + RawPlanDefinition
   + RowSet
   + RowRecord
   + RowRecord - column metadata

   TO DO:
   + RowRecord - sequence column values
   + RowManager.resultRowsAs()
   + PlanBuilder - node constructors
 */

/**
 * A Row Manager provides database operations on rows projected from documents.
 *
 */
public interface RowManager {
    /**
     * Creates a builder to define a plan for constructing and retrieving database rows.
     * @return	a builder for row plans
     */
	public PlanBuilder newPlanBuilder();

	/**
     * Defines a plan from a JSON serialization of the plan AST (Abstract Syntax Tree).
     * @param	handle a handle for a JSON serialization of a PlanAST
	 * @return	a plan for constructing and retrieving database rows
	 */
	public RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle);

	/**
	 * Constructs and retrieves a set of database rows based on a plan.
	 * @param plan	the definition of a plan for the database rows
	 * @return	an iterable over the results with a map-like interface for each row
	 */
	public RowSet<RowRecord> resultRows(Plan plan);
	/**
	 * Constructs and retrieves a set of database rows based on a plan reflecting
	 * documents written or deleted by an uncommitted transaction.
	 * @param plan	the definition of a plan for the database rows
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @return	an iterable over the results with a map-like interface for each row
	 */
	public RowSet<RowRecord> resultRows(Plan plan, Transaction transaction);

	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle);
    public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction);

/* TODO:
    public <T> RowSet<T> resultRows(Plan plan, Class<T> as);
    public <T> RowSet<T> resultRows(Plan plan, Class<T> as, Transaction transaction);
 */

	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle);
	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle, Transaction transaction);

	public <T> T resultDocAs(Plan plan, Class<T> as);
	public <T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction);
}
