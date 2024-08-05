/*
 * Copyright (c) 2024 MarkLogic Corporation
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

package com.marklogic.client.expression;

import com.marklogic.client.type.*;

import java.util.Map;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds the plan for a row pipeline to execute on the server.
 */
public abstract class PlanBuilder implements PlanBuilderBase {
  protected PlanBuilder(
    CtsExpr cts, FnExpr fn, GeoExpr geo, JsonExpr json, MapExpr map, MathExpr math, RdfExpr rdf, SemExpr sem, SpellExpr spell, SqlExpr sql, VecExpr vec, XdmpExpr xdmp, XsExpr xs, RdtExpr rdt
    ) {
    this.cts = cts;
     this.fn = fn;
     this.geo = geo;
     this.json = json;
     this.map = map;
     this.math = math;
     this.rdf = rdf;
     this.sem = sem;
     this.spell = spell;
     this.sql = sql;
     this.vec = vec;
     this.xdmp = xdmp;
     this.xs = xs;
     this.rdt = rdt;

  }
/**
  * Builds expressions with cts server functions.
  */
  public final CtsExpr cts;
 /**
  * Builds expressions with fn server functions.
  */
  public final FnExpr fn;
 /**
  * Builds expressions with geo server functions.
  */
  public final GeoExpr geo;
 /**
  * Builds expressions with json server functions.
  */
  public final JsonExpr json;
 /**
  * Builds expressions with map server functions.
  */
  public final MapExpr map;
 /**
  * Builds expressions with math server functions.
  */
  public final MathExpr math;
 /**
  * Builds expressions with rdf server functions.
  */
  public final RdfExpr rdf;
 /**
  * Builds expressions with sem server functions.
  */
  public final SemExpr sem;
 /**
  * Builds expressions with spell server functions.
  */
  public final SpellExpr spell;
 /**
  * Builds expressions with sql server functions.
  */
  public final SqlExpr sql;
 /**
  * Builds expressions with vec server functions.
  */
  public final VecExpr vec;
 /**
  * Builds expressions with xdmp server functions.
  */
  public final XdmpExpr xdmp;
 /**
  * Builds expressions with xs server functions.
  */
  public final XsExpr xs;
 /**
  * Builds expressions with ordt server functions.
  */
  public final RdtExpr rdt;


