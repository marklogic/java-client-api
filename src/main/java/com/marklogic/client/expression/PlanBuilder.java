/*
 * Copyright 2016-2017 MarkLogic Corporation
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
import com.marklogic.client.type.XmlContentNodeSeqExpr;
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
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanParamSeqExpr;
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
    public final CtsExpr cts;
     public final FnExpr fn;
     public final JsonExpr json;
     public final MapExpr map;
     public final MathExpr math;
     public final RdfExpr rdf;
     public final SemExpr sem;
     public final SpellExpr spell;
     public final SqlExpr sql;
     public final XdmpExpr xdmp;
     public final XsExpr xs;
/**
    * This function returns the sum of the specified numeric expressions. In expressions, the call should pass the result from an <a>op:col</a> function to identify a column.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:add" target="mlserverdoc">plan:add</a>
    * @param operand  Two or more boolean expressions, such as <a>op:eq</a> or <a>op:not</a>.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr add(XsNumericExpr... operand);
    /**
    * This function returns <code>true</code> if the specified expressions all return <code>true</code>. Otherwise, it returns <code>false</code>. You can either compair [need to talk to Erik about the difference between operand and left/right parameters. Seems that either can be specified, but perhaps not both ?????]
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:and" target="mlserverdoc">plan:and</a>
    * @param operand  Two or more boolean expressions, such as <a>op:eq</a> or <a>op:not</a>.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr and(XsAnyAtomicTypeExpr... operand);
    /**
    * This function divides the left <code>numericExpression</code> by the right <code>numericExpression</code> and returns the value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:divide" target="mlserverdoc">plan:divide</a>
    * @param operand1  The left numeric expression.
    * @param operand2  The left numeric expression.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr divide(XsNumericExpr operand1, XsNumericExpr operand2);
    /**
    * This function returns <code>true</code> if the left and right expressions return the same value. Otherwise, it returns <code>false</code>. In expressions, the call should pass the result from an <a>op:col</a> function to identify a column.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:eq" target="mlserverdoc">plan:eq</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr eq(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function returns <code>true</code> if the value of the left expression is greater than or equal to the value of the right expression. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:ge" target="mlserverdoc">plan:ge</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr ge(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function returns <code>true</code> if the value of the left expression is greater than the value of the right expression. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:gt" target="mlserverdoc">plan:gt</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr gt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function tests a value expression, <a>op.col</a> or another a column identifier function must be used to test whether a column has a null value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:is-defined" target="mlserverdoc">plan:is-defined</a>
    * @param operand  A boolean expression, such as <a>op:eq</a> or <a>op:not</a>.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr isDefined(ItemExpr operand);
    /**
    * This function returns <code>true</code> if the value of the left expression is less than or equal to the value of the right expression. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:le" target="mlserverdoc">plan:le</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr le(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function returns <code>true</code> if the value of the left expression is less than the value of the right expression. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:lt" target="mlserverdoc">plan:lt</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr lt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function multipies the left <code>numericExpression</code> by the right <code>numericExpression</code> and returns the value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:multiply" target="mlserverdoc">plan:multiply</a>
    * @param operand  Two or more numeric expressions, such as <a>op:divide</a> or <a>op:subtract</a>.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr multiply(XsNumericExpr... operand);
    /**
    * This function returns <code>true</code> if the value of the left expression is not equal to the value of the right expression. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:ne" target="mlserverdoc">plan:ne</a>
    * @param operand1  The left value expression.
    * @param operand2  The left value expression.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr ne(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    /**
    * This function returns <code>true</code> if neither of the specified boolean expressions return <code>true</code>. Otherwise, it returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:not" target="mlserverdoc">plan:not</a>
    * @param operand  Two or more [ or is it just one????] boolean expressions, such as <a>op:eq</a> or <a>op:not</a>.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr not(XsAnyAtomicTypeExpr operand);
    /**
    * This function returns <code>true</code> if the specified expressions all return <code>true</code>. Otherwise, it returns <code>false</code>. You can either compair [need to talk to Erik about the difference between operand and left/right parameters. Seems that either can be specified, but perhaps not both ?????]
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:or" target="mlserverdoc">plan:or</a>
    * @param operand  Two or more boolean expressions, such as <a>op:eq</a> or <a>op:not</a>.
    * @return  a XsBooleanExpr expression
    */
    public abstract XsBooleanExpr or(XsAnyAtomicTypeExpr... operand);
    /**
    * This function subtracts the right <code>numericExpression</code> from the left <code>numericExpression</code> and returns the value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:subtract" target="mlserverdoc">plan:subtract</a>
    * @param operand1  The left numeric expression.
    * @param operand2  The left numeric expression.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr subtract(XsNumericExpr operand1, XsNumericExpr operand2);
    /**
    * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The <a>op:result</a> function throws in an error if the binding parameter does not specify a literal value for the parameter.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:param" target="mlserverdoc">plan:param</a>
    
    */
    public abstract PlanParamExpr param(String name);
    /**
    * This function creates a placeholder for a literal value in an expression or as the offset or max for a limit. The <a>op:result</a> function throws in an error if the binding parameter does not specify a literal value for the parameter.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:param" target="mlserverdoc">plan:param</a>
    
    */
    public abstract PlanParamExpr param(XsStringVal name);
    /**
    * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string. Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist). <p> In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.  <p> The returned value from this function can be modified by any of the functions described in 'Expression Functions For Processing Column Values' in the <em>Application Developer's Guide</em> 
    * @param column  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn col(String column);
    /**
    * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string. Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist). <p> In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.  <p> The returned value from this function can be modified by any of the functions described in 'Expression Functions For Processing Column Values' in the <em>Application Developer's Guide</em> 
    * @param column  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn col(XsStringVal column);
    /**
    * Unambiguously identifies a column with the schema name, view name, and column name. Useful only for columns provided by a [template?] view.
    * @param schema  The name of the schema.
    * @param view  The name of the view.
    * @param column  The name of the column.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn schemaCol(String schema, String view, String column);
    /**
    * Unambiguously identifies a column with the schema name, view name, and column name. Useful only for columns provided by a [template?] view.
    * @param schema  The name of the schema.
    * @param view  The name of the view.
    * @param column  The name of the column.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column);
    /**
    * Identifies a column where the combination of view and column name is unique. Identifying the schema isn't necessary (and it might not exist). <p> If the combination of view and column name is not unique, an ambiguous column error is thrown. 
    * @param view  The name of the view.
    * @param column  The name of the column.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn viewCol(String view, String column);
    /**
    * Identifies a column where the combination of view and column name is unique. Identifying the schema isn't necessary (and it might not exist). <p> If the combination of view and column name is not unique, an ambiguous column error is thrown. 
    * @param view  The name of the view.
    * @param column  The name of the column.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn viewCol(XsStringVal view, XsStringVal column);
    /**
    * Specifies a name for adding a fragment id column to the row set for either a view or a dynamic list of lexicons.
    
    */
    public abstract PlanSystemColumn fragmentIdCol(String column);
    /**
    * Specifies a name for adding a fragment id column to the row set for either a view or a dynamic list of lexicons.
    
    */
    public abstract PlanSystemColumn fragmentIdCol(XsStringVal column);
    /**
    * Identifies a column [This function is TBD ??????] <p> 
    * @param column  The name of the view.
    * @return  a PlanSystemColumn object
    */
    public abstract PlanSystemColumn graphCol(String column);
    /**
    * Identifies a column [This function is TBD ??????] <p> 
    * @param column  The name of the view.
    * @return  a PlanSystemColumn object
    */
    public abstract PlanSystemColumn graphCol(XsStringVal column);
    /**
    * This function defines a column by assigning the value of an expression over the rows in the row set.
    * @param column  The name of the column to be defined.
    * @param expression  The expression used to identify the column.
    * @return  a PlanExprCol object
    */
    public abstract PlanExprCol as(String column, ItemSeqExpr expression);
    /**
    * This function defines a column by assigning the value of an expression over the rows in the row set.
    * @param column  The name of the column to be defined.
    * @param expression  The expression used to identify the column.
    * @return  a PlanExprCol object
    */
    public abstract PlanExprCol as(PlanColumn column, ItemSeqExpr expression);
    public abstract PlanExprColSeq colSeq(String... col);
    public abstract PlanExprColSeq colSeq(PlanExprCol... col);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(String schema, String view);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(String schema, String view, String qualifierName);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param sysCols  A list that can specify <a>op:fragment-id-col</a> to add columns for the row id or fragment id. One use case for fragment ids in joins with lexicons.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols);
    /**
    *  This function reads a row set from a configured view over TDE-indexed rows or over the co-occurrence of range indexed values in documents. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit.  
    * @param schema  The name identifying the schema containing the view. If the schema name is null, the engine searches for a view with the specified name.
    * @param view  The name identifying a configured template or range view for rows projected from documents.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param sysCols  A list that can specify <a>op:fragment-id-col</a> to add columns for the row id or fragment id. One use case for fragment ids in joins with lexicons.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols);
    /**
    * This function takes a name and returns a <code>sem.iri</code>, prepending the specified base URI onto the name.
    * @param base  The base URI to be prepended to the name.
    * @return  a PlanPrefixer object
    */
    public abstract PlanPrefixer prefixer(String base);
    /**
    * This function takes a name and returns a <code>sem.iri</code>, prepending the specified base URI onto the name.
    * @param base  The base URI to be prepended to the name.
    * @return  a PlanPrefixer object
    */
    public abstract PlanPrefixer prefixer(XsStringVal base);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePattern... patterns);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The <a>sem:default-graph-iri</a> function returns the iri that identifies the default graph.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The <a>sem:default-graph-iri</a> function returns the iri that identifies the default graph.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The <a>sem:default-graph-iri</a> function returns the iri that identifies the default graph.
    * @param option  A <a>sem:store</a> or <a>cts:query</a> that restricts the rows to triples from the specified store or matched source documents.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris, PlanTripleOption option);
    /**
    * Reads rows by matching patterns in the triple index. <p> The rows have a column for each column name in the patterns. While each column will have a consistent datatype for all rows from a view, the columns of rows from a graph may have varying data types, which could affect joins.  <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param patterns  A <code>patterndef</code> returned by the <a>op:pattern</a> function.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param graphIris  A list of graph IRIs to restrict the results to triples in the specified graphs. The <a>sem:default-graph-iri</a> function returns the iri that identifies the default graph.
    * @param option  A <a>sem:store</a> or <a>cts:query</a> that restricts the rows to triples from the specified store or matched source documents.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris, PlanTripleOption option);
    /**
    * This function builds the parameters for the <a>op.fromTriples</a> function.
    * @param subjects  One or more subjects returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @param predicates  One or more predicates returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @param objects  One or more objects returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @return  a PlanTriplePattern object
    */
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects);
    /**
    * This function builds the parameters for the <a>op.fromTriples</a> function.
    * @param subjects  One or more subjects returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @param predicates  One or more predicates returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @param objects  One or more objects returned by an <a>op:col</a> call or by an expression (including an IRI or property path).
    * @param sysCols  Specifies the result of an <a>op:fragment-id-col</a> or <a>op:graph-col</a> function to add columns for the fragment id or graph iri. If the graph column is specified, the fragment id column is also provided whether or not it is specified.
    * @return  a PlanTriplePattern object
    */
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects, PlanSystemColumnSeq sysCols);
    public abstract PlanTriplePatternSeq patternSeq(PlanTriplePattern... pattern);
    public abstract PlanTriplePositionSeq subjectSeq(PlanTriplePosition... subject);
    public abstract PlanTriplePositionSeq predicateSeq(PlanTriplePosition... predicate);
    public abstract PlanTriplePositionSeq objectSeq(PlanTriplePosition... object);
    /**
    * This function dynamically constructs a view with column names that are prefixed to distinguish the columns from those of other views (instead of naming a pre-defined view). If the <a>cts:reference</a> sets the nullable option to <code>true</code>, the column is optional in the row. (That is, the API does a left outer join on the lexicon.) At least one of the lexicons must be required instead of optional. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param indexes  An object in which each key is a column name and each value specifies a <code>cts:reference</code> for a range index or other lexicon (especially the <a>cts:uri-reference</a> lexicon) with the column values.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes);
    /**
    * This function dynamically constructs a view with column names that are prefixed to distinguish the columns from those of other views (instead of naming a pre-defined view). If the <a>cts:reference</a> sets the nullable option to <code>true</code>, the column is optional in the row. (That is, the API does a left outer join on the lexicon.) At least one of the lexicons must be required instead of optional. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param indexes  An object in which each key is a column name and each value specifies a <code>cts:reference</code> for a range index or other lexicon (especially the <a>cts:uri-reference</a> lexicon) with the column values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName);
    /**
    * This function dynamically constructs a view with column names that are prefixed to distinguish the columns from those of other views (instead of naming a pre-defined view). If the <a>cts:reference</a> sets the nullable option to <code>true</code>, the column is optional in the row. (That is, the API does a left outer join on the lexicon.) At least one of the lexicons must be required instead of optional. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param indexes  An object in which each key is a column name and each value specifies a <code>cts:reference</code> for a range index or other lexicon (especially the <a>cts:uri-reference</a> lexicon) with the column values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName);
    /**
    * This function dynamically constructs a view with column names that are prefixed to distinguish the columns from those of other views (instead of naming a pre-defined view). If the <a>cts:reference</a> sets the nullable option to <code>true</code>, the column is optional in the row. (That is, the API does a left outer join on the lexicon.) At least one of the lexicons must be required instead of optional. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param indexes  An object in which each key is a column name and each value specifies a <code>cts:reference</code> for a range index or other lexicon (especially the <a>cts:uri-reference</a> lexicon) with the column values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param sysCols  One or more fragment id columns returned by the <a>op:fragment-id-col</a> function. This is used to join the lexicon rows, which always contain fragment id columns.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName, PlanSystemColumn sysCols);
    /**
    * This function dynamically constructs a view with column names that are prefixed to distinguish the columns from those of other views (instead of naming a pre-defined view). If the <a>cts:reference</a> sets the nullable option to <code>true</code>, the column is optional in the row. (That is, the API does a left outer join on the lexicon.) At least one of the lexicons must be required instead of optional. <p> This function creates a row set without a limit. Use <a>op:limit</a> or <a>op:offset-limit</a> to set a limit. 
    * @param indexes  An object in which each key is a column name and each value specifies a <code>cts:reference</code> for a range index or other lexicon (especially the <a>cts:uri-reference</a> lexicon) with the column values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @param sysCols  One or more fragment id columns returned by the <a>op:fragment-id-col</a> function. This is used to join the lexicon rows, which always contain fragment id columns.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName, PlanSystemColumn sysCols);
    /**
    * Constructs a literal row set as in the SQL VALUES or SPARQL VALUES statements. When specifying rows with arrays, values are mapped to column names by position.
    * @param rows  This parameter is either an array of object literals or <a>sem:binding</a> objects in which the key is a column name string identifying the column and the value is a literal with the value of the column, or this parameter is an object with a columnNames key having a value of an array of column names and a rowValues key having a value of an array of arrays with literal values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLiterals(Map<String,Object>[] rows, String qualifierName);
    /**
    * Constructs a literal row set as in the SQL VALUES or SPARQL VALUES statements. When specifying rows with arrays, values are mapped to column names by position.
    * @param rows  This parameter is either an array of object literals or <a>sem:binding</a> objects in which the key is a column name string identifying the column and the value is a literal with the value of the column, or this parameter is an object with a columnNames key having a value of an array of column names and a rowValues key having a value of an array of arrays with literal values.
    * @param qualifierName  Specifies a name for qualifying the column names in place of the combination of the schema and view names. Use cases for the qualifier include self joins. Using an empty string removes all qualification from the column names.
    * @return  a AccessPlan object
    */
    public abstract AccessPlan fromLiterals(Map<String,Object>[] rows, XsStringVal qualifierName);
    /**
    * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the <a>op:join-inner</a> and <a>op:join-left-outer</a> functions.
    * @param left  The rows from the left view.
    * @param right  The row set from the right view.
    * @return  a PlanJoinKey object
    */
    public abstract PlanJoinKey on(String left, String right);
    /**
    * Specifies an equijoin using one columndef each from the left and right rows. The result is used by the <a>op:join-inner</a> and <a>op:join-left-outer</a> functions.
    * @param left  The rows from the left view.
    * @param right  The row set from the right view.
    * @return  a PlanJoinKey object
    */
    public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
    public abstract PlanJoinKeySeq joinKeySeq(PlanJoinKey... key);
    /**
    * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol avg(String name, String column);
    /**
    * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column);
    /**
    * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol avg(String name, String column, PlanValueOption option);
    /**
    * This function averages the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol arrayAggregate(String name, String column);
    /**
    * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column);
    /**
    * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol arrayAggregate(String name, String column, PlanValueOption option);
    /**
    * This function constructs an array whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(String name);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(PlanColumn name);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @param column  The columns to be counted.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(String name, String column);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @param column  The columns to be counted.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @param column  The columns to be counted.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(String name, String column, PlanValueOption option);
    /**
    * This function counts the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the column values.
    * @param column  The columns to be counted.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function concatenates the non-null values of the column for the rows in the group or row set. In addition to the <code>values</code> key, the options can take a <code>separator</code> key specifying a separator character. The value can be a string or placeholder parameter. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the concatenated columns.
    * @param column  The columns to be concatenated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol groupConcat(String name, String column);
    /**
    * This function concatenates the non-null values of the column for the rows in the group or row set. In addition to the <code>values</code> key, the options can take a <code>separator</code> key specifying a separator character. The value can be a string or placeholder parameter. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the concatenated columns.
    * @param column  The columns to be concatenated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column);
    /**
    * This function concatenates the non-null values of the column for the rows in the group or row set. In addition to the <code>values</code> key, the options can take a <code>separator</code> key specifying a separator character. The value can be a string or placeholder parameter. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the concatenated columns.
    * @param column  The columns to be concatenated.
    * @param options  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol groupConcat(String name, String column, PlanGroupConcatOptionSeq options);
    /**
    * This function concatenates the non-null values of the column for the rows in the group or row set. In addition to the <code>values</code> key, the options can take a <code>separator</code> key specifying a separator character. The value can be a string or placeholder parameter. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the concatenated columns.
    * @param column  The columns to be concatenated.
    * @param options  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column, PlanGroupConcatOptionSeq options);
    /**
    * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the largest value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol max(String name, String column);
    /**
    * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the largest value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column);
    /**
    * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the largest value.
    * @param column  The group or row set.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol max(String name, String column, PlanValueOption option);
    /**
    * This function gets the largest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the largest value.
    * @param column  The group or row set.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the smallest value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol min(String name, String column);
    /**
    * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the smallest value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column);
    /**
    * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the smallest value.
    * @param column  The group or row set.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol min(String name, String column, PlanValueOption option);
    /**
    * This function gets the smallest non-null value of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the smallest value.
    * @param column  The group or row set.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sample(String name, String column);
    /**
    * This function randomly selects one non-null value of the column from the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the value.
    * @param column  The group or row set.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sample(PlanColumn name, PlanExprCol column);
    /**
    * This function constructs a sequence whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sequenceAggregate(String name, String column);
    /**
    * This function constructs a sequence whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column);
    /**
    * This function constructs a sequence whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sequenceAggregate(String name, String column, PlanValueOption option);
    /**
    * This function constructs a sequence whose items are the result of evaluating the column for each row in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to which to add the values.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sum(String name, String column);
    /**
    * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to which to add the values.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column);
    /**
    * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to which to add the values.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sum(String name, String column, PlanValueOption option);
    /**
    * This function adds the non-null values of the column for the rows in the group or row set. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to which to add the values.
    * @param option  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column, PlanValueOption option);
    /**
    * This function processes the column with the specified user-defined aggregate implemented by a UDF plugin for aggregation. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param module  The path to the module.....
    * @param function  The name of the function......
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol uda(String name, String column, String module, String function);
    /**
    * This function processes the column with the specified user-defined aggregate implemented by a UDF plugin for aggregation. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param module  The path to the module.....
    * @param function  The name of the function......
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function);
    /**
    * This function processes the column with the specified user-defined aggregate implemented by a UDF plugin for aggregation. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param module  The path to the module.....
    * @param function  The name of the function......
    * @param arg  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol uda(String name, String column, String module, String function, String arg);
    /**
    * This function processes the column with the specified user-defined aggregate implemented by a UDF plugin for aggregation. The result is used for building the parameters used by the <a>op:group-by</a> function.
    * @param name  The name to be used for the aggregated column.
    * @param column  The columns to be aggregated.
    * @param module  The path to the module.....
    * @param function  The name of the function......
    * @param arg  The options can take a values key with a distinct value to average the distinct values of the column.
    * @return  a PlanAggregateCol object
    */
    public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg);
    public abstract PlanAggregateColSeq aggregateSeq(PlanAggregateCol... aggregate);
    /**
    * This function sorts the specified <code>columndef</code> in ascending order. The results are used by the <a>op:order-by</a> function.
    * @param column  The column by which order the output.
    * @return  a PlanSortKey object
    */
    public abstract PlanSortKey asc(String column);
    /**
    * This function sorts the specified <code>columndef</code> in ascending order. The results are used by the <a>op:order-by</a> function.
    * @param column  The column by which order the output.
    * @return  a PlanSortKey object
    */
    public abstract PlanSortKey asc(PlanExprCol column);
    /**
    * This function sorts the specified <code>columndef</code> in descending order. The results are used by the <a>op:order-by</a> function.
    * @param column  The column by which order the output.
    * @return  a PlanSortKey object
    */
    public abstract PlanSortKey desc(String column);
    /**
    * This function sorts the specified <code>columndef</code> in descending order. The results are used by the <a>op:order-by</a> function.
    * @param column  The column by which order the output.
    * @return  a PlanSortKey object
    */
    public abstract PlanSortKey desc(PlanExprCol column);
    public abstract PlanSortKeySeq sortKeySeq(PlanSortKey... key);
    /**
    * This function returns the remainder afer the division of the dividend and divisor expressions. For example: 5/2=2 (modulo 1).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:modulo" target="mlserverdoc">plan:modulo</a>
    * @param left  The dividend numeric expression.
    * @param right  The divisor numeric expression.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr modulo(double left, double right);
    /**
    * This function returns the remainder afer the division of the dividend and divisor expressions. For example: 5/2=2 (modulo 1).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:modulo" target="mlserverdoc">plan:modulo</a>
    * @param left  The dividend numeric expression.
    * @param right  The divisor numeric expression.
    * @return  a XsNumericExpr expression
    */
    public abstract XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right);
    /**
    * This function returns the specified <code>valueExpression</code> if the specified <code>valueExpression</code> is <code>true</code>. Otherwise, it returns null.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:case" target="mlserverdoc">plan:case</a>
    * @param cases  One or more WHEN expressions.
    * @return  a ItemSeqExpr expression sequence
    */
    public abstract ItemSeqExpr caseExpr(PlanCase... cases);
    /**
    * This function returns the specified <code>value</code> if the specified <code>condition</code> is <code>true</code>. Otherwise, it returns null.
    * @param condition  A boolean expression.
    * @param value  The value expression to return if the boolean expression is <code>true</code>.
    * @return  a PlanCase object
    */
    public abstract PlanCase when(boolean condition, ItemExpr... value);
    /**
    * This function returns the specified <code>value</code> if the specified <code>condition</code> is <code>true</code>. Otherwise, it returns null.
    * @param condition  A boolean expression.
    * @param value  The value expression to return if the boolean expression is <code>true</code>.
    * @return  a PlanCase object
    */
    public abstract PlanCase when(XsBooleanExpr condition, ItemExpr... value);
    public abstract PlanCase elseExpr(ItemExpr value);
    /**
    * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xpath" target="mlserverdoc">plan:xpath</a>
    * @param column  The name of the column from which to extract the child nodes.
    * @param path  An XPath (specified as a string) to apply to each node.
    * @return  a NodeSeqExpr expression sequence
    */
    public abstract NodeSeqExpr xpath(String column, String path);
    /**
    * This function extracts a sequence of child nodes from a column with node values -- especially, the document nodes from a document join. The path is an XPath (specified as a string) to apply to each node to generate a sequence of nodes as an expression value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xpath" target="mlserverdoc">plan:xpath</a>
    * @param column  The name of the column from which to extract the child nodes.
    * @param path  An XPath (specified as a string) to apply to each node.
    * @return  a NodeSeqExpr expression sequence
    */
    public abstract NodeSeqExpr xpath(PlanColumn column, XsStringExpr path);
    /**
    * This function constructs a JSON document with the root content, which must be exactly one JSON object or array node.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-document" target="mlserverdoc">plan:json-document</a>
    * @param root  The JSON object or array node used to construct the JSON document.
    * @return  a DocumentNodeExpr expression
    */
    public abstract DocumentNodeExpr jsonDocument(JsonRootNodeExpr root);
    /**
    * This function constructs a JSON object with the specified properties. The object can be used as the value of a column in a row or passed to a builtin function.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-object" target="mlserverdoc">plan:json-object</a>
    * @param property  The properties to be used to contruct the object. This is constructed by the <a>op:prop</a> function.
    * @return  a ObjectNodeExpr expression
    */
    public abstract ObjectNodeExpr jsonObject(PlanJsonProperty... property);
    /**
    * This function specifies the key expression and value content for a JSON property of a JSON object contructed by the <a>op:json-object</a> function.
    * @param key  The key expression. This must evaluate to a string.
    * @param value  The value content. This must be exactly one JSON node.
    * @return  a PlanJsonProperty object
    */
    public abstract PlanJsonProperty prop(String key, JsonContentNodeExpr value);
    /**
    * This function specifies the key expression and value content for a JSON property of a JSON object contructed by the <a>op:json-object</a> function.
    * @param key  The key expression. This must evaluate to a string.
    * @param value  The value content. This must be exactly one JSON node.
    * @return  a PlanJsonProperty object
    */
    public abstract PlanJsonProperty prop(XsStringExpr key, JsonContentNodeExpr value);
    /**
    * This function constructs a JSON array with the specified JSON nodes as items. The array can be used as the value of a column in a row or passed to a builtin function.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-array" target="mlserverdoc">plan:json-array</a>
    * @param property  The JSON nodes for the array.
    * @return  a ArrayNodeExpr expression
    */
    public abstract ArrayNodeExpr jsonArray(JsonContentNodeExpr... property);
    /**
    * This function constructs a JSON text node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-string" target="mlserverdoc">plan:json-string</a>
    * @param value  The value of the JSON text node.
    * @return  a TextNodeExpr expression
    */
    public abstract TextNodeExpr jsonString(String value);
    /**
    * This function constructs a JSON text node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-string" target="mlserverdoc">plan:json-string</a>
    * @param value  The value of the JSON text node.
    * @return  a TextNodeExpr expression
    */
    public abstract TextNodeExpr jsonString(XsAnyAtomicTypeExpr value);
    /**
    * This function constructs a JSON number node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-number" target="mlserverdoc">plan:json-number</a>
    * @param value  The value of the JSON number node.
    * @return  a NumberNodeExpr expression
    */
    public abstract NumberNodeExpr jsonNumber(double value);
    /**
    * This function constructs a JSON number node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-number" target="mlserverdoc">plan:json-number</a>
    * @param value  The value of the JSON number node.
    * @return  a NumberNodeExpr expression
    */
    public abstract NumberNodeExpr jsonNumber(XsNumericExpr value);
    /**
    * This function constructs a JSON boolean node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-boolean" target="mlserverdoc">plan:json-boolean</a>
    * @param value  The value of the JSON boolean node.
    * @return  a BooleanNodeExpr expression
    */
    public abstract BooleanNodeExpr jsonBoolean(boolean value);
    /**
    * This function constructs a JSON boolean node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-boolean" target="mlserverdoc">plan:json-boolean</a>
    * @param value  The value of the JSON boolean node.
    * @return  a BooleanNodeExpr expression
    */
    public abstract BooleanNodeExpr jsonBoolean(XsBooleanExpr value);
    /**
    * This function constructs a JSON null node.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:json-null" target="mlserverdoc">plan:json-null</a>
    
    */
    public abstract NullNodeExpr jsonNull();
    /**
    * This function constructs an XML document with the root content, which must be exactly one node.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-document" target="mlserverdoc">plan:xml-document</a>
    * @param root  The XML node used to construct the XML document.
    * @return  a DocumentNodeExpr expression
    */
    public abstract DocumentNodeExpr xmlDocument(XmlRootNodeExpr root);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(String name);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @param attributes  Any element attributes returned from <a>op:xml-attribute</a>, or <code>null</code> if no attributes.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @param attributes  Any element attributes returned from <a>op:xml-attribute</a>, or <code>null</code> if no attributes.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @param attributes  Any element attributes returned from <a>op:xml-attribute</a>, or <code>null</code> if no attributes.
    * @param content  A sequence or array of atomic values or an element, a comment from <a>op:xml-comment</a>, or processing instruction nodes from <a>op:xml-pi</a>.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    /**
    * This function constructs an XML element with the name (which can be a string or QName), zero or more attributes, and child content. The child content can include a sequence or array of atomic values or an element, comment, or processing instruction nodes. Atomic values are converted to text nodes.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-element" target="mlserverdoc">plan:xml-element</a>
    * @param name  The string or QName for the constructed element.
    * @param attributes  Any element attributes returned from <a>op:xml-attribute</a>, or <code>null</code> if no attributes.
    * @param content  A sequence or array of atomic values or an element, a comment from <a>op:xml-comment</a>, or processing instruction nodes from <a>op:xml-pi</a>.
    * @return  a ElementNodeExpr expression
    */
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    /**
    * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-attribute" target="mlserverdoc">plan:xml-attribute</a>
    * @param name  The attribute name.
    * @param value  The attribute value.
    * @return  a AttributeNodeExpr expression
    */
    public abstract AttributeNodeExpr xmlAttribute(String name, String value);
    /**
    * This function constructs an XML attribute with the name (which can be a string or QName) and atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-attribute" target="mlserverdoc">plan:xml-attribute</a>
    * @param name  The attribute name.
    * @param value  The attribute value.
    * @return  a AttributeNodeExpr expression
    */
    public abstract AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnyAtomicTypeExpr value);
    /**
    * This function constructs an XML text node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-text" target="mlserverdoc">plan:xml-text</a>
    * @param value  The value of the XML text node.
    * @return  a TextNodeExpr expression
    */
    public abstract TextNodeExpr xmlText(String value);
    /**
    * This function constructs an XML text node with the specified value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-text" target="mlserverdoc">plan:xml-text</a>
    * @param value  The value of the XML text node.
    * @return  a TextNodeExpr expression
    */
    public abstract TextNodeExpr xmlText(XsAnyAtomicTypeExpr value);
    /**
    * This function constructs an XML comment with the atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-comment" target="mlserverdoc">plan:xml-comment</a>
    * @param content  The comment text.
    * @return  a CommentNodeExpr expression
    */
    public abstract CommentNodeExpr xmlComment(String content);
    /**
    * This function constructs an XML comment with the atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-comment" target="mlserverdoc">plan:xml-comment</a>
    * @param content  The comment text.
    * @return  a CommentNodeExpr expression
    */
    public abstract CommentNodeExpr xmlComment(XsAnyAtomicTypeExpr content);
    /**
    * This function constructs an XML processing instruction with the atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-pi" target="mlserverdoc">plan:xml-pi</a>
    * @param name  The name of the processing instruction.
    * @param value  The value of the processing instruction.
    * @return  a ProcessingInstructionNodeExpr expression
    */
    public abstract ProcessingInstructionNodeExpr xmlPi(String name, String value);
    /**
    * This function constructs an XML processing instruction with the atomic value.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/plan:xml-pi" target="mlserverdoc">plan:xml-pi</a>
    * @param name  The name of the processing instruction.
    * @param value  The value of the processing instruction.
    * @return  a ProcessingInstructionNodeExpr expression
    */
    public abstract ProcessingInstructionNodeExpr xmlPi(XsStringExpr name, XsAnyAtomicTypeExpr value);
    public abstract AttributeNodeSeqExpr xmlAttributeSeq(AttributeNodeExpr... attribute);
    public abstract PlanFunction resolveFunction(String functionName, String modulePath);
    public abstract PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath);
    public interface AccessPlan extends ModifyPlan, PlanBuilderBase.AccessPlanBase {
        /**
    * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string. Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist). <p> In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.  <p> The returned value from this function can be modified by any of the functions described in 'Expression Functions For Processing Column Values' in the <em>Application Developer's Guide</em> 
    * @param column  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn col(String column);
        /**
    * This method identifies a column, where the column name is unique. A qualifier on the column name isn't necessary (and might not exist). In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string. Identifies a column where the column name is unique and a qualifier on the column name isn't necessary (and might not exist). <p> In positions where only a column name can appear, the unqualified column name can also be provided as a string. Qualified column names cannot be provided as a string.  <p> The returned value from this function can be modified by any of the functions described in 'Expression Functions For Processing Column Values' in the <em>Application Developer's Guide</em> 
    * @param column  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PlanColumn object
    */
    public abstract PlanColumn col(XsStringVal column);
    }

    
    public interface ExportablePlan extends Plan, PlanBuilderBase.ExportablePlanBase {
        
    }

    
    public interface ModifyPlan extends PreparePlan, PlanBuilderBase.ModifyPlanBase {
        /**
    * This method restricts the left row set to rows where a row with the same columns and values don't exist in the right row set.
    * @param right  The row set from the left view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan except(ModifyPlan right);
        /**
    * This method collapses a group of rows into a single row. <p> If you want the results to include a column, specify the column either as a grouping key or as one of the aggregates. A groupBy operation without a grouping key outputs a single group reflecting the entire row set. A <code>select</code> operation before a groupBy applies an aggregate to the output from an expression. A <code>select</code> operation after a groupBy to applies an expression to the output from aggregates. 
    * @param keys  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan groupBy(PlanExprColSeq keys);
        /**
    * This method collapses a group of rows into a single row. <p> If you want the results to include a column, specify the column either as a grouping key or as one of the aggregates. A groupBy operation without a grouping key outputs a single group reflecting the entire row set. A <code>select</code> operation before a groupBy applies an aggregate to the output from an expression. A <code>select</code> operation after a groupBy to applies an expression to the output from aggregates. 
    * @param keys  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param aggregates  This parameter specifies one or more columns to be collapsed into a single row. The columns can be identified by either a column name in the row set or a column binding returned from <a>op:as</a>. Specify <code>null</code> to group all of the rows on one or more columndefs.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates);
        /**
    * This method restricts the left row set to rows where a row with the same columns and values exists in the right row set.
    * @param right  The row set from the left view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan intersect(ModifyPlan right);
        /**
    * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
    * @param right  The row set from the left view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinCrossProduct(ModifyPlan right);
        /**
    * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
    * @param right  The row set from the left view.
    * @param condition  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition);
        /**
    * This method yields one output row set that concatenates every left row with every right row. Matches other than equality matches (for instance, greater-than comparisons between keys) can be implemented with a condition on the cross product.
    * @param right  The row set from the left view.
    * @param condition  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition);
        /**
    * This method specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows) as the value. <p> The column for a document URI can be: <ul><li>Indexed in a TDE view.</li><li>Projected from indexed triples by fromTriples <a>op:from-triples</a></li><li>Provided by a lexicon -- especially <a>cts:uri-reference</a> but also a range index where the uris are in elements</li><li>Provided as literals by <a>op:from-literals</a></li><li>Constructed dynamically by an <a>op:as</a> expression.</li></ul> So long as the values of the column are the same as document uris, the document join will work. <p> If the document doesn't exist or the uri or fragment id is null in the row, the row is dropped from the rowset.  <p> If the input row set for the document join is not limited and the document join is not followed immediately by a <a>op:limit</a> operation, an implicit limit of 100 is used. The <a>op:from-literals</a> accessor and <a>op:limit</a> and <a>op:offset-limit</a> methods produce a limited output row set. The output row set for other modifiers is limited if the input row set is limited. The output row set for a composer is limited if both input row sets are limited. The output row set for an implicitly limited document join is limited. 
    * @param docCol  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param sourceCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinDoc(String docCol, String sourceCol);
        /**
    * This method specifies a document column to add to the rows by reading the documents for an existing source column having a value of a document uri (which can be used to read other documents) or a fragment id (which can be used to read the source documents for rows) as the value. <p> The column for a document URI can be: <ul><li>Indexed in a TDE view.</li><li>Projected from indexed triples by fromTriples <a>op:from-triples</a></li><li>Provided by a lexicon -- especially <a>cts:uri-reference</a> but also a range index where the uris are in elements</li><li>Provided as literals by <a>op:from-literals</a></li><li>Constructed dynamically by an <a>op:as</a> expression.</li></ul> So long as the values of the column are the same as document uris, the document join will work. <p> If the document doesn't exist or the uri or fragment id is null in the row, the row is dropped from the rowset.  <p> If the input row set for the document join is not limited and the document join is not followed immediately by a <a>op:limit</a> operation, an implicit limit of 100 is used. The <a>op:from-literals</a> accessor and <a>op:limit</a> and <a>op:offset-limit</a> methods produce a limited output row set. The output row set for other modifiers is limited if the input row set is limited. The output row set for a composer is limited if both input row sets are limited. The output row set for an implicitly limited document join is limited. 
    * @param docCol  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param sourceCol  The document column to add to the rows. This can be a string or column specifying the name of the new column that should have the document as its value.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol);
        /**
    * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an <a>op.fragmentIdCol</a> <a>op:fragment-id-col</a> specifying a fragment id column. <p> If the fragment id column is null in the row, the row is dropped from the rowset. 
    * @param uriCol  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param fragmentIdCol  The document uri. This is either the output from <a>op:col('uri')</a> specifying a document uri column.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinDocUri(String uriCol, String fragmentIdCol);
        /**
    * This method adds a uri column to rows based on an existing fragment id column to identify the source document for each row. The fragmentIdCol must be an <a>op.fragmentIdCol</a> <a>op:fragment-id-col</a> specifying a fragment id column. <p> If the fragment id column is null in the row, the row is dropped from the rowset. 
    * @param uriCol  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param fragmentIdCol  The document uri. This is either the output from <a>op:col('uri')</a> specifying a document uri column.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol);
        /**
    * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. <p> The join performs natural joins between columns with the same identifiers. To prevent inadvertent natural joins, specify a different qualifier for the left or right columns or use different column names for the left and right columns. 
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys);
        /**
    * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. <p> The join performs natural joins between columns with the same identifiers. To prevent inadvertent natural joins, specify a different qualifier for the left or right columns or use different column names for the left and right columns. 
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys);
        /**
    * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. <p> The join performs natural joins between columns with the same identifiers. To prevent inadvertent natural joins, specify a different qualifier for the left or right columns or use different column names for the left and right columns. 
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @param condition  The equijoin from one or more calls to the <a>op:on</a> function.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
        /**
    * This method returns all rows from multiple tables where the join condition is met. In the output row set, each row concatenates one left row and one right row for each match between the keys in the left and right row sets. <p> The join performs natural joins between columns with the same identifiers. To prevent inadvertent natural joins, specify a different qualifier for the left or right columns or use different column names for the left and right columns. 
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @param condition  The equijoin from one or more calls to the <a>op:on</a> function.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
        /**
    * This method yields one output row set with the rows from an inner join as well as rows from the left row set.
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys);
        /**
    * This method yields one output row set with the rows from an inner join as well as rows from the left row set.
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys);
        /**
    * This method yields one output row set with the rows from an inner join as well as rows from the left row set.
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @param condition  The equijoin from one or more calls to the <a>op:on</a> function.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
        /**
    * This method yields one output row set with the rows from an inner join as well as rows from the left row set.
    * @param right  The row set from the left view.
    * @param keys  The row set from the right view.
    * @param condition  The equijoin from one or more calls to the <a>op:on</a> function.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
        /**
    * This method sorts the row set by the specified order definition.
    * @param keys  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan orderBy(PlanSortKeySeq keys);
        /**
    * This method prepares the specified plan for execution as an optional final step before execution. The prepared plan can specify the optimization level any number supported by the builtins (currently expected to be 0 through 2 with 1 as the default).
    * @param optimize  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PreparePlan object
    */
    public abstract PreparePlan prepare(int optimize);
        /**
    * This method prepares the specified plan for execution as an optional final step before execution. The prepared plan can specify the optimization level any number supported by the builtins (currently expected to be 0 through 2 with 1 as the default).
    * @param optimize  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a PreparePlan object
    */
    public abstract PreparePlan prepare(XsIntVal optimize);
        /**
    * This method retrieves the specified columns from one or more tables. If the list of columns is null, all columns other than system columns (such as the fragment id) are returned.
    * @param columns  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan select(PlanExprCol... columns);
        /**
    * This method retrieves the specified columns from one or more tables. If the list of columns is null, all columns other than system columns (such as the fragment id) are returned.
    * @param columns  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan select(PlanExprColSeq columns);
        /**
    * This method retrieves the specified columns from one or more tables. If the list of columns is null, all columns other than system columns (such as the fragment id) are returned.
    * @param columns  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param qualifierName  The columns to select.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan select(PlanExprColSeq columns, String qualifierName);
        /**
    * This method retrieves the specified columns from one or more tables. If the list of columns is null, all columns other than system columns (such as the fragment id) are returned.
    * @param columns  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param qualifierName  The columns to select.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan select(PlanExprColSeq columns, XsStringVal qualifierName);
        /**
    * This method yields all of the rows from the input row sets. Columns that are present only in some input row sets effectively have a null value in the rows from the other row sets. <p> This method is often followed by the <a>op:where-distinct</a> modifier. 
    * @param right  The row set from the left view.
    * @return  a ModifyPlan object
    */
    public abstract ModifyPlan union(ModifyPlan right);
        /**
    * This method removes duplicate rows from the row set.
    
    */
    public abstract ModifyPlan whereDistinct();
    }

    
    public interface Plan extends PlanBuilderBase.PlanBase {
        public abstract Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal);
    }

    
    public interface PreparePlan extends ExportablePlan, PlanBuilderBase.PreparePlanBase {
        /**
    * This method applies the specified function to each row returned by the plan to produce a different result row as with the map method of JavaScript Array. This method can operate on a plan, docuument plan, or prepared plan and should be applied concurrently in multiple threads, preserving the output order.
    * @param func  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ExportablePlan object
    */
    public abstract ExportablePlan map(PlanFunction func);
        /**
    * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the <code>reduce()</code> method of JavaScript Array. <p> The signature of the reducer must be <code>function(previous, row, i, state)</code>, where <code>previous</code> is the seed on the first request and the return from the previous call on subsequent request; <code>row</code> is the current row; <code>i</code> is the zero-based index of the <code>row</code>, and <code>state</code> is an object with an <code>isLast</code> property (which is true on the last iteration but can be set to true on previous iterations for early termination).  <p> The implementation of a <code>op:reduce</code> function can call <a>op:map</a> functions to chain map calls with reduce calls. 
    * @param func  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @return  a ExportablePlan object
    */
    public abstract ExportablePlan reduce(PlanFunction func);
        /**
    * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the <code>reduce()</code> method of JavaScript Array. <p> The signature of the reducer must be <code>function(previous, row, i, state)</code>, where <code>previous</code> is the seed on the first request and the return from the previous call on subsequent request; <code>row</code> is the current row; <code>i</code> is the zero-based index of the <code>row</code>, and <code>state</code> is an object with an <code>isLast</code> property (which is true on the last iteration but can be set to true on previous iterations for early termination).  <p> The implementation of a <code>op:reduce</code> function can call <a>op:map</a> functions to chain map calls with reduce calls. 
    * @param func  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param seed  The function to be appied.
    * @return  a ExportablePlan object
    */
    public abstract ExportablePlan reduce(PlanFunction func, String seed);
        /**
    * This method applies a function or the builtin reducer to each row returned by the plan to produce a single result as with the <code>reduce()</code> method of JavaScript Array. <p> The signature of the reducer must be <code>function(previous, row, i, state)</code>, where <code>previous</code> is the seed on the first request and the return from the previous call on subsequent request; <code>row</code> is the current row; <code>i</code> is the zero-based index of the <code>row</code>, and <code>state</code> is an object with an <code>isLast</code> property (which is true on the last iteration but can be set to true on previous iterations for early termination).  <p> The implementation of a <code>op:reduce</code> function can call <a>op:map</a> functions to chain map calls with reduce calls. 
    * @param func  The Optic Plan, input as a state object by the XQuery => chaining operator.
    * @param seed  The function to be appied.
    * @return  a ExportablePlan object
    */
    public abstract ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed);
    }


}
