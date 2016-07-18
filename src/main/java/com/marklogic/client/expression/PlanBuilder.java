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

import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.CtsQuery;
import com.marklogic.client.io.marker.JSONReadHandle;


import com.marklogic.client.expression.Cts; 
import com.marklogic.client.expression.Fn; 
import com.marklogic.client.expression.Json; 
import com.marklogic.client.expression.Map; 
import com.marklogic.client.expression.Math; 
import com.marklogic.client.expression.Rdf; 
import com.marklogic.client.expression.Sem; 
import com.marklogic.client.expression.Sql; 
import com.marklogic.client.expression.Xdmp; 
import com.marklogic.client.expression.Xs; import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;


// IMPORTANT: Do not edit. This file is generated. 
public abstract class PlanBuilder {
    protected PlanBuilder(
        Cts cts, Fn fn, Json json, Map map, Math math, Rdf rdf, Sem sem, Sql sql, Xdmp xdmp, Xs xs
        ) {
        this.cts = cts;
         this.fn = fn;
         this.json = json;
         this.map = map;
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
     public final Map map;
     public final Math math;
     public final Rdf rdf;
     public final Sem sem;
     public final Sql sql;
     public final Xdmp xdmp;
     public final Xs xs;
     public abstract Xs.AnyAtomicTypeExpr add(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract AggregateColSeq aggregates(AggregateCol... aggregate);
    public abstract Xs.BooleanExpr and(Xs.BooleanExpr... list);
    public abstract Xs.BooleanExpr and(Xs.BooleanSeqExpr list);
    public abstract AggregateCol arrayAggregate(String name, String column);
    public abstract AggregateCol arrayAggregate(ExprCol name, ExprCol column);
    public abstract ExprCol as(String column, BaseType.ItemExpr expression);
    public abstract ExprCol as(Column column, BaseType.ItemExpr expression);
    public abstract SortKey asc(String column);
    public abstract SortKey asc(ExprCol column);
    public abstract AggregateCol avg(String name, String column);
    public abstract AggregateCol avg(ExprCol name, ExprCol column);
    public abstract Column col(String column);
    public abstract Column col(Xs.StringParam column);
    public abstract ExprColSeq cols(String... col);
    public abstract ExprColSeq cols(ExprCol... col);
    public abstract AggregateCol count(String name, String column);
    public abstract AggregateCol count(ExprCol name, ExprCol column);
    public abstract SortKey desc(String column);
    public abstract SortKey desc(ExprCol column);
    public abstract Xs.AnyAtomicTypeExpr divide(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.BooleanExpr eq(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract QualifiedPlan fromTriples(TriplePattern... patterns);
    public abstract QualifiedPlan fromTriples(TriplePatternSeq patterns);
    public abstract QualifiedPlan fromTriples(TriplePatternSeq patterns, String qualifierName);
    public abstract QualifiedPlan fromTriples(TriplePatternSeq patterns, Xs.StringParam qualifierName);
    public abstract QualifiedPlan fromTriples(TriplePatternSeq patterns, String qualifierName, String graphIri);
    public abstract QualifiedPlan fromTriples(TriplePatternSeq patterns, Xs.StringParam qualifierName, Xs.StringParam graphIri);
    public abstract ViewPlan fromView(String schema, String view);
    public abstract ViewPlan fromView(Xs.StringParam schema, Xs.StringParam view);
    public abstract ViewPlan fromView(String schema, String view, String qualifierName);
    public abstract ViewPlan fromView(Xs.StringParam schema, Xs.StringParam view, Xs.StringParam qualifierName);
    public abstract Xs.BooleanExpr ge(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract AggregateCol groupConcat(String name, String column);
    public abstract AggregateCol groupConcat(ExprCol name, ExprCol column);
    public abstract Xs.BooleanExpr gt(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.BooleanExpr isDefined(BaseType.ItemExpr expression);
    public abstract JoinKeySeq joinKeys(JoinKey... key);
    public abstract Xs.BooleanExpr le(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.BooleanExpr lt(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract AggregateCol max(String name, String column);
    public abstract AggregateCol max(ExprCol name, ExprCol column);
    public abstract AggregateCol min(String name, String column);
    public abstract AggregateCol min(ExprCol name, ExprCol column);
    public abstract Xs.AnyAtomicTypeExpr modulo(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.AnyAtomicTypeExpr multiply(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.BooleanExpr ne(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract Xs.BooleanExpr not(Xs.BooleanExpr condition);
    public abstract JoinKey on(String left, String right);
    public abstract JoinKey on(ExprCol left, ExprCol right);
    public abstract Xs.BooleanExpr or(Xs.BooleanExpr... list);
    public abstract Xs.BooleanExpr or(Xs.BooleanSeqExpr list);
    public abstract TriplePattern pattern(TriplePosition... subject);
    public abstract TriplePattern pattern(TriplePositionSeq subject);
    public abstract TriplePattern pattern(TriplePositionSeq subject, TriplePosition... predicate);
    public abstract TriplePattern pattern(TriplePositionSeq subject, TriplePositionSeq predicate);
    public abstract TriplePattern pattern(TriplePositionSeq subject, TriplePositionSeq predicate, TriplePosition... object);
    public abstract TriplePattern pattern(TriplePositionSeq subject, TriplePositionSeq predicate, TriplePositionSeq object);
    public abstract AggregateCol sample(String name, String column);
    public abstract AggregateCol sample(ExprCol name, ExprCol column);
    public abstract Column schemaCol(String schema, String view, String column);
    public abstract Column schemaCol(Xs.StringParam schema, Xs.StringParam view, Xs.StringParam column);
    public abstract AggregateCol sequenceAggregate(String name, String column);
    public abstract AggregateCol sequenceAggregate(ExprCol name, ExprCol column);
    public abstract SortKeySeq sortKeys(String... key);
    public abstract SortKeySeq sortKeys(SortKey... key);
    public abstract Xs.AnyAtomicTypeExpr subtract(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right);
    public abstract AggregateCol sum(String name, String column);
    public abstract AggregateCol sum(ExprCol name, ExprCol column);
    public abstract AggregateCol uda(String name, String column, String module, String function);
    public abstract AggregateCol uda(ExprCol name, ExprCol column, Xs.StringParam module, Xs.StringParam function);
    public abstract Column viewCol(String view, String column);
    public abstract Column viewCol(Xs.StringParam view, Xs.StringParam column); public interface Plan {
    public Plan bindParam(PlanParam param, String literal);

}
 public interface ViewPlan extends AccessPlan {
    public Column col(String column);
    public Column col(Xs.StringParam column);
}
 public interface Column extends ExprCol, Xs.AnyURIExpr, Xs.Base64BinaryExpr, Xs.BooleanExpr, Xs.DateExpr, Xs.DateTimeExpr, Xs.DecimalExpr, Xs.IntegerExpr, Xs.LongExpr, Xs.IntExpr, Xs.ShortExpr, Xs.ByteExpr, Xs.UnsignedLongExpr, Xs.UnsignedIntExpr, Xs.UnsignedShortExpr, Xs.UnsignedByteExpr, Xs.DoubleExpr, Xs.DayTimeDurationExpr, Xs.YearMonthDurationExpr, Xs.FloatExpr, Xs.GDayExpr, Xs.GMonthExpr, Xs.GMonthDayExpr, Xs.GYearExpr, Xs.GYearMonthExpr, Xs.HexBinaryExpr, Xs.QNameExpr, Xs.StringExpr, Xs.TimeExpr, Xs.UntypedAtomicExpr, TriplePosition {

}
 public interface ExprCol extends AggregateCol, SortKey, ExprColSeq {

}
 public interface AccessPlan extends ModifyPlan {

}
 public interface SortKey extends SortKeySeq {

}
 public interface ModifyPlan extends PreparePlan {
    public ModifyPlan select(String... cols);
    public ModifyPlan orderBy(String... cols);
    public ModifyPlan groupBy(String... cols);
     public ModifyPlan groupBy(ExprCol... keys);
    public ModifyPlan groupBy(ExprColSeq keys);
    public ModifyPlan groupBy(ExprColSeq keys, AggregateCol... aggregates);
    public ModifyPlan groupBy(ExprColSeq keys, AggregateColSeq aggregates);
    public ModifyPlan joinCrossProduct(ModifyPlan right);
    public ModifyPlan joinCrossProduct(ModifyPlan right, Xs.BooleanExpr condition);
    public ModifyPlan joinInner(ModifyPlan right, JoinKey... keys);
    public ModifyPlan joinInner(ModifyPlan right, JoinKeySeq keys);
    public ModifyPlan joinInner(ModifyPlan right, JoinKeySeq keys, Xs.BooleanExpr condition);
    public ModifyPlan joinInnerDoc(String docCol, String uriCol);
    public ModifyPlan joinInnerDoc(Column docCol, ExprCol uriCol);
    public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKey... keys);
    public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKeySeq keys);
    public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKeySeq keys, Xs.BooleanExpr condition);
    public ModifyPlan joinLeftOuterDoc(String docCol, String uriCol);
    public ModifyPlan joinLeftOuterDoc(Column docCol, ExprCol uriCol);
    public ModifyPlan limit(long length);
    public ModifyPlan limit(Xs.LongParam length);
    public ModifyPlan offset(long start);
    public ModifyPlan offset(Xs.LongParam start);
    public ModifyPlan offsetLimit(long start, long length);
    public ModifyPlan offsetLimit(Xs.LongParam start, Xs.LongParam length);
    public ModifyPlan orderBy(SortKey... keys);
    public ModifyPlan orderBy(SortKeySeq keys);
    public PreparePlan prepare(int optimize);
    public PreparePlan prepare(Xs.IntParam optimize);
    public ModifyPlan select(ExprCol... columns);
    public ModifyPlan select(ExprColSeq columns);
    public ModifyPlan select(ExprColSeq columns, String qualifierName);
    public ModifyPlan select(ExprColSeq columns, Xs.StringParam qualifierName);
    public ModifyPlan union(ModifyPlan right);
    public ModifyPlan where(Xs.BooleanExpr condition);
    public ModifyPlan whereDistinct();
}
 public interface PreparePlan extends ExportablePlan {
    public PlanFunction installedFunction(String modulePath, String functionName);
    public PlanFunction installedFunction(Xs.StringParam modulePath, Xs.StringParam functionName);
    public ExportablePlan map(PlanFunction func);
    public PlanFunction mapFunction(String moduleName);
    public PlanFunction mapFunction(Xs.StringParam moduleName);
    public ExportablePlan reduce(PlanFunction func);
    public ExportablePlan reduce(PlanFunction func, Xs.AnyAtomicTypeParam seed);
    public PlanFunction reduceFunction(String moduleName);
    public PlanFunction reduceFunction(Xs.StringParam moduleName);
}
 public interface PlanParam extends Xs.AnyURIParam, Xs.Base64BinaryParam, Xs.BooleanParam, Xs.DateParam, Xs.DateTimeParam, Xs.DecimalParam, Xs.IntegerParam, Xs.LongParam, Xs.IntParam, Xs.ShortParam, Xs.ByteParam, Xs.UnsignedLongParam, Xs.UnsignedIntParam, Xs.UnsignedShortParam, Xs.UnsignedByteParam, Xs.DoubleParam, Xs.DayTimeDurationParam, Xs.YearMonthDurationParam, Xs.FloatParam, Xs.GDayParam, Xs.GMonthParam, Xs.GMonthDayParam, Xs.GYearParam, Xs.GYearMonthParam, Xs.HexBinaryParam, Xs.QNameParam, Xs.StringParam, Xs.TimeParam, Xs.UntypedAtomicParam {

}
 public interface QualifiedPlan extends AccessPlan {
    public Column col(String column);
    public Column col(Xs.StringParam column);
}
 public interface ExportablePlan extends Plan {
    public <T extends JSONReadHandle> T export(T handle);
    public <T> T exportAs(Class<T> as);

}
 public interface AggregateCol extends AggregateColSeq {

}
 
    public abstract PlanParam param(String name);

    public abstract TriplePatternSeq  patterns(TriplePattern... pattern);
    public abstract TriplePositionSeq positions(TriplePosition... position);

    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsQuery.ReferenceExpr> indexes);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsQuery.ReferenceExpr> indexes, String qualifierName);

    public abstract QualifiedPlan fromLiterals(@SuppressWarnings("unchecked") java.util.Map<String,Object>... rows);
    public abstract QualifiedPlan fromLiterals(java.util.Map<String,Object>[] rows, String qualifierName);
 public interface AggregateColSeq { }
 public interface ExprColSeq extends AggregateColSeq, SortKeySeq { }
 public interface JoinKey extends JoinKeySeq { }
 public interface JoinKeySeq { }
 public interface PlanFunction { }
 public interface SortKeySeq { }
 public interface TriplePattern extends TriplePatternSeq { }
 public interface TriplePatternSeq { }
 public interface TriplePosition extends TriplePositionSeq { }
 public interface TriplePositionSeq { }

}
