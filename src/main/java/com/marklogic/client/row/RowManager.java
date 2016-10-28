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
import com.marklogic.client.io.marker.StructureReadHandle;

/**
 * A Row Manager provides database operations on rows projected from documents.
 */
public interface RowManager {
    /**
     * Creates a builder to define a plan for constructing and retrieving database rows.
     * @return	a builder for row plans
     */
	PlanBuilder newPlanBuilder();

	/**
     * Defines a plan from a JSON serialization of the plan AST (Abstract Syntax Tree).
     * @param	handle a handle for a JSON serialization of a PlanAST
	 * @return	a plan for constructing and retrieving database rows
	 */
	RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle);

	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a map interface for the column values in each row.
	 * @param plan	the definition of a plan for the database rows
	 * @return	an iterable over the results with a map interface
	 */
	RowSet<RowRecord> resultRows(Plan plan);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a map interface and reflecting documents written or deleted by an
	 * uncommitted transaction.
	 * @param plan	the definition of a plan for the database rows
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @return	an iterable over the results with a map interface for each row
	 */
	RowSet<RowRecord> resultRows(Plan plan, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row.
	 * @param plan	the definition of a plan for the database rows
	 * @param rowHandle	the JSON or XML handle that provides each row
     * @param <T> the type of the row handle
	 * @return	an iterable over the result rows
	 */
	<T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle);
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
	<T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row and reflecting documents written or 
	 * deleted by an uncommitted transaction with the column data types declared
	 * in the specified location.
	 * @param plan	the definition of a plan for the database rows
	 * @param rowHandle	the JSON or XML handle that provides each row
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @param typeLocation	specifies whether column types should appear in each row or once on the header
     * @param <T> the type of the row handle
	 * @return	an iterable over the result rows
	 */
	<T extends StructureReadHandle> RowSet<T> resultRows(Plan plan, T rowHandle, Transaction transaction, ColumnTypes typeLocation);

	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row and reflecting documents written or 
	 * deleted by an uncommitted transaction.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading each row as JSON or XML content
     * @param <T> the type of object that will be returned by the handle registered for it
	 * @return	an iterable over the result rows
	 */
	<T> RowSet<T> resultRowsAs(Plan plan, Class<T> as);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row and reflecting documents written or 
	 * deleted by an uncommitted transaction.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading each row as JSON or XML content
     * @param transaction	a open transaction for documents from which rows have been projected
     * @param <T> the type of object that will be returned by the handle registered for it
	 * @return	an iterable over the result rows
	 */
	<T> RowSet<T> resultRowsAs(Plan plan, Class<T> as, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a JSON or XML handle for each row and reflecting documents written or 
	 * deleted by an uncommitted transaction with the column data types declared
	 * in the specified location.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading each row as JSON or XML content
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @param typeLocation	specifies whether column types should appear in each row or once on the header
     * @param <T> the type of object that will be returned by the handle registered for it
	 * @return	an iterable over the result rows
	 */
	<T> RowSet<T> resultRowsAs(Plan plan, Class<T> as, Transaction transaction, ColumnTypes typeLocation);

	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a handle to get the set of rows as a single JSON or XML structure.
	 * @param plan	the definition of a plan for the database rows
	 * @param handle	the JSON or XML handle for the set of rows
     * @param <T> the type of the row handle
	 * @return	the JSON or XML handle populated with the set of rows
	 */
	<T extends StructureReadHandle> T resultDoc(Plan plan, T handle);
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
	<T extends StructureReadHandle> T resultDoc(Plan plan, T handle, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan using
	 * a handle to get the set of rows as a single JSON or XML structure
	 * and reflecting documents written or deleted by an uncommitted transaction
	 * with the column data types declared in the specified location.
	 * @param plan	the definition of a plan for the database rows
	 * @param handle	the JSON or XML handle for the set of rows
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @param typeLocation	specifies whether column types should appear in each row or once on the header
     * @param <T> the type of the row handle
	 * @return	the JSON or XML handle populated with the set of rows
	 */
	<T extends StructureReadHandle> T resultDoc(Plan plan, T handle, Transaction transaction, ColumnTypes typeLocation);
	
	/**
	 * Constructs and retrieves a set of database rows based on a plan
	 * in the representation specified by the IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading the set of rows
     * @param <T> the type of the IO object for reading the set of rows
     * @return	an object of the IO class with the content of the set of rows
	 */
	<T> T resultDocAs(Plan plan, Class<T> as);
	/**
	 * Constructs and retrieves a set of database rows based on a plan
	 * in the representation specified by the IO class and reflecting
	 * documents written or deleted by an uncommitted transaction.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading the set of rows
     * @param transaction	a open transaction for documents from which rows have been projected
     * @param <T> the type of the IO object for reading the set of rows
     * @return	an object of the IO class with the content of the set of rows
	 */
	<T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction);
	/**
	 * Constructs and retrieves a set of database rows based on a plan
	 * in the representation specified by the IO class and reflecting
	 * documents written or deleted by an uncommitted transaction with
	 * the column data types declared in the specified location.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for the database rows
     * @param as	the IO class for reading the set of rows
     * @param transaction	a open transaction for documents from which rows have been projected
	 * @param typeLocation	specifies whether column types should appear in each row or once on the header
     * @param <T> the type of the IO object for reading the set of rows
     * @return	an object of the IO class with the content of the set of rows
	 */
	<T> T resultDocAs(Plan plan, Class<T> as, Transaction transaction, ColumnTypes typeLocation);

	/**
	 * Constructs a plan for retrieving a set of database rows and returns a handle
	 * for the explanation of the plan as a JSON or XML structure.
	 * @param plan	the definition of a plan for database rows
	 * @param handle	the JSON or XML handle on the explanation for the plan
     * @param <T> the type of the explanation handle
     * @return	an object of the IO class with the content of the explanation for the plan
	 */
	<T extends StructureReadHandle> T explain(Plan plan, T handle);
	/**
	 * Constructs a plan for retrieving a set of database rows and returns an explanation
	 * of the plan in the representation specified by the IO class.
	 * 
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement 
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     * 
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     * 
	 * @param plan	the definition of a plan for database rows
     * @param as	the IO class for reading the explanation for the plan
     * @param <T> the type of the IO object for reading the explanation
     * @return	an object of the IO class with the content of the explanation for the plan
	 */
	<T> T explainAs(Plan plan, Class<T> as);

	/**
	 * Distinguishes between column types declared in each row and column types declared
	 * once in the header. Declaring the column types in the header is appropriate only
	 * if each column has a consistent data type or if the data type of the columns
	 * doesn't matter.
	 */
	public enum ColumnTypes{HEADER, ROWS};
}
