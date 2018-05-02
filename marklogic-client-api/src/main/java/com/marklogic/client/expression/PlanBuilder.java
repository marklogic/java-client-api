/*
 * Copyright 2016-2018 MarkLogic Corporation
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

import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanGroupConcatOptionSeq;
import com.marklogic.client.type.PlanParamBindingVal;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanPrefixer;
import com.marklogic.client.type.PlanTripleOption;
import com.marklogic.client.type.PlanValueOption;
import java.util.Map;
import java.util.Map;

import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.AttributeNodeExpr;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.BooleanNodeExpr;
import com.marklogic.client.type.CommentNodeExpr;
import com.marklogic.client.type.DocumentNodeExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.JsonContentNodeExpr;
import com.marklogic.client.type.JsonRootNodeExpr;
import com.marklogic.client.type.NodeSeqExpr;
import com.marklogic.client.type.NullNodeExpr;
import com.marklogic.client.type.NumberNodeExpr;
import com.marklogic.client.type.ObjectNodeExpr;
import com.marklogic.client.type.ProcessingInstructionNodeExpr;
import com.marklogic.client.type.SemIriExpr;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.TextNodeExpr;
import com.marklogic.client.type.XmlContentNodeExpr;
import com.marklogic.client.type.XmlRootNodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsIntExpr;
import com.marklogic.client.type.XsIntVal;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsNumericVal;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.PlanAggregateCol;
import com.marklogic.client.type.PlanAggregateColSeq;
import com.marklogic.client.type.PlanCase;
import com.marklogic.client.type.PlanCaseSeq;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanColumnSeq;
import com.marklogic.client.type.PlanCondition;
import com.marklogic.client.type.PlanConditionSeq;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.PlanExprColSeq;
import com.marklogic.client.type.PlanFunction;
import com.marklogic.client.type.PlanFunctionSeq;
import com.marklogic.client.type.PlanJoinKey;
import com.marklogic.client.type.PlanJoinKeySeq;
import com.marklogic.client.type.PlanJsonProperty;
import com.marklogic.client.type.PlanJsonPropertySeq;
import com.marklogic.client.type.PlanParamBindingSeqVal;
import com.marklogic.client.type.PlanParamBindingVal;
import com.marklogic.client.type.PlanSortKey;
import com.marklogic.client.type.PlanSortKeySeq;
import com.marklogic.client.type.PlanSystemColumn;
import com.marklogic.client.type.PlanSystemColumnSeq;
import com.marklogic.client.type.PlanTriplePattern;
import com.marklogic.client.type.PlanTriplePatternSeq;
import com.marklogic.client.type.PlanTriplePosition;
import com.marklogic.client.type.PlanTriplePositionSeq;


import com.marklogic.client.expression.CtsExpr; 
import com.marklogic.client.expression.FnExpr; 
import com.marklogic.client.expression.JsonExpr; 
import com.marklogic.client.expression.MapExpr; 
import com.marklogic.client.expression.MathExpr; 
import com.marklogic.client.expression.RdfExpr; 
import com.marklogic.client.expression.SemExpr; 
import com.marklogic.client.expression.SpellExpr; 
import com.marklogic.client.expression.SqlExpr; 
import com.marklogic.client.expression.XdmpExpr; 
import com.marklogic.client.expression.XsExpr;

import com.marklogic.client.expression.PlanBuilderBase;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds the plan for a row pipeline to execute on the server.
 */
