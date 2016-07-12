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
package com.marklogic.client.impl;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.PlanBuilder;

import com.marklogic.client.expression.Cts;
import com.marklogic.client.impl.CtsExprImpl; 
import com.marklogic.client.expression.Fn;
import com.marklogic.client.impl.FnExprImpl; 
import com.marklogic.client.expression.Json;
import com.marklogic.client.impl.JsonExprImpl; 
import com.marklogic.client.expression.Map;
import com.marklogic.client.impl.MapExprImpl; 
import com.marklogic.client.expression.Math;
import com.marklogic.client.impl.MathExprImpl; 
import com.marklogic.client.expression.Rdf;
import com.marklogic.client.impl.RdfExprImpl; 
import com.marklogic.client.expression.Sem;
import com.marklogic.client.impl.SemExprImpl; 
import com.marklogic.client.expression.Sql;
import com.marklogic.client.impl.SqlExprImpl; 
import com.marklogic.client.expression.Xdmp;
import com.marklogic.client.impl.XdmpExprImpl; 
import com.marklogic.client.expression.Xs;
import com.marklogic.client.impl.XsExprImpl; import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;


// IMPORTANT: Do not edit. This file is generated. 
public class PlanBuilderImpl extends PlanBuilderBase {
    public PlanBuilderImpl(
        Cts cts, Fn fn, Json json, Map map, Math math, Rdf rdf, Sem sem, Sql sql, Xdmp xdmp, Xs xs
        ) {
        super(
            cts, fn, json, map, math, rdf, sem, sql, xdmp, xs
            );
    }

