/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import com.marklogic.client.io.marker.TextWriteHandle;

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
     * Distinguishes between the header and rows that constitute a row set.
     */
    public enum RowSetPart{HEADER, ROWS}

    /**
     * Distinguishes between rows in an object structure or array structure.
     */
    public enum RowStructure{ARRAY, OBJECT}

    /**
     * Returns whether data types should be emitted in each row (the default) or in the header
     * in the response for requests made with the row manager.
     * @return	the part of the row set that identifies the column data types
     */
    RowSetPart getDatatypeStyle();
    /**
     * Specifies whether to emit the data type of each column in each row or only in the header
     * in the response for requests made with the row manager.
     * 
     * The distinction is significant when getting the rows as JSON or XML.
     * Because the server supports columns with variant data types, the default is in each row.
     * You can configure the row manager to return more concise objects if the data type
     * is consistent or if you aren't using the data type.
     * 
     * @param style	the part of the rowset that should contain data types
     */
    void setDatatypeStyle(RowSetPart style);

    /**
     * Returns whether each row should have an array or object structure 
     * in the response for requests made with the row manager.
     * @return	the style of row structure
     */
    RowStructure getRowStructureStyle();
    /**
     * Specifies whether to get each row as an object (the default) or as an array
     * in the response for requests made with the row manager.
     * 
     * The distinction is significant when getting the rows as JSON
     * and also when executing a map or reduce function on the server.
     * 
     * @param style	the structure of rows in the response
     */
    void setRowStructureStyle(RowStructure style);

    /**
     * Defines a plan from a JSON serialization of the plan AST (Abstract Syntax Tree).
     * @param	handle a handle for a JSON serialization of a PlanAST
     * @return	a plan for constructing and retrieving database rows
     */
    RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle);

// TODO: JavaDoc
    RawQueryDSLPlan newQueryDSLPlan(TextWriteHandle handle);
    RawSQLPlan newRawSQLPlan(TextWriteHandle handle);
    RawSPARQLSelectPlan newRawSPARQLSelectPlan(TextWriteHandle handle);


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
}
