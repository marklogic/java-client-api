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
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a map interface for the column values in each row.
	 * @param plan	the definition of a plan for the database rows
	 * @return	an iterable over the results with a map interface
	 */
	public RowSet<RowRecord> resultRows(Plan plan);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a map interface and reflecting documents written or deleted by an
	 * uncommitted transaction.
	 * @param plan	the definition of a plan for the database rows
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @return	an iterable over the results with a map interface for each row
	 */
	public RowSet<RowRecord> resultRows(Plan plan, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row.
	 * @param plan	the definition of a plan for the database rows
	 * @param rowHandle	the JSON or XML handle that provides each row
     * @param <T> the type of the row handle
	 * @return	an iterable over the result rows
	 */
	public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row and reflecting documents written or 
	 * deleted by an uncommitted transaction.
	 * @param plan	the definition of a plan for the database rows
	 * @param rowHandle	the JSON or XML handle that provides each row
     * @param transaction	a open transaction for documents from which rows have been projected
     * @param <T> the type of the row handle
	 * @return	an iterable over the result rows
	 */
    public <T extends RowReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction);

/* TODO:
    public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as);
    public <T> RowSet<T> resultRowsAs(Plan plan, Class<T> as, Transaction transaction);
 */

	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a handle to get the set of rows as a single JSON or XML structure.
	 * @param plan	the definition of a plan for the database rows
	 * @param handle	the JSON or XML handle for the set of rows
     * @param <T> the type of the row handle
	 * @return	the JSON or XML handle populated with the set of rows
	 */
	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a handle to get the set of rows as a single JSON or XML structure
	 * and reflecting documents written or deleted by an uncommitted transaction.
	 * @param plan	the definition of a plan for the database rows
	 * @param handle	the JSON or XML handle for the set of rows
     * @param transaction	a open transaction for documents from which rows have been projected
     * @param <T> the type of the row handle
	 * @return	the JSON or XML handle populated with the set of rows
	 */
	public <T extends RowReadHandle> T resultDoc(Plan plan, T handle, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan
	 * in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for JSON and XML content are registered.
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading the set of rows
     * @param <T> the type of the IO object for reading the set of rows
     * @return	an object of the IO class with the content of the set of rows
	 */
	public <T> T resultDocAs(Plan plan, Class<T> as);
	/**
	 * Constructs and retrieves a set of database rows based on a plan
	 * in the representation specified by the IO class and reflecting
	 * documents written or deleted by an uncommitted transaction.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for JSON and XML content are registered.
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading the set of rows
     * @param transaction	a open transaction for documents from which rows have been projected
     * @param <T> the type of the IO object for reading the set of rows
     * @return	an object of the IO class with the content of the set of rows
	 */
	public <T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction);
}
