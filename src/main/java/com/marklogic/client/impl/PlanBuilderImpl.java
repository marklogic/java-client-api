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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

import com.marklogic.client.expression.PlanBuilder;

import com.marklogic.client.expression.CtsExpr;
import com.marklogic.client.impl.CtsExprImpl; 
import com.marklogic.client.expression.FnExpr;
import com.marklogic.client.impl.FnExprImpl; 
import com.marklogic.client.expression.JsonExpr;
import com.marklogic.client.impl.JsonExprImpl; 
import com.marklogic.client.expression.MapExpr;
import com.marklogic.client.impl.MapExprImpl; 
import com.marklogic.client.expression.MathExpr;
import com.marklogic.client.impl.MathExprImpl; 
import com.marklogic.client.expression.RdfExpr;
import com.marklogic.client.impl.RdfExprImpl; 
import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.impl.SemExprImpl; 
import com.marklogic.client.expression.SpellExpr;
import com.marklogic.client.impl.SpellExprImpl; 
import com.marklogic.client.expression.SqlExpr;
import com.marklogic.client.impl.SqlExprImpl; 
import com.marklogic.client.expression.XdmpExpr;
import com.marklogic.client.impl.XdmpExprImpl; 
import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.impl.XsExprImpl; import com.marklogic.client.type.CtsQueryExpr;
 import com.marklogic.client.type.CtsReferenceExpr;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.PlanAggregateCol;
 import com.marklogic.client.type.PlanAggregateColSeq;
 import com.marklogic.client.type.PlanAggregateOptions;
 import com.marklogic.client.type.PlanAggregateOptionsSeq;
 import com.marklogic.client.type.PlanColumn;
 import com.marklogic.client.type.PlanColumnSeq;
 import com.marklogic.client.type.PlanExprCol;
 import com.marklogic.client.type.PlanExprColSeq;
 import com.marklogic.client.type.PlanFunction;
 import com.marklogic.client.type.PlanFunctionSeq;
 import com.marklogic.client.type.PlanGroupConcatOptions;
 import com.marklogic.client.type.PlanGroupConcatOptionsSeq;
 import com.marklogic.client.type.PlanJoinKey;
 import com.marklogic.client.type.PlanJoinKeySeq;
 import com.marklogic.client.type.PlanParam;
 import com.marklogic.client.type.PlanParamSeq;
 import com.marklogic.client.type.PlanSortKey;
 import com.marklogic.client.type.PlanSortKeySeq;
 import com.marklogic.client.type.PlanSystemColumn;
 import com.marklogic.client.type.PlanSystemColumnSeq;
 import com.marklogic.client.type.PlanTripleIri;
 import com.marklogic.client.type.PlanTripleIriSeq;
 import com.marklogic.client.type.PlanTripleOptions;
 import com.marklogic.client.type.PlanTripleOptionsSeq;
 import com.marklogic.client.type.PlanTriplePattern;
 import com.marklogic.client.type.PlanTriplePatternSeq;
 import com.marklogic.client.type.PlanTripleVal;
 import com.marklogic.client.type.PlanTripleValSeq;
 import com.marklogic.client.type.RdfLangStringExpr;
 import com.marklogic.client.type.RdfLangStringParam;
 import com.marklogic.client.type.SemIriExpr;
 import com.marklogic.client.type.SemIriParam;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeParam;
 import com.marklogic.client.type.XsAnyAtomicTypeVal;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsAnyURIParam;
 import com.marklogic.client.type.XsBase64BinaryExpr;
 import com.marklogic.client.type.XsBase64BinaryParam;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsBooleanParam;
 import com.marklogic.client.type.XsByteExpr;
 import com.marklogic.client.type.XsByteParam;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsDateParam;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsDateTimeParam;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.XsDayTimeDurationParam;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.XsDecimalParam;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDoubleParam;
 import com.marklogic.client.type.XsFloatExpr;
 import com.marklogic.client.type.XsFloatParam;
 import com.marklogic.client.type.XsGDayExpr;
 import com.marklogic.client.type.XsGDayParam;
 import com.marklogic.client.type.XsGMonthDayExpr;
 import com.marklogic.client.type.XsGMonthDayParam;
 import com.marklogic.client.type.XsGMonthExpr;
 import com.marklogic.client.type.XsGMonthParam;
 import com.marklogic.client.type.XsGYearExpr;
 import com.marklogic.client.type.XsGYearMonthExpr;
 import com.marklogic.client.type.XsGYearMonthParam;
 import com.marklogic.client.type.XsGYearParam;
 import com.marklogic.client.type.XsHexBinaryExpr;
 import com.marklogic.client.type.XsHexBinaryParam;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsIntegerParam;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.XsIntParam;
 import com.marklogic.client.type.XsLongExpr;
 import com.marklogic.client.type.XsLongParam;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsQNameParam;
 import com.marklogic.client.type.XsShortExpr;
 import com.marklogic.client.type.XsShortParam;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringParam;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsTimeParam;
 import com.marklogic.client.type.XsUnsignedByteExpr;
 import com.marklogic.client.type.XsUnsignedByteParam;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsUnsignedIntParam;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsUnsignedLongParam;
 import com.marklogic.client.type.XsUnsignedShortExpr;
 import com.marklogic.client.type.XsUnsignedShortParam;
 import com.marklogic.client.type.XsUntypedAtomicExpr;
 import com.marklogic.client.type.XsUntypedAtomicParam;
 import com.marklogic.client.type.XsYearMonthDurationExpr;
 import com.marklogic.client.type.XsYearMonthDurationParam;

