/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import java.util.Arrays;

import com.marklogic.client.type.*;

// IMPORTANT: Do not edit. This file is generated.
abstract class PlanBuilderImpl extends PlanBuilderBaseImpl {
  PlanBuilderImpl() {
  }

  // builder methods

  @Override
  public ServerExpression add(ServerExpression... left) {
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
  public ServerExpression and(ServerExpression... left) {
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
  public PlanExprCol as(String column, ServerExpression expression) {
    return as((column == null) ? (PlanColumn) null : col(column), expression);
  }


  @Override
  public PlanExprCol as(PlanColumn column, ServerExpression expression) {
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
  public PlanNamedGroup bucketGroup(String name, String key, String boundaries) {
    return bucketGroup((name == null) ? (XsStringVal) null : xs.string(name), (key == null) ? (PlanExprCol) null : exprCol(key), (boundaries == null) ? (XsAnyAtomicTypeVal) null : xs.string(boundaries));
  }


  @Override
  public PlanNamedGroup bucketGroup(XsStringVal name, PlanExprCol key, XsAnyAtomicTypeSeqVal boundaries) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for bucketGroup() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for bucketGroup() cannot be null");
    }
    if (boundaries == null) {
      throw new IllegalArgumentException("boundaries parameter for bucketGroup() cannot be null");
    }
    return new NamedGroupCallImpl("op", "bucket-group", new Object[]{ name, key, boundaries });
  }


  @Override
  public PlanNamedGroup bucketGroup(String name, String key, String boundaries, String collation) {
    return bucketGroup((name == null) ? (XsStringVal) null : xs.string(name), (key == null) ? (PlanExprCol) null : exprCol(key), (boundaries == null) ? (XsAnyAtomicTypeVal) null : xs.string(boundaries), (collation == null) ? (XsStringVal) null : xs.string(collation));
  }


  @Override
  public PlanNamedGroup bucketGroup(XsStringVal name, PlanExprCol key, XsAnyAtomicTypeSeqVal boundaries, XsStringVal collation) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for bucketGroup() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for bucketGroup() cannot be null");
    }
    if (boundaries == null) {
      throw new IllegalArgumentException("boundaries parameter for bucketGroup() cannot be null");
    }
    return new NamedGroupCallImpl("op", "bucket-group", new Object[]{ name, key, boundaries, collation });
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
  public PlanGroupSeq cube(PlanExprColSeq keys) {
    if (keys == null) {
      throw new IllegalArgumentException("keys parameter for cube() cannot be null");
    }
    return new GroupSeqCallImpl("op", "cube", new Object[]{ keys });
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
  public ServerExpression divide(ServerExpression left, ServerExpression right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for divide() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for divide() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "divide", new Object[]{ left, right });
  }


  @Override
  public PlanRowColTypesSeq docColTypes() {
    return new RowColTypesSeqCallImpl("op", "doc-col-types", new Object[]{  });
  }


  @Override
  public PlanDocColsIdentifier docCols() {
    return new DocColsIdentifierCallImpl("op", "doc-cols", new Object[]{  });
  }


  @Override
  public PlanDocColsIdentifier docCols(String qualifier) {
    return docCols((qualifier == null) ? (XsStringVal) null : xs.string(qualifier));
  }


  @Override
  public PlanDocColsIdentifier docCols(XsStringVal qualifier) {
    return new DocColsIdentifierCallImpl("op", "doc-cols", new Object[]{ qualifier });
  }


  @Override
  public PlanDocColsIdentifier docCols(String qualifier, String names) {
    return docCols((qualifier == null) ? (XsStringVal) null : xs.string(qualifier), (names == null) ? (XsStringVal) null : xs.string(names));
  }


  @Override
  public PlanDocColsIdentifier docCols(XsStringVal qualifier, XsStringSeqVal names) {
    return new DocColsIdentifierCallImpl("op", "doc-cols", new Object[]{ qualifier, names });
  }


