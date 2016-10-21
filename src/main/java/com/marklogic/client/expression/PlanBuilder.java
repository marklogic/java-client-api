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
import com.marklogic.client.expression.XsExpr; import com.marklogic.client.type.SemIriExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsGMonthDayExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.XsYearMonthDurationParam;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.PlanTriplePatternSeq;
 import com.marklogic.client.type.RdfLangStringParam;
 import com.marklogic.client.type.XsHexBinaryExpr;
 import com.marklogic.client.type.PlanSortKey;
 import com.marklogic.client.type.XsBase64BinaryParam;
 import com.marklogic.client.type.PlanTripleVal;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsGMonthExpr;
 import com.marklogic.client.type.XsIntegerParam;
 import com.marklogic.client.type.XsUnsignedLongParam;
 import com.marklogic.client.type.XsShortParam;
 import com.marklogic.client.type.PlanColumnSeq;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsByteExpr;
 import com.marklogic.client.type.XsGMonthParam;
 import com.marklogic.client.type.PlanAggregateCol;
 import com.marklogic.client.type.PlanJoinKey;
 import com.marklogic.client.type.XsDateTimeParam;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.XsByteParam;
 import com.marklogic.client.type.XsGYearMonthExpr;
 import com.marklogic.client.type.PlanColumn;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.PlanTriplePattern;
 import com.marklogic.client.type.XsDoubleParam;
 import com.marklogic.client.type.XsBase64BinaryExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsAnyURIParam;
 import com.marklogic.client.type.XsGDayParam;
 import com.marklogic.client.type.CtsQueryExpr;
 import com.marklogic.client.type.PlanTripleIri;
 import com.marklogic.client.type.XsBooleanSeqExpr;
 import com.marklogic.client.type.XsShortExpr;
 import com.marklogic.client.type.XsNumericSeqExpr;
 import com.marklogic.client.type.XsGYearParam;
 import com.marklogic.client.type.XsUntypedAtomicParam;
 import com.marklogic.client.type.PlanFunctionSeq;
 import com.marklogic.client.type.CtsReferenceExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeVal;
 import com.marklogic.client.type.XsYearMonthDurationExpr;
 import com.marklogic.client.type.SemIriParam;
 import com.marklogic.client.type.XsDecimalParam;
 import com.marklogic.client.type.PlanFunction;
 import com.marklogic.client.type.XsGYearMonthParam;
 import com.marklogic.client.type.XsUnsignedShortParam;
 import com.marklogic.client.type.RdfLangStringExpr;
 import com.marklogic.client.type.XsGDayExpr;
 import com.marklogic.client.type.XsLongParam;
 import com.marklogic.client.type.PlanJoinKeySeq;
 import com.marklogic.client.type.XsGYearExpr;
 import com.marklogic.client.type.XsUnsignedIntParam;
 import com.marklogic.client.type.XsLongExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsDateParam;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsUnsignedShortExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsStringParam;
 import com.marklogic.client.type.PlanTripleValSeq;
 import com.marklogic.client.type.XsBooleanParam;
 import com.marklogic.client.type.XsHexBinaryParam;
 import com.marklogic.client.type.PlanSortKeySeq;
 import com.marklogic.client.type.XsAnyAtomicTypeParam;
 import com.marklogic.client.type.PlanParam;
 import com.marklogic.client.type.XsQNameParam;
 import com.marklogic.client.type.PlanExprColSeq;
 import com.marklogic.client.type.XsUntypedAtomicExpr;
 import com.marklogic.client.type.XsUnsignedByteParam;
 import com.marklogic.client.type.XsFloatParam;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsDayTimeDurationParam;
 import com.marklogic.client.type.XsIntParam;
 import com.marklogic.client.type.PlanParamSeq;
 import com.marklogic.client.type.XsUnsignedByteExpr;
 import com.marklogic.client.type.PlanTripleIriSeq;
 import com.marklogic.client.type.PlanAggregateColSeq;
 import com.marklogic.client.type.PlanExprCol;
 import com.marklogic.client.type.XsTimeParam;
 import com.marklogic.client.type.XsFloatExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsGMonthDayParam;


import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.XsAnySimpleTypeSeqExpr;

import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.AttributeNodeExpr;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.BooleanNodeExpr;
import com.marklogic.client.type.CommentNodeExpr;
import com.marklogic.client.type.DocumentNodeExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.NullNodeExpr;
import com.marklogic.client.type.NumberNodeExpr;
import com.marklogic.client.type.ObjectNodeExpr;
import com.marklogic.client.type.PINodeExpr;
import com.marklogic.client.type.TextNodeExpr;

import com.marklogic.client.type.JsonPropertyExpr;

import com.marklogic.client.type.JsonRootNodeExpr;
import com.marklogic.client.type.JsonContentNodeExpr;
import com.marklogic.client.type.XmlRootNodeExpr;
import com.marklogic.client.type.XmlContentNodeExpr;

