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

import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.type.SemBlankExpr;
import com.marklogic.client.type.SemBlankSeqExpr;
import com.marklogic.client.type.SemInvalidExpr;
import com.marklogic.client.type.SemInvalidSeqExpr;
import com.marklogic.client.type.SemIriExpr;
import com.marklogic.client.type.SemIriSeqExpr;
import com.marklogic.client.type.SemUnknownExpr;
import com.marklogic.client.type.SemUnknownSeqExpr;

import com.marklogic.client.expression.SemExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class SemExprImpl extends SemValueImpl implements SemExpr {

    final static XsExprImpl xs = XsExprImpl.xs;

    final static SemExprImpl sem = new SemExprImpl();

    SemExprImpl() {
    }

    
    @Override
    public SemBlankExpr bnode() {
        return new BlankCallImpl("sem", "bnode", new Object[]{  });
    }

    
    @Override
    public SemBlankExpr bnode(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for bnode() cannot be null");
        }
        return new BlankCallImpl("sem", "bnode", new Object[]{ value });
    }

    
    @Override
    public ItemSeqExpr coalesce(ItemExpr... parameter1) {
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "coalesce", parameter1);
    }

    
    @Override
    public SemIriExpr datatype(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for datatype() cannot be null");
        }
        return new IriCallImpl("sem", "datatype", new Object[]{ value });
    }

    
    @Override
    public ItemSeqExpr ifExpr(XsBooleanExpr condition, ItemSeqExpr then, ItemSeqExpr elseExpr) {
        if (condition == null) {
            throw new IllegalArgumentException("condition parameter for ifExpr() cannot be null");
        }
        return new BaseTypeImpl.ItemSeqCallImpl("sem", "if", new Object[]{ condition, then, elseExpr });
    }

    
    @Override
    public SemInvalidExpr invalid(XsStringExpr string, String datatype) {
        return invalid(string, (datatype == null) ? (SemIriExpr) null : iri(datatype));
    }

    
    @Override
    public SemInvalidExpr invalid(XsStringExpr string, SemIriExpr datatype) {
        if (string == null) {
            throw new IllegalArgumentException("string parameter for invalid() cannot be null");
        }
        if (datatype == null) {
            throw new IllegalArgumentException("datatype parameter for invalid() cannot be null");
        }
        return new InvalidCallImpl("sem", "invalid", new Object[]{ string, datatype });
    }

    
    @Override
    public SemIriExpr invalidDatatype(SemInvalidExpr val) {
        if (val == null) {
            throw new IllegalArgumentException("val parameter for invalidDatatype() cannot be null");
        }
        return new IriCallImpl("sem", "invalid-datatype", new Object[]{ val });
    }

    
    @Override
    public SemIriExpr iri(XsAnyAtomicTypeExpr stringIri) {
        return new IriCallImpl("sem", "iri", new Object[]{ stringIri });
    }

    
    @Override
    public XsQNameExpr iriToQName(XsStringExpr arg1) {
        if (arg1 == null) {
            throw new IllegalArgumentException("arg1 parameter for iriToQName() cannot be null");
        }
        return new XsExprImpl.QNameCallImpl("sem", "iri-to-QName", new Object[]{ arg1 });
    }

    
    @Override
    public XsBooleanExpr isBlank(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for isBlank() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "isBlank", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr isIRI(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for isIRI() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "isIRI", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr isLiteral(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for isLiteral() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "isLiteral", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr isNumeric(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for isNumeric() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "isNumeric", new Object[]{ value });
    }

    
    @Override
    public XsStringExpr lang(XsAnyAtomicTypeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for lang() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("sem", "lang", new Object[]{ value });
    }

    
    @Override
    public XsBooleanExpr langMatches(XsStringExpr langTag, String langRange) {
        return langMatches(langTag, (langRange == null) ? (XsStringExpr) null : xs.string(langRange));
    }

    
    @Override
    public XsBooleanExpr langMatches(XsStringExpr langTag, XsStringExpr langRange) {
        if (langTag == null) {
            throw new IllegalArgumentException("langTag parameter for langMatches() cannot be null");
        }
        if (langRange == null) {
            throw new IllegalArgumentException("langRange parameter for langMatches() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "langMatches", new Object[]{ langTag, langRange });
    }

    
    @Override
    public SemIriExpr QNameToIri(XsQNameExpr arg1) {
        if (arg1 == null) {
            throw new IllegalArgumentException("arg1 parameter for QNameToIri() cannot be null");
        }
        return new IriCallImpl("sem", "QName-to-iri", new Object[]{ arg1 });
    }

    
    @Override
    public XsDoubleExpr random() {
        return new XsExprImpl.DoubleCallImpl("sem", "random", new Object[]{  });
    }

    
    @Override
    public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, String b) {
        return sameTerm(a, (b == null) ? (XsAnyAtomicTypeExpr) null : xs.string(b));
    }

    
    @Override
    public XsBooleanExpr sameTerm(XsAnyAtomicTypeExpr a, XsAnyAtomicTypeExpr b) {
        if (a == null) {
            throw new IllegalArgumentException("a parameter for sameTerm() cannot be null");
        }
        if (b == null) {
            throw new IllegalArgumentException("b parameter for sameTerm() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("sem", "sameTerm", new Object[]{ a, b });
    }

    
    @Override
    public XsStringExpr timezoneString(XsDateTimeExpr value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for timezoneString() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("sem", "timezone-string", new Object[]{ value });
    }

    
    @Override
    public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, String datatype) {
        return typedLiteral(value, (datatype == null) ? (SemIriExpr) null : iri(datatype));
    }

    
    @Override
    public XsAnyAtomicTypeExpr typedLiteral(XsStringExpr value, SemIriExpr datatype) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter for typedLiteral() cannot be null");
        }
        if (datatype == null) {
            throw new IllegalArgumentException("datatype parameter for typedLiteral() cannot be null");
        }
        return new XsExprImpl.AnyAtomicTypeCallImpl("sem", "typed-literal", new Object[]{ value, datatype });
    }

    
    @Override
    public SemUnknownExpr unknown(XsStringExpr string, String datatype) {
        return unknown(string, (datatype == null) ? (SemIriExpr) null : iri(datatype));
    }

    
    @Override
    public SemUnknownExpr unknown(XsStringExpr string, SemIriExpr datatype) {
        if (string == null) {
            throw new IllegalArgumentException("string parameter for unknown() cannot be null");
        }
        if (datatype == null) {
            throw new IllegalArgumentException("datatype parameter for unknown() cannot be null");
        }
        return new UnknownCallImpl("sem", "unknown", new Object[]{ string, datatype });
    }

    
    @Override
    public SemIriExpr unknownDatatype(SemUnknownExpr val) {
        if (val == null) {
            throw new IllegalArgumentException("val parameter for unknownDatatype() cannot be null");
        }
        return new IriCallImpl("sem", "unknown-datatype", new Object[]{ val });
    }

    
    @Override
    public SemIriExpr uuid() {
        return new IriCallImpl("sem", "uuid", new Object[]{  });
    }

    
    @Override
    public XsStringExpr uuidString() {
        return new XsExprImpl.StringCallImpl("sem", "uuid-string", new Object[]{  });
    }

    @Override
    public SemBlankSeqExpr blankSeq(SemBlankExpr... items) {
        return new BlankSeqListImpl(items);
    }
    static class BlankSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankSeqExpr {
        BlankSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class BlankSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankSeqExpr {
        BlankSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class BlankCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemBlankExpr {
        BlankCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public SemInvalidSeqExpr invalidSeq(SemInvalidExpr... items) {
        return new InvalidSeqListImpl(items);
    }
    static class InvalidSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidSeqExpr {
        InvalidSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class InvalidSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidSeqExpr {
        InvalidSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class InvalidCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemInvalidExpr {
        InvalidCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public SemIriSeqExpr iriSeq(SemIriExpr... items) {
        return new IriSeqListImpl(items);
    }
    static class IriSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemIriSeqExpr {
        IriSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class IriSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemIriSeqExpr {
        IriSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class IriCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemIriExpr {
        IriCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
 
    @Override
    public SemUnknownSeqExpr unknownSeq(SemUnknownExpr... items) {
        return new UnknownSeqListImpl(items);
    }
    static class UnknownSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownSeqExpr {
        UnknownSeqListImpl(Object[] items) {
            super(BaseTypeImpl.convertList(items));
        }
    }
    static class UnknownSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownSeqExpr {
        UnknownSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }
    static class UnknownCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SemUnknownExpr {
        UnknownCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
            super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
        }
    }

    }