public abstract class PlanBuilder implements PlanBuilderBase {
  protected PlanBuilder(
    CtsExpr cts, FnExpr fn, JsonExpr json, MapExpr map, MathExpr math, RdfExpr rdf, SemExpr sem, SpellExpr spell, SqlExpr sql, XdmpExpr xdmp, XsExpr xs
    ) {
    this.cts = cts;
     this.fn = fn;
     this.json = json;
     this.map = map;
     this.math = math;
     this.rdf = rdf;
     this.sem = sem;
     this.spell = spell;
     this.sql = sql;
     this.xdmp = xdmp;
     this.xs = xs;

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
  * Builds expressions with xdmp server functions.
  */
  public final XdmpExpr xdmp;
 /**
  * Builds expressions with xs server functions.
  */
  public final XsExpr xs;
/**
  * This function returns the sum of the specified numeric expressions. In expressions, the call should pass the result from an op:col function to identify a column. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:add" target="mlserverdoc">op:add</a>
  * @param left  The left value expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr add(XsNumericExpr... left);
  /**
  * This function returns true if the specified expressions all return true. Otherwise, it returns false. You can either compair 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:and" target="mlserverdoc">op:and</a>
  * @param left  The left value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr and(XsAnyAtomicTypeExpr... left);
  /**
  * This function divides the left numericExpression by the right numericExpression and returns the value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:divide" target="mlserverdoc">op:divide</a>
  * @param left  The left numeric expression.
  * @param right  The right numeric expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr divide(XsNumericExpr left, XsNumericExpr right);
  /**
  * This function returns true if the left and right expressions return the same value. Otherwise, it returns false. In expressions, the call should pass the result from an op:col function to identify a column.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:eq" target="mlserverdoc">op:eq</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr eq(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function returns true if the value of the left expression is greater than or equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:ge" target="mlserverdoc">op:ge</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr ge(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function returns true if the value of the left expression is greater than the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:gt" target="mlserverdoc">op:gt</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr gt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function tests whether the value of an expression is null in the row where the expression might be as simple as a column identified by op:col.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:is-defined" target="mlserverdoc">op:is-defined</a>
  * @param operand  A boolean expression, such as op:eq or op:not, that might be null.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr isDefined(ItemExpr operand);
  /**
  * This function returns true if the value of the left expression is less than or equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:le" target="mlserverdoc">op:le</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr le(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function returns true if the value of the left expression is less than the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:lt" target="mlserverdoc">op:lt</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr lt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function multipies the left numericExpression by the right numericExpression and returns the value. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:multiply" target="mlserverdoc">op:multiply</a>
  * @param left  The left numeric expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr multiply(XsNumericExpr... left);
  /**
  * This function returns true if the value of the left expression is not equal to the value of the right expression. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:ne" target="mlserverdoc">op:ne</a>
  * @param left  The left value expression.
  * @param right  The right value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr ne(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
  /**
  * This function returns true if neither of the specified boolean expressions return true. Otherwise, it returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:not" target="mlserverdoc">op:not</a>
  * @param operand  Exactly one boolean expression, such as op:and or op:or, or op:is-defined.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr not(XsAnyAtomicTypeExpr operand);
  /**
  * This function returns true if the specified expressions all return true. Otherwise, it returns false. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:or" target="mlserverdoc">op:or</a>
  * @param left  The left value expression.
  * @return  a XsBooleanExpr expression
  */
  public abstract XsBooleanExpr or(XsAnyAtomicTypeExpr... left);
  /**
  * This function subtracts the right numericExpression from the left numericExpression and returns the value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:subtract" target="mlserverdoc">op:subtract</a>
  * @param left  The left numeric expression.
  * @param right  The right numeric expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr subtract(XsNumericExpr left, XsNumericExpr right);
  /**
  * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The op:result function throws in an error if the binding parameter does not specify a literal value for the parameter.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param</a>
  * @param name  The name of the parameter.
  * @return  a PlanParamExpr expression
  */
  public abstract PlanParamExpr param(String name);
  /**
  * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The op:result function throws in an error if the binding parameter does not specify a literal value for the parameter.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:param" target="mlserverdoc">op:param</a>
  * @param name  The name of the parameter.
  * @return  a PlanParamExpr expression
  */
  public abstract PlanParamExpr param(XsStringVal name);
  /**
  * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.
  * @param column  The Optic AccessorPlan created by op:from-view, op:from-triples, or op:from-lexicons.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(String column);
  /**
  * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.
  * @param column  The Optic AccessorPlan created by op:from-view, op:from-triples, or op:from-lexicons.
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
  * @param column  the column  value
  * @return  a PlanSystemColumn object
  */
  public abstract PlanSystemColumn fragmentIdCol(String column);
  /**
  * Specifies a name for adding a fragment id column to the row set identifying the source documents for the rows from a view, lexicons or triples. The only use for the fragment id is joining other rows from the same document, the document uri, or the document content. The fragment id is only useful during execution of the query and not after.
  * @param column  the column  value
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
  * @param column  The name of the column to be defined. This can be either a string or the return value from op:col, op:view-col, or op:schema-col.
  * @param expression  The expression used to define the value the column.
  * @return  a PlanExprCol object
  */
  public abstract PlanExprCol as(String column, ItemSeqExpr expression);
  /**
  * This function defines a column by assigning the value of an expression over the rows in the row set.
  * @param column  The name of the column to be defined. This can be either a string or the return value from op:col, op:view-col, or op:schema-col.
  * @param expression  The expression used to define the value the column.
  * @return  a PlanExprCol object
  */
  public abstract PlanExprCol as(PlanColumn column, ItemSeqExpr expression);
  public abstract PlanExprColSeq colSeq(String... col);
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
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePattern... patterns);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup which can take an on|off value to enable or disable deduplication. Deduplication is off by default.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris, PlanTripleOption option);
  /**
  * Reads rows by matching patterns in the triple index. 
  * @param patterns  One or more pattern definitions returned by the op:pattern function.
  * @param qualifierName  Specifies a name for qualifying the column names. By default, triple rows have no qualification. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The sem:default-graph-iri function returns the iri that identifies the default graph.
  * @param option  Options consisting of key-value pairs that set options. At present, the options consist of dedup which can take an on|off value to enable or disable deduplication. Deduplication is off by default.
  * @return  a AccessPlan object
  */
  public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris, PlanTripleOption option);
  /**
  * This function builds the parameters for the op:from-triples function. The result is passed to op:from-triples to project rows from the graph of triples. The columns in a pattern become the columns of the row. The literals in a pattern are used to match triples. You should specify at least one literal in each pattern, usually the predicate. Where a column appears in more than one pattern, the matched triples are joined to form the row. You can specify optional triples with a op:join-left-outer with a separate op:from-triples.
  * @param subjects  One column or one or more literal values, such as the literal returned by a sem:iri call.
  * @param predicates  One column or one or more literal values, such as the literal returned by a sem.iri call.
  * @param objects  One column or one or more literal values, such as the literal returned by a sem:iri call.
  * @return  a PlanTriplePattern object
  */
  public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects);
  /**
  * This function builds the parameters for the op:from-triples function. The result is passed to op:from-triples to project rows from the graph of triples. The columns in a pattern become the columns of the row. The literals in a pattern are used to match triples. You should specify at least one literal in each pattern, usually the predicate. Where a column appears in more than one pattern, the matched triples are joined to form the row. You can specify optional triples with a op:join-left-outer with a separate op:from-triples.
  * @param subjects  One column or one or more literal values, such as the literal returned by a sem:iri call.
  * @param predicates  One column or one or more literal values, such as the literal returned by a sem.iri call.
  * @param objects  One column or one or more literal values, such as the literal returned by a sem:iri call.
  * @param sysCols  Specifies the result of an op:fragment-id-col or op:graph-col function to add columns for the fragment id or graph iri.
  * @return  a PlanTriplePattern object
  */
  public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects, PlanSystemColumnSeq sysCols);
  public abstract PlanTriplePatternSeq patternSeq(PlanTriplePattern... pattern);
  public abstract PlanTriplePositionSeq subjectSeq(PlanTriplePosition... subject);
  public abstract PlanTriplePositionSeq predicateSeq(PlanTriplePosition... predicate);
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
  public abstract ModifyPlan fromSparql(String select);
  public abstract ModifyPlan fromSparql(XsStringVal select);
  public abstract ModifyPlan fromSparql(String select, String qualifierName);
  public abstract ModifyPlan fromSparql(XsStringVal select, XsStringVal qualifierName);
  public abstract ModifyPlan fromSql(String select);
  public abstract ModifyPlan fromSql(XsStringVal select);
  public abstract ModifyPlan fromSql(String select, String qualifierName);
  public abstract ModifyPlan fromSql(XsStringVal select, XsStringVal qualifierName);
  public abstract PlanCondition sqlCondition(String expression);
  public abstract PlanCondition sqlCondition(XsStringVal expression);
  /**
  * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the op:join-inner and op:join-left-outer functions. 
  * @param left  The rows from the left view.
  * @param right  The row set from the right view.
  * @return  a PlanJoinKey object
  */
  public abstract PlanJoinKey on(String left, String right);
  /**
  * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the op:join-inner and op:join-left-outer functions. 
  * @param left  The rows from the left view.
  * @param right  The row set from the right view.
  * @return  a PlanJoinKey object
  */
  public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
  public abstract PlanJoinKeySeq joinKeySeq(PlanJoinKey... key);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column to be aggregated.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(String name, String column);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column to be aggregated.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column to be aggregated.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(String name, String column, PlanValueOption option);
  /**
  * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column to be aggregated.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The columns to be aggregated.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(String name, String column);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The columns to be aggregated.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The columns to be aggregated.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(String name, String column, PlanValueOption option);
  /**
  * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The columns to be aggregated.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @param column  The columns to be counted.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name, String column);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @param column  The columns to be counted.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @param column  The columns to be counted.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(String name, String column, PlanValueOption option);
  /**
  * This function counts the rows where the specified input column has a value. If the input column is omitted, all rows in the group or row set are counted. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the column values.
  * @param column  The columns to be counted.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(String name, String column);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value.
  * @param column  The group or row set.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(String name, String column, PlanValueOption option);
  /**
  * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the largest value.
  * @param column  The group or row set.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(String name, String column);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value.
  * @param column  The group or row set.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(String name, String column, PlanValueOption option);
  /**
  * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the smallest value.
  * @param column  The group or row set.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sample(String name, String column);
  /**
  * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the value.
  * @param column  The group or row set.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sample(PlanColumn name, PlanExprCol column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(String name, String column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(String name, String column, PlanValueOption option);
  /**
  * This call constructs a sequence whose items are the values of a column for each row in the group or row set. The result is used for building the parameters used by the op:group-by function.
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to add.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(String name, String column);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to add.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to add.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(String name, String column, PlanValueOption option);
  /**
  * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to add.
  * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column, PlanValueOption option);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(String name, String column, String module, String function);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @param arg  The options can take a values key with a distinct value to average the distinct values of the column and an arg key specifying an argument for the user-defined aggregate. The value can be a string or placeholder parameter.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(String name, String column, String module, String function, String arg);
  /**
  * This function processes the values of column for each row in the group or row set with the specified user-defined aggregate as implemented by an aggregate user-defined function (UDF) plugin. The UDF plugin must be installed on each host. The result is used for building the parameters used by the op:group-by function. 
  * @param name  The name to be used for the aggregated column.
  * @param column  The column with the values to aggregate.
  * @param module  The path to the installed plugin module.
  * @param function  The name of the UDF function.
  * @param arg  The options can take a values key with a distinct value to average the distinct values of the column and an arg key specifying an argument for the user-defined aggregate. The value can be a string or placeholder parameter.
  * @return  a PlanAggregateCol object
  */
  public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg);
  public abstract PlanAggregateColSeq aggregateSeq(PlanAggregateCol... aggregate);
  /**
  * This function sorts the specified columndef in ascending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output.
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey asc(String column);
  /**
  * This function sorts the specified columndef in ascending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output.
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey asc(PlanExprCol column);
  /**
  * This function sorts the specified columndef in descending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output.
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey desc(String column);
  /**
  * This function sorts the specified columndef in descending order. The results are used by the op:order-by function.
  * @param column  The column by which order the output.
  * @return  a PlanSortKey object
  */
  public abstract PlanSortKey desc(PlanExprCol column);
  public abstract PlanSortKeySeq sortKeySeq(PlanSortKey... key);
  /**
  * This function returns the remainder afer the division of the dividend and divisor expressions. For example, op:modulo(5, 2) returns 1.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:modulo" target="mlserverdoc">op:modulo</a>
  * @param left  The dividend numeric expression.
  * @param right  The divisor numeric expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr modulo(double left, double right);
  /**
  * This function returns the remainder afer the division of the dividend and divisor expressions. For example, op:modulo(5, 2) returns 1.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:modulo" target="mlserverdoc">op:modulo</a>
  * @param left  The dividend numeric expression.
  * @param right  The divisor numeric expression.
  * @return  a XsNumericExpr expression
  */
  public abstract XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right);
  /**
  * This function executes the specified expression if the specified condition is true for the row. Otherwise, the expression is not executed and the next 'when' test is checked or, if there is no next 'when' text, the otherwise expression for the op:case expression is executed.
  * @param condition  A boolean expression.
  * @param value  The value expression to return if the boolean expression is true.
  * @return  a PlanCase object
  */
  public abstract PlanCase when(boolean condition, ItemExpr... value);
  /**
  * This function executes the specified expression if the specified condition is true for the row. Otherwise, the expression is not executed and the next 'when' test is checked or, if there is no next 'when' text, the otherwise expression for the op:case expression is executed.
  * @param condition  A boolean expression.
  * @param value  The value expression to return if the boolean expression is true.
  * @return  a PlanCase object
  */
  public abstract PlanCase when(XsBooleanExpr condition, ItemExpr... value);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a>
  * @param column  The name of the column from which to extract the child nodes.
  * @param path  An XPath (specified as a string) to apply to each node.
  * @return  a NodeSeqExpr expression sequence
  */
  public abstract NodeSeqExpr xpath(String column, String path);
  /**
  * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xpath" target="mlserverdoc">op:xpath</a>
  * @param column  The name of the column from which to extract the child nodes.
  * @param path  An XPath (specified as a string) to apply to each node.
  * @return  a NodeSeqExpr expression sequence
  */
  public abstract NodeSeqExpr xpath(PlanColumn column, XsStringExpr path);
  /**
  * This function constructs a JSON document with the root content, which must be exactly one JSON object or array node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-document" target="mlserverdoc">op:json-document</a>
  * @param root  The JSON object or array node used to construct the JSON document.
  * @return  a DocumentNodeExpr expression
  */
  public abstract DocumentNodeExpr jsonDocument(JsonRootNodeExpr root);
  /**
  * This function specifies the key expression and value content for a JSON property of a JSON object contructed by the op:json-object function.
  * @param key  The key expression. This must evaluate to a string.
  * @param value  The value content. This must be exactly one JSON node expression.
  * @return  a PlanJsonProperty object
  */
  public abstract PlanJsonProperty prop(String key, JsonContentNodeExpr value);
  /**
  * This function specifies the key expression and value content for a JSON property of a JSON object contructed by the op:json-object function.
  * @param key  The key expression. This must evaluate to a string.
  * @param value  The value content. This must be exactly one JSON node expression.
  * @return  a PlanJsonProperty object
  */
  public abstract PlanJsonProperty prop(XsStringExpr key, JsonContentNodeExpr value);
  /**
  * This function constructs a JSON text node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-string" target="mlserverdoc">op:json-string</a>
  * @param value  The value of the JSON text node.
  * @return  a TextNodeExpr expression
  */
  public abstract TextNodeExpr jsonString(String value);
  /**
  * This function constructs a JSON text node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-string" target="mlserverdoc">op:json-string</a>
  * @param value  The value of the JSON text node.
  * @return  a TextNodeExpr expression
  */
  public abstract TextNodeExpr jsonString(XsAnyAtomicTypeExpr value);
  /**
  * This function constructs a JSON number node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-number" target="mlserverdoc">op:json-number</a>
  * @param value  The value of the JSON number node.
  * @return  a NumberNodeExpr expression
  */
  public abstract NumberNodeExpr jsonNumber(double value);
  /**
  * This function constructs a JSON number node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-number" target="mlserverdoc">op:json-number</a>
  * @param value  The value of the JSON number node.
  * @return  a NumberNodeExpr expression
  */
  public abstract NumberNodeExpr jsonNumber(XsNumericExpr value);
  /**
  * This function constructs a JSON boolean node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-boolean" target="mlserverdoc">op:json-boolean</a>
  * @param value  The value of the JSON boolean node.
  * @return  a BooleanNodeExpr expression
  */
  public abstract BooleanNodeExpr jsonBoolean(boolean value);
  /**
  * This function constructs a JSON boolean node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-boolean" target="mlserverdoc">op:json-boolean</a>
  * @param value  The value of the JSON boolean node.
  * @return  a BooleanNodeExpr expression
  */
  public abstract BooleanNodeExpr jsonBoolean(XsBooleanExpr value);
  /**
  * This function constructs a JSON null node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:json-null" target="mlserverdoc">op:json-null</a>
  * @return  a NullNodeExpr expression
  */
  public abstract NullNodeExpr jsonNull();
  /**
  * This function constructs an XML document with the root content, which must be exactly one node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-document" target="mlserverdoc">op:xml-document</a>
  * @param root  The XML node used to construct the XML document.
  * @return  a DocumentNodeExpr expression
  */
  public abstract DocumentNodeExpr xmlDocument(XmlRootNodeExpr root);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(String name);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(XsQNameExpr name);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.
  * @param content  A sequence or array of atomic values or an element, a comment from op:xml-comment, or processing instruction nodes from op:xml-pi.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
  /**
  * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-element" target="mlserverdoc">op:xml-element</a>
  * @param name  The string or QName for the constructed element.
  * @param attributes  Any element attributes returned from op:xml-attribute, or null if no attributes.
  * @param content  A sequence or array of atomic values or an element, a comment from op:xml-comment, or processing instruction nodes from op:xml-pi.
  * @return  a ElementNodeExpr expression
  */
  public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
  /**
  * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-attribute" target="mlserverdoc">op:xml-attribute</a>
  * @param name  The attribute name.
  * @param value  The attribute value.
  * @return  a AttributeNodeExpr expression
  */
  public abstract AttributeNodeExpr xmlAttribute(String name, String value);
  /**
  * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-attribute" target="mlserverdoc">op:xml-attribute</a>
  * @param name  The attribute name.
  * @param value  The attribute value.
  * @return  a AttributeNodeExpr expression
  */
  public abstract AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnyAtomicTypeExpr value);
  /**
  * This function constructs an XML text node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-text" target="mlserverdoc">op:xml-text</a>
  * @param value  The value of the XML text node.
  * @return  a TextNodeExpr expression
  */
  public abstract TextNodeExpr xmlText(String value);
  /**
  * This function constructs an XML text node with the specified value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-text" target="mlserverdoc">op:xml-text</a>
  * @param value  The value of the XML text node.
  * @return  a TextNodeExpr expression
  */
  public abstract TextNodeExpr xmlText(XsAnyAtomicTypeExpr value);
  /**
  * This function constructs an XML comment with the atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-comment" target="mlserverdoc">op:xml-comment</a>
  * @param content  The comment text.
  * @return  a CommentNodeExpr expression
  */
  public abstract CommentNodeExpr xmlComment(String content);
  /**
  * This function constructs an XML comment with the atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-comment" target="mlserverdoc">op:xml-comment</a>
  * @param content  The comment text.
  * @return  a CommentNodeExpr expression
  */
  public abstract CommentNodeExpr xmlComment(XsAnyAtomicTypeExpr content);
  /**
  * This function constructs an XML processing instruction with the atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-pi" target="mlserverdoc">op:xml-pi</a>
  * @param name  The name of the processing instruction.
  * @param value  The value of the processing instruction.
  * @return  a ProcessingInstructionNodeExpr expression
  */
  public abstract ProcessingInstructionNodeExpr xmlPi(String name, String value);
  /**
  * This function constructs an XML processing instruction with the atomic value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/op:xml-pi" target="mlserverdoc">op:xml-pi</a>
  * @param name  The name of the processing instruction.
  * @param value  The value of the processing instruction.
  * @return  a ProcessingInstructionNodeExpr expression
  */
  public abstract ProcessingInstructionNodeExpr xmlPi(XsStringExpr name, XsAnyAtomicTypeExpr value);
  public abstract AttributeNodeSeqExpr xmlAttributeSeq(AttributeNodeExpr... attribute);
  public abstract PlanFunction resolveFunction(String functionName, String modulePath);
  public abstract PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath);
