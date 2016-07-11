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

// TODO: single import
import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;

import com.marklogic.client.expression.Cts;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.impl.XsExprImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class CtsExprImpl extends CtsQueryExprImpl implements Cts {
    private Xs xs = null;
    public CtsExprImpl(Xs xs) {
        super(xs);
        this.xs = xs;
    }
     @Override
        public Xs.StringSeqExpr stem(String text) {
        return stem(xs.string(text)); 
    }
    @Override
        public Xs.StringSeqExpr stem(Xs.StringExpr text) {
        return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text });
    }
    @Override
        public Xs.StringSeqExpr stem(String text, String language) {
        return stem(xs.string(text), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public Xs.StringSeqExpr stem(Xs.StringExpr text, Xs.StringExpr language) {
        return new XsExprImpl.StringSeqCallImpl("cts", "stem", new Object[]{ text, language });
    }
    @Override
        public Xs.StringSeqExpr tokenize(String text) {
        return tokenize(xs.string(text)); 
    }
    @Override
        public Xs.StringSeqExpr tokenize(Xs.StringExpr text) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text });
    }
    @Override
        public Xs.StringSeqExpr tokenize(String text, String language) {
        return tokenize(xs.string(text), (language == null) ? null : xs.string(language)); 
    }
    @Override
        public Xs.StringSeqExpr tokenize(Xs.StringExpr text, Xs.StringExpr language) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language });
    }
    @Override
        public Xs.StringSeqExpr tokenize(String text, String language, String field) {
        return tokenize(xs.string(text), (language == null) ? null : xs.string(language), (field == null) ? null : xs.string(field)); 
    }
    @Override
        public Xs.StringSeqExpr tokenize(Xs.StringExpr text, Xs.StringExpr language, Xs.StringExpr field) {
        return new XsExprImpl.StringSeqCallImpl("cts", "tokenize", new Object[]{ text, language, field });
    }
}
