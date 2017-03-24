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

import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.type.RdfLangStringExpr;
import com.marklogic.client.type.RdfLangStringSeqExpr;

import com.marklogic.client.expression.RdfExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class RdfExprImpl extends RdfValueImpl implements RdfExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static RdfExprImpl rdf = new RdfExprImpl();

  RdfExprImpl() {
  }


  @Override
  public RdfLangStringExpr langString(XsStringExpr string, String lang) {
    return langString(string, (lang == null) ? (XsStringExpr) null : xs.string(lang));
  }


  @Override
  public RdfLangStringExpr langString(XsStringExpr string, XsStringExpr lang) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for langString() cannot be null");
    }
    if (lang == null) {
      throw new IllegalArgumentException("lang parameter for langString() cannot be null");
    }
    return new LangStringCallImpl("rdf", "langString", new Object[]{ string, lang });
  }


  @Override
  public XsStringExpr langStringLanguage(RdfLangStringExpr val) {
    if (val == null) {
      throw new IllegalArgumentException("val parameter for langStringLanguage() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("rdf", "langString-language", new Object[]{ val });
  }

  @Override
  public RdfLangStringSeqExpr langStringSeq(RdfLangStringExpr... items) {
    return new LangStringSeqListImpl(items);
  }
  static class LangStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringSeqExpr {
    LangStringSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class LangStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringSeqExpr {
    LangStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class LangStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringExpr {
    LangStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }

}
