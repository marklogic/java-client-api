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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;



// IMPORTANT: Do not edit. This file is generated. 
public interface Xs extends XsValue {
    public Xs.AnyURIExpr anyURI(Xs.AnyAtomicTypeExpr arg1);
    public Xs.Base64BinaryExpr base64Binary(Xs.AnyAtomicTypeExpr arg1);
    public Xs.BooleanExpr booleanExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.ByteExpr byteExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DateExpr date(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DateTimeExpr dateTime(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DayTimeDurationExpr dayTimeDuration(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DecimalExpr decimal(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DoubleExpr doubleExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.DurationExpr duration(Xs.AnyAtomicTypeExpr arg1);
    public Xs.FloatExpr floatExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.GDayExpr gDay(Xs.AnyAtomicTypeExpr arg1);
    public Xs.GMonthExpr gMonth(Xs.AnyAtomicTypeExpr arg1);
    public Xs.GMonthDayExpr gMonthDay(Xs.AnyAtomicTypeExpr arg1);
    public Xs.GYearExpr gYear(Xs.AnyAtomicTypeExpr arg1);
    public Xs.GYearMonthExpr gYearMonth(Xs.AnyAtomicTypeExpr arg1);
    public Xs.HexBinaryExpr hexBinary(Xs.AnyAtomicTypeExpr arg1);
    public Xs.IntExpr intExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.IntegerExpr integer(Xs.AnyAtomicTypeExpr arg1);
    public Xs.LanguageExpr language(Xs.AnyAtomicTypeExpr arg1);
    public Xs.LongExpr longExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NameExpr Name(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NCNameExpr NCName(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NMTOKENExpr NMTOKEN(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NegativeIntegerExpr negativeInteger(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NonNegativeIntegerExpr nonNegativeInteger(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NonPositiveIntegerExpr nonPositiveInteger(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NormalizedStringExpr normalizedString(Xs.AnyAtomicTypeExpr arg1);
    public Xs.NumericExpr numeric(Xs.AnyAtomicTypeExpr arg1);
    public Xs.PositiveIntegerExpr positiveInteger(Xs.AnyAtomicTypeExpr arg1);
    public Xs.QNameExpr QName(Xs.AnyAtomicTypeExpr arg1);
    public Xs.ShortExpr shortExpr(Xs.AnyAtomicTypeExpr arg1);
    public Xs.StringExpr string(Xs.AnyAtomicTypeExpr arg1);
    public Xs.TimeExpr time(Xs.AnyAtomicTypeExpr arg1);
    public Xs.TokenExpr token(Xs.AnyAtomicTypeExpr arg1);
    public Xs.UnsignedByteExpr unsignedByte(Xs.AnyAtomicTypeExpr arg1);
    public Xs.UnsignedIntExpr unsignedInt(Xs.AnyAtomicTypeExpr arg1);
    public Xs.UnsignedLongExpr unsignedLong(Xs.AnyAtomicTypeExpr arg1);
    public Xs.UnsignedShortExpr unsignedShort(Xs.AnyAtomicTypeExpr arg1);
    public Xs.UntypedAtomicExpr untypedAtomic(Xs.AnyAtomicTypeExpr arg1);
    public Xs.YearMonthDurationExpr yearMonthDuration(Xs.AnyAtomicTypeExpr arg1);     public Xs.AnyAtomicTypeSeqExpr anyAtomicType(Xs.AnyAtomicTypeExpr... items);
     public Xs.AnySimpleTypeSeqExpr anySimpleType(Xs.AnySimpleTypeExpr... items);
     public Xs.AnyURISeqExpr anyURI(Xs.AnyURIExpr... items);
     public Xs.Base64BinarySeqExpr base64Binary(Xs.Base64BinaryExpr... items);
     public Xs.BooleanSeqExpr booleanExpr(Xs.BooleanExpr... items);
     public Xs.ByteSeqExpr byteExpr(Xs.ByteExpr... items);
     public Xs.DateSeqExpr date(Xs.DateExpr... items);
     public Xs.DateTimeSeqExpr dateTime(Xs.DateTimeExpr... items);
     public Xs.DayTimeDurationSeqExpr dayTimeDuration(Xs.DayTimeDurationExpr... items);
     public Xs.DecimalSeqExpr decimal(Xs.DecimalExpr... items);
     public Xs.DoubleSeqExpr doubleExpr(Xs.DoubleExpr... items);
     public Xs.DurationSeqExpr duration(Xs.DurationExpr... items);
     public Xs.FloatSeqExpr floatExpr(Xs.FloatExpr... items);
     public Xs.GDaySeqExpr gDay(Xs.GDayExpr... items);
     public Xs.GMonthSeqExpr gMonth(Xs.GMonthExpr... items);
     public Xs.GMonthDaySeqExpr gMonthDay(Xs.GMonthDayExpr... items);
     public Xs.GYearSeqExpr gYear(Xs.GYearExpr... items);
     public Xs.GYearMonthSeqExpr gYearMonth(Xs.GYearMonthExpr... items);
     public Xs.HexBinarySeqExpr hexBinary(Xs.HexBinaryExpr... items);
     public Xs.IntegerSeqExpr integer(Xs.IntegerExpr... items);
     public Xs.IntSeqExpr intExpr(Xs.IntExpr... items);
     public Xs.LanguageSeqExpr language(Xs.LanguageExpr... items);
     public Xs.LongSeqExpr longExpr(Xs.LongExpr... items);
     public Xs.NameSeqExpr name(Xs.NameExpr... items);
     public Xs.NCNameSeqExpr nCName(Xs.NCNameExpr... items);
     public Xs.NegativeIntegerSeqExpr negativeInteger(Xs.NegativeIntegerExpr... items);
     public Xs.NMTOKENSeqExpr nMTOKEN(Xs.NMTOKENExpr... items);
     public Xs.NonNegativeIntegerSeqExpr nonNegativeInteger(Xs.NonNegativeIntegerExpr... items);
     public Xs.NonPositiveIntegerSeqExpr nonPositiveInteger(Xs.NonPositiveIntegerExpr... items);
     public Xs.NormalizedStringSeqExpr normalizedString(Xs.NormalizedStringExpr... items);
     public Xs.NumericSeqExpr numeric(Xs.NumericExpr... items);
     public Xs.PositiveIntegerSeqExpr positiveInteger(Xs.PositiveIntegerExpr... items);
     public Xs.QNameSeqExpr qName(Xs.QNameExpr... items);
     public Xs.ShortSeqExpr shortExpr(Xs.ShortExpr... items);
     public Xs.StringSeqExpr string(Xs.StringExpr... items);
     public Xs.TimeSeqExpr time(Xs.TimeExpr... items);
     public Xs.TokenSeqExpr token(Xs.TokenExpr... items);
     public Xs.UnsignedByteSeqExpr unsignedByte(Xs.UnsignedByteExpr... items);
     public Xs.UnsignedIntSeqExpr unsignedInt(Xs.UnsignedIntExpr... items);
     public Xs.UnsignedLongSeqExpr unsignedLong(Xs.UnsignedLongExpr... items);
     public Xs.UnsignedShortSeqExpr unsignedShort(Xs.UnsignedShortExpr... items);
     public Xs.UntypedAtomicSeqExpr untypedAtomic(Xs.UntypedAtomicExpr... items);
     public Xs.YearMonthDurationSeqExpr yearMonthDuration(Xs.YearMonthDurationExpr... items);
        public interface AnyAtomicTypeSeqExpr extends AnySimpleTypeSeqExpr { }
        public interface AnyAtomicTypeExpr extends AnyAtomicTypeSeqExpr, AnySimpleTypeExpr { }
         public interface AnyAtomicTypeParam extends AnyAtomicTypeExpr, AnySimpleTypeParam {}
         public interface AnySimpleTypeSeqExpr extends BaseType.ItemSeqExpr { }
        public interface AnySimpleTypeExpr extends AnySimpleTypeSeqExpr, BaseType.ItemExpr { }
         public interface AnySimpleTypeParam extends AnySimpleTypeExpr, BaseType.ItemParam {}
         public interface AnyURISeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface AnyURIExpr extends AnyURISeqExpr, AnyAtomicTypeExpr { }
         public interface AnyURIParam extends AnyURIExpr, AnyAtomicTypeParam {}
         public interface NamedAnyURIParam extends AnyURIParam, BaseType.NamedItemParam {}
         public interface Base64BinarySeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface Base64BinaryExpr extends Base64BinarySeqExpr, AnyAtomicTypeExpr { }
         public interface Base64BinaryParam extends Base64BinaryExpr, AnyAtomicTypeParam {}
         public interface NamedBase64BinaryParam extends Base64BinaryParam, BaseType.NamedItemParam {}
         public interface BooleanSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface BooleanExpr extends BooleanSeqExpr, AnyAtomicTypeExpr { }
         public interface BooleanParam extends BooleanExpr, AnyAtomicTypeParam {}
         public interface NamedBooleanParam extends BooleanParam, BaseType.NamedItemParam {}
         public interface ByteSeqExpr extends ShortSeqExpr { }
        public interface ByteExpr extends ByteSeqExpr, ShortExpr { }
         public interface ByteParam extends ByteExpr, ShortParam {}
         public interface NamedByteParam extends ByteParam, BaseType.NamedItemParam {}
         public interface DateSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface DateExpr extends DateSeqExpr, AnyAtomicTypeExpr { }
         public interface DateParam extends DateExpr, AnyAtomicTypeParam {}
         public interface NamedDateParam extends DateParam, BaseType.NamedItemParam {}
         public interface DateTimeSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface DateTimeExpr extends DateTimeSeqExpr, AnyAtomicTypeExpr { }
         public interface DateTimeParam extends DateTimeExpr, AnyAtomicTypeParam {}
         public interface NamedDateTimeParam extends DateTimeParam, BaseType.NamedItemParam {}
         public interface DayTimeDurationSeqExpr extends DurationSeqExpr { }
        public interface DayTimeDurationExpr extends DayTimeDurationSeqExpr, DurationExpr { }
         public interface DayTimeDurationParam extends DayTimeDurationExpr, DurationParam {}
         public interface NamedDayTimeDurationParam extends DayTimeDurationParam, BaseType.NamedItemParam {}
         public interface DecimalSeqExpr extends AnyAtomicTypeSeqExpr, NumericSeqExpr { }
        public interface DecimalExpr extends DecimalSeqExpr, AnyAtomicTypeExpr, NumericExpr { }
         public interface DecimalParam extends DecimalExpr, AnyAtomicTypeParam, NumericParam {}
         public interface NamedDecimalParam extends DecimalParam, BaseType.NamedItemParam {}
         public interface DoubleSeqExpr extends AnyAtomicTypeSeqExpr, NumericSeqExpr { }
        public interface DoubleExpr extends DoubleSeqExpr, AnyAtomicTypeExpr, NumericExpr { }
         public interface DoubleParam extends DoubleExpr, AnyAtomicTypeParam, NumericParam {}
         public interface NamedDoubleParam extends DoubleParam, BaseType.NamedItemParam {}
         public interface DurationSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface DurationExpr extends DurationSeqExpr, AnyAtomicTypeExpr { }
         public interface DurationParam extends DurationExpr, AnyAtomicTypeParam {}
         public interface FloatSeqExpr extends AnyAtomicTypeSeqExpr, NumericSeqExpr { }
        public interface FloatExpr extends FloatSeqExpr, AnyAtomicTypeExpr, NumericExpr { }
         public interface FloatParam extends FloatExpr, AnyAtomicTypeParam, NumericParam {}
         public interface NamedFloatParam extends FloatParam, BaseType.NamedItemParam {}
         public interface GDaySeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface GDayExpr extends GDaySeqExpr, AnyAtomicTypeExpr { }
         public interface GDayParam extends GDayExpr, AnyAtomicTypeParam {}
         public interface NamedGDayParam extends GDayParam, BaseType.NamedItemParam {}
         public interface GMonthSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface GMonthExpr extends GMonthSeqExpr, AnyAtomicTypeExpr { }
         public interface GMonthParam extends GMonthExpr, AnyAtomicTypeParam {}
         public interface NamedGMonthParam extends GMonthParam, BaseType.NamedItemParam {}
         public interface GMonthDaySeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface GMonthDayExpr extends GMonthDaySeqExpr, AnyAtomicTypeExpr { }
         public interface GMonthDayParam extends GMonthDayExpr, AnyAtomicTypeParam {}
         public interface NamedGMonthDayParam extends GMonthDayParam, BaseType.NamedItemParam {}
         public interface GYearSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface GYearExpr extends GYearSeqExpr, AnyAtomicTypeExpr { }
         public interface GYearParam extends GYearExpr, AnyAtomicTypeParam {}
         public interface NamedGYearParam extends GYearParam, BaseType.NamedItemParam {}
         public interface GYearMonthSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface GYearMonthExpr extends GYearMonthSeqExpr, AnyAtomicTypeExpr { }
         public interface GYearMonthParam extends GYearMonthExpr, AnyAtomicTypeParam {}
         public interface NamedGYearMonthParam extends GYearMonthParam, BaseType.NamedItemParam {}
         public interface HexBinarySeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface HexBinaryExpr extends HexBinarySeqExpr, AnyAtomicTypeExpr { }
         public interface HexBinaryParam extends HexBinaryExpr, AnyAtomicTypeParam {}
         public interface NamedHexBinaryParam extends HexBinaryParam, BaseType.NamedItemParam {}
         public interface IntSeqExpr extends LongSeqExpr { }
        public interface IntExpr extends IntSeqExpr, LongExpr { }
         public interface IntParam extends IntExpr, LongParam {}
         public interface NamedIntParam extends IntParam, BaseType.NamedItemParam {}
         public interface IntegerSeqExpr extends DecimalSeqExpr { }
        public interface IntegerExpr extends IntegerSeqExpr, DecimalExpr { }
         public interface IntegerParam extends IntegerExpr, DecimalParam {}
         public interface NamedIntegerParam extends IntegerParam, BaseType.NamedItemParam {}
         public interface LanguageSeqExpr extends TokenSeqExpr { }
        public interface LanguageExpr extends LanguageSeqExpr, TokenExpr { }
         public interface LanguageParam extends LanguageExpr, TokenParam {}
         public interface LongSeqExpr extends IntegerSeqExpr { }
        public interface LongExpr extends LongSeqExpr, IntegerExpr { }
         public interface LongParam extends LongExpr, IntegerParam {}
         public interface NamedLongParam extends LongParam, BaseType.NamedItemParam {}
         public interface NameSeqExpr extends TokenSeqExpr { }
        public interface NameExpr extends NameSeqExpr, TokenExpr { }
         public interface NameParam extends NameExpr, TokenParam {}
         public interface NCNameSeqExpr extends NameSeqExpr { }
        public interface NCNameExpr extends NCNameSeqExpr, NameExpr { }
         public interface NCNameParam extends NCNameExpr, NameParam {}
         public interface NegativeIntegerSeqExpr extends NonPositiveIntegerSeqExpr { }
        public interface NegativeIntegerExpr extends NegativeIntegerSeqExpr, NonPositiveIntegerExpr { }
         public interface NegativeIntegerParam extends NegativeIntegerExpr, NonPositiveIntegerParam {}
         public interface NMTOKENSeqExpr extends TokenSeqExpr { }
        public interface NMTOKENExpr extends NMTOKENSeqExpr, TokenExpr { }
         public interface NMTOKENParam extends NMTOKENExpr, TokenParam {}
         public interface NonNegativeIntegerSeqExpr extends IntegerSeqExpr { }
        public interface NonNegativeIntegerExpr extends NonNegativeIntegerSeqExpr, IntegerExpr { }
         public interface NonNegativeIntegerParam extends NonNegativeIntegerExpr, IntegerParam {}
         public interface NonPositiveIntegerSeqExpr extends IntegerSeqExpr { }
        public interface NonPositiveIntegerExpr extends NonPositiveIntegerSeqExpr, IntegerExpr { }
         public interface NonPositiveIntegerParam extends NonPositiveIntegerExpr, IntegerParam {}
         public interface NormalizedStringSeqExpr extends StringSeqExpr { }
        public interface NormalizedStringExpr extends NormalizedStringSeqExpr, StringExpr { }
         public interface NormalizedStringParam extends NormalizedStringExpr, StringParam {}
         public interface NumericSeqExpr extends AnySimpleTypeSeqExpr { }
        public interface NumericExpr extends NumericSeqExpr, AnySimpleTypeExpr { }
         public interface NumericParam extends NumericExpr, AnySimpleTypeParam {}
         public interface PositiveIntegerSeqExpr extends NonNegativeIntegerSeqExpr { }
        public interface PositiveIntegerExpr extends PositiveIntegerSeqExpr, NonNegativeIntegerExpr { }
         public interface PositiveIntegerParam extends PositiveIntegerExpr, NonNegativeIntegerParam {}
         public interface QNameSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface QNameExpr extends QNameSeqExpr, AnyAtomicTypeExpr { }
         public interface QNameParam extends QNameExpr, AnyAtomicTypeParam {}
         public interface NamedQNameParam extends QNameParam, BaseType.NamedItemParam {}
         public interface ShortSeqExpr extends IntSeqExpr { }
        public interface ShortExpr extends ShortSeqExpr, IntExpr { }
         public interface ShortParam extends ShortExpr, IntParam {}
         public interface NamedShortParam extends ShortParam, BaseType.NamedItemParam {}
         public interface StringSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface StringExpr extends StringSeqExpr, AnyAtomicTypeExpr { }
         public interface StringParam extends StringExpr, AnyAtomicTypeParam {}
         public interface NamedStringParam extends StringParam, BaseType.NamedItemParam {}
         public interface TimeSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface TimeExpr extends TimeSeqExpr, AnyAtomicTypeExpr { }
         public interface TimeParam extends TimeExpr, AnyAtomicTypeParam {}
         public interface NamedTimeParam extends TimeParam, BaseType.NamedItemParam {}
         public interface TokenSeqExpr extends NormalizedStringSeqExpr { }
        public interface TokenExpr extends TokenSeqExpr, NormalizedStringExpr { }
         public interface TokenParam extends TokenExpr, NormalizedStringParam {}
         public interface UnsignedByteSeqExpr extends UnsignedShortSeqExpr { }
        public interface UnsignedByteExpr extends UnsignedByteSeqExpr, UnsignedShortExpr { }
         public interface UnsignedByteParam extends UnsignedByteExpr, UnsignedShortParam {}
         public interface NamedUnsignedByteParam extends UnsignedByteParam, BaseType.NamedItemParam {}
         public interface UnsignedIntSeqExpr extends UnsignedLongSeqExpr { }
        public interface UnsignedIntExpr extends UnsignedIntSeqExpr, UnsignedLongExpr { }
         public interface UnsignedIntParam extends UnsignedIntExpr, UnsignedLongParam {}
         public interface NamedUnsignedIntParam extends UnsignedIntParam, BaseType.NamedItemParam {}
         public interface UnsignedLongSeqExpr extends NonNegativeIntegerSeqExpr { }
        public interface UnsignedLongExpr extends UnsignedLongSeqExpr, NonNegativeIntegerExpr { }
         public interface UnsignedLongParam extends UnsignedLongExpr, NonNegativeIntegerParam {}
         public interface NamedUnsignedLongParam extends UnsignedLongParam, BaseType.NamedItemParam {}
         public interface UnsignedShortSeqExpr extends UnsignedIntSeqExpr { }
        public interface UnsignedShortExpr extends UnsignedShortSeqExpr, UnsignedIntExpr { }
         public interface UnsignedShortParam extends UnsignedShortExpr, UnsignedIntParam {}
         public interface NamedUnsignedShortParam extends UnsignedShortParam, BaseType.NamedItemParam {}
         public interface UntypedAtomicSeqExpr extends AnyAtomicTypeSeqExpr { }
        public interface UntypedAtomicExpr extends UntypedAtomicSeqExpr, AnyAtomicTypeExpr { }
         public interface UntypedAtomicParam extends UntypedAtomicExpr, AnyAtomicTypeParam {}
         public interface NamedUntypedAtomicParam extends UntypedAtomicParam, BaseType.NamedItemParam {}
         public interface YearMonthDurationSeqExpr extends DurationSeqExpr { }
        public interface YearMonthDurationExpr extends YearMonthDurationSeqExpr, DurationExpr { }
         public interface YearMonthDurationParam extends YearMonthDurationExpr, DurationParam {}
         public interface NamedYearMonthDurationParam extends YearMonthDurationParam, BaseType.NamedItemParam {}

}
