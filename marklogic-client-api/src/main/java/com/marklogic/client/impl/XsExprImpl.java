/*
 * Copyright 2016-2019 MarkLogic Corporation
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

import com.marklogic.client.type.ItemSeqExpr;

import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class XsExprImpl extends XsValueImpl implements XsExpr {

  final static XsExprImpl xs = new XsExprImpl();

  XsExprImpl() {
  }

    
  @Override
  public XsAnyURIExpr anyURI(ServerExpression arg1) {
    return new AnyURICallImpl("xs", "anyURI", new Object[]{ arg1 });
  }

  
  @Override
  public XsBase64BinaryExpr base64Binary(ServerExpression arg1) {
    return new Base64BinaryCallImpl("xs", "base64Binary", new Object[]{ arg1 });
  }

  
  @Override
  public XsBooleanExpr booleanExpr(ServerExpression arg1) {
    return new BooleanCallImpl("xs", "boolean", new Object[]{ arg1 });
  }

  
  @Override
  public XsByteExpr byteExpr(ServerExpression arg1) {
    return new ByteCallImpl("xs", "byte", new Object[]{ arg1 });
  }

  
  @Override
  public XsDateExpr date(ServerExpression arg1) {
    return new DateCallImpl("xs", "date", new Object[]{ arg1 });
  }

  
  @Override
  public XsDateTimeExpr dateTime(ServerExpression arg1) {
    return new DateTimeCallImpl("xs", "dateTime", new Object[]{ arg1 });
  }

  
  @Override
  public XsDayTimeDurationExpr dayTimeDuration(ServerExpression arg1) {
    return new DayTimeDurationCallImpl("xs", "dayTimeDuration", new Object[]{ arg1 });
  }

  
  @Override
  public XsDecimalExpr decimal(ServerExpression arg1) {
    return new DecimalCallImpl("xs", "decimal", new Object[]{ arg1 });
  }

  
  @Override
  public XsDoubleExpr doubleExpr(ServerExpression arg1) {
    return new DoubleCallImpl("xs", "double", new Object[]{ arg1 });
  }

  
  @Override
  public XsFloatExpr floatExpr(ServerExpression arg1) {
    return new FloatCallImpl("xs", "float", new Object[]{ arg1 });
  }

  
  @Override
  public XsGDayExpr gDay(ServerExpression arg1) {
    return new GDayCallImpl("xs", "gDay", new Object[]{ arg1 });
  }

  
  @Override
  public XsGMonthExpr gMonth(ServerExpression arg1) {
    return new GMonthCallImpl("xs", "gMonth", new Object[]{ arg1 });
  }

  
  @Override
  public XsGMonthDayExpr gMonthDay(ServerExpression arg1) {
    return new GMonthDayCallImpl("xs", "gMonthDay", new Object[]{ arg1 });
  }

  
  @Override
  public XsGYearExpr gYear(ServerExpression arg1) {
    return new GYearCallImpl("xs", "gYear", new Object[]{ arg1 });
  }

  
  @Override
  public XsGYearMonthExpr gYearMonth(ServerExpression arg1) {
    return new GYearMonthCallImpl("xs", "gYearMonth", new Object[]{ arg1 });
  }

  
  @Override
  public XsHexBinaryExpr hexBinary(ServerExpression arg1) {
    return new HexBinaryCallImpl("xs", "hexBinary", new Object[]{ arg1 });
  }

  
  @Override
  public XsIntExpr intExpr(ServerExpression arg1) {
    return new IntCallImpl("xs", "int", new Object[]{ arg1 });
  }

  
  @Override
  public XsIntegerExpr integer(ServerExpression arg1) {
    return new IntegerCallImpl("xs", "integer", new Object[]{ arg1 });
  }

  
  @Override
  public XsLanguageExpr language(ServerExpression arg1) {
    return new LanguageCallImpl("xs", "language", new Object[]{ arg1 });
  }

  
  @Override
  public XsLongExpr longExpr(ServerExpression arg1) {
    return new LongCallImpl("xs", "long", new Object[]{ arg1 });
  }

  
  @Override
  public XsNameExpr Name(ServerExpression arg1) {
    return new NameCallImpl("xs", "Name", new Object[]{ arg1 });
  }

  
  @Override
  public XsNCNameExpr NCName(ServerExpression arg1) {
    return new NCNameCallImpl("xs", "NCName", new Object[]{ arg1 });
  }

  
  @Override
  public XsNegativeIntegerExpr negativeInteger(ServerExpression arg1) {
    return new NegativeIntegerCallImpl("xs", "negativeInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNMTOKENExpr NMTOKEN(ServerExpression arg1) {
    return new NMTOKENCallImpl("xs", "NMTOKEN", new Object[]{ arg1 });
  }

  
  @Override
  public XsNonNegativeIntegerExpr nonNegativeInteger(ServerExpression arg1) {
    return new NonNegativeIntegerCallImpl("xs", "nonNegativeInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNonPositiveIntegerExpr nonPositiveInteger(ServerExpression arg1) {
    return new NonPositiveIntegerCallImpl("xs", "nonPositiveInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNormalizedStringExpr normalizedString(ServerExpression arg1) {
    return new NormalizedStringCallImpl("xs", "normalizedString", new Object[]{ arg1 });
  }

  
  @Override
  public XsNumericExpr numeric(ServerExpression arg1) {
    return new NumericCallImpl("xs", "numeric", new Object[]{ arg1 });
  }

  
  @Override
  public XsPositiveIntegerExpr positiveInteger(ServerExpression arg1) {
    return new PositiveIntegerCallImpl("xs", "positiveInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsQNameExpr QName(ServerExpression arg1) {
    return new QNameCallImpl("xs", "QName", new Object[]{ arg1 });
  }

  
  @Override
  public XsShortExpr shortExpr(ServerExpression arg1) {
    return new ShortCallImpl("xs", "short", new Object[]{ arg1 });
  }

  
  @Override
  public XsStringExpr string(ServerExpression arg1) {
    return new StringCallImpl("xs", "string", new Object[]{ arg1 });
  }

  
  @Override
  public XsTimeExpr time(ServerExpression arg1) {
    return new TimeCallImpl("xs", "time", new Object[]{ arg1 });
  }

  
  @Override
  public XsTokenExpr token(ServerExpression arg1) {
    return new TokenCallImpl("xs", "token", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedByteExpr unsignedByte(ServerExpression arg1) {
    return new UnsignedByteCallImpl("xs", "unsignedByte", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedIntExpr unsignedInt(ServerExpression arg1) {
    return new UnsignedIntCallImpl("xs", "unsignedInt", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedLongExpr unsignedLong(ServerExpression arg1) {
    return new UnsignedLongCallImpl("xs", "unsignedLong", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedShortExpr unsignedShort(ServerExpression arg1) {
    return new UnsignedShortCallImpl("xs", "unsignedShort", new Object[]{ arg1 });
  }

  
  @Override
  public XsUntypedAtomicExpr untypedAtomic(ServerExpression arg1) {
    return new UntypedAtomicCallImpl("xs", "untypedAtomic", new Object[]{ arg1 });
  }

  
  @Override
  public XsYearMonthDurationExpr yearMonthDuration(ServerExpression arg1) {
    return new YearMonthDurationCallImpl("xs", "yearMonthDuration", new Object[]{ arg1 });
  }

  @Override
  public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items) {
    return new AnyAtomicTypeSeqListImpl(items);
  }
  static class AnyAtomicTypeSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsAnyAtomicTypeSeqExpr {
    AnyAtomicTypeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class AnyAtomicTypeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsAnyAtomicTypeSeqExpr {
    AnyAtomicTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class AnyAtomicTypeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsAnyAtomicTypeExpr {
    AnyAtomicTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items) {
    return new AnyURISeqListImpl(items);
  }
  static class AnyURISeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsAnyURISeqExpr {
    AnyURISeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class AnyURISeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsAnyURISeqExpr {
    AnyURISeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class AnyURICallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsAnyURIExpr {
    AnyURICallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items) {
    return new Base64BinarySeqListImpl(items);
  }
  static class Base64BinarySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsBase64BinarySeqExpr {
    Base64BinarySeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class Base64BinarySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsBase64BinarySeqExpr {
    Base64BinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class Base64BinaryCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsBase64BinaryExpr {
    Base64BinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items) {
    return new BooleanSeqListImpl(items);
  }
  static class BooleanSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsBooleanSeqExpr {
    BooleanSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class BooleanSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsBooleanSeqExpr {
    BooleanSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class BooleanCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsBooleanExpr {
    BooleanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsByteSeqExpr byteExprSeq(XsByteExpr... items) {
    return new ByteSeqListImpl(items);
  }
  static class ByteSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsByteSeqExpr {
    ByteSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ByteSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsByteSeqExpr {
    ByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ByteCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsByteExpr {
    ByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsDateSeqExpr dateSeq(XsDateExpr... items) {
    return new DateSeqListImpl(items);
  }
  static class DateSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsDateSeqExpr {
    DateSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DateSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDateSeqExpr {
    DateSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DateCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDateExpr {
    DateCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items) {
    return new DateTimeSeqListImpl(items);
  }
  static class DateTimeSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsDateTimeSeqExpr {
    DateTimeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DateTimeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDateTimeSeqExpr {
    DateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DateTimeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDateTimeExpr {
    DateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items) {
    return new DayTimeDurationSeqListImpl(items);
  }
  static class DayTimeDurationSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsDayTimeDurationSeqExpr {
    DayTimeDurationSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DayTimeDurationSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDayTimeDurationSeqExpr {
    DayTimeDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DayTimeDurationCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDayTimeDurationExpr {
    DayTimeDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items) {
    return new DecimalSeqListImpl(items);
  }
  static class DecimalSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsDecimalSeqExpr {
    DecimalSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DecimalSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDecimalSeqExpr {
    DecimalSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DecimalCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDecimalExpr {
    DecimalCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items) {
    return new DoubleSeqListImpl(items);
  }
  static class DoubleSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsDoubleSeqExpr {
    DoubleSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class DoubleSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDoubleSeqExpr {
    DoubleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DoubleCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsDoubleExpr {
    DoubleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items) {
    return new FloatSeqListImpl(items);
  }
  static class FloatSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsFloatSeqExpr {
    FloatSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class FloatSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsFloatSeqExpr {
    FloatSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class FloatCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsFloatExpr {
    FloatCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsGDaySeqExpr GDaySeq(XsGDayExpr... items) {
    return new GDaySeqListImpl(items);
  }
  static class GDaySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsGDaySeqExpr {
    GDaySeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class GDaySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGDaySeqExpr {
    GDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GDayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGDayExpr {
    GDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items) {
    return new GMonthSeqListImpl(items);
  }
  static class GMonthSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsGMonthSeqExpr {
    GMonthSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class GMonthSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGMonthSeqExpr {
    GMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GMonthCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGMonthExpr {
    GMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items) {
    return new GMonthDaySeqListImpl(items);
  }
  static class GMonthDaySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsGMonthDaySeqExpr {
    GMonthDaySeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class GMonthDaySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGMonthDaySeqExpr {
    GMonthDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GMonthDayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGMonthDayExpr {
    GMonthDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsGYearSeqExpr GYearSeq(XsGYearExpr... items) {
    return new GYearSeqListImpl(items);
  }
  static class GYearSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsGYearSeqExpr {
    GYearSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class GYearSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGYearSeqExpr {
    GYearSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GYearCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGYearExpr {
    GYearCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items) {
    return new GYearMonthSeqListImpl(items);
  }
  static class GYearMonthSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsGYearMonthSeqExpr {
    GYearMonthSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class GYearMonthSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGYearMonthSeqExpr {
    GYearMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GYearMonthCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsGYearMonthExpr {
    GYearMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items) {
    return new HexBinarySeqListImpl(items);
  }
  static class HexBinarySeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsHexBinarySeqExpr {
    HexBinarySeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class HexBinarySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsHexBinarySeqExpr {
    HexBinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class HexBinaryCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsHexBinaryExpr {
    HexBinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsIntSeqExpr intExprSeq(XsIntExpr... items) {
    return new IntSeqListImpl(items);
  }
  static class IntSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsIntSeqExpr {
    IntSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class IntSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsIntSeqExpr {
    IntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class IntCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsIntExpr {
    IntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items) {
    return new IntegerSeqListImpl(items);
  }
  static class IntegerSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsIntegerSeqExpr {
    IntegerSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class IntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsIntegerSeqExpr {
    IntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class IntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsIntegerExpr {
    IntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items) {
    return new LanguageSeqListImpl(items);
  }
  static class LanguageSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsLanguageSeqExpr {
    LanguageSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class LanguageSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsLanguageSeqExpr {
    LanguageSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LanguageCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsLanguageExpr {
    LanguageCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsLongSeqExpr longExprSeq(XsLongExpr... items) {
    return new LongSeqListImpl(items);
  }
  static class LongSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsLongSeqExpr {
    LongSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class LongSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsLongSeqExpr {
    LongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LongCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsLongExpr {
    LongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNameSeqExpr NameSeq(XsNameExpr... items) {
    return new NameSeqListImpl(items);
  }
  static class NameSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNameSeqExpr {
    NameSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNameSeqExpr {
    NameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNameExpr {
    NameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items) {
    return new NCNameSeqListImpl(items);
  }
  static class NCNameSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNCNameSeqExpr {
    NCNameSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NCNameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNCNameSeqExpr {
    NCNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NCNameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNCNameExpr {
    NCNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items) {
    return new NegativeIntegerSeqListImpl(items);
  }
  static class NegativeIntegerSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNegativeIntegerSeqExpr {
    NegativeIntegerSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NegativeIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNegativeIntegerSeqExpr {
    NegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NegativeIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNegativeIntegerExpr {
    NegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items) {
    return new NMTOKENSeqListImpl(items);
  }
  static class NMTOKENSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNMTOKENSeqExpr {
    NMTOKENSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NMTOKENSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNMTOKENSeqExpr {
    NMTOKENSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NMTOKENCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNMTOKENExpr {
    NMTOKENCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items) {
    return new NonNegativeIntegerSeqListImpl(items);
  }
  static class NonNegativeIntegerSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNonNegativeIntegerSeqExpr {
    NonNegativeIntegerSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NonNegativeIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNonNegativeIntegerSeqExpr {
    NonNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NonNegativeIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNonNegativeIntegerExpr {
    NonNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items) {
    return new NonPositiveIntegerSeqListImpl(items);
  }
  static class NonPositiveIntegerSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNonPositiveIntegerSeqExpr {
    NonPositiveIntegerSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NonPositiveIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNonPositiveIntegerSeqExpr {
    NonPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NonPositiveIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNonPositiveIntegerExpr {
    NonPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items) {
    return new NormalizedStringSeqListImpl(items);
  }
  static class NormalizedStringSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNormalizedStringSeqExpr {
    NormalizedStringSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NormalizedStringSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNormalizedStringSeqExpr {
    NormalizedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NormalizedStringCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNormalizedStringExpr {
    NormalizedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsNumericSeqExpr numericSeq(XsNumericExpr... items) {
    return new NumericSeqListImpl(items);
  }
  static class NumericSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsNumericSeqExpr {
    NumericSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class NumericSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNumericSeqExpr {
    NumericSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NumericCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsNumericExpr {
    NumericCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items) {
    return new PositiveIntegerSeqListImpl(items);
  }
  static class PositiveIntegerSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsPositiveIntegerSeqExpr {
    PositiveIntegerSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class PositiveIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsPositiveIntegerSeqExpr {
    PositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PositiveIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsPositiveIntegerExpr {
    PositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsQNameSeqExpr QNameSeq(XsQNameExpr... items) {
    return new QNameSeqListImpl(items);
  }
  static class QNameSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsQNameSeqExpr {
    QNameSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class QNameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsQNameSeqExpr {
    QNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class QNameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsQNameExpr {
    QNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsShortSeqExpr shortExprSeq(XsShortExpr... items) {
    return new ShortSeqListImpl(items);
  }
  static class ShortSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsShortSeqExpr {
    ShortSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class ShortSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsShortSeqExpr {
    ShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ShortCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsShortExpr {
    ShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsStringSeqExpr stringSeq(XsStringExpr... items) {
    return new StringSeqListImpl(items);
  }
  static class StringSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsStringSeqExpr {
    StringSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class StringSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsStringSeqExpr {
    StringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class StringCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsStringExpr {
    StringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsTimeSeqExpr timeSeq(XsTimeExpr... items) {
    return new TimeSeqListImpl(items);
  }
  static class TimeSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsTimeSeqExpr {
    TimeSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class TimeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsTimeSeqExpr {
    TimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class TimeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsTimeExpr {
    TimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsTokenSeqExpr tokenSeq(XsTokenExpr... items) {
    return new TokenSeqListImpl(items);
  }
  static class TokenSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsTokenSeqExpr {
    TokenSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class TokenSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsTokenSeqExpr {
    TokenSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class TokenCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsTokenExpr {
    TokenCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items) {
    return new UnsignedByteSeqListImpl(items);
  }
  static class UnsignedByteSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsUnsignedByteSeqExpr {
    UnsignedByteSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class UnsignedByteSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedByteSeqExpr {
    UnsignedByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedByteCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedByteExpr {
    UnsignedByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items) {
    return new UnsignedIntSeqListImpl(items);
  }
  static class UnsignedIntSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsUnsignedIntSeqExpr {
    UnsignedIntSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class UnsignedIntSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedIntSeqExpr {
    UnsignedIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedIntCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedIntExpr {
    UnsignedIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items) {
    return new UnsignedLongSeqListImpl(items);
  }
  static class UnsignedLongSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsUnsignedLongSeqExpr {
    UnsignedLongSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class UnsignedLongSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedLongSeqExpr {
    UnsignedLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedLongCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedLongExpr {
    UnsignedLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items) {
    return new UnsignedShortSeqListImpl(items);
  }
  static class UnsignedShortSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsUnsignedShortSeqExpr {
    UnsignedShortSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class UnsignedShortSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedShortSeqExpr {
    UnsignedShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedShortCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUnsignedShortExpr {
    UnsignedShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items) {
    return new UntypedAtomicSeqListImpl(items);
  }
  static class UntypedAtomicSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsUntypedAtomicSeqExpr {
    UntypedAtomicSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class UntypedAtomicSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUntypedAtomicSeqExpr {
    UntypedAtomicSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UntypedAtomicCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsUntypedAtomicExpr {
    UntypedAtomicCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
 
  @Override
  public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items) {
    return new YearMonthDurationSeqListImpl(items);
  }
  static class YearMonthDurationSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements XsYearMonthDurationSeqExpr {
    YearMonthDurationSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class YearMonthDurationSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsYearMonthDurationSeqExpr {
    YearMonthDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class YearMonthDurationCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements XsYearMonthDurationExpr {
    YearMonthDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
