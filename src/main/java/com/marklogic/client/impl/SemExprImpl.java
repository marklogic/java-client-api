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

import com.marklogic.client.expression.Sem;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class SemExprImpl implements Sem {
    private Xs xs = null;
    public SemExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Sem.BlankExpr bnode() {
        return new SemExprImpl.BlankCallImpl("sem", "bnode", new Object[]{  });
    }
    @Override
        public Sem.BlankExpr bnode(Xs.AnyAtomicTypeExpr value) {
        return new SemExprImpl.BlankCallImpl("sem", "bnode", new Object[]{ value });
    }
    @Override
        public BaseType.ItemSeqExpr coalesce(BaseType.ItemExpr... parameter1) {
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "coalesce", parameter1);
    }
    @Override
        public Sem.IriExpr datatype(Xs.AnyAtomicTypeExpr value) {
        return new SemExprImpl.IriCallImpl("sem", "datatype", new Object[]{ value });
    }
    @Override
    public Sem.IriExpr defaultGraphIri() {
        return new SemExprImpl.IriCallImpl("sem", "default-graph-iri", null);
    }
    @Override
        public BaseType.ItemSeqExpr ifExpr(boolean condition, BaseType.ItemSeqExpr then, BaseType.ItemExpr... elseExpr) {
        return ifExpr(xs.booleanVal(condition), then, BaseTypeImpl.items(elseExpr)); 
    }
    @Override
        public BaseType.ItemSeqExpr ifExpr(Xs.BooleanExpr condition, BaseType.ItemSeqExpr then, BaseType.ItemSeqExpr elseExpr) {
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "if", new Object[]{ condition, then, elseExpr });
    }
    @Override
        public Sem.InvalidExpr invalid(String string, Sem.IriExpr datatype) {
        return invalid(xs.string(string), datatype); 
    }
    @Override
        public Sem.InvalidExpr invalid(Xs.StringExpr string, Sem.IriExpr datatype) {
        return new SemExprImpl.InvalidCallImpl("sem", "invalid", new Object[]{ string, datatype });
    }
    @Override
        public Sem.IriExpr invalidDatatype(Sem.InvalidExpr val) {
        return new SemExprImpl.IriCallImpl("sem", "invalid-datatype", new Object[]{ val });
    }
    @Override
        public Sem.IriExpr iri(Xs.AnyAtomicTypeExpr stringIri) {
        return new SemExprImpl.IriCallImpl("sem", "iri", new Object[]{ stringIri });
    }
    @Override
        public Xs.QNameExpr iriToQName(String arg1) {
        return iriToQName(xs.string(arg1)); 
    }
    @Override
        public Xs.QNameExpr iriToQName(Xs.StringExpr arg1) {
        return new XsExprImpl.QNameCallImpl("sem", "iri-to-QName", new Object[]{ arg1 });
    }
    @Override
        public Xs.BooleanExpr isBlank(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.BooleanCallImpl("sem", "isBlank", new Object[]{ value });
    }
    @Override
        public Xs.BooleanExpr isIRI(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.BooleanCallImpl("sem", "isIRI", new Object[]{ value });
    }
    @Override
        public Xs.BooleanExpr isLiteral(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.BooleanCallImpl("sem", "isLiteral", new Object[]{ value });
    }
    @Override
        public Xs.BooleanExpr isNumeric(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.BooleanCallImpl("sem", "isNumeric", new Object[]{ value });
    }
    @Override
        public Xs.StringExpr lang(Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.StringCallImpl("sem", "lang", new Object[]{ value });
    }
    @Override
        public Xs.BooleanExpr langMatches(String langTag, String langRange) {
        return langMatches(xs.string(langTag), xs.string(langRange)); 
    }
    @Override
        public Xs.BooleanExpr langMatches(Xs.StringExpr langTag, Xs.StringExpr langRange) {
        return new XsExprImpl.BooleanCallImpl("sem", "langMatches", new Object[]{ langTag, langRange });
    }
    @Override
        public Sem.IriExpr QNameToIri(Xs.QNameExpr arg1) {
        return new SemExprImpl.IriCallImpl("sem", "QName-to-iri", new Object[]{ arg1 });
    }
    @Override
    public Xs.DoubleExpr random() {
        return new XsExprImpl.DoubleCallImpl("sem", "random", null);
    }
    @Override
        public Xs.BooleanExpr sameTerm(Xs.AnyAtomicTypeExpr a, Xs.AnyAtomicTypeExpr b) {
        return new XsExprImpl.BooleanCallImpl("sem", "sameTerm", new Object[]{ a, b });
    }
    @Override
        public Xs.StringExpr timezoneString(Xs.DateTimeExpr value) {
        return new XsExprImpl.StringCallImpl("sem", "timezone-string", new Object[]{ value });
    }
    @Override
        public Xs.AnyAtomicTypeExpr typedLiteral(String value, Sem.IriExpr datatype) {
        return typedLiteral(xs.string(value), datatype); 
    }
    @Override
        public Xs.AnyAtomicTypeExpr typedLiteral(Xs.StringExpr value, Sem.IriExpr datatype) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("sem", "typed-literal", new Object[]{ value, datatype });
    }
    @Override
        public Sem.UnknownExpr unknown(String string, Sem.IriExpr datatype) {
        return unknown(xs.string(string), datatype); 
    }
    @Override
        public Sem.UnknownExpr unknown(Xs.StringExpr string, Sem.IriExpr datatype) {
        return new SemExprImpl.UnknownCallImpl("sem", "unknown", new Object[]{ string, datatype });
    }
    @Override
        public Sem.IriExpr unknownDatatype(Sem.UnknownExpr val) {
        return new SemExprImpl.IriCallImpl("sem", "unknown-datatype", new Object[]{ val });
    }
    @Override
    public Sem.IriExpr uuid() {
        return new SemExprImpl.IriCallImpl("sem", "uuid", null);
    }
    @Override
    public Xs.StringExpr uuidString() {
        return new XsExprImpl.StringCallImpl("sem", "uuid-string", null);
    }     @Override
    public Sem.BlankSeqExpr blank(Sem.BlankExpr... items) {
        return new SemExprImpl.BlankSeqListImpl(items);
    }
     @Override
    public Sem.InvalidSeqExpr invalid(Sem.InvalidExpr... items) {
        return new SemExprImpl.InvalidSeqListImpl(items);
    }
     @Override
    public Sem.IriSeqExpr iri(Sem.IriExpr... items) {
        return new SemExprImpl.IriSeqListImpl(items);
    }
     @Override
    public Sem.UnknownSeqExpr unknown(Sem.UnknownExpr... items) {
        return new SemExprImpl.UnknownSeqListImpl(items);
    }
        static class BlankSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements BlankSeqExpr {
            BlankSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class BlankSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BlankSeqExpr {
            BlankSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class BlankCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BlankExpr {
            BlankCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class InvalidSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements InvalidSeqExpr {
            InvalidSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class InvalidSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements InvalidSeqExpr {
            InvalidSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class InvalidCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements InvalidExpr {
            InvalidCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class IriSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements IriSeqExpr {
            IriSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class IriSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IriSeqExpr {
            IriSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class IriCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IriExpr {
            IriCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UnknownSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UnknownSeqExpr {
            UnknownSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UnknownSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnknownSeqExpr {
            UnknownSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UnknownCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnknownExpr {
            UnknownCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