  /**
  * This function returns the sum of the specified numeric expressions. In expressions, the call should pass the result from an op:col function to identify a column.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:add" target="mlserverdoc">op:add</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression add(ServerExpression... left);
  /**
  * This function returns true if the specified expressions all return true. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:and" target="mlserverdoc">op:and</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression and(ServerExpression... left);
  /**
  * This function divides the left numericExpression by the right numericExpression and returns the value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:divide" target="mlserverdoc">op:divide</a> server function.
  * @param left  The left numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression divide(ServerExpression left, ServerExpression right);
  /**
  * This function takes two or more expressions and returns true if all of the expressions return the same value. Otherwise, it returns false. The expressions can include calls to the op:col function to get the value of a column.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:eq" target="mlserverdoc">op:eq</a> server function.
  * @param operand  Two or more expressions.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression eq(ServerExpression... operand);
  /**
  * This function returns true if the value of the left expression is greater than or equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:ge" target="mlserverdoc">op:ge</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression ge(ServerExpression left, ServerExpression right);
  /**
  * This function returns true if the value of the left expression is greater than the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:gt" target="mlserverdoc">op:gt</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression gt(ServerExpression left, ServerExpression right);
  /**
  * This function returns true if a test expression evaluates to the same value as any of a list of candidate expressions. Otherwise, it returns false. The expressions can include calls to the op:col function to get the value of a column.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:in" target="mlserverdoc">op:in</a> server function.
  * @param value  The expression providing the value to test.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param anyOf  One or more expressions providing the candidate values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression in(ServerExpression value, ServerExpression anyOf);
  /**
  * This function tests whether the value of an expression is null in the row where the expression might be as simple as a column identified by op:col.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:is-defined" target="mlserverdoc">op:is-defined</a> server function.
  * @param operand  A boolean expression, such as op:eq or op:not, that might be null.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression isDefined(ServerExpression operand);
  /**
  * This function returns true if the value of the left expression is less than or equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:le" target="mlserverdoc">op:le</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression le(ServerExpression left, ServerExpression right);
  /**
  * This function returns true if the value of the left expression is less than the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:lt" target="mlserverdoc">op:lt</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression lt(ServerExpression left, ServerExpression right);
  /**
  * This function multiplies the left numericExpression by the right numericExpression and returns the value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:multiply" target="mlserverdoc">op:multiply</a> server function.
  * @param left  The left numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression multiply(ServerExpression... left);
  /**
  * This function returns true if the value of the left expression is not equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:ne" target="mlserverdoc">op:ne</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression ne(ServerExpression left, ServerExpression right);
  /**
  * This function returns true if neither of the specified boolean expressions return true. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:not" target="mlserverdoc">op:not</a> server function.
  * @param operand  Exactly one boolean expression, such as op:and or op:or, or op:is-defined.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression not(ServerExpression operand);
  /**
  * This function returns true if the specified expressions all return true. Otherwise, it returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:or" target="mlserverdoc">op:or</a> server function.
  * @param left  The left value expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public abstract ServerExpression or(ServerExpression... left);
  /**
  * This function subtracts the right numericExpression from the left numericExpression and returns the value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:subtract" target="mlserverdoc">op:subtract</a> server function.
  * @param left  The left numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param right  The right numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression subtract(ServerExpression left, ServerExpression right);
  /**
  * Create a patch builder which can be used to chain patch operations.
  * @param contextPath  The context path to patch.
  * @return  a PatchBuilder object
  */
  public abstract PatchBuilder patchBuilder(String contextPath);
  /**
  * Create a patch builder which can be used to chain patch operations.
  * @param contextPath  The context path to patch.
  * @return  a PatchBuilder object
  */
  public abstract PatchBuilder patchBuilder(XsStringVal contextPath);
  /**
  * Create a patch builder which can be used to chain patch operations.
  * @param contextPath  The context path to patch.
  * @param namespaces  Namespaces prefix (key) and uri (value).
  * @return  a PatchBuilder object
  */
  public abstract PatchBuilder patchBuilder(String contextPath, Map<String,String> namespaces);
  /**
  * Create a patch builder which can be used to chain patch operations.
  * @param contextPath  The context path to patch.
  * @param namespaces  Namespaces prefix (key) and uri (value).
  * @return  a PatchBuilder object
  */
  public abstract PatchBuilder patchBuilder(XsStringVal contextPath, Map<String,String> namespaces);
  /**
  * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The op:result function throws in an error if the binding parameter does not specify a literal value for the parameter.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param</a> server function.
  * @param name  The name of the parameter.
  * @return  a <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param()</a> server expression
  */
  public abstract PlanParamExpr param(String name);
  /**
  * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The op:result function throws in an error if the binding parameter does not specify a literal value for the parameter.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param</a> server function.
  * @param name  The name of the parameter.
  * @return  a <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param()</a> server expression
  */
  public abstract PlanParamExpr param(XsStringVal name);
  /**
  * Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist).
  * @param column  the column  value.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(String column);
  /**
  * Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist).
  * @param column  the column  value.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(XsStringVal column);
  /**
  * Unambiguously identifies a column with the schema name, view name, and column name. Useful only for columns provided by a view.
  * @param schema  The name of the schema.
  * @param view  The name of the view.
  * @param column  The name of the column.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn schemaCol(String schema, String view, String column);
  /**
  * Unambiguously identifies a column with the schema name, view name, and column name. Useful only for columns provided by a view.
  * @param schema  The name of the schema.
  * @param view  The name of the view.
  * @param column  The name of the column.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column);
  /**
  * Identifies a column where the combination of view and column name is unique. Identifying the schema isn't necessary (and it might not exist).
  * @param view  The name of the view.
  * @param column  The name of the column.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn viewCol(String view, String column);
  /**
  * Identifies a column where the combination of view and column name is unique. Identifying the schema isn't necessary (and it might not exist).
  * @param view  The name of the view.
  * @param column  The name of the column.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn viewCol(XsStringVal view, XsStringVal column);
  /**
  * Specifies a name for adding a fragment id column to the row set identifying the source documents for the rows from a view, lexicons or triples. The only use for the fragment id is joining other rows from the same document, the document uri, or the document content. The fragment id is only useful during execution of the query and not after.
  * @param column  The name of the fragment ID column.
  * @return  a PlanSystemColumn object
  */
  public abstract PlanSystemColumn fragmentIdCol(String column);
  /**
  * Specifies a name for adding a fragment id column to the row set identifying the source documents for the rows from a view, lexicons or triples. The only use for the fragment id is joining other rows from the same document, the document uri, or the document content. The fragment id is only useful during execution of the query and not after.
  * @param column  The name of the fragment ID column.
  * @return  a PlanSystemColumn object
  */
  public abstract PlanSystemColumn fragmentIdCol(XsStringVal column);
  /**
  * Identifies the graph for a triple providing one or more columns for a row. You pass the graph column as a system column parameter to the op:pattern function.
  * @param column  The name to use for the graph column.
  * @return  a PlanSystemColumn object
  */
  public abstract PlanSystemColumn graphCol(String column);
  /**
  * Identifies the graph for a triple providing one or more columns for a row. You pass the graph column as a system column parameter to the op:pattern function.
  * @param column  The name to use for the graph column.
  * @return  a PlanSystemColumn object
  */
  public abstract PlanSystemColumn graphCol(XsStringVal column);
  /**
  * This function defines a column by assigning the value of an expression over the rows in the row set.
  * @param column  The name of the column to be defined. This can be either a string or the return value from op:col, op:view-col, or op:schema-col. See {@link PlanBuilder#col(XsStringVal)}
  * @param expression  The expression used to define the value the column.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a PlanExprCol object
  */
  public abstract PlanExprCol as(String column, ServerExpression expression);
  /**
  * This function defines a column by assigning the value of an expression over the rows in the row set.
  * @param column  The name of the column to be defined. This can be either a string or the return value from op:col, op:view-col, or op:schema-col. See {@link PlanBuilder#col(XsStringVal)}
  * @param expression  The expression used to define the value the column.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a PlanExprCol object
  */
  public abstract PlanExprCol as(PlanColumn column, ServerExpression expression);
  /**
  * Constructs a sequence from multiple col values to pass as a parameter to an operation.
  * @param col  the col values for the sequence See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanExprColSeq object sequence
  */
  public abstract PlanExprColSeq colSeq(String... col);
  /**
  * Constructs a sequence from multiple col values to pass as a parameter to an operation.
  * @param col  the col values for the sequence See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanExprColSeq object sequence
  */
  public abstract PlanExprColSeq colSeq(PlanExprCol... col);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(String schema, String view);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(String schema, String view, String qualifierName);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param sysCols  An optional named fragment id column returned by op:fragment-id-col. One use case for fragment ids is in joins with lexicons or document content.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols);
  /**
  * This function reads a row set from a configured view over TDE-indexed rows or a predefined view over range indexes.
  * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
  * @param view  The name identifying a configured template or range view for rows projected from documents.
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param sysCols  An optional named fragment id column returned by op:fragment-id-col. One use case for fragment ids is in joins with lexicons or document content.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols);
  /**
  * This function factory returns a new function that takes a name parameter and returns a sem:iri, prepending the specified base URI onto the name.
  * @param base  The base URI to be prepended to the name.
  * @return  a PlanPrefixer object
  */
  public abstract PlanPrefixer prefixer(String base);
  /**
  * This function factory returns a new function that takes a name parameter and returns a sem:iri, prepending the specified base URI onto the name.
  * @param base  The base URI to be prepended to the name.
  * @return  a PlanPrefixer object
  */
  public abstract PlanPrefixer prefixer(XsStringVal base);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePattern... patterns);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup which can take an on|off value to enable or disable deduplication. Deduplication is off by default.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris, PlanTripleOption option);
  /**
  * Reads rows by matching patterns in the triple index.
  * @param patterns  One or more pattern definitions returned by the op:pattern function. See {@link PlanBuilder#patternSeq(PlanTriplePattern...)}
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup which can take an on|off value to enable or disable deduplication. Deduplication is off by default.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris, PlanTripleOption option);
  /**
  * This function builds the parameters for the op:from-triples function. The result is passed to op:from-triples to project rows from the graph of triples. The columns in a pattern become the columns of the row. The literals in a pattern are used to match triples. You should specify at least one literal in each pattern, usually the predicate. Where a column appears in more than one pattern, the matched triples are joined to form the row. You can specify optional triples with a op:join-left-outer with a separate op:from-triples.
  * @param subjects  One column or one or more literal values, such as the literal returned by a sem:iri call. See {@link PlanBuilder#subjectSeq(PlanTriplePosition...)}
  * @param predicates  One column or one or more literal values, such as the literal returned by a sem.iri call. See {@link PlanBuilder#predicateSeq(PlanTriplePosition...)}
  * @param objects  One column or one or more literal values, such as the literal returned by a sem:iri call. See {@link PlanBuilder#objectSeq(PlanTriplePosition...)}
  * @return  a PlanTriplePattern object
  */
  public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects);
  /**
  * This function builds the parameters for the op:from-triples function. The result is passed to op:from-triples to project rows from the graph of triples. The columns in a pattern become the columns of the row. The literals in a pattern are used to match triples. You should specify at least one literal in each pattern, usually the predicate. Where a column appears in more than one pattern, the matched triples are joined to form the row. You can specify optional triples with a op:join-left-outer with a separate op:from-triples.
  * @param subjects  One column or one or more literal values, such as the literal returned by a sem:iri call. See {@link PlanBuilder#subjectSeq(PlanTriplePosition...)}
  * @param predicates  One column or one or more literal values, such as the literal returned by a sem.iri call. See {@link PlanBuilder#predicateSeq(PlanTriplePosition...)}
  * @param objects  One column or one or more literal values, such as the literal returned by a sem:iri call. See {@link PlanBuilder#objectSeq(PlanTriplePosition...)}
  * @param sysCols  Specifies the result of an op:fragment-id-col or op:graph-col function to add columns for the fragment id or graph iri.
  * @return  a PlanTriplePattern object
  */
  public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects, PlanSystemColumnSeq sysCols);
  /**
  * Constructs a sequence from multiple pattern values to pass as a parameter to an operation.
  * @param pattern  the pattern values for the sequence See {@link PlanBuilder#pattern(PlanTriplePositionSeq, PlanTriplePositionSeq, PlanTriplePositionSeq, PlanSystemColumnSeq)}
  * @return  a PlanTriplePatternSeq object sequence
  */
  public abstract PlanTriplePatternSeq patternSeq(PlanTriplePattern... pattern);
  /**
  * Constructs a sequence from multiple subject values to pass as a parameter to an operation.
  * @param subject  the subject values for the sequence
  * @return  a PlanTriplePositionSeq object sequence
  */
  public abstract PlanTriplePositionSeq subjectSeq(PlanTriplePosition... subject);
  /**
  * Constructs a sequence from multiple predicate values to pass as a parameter to an operation.
  * @param predicate  the predicate values for the sequence
  * @return  a PlanTriplePositionSeq object sequence
  */
  public abstract PlanTriplePositionSeq predicateSeq(PlanTriplePosition... predicate);
  /**
  * Constructs a sequence from multiple object values to pass as a parameter to an operation.
  * @param object  the object values for the sequence
  * @return  a PlanTriplePositionSeq object sequence
  */
  public abstract PlanTriplePositionSeq objectSeq(PlanTriplePosition... object);
  /**
  * This function dynamically constructs a view from range indexes or the uri or collection lexicons. This function will only return rows for documents where the first column has a value. The keys in the map specify the names of the columns and the values in the map provide cts:reference objects to specify the lexicon providing the values of the columns. Optic emits rows based on co-occurrence of lexicon values within the same document similar to cts:value-tuples If the cts:reference sets the nullable option to true, the column is optional in the row.
  * @param indexes  An object in which each key is a column name and each value specifies a cts:reference for a range index or other lexicon (especially the cts:uri-reference lexicon) with the column values.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes);
  /**
  * This function dynamically constructs a view from range indexes or the uri or collection lexicons. This function will only return rows for documents where the first column has a value. The keys in the map specify the names of the columns and the values in the map provide cts:reference objects to specify the lexicon providing the values of the columns. Optic emits rows based on co-occurrence of lexicon values within the same document similar to cts:value-tuples If the cts:reference sets the nullable option to true, the column is optional in the row.
  * @param indexes  An object in which each key is a column name and each value specifies a cts:reference for a range index or other lexicon (especially the cts:uri-reference lexicon) with the column values.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, lexicon rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName);
  /**
  * This function dynamically constructs a view from range indexes or the uri or collection lexicons. This function will only return rows for documents where the first column has a value. The keys in the map specify the names of the columns and the values in the map provide cts:reference objects to specify the lexicon providing the values of the columns. Optic emits rows based on co-occurrence of lexicon values within the same document similar to cts:value-tuples If the cts:reference sets the nullable option to true, the column is optional in the row.
  * @param indexes  An object in which each key is a column name and each value specifies a cts:reference for a range index or other lexicon (especially the cts:uri-reference lexicon) with the column values.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, lexicon rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName);
  /**
  * This function dynamically constructs a view from range indexes or the uri or collection lexicons. This function will only return rows for documents where the first column has a value. The keys in the map specify the names of the columns and the values in the map provide cts:reference objects to specify the lexicon providing the values of the columns. Optic emits rows based on co-occurrence of lexicon values within the same document similar to cts:value-tuples If the cts:reference sets the nullable option to true, the column is optional in the row.
  * @param indexes  An object in which each key is a column name and each value specifies a cts:reference for a range index or other lexicon (especially the cts:uri-reference lexicon) with the column values.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, lexicon rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param sysCols  An optional named fragment id column returned by the op:fragment-id-col function. The fragment id column can be used for joins.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName, PlanSystemColumn sysCols);
  /**
  * This function dynamically constructs a view from range indexes or the uri or collection lexicons. This function will only return rows for documents where the first column has a value. The keys in the map specify the names of the columns and the values in the map provide cts:reference objects to specify the lexicon providing the values of the columns. Optic emits rows based on co-occurrence of lexicon values within the same document similar to cts:value-tuples If the cts:reference sets the nullable option to true, the column is optional in the row.
  * @param indexes  An object in which each key is a column name and each value specifies a cts:reference for a range index or other lexicon (especially the cts:uri-reference lexicon) with the column values.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, lexicon rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param sysCols  An optional named fragment id column returned by the op:fragment-id-col function. The fragment id column can be used for joins.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName, PlanSystemColumn sysCols);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(String select);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(XsStringVal select);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. An "@" in front of the name specifies a parameter placeholder. A parameter placeholder in the SPARQL string must be bound to a parameter value in the result() call.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(String select, String qualifierName);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. An "@" in front of the name specifies a parameter placeholder. A parameter placeholder in the SPARQL string must be bound to a parameter value in the result() call.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(XsStringVal select, XsStringVal qualifierName);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. An "@" in front of the name specifies a parameter placeholder. A parameter placeholder in the SPARQL string must be bound to a parameter value in the result() call.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup and base. Option dedup can take an on|off value to enable or disable deduplication. Deduplication is off by default. Option base takes a string as the initial base IRI for the query.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(String select, String qualifierName, PlanSparqlOptions option);
  /**
  * This function dynamically constructs a row set based on a SPARQL SELECT query from triples.
  * @param select  A SPARQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. An "@" in front of the name specifies a parameter placeholder. A parameter placeholder in the SPARQL string must be bound to a parameter value in the result() call.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup and base. Option dedup can take an on|off value to enable or disable deduplication. Deduplication is off by default. Option base takes a string as the initial base IRI for the query.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSparql(XsStringVal select, XsStringVal qualifierName, PlanSparqlOptions option);
  /**
  * This function dynamically constructs a row set based on a SQL SELECT query from views.
  * @param select  A SQL SELECT query expressed as a string.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSql(String select);
  /**
  * This function dynamically constructs a row set based on a SQL SELECT query from views.
  * @param select  A SQL SELECT query expressed as a string.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSql(XsStringVal select);
  /**
  * This function dynamically constructs a row set based on a SQL SELECT query from views.
  * @param select  A SQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. Placeholder parameters in the SQL string may be bound in the result() call
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSql(String select, String qualifierName);
  /**
  * This function dynamically constructs a row set based on a SQL SELECT query from views.
  * @param select  A SQL SELECT query expressed as a string.
  * @param qualifierName  Specifies a name for qualifying the column names. Placeholder parameters in the SQL string may be bound in the result() call
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan fromSql(XsStringVal select, XsStringVal qualifierName);
  /**
  * This function constructs document rows with rows provided by a parameter.
  * @param paramName  The paramName parameter specifies the placeholder parameter supplying the rows.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @param rowColTypes  Describes the columns with a sequence of maps. It's a combinations of column, type and nullable. The 'column' is the column name, which is required. The 'type' is the optional type of the column, which can be an atomic type or the default of none. The 'nullable' is an optional boolean defaulting to false. If your rows contains only uri, doc, collections, metadata, permissions, quality and temporalCollection columns, you could simply use op:doc-col-types instead.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromParam(String paramName, String qualifier, PlanRowColTypesSeq rowColTypes);
  /**
  * This function constructs document rows with rows provided by a parameter.
  * @param paramName  The paramName parameter specifies the placeholder parameter supplying the rows.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @param rowColTypes  Describes the columns with a sequence of maps. It's a combinations of column, type and nullable. The 'column' is the column name, which is required. The 'type' is the optional type of the column, which can be an atomic type or the default of none. The 'nullable' is an optional boolean defaulting to false. If your rows contains only uri, doc, collections, metadata, permissions, quality and temporalCollection columns, you could simply use op:doc-col-types instead.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromParam(XsStringVal paramName, XsStringVal qualifier, PlanRowColTypesSeq rowColTypes);
  /**
  * This function constructs document rows from the docsDescriptors.
  * @param docDescriptor  A map of document descriptors. Each document descriptor describes a document. A document descriptor contains a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection. This is a simpler form of op:from-param.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromDocDescriptors(PlanDocDescriptor... docDescriptor);
  /**
  * This function constructs document rows from the docsDescriptors.
  * @param docDescriptor  A map of document descriptors. Each document descriptor describes a document. A document descriptor contains a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection. This is a simpler form of op:from-param.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor);
  /**
  * This function constructs document rows from the docsDescriptors.
  * @param docDescriptor  A map of document descriptors. Each document descriptor describes a document. A document descriptor contains a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection. This is a simpler form of op:from-param.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor, String qualifier);
  /**
  * This function constructs document rows from the docsDescriptors.
  * @param docDescriptor  A map of document descriptors. Each document descriptor describes a document. A document descriptor contains a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection. This is a simpler form of op:from-param.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor, XsStringVal qualifier);
  /**
  * This function returns a filter definition as input for a WHERE operation. As with a cts:query or sem:store, the filter definition cannot be used in an Optic Boolean expression but, instead, must be the only argument to the WHERE call. Add a separate WHERE call to filter based on an Optic Boolean expression. The condition must be a valid simple SQL Boolean expression expressed as a string.
  * @param expression  A boolean expression, such as op:eq or op:not, that might be null.
  * @return  a PlanCondition object
  */
  public abstract PlanCondition sqlCondition(String expression);
  /**
  * This function returns a filter definition as input for a WHERE operation. As with a cts:query or sem:store, the filter definition cannot be used in an Optic Boolean expression but, instead, must be the only argument to the WHERE call. Add a separate WHERE call to filter based on an Optic Boolean expression. The condition must be a valid simple SQL Boolean expression expressed as a string.
  * @param expression  A boolean expression, such as op:eq or op:not, that might be null.
  * @return  a PlanCondition object
  */
  public abstract PlanCondition sqlCondition(XsStringVal expression);
  /**
  * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the op:join-inner, op:join-left-outer, and op:join-full-outer, and functions.
  * @param left  The rows from the left view. See {@link PlanBuilder#col(XsStringVal)}
  * @param right  The row set from the right view. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanJoinKey object
  */
  public abstract PlanJoinKey on(String left, String right);
  /**
  * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the op:join-inner, op:join-left-outer, and op:join-full-outer, and functions.
  * @param left  The rows from the left view. See {@link PlanBuilder#col(XsStringVal)}
  * @param right  The row set from the right view. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanJoinKey object
  */
  public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
  /**
  * Constructs a sequence from multiple key values to pass as a parameter to an operation.
  * @param key  the key values for the sequence See {@link PlanBuilder#on(PlanExprCol, PlanExprCol)}
  * @return  a PlanJoinKeySeq object sequence
  */
  public abstract PlanJoinKeySeq joinKeySeq(PlanJoinKey... key);
  /**
  * This function specifies the grouping keys for a group as a list of zero or more columns. The result is used for building the first parameter for the op:group-by-union function.
  * @param keys  The columns (if any) to use as grouping keys. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a PlanGroup object
  */
  public abstract PlanGroup group(PlanExprColSeq keys);
  /**
  * This function specifies a list of grouping keys for a group and returns that group and larger groups (including all rows) formed by dropping columns from right to left. The result is used for building the first parameter for the op:group-by-union or op:group-to-arrays functions.
  * @param keys  The columns to use as grouping keys. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a PlanGroupSeq object sequence
  */
  public abstract PlanGroupSeq rollup(PlanExprColSeq keys);
  /**
  * This function specifies a list of grouping keys for a group and returns that group and every possible larger group (including all rows) formed from any subset of keys. The result is used for building the first parameter for the op:group-by-union or op:group-to-arrays functions.
  * @param keys  The columns to use as grouping keys. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a PlanGroupSeq object sequence
  */
  public abstract PlanGroupSeq cube(PlanExprColSeq keys);
  /**
  * This function specifies the grouping keys for a group as a named list of zero or more columns. The result is used for building the first parameter for the op:group-to-arrays function.
  * @param name  The name for the list of grouping keys.
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup namedGroup(String name);
  /**
  * This function specifies the grouping keys for a group as a named list of zero or more columns. The result is used for building the first parameter for the op:group-to-arrays function.
  * @param name  The name for the list of grouping keys.
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup namedGroup(XsStringVal name);
  /**
  * This function specifies the grouping keys for a group as a named list of zero or more columns. The result is used for building the first parameter for the op:group-to-arrays function.
  * @param name  The name for the list of grouping keys.
  * @param keys  The columns (if any) to use as grouping keys. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup namedGroup(String name, String keys);
  /**
  * This function specifies the grouping keys for a group as a named list of zero or more columns. The result is used for building the first parameter for the op:group-to-arrays function.
  * @param name  The name for the list of grouping keys.
  * @param keys  The columns (if any) to use as grouping keys. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup namedGroup(XsStringVal name, PlanExprColSeq keys);
  /**
  * This function can be used as a named group in functions op:group-to-arrays or op:facet-by. After grouping, the plan can also join a literal table with descriptive metadata based for each bucket number. Developers can handle special cases by taking the same approach as the convenience function and binding a new column on the return value of an sql:bucket expression on a numeric or datetime column to use as a grouping key.
  * @param name  The name of both the group and the new grouping key column with numbered buckets.
  * @param key  The identifier for the existing column with the values (typically numeric or datetime) to put into buckets. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @param boundaries  An ordered XQuery sequence of values that specify the boundaries between buckets. The values must have the same type as the existing column.
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup bucketGroup(String name, String key, String boundaries);
  /**
  * This function can be used as a named group in functions op:group-to-arrays or op:facet-by. After grouping, the plan can also join a literal table with descriptive metadata based for each bucket number. Developers can handle special cases by taking the same approach as the convenience function and binding a new column on the return value of an sql:bucket expression on a numeric or datetime column to use as a grouping key.
  * @param name  The name of both the group and the new grouping key column with numbered buckets.
  * @param key  The identifier for the existing column with the values (typically numeric or datetime) to put into buckets. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @param boundaries  An ordered XQuery sequence of values that specify the boundaries between buckets. The values must have the same type as the existing column.
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup bucketGroup(XsStringVal name, PlanExprCol key, XsAnyAtomicTypeSeqVal boundaries);
  /**
  * This function can be used as a named group in functions op:group-to-arrays or op:facet-by. After grouping, the plan can also join a literal table with descriptive metadata based for each bucket number. Developers can handle special cases by taking the same approach as the convenience function and binding a new column on the return value of an sql:bucket expression on a numeric or datetime column to use as a grouping key.
  * @param name  The name of both the group and the new grouping key column with numbered buckets.
  * @param key  The identifier for the existing column with the values (typically numeric or datetime) to put into buckets. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @param boundaries  An ordered XQuery sequence of values that specify the boundaries between buckets. The values must have the same type as the existing column.
  * @param collation  The collation to use when comparing strings as described in 'Collation URI Syntax' in the  Application Developer's Guide
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup bucketGroup(String name, String key, String boundaries, String collation);
  /**
  * This function can be used as a named group in functions op:group-to-arrays or op:facet-by. After grouping, the plan can also join a literal table with descriptive metadata based for each bucket number. Developers can handle special cases by taking the same approach as the convenience function and binding a new column on the return value of an sql:bucket expression on a numeric or datetime column to use as a grouping key.
  * @param name  The name of both the group and the new grouping key column with numbered buckets.
  * @param key  The identifier for the existing column with the values (typically numeric or datetime) to put into buckets. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @param boundaries  An ordered XQuery sequence of values that specify the boundaries between buckets. The values must have the same type as the existing column.
  * @param collation  The collation to use when comparing strings as described in 'Collation URI Syntax' in the  Application Developer's Guide
  * @return  a PlanNamedGroup object
  */
  public abstract PlanNamedGroup bucketGroup(XsStringVal name, PlanExprCol key, XsAnyAtomicTypeSeqVal boundaries, XsStringVal collation);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(String name, String column);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(String name, String column, PlanValueOption option);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(String name, String column);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(String name, String column, PlanValueOption option);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be aggregated. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be counted. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name, String column);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be counted. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be counted. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a 'distinct' value to count the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name, String column, PlanValueOption option);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The columns to be counted. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a 'distinct' value to count the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column, PlanValueOption option);
  public abstract PlanAggregateCol groupKey(String name, String column);
  public abstract PlanAggregateCol groupKey(PlanColumn name, PlanExprCol column);
  /**
  * This aggregate function adds a flag to a grouped row specifying whether a column acted as a grouping key for the row.
  * @param name  The name to be used for the aggregated flag column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to flag as a grouping key. The column can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol hasGroupKey(String name, String column);
  /**
  * This aggregate function adds a flag to a grouped row specifying whether a column acted as a grouping key for the row.
  * @param name  The name to be used for the aggregated flag column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column to flag as a grouping key. The column can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol hasGroupKey(PlanColumn name, PlanExprCol column);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(String name, String column);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(String name, String column, PlanValueOption option);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(String name, String column);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(String name, String column, PlanValueOption option);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sample(String name, String column);
  /**
  * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the value. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The group or row set. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sample(PlanColumn name, PlanExprCol column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(String name, String column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(String name, String column, PlanValueOption option);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to add. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(String name, String column);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to add. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to add. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(String name, String column, PlanValueOption option);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to add. See {@link PlanBuilder#col(XsStringVal)}
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(String name, String column, String module, String function);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @param arg  The options can take a values key with a distinct value to average the distinct values of the column and an arg key specifying an argument for the user-defined aggregate. The value can be a string or placeholder parameter.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(String name, String column, String module, String function, String arg);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column. See {@link PlanBuilder#col(XsStringVal)}
  * @param column  The column with the values to aggregate. See {@link PlanBuilder#col(XsStringVal)}
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @param arg  The options can take a values key with a distinct value to average the distinct values of the column and an arg key specifying an argument for the user-defined aggregate. The value can be a string or placeholder parameter.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg);
  /**
  * Constructs a sequence from multiple aggregate values to pass as a parameter to an operation.
  * @param aggregate  the aggregate values for the sequence
  * @return  a PlanAggregateColSeq object sequence
  */
  public abstract PlanAggregateColSeq aggregateSeq(PlanAggregateCol... aggregate);
  /**
  * This function sorts the rows by the values of the specified column in ascending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey asc(String column);
  /**
  * This function sorts the rows by the values of the specified column in ascending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey asc(PlanExprCol column);
  /**
  * This function sorts the rows by the values of the specified column in descending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey desc(String column);
  /**
  * This function sorts the rows by the values of the specified column in descending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey desc(PlanExprCol column);
  /**
  * Constructs a sequence from multiple key values to pass as a parameter to an operation.
  * @param key  the key values for the sequence See {@link PlanBuilder#desc(PlanExprCol)}
  * @return  a PlanSortKeySeq object sequence
  */
  public abstract PlanSortKeySeq sortKeySeq(PlanSortKey... key);
  /**
  * This function returns the remainder afer the division of the dividend and divisor expressions. For example, op:modulo(5, 2) returns 1.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:modulo" target="mlserverdoc">op:modulo</a> server function.
  * @param left  The dividend numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param right  The divisor numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression modulo(double left, double right);
  /**
  * This function returns the remainder afer the division of the dividend and divisor expressions. For example, op:modulo(5, 2) returns 1.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:modulo" target="mlserverdoc">op:modulo</a> server function.
  * @param left  The dividend numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param right  The divisor numeric expression.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public abstract ServerExpression modulo(ServerExpression left, ServerExpression right);
  /**
  * This function executes the specified expression if the specified condition is true for the row. Otherwise, the expression is not executed and the next 'when' test is checked or, if there is no next 'when' text, the otherwise expression for the op:case expression is executed.
  * @param condition  A boolean expression.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @param value  The value expression to return if the boolean expression is true.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a PlanCase object
  */
  public abstract PlanCase when(boolean condition, ServerExpression... value);
  /**
  * This function executes the specified expression if the specified condition is true for the row. Otherwise, the expression is not executed and the next 'when' test is checked or, if there is no next 'when' text, the otherwise expression for the op:case expression is executed.
  * @param condition  A boolean expression.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @param value  The value expression to return if the boolean expression is true.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a PlanCase object
  */
  public abstract PlanCase when(ServerExpression condition, ServerExpression... value);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a> server function.
  * @param column  The name of the column from which to extract the child nodes. See {@link PlanBuilder#col(XsStringVal)}
  * @param path  An XPath (specified as a string) to apply to each node.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public abstract ServerExpression xpath(String column, String path);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a> server function.
  * @param column  The name of the column from which to extract the child nodes. See {@link PlanBuilder#col(XsStringVal)}
  * @param path  An XPath (specified as a string) to apply to each node.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public abstract ServerExpression xpath(PlanColumn column, ServerExpression path);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a> server function.
  * @param column  The name of the column from which to extract the child nodes. See {@link PlanBuilder#col(XsStringVal)}
  * @param path  An XPath (specified as a string) to apply to each node.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param namespaceBindings  A map of namespace bindings. The keys should be namespace prefixes and the values should be namespace URIs. These namespace bindings will be added to the in-scope namespace bindings in the evaluation of the path.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public abstract ServerExpression xpath(String column, String path, PlanNamespaceBindingsSeq namespaceBindings);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a> server function.
  * @param column  The name of the column from which to extract the child nodes. See {@link PlanBuilder#col(XsStringVal)}
  * @param path  An XPath (specified as a string) to apply to each node.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param namespaceBindings  A map of namespace bindings. The keys should be namespace prefixes and the values should be namespace URIs. These namespace bindings will be added to the in-scope namespace bindings in the evaluation of the path.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public abstract ServerExpression xpath(PlanColumn column, ServerExpression path, PlanNamespaceBindingsSeq namespaceBindings);
  /**
  * This function constructs a JSON document with the root content, which must be exactly one JSON object or array node.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-document" target="mlserverdoc">op:json-document</a> server function.
  * @param root  The JSON object or array node used to construct the JSON document.  (of <a href="{@docRoot}/doc-files/types/json-root-node.html">json-root-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public abstract ServerExpression jsonDocument(ServerExpression root);
  /**
  * This function specifies the key expression and value content for a JSON property of a JSON object constructed by the op:json-object function.
  * @param key  The key expression. This must evaluate to a string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The value content. This must be exactly one JSON node expression.  (of <a href="{@docRoot}/doc-files/types/json-content-node.html">json-content-node</a>)
  * @return  a PlanJsonProperty object
  */
  public abstract PlanJsonProperty prop(String key, ServerExpression value);
  /**
  * This function specifies the key expression and value content for a JSON property of a JSON object constructed by the op:json-object function.
  * @param key  The key expression. This must evaluate to a string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The value content. This must be exactly one JSON node expression.  (of <a href="{@docRoot}/doc-files/types/json-content-node.html">json-content-node</a>)
  * @return  a PlanJsonProperty object
  */
  public abstract PlanJsonProperty prop(ServerExpression key, ServerExpression value);
  /**
  * This function constructs a JSON text node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-string" target="mlserverdoc">op:json-string</a> server function.
  * @param value  The value of the JSON text node.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/text-node.html">text-node</a> server data type
  */
  public abstract ServerExpression jsonString(String value);
  /**
  * This function constructs a JSON text node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-string" target="mlserverdoc">op:json-string</a> server function.
  * @param value  The value of the JSON text node.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/text-node.html">text-node</a> server data type
  */
  public abstract ServerExpression jsonString(ServerExpression value);
  /**
  * This function constructs a JSON number node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-number" target="mlserverdoc">op:json-number</a> server function.
  * @param value  The value of the JSON number node.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/number-node.html">number-node</a> server data type
  */
  public abstract ServerExpression jsonNumber(double value);
  /**
  * This function constructs a JSON number node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-number" target="mlserverdoc">op:json-number</a> server function.
  * @param value  The value of the JSON number node.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/number-node.html">number-node</a> server data type
  */
  public abstract ServerExpression jsonNumber(ServerExpression value);
  /**
  * This function constructs a JSON boolean node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-boolean" target="mlserverdoc">op:json-boolean</a> server function.
  * @param value  The value of the JSON boolean node.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/boolean-node.html">boolean-node</a> server data type
  */
  public abstract ServerExpression jsonBoolean(boolean value);
  /**
  * This function constructs a JSON boolean node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-boolean" target="mlserverdoc">op:json-boolean</a> server function.
  * @param value  The value of the JSON boolean node.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/boolean-node.html">boolean-node</a> server data type
  */
  public abstract ServerExpression jsonBoolean(ServerExpression value);
  /**
  * This function constructs a JSON null node.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:json-null" target="mlserverdoc">op:json-null</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/null-node.html">null-node</a> server data type
  */
  public abstract ServerExpression jsonNull();
  /**
  * This function constructs an XML document with the root content, which must be exactly one node.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-document" target="mlserverdoc">op:xml-document</a> server function.
  * @param root  The XML node used to construct the XML document.  (of <a href="{@docRoot}/doc-files/types/xml-root-node.html">xml-root-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public abstract ServerExpression xmlDocument(ServerExpression root);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(String name);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(ServerExpression name);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.  (of <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(String name, ServerExpression attributes);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.  (of <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(ServerExpression name, ServerExpression attributes);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.  (of <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a>)
  * @param content  A sequence or array of atomic values or an element, a comment from op:xml-comment, or processing instruction nodes from op:xml-pi.  (of <a href="{@docRoot}/doc-files/types/xml-content-node.html">xml-content-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(String name, ServerExpression attributes, ServerExpression... content);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a> server function.
  * @param name  The string or QName for the constructed element.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.  (of <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a>)
  * @param content  A sequence or array of atomic values or an element, a comment from op:xml-comment, or processing instruction nodes from op:xml-pi.  (of <a href="{@docRoot}/doc-files/types/xml-content-node.html">xml-content-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public abstract ServerExpression xmlElement(ServerExpression name, ServerExpression attributes, ServerExpression... content);
  /**
  * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-attribute" target="mlserverdoc">op:xml-attribute</a> server function.
  * @param name  The attribute name.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param value  The attribute value.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a> server data type
  */
  public abstract ServerExpression xmlAttribute(String name, String value);
  /**
  * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-attribute" target="mlserverdoc">op:xml-attribute</a> server function.
  * @param name  The attribute name.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @param value  The attribute value.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a> server data type
  */
  public abstract ServerExpression xmlAttribute(ServerExpression name, ServerExpression value);
  /**
  * This function constructs an XML text node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-text" target="mlserverdoc">op:xml-text</a> server function.
  * @param value  The value of the XML text node.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/text-node.html">text-node</a> server data type
  */
  public abstract ServerExpression xmlText(String value);
  /**
  * This function constructs an XML text node with the specified value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-text" target="mlserverdoc">op:xml-text</a> server function.
  * @param value  The value of the XML text node.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/text-node.html">text-node</a> server data type
  */
  public abstract ServerExpression xmlText(ServerExpression value);
  /**
  * This function constructs an XML comment with the atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-comment" target="mlserverdoc">op:xml-comment</a> server function.
  * @param content  The comment text.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/comment-node.html">comment-node</a> server data type
  */
  public abstract ServerExpression xmlComment(String content);
  /**
  * This function constructs an XML comment with the atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-comment" target="mlserverdoc">op:xml-comment</a> server function.
  * @param content  The comment text.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/comment-node.html">comment-node</a> server data type
  */
  public abstract ServerExpression xmlComment(ServerExpression content);
  /**
  * This function constructs an XML processing instruction with the atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-pi" target="mlserverdoc">op:xml-pi</a> server function.
  * @param name  The name of the processing instruction.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The value of the processing instruction.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/processing-instruction-node.html">processing-instruction-node</a> server data type
  */
  public abstract ServerExpression xmlPi(String name, String value);
  /**
  * This function constructs an XML processing instruction with the atomic value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-pi" target="mlserverdoc">op:xml-pi</a> server function.
  * @param name  The name of the processing instruction.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The value of the processing instruction.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/processing-instruction-node.html">processing-instruction-node</a> server data type
  */
  public abstract ServerExpression xmlPi(ServerExpression name, ServerExpression value);
  /**
  * Constructs a sequence from multiple attribute values to pass as a parameter to an operation.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/op:xml-attribute-seq" target="mlserverdoc">op:xml-attribute-seq</a> server function.
  * @param attribute  the attribute values for the sequence
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/attribute-node.html">attribute-node</a> server data type
  */
  public abstract ServerExpression xmlAttributeSeq(ServerExpression... attribute);
  /**
  * Specifies a JavaScript or XQuery function installed on the server for use in post-processing in a map() or reduce() operation.
  * @param functionName  the name of the function installed on the server
  * @param modulePath  the path on the server for the library module providing the function
  * @return  a PlanFunction object
  */
  public abstract PlanFunction resolveFunction(String functionName, String modulePath);
  /**
  * Specifies a JavaScript or XQuery function installed on the server for use in post-processing in a map() or reduce() operation.
  * @param functionName  the name of the function installed on the server
  * @param modulePath  the path on the server for the library module providing the function
  * @return  a PlanFunction object
  */
  public abstract PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath);
  /**
  * Constructs a document column identifier object for columns of uri, doc, collections, metadata, permissions, quality and temporalCollection. The document column identifier object can be passed to the op:join-doc-cols or op:write.
  * @return  a PlanDocColsIdentifier object
  */
  public abstract PlanDocColsIdentifier docCols();
  /**
  * Constructs a document column identifier object for columns of uri, doc, collections, metadata, permissions, quality and temporalCollection. The document column identifier object can be passed to the op:join-doc-cols or op:write.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @return  a PlanDocColsIdentifier object
  */
  public abstract PlanDocColsIdentifier docCols(String qualifier);
  /**
  * Constructs a document column identifier object for columns of uri, doc, collections, metadata, permissions, quality and temporalCollection. The document column identifier object can be passed to the op:join-doc-cols or op:write.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @return  a PlanDocColsIdentifier object
  */
  public abstract PlanDocColsIdentifier docCols(XsStringVal qualifier);
  /**
  * Constructs a document column identifier object for columns of uri, doc, collections, metadata, permissions, quality and temporalCollection. The document column identifier object can be passed to the op:join-doc-cols or op:write.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @param names  A sequence of columns names, a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection.
  * @return  a PlanDocColsIdentifier object
  */
  public abstract PlanDocColsIdentifier docCols(String qualifier, String names);
  /**
  * Constructs a document column identifier object for columns of uri, doc, collections, metadata, permissions, quality and temporalCollection. The document column identifier object can be passed to the op:join-doc-cols or op:write.
  * @param qualifier  Specifies a name for qualifying the column names.
  * @param names  A sequence of columns names, a combination of uri, doc, collections, metadata, permissions, quality and temporalCollection.
  * @return  a PlanDocColsIdentifier object
  */
  public abstract PlanDocColsIdentifier docCols(XsStringVal qualifier, XsStringSeqVal names);
  /**
  * Provides the 3rd parameter for op:from-param for row column types.
  * @return  a PlanRowColTypesSeq object sequence
  */
  public abstract PlanRowColTypesSeq docColTypes();
/**
 * Provides functions and operations in the access phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface AccessPlan extends ModifyPlan, PlanBuilderBase.AccessPlanBase {
/**
  * Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist).
  * @param column  the column  value.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(String column);
/**
  * Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist).
  * @param column  the column  value.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(XsStringVal column);
/**
  * This function samples rows from a view or from a pattern match on the triple index.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan sampleBy();
/**
  * This function samples rows from a view or from a pattern match on the triple index.
  * @param option  the option  value.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan sampleBy(PlanSampleByOptions option);
  }


/**
 * Provides functions and operations in the exportable phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface ExportablePlan extends Plan, PlanBuilderBase.ExportablePlanBase {

  }


/**
 * Provides functions and operations in the modify phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface ModifyPlan extends PreparePlan, PlanBuilderBase.ModifyPlanBase {
/**
  * This function adds new columns or modifies existing columns based on expressions while preserving existing unmodified columns in the row set.
  * @param columns  The op:as calls that specify the column name and the expression that constructs the column values. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan bind(PlanExprColSeq columns);
/**
  * This function is deprecated in favor of the bind() function and will not be supported in MarkLogic 11. This function adds a column based on an expression without altering the existing columns in the row set.
  * @param column  The name of the column to be defined. See {@link PlanBuilder#col(XsStringVal)}
  * @param expression  The expression that specifies the value the column in the row.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan bindAs(String column, ServerExpression expression);
/**
  * This function is deprecated in favor of the bind() function and will not be supported in MarkLogic 11. This function adds a column based on an expression without altering the existing columns in the row set.
  * @param column  The name of the column to be defined. See {@link PlanBuilder#col(XsStringVal)}
  * @param expression  The expression that specifies the value the column in the row.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan bindAs(PlanColumn column, ServerExpression expression);
/**
  * This method restricts the left row set to rows where a row with the same columns and values doesn't exist in the right row set.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan except(ModifyPlan right);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan existsJoin(ModifyPlan right);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan existsJoin(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition);
/**
  * This method collapses a group of rows into a single row.
  * @param keys  This parameter specifies the columns used to determine the groups. Rows with the same values in these columns are consolidated into a single group. The columns can be existing columns or new columns created by an expression specified with op:as. The rows produced by the group by operation include the key columns. Specify an empty sequence to create a single group for all of the rows in the row set. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan groupBy(PlanExprColSeq keys);
/**
  * This method collapses a group of rows into a single row.
  * @param keys  This parameter specifies the columns used to determine the groups. Rows with the same values in these columns are consolidated into a single group. The columns can be existing columns or new columns created by an expression specified with op:as. The rows produced by the group by operation include the key columns. Specify an empty sequence to create a single group for all of the rows in the row set. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @param aggregates  This parameter specifies either new columns for aggregate functions over the rows in the group or columndefs that are constant for the group. The aggregate library functions are listed below. See {@link PlanBuilder#aggregateSeq(PlanAggregateCol...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates);
/**
  * This method restricts the left row set to rows where a row with the same columns and values exists in the right row set.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan intersect(ModifyPlan right);
/**
  * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinCrossProduct(ModifyPlan right);
/**
  * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
  * @param right  The row set from the right view.
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition);
/**
  * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
  * @param right  The row set from the right view.
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinCrossProduct(ModifyPlan right, ServerExpression condition);
/**
  * This function specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows).
  * @param docCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param sourceCol  The document uri or fragment id value. This is either the output from op:fragment-id-col specifying a fragment id column or a document uri column. Joining on a fragment id is more efficient than joining on a uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDoc(String docCol, String sourceCol);
/**
  * This function specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows).
  * @param docCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param sourceCol  The document uri or fragment id value. This is either the output from op:fragment-id-col specifying a fragment id column or a document uri column. Joining on a fragment id is more efficient than joining on a uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol);
/**
  * This method adds an uri column and a document column to rows based on an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows). If the fragment id column is null in the row, the row is dropped from the rowset.
  * @param docCol  The document column to add to the rows. This can be a string or a column, op:col, op:view-col or op:schema-col, specifying the name of the new column that should have the document as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param uriCol  The uri column to add to the rows. This can be a string or a column, op:col, op:view-col or op:schema-col, specifying the name of the new column that should have the document uri as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param sourceCol  The document uri or fragment id value. This is either an op:fragment-id-col specifying a fragment id column or a document uri column as xs:string or as a column using op:col, op:view-col or op:schema-col. Joining on a fragment id is more efficient than joining on a uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocAndUri(String docCol, String uriCol, String sourceCol);
/**
  * This method adds an uri column and a document column to rows based on an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows). If the fragment id column is null in the row, the row is dropped from the rowset.
  * @param docCol  The document column to add to the rows. This can be a string or a column, op:col, op:view-col or op:schema-col, specifying the name of the new column that should have the document as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param uriCol  The uri column to add to the rows. This can be a string or a column, op:col, op:view-col or op:schema-col, specifying the name of the new column that should have the document uri as its value. See {@link PlanBuilder#col(XsStringVal)}
  * @param sourceCol  The document uri or fragment id value. This is either an op:fragment-id-col specifying a fragment id column or a document uri column as xs:string or as a column using op:col, op:view-col or op:schema-col. Joining on a fragment id is more efficient than joining on a uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocAndUri(PlanColumn docCol, PlanColumn uriCol, PlanColumn sourceCol);
/**
  * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an op:fragment-id-col specifying a fragment id column. If the fragment id column is null in the row, the row is dropped from the rowset.
  * @param uriCol  The document uri. This is the output from op:col('uri') that specifies a document uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @param fragmentIdCol  The document fragment id value. This is the output from op:fragment-id-col specifying a fragment id column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocUri(String uriCol, String fragmentIdCol);
/**
  * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an op:fragment-id-col specifying a fragment id column. If the fragment id column is null in the row, the row is dropped from the rowset.
  * @param uriCol  The document uri. This is the output from op:col('uri') that specifies a document uri column. See {@link PlanBuilder#col(XsStringVal)}
  * @param fragmentIdCol  The document fragment id value. This is the output from op:fragment-id-col specifying a fragment id column. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from the left row set.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from the left row set.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from the left row set.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from the left row set.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from the left row set.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from both the left and right row sets.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinFullOuter(ModifyPlan right);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from both the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from both the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from both the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method yields one output row set with the rows from an inner join as well as the other rows from both the left and right row sets.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan notExistsJoin(ModifyPlan right);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method is a filtering join that filters based on whether the join exists or not but doesn't add any columns.
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function. See {@link PlanBuilder#joinKeySeq(PlanJoinKey...)}
  * @param condition  A boolean expression that filters the join output rows.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition);
/**
  * This method sorts the row set by the specified order definition.
  * @param keys  The specified column or sortdef output from the op:asc or op:desc function. See {@link PlanBuilder#sortKeySeq(PlanSortKey...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan orderBy(PlanSortKeySeq keys);
/**
  * Add an error-handler to the Optic Pipeline to catch Optic Update runtime errors. The runtime errors are added in the errors column. If no error occurred the value of the error column is null. When added, the error-handler should be the last operator before op:result.
  * @param action  Valid options are: "fail" - stop processing and "continue" - add an error to the error column and continue processing.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan onError(String action);
/**
  * Add an error-handler to the Optic Pipeline to catch Optic Update runtime errors. The runtime errors are added in the errors column. If no error occurred the value of the error column is null. When added, the error-handler should be the last operator before op:result.
  * @param action  Valid options are: "fail" - stop processing and "continue" - add an error to the error column and continue processing.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan onError(XsStringVal action);
/**
  * Add an error-handler to the Optic Pipeline to catch Optic Update runtime errors. The runtime errors are added in the errors column. If no error occurred the value of the error column is null. When added, the error-handler should be the last operator before op:result.
  * @param action  Valid options are: "fail" - stop processing and "continue" - add an error to the error column and continue processing.
  * @param errorColumn  An optional error column which is not used in the plan. If this parameter is not passed in 'sys.errors' is used. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan onError(String action, String errorColumn);
/**
  * Add an error-handler to the Optic Pipeline to catch Optic Update runtime errors. The runtime errors are added in the errors column. If no error occurred the value of the error column is null. When added, the error-handler should be the last operator before op:result.
  * @param action  Valid options are: "fail" - stop processing and "continue" - add an error to the error column and continue processing.
  * @param errorColumn  An optional error column which is not used in the plan. If this parameter is not passed in 'sys.errors' is used. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan onError(XsStringVal action, PlanExprCol errorColumn);
/**
  * Builds a patch operation including a sequence of inserts, replaces, replace-inserts and deletes.
  * @param docColumn  The document column which need to be patched. See {@link PlanBuilder#col(XsStringVal)}
  * @param patchDef  The patch definition as op:patch-builder
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan patch(String docColumn, PatchBuilder patchDef);
/**
  * Builds a patch operation including a sequence of inserts, replaces, replace-inserts and deletes.
  * @param docColumn  The document column which need to be patched. See {@link PlanBuilder#col(XsStringVal)}
  * @param patchDef  The patch definition as op:patch-builder
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan patch(PlanExprCol docColumn, PatchBuilder patchDef);
/**
  * This method prepares the specified plan for execution as an optional final step before execution.
  * @param optimize  The optimization level, which can be 0, 1, or 2 (1 is mostly used).
  * @return  a PreparePlan object
  */
  public abstract PreparePlan prepare(int optimize);
/**
  * This method prepares the specified plan for execution as an optional final step before execution.
  * @param optimize  The optimization level, which can be 0, 1, or 2 (1 is mostly used).
  * @return  a PreparePlan object
  */
  public abstract PreparePlan prepare(XsIntVal optimize);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to project from the input rows. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprCol... columns);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to project from the input rows. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprColSeq columns);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to project from the input rows. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprColSeq columns, String qualifierName);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to project from the input rows. The columns can be named with a string or a column parameter function such as op:col or constructed from an expression with op:as. See {@link PlanBuilder#colSeq(PlanExprCol...)}
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprColSeq columns, XsStringVal qualifierName);
/**
  * This method yields all of the rows from the input row sets. Columns that are present only in some input row sets effectively have a null value in the rows from the other row sets.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan union(ModifyPlan right);
/**
  * This method removes duplicate rows from the row set.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan whereDistinct();
/**
  * Inserts or overwrites the documents identified by the uri column with the data supplied by the other document descriptor columns.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan write();
/**
  * Inserts or overwrites the documents identified by the uri column with the data supplied by the other document descriptor columns.
  * @param docCols  the docCols  value.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan write(PlanDocColsIdentifier docCols);
/**
  * This function populates the view with the uri, doc, collections, metadata, permissions, and / or quality document descriptor columns for database document values.
  * @param cols  The source column to join. This is either an op:fragment-id-col specifying a fragment id column or a op:col, op:view-col or op:schema-col that contains document uris. Joining on a fragment id is more efficient than joining on an uri column.
  * @param docIdCol  the docIdCol  value. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocCols(PlanDocColsIdentifier cols, String docIdCol);
/**
  * This function populates the view with the uri, doc, collections, metadata, permissions, and / or quality document descriptor columns for database document values.
  * @param cols  The source column to join. This is either an op:fragment-id-col specifying a fragment id column or a op:col, op:view-col or op:schema-col that contains document uris. Joining on a fragment id is more efficient than joining on an uri column.
  * @param docIdCol  the docIdCol  value. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocCols(PlanDocColsIdentifier cols, PlanColumn docIdCol);
/**
  * Validate the document based on a supplied schema. This schema needs to be stored in the schema database. Check appserver error log for validate errors.
  * @param validateDocCol  The required 'kind' key of the schemaDef map must be 'jsonSchema', 'schematron', or 'xmlSchema'. When 'kind' is 'jsonSchema' or 'schemtron' then a key 'schemaUri' is required. Key 'mode' takes 'strict', 'lax' or 'type' (refer to xdmp:validate). See {@link PlanBuilder#col(XsStringVal)}
  * @param schemaDef  the schemaDef  value.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan validateDoc(String validateDocCol, PlanSchemaDef schemaDef);
/**
  * Validate the document based on a supplied schema. This schema needs to be stored in the schema database. Check appserver error log for validate errors.
  * @param validateDocCol  The required 'kind' key of the schemaDef map must be 'jsonSchema', 'schematron', or 'xmlSchema'. When 'kind' is 'jsonSchema' or 'schemtron' then a key 'schemaUri' is required. Key 'mode' takes 'strict', 'lax' or 'type' (refer to xdmp:validate). See {@link PlanBuilder#col(XsStringVal)}
  * @param schemaDef  the schemaDef  value.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan validateDoc(PlanColumn validateDocCol, PlanSchemaDef schemaDef);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-inner on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestInner(String inputColumn, String valueColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-inner on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestInner(PlanExprCol inputColumn, PlanExprCol valueColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-inner on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @param ordinalColumn  The ordinalColumn is optional. If specified, an additional column will be added to the rows of flattened array values, starting from 1. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestInner(String inputColumn, String valueColumn, String ordinalColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-inner on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @param ordinalColumn  The ordinalColumn is optional. If specified, an additional column will be added to the rows of flattened array values, starting from 1. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestInner(PlanExprCol inputColumn, PlanExprCol valueColumn, PlanExprCol ordinalColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-left-outer on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestLeftOuter(String inputColumn, String valueColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-left-outer on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestLeftOuter(PlanExprCol inputColumn, PlanExprCol valueColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-left-outer on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @param ordinalColumn  The ordinalColumn is optional. If specified, an additional column will be added to the rows of flattened array values, starting from 1. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestLeftOuter(String inputColumn, String valueColumn, String ordinalColumn);
/**
  * This function flattens an array value into multiple rows.Then performs a op:join-left-outer on the rest of the rows.
  * @param inputColumn  The input column, which contains an array, to flatten into rows. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col if you need to identify columns in the two views that have the same column name. See {@link PlanBuilder#col(XsStringVal)}
  * @param valueColumn  The output column which contains the flattened array values. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @param ordinalColumn  The ordinalColumn is optional. If specified, an additional column will be added to the rows of flattened array values, starting from 1. This can be a string of the column name or an op:col. Use op:view-col or op:schema-col as needed. See {@link PlanBuilder#col(XsStringVal)}
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan unnestLeftOuter(PlanExprCol inputColumn, PlanExprCol valueColumn, PlanExprCol ordinalColumn);
  }


/**
 * Provides functions and operations in the final phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface Plan extends PlanBuilderBase.PlanBase {
/**
  * Specifies a value to replace a placeholder parameter during this execution of the plan in all expressions in which the parameter appears. <p>As when building a plan, binding a parameter constructs a new instance of the plan with the binding instead of mutating the existing instance of the plan.</p>
  * @param param  the name of a placeholder parameter
  * @param literal  a value to replace the parameter
  * @return  a new instance of the Plan object with the parameter binding
  */
  public abstract Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal);
  }


/**
 * Provides functions and operations in the prepare phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface PreparePlan extends ExportablePlan, PlanBuilderBase.PreparePlanBase {
/**
  * This method applies the specified function to each row returned by the plan to produce a different result row.
  * @param func  The function to be applied.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan map(PlanFunction func);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array.
  * @param func  The function to be applied.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array.
  * @param func  The function to be applied.
  * @param seed  The value returned by the previous request.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func, String seed);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array.
  * @param func  The function to be applied.
  * @param seed  The value returned by the previous request.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed);
  }


}