// IMPORTANT: Do not edit. This file is generated. 
public abstract class PlanBuilder {
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
     public abstract XsNumericExpr add(XsNumericExpr... list);
    public abstract PlanAggregateColSeq aggregates(PlanAggregateCol... aggregate);
    public abstract XsBooleanExpr and(XsBooleanExpr... list);
    public abstract PlanAggregateCol arrayAggregate(String name, String column);
    public abstract PlanAggregateCol arrayAggregate(PlanExprCol name, PlanExprCol column);
    public abstract PlanExprCol as(String column, ItemSeqExpr expression);
    public abstract PlanExprCol as(PlanColumn column, ItemSeqExpr expression);
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
    public abstract XsNumericExpr divide(XsNumericExpr left, XsNumericExpr right);
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
    public abstract XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right);
    public abstract XsNumericExpr multiply(XsNumericExpr... list);
    public abstract XsBooleanExpr ne(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right);
    public abstract XsBooleanExpr not(XsBooleanExpr condition);
    public abstract PlanJoinKey on(String left, String right);
    public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
    public abstract XsBooleanExpr or(XsBooleanExpr... list);
    public abstract PlanTriplePattern pattern(PlanTripleIriSeq subject, PlanTripleIriSeq predicate, PlanTripleVal... object);
    public abstract PlanTriplePattern pattern(PlanTripleIriSeq subject, PlanTripleIriSeq predicate, PlanTripleValSeq object);
    public abstract PlanAggregateCol sample(String name, String column);
    public abstract PlanAggregateCol sample(PlanExprCol name, PlanExprCol column);
    public abstract PlanColumn schemaCol(String schema, String view, String column);
    public abstract PlanColumn schemaCol(XsStringParam schema, XsStringParam view, XsStringParam column);
    public abstract PlanAggregateCol sequenceAggregate(String name, String column);
    public abstract PlanAggregateCol sequenceAggregate(PlanExprCol name, PlanExprCol column);
    public abstract PlanSortKeySeq sortKeys(String... key);
    public abstract PlanSortKeySeq sortKeys(PlanSortKey... key);
    public abstract XsNumericExpr subtract(XsNumericExpr left, XsNumericExpr right);
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
     public ModifyPlan except(ModifyPlan right);
    public ModifyPlan groupBy(PlanExprCol... keys);
    public ModifyPlan groupBy(PlanExprColSeq keys);
    public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateCol... aggregates);
    public ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates);
    public ModifyPlan intersect(ModifyPlan right);
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
    public abstract Prefixer prefixer(String base);

    public abstract PlanTriplePatternSeq  patterns(PlanTriplePattern... pattern);
    public abstract PlanTripleIriSeq      subjects(PlanTripleIri...   subjects);
    public abstract PlanTripleIriSeq      predicates(PlanTripleIri... predicates);
    public abstract PlanTripleValSeq      objects(PlanTripleVal...    objects);

    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanColumn... sysCols);
    public abstract QualifiedPlan fromLexicons(java.util.Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanColumnSeq sysCols, CtsQueryExpr constrainingQuery);

    public abstract QualifiedPlan fromLiterals(@SuppressWarnings("unchecked") Map<String,Object>... rows);
    public abstract QualifiedPlan fromLiterals(Map<String,Object>[] rows, String qualifierName);

    public abstract DocumentNodeExpr jsonDocument(JsonRootNodeExpr root);
    public abstract ObjectNodeExpr   jsonObject(JsonPropertyExpr... properties);
    public abstract ArrayNodeExpr    jsonArray(JsonContentNodeExpr... items);
    public abstract JsonPropertyExpr prop(String key, JsonContentNodeExpr value);
    public abstract JsonPropertyExpr prop(XsStringExpr key, JsonContentNodeExpr value);
    public abstract TextNodeExpr     jsonString(String value);
    public abstract TextNodeExpr     jsonString(XsAnySimpleTypeSeqExpr value);
    public abstract NumberNodeExpr   jsonNumber(double value);
    public abstract NumberNodeExpr   jsonNumber(long value);
    public abstract NumberNodeExpr   jsonNumber(XsNumericExpr value);
    public abstract BooleanNodeExpr  jsonBoolean(boolean value);
    public abstract BooleanNodeExpr  jsonBoolean(XsBooleanExpr value);
    public abstract NullNodeExpr     jsonNull();

    public abstract DocumentNodeExpr  xmlDocument(XmlRootNodeExpr root);
    public abstract ElementNodeExpr   xmlElement(String name, AttributeNodeExpr... attributes);
    public abstract ElementNodeExpr   xmlElement(XsQNameExpr name, AttributeNodeExpr... attributes);
    public abstract ElementNodeExpr   xmlElement(String name, XmlContentNodeExpr... content);
    public abstract ElementNodeExpr   xmlElement(XsQNameExpr name, XmlContentNodeExpr... content);
    public abstract ElementNodeExpr   xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    public abstract ElementNodeExpr   xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    public abstract AttributeNodeExpr xmlAttribute(String name, XsAnySimpleTypeSeqExpr value);
    public abstract AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnySimpleTypeSeqExpr value);
    public abstract TextNodeExpr      xmlText(String value);
    public abstract TextNodeExpr      xmlText(XsAnySimpleTypeSeqExpr value);
    public abstract CommentNodeExpr   xmlComment(String content);
    public abstract CommentNodeExpr   xmlComment(XsAnySimpleTypeSeqExpr content);
    public abstract PINodeExpr        xmlPI(String name, String value);
    public abstract PINodeExpr        xmlPI(XsStringExpr name, XsAnySimpleTypeSeqExpr value);

    public abstract AttributeNodeSeqExpr xmlAttributes(AttributeNodeExpr... attributes);

    public interface Prefixer {
        public SemIriVal iri(String name);
    }

}
