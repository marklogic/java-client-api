/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import jakarta.xml.bind.DatatypeConverter;
import javax.xml.datatype.Duration;

/**
 * ValueConverter roundtrips Java atomic values to and from XML.
 *
 * Note that ValueConverter does not enable Java equivalents for the following
 * XML Schema concrete atomic datatypes:  anyUri, gYear, gMonth, gDay, gMonthDay,
 * hexBinary, ID, language, Name, NCName, xs:unsignedByte, or token.
 */
public class ValueConverter {
  /**
   * ValueProcessor specifies a callback for processing a Java value
   * converted to the lexical form of an XML value.
   */
  public interface ValueProcessor {
    public void process(Object original, String type, String value);
  }

  final static private BigInteger MIN_UNSIGNED       = new BigInteger("0");
  /**
   * The largest unsigned long.
   */
  final static public BigInteger MAX_UNSIGNED_LONG  = new BigInteger("18446744073709551615");
  /**
   * The largest unsigned int.
   */
  final static public long       MAX_UNSIGNED_INT   = 4294967295l;
  /**
   * The largest unsigned short.
   */
  final static public int        MAX_UNSIGNED_SHORT = 65535;

  static private Pattern instantPattern = null;

  private ValueConverter() {
    super();
  }

  static public void convertFromJava(Object value, ValueProcessor processor) {
    if (value == null)
      processor.process(null, null, null);
    else if (value instanceof BigDecimal)
      convertFromJava((BigDecimal) value, processor);
    else if (value instanceof BigInteger)
      convertFromJava((BigInteger) value, processor);
    else if (value instanceof Boolean)
      convertFromJava((Boolean) value, processor);
    else if (value instanceof Byte)
      convertFromJava((Byte) value, processor);
    else if (value instanceof byte[])
      convertFromJava((byte[]) value, processor);
    else if (value instanceof Calendar)
      convertFromJava((Calendar) value, processor);
    else if (value instanceof Double)
      convertFromJava((Double) value, processor);
    else if (value instanceof Duration)
      convertFromJava((Duration) value, processor);
    else if (value instanceof Float)
      convertFromJava((Float) value, processor);
    else if (value instanceof Integer)
      convertFromJava((Integer) value, processor);
    else if (value instanceof Long)
      convertFromJava((Long) value, processor);
    else if (value instanceof Short)
      convertFromJava((Short) value, processor);
    else if (value instanceof String)
      convertFromJava((String) value, processor);
    else
      processor.process(
        value,
        "xs:anySimpleType",
        DatatypeConverter.printString(value.toString())
      );
  }
  static public void convertFromJava(BigDecimal value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(value,"xs:decimal", BigDecimalToString(value));
  }
  static public void convertFromJava(BigInteger value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    if (MIN_UNSIGNED.compareTo(value) <= 0 &&
      MAX_UNSIGNED_LONG.compareTo(value) >= 0)
      // DatatypeConverter doesn't provide printUnsignedLong()
      processor.process(
        value,
        "xs:unsignedLong",
        DatatypeConverter.printInteger(value)
      );
    else
      processor.process(
        value,
        "xs:integer",
        DatatypeConverter.printInteger(value)
      );
  }
  static public void convertFromJava(Boolean value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(value,"xs:boolean", BooleanToString(value));
  }
  static public void convertFromJava(Byte value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(
      value,
      "xs:byte",
      DatatypeConverter.printByte(value)
    );
  }
  static public void convertFromJava(byte[] value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(
      value,
      "xs:base64Binary",
      DatatypeConverter.printBase64Binary(value)
    );
  }
  static public void convertFromJava(Calendar value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    if (!value.isSet(Calendar.DAY_OF_MONTH))
      processor.process(
        value,
        "xs:time",
        DatatypeConverter.printTime(value)
      );
    else if (value.isSet(Calendar.HOUR_OF_DAY))
      processor.process(
        value,
        "xs:dateTime",
        DatatypeConverter.printDateTime(value)
      );
    else
      processor.process(
        value,
        "xs:date",
        DatatypeConverter.printDate(value)
      );
  }
  static public void convertFromJava(Double value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(value,"xs:double", DoubleToString(value));
  }
  static public void convertFromJava(Duration value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(
      value,
      "xs:"+value.getXMLSchemaType().getLocalPart(),
      value.toString()
    );
  }
  static public void convertFromJava(Float value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(value,"xs:float", FloatToString(value));
  }
  static public void convertFromJava(Integer value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    int ival = value.intValue();
    if (0 <= ival && ival <= MAX_UNSIGNED_SHORT)
      processor.process(
        value,
        "xs:unsignedShort",
        DatatypeConverter.printUnsignedShort(ival)
      );
    else
      processor.process(value, "xs:int", IntegerPrimitiveToString(ival));
  }
  static public void convertFromJava(Long value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    long longVal = ((Long) value).longValue();
    if (0 <= longVal && longVal <= MAX_UNSIGNED_INT)
      processor.process(
        value,
        "xs:unsignedInt",
        DatatypeConverter.printUnsignedInt(longVal)
      );
    else
      processor.process(value,"xs:long", LongPrimitiveToString(longVal));
  }
  static public void convertFromJava(Short value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(
      value,
      "xs:short",
      DatatypeConverter.printShort(value)
    );
  }
  static public void convertFromJava(String value, ValueProcessor processor) {
    if (value == null) {
      processor.process(null, null, null);
      return;
    }
    processor.process(value,"xs:string", StringToString(value));
  }
  static public Object convertToJava(String type, String value) {
    if ("xs:anySimpleType".equals(type))
      return DatatypeConverter.parseAnySimpleType(value);
    if ("xs:base64Binary".equals(type))
      return DatatypeConverter.parseBase64Binary(value);
    if ("xs:boolean".equals(type)) return StringToBoolean(value);
    if ("xs:byte".equals(type))
      return DatatypeConverter.parseByte(value);
    if ("xs:date".equals(type))
      return DatatypeConverter.parseDate(value);
    if ("xs:dateTime".equals(type))
      return DatatypeConverter.parseDateTime(value);
    if ("xs:dayTimeDuration".equals(type))
      return Utilities.getDatatypeFactory().newDurationDayTime(value);
    if ("xs:decimal".equals(type))
      return DatatypeConverter.parseDecimal(value);
    if ("xs:double".equals(type)) return StringToDouble(value);
    if ("xs:duration".equals(type))
      return Utilities.getDatatypeFactory().newDuration(value);
    if ("xs:float".equals(type)) return StringToFloat(value);
    if ("xs:int".equals(type)) return StringToInteger(value);
    if ("xs:integer".equals(type))
      return DatatypeConverter.parseInteger(value);
    if ("xs:long".equals(type)) return StringToLong(value);
    if ("xs:short".equals(type))
      return DatatypeConverter.parseShort(value);
    if ("xs:string".equals(type))
      return DatatypeConverter.parseString(value);
    if ("xs:time".equals(type))
      return DatatypeConverter.parseTime(value);
    if ("xs:unsignedInt".equals(type))
      return DatatypeConverter.parseUnsignedInt(value);
    if ("xs:unsignedLong".equals(type)) {
      BigInteger bi = DatatypeConverter.parseInteger(value);
      if (bi.compareTo(MAX_UNSIGNED_LONG) < 0) {
        return bi.longValue();
      } else {
        return bi;
      }
    } if ("xs:unsignedShort".equals(type))
      return DatatypeConverter.parseUnsignedShort(value);
    if ("xs:yearMonthDuration".equals(type))
      return Utilities.getDatatypeFactory().newDurationYearMonth(value);
    return value;
  }
  @SuppressWarnings("unchecked")
  static public <T> T convertToJava(String type, String value, Class<T> as) {
    return (T) convertToJava(type, value);
  }

