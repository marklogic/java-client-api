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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.type.*;

abstract class PlanBuilderBaseImpl extends PlanBuilder {
  final static PlanBuilderSubImpl pb = new PlanBuilderSubImpl();

  private HandleFactoryRegistry handleRegistry;

  PlanBuilderBaseImpl() {
    super(
      CtsExprImpl.cts, FnExprImpl.fn, GeoExprImpl.geo, JsonExprImpl.json, MapExprImpl.map,
      MathExprImpl.math, RdfExprImpl.rdf, SemExprImpl.sem, SpellExprImpl.spell,
      SqlExprImpl.sql, XdmpExprImpl.xdmp, XsExprImpl.xs, RdtExprImpl.rdt
    );
  }

  HandleFactoryRegistry getHandleRegistry() {
    return handleRegistry;
  }
  void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
    this.handleRegistry = handleRegistry;
  }

  public PlanColumn exprCol(String column) {
    return col(column);
  }

  static BaseTypeImpl.Literal literal(Object value) {
    return new BaseTypeImpl.Literal(value);
  }

  @Override
  public PlanSearchOptions searchOptions() {
    return new PlanSearchOptionsImpl(this);
  }

  static class PlanSearchOptionsImpl implements PlanSearchOptions {
    private PlanBuilderBaseImpl pb;
    private XsFloatVal qualityWeight;
    private ScoreMethod scoreMethod;
	private XsDoubleVal bm25LengthWeight;
    PlanSearchOptionsImpl(PlanBuilderBaseImpl pb) {
      this.pb = pb;
    }
	PlanSearchOptionsImpl(PlanBuilderBaseImpl pb, XsFloatVal qualityWeight,
						  ScoreMethod scoreMethod, XsDoubleVal bm25LengthWeight) {
		  this(pb);
		  this.qualityWeight = qualityWeight;
		  this.scoreMethod   = scoreMethod;
		  this.bm25LengthWeight = bm25LengthWeight;
	}

    @Override
    public XsFloatVal getQualityWeight() {
      return qualityWeight;
    }
    @Override
    public ScoreMethod getScoreMethod() {
      return scoreMethod;
    }
	@Override
	public XsDoubleVal getBm25LengthWeight() {
		  return bm25LengthWeight;
	  }
    @Override
    public PlanSearchOptions withQualityWeight(float qualityWeight) {
      return withQualityWeight(pb.xs.floatVal(qualityWeight));
    }
    @Override
    public PlanSearchOptions withQualityWeight(XsFloatVal qualityWeight) {
      return new PlanSearchOptionsImpl(pb, qualityWeight, getScoreMethod(), getBm25LengthWeight());
    }
    @Override
    public PlanSearchOptions withScoreMethod(ScoreMethod scoreMethod) {
      return new PlanSearchOptionsImpl(pb, getQualityWeight(), scoreMethod, getBm25LengthWeight());
    }

	  @Override
	  public PlanSearchOptions withBm25LengthWeight(double bm25LengthWeight) {
		  return new PlanSearchOptionsImpl(pb, getQualityWeight(), getScoreMethod(), pb.xs.doubleVal(bm25LengthWeight));
	  }

	  Map<String,Object> makeMap() {
	      if (qualityWeight == null && scoreMethod == null && bm25LengthWeight == null) return null;

	      Map<String, Object> map = new HashMap<>();
    	  if (qualityWeight != null) {
			  map.put("qualityWeight", qualityWeight);
		  }
		  if (scoreMethod != null) {
			  map.put("scoreMethod", scoreMethod.name().toLowerCase());
		  }
		  if (bm25LengthWeight != null) {
			  map.put("bm25LengthWeight", bm25LengthWeight);
		  }
		  return map;
	}
  }

  static class PlanParamBase extends BaseTypeImpl.BaseCallImpl<XsValueImpl.StringValImpl> implements PlanParamExpr {
    String name = null;
    PlanParamBase(String name) {
      super("op", "param", new XsValueImpl.StringValImpl[]{new XsValueImpl.StringValImpl(name)});
      if (name == null) {
        throw new IllegalArgumentException("cannot define parameter with null name");
      }
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

  interface RequestPlan {
    Map<PlanParamBase,BaseTypeImpl.ParamBinder> getParams();
    AbstractWriteHandle getHandle();
    List<ContentParam> getContentParams();
  }

  static abstract class PlanBaseImpl
    extends BaseTypeImpl.BaseChainImpl<BaseTypeImpl.BaseArgImpl>
    implements PlanBuilder.Plan, RequestPlan, BaseTypeImpl.BaseArgImpl {
    final static CtsExprImpl   cts   = CtsExprImpl.cts;
    final static FnExprImpl    fn    = FnExprImpl.fn;
    final static JsonExprImpl  json  = JsonExprImpl.json;
    final static MapExprImpl   map   = MapExprImpl.map;
    final static MathExprImpl  math  = MathExprImpl.math;
    final static RdfExprImpl   rdf   = RdfExprImpl.rdf;
    final static SemExprImpl   sem   = SemExprImpl.sem;
    final static SpellExprImpl spell = SpellExprImpl.spell;
    final static SqlExprImpl   sql   = SqlExprImpl.sql;
    final static XdmpExprImpl  xdmp  = XdmpExprImpl.xdmp;
    final static XsExprImpl    xs    = XsExprImpl.xs;

    final static PlanBuilderSubImpl pb = PlanBuilderBaseImpl.pb;

    private HandleFactoryRegistry handleRegistry;

    PlanBaseImpl(PlanBaseImpl prior, String fnPrefix, String fnName, Object[] fnArgs) {
      super(prior, fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
      if (prior != null) {
        setHandleRegistry(prior.getHandleRegistry());
      }
    }


    HandleFactoryRegistry getHandleRegistry() {
      return handleRegistry;
    }
    void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
      this.handleRegistry = handleRegistry;
    }

    @Override
    public AbstractWriteHandle getHandle() {
// TODO: maybe serialize plan to JSON using JSON writer?
      return new StringHandle(getAst()).withFormat(Format.JSON);
    }

    String getAst() {
      StringBuilder strb = new StringBuilder();
      strb.append("{\"$optic\":");
      return exportAst(strb).append("}").toString();
    }

    public PlanColumn col(String column) {
      return pb.col(column);
    }
    public PlanColumn exprCol(String column) {
      return pb.exprCol(column);
    }
  }

  static class PlanCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    PlanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PlanSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl {
    PlanSeqListImpl(Object[] items) {
      super(items);
    }
  }
}
