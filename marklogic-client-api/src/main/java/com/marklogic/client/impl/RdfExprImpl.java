/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.RdfExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class RdfExprImpl extends RdfValueImpl implements RdfExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static RdfExprImpl rdf = new RdfExprImpl();

  RdfExprImpl() {
  }


  @Override
  public ServerExpression langString(ServerExpression string, String lang) {
    return langString(string, (lang == null) ? (ServerExpression) null : xs.string(lang));
  }


  @Override
  public ServerExpression langString(ServerExpression string, ServerExpression lang) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for langString() cannot be null");
    }
    if (lang == null) {
      throw new IllegalArgumentException("lang parameter for langString() cannot be null");
    }
    return new LangStringCallImpl("rdf", "langString", new Object[]{ string, lang });
  }


  @Override
  public ServerExpression langStringLanguage(ServerExpression val) {
    if (val == null) {
      throw new IllegalArgumentException("val parameter for langStringLanguage() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("rdf", "langString-language", new Object[]{ val });
  }

  static class LangStringSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LangStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LangStringCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LangStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
