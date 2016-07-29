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
package com.marklogic.client.expression;

import java.util.Map;
import com.marklogic.client.io.marker.JSONReadHandle;


import com.marklogic.client.expression.Cts; 
import com.marklogic.client.expression.Fn; 
import com.marklogic.client.expression.Json; 
import com.marklogic.client.expression.Math; 
import com.marklogic.client.expression.Rdf; 
import com.marklogic.client.expression.Sem; 
import com.marklogic.client.expression.Sql; 
import com.marklogic.client.expression.Xdmp; 
import com.marklogic.client.expression.Xs; import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsGMonthDayExpr;
 import com.marklogic.client.type.XsDecimalParam;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.PlanFunction;
 import com.marklogic.client.type.XsGYearMonthParam;
 import com.marklogic.client.type.XsUnsignedShortParam;
 import com.marklogic.client.type.XsYearMonthDurationParam;
 import com.marklogic.client.type.XsGDayExpr;
 import com.marklogic.client.type.PlanTriplePatternSeq;
 import com.marklogic.client.type.XsLongParam;
 import com.marklogic.client.type.XsHexBinaryExpr;
 import com.marklogic.client.type.PlanSortKey;
 import com.marklogic.client.type.PlanJoinKeySeq;
 import com.marklogic.client.type.XsBase64BinaryParam;
 import com.marklogic.client.type.XsUnsignedIntParam;
 import com.marklogic.client.type.XsLongExpr;
 import com.marklogic.client.type.PlanTriplePosition;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsGYearExpr;
 import com.marklogic.client.type.PlanTriplePositionSeq;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsGMonthExpr;
 import com.marklogic.client.type.XsUnsignedLongParam;
 import com.marklogic.client.type.XsDateParam;
 import com.marklogic.client.type.XsShortParam;
 import com.marklogic.client.type.PlanColumnSeq;
 import com.marklogic.client.type.XsIntegerParam;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsByteExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsStringParam;
 import com.marklogic.client.type.PlanAggregateCol;
 import com.marklogic.client.type.XsDateTimeParam;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.PlanJoinKey;
 import com.marklogic.client.type.XsBooleanParam;
 import com.marklogic.client.type.XsByteParam;
 import com.marklogic.client.type.XsGMonthParam;
 import com.marklogic.client.type.XsHexBinaryParam;
 import com.marklogic.client.type.XsUnsignedShortExpr;
 import com.marklogic.client.type.PlanColumn;
 import com.marklogic.client.type.PlanSortKeySeq;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.XsGYearMonthExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeParam;
 import com.marklogic.client.type.PlanParam;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.XsQNameParam;
 import com.marklogic.client.type.PlanTriplePattern;
 import com.marklogic.client.type.PlanExprColSeq;
 import com.marklogic.client.type.XsDoubleParam;
 import com.marklogic.client.type.XsBase64BinaryExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsAnyURIParam;
 import com.marklogic.client.type.XsUntypedAtomicExpr;
 import com.marklogic.client.type.XsUnsignedByteParam;
 import com.marklogic.client.type.XsGDayParam;
 import com.marklogic.client.type.XsFloatParam;
 import com.marklogic.client.type.CtsQueryExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsBooleanSeqExpr;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsDayTimeDurationParam;
 import com.marklogic.client.type.XsShortExpr;
 import com.marklogic.client.type.XsGYearParam;
 import com.marklogic.client.type.XsIntParam;
 import com.marklogic.client.type.PlanParamSeq;
 import com.marklogic.client.type.XsUntypedAtomicParam;
 import com.marklogic.client.type.CtsReferenceExpr;
 import com.marklogic.client.type.XsUnsignedByteExpr;
 import com.marklogic.client.type.PlanFunctionSeq;
 import com.marklogic.client.type.PlanExprCol;
 import com.marklogic.client.type.PlanAggregateColSeq;
 import com.marklogic.client.type.XsAnyAtomicTypeVal;
 import com.marklogic.client.type.XsYearMonthDurationExpr;
 import com.marklogic.client.type.XsTimeParam;
 import com.marklogic.client.type.XsFloatExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsGMonthDayParam;


