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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.RdfExpr;
import com.marklogic.client.type.RdfLangStringExpr;
 import com.marklogic.client.type.RdfLangStringSeqExpr;
 import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class RdfExprImpl extends RdfValueImpl implements RdfExpr {
    private XsExprImpl xs = null;
    public RdfExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public RdfLangStringExpr langString(XsStringExpr string, String lang) {
        return langString(string, xs.string(lang)); 
    }
    @Override
        public RdfLangStringExpr langString(XsStringExpr string, XsStringExpr lang) {
        return new RdfExprImpl.RdfLangStringCallImpl("rdf", "langString", new Object[]{ string, lang });
    }
    @Override
        public XsStringExpr langStringLanguage(RdfLangStringExpr val) {
        return new XsExprImpl.XsStringCallImpl("rdf", "langString-language", new Object[]{ val });
    }     @Override
    public RdfLangStringSeqExpr langString(RdfLangStringExpr... items) {
        return new RdfLangStringSeqListImpl(items);
    }
        static class RdfLangStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringSeqExpr {
            RdfLangStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class RdfLangStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringSeqExpr {
            RdfLangStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class RdfLangStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements RdfLangStringExpr {
            RdfLangStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
