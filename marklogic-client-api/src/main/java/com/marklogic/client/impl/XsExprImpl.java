/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;



import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class XsExprImpl extends XsValueImpl implements XsExpr {

  final static XsExprImpl xs = new XsExprImpl();

  XsExprImpl() {
  }


  @Override
  public ServerExpression anyURI(ServerExpression arg1) {
    return new AnyURICallImpl("xs", "anyURI", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression base64Binary(ServerExpression arg1) {
    return new Base64BinaryCallImpl("xs", "base64Binary", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression booleanExpr(ServerExpression arg1) {
    return new BooleanCallImpl("xs", "boolean", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression byteExpr(ServerExpression arg1) {
    return new ByteCallImpl("xs", "byte", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression date(ServerExpression arg1) {
    return new DateCallImpl("xs", "date", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression dateTime(ServerExpression arg1) {
    return new DateTimeCallImpl("xs", "dateTime", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression dayTimeDuration(ServerExpression arg1) {
    return new DayTimeDurationCallImpl("xs", "dayTimeDuration", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression decimal(ServerExpression arg1) {
    return new DecimalCallImpl("xs", "decimal", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression doubleExpr(ServerExpression arg1) {
    return new DoubleCallImpl("xs", "double", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression floatExpr(ServerExpression arg1) {
    return new FloatCallImpl("xs", "float", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression gDay(ServerExpression arg1) {
    return new GDayCallImpl("xs", "gDay", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression gMonth(ServerExpression arg1) {
    return new GMonthCallImpl("xs", "gMonth", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression gMonthDay(ServerExpression arg1) {
    return new GMonthDayCallImpl("xs", "gMonthDay", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression gYear(ServerExpression arg1) {
    return new GYearCallImpl("xs", "gYear", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression gYearMonth(ServerExpression arg1) {
    return new GYearMonthCallImpl("xs", "gYearMonth", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression hexBinary(ServerExpression arg1) {
    return new HexBinaryCallImpl("xs", "hexBinary", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression intExpr(ServerExpression arg1) {
    return new IntCallImpl("xs", "int", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression integer(ServerExpression arg1) {
    return new IntegerCallImpl("xs", "integer", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression language(ServerExpression arg1) {
    return new LanguageCallImpl("xs", "language", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression longExpr(ServerExpression arg1) {
    return new LongCallImpl("xs", "long", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression Name(ServerExpression arg1) {
    return new NameCallImpl("xs", "Name", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression NCName(ServerExpression arg1) {
    return new NCNameCallImpl("xs", "NCName", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression negativeInteger(ServerExpression arg1) {
    return new NegativeIntegerCallImpl("xs", "negativeInteger", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression NMTOKEN(ServerExpression arg1) {
    return new NMTOKENCallImpl("xs", "NMTOKEN", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression nonNegativeInteger(ServerExpression arg1) {
    return new NonNegativeIntegerCallImpl("xs", "nonNegativeInteger", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression nonPositiveInteger(ServerExpression arg1) {
    return new NonPositiveIntegerCallImpl("xs", "nonPositiveInteger", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression normalizedString(ServerExpression arg1) {
    return new NormalizedStringCallImpl("xs", "normalizedString", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression numeric(ServerExpression arg1) {
    return new NumericCallImpl("xs", "numeric", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression positiveInteger(ServerExpression arg1) {
    return new PositiveIntegerCallImpl("xs", "positiveInteger", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression QName(ServerExpression arg1) {
    return new QNameCallImpl("xs", "QName", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression shortExpr(ServerExpression arg1) {
    return new ShortCallImpl("xs", "short", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression string(ServerExpression arg1) {
    return new StringCallImpl("xs", "string", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression time(ServerExpression arg1) {
    return new TimeCallImpl("xs", "time", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression token(ServerExpression arg1) {
    return new TokenCallImpl("xs", "token", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression unsignedByte(ServerExpression arg1) {
    return new UnsignedByteCallImpl("xs", "unsignedByte", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression unsignedInt(ServerExpression arg1) {
    return new UnsignedIntCallImpl("xs", "unsignedInt", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression unsignedLong(ServerExpression arg1) {
    return new UnsignedLongCallImpl("xs", "unsignedLong", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression unsignedShort(ServerExpression arg1) {
    return new UnsignedShortCallImpl("xs", "unsignedShort", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression untypedAtomic(ServerExpression arg1) {
    return new UntypedAtomicCallImpl("xs", "untypedAtomic", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression yearMonthDuration(ServerExpression arg1) {
    return new YearMonthDurationCallImpl("xs", "yearMonthDuration", new Object[]{ arg1 });
  }

  static class AnyAtomicTypeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    AnyAtomicTypeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class AnyAtomicTypeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    AnyAtomicTypeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class AnyURISeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    AnyURISeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class AnyURICallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    AnyURICallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class Base64BinarySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    Base64BinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class Base64BinaryCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    Base64BinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class BooleanSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    BooleanSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class BooleanCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    BooleanCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class ByteSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ByteCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class DateSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DateSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DateCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DateCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class DateTimeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DateTimeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class DayTimeDurationSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DayTimeDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DayTimeDurationCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DayTimeDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class DecimalSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DecimalSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DecimalCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DecimalCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class DoubleSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DoubleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class DoubleCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    DoubleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class FloatSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    FloatSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class FloatCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    FloatCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class GDaySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GDayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class GMonthSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GMonthCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class GMonthDaySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GMonthDaySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GMonthDayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GMonthDayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class GYearSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GYearSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GYearCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GYearCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class GYearMonthSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GYearMonthSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class GYearMonthCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    GYearMonthCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class HexBinarySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    HexBinarySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class HexBinaryCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    HexBinaryCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class IntSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class IntCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class IntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class IntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    IntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class LanguageSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LanguageSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LanguageCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LanguageCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class LongSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LongCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NCNameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NCNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NCNameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NCNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NegativeIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NegativeIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NMTOKENSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NMTOKENSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NMTOKENCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NMTOKENCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NonNegativeIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NonNegativeIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NonNegativeIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NonNegativeIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NonPositiveIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NonPositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NonPositiveIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NonPositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NormalizedStringSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NormalizedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NormalizedStringCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NormalizedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class NumericSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NumericSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class NumericCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    NumericCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class PositiveIntegerSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    PositiveIntegerSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class PositiveIntegerCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    PositiveIntegerCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class QNameSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    QNameSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class QNameCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    QNameCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class ShortSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ShortCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class StringSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    StringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class StringCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    StringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class TimeSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    TimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class TimeCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    TimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class TokenSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    TokenSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class TokenCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    TokenCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class UnsignedByteSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedByteSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedByteCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedByteCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class UnsignedIntSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedIntSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedIntCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedIntCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class UnsignedLongSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedLongSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedLongCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedLongCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class UnsignedShortSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedShortSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UnsignedShortCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UnsignedShortCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class UntypedAtomicSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UntypedAtomicSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class UntypedAtomicCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    UntypedAtomicCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class YearMonthDurationSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    YearMonthDurationSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class YearMonthDurationCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    YearMonthDurationCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