  @Override
  public ServerExpression eq(ServerExpression... operand) {
    if (operand == null) {
      throw new IllegalArgumentException("operand parameter for eq() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "eq", operand);
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
  public AccessPlan fromDocDescriptors(PlanDocDescriptor... docDescriptor) {
    return fromDocDescriptors(new DocDescriptorSeqListImpl(docDescriptor));
  }


  @Override
  public AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor) {
    if (docDescriptor == null) {
      throw new IllegalArgumentException("docDescriptor parameter for fromDocDescriptors() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-doc-descriptors", new Object[]{ docDescriptor });
  }


  @Override
  public AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor, String qualifier) {
    return fromDocDescriptors(docDescriptor, (qualifier == null) ? (XsStringVal) null : xs.string(qualifier));
  }


  @Override
  public AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor, XsStringVal qualifier) {
    if (docDescriptor == null) {
      throw new IllegalArgumentException("docDescriptor parameter for fromDocDescriptors() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-doc-descriptors", new Object[]{ docDescriptor, qualifier });
  }


  @Override
  public AccessPlan fromParam(String paramName, String qualifier, PlanRowColTypesSeq rowColTypes) {
    return fromParam((paramName == null) ? (XsStringVal) null : xs.string(paramName), (qualifier == null) ? (XsStringVal) null : xs.string(qualifier), rowColTypes);
  }


  @Override
  public AccessPlan fromParam(XsStringVal paramName, XsStringVal qualifier, PlanRowColTypesSeq rowColTypes) {
    if (paramName == null) {
      throw new IllegalArgumentException("paramName parameter for fromParam() cannot be null");
    }
    if (rowColTypes == null) {
      throw new IllegalArgumentException("rowColTypes parameter for fromParam() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl("op", "from-param", new Object[]{ paramName, qualifier, rowColTypes });
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
  public ServerExpression ge(ServerExpression left, ServerExpression right) {
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
  public PlanGroup group(PlanExprColSeq keys) {
    return new GroupCallImpl("op", "group", new Object[]{ keys });
  }


  @Override
  public PlanAggregateCol groupKey(String name, String column) {
    return groupKey((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }


  @Override
  public PlanAggregateCol groupKey(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for groupKey() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for groupKey() cannot be null");
    }
    return new AggregateColCallImpl("op", "group-key", new Object[]{ name, column });
  }


  @Override
  public ServerExpression gt(ServerExpression left, ServerExpression right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for gt() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for gt() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "gt", new Object[]{ left, right });
  }


  @Override
  public PlanAggregateCol hasGroupKey(String name, String column) {
    return hasGroupKey((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }


  @Override
  public PlanAggregateCol hasGroupKey(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for hasGroupKey() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for hasGroupKey() cannot be null");
    }
    return new AggregateColCallImpl("op", "has-group-key", new Object[]{ name, column });
  }


  @Override
  public ServerExpression in(ServerExpression value, ServerExpression anyOf) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for in() cannot be null");
    }
    if (anyOf == null) {
      throw new IllegalArgumentException("anyOf parameter for in() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "in", new Object[]{ value, anyOf });
  }


  @Override
  public ServerExpression isDefined(ServerExpression operand) {
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
  public ServerExpression jsonBoolean(boolean value) {
    return jsonBoolean(xs.booleanVal(value));
  }


  @Override
  public ServerExpression jsonBoolean(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonBoolean() cannot be null");
    }
    return new BaseTypeImpl.BooleanNodeCallImpl("op", "json-boolean", new Object[]{ value });
  }


  @Override
  public ServerExpression jsonDocument(ServerExpression root) {
    if (root == null) {
      throw new IllegalArgumentException("root parameter for jsonDocument() cannot be null");
    }
    return new BaseTypeImpl.DocumentNodeCallImpl("op", "json-document", new Object[]{ root });
  }


  @Override
  public ServerExpression jsonNull() {
    return new BaseTypeImpl.NullNodeCallImpl("op", "json-null", new Object[]{  });
  }


  @Override
  public ServerExpression jsonNumber(double value) {
    return jsonNumber(xs.doubleVal(value));
  }


  @Override
  public ServerExpression jsonNumber(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonNumber() cannot be null");
    }
    return new BaseTypeImpl.NumberNodeCallImpl("op", "json-number", new Object[]{ value });
  }


  @Override
  public ServerExpression jsonString(String value) {
    return jsonString((value == null) ? (ServerExpression) null : xs.string(value));
  }


  @Override
  public ServerExpression jsonString(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for jsonString() cannot be null");
    }
    return new BaseTypeImpl.TextNodeCallImpl("op", "json-string", new Object[]{ value });
  }


  @Override
  public ServerExpression le(ServerExpression left, ServerExpression right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for le() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for le() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "le", new Object[]{ left, right });
  }


  @Override
  public ServerExpression lt(ServerExpression left, ServerExpression right) {
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
  public ServerExpression modulo(double left, double right) {
    return modulo(xs.doubleVal(left), xs.doubleVal(right));
  }


  @Override
  public ServerExpression modulo(ServerExpression left, ServerExpression right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for modulo() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for modulo() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "modulo", new Object[]{ left, right });
  }


  @Override
  public ServerExpression multiply(ServerExpression... left) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for multiply() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("op", "multiply", left);
  }


  @Override
  public PlanNamedGroup namedGroup(String name) {
    return namedGroup((name == null) ? (XsStringVal) null : xs.string(name));
  }


  @Override
  public PlanNamedGroup namedGroup(XsStringVal name) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for namedGroup() cannot be null");
    }
    return new NamedGroupCallImpl("op", "named-group", new Object[]{ name });
  }


  @Override
  public PlanNamedGroup namedGroup(String name, String keys) {
    return namedGroup((name == null) ? (XsStringVal) null : xs.string(name), (keys == null) ? (PlanExprCol) null : exprCol(keys));
  }


  @Override
  public PlanNamedGroup namedGroup(XsStringVal name, PlanExprColSeq keys) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for namedGroup() cannot be null");
    }
    return new NamedGroupCallImpl("op", "named-group", new Object[]{ name, keys });
  }


  @Override
  public ServerExpression ne(ServerExpression left, ServerExpression right) {
    if (left == null) {
      throw new IllegalArgumentException("left parameter for ne() cannot be null");
    }
    if (right == null) {
      throw new IllegalArgumentException("right parameter for ne() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("op", "ne", new Object[]{ left, right });
  }


  @Override
  public ServerExpression not(ServerExpression operand) {
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
  public ServerExpression or(ServerExpression... left) {
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
  public PlanJsonProperty prop(String key, ServerExpression value) {
    return prop((key == null) ? (ServerExpression) null : xs.string(key), value);
  }


  @Override
  public PlanJsonProperty prop(ServerExpression key, ServerExpression value) {
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
  public PlanGroupSeq rollup(PlanExprColSeq keys) {
    if (keys == null) {
      throw new IllegalArgumentException("keys parameter for rollup() cannot be null");
    }
    return new GroupSeqCallImpl("op", "rollup", new Object[]{ keys });
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
  public ServerExpression subtract(ServerExpression left, ServerExpression right) {
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
  public PlanCase when(boolean condition, ServerExpression... value) {
    return when(xs.booleanVal(condition), value);
  }


  @Override
  public PlanCase when(ServerExpression condition, ServerExpression... value) {
    if (condition == null) {
      throw new IllegalArgumentException("condition parameter for when() cannot be null");
    }
    return new CaseCallImpl("op", "when", new Object[]{ condition, new BaseTypeImpl.ItemSeqListImpl(value) });
  }


  @Override
  public ServerExpression xmlAttribute(String name, String value) {
    return xmlAttribute((name == null) ? (ServerExpression) null : xs.QName(name), (value == null) ? (ServerExpression) null : xs.string(value));
  }


  @Override
  public ServerExpression xmlAttribute(ServerExpression name, ServerExpression value) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlAttribute() cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlAttribute() cannot be null");
    }
    return new BaseTypeImpl.AttributeNodeCallImpl("op", "xml-attribute", new Object[]{ name, value });
  }


  @Override
  public ServerExpression xmlAttributeSeq(ServerExpression... attribute) {
    if (attribute == null) {
      throw new IllegalArgumentException("attribute parameter for xmlAttributeSeq() cannot be null");
    }
    return new BaseTypeImpl.AttributeNodeSeqListImpl(attribute);
  }


  @Override
  public ServerExpression xmlComment(String content) {
    return xmlComment((content == null) ? (ServerExpression) null : xs.string(content));
  }


  @Override
  public ServerExpression xmlComment(ServerExpression content) {
    if (content == null) {
      throw new IllegalArgumentException("content parameter for xmlComment() cannot be null");
    }
    return new BaseTypeImpl.CommentNodeCallImpl("op", "xml-comment", new Object[]{ content });
  }


  @Override
  public ServerExpression xmlDocument(ServerExpression root) {
    if (root == null) {
      throw new IllegalArgumentException("root parameter for xmlDocument() cannot be null");
    }
    return new BaseTypeImpl.DocumentNodeCallImpl("op", "xml-document", new Object[]{ root });
  }


  @Override
  public ServerExpression xmlElement(String name) {
    return xmlElement((name == null) ? (ServerExpression) null : xs.QName(name));
  }


  @Override
  public ServerExpression xmlElement(ServerExpression name) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name });
  }


  @Override
  public ServerExpression xmlElement(String name, ServerExpression attributes) {
    return xmlElement((name == null) ? (ServerExpression) null : xs.QName(name), attributes);
  }


  @Override
  public ServerExpression xmlElement(ServerExpression name, ServerExpression attributes) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes });
  }


  @Override
  public ServerExpression xmlElement(String name, ServerExpression attributes, ServerExpression... content) {
    return xmlElement((name == null) ? (ServerExpression) null : xs.QName(name), attributes, content);
  }


  @Override
  public ServerExpression xmlElement(ServerExpression name, ServerExpression attributes, ServerExpression... content) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlElement() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("op", "xml-element", new Object[]{ name, attributes, new BaseTypeImpl.XmlContentNodeSeqListImpl(content) });
  }


  @Override
  public ServerExpression xmlPi(String name, String value) {
    return xmlPi((name == null) ? (ServerExpression) null : xs.string(name), (value == null) ? (ServerExpression) null : xs.string(value));
  }


  @Override
  public ServerExpression xmlPi(ServerExpression name, ServerExpression value) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for xmlPi() cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlPi() cannot be null");
    }
    return new BaseTypeImpl.ProcessingInstructionNodeCallImpl("op", "xml-pi", new Object[]{ name, value });
  }


  @Override
  public ServerExpression xmlText(String value) {
    return xmlText((value == null) ? (ServerExpression) null : xs.string(value));
  }


  @Override
  public ServerExpression xmlText(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for xmlText() cannot be null");
    }
    return new BaseTypeImpl.TextNodeCallImpl("op", "xml-text", new Object[]{ value });
  }


  @Override
  public ServerExpression xpath(String column, String path) {
    return xpath((column == null) ? (PlanColumn) null : col(column), (path == null) ? (ServerExpression) null : xs.string(path));
  }


  @Override
  public ServerExpression xpath(PlanColumn column, ServerExpression path) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for xpath() cannot be null");
    }
    if (path == null) {
      throw new IllegalArgumentException("path parameter for xpath() cannot be null");
    }
    return new BaseTypeImpl.NodeSeqCallImpl("op", "xpath", new Object[]{ column, path });
  }


  @Override
  public ServerExpression xpath(String column, String path, PlanNamespaceBindingsSeq namespaceBindings) {
    return xpath((column == null) ? (PlanColumn) null : col(column), (path == null) ? (ServerExpression) null : xs.string(path), namespaceBindings);
  }


  @Override
  public ServerExpression xpath(PlanColumn column, ServerExpression path, PlanNamespaceBindingsSeq namespaceBindings) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for xpath() cannot be null");
    }
    if (path == null) {
      throw new IllegalArgumentException("path parameter for xpath() cannot be null");
    }
    return new BaseTypeImpl.NodeSeqCallImpl("op", "xpath", new Object[]{ column, path, namespaceBindings });
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


  static class DocColsIdentifierSeqListImpl extends PlanSeqListImpl implements PlanDocColsIdentifierSeq {
    DocColsIdentifierSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class DocColsIdentifierSeqCallImpl extends PlanCallImpl implements PlanDocColsIdentifierSeq {
    DocColsIdentifierSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class DocColsIdentifierCallImpl extends PlanCallImpl implements PlanDocColsIdentifier {
    DocColsIdentifierCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class DocDescriptorSeqListImpl extends PlanSeqListImpl implements PlanDocDescriptorSeq {
    DocDescriptorSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class DocDescriptorSeqCallImpl extends PlanCallImpl implements PlanDocDescriptorSeq {
    DocDescriptorSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class DocDescriptorCallImpl extends PlanCallImpl implements PlanDocDescriptor {
    DocDescriptorCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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


  static class GroupSeqListImpl extends PlanSeqListImpl implements PlanGroupSeq {
    GroupSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class GroupSeqCallImpl extends PlanCallImpl implements PlanGroupSeq {
    GroupSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class GroupCallImpl extends PlanCallImpl implements PlanGroup {
    GroupCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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


  static class NamedGroupSeqListImpl extends PlanSeqListImpl implements PlanNamedGroupSeq {
    NamedGroupSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class NamedGroupSeqCallImpl extends PlanCallImpl implements PlanNamedGroupSeq {
    NamedGroupSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class NamedGroupCallImpl extends PlanCallImpl implements PlanNamedGroup {
    NamedGroupCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class NamespaceBindingsSeqListImpl extends PlanSeqListImpl implements PlanNamespaceBindingsSeq {
    NamespaceBindingsSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class NamespaceBindingsSeqCallImpl extends PlanCallImpl implements PlanNamespaceBindingsSeq {
    NamespaceBindingsSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class NamespaceBindingsCallImpl extends PlanCallImpl implements PlanNamespaceBindings {
    NamespaceBindingsCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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


  static class RowColTypesSeqListImpl extends PlanSeqListImpl implements PlanRowColTypesSeq {
    RowColTypesSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class RowColTypesSeqCallImpl extends PlanCallImpl implements PlanRowColTypesSeq {
    RowColTypesSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class RowColTypesCallImpl extends PlanCallImpl implements PlanRowColTypes {
    RowColTypesCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class SchemaDefSeqListImpl extends PlanSeqListImpl implements PlanSchemaDefSeq {
    SchemaDefSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class SchemaDefSeqCallImpl extends PlanCallImpl implements PlanSchemaDefSeq {
    SchemaDefSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class SchemaDefCallImpl extends PlanCallImpl implements PlanSchemaDef {
    SchemaDefCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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


  static class TransformDefSeqListImpl extends PlanSeqListImpl implements PlanTransformDefSeq {
    TransformDefSeqListImpl(Object[] items) {
      super(items);
    }
  }


  static class TransformDefSeqCallImpl extends PlanCallImpl implements PlanTransformDefSeq {
    TransformDefSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }


  static class TransformDefCallImpl extends PlanCallImpl implements PlanTransformDef {
    TransformDefCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
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


  @Override
  public ModifyPlan sampleBy() {
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "sample-by", new Object[]{  });
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
  public ModifyPlan bind(PlanExprColSeq columns) {
    if (columns == null) {
      throw new IllegalArgumentException("columns parameter for bind() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "bind", new Object[]{ columns });
  }


  @Override
  public ModifyPlan bindAs(String column, ServerExpression expression) {
    return bindAs((column == null) ? (PlanColumn) null : col(column), expression);
  }


  @Override
  public ModifyPlan bindAs(PlanColumn column, ServerExpression expression) {
    if (column == null) {
      throw new IllegalArgumentException("column parameter for bindAs() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "bind-as", new Object[]{ column, expression });
  }


  @Override
  public ModifyPlan except(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for except() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "except", new Object[]{ right });
  }


  @Override
  public ModifyPlan existsJoin(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for existsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "exists-join", new Object[]{ right });
  }


  @Override
  public ModifyPlan existsJoin(ModifyPlan right, PlanJoinKey... keys) {
    return existsJoin(right, new JoinKeySeqListImpl(keys));
  }


  @Override
  public ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for existsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "exists-join", new Object[]{ right, keys });
  }


  @Override
  public ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
    return existsJoin(right, keys, xs.booleanVal(condition));
  }


  @Override
  public ModifyPlan existsJoin(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for existsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "exists-join", new Object[]{ right, keys, condition });
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
  public ModifyPlan joinCrossProduct(ModifyPlan right, ServerExpression condition) {
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
  public ModifyPlan joinDocAndUri(String docCol, String uriCol, String sourceCol) {
    return joinDocAndUri((docCol == null) ? (PlanColumn) null : col(docCol), (uriCol == null) ? (PlanColumn) null : col(uriCol), (sourceCol == null) ? (PlanColumn) null : col(sourceCol));
  }


  @Override
  public ModifyPlan joinDocAndUri(PlanColumn docCol, PlanColumn uriCol, PlanColumn sourceCol) {
    if (docCol == null) {
      throw new IllegalArgumentException("docCol parameter for joinDocAndUri() cannot be null");
    }
    if (uriCol == null) {
      throw new IllegalArgumentException("uriCol parameter for joinDocAndUri() cannot be null");
    }
    if (sourceCol == null) {
      throw new IllegalArgumentException("sourceCol parameter for joinDocAndUri() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc-and-uri", new Object[]{ docCol, uriCol, sourceCol });
  }


  @Override
  public ModifyPlan joinDocCols(PlanDocColsIdentifier cols, String docIdCol) {
    return joinDocCols(cols, (docIdCol == null) ? (PlanColumn) null : col(docIdCol));
  }


  @Override
  public ModifyPlan joinDocCols(PlanDocColsIdentifier cols, PlanColumn docIdCol) {
    if (docIdCol == null) {
      throw new IllegalArgumentException("docIdCol parameter for joinDocCols() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-doc-cols", new Object[]{ cols, docIdCol });
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
  public ModifyPlan joinFullOuter(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinFullOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-full-outer", new Object[]{ right });
  }


  @Override
  public ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKey... keys) {
    return joinFullOuter(right, new JoinKeySeqListImpl(keys));
  }


  @Override
  public ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinFullOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-full-outer", new Object[]{ right, keys });
  }


  @Override
  public ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
    return joinFullOuter(right, keys, xs.booleanVal(condition));
  }


  @Override
  public ModifyPlan joinFullOuter(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinFullOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-full-outer", new Object[]{ right, keys, condition });
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
  public ModifyPlan joinInner(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition) {
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
  public ModifyPlan joinLeftOuter(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for joinLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "join-left-outer", new Object[]{ right, keys, condition });
  }


  @Override
  public ModifyPlan notExistsJoin(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for notExistsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "not-exists-join", new Object[]{ right });
  }


  @Override
  public ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKey... keys) {
    return notExistsJoin(right, new JoinKeySeqListImpl(keys));
  }


  @Override
  public ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for notExistsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "not-exists-join", new Object[]{ right, keys });
  }


  @Override
  public ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys, boolean condition) {
    return notExistsJoin(right, keys, xs.booleanVal(condition));
  }


  @Override
  public ModifyPlan notExistsJoin(ModifyPlan right, PlanJoinKeySeq keys, ServerExpression condition) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for notExistsJoin() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "not-exists-join", new Object[]{ right, keys, condition });
  }


  @Override
  public ModifyPlan onError(String action) {
    return onError((action == null) ? (XsStringVal) null : xs.string(action));
  }


  @Override
  public ModifyPlan onError(XsStringVal action) {
    if (action == null) {
      throw new IllegalArgumentException("action parameter for onError() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "on-error", new Object[]{ action });
  }


  @Override
  public ModifyPlan onError(String action, String errorColumn) {
    return onError((action == null) ? (XsStringVal) null : xs.string(action), (errorColumn == null) ? (PlanExprCol) null : exprCol(errorColumn));
  }


  @Override
  public ModifyPlan onError(XsStringVal action, PlanExprCol errorColumn) {
    if (action == null) {
      throw new IllegalArgumentException("action parameter for onError() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "on-error", new Object[]{ action, errorColumn });
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
  public ModifyPlan shortestPath(String start, String end) {
    return shortestPath((start == null) ? (PlanExprCol) null : exprCol(start), (end == null) ? (PlanExprCol) null : exprCol(end));
  }


  @Override
  public ModifyPlan shortestPath(PlanExprCol start, PlanExprCol end) {
    if (start == null) {
      throw new IllegalArgumentException("start parameter for shortestPath() cannot be null");
    }
    if (end == null) {
      throw new IllegalArgumentException("end parameter for shortestPath() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "shortest-path", new Object[]{ start, end });
  }


  @Override
  public ModifyPlan shortestPath(String start, String end, String path) {
    return shortestPath((start == null) ? (PlanExprCol) null : exprCol(start), (end == null) ? (PlanExprCol) null : exprCol(end), (path == null) ? (PlanExprCol) null : exprCol(path));
  }


  @Override
  public ModifyPlan shortestPath(PlanExprCol start, PlanExprCol end, PlanExprCol path) {
    if (start == null) {
      throw new IllegalArgumentException("start parameter for shortestPath() cannot be null");
    }
    if (end == null) {
      throw new IllegalArgumentException("end parameter for shortestPath() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "shortest-path", new Object[]{ start, end, path });
  }


  @Override
  public ModifyPlan shortestPath(String start, String end, String path, String length) {
    return shortestPath((start == null) ? (PlanExprCol) null : exprCol(start), (end == null) ? (PlanExprCol) null : exprCol(end), (path == null) ? (PlanExprCol) null : exprCol(path), (length == null) ? (PlanExprCol) null : exprCol(length));
  }

  @Override
  public ModifyPlan shortestPath(PlanExprCol start, PlanExprCol end, PlanExprCol path, PlanExprCol length) {
    if (start == null) {
      throw new IllegalArgumentException("start parameter for shortestPath() cannot be null");
    }
    if (end == null) {
      throw new IllegalArgumentException("end parameter for shortestPath() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "shortest-path", new Object[]{ start, end, path, length });
  }

  @Override
  public ModifyPlan union(ModifyPlan right) {
    if (right == null) {
      throw new IllegalArgumentException("right parameter for union() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "union", new Object[]{ right });
  }


  @Override
  public ModifyPlan unnestInner(String inputColumn, String valueColumn) {
    return unnestInner((inputColumn == null) ? (PlanExprCol) null : exprCol(inputColumn), (valueColumn == null) ? (PlanExprCol) null : exprCol(valueColumn));
  }


  @Override
  public ModifyPlan unnestInner(PlanExprCol inputColumn, PlanExprCol valueColumn) {
    if (inputColumn == null) {
      throw new IllegalArgumentException("inputColumn parameter for unnestInner() cannot be null");
    }
    if (valueColumn == null) {
      throw new IllegalArgumentException("valueColumn parameter for unnestInner() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "unnest-inner", new Object[]{ inputColumn, valueColumn });
  }


  @Override
  public ModifyPlan unnestInner(String inputColumn, String valueColumn, String ordinalColumn) {
    return unnestInner((inputColumn == null) ? (PlanExprCol) null : exprCol(inputColumn), (valueColumn == null) ? (PlanExprCol) null : exprCol(valueColumn), (ordinalColumn == null) ? (PlanExprCol) null : exprCol(ordinalColumn));
  }


  @Override
  public ModifyPlan unnestInner(PlanExprCol inputColumn, PlanExprCol valueColumn, PlanExprCol ordinalColumn) {
    if (inputColumn == null) {
      throw new IllegalArgumentException("inputColumn parameter for unnestInner() cannot be null");
    }
    if (valueColumn == null) {
      throw new IllegalArgumentException("valueColumn parameter for unnestInner() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "unnest-inner", new Object[]{ inputColumn, valueColumn, ordinalColumn });
  }


  @Override
  public ModifyPlan unnestLeftOuter(String inputColumn, String valueColumn) {
    return unnestLeftOuter((inputColumn == null) ? (PlanExprCol) null : exprCol(inputColumn), (valueColumn == null) ? (PlanExprCol) null : exprCol(valueColumn));
  }


  @Override
  public ModifyPlan unnestLeftOuter(PlanExprCol inputColumn, PlanExprCol valueColumn) {
    if (inputColumn == null) {
      throw new IllegalArgumentException("inputColumn parameter for unnestLeftOuter() cannot be null");
    }
    if (valueColumn == null) {
      throw new IllegalArgumentException("valueColumn parameter for unnestLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "unnest-left-outer", new Object[]{ inputColumn, valueColumn });
  }


  @Override
  public ModifyPlan unnestLeftOuter(String inputColumn, String valueColumn, String ordinalColumn) {
    return unnestLeftOuter((inputColumn == null) ? (PlanExprCol) null : exprCol(inputColumn), (valueColumn == null) ? (PlanExprCol) null : exprCol(valueColumn), (ordinalColumn == null) ? (PlanExprCol) null : exprCol(ordinalColumn));
  }


  @Override
  public ModifyPlan unnestLeftOuter(PlanExprCol inputColumn, PlanExprCol valueColumn, PlanExprCol ordinalColumn) {
    if (inputColumn == null) {
      throw new IllegalArgumentException("inputColumn parameter for unnestLeftOuter() cannot be null");
    }
    if (valueColumn == null) {
      throw new IllegalArgumentException("valueColumn parameter for unnestLeftOuter() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "unnest-left-outer", new Object[]{ inputColumn, valueColumn, ordinalColumn });
  }


  @Override
  public ModifyPlan validateDoc(String validateDocCol, PlanSchemaDef schemaDef) {
    return validateDoc((validateDocCol == null) ? (PlanColumn) null : col(validateDocCol), schemaDef);
  }


  @Override
  public ModifyPlan validateDoc(PlanColumn validateDocCol, PlanSchemaDef schemaDef) {
    if (validateDocCol == null) {
      throw new IllegalArgumentException("validateDocCol parameter for validateDoc() cannot be null");
    }
    if (schemaDef == null) {
      throw new IllegalArgumentException("schemaDef parameter for validateDoc() cannot be null");
    }
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "validate-doc", new Object[]{ validateDocCol, schemaDef });
  }


  @Override
  public ModifyPlan whereDistinct() {
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "where-distinct", new Object[]{  });
  }


  @Override
  public ModifyPlan write() {
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "write", new Object[]{  });
  }


  @Override
  public ModifyPlan write(PlanDocColsIdentifier docCols) {
    return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "write", new Object[]{ docCols });
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