// IMPORTANT: Do not edit. This file is generated. 
public abstract class PlanBuilder {
    protected PlanBuilder(
        Cts cts, Fn fn, Json json, Math math, Rdf rdf, Sem sem, Sql sql, Xdmp xdmp, Xs xs
        ) {
        this.cts = cts;
         this.fn = fn;
         this.json = json;
         this.math = math;
         this.rdf = rdf;
         this.sem = sem;
         this.sql = sql;
         this.xdmp = xdmp;
         this.xs = xs;

    }
    public final Cts cts;
     public final Fn fn;
     public final Json json;
     public final Math math;
     public final Rdf rdf;
     public final Sem sem;
     public final Sql sql;
     public final Xdmp xdmp;
     public final Xs xs;
     public abstract XsAnyAtomicTypeExpr add(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract PlanAggregateColSeq aggregates(PlanAggregateCol... aggregate);
    public abstract XsBooleanExpr and(XsBooleanExpr... list);
    public abstract XsBooleanExpr and(XsBooleanSeqExpr list);
    public abstract PlanAggregateCol arrayAggregate(String name, String column);
    public abstract PlanAggregateCol arrayAggregate(PlanExprCol name, PlanExprCol column);
    public abstract PlanExprCol as(String column, ItemExpr expression);
    public abstract PlanExprCol as(PlanColumn column, ItemExpr expression);
    public abstract PlanSortKey asc(String column);
    public abstract PlanSortKey asc(PlanExprCol column);
    public abstract PlanAggregateCol avg(String name, String column);
    public abstract PlanAggregateCol avg(PlanExprCol name, PlanExprCol column);
    public abstract PlanColumn col(String column);
    public abstract PlanColumn col(XsStringParam column);
    public abstract PlanExprColSeq cols(String... col);
    public abstract PlanExprColSeq cols(PlanExprCol... col);
    public abstract PlanAggregateCol count(String name, String column);
    public abstract PlanAggregateCol count(PlanExprCol name, PlanExprCol column);
    public abstract PlanSortKey desc(String column);
    public abstract PlanSortKey desc(PlanExprCol column);
    public abstract XsAnyAtomicTypeExpr divide(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr eq(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract QualifiedPlan fromTriples(PlanTriplePattern... patterns);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIri);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName, XsStringParam graphIri);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIri, CtsQueryExpr constrainingQuery);
    public abstract QualifiedPlan fromTriples(PlanTriplePatternSeq patterns, XsStringParam qualifierName, XsStringParam graphIri, CtsQueryExpr constrainingQuery);
    public abstract ViewPlan fromView(String schema, String view);
    public abstract ViewPlan fromView(XsStringParam schema, XsStringParam view);
    public abstract ViewPlan fromView(String schema, String view, String qualifierName);
    public abstract ViewPlan fromView(XsStringParam schema, XsStringParam view, XsStringParam qualifierName);
    public abstract ViewPlan fromView(String schema, String view, String qualifierName, PlanColumn... sysCols);
    public abstract ViewPlan fromView(XsStringParam schema, XsStringParam view, XsStringParam qualifierName, PlanColumnSeq sysCols);
    public abstract ViewPlan fromView(String schema, String view, String qualifierName, PlanColumnSeq sysCols, CtsQueryExpr constrainingQuery);
    public abstract ViewPlan fromView(XsStringParam schema, XsStringParam view, XsStringParam qualifierName, PlanColumnSeq sysCols, CtsQueryExpr constrainingQuery);
    public abstract XsBooleanExpr ge(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract PlanAggregateCol groupConcat(String name, String column);
    public abstract PlanAggregateCol groupConcat(PlanExprCol name, PlanExprCol column);
    public abstract XsBooleanExpr gt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr isDefined(ItemExpr expression);
    public abstract PlanJoinKeySeq joinKeys(PlanJoinKey... key);
    public abstract XsBooleanExpr le(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr lt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract PlanAggregateCol max(String name, String column);
    public abstract PlanAggregateCol max(PlanExprCol name, PlanExprCol column);
    public abstract PlanAggregateCol min(String name, String column);
    public abstract PlanAggregateCol min(PlanExprCol name, PlanExprCol column);
    public abstract XsAnyAtomicTypeExpr modulo(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsAnyAtomicTypeExpr multiply(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr ne(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr not(XsBooleanExpr condition);
    public abstract PlanJoinKey on(String left, String right);
    public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
    public abstract XsBooleanExpr or(XsBooleanExpr... list);
    public abstract XsBooleanExpr or(XsBooleanSeqExpr list);
    public abstract PlanTriplePattern pattern(PlanTriplePosition... subject);
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subject);
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subject, PlanTriplePositionSeq predicate);
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subject, PlanTriplePositionSeq predicate, PlanTriplePositionSeq object);
    public abstract PlanAggregateCol sample(String name, String column);
    public abstract PlanAggregateCol sample(PlanExprCol name, PlanExprCol column);
    public abstract PlanColumn schemaCol(String schema, String view, String column);
    public abstract PlanColumn schemaCol(XsStringParam schema, XsStringParam view, XsStringParam column);
    public abstract PlanAggregateCol sequenceAggregate(String name, String column);
    public abstract PlanAggregateCol sequenceAggregate(PlanExprCol name, PlanExprCol column);
    public abstract PlanSortKeySeq sortKeys(String... key);
    public abstract PlanSortKeySeq sortKeys(PlanSortKey... key);
    public abstract XsAnyAtomicTypeExpr subtract(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract PlanAggregateCol sum(String name, String column);
    public abstract PlanAggregateCol sum(PlanExprCol name, PlanExprCol column);
    public abstract PlanAggregateCol uda(String name, String column, String module, String function);
    public abstract PlanAggregateCol uda(PlanExprCol name, PlanExprCol column, XsStringParam module, XsStringParam function);
    public abstract PlanColumn viewCol(String view, String column);
    public abstract PlanColumn viewCol(XsStringParam view, XsStringParam column); public interface Plan {
    
    public Plan bindParam(PlanParam param, boolean            literal);
    public Plan bindParam(PlanParam param, byte               literal);
    public Plan bindParam(PlanParam param, double             literal);
    public Plan bindParam(PlanParam param, float              literal);
    public Plan bindParam(PlanParam param, int                literal);
    public Plan bindParam(PlanParam param, long               literal);
    public Plan bindParam(PlanParam param, short              literal);
    public Plan bindParam(PlanParam param, String             literal);
    public Plan bindParam(PlanParam param, XsAnyAtomicTypeVal literal);

}
 public interface ViewPlan extends AccessPlan {
    public PlanColumn col(String column);
    public PlanColumn col(XsStringParam column);
}
 public interface AccessPlan extends ModifyPlan {

}
 public interface ModifyPlan extends PreparePlan {
    public ModifyPlan select(String... cols);
    public ModifyPlan orderBy(String... cols);
    public ModifyPlan groupBy(String... cols);
     public ModifyPlan groupBy(PlanExprCol... keys);
    public ModifyPlan groupBy(PlanExprColSeq keys);
    public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateCol... aggregates);
    public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates);
    public ModifyPlan joinCrossProduct(ModifyPlan right);
    public ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition);
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys);
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys);
    public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
    public ModifyPlan joinInnerDoc(String docCol, String uriCol);
    public ModifyPlan joinInnerDoc(PlanColumn docCol, PlanExprCol uriCol);
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys);
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys);
    public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
    public ModifyPlan joinLeftOuterDoc(String docCol, String uriCol);
    public ModifyPlan joinLeftOuterDoc(PlanColumn docCol, PlanExprCol uriCol);
    public ModifyPlan limit(long length);
    public ModifyPlan limit(XsLongParam length);
    public ModifyPlan offset(long start);
    public ModifyPlan offset(XsLongParam start);
    public ModifyPlan offsetLimit(long start, long length);
    public ModifyPlan offsetLimit(XsLongParam start, XsLongParam length);
    public ModifyPlan orderBy(PlanSortKey... keys);
    public ModifyPlan orderBy(PlanSortKeySeq keys);
    public PreparePlan prepare(int optimize);
    public PreparePlan prepare(XsIntParam optimize);
    public ModifyPlan select(PlanExprCol... columns);
    public ModifyPlan select(PlanExprColSeq columns);
    public ModifyPlan select(PlanExprColSeq columns, String qualifierName);
    public ModifyPlan select(PlanExprColSeq columns, XsStringParam qualifierName);
    public ModifyPlan union(ModifyPlan right);
    public ModifyPlan where(XsBooleanExpr condition);
    public ModifyPlan whereDistinct();
}
 public interface PreparePlan extends ExportablePlan {
    public PlanFunction installedFunction(String modulePath, String functionName);
    public PlanFunction installedFunction(XsStringParam modulePath, XsStringParam functionName);
    public ExportablePlan map(PlanFunction func);
    public PlanFunction mapFunction(String moduleName);
    public PlanFunction mapFunction(XsStringParam moduleName);
    public ExportablePlan reduce(PlanFunction func);
    public ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeParam seed);
    public PlanFunction reduceFunction(String moduleName);
    public PlanFunction reduceFunction(XsStringParam moduleName);
}
 public interface QualifiedPlan extends AccessPlan {
    public PlanColumn col(String column);
    public PlanColumn col(XsStringParam column);
}
 public interface ExportablePlan extends Plan {
    public <T extends JSONReadHandle> T export(T handle);
    public <T> T exportAs(Class<T> as);

}
 
    public abstract PlanParam param(String name);

    public abstract PlanTriplePatternSeq  patterns(PlanTriplePattern... pattern);
    public abstract PlanTriplePositionSeq positions(PlanTriplePosition... position);


    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanColumn... sysCols);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanColumnSeq sysCols, CtsQueryExpr constrainingQuery);

    public abstract QualifiedPlan fromLiterals(@SuppressWarnings("unchecked") Map<String,Object>... rows);
    public abstract QualifiedPlan fromLiterals(Map<String,Object>[] rows, String qualifierName);

}
