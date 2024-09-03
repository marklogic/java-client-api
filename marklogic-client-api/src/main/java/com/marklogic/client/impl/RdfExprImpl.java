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
