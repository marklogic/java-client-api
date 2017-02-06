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
public abstract XsNumericExpr add(XsNumericExpr... operand);
    public abstract XsBooleanExpr and(XsAnyAtomicTypeExpr... operand);
    public abstract XsNumericExpr divide(XsNumericExpr operand1, XsNumericExpr operand2);
    public abstract XsBooleanExpr eq(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsBooleanExpr ge(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsBooleanExpr gt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsBooleanExpr isDefined(ItemExpr operand);
    public abstract XsBooleanExpr le(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsBooleanExpr lt(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsNumericExpr multiply(XsNumericExpr... operand);
    public abstract XsBooleanExpr ne(XsAnyAtomicTypeExpr operand1, XsAnyAtomicTypeExpr operand2);
    public abstract XsBooleanExpr not(XsAnyAtomicTypeExpr operand);
    public abstract XsBooleanExpr or(XsAnyAtomicTypeExpr... operand);
    public abstract XsNumericExpr subtract(XsNumericExpr operand1, XsNumericExpr operand2);
    public abstract PlanParamExpr param(String name);
    public abstract PlanParamExpr param(XsStringVal name);
    public abstract PlanColumn col(String column);
    public abstract PlanColumn col(XsStringVal column);
    public abstract PlanColumn schemaCol(String schema, String view, String column);
    public abstract PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column);
    public abstract PlanColumn viewCol(String view, String column);
    public abstract PlanColumn viewCol(XsStringVal view, XsStringVal column);
    public abstract PlanSystemColumn fragmentIdCol(String column);
    public abstract PlanSystemColumn fragmentIdCol(XsStringVal column);
    public abstract PlanSystemColumn graphCol(String column);
    public abstract PlanSystemColumn graphCol(XsStringVal column);
    public abstract PlanExprCol as(String column, ItemSeqExpr expression);
    public abstract PlanExprCol as(PlanColumn column, ItemSeqExpr expression);
    public abstract PlanExprColSeq cols(String... col);
    public abstract PlanExprColSeq cols(PlanExprCol... col);
    public abstract AccessPlan fromView(String schema, String view);
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view);
    public abstract AccessPlan fromView(String schema, String view, String qualifierName);
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName);
    public abstract AccessPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols);
    public abstract AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols);
    public abstract PlanPrefixer prefixer(String base);
    public abstract PlanPrefixer prefixer(XsStringVal base);
    public abstract AccessPlan fromTriples(PlanTriplePattern... patterns);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris, PlanTripleOption option);
    public abstract AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris, PlanTripleOption option);
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects);
    public abstract PlanTriplePattern pattern(PlanTriplePositionSeq subjects, PlanTriplePositionSeq predicates, PlanTriplePositionSeq objects, PlanSystemColumnSeq sysCols);
    public abstract PlanTriplePositionSeq subjects(PlanTriplePosition... subject);
    public abstract PlanTriplePositionSeq predicates(PlanTriplePosition... predicate);
    public abstract PlanTriplePositionSeq objects(PlanTriplePosition... object);
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes);
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName);
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName);
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, String qualifierName, PlanSystemColumn sysCols);
    public abstract AccessPlan fromLexicons(Map<String,CtsReferenceExpr> indexes, XsStringVal qualifierName, PlanSystemColumn sysCols);
    public abstract AccessPlan fromLiterals(Map<String,Object>[] rows, String qualifierName);
    public abstract AccessPlan fromLiterals(Map<String,Object>[] rows, XsStringVal qualifierName);
    public abstract PlanJoinKey on(String left, String right);
    public abstract PlanJoinKey on(PlanExprCol left, PlanExprCol right);
    public abstract PlanJoinKeySeq joinKeys(PlanJoinKey... key);
    public abstract PlanAggregateCol avg(String name, String column);
    public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol avg(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol avg(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol arrayAggregate(String name, String column);
    public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol arrayAggregate(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol count(String name);
    public abstract PlanAggregateCol count(PlanColumn name);
    public abstract PlanAggregateCol count(String name, String column);
    public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol count(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol count(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol groupConcat(String name, String column);
    public abstract PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol groupConcat(String name, String column, PlanGroupConcatOptionSeq options);
    public abstract PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column, PlanGroupConcatOptionSeq options);
    public abstract PlanAggregateCol max(String name, String column);
    public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol max(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol max(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol min(String name, String column);
    public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol min(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol min(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol sample(String name, String column);
    public abstract PlanAggregateCol sample(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol sequenceAggregate(String name, String column);
    public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol sequenceAggregate(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol sum(String name, String column);
    public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column);
    public abstract PlanAggregateCol sum(String name, String column, PlanValueOption option);
    public abstract PlanAggregateCol sum(PlanColumn name, PlanExprCol column, PlanValueOption option);
    public abstract PlanAggregateCol uda(String name, String column, String module, String function);
    public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function);
    public abstract PlanAggregateCol uda(String name, String column, String module, String function, String arg);
    public abstract PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg);
    public abstract PlanAggregateColSeq aggregates(PlanAggregateCol... aggregate);
    public abstract PlanSortKey asc(String column);
    public abstract PlanSortKey asc(PlanExprCol column);
    public abstract PlanSortKey desc(String column);
    public abstract PlanSortKey desc(PlanExprCol column);
    public abstract PlanSortKeySeq sortKeys(PlanSortKey... key);
    public abstract XsNumericExpr modulo(double left, double right);
    public abstract XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right);
    public abstract ItemSeqExpr caseExpr(PlanCase... cases);
    public abstract PlanCase when(boolean condition, ItemExpr... value);
    public abstract PlanCase when(XsBooleanExpr condition, ItemExpr... value);
    public abstract PlanCase elseExpr(ItemExpr value);
    public abstract NodeSeqExpr xpath(String column, String path);
    public abstract NodeSeqExpr xpath(PlanColumn column, XsStringExpr path);
    public abstract DocumentNodeExpr jsonDocument(JsonRootNodeExpr root);
    public abstract ObjectNodeExpr jsonObject(PlanJsonProperty... property);
    public abstract PlanJsonProperty prop(String key, JsonContentNodeExpr value);
    public abstract PlanJsonProperty prop(XsStringExpr key, JsonContentNodeExpr value);
    public abstract ArrayNodeExpr jsonArray(JsonContentNodeExpr... property);
    public abstract TextNodeExpr jsonString(String value);
    public abstract TextNodeExpr jsonString(XsAnyAtomicTypeExpr value);
    public abstract NumberNodeExpr jsonNumber(double value);
    public abstract NumberNodeExpr jsonNumber(XsNumericExpr value);
    public abstract BooleanNodeExpr jsonBoolean(boolean value);
    public abstract BooleanNodeExpr jsonBoolean(XsBooleanExpr value);
    public abstract NullNodeExpr jsonNull();
    public abstract DocumentNodeExpr xmlDocument(XmlRootNodeExpr root);
    public abstract ElementNodeExpr xmlElement(String name);
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name);
    public abstract ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes);
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes);
    public abstract ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    public abstract ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content);
    public abstract AttributeNodeExpr xmlAttribute(String name, String value);
    public abstract AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnyAtomicTypeExpr value);
    public abstract TextNodeExpr xmlText(String value);
    public abstract TextNodeExpr xmlText(XsAnyAtomicTypeExpr value);
    public abstract CommentNodeExpr xmlComment(String content);
    public abstract CommentNodeExpr xmlComment(XsAnyAtomicTypeExpr content);
    public abstract ProcessingInstructionNodeExpr xmlPi(String name, String value);
    public abstract ProcessingInstructionNodeExpr xmlPi(XsStringExpr name, XsAnyAtomicTypeExpr value);
    public abstract AttributeNodeSeqExpr xmlAttributes(AttributeNodeExpr... attribute);
    public abstract PlanFunction resolveFunction(String functionName, String modulePath);
    public abstract PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath);
    public interface AccessPlan extends ModifyPlan, PlanBuilderBase.AccessPlanBase {
        public abstract PlanColumn col(String column);
        public abstract PlanColumn col(XsStringVal column);
    }

    
    public interface ExportablePlan extends Plan, PlanBuilderBase.ExportablePlanBase {
        
    }

    
    public interface ModifyPlan extends PreparePlan, PlanBuilderBase.ModifyPlanBase {
        public abstract ModifyPlan except(ModifyPlan right);
        public abstract ModifyPlan groupBy(PlanExprColSeq keys);
        public abstract ModifyPlan groupBy(PlanExprColSeq keys, PlanAggregateColSeq aggregates);
        public abstract ModifyPlan intersect(ModifyPlan right);
        public abstract ModifyPlan joinCrossProduct(ModifyPlan right);
        public abstract ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition);
        public abstract ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition);
        public abstract ModifyPlan joinDoc(String docCol, String sourceCol);
        public abstract ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol);
        public abstract ModifyPlan joinDocUri(String uriCol, String fragmentIdCol);
        public abstract ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol);
        public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys);
        public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys);
        public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
        public abstract ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
        public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys);
        public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys);
        public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition);
        public abstract ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition);
        public abstract ModifyPlan orderBy(PlanSortKeySeq keys);
        public abstract PreparePlan prepare(int optimize);
        public abstract PreparePlan prepare(XsIntVal optimize);
        public abstract ModifyPlan select(PlanExprCol... columns);
        public abstract ModifyPlan select(PlanExprColSeq columns);
        public abstract ModifyPlan select(PlanExprColSeq columns, String qualifierName);
        public abstract ModifyPlan select(PlanExprColSeq columns, XsStringVal qualifierName);
        public abstract ModifyPlan union(ModifyPlan right);
        public abstract ModifyPlan whereDistinct();
    }

    
    public interface Plan extends PlanBuilderBase.PlanBase {
        public abstract Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal);
    }

    
    public interface PreparePlan extends ExportablePlan, PlanBuilderBase.PreparePlanBase {
        public abstract ExportablePlan map(PlanFunction func);
        public abstract ExportablePlan reduce(PlanFunction func);
        public abstract ExportablePlan reduce(PlanFunction func, String seed);
        public abstract ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed);
    }


}
