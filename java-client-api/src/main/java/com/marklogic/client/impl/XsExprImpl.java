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

import com.marklogic.client.type.ItemSeqExpr;

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
  public XsAnyURIExpr anyURI(ItemSeqExpr arg1) {
    return new AnyURICallImpl("xs", "anyURI", new Object[]{ arg1 });
  }

  
  @Override
  public XsBase64BinaryExpr base64Binary(ItemSeqExpr arg1) {
    return new Base64BinaryCallImpl("xs", "base64Binary", new Object[]{ arg1 });
  }

  
  @Override
  public XsBooleanExpr booleanExpr(ItemSeqExpr arg1) {
    return new BooleanCallImpl("xs", "boolean", new Object[]{ arg1 });
  }

  
  @Override
  public XsByteExpr byteExpr(ItemSeqExpr arg1) {
    return new ByteCallImpl("xs", "byte", new Object[]{ arg1 });
  }

  
  @Override
  public XsDateExpr date(ItemSeqExpr arg1) {
    return new DateCallImpl("xs", "date", new Object[]{ arg1 });
  }

  
  @Override
  public XsDateTimeExpr dateTime(ItemSeqExpr arg1) {
    return new DateTimeCallImpl("xs", "dateTime", new Object[]{ arg1 });
  }

  
  @Override
  public XsDayTimeDurationExpr dayTimeDuration(ItemSeqExpr arg1) {
    return new DayTimeDurationCallImpl("xs", "dayTimeDuration", new Object[]{ arg1 });
  }

  
  @Override
  public XsDecimalExpr decimal(ItemSeqExpr arg1) {
    return new DecimalCallImpl("xs", "decimal", new Object[]{ arg1 });
  }

  
  @Override
  public XsDoubleExpr doubleExpr(ItemSeqExpr arg1) {
    return new DoubleCallImpl("xs", "double", new Object[]{ arg1 });
  }

  
  @Override
  public XsFloatExpr floatExpr(ItemSeqExpr arg1) {
    return new FloatCallImpl("xs", "float", new Object[]{ arg1 });
  }

  
  @Override
  public XsGDayExpr gDay(ItemSeqExpr arg1) {
    return new GDayCallImpl("xs", "gDay", new Object[]{ arg1 });
  }

  
  @Override
  public XsGMonthExpr gMonth(ItemSeqExpr arg1) {
    return new GMonthCallImpl("xs", "gMonth", new Object[]{ arg1 });
  }

  
  @Override
  public XsGMonthDayExpr gMonthDay(ItemSeqExpr arg1) {
    return new GMonthDayCallImpl("xs", "gMonthDay", new Object[]{ arg1 });
  }

  
  @Override
  public XsGYearExpr gYear(ItemSeqExpr arg1) {
    return new GYearCallImpl("xs", "gYear", new Object[]{ arg1 });
  }

  
  @Override
  public XsGYearMonthExpr gYearMonth(ItemSeqExpr arg1) {
    return new GYearMonthCallImpl("xs", "gYearMonth", new Object[]{ arg1 });
  }

  
  @Override
  public XsHexBinaryExpr hexBinary(ItemSeqExpr arg1) {
    return new HexBinaryCallImpl("xs", "hexBinary", new Object[]{ arg1 });
  }

  
  @Override
  public XsIntExpr intExpr(ItemSeqExpr arg1) {
    return new IntCallImpl("xs", "int", new Object[]{ arg1 });
  }

  
  @Override
  public XsIntegerExpr integer(ItemSeqExpr arg1) {
    return new IntegerCallImpl("xs", "integer", new Object[]{ arg1 });
  }

  
  @Override
  public XsLanguageExpr language(ItemSeqExpr arg1) {
    return new LanguageCallImpl("xs", "language", new Object[]{ arg1 });
  }

  
  @Override
  public XsLongExpr longExpr(ItemSeqExpr arg1) {
    return new LongCallImpl("xs", "long", new Object[]{ arg1 });
  }

  
  @Override
  public XsNameExpr Name(ItemSeqExpr arg1) {
    return new NameCallImpl("xs", "Name", new Object[]{ arg1 });
  }

  
  @Override
  public XsNCNameExpr NCName(ItemSeqExpr arg1) {
    return new NCNameCallImpl("xs", "NCName", new Object[]{ arg1 });
  }

  
  @Override
  public XsNegativeIntegerExpr negativeInteger(ItemSeqExpr arg1) {
    return new NegativeIntegerCallImpl("xs", "negativeInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNMTOKENExpr NMTOKEN(ItemSeqExpr arg1) {
    return new NMTOKENCallImpl("xs", "NMTOKEN", new Object[]{ arg1 });
  }

  
  @Override
  public XsNonNegativeIntegerExpr nonNegativeInteger(ItemSeqExpr arg1) {
    return new NonNegativeIntegerCallImpl("xs", "nonNegativeInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNonPositiveIntegerExpr nonPositiveInteger(ItemSeqExpr arg1) {
    return new NonPositiveIntegerCallImpl("xs", "nonPositiveInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsNormalizedStringExpr normalizedString(ItemSeqExpr arg1) {
    return new NormalizedStringCallImpl("xs", "normalizedString", new Object[]{ arg1 });
  }

  
  @Override
  public XsNumericExpr numeric(ItemSeqExpr arg1) {
    return new NumericCallImpl("xs", "numeric", new Object[]{ arg1 });
  }

  
  @Override
  public XsPositiveIntegerExpr positiveInteger(ItemSeqExpr arg1) {
    return new PositiveIntegerCallImpl("xs", "positiveInteger", new Object[]{ arg1 });
  }

  
  @Override
  public XsQNameExpr QName(ItemSeqExpr arg1) {
    return new QNameCallImpl("xs", "QName", new Object[]{ arg1 });
  }

  
  @Override
  public XsShortExpr shortExpr(ItemSeqExpr arg1) {
    return new ShortCallImpl("xs", "short", new Object[]{ arg1 });
  }

  
  @Override
  public XsStringExpr string(ItemSeqExpr arg1) {
    return new StringCallImpl("xs", "string", new Object[]{ arg1 });
  }

  
  @Override
  public XsTimeExpr time(ItemSeqExpr arg1) {
    return new TimeCallImpl("xs", "time", new Object[]{ arg1 });
  }

  
  @Override
  public XsTokenExpr token(ItemSeqExpr arg1) {
    return new TokenCallImpl("xs", "token", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedByteExpr unsignedByte(ItemSeqExpr arg1) {
    return new UnsignedByteCallImpl("xs", "unsignedByte", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedIntExpr unsignedInt(ItemSeqExpr arg1) {
    return new UnsignedIntCallImpl("xs", "unsignedInt", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedLongExpr unsignedLong(ItemSeqExpr arg1) {
    return new UnsignedLongCallImpl("xs", "unsignedLong", new Object[]{ arg1 });
  }

  
  @Override
  public XsUnsignedShortExpr unsignedShort(ItemSeqExpr arg1) {
    return new UnsignedShortCallImpl("xs", "unsignedShort", new Object[]{ arg1 });
  }

  
  @Override
  public XsUntypedAtomicExpr untypedAtomic(ItemSeqExpr arg1) {
    return new UntypedAtomicCallImpl("xs", "untypedAtomic", new Object[]{ arg1 });
  }

  
  @Override
  public XsYearMonthDurationExpr yearMonthDuration(ItemSeqExpr arg1) {
    return new YearMonthDurationCallImpl("xs", "yearMonthDuration", new Object[]{ arg1 });
  }

  @Override
  public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items) {
    return new AnyAtomicTypeSeqListImpl(items);
  }
  static class AnyAtomicTypeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeSeqExpr {
    AnyAtomicTypeSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class AnyAtomicTypeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeSeqExpr {
    AnyAtomicTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class AnyAtomicTypeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyAtomicTypeExpr {
    AnyAtomicTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items) {
    return new AnyURISeqListImpl(items);
  }
  static class AnyURISeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURISeqExpr {
    AnyURISeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class AnyURISeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURISeqExpr {
    AnyURISeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class AnyURICallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsAnyURIExpr {
    AnyURICallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items) {
    return new Base64BinarySeqListImpl(items);
  }
  static class Base64BinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinarySeqExpr {
    Base64BinarySeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class Base64BinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinarySeqExpr {
    Base64BinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class Base64BinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBase64BinaryExpr {
    Base64BinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items) {
    return new BooleanSeqListImpl(items);
  }
  static class BooleanSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanSeqExpr {
    BooleanSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class BooleanSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanSeqExpr {
    BooleanSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class BooleanCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsBooleanExpr {
    BooleanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsByteSeqExpr byteExprSeq(XsByteExpr... items) {
    return new ByteSeqListImpl(items);
  }
  static class ByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsByteSeqExpr {
    ByteSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class ByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsByteSeqExpr {
    ByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class ByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsByteExpr {
    ByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsDateSeqExpr dateSeq(XsDateExpr... items) {
    return new DateSeqListImpl(items);
  }
  static class DateSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDateSeqExpr {
    DateSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class DateSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateSeqExpr {
    DateSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class DateCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateExpr {
    DateCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items) {
    return new DateTimeSeqListImpl(items);
  }
  static class DateTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeSeqExpr {
    DateTimeSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class DateTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeSeqExpr {
    DateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class DateTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDateTimeExpr {
    DateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items) {
    return new DayTimeDurationSeqListImpl(items);
  }
  static class DayTimeDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationSeqExpr {
    DayTimeDurationSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class DayTimeDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationSeqExpr {
    DayTimeDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class DayTimeDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDayTimeDurationExpr {
    DayTimeDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items) {
    return new DecimalSeqListImpl(items);
  }
  static class DecimalSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalSeqExpr {
    DecimalSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class DecimalSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalSeqExpr {
    DecimalSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class DecimalCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDecimalExpr {
    DecimalCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items) {
    return new DoubleSeqListImpl(items);
  }
  static class DoubleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleSeqExpr {
    DoubleSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class DoubleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleSeqExpr {
    DoubleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class DoubleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsDoubleExpr {
    DoubleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items) {
    return new FloatSeqListImpl(items);
  }
  static class FloatSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatSeqExpr {
    FloatSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class FloatSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatSeqExpr {
    FloatSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class FloatCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsFloatExpr {
    FloatCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsGDaySeqExpr GDaySeq(XsGDayExpr... items) {
    return new GDaySeqListImpl(items);
  }
  static class GDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGDaySeqExpr {
    GDaySeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class GDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGDaySeqExpr {
    GDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class GDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGDayExpr {
    GDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items) {
    return new GMonthSeqListImpl(items);
  }
  static class GMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthSeqExpr {
    GMonthSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class GMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthSeqExpr {
    GMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class GMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthExpr {
    GMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items) {
    return new GMonthDaySeqListImpl(items);
  }
  static class GMonthDaySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDaySeqExpr {
    GMonthDaySeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class GMonthDaySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDaySeqExpr {
    GMonthDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class GMonthDayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGMonthDayExpr {
    GMonthDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsGYearSeqExpr GYearSeq(XsGYearExpr... items) {
    return new GYearSeqListImpl(items);
  }
  static class GYearSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearSeqExpr {
    GYearSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class GYearSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearSeqExpr {
    GYearSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class GYearCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearExpr {
    GYearCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items) {
    return new GYearMonthSeqListImpl(items);
  }
  static class GYearMonthSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthSeqExpr {
    GYearMonthSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class GYearMonthSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthSeqExpr {
    GYearMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class GYearMonthCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsGYearMonthExpr {
    GYearMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items) {
    return new HexBinarySeqListImpl(items);
  }
  static class HexBinarySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinarySeqExpr {
    HexBinarySeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class HexBinarySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinarySeqExpr {
    HexBinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class HexBinaryCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsHexBinaryExpr {
    HexBinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsIntSeqExpr intExprSeq(XsIntExpr... items) {
    return new IntSeqListImpl(items);
  }
  static class IntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsIntSeqExpr {
    IntSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class IntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntSeqExpr {
    IntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class IntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntExpr {
    IntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items) {
    return new IntegerSeqListImpl(items);
  }
  static class IntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerSeqExpr {
    IntegerSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class IntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerSeqExpr {
    IntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class IntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsIntegerExpr {
    IntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items) {
    return new LanguageSeqListImpl(items);
  }
  static class LanguageSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageSeqExpr {
    LanguageSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class LanguageSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageSeqExpr {
    LanguageSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class LanguageCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLanguageExpr {
    LanguageCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsLongSeqExpr longExprSeq(XsLongExpr... items) {
    return new LongSeqListImpl(items);
  }
  static class LongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsLongSeqExpr {
    LongSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class LongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLongSeqExpr {
    LongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class LongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsLongExpr {
    LongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNameSeqExpr NameSeq(XsNameExpr... items) {
    return new NameSeqListImpl(items);
  }
  static class NameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNameSeqExpr {
    NameSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNameSeqExpr {
    NameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNameExpr {
    NameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items) {
    return new NCNameSeqListImpl(items);
  }
  static class NCNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameSeqExpr {
    NCNameSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NCNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameSeqExpr {
    NCNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NCNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNCNameExpr {
    NCNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items) {
    return new NegativeIntegerSeqListImpl(items);
  }
  static class NegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerSeqExpr {
    NegativeIntegerSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerSeqExpr {
    NegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNegativeIntegerExpr {
    NegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items) {
    return new NMTOKENSeqListImpl(items);
  }
  static class NMTOKENSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENSeqExpr {
    NMTOKENSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NMTOKENSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENSeqExpr {
    NMTOKENSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NMTOKENCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNMTOKENExpr {
    NMTOKENCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items) {
    return new NonNegativeIntegerSeqListImpl(items);
  }
  static class NonNegativeIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerSeqExpr {
    NonNegativeIntegerSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NonNegativeIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerSeqExpr {
    NonNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NonNegativeIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonNegativeIntegerExpr {
    NonNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items) {
    return new NonPositiveIntegerSeqListImpl(items);
  }
  static class NonPositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerSeqExpr {
    NonPositiveIntegerSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NonPositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerSeqExpr {
    NonPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NonPositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNonPositiveIntegerExpr {
    NonPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items) {
    return new NormalizedStringSeqListImpl(items);
  }
  static class NormalizedStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringSeqExpr {
    NormalizedStringSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NormalizedStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringSeqExpr {
    NormalizedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NormalizedStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNormalizedStringExpr {
    NormalizedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsNumericSeqExpr numericSeq(XsNumericExpr... items) {
    return new NumericSeqListImpl(items);
  }
  static class NumericSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericSeqExpr {
    NumericSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class NumericSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericSeqExpr {
    NumericSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class NumericCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsNumericExpr {
    NumericCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items) {
    return new PositiveIntegerSeqListImpl(items);
  }
  static class PositiveIntegerSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerSeqExpr {
    PositiveIntegerSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class PositiveIntegerSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerSeqExpr {
    PositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class PositiveIntegerCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsPositiveIntegerExpr {
    PositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsQNameSeqExpr QNameSeq(XsQNameExpr... items) {
    return new QNameSeqListImpl(items);
  }
  static class QNameSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameSeqExpr {
    QNameSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class QNameSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameSeqExpr {
    QNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class QNameCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsQNameExpr {
    QNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsShortSeqExpr shortExprSeq(XsShortExpr... items) {
    return new ShortSeqListImpl(items);
  }
  static class ShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsShortSeqExpr {
    ShortSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class ShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsShortSeqExpr {
    ShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class ShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsShortExpr {
    ShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsStringSeqExpr stringSeq(XsStringExpr... items) {
    return new StringSeqListImpl(items);
  }
  static class StringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsStringSeqExpr {
    StringSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class StringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsStringSeqExpr {
    StringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class StringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsStringExpr {
    StringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsTimeSeqExpr timeSeq(XsTimeExpr... items) {
    return new TimeSeqListImpl(items);
  }
  static class TimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeSeqExpr {
    TimeSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class TimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeSeqExpr {
    TimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class TimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTimeExpr {
    TimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsTokenSeqExpr tokenSeq(XsTokenExpr... items) {
    return new TokenSeqListImpl(items);
  }
  static class TokenSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenSeqExpr {
    TokenSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class TokenSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenSeqExpr {
    TokenSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class TokenCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsTokenExpr {
    TokenCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items) {
    return new UnsignedByteSeqListImpl(items);
  }
  static class UnsignedByteSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteSeqExpr {
    UnsignedByteSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class UnsignedByteSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteSeqExpr {
    UnsignedByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class UnsignedByteCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedByteExpr {
    UnsignedByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items) {
    return new UnsignedIntSeqListImpl(items);
  }
  static class UnsignedIntSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntSeqExpr {
    UnsignedIntSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class UnsignedIntSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntSeqExpr {
    UnsignedIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class UnsignedIntCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedIntExpr {
    UnsignedIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items) {
    return new UnsignedLongSeqListImpl(items);
  }
  static class UnsignedLongSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongSeqExpr {
    UnsignedLongSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class UnsignedLongSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongSeqExpr {
    UnsignedLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class UnsignedLongCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedLongExpr {
    UnsignedLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items) {
    return new UnsignedShortSeqListImpl(items);
  }
  static class UnsignedShortSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortSeqExpr {
    UnsignedShortSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class UnsignedShortSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortSeqExpr {
    UnsignedShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class UnsignedShortCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUnsignedShortExpr {
    UnsignedShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items) {
    return new UntypedAtomicSeqListImpl(items);
  }
  static class UntypedAtomicSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicSeqExpr {
    UntypedAtomicSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class UntypedAtomicSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicSeqExpr {
    UntypedAtomicSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class UntypedAtomicCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsUntypedAtomicExpr {
    UntypedAtomicCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items) {
    return new YearMonthDurationSeqListImpl(items);
  }
  static class YearMonthDurationSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationSeqExpr {
    YearMonthDurationSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class YearMonthDurationSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationSeqExpr {
    YearMonthDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class YearMonthDurationCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements XsYearMonthDurationExpr {
    YearMonthDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }

  }
