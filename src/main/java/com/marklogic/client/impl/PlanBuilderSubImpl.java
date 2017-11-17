/*
 * Copyright 2017 MarkLogic Corporation
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.AttributeNodeExpr;
import com.marklogic.client.type.AttributeNodeSeqExpr;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.JsonContentNodeExpr;
import com.marklogic.client.type.ObjectNodeExpr;
import com.marklogic.client.type.PlanAggregateCol;
import com.marklogic.client.type.PlanCase;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.PlanFunction;
import com.marklogic.client.type.PlanGroupConcatOptionSeq;
import com.marklogic.client.type.PlanJsonProperty;
import com.marklogic.client.type.PlanParamBindingVal;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanPrefixer;
import com.marklogic.client.type.PlanSystemColumn;
import com.marklogic.client.type.PlanTripleOption;
import com.marklogic.client.type.PlanTriplePatternSeq;
import com.marklogic.client.type.PlanValueOption;
import com.marklogic.client.type.SemIriVal;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.XmlContentNodeExpr;
import com.marklogic.client.type.XmlContentNodeSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

public class PlanBuilderSubImpl extends PlanBuilderImpl {
  public PlanBuilderSubImpl() {
    super();
  }

  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, String qualifierName, String graphIris, PlanTripleOption option) {
    return fromTriples(patterns,
      (qualifierName == null) ? null : xs.string(qualifierName),
      (graphIris == null)     ? null : xs.string(graphIris),
      option);
  }

  // TODO: delete override of fromTriples() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns) {
    return new AccessPlanSubImpl(this, "op", "from-triples", new Object[]{ patterns });
  }
  // TODO: delete override of fromTriples() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName) {
    return new AccessPlanSubImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName });
  }
  // TODO: delete override of fromTriples() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris) {
    return new AccessPlanSubImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName, graphIris });
  }

  @Override
  public AccessPlan fromTriples(PlanTriplePatternSeq patterns, XsStringVal qualifierName, XsStringSeqVal graphIris, PlanTripleOption option) {
    return new AccessPlanSubImpl(this, "op", "from-triples", new Object[]{ patterns, qualifierName, graphIris, asArg(makeMap(option))});
  }

  @Override
  public AccessPlan fromLexicons(Map<String, CtsReferenceExpr> indexes) {
    return new AccessPlanSubImpl(this, "op", "from-lexicons", new Object[]{ literal(indexes) });
  }

  @Override
  public AccessPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, String qualifierName) {
    return fromLexicons(indexes, (qualifierName == null) ? null : xs.string(qualifierName));
  }

  @Override
  public AccessPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, XsStringVal qualifierName) {
    return new AccessPlanSubImpl(this, "op", "from-lexicons", new Object[]{ literal(indexes), qualifierName});
  }

  @Override
  public AccessPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, String qualifierName, PlanSystemColumn sysCols) {
    return fromLexicons(indexes, (qualifierName == null) ? null : xs.string(qualifierName), sysCols);
  }

  @Override
  public AccessPlan fromLexicons(Map<String, CtsReferenceExpr> indexes, XsStringVal qualifierName, PlanSystemColumn sysCols) {
    return new AccessPlanSubImpl(this, "op", "from-lexicons", new Object[]{ literal(indexes), qualifierName, sysCols});
  }

  @Override
  public AccessPlan fromLiterals(@SuppressWarnings("unchecked") Map<String, Object>... rows) {
    return new AccessPlanSubImpl(this, "op", "from-literals", new Object[]{ literal(rows) });
  }

  @Override
  public AccessPlan fromLiterals(Map<String, Object>[] rows, String qualifierName) {
    return fromLiterals(rows, (qualifierName == null) ? null : xs.string(qualifierName));
  }

  @Override
  public AccessPlan fromLiterals(Map<String, Object>[] rows, XsStringVal qualifierName) {
    return new AccessPlanSubImpl(this, "op", "from-literals", new Object[]{ literal(rows), qualifierName});
  }

  // TODO: delete override of fromView() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view) {
    return new AccessPlanSubImpl(this, "op", "from-view", new Object[]{ schema, view });
  }
  // TODO: delete override of fromView() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName) {
    return new AccessPlanSubImpl(this, "op", "from-view", new Object[]{ schema, view, qualifierName });
  }
  // TODO: delete override of fromView() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromView(XsStringVal schema, XsStringVal view, XsStringVal qualifierName, PlanSystemColumn sysCols) {
    return new AccessPlanSubImpl(this, "op", "from-view", new Object[]{ schema, view, qualifierName, sysCols });
  }

  @Override
  public PlanAggregateCol avg(String name, String column, PlanValueOption option) {
    return avg(col(name), col(column), option);
  }

  @Override
  public PlanAggregateCol avg(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "avg", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol arrayAggregate(String name, String column, PlanValueOption option) {
    return arrayAggregate(col(name), col(column), option);
  }

  @Override
  public PlanAggregateCol arrayAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "array-aggregate", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol count(String name, String column, PlanValueOption option) {
    return count(col(name), exprCol(column), option);
  }

  @Override
  public PlanAggregateCol count(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "count", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol max(String name, String column, PlanValueOption option) {
    return max(col(name), exprCol(column), option);
  }

  @Override
  public PlanAggregateCol max(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "max", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol min(String name, String column, PlanValueOption option) {
    return min(col(name), exprCol(column), option);
  }

  @Override
  public PlanAggregateCol min(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "min", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol sequenceAggregate(String name, String column, PlanValueOption option) {
    return sequenceAggregate(col(name), exprCol(column), option);
  }

  @Override
  public PlanAggregateCol sequenceAggregate(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "sequence-aggregate", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol sum(String name, String column, PlanValueOption option) {
    return sum(col(name), exprCol(column), option);
  }

  @Override
  public PlanAggregateCol sum(PlanColumn name, PlanExprCol column, PlanValueOption option) {
    return new AggregateColCallImpl("op", "sum", new Object[]{ name, column, asArg(makeMap(option)) });
  }

  @Override
  public PlanAggregateCol groupConcat(String name, String column) {
    return groupConcat((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column));
  }
  @Override
  public PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for groupConcat() cannot be null");
    }
    if (column == null) {
      throw new IllegalArgumentException("column parameter for groupConcat() cannot be null");
    }
    return new AggregateColCallImpl("op", "group-concat", new Object[]{ name, column });
  }
  @Override
  public PlanAggregateCol groupConcat(String name, String column, PlanGroupConcatOptionSeq options) {
    return groupConcat((name == null) ? (PlanColumn) null : col(name), (column == null) ? (PlanExprCol) null : exprCol(column), options);
  }
  @Override
  public PlanAggregateCol groupConcat(PlanColumn name, PlanExprCol column, PlanGroupConcatOptionSeq options) {
    return new AggregateColCallImpl("op", "group-concat", new Object[]{ name, column, asArg(makeMap(options)) });
  }
  @Override
  public PlanGroupConcatOptionSeq groupConcatOptions(String separator) {
    return new PlanGroupConcatOptionSeqImpl(separator, null);
  }
  @Override
  public PlanGroupConcatOptionSeq groupConcatOptions(PlanValueOption option) {
    return new PlanGroupConcatOptionSeqImpl(null, option);
  }
  @Override
  public PlanGroupConcatOptionSeq groupConcatOptions(String separator, PlanValueOption option) {
    return new PlanGroupConcatOptionSeqImpl(separator, option);
  }
  static class PlanGroupConcatOptionSeqImpl implements PlanGroupConcatOptionSeq {
    private String separator;
    private PlanValueOption option;
    PlanGroupConcatOptionSeqImpl(String separator, PlanValueOption option) {
      this.separator = separator;
      this.option    = option;
    }
    PlanValueOption getOption() {
      return option;
    }
    String getSeparator() {
      return separator;
    }
  }

  @Override
  public ItemSeqExpr caseExpr(PlanCase... cases) {
    int lastPos = cases.length - 1;
    if (lastPos < 1) {
      throw new IllegalArgumentException("cannot specify caseExpr() without when() and elseExpr()");
    }

    BaseTypeImpl.BaseArgImpl[] whenList = new BaseTypeImpl.BaseArgImpl[lastPos];
    for (int i=0; i < lastPos; i++) {
      PlanCase currCase = cases[i];
      if (!(currCase instanceof CaseWhenCallImpl)) {
        throw new IllegalArgumentException(
          "caseExpr() can only have when() cases and final elseExpr(): "+currCase.getClass().getName()
        );
      }
      whenList[i] = (CaseWhenCallImpl) currCase;
    }

    PlanCase lastCase = cases[lastPos];
    if (!(lastCase instanceof CaseElseImpl)) {
      throw new IllegalArgumentException(
        "caseExpr() must have a last case of elseExpr(): "+lastCase.getClass().getName()
      );
    }

    return new CaseCallImpl(whenList, ((CaseElseImpl) lastCase).getArg());
  }

  // TODO: delete as(), col(), viewCol(), schemaCol(), fragmentIdCol(), and graphCol()
// after code generation can specify base class
  @Override
  public PlanExprCol as(PlanColumn column, ItemSeqExpr expression) {
    return new ExprColCallImpl("op", "as", new Object[]{ column, expression });
  }
  @Override
  public PlanColumn col(XsStringVal column) {
    return new ColumnCallImpl("op", "col", new Object[]{ column });
  }
  @Override
  public PlanColumn viewCol(XsStringVal view, XsStringVal column) {
    return new ColumnCallImpl("op", "view-col", new Object[]{ view, column });
  }
  @Override
  public PlanColumn schemaCol(XsStringVal schema, XsStringVal view, XsStringVal column) {
    return new ColumnCallImpl("op", "schema-col", new Object[]{ schema, view, column });
  }
  @Override
  public PlanSystemColumn fragmentIdCol(XsStringVal column) {
    return new SystemColumnCallImpl("op", "fragment-id-col", new Object[]{ column });
  }
  @Override
  public PlanSystemColumn graphCol(XsStringVal column) {
    return new SystemColumnCallImpl("op", "graph-col", new Object[]{ column });
  }

  // TODO: move when() and elseExpr() into generated code
  @Override
  public PlanCase when(boolean condition, ItemExpr... value) {
    return when(xs.booleanVal(condition), new BaseTypeImpl.ItemSeqListImpl(value));
  }
  @Override
  public PlanCase when(XsBooleanExpr condition, ItemExpr... value) {
    return when(condition, new BaseTypeImpl.ItemSeqListImpl(value));
  }
  @Override
  public PlanCase when(XsBooleanExpr condition, ItemSeqExpr value) {
    if (!(value instanceof BaseTypeImpl.BaseArgImpl)) {
      throw new IllegalArgumentException("invalid value for when(): "+value.getClass().getName());
    }
    return new CaseWhenCallImpl(new Object[]{condition, value});
  }
  @Override
  public PlanCase elseExpr(ItemExpr value) {
    if (!(value instanceof BaseTypeImpl.BaseArgImpl)) {
      throw new IllegalArgumentException("invalid value for elseExpr(): "+value.getClass().getName());
    }
    return new CaseElseImpl((BaseTypeImpl.BaseArgImpl) value);
  }

  // TODO: move jsonArray() and jsonObject() into generated code
  @Override
  public ObjectNodeExpr jsonObject(PlanJsonProperty... properties) {
    return new JsonObjectCallImpl(new Object[]{ new JsonPropertySeqListImpl(properties) });
  }
  @Override
  public ArrayNodeExpr jsonArray(JsonContentNodeExpr... items) {
    return new JsonArrayCallImpl(new Object[]{ new JsonContentSeqListImpl(items) });
  }

  // TODO: move xmlElement() into generated code
  @Override
  public ElementNodeExpr xmlElement(XsQNameExpr name, AttributeNodeSeqExpr attributes, XmlContentNodeSeqExpr content) {
    return new XmlElementCallImpl(new Object[]{ name, attributes, content });
  }

  @Override
  public AttributeNodeSeqExpr xmlAttributeSeq(AttributeNodeExpr... attributes) {
    return new XmlAttributeSeqListImpl(attributes);
  }

  @Override
  public PlanFunction resolveFunction(XsQNameVal functionName, String modulePath) {
    return resolveFunction(functionName, xs.string(modulePath));
  }

  @Override
  public PlanPrefixer prefixer(XsStringVal base) {
    return prefixer(base.getString());
  }
  @Override
  public PlanPrefixer prefixer(String base) {
    if (base == null || base.length() == 0) {
      throw new IllegalArgumentException("cannot create prefixer with empty string");
    }

    String lastChar = base.substring(base.length() - 1);
    String prefix = ("/".equals(lastChar) || "#".equals(lastChar) || "?".equals(lastChar)) ?
      base : base + "/";

    return new PrefixerImpl(sem, prefix);
  }

  @Override
  public PlanParamExpr param(String name) {
    return new PlanParamBase(name);
  }
  @Override
  public PlanParamExpr param(XsStringVal name) {
    if (name == null) {
      throw new IllegalArgumentException("name parameter for param() cannot be null");
    }
    return new ParamCallImpl("op", "param", new Object[]{ name });
  }

  static class ParamCallImpl extends PlanCallImpl implements PlanParamExpr {
    ParamCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static Map<String,String> makeMap(PlanValueOption option) {
    if (option == null) {
      return null;
    }
    return (option == PlanValueOption.DISTINCT) ? makeMap("values", "distinct") : null;
  }
  static Map<String,String> makeMap(PlanTripleOption option) {
    if (option == null) {
      return null;
    }
    return makeMap("dedup", (option == PlanTripleOption.DEDUPLICATED) ? "on" : "off");
  }
  static Map<String,String> makeMap(PlanGroupConcatOptionSeq options) {
    if (options == null) {
      return null;
    }
    if (options instanceof PlanValueOption) {
      return makeMap((PlanValueOption) options);
    }
    if (!(options instanceof PlanGroupConcatOptionSeqImpl)) {
      throw new IllegalArgumentException("invalid implementation of PlanGroupConcatOptionSeq");
    }

    PlanGroupConcatOptionSeqImpl optiondef = (PlanGroupConcatOptionSeqImpl) options;
    PlanValueOption option    = optiondef.getOption();
    String          separator = optiondef.getSeparator();
    if (option != null) {
      Map<String,String> mapdef = makeMap(option);
      if (separator != null) {
        mapdef.put("separator", separator);
      }
      return mapdef;
    } else if (separator != null) {
      return makeMap("separator", separator);
    }
    return null;
  }
  static Map<String,String> makeMap(String key, String value) {
    Map<String, String> map = new HashMap<String, String>();
    if (key != null) {
      map.put(key, value);
    }
    return map;
  }

  static BaseTypeImpl.BaseMapImpl asArg(Map<String,String> arg) {
    if (arg == null) {
      return null;
    }
    return new BaseTypeImpl.BaseMapImpl(arg);
  }

  static class PlanSubImpl
    extends PlanBuilderImpl.PlanImpl {
    private PlanBuilderBaseImpl.PlanBaseImpl prior    = null;
    private String                           fnPrefix = null;
    private String                           fnName   = null;
    private Object[]                         fnArgs   = null;

    private Map<PlanParamBase,BaseTypeImpl.ParamBinder> params = null;

    PlanSubImpl(PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
      this.prior    = prior;
      this.fnPrefix = fnPrefix;
      this.fnName   = fnName;
      this.fnArgs   = fnArgs;
    }
    private PlanSubImpl(
      PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs,
      Map<PlanParamBase,BaseTypeImpl.ParamBinder> params) {
      this(prior, fnPrefix, fnName, fnArgs);
      this.params = params;
    }

    @Override
    public Map<PlanParamBase,BaseTypeImpl.ParamBinder> getParams() {
      return params;
    }

    @Override
    public Plan bindParam(PlanParamExpr param, boolean literal) {
      return bindParam(param, new XsValueImpl.BooleanValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, byte literal) {
      return bindParam(param, new XsValueImpl.ByteValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, double literal) {
      return bindParam(param, new XsValueImpl.DoubleValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, float literal) {
      return bindParam(param, new XsValueImpl.FloatValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, int literal) {
      return bindParam(param, new XsValueImpl.IntValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, long literal) {
      return bindParam(param, new XsValueImpl.LongValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, short literal) {
      return bindParam(param, new XsValueImpl.ShortValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, String literal) {
      return bindParam(param, new XsValueImpl.StringValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal) {
      if (!(param instanceof PlanParamBase)) {
        throw new IllegalArgumentException("cannot set parameter that doesn't extend base");
      }

      Map<PlanParamBase,BaseTypeImpl.ParamBinder> nextParams = new HashMap<>();
      if (this.params != null) {
        nextParams.putAll(this.params);
      }

      if (literal instanceof XsValueImpl.AnyAtomicTypeValImpl) {
        nextParams.put((PlanParamBase) param, (XsValueImpl.AnyAtomicTypeValImpl)  literal);
      } else if (literal instanceof RdfValueImpl.RdfLangStringValImpl) {
        nextParams.put((PlanParamBase) param, (RdfValueImpl.RdfLangStringValImpl) literal);
      } else if (literal instanceof SemValueImpl.SemIriValImpl) {
        nextParams.put((PlanParamBase) param, (SemValueImpl.SemIriValImpl)        literal);
      } else {
        throw new IllegalArgumentException("cannot set value with unknown implementation");
      }

      return new PlanSubImpl(this.prior, this.fnPrefix, this.fnName, this.fnArgs, nextParams);
    }

  }

  static class ExportablePlanSubImpl
    extends PlanBuilderImpl.ExportablePlanImpl {
    ExportablePlanSubImpl(PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends JSONReadHandle> T export(T handle) {
      if (!(handle instanceof BaseHandle)) {
        throw new IllegalArgumentException("cannot export with handle that doesn't extend base");
      }
      String planAst = getAst();
// TODO: move to a method of BaseHandle?
      @SuppressWarnings("rawtypes")
      BaseHandle baseHandle = (BaseHandle) handle;
      @SuppressWarnings("rawtypes")
      Class as = baseHandle.receiveAs();
      if (InputStream.class.isAssignableFrom(as)) {
        baseHandle.receiveContent(new ByteArrayInputStream(planAst.getBytes()));
      } else if (Reader.class.isAssignableFrom(as)) {
        baseHandle.receiveContent(new StringReader(planAst));
      } else if (byte[].class.isAssignableFrom(as)) {
        baseHandle.receiveContent(planAst.getBytes());
      } else if (String.class.isAssignableFrom(as)) {
        baseHandle.receiveContent(planAst);
      } else {
        throw new IllegalArgumentException("cannot export with handle that doesn't accept content as byte[], input stream, reader, or string");
      }
      return handle;
    }
    @Override
    public <T> T exportAs(Class<T> as) {
      if (as == null) {
        throw new IllegalArgumentException("Must specify a class to export content with a registered handle");
      }

      ContentHandle<T> handle = getHandleRegistry().makeHandle(as);
      if (handle == null) {
        throw new IllegalArgumentException("No handle registered for class: "+as.getName());
      }
      if (!(handle instanceof JSONReadHandle)) {
        throw new IllegalArgumentException("Cannot parse JSON with handle registered for class: "+as.getName());
      }

      export((JSONReadHandle) handle);

      return handle.get();
    }

  }

  static class PreparePlanSubImpl
    extends PlanBuilderImpl.PreparePlanImpl {
    PreparePlanSubImpl(PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
    }
  }

  static class ModifyPlanSubImpl
    extends PlanBuilderImpl.ModifyPlanImpl {
    ModifyPlanSubImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      this(null, fnPrefix, fnName, fnArgs);
    }
    ModifyPlanSubImpl(PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
    }

    @Override
    public ModifyPlan limit(long length) {
      return limit(xs.longVal(length));
    }

    @Override
    public ModifyPlan limit(XsLongVal length) {
      return new ModifyPlanSubImpl(this, "op", "limit", new Object[]{ length });
    }

    @Override
    public ModifyPlan limit(PlanParamExpr length) {
      return new ModifyPlanSubImpl(this, "op", "limit", new Object[]{ length });
    }

    @Override
    public ModifyPlan offset(long start) {
      return offset(xs.longVal(start));
    }

    @Override
    public ModifyPlan offset(XsLongVal start) {
      return new ModifyPlanSubImpl(this, "op", "offset", new Object[]{ start });
    }

    @Override
    public ModifyPlan offset(PlanParamExpr start) {
      return new ModifyPlanSubImpl(this, "op", "offset", new Object[]{ start });
    }

    @Override
    public ModifyPlan offsetLimit(long start, long length) {
      return offsetLimit(xs.longVal(start), xs.longVal(length));
    }

    @Override
    public ModifyPlan offsetLimit(XsLongVal start, XsLongVal length) {
      return new ModifyPlanSubImpl(this, "op", "offset-limit", new Object[]{ start, length });
    }

    @Override
    public ModifyPlan where(XsBooleanExpr condition) {
      return new ModifyPlanSubImpl(this, "op", "where", new Object[]{ condition });
    }

    @Override
    public ModifyPlan where(CtsQueryExpr condition) {
      return new ModifyPlanSubImpl(this, "op", "where", new Object[]{ condition });
    }

    @Override
    public ModifyPlan where(SemStoreExpr condition) {
      return new ModifyPlanSubImpl(this, "op", "where", new Object[]{ condition });
    }

  }

  static class AccessPlanSubImpl
    extends PlanBuilderImpl.AccessPlanImpl {
    XsStringVal schema    = null;
    XsStringVal qualifier = null;
    // TODO: delete overload constructor once generated PlanBuilderImpl arities pass builder reference
    AccessPlanSubImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      this(null, fnPrefix, fnName, fnArgs);
    }
    AccessPlanSubImpl(PlanBuilderBaseImpl builder, String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
      if (!"op".equals(fnPrefix)) {
        throw new IllegalArgumentException("unknown accessor constructor prefix: "+fnPrefix);
      }

      if (builder != null) {
        setHandleRegistry(builder.getHandleRegistry());
      }

      switch(fnName) {
        case "from-view":
          if (fnArgs.length < 2) {
            throw new IllegalArgumentException("fromView() constructor missing view name parameter: "+fnArgs.length);
          }
          Object firstArg = fnArgs[0];
          if (firstArg != null) {
            schema = xs.string(firstArg.toString());
          }
          qualifier = xs.string(fnArgs[1].toString());
          break;
        case "from-lexicons":
        case "from-literals":
        case "from-triples":
          if (fnArgs.length < 1) {
            throw new IllegalArgumentException("accessor constructor without parameters: "+fnArgs.length);
          }
          qualifier = xs.string(fnArgs[0].toString());
          break;
        default:
          throw new IllegalArgumentException("unknown accessor constructor name: "+fnName);
      }
    }
    @Override
    public PlanColumn col(String name) {
      return col(xs.string(name));
    }
    @Override
    public PlanColumn col(XsStringVal name) {
      if (schema != null) {
        return pb.schemaCol(schema, qualifier, name);
      } else if (qualifier != null) {
        return pb.viewCol(qualifier, name);
      }
      return pb.col(name);
    }
  }

  static class ExprColCallImpl extends PlanCallImpl implements PlanExprCol, ColumnNamer {
    private ColumnNamer namedCol;
    ExprColCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
      Object firstArg = fnArgs[0];
      if (!(firstArg instanceof ColumnNamer)) {
        throw new IllegalArgumentException("invalid column class: "+firstArg.getClass().getName());
      }
      namedCol = (ColumnNamer) firstArg;
    }
    @Override
    public String getColName() {
      return namedCol.getColName();
    }
  }

  static class ColumnCallImpl extends PlanCallImpl implements PlanColumn, ColumnNamer {
    private Object[] fnArgs = null;
    private String   name   = null;
    ColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
      this.fnArgs = fnArgs;
    }
    @Override
    public String getColName() {
      if (name == null) {
        int stepCount = fnArgs.length;
        if (stepCount == 1) {
          name = fnArgs[0].toString();
        } else {
          name = Arrays.stream(fnArgs)
            .filter(step -> step != null)
            .map(Object::toString)
            .collect(Collectors.joining("."));
        }
      }
      return name;
    }
  }

  static class SystemColumnCallImpl extends ColumnCallImpl implements PlanSystemColumn, ColumnNamer {
    private String name;
    SystemColumnCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
      this.name = fnArgs[0].toString();
    }
    @Override
    public String getColName() {
      return name;
    }
  }

  static interface ColumnNamer {
    public String getColName();
  }

  static class CaseCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ItemExpr {
    CaseCallImpl(BaseTypeImpl.BaseArgImpl[] whenList, BaseTypeImpl.BaseArgImpl otherwise) {
      super("op", "case", new BaseTypeImpl.BaseArgImpl[]{
        new BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl>(whenList), otherwise
      });
    }
  }
  static class CaseWhenCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PlanCase {
    CaseWhenCallImpl(Object[] args) {
      super("op", "when", BaseTypeImpl.convertList(args));
    }
  }
  static class CaseElseImpl implements PlanCase {
    private BaseTypeImpl.BaseArgImpl arg = null;
    CaseElseImpl(BaseTypeImpl.BaseArgImpl arg) {
      this.arg = arg;
    }
    BaseTypeImpl.BaseArgImpl getArg() {
      return arg;
    }
  }

  static interface JsonContentCallImpl extends JsonContentNodeExpr, BaseTypeImpl.BaseArgImpl {}
  static class JsonContentSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    JsonContentSeqListImpl(JsonContentNodeExpr[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.BaseArgImpl[].class));
    }
  }

  static class JsonPropertySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    JsonPropertySeqListImpl(PlanJsonProperty[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.BaseArgImpl[].class));
    }
  }

  static class JsonObjectCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ObjectNodeExpr, JsonContentCallImpl {
    JsonObjectCallImpl(Object[] args) {
      super("op", "json-object", BaseTypeImpl.convertList(args));
    }
  }
  static class JsonPropertyCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PlanJsonProperty {
    JsonPropertyCallImpl(Object[] args) {
      super("op", "prop", BaseTypeImpl.convertList(args));
    }
  }
  static class JsonArrayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ArrayNodeExpr, JsonContentCallImpl {
    JsonArrayCallImpl(Object[] args) {
      super("op", "json-array", BaseTypeImpl.convertList(args));
    }
  }

  static class XmlAttributeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements AttributeNodeSeqExpr {
    XmlAttributeSeqListImpl(AttributeNodeExpr[] items) {
      super(Arrays.copyOf(items, items.length, XmlAttributeCallImpl[].class));
    }
  }

  static interface XmlContentCallImpl  extends XmlContentNodeExpr,  BaseTypeImpl.BaseArgImpl {}
  static class XmlContentSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> {
    XmlContentSeqListImpl(XmlContentNodeExpr[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.NodeCallImpl[].class));
    }
  }

  static class XmlElementCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ElementNodeExpr, XmlContentCallImpl {
    XmlElementCallImpl(Object[] args) {
      super("op", "xml-element", BaseTypeImpl.convertList(args));
    }
  }
  static class XmlAttributeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AttributeNodeExpr {
    XmlAttributeCallImpl(Object[] args) {
      super("op", "xml-attribute", BaseTypeImpl.convertList(args));
    }
  }

  static class PrefixerImpl implements PlanPrefixer {
    private SemExpr sem;
    private String prefix;
    private PrefixerImpl(SemExpr sem, String prefix) {
      this.prefix = prefix;
      this.sem    = sem;
    }

    @Override
    public SemIriVal iri(XsStringVal suffix) {
      return iri(suffix.getString());
    }
    @Override
    public SemIriVal iri(String suffix) {
      if (suffix == null || suffix.length() == 0) {
        throw new IllegalArgumentException("cannot create SemIriVal with empty string");
      }

      String firstChar = suffix.substring(0, 1);
      if ("/".equals(firstChar) || "#".equals(firstChar) || "?".equals(firstChar)) {
        if (suffix.length() == 1) {
          throw new IllegalArgumentException("cannot create SemIriVal from: "+suffix);
        }
        suffix = suffix.substring(1);
      }

      return sem.iri(prefix+suffix);
    }
  }
}
