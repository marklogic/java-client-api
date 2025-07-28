/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import com.marklogic.client.Transaction;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.JSONReadHandle;

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
     * Set an optional label and enable the "optic" trace event for the duration of each plan execution
     *
     * @param label
     */
    void setTraceLabel(String label);

	/**
	 * As of MarkLogic 11.2, the "v1/rows/update" endpoint must be used in order to submit an Optic plan that performs
	 * an update. This method must be called with a value of {@code true} in order for that endpoint to be used instead
	 * of "v1/rows". You may later call this method with a value of {@code false} in order to submit a plan that does
	 * not perform an update.
	 *
	 * @param update set to {@code true} if submitting a plan that performs an update
	 * @return the instance of this class
	 * @since 6.5.0
	 */
	RowManager withUpdate(boolean update);

    /**
     * @return the label that will be used for all log messages associated with the "optic" trace event
     */
    String getTraceLabel();

    /**
     * Set an optional optimization level. Must be zero or higher.
     * @param value
     */
    void setOptimize(Integer value);

    /**
     * @return optional optimization level
     */
    Integer getOptimize();

    /**
     * Defines a plan from a JSON serialization of the plan AST (Abstract Syntax Tree).
     * @param	handle a handle for a JSON serialization of a plan AST
     * @return	a plan for constructing and retrieving database rows
     */
    RawPlanDefinition newRawPlanDefinition(JSONWriteHandle handle);
    /**
     * Defines a plan from a Query DSL in a JavaScript serialization.
     * @param handle a textual handle for Query DSL in JavaScript format
     * @return	a plan for constructing and retrieving database rows
     */
    RawQueryDSLPlan newRawQueryDSLPlan(TextWriteHandle handle);
    /**
     * Defines a plan from an SQL query.
     * @param handle a textual handle for the SQL serialization
     * @return	a plan for constructing and retrieving database rows
     */
    RawSQLPlan newRawSQLPlan(TextWriteHandle handle);
    /**
     * Defines a plan from a SPARQL SELECT query.
     * @param handle a textual handle for the SPARQL serialization
     * @return	a plan for constructing and retrieving database rows
     */
    RawSPARQLSelectPlan newRawSPARQLSelectPlan(TextWriteHandle handle);

    /**
     * Execute the given plan without returning any result.
     *
     * @param plan the definition of a plan
     */
    void execute(Plan plan);

    /**
     * Execute the given plan without returning any result.
     *
     * @param plan the definition of a plan
     * @param transaction a open transaction for the execute operation to run within
     */
    void execute(Plan plan, Transaction transaction);

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
     * @return	the handle with the content of the explanation for the plan
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
     * Generates generates a view that encapsulates a plan.
     *
     * Insert the generated XML into the schemas database and then use the
     * {@link PlanBuilder#fromView(String, String)} accessor to query
     * against the generated view.
     * @param plan	the definition of a plan for database rows
     * @param schema  the name of the schema for the new view generated from the plan
     * @param view  the name of the new view generated from the plan
     * @param handle	the XML handle on the generated view for the plan
     * @param <T> the type of the handle for the generated view
     * @return	the handle with the content of the generated view for the plan
     */
    <T extends XMLReadHandle> T generateView(PlanBuilder.PreparePlan plan, String schema, String view, T handle);
    /**
     * Generates generates a view that encapsulates a plan.
     *
     * Insert the generated XML into the schemas database and then use the
     * {@link PlanBuilder#fromView(String, String)} accessor to query
     * against the generated view.
     *
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     *
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     *
     * @param plan	the definition of a plan for database rows
     * @param schema  the name of the schema for the new view generated from the plan
     * @param view  the name of the new view generated from the plan
     * @param as	the IO class for reading the generated view for the plan
     * @param <T> the type of the IO object for reading the generated view
     * @return	an object of the IO class with the content of the generated view for the plan
     */
    <T> T generateViewAs(PlanBuilder.PreparePlan plan, String schema, String view, Class<T> as);

    /**
     * This function can be used to inspect the state of a plan before execution. It returns the information about each
     * column in the plan, including schema name, view name, column name, data type and nullability. It also returns the
     * information about system columns.
     *
     * @param plan	the definition of a plan for database rows
     * @param handle	the Json handle on the column information for the plan
     * @param <T> the type of the handle for the column information
     * @return	the handle with the content of the column information for the plan
     */
    <T extends JSONReadHandle> T columnInfo(PlanBuilder.Plan plan, T handle);

    /**
	 * This function can be used to inspect the state of a plan before execution. It returns the information about each
     * column in the plan, including schema name, view name, column name, data type and nullability. It also returns the
     * information about system columns.
     *
     * The IO class must have been registered before creating the database client.
     * By default, the provided handles that implement
     * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
     *
     * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
     *
     * @param plan	the definition of a plan for database rows
     * @param as	the IO class for reading the column information for the plan
     * @param <T> the type of the IO object for reading the column information
     * @return	an object of the IO class with the content of the column information for the plan
     */
    <T> T columnInfoAs(PlanBuilder.Plan plan, Class<T> as);

	/**
	 * Executes a GraphQL query against the database and returns the results as a JSON object.
	 *
	 * @param query the GraphQL query to execute
	 * @param resultsHandle the IO class for capturing the results
	 * @param <T> the type of the IO object for r the results
	 * @return an object of the IO class containing the query results, which will include error messages if the query fails
	 * @since 6.2.0
	 */
	<T extends JSONReadHandle> T graphql(JSONWriteHandle query, T resultsHandle);

	/**
	 * Executes a GraphQL query against the database and returns the results as a JSON object.
	 *
	 * @param query the GraphQL query to execute
	 * @param as the class type of the results to return; typically JsonNode or String
	 * @param <T> the type of the results to return
	 * @return an instance of the given return type that contains the query results, which will include error messages if the query fails
	 * @since 6.2.0
	 */
	<T> T graphqlAs(JSONWriteHandle query, Class<T> as);
}