/**
 * Provides functions and operations in the access phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface AccessPlan extends ModifyPlan, PlanBuilderBase.AccessPlanBase {
/**
  * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.
  * @param column  The Optic AccessorPlan created by op:from-view, op:from-triples, or op:from-lexicons.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(String column);
/**
  * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.
  * @param column  The Optic AccessorPlan created by op:from-view, op:from-triples, or op:from-lexicons.
  * @return  a PlanColumn object
  */
  public abstract PlanColumn col(XsStringVal column);
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
  * This method restricts the left row set to rows where a row with the same columns and values doesn't exist in the right row set.
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan except(ModifyPlan right);
/**
  * This method collapses a group of rows into a single row. 
  * @param keys  This parameter specifies the columns used to determine the groups. Rows with the same values in these columns are consolidated into a single group. The columns can be existing columns or new columns created by an expression specified with op:as. The rows produced by the group by operation include the key columns. Specify an empty sequence to create a single group for all of the rows in the row set.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan groupBy(PlanExprColSeq keys);
/**
  * This method collapses a group of rows into a single row. 
  * @param keys  This parameter specifies the columns used to determine the groups. Rows with the same values in these columns are consolidated into a single group. The columns can be existing columns or new columns created by an expression specified with op:as. The rows produced by the group by operation include the key columns. Specify an empty sequence to create a single group for all of the rows in the row set.
  * @param aggregates  This parameter specifies either new columns for aggregate functions over the rows in the group or columndefs that are constant for the group. The aggregate library functions are listed below.
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
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition);
/**
  * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product. 
  * @param right  The row set from the right view.
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition);
/**
  * This function specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows). 
  * @param docCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value.
  * @param sourceCol  The document uri or fragment id value. This is either the output from op:fragment-id-col specifying a fragment id column or a document uri column. Joining on a fragment id is more efficient than joining on a uri column.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDoc(String docCol, String sourceCol);
/**
  * This function specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows). 
  * @param docCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value.
  * @param sourceCol  The document uri or fragment id value. This is either the output from op:fragment-id-col specifying a fragment id column or a document uri column. Joining on a fragment id is more efficient than joining on a uri column.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol);
/**
  * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an op:fragment-id-col specifying a fragment id column. If the fragment id column is null in the row, the row is dropped from the rowset. 
  * @param uriCol  The document uri. This is the output from op:col('uri') that specifies a document uri column.
  * @param fragmentIdCol  The document fragment id value. This is the output from op:fragment-id-col specifying a fragment id column.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinDocUri(String uriCol, String fragmentIdCol);
/**
  * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an op:fragment-id-col specifying a fragment id column. If the fragment id column is null in the row, the row is dropped from the rowset. 
  * @param uriCol  The document uri. This is the output from op:col('uri') that specifies a document uri column.
  * @param fragmentIdCol  The document fragment id value. This is the output from op:fragment-id-col specifying a fragment id column.
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
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
/**
  * This method yields one output row set with the rows from an inner join as well as rows from the left row set. 
  * @param right  The row set from the right view.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right);
/**
  * This method yields one output row set with the rows from an inner join as well as rows from the left row set. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys);
/**
  * This method yields one output row set with the rows from an inner join as well as rows from the left row set. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys);
/**
  * This method yields one output row set with the rows from an inner join as well as rows from the left row set. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
/**
  * This method yields one output row set with the rows from an inner join as well as rows from the left row set. 
  * @param right  The row set from the right view.
  * @param keys  The equijoin from one or more calls to the op:on function.
  * @param condition  A boolean expression that filters the join output rows.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
/**
  * This method sorts the row set by the specified order definition.
  * @param keys  The specified column or sortdef output from the op:asc or op:desc function.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan orderBy(PlanSortKeySeq keys);
/**
  * This method prepares the specified plan for execution as an optional final step before execution.
  * @param optimize  The optimization level, which can be 0, 1, or 2 (with 1 as the default).
  * @return  a PreparePlan object
  */
  public abstract PreparePlan prepare(int optimize);