import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.SemStoreExpr;

// IMPORTANT: Do not edit. This file is generated. 
public class PlanBuilderImpl extends PlanBuilderBase {
    public PlanBuilderImpl(
        CtsExpr cts, FnExpr fn, JsonExpr json, MapExpr map, MathExpr math, RdfExpr rdf, SemExpr sem, SpellExpr spell, SqlExpr sql, XdmpExpr xdmp, XsExpr xs
        ) {
        super(
            cts, fn, json, map, math, rdf, sem, spell, sql, xdmp, xs
            );
    }

    @Override
        public XsNumericExpr add(XsNumericExpr... list) {
        return new XsExprImpl.XsNumericCallImpl("op", "add", list);
    }
    @Override
        public PlanAggregateColSeq aggregates(PlanAggregateCol... aggregate) {
        return new PlanAggregateColSeqListImpl(aggregate);
    }
    @Override
        public XsBooleanExpr and(XsBooleanExpr... list) {
        return new XsExprImpl.XsBooleanCallImpl("op", "and", list);
    }
    @Override
        public PlanAggregateCol arrayAggregate(String name, String column) {
        return arrayAggregate(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol arrayAggregate(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol arrayAggregate(String name, String column, PlanAggregateOptions options) {
        return arrayAggregate(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol arrayAggregate(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column, options });
    }
    @Override
        public PlanExprCol as(String column, ItemSeqExpr expression) {
        return as(col(column), expression); 
    }
    @Override
        public PlanExprCol as(PlanColumn column, ItemSeqExpr expression) {
        return new PlanExprColCallImpl("op", "as", new Object[]{ column, expression });
    }
    @Override
        public PlanSortKey asc(String column) {
        return asc(col(column)); 
    }
    @Override
        public PlanSortKey asc(PlanExprCol column) {
        return new PlanSortKeyCallImpl("op", "asc", new Object[]{ column });
    }
    @Override
        public PlanAggregateCol avg(String name, String column) {
        return avg(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol avg(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "avg", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol avg(String name, String column, PlanAggregateOptions options) {
        return avg(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol avg(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "avg", new Object[]{ name, column, options });
    }
    @Override
        public PlanColumn col(String column) {
        return col(xs.string(column)); 
    }
    @Override
        public PlanColumn col(XsStringParam column) {
        return new PlanColumnCallImpl("op", "col", new Object[]{ column });
    }
    @Override
        public PlanExprColSeq cols(String... col) {
        return cols((PlanExprCol[]) Arrays.stream(col)
            .map(item -> col(item))
            .toArray(size -> new PlanExprCol[size])); 
    }
    @Override
        public PlanExprColSeq cols(PlanExprCol... col) {
        return new PlanExprColSeqListImpl(col);
    }
    @Override
        public PlanAggregateCol count(String name) {
        return count(col(name)); 
    }
    @Override
        public PlanAggregateCol count(PlanExprCol name) {
        return new PlanAggregateColCallImpl("op", "count", new Object[]{ name });
    }
    @Override
        public PlanAggregateCol count(String name, String column) {
        return count(col(name), (column == null) ? null : col(column)); 
    }
    @Override
        public PlanAggregateCol count(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "count", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol count(String name, String column, PlanAggregateOptions options) {
        return count(col(name), (column == null) ? null : col(column), options); 
    }
    @Override
        public PlanAggregateCol count(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "count", new Object[]{ name, column, options });
    }
    @Override
        public PlanSortKey desc(String column) {
        return desc(col(column)); 
    }
    @Override
        public PlanSortKey desc(PlanExprCol column) {
        return new PlanSortKeyCallImpl("op", "desc", new Object[]{ column });
    }
    @Override
        public XsNumericExpr divide(XsNumericExpr left, XsNumericExpr right) {
        return new XsExprImpl.XsNumericCallImpl("op", "divide", new Object[]{ left, right });
    }
    @Override
        public XsBooleanExpr eq(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "eq", new Object[]{ left, right });
    }
    @Override
        public PlanSystemColumn fragmentIdCol(String column) {
        return fragmentIdCol(xs.string(column)); 
    }
    @Override
        public PlanSystemColumn fragmentIdCol(XsStringParam column) {
        return new PlanSystemColumnCallImpl("op", "fragment-id-col", new Object[]{ column });
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePattern... patterns) {
        return fromTriples(new PlanTriplePatternSeqListImpl(patterns)); 
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns) {
        return new QualifiedPlanCallImpl(this, "op", "from-triples", new Object[]{ patterns });
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName) {
        return fromTriples(patterns, (qualifierName == null) ? null : xs.string(qualifierName)); 
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName) {
        return new QualifiedPlanCallImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName });
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIri) {
        return fromTriples(patterns, (qualifierName == null) ? null : xs.string(qualifierName), (graphIri == null) ? null : xs.string(graphIri)); 
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName, XsStringParam graphIri) {
        return new QualifiedPlanCallImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName, graphIri });
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIri, PlanTripleOptions options) {
        return fromTriples(patterns, (qualifierName == null) ? null : xs.string(qualifierName), (graphIri == null) ? null : xs.string(graphIri), options); 
    }
    @Override
        public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName, XsStringParam graphIri, PlanTripleOptions options) {
        return new QualifiedPlanCallImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName, graphIri, options });
    }
    @Override
        public ViewPlan fromView(String schema, String view) {
        return fromView(xs.string(schema), xs.string(view)); 
    }
    @Override
        public ViewPlan fromView(XsStringParam schema, XsStringParam view) {
        return new ViewPlanCallImpl(this, "op", "from-view", new Object[]{ schema, view });
    }
    @Override
        public ViewPlan fromView(String schema, String view, String qualifierName) {
        return fromView(xs.string(schema), xs.string(view), (qualifierName == null) ? null : xs.string(qualifierName)); 
    }
    @Override
        public ViewPlan fromView(XsStringParam schema, XsStringParam view, XsStringParam qualifierName) {
        return new ViewPlanCallImpl(this, "op", "from-view", new Object[]{ schema, view, qualifierName });
    }
    @Override
        public ViewPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols) {
        return fromView(xs.string(schema), xs.string(view), (qualifierName == null) ? null : xs.string(qualifierName), sysCols); 
    }
    @Override
        public ViewPlan fromView(XsStringParam schema, XsStringParam view, XsStringParam qualifierName, PlanSystemColumn sysCols) {
        return new ViewPlanCallImpl(this, "op", "from-view", new Object[]{ schema, view, qualifierName, sysCols });
    }
    @Override
        public XsBooleanExpr ge(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "ge", new Object[]{ left, right });
    }
    @Override
        public PlanSystemColumn graphCol(String column) {
        return graphCol(xs.string(column)); 
    }
    @Override
        public PlanSystemColumn graphCol(XsStringParam column) {
        return new PlanSystemColumnCallImpl("op", "graph-col", new Object[]{ column });
    }
    @Override
        public PlanAggregateCol groupConcat(String name, String column) {
        return groupConcat(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol groupConcat(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "group-concat", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol groupConcat(String name, String column, PlanGroupConcatOptions options) {
        return groupConcat(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol groupConcat(PlanExprCol name, PlanExprCol column, PlanGroupConcatOptions options) {
        return new PlanAggregateColCallImpl("op", "group-concat", new Object[]{ name, column, options });
    }
    @Override
        public XsBooleanExpr gt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "gt", new Object[]{ left, right });
    }
    @Override
        public XsBooleanExpr isDefined(ItemExpr expression) {
        return new XsExprImpl.XsBooleanCallImpl("op", "is-defined", new Object[]{ expression });
    }
    @Override
        public PlanJoinKeySeq joinKeys(PlanJoinKey... key) {
        return new PlanJoinKeySeqListImpl(key);
    }
    @Override
        public XsBooleanExpr le(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "le", new Object[]{ left, right });
    }
    @Override
        public XsBooleanExpr lt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "lt", new Object[]{ left, right });
    }
    @Override
        public PlanAggregateCol max(String name, String column) {
        return max(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol max(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "max", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol max(String name, String column, PlanAggregateOptions options) {
        return max(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol max(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "max", new Object[]{ name, column, options });
    }
    @Override
        public PlanAggregateCol min(String name, String column) {
        return min(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol min(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "min", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol min(String name, String column, PlanAggregateOptions options) {
        return min(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol min(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "min", new Object[]{ name, column, options });
    }
    @Override
        public XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right) {
        return new XsExprImpl.XsNumericCallImpl("op", "modulo", new Object[]{ left, right });
    }
    @Override
        public XsNumericExpr multiply(XsNumericExpr... list) {
        return new XsExprImpl.XsNumericCallImpl("op", "multiply", list);
    }
    @Override
        public XsBooleanExpr ne(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
        return new XsExprImpl.XsBooleanCallImpl("op", "ne", new Object[]{ left, right });
    }
    @Override
        public XsBooleanExpr not(XsBooleanExpr condition) {
        return new XsExprImpl.XsBooleanCallImpl("op", "not", new Object[]{ condition });
    }
    @Override
        public PlanJoinKey on(String left, String right) {
        return on(col(left), col(right)); 
    }
    @Override
        public PlanJoinKey on(PlanExprCol left, PlanExprCol right) {
        return new PlanJoinKeyCallImpl("op", "on", new Object[]{ left, right });
    }
    @Override
        public XsBooleanExpr or(XsBooleanExpr... list) {
        return new XsExprImpl.XsBooleanCallImpl("op", "or", list);
    }
    @Override
        public PlanTriplePattern pattern(PlanTripleIriSeq subject, PlanTripleIriSeq predicate, PlanTripleVal... object) {
        return pattern(subject, predicate, new PlanTripleValSeqListImpl(object)); 
    }
    @Override
        public PlanTriplePattern pattern(PlanTripleIriSeq subject, PlanTripleIriSeq predicate, PlanTripleValSeq object) {
        return new PlanTriplePatternCallImpl("op", "pattern", new Object[]{ subject, predicate, object });
    }
    @Override
        public PlanTriplePattern pattern(PlanTripleIriSeq subject, PlanTripleIriSeq predicate, PlanTripleValSeq object, PlanSystemColumnSeq sysCols) {
        return new PlanTriplePatternCallImpl("op", "pattern", new Object[]{ subject, predicate, object, sysCols });
    }
    @Override
        public PlanFunction resolveFunction(XsQNameParam functionName, String modulePath) {
        return resolveFunction(functionName, xs.string(modulePath)); 
    }
    @Override
        public PlanFunction resolveFunction(XsQNameParam functionName, XsStringParam modulePath) {
        return new PlanFunctionCallImpl("op", "resolve-function", new Object[]{ functionName, modulePath });
    }
    @Override
        public PlanAggregateCol sample(String name, String column) {
        return sample(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol sample(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "sample", new Object[]{ name, column });
    }
    @Override
        public PlanColumn schemaCol(String schema, String view, String column) {
        return schemaCol(xs.string(schema), xs.string(view), xs.string(column)); 
    }
    @Override
        public PlanColumn schemaCol(XsStringParam schema, XsStringParam view, XsStringParam column) {
        return new PlanColumnCallImpl("op", "schema-col", new Object[]{ schema, view, column });
    }
    @Override
        public PlanAggregateCol sequenceAggregate(String name, String column) {
        return sequenceAggregate(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol sequenceAggregate(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol sequenceAggregate(String name, String column, PlanAggregateOptions options) {
        return sequenceAggregate(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol sequenceAggregate(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column, options });
    }
    @Override
        public PlanSortKeySeq sortKeys(String... key) {
        return sortKeys((PlanSortKey[]) Arrays.stream(key)
            .map(item -> col(item))
            .toArray(size -> new PlanSortKey[size])); 
    }
    @Override
        public PlanSortKeySeq sortKeys(PlanSortKey... key) {
        return new PlanSortKeySeqListImpl(key);
    }
    @Override
        public XsNumericExpr subtract(XsNumericExpr left, XsNumericExpr right) {
        return new XsExprImpl.XsNumericCallImpl("op", "subtract", new Object[]{ left, right });
    }
    @Override
        public PlanAggregateCol sum(String name, String column) {
        return sum(col(name), col(column)); 
    }
    @Override
        public PlanAggregateCol sum(PlanExprCol name, PlanExprCol column) {
        return new PlanAggregateColCallImpl("op", "sum", new Object[]{ name, column });
    }
    @Override
        public PlanAggregateCol sum(String name, String column, PlanAggregateOptions options) {
        return sum(col(name), col(column), options); 
    }
    @Override
        public PlanAggregateCol sum(PlanExprCol name, PlanExprCol column, PlanAggregateOptions options) {
        return new PlanAggregateColCallImpl("op", "sum", new Object[]{ name, column, options });
    }
    @Override
        public PlanAggregateCol uda(String name, String column, String module, String function) {
        return uda(col(name), col(column), xs.string(module), xs.string(function)); 
    }
    @Override
        public PlanAggregateCol uda(PlanExprCol name, PlanExprCol column, XsStringParam module, XsStringParam function) {
        return new PlanAggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function });
    }
    @Override
        public PlanAggregateCol uda(String name, String column, String module, String function, XsAnyAtomicTypeParam arg) {
        return uda(col(name), col(column), xs.string(module), xs.string(function), arg); 
    }
    @Override
        public PlanAggregateCol uda(PlanExprCol name, PlanExprCol column, XsStringParam module, XsStringParam function, XsAnyAtomicTypeParam arg) {
        return new PlanAggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function, arg });
    }
    @Override
        public PlanColumn viewCol(String view, String column) {
        return viewCol(xs.string(view), xs.string(column)); 
    }
    @Override
        public PlanColumn viewCol(XsStringParam view, XsStringParam column) {
        return new PlanColumnCallImpl("op", "view-col", new Object[]{ view, column });
    } 
    @Override
    public PlanTriplePatternSeq patterns(PlanTriplePattern... patterns) {
        return new PlanTriplePatternSeqListImpl(patterns);
    }
    @Override
    public PlanTripleIriSeq subjects(PlanTripleIri...   subjects) {
        return new PlanTripleIriSeqListImpl(subjects);
    }
    @Override
    public PlanTripleIriSeq predicates(PlanTripleIri... predicates) {
        return new PlanTripleIriSeqListImpl(predicates);
    }
    @Override
    public PlanTripleValSeq objects(PlanTripleVal...    objects) {
        return new PlanTripleValSeqListImpl(objects);
    }
    @Override
    public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, PlanTripleOptions options) {
        return fromTriples(patterns, (qualifierName == null) ? null : xs.string(qualifierName), options); 
    }
    @Override
    public QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName, PlanTripleOptions options) {
        return new QualifiedPlanCallImpl(this, "op", "from-triples", new Object[]{patterns, qualifierName, null, options});
    }
    @Override
    public QualifiedPlan fromLexicons(Map<String, CtsReferenceExpr> indexes) {
        return new QualifiedPlanCallImpl(this, "op", "from-lexicons", new Object[]{literal(indexes)});
    }
    @Override
    public QualifiedPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, String qualifierName) {
        return new QualifiedPlanCallImpl(this, "op", "from-lexicons", new Object[]{literal(indexes), xs.string(qualifierName)});
    }
    @Override
    public QualifiedPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanSystemColumnSeq sysCols) {
        return new QualifiedPlanCallImpl(this, "op", "from-lexicons", new Object[]{
            literal(indexes), (qualifierName == null) ? null : xs.string(qualifierName), sysCols
            });
    }
    @Override
    public QualifiedPlan fromLiterals(@SuppressWarnings("unchecked") Map<String,Object>... rows) {
        return new QualifiedPlanCallImpl(this, "op", "from-literals", new Object[]{literal(rows)});
    }
    @Override
    public QualifiedPlan fromLiterals(Map<String,Object>[] rows, String qualifierName) {
        return new QualifiedPlanCallImpl(this, "op", "from-literals", new Object[]{literal(rows), xs.string(qualifierName)});
    }
 public class PlanCallImpl  extends PlanBase  implements Plan {
        PlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class ViewPlanCallImpl  extends AccessPlanCallImpl  implements ViewPlan {
        XsStringParam view = null;
         XsStringParam schema = null;
         XsStringParam qualifier = null;
         ViewPlanCallImpl(PlanBuilderBase builder, String fnPrefix, String fnName, Object[] fnArgs) {
            super(null, fnPrefix, fnName, fnArgs);
            setHandleRegistry(builder.getHandleRegistry());
         schema = (fnArgs.length <= 0) ? null : (XsStringParam) fnArgs[0];
         view = (fnArgs.length <= 1) ? null : (XsStringParam) fnArgs[1];
         qualifier = (fnArgs.length <= 2) ? null : (XsStringParam) fnArgs[2];
         }
     @Override
        public PlanColumn col(String column) {
        return col(xs.string(column)); 
    }
    @Override
        public PlanColumn col(XsStringParam column) { 
        if (this.qualifier != null) { 
        return viewCol(this.qualifier, column); 
        } else { 
        return schemaCol(this.schema, this.view, column);
        }
    }
}
 public class PlanSystemColumnCallImpl  extends PlanColumnCallImpl  implements PlanSystemColumn {
        PlanSystemColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class PlanColumnCallImpl  extends PlanExprColCallImpl  implements PlanColumn {
        PlanColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class AccessPlanCallImpl  extends ModifyPlanCallImpl  implements AccessPlan {
        AccessPlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class ModifyPlanCallImpl  extends PreparePlanCallImpl  implements ModifyPlan {
        ModifyPlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }
 
    @Override
    public ModifyPlan select(String... cols) {
        return select((PlanExprCol[]) Arrays.stream(cols)
            .map(item -> col(item))
            .toArray(size -> new PlanExprCol[size])); 
    }
    @Override
    public ModifyPlan orderBy(String... cols) {
        return orderBy((PlanSortKey[]) Arrays.stream(cols)
            .map(item -> col(item))
            .toArray(size -> new PlanSortKey[size])); 
    }
    @Override
    public ModifyPlan groupBy(String... cols) {
        return groupBy((PlanExprCol[]) Arrays.stream(cols)
            .map(item -> col(item))
            .toArray(size -> new PlanExprCol[size])); 
    }
    @Override
    public ModifyPlan where(CtsQueryExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "where", new Object[]{ condition });
    }
    @Override
    public ModifyPlan where(SemStoreExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "where", new Object[]{ condition });
    }
     @Override
        public ModifyPlan except(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "except", new Object[]{ right });
    }
    @Override
        public ModifyPlan groupBy(PlanExprCol... keys) {
        return groupBy(new PlanExprColSeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan groupBy(PlanExprColSeq keys) {
        return new ModifyPlanCallImpl(this, "op", "group-by", new Object[]{ keys });
    }
    @Override
        public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates) {
        return new ModifyPlanCallImpl(this, "op", "group-by", new Object[]{ keys, aggregates });
    }
    @Override
        public ModifyPlan intersect(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "intersect", new Object[]{ right });
    }
    @Override
        public ModifyPlan joinCrossProduct(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "join-cross-product", new Object[]{ right });
    }
    @Override
        public ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-cross-product", new Object[]{ right, condition });
    }
    @Override
        public ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol) {
        return new ModifyPlanCallImpl(this, "op", "join-doc", new Object[]{ docCol, sourceCol });
    }
    @Override
        public ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol) {
        return new ModifyPlanCallImpl(this, "op", "join-doc-uri", new Object[]{ uriCol, fragmentIdCol });
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys) {
        return joinInner(right, new PlanJoinKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "join-inner", new Object[]{ right, keys });
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-inner", new Object[]{ right, keys, condition });
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys) {
        return joinLeftOuter(right, new PlanJoinKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "join-left-outer", new Object[]{ right, keys });
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-left-outer", new Object[]{ right, keys, condition });
    }
    @Override
        public ModifyPlan limit(long length) {
        return limit(xs.longVal(length)); 
    }
    @Override
        public ModifyPlan limit(XsLongParam length) {
        return new ModifyPlanCallImpl(this, "op", "limit", new Object[]{ length });
    }
    @Override
        public ModifyPlan offset(long start) {
        return offset(xs.longVal(start)); 
    }
    @Override
        public ModifyPlan offset(XsLongParam start) {
        return new ModifyPlanCallImpl(this, "op", "offset", new Object[]{ start });
    }
    @Override
        public ModifyPlan offsetLimit(long start, long length) {
        return offsetLimit(xs.longVal(start), xs.longVal(length)); 
    }
    @Override
        public ModifyPlan offsetLimit(XsLongParam start, XsLongParam length) {
        return new ModifyPlanCallImpl(this, "op", "offset-limit", new Object[]{ start, length });
    }
    @Override
        public ModifyPlan orderBy(PlanSortKey... keys) {
        return orderBy(new PlanSortKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan orderBy(PlanSortKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "order-by", new Object[]{ keys });
    }
    @Override
        public PreparePlan prepare(int optimize) {
        return prepare(xs.intVal(optimize)); 
    }
    @Override
        public PreparePlan prepare(XsIntParam optimize) {
        return new PreparePlanCallImpl(this, "op", "prepare", new Object[]{ optimize });
    }
    @Override
        public ModifyPlan select(PlanExprCol... columns) {
        return select(new PlanExprColSeqListImpl(columns)); 
    }
    @Override
        public ModifyPlan select(PlanExprColSeq columns) {
        return new ModifyPlanCallImpl(this, "op", "select", new Object[]{ columns });
    }
    @Override
        public ModifyPlan select(PlanExprColSeq columns, String qualifierName) {
        return select(columns, (qualifierName == null) ? null : xs.string(qualifierName)); 
    }
    @Override
        public ModifyPlan select(PlanExprColSeq columns, XsStringParam qualifierName) {
        return new ModifyPlanCallImpl(this, "op", "select", new Object[]{ columns, qualifierName });
    }
    @Override
        public ModifyPlan union(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "union", new Object[]{ right });
    }
    @Override
        public ModifyPlan where(XsBooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "where", new Object[]{ condition });
    }
    @Override
    public ModifyPlan whereDistinct() {
        return new ModifyPlanCallImpl(this, "op", "whereDistinct", null);
    }
}
 public class PreparePlanCallImpl  extends ExportablePlanCallImpl  implements PreparePlan {
        PreparePlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }
     @Override
        public ExportablePlan map(PlanFunction func) {
        return new ExportablePlanCallImpl(this, "op", "map", new Object[]{ func });
    }
    @Override
        public ExportablePlan reduce(PlanFunction func) {
        return new ExportablePlanCallImpl(this, "op", "reduce", new Object[]{ func });
    }
    @Override
        public ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeParam seed) {
        return new ExportablePlanCallImpl(this, "op", "reduce", new Object[]{ func, seed });
    }
}
 public class PlanParamCallImpl  extends PlanBaseImpl  implements PlanParam {
        PlanParamCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class QualifiedPlanCallImpl  extends AccessPlanCallImpl  implements QualifiedPlan {
        XsStringParam qualifier = null;
         QualifiedPlanCallImpl(PlanBuilderBase builder, String fnPrefix, String fnName, Object[] fnArgs) {
            super(null, fnPrefix, fnName, fnArgs);
            setHandleRegistry(builder.getHandleRegistry());
         qualifier = (fnArgs.length <= 1) ? null : (XsStringParam) fnArgs[1];
         }
     @Override
        public PlanColumn col(String column) {
        return col(xs.string(column)); 
    }
    @Override
        public PlanColumn col(XsStringParam column) { 
        return viewCol(this.qualifier, column);
    }
}
 public class PlanExprColCallImpl  extends PlanAggregateColCallImpl  implements PlanExprCol {
        PlanExprColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class ExportablePlanCallImpl  extends PlanCallImpl  implements ExportablePlan {
        ExportablePlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class PlanAggregateColCallImpl  extends PlanBaseImpl  implements PlanAggregateCol {
        PlanAggregateColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class PlanSortKeyCallImpl  extends PlanBaseImpl  implements PlanSortKey {
        PlanSortKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 static class PlanAggregateColSeqListImpl extends PlanListImpl implements PlanAggregateColSeq {
        PlanAggregateColSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanAggregateOptionsCallImpl extends PlanBaseImpl implements PlanAggregateOptions {
        PlanAggregateOptionsCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanAggregateOptionsSeqListImpl extends PlanListImpl implements PlanAggregateOptionsSeq {
        PlanAggregateOptionsSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanColumnSeqListImpl extends PlanListImpl implements PlanColumnSeq {
        PlanColumnSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanExprColSeqListImpl extends PlanListImpl implements PlanExprColSeq {
        PlanExprColSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanFunctionCallImpl extends PlanBaseImpl implements PlanFunction {
        PlanFunctionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanFunctionSeqListImpl extends PlanListImpl implements PlanFunctionSeq {
        PlanFunctionSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanGroupConcatOptionsCallImpl extends PlanBaseImpl implements PlanGroupConcatOptions {
        PlanGroupConcatOptionsCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanGroupConcatOptionsSeqListImpl extends PlanListImpl implements PlanGroupConcatOptionsSeq {
        PlanGroupConcatOptionsSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanJoinKeyCallImpl extends PlanBaseImpl implements PlanJoinKey {
        PlanJoinKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanJoinKeySeqListImpl extends PlanListImpl implements PlanJoinKeySeq {
        PlanJoinKeySeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanParamSeqListImpl extends PlanListImpl implements PlanParamSeq {
        PlanParamSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanSortKeySeqListImpl extends PlanListImpl implements PlanSortKeySeq {
        PlanSortKeySeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanSystemColumnSeqListImpl extends PlanListImpl implements PlanSystemColumnSeq {
        PlanSystemColumnSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanTripleIriCallImpl extends PlanBaseImpl implements PlanTripleIri {
        PlanTripleIriCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanTripleIriSeqListImpl extends PlanListImpl implements PlanTripleIriSeq {
        PlanTripleIriSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanTripleOptionsCallImpl extends PlanBaseImpl implements PlanTripleOptions {
        PlanTripleOptionsCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanTripleOptionsSeqListImpl extends PlanListImpl implements PlanTripleOptionsSeq {
        PlanTripleOptionsSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanTriplePatternCallImpl extends PlanBaseImpl implements PlanTriplePattern {
        PlanTriplePatternCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanTriplePatternSeqListImpl extends PlanListImpl implements PlanTriplePatternSeq {
        PlanTriplePatternSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanTripleValCallImpl extends PlanBaseImpl implements PlanTripleVal {
        PlanTripleValCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class PlanTripleValSeqListImpl extends PlanListImpl implements PlanTripleValSeq {
        PlanTripleValSeqListImpl(Object[] items) {
            super(items);
        }
    }

}
