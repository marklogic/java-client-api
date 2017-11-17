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
  public XsNumericExpr add(XsNumericExpr... left) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for add() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "add", left);
  }

  
  @Override
  public PlanAggregateColSeq aggregateSeq(PlanAggregateCol... aggregate) {
    if (aggregate == null) {
      throw new IllegalArgumentException("aggregate parameter for aggregateSeq() cannot be null");
    }
    return new AggregateColSeqListImpl(aggregate);
  }

  
  @Override
  public XsBooleanExpr and(XsAnyAtomicTypeExpr... left) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for and() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "and", left);
  }

  
  @Override
  public PlanAggregateCol arrayAggregate(String name, String column) {
    return arrayAggregate((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for arrayAggregate() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for arrayAggregate() cannot be null");
    }
    return new AggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column });
  }

  
  @Override
  public PlanExprCol as(String column, ItemSeqExpr expression) {
    return as((column == null) ? (PlanColumn) null : col(column), expression);
  }

  
  @Override
  public PlanExprCol as(PlanColumn column, ItemSeqExpr expression) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for as() cannot be null");
    }
    return new ExprColCallImpl("op", "as", new Object[]{ column, expression });
  }

  
  @Override
  public PlanSortKey asc(String column) {
    return asc((column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanSortKey asc(PlanExprCol column) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for asc() cannot be null");
    }
    return new SortKeyCallImpl("op", "asc", new Object[]{ column });
  }

  
  @Override
  public PlanAggregateCol avg(String name, String column) {
    return avg((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol avg(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for avg() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for avg() cannot be null");
    }
    return new AggregateColCallImpl("op", "avg", new Object[]{ name, column });
  }

  
  @Override
  public PlanColumn col(String column) {
    return col((column == null) ? (XsStringVal) null : xs.string(column));
  }

  
  @Override
  public PlanColumn col(XsStringVal column) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for col() cannot be null");
    }
    return new ColumnCallImpl("op", "col", new Object[]{ column });
  }

  
  @Override
  public PlanExprColSeq colSeq(String... col) {
    return colSeq(
      (PlanExprCol[]) Arrays.stream(col)
        .map(item -> exprCol(item))
        .toArray(size -> new PlanExprCol[size])
        );
  }

  
  @Override
  public PlanExprColSeq colSeq(PlanExprCol... col) {
    if (col == null) {
      throw new IllegalArgumentException("col parameter for colSeq() cannot be null");
    }
    return new ExprColSeqListImpl(col);
  }

  
  @Override
  public PlanAggregateCol count(String name) {
    return count((name == null) ? (PlanColumn) null : col(name));
  }

  
  @Override
  public PlanAggregateCol count(PlanColumn name) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for count() cannot be null");
    }
    return new AggregateColCallImpl("op", "count", new Object[]{ name });
  }

  
  @Override
  public PlanAggregateCol count(String name, String column) {
    return count((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol count(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for count() cannot be null");
    }
    return new AggregateColCallImpl("op", "count", new Object[]{ name, column });
  }

  
  @Override
  public PlanSortKey desc(String column) {
    return desc((column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanSortKey desc(PlanExprCol column) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for desc() cannot be null");
    }
    return new SortKeyCallImpl("op", "desc", new Object[]{ column });
  }

  
  @Override
  public XsNumericExpr divide(XsNumericExpr left, XsNumericExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for divide() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for divide() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "divide", new Object[]{ left, right });
  }

  
  @Override
  public XsBooleanExpr eq(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for eq() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for eq() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "eq", new Object[]{ left, right });
  }

  
  @Override
  public PlanSystemColumn fragmentIdCol(String column) {
    return fragmentIdCol((column == null) ? (XsStringVal) null : xs.string(column));
  }

  
  @Override
  public PlanSystemColumn fragmentIdCol(XsStringVal column) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for fragmentIdCol() cannot be null");
    }
    return new SystemColumnCallImpl("op", "fragment-id-col", new Object[]{ column });
  }

  
  @Override
  public ModifyPlan fromSparql(String select) {
    return fromSparql((select == null) ? (XsStringVal) null : xs.string(select));
  }

  
  @Override
  public ModifyPlan fromSparql(XsStringVal select) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSparql() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl("op", "from-sparql", new Object[]{ select });
  }

  
  @Override
  public ModifyPlan fromSparql(String select, String qualifierName) {
    return fromSparql((select == null) ? (XsStringVal) null : xs.string(select), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
  }

  
  @Override
  public ModifyPlan fromSparql(XsStringVal select, XsStringVal qualifierName) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSparql() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl("op", "from-sparql", new Object[]{ select, qualifierName });
  }

  
  @Override
  public ModifyPlan fromSql(String select) {
    return fromSql((select == null) ? (XsStringVal) null : xs.string(select));
  }

  
  @Override
  public ModifyPlan fromSql(XsStringVal select) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSql() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl("op", "from-sql", new Object[]{ select });
  }

  
  @Override
  public ModifyPlan fromSql(String select, String qualifierName) {
    return fromSql((select == null) ? (XsStringVal) null : xs.string(select), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
  }

  
  @Override
  public ModifyPlan fromSql(XsStringVal select, XsStringVal qualifierName) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSql() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl("op", "from-sql", new Object[]{ select, qualifierName });
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePattern... patterns) {
    return fromTriples(new TriplePatternSeqListImpl(patterns));
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns) {
    if (patterns == null) {
      throw new IllegalArgumentException("patterns parameter for fromTriples() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns });
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName) {
    return fromTriples(patterns, (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName) {
    if (patterns == null) {
      throw new IllegalArgumentException("patterns parameter for fromTriples() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns, qualifierName });
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris) {
    return fromTriples(patterns, (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName), (graphIris == null) ? (XsStringVal) null : xs.string(graphIris));
  }

  
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris) {
    if (patterns == null) {
      throw new IllegalArgumentException("patterns parameter for fromTriples() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-triples", new Object[]{ patterns, qualifierName, graphIris });
  }

  
  @Override
  public AccessPlan fromView(String schema, String view) {
    return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view));
  }

  
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view) {
    if (view == null) {
      throw new IllegalArgumentException("view parameter for fromView() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view });
  }

  
  @Override
  public AccessPlan fromView(String schema, String view, String qualifierName) {
    return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName));
  }

  
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName) {
    if (view == null) {
      throw new IllegalArgumentException("view parameter for fromView() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view, qualifierName });
  }

  
  @Override
  public AccessPlan fromView(String schema, String view, String qualifierName, PlanSystemColumn sysCols) {
    return fromView((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName), sysCols);
  }

  
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols) {
    if (view == null) {
      throw new IllegalArgumentException("view parameter for fromView() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-view", new Object[]{ schema, view, qualifierName, sysCols });
  }

  
  @Override
  public XsBooleanExpr ge(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for ge() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for ge() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "ge", new Object[]{ left, right });
  }

  
  @Override
  public PlanSystemColumn graphCol(String column) {
    return graphCol((column == null) ? (XsStringVal) null : xs.string(column));
  }

  
  @Override
  public PlanSystemColumn graphCol(XsStringVal column) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for graphCol() cannot be null");
    }
    return new SystemColumnCallImpl("op", "graph-col", new Object[]{ column });
  }

  
  @Override
  public XsBooleanExpr gt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for gt() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for gt() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "gt", new Object[]{ left, right });
  }

  
  @Override
  public XsBooleanExpr isDefined(ItemExpr operand) {
    if (operand == null) {
      throw new IllegalArgumentException("operand parameter for isDefined() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "is-defined", new Object[]{ operand });
  }

  
  @Override
  public PlanJoinKeySeq joinKeySeq(PlanJoinKey... key) {
    if (key == null) {
      throw new IllegalArgumentException("key parameter for joinKeySeq() cannot be null");
    }
    return new JoinKeySeqListImpl(key);
  }

  
  @Override
  public BooleanNodeExpr jsonBoolean(boolean value) {
    return jsonBoolean(xs.booleanVal(value));
  }

  
  @Override
  public BooleanNodeExpr jsonBoolean(XsBooleanExpr value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonBoolean() cannot be null");
    }
    return new BaseTypeImpl.BooleanNodeCallImpl("op", "json-boolean", new Object[]{ value });
  }

  
  @Override
  public DocumentNodeExpr jsonDocument(JsonRootNodeExpr root) {
    if (root == null) {
      throw new IllegalArgumentException("root parameter for jsonDocument() cannot be null");
    }
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
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonNumber() cannot be null");
    }
    return new BaseTypeImpl.NumberNodeCallImpl("op", "json-number", new Object[]{ value });
  }

  
  @Override
  public TextNodeExpr jsonString(String value) {
    return jsonString((value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public TextNodeExpr jsonString(XsAnyAtomicTypeExpr value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonString() cannot be null");
    }
    return new BaseTypeImpl.TextNodeCallImpl("op", "json-string", new Object[]{ value });
  }

  
  @Override
  public XsBooleanExpr le(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for le() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for le() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "le", new Object[]{ left, right });
  }

  
  @Override
  public XsBooleanExpr lt(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for lt() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for lt() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "lt", new Object[]{ left, right });
  }

  
  @Override
  public PlanAggregateCol max(String name, String column) {
    return max((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol max(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for max() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for max() cannot be null");
    }
    return new AggregateColCallImpl("op", "max", new Object[]{ name, column });
  }

  
  @Override
  public PlanAggregateCol min(String name, String column) {
    return min((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol min(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for min() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for min() cannot be null");
    }
    return new AggregateColCallImpl("op", "min", new Object[]{ name, column });
  }

  
  @Override
  public XsNumericExpr modulo(double left, double right) {
    return modulo(xs.doubleVal(left), xs.doubleVal(right));
  }

  
  @Override
  public XsNumericExpr modulo(XsNumericExpr left, XsNumericExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for modulo() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for modulo() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "modulo", new Object[]{ left, right });
  }

  
  @Override
  public XsNumericExpr multiply(XsNumericExpr... left) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for multiply() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "multiply", left);
  }

  
  @Override
  public XsBooleanExpr ne(XsAnyAtomicTypeExpr left, XsAnyAtomicTypeExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for ne() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for ne() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "ne", new Object[]{ left, right });
  }

  
  @Override
  public XsBooleanExpr not(XsAnyAtomicTypeExpr operand) {
    if (operand == null) {
      throw new IllegalArgumentException("operand parameter for not() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "not", new Object[]{ operand });
  }

  
  @Override
  public PlanTriplePositionSeq objectSeq(PlanTriplePosition... object) {
    if (object == null) {
      throw new IllegalArgumentException("object parameter for objectSeq() cannot be null");
    }
    return new TriplePositionSeqListImpl(object);
  }

  
  @Override
  public PlanJoinKey on(String left, String right) {
    return on((left == null) ? (PlanExprCol) null : exprCol(left), (right == null) ? (PlanExprCol) null : exprCol(right));
  }

  
  @Override
  public PlanJoinKey on(PlanExprCol left, PlanExprCol right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for on() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for on() cannot be null");
    }
    return new JoinKeyCallImpl("op", "on", new Object[]{ left, right });
  }

  
  @Override
  public XsBooleanExpr or(XsAnyAtomicTypeExpr... left) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for or() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "or", left);
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
  public PlanTriplePatternSeq patternSeq(PlanTriplePattern... pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for patternSeq() cannot be null");
    }
    return new TriplePatternSeqListImpl(pattern);
  }

  
  @Override
  public PlanTriplePositionSeq predicateSeq(PlanTriplePosition... predicate) {
    if (predicate == null) {
      throw new IllegalArgumentException("predicate parameter for predicateSeq() cannot be null");
    }
    return new TriplePositionSeqListImpl(predicate);
  }

  
  @Override
  public PlanJsonProperty prop(String key, JsonContentNodeExpr value) {
    return prop((key == null) ? (XsStringExpr) null : xs.string(key), value);
  }

  
  @Override
  public PlanJsonProperty prop(XsStringExpr key, JsonContentNodeExpr value) {
    if (key == null) {
      throw new IllegalArgumentException("key parameter for prop() cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value parameter for prop() cannot be null");
    }
    return new JsonPropertyCallImpl("op", "prop", new Object[]{ key, value });
  }

  
  @Override
  public PlanFunction resolveFunction(String functionName, String modulePath) {
    return resolveFunction((functionName == null) ? (XsQNameVal) null : xs.QName(functionName), (modulePath == null) ? (XsStringVal) null : xs.string(modulePath));
  }

  
  @Override
  public PlanFunction resolveFunction(XsQNameVal functionName, XsStringVal modulePath) {
    if (functionName == null) {
      throw new IllegalArgumentException("functionName parameter for resolveFunction() cannot be null");
    }
    if (modulePath == null) {
      throw new IllegalArgumentException("modulePath parameter for resolveFunction() cannot be null");
    }
    return new FunctionCallImpl("op", "resolve-function", new Object[]{ functionName, modulePath });
  }

  
  @Override
  public PlanAggregateCol sample(String name, String column) {
    return sample((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol sample(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for sample() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for sample() cannot be null");
    }
    return new AggregateColCallImpl("op", "sample", new Object[]{ name, column });
  }

  
  @Override
  public PlanColumn schemaCol(String schema, String view, String column) {
    return schemaCol((schema == null) ? (XsStringVal) null : xs.string(schema), (view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column));
  }

  
  @Override
  public PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column) {
    if (schema == null) {
      throw new IllegalArgumentException("schema parameter for schemaCol() cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("view parameter for schemaCol() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for schemaCol() cannot be null");
    }
    return new ColumnCallImpl("op", "schema-col", new Object[]{ schema, view, column });
  }

  
  @Override
  public PlanAggregateCol sequenceAggregate(String name, String column) {
    return sequenceAggregate((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for sequenceAggregate() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for sequenceAggregate() cannot be null");
    }
    return new AggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column });
  }

  
  @Override
  public PlanSortKeySeq sortKeySeq(PlanSortKey... key) {
    if (key == null) {
      throw new IllegalArgumentException("key parameter for sortKeySeq() cannot be null");
    }
    return new SortKeySeqListImpl(key);
  }

  
  @Override
  public PlanCondition sqlCondition(String expression) {
    return sqlCondition((expression == null) ? (XsStringVal) null : xs.string(expression));
  }

  
  @Override
  public PlanCondition sqlCondition(XsStringVal expression) {
    if (expression == null) {
      throw new IllegalArgumentException("expression parameter for sqlCondition() cannot be null");
    }
    return new ConditionCallImpl("op", "sql-condition", new Object[]{ expression });
  }

  
  @Override
  public PlanTriplePositionSeq subjectSeq(PlanTriplePosition... subject) {
    if (subject == null) {
      throw new IllegalArgumentException("subject parameter for subjectSeq() cannot be null");
    }
    return new TriplePositionSeqListImpl(subject);
  }

  
  @Override
  public XsNumericExpr subtract(XsNumericExpr left, XsNumericExpr right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for subtract() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for subtract() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "subtract", new Object[]{ left, right });
  }

  
  @Override
  public PlanAggregateCol sum(String name, String column) {
    return sum((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }

  
  @Override
  public PlanAggregateCol sum(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for sum() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for sum() cannot be null");
    }
    return new AggregateColCallImpl("op", "sum", new Object[]{ name, column });
  }

  
  @Override
  public PlanAggregateCol uda(String name, String column, String module, String function) {
    return uda((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column), (module == null) ? (XsStringVal) null : xs.string(module), (function == null) ? (XsStringVal) null : xs.string(function));
  }

  
  @Override
  public PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for uda() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for uda() cannot be null");
    }
    if (module == null) {
      throw new IllegalArgumentException("module parameter for uda() cannot be null");
    }
    if (function == null) {
      throw new IllegalArgumentException("function parameter for uda() cannot be null");
    }
    return new AggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function });
  }

  
  @Override
  public PlanAggregateCol uda(String name, String column, String module, String function, String arg) {
    return uda((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column), (module == null) ? (XsStringVal) null : xs.string(module), (function == null) ? (XsStringVal) null : xs.string(function), (arg == null) ? (XsAnyAtomicTypeVal) null : xs.string(arg));
  }

  
  @Override
  public PlanAggregateCol uda(PlanColumn name, PlanExprCol column, XsStringVal module, XsStringVal function, XsAnyAtomicTypeVal arg) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for uda() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for uda() cannot be null");
    }
    if (module == null) {
      throw new IllegalArgumentException("module parameter for uda() cannot be null");
    }
    if (function == null) {
      throw new IllegalArgumentException("function parameter for uda() cannot be null");
    }
    return new AggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function, arg });
  }

  
  @Override
  public PlanColumn viewCol(String view, String column) {
    return viewCol((view == null) ? (XsStringVal) null : xs.string(view), (column == null) ? (XsStringVal) null : xs.string(column));
  }

  
  @Override
  public PlanColumn viewCol(XsStringVal view, XsStringVal column) {
    if (view == null) {
      throw new IllegalArgumentException("view parameter for viewCol() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for viewCol() cannot be null");
    }
    return new ColumnCallImpl("op", "view-col", new Object[]{ view, column });
  }

  
  @Override
  public PlanCase when(boolean condition, ItemExpr... value) {
    return when(xs.booleanVal(condition), new BaseTypeImpl.ItemSeqListImpl(value));
  }

  
  @Override
  public PlanCase when(XsBooleanExpr condition, ItemExpr... value) {
    if (condition == null) {
      throw new IllegalArgumentException("condition parameter for when() cannot be null");
    }
    return new CaseCallImpl("op", "when", new Object[]{ condition, new BaseTypeImpl.ItemSeqListImpl(value) });
  }

  
  @Override
  public AttributeNodeExpr xmlAttribute(String name, String value) {
    return xmlAttribute((name == null) ? (XsQNameExpr) null : xs.QName(name), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public AttributeNodeExpr xmlAttribute(XsQNameExpr name, XsAnyAtomicTypeExpr value) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlAttribute() cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlAttribute() cannot be null");
    }
    return new BaseTypeImpl.AttributeNodeCallImpl("op", "xml-attribute", new Object[]{ name, value });
  }

  
  @Override
  public AttributeNodeSeqExpr xmlAttributeSeq(AttributeNodeExpr... attribute) {
    if (attribute == null) {
      throw new IllegalArgumentException("attribute parameter for xmlAttributeSeq() cannot be null");
    }
    return new BaseTypeImpl.AttributeNodeSeqListImpl(attribute);
  }

  
  @Override
  public CommentNodeExpr xmlComment(String content) {
    return xmlComment((content == null) ? (XsAnyAtomicTypeExpr) null : xs.string(content));
  }

  
  @Override
  public CommentNodeExpr xmlComment(XsAnyAtomicTypeExpr content) {
    if (content == null) {
      throw new IllegalArgumentException("content parameter for xmlComment() cannot be null");
    }
    return new BaseTypeImpl.CommentNodeCallImpl("op", "xml-comment", new Object[]{ content });
  }

  
  @Override
  public DocumentNodeExpr xmlDocument(XmlRootNodeExpr root) {
    if (root == null) {
      throw new IllegalArgumentException("root parameter for xmlDocument() cannot be null");
    }
    return new BaseTypeImpl.DocumentNodeCallImpl("op", "xml-document", new Object[]{ root });
  }

  
  @Override
  public ElementNodeExpr xmlElement(String name) {
    return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name));
  }

  
  @Override
  public ElementNodeExpr xmlElement(XsQNameExpr name) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name });
  }

  
  @Override
  public ElementNodeExpr xmlElement(String name, AttributeNodeExpr... attributes) {
    return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name), new BaseTypeImpl.AttributeNodeSeqListImpl(attributes));
  }

  
  @Override
  public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes });
  }

  
  @Override
  public ElementNodeExpr xmlElement(String name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
    return xmlElement((name == null) ? (XsQNameExpr) null : xs.QName(name), attributes, new BaseTypeImpl.XmlContentNodeSeqListImpl(content));
  }

  
  @Override
  public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeExpr... content) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes, new BaseTypeImpl.XmlContentNodeSeqListImpl(content) });
  }

  
  @Override
  public ProcessingInstructionNodeExpr xmlPi(String name, String value) {
    return xmlPi((name == null) ? (XsStringExpr) null : xs.string(name), (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public ProcessingInstructionNodeExpr xmlPi(XsStringExpr name, XsAnyAtomicTypeExpr value) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlPi() cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlPi() cannot be null");
    }
    return new BaseTypeImpl.ProcessingInstructionNodeCallImpl("op", "xml-pi", new Object[]{ name, value });
  }

  
  @Override
  public TextNodeExpr xmlText(String value) {
    return xmlText((value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public TextNodeExpr xmlText(XsAnyAtomicTypeExpr value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlText() cannot be null");
    }
    return new BaseTypeImpl.TextNodeCallImpl("op", "xml-text", new Object[]{ value });
  }

  
  @Override
  public NodeSeqExpr xpath(String column, String path) {
    return xpath((column == null) ? (PlanColumn) null : col(column), (path == null) ? (XsStringExpr) null : xs.string(path));
  }

  
  @Override
  public NodeSeqExpr xpath(PlanColumn column, XsStringExpr path) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for xpath() cannot be null");
    }
    if (path == null) {
      throw new IllegalArgumentException("path parameter for xpath() cannot be null");
    }
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

  
  static class ConditionSeqListImpl extends PlanSeqListImpl implements PlanConditionSeq {
    ConditionSeqListImpl(Object[] items) {
      super(items);
    }
  }

  
  static class ConditionSeqCallImpl extends PlanCallImpl implements PlanConditionSeq {
    ConditionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  
  static class ConditionCallImpl extends PlanCallImpl implements PlanCondition {
    ConditionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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
    if (column == null) {
      throw new IllegalArgumentException("column parameter for col() cannot be null");
    }
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
    if (right == null) {
      throw new IllegalArgumentException("right parameter for except() cannot be null");
    }
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
    if (right == null) {
      throw new IllegalArgumentException("right parameter for intersect() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "intersect", new Object[]{ right });
  }

    
  @Override
  public ModifyPlan joinCrossProduct(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinCrossProduct() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-cross-product", new Object[]{ right });
  }

    
  @Override
  public ModifyPlan joinCrossProduct(ModifyPlan right, boolean condition) {
    return joinCrossProduct(right, xs.booleanVal(condition));
  }

    
  @Override
  public ModifyPlan joinCrossProduct(ModifyPlan right, XsBooleanExpr condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinCrossProduct() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-cross-product", new Object[]{ right, condition });
  }

    
  @Override
  public ModifyPlan joinDoc(String docCol, String sourceCol) {
    return joinDoc((docCol == null) ? (PlanColumn) null : col(docCol), (sourceCol == null) ? (PlanColumn) null : col(sourceCol));
  }

    
  @Override
  public ModifyPlan joinDoc(PlanColumn docCol, PlanColumn sourceCol) {
    if (docCol == null) {
      throw new IllegalArgumentException("docCol parameter for joinDoc() cannot be null");
    }
    if (sourceCol == null) {
      throw new IllegalArgumentException("sourceCol parameter for joinDoc() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc", new Object[]{ docCol, sourceCol });
  }

    
  @Override
  public ModifyPlan joinDocUri(String uriCol, String fragmentIdCol) {
    return joinDocUri((uriCol == null) ? (PlanColumn) null : col(uriCol), (fragmentIdCol == null) ? (PlanColumn) null : col(fragmentIdCol));
  }

    
  @Override
  public ModifyPlan joinDocUri(PlanColumn uriCol, PlanColumn fragmentIdCol) {
    if (uriCol == null) {
      throw new IllegalArgumentException("uriCol parameter for joinDocUri() cannot be null");
    }
    if (fragmentIdCol == null) {
      throw new IllegalArgumentException("fragmentIdCol parameter for joinDocUri() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc-uri", new Object[]{ uriCol, fragmentIdCol });
  }

    
  @Override
  public ModifyPlan joinInner(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinInner() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-inner", new Object[]{ right });
  }

    
  @Override
  public ModifyPlan joinInner(ModifyPlan right, PlanJoinKey... keys) {
    return joinInner(right, new JoinKeySeqListImpl(keys));
  }

    
  @Override
  public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinInner() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-inner", new Object[]{ right, keys });
  }

    
  @Override
  public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
    return joinInner(right, keys, xs.booleanVal(condition));
  }

    
  @Override
  public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinInner() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-inner", new Object[]{ right, keys, condition });
  }

    
  @Override
  public ModifyPlan joinLeftOuter(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right });
  }

    
  @Override
  public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKey... keys) {
    return joinLeftOuter(right, new JoinKeySeqListImpl(keys));
  }

    
  @Override
  public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right, keys });
  }

    
  @Override
  public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
    return joinLeftOuter(right, keys, xs.booleanVal(condition));
  }

    
  @Override
  public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, XsBooleanExpr condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right, keys, condition });
  }

    
  @Override
  public ModifyPlan orderBy(PlanSortKeySeq keys) {
    if (keys == null) {
      throw new IllegalArgumentException("keys parameter for orderBy() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "order-by", new Object[]{ keys });
  }

    
  @Override
  public PreparePlan prepare(int optimize) {
    return prepare(xs.intVal(optimize));
  }

    
  @Override
  public PreparePlan prepare(XsIntVal optimize) {
    if (optimize == null) {
      throw new IllegalArgumentException("optimize parameter for prepare() cannot be null");
    }
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
    if (right == null) {
      throw new IllegalArgumentException("right parameter for union() cannot be null");
    }
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

    
  }

  
  static abstract class PreparePlanImpl extends PlanBuilderSubImpl.ExportablePlanSubImpl implements PreparePlan {
    PreparePlanImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
    }

    
  @Override
  public ExportablePlan map(PlanFunction func) {
    if (func == null) {
      throw new IllegalArgumentException("func parameter for map() cannot be null");
    }
    return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "map", new Object[]{ func });
  }

    
  @Override
  public ExportablePlan reduce(PlanFunction func) {
    if (func == null) {
      throw new IllegalArgumentException("func parameter for reduce() cannot be null");
    }
    return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "reduce", new Object[]{ func });
  }

    
  @Override
  public ExportablePlan reduce(PlanFunction func, String seed) {
    return reduce(func, (seed == null) ? (XsAnyAtomicTypeVal) null : xs.string(seed));
  }

    
  @Override
  public ExportablePlan reduce(PlanFunction func, XsAnyAtomicTypeVal seed) {
    if (func == null) {
      throw new IllegalArgumentException("func parameter for reduce() cannot be null");
    }
    return new PlanBuilderSubImpl.ExportablePlanSubImpl(this, "op", "reduce", new Object[]{ func, seed });
  }

  }


}