    @Override
        public Xs.AnyAtomicTypeExpr add(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("op", "add", new Object[]{ left, right });
    }
    @Override
        public AggregateColSeq aggregates(AggregateCol... aggregate) {
        return new AggregateColSeqListImpl(aggregate);
    }
    @Override
        public Xs.BooleanExpr and(Xs.BooleanExpr... list) {
        return and(list); 
    }
    @Override
        public Xs.BooleanExpr and(Xs.BooleanSeqExpr list) {
        return new XsExprImpl.BooleanCallImpl("op", "and", new Object[]{ list });
    }
    @Override
        public AggregateCol arrayAggregate(String name, String column) {
        return arrayAggregate(col(name), col(column)); 
    }
    @Override
        public AggregateCol arrayAggregate(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column });
    }
    @Override
        public ExprCol as(String column, BaseType.ItemExpr expression) {
        return as(col(column), expression); 
    }
    @Override
        public ExprCol as(Column column, BaseType.ItemExpr expression) {
        return new ExprColCallImpl("op", "as", new Object[]{ column, expression });
    }
    @Override
        public SortKey asc(String column) {
        return asc(col(column)); 
    }
    @Override
        public SortKey asc(ExprCol column) {
        return new SortKeyCallImpl("op", "asc", new Object[]{ column });
    }
    @Override
        public AggregateCol avg(String name, String column) {
        return avg(col(name), col(column)); 
    }
    @Override
        public AggregateCol avg(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "avg", new Object[]{ name, column });
    }
    @Override
        public Column col(String column) {
        return col(xs.string(column)); 
    }
    @Override
        public Column col(Xs.StringParam column) {
        return new ColumnCallImpl("op", "col", new Object[]{ column });
    }
    @Override
        public ExprColSeq cols(String... col) {
        return cols((ExprCol[]) Arrays.stream(col)
            .map(item -> col(item))
            .toArray(size -> new ExprCol[size])); 
    }
    @Override
        public ExprColSeq cols(ExprCol... col) {
        return new ExprColSeqListImpl(col);
    }
    @Override
        public AggregateCol count(String name, String column) {
        return count(col(name), col(column)); 
    }
    @Override
        public AggregateCol count(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "count", new Object[]{ name, column });
    }
    @Override
        public SortKey desc(String column) {
        return desc(col(column)); 
    }
    @Override
        public SortKey desc(ExprCol column) {
        return new SortKeyCallImpl("op", "desc", new Object[]{ column });
    }
    @Override
        public Xs.AnyAtomicTypeExpr divide(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("op", "divide", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr eq(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "eq", new Object[]{ left, right });
    }
    @Override
        public ViewPlan fromView(String schema, String view) {
        return fromView(xs.string(schema), xs.string(view)); 
    }
    @Override
        public ViewPlan fromView(Xs.StringParam schema, Xs.StringParam view) {
        return new ViewPlanCallImpl("op", "from-view", new Object[]{ schema, view });
    }
    @Override
        public ViewPlan fromView(String schema, String view, String qualifierName) {
        return fromView(xs.string(schema), xs.string(view), (qualifierName == null) ? null : xs.string(qualifierName)); 
    }
    @Override
        public ViewPlan fromView(Xs.StringParam schema, Xs.StringParam view, Xs.StringParam qualifierName) {
        return new ViewPlanCallImpl("op", "from-view", new Object[]{ schema, view, qualifierName });
    }
    @Override
        public Xs.BooleanExpr ge(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "ge", new Object[]{ left, right });
    }
    @Override
        public AggregateCol groupConcat(String name, String column) {
        return groupConcat(col(name), col(column)); 
    }
    @Override
        public AggregateCol groupConcat(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "group-concat", new Object[]{ name, column });
    }
    @Override
        public Xs.BooleanExpr gt(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "gt", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr isDefined(BaseType.ItemExpr expression) {
        return new XsExprImpl.BooleanCallImpl("op", "is-defined", new Object[]{ expression });
    }
    @Override
        public JoinKeySeq joinKeys(JoinKey... key) {
        return new JoinKeySeqListImpl(key);
    }
    @Override
        public Xs.BooleanExpr le(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "le", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr lt(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "lt", new Object[]{ left, right });
    }
    @Override
        public AggregateCol max(String name, String column) {
        return max(col(name), col(column)); 
    }
    @Override
        public AggregateCol max(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "max", new Object[]{ name, column });
    }
    @Override
        public AggregateCol min(String name, String column) {
        return min(col(name), col(column)); 
    }
    @Override
        public AggregateCol min(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "min", new Object[]{ name, column });
    }
    @Override
        public Xs.AnyAtomicTypeExpr modulo(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("op", "modulo", new Object[]{ left, right });
    }
    @Override
        public Xs.AnyAtomicTypeExpr multiply(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("op", "multiply", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr ne(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.BooleanCallImpl("op", "ne", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr not(Xs.BooleanExpr condition) {
        return new XsExprImpl.BooleanCallImpl("op", "not", new Object[]{ condition });
    }
    @Override
        public JoinKey on(String left, String right) {
        return on(col(left), col(right)); 
    }
    @Override
        public JoinKey on(ExprCol left, ExprCol right) {
        return new JoinKeyCallImpl("op", "on", new Object[]{ left, right });
    }
    @Override
        public Xs.BooleanExpr or(Xs.BooleanExpr... list) {
        return or(list); 
    }
    @Override
        public Xs.BooleanExpr or(Xs.BooleanSeqExpr list) {
        return new XsExprImpl.BooleanCallImpl("op", "or", new Object[]{ list });
    }
    @Override
        public AggregateCol sample(String name, String column) {
        return sample(col(name), col(column)); 
    }
    @Override
        public AggregateCol sample(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "sample", new Object[]{ name, column });
    }
    @Override
        public Column schemaCol(String schema, String view, String column) {
        return schemaCol(xs.string(schema), xs.string(view), xs.string(column)); 
    }
    @Override
        public Column schemaCol(Xs.StringParam schema, Xs.StringParam view, Xs.StringParam column) {
        return new ColumnCallImpl("op", "schema-col", new Object[]{ schema, view, column });
    }
    @Override
        public AggregateCol sequenceAggregate(String name, String column) {
        return sequenceAggregate(col(name), col(column)); 
    }
    @Override
        public AggregateCol sequenceAggregate(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column });
    }
    @Override
        public SortKeySeq sortKeys(String... key) {
        return sortKeys((SortKey[]) Arrays.stream(key)
            .map(item -> col(item))
            .toArray(size -> new SortKey[size])); 
    }
    @Override
        public SortKeySeq sortKeys(SortKey... key) {
        return new SortKeySeqListImpl(key);
    }
    @Override
        public Xs.AnyAtomicTypeExpr subtract(Xs.AnyAtomicTypeExpr left, Xs.AnyAtomicTypeExpr right) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("op", "subtract", new Object[]{ left, right });
    }
    @Override
        public AggregateCol sum(String name, String column) {
        return sum(col(name), col(column)); 
    }
    @Override
        public AggregateCol sum(ExprCol name, ExprCol column) {
        return new AggregateColCallImpl("op", "sum", new Object[]{ name, column });
    }
    @Override
        public AggregateCol uda(String name, String column, String module, String function) {
        return uda(col(name), col(column), xs.string(module), xs.string(function)); 
    }
    @Override
        public AggregateCol uda(ExprCol name, ExprCol column, Xs.StringParam module, Xs.StringParam function) {
        return new AggregateColCallImpl("op", "uda", new Object[]{ name, column, module, function });
    }
    @Override
        public Column viewCol(String view, String column) {
        return viewCol(xs.string(view), xs.string(column)); 
    }
    @Override
        public Column viewCol(Xs.StringParam view, Xs.StringParam column) {
        return new ColumnCallImpl("op", "view-col", new Object[]{ view, column });
    } 
    @Override
    public AccessPlan fromLiterals(@SuppressWarnings("unchecked") java.util.Map<String,Object>... rows) {
        return new AccessPlanCallImpl(null, "op", "from-literals", new Object[]{literal(rows)});
    }
    @Override
    public AccessPlan fromLiterals(java.util.Map<String,Object>[] rows, String qualifierName) {
        return new AccessPlanCallImpl(null, "op", "from-literals", new Object[]{literal(rows), xs.string(qualifierName)});
    }
 public class PlanCallImpl  extends PlanBase  implements PlanBuilder.Plan {
        PlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class PreparePlanCallImpl  extends ExportablePlanCallImpl  implements PlanBuilder.PreparePlan {
        PreparePlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }
     @Override
        public PlanFunction installedFunction(String modulePath, String functionName) {
        return installedFunction(xs.string(modulePath), xs.string(functionName)); 
    }
    @Override
        public PlanFunction installedFunction(Xs.StringParam modulePath, Xs.StringParam functionName) {
        return new PlanFunctionCallImpl("op", "installed-function", new Object[]{ modulePath, functionName });
    }
    @Override
        public ExportablePlan map(PlanFunction func) {
        return new ExportablePlanCallImpl(this, "op", "map", new Object[]{ func });
    }
    @Override
        public PlanFunction mapFunction(String moduleName) {
        return mapFunction(xs.string(moduleName)); 
    }
    @Override
        public PlanFunction mapFunction(Xs.StringParam moduleName) {
        return new PlanFunctionCallImpl("op", "map-function", new Object[]{ moduleName });
    }
    @Override
        public ExportablePlan reduce(PlanFunction func) {
        return new ExportablePlanCallImpl(this, "op", "reduce", new Object[]{ func });
    }
    @Override
        public ExportablePlan reduce(PlanFunction func, Xs.AnyAtomicTypeParam seed) {
        return new ExportablePlanCallImpl(this, "op", "reduce", new Object[]{ func, seed });
    }
    @Override
        public PlanFunction reduceFunction(String moduleName) {
        return reduceFunction(xs.string(moduleName)); 
    }
    @Override
        public PlanFunction reduceFunction(Xs.StringParam moduleName) {
        return new PlanFunctionCallImpl("op", "reduce-function", new Object[]{ moduleName });
    }
}
 public class PlanParamCallImpl  extends PlanBaseImpl  implements PlanBuilder.PlanParam {
        PlanParamCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class ViewPlanCallImpl  extends AccessPlanCallImpl  implements PlanBuilder.ViewPlan {
        Xs.StringParam view = null;
         Xs.StringParam schema = null;
         ViewPlanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(null, fnPrefix, fnName, fnArgs);
         schema = (Xs.StringParam) fnArgs[0];
         view = (Xs.StringParam) fnArgs[1];
         }
     @Override
        public Column col(String column) {
        return col(xs.string(column)); 
    }
    @Override
        public Column col(Xs.StringParam column) {
        return schemaCol(this.schema, this.view, column);
    }
}
 public class ExportablePlanCallImpl  extends PlanCallImpl  implements PlanBuilder.ExportablePlan {
        ExportablePlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class ColumnCallImpl  extends ExprColCallImpl  implements PlanBuilder.Column {
        ColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class AggregateColCallImpl  extends PlanBaseImpl  implements PlanBuilder.AggregateCol {
        AggregateColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class ExprColCallImpl  extends AggregateColCallImpl  implements PlanBuilder.ExprCol {
        ExprColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class AccessPlanCallImpl  extends ModifyPlanCallImpl  implements PlanBuilder.AccessPlan {
        AccessPlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }

}
 public class SortKeyCallImpl  extends PlanBaseImpl  implements PlanBuilder.SortKey {
        SortKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
         }

}
 public class ModifyPlanCallImpl  extends PreparePlanCallImpl  implements PlanBuilder.ModifyPlan {
        ModifyPlanCallImpl(PlanChainedImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
            super(prior, fnPrefix, fnName, fnArgs);
         }
     @Override
        public ModifyPlan groupBy(ExprCol... keys) {
        return groupBy(new ExprColSeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan groupBy(ExprColSeq keys) {
        return new ModifyPlanCallImpl(this, "op", "group-by", new Object[]{ keys });
    }
    @Override
        public ModifyPlan groupBy(ExprColSeq keys, AggregateCol... aggregates) {
        return groupBy(keys, new AggregateColSeqListImpl(aggregates)); 
    }
    @Override
        public ModifyPlan groupBy(ExprColSeq keys, AggregateColSeq aggregates) {
        return new ModifyPlanCallImpl(this, "op", "group-by", new Object[]{ keys, aggregates });
    }
    @Override
        public ModifyPlan joinCrossProduct(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "join-cross-product", new Object[]{ right });
    }
    @Override
        public ModifyPlan joinCrossProduct(ModifyPlan right, Xs.BooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-cross-product", new Object[]{ right, condition });
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, JoinKey... keys) {
        return joinInner(right, new JoinKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, JoinKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "join-inner", new Object[]{ right, keys });
    }
    @Override
        public ModifyPlan joinInner(ModifyPlan right, JoinKeySeq keys, Xs.BooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-inner", new Object[]{ right, keys, condition });
    }
    @Override
        public ModifyPlan joinInnerDoc(String docCol, String uriCol) {
        return joinInnerDoc(col(docCol), col(uriCol)); 
    }
    @Override
        public ModifyPlan joinInnerDoc(Column docCol, ExprCol uriCol) {
        return new ModifyPlanCallImpl(this, "op", "join-inner-doc", new Object[]{ docCol, uriCol });
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKey... keys) {
        return joinLeftOuter(right, new JoinKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "join-left-outer", new Object[]{ right, keys });
    }
    @Override
        public ModifyPlan joinLeftOuter(ModifyPlan right, JoinKeySeq keys, Xs.BooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "join-left-outer", new Object[]{ right, keys, condition });
    }
    @Override
        public ModifyPlan joinLeftOuterDoc(String docCol, String uriCol) {
        return joinLeftOuterDoc(col(docCol), col(uriCol)); 
    }
    @Override
        public ModifyPlan joinLeftOuterDoc(Column docCol, ExprCol uriCol) {
        return new ModifyPlanCallImpl(this, "op", "join-left-outer-doc", new Object[]{ docCol, uriCol });
    }
    @Override
        public ModifyPlan limit(long length) {
        return limit(xs.longVal(length)); 
    }
    @Override
        public ModifyPlan limit(Xs.LongParam length) {
        return new ModifyPlanCallImpl(this, "op", "limit", new Object[]{ length });
    }
    @Override
        public ModifyPlan offset(long start) {
        return offset(xs.longVal(start)); 
    }
    @Override
        public ModifyPlan offset(Xs.LongParam start) {
        return new ModifyPlanCallImpl(this, "op", "offset", new Object[]{ start });
    }
    @Override
        public ModifyPlan offsetLimit(long start, long length) {
        return offsetLimit(xs.longVal(start), xs.longVal(length)); 
    }
    @Override
        public ModifyPlan offsetLimit(Xs.LongParam start, Xs.LongParam length) {
        return new ModifyPlanCallImpl(this, "op", "offset-limit", new Object[]{ start, length });
    }
    @Override
        public ModifyPlan orderBy(SortKey... keys) {
        return orderBy(new SortKeySeqListImpl(keys)); 
    }
    @Override
        public ModifyPlan orderBy(SortKeySeq keys) {
        return new ModifyPlanCallImpl(this, "op", "order-by", new Object[]{ keys });
    }
    @Override
        public PreparePlan prepare(int optimize) {
        return prepare(xs.intVal(optimize)); 
    }
    @Override
        public PreparePlan prepare(Xs.IntParam optimize) {
        return new PreparePlanCallImpl(this, "op", "prepare", new Object[]{ optimize });
    }
    @Override
        public ModifyPlan select(ExprCol... columns) {
        return select(new ExprColSeqListImpl(columns)); 
    }
    @Override
        public ModifyPlan select(ExprColSeq columns) {
        return new ModifyPlanCallImpl(this, "op", "select", new Object[]{ columns });
    }
    @Override
        public ModifyPlan select(ExprColSeq columns, String qualifierName) {
        return select(columns, (qualifierName == null) ? null : xs.string(qualifierName)); 
    }
    @Override
        public ModifyPlan select(ExprColSeq columns, Xs.StringParam qualifierName) {
        return new ModifyPlanCallImpl(this, "op", "select", new Object[]{ columns, qualifierName });
    }
    @Override
        public ModifyPlan union(ModifyPlan right) {
        return new ModifyPlanCallImpl(this, "op", "union", new Object[]{ right });
    }
    @Override
        public ModifyPlan where(Xs.BooleanExpr condition) {
        return new ModifyPlanCallImpl(this, "op", "where", new Object[]{ condition });
    }
    @Override
    public ModifyPlan whereDistinct() {
        return new ModifyPlanCallImpl(this, "op", "whereDistinct", null);
    }
}
 static class AggregateColSeqListImpl extends PlanListImpl implements PlanBuilder.AggregateColSeq {
        AggregateColSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class ExprColSeqListImpl extends PlanListImpl implements PlanBuilder.ExprColSeq {
        ExprColSeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class JoinKeyCallImpl extends PlanBaseImpl implements PlanBuilder.JoinKey {
        JoinKeyCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class JoinKeySeqListImpl extends PlanListImpl implements PlanBuilder.JoinKeySeq {
        JoinKeySeqListImpl(Object[] items) {
            super(items);
        }
    }
 static class PlanFunctionCallImpl extends PlanBaseImpl implements PlanBuilder.PlanFunction {
        PlanFunctionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, fnArgs);
        }
    }
 static class SortKeySeqListImpl extends PlanListImpl implements PlanBuilder.SortKeySeq {
        SortKeySeqListImpl(Object[] items) {
            super(items);
        }
    }

}
