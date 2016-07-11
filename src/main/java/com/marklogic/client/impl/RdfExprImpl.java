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

import com.marklogic.client.expression.Rdf;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.Sql;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.SqlExprImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class RdfExprImpl implements Rdf {
    private Xs xs = null;
    public RdfExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Rdf.LangStringExpr langString(String string, String lang) {
        return langString(xs.string(string), xs.string(lang)); 
    }
    @Override
        public Rdf.LangStringExpr langString(Xs.StringExpr string, Xs.StringExpr lang) {
        return new RdfExprImpl.LangStringCallImpl("rdf", "langString", new Object[]{ string, lang });
    }
    @Override
        public Sql.CollatedStringExpr langStringLanguage(Rdf.LangStringExpr val) {
        return new SqlExprImpl.CollatedStringCallImpl("rdf", "langString-language", new Object[]{ val });
    }     @Override
    public Rdf.LangStringSeqExpr langString(Rdf.LangStringExpr... items) {
        return new RdfExprImpl.LangStringSeqListImpl(items);
    }
        static class LangStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements LangStringSeqExpr {
            LangStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class LangStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LangStringSeqExpr {
            LangStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class LangStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LangStringExpr {
            LangStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
