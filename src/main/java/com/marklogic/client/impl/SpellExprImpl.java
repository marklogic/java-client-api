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

import com.marklogic.client.expression.Spell;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.impl.XsExprImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class SpellExprImpl implements Spell {
    private Xs xs = null;
    public SpellExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Xs.StringSeqExpr doubleMetaphone(String word) {
        return doubleMetaphone(xs.string(word)); 
    }
    @Override
        public Xs.StringSeqExpr doubleMetaphone(Xs.StringExpr word) {
        return new XsExprImpl.StringSeqCallImpl("spell", "double-metaphone", new Object[]{ word });
    }
    @Override
        public Xs.IntegerExpr levenshteinDistance(String str1, String str2) {
        return levenshteinDistance(xs.string(str1), xs.string(str2)); 
    }
    @Override
        public Xs.IntegerExpr levenshteinDistance(Xs.StringExpr str1, Xs.StringExpr str2) {
        return new XsExprImpl.IntegerCallImpl("spell", "levenshtein-distance", new Object[]{ str1, str2 });
    }
    @Override
        public Xs.StringExpr romanize(String string) {
        return romanize(xs.string(string)); 
    }
    @Override
        public Xs.StringExpr romanize(Xs.StringExpr string) {
        return new XsExprImpl.StringCallImpl("spell", "romanize", new Object[]{ string });
    }
}
