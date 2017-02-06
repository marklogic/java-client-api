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

import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;



import com.marklogic.client.expression.SpellExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class SpellExprImpl implements SpellExpr {

    final static XsExprImpl xs = XsExprImpl.xs;

    final static SpellExprImpl spell = new SpellExprImpl();

    SpellExprImpl() {
    }

    
    @Override
    public XsStringSeqExpr doubleMetaphone(XsStringExpr word) {
        return new XsExprImpl.StringSeqCallImpl("spell", "double-metaphone", new Object[]{ word });
    }

    
    @Override
    public XsIntegerExpr levenshteinDistance(XsStringExpr str1, String str2) {
        return levenshteinDistance(str1, (str2 == null) ? (XsStringExpr) null : xs.string(str2));
    }

    
    @Override
    public XsIntegerExpr levenshteinDistance(XsStringExpr str1, XsStringExpr str2) {
        return new XsExprImpl.IntegerCallImpl("spell", "levenshtein-distance", new Object[]{ str1, str2 });
    }

    
    @Override
    public XsStringExpr romanize(XsStringExpr string) {
        return new XsExprImpl.StringCallImpl("spell", "romanize", new Object[]{ string });
    }

    }
