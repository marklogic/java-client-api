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

import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.SemBlankExpr;
 import com.marklogic.client.type.SemBlankSeqExpr;
 import com.marklogic.client.type.SemInvalidExpr;
 import com.marklogic.client.type.SemInvalidSeqExpr;
 import com.marklogic.client.type.SemIriExpr;
 import com.marklogic.client.type.SemIriSeqExpr;
 import com.marklogic.client.type.SemUnknownExpr;
 import com.marklogic.client.type.SemUnknownSeqExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class SemExprImpl extends SemValueImpl implements SemExpr {
    private XsExprImpl xs = null;
    public SemExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public SemBlankExpr bnode() {
        return new SemExprImpl.SemBlankCallImpl("sem", "bnode", new Object[]{  });
    }
    @Override
        public SemBlankExpr bnode(XsAnyAtomicTypeExpr value) {
        return new SemExprImpl.SemBlankCallImpl("sem", "bnode", new Object[]{ value });
    }
    @Override
        public ItemSeqExpr coalesce(ItemExpr... parameter1) {
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "coalesce", new Object[] { parameter1 });
    }
    @Override
        public SemIriExpr datatype(XsAnyAtomicTypeExpr value) {
        return new SemExprImpl.SemIriCallImpl("sem", "datatype", new Object[]{ value });
    }
    @Override
        public ItemSeqExpr ifExpr(XsBooleanExpr condition, ItemSeqExpr then, ItemSeqExpr elseExpr) {
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "if", new Object[]{ condition, then, elseExpr });
    }
    @Override
        public SemInvalidExpr invalid(XsStringExpr string, SemIriExpr datatype) {
        return new SemExprImpl.SemInvalidCallImpl("sem", "invalid", new Object[]{ string, datatype });
    }
    @Override
        public SemIriExpr invalidDatatype(SemInvalidExpr val) {
        return new SemExprImpl.SemIriCallImpl("sem", "invalid-datatype", new Object[]{ val });
    }
    @Override
        public SemIriExpr iri(XsAnyAtomicTypeExpr stringIri) {
        return new SemExprImpl.SemIriCallImpl("sem", "iri", new Object[]{ stringIri });
    }
    @Override
        public XsQNameExpr iriToQName(XsStringExpr arg1) {
        return new XsExprImpl.XsQNameCallImpl("sem", "iri-to-QName", new Object[]{ arg1 });
    }
    @Override
        public XsBooleanExpr isBlank(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "isBlank", new Object[]{ value });
    }
    @Override
        public XsBooleanExpr isIRI(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "isIRI", new Object[]{ value });
    }
    @Override
        public XsBooleanExpr isLiteral(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "isLiteral", new Object[]{ value });
    }
    @Override
        public XsBooleanExpr isNumeric(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "isNumeric", new Object[]{ value });
    }
    @Override
        public XsStringExpr lang(XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsStringCallImpl("sem", "lang", new Object[]{ value });
    }
    @Override
        public XsBooleanExpr langMatches(XsStringExpr langTag, String langRange) {
        return langMatches(langTag, xs.string(langRange)); 
    }
    @Override
        public XsBooleanExpr langMatches(XsStringExpr langTag, XsStringExpr langRange) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "langMatches", new Object[]{ langTag, langRange });
    }
    @Override
        public SemIriExpr QNameToIri(XsQNameExpr arg1) {
        return new SemExprImpl.SemIriCallImpl("sem", "QName-to-iri", new Object[]{ arg1 });
    }
    @Override
    public XsDoubleExpr random() {
        return new XsExprImpl.XsDoubleCallImpl("sem", "random", null);
    }
    @Override
        public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, XsAnyAtomicTypeExpr b) {
        return new XsExprImpl.XsBooleanCallImpl("sem", "sameTerm", new Object[]{ a, b });
    }
    @Override
        public XsStringExpr timezoneString(XsDateTimeExpr value) {
        return new XsExprImpl.XsStringCallImpl("sem", "timezone-string", new Object[]{ value });
    }
    @Override
        public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, SemIriExpr datatype) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("sem", "typed-literal", new Object[]{ value, datatype });
    }
    @Override
        public SemUnknownExpr unknown(XsStringExpr string, SemIriExpr datatype) {
        return new SemExprImpl.SemUnknownCallImpl("sem", "unknown", new Object[]{ string, datatype });
    }
    @Override
        public SemIriExpr unknownDatatype(SemUnknownExpr val) {
        return new SemExprImpl.SemIriCallImpl("sem", "unknown-datatype", new Object[]{ val });
    }
    @Override
    public SemIriExpr uuid() {
        return new SemExprImpl.SemIriCallImpl("sem", "uuid", null);
    }
    @Override
    public XsStringExpr uuidString() {
        return new XsExprImpl.XsStringCallImpl("sem", "uuid-string", null);
    }     @Override
    public SemBlankSeqExpr blank(SemBlankExpr... items) {
        return new SemBlankSeqListImpl(items);
    }
     @Override
    public SemInvalidSeqExpr invalid(SemInvalidExpr... items) {
        return new SemInvalidSeqListImpl(items);
    }
     @Override
    public SemIriSeqExpr iri(SemIriExpr... items) {
        return new SemIriSeqListImpl(items);
    }
     @Override
    public SemUnknownSeqExpr unknown(SemUnknownExpr... items) {
        return new SemUnknownSeqListImpl(items);
    }
        static class SemBlankSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankSeqExpr {
            SemBlankSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SemBlankSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankSeqExpr {
            SemBlankSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SemBlankCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankExpr {
            SemBlankCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class SemInvalidSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidSeqExpr {
            SemInvalidSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SemInvalidSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidSeqExpr {
            SemInvalidSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SemInvalidCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidExpr {
            SemInvalidCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class SemIriSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemIriSeqExpr {
            SemIriSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SemIriSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemIriSeqExpr {
            SemIriSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SemIriCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemIriExpr {
            SemIriCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class SemUnknownSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownSeqExpr {
            SemUnknownSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SemUnknownSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownSeqExpr {
            SemUnknownSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SemUnknownCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownExpr {
            SemUnknownCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
