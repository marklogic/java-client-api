/*
 * Copyright (c) 2024 MarkLogic Corporation
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

import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.SemStoreExpr;
import com.marklogic.client.type.SemStoreSeqExpr;

import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class SemExprImpl extends SemValueImpl implements SemExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static SemExprImpl sem = new SemExprImpl();

  SemExprImpl() {
  }

    
  @Override
  public ServerExpression bnode() {
    return new BlankCallImpl("sem", "bnode", new Object[]{  });
  }

  
  @Override
  public ServerExpression bnode(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for bnode() cannot be null");
    }
    return new BlankCallImpl("sem", "bnode", new Object[]{ value });
  }

  
  @Override
  public ServerExpression coalesce(ServerExpression... parameter1) {
    return new BaseTypeImpl.ItemSeqCallImpl("sem", "coalesce", parameter1);
  }

  
  @Override
  public ServerExpression datatype(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for datatype() cannot be null");
    }
    return new IriCallImpl("sem", "datatype", new Object[]{ value });
  }

  
  @Override
  public ServerExpression defaultGraphIri() {
    return new IriCallImpl("sem", "default-graph-iri", new Object[]{  });
  }

  
  @Override
  public ServerExpression ifExpr(ServerExpression condition, ServerExpression then, ServerExpression elseExpr) {
    if (condition == null) {
      throw new IllegalArgumentException("condition parameter for ifExpr() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("sem", "if", new Object[]{ condition, then, elseExpr });
  }

  
  @Override
  public ServerExpression invalid(ServerExpression string, String datatype) {
    return invalid(string, (datatype == null) ? (ServerExpression) null : iri(datatype));
  }

  
  @Override
  public ServerExpression invalid(ServerExpression string, ServerExpression datatype) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for invalid() cannot be null");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("datatype parameter for invalid() cannot be null");
    }
    return new InvalidCallImpl("sem", "invalid", new Object[]{ string, datatype });
  }

  
  @Override
  public ServerExpression invalidDatatype(ServerExpression val) {
    if (val == null) {
      throw new IllegalArgumentException("val parameter for invalidDatatype() cannot be null");
    }
    return new IriCallImpl("sem", "invalid-datatype", new Object[]{ val });
  }

  
  @Override
  public ServerExpression iri(ServerExpression stringIri) {
    return new IriCallImpl("sem", "iri", new Object[]{ stringIri });
  }

  
  @Override
  public ServerExpression iriToQName(ServerExpression arg1) {
    if (arg1 == null) {
      throw new IllegalArgumentException("arg1 parameter for iriToQName() cannot be null");
    }
    return new XsExprImpl.QNameCallImpl("sem", "iri-to-QName", new Object[]{ arg1 });
  }

  
  @Override
  public ServerExpression isBlank(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for isBlank() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sem", "isBlank", new Object[]{ value });
  }

  
  @Override
  public ServerExpression isIRI(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for isIRI() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sem", "isIRI", new Object[]{ value });
  }

  
  @Override
  public ServerExpression isLiteral(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for isLiteral() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sem", "isLiteral", new Object[]{ value });
  }

  
  @Override
  public ServerExpression isNumeric(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for isNumeric() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sem", "isNumeric", new Object[]{ value });
  }

  
  @Override
  public ServerExpression lang(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for lang() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sem", "lang", new Object[]{ value });
  }

  
  @Override
  public ServerExpression langMatches(ServerExpression langTag, String langRange) {
    return langMatches(langTag, (langRange == null) ? (ServerExpression) null : xs.string(langRange));
  }

  
  @Override
  public ServerExpression langMatches(ServerExpression langTag, ServerExpression langRange) {
    if (langTag == null) {
      throw new IllegalArgumentException("langTag parameter for langMatches() cannot be null");
    }
    if (langRange == null) {
      throw new IllegalArgumentException("langRange parameter for langMatches() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sem", "langMatches", new Object[]{ langTag, langRange });
  }

  
  @Override
  public ServerExpression QNameToIri(ServerExpression arg1) {
    if (arg1 == null) {
      throw new IllegalArgumentException("arg1 parameter for QNameToIri() cannot be null");
    }
    return new IriCallImpl("sem", "QName-to-iri", new Object[]{ arg1 });
  }

  
  @Override
  public ServerExpression random() {
    return new XsExprImpl.DoubleCallImpl("sem", "random", new Object[]{  });
  }

  
  @Override
  public SemStoreExpr rulesetStore(String locations) {
    return rulesetStore((locations == null) ? (XsStringVal) null : xs.string(locations));
  }

  
  @Override
  public SemStoreExpr rulesetStore(ServerExpression locations) {
    return new StoreCallImpl("sem", "ruleset-store", new Object[]{ locations });
  }

  
  @Override
  public SemStoreExpr rulesetStore(String locations, SemStoreExpr... store) {
    return rulesetStore((locations == null) ? (XsStringVal) null : xs.string(locations), new StoreSeqListImpl(store));
  }

  
  @Override
  public SemStoreExpr rulesetStore(ServerExpression locations, ServerExpression store) {
    return new StoreCallImpl("sem", "ruleset-store", new Object[]{ locations, store });
  }

  
  @Override
  public SemStoreExpr rulesetStore(String locations, ServerExpression store, String options) {
    return rulesetStore((locations == null) ? (XsStringVal) null : xs.string(locations), store, (options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public SemStoreExpr rulesetStore(ServerExpression locations, ServerExpression store, ServerExpression options) {
    return new StoreCallImpl("sem", "ruleset-store", new Object[]{ locations, store, options });
  }

  
  @Override
  public ServerExpression sameTerm(ServerExpression a, String b) {
    return sameTerm(a, (b == null) ? (ServerExpression) null : xs.string(b));
  }

  
  @Override
  public ServerExpression sameTerm(ServerExpression a, ServerExpression b) {
    return new XsExprImpl.BooleanCallImpl("sem", "sameTerm", new Object[]{ a, b });
  }

  
  @Override
  public SemStoreExpr store() {
    return new StoreCallImpl("sem", "store", new Object[]{  });
  }

  
  @Override
  public SemStoreExpr store(String options) {
    return store((options == null) ? (XsStringVal) null : xs.string(options));
  }

  
  @Override
  public SemStoreExpr store(ServerExpression options) {
    return new StoreCallImpl("sem", "store", new Object[]{ options });
  }

  
  @Override
  public SemStoreExpr store(String options, ServerExpression query) {
    return store((options == null) ? (XsStringVal) null : xs.string(options), query);
  }

  
  @Override
  public SemStoreExpr store(ServerExpression options, ServerExpression query) {
    return new StoreCallImpl("sem", "store", new Object[]{ options, query });
  }

  
  @Override
  public ServerExpression timezoneString(ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for timezoneString() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sem", "timezone-string", new Object[]{ value });
  }

  
  @Override
  public ServerExpression typedLiteral(ServerExpression value, String datatype) {
    return typedLiteral(value, (datatype == null) ? (ServerExpression) null : iri(datatype));
  }

  
  @Override
  public ServerExpression typedLiteral(ServerExpression value, ServerExpression datatype) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for typedLiteral() cannot be null");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("datatype parameter for typedLiteral() cannot be null");
    }
    return new XsExprImpl.AnyAtomicTypeCallImpl("sem", "typed-literal", new Object[]{ value, datatype });
  }

  
  @Override
  public ServerExpression unknown(ServerExpression string, String datatype) {
    return unknown(string, (datatype == null) ? (ServerExpression) null : iri(datatype));
  }

  
  @Override
  public ServerExpression unknown(ServerExpression string, ServerExpression datatype) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for unknown() cannot be null");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("datatype parameter for unknown() cannot be null");
    }
    return new UnknownCallImpl("sem", "unknown", new Object[]{ string, datatype });
  }

  
  @Override
  public ServerExpression unknownDatatype(ServerExpression val) {
    if (val == null) {
      throw new IllegalArgumentException("val parameter for unknownDatatype() cannot be null");
    }
    return new IriCallImpl("sem", "unknown-datatype", new Object[]{ val });
  }

  
  @Override
  public ServerExpression uuid() {
    return new IriCallImpl("sem", "uuid", new Object[]{  });
  }

  
  @Override
  public ServerExpression uuidString() {
    return new XsExprImpl.StringCallImpl("sem", "uuid-string", new Object[]{  });
  }

  static class BlankSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    BlankSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class BlankCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    BlankCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  static class InvalidSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    InvalidSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class InvalidCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    InvalidCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  static class IriSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IriSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class IriCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IriCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public SemStoreSeqExpr storeSeq(SemStoreExpr... items) {
    return new StoreSeqListImpl(items);
  }
  static class StoreSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements SemStoreSeqExpr {
    StoreSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class StoreSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements SemStoreSeqExpr {
    StoreSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class StoreCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements SemStoreExpr {
    StoreCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  static class UnknownSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnknownSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnknownCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnknownCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