/**
  * This method prepares the specified plan for execution as an optional final step before execution.
  * @param optimize  The optimization level, which can be 0, 1, or 2 (with 1 as the default).
  * @return  a PreparePlan object
  */
  public abstract PreparePlan prepare(XsIntVal optimize);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to select.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprCol... columns);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to select.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprColSeq columns);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to select.
  * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
  * @return  a ModifyPlan object
  */
  public abstract ModifyPlan select(PlanExprColSeq columns, String qualifierName);
/**
  * This call projects the specified columns from the current row set and / or applies a qualifier to the columns in the row set. Unlike SQL, a select call is not required in an Optic query.
  * @param columns  The columns to select.
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
  }

  
/**
 * Provides functions and operations in the final phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface Plan extends PlanBuilderBase.PlanBase {
public abstract Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal);
  }

  
/**
 * Provides functions and operations in the prepare phase
 * of the plan for executing a row pipeline on the server.
 */
  public interface PreparePlan extends ExportablePlan, PlanBuilderBase.PreparePlanBase {
/**
  * This method applies the specified function to each row returned by the plan to produce a different result row.
  * @param func  The function to be appied.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan map(PlanFunction func);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array. 
  * @param func  The function to be appied.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array. 
  * @param func  The function to be appied.
  * @param seed  The value returned by the previous request.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func, String seed);
/**
  * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the reduce() method of JavaScript Array. 
  * @param func  The function to be appied.
  * @param seed  The value returned by the previous request.
  * @return  a ExportablePlan object
  */
  public abstract ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed);
  }


}
