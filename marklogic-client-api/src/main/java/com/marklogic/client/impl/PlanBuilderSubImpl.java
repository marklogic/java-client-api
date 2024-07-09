/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.*;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.type.*;

public class PlanBuilderSubImpl extends PlanBuilderImpl {
  public PlanBuilderSubImpl() {
    super();
  }

	@Override
	public PatchBuilder patchBuilder(String contextPath) {
		return new PatchBuilderImpl(new XsValueImpl.StringValImpl(contextPath));
	}

	@Override
	public PatchBuilder patchBuilder(XsStringVal contextPath) {
		return new PatchBuilderImpl(contextPath);
	}

	@Override
	public PatchBuilder patchBuilder(String contextPath, Map<String, String> namespaces) {
		return new PatchBuilderImpl(new XsValueImpl.StringValImpl(contextPath), namespaces);
	}

	@Override
	public PatchBuilder patchBuilder(XsStringVal contextPath, Map<String, String> namespaces) {
		return new PatchBuilderImpl(contextPath, namespaces);
	}

	@Override
  public PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query) {
    return fromSearchDocs(query, null);
  }
  @Override
  public PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query, String qualifierName) {
	  return fromSearchDocs(query, null, null);
  }
  @Override
  public PlanBuilder.AccessPlan fromSearchDocs(CtsQueryExpr query, String qualifierName, PlanSearchOptions options) {
	  XsStringVal qualifierValue = qualifierName == null ? null : xs.string(qualifierName);
	  Object[] args = options == null ?
		  new Object[] {query, qualifierValue} :
		  new Object[] {query, qualifierValue, asArg(makeMap(options))};
	  return new AccessPlanSubImpl(this, "op", "from-search-docs", args);
  }

  @Override
  public PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query) {
    return fromSearch(query, null, null, null);
  }
  @Override
  public PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query, PlanExprCol... columns) {
    return fromSearch(query, new ExprColSeqListImpl(columns), null, null);
  }
  @Override
  public PlanBuilder.AccessPlan fromSearch(CtsQueryExpr query, PlanExprColSeq columns, String qualifierName) {
    return fromSearch(query, columns, (qualifierName == null) ? null : xs.string(qualifierName), null);
  }
  @Override
  public PlanBuilder.AccessPlan fromSearch(
          CtsQueryExpr query, PlanExprColSeq columns, XsStringVal qualifierName, PlanSearchOptions options
  ) {
    return new AccessPlanSubImpl(this, "op", "from-search",
            new Object[]{query, columns, qualifierName, asArg(makeMap(options))});
  }

  @Override
  public ModifyPlan fromSparql(String select, String qualifierName, PlanSparqlOptions option) {
    return fromSparql((select == null) ? (XsStringVal) null : xs.string(select), (qualifierName == null) ? (XsStringVal) null : xs.string(qualifierName), option);
  }

  @Override
  public ModifyPlan fromSparql(XsStringVal select, XsStringVal qualifierName, PlanSparqlOptions option) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSparql() cannot be null");
    }
    ModifyPlanSubImpl plan = new PlanBuilderSubImpl.ModifyPlanSubImpl(
            "op", "from-sparql", new Object[]{ select, qualifierName, asArg(makeMap(option)) }
    );
    plan.setHandleRegistry(this.getHandleRegistry());
    return plan;
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
  // TODO: delete override of fromParam() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromParam(XsStringVal paramName, XsStringVal qualifier, PlanRowColTypesSeq colTypes) {
    if (paramName == null) {
      throw new IllegalArgumentException("paramName parameter for fromParam() cannot be null");
    }
    if (colTypes == null) {
      throw new IllegalArgumentException("colTypes parameter for fromParam() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl(this, "op", "from-param", new Object[]{ paramName, qualifier, colTypes });
  }
  // TODO: delete override of fromDocDescriptors() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor) {
    if (docDescriptor == null) {
      throw new IllegalArgumentException("docDescriptor parameter for fromDocDescriptors() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl(this,"op", "from-doc-descriptors", new Object[]{ docDescriptor });
  }
  @Override
  public AccessPlan fromDocDescriptors(PlanDocDescriptorSeq docDescriptor, XsStringVal qualifier) {
    if (docDescriptor == null) {
      throw new IllegalArgumentException("docDescriptor parameter for fromDocDescriptors() cannot be null");
    }
    return new PlanBuilderSubImpl.AccessPlanSubImpl(this, "op", "from-doc-descriptors", new Object[]{ docDescriptor, qualifier });
  }
  // TODO: delete override of fromSql() arity after code generation passes PlanBuilderBaseImpl this
  @Override
  public ModifyPlan fromSql(XsStringVal select) {
    if (select == null) {
      throw new IllegalArgumentException("select parameter for fromSql() cannot be null");
    }
    ModifyPlanSubImpl plan = new PlanBuilderSubImpl.ModifyPlanSubImpl("op", "from-sql", new Object[]{ select });
    plan.setHandleRegistry(this.getHandleRegistry());
    return plan;
  }

  @Override
  public AccessPlan fromDocUris(String... uris) {
    return fromDocUris(this.cts.documentQuery(xs.stringSeq(uris)));
  }

  @Override
  public AccessPlan fromDocUris(CtsQueryExpr querydef) {
    return fromDocUris(querydef, null);
  }

  @Override
  public AccessPlan fromDocUris(CtsQueryExpr querydef, String qualifierName) {
    return new AccessPlanSubImpl(
            this, "op", "from-doc-uris", new Object[]{querydef, (qualifierName == null) ?
            null : xs.string(qualifierName)}
    );
  }

  @Override
  public PlanDocColsIdentifier docCols(Map<String, PlanColumn> descriptorColumnMapping) {
    return new PlanDocColsIdentifierImpl(descriptorColumnMapping);
  }

  public static class PlanDocDescriptorImpl implements PlanDocDescriptor, BaseTypeImpl.BaseArgImpl {
    private String template;
    // Threadsafe, thus safe to reuse to avoid creating many instances
    private static ObjectMapper mapper = new ObjectMapper();
    public PlanDocDescriptorImpl(DocumentWriteOperation writeOp) {
      ObjectNode node = mapper.createObjectNode();
      DocDescriptorUtil.populateDocDescriptor(writeOp, node);
      this.template = node.toString();
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
      return strb.append(template);
    }
  }

  @Override
  public PlanDocDescriptor docDescriptor(DocumentWriteOperation writeOp) {
    return new PlanDocDescriptorImpl(writeOp);
  }

  static class PlanDocDescriptorSeqImpl implements PlanDocDescriptorSeq, BaseTypeImpl.BaseArgImpl {
    private String template;
    public PlanDocDescriptorSeqImpl(DocumentWriteSet writeSet) {
      this.template = DocDescriptorUtil.buildDocDescriptors(writeSet).toString();
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
      return strb.append(template);
    }
  }

  @Override
  public PlanDocDescriptorSeq docDescriptors(DocumentWriteSet writeSet) {
    return new PlanDocDescriptorSeqImpl(writeSet);
  }

  @Override
  public PlanRowColTypesSeq colTypes(PlanRowColTypes... colTypes) {
    return new PlanRowColTypesSeqImpl(colTypes);
  }

  @Override
  public PlanRowColTypes colType(String column) {
    return colType(column, null);
  }

  @Override
  public PlanRowColTypes colType(String column, String type) {
    return colType(column, type, null);
  }

  @Override
  public PlanRowColTypes colType(String column, String type, Boolean nullable) {
    return new PlanRowColTypesImpl(column, type, nullable);
  }


  @Override
  public TransformDef transformDef(String path) {
    return new TransformDefImpl(path);
  }
  @Override
  public SchemaDefExpr schemaDefinition(String kind) {
    return new SchemaDefImpl(kind);
  }

  @Override
  public ServerExpression permission(String roleName, String capability) {
    return jsonObject(
        prop(xs.string("roleName"), xs.string(roleName)),
        prop(xs.string("capability"), xs.string(capability))
    );
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
  public PlanGroup group(String... keys) {
    return group(colSeq(keys));
  }
  @Override
  public PlanGroupSeq rollup(String... keys) {
    return rollup(colSeq(keys));
  }
  @Override
  public PlanGroupSeq cube(String... keys) {
    return cube(colSeq(keys));
  }
  @Override
  public PlanNamedGroup namedGroup(String name) {
    return namedGroup(xs.string(name), null);
  }
  @Override
  public PlanNamedGroup namedGroup(String name, PlanExprColSeq keys) {
    return namedGroup(xs.string(name), keys);
  }

  @Override
  public PlanSampleByOptions sampleByOptions() {
    return new PlanSampleByOptionsImpl(this);
  }
  static class PlanSampleByOptionsImpl implements PlanSampleByOptions {
    private PlanBuilderBaseImpl pb;
    private XsIntVal limit;
    PlanSampleByOptionsImpl(PlanBuilderBaseImpl pb) {
      this.pb = pb;
    }
    PlanSampleByOptionsImpl(PlanBuilderBaseImpl pb, XsIntVal limit) {
      this(pb);
      this.limit = limit;
    }
    @Override
    public XsIntVal getLimit() {
      return limit;
    }
    @Override
    public PlanSampleByOptions withLimit(int limit) {
      return withLimit(pb.xs.intVal(limit));
    }
    @Override
    public PlanSampleByOptions withLimit(XsIntVal limit) {
      return new PlanSampleByOptionsImpl(this.pb, limit);
    }
  }

  @Override
  public PlanSparqlOptions sparqlOptions() {
    return new PlanSparqlOptionsImpl(this);
  }
  static class PlanSparqlOptionsImpl implements PlanSparqlOptions {
    private PlanBuilderBaseImpl pb;
    private XsBooleanVal deduplicate;
    private XsStringVal base;
    PlanSparqlOptionsImpl(PlanBuilderBaseImpl pb) {
      this.pb = pb;
    }
    PlanSparqlOptionsImpl(PlanBuilderBaseImpl pb, XsBooleanVal deduplicate, XsStringVal base) {
      this(pb);
      this.deduplicate = deduplicate;
      this.base = base;
    }

    @Override
    public XsStringVal getBase() {
      return base;
    }
    @Override
    public PlanSparqlOptions withBase(String base) {
      return withBase(pb.xs.string(base));
    }
    @Override
    public PlanSparqlOptions withBase(XsStringVal base) {
      return new PlanSparqlOptionsImpl(this.pb, this.deduplicate, base);
    }
    @Override
    public XsBooleanVal getDeduplicated() {
      return deduplicate;
    }
    @Override
    public PlanSparqlOptions withDeduplicated(boolean deduplicate) {
      return withDeduplicated(pb.xs.booleanVal(deduplicate));
    }
    @Override
    public PlanSparqlOptions withDeduplicated(XsBooleanVal deduplicate) {
      return new PlanSparqlOptionsImpl(this.pb, deduplicate, this.base);
    }
  }

  @Override
  public ServerExpression caseExpr(PlanCase... cases) {
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
  public PlanExprCol as(PlanColumn column, ServerExpression expression) {
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
  public PlanCase when(boolean condition, ServerExpression... value) {
    return when(xs.booleanVal(condition), value);
  }
  @Override
  public PlanCase when(ServerExpression condition, ServerExpression... value) {
    if (condition == null) {
      throw new IllegalArgumentException("condition parameter for when() cannot be null");
    }
    return new CaseWhenCallImpl(new Object[]{ condition, new BaseTypeImpl.ItemSeqListImpl(value) });
  }
  @Override
  public PlanCase elseExpr(ServerExpression value) {
    if (!(value instanceof BaseTypeImpl.BaseArgImpl)) {
      throw new IllegalArgumentException("invalid value for elseExpr(): "+value.getClass().getName());
    }
    return new CaseElseImpl((BaseTypeImpl.BaseArgImpl) value);
  }

  // TODO: move jsonArray() and jsonObject() into generated code
  @Override
  public ServerExpression jsonObject(PlanJsonProperty... properties) {
    return new JsonObjectCallImpl(new Object[]{ new JsonPropertySeqListImpl(properties) });
  }
  @Override
  public ServerExpression jsonArray(ServerExpression... items) {
    return new JsonArrayCallImpl(new Object[]{ new BaseTypeImpl.ItemSeqListImpl(items) });
  }

  @Override
  public ServerExpression xmlAttributeSeq(ServerExpression... attributes) {
    return new XmlAttributeSeqListImpl(attributes);
  }

  @Override
  public PlanGroupSeq groupSeq(PlanGroup... groups) {
    return new GroupSeqListImpl(groups);
  }
  @Override
  public PlanNamedGroupSeq namedGroupSeq(PlanNamedGroup... namedGroups) {
    return new NamedGroupSeqListImpl(namedGroups);
  }

  @Override
  public PlanFunction resolveFunction(XsQNameVal functionName, String modulePath) {
    return resolveFunction(functionName, xs.string(modulePath));
  }

  @Override
  public ServerExpression seq(ServerExpression... expr) {
    return new BaseTypeImpl.ServerExpressionListImpl(expr, true);
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

  static Map<String,Object> makeMap(PlanSearchOptions options) {
    if (options == null) {
      return null;
    }
    if (!(options instanceof PlanSearchOptionsImpl)) {
      throw new IllegalArgumentException("invalid implementation of PlanSearchOptions");
    }
    return ((PlanSearchOptionsImpl) options).makeMap();
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
    PlanValueOption option = optiondef.getOption();

    Map<String,String> mapdef = (option == null) ? null : makeMap(option);

    String separator = optiondef.getSeparator();
    if (separator != null) {
      if (mapdef != null) {
        mapdef.put("separator", separator);
      } else {
        mapdef = makeMap("separator", separator);
      }
    }

    return mapdef;
  }
  static Map<String,XsIntVal> makeMap(PlanSampleByOptions options) {
    if (options == null) {
      return null;
    }

    XsIntVal limit = options.getLimit();
    return (limit == null) ? null : makeMap("limit", limit);
  }
  static Map<String,String> makeMap(PlanSparqlOptions options) {
    if (options == null) {
      return null;
    }

    Map<String,String> mapdef = null;

    XsBooleanVal dedup = options.getDeduplicated();
    if (dedup != null) {
      mapdef = makeMap("dedup", dedup.getBoolean() ? "on" : "off");
    }

    XsStringVal base = options.getBase();
    if (base != null) {
      if (mapdef == null) {
        mapdef = makeMap("base", base.getString());
      } else {
        mapdef.put("base", base.getString());
      }
    }

    return mapdef;
  }
  static Map<String,String> makeMap(String key, String value) {
    Map<String, String> map = new HashMap<String, String>();
    if (key != null) {
      map.put(key, value);
    }
    return map;
  }

  static Map<String, XsIntVal> makeMap(String key, XsIntVal value) {
    Map<String, XsIntVal> map = new HashMap<String, XsIntVal>();
    if (key != null) {
      map.put(key, value);
    }
    return map;
  }

  static BaseTypeImpl.BaseMapImpl asArg(Map<String, ?> arg) {
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
    private List<ContentParam> contentParams;

    PlanSubImpl(PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, fnArgs);
      this.prior    = prior;
      this.fnPrefix = fnPrefix;
      this.fnName   = fnName;
      this.fnArgs   = fnArgs;
    }
    private PlanSubImpl(
      PlanBuilderBaseImpl.PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs,
      Map<PlanParamBase,BaseTypeImpl.ParamBinder> params,
      List<ContentParam> contentParams) {
      this(prior, fnPrefix, fnName, fnArgs);
      this.params = params;
      this.contentParams = contentParams;
    }

    @Override
    public Map<PlanParamBase,BaseTypeImpl.ParamBinder> getParams() {
      return params;
    }

    @Override
    public Plan bindParam(String paramName, boolean literal) {
      return bindParam(new PlanParamBase(paramName), literal);
    }
    @Override
    public Plan bindParam(PlanParamExpr param, boolean literal) {
      return bindParam(param, new XsValueImpl.BooleanValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, byte literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.ByteValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, byte literal) {
      return bindParam(param, new XsValueImpl.ByteValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, double literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.DoubleValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, double literal) {
      return bindParam(param, new XsValueImpl.DoubleValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, float literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.FloatValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, float literal) {
      return bindParam(param, new XsValueImpl.FloatValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, int literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.IntValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, int literal) {
      return bindParam(param, new XsValueImpl.IntValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, long literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.LongValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, long literal) {
      return bindParam(param, new XsValueImpl.LongValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, short literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.ShortValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, short literal) {
      return bindParam(param, new XsValueImpl.ShortValImpl(literal));
    }
    @Override
    public Plan bindParam(String paramName, String literal) {
      return bindParam(new PlanParamBase(paramName), new XsValueImpl.StringValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, String literal) {
      return bindParam(param, new XsValueImpl.StringValImpl(literal));
    }
    @Override
    public Plan bindParam(PlanParamExpr param, PlanParamBindingVal literal) {
      if (!(param instanceof PlanParamBase)) {
        throw new IllegalArgumentException("param must be an instance of PlanParamBase");
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

      return new PlanSubImpl(this.prior, this.fnPrefix, this.fnName, this.fnArgs, nextParams, this.contentParams);
    }

    @Override
    public Plan bindParam(String param, DocumentWriteSet writeSet) {
      return bindParam(new PlanParamBase(param), writeSet);
    }

    @Override
    public Plan bindParam(PlanParamExpr param, DocumentWriteSet writeSet) {
      if (!(param instanceof PlanParamBase)) {
        throw new IllegalArgumentException("param must be an instance of PlanParamBase");
      }
      List<ContentParam> nextContentParams = new ArrayList<>();
      if (this.contentParams != null) {
        nextContentParams.addAll(this.contentParams);
      }
      nextContentParams.add(ContentParam.fromDocumentWriteSet((PlanParamBase) param, writeSet));
      return new PlanSubImpl(this.prior, this.fnPrefix, this.fnName, this.fnArgs, this.params, nextContentParams);
    }

    @Override
    public Plan bindParam(String param, AbstractWriteHandle content) {
      return bindParam(new PlanParamBase(param), content, null);
    }

    @Override
    public Plan bindParam(String param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments) {
      return bindParam(new PlanParamBase(param), content, columnAttachments);
    }

    @Override
    public Plan bindParam(PlanParamExpr param, AbstractWriteHandle content, Map<String, Map<String, AbstractWriteHandle>> columnAttachments) {
      if (!(param instanceof PlanParamBase)) {
        throw new IllegalArgumentException("param must be an instance of PlanParamBase");
      }
      PlanParamBase baseParam = (PlanParamBase) param;
      List<ContentParam> nextContentParams = new ArrayList<>();
      if (this.contentParams != null) {
        nextContentParams.addAll(this.contentParams);
      }
      nextContentParams.add(new ContentParam(baseParam, content, columnAttachments));
      return new PlanSubImpl(this.prior, this.fnPrefix, this.fnName, this.fnArgs, this.params, nextContentParams);
    }
    @Override
    public List<ContentParam> getContentParams() {
      return contentParams;
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
      Utilities.setHandleToString(handle, getAst());
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
	  public ModifyPlan patch(String docColumn, PatchBuilder patchDef) {
		  return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "patch", new Object[]{ this.col(docColumn), patchDef });
	  }

	  @Override
	  public ModifyPlan patch(PlanExprCol docColumn, PatchBuilder patchDef) {
		  return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "patch", new Object[]{ docColumn, patchDef });
	  }

	  @Override
    public PlanBuilder.ModifyPlan groupByUnion(PlanGroupSeq keys) {
      return groupByUnion(keys, null);
    }
    @Override
    public PlanBuilder.ModifyPlan groupByUnion(PlanGroupSeq keys, PlanAggregateColSeq aggregates) {
      return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "group-by-union", new Object[]{ keys, aggregates });
    }
    @Override
    public PlanBuilder.ModifyPlan groupToArrays(PlanNamedGroupSeq keys) {
      return groupToArrays(keys, null);
    }
    @Override
    public PlanBuilder.ModifyPlan groupToArrays(PlanNamedGroupSeq keys, PlanAggregateColSeq aggregates) {
      return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "group-to-arrays", new Object[]{ keys, aggregates });
    }

    @Override
    public ModifyPlan facetBy(PlanNamedGroupSeq keys) {
      if (keys == null) {
        throw new IllegalArgumentException("keys parameter for facetBy() cannot be null");
      }
      return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "facet-by", new Object[]{ keys });
    }
    @Override
    public ModifyPlan facetBy(PlanNamedGroupSeq keys, String countCol) {
      return facetBy(keys, (countCol == null) ? (PlanExprCol) null : exprCol(countCol));
    }
    @Override
    public ModifyPlan facetBy(PlanNamedGroupSeq keys, PlanExprCol countCol) {
      if (keys == null) {
        throw new IllegalArgumentException("keys parameter for facetBy() cannot be null");
      }
      return new PlanBuilderSubImpl.ModifyPlanSubImpl(this, "op", "facet-by", new Object[]{ keys, countCol });
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
    public ModifyPlan lockForUpdate() {
      return new ModifyPlanSubImpl(this, "op", "lockForUpdate", new Object[]{});
    }

    @Override
    public ModifyPlan lockForUpdate(PlanColumn uriColumn) {
      return new ModifyPlanSubImpl(this, "op", "lockForUpdate", new Object[]{uriColumn});
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
    public ModifyPlan remove() {
      return new ModifyPlanSubImpl(this, "op", "remove", new Object[]{});
    }

    @Override
    public ModifyPlan remove(PlanColumn uriColumn) {
      return new ModifyPlanSubImpl(this, "op", "remove", new Object[]{uriColumn});
    }

    static class TemporalRemoval implements BaseArgImpl {
        private String template;

        public TemporalRemoval(PlanColumn temporalCollection, PlanColumn uriColumn) {
            this.template = String.format("{\"temporalCollection\":%s, \"uri\": %s}",
                ((BaseArgImpl)temporalCollection).exportAst(new StringBuilder()),
                    ((BaseArgImpl) uriColumn).exportAst(new StringBuilder()));
        }

        @Override
        public StringBuilder exportAst(StringBuilder strb) {
            return strb.append(template);
        }
    }

    @Override
    public ModifyPlan remove(PlanColumn temporalCollection, PlanColumn uriColumn) {
        return new ModifyPlanSubImpl(this, "op", "remove", new Object[]{new TemporalRemoval(temporalCollection, uriColumn)});
    }

    @Override
    public ModifyPlan transformDoc(PlanColumn docColumn, TransformDef transformDef) {
      return new ModifyPlanSubImpl(this, "op", "transformDoc", new Object[]{docColumn, transformDef});
    }

    @Override
    public ModifyPlan where(ServerExpression condition) {
      return new ModifyPlanSubImpl(this, "op", "where", new Object[]{ condition });
    }

    @Override
    public ModifyPlan where(CtsQueryExpr condition) {
      return new ModifyPlanSubImpl(this, "op", "where", new Object[]{ condition });
    }

    @Override
    public ModifyPlan where(PlanCondition condition) {
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
        case "from-search":
        case "from-search-docs":
        case "from-triples":
        case "from-doc-uris":
        case "from-param":
        case "from-doc-descriptors":
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
    @Override
    public ModifyPlan sampleBy(PlanSampleByOptions option) {
      return new PlanBuilderSubImpl.ModifyPlanSubImpl(
              this, "op", "sample-by", new Object[]{ asArg(makeMap(option)) }
      );
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

  static class CaseCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    CaseCallImpl(BaseTypeImpl.BaseArgImpl[] whenList, BaseTypeImpl.BaseArgImpl otherwise) {
      super("op", "case", new BaseTypeImpl.BaseArgImpl[]{
        new BaseTypeImpl.ServerExpressionListImpl(whenList), otherwise
      });
    }
  }
  static class CaseWhenCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements PlanCase {
    CaseWhenCallImpl(Object[] args) {
      super("op", "when", args);
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

  static interface JsonContentCallImpl extends BaseTypeImpl.BaseArgImpl {}
  static class JsonContentSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl {
    JsonContentSeqListImpl(ServerExpression[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.BaseArgImpl[].class));
    }
  }

  static class JsonPropertySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl {
    JsonPropertySeqListImpl(PlanJsonProperty[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.BaseArgImpl[].class));
    }
  }

  static class JsonObjectCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements JsonContentCallImpl {
    JsonObjectCallImpl(Object[] args) {
      super("op", "json-object", args);
    }
  }
  static class JsonPropertyCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements PlanJsonProperty {
    JsonPropertyCallImpl(Object[] args) {
      super("op", "prop", args);
    }
  }
  static class JsonArrayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements JsonContentCallImpl {
    JsonArrayCallImpl(Object[] args) {
      super("op", "json-array", args);
    }
  }

  static class XmlAttributeSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl {
    XmlAttributeSeqListImpl(ServerExpression[] items) {
      super(Arrays.copyOf(items, items.length, XmlAttributeCallImpl[].class));
    }
  }

  static interface XmlContentCallImpl  extends BaseTypeImpl.BaseArgImpl {}
  static class XmlContentSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl {
    XmlContentSeqListImpl(ServerExpression[] items) {
      super(Arrays.copyOf(items, items.length, BaseTypeImpl.NodeCallImpl[].class));
    }
  }

/* TODO: DELETE
  static class XmlElementCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements ElementNodeExpr, XmlContentCallImpl {
    XmlElementCallImpl(Object[] args) {
      super("op", "xml-element", args);
    }
  }
 */
  static class XmlAttributeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    XmlAttributeCallImpl(Object[] args) {
      super("op", "xml-attribute", args);
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
