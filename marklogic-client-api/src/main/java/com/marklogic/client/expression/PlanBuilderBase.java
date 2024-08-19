/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;

import java.util.Map;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.JSONReadHandle;

import com.marklogic.client.type.*;

/**
 * Defines base methods for PlanBuilder. This interface is an implementation detail.
 * Use PlanBuilder as the type for instances of PlanBuilder.
 */
public interface PlanBuilderBase {
    /**
     * Provides a convenience for matching documents and constructing rows with the score,
     * document URI, and document content. The convenience is equivalent to chaining
     * {@link PlanBuilder#fromSearch(CtsQueryExpr)},
     * {@link PlanBuilder.ModifyPlan#joinDocUri(String, String)},
     * and {@link PlanBuilder.ModifyPlan#joinDoc(String, String)}.
     * <p>The documents can be ordered by the score and limited for the most relevant
     * documents.</p>
     * @param query  The cts.query expression for matching the documents.
     * @return  a ModifyPlan object
     */
    PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query);
    /**
     * Provides a convenience for matching documents and constructing rows with the score,
     * document URI, and document content. The convenience is equivalent to chaining
     * {@link PlanBuilder#fromSearch(CtsQueryExpr)},
     * {@link PlanBuilder.ModifyPlan#joinDocUri(String, String)},
     * and {@link PlanBuilder.ModifyPlan#joinDoc(String, String)}.
     * <p>The documents can be ordered by the score and limited for the most relevant
     * documents.</p>
     * @param query  The cts.query expression for matching the documents.
     * @param qualifierName Specifies a name for qualifying the column names similar to a view name.
     * @return  a ModifyPlan object
     */
    PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query, String qualifierName);
	/**
	 * Provides a convenience for matching documents and constructing rows with the score,
	 * document URI, and document content. The convenience is equivalent to chaining
	 * {@link PlanBuilder#fromSearch(CtsQueryExpr)},
	 * {@link PlanBuilder.ModifyPlan#joinDocUri(String, String)},
	 * and {@link PlanBuilder.ModifyPlan#joinDoc(String, String)}.
	 * <p>The documents can be ordered by the score and limited for the most relevant
	 * documents.</p>
	 * @param query  The cts.query expression for matching the documents.
	 * @param qualifierName Specifies a name for qualifying the column names similar to a view name.
	 * @return  a ModifyPlan object
	 * @since 7.0.0; requires MarkLogic 12 or higher.
	 */
	PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query, String qualifierName, PlanSearchOptions options);
    /**
     * Supports document matching and relevance by constructing rows with the document fragment id and
     * columns for relevance factors.  Typically, the plan will join the rows on the document fragment id
     * with the content of documents or with rows, triples, or lexicons indexed on the documents (where
     * the lexicons include range indexes, the document URI lexicon, and the collection lexicon).
     * <p>The documents can be ordered by the score or other relevance factors and limited for the
     * most relevant documents.</p>
     * <p>By default, the rows provide fragmentId and score columns.
     * </p>
     * @param query  The cts.query expression for matching the documents.
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query);
    /**
     * Supports document matching and relevance by constructing rows with the document fragment id and
     * columns for relevance factors.  Typically, the plan will join the rows on the document fragment id
     * with the content of documents or with rows, triples, or lexicons indexed on the documents (where
     * the lexicons include range indexes, the document URI lexicon, and the collection lexicon).
     * <p>The documents can be ordered by the score or other relevance factors and limited for the
     * most relevant documents.</p>
     * <p>The list of possible columns to project consists of fragmentId, confidence, fitness,
     * quality, and score. The columns other than the fragmentId provide the same relevance factors
     * as the <a href="https://docs.marklogic.com/cts.confidence">cts.confidence()</a>,
     * <a href="https://docs.marklogic.com/cts.fitness">cts.fitness()</a>,
     * <a href="https://docs.marklogic.com/cts.quality">cts.quality()</a>,
     * and <a href="https://docs.marklogic.com/cts.score">cts.score()</a> functions
     * on the server. Pass {@link PlanBuilder#as(String, ServerExpression)} to rename a column.
     * The default columns are fragmentId and score.
     * </p>
     * @param query  The cts.query expression for matching the documents.
     * @param columns  The columns to project for the documents. See {@link PlanBuilder#colSeq(String...)}
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query, PlanExprCol... columns);
    /**
     * Supports document matching and relevance by constructing rows with the document fragment id and
     * columns for relevance factors.  Typically, the plan will join the rows on the document fragment id
     * with the content of documents or with rows, triples, or lexicons indexed on the documents (where
     * the lexicons include range indexes, the document URI lexicon, and the collection lexicon).
     * <p>The documents can be ordered by the score or other relevance factors and limited for the
     * most relevant documents.</p>
     * <p>The list of possible columns to project consists of fragmentId, confidence, fitness,
     * quality, and score. The columns other than the fragmentId provide the same relevance factors
     * as the <a href="https://docs.marklogic.com/cts.confidence">cts.confidence()</a>,
     * <a href="https://docs.marklogic.com/cts.fitness">cts.fitness()</a>,
     * <a href="https://docs.marklogic.com/cts.quality">cts.quality()</a>,
     * and <a href="https://docs.marklogic.com/cts.score">cts.score()</a> functions
     * on the server. Pass {@link PlanBuilder#as(String, ServerExpression)} to rename a column.
     * The default columns are fragmentId and score.
     * </p>
     * @param query  The cts.query expression for matching the documents.
     * @param columns  The columns to project for the documents. See {@link PlanBuilder#colSeq(String...)}
     * @param qualifierName Specifies a name for qualifying the column names similar to a view name.
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query, PlanExprColSeq columns, String qualifierName);
    /**
     * Supports document matching and relevance by constructing rows with the document fragment id and
     * columns for relevance factors.  Typically, the plan will join the rows on the document fragment id
     * with the content of documents or with rows, triples, or lexicons indexed on the documents (where
     * the lexicons include range indexes, the document URI lexicon, and the collection lexicon).
     * <p>The documents can be ordered by the score or other relevance factors and limited for the
     * most relevant documents.</p>
     * <p>The list of possible columns to project consists of fragmentId, confidence, fitness,
     * quality, and score. The columns other than the fragmentId provide the same relevance factors
     * as the <a href="https://docs.marklogic.com/cts.confidence">cts.confidence()</a>,
     * <a href="https://docs.marklogic.com/cts.fitness">cts.fitness()</a>,
     * <a href="https://docs.marklogic.com/cts.quality">cts.quality()</a>,
     * and <a href="https://docs.marklogic.com/cts.score">cts.score()</a> functions
     * on the server. Pass {@link PlanBuilder#as(String, ServerExpression)} to rename a column.
     * The default columns are fragmentId and score.
     * </p>
     * @param query  The cts.query expression for matching the documents.
     * @param columns  The columns to project for the documents. See {@link PlanBuilder#colSeq(String...)}
     * @param qualifierName Specifies a name for qualifying the column names similar to a view name.
     * @param options  Specifies how to calculate the score for the matching documents. See {@link PlanBuilder#searchOptions()}
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query, PlanExprColSeq columns, XsStringVal qualifierName, PlanSearchOptions options);

    /**
    * Constructs a literal row set as in the SQL VALUES or SPARQL VALUES statements.
    * @param rows  This parameter provides any number of objects in which the key is a column name string identifying the column and the value is a literal with the value of the column.
    * @return  an AccessPlan object
    */
    PlanBuilder.AccessPlan fromLiterals(@SuppressWarnings("unchecked") Map<String,Object>... rows);
    /**
     * Constructs a literal row set as in the SQL VALUES or SPARQL VALUES statements. When specifying rows with arrays, values are mapped to column names by position.
     * @param rows  This parameter is either an array of object literals or sem:binding objects in which the key is a column name string identifying the column and the value is a literal with the value of the column, or this parameter is an object with a columnNames key having a value of an array of column names and a rowValues key having a value of an array of arrays with literal values.
     * @param qualifierName  Specifies a name for qualifying the column names similar to a view name
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromLiterals(Map<String,Object>[] rows, String qualifierName);
    /**
     * Constructs a literal row set as in the SQL VALUES or SPARQL VALUES statements. When specifying rows with arrays, values are mapped to column names by position.
     * @param rows  This parameter is either an array of object literals or sem:binding objects in which the key is a column name string identifying the column and the value is a literal with the value of the column, or this parameter is an object with a columnNames key having a value of an array of column names and a rowValues key having a value of an array of arrays with literal values.
     * @param qualifierName  Specifies a name for qualifying the column names similar to a view name
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromLiterals(Map<String,Object>[] rows, XsStringVal qualifierName);
    /**
     * Construct an {@code AccessPlan} based on the URIs returned by the given query.
     * @param querydef a CTS query
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromDocUris(CtsQueryExpr querydef);
    /**
     * Construct an {@code AccessPlan} based on the URIs returned by the given query.
     * @param querydef a CTS query
     * @param qualifierName  Optional name for qualifying the column names similar to a view name
     * @return  an AccessPlan object
     */
    PlanBuilder.AccessPlan fromDocUris(CtsQueryExpr querydef, String qualifierName);
    /**
     * Convenience method for constructing an {@code AccessPlan} based on a given set of URIs.
     *
     * @param uris one or more URIs to pass into a {@code cts.documentQuery}
     * @return an AccessPlanObject
     */
    PlanBuilder.AccessPlan fromDocUris(String... uris);
    /**
     * This function constructs a JSON object with the specified properties. The object can be used as the value of a column in a row or passed to a builtin function.
     * <p>
     * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-object" target="mlserverdoc">op:json-object</a>
     * @param property  The properties to be used to contruct the object. This is constructed by the <a>op:prop</a> function.
     * @return  a ObjectNodeExpr expression
     */
    ServerExpression jsonObject(PlanJsonProperty... property);
    /**
     * This function constructs a JSON array during row processing. The array can be used as the value of a column in a row or passed to a builtin expression function. The node is constructed during processing of the plan, rather than when building the plan.
     * <p>
     * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-array" target="mlserverdoc">op:json-array</a>
     * @param property  The JSON nodes for the array.
     * @return  a ArrayNodeExpr expression
     */
    ServerExpression jsonArray(ServerExpression... property);

    /**
     * This function returns the specified value expression if the specified value expression is true. Otherwise, it returns null.
     * <p>
     * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:case" target="mlserverdoc">op:case</a>
     * @param cases  One or more when or else expressions.
     * @return  a ItemSeqExpr expression sequence
     */
    ServerExpression caseExpr(PlanCase... cases);

    /**
     * This function returns the specified value if none of the preceeding when() conditions are true.
     * @param value  The value expression to return
     * @return  a PlanCase object
     */
    PlanCase elseExpr(ServerExpression value);

    /**
     * This function concatenates the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the groupBy() function.
     * @param name  The name to be used for column with the concatenated values.
     * @param column  The name of the column with the values to be concatenated for the group.
     * @return  a PlanAggregateCol object
     */
    PlanAggregateCol groupConcat(String name, String column);
    /**
     * This function concatenates the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the groupBy() function.
     * @param name  The name to be used for column with the concatenated values.
     * @param column  The name of the column with the values to be concatenated for the group.
     * @return  a PlanAggregateCol object
     */
    PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column);
    /**
     * This function concatenates the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the groupBy() function.
     * @param name  The name to be used for column with the concatenated values.
     * @param column  The name of the column with the values to be concatenated for the group.
     * @param options  The options can take a values key with a distinct value to average the distinct values of the column. In addition to the values key, the options can take a separator key specifying a separator character. The value can be a string or placeholder parameter. See {@link PlanBuilder#groupConcatOptions(String, PlanValueOption)}
     * @return  a PlanAggregateCol object
     */
    PlanAggregateCol groupConcat(String name, String column, PlanGroupConcatOptionSeq options);
    /**
     * This function concatenates the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the groupBy() function.
     * @param name  The name to be used for column with the concatenated values.
     * @param column  The name of the column with the values to be concatenated for the group.
     * @param options  The options can take a values key with a distinct value to average the distinct values of the column. In addition to the values key, the options can take a separator key specifying a separator character. The value can be a string or placeholder parameter. See {@link PlanBuilder#groupConcatOptions(String, PlanValueOption)}
     * @return  a PlanAggregateCol object
     */
    PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column, PlanGroupConcatOptionSeq options);

    /**
     * Specifies options for aggregating the values of a column for the rows
     * belonging to each group by concatenating the values into a single string value.
     * @param separator  a string for separating the values
     * @return  a PlanGroupConcatOptionSeq object
     */
    PlanGroupConcatOptionSeq groupConcatOptions(String separator);
    /**
     * Specifies options for aggregating the values of a column for the rows
     * belonging to each group by concatenating the values into a single string value.
     * @param option  an option controlling whether to concatenate all values including duplicates or concatenate distinct values
     * @return  a PlanGroupConcatOptionSeq object
     */
    PlanGroupConcatOptionSeq groupConcatOptions(PlanValueOption option);
    /**
     * Specifies options for aggregating the values of a column for the rows
     * belonging to each group by concatenating the values into a single string value.
     * @param separator  a string for separating the values
     * @param option  an option controlling whether to concatenate all values including duplicates or concatenate distinct values
     * @return  a PlanGroupConcatOptionSeq object
     */
    PlanGroupConcatOptionSeq groupConcatOptions(String separator, PlanValueOption option);

    /**
     * Specifies the keys for a group when grouping a row set in multiple ways
     * with {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}.
     * Omit the keys to group over all rows.
     * @param keys the columns, if any, in the group
     * @return  a PlanGroup object defining the keys for a group
     */
    PlanGroup group(String... keys);
    /**
     * Specifies the keys for a sequence of groups that provide a rollup for a row set.
     * The rollup consists of each group formed by dropping key columns from right to left
     * including the empty group for all rows.
     * The group sequence for the rollup can be passed to
     * {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}
     * or {@link PlanBuilder.ModifyPlan#groupToArrays(PlanNamedGroupSeq, PlanAggregateColSeq)}.
     * @param keys the columns in the rollup
     * @return  a PlanGroupSeq object with the groups for the rollup
     */
    PlanGroupSeq rollup(String... keys);
    /**
     * Specifies the keys for a sequence of groups that provide a cube for a row set.
     * The cube consists of each group with a unique set of key columns
     * including the empty group for all rows.
     * The group sequence for the cube can be passed to
     * {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}
     * or {@link PlanBuilder.ModifyPlan#groupToArrays(PlanNamedGroupSeq, PlanAggregateColSeq)}.
     * @param keys the columns in the cube
     * @return  a PlanGroupSeq object with the groups for the cube
     */
    PlanGroupSeq cube(String... keys);
    /**
     * Provides the sequence of groups when grouping a row set in multiple ways
     * with {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}.
     * Each group must provide a unique set of key columns, but a key column can appear
     * in multiple groups
     * @param group  the groups to apply to the row set
     * @return  a PlanGroupSeq object with the groups of key columns
     */
    PlanGroupSeq groupSeq(PlanGroup... group);
    /**
     * Specifies the name for a named group over all rows when grouping a row set in multiple ways
     * with {@link PlanBuilder.ModifyPlan#groupToArrays(PlanNamedGroupSeq, PlanAggregateColSeq)}.
     * @param name the name of the array for the group
     * @return  a PlanNamedGroup object defining the name for a named group over all rows
     */
    PlanNamedGroup namedGroup(String name);
    /**
     * Specifies the name and keys for a named group when grouping a row set in multiple ways
     * with {@link PlanBuilder.ModifyPlan#groupToArrays(PlanNamedGroupSeq, PlanAggregateColSeq)}.
     * @param name the name of the array for the group
     * @param keys the columns, if any, in the group.  See {@link PlanBuilder#colSeq(String...)}
     * @return  a PlanNamedGroup object defining the name and keys for a named group
     */
    PlanNamedGroup namedGroup(String name, PlanExprColSeq keys);
    /**
     * Provides the sequence of named groups when grouping a row set in multiple ways
     * with {@link PlanBuilder.ModifyPlan#groupToArrays(PlanNamedGroupSeq, PlanAggregateColSeq)}.
     * Each group must provide a unique set of key columns, but a key column can appear
     * in multiple groups. Names are assigned to groups without a name; if supplied,
     * the group name must be unique.
     * @param namedGroup  the named groups to apply to the row set
     * @return a PlanNamedGroupSeq object with the named groups of key columns
     */
    PlanNamedGroupSeq namedGroupSeq(PlanNamedGroup... namedGroup);

    /**
     * Provides a search options object to configure the execution of the
     * {@link PlanBuilder#fromSearch(CtsQueryExpr, PlanExprColSeq, XsStringVal, PlanSearchOptions)}
     * accessor.  Use the fluent methods of the search options object
     * to set the configuration.
     * @return  the configuration object
     */
    PlanSearchOptions searchOptions();

    /**
     * Provides a sample-by option object to configure the execution of the
     * {@link PlanBuilder#sample(PlanColumn, PlanExprCol)}
     * modifier for sampling a view, triples, or lexicon index.  Use the fluent methods of the sample-by option object
     * to set the configuration.
     * @return  the configuration object
     */
    PlanSampleByOptions sampleByOptions();

    /**
     * Provides a SPARQL option object to configure the execution of the
     * {@link PlanBuilder#fromSparql(XsStringVal, XsStringVal, PlanSparqlOptions)}
     * accessor.  Use the fluent methods of the sample-by option object
     * to set the configuration.
     * @return  the configuration object
     */
    PlanSparqlOptions sparqlOptions();

    /**
     * Specifies a JavaScript or XQuery function installed on the server for use
     * in post-processing in a map() or reduce() operation.
     * @param functionName  the name of the function installed on the server
     * @param modulePath  the path on the server for the library module providing the function
     * @return  a PlanFunction object
     */
    PlanFunction resolveFunction(XsQNameVal functionName, String modulePath);

    /**
     * Collects a sequence of server expressions as a new server expression
     * for evaluation on the server.
     *
     * <a name="ml-server-expression-sequence"></a>
     * The collected server expressions can include values, the results produced
     * by executing callson the server, and other sequences of server expressions.
     * The individual items in another sequence of server expressions become items
     * in the new sequence.
     *
     * The {@link XsValue} class provides methods for constructing sequences
     * of literals values such as {@link XsValue#dateSeq(String...)}. These
     * sequences can be passed to any parameter with a ServerExpression type
     * as well as to the seq() function.
     *
     * @param expression  one or more server expressions
     * @return a server expression representing a sequence of server expressions
     * @see XsValue#doubleSeq(double...)
     * @see XsValue#intSeq(int...)
     * @see XsValue#stringSeq(String...)
     */
    ServerExpression seq(ServerExpression... expression);

    /**
     * Build a new column identifier; the type will default to "none" and nullable will default to "false".
     *
     * @param column name of the column
     * @return a new column identifier
     */
    PlanRowColTypes colType(String column);

    /**
     * Build a new column identifier; nullable will default to "false".
     *
     * @param column name of the column
     * @param type type of the column, e.g. "string"
     * @return a new column identifier
     */
    PlanRowColTypes colType(String column, String type);

    /**
     * Build a new column identifier.
     *
     * @param column name of the column
     * @param type type of the column, e.g. "string"
     * @param nullable whether a column value is required
     * @return a new column identifier
     */
    PlanRowColTypes colType(String column, String type, Boolean nullable);

    /**
     * Build a sequence of column identifiers that can be used with {@code fromParam}.
     *
     * @param colTypes the column identifiers to associate with the plan
     * @return a sequence of column identifiers
     */
    PlanRowColTypesSeq colTypes(PlanRowColTypes... colTypes);

    /**
     * Construct a mapping of document descriptor column names to columns in the plan. The available set of
     * document descriptor names are: uri, doc, collections, permissions, metadata, quality, and temporalCollection.
     * Use this when mapping to non-standard column names.
     *
     * @param descriptorColumnMapping contains the mapping for column names to String values.
     * @return a new {@code PlanDocColsIdentifier}
     */
    PlanDocColsIdentifier docCols(Map<String, PlanColumn> descriptorColumnMapping);

    /**
     * Build a single doc descriptor that can be used with {@code fromDocDescriptors}.
     *
     * @param writeOp contains the inputs for the doc descriptor
     * @return a doc descriptor
     */
    PlanDocDescriptor docDescriptor(DocumentWriteOperation writeOp);

    /**
     * Build a sequence of doc descriptors that can be used with {@code fromDocDescriptors}.
     *
     * @param writeSet each {@code DocumentWriteOperation} in this will be converted into a doc descriptor
     * @return a sequence of doc descriptors
     */
    PlanDocDescriptorSeq docDescriptors(DocumentWriteSet writeSet);

    /**
     * Build a transform definition for use with {@code transformDoc}.
     *
     * @param path the path (URI) of either a *.mjs or *.xslt module in a modules database
     * @return a new {@code TransformDef}
     */
    TransformDef transformDef(String path);

    /**
     * Build a schema definition for use with {@code validateDoc}.
     * @param kind the kind of schema - jsonSchema, xmlSchema or schematron.
     * @return a new {@code SchemaDefExpr}
     */
    SchemaDefExpr schemaDefinition(String kind);
    /**
     * Convenience method for constructing a permission that can then be used e.g. with {@code jsonArray} when binding
     * an array of permissions to a column.
     *
     * @param roleName name of the role for the permission
     * @param capability lower-cased capability; e.g. "read", "update", "execute"
     * @return a new {@code ServerExpression}
     */
    ServerExpression permission(String roleName, String capability);

    /**
     * Defines base methods for Plan. This interface is an implementation detail.
     * Use Plan as the type for instances of Plan.
     */
    interface PlanBase {
        /**
         * Specifies a boolean primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a boolean primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, boolean literal);
        /**
         * Specifies a boolean primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a boolean primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, boolean literal);
        /**
         * Specifies a byte primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a byte primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, byte    literal);
        /**
         * Specifies a byte primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a byte primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, byte    literal);
        /**
         * Specifies a double primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a double primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, double  literal);
        /**
         * Specifies a double primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a double primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, double  literal);
        /**
         * Specifies a float primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a float primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, float   literal);
        /**
         * Specifies a float primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a float primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, float   literal);
        /**
         * Specifies an int primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   an int primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, int     literal);
        /**
         * Specifies an int primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   an int primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, int     literal);
        /**
         * Specifies a long primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a long primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, long    literal);
        /**
         * Specifies a long primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a long primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, long    literal);
        /**
         * Specifies a short primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a short primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, short   literal);
        /**
         * Specifies a short primitive value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a short primitive value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, short   literal);
        /**
         * Specifies a string literal value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param paramName  the name of a placeholder parameter
         * @param literal   a string literal value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String paramName, String  literal);
        /**
         * Specifies a string literal value to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param  a placeholder parameter as constructed by the param() method
         * @param literal   a string literal value to replace the parameter
         * @return  a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, String  literal);
        /**
         * Specifies a set of documents to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param the name of a placeholder parameter
         * @param writeSet the set of documents to bind; the URI, content, and metadata in each document will be
         *                 honored except for the properties fragment config in the metadata
         * @return a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String param, DocumentWriteSet writeSet);
        /**
         * Specifies a set of documents to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param a placeholder parameter as constructed by the param() method
         * @param writeSet the set of documents to bind; the URI, content, and metadata in each document will be
         *                 honored except for the properties fragment config in the metadata
         * @return a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, DocumentWriteSet writeSet);
        /**
         * Specifies a content handle to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param the name of a placeholder parameter
         * @param content the content to replace the parameter
         * @return a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String param, AbstractWriteHandle content);
        /**
         * Specifies a content handle to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param the name of a placeholder parameter
         * @param content the content to replace the parameter
         * @param columnAttachments optional (can be null) map that associates column names with maps of attachments,
         *                          which map filenames to content handles. For each column name in the map, the
         *                          expectation is that the associated column in the content object will contain values
         *                          matching the filenames in the map of filenames to content handles. When the plan is
         *                          executed, those filenames will then be replaced with the associated attachment
         *                          content objects.
         * @return a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(String param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments);
        /**
         * Specifies a content handle to replace a placeholder parameter during this
         * execution of the plan in all expressions in which the parameter appears.
         * <p>As when building a plan, binding a parameter constructs a new instance
         * of the plan with the binding instead of mutating the existing instance
         * of the plan.</p>
         * @param param a placeholder parameter as constructed by the param() method
         * @param content the content to replace the parameter
         * @param columnAttachments optional (can be null) map that associates column names with maps of attachments,
         *                          which map filenames to content handles. For each column name in the map, the
         *                          expectation is that the associated column in the content object will contain values
         *                          matching the filenames in the map of filenames to content handles. When the plan is
         *                          executed, those filenames will then be replaced with the associated attachment
         *                          content objects.
         * @return a new instance of the Plan object with the parameter binding
         */
        PlanBuilder.Plan bindParam(PlanParamExpr param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments);
    }
    /**
     * Defines base methods for AccessPlan. This interface is an implementation detail.
     * Use AccessPlan as the type for instances of AccessPlan.
     */
    interface AccessPlanBase {

    }
    /**
     * Defines base methods for ExportablePlan. This interface is an implementation detail.
     * Use ExportablePlan as the type for instances of ExportablePlan.
     */
    interface ExportablePlanBase {
        /**
         * This method exports the plan to an AST (Abstract Sytax Tree)
         * using the specified JSON handle.
         * @param handle  a handle for reading the AST as JSON
         * @param <T> the type of the handle for reading the AST as JSON
         * @return  the JSON handle populated with the AST
         */
        <T extends JSONReadHandle> T export(T handle);
        /**
         * This method exports the plan to an AST (Abstract Sytax Tree)
         * using the specified JSON handle.
         * <p>
         * The IO class must have been registered before creating the database client.
         * By default, the provided handles that implement
         * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
         * <p>
         * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
         * @param as    the IO class for reading the AST by means of JSON
         * @param <T> the type of the IO object for the AST
         * @return  an object of the IO class populated with the AST
         */
        <T> T exportAs(Class<T> as);
    }
    /**
     * Defines base methods for ModifyPlan. This interface is an implementation detail.
     * Use ModifyPlan as the type for instances of ModifyPlan.
     */
    interface ModifyPlanBase {
        /**
         * Constructs multiple groups over a single row set in a single pass.
         * <p>The keys parameter takes a {@link PlanBuilder#groupSeq(PlanGroup...)} sequence
         * providing the group specifications.
         * Use a {@link PlanBuilder#col(String)} value to specify a group with a single key
         * or a {@link PlanBuilder#group(String...)} value to specify a group with many keys
         * or (for all rows) no key.
         * The {@link PlanBuilder#rollup(String...)} and {@link PlanBuilder#cube(String...)}
         * functions provide conveniences to generate multiple groups over a list of column keys.
         * Each group must specify a unique set of grouping keys.</p>
         * @param keys  Specifies the grouping keys for each group.  See {@link PlanBuilder#groupSeq(PlanGroup...)}
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan groupByUnion(PlanGroupSeq keys);
        /**
         * Constructs multiple groups over a single row set in a single pass.
         * <p>The keys parameter takes a {@link PlanBuilder#groupSeq(PlanGroup...)} sequence
         * providing the group specifications.
         * Use a {@link PlanBuilder#col(String)} value to specify a group with a single key
         * or a {@link PlanBuilder#group(String...)} value to specify a group with many keys
         * or (for all rows) no key.
         * The {@link PlanBuilder#rollup(String...)} and {@link PlanBuilder#cube(String...)}
         * functions provide conveniences to generate multiple groups over a list of column keys.
         * Each group must specify a unique set of grouping keys.</p>
         * <p>The aggregates parameter takes a single aggregate or a sequence of aggregates
         * collected by {@link PlanBuilder#aggregateSeq(PlanAggregateCol...)}.
         * An aggregate can be a {@link PlanBuilder#col(String)} to sample the column or a
         * {@link PlanBuilder#arrayAggregate(String, String)},
         * {@link PlanBuilder#avg(String, String)},
         * {@link PlanBuilder#count(String, String)},
         * {@link PlanBuilder#groupConcat(String, String)},
         * {@link PlanBuilder#hasGroupKey(String, String)},
         * {@link PlanBuilder#max(String, String)},
         * {@link PlanBuilder#min(String, String)},
         * {@link PlanBuilder#sample(String, String)},
         * {@link PlanBuilder#sum(String, String)},
         * {@link PlanBuilder#uda(String, String, String, String, String)} aggregate function.</p>
         * @param keys  Specifies the grouping keys for each group.  See {@link PlanBuilder#groupSeq(PlanGroup...)}
         * @param aggregates  This parameter specifies either new columns for aggregate functions over the rows or columndefs that are constant. See {@link PlanBuilder#aggregateSeq(PlanAggregateCol...)}
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan groupByUnion(PlanGroupSeq keys, PlanAggregateColSeq aggregates);
        /**
         * Provides a convenience that executes a
         * {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}
         * to produce a single row with a separate array for each group.
         * The convenience constructs a {@link PlanBuilder#jsonObject(PlanJsonProperty...)}
         * for each row in each group with a property for each column belonging to the group and
         * aggregates the objects for each group with
         * a {@link PlanBuilder#arrayAggregate(PlanColumn, PlanExprCol)}.
         * @param keys  Specifies the name and grouping keys for each group.  See {@link PlanBuilder#namedGroupSeq(PlanNamedGroup...)}
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan groupToArrays(PlanNamedGroupSeq keys);
        /**
         * Provides a convenience that executes a
         * {@link PlanBuilder.ModifyPlan#groupByUnion(PlanGroupSeq, PlanAggregateColSeq)}
         * to produce a single row with a separate array for each group.
         * The convenience constructs a {@link PlanBuilder#jsonObject(PlanJsonProperty...)}
         * for each row in each group with a property for each column belonging to the group and
         * aggregates the objects for each group with
         * a {@link PlanBuilder#arrayAggregate(PlanColumn, PlanExprCol)}.
         * <p>The aggregates parameter takes a single aggregate or a sequence of aggregates
         * collected by {@link PlanBuilder#aggregateSeq(PlanAggregateCol...)}.
         * An aggregate can be a {@link PlanBuilder#col(String)} to sample the column or a
         * {@link PlanBuilder#arrayAggregate(String, String)},
         * {@link PlanBuilder#avg(String, String)},
         * {@link PlanBuilder#count(String, String)},
         * {@link PlanBuilder#groupConcat(String, String)},
         * {@link PlanBuilder#hasGroupKey(String, String)},
         * {@link PlanBuilder#max(String, String)},
         * {@link PlanBuilder#min(String, String)},
         * {@link PlanBuilder#sample(String, String)},
         * {@link PlanBuilder#sum(String, String)},
         * {@link PlanBuilder#uda(String, String, String, String, String)} aggregate function.</p>
         * @param keys  Specifies the name and grouping keys for each group.  See {@link PlanBuilder#namedGroupSeq(PlanNamedGroup...)}
         * @param aggregates  This parameter specifies either new columns for aggregate functions over the rows or columndefs that are constant. See {@link PlanBuilder#aggregateSeq(PlanAggregateCol...)}
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan groupToArrays(PlanNamedGroupSeq keys, PlanAggregateColSeq aggregates);
        /**
         * This method counts values for multiple grouping key columns.
         * @param keys  This parameter specifies the list of column keys or group keys for performing counts. For each
         *              column/group, the operation determines the unique values of that column/group and produces a
         *              separate count for the rows with that value.  A column can be named with a string or a column
         *              parameter function such as op:col or constructed from an expression with the op:as function.
         *              See {@link PlanBuilder#colSeq(String...)}.  A group can be a namedGroup or bucketGroup. see
         *              {@link PlanBuilder#namedGroupSeq(PlanNamedGroup...)} , {@link PlanBuilder#namedGroup(String, PlanExprColSeq)}
         *              and {@link PlanBuilder#bucketGroup(XsStringVal, PlanExprCol, XsAnyAtomicTypeSeqVal)}
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan facetBy(PlanNamedGroupSeq keys);
        /**
         * This method counts values for multiple grouping key columns.
         * @param keys  This parameter specifies the list of column keys or group keys for performing counts. For each
         *              column, the operation determines the unique values of that column/group and produces a separate
         *              count for the rows with that value.  A column can be named with a string or a column parameter
         *              function such as op:col or constructed from an expression with the op:as function.
         *              See {@link PlanBuilder#colSeq(String...)}. A group can be a namedGroup or bucketGroup. see
         *              {@link PlanBuilder#namedGroupSeq(PlanNamedGroup...)}, {@link PlanBuilder#namedGroup(String, PlanExprColSeq)}
         *              and {@link PlanBuilder#bucketGroup(XsStringVal, PlanExprCol, XsAnyAtomicTypeSeqVal)}.
         * @param countCol  Specifies what to count over the rows for each unique value of each key column.  By default,
         *                  the operation counts the rows. To count the values of a column instead, specify the column
         *                  to count with this parameter. To count documents, specify a fragment id column with
         *                  op:fragment-id-col.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan facetBy(PlanNamedGroupSeq keys, String countCol);
        /**
         * This method counts values for multiple grouping key columns.
         * @param keys  This parameter specifies the list of column keys or group keys for performing counts. For each
         *              column, the operation determines the unique values of that column/group and produces a separate
         *              count for the rows with that value.  A column can be named with a string or a column parameter
         *              function such as op:col or constructed from an expression with the op:as function.
         *              See {@link PlanBuilder#colSeq(String...)}, {@link PlanBuilder#namedGroup(String, PlanExprColSeq)}
         *              and {@link PlanBuilder#bucketGroup(XsStringVal, PlanExprCol, XsAnyAtomicTypeSeqVal)}.
         * @param countCol  Specifies what to count over the rows for each unique value of each key column.  By default,
         *                  the operation counts the rows. To count the values of a column instead, specify the column
         *                  to count with this parameter. To count documents, specify a fragment id column with
         *                  op:fragment-id-col.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan facetBy(PlanNamedGroupSeq keys, PlanExprCol countCol);

        /**
         * This method returns a subset of the rows in the result set by returning the specified number of rows.
         * @param length  The number of rows to return.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan limit(long length);
        /**
         * This method returns a subset of the rows in the result set by returning the specified number of rows.
         * @param length  The number of rows to return.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan limit(XsLongVal length);
        /**
         * This method returns a subset of the rows in the result set by returning the specified number of rows.
         * @param length  The number of rows to return.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan limit(PlanParamExpr length);
        /**
         * Acquires exclusive locks on the URI in the "uri" column of each row in the pipeline.
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan lockForUpdate();
        /**
         * Acquires exclusive locks on the URI in the given column of each row in the pipeline.
         * @param uriColumn the column containing URIs to be locked
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan lockForUpdate(PlanColumn uriColumn);
        /**
         * This method returns a subset of the rows in the result set by skipping the number of rows specified by start and returning the remaining rows up to the number specified by the prototype.limit method.
         * @param start  The number of rows to skip.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan offset(long start);
        /**
         * This method returns a subset of the rows in the result set by skipping the number of rows specified by start and returning the remaining rows up to the number specified by the prototype.limit method.
         * @param start  The number of rows to skip.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan offset(XsLongVal start);
        /**
         * This method returns a subset of the rows in the result set by skipping the number of rows specified by start and returning the remaining rows up to the number specified by the prototype.limit method.
         * @param start  The number of rows to skip.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan offset(PlanParamExpr start);
        /**
         * This method returns a subset of the rows in the result set by skipping the number of rows specified by start and returning the remaining rows up to the length limit. The offset for the next subset of rows is start + length.
         * @param start  The number of rows to skip.
         * @param length  The number of rows to return.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan offsetLimit(long start, long length);
        /**
         * This method returns a subset of the rows in the result set by skipping the number of rows specified by start and returning the remaining rows up to the length limit. The offset for the next subset of rows is start + length.
         * @param start  The number of rows to skip.
         * @param length  The number of rows to return.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan offsetLimit(XsLongVal start, XsLongVal length);
        /**
         * Removes (deletes) any document with a URI matching the value of the "uri" column in at least one row in the
         * pipeline.
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan remove();
        /**
         * Removes (deletes) any document with a URI matching the value of the given column in at least one row in the
         * pipeline.
         * @param uriColumn the column containing URIs to be removed
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan remove(PlanColumn uriColumn);
        /**
         * Removes (deletes) any temporal document with a URI matching the value of the given column in at least one
         * row in the pipeline. Results in each temporal document being marked as deleted.
         *
         * @param temporalCollection the name of the temporal collection containing URIs to remove
         * @param uriColumn the column containing URIs to be removed
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan remove(PlanColumn temporalCollection, PlanColumn uriColumn);
        /**
         * Applies the given transformation to the content in the given column in each row. A {@code TransformDef}
         * can be constructed via {@code PlanBuilder#transformDef(String)}.
         *
         * @param docColumn the column containing content to be transformed.
         * @param transformDef defines a transform for using with the {@code transformDoc} operator.
         * @return a ModifyPlan object
         */
        PlanBuilder.ModifyPlan transformDoc(PlanColumn docColumn, TransformDef transformDef);
        /**
         * This method restricts the row set to rows matched by the boolean expression. Use boolean composers such as op.and and op.or to combine multiple expressions.
         * @param condition  The boolean expression on which to match.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan where(ServerExpression condition);
        /**
         * This method restricts the row set to rows from the documents matched by the cts.query expression.
         * @param condition  The cts.query expression for matching the documents.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan where(CtsQueryExpr condition);
        /**
         * This method restricts the row set to rows matched by an SQL boolean expression. To construct the condition argument, pass the SQL boolean expression as a string to the sqlCondition() method.
         * @param condition  The SQL boolean expression on which to match as returned by sqlCondition().
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan where(PlanCondition condition);
        /**
         * This method adjusts the row set based on the triples for the sem.store definition,
         * restricting the triples to the documents matched by a cts.query expression and
         * expanding the triples based on inferencing rules.
         * @param condition  The sem.store for modifying the initial set of triples from which rows are projected.
         * @return  a ModifyPlan object
         */
        PlanBuilder.ModifyPlan where(SemStoreExpr condition);
    }
    /**
     * Defines base methods for PreparePlan. This interface is an implementation detail.
     * Use PreparePlan as the type for instances of PreparePlan.
     */
    interface PreparePlanBase {
    }
}
