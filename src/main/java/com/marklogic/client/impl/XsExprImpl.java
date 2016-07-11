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

import com.marklogic.client.expression.Xs;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class XsExprImpl extends XsValueImpl implements Xs {
     @Override
        public Xs.AnyURIExpr anyURI(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.AnyURICallImpl("xs", "anyURI", new Object[]{ arg1 });
    }
    @Override
        public Xs.Base64BinaryExpr base64Binary(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.Base64BinaryCallImpl("xs", "base64Binary", new Object[]{ arg1 });
    }
    @Override
        public Xs.BooleanExpr booleanExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.BooleanCallImpl("xs", "boolean", new Object[]{ arg1 });
    }
    @Override
        public Xs.ByteExpr byteExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.ByteCallImpl("xs", "byte", new Object[]{ arg1 });
    }
    @Override
        public Xs.DateExpr date(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DateCallImpl("xs", "date", new Object[]{ arg1 });
    }
    @Override
        public Xs.DateTimeExpr dateTime(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DateTimeCallImpl("xs", "dateTime", new Object[]{ arg1 });
    }
    @Override
        public Xs.DayTimeDurationExpr dayTimeDuration(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DayTimeDurationCallImpl("xs", "dayTimeDuration", new Object[]{ arg1 });
    }
    @Override
        public Xs.DecimalExpr decimal(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DecimalCallImpl("xs", "decimal", new Object[]{ arg1 });
    }
    @Override
        public Xs.DoubleExpr doubleExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DoubleCallImpl("xs", "double", new Object[]{ arg1 });
    }
    @Override
        public Xs.DurationExpr duration(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.DurationCallImpl("xs", "duration", new Object[]{ arg1 });
    }
    @Override
        public Xs.FloatExpr floatExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.FloatCallImpl("xs", "float", new Object[]{ arg1 });
    }
    @Override
        public Xs.GDayExpr gDay(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.GDayCallImpl("xs", "gDay", new Object[]{ arg1 });
    }
    @Override
        public Xs.GMonthExpr gMonth(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.GMonthCallImpl("xs", "gMonth", new Object[]{ arg1 });
    }
    @Override
        public Xs.GMonthDayExpr gMonthDay(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.GMonthDayCallImpl("xs", "gMonthDay", new Object[]{ arg1 });
    }
    @Override
        public Xs.GYearExpr gYear(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.GYearCallImpl("xs", "gYear", new Object[]{ arg1 });
    }
    @Override
        public Xs.GYearMonthExpr gYearMonth(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.GYearMonthCallImpl("xs", "gYearMonth", new Object[]{ arg1 });
    }
    @Override
        public Xs.HexBinaryExpr hexBinary(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.HexBinaryCallImpl("xs", "hexBinary", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntExpr intExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.IntCallImpl("xs", "int", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr integer(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("xs", "integer", new Object[]{ arg1 });
    }
    @Override
        public Xs.LanguageExpr language(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.LanguageCallImpl("xs", "language", new Object[]{ arg1 });
    }
    @Override
        public Xs.LongExpr longExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.LongCallImpl("xs", "long", new Object[]{ arg1 });
    }
    @Override
        public Xs.NameExpr Name(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NameCallImpl("xs", "Name", new Object[]{ arg1 });
    }
    @Override
        public Xs.NCNameExpr NCName(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NCNameCallImpl("xs", "NCName", new Object[]{ arg1 });
    }
    @Override
        public Xs.NMTOKENExpr NMTOKEN(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NMTOKENCallImpl("xs", "NMTOKEN", new Object[]{ arg1 });
    }
    @Override
        public Xs.NegativeIntegerExpr negativeInteger(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NegativeIntegerCallImpl("xs", "negativeInteger", new Object[]{ arg1 });
    }
    @Override
        public Xs.NonNegativeIntegerExpr nonNegativeInteger(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NonNegativeIntegerCallImpl("xs", "nonNegativeInteger", new Object[]{ arg1 });
    }
    @Override
        public Xs.NonPositiveIntegerExpr nonPositiveInteger(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NonPositiveIntegerCallImpl("xs", "nonPositiveInteger", new Object[]{ arg1 });
    }
    @Override
        public Xs.NormalizedStringExpr normalizedString(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NormalizedStringCallImpl("xs", "normalizedString", new Object[]{ arg1 });
    }
    @Override
        public Xs.NumericExpr numeric(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.NumericCallImpl("xs", "numeric", new Object[]{ arg1 });
    }
    @Override
        public Xs.PositiveIntegerExpr positiveInteger(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.PositiveIntegerCallImpl("xs", "positiveInteger", new Object[]{ arg1 });
    }
    @Override
        public Xs.QNameExpr QName(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.QNameCallImpl("xs", "QName", new Object[]{ arg1 });
    }
    @Override
        public Xs.ShortExpr shortExpr(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.ShortCallImpl("xs", "short", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr string(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.StringCallImpl("xs", "string", new Object[]{ arg1 });
    }
    @Override
        public Xs.TimeExpr time(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.TimeCallImpl("xs", "time", new Object[]{ arg1 });
    }
    @Override
        public Xs.TokenExpr token(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.TokenCallImpl("xs", "token", new Object[]{ arg1 });
    }
    @Override
        public Xs.UnsignedByteExpr unsignedByte(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.UnsignedByteCallImpl("xs", "unsignedByte", new Object[]{ arg1 });
    }
    @Override
        public Xs.UnsignedIntExpr unsignedInt(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.UnsignedIntCallImpl("xs", "unsignedInt", new Object[]{ arg1 });
    }
    @Override
        public Xs.UnsignedLongExpr unsignedLong(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.UnsignedLongCallImpl("xs", "unsignedLong", new Object[]{ arg1 });
    }
    @Override
        public Xs.UnsignedShortExpr unsignedShort(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.UnsignedShortCallImpl("xs", "unsignedShort", new Object[]{ arg1 });
    }
    @Override
        public Xs.UntypedAtomicExpr untypedAtomic(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.UntypedAtomicCallImpl("xs", "untypedAtomic", new Object[]{ arg1 });
    }
    @Override
        public Xs.YearMonthDurationExpr yearMonthDuration(Xs.AnyAtomicTypeExpr arg1) {
        return new XsExprImpl.YearMonthDurationCallImpl("xs", "yearMonthDuration", new Object[]{ arg1 });
    }     @Override
    public Xs.AnyAtomicTypeSeqExpr anyAtomicType(Xs.AnyAtomicTypeExpr... items) {
        return new XsExprImpl.AnyAtomicTypeSeqListImpl(items);
    }
     @Override
    public Xs.AnySimpleTypeSeqExpr anySimpleType(Xs.AnySimpleTypeExpr... items) {
        return new XsExprImpl.AnySimpleTypeSeqListImpl(items);
    }
     @Override
    public Xs.AnyURISeqExpr anyURI(Xs.AnyURIExpr... items) {
        return new XsExprImpl.AnyURISeqListImpl(items);
    }
     @Override
    public Xs.Base64BinarySeqExpr base64Binary(Xs.Base64BinaryExpr... items) {
        return new XsExprImpl.Base64BinarySeqListImpl(items);
    }
     @Override
    public Xs.BooleanSeqExpr booleanExpr(Xs.BooleanExpr... items) {
        return new XsExprImpl.BooleanSeqListImpl(items);
    }
     @Override
    public Xs.ByteSeqExpr byteExpr(Xs.ByteExpr... items) {
        return new XsExprImpl.ByteSeqListImpl(items);
    }
     @Override
    public Xs.DateSeqExpr date(Xs.DateExpr... items) {
        return new XsExprImpl.DateSeqListImpl(items);
    }
     @Override
    public Xs.DateTimeSeqExpr dateTime(Xs.DateTimeExpr... items) {
        return new XsExprImpl.DateTimeSeqListImpl(items);
    }
     @Override
    public Xs.DayTimeDurationSeqExpr dayTimeDuration(Xs.DayTimeDurationExpr... items) {
        return new XsExprImpl.DayTimeDurationSeqListImpl(items);
    }
     @Override
    public Xs.DecimalSeqExpr decimal(Xs.DecimalExpr... items) {
        return new XsExprImpl.DecimalSeqListImpl(items);
    }
     @Override
    public Xs.DoubleSeqExpr doubleExpr(Xs.DoubleExpr... items) {
        return new XsExprImpl.DoubleSeqListImpl(items);
    }
     @Override
    public Xs.DurationSeqExpr duration(Xs.DurationExpr... items) {
        return new XsExprImpl.DurationSeqListImpl(items);
    }
     @Override
    public Xs.FloatSeqExpr floatExpr(Xs.FloatExpr... items) {
        return new XsExprImpl.FloatSeqListImpl(items);
    }
     @Override
    public Xs.GDaySeqExpr gDay(Xs.GDayExpr... items) {
        return new XsExprImpl.GDaySeqListImpl(items);
    }
     @Override
    public Xs.GMonthSeqExpr gMonth(Xs.GMonthExpr... items) {
        return new XsExprImpl.GMonthSeqListImpl(items);
    }
     @Override
    public Xs.GMonthDaySeqExpr gMonthDay(Xs.GMonthDayExpr... items) {
        return new XsExprImpl.GMonthDaySeqListImpl(items);
    }
     @Override
    public Xs.GYearSeqExpr gYear(Xs.GYearExpr... items) {
        return new XsExprImpl.GYearSeqListImpl(items);
    }
     @Override
    public Xs.GYearMonthSeqExpr gYearMonth(Xs.GYearMonthExpr... items) {
        return new XsExprImpl.GYearMonthSeqListImpl(items);
    }
     @Override
    public Xs.HexBinarySeqExpr hexBinary(Xs.HexBinaryExpr... items) {
        return new XsExprImpl.HexBinarySeqListImpl(items);
    }
     @Override
    public Xs.IntegerSeqExpr integer(Xs.IntegerExpr... items) {
        return new XsExprImpl.IntegerSeqListImpl(items);
    }
     @Override
    public Xs.IntSeqExpr intExpr(Xs.IntExpr... items) {
        return new XsExprImpl.IntSeqListImpl(items);
    }
     @Override
    public Xs.LanguageSeqExpr language(Xs.LanguageExpr... items) {
        return new XsExprImpl.LanguageSeqListImpl(items);
    }
     @Override
    public Xs.LongSeqExpr longExpr(Xs.LongExpr... items) {
        return new XsExprImpl.LongSeqListImpl(items);
    }
     @Override
    public Xs.NameSeqExpr name(Xs.NameExpr... items) {
        return new XsExprImpl.NameSeqListImpl(items);
    }
     @Override
    public Xs.NCNameSeqExpr nCName(Xs.NCNameExpr... items) {
        return new XsExprImpl.NCNameSeqListImpl(items);
    }
     @Override
    public Xs.NegativeIntegerSeqExpr negativeInteger(Xs.NegativeIntegerExpr... items) {
        return new XsExprImpl.NegativeIntegerSeqListImpl(items);
    }
     @Override
    public Xs.NMTOKENSeqExpr nMTOKEN(Xs.NMTOKENExpr... items) {
        return new XsExprImpl.NMTOKENSeqListImpl(items);
    }
     @Override
    public Xs.NonNegativeIntegerSeqExpr nonNegativeInteger(Xs.NonNegativeIntegerExpr... items) {
        return new XsExprImpl.NonNegativeIntegerSeqListImpl(items);
    }
     @Override
    public Xs.NonPositiveIntegerSeqExpr nonPositiveInteger(Xs.NonPositiveIntegerExpr... items) {
        return new XsExprImpl.NonPositiveIntegerSeqListImpl(items);
    }
     @Override
    public Xs.NormalizedStringSeqExpr normalizedString(Xs.NormalizedStringExpr... items) {
        return new XsExprImpl.NormalizedStringSeqListImpl(items);
    }
     @Override
    public Xs.NumericSeqExpr numeric(Xs.NumericExpr... items) {
        return new XsExprImpl.NumericSeqListImpl(items);
    }
     @Override
    public Xs.PositiveIntegerSeqExpr positiveInteger(Xs.PositiveIntegerExpr... items) {
        return new XsExprImpl.PositiveIntegerSeqListImpl(items);
    }
     @Override
    public Xs.QNameSeqExpr qName(Xs.QNameExpr... items) {
        return new XsExprImpl.QNameSeqListImpl(items);
    }
     @Override
    public Xs.ShortSeqExpr shortExpr(Xs.ShortExpr... items) {
        return new XsExprImpl.ShortSeqListImpl(items);
    }
     @Override
    public Xs.StringSeqExpr string(Xs.StringExpr... items) {
        return new XsExprImpl.StringSeqListImpl(items);
    }
     @Override
    public Xs.TimeSeqExpr time(Xs.TimeExpr... items) {
        return new XsExprImpl.TimeSeqListImpl(items);
    }
     @Override
    public Xs.TokenSeqExpr token(Xs.TokenExpr... items) {
        return new XsExprImpl.TokenSeqListImpl(items);
    }
     @Override
    public Xs.UnsignedByteSeqExpr unsignedByte(Xs.UnsignedByteExpr... items) {
        return new XsExprImpl.UnsignedByteSeqListImpl(items);
    }
     @Override
    public Xs.UnsignedIntSeqExpr unsignedInt(Xs.UnsignedIntExpr... items) {
        return new XsExprImpl.UnsignedIntSeqListImpl(items);
    }
     @Override
    public Xs.UnsignedLongSeqExpr unsignedLong(Xs.UnsignedLongExpr... items) {
        return new XsExprImpl.UnsignedLongSeqListImpl(items);
    }
     @Override
    public Xs.UnsignedShortSeqExpr unsignedShort(Xs.UnsignedShortExpr... items) {
        return new XsExprImpl.UnsignedShortSeqListImpl(items);
    }
     @Override
    public Xs.UntypedAtomicSeqExpr untypedAtomic(Xs.UntypedAtomicExpr... items) {
        return new XsExprImpl.UntypedAtomicSeqListImpl(items);
    }
     @Override
    public Xs.YearMonthDurationSeqExpr yearMonthDuration(Xs.YearMonthDurationExpr... items) {
        return new XsExprImpl.YearMonthDurationSeqListImpl(items);
    }
        static class AnyAtomicTypeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements AnyAtomicTypeSeqExpr {
            AnyAtomicTypeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class AnyAtomicTypeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnyAtomicTypeSeqExpr {
            AnyAtomicTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class AnyAtomicTypeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnyAtomicTypeExpr {
            AnyAtomicTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class AnySimpleTypeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements AnySimpleTypeSeqExpr {
            AnySimpleTypeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class AnySimpleTypeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnySimpleTypeSeqExpr {
            AnySimpleTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class AnySimpleTypeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnySimpleTypeExpr {
            AnySimpleTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class AnyURISeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements AnyURISeqExpr {
            AnyURISeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class AnyURISeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnyURISeqExpr {
            AnyURISeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class AnyURICallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements AnyURIExpr {
            AnyURICallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class Base64BinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements Base64BinarySeqExpr {
            Base64BinarySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class Base64BinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements Base64BinarySeqExpr {
            Base64BinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class Base64BinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements Base64BinaryExpr {
            Base64BinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class BooleanSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements BooleanSeqExpr {
            BooleanSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class BooleanSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BooleanSeqExpr {
            BooleanSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class BooleanCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements BooleanExpr {
            BooleanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class ByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements ByteSeqExpr {
            ByteSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class ByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ByteSeqExpr {
            ByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class ByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ByteExpr {
            ByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DateSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DateSeqExpr {
            DateSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DateSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DateSeqExpr {
            DateSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DateCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DateExpr {
            DateCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DateTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DateTimeSeqExpr {
            DateTimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DateTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DateTimeSeqExpr {
            DateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DateTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DateTimeExpr {
            DateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DayTimeDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DayTimeDurationSeqExpr {
            DayTimeDurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DayTimeDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DayTimeDurationSeqExpr {
            DayTimeDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DayTimeDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DayTimeDurationExpr {
            DayTimeDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DecimalSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DecimalSeqExpr {
            DecimalSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DecimalSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DecimalSeqExpr {
            DecimalSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DecimalCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DecimalExpr {
            DecimalCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DoubleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DoubleSeqExpr {
            DoubleSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DoubleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DoubleSeqExpr {
            DoubleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DoubleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DoubleExpr {
            DoubleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class DurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements DurationSeqExpr {
            DurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class DurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DurationSeqExpr {
            DurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class DurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements DurationExpr {
            DurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class FloatSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements FloatSeqExpr {
            FloatSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class FloatSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements FloatSeqExpr {
            FloatSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class FloatCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements FloatExpr {
            FloatCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GDaySeqExpr {
            GDaySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GDaySeqExpr {
            GDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GDayExpr {
            GDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GMonthSeqExpr {
            GMonthSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GMonthSeqExpr {
            GMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GMonthExpr {
            GMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GMonthDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GMonthDaySeqExpr {
            GMonthDaySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GMonthDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GMonthDaySeqExpr {
            GMonthDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GMonthDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GMonthDayExpr {
            GMonthDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GYearSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GYearSeqExpr {
            GYearSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GYearSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GYearSeqExpr {
            GYearSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GYearCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GYearExpr {
            GYearCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GYearMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GYearMonthSeqExpr {
            GYearMonthSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GYearMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GYearMonthSeqExpr {
            GYearMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GYearMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GYearMonthExpr {
            GYearMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class HexBinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements HexBinarySeqExpr {
            HexBinarySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class HexBinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements HexBinarySeqExpr {
            HexBinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class HexBinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements HexBinaryExpr {
            HexBinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class IntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements IntSeqExpr {
            IntSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class IntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IntSeqExpr {
            IntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class IntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IntExpr {
            IntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class IntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements IntegerSeqExpr {
            IntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class IntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IntegerSeqExpr {
            IntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class IntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements IntegerExpr {
            IntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class LanguageSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements LanguageSeqExpr {
            LanguageSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class LanguageSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LanguageSeqExpr {
            LanguageSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class LanguageCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LanguageExpr {
            LanguageCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class LongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements LongSeqExpr {
            LongSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class LongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LongSeqExpr {
            LongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class LongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LongExpr {
            LongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NameSeqExpr {
            NameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NameSeqExpr {
            NameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NameExpr {
            NameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NCNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NCNameSeqExpr {
            NCNameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NCNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NCNameSeqExpr {
            NCNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NCNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NCNameExpr {
            NCNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NegativeIntegerSeqExpr {
            NegativeIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NegativeIntegerSeqExpr {
            NegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NegativeIntegerExpr {
            NegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NMTOKENSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NMTOKENSeqExpr {
            NMTOKENSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NMTOKENSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NMTOKENSeqExpr {
            NMTOKENSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NMTOKENCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NMTOKENExpr {
            NMTOKENCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NonNegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NonNegativeIntegerSeqExpr {
            NonNegativeIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NonNegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NonNegativeIntegerSeqExpr {
            NonNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NonNegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NonNegativeIntegerExpr {
            NonNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NonPositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NonPositiveIntegerSeqExpr {
            NonPositiveIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NonPositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NonPositiveIntegerSeqExpr {
            NonPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NonPositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NonPositiveIntegerExpr {
            NonPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NormalizedStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NormalizedStringSeqExpr {
            NormalizedStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NormalizedStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NormalizedStringSeqExpr {
            NormalizedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NormalizedStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NormalizedStringExpr {
            NormalizedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class NumericSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements NumericSeqExpr {
            NumericSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class NumericSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NumericSeqExpr {
            NumericSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class NumericCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements NumericExpr {
            NumericCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class PositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements PositiveIntegerSeqExpr {
            PositiveIntegerSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class PositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PositiveIntegerSeqExpr {
            PositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class PositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements PositiveIntegerExpr {
            PositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class QNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements QNameSeqExpr {
            QNameSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class QNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements QNameSeqExpr {
            QNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class QNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements QNameExpr {
            QNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class ShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements ShortSeqExpr {
            ShortSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class ShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ShortSeqExpr {
            ShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class ShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ShortExpr {
            ShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class StringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements StringSeqExpr {
            StringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class StringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements StringSeqExpr {
            StringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class StringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements StringExpr {
            StringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class TimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements TimeSeqExpr {
            TimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class TimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TimeSeqExpr {
            TimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class TimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TimeExpr {
            TimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class TokenSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements TokenSeqExpr {
            TokenSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class TokenSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TokenSeqExpr {
            TokenSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class TokenCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements TokenExpr {
            TokenCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UnsignedByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedByteSeqExpr {
            UnsignedByteSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UnsignedByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedByteSeqExpr {
            UnsignedByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UnsignedByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedByteExpr {
            UnsignedByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UnsignedIntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedIntSeqExpr {
            UnsignedIntSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UnsignedIntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedIntSeqExpr {
            UnsignedIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UnsignedIntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedIntExpr {
            UnsignedIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UnsignedLongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedLongSeqExpr {
            UnsignedLongSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UnsignedLongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedLongSeqExpr {
            UnsignedLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UnsignedLongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedLongExpr {
            UnsignedLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UnsignedShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedShortSeqExpr {
            UnsignedShortSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UnsignedShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedShortSeqExpr {
            UnsignedShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UnsignedShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UnsignedShortExpr {
            UnsignedShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class UntypedAtomicSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements UntypedAtomicSeqExpr {
            UntypedAtomicSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class UntypedAtomicSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UntypedAtomicSeqExpr {
            UntypedAtomicSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class UntypedAtomicCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements UntypedAtomicExpr {
            UntypedAtomicCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class YearMonthDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements YearMonthDurationSeqExpr {
            YearMonthDurationSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class YearMonthDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements YearMonthDurationSeqExpr {
            YearMonthDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class YearMonthDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements YearMonthDurationExpr {
            YearMonthDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