  static public String ObjectToString(Object value) {
    return (value == null || value instanceof String) ? (String) value : value.toString();
  }
  static public Stream<String> ObjectToString(Stream<?> values) {
    return (values == null) ? null : values.map(ValueConverter::ObjectToString);
  }

  static public String BigDecimalToString(BigDecimal value) {
    return (value == null) ? null : DatatypeConverter.printDecimal(value);
  }
  static public Stream<String> BigDecimalToString(Stream<? extends BigDecimal> values) {
    return (values == null) ? null : values.map(ValueConverter::BigDecimalToString);
  }
  static public BigDecimal StringToBigDecimal(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DatatypeConverter.parseDecimal(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to BigDecimal: "+value, e);
    }
  }
  static public Stream<BigDecimal> StringToBigDecimal(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToBigDecimal);
  }
  static public String BooleanToString(Boolean value) {
    return (value == null) ? null : BooleanPrimitiveToString(value.booleanValue());
  }
  static public String BooleanPrimitiveToString(boolean value) {
    return DatatypeConverter.printBoolean(value);
  }
  static public Stream<String> BooleanToString(Stream<? extends Boolean> values) {
    return (values == null) ? null : values.map(ValueConverter::BooleanToString);
  }
  static public Boolean StringToBoolean(String value) {
    return (value == null || value.length() == 0) ? null :
          Boolean.valueOf(StringToBooleanPrimitive(value));
  }
  static public boolean StringToBooleanPrimitive(String value) {
    try {
      return DatatypeConverter.parseBoolean(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to boolean: "+value, e);
    }
  }
  static public Stream<Boolean> StringToBoolean(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToBoolean);
  }
  static public String DateToString(Date value) {
    if (value == null) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(value);
    return DatatypeConverter.printDateTime(cal);
  }
  static public Stream<String> DateToString(Stream<? extends Date> values) {
    return (values == null) ? null : values.map(ValueConverter::DateToString);
  }
  static public Date StringToDate(String value) {
    if (value == null || value.length() == 0) {
      return null;
    }
    try {
      Calendar cal = DatatypeConverter.parseDateTime(value);
      return cal.getTime();
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to Date: "+value, e);
    }
  }
  static public Stream<Date> StringToDate(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToDate);
  }
  static public String DoubleToString(Double value) {
    return (value == null) ? null : DoublePrimitiveToString(value.doubleValue());
  }
  static public String DoublePrimitiveToString(double value) {
    return DatatypeConverter.printDouble(value);
  }
  static public Stream<String> DoubleToString(Stream<? extends Double> values) {
    return (values == null) ? null : values.map(ValueConverter::DoubleToString);
  }
  static public Double StringToDouble(String value) {
    return (value == null || value.length() == 0) ? null :
          Double.valueOf(StringToDoublePrimitive(value));
  }
  static public double StringToDoublePrimitive(String value) {
    try {
      return DatatypeConverter.parseDouble(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to double: "+value, e);
    }
  }
  static public Stream<Double> StringToDouble(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToDouble);
  }
  static public String DurationToString(java.time.Duration value) {
    return (value == null) ? null : value.toString();
  }
  static public Stream<String> DurationToString(Stream<? extends java.time.Duration> values) {
    return (values == null) ? null : values.map(ValueConverter::DurationToString);
  }
  static public java.time.Duration StringToDuration(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            java.time.Duration.parse(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to Duration: "+value, e);
    }
  }
  static public Stream<java.time.Duration> StringToDuration(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToDuration);
  }
  static public String FloatToString(Float value) {
    return (value == null) ? null : FloatPrimitiveToString(value.floatValue());
  }
  static public String FloatPrimitiveToString(float value) {
    return DatatypeConverter.printFloat(value);
  }
  static public Stream<String> FloatToString(Stream<? extends Float> values) {
    return (values == null) ? null : values.map(ValueConverter::FloatToString);
  }
  static public Float StringToFloat(String value) {
    return (value == null || value.length() == 0) ? null : Float.valueOf(StringToFloatPrimitive(value));
  }
  static public float StringToFloatPrimitive(String value) {
    try {
      return DatatypeConverter.parseFloat(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to float: "+value, e);
    }
  }
  static public Stream<Float> StringToFloat(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToFloat);
  }
  static public String IntegerToString(Integer value) {
    return (value == null) ? null : IntegerPrimitiveToString(value.intValue());
  }
  static public String IntegerPrimitiveToString(int value) {
    return DatatypeConverter.printInt(value);
  }
  static public Stream<String> IntegerToString(Stream<? extends Integer> values) {
    return (values == null) ? null : values.map(ValueConverter::IntegerToString);
  }
  static public Integer StringToInteger(String value) {
    return (value == null || value.length() == 0) ? null :
          Integer.valueOf(StringToIntegerPrimitive(value));
  }
  static public int StringToIntegerPrimitive(String value) {
    try {
      return DatatypeConverter.parseInt(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to int: "+value, e);
    }
  }
  static public Stream<Integer> StringToInteger(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToInteger);
  }
  static public String LocalDateTimeToString(LocalDateTime value) {
    return (value == null) ? null : value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
  static public Stream<String> LocalDateTimeToString(Stream<? extends LocalDateTime> values) {
    return (values == null) ? null : values.map(ValueConverter::LocalDateTimeToString);
  }
  static public LocalDateTime StringToLocalDateTime(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(
                  getInstantPattern().matcher(value).replaceFirst(""),
                  LocalDateTime::from
            );
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to LocalDateTime: "+value, e);
    }
  }
  static public Stream<LocalDateTime> StringToLocalDateTime(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToLocalDateTime);
  }
  static public String LocalDateToString(LocalDate value) {
    return (value == null) ? null : value.format(DateTimeFormatter.ISO_LOCAL_DATE);
  }
  static public Stream<String> LocalDateToString(Stream<? extends LocalDate> values) {
    return (values == null) ? null : values.map(ValueConverter::LocalDateToString);
  }
  static public LocalDate StringToLocalDate(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DateTimeFormatter.ISO_LOCAL_DATE.parse(
                  getInstantPattern().matcher(value).replaceFirst(""),
                  LocalDate::from
            );
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to LocalDate: "+value, e);
    }
  }
  static public Stream<LocalDate> StringToLocalDate(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToLocalDate);
  }
  static public String LocalTimeToString(LocalTime value) {
    return (value == null) ? null : value.format(DateTimeFormatter.ISO_LOCAL_TIME);
  }
  static public Stream<String> LocalTimeToString(Stream<? extends LocalTime> values) {
    return (values == null) ? null : values.map(ValueConverter::LocalTimeToString);
  }
  static public LocalTime StringToLocalTime(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DateTimeFormatter.ISO_LOCAL_TIME.parse(
                  getInstantPattern().matcher(value).replaceFirst(""),
                  LocalTime::from
            );
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to LocalTime: "+value, e);
    }
  }
  static public Stream<LocalTime> StringToLocalTime(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToLocalTime);
  }
  static public String LongToString(Long value) {
    return (value == null) ? null : LongPrimitiveToString(value.longValue());
  }
  static public String LongPrimitiveToString(long value) {
    return DatatypeConverter.printLong(value);
  }
  static public Stream<String> LongToString(Stream<? extends Long> values) {
    return (values == null) ? null : values.map(ValueConverter::LongToString);
  }
  static public Long StringToLong(String value) {
    return (value == null || value.length() == 0) ? null :
          Long.valueOf(StringToLongPrimitive(value));
  }
  static public long StringToLongPrimitive(String value) {
    try {
      return DatatypeConverter.parseLong(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to long: "+value, e);
    }
  }
  static public Stream<Long> StringToLong(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToLong);
  }
  static public String OffsetDateTimeToString(OffsetDateTime value) {
    return (value == null) ? null : value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }
  static public Stream<String> OffsetDateTimeToString(Stream<? extends OffsetDateTime> values) {
    return (values == null) ? null : values.map(ValueConverter::OffsetDateTimeToString);
  }
  static public OffsetDateTime StringToOffsetDateTime(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to OffsetDateTime: "+value, e);
    }
  }
  static public Stream<OffsetDateTime> StringToOffsetDateTime(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToOffsetDateTime);
  }
  static public String OffsetTimeToString(OffsetTime value) {
    return (value == null) ? null : value.format(DateTimeFormatter.ISO_OFFSET_TIME);
  }
  static public Stream<String> OffsetTimeToString(Stream<? extends OffsetTime> values) {
    return (values == null) ? null : values.map(ValueConverter::OffsetTimeToString);
  }
  static public OffsetTime StringToOffsetTime(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DateTimeFormatter.ISO_OFFSET_TIME.parse(value, OffsetTime::from);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to OffsetTime: "+value, e);
    }
  }
  static public Stream<OffsetTime> StringToOffsetTime(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToOffsetTime);
  }
  static public String StringToString(String value) {
    try {
      return (value == null || value.length() == 0) ? null :
            DatatypeConverter.printString(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert: "+value, e);
    }
  }
  static public Stream<String> StringToString(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToString);
  }
  static public String UnsignedIntegerToString(Integer value) {
    return (value == null) ? null : UnsignedIntegerPrimitiveToString(value.intValue());
  }
  static public String UnsignedIntegerPrimitiveToString(int value){
    return Integer.toUnsignedString(value);
  }
  static public Stream<String> UnsignedIntegerToString(Stream<? extends Integer> values) {
    return (values == null) ? null : values.map(ValueConverter::UnsignedIntegerToString);
  }
  static public Integer StringToUnsignedInteger(String value) {
    return (value == null || value.length() == 0) ? null :
          Integer.valueOf(StringToUnsignedIntegerPrimitive(value));
  }
  static public int StringToUnsignedIntegerPrimitive(String value) {
    try {
      return Integer.parseUnsignedInt(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to unsigned int: "+value, e);
    }
  }
  static public Stream<Integer> StringToUnsignedInteger(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToUnsignedInteger);
  }
  static public String UnsignedLongToString(Long value) {
    return (value == null) ? null : UnsignedLongPrimitiveToString(value.longValue());
  }
  static public String UnsignedLongPrimitiveToString(long value){
    return Long.toUnsignedString(value);
  }
  static public Stream<String> UnsignedLongToString(Stream<? extends Long> values) {
    return (values == null) ? null : values.map(ValueConverter::UnsignedLongToString);
  }
  static public Long StringToUnsignedLong(String value) {
    return (value == null || value.length() == 0) ? null : Long.valueOf(StringToUnsignedIntegerLong(value));
  }
  static public long StringToUnsignedIntegerLong(String value) {
    try {
      return Long.parseUnsignedLong(value);
    } catch(Exception e) {
      throw new IllegalArgumentException("Could not convert to unsigned long: "+value, e);
    }
  }
  static public Stream<Long> StringToUnsignedLong(Stream<? extends String> values) {
    return (values == null) ? null : values.map(ValueConverter::StringToUnsignedLong);
  }

  static private Pattern getInstantPattern() {
    // okay if one thread overwrites another during lazy initialization
    if (instantPattern == null) {
      instantPattern = Pattern.compile("Z.*$");
    }
    return instantPattern;
  }

  static public <I> String[] convert(I[] in, Function<I, String> converter) {
      if (in == null) return null;
      String[] out = new String[in.length];
      for (int i=0; i < in.length; i++) {
        out[i] = converter.apply(in[i]);
      }
      return out;
    }
}
