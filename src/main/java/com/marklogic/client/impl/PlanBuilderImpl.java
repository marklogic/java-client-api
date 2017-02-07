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
package com.marklogic.client.impl;

import java.util.Arrays;

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

import com.marklogic.client.expression.PlanBuilder;

import com.marklogic.client.expression.PlanBuilder.AccessPlan; 
import com.marklogic.client.expression.PlanBuilder.ExportablePlan; 
import com.marklogic.client.expression.PlanBuilder.ModifyPlan; 
import com.marklogic.client.expression.PlanBuilder.Plan; 
import com.marklogic.client.expression.PlanBuilder.PreparePlan;

// IMPORTANT: Do not edit. This file is generated. 
abstract class PlanBuilderImpl extends PlanBuilderBaseImpl {
    PlanBuilderImpl() {
    }

    // builder methods
    
    @Override
    public XsNumericExpr add(XsNumericExpr... operand) {
        return new XsExprImpl.NumericCallImpl("op", "add", operand);
    }

    
    @Override
    public PlanAggregateColSeq aggregates(PlanAggregateCol... aggregate) {
        return new AggregateColSeqListImpl(aggregate);
    }

    
    @Override
    public XsBooleanExpr and(XsAnyAtomicTypeExpr... operand) {
        return new XsExprImpl.BooleanCallImpl("op", "and", operand);
    }

    
    @Override
    public PlanAggregateCol arrayAggregate(String name, String column) {
        return arrayAggregate((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column });
    }

    
    @Override
    public PlanExprCol as(String column, ItemSeqExpr expression) {
        return as((column == null) ? (PlanColumn) null : col(column), expression);
    }

    
    @Override
    public PlanExprCol as(PlanColumn column, ItemSeqExpr expression) {
        return new ExprColCallImpl("op", "as", new Object[]{ column, expression });
    }

    
    @Override
    public PlanSortKey asc(String column) {
        return asc((column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanSortKey asc(PlanExprCol column) {
        return new SortKeyCallImpl("op", "asc", new Object[]{ column });
    }

    
    @Override
    public PlanAggregateCol avg(String name, String column) {
        return avg((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol avg(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "avg", new Object[]{ name, column });
    }

    
    @Override
    public ItemSeqExpr caseExpr(PlanCase... cases) {
        return new BaseTypeImpl.ItemSeqCallImpl("op", "case", cases);
    }

    
    @Override
    public PlanColumn col(String column) {
        return col((column == null) ? (XsStringVal) null : xs.string(column));
    }

    
    @Override
    public PlanColumn col(XsStringVal column) {
        return new ColumnCallImpl("op", "col", new Object[]{ column });
    }

    
    @Override
    public PlanExprColSeq cols(String... col) {
        return cols(
                (PlanExprCol[]) Arrays.stream(col)
                .map(item -> exprCol(item))
                .toArray(size -> new PlanExprCol[size])
                );
    }

    
    @Override
    public PlanExprColSeq cols(PlanExprCol... col) {
        return new ExprColSeqListImpl(col);
    }

    
    @Override
    public PlanAggregateCol count(String name) {
        return count((name == null) ? (PlanColumn) null : col(name));
    }

    
    @Override
    public PlanAggregateCol count(PlanColumn name) {
        return new AggregateColCallImpl("op", "count", new Object[]{ name });
    }

    
    @Override
    public PlanAggregateCol count(String name, String column) {
        return count((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol count(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "count", new Object[]{ name, column });
    }

    
    @Override
    public PlanSortKey desc(String column) {
        return desc((column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanSortKey desc(PlanExprCol column) {
        return new SortKeyCallImpl("op", "desc", new Object[]{ column });
    }

    
    @Override
    public XsNumericExpr divide(XsNumericExpr operand1, XsNumericExpr operand2) {
        return new XsExprImpl.NumericCallImpl("op", "divide", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public PlanCase elseExpr(ItemExpr value) {
        return new CaseCallImpl("op", "else", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr eq(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "eq", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public PlanSystemColumn fragmentIdCol(String column) {
        return fragmentIdCol((column == null) ? (XsStringVal) null : xs.string(column));
    }

    
    @Override
    public PlanSystemColumn fragmentIdCol(XsStringVal column) {
        return new SystemColumnCallImpl("op", "fragment-id-col", new Object[]{ column });
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePattern... patterns) {
        return fromTriples(new TriplePatternSeqListImpl(patterns));
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePatternSeq patterns) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns });
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName) {
        return fromTriples(patterns, (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns, qualifierName });
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris) {
        return fromTriples(patterns, (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName), (graphIris == null) ? (XsStringVal) null : xs.string(graphIris));
    }

    
    @Override
    public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns, qualifierName, graphIris });
    }

    
    @Override
    public AccessPlan fromView(String schema, String view) {
        return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view));
    }

    
    @Override
    public AccessPlan fromView(XsStringVal schema, XsStringVal view) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view });
    }

    
    @Override
    public AccessPlan fromView(String schema, String view, String qualifierName) {
        return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
    }

    
    @Override
    public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view, qualifierName });
    }

    
    @Override
    public AccessPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols) {
        return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName), sysCols);
    }

    
    @Override
    public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols) {
        return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view, qualifierName, sysCols });
    }

    
    @Override
    public XsBooleanExpr ge(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "ge", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public PlanSystemColumn graphCol(String column) {
        return graphCol((column == null) ? (XsStringVal) null : xs.string(column));
    }

    
    @Override
    public PlanSystemColumn graphCol(XsStringVal column) {
        return new SystemColumnCallImpl("op", "graph-col", new Object[]{ column });
    }

    
    @Override
    public PlanAggregateCol groupConcat(String name, String column) {
        return groupConcat((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "group-concat", new Object[]{ name, column });
    }

    
    @Override
    public XsBooleanExpr gt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "gt", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public XsBooleanExpr isDefined(ItemExpr operand) {
        return new XsExprImpl.BooleanCallImpl("op", "is-defined", new Object[]{ operand });
    }

    
    @Override
    public PlanJoinKeySeq joinKeys(PlanJoinKey... key) {
        return new JoinKeySeqListImpl(key);
    }

    
    @Override
    public ArrayNodeExpr jsonArray(JsonContentNodeExpr... property) {
        return new BaseTypeImpl.ArrayNodeCallImpl("op", "json-array", property);
    }

    
    @Override
    public BooleanNodeExpr jsonBoolean(boolean value) {
        return jsonBoolean(xs.booleanVal(value));
    }

    
    @Override
    public BooleanNodeExpr jsonBoolean(XsBooleanExpr value) {
        return new BaseTypeImpl.BooleanNodeCallImpl("op", "json-boolean", new Object[]{ value });
    }

    
    @Override
    public DocumentNodeExpr jsonDocument(JsonRootNodeExpr root) {
        return new BaseTypeImpl.DocumentNodeCallImpl("op", "json-document", new Object[]{ root });
    }

    
    @Override
    public NullNodeExpr jsonNull() {
        return new BaseTypeImpl.NullNodeCallImpl("op", "json-null", new Object[]{  });
    }

    
    @Override
    public NumberNodeExpr jsonNumber(double value) {
        return jsonNumber(xs.doubleVal(value));
    }

    
    @Override
    public NumberNodeExpr jsonNumber(XsNumericExpr value) {
        return new BaseTypeImpl.NumberNodeCallImpl("op", "json-number", new Object[]{ value });
    }

    
    @Override
    public ObjectNodeExpr jsonObject(PlanJsonProperty... property) {
        return new BaseTypeImpl.ObjectNodeCallImpl("op", "json-object", property);
    }

    
    @Override
    public TextNodeExpr jsonString(String value) {
        return jsonString((value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public TextNodeExpr jsonString(XsAnyAtomicTypeExpr value) {
        return new BaseTypeImpl.TextNodeCallImpl("op", "json-string", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr le(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "le", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public XsBooleanExpr lt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "lt", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public PlanAggregateCol max(String name, String column) {
        return max((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol max(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "max", new Object[]{ name, column });
    }

    
    @Override
    public PlanAggregateCol min(String name, String column) {
        return min((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol min(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "min", new Object[]{ name, column });
    }

    
    @Override
    public XsNumericExpr modulo(double left, double right) {
        return modulo(xs.doubleVal(left), xs.doubleVal(right));
    }

    
    @Override
    public XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right) {
        return new XsExprImpl.NumericCallImpl("op", "modulo", new Object[]{ left, right });
    }

    
    @Override
    public XsNumericExpr multiply(XsNumericExpr... operand) {
        return new XsExprImpl.NumericCallImpl("op", "multiply", operand);
    }

    
    @Override
    public XsBooleanExpr ne(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2) {
        return new XsExprImpl.BooleanCallImpl("op", "ne", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public XsBooleanExpr not(XsAnyAtomicTypeExpr operand) {
        return new XsExprImpl.BooleanCallImpl("op", "not", new Object[]{ operand });
    }

    
    @Override
    public PlanTriplePositionSeq objects(PlanTriplePosition... object) {
        return new TriplePositionSeqListImpl(object);
    }

    
    @Override
    public PlanJoinKey on(String left, String right) {
        return on((left == null) ? (PlanExprCol) null : exprCol(left), (right == null) ? (PlanExprCol) null : exprCol(right));
    }

    
    @Override
    public PlanJoinKey on(PlanExprCol left, PlanExprCol right) {
        return new JoinKeyCallImpl("op", "on", new Object[]{ left, right });
    }

    
    @Override
    public XsBooleanExpr or(XsAnyAtomicTypeExpr... operand) {
        return new XsExprImpl.BooleanCallImpl("op", "or", operand);
    }

    
    @Override
    public PlanParamExpr param(String name) {
        return param((name == null) ? (XsStringVal) null : xs.string(name));
    }

    
    @Override
    public PlanParamExpr param(XsStringVal name) {
        return new ParamCallImpl("op", "param", new Object[]{ name });
    }

    
    @Override
    public PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects) {
        return new TriplePatternCallImpl("op", "pattern", new Object[]{ subjects, predicates, objects });
    }

    
    @Override
    public PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects, PlanSystemColumnSeq sysCols) {
        return new TriplePatternCallImpl("op", "pattern", new Object[]{ subjects, predicates, objects, sysCols });
    }

    
    @Override
    public PlanTriplePatternSeq patterns(PlanTriplePattern... pattern) {
        return new TriplePatternSeqListImpl(pattern);
    }

    
    @Override
    public PlanTriplePositionSeq predicates(PlanTriplePosition... predicate) {
        return new TriplePositionSeqListImpl(predicate);
    }

    
    @Override
    public PlanJsonProperty prop(String key, JsonContentNodeExpr value) {
        return prop((key == null) ? (XsStringExpr) null : xs.string(key), value);
    }

    
    @Override
    public PlanJsonProperty prop(XsStringExpr key, JsonContentNodeExpr value) {
        return new JsonPropertyCallImpl("op", "prop", new Object[]{ key, value });
    }

    
    @Override
    public PlanFunction resolveFunction(String functionName, String modulePath) {
        return resolveFunction((functionName == null) ? (XsQNameVal) null : xs.QName(functionName), (modulePath == null) ? (XsStringVal) null : xs.string(modulePath));
    }

    
    @Override
    public PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath) {
        return new FunctionCallImpl("op", "resolve-function", new Object[]{ functionName, modulePath });
    }

    
    @Override
    public PlanAggregateCol sample(String name, String column) {
        return sample((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol sample(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "sample", new Object[]{ name, column });
    }

    
    @Override
    public PlanColumn schemaCol(String schema, String view, String column) {
        return schemaCol((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column));
    }

    
    @Override
    public PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column) {
        return new ColumnCallImpl("op", "schema-col", new Object[]{ schema, view, column });
    }

    
    @Override
    public PlanAggregateCol sequenceAggregate(String name, String column) {
        return sequenceAggregate((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column });
    }

    
    @Override
    public PlanSortKeySeq sortKeys(PlanSortKey... key) {
        return new SortKeySeqListImpl(key);
    }

    
    @Override
    public PlanTriplePositionSeq subjects(PlanTriplePosition... subject) {
        return new TriplePositionSeqListImpl(subject);
    }

    
    @Override
    public XsNumericExpr subtract(XsNumericExpr operand1, XsNumericExpr operand2) {
        return new XsExprImpl.NumericCallImpl("op", "subtract", new Object[]{ operand1, operand2 });
    }

    
    @Override
    public PlanAggregateCol sum(String name, String column) {
        return sum((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
    }

    
    @Override
    public PlanAggregateCol sum(PlanColumn name, PlanExprCol column) {
        return new AggregateColCallImpl("op", "sum", new Object[]{ name, column });
    }

    
    @Override
    public PlanAggregateCol uda(String name, String column, String module, String function) {
        return uda((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column), (module == null) ? (XsStringVal) null : xs.string(module), (function == null) ? (XsStringVal) null : xs.string(function));
    }

    
    @Override
    public PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function) {
        return new AggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function });
    }

    
    @Override
    public PlanAggregateCol uda(String name, String column, String module, String function, String arg) {
        return uda((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column), (module == null) ? (XsStringVal) null : xs.string(module), (function == null) ? (XsStringVal) null : xs.string(function), (arg == null) ? (XsAnyAtomicTypeVal) null : xs.string(arg));
    }

    
    @Override
    public PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg) {
        return new AggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function, arg });
    }

    
    @Override
    public PlanColumn viewCol(String view, String column) {
        return viewCol((view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column));
    }

    
    @Override
    public PlanColumn viewCol(XsStringVal view, XsStringVal column) {
        return new ColumnCallImpl("op", "view-col", new Object[]{ view, column });
    }

    
    @Override
    public PlanCase when(boolean condition, ItemExpr... value) {
        return when(xs.booleanVal(condition), new BaseTypeImpl.ItemSeqListImpl(value));
    }

    
    @Override
    public PlanCase when(XsBooleanExpr condition, ItemExpr... value) {
        return new CaseCallImpl("op", "when", new Object[]{ condition, new BaseTypeImpl.ItemSeqListImpl(value) });
    }

    
    @Override
    public AttributeNodeExpr xmlAttribute(String name, String value) {
        return xmlAttribute((name == null) ? (XsQNameExpr) null : xs.QName(name), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnyAtomicTypeExpr value) {
        return new BaseTypeImpl.AttributeNodeCallImpl("op", "xml-attribute", new Object[]{ name, value });
    }

    
    @Override
    public AttributeNodeSeqExpr xmlAttributes(AttributeNodeExpr... attribute) {
        return new BaseTypeImpl.AttributeNodeSeqListImpl(attribute);
    }

    
    @Override
    public CommentNodeExpr xmlComment(String content) {
        return xmlComment((content == null) ? (XsAnyAtomicTypeExpr) null : xs.string(content));
    }

    
    @Override
    public CommentNodeExpr xmlComment(XsAnyAtomicTypeExpr content) {
        return new BaseTypeImpl.CommentNodeCallImpl("op", "xml-comment", new Object[]{ content });
    }

    
    @Override
    public DocumentNodeExpr xmlDocument(XmlRootNodeExpr root) {
        return new BaseTypeImpl.DocumentNodeCallImpl("op", "xml-document", new Object[]{ root });
    }

    
    @Override
    public ElementNodeExpr xmlElement(String name) {
        return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name));
    }

    
    @Override
    public ElementNodeExpr xmlElement(XsQNameExpr name) {
        return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name });
    }

    
    @Override
    public ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes) {
        return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name), new BaseTypeImpl.AttributeNodeSeqListImpl(attributes));
    }

    
    @Override
    public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes) {
        return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes });
    }

    
    @Override
    public ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
        return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name), attributes, new BaseTypeImpl.XmlContentNodeSeqListImpl(content));
    }

    
    @Override
    public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
        return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes, new BaseTypeImpl.XmlContentNodeSeqListImpl(content) });
    }

    
    @Override
    public ProcessingInstructionNodeExpr xmlPi(String name, String value) {
        return xmlPi((name == null) ? (XsStringExpr) null : xs.string(name), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public ProcessingInstructionNodeExpr xmlPi(XsStringExpr name, XsAnyAtomicTypeExpr value) {
        return new BaseTypeImpl.ProcessingInstructionNodeCallImpl("op", "xml-pi", new Object[]{ name, value });
    }

    
    @Override
    public TextNodeExpr xmlText(String value) {
        return xmlText((value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
    }

    
    @Override
    public TextNodeExpr xmlText(XsAnyAtomicTypeExpr value) {
        return new BaseTypeImpl.TextNodeCallImpl("op", "xml-text", new Object[]{ value });
    }

    
    @Override
    public NodeSeqExpr xpath(String column, String path) {
        return xpath((column == null) ? (PlanColumn) null : col(column), (path == null) ? (XsStringExpr) null : xs.string(path));
    }

    
    @Override
    public NodeSeqExpr xpath(PlanColumn column, XsStringExpr path) {
        return new BaseTypeImpl.NodeSeqCallImpl("op", "xpath", new Object[]{ column, path });
    }


    // external type implementations
    
    static class AggregateColSeqListImpl extends PlanSeqListImpl implements PlanAggregateColSeq {
        AggregateColSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class AggregateColSeqCallImpl extends PlanCallImpl implements PlanAggregateColSeq {
        AggregateColSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class AggregateColCallImpl extends PlanCallImpl implements PlanAggregateCol {
        AggregateColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class CaseSeqListImpl extends PlanSeqListImpl implements PlanCaseSeq {
        CaseSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class CaseSeqCallImpl extends PlanCallImpl implements PlanCaseSeq {
        CaseSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class CaseCallImpl extends PlanCallImpl implements PlanCase {
        CaseCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ColumnSeqListImpl extends PlanSeqListImpl implements PlanColumnSeq {
        ColumnSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class ColumnSeqCallImpl extends PlanCallImpl implements PlanColumnSeq {
        ColumnSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ColumnCallImpl extends PlanCallImpl implements PlanColumn {
        ColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ExprColSeqListImpl extends PlanSeqListImpl implements PlanExprColSeq {
        ExprColSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class ExprColSeqCallImpl extends PlanCallImpl implements PlanExprColSeq {
        ExprColSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ExprColCallImpl extends PlanCallImpl implements PlanExprCol {
        ExprColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class FunctionSeqListImpl extends PlanSeqListImpl implements PlanFunctionSeq {
        FunctionSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class FunctionSeqCallImpl extends PlanCallImpl implements PlanFunctionSeq {
        FunctionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class FunctionCallImpl extends PlanCallImpl implements PlanFunction {
        FunctionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class JoinKeySeqListImpl extends PlanSeqListImpl implements PlanJoinKeySeq {
        JoinKeySeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class JoinKeySeqCallImpl extends PlanCallImpl implements PlanJoinKeySeq {
        JoinKeySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class JoinKeyCallImpl extends PlanCallImpl implements PlanJoinKey {
        JoinKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class JsonPropertySeqListImpl extends PlanSeqListImpl implements PlanJsonPropertySeq {
        JsonPropertySeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class JsonPropertySeqCallImpl extends PlanCallImpl implements PlanJsonPropertySeq {
        JsonPropertySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class JsonPropertyCallImpl extends PlanCallImpl implements PlanJsonProperty {
        JsonPropertyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ParamSeqListImpl extends PlanSeqListImpl implements PlanParamSeqExpr {
        ParamSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class ParamSeqCallImpl extends PlanCallImpl implements PlanParamSeqExpr {
        ParamSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ParamCallImpl extends PlanCallImpl implements PlanParamExpr {
        ParamCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ParamBindingSeqListImpl extends PlanSeqListImpl implements PlanParamBindingSeqVal {
        ParamBindingSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class ParamBindingSeqCallImpl extends PlanCallImpl implements PlanParamBindingSeqVal {
        ParamBindingSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class ParamBindingCallImpl extends PlanCallImpl implements PlanParamBindingVal {
        ParamBindingCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class SortKeySeqListImpl extends PlanSeqListImpl implements PlanSortKeySeq {
        SortKeySeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class SortKeySeqCallImpl extends PlanCallImpl implements PlanSortKeySeq {
        SortKeySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class SortKeyCallImpl extends PlanCallImpl implements PlanSortKey {
        SortKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class SystemColumnSeqListImpl extends ColumnSeqListImpl implements PlanSystemColumnSeq {
        SystemColumnSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class SystemColumnSeqCallImpl extends ColumnCallImpl implements PlanSystemColumnSeq {
        SystemColumnSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class SystemColumnCallImpl extends ColumnCallImpl implements PlanSystemColumn {
        SystemColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class TriplePatternSeqListImpl extends PlanSeqListImpl implements PlanTriplePatternSeq {
        TriplePatternSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class TriplePatternSeqCallImpl extends PlanCallImpl implements PlanTriplePatternSeq {
        TriplePatternSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class TriplePatternCallImpl extends PlanCallImpl implements PlanTriplePattern {
        TriplePatternCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class TriplePositionSeqListImpl extends PlanSeqListImpl implements PlanTriplePositionSeq {
        TriplePositionSeqListImpl(Object[] items) {
            super(items);
        }
    }

    
    static class TriplePositionSeqCallImpl extends PlanCallImpl implements PlanTriplePositionSeq {
        TriplePositionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }

    
    static class TriplePositionCallImpl extends PlanCallImpl implements PlanTriplePosition {
        TriplePositionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }


    // nested type implementations
    
    static abstract class AccessPlanImpl extends PlanBuilderSubImpl.ModifyPlanSubImpl implements AccessPlan {
        AccessPlanImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(null, fnPrefix, fnName, fnArgs);
        }

        
    @Override
    public PlanColumn col(String column) {
        return col((column == null) ? (XsStringVal) null : xs.string(column));
    }

        
    @Override
    public PlanColumn col(XsStringVal column) {
        return new ColumnCallImpl("op", "col", new Object[]{ column });
    }

    }

    
    static abstract class ExportablePlanImpl extends PlanBuilderSubImpl.PlanSubImpl implements ExportablePlan {
        ExportablePlanImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
        }

        
    }

    
    static abstract class ModifyPlanImpl extends PlanBuilderSubImpl.PreparePlanSubImpl implements ModifyPlan {
        ModifyPlanImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
        }

        
    @Override
    public ModifyPlan except(ModifyPlan right) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "except", new Object[]{ right });
    }

        
    @Override
    public ModifyPlan groupBy(PlanExprColSeq keys) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "group-by", new Object[]{ keys });
    }

        
    @Override
    public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "group-by", new Object[]{ keys, aggregates });
    }

        
    @Override
    public ModifyPlan intersect(ModifyPlan right) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "intersect", new Object[]{ right });
    }

        
    @Override
    public ModifyPlan joinCrossProduct(ModifyPlan right) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-cross-product", new Object[]{ right });
    }

        
    @Override
    public ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition) {
        return joinCrossProduct(right, xs.booleanVal(condition));
    }

        
    @Override
    public ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-cross-product", new Object[]{ right, condition });
    }

        
    @Override
    public ModifyPlan joinDoc(String docCol, String sourceCol) {
        return joinDoc((docCol == null) ? (PlanColumn) null : col(docCol), (sourceCol == null) ? (PlanColumn) null : col(sourceCol));
    }

        
    @Override
    public ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc", new Object[]{ docCol, sourceCol });
    }

        
    @Override
    public ModifyPlan joinDocUri(String uriCol, String fragmentIdCol) {
        return joinDocUri((uriCol == null) ? (PlanColumn) null : col(uriCol), (fragmentIdCol == null) ? (PlanColumn) null : col(fragmentIdCol));
    }

        
    @Override
    public ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc-uri", new Object[]{ uriCol, fragmentIdCol });
    }

        
    @Override
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys) {
        return joinInner(right, new JoinKeySeqListImpl(keys));
    }

        
    @Override
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-inner", new Object[]{ right, keys });
    }

        
    @Override
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
        return joinInner(right, keys, xs.booleanVal(condition));
    }

        
    @Override
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-inner", new Object[]{ right, keys, condition });
    }

        
    @Override
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys) {
        return joinLeftOuter(right, new JoinKeySeqListImpl(keys));
    }

        
    @Override
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right, keys });
    }

        
    @Override
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
        return joinLeftOuter(right, keys, xs.booleanVal(condition));
    }

        
    @Override
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right, keys, condition });
    }

        
    @Override
    public ModifyPlan orderBy(PlanSortKeySeq keys) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "order-by", new Object[]{ keys });
    }

        
    @Override
    public PreparePlan prepare(int optimize) {
        return prepare(xs.intVal(optimize));
    }

        
    @Override
    public PreparePlan prepare(XsIntVal optimize) {
        return new PlanBuilderSubImpl.PreparePlanSubImpl(this, "op", "prepare", new Object[]{ optimize });
    }

        
    @Override
    public ModifyPlan select(PlanExprCol... columns) {
        return select(new ExprColSeqListImpl(columns));
    }

        
    @Override
    public ModifyPlan select(PlanExprColSeq columns) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "select", new Object[]{ columns });
    }

        
    @Override
    public ModifyPlan select(PlanExprColSeq columns, String qualifierName) {
        return select(columns, (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
    }

        
    @Override
    public ModifyPlan select(PlanExprColSeq columns, XsStringVal qualifierName) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "select", new Object[]{ columns, qualifierName });
    }

        
    @Override
    public ModifyPlan union(ModifyPlan right) {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "union", new Object[]{ right });
    }

        
    @Override
    public ModifyPlan whereDistinct() {
        return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "where-distinct", new Object[]{  });
    }

    }

    
    static abstract class PlanImpl extends PlanBaseImpl implements Plan {
        PlanImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
        }

        
    @Override
    public Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal) {
        return new PlanBuilderSubImpl.PlanSubImpl(this, "op", "bind-param", new Object[]{ param, literal });
    }

    }

    
    static abstract class PreparePlanImpl extends PlanBuilderSubImpl.ExportablePlanSubImpl implements PreparePlan {
        PreparePlanImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
        }

        
    @Override
    public ExportablePlan map(PlanFunction func) {
        return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "map", new Object[]{ func });
    }

        
    @Override
    public ExportablePlan reduce(PlanFunction func) {
        return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "reduce", new Object[]{ func });
    }

        
    @Override
    public ExportablePlan reduce(PlanFunction func, String seed) {
        return reduce(func, (seed == null) ? (XsAnyAtomicTypeVal) null : xs.string(seed));
    }

        
    @Override
    public ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed) {
        return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "reduce", new Object[]{ func, seed });
    }

    }


}
