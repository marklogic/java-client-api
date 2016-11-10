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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
 import com.marklogic.client.type.XsAnySimpleTypeExpr;
 import com.marklogic.client.type.XsAnySimpleTypeSeqExpr;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsAnyURISeqExpr;
 import com.marklogic.client.type.XsBase64BinaryExpr;
 import com.marklogic.client.type.XsBase64BinarySeqExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsBooleanSeqExpr;
 import com.marklogic.client.type.XsByteExpr;
 import com.marklogic.client.type.XsByteSeqExpr;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsDateSeqExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsDateTimeSeqExpr;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.XsDayTimeDurationSeqExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.XsDecimalSeqExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDoubleSeqExpr;
 import com.marklogic.client.type.XsDurationExpr;
 import com.marklogic.client.type.XsDurationSeqExpr;
 import com.marklogic.client.type.XsFloatExpr;
 import com.marklogic.client.type.XsFloatSeqExpr;
 import com.marklogic.client.type.XsGDayExpr;
 import com.marklogic.client.type.XsGDaySeqExpr;
 import com.marklogic.client.type.XsGMonthDayExpr;
 import com.marklogic.client.type.XsGMonthDaySeqExpr;
 import com.marklogic.client.type.XsGMonthExpr;
 import com.marklogic.client.type.XsGMonthSeqExpr;
 import com.marklogic.client.type.XsGYearExpr;
 import com.marklogic.client.type.XsGYearMonthExpr;
 import com.marklogic.client.type.XsGYearMonthSeqExpr;
 import com.marklogic.client.type.XsGYearSeqExpr;
 import com.marklogic.client.type.XsHexBinaryExpr;
 import com.marklogic.client.type.XsHexBinarySeqExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsIntegerSeqExpr;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.XsIntSeqExpr;
 import com.marklogic.client.type.XsLanguageExpr;
 import com.marklogic.client.type.XsLanguageSeqExpr;
 import com.marklogic.client.type.XsLongExpr;
 import com.marklogic.client.type.XsLongSeqExpr;
 import com.marklogic.client.type.XsNameExpr;
 import com.marklogic.client.type.XsNameSeqExpr;
 import com.marklogic.client.type.XsNCNameExpr;
 import com.marklogic.client.type.XsNCNameSeqExpr;
 import com.marklogic.client.type.XsNegativeIntegerExpr;
 import com.marklogic.client.type.XsNegativeIntegerSeqExpr;
 import com.marklogic.client.type.XsNMTOKENExpr;
 import com.marklogic.client.type.XsNMTOKENSeqExpr;
 import com.marklogic.client.type.XsNonNegativeIntegerExpr;
 import com.marklogic.client.type.XsNonNegativeIntegerSeqExpr;
 import com.marklogic.client.type.XsNonPositiveIntegerExpr;
 import com.marklogic.client.type.XsNonPositiveIntegerSeqExpr;
 import com.marklogic.client.type.XsNormalizedStringExpr;
 import com.marklogic.client.type.XsNormalizedStringSeqExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsNumericSeqExpr;
 import com.marklogic.client.type.XsPositiveIntegerExpr;
 import com.marklogic.client.type.XsPositiveIntegerSeqExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsQNameSeqExpr;
 import com.marklogic.client.type.XsShortExpr;
 import com.marklogic.client.type.XsShortSeqExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsTimeSeqExpr;
 import com.marklogic.client.type.XsTokenExpr;
 import com.marklogic.client.type.XsTokenSeqExpr;
 import com.marklogic.client.type.XsUnsignedByteExpr;
 import com.marklogic.client.type.XsUnsignedByteSeqExpr;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsUnsignedIntSeqExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsUnsignedLongSeqExpr;
 import com.marklogic.client.type.XsUnsignedShortExpr;
 import com.marklogic.client.type.XsUnsignedShortSeqExpr;
 import com.marklogic.client.type.XsUntypedAtomicExpr;
 import com.marklogic.client.type.XsUntypedAtomicSeqExpr;
 import com.marklogic.client.type.XsYearMonthDurationExpr;
 import com.marklogic.client.type.XsYearMonthDurationSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class XsExprImpl extends XsValueImpl implements XsExpr {
     @Override
        public XsAnyURIExpr anyURI(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsAnyURICallImpl("xs", "anyURI", new Object[]{ arg1 });
    }
    @Override
        public XsBase64BinaryExpr base64Binary(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsBase64BinaryCallImpl("xs", "base64Binary", new Object[]{ arg1 });
    }
    @Override
        public XsBooleanExpr booleanExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsBooleanCallImpl("xs", "boolean", new Object[]{ arg1 });
    }
    @Override
        public XsByteExpr byteExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsByteCallImpl("xs", "byte", new Object[]{ arg1 });
    }
    @Override
        public XsDateExpr date(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDateCallImpl("xs", "date", new Object[]{ arg1 });
    }
    @Override
        public XsDateTimeExpr dateTime(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDateTimeCallImpl("xs", "dateTime", new Object[]{ arg1 });
    }
    @Override
        public XsDayTimeDurationExpr dayTimeDuration(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDayTimeDurationCallImpl("xs", "dayTimeDuration", new Object[]{ arg1 });
    }
    @Override
        public XsDecimalExpr decimal(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDecimalCallImpl("xs", "decimal", new Object[]{ arg1 });
    }
    @Override
        public XsDoubleExpr doubleExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDoubleCallImpl("xs", "double", new Object[]{ arg1 });
    }
    @Override
        public XsDurationExpr duration(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsDurationCallImpl("xs", "duration", new Object[]{ arg1 });
    }
    @Override
        public XsFloatExpr floatExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsFloatCallImpl("xs", "float", new Object[]{ arg1 });
    }
    @Override
        public XsGDayExpr gDay(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsGDayCallImpl("xs", "gDay", new Object[]{ arg1 });
    }
    @Override
        public XsGMonthExpr gMonth(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsGMonthCallImpl("xs", "gMonth", new Object[]{ arg1 });
    }
    @Override
        public XsGMonthDayExpr gMonthDay(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsGMonthDayCallImpl("xs", "gMonthDay", new Object[]{ arg1 });
    }
    @Override
        public XsGYearExpr gYear(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsGYearCallImpl("xs", "gYear", new Object[]{ arg1 });
    }
    @Override
        public XsGYearMonthExpr gYearMonth(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsGYearMonthCallImpl("xs", "gYearMonth", new Object[]{ arg1 });
    }
    @Override
        public XsHexBinaryExpr hexBinary(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsHexBinaryCallImpl("xs", "hexBinary", new Object[]{ arg1 });
    }
    @Override
        public XsIntExpr intExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsIntCallImpl("xs", "int", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr integer(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("xs", "integer", new Object[]{ arg1 });
    }
    @Override
        public XsLanguageExpr language(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsLanguageCallImpl("xs", "language", new Object[]{ arg1 });
    }
    @Override
        public XsLongExpr longExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsLongCallImpl("xs", "long", new Object[]{ arg1 });
    }
    @Override
        public XsNameExpr Name(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNameCallImpl("xs", "Name", new Object[]{ arg1 });
    }
    @Override
        public XsNCNameExpr NCName(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNCNameCallImpl("xs", "NCName", new Object[]{ arg1 });
    }
    @Override
        public XsNMTOKENExpr NMTOKEN(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNMTOKENCallImpl("xs", "NMTOKEN", new Object[]{ arg1 });
    }
    @Override
        public XsNegativeIntegerExpr negativeInteger(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNegativeIntegerCallImpl("xs", "negativeInteger", new Object[]{ arg1 });
    }
    @Override
        public XsNonNegativeIntegerExpr nonNegativeInteger(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNonNegativeIntegerCallImpl("xs", "nonNegativeInteger", new Object[]{ arg1 });
    }
    @Override
        public XsNonPositiveIntegerExpr nonPositiveInteger(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNonPositiveIntegerCallImpl("xs", "nonPositiveInteger", new Object[]{ arg1 });
    }
    @Override
        public XsNormalizedStringExpr normalizedString(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNormalizedStringCallImpl("xs", "normalizedString", new Object[]{ arg1 });
    }
    @Override
        public XsNumericExpr numeric(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsNumericCallImpl("xs", "numeric", new Object[]{ arg1 });
    }
    @Override
        public XsPositiveIntegerExpr positiveInteger(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsPositiveIntegerCallImpl("xs", "positiveInteger", new Object[]{ arg1 });
    }
    @Override
        public XsQNameExpr QName(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsQNameCallImpl("xs", "QName", new Object[]{ arg1 });
    }
    @Override
        public XsShortExpr shortExpr(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsShortCallImpl("xs", "short", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr string(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("xs", "string", new Object[]{ arg1 });
    }
    @Override
        public XsTimeExpr time(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsTimeCallImpl("xs", "time", new Object[]{ arg1 });
    }
    @Override
        public XsTokenExpr token(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsTokenCallImpl("xs", "token", new Object[]{ arg1 });
    }
    @Override
        public XsUnsignedByteExpr unsignedByte(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsUnsignedByteCallImpl("xs", "unsignedByte", new Object[]{ arg1 });
    }
    @Override
        public XsUnsignedIntExpr unsignedInt(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsUnsignedIntCallImpl("xs", "unsignedInt", new Object[]{ arg1 });
    }
    @Override
        public XsUnsignedLongExpr unsignedLong(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsUnsignedLongCallImpl("xs", "unsignedLong", new Object[]{ arg1 });
    }
    @Override
        public XsUnsignedShortExpr unsignedShort(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsUnsignedShortCallImpl("xs", "unsignedShort", new Object[]{ arg1 });
    }
    @Override
        public XsUntypedAtomicExpr untypedAtomic(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsUntypedAtomicCallImpl("xs", "untypedAtomic", new Object[]{ arg1 });
    }
    @Override
        public XsYearMonthDurationExpr yearMonthDuration(XsAnyAtomicTypeExpr arg1) {
        return new XsExprImpl.XsYearMonthDurationCallImpl("xs", "yearMonthDuration", new Object[]{ arg1 });
    }     @Override
    public XsAnyAtomicTypeSeqExpr anyAtomicType(XsAnyAtomicTypeExpr... items) {
        return new XsAnyAtomicTypeSeqListImpl(items);
    }
     @Override
    public XsAnySimpleTypeSeqExpr anySimpleType(XsAnySimpleTypeExpr... items) {
        return new XsAnySimpleTypeSeqListImpl(items);
    }
     @Override
    public XsAnyURISeqExpr anyURI(XsAnyURIExpr... items) {
        return new XsAnyURISeqListImpl(items);
    }
     @Override
    public XsBase64BinarySeqExpr base64Binary(XsBase64BinaryExpr... items) {
        return new XsBase64BinarySeqListImpl(items);
    }
     @Override
    public XsBooleanSeqExpr booleanExpr(XsBooleanExpr... items) {
        return new XsBooleanSeqListImpl(items);
    }
     @Override
    public XsByteSeqExpr byteExpr(XsByteExpr... items) {
        return new XsByteSeqListImpl(items);
    }
     @Override
    public XsDateSeqExpr date(XsDateExpr... items) {
        return new XsDateSeqListImpl(items);
    }
     @Override
    public XsDateTimeSeqExpr dateTime(XsDateTimeExpr... items) {
        return new XsDateTimeSeqListImpl(items);
    }
     @Override
    public XsDayTimeDurationSeqExpr dayTimeDuration(XsDayTimeDurationExpr... items) {
        return new XsDayTimeDurationSeqListImpl(items);
    }
     @Override
    public XsDecimalSeqExpr decimal(XsDecimalExpr... items) {
        return new XsDecimalSeqListImpl(items);
    }
     @Override
    public XsDoubleSeqExpr doubleExpr(XsDoubleExpr... items) {
        return new XsDoubleSeqListImpl(items);
    }
     @Override
    public XsDurationSeqExpr duration(XsDurationExpr... items) {
        return new XsDurationSeqListImpl(items);
    }
     @Override
    public XsFloatSeqExpr floatExpr(XsFloatExpr... items) {
        return new XsFloatSeqListImpl(items);
    }
     @Override
    public XsGDaySeqExpr gDay(XsGDayExpr... items) {
        return new XsGDaySeqListImpl(items);
    }
     @Override
    public XsGMonthSeqExpr gMonth(XsGMonthExpr... items) {
        return new XsGMonthSeqListImpl(items);
    }
     @Override
    public XsGMonthDaySeqExpr gMonthDay(XsGMonthDayExpr... items) {
        return new XsGMonthDaySeqListImpl(items);
    }
     @Override
    public XsGYearSeqExpr gYear(XsGYearExpr... items) {
        return new XsGYearSeqListImpl(items);
    }
     @Override
    public XsGYearMonthSeqExpr gYearMonth(XsGYearMonthExpr... items) {
        return new XsGYearMonthSeqListImpl(items);
    }
     @Override
    public XsHexBinarySeqExpr hexBinary(XsHexBinaryExpr... items) {
        return new XsHexBinarySeqListImpl(items);
    }
     @Override
    public XsIntegerSeqExpr integer(XsIntegerExpr... items) {
        return new XsIntegerSeqListImpl(items);
    }
     @Override
    public XsIntSeqExpr intExpr(XsIntExpr... items) {
        return new XsIntSeqListImpl(items);
    }
     @Override
    public XsLanguageSeqExpr language(XsLanguageExpr... items) {
        return new XsLanguageSeqListImpl(items);
    }
     @Override
    public XsLongSeqExpr longExpr(XsLongExpr... items) {
        return new XsLongSeqListImpl(items);
    }
     @Override
    public XsNameSeqExpr name(XsNameExpr... items) {
        return new XsNameSeqListImpl(items);
    }
     @Override
    public XsNCNameSeqExpr nCName(XsNCNameExpr... items) {
        return new XsNCNameSeqListImpl(items);
    }
     @Override
    public XsNegativeIntegerSeqExpr negativeInteger(XsNegativeIntegerExpr... items) {
        return new XsNegativeIntegerSeqListImpl(items);
    }
     @Override
    public XsNMTOKENSeqExpr nMTOKEN(XsNMTOKENExpr... items) {
        return new XsNMTOKENSeqListImpl(items);
    }
     @Override
    public XsNonNegativeIntegerSeqExpr nonNegativeInteger(XsNonNegativeIntegerExpr... items) {
        return new XsNonNegativeIntegerSeqListImpl(items);
    }
     @Override
    public XsNonPositiveIntegerSeqExpr nonPositiveInteger(XsNonPositiveIntegerExpr... items) {
        return new XsNonPositiveIntegerSeqListImpl(items);
    }
     @Override
    public XsNormalizedStringSeqExpr normalizedString(XsNormalizedStringExpr... items) {
        return new XsNormalizedStringSeqListImpl(items);
    }
     @Override
    public XsNumericSeqExpr numeric(XsNumericExpr... items) {
        return new XsNumericSeqListImpl(items);
    }
     @Override
    public XsPositiveIntegerSeqExpr positiveInteger(XsPositiveIntegerExpr... items) {
        return new XsPositiveIntegerSeqListImpl(items);
    }
     @Override
    public XsQNameSeqExpr qName(XsQNameExpr... items) {
        return new XsQNameSeqListImpl(items);
    }
     @Override
    public XsShortSeqExpr shortExpr(XsShortExpr... items) {
        return new XsShortSeqListImpl(items);
    }
     @Override
    public XsStringSeqExpr string(XsStringExpr... items) {
        return new XsStringSeqListImpl(items);
    }
     @Override
    public XsTimeSeqExpr time(XsTimeExpr... items) {
        return new XsTimeSeqListImpl(items);
    }
     @Override
    public XsTokenSeqExpr token(XsTokenExpr... items) {
        return new XsTokenSeqListImpl(items);
    }
     @Override
    public XsUnsignedByteSeqExpr unsignedByte(XsUnsignedByteExpr... items) {
        return new XsUnsignedByteSeqListImpl(items);
    }
     @Override
    public XsUnsignedIntSeqExpr unsignedInt(XsUnsignedIntExpr... items) {
        return new XsUnsignedIntSeqListImpl(items);
    }
     @Override
    public XsUnsignedLongSeqExpr unsignedLong(XsUnsignedLongExpr... items) {
        return new XsUnsignedLongSeqListImpl(items);
    }
     @Override
    public XsUnsignedShortSeqExpr unsignedShort(XsUnsignedShortExpr... items) {
        return new XsUnsignedShortSeqListImpl(items);
    }
     @Override
    public XsUntypedAtomicSeqExpr untypedAtomic(XsUntypedAtomicExpr... items) {
        return new XsUntypedAtomicSeqListImpl(items);
    }
     @Override
    public XsYearMonthDurationSeqExpr yearMonthDuration(XsYearMonthDurationExpr... items) {
        return new XsYearMonthDurationSeqListImpl(items);
    }
        static class XsAnyAtomicTypeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeSeqExpr {
            XsAnyAtomicTypeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsAnyAtomicTypeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeSeqExpr {
            XsAnyAtomicTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsAnyAtomicTypeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeExpr {
            XsAnyAtomicTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsAnySimpleTypeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsAnySimpleTypeSeqExpr {
            XsAnySimpleTypeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsAnySimpleTypeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnySimpleTypeSeqExpr {
            XsAnySimpleTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsAnySimpleTypeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnySimpleTypeExpr {
            XsAnySimpleTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsAnyURISeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURISeqExpr {
            XsAnyURISeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsAnyURISeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURISeqExpr {
            XsAnyURISeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsAnyURICallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURIExpr {
            XsAnyURICallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsBase64BinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinarySeqExpr {
            XsBase64BinarySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsBase64BinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinarySeqExpr {
            XsBase64BinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsBase64BinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinaryExpr {
            XsBase64BinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsBooleanSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanSeqExpr {
            XsBooleanSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsBooleanSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanSeqExpr {
            XsBooleanSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsBooleanCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanExpr {
            XsBooleanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsByteSeqExpr {
            XsByteSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsByteSeqExpr {
            XsByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsByteExpr {
            XsByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDateSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDateSeqExpr {
            XsDateSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDateSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateSeqExpr {
            XsDateSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDateCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateExpr {
            XsDateCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDateTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeSeqExpr {
            XsDateTimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDateTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeSeqExpr {
            XsDateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDateTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeExpr {
            XsDateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDayTimeDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationSeqExpr {
            XsDayTimeDurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDayTimeDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationSeqExpr {
            XsDayTimeDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDayTimeDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationExpr {
            XsDayTimeDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDecimalSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalSeqExpr {
            XsDecimalSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDecimalSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalSeqExpr {
            XsDecimalSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDecimalCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalExpr {
            XsDecimalCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDoubleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleSeqExpr {
            XsDoubleSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDoubleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleSeqExpr {
            XsDoubleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDoubleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleExpr {
            XsDoubleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDurationSeqExpr {
            XsDurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDurationSeqExpr {
            XsDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDurationExpr {
            XsDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsFloatSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatSeqExpr {
            XsFloatSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsFloatSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatSeqExpr {
            XsFloatSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsFloatCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatExpr {
            XsFloatCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsGDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGDaySeqExpr {
            XsGDaySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsGDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGDaySeqExpr {
            XsGDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsGDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGDayExpr {
            XsGDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsGMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthSeqExpr {
            XsGMonthSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsGMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthSeqExpr {
            XsGMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsGMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthExpr {
            XsGMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsGMonthDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDaySeqExpr {
            XsGMonthDaySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsGMonthDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDaySeqExpr {
            XsGMonthDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsGMonthDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDayExpr {
            XsGMonthDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsGYearSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearSeqExpr {
            XsGYearSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsGYearSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearSeqExpr {
            XsGYearSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsGYearCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearExpr {
            XsGYearCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsGYearMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthSeqExpr {
            XsGYearMonthSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsGYearMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthSeqExpr {
            XsGYearMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsGYearMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthExpr {
            XsGYearMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsHexBinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinarySeqExpr {
            XsHexBinarySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsHexBinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinarySeqExpr {
            XsHexBinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsHexBinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinaryExpr {
            XsHexBinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsIntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsIntSeqExpr {
            XsIntSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsIntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntSeqExpr {
            XsIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsIntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntExpr {
            XsIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerSeqExpr {
            XsIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerSeqExpr {
            XsIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerExpr {
            XsIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsLanguageSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageSeqExpr {
            XsLanguageSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsLanguageSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageSeqExpr {
            XsLanguageSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsLanguageCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageExpr {
            XsLanguageCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsLongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsLongSeqExpr {
            XsLongSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsLongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLongSeqExpr {
            XsLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsLongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLongExpr {
            XsLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNameSeqExpr {
            XsNameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNameSeqExpr {
            XsNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNameExpr {
            XsNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNCNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameSeqExpr {
            XsNCNameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNCNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameSeqExpr {
            XsNCNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNCNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameExpr {
            XsNCNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerSeqExpr {
            XsNegativeIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerSeqExpr {
            XsNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerExpr {
            XsNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNMTOKENSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENSeqExpr {
            XsNMTOKENSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNMTOKENSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENSeqExpr {
            XsNMTOKENSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNMTOKENCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENExpr {
            XsNMTOKENCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNonNegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerSeqExpr {
            XsNonNegativeIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNonNegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerSeqExpr {
            XsNonNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNonNegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerExpr {
            XsNonNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNonPositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerSeqExpr {
            XsNonPositiveIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNonPositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerSeqExpr {
            XsNonPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNonPositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerExpr {
            XsNonPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNormalizedStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringSeqExpr {
            XsNormalizedStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNormalizedStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringSeqExpr {
            XsNormalizedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNormalizedStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringExpr {
            XsNormalizedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsNumericSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericSeqExpr {
            XsNumericSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsNumericSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericSeqExpr {
            XsNumericSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsNumericCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericExpr {
            XsNumericCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsPositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerSeqExpr {
            XsPositiveIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsPositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerSeqExpr {
            XsPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsPositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerExpr {
            XsPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsQNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameSeqExpr {
            XsQNameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsQNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameSeqExpr {
            XsQNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsQNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameExpr {
            XsQNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsShortSeqExpr {
            XsShortSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsShortSeqExpr {
            XsShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsShortExpr {
            XsShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsStringSeqExpr {
            XsStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsStringSeqExpr {
            XsStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsStringExpr {
            XsStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeSeqExpr {
            XsTimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeSeqExpr {
            XsTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeExpr {
            XsTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsTokenSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenSeqExpr {
            XsTokenSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsTokenSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenSeqExpr {
            XsTokenSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsTokenCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenExpr {
            XsTokenCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsUnsignedByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteSeqExpr {
            XsUnsignedByteSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsUnsignedByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteSeqExpr {
            XsUnsignedByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsUnsignedByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteExpr {
            XsUnsignedByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsUnsignedIntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntSeqExpr {
            XsUnsignedIntSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsUnsignedIntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntSeqExpr {
            XsUnsignedIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsUnsignedIntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntExpr {
            XsUnsignedIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsUnsignedLongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongSeqExpr {
            XsUnsignedLongSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsUnsignedLongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongSeqExpr {
            XsUnsignedLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsUnsignedLongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongExpr {
            XsUnsignedLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsUnsignedShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortSeqExpr {
            XsUnsignedShortSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsUnsignedShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortSeqExpr {
            XsUnsignedShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsUnsignedShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortExpr {
            XsUnsignedShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsUntypedAtomicSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicSeqExpr {
            XsUntypedAtomicSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsUntypedAtomicSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicSeqExpr {
            XsUntypedAtomicSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsUntypedAtomicCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicExpr {
            XsUntypedAtomicCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class XsYearMonthDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationSeqExpr {
            XsYearMonthDurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class XsYearMonthDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationSeqExpr {
            XsYearMonthDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class XsYearMonthDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationExpr {
            XsYearMonthDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
