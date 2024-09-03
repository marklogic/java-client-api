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

import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.SpellExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class SpellExprImpl implements SpellExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static SpellExprImpl spell = new SpellExprImpl();

  SpellExprImpl() {
  }

    
  @Override
  public ServerExpression doubleMetaphone(ServerExpression word) {
    if (word == null) {
      throw new IllegalArgumentException("word parameter for doubleMetaphone() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("spell", "double-metaphone", new Object[]{ word });
  }

  
  @Override
  public ServerExpression levenshteinDistance(ServerExpression str1, String str2) {
    return levenshteinDistance(str1, (str2 == null) ? (ServerExpression) null : xs.string(str2));
  }

  
  @Override
  public ServerExpression levenshteinDistance(ServerExpression str1, ServerExpression str2) {
    if (str1 == null) {
      throw new IllegalArgumentException("str1 parameter for levenshteinDistance() cannot be null");
    }
    if (str2 == null) {
      throw new IllegalArgumentException("str2 parameter for levenshteinDistance() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("spell", "levenshtein-distance", new Object[]{ str1, str2 });
  }

  
  @Override
  public ServerExpression romanize(ServerExpression string) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for romanize() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("spell", "romanize", new Object[]{ string });
  }

  }
