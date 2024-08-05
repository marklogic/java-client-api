/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import jakarta.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.marklogic.client.expression.XsValue;
import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnySimpleTypeSeqVal;
import com.marklogic.client.type.XsAnySimpleTypeVal;
import com.marklogic.client.type.XsAnyURISeqVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsBase64BinarySeqVal;
import com.marklogic.client.type.XsBase64BinaryVal;
import com.marklogic.client.type.XsBooleanSeqVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsByteSeqVal;
import com.marklogic.client.type.XsByteVal;
import com.marklogic.client.type.XsDateSeqVal;
import com.marklogic.client.type.XsDateTimeSeqVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDateVal;
import com.marklogic.client.type.XsDayTimeDurationSeqVal;
import com.marklogic.client.type.XsDayTimeDurationVal;
import com.marklogic.client.type.XsDecimalSeqVal;
import com.marklogic.client.type.XsDecimalVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsDurationVal;
import com.marklogic.client.type.XsFloatSeqVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsGDaySeqVal;
import com.marklogic.client.type.XsGDayVal;
import com.marklogic.client.type.XsGMonthDaySeqVal;
import com.marklogic.client.type.XsGMonthDayVal;
import com.marklogic.client.type.XsGMonthSeqVal;
import com.marklogic.client.type.XsGMonthVal;
import com.marklogic.client.type.XsGYearMonthSeqVal;
import com.marklogic.client.type.XsGYearMonthVal;
import com.marklogic.client.type.XsGYearSeqVal;
import com.marklogic.client.type.XsGYearVal;
import com.marklogic.client.type.XsHexBinarySeqVal;
import com.marklogic.client.type.XsHexBinaryVal;
import com.marklogic.client.type.XsIntSeqVal;
import com.marklogic.client.type.XsIntVal;
import com.marklogic.client.type.XsIntegerSeqVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsLongSeqVal;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsNonNegativeIntegerVal;
import com.marklogic.client.type.XsNumericVal;
import com.marklogic.client.type.XsQNameSeqVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsShortSeqVal;
import com.marklogic.client.type.XsShortVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsTimeSeqVal;
import com.marklogic.client.type.XsTimeVal;
import com.marklogic.client.type.XsUnsignedByteSeqVal;
import com.marklogic.client.type.XsUnsignedByteVal;
import com.marklogic.client.type.XsUnsignedIntSeqVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongSeqVal;
import com.marklogic.client.type.XsUnsignedLongVal;
import com.marklogic.client.type.XsUnsignedShortSeqVal;
import com.marklogic.client.type.XsUnsignedShortVal;
import com.marklogic.client.type.XsUntypedAtomicSeqVal;
import com.marklogic.client.type.XsUntypedAtomicVal;
import com.marklogic.client.type.XsYearMonthDurationSeqVal;
import com.marklogic.client.type.XsYearMonthDurationVal;

public class XsValueImpl implements XsValue {
    @Override
    public XsAnyURIVal anyURI(String value) {
        return new AnyURIValImpl(value);
    }
    @Override
    public XsAnyURISeqVal anyURISeq(String... values) {
        return new AnyURISeqValImpl(values);
    }
    @Override
    public XsAnyURISeqVal anyURISeq(XsAnyURIVal... values) {
        return new AnyURISeqValImpl(values);
    }

    @Override
    public XsBase64BinaryVal base64Binary(byte[] value) {
        return new Base64BinaryValImpl(value);
    }
    @Override
    public XsBase64BinarySeqVal base64BinarySeq(byte[]... values) {
        return new Base64BinarySeqValImpl(values);
    }
    @Override
    public XsBase64BinarySeqVal base64BinarySeq(XsBase64BinaryVal... values) {
        return new Base64BinarySeqValImpl(values);
    }

    @Override
    public XsBooleanVal booleanVal(boolean value) {
        return new BooleanValImpl(value);
    }
    @Override
    public XsBooleanSeqVal booleanSeq(boolean... values) {
        return new BooleanSeqValImpl(values);
    }
    @Override
    public XsBooleanSeqVal booleanSeq(XsBooleanVal... values) {
        return new BooleanSeqValImpl(values);
    }

    @Override
    public XsByteVal byteVal(byte value) {
        return new ByteValImpl(value);
    }
    @Override
    public XsByteSeqVal byteSeq(byte... values) {
        return new ByteSeqValImpl(values);
    }
    @Override
    public XsByteSeqVal byteSeq(XsByteVal... values) {
        return new ByteSeqValImpl(values);
    }

    @Override
    public XsDateVal date(String value) {
        return new DateValImpl(value);
    }
    @Override
    public XsDateVal date(Calendar value) {
        return new DateValImpl(value);
    }
    @Override
    public XsDateVal date(XMLGregorianCalendar value) {
        return new DateValImpl(value);
    }
    @Override
    public XsDateSeqVal dateSeq(String... values) {
        return new DateSeqValImpl(values);
    }
    @Override
    public XsDateSeqVal dateSeq(Calendar... values) {
        return new DateSeqValImpl(values);
    }
    @Override
    public XsDateSeqVal dateSeq(XMLGregorianCalendar... values) {
        return new DateSeqValImpl(values);
    }
    @Override
    public XsDateSeqVal dateSeq(XsDateVal... values) {
        return new DateSeqValImpl(values);
    }

    @Override
    public XsDateTimeVal dateTime(String value) {
        return new DateTimeValImpl(value);
    }
    @Override
    public XsDateTimeVal dateTime(Date value) {
        return new DateTimeValImpl(value);
    }
    @Override
    public XsDateTimeVal dateTime(Calendar value) {
        return new DateTimeValImpl(value);
    }
    @Override
    public XsDateTimeVal dateTime(XMLGregorianCalendar value) {
        return new DateTimeValImpl(value);
    }
    @Override
    public XsDateTimeSeqVal dateTimeSeq(String... values) {
        return new DateTimeSeqValImpl(values);
    }
    @Override
    public XsDateTimeSeqVal dateTimeSeq(Date... values) {
        return new DateTimeSeqValImpl(values);
    }
    @Override
    public XsDateTimeSeqVal dateTimeSeq(Calendar... values) {
        return new DateTimeSeqValImpl(values);
    }
    @Override
    public XsDateTimeSeqVal dateTimeSeq(XMLGregorianCalendar... values) {
        return new DateTimeSeqValImpl(values);
    }
    @Override
    public XsDateTimeSeqVal dateTimeSeq(XsDateTimeVal... values) {
        return new DateTimeSeqValImpl(values);
    }

    @Override
    public XsDayTimeDurationVal dayTimeDuration(String value) {
        return new DayTimeDurationValImpl(value);
    }
    @Override
    public XsDayTimeDurationVal dayTimeDuration(Duration value) {
        return new DayTimeDurationValImpl(value);
    }
    @Override
    public XsDayTimeDurationSeqVal dayTimeDurationSeq(String... values) {
        return new DayTimeDurationSeqValImpl(values);
    }
    @Override
    public XsDayTimeDurationSeqVal dayTimeDurationSeq(Duration... values) {
        return new DayTimeDurationSeqValImpl(values);
    }
    @Override
    public XsDayTimeDurationSeqVal dayTimeDurationSeq(XsDayTimeDurationVal... values) {
        return new DayTimeDurationSeqValImpl(values);
    }

    @Override
    public XsDecimalVal decimal(String value) {
        return new DecimalValImpl(value);
    }
    @Override
    public XsDecimalVal decimal(long value) {
        return new DecimalValImpl(value);
    }
    @Override
    public XsDecimalVal decimal(double value) {
        return new DecimalValImpl(value);
    }
    @Override
    public XsDecimalVal decimal(BigDecimal value) {
        return new DecimalValImpl(value);
    }
    @Override
    public XsDecimalSeqVal decimalSeq(String... values) {
        return new DecimalSeqValImpl(values);
    }
    @Override
    public XsDecimalSeqVal decimalSeq(long... values) {
        return new DecimalSeqValImpl(values);
    }
    @Override
    public XsDecimalSeqVal decimalSeq(double... values) {
        return new DecimalSeqValImpl(values);
    }
    @Override
    public XsDecimalSeqVal decimalSeq(BigDecimal... values) {
        return new DecimalSeqValImpl(values);
    }
    @Override
    public XsDecimalSeqVal decimalSeq(XsDecimalVal... values) {
        return new DecimalSeqValImpl(values);
    }

    @Override
    public XsDoubleVal doubleVal(double value) {
        return new DoubleValImpl(value);
    }
    @Override
    public XsDoubleSeqVal doubleSeq(double... values) {
        return new DoubleSeqValImpl(values);
    }
    @Override
    public XsDoubleSeqVal doubleSeq(XsDoubleVal... values) {
        return new DoubleSeqValImpl(values);
    }

    @Override
    public XsFloatVal floatVal(float value) {
        return new FloatValImpl(value);
    }
    @Override
    public XsFloatSeqVal floatSeq(float... values) {
        return new FloatSeqValImpl(values);
    }
    @Override
    public XsFloatSeqVal floatSeq(XsFloatVal... values) {
        return new FloatSeqValImpl(values);
    }

    @Override
    public XsGDayVal gDay(String value) {
        return new GDayValImpl(value);
    }
    @Override
    public XsGDayVal gDay(XMLGregorianCalendar value) {
        return new GDayValImpl(value);
    }
    @Override
    public XsGDaySeqVal gDaySeq(String... values) {
        return new GDaySeqValImpl(values);
    }
    @Override
    public XsGDaySeqVal gDaySeq(XMLGregorianCalendar... values) {
        return new GDaySeqValImpl(values);
    }
    @Override
    public XsGDaySeqVal gDaySeq(XsGDayVal... values) {
        return new GDaySeqValImpl(values);
    }

    @Override
    public XsGMonthVal gMonth(String value) {
        return new GMonthValImpl(value);
    }
    @Override
    public XsGMonthVal gMonth(XMLGregorianCalendar value) {
        return new GMonthValImpl(value);
    }
    @Override
    public XsGMonthSeqVal gMonthSeq(String... values) {
        return new GMonthSeqValImpl(values);
    }
    @Override
    public XsGMonthSeqVal gMonthSeq(XMLGregorianCalendar... values) {
        return new GMonthSeqValImpl(values);
    }
    @Override
    public XsGMonthSeqVal gMonthSeq(XsGMonthVal... values) {
        return new GMonthSeqValImpl(values);
    }

    @Override
    public XsGMonthDayVal gMonthDay(String value) {
        return new GMonthDayValImpl(value);
    }
    @Override
    public XsGMonthDayVal gMonthDay(XMLGregorianCalendar value) {
        return new GMonthDayValImpl(value);
    }
    @Override
    public XsGMonthDaySeqVal gMonthDaySeq(String... values) {
        return new GMonthDaySeqValImpl(values);
    }
    @Override
    public XsGMonthDaySeqVal gMonthDaySeq(XMLGregorianCalendar... values) {
        return new GMonthDaySeqValImpl(values);
    }
    @Override
    public XsGMonthDaySeqVal gMonthDaySeq(XsGMonthDayVal... values) {
        return new GMonthDaySeqValImpl(values);
    }

    @Override
    public XsGYearVal gYear(String value) {
        return new GYearValImpl(value);
    }
    @Override
    public XsGYearVal gYear(XMLGregorianCalendar value) {
        return new GYearValImpl(value);
    }
    @Override
    public XsGYearSeqVal gYearSeq(String... values) {
        return new GYearSeqValImpl(values);
    }
    @Override
    public XsGYearSeqVal gYearSeq(XMLGregorianCalendar... values) {
        return new GYearSeqValImpl(values);
    }
    @Override
    public XsGYearSeqVal gYearSeq(XsGYearVal... values) {
        return new GYearSeqValImpl(values);
    }

    @Override
    public XsGYearMonthVal gYearMonth(String value) {
        return new GYearMonthValImpl(value);
    }
    @Override
    public XsGYearMonthVal gYearMonth(XMLGregorianCalendar value) {
        return new GYearMonthValImpl(value);
    }
    @Override
    public XsGYearMonthSeqVal gYearMonthSeq(String... values) {
        return new GYearMonthSeqValImpl(values);
    }
    @Override
    public XsGYearMonthSeqVal gYearMonthSeq(XMLGregorianCalendar... values) {
        return new GYearMonthSeqValImpl(values);
    }
    @Override
    public XsGYearMonthSeqVal gYearMonthSeq(XsGYearMonthVal... values) {
        return new GYearMonthSeqValImpl(values);
    }

    @Override
    public XsHexBinaryVal hexBinary(byte[] value) {
        return new HexBinaryValImpl(value);
    }
    @Override
    public XsHexBinarySeqVal hexBinarySeq(byte[]... values) {
        return new HexBinarySeqValImpl(values);
    }
    @Override
    public XsHexBinarySeqVal hexBinarySeq(XsHexBinaryVal... values) {
        return new HexBinarySeqValImpl(values);
    }

    @Override
    public XsIntVal intVal(int value) {
        return new IntValImpl(value);
    }
    @Override
    public XsIntSeqVal intSeq(int... values) {
        return new IntSeqValImpl(values);
    }
    @Override
    public XsIntSeqVal intSeq(XsIntVal... values) {
        return new IntSeqValImpl(values);
    }

    @Override
    public XsIntegerVal integer(String value) {
        return new IntegerValImpl(value);
    }
    @Override
    public XsIntegerVal integer(long value) {
        return new IntegerValImpl(value);
    }
    @Override
    public XsIntegerVal integer(BigInteger value) {
        return new IntegerValImpl(value);
    }
    @Override
    public XsIntegerSeqVal integerSeq(String... values) {
        return new IntegerSeqValImpl(values);
    }
    @Override
    public XsIntegerSeqVal integerSeq(long... values) {
        return new IntegerSeqValImpl(values);
    }
    @Override
    public XsIntegerSeqVal integerSeq(BigInteger... values) {
        return new IntegerSeqValImpl(values);
    }
    @Override
    public XsIntegerSeqVal integerSeq(XsIntegerVal... values) {
        return new IntegerSeqValImpl(values);
    }

    @Override
    public XsLongVal longVal(long value) {
        return new LongValImpl(value);
    }
    @Override
    public XsLongSeqVal longSeq(long... values) {
        return new LongSeqValImpl(values);
    }
    @Override
    public XsLongSeqVal longSeq(XsLongVal... values) {
        return new LongSeqValImpl(values);
    }

    @Override
    public XsShortVal shortVal(short value) {
        return new ShortValImpl(value);
    }
    @Override
    public XsShortSeqVal shortSeq(short... values) {
        return new ShortSeqValImpl(values);
    }
    @Override
    public XsShortSeqVal shortSeq(XsShortVal... values) {
        return new ShortSeqValImpl(values);
    }

    @Override
    public XsStringVal string(String value) {
        return new StringValImpl(value);
    }
    @Override
    public XsStringSeqVal stringSeq(String... values) {
        return new StringSeqValImpl(values);
    }
    @Override
    public XsStringSeqVal stringSeq(XsStringVal... values) {
       return new StringSeqValImpl(values);
    }

    @Override
    public XsTimeVal time(String value) {
        return new TimeValImpl(value);
    }
    @Override
    public XsTimeVal time(Calendar value) {
        return new TimeValImpl(value);
    }
    @Override
    public XsTimeVal time(XMLGregorianCalendar value) {
        return new TimeValImpl(value);
    }
    @Override
    public XsTimeSeqVal timeSeq(String... values) {
        return new TimeSeqValImpl(values);
    }
    @Override
    public XsTimeSeqVal timeSeq(Calendar... values) {
        return new TimeSeqValImpl(values);
    }
    @Override
    public XsTimeSeqVal timeSeq(XMLGregorianCalendar... values) {
        return new TimeSeqValImpl(values);
    }
    @Override
    public XsTimeSeqVal timeSeq(XsTimeVal... values) {
        return new TimeSeqValImpl(values);
    }

    @Override
    public XsUnsignedByteVal unsignedByte(byte value) {
        return new UnsignedByteValImpl(value);
    }
    @Override
    public XsUnsignedByteSeqVal unsignedByteSeq(byte... values) {
        return new UnsignedByteSeqValImpl(values);
    }
    @Override
    public XsUnsignedByteSeqVal unsignedByteSeq(XsUnsignedByteVal... values) {
        return new UnsignedByteSeqValImpl(values);
    }

    @Override
    public XsUnsignedIntVal unsignedInt(int value) {
        return new UnsignedIntValImpl(value);
    }
    @Override
    public XsUnsignedIntSeqVal unsignedIntSeq(int... values) {
        return new UnsignedIntSeqValImpl(values);
    }
    @Override
    public XsUnsignedIntSeqVal unsignedIntSeq(XsUnsignedIntVal... values) {
        return new UnsignedIntSeqValImpl(values);
    }

    @Override
    public XsUnsignedLongVal unsignedLong(long value) {
        return new UnsignedLongValImpl(value);
    }
    @Override
    public XsUnsignedLongSeqVal unsignedLongSeq(long... values) {
        return new UnsignedLongSeqValImpl(values);
    }
    @Override
    public XsUnsignedLongSeqVal unsignedLongSeq(XsUnsignedLongVal... values) {
        return new UnsignedLongSeqValImpl(values);
    }

    @Override
    public XsUnsignedShortVal unsignedShort(short value) {
        return new UnsignedShortValImpl(value);
    }
    @Override
    public XsUnsignedShortSeqVal unsignedShortSeq(short... values) {
        return new UnsignedShortSeqValImpl(values);
    }
    @Override
    public XsUnsignedShortSeqVal unsignedShortSeq(XsUnsignedShortVal... values) {
        return new UnsignedShortSeqValImpl(values);
    }

    @Override
    public XsUntypedAtomicVal untypedAtomic(String value) {
        return new UntypedAtomicValImpl(value);
    }
    @Override
    public XsUntypedAtomicSeqVal untypedAtomicSeq(String... values) {
        return new UntypedAtomicSeqValImpl(values);
    }
    @Override
    public XsUntypedAtomicSeqVal untypedAtomicSeq(XsUntypedAtomicVal... values) {
        return new UntypedAtomicSeqValImpl(values);
    }

    @Override
    public XsYearMonthDurationVal yearMonthDuration(String value) {
        return new YearMonthDurationValImpl(value);
    }
    @Override
    public XsYearMonthDurationVal yearMonthDuration(Duration value) {
        return new YearMonthDurationValImpl(value);
    }
    @Override
    public XsYearMonthDurationSeqVal yearMonthDurationSeq(String... values) {
        return new YearMonthDurationSeqValImpl(values);
    }
    @Override
    public XsYearMonthDurationSeqVal yearMonthDurationSeq(Duration... values) {
        return new YearMonthDurationSeqValImpl(values);
    }
    @Override
    public XsYearMonthDurationSeqVal yearMonthDurationSeq(XsYearMonthDurationVal... values) {
        return new YearMonthDurationSeqValImpl(values);
    }

    @Override
    public XsQNameVal QName(String localName) {
        return new QNameValImpl(localName);
    }
    @Override
    public XsQNameVal QName(String namespace, String localName) {
        return new QNameValImpl(namespace, localName);
    }
    @Override
    public XsQNameVal QName(QName value) {
        return new QNameValImpl(value);
    }
    @Override
    public XsQNameSeqVal QNameSeq(String... localNames) {
        return new QNameSeqValImpl(localNames);
    }
    @Override
    public XsQNameSeqVal QNameSeq(String namespace, String... localNames) {
        return new QNameSeqValImpl(namespace, localNames);
    }
    @Override
    public XsQNameSeqVal QNameSeq(QName... values) {
        return new QNameSeqValImpl(values);
    }
    @Override
    public XsQNameSeqVal QNameSeq(XsQNameVal... values) {
        return new QNameSeqValImpl(values);
    }

    static XsAnySimpleTypeSeqVal anySimpleTypes(XsAnySimpleTypeVal... items) {
        return new AnySimpleTypeSeqValImpl<>(BaseTypeImpl.convertList(items, AnySimpleTypeValImpl.class));
    }
    static XsAnyAtomicTypeSeqVal anyAtomicTypes(XsAnyAtomicTypeVal... items) {
        return new AnyAtomicTypeSeqValImpl<>(BaseTypeImpl.convertList(items, AnyAtomicTypeValImpl.class));
    }

    static class AnySimpleTypeSeqValImpl<T extends AnySimpleTypeValImpl>
    extends BaseTypeImpl.BaseListImpl<T>
    implements XsAnySimpleTypeSeqVal, BaseTypeImpl.BaseArgImpl {
        AnySimpleTypeSeqValImpl(T[] values) {
            super(values);
        }
        @Override
        public XsAnySimpleTypeVal[] getAnySimpleTypeItems() {
            return getArgsImpl();
        }
        @Override
        public T[] getItems() {
            return getArgsImpl();
        }
    }
    static class AnySimpleTypeValImpl implements XsAnySimpleTypeVal, BaseTypeImpl.BaseArgImpl {
        private String typePrefix = null;
        private String typeName   = null;
        AnySimpleTypeValImpl(String typeName) {
            this("xs", typeName);
        }
        AnySimpleTypeValImpl(String typePrefix, String typeName) {
            this.typePrefix = typePrefix;
            this.typeName   = typeName;
        }
        @Override
        public XsAnySimpleTypeVal[] getAnySimpleTypeItems() {
            return getItems();
        }
        @Override
        public XsAnySimpleTypeVal[] getItems() {
            return new XsAnySimpleTypeVal[]{this};
        }
        @Override
        public StringBuilder exportAst(StringBuilder strb) {
            return strb.append("{\"ns\":\"").append(typePrefix)
                    .append("\", \"fn\":\"").append(typeName)
                    .append("\", \"args\":[\"").append(toString()).append("\"]}");
        }
    }
    static class AnyAtomicTypeSeqValImpl<T extends AnyAtomicTypeValImpl>
    extends AnySimpleTypeSeqValImpl<T>
    implements XsAnyAtomicTypeSeqVal {
        AnyAtomicTypeSeqValImpl(T[] values) {
            super(values);
        }
        @Override
        public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
            return getItems();
        }
    }
    static class AnyAtomicTypeValImpl extends AnySimpleTypeValImpl implements XsAnyAtomicTypeVal, BaseTypeImpl.ParamBinder {
        AnyAtomicTypeValImpl(String typeName) {
            super(typeName);
        }
        AnyAtomicTypeValImpl(String typePrefix, String typeName) {
            super(typePrefix, typeName);
        }
        @Override
        public XsAnyAtomicTypeVal[] getAnyAtomicTypeItems() {
            return new XsAnyAtomicTypeVal[]{this};
        }
        @Override
        public String getParamQualifier() {
            String datatype = getClass().getSimpleName();
            datatype = datatype.substring(0, datatype.length() - "ValImpl".length());
            datatype = datatype.substring(0, 1).toLowerCase() + datatype.substring(1);
            return ":"+datatype;
        }
        @Override
        public String getParamValue() {
            return toString();
        }
    }

    // implementations
    static class AnyURISeqValImpl extends AnyAtomicTypeSeqValImpl<AnyURIValImpl> implements XsAnyURISeqVal {
        AnyURISeqValImpl(String[] values) {
            this((XsAnyURIVal[]) Arrays.stream(values)
                                  .map(val -> new AnyURIValImpl(val))
                                  .toArray(size -> new AnyURIValImpl[size]));
        }
        AnyURISeqValImpl(XsAnyURIVal[] values) {
            this(Arrays.copyOf(values, values.length, AnyURIValImpl[].class));
        }
        AnyURISeqValImpl(AnyURIValImpl[] values) {
            super(values);
        }

        @Override
        public XsAnyURIVal[] getAnyURIItems() {
            return getItems();
        }
    }
    static class AnyURIValImpl extends AnyAtomicTypeValImpl implements XsAnyURIVal {
        private String value = null;
        public AnyURIValImpl(String value) {
            super("anyURI");
            checkNull(value);
            this.value = value;
        }
        @Override
        public String getString() {
            return value;
        }
        @Override
        public XsAnyURIVal[] getAnyURIItems() {
            return new XsAnyURIVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printAnySimpleType(value);
        }
    }
    static class Base64BinarySeqValImpl extends AnyAtomicTypeSeqValImpl<Base64BinaryValImpl> implements XsBase64BinarySeqVal {
        Base64BinarySeqValImpl(byte[][] values) {
            this((XsBase64BinaryVal[]) Arrays.stream(values)
                                        .map(val -> new Base64BinaryValImpl(val))
                                        .toArray(size -> new Base64BinaryValImpl[size]));
        }
        Base64BinarySeqValImpl(XsBase64BinaryVal[] values) {
            this(Arrays.copyOf(values, values.length, Base64BinaryValImpl[].class));
        }
        Base64BinarySeqValImpl(Base64BinaryValImpl[] values) {
            super(values);
        }

        @Override
        public XsBase64BinaryVal[] getBase64BinaryItems() {
            return getItems();
        }
    }
    static class Base64BinaryValImpl extends AnyAtomicTypeValImpl implements XsBase64BinaryVal {
        private byte[] value = null;
        public Base64BinaryValImpl(byte[] value) {
            super("base64Binary");
            checkLength(value);
            this.value = value;
        }
        @Override
        public byte[] getBytes() {
            return value;
        }
        @Override
        public XsBase64BinaryVal[] getBase64BinaryItems() {
            return new XsBase64BinaryVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printBase64Binary(value);
        }
    }
    static class BooleanSeqValImpl extends AnyAtomicTypeSeqValImpl<BooleanValImpl> implements XsBooleanSeqVal {
        BooleanSeqValImpl(boolean[] values) {
            this(toArray(values));
        }
        BooleanSeqValImpl(XsBooleanVal[] values) {
            this(Arrays.copyOf(values, values.length, BooleanValImpl[].class));
        }
        BooleanSeqValImpl(BooleanValImpl[] values) {
            super(values);
        }
        private static BooleanValImpl[] toArray(boolean[] vals) {
            if (vals == null) {
                return null;
            }
            BooleanValImpl[] result = new BooleanValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new BooleanValImpl(vals[i]);
            }
            return result;
        }
        @Override
        public XsBooleanVal[] getBooleanItems() {
            return getItems();
        }
    }
    static class BooleanValImpl extends AnyAtomicTypeValImpl implements XsBooleanVal {
        private boolean value = false;
        public BooleanValImpl(String value) {
            this(DatatypeConverter.parseBoolean(value));
        }
        public BooleanValImpl(boolean value) {
            super("boolean");
            this.value = value;
        }
        @Override
        public boolean getBoolean() {
            return value;
        }
        @Override
        public XsBooleanVal[] getBooleanItems() {
            return new XsBooleanVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printBoolean(value);
        }
    }
    static class ByteSeqValImpl extends AnyAtomicTypeSeqValImpl<ByteValImpl> implements XsByteSeqVal {
        ByteSeqValImpl(byte[] values) {
            this(toArray(values));
        }
        ByteSeqValImpl(XsByteVal[] values) {
            this(Arrays.copyOf(values, values.length, ByteValImpl[].class));
        }
        ByteSeqValImpl(ByteValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsShortVal[] getShortItems() {
            return getItems();
        }
        @Override
        public XsIntVal[] getIntItems() {
            return getItems();
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsByteVal[] getByteItems() {
            return getItems();
        }
        private static ByteValImpl[] toArray(byte[] vals) {
            if (vals == null) {
                return null;
            }
            ByteValImpl[] result = new ByteValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new ByteValImpl(vals[i]);
            }
            return result;
        }
    }
    static class ByteValImpl extends AnyAtomicTypeValImpl implements XsByteVal {
        private byte value = 0;
        public ByteValImpl(String value) {
            this(DatatypeConverter.parseByte(value));
        }
        public ByteValImpl(byte value) {
            super("byte");
            this.value = value;
        }
        @Override
        public byte getByte() {
            return value;
        }
        @Override
        public XsByteVal[] getByteItems() {
            return new XsByteVal[]{this};
        }
        @Override
        public short getShort() {
            return value;
        }
        @Override
        public XsShortVal[] getShortItems() {
            return getByteItems();
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsIntVal[] getIntItems() {
            return getByteItems();
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getByteItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getByteItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getByteItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getByteItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printByte(value);
        }
    }
    static class DateSeqValImpl extends AnyAtomicTypeSeqValImpl<DateValImpl> implements XsDateSeqVal {
        DateSeqValImpl(String[] values) {
            this((DateValImpl[]) Arrays.stream(values)
                     .map(val -> new DateValImpl(val))
                     .toArray(size -> new DateValImpl[size]));
        }
        DateSeqValImpl(XMLGregorianCalendar[] values) {
            this((DateValImpl[]) Arrays.stream(values)
                       .map(val -> new DateValImpl(val))
                       .toArray(size -> new DateValImpl[size]));
        }
        DateSeqValImpl(Calendar[] values) {
            this((DateValImpl[]) Arrays.stream(values)
                       .map(val -> new DateValImpl(val))
                       .toArray(size -> new DateValImpl[size]));
        }
        DateSeqValImpl(XsDateVal[] values) {
            this(Arrays.copyOf(values, values.length, DateValImpl[].class));
        }
        DateSeqValImpl(DateValImpl[] values) {
            super(values);
        }
        @Override
        public XsDateVal[] getDateItems() {
            return getItems();
        }
    }
    static class DateValImpl extends AnyAtomicTypeValImpl implements XsDateVal {
        private Calendar value = null;
        public DateValImpl(String value) {
            this(DatatypeConverter.parseDate(value));
        }
        DateValImpl(XMLGregorianCalendar value) {
            this((value == null) ? (Calendar) null : value.toGregorianCalendar());
        }
        DateValImpl(Calendar value) {
            super("date");
            checkNull(value);
/* TODO: validation
            if (value.isSet(Calendar.HOUR_OF_DAY)) {
                throw new IllegalArgumentException(
                        "date value has hour: "+value.toString()
                        );
            }
 */
            this.value = value;
        }
        @Override
        public Calendar getCalendar() {
            return value;
        }
        @Override
        public XsDateVal[] getDateItems() {
            return new XsDateVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printDate(value);
        }
    }
    static class DateTimeSeqValImpl extends AnyAtomicTypeSeqValImpl<DateTimeValImpl> implements XsDateTimeSeqVal {
        DateTimeSeqValImpl(String[] values) {
            this((DateTimeValImpl[]) Arrays.stream(values)
                       .map(val -> new DateTimeValImpl(val))
                       .toArray(size -> new DateTimeValImpl[size]));
        }
        DateTimeSeqValImpl(Date[] values) {
            this((DateTimeValImpl[]) Arrays.stream(values)
                       .map(val -> new DateTimeValImpl(val))
                       .toArray(size -> new DateTimeValImpl[size]));
        }
        DateTimeSeqValImpl(XMLGregorianCalendar[] values) {
            this((DateTimeValImpl[]) Arrays.stream(values)
                       .map(val -> new DateTimeValImpl(val))
                       .toArray(size -> new DateTimeValImpl[size]));
        }
        DateTimeSeqValImpl(Calendar[] values) {
            this((DateTimeValImpl[]) Arrays.stream(values)
                       .map(val -> new DateTimeValImpl(val))
                       .toArray(size -> new DateTimeValImpl[size]));
        }
        DateTimeSeqValImpl(XsDateTimeVal[] values) {
            this(Arrays.copyOf(values, values.length, DateTimeValImpl[].class));
        }
        DateTimeSeqValImpl(DateTimeValImpl[] values) {
            super(values);
        }
        @Override
        public XsDateTimeVal[] getDateTimeItems() {
            return getItems();
        }
    }
    static class DateTimeValImpl extends AnyAtomicTypeValImpl implements XsDateTimeVal {
// TODO: SHOULD Xs.DateTime BE REPRESENTED AS XMLGregorianCalendar?
        private Calendar value = null;
        public DateTimeValImpl(String value) {
            this(DatatypeConverter.parseDateTime(value));
        }
        DateTimeValImpl(Date value) {
            this(from(value));
        }
        DateTimeValImpl(XMLGregorianCalendar value) {
            this((value == null) ? (Calendar) null : value.toGregorianCalendar());
        }
        DateTimeValImpl(Calendar value) {
            super("dateTime");
            checkNull(value);
            this.value = value;
        }
        @Override
        public Calendar getCalendar() {
            return value;
        }
        @Override
        public XsDateTimeVal[] getDateTimeItems() {
            return new XsDateTimeVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printDateTime(value);
        }
    }
    static class DayTimeDurationSeqValImpl extends AnyAtomicTypeSeqValImpl<DayTimeDurationValImpl> implements XsDayTimeDurationSeqVal {
        DayTimeDurationSeqValImpl(String[] values) {
            this((DayTimeDurationValImpl[]) Arrays.stream(values)
                       .map(val -> new DayTimeDurationValImpl(val))
                       .toArray(size -> new DayTimeDurationValImpl[size]));
        }
        DayTimeDurationSeqValImpl(Duration[] values) {
            this((DayTimeDurationValImpl[]) Arrays.stream(values)
                       .map(val -> new DayTimeDurationValImpl(val))
                       .toArray(size -> new DayTimeDurationValImpl[size]));
        }
        DayTimeDurationSeqValImpl(XsDayTimeDurationVal[] values) {
            this(Arrays.copyOf(values, values.length, DayTimeDurationValImpl[].class));
        }
        DayTimeDurationSeqValImpl(DayTimeDurationValImpl[] values) {
            super(values);
        }
        @Override
        public XsDurationVal[] getDurationItems() {
            return getItems();
        }
        @Override
        public XsDayTimeDurationVal[] getDayTimeDurationItems() {
            return getItems();
        }
    }
    static class DayTimeDurationValImpl extends AnyAtomicTypeValImpl implements XsDayTimeDurationVal {
        private Duration value = null;
        public DayTimeDurationValImpl(String value) {
            this(Utilities.getDatatypeFactory().newDuration(value));
        }
        DayTimeDurationValImpl(Duration value) {
            super("dayTimeDuration");
            checkNull(value);
            checkType("dayTimeDuration", getDurationType(value));
            this.value = value;
        }
        @Override
        public Duration getDuration() {
            return value;
        }
        @Override
        public XsDayTimeDurationVal[] getDayTimeDurationItems() {
            return new XsDayTimeDurationVal[]{this};
        }
        @Override
        public XsDurationVal[] getDurationItems() {
            return getDayTimeDurationItems();
        }
        @Override
        public String toString() {
            return value.toString();
        }
    }
    static class DecimalSeqValImpl extends AnyAtomicTypeSeqValImpl<DecimalValImpl> implements XsDecimalSeqVal {
        DecimalSeqValImpl(String[] values) {
            this((DecimalValImpl[]) Arrays.stream(values)
                       .map(val -> new DecimalValImpl(val))
                       .toArray(size -> new DecimalValImpl[size]));
        }
        DecimalSeqValImpl(double[] values) {
            this((DecimalValImpl[]) Arrays.stream(values)
                       .mapToObj(val -> new DecimalValImpl(val))
                       .toArray(size -> new DecimalValImpl[size]));
        }
        DecimalSeqValImpl(long[] values) {
            this((DecimalValImpl[]) Arrays.stream(values)
                       .mapToObj(val -> new DecimalValImpl(val))
                       .toArray(size -> new DecimalValImpl[size]));
        }
        DecimalSeqValImpl(BigDecimal[] values) {
            this((DecimalValImpl[]) Arrays.stream(values)
                       .map(val -> new DecimalValImpl(val))
                       .toArray(size -> new DecimalValImpl[size]));
        }
        DecimalSeqValImpl(XsDecimalVal[] values) {
            this(Arrays.copyOf(values, values.length, DecimalValImpl[].class));
        }
        DecimalSeqValImpl(DecimalValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
    }
    static class DecimalValImpl extends AnyAtomicTypeValImpl implements XsDecimalVal {
        private BigDecimal value = null;
        public DecimalValImpl(String value) {
            this(new BigDecimal(value));
        }
        DecimalValImpl(long value) {
            this(BigDecimal.valueOf(value));
        }
        DecimalValImpl(double value) {
            this(BigDecimal.valueOf(value));
        }
        DecimalValImpl(BigDecimal value) {
            super("decimal");
            checkNull(value);
            this.value = value;
        }
        @Override
        public BigDecimal getBigDecimal() {
            return value;
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return new XsDecimalVal[]{this};
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getDecimalItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printDecimal(value);
        }
    }
    static class DoubleSeqValImpl extends AnyAtomicTypeSeqValImpl<DoubleValImpl> implements XsDoubleSeqVal {
        DoubleSeqValImpl(double[] values) {
            this(toArray(values));
        }
        DoubleSeqValImpl(XsDoubleVal[] values) {
            this(Arrays.copyOf(values, values.length, DoubleValImpl[].class));
        }
        DoubleSeqValImpl(DoubleValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsDoubleVal[] getDoubleItems() {
            return getItems();
        }
        private static DoubleValImpl[] toArray(double[] vals) {
            if (vals == null) {
                return null;
            }
            DoubleValImpl[] result = new DoubleValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new DoubleValImpl(vals[i]);
            }
            return result;
        }
    }
    static class DoubleValImpl extends AnyAtomicTypeValImpl implements XsDoubleVal {
        private double value = 0;
        public DoubleValImpl(String value) {
            this(DatatypeConverter.parseDouble(value));
        }
        public DoubleValImpl(double value) {
            super("double");
            this.value = value;
        }
        @Override
        public double getDouble() {
            return value;
        }
        @Override
        public XsDoubleVal[] getDoubleItems() {
            return new XsDoubleVal[]{this};
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getDoubleItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printDouble(value);
        }
    }
    static class FloatSeqValImpl extends AnyAtomicTypeSeqValImpl<FloatValImpl> implements XsFloatSeqVal {
        FloatSeqValImpl(float[] values) {
            this(toArray(values));
        }
        FloatSeqValImpl(XsFloatVal[] values) {
            this(Arrays.copyOf(values, values.length, FloatValImpl[].class));
        }
        FloatSeqValImpl(FloatValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsFloatVal[] getFloatItems() {
            return getItems();
        }
        private static FloatValImpl[] toArray(float[] vals) {
            if (vals == null) {
                return null;
            }
            FloatValImpl[] result = new FloatValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new FloatValImpl(vals[i]);
            }
            return result;
        }
    }
    static class FloatValImpl extends AnyAtomicTypeValImpl implements XsFloatVal {
        private float value = 0;
        public FloatValImpl(String value) {
            this(DatatypeConverter.parseFloat(value));
        }
        public FloatValImpl(float value) {
            super("float");
            this.value = value;
        }
        @Override
        public float getFloat() {
            return value;
        }
        @Override
        public XsFloatVal[] getFloatItems() {
            return new XsFloatVal[]{this};
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getFloatItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printFloat(value);
        }
    }
    static class GDaySeqValImpl extends AnyAtomicTypeSeqValImpl<GDayValImpl> implements XsGDaySeqVal {
        GDaySeqValImpl(String[] values) {
            this((GDayValImpl[]) Arrays.stream(values)
                        .map(val -> new GDayValImpl(val))
                        .toArray(size -> new GDayValImpl[size]));
        }
        GDaySeqValImpl(XMLGregorianCalendar[] values) {
            this((GDayValImpl[]) Arrays.stream(values)
                        .map(val -> new GDayValImpl(val))
                        .toArray(size -> new GDayValImpl[size]));
        }
        GDaySeqValImpl(XsGDayVal[] values) {
            this(Arrays.copyOf(values, values.length, GDayValImpl[].class));
        }
        GDaySeqValImpl(GDayValImpl[] values) {
            super(values);
        }
        @Override
        public XsGDayVal[] getGDayItems() {
            return getItems();
        }
    }
    static class GDayValImpl extends AnyAtomicTypeValImpl implements XsGDayVal {
        private XMLGregorianCalendar value = null;
        public GDayValImpl(String value) {
            this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
        }
        GDayValImpl(XMLGregorianCalendar value) {
            super("gDay");
            checkNull(value);
            checkType("gDay", value.getXMLSchemaType());
            this.value = value;
        }
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return value;
        }
        @Override
        public XsGDayVal[] getGDayItems() {
            return new XsGDayVal[]{this};
        }
        @Override
        public String toString() {
            return value.toXMLFormat();
        }
    }
    static class GMonthSeqValImpl extends AnyAtomicTypeSeqValImpl<GMonthValImpl> implements XsGMonthSeqVal {
        GMonthSeqValImpl(String[] values) {
            this((GMonthValImpl[]) Arrays.stream(values)
                        .map(val -> new GMonthValImpl(val))
                        .toArray(size -> new GMonthValImpl[size]));
        }
        GMonthSeqValImpl(XMLGregorianCalendar[] values) {
            this((GMonthValImpl[]) Arrays.stream(values)
                        .map(val -> new GMonthValImpl(val))
                        .toArray(size -> new GMonthValImpl[size]));
        }
        GMonthSeqValImpl(XsGMonthVal[] values) {
            this(Arrays.copyOf(values, values.length, GMonthValImpl[].class));
        }
        GMonthSeqValImpl(GMonthValImpl[] values) {
            super(values);
        }
        @Override
        public XsGMonthVal[] getGMonthItems() {
            return getItems();
        }
    }
    static class GMonthValImpl extends AnyAtomicTypeValImpl implements XsGMonthVal {
        private XMLGregorianCalendar value = null;
        public GMonthValImpl(String value) {
            this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
        }
        GMonthValImpl(XMLGregorianCalendar value) {
            super("gMonth");
            checkNull(value);
            checkType("gMonth", value.getXMLSchemaType());
            this.value = value;
        }
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return value;
        }
        @Override
        public XsGMonthVal[] getGMonthItems() {
            return new XsGMonthVal[]{this};
        }
        @Override
        public String toString() {
            String val = value.toXMLFormat();
            // the server rejects values with the trailing hyphens
            if (val.endsWith("--")) {
                val = val.substring(0, val.length() - 2);
            }
            return val;
        }
    }
    static class GMonthDaySeqValImpl extends AnyAtomicTypeSeqValImpl<GMonthDayValImpl> implements XsGMonthDaySeqVal {
        GMonthDaySeqValImpl(String[] values) {
            this((GMonthDayValImpl[]) Arrays.stream(values)
                        .map(val -> new GMonthDayValImpl(val))
                        .toArray(size -> new GMonthDayValImpl[size]));
        }
        GMonthDaySeqValImpl(XMLGregorianCalendar[] values) {
            this((GMonthDayValImpl[]) Arrays.stream(values)
                        .map(val -> new GMonthDayValImpl(val))
                        .toArray(size -> new GMonthDayValImpl[size]));
        }
        GMonthDaySeqValImpl(XsGMonthDayVal[] values) {
            this(Arrays.copyOf(values, values.length, GMonthDayValImpl[].class));
        }
        GMonthDaySeqValImpl(GMonthDayValImpl[] values) {
            super(values);
        }
        @Override
        public XsGMonthDayVal[] getGMonthDayItems() {
            return getItems();
        }
    }
    static class GMonthDayValImpl extends AnyAtomicTypeValImpl implements XsGMonthDayVal {
        private XMLGregorianCalendar value = null;
        public GMonthDayValImpl(String value) {
            this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
        }
        GMonthDayValImpl(XMLGregorianCalendar value) {
            super("gMonthDay");
            checkNull(value);
            checkType("gMonthDay", value.getXMLSchemaType());
            this.value = value;
        }
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return value;
        }
        @Override
        public XsGMonthDayVal[] getGMonthDayItems() {
            return new XsGMonthDayVal[]{this};
        }
        @Override
        public String toString() {
            return value.toXMLFormat();
        }
    }
    static class GYearSeqValImpl extends AnyAtomicTypeSeqValImpl<GYearValImpl> implements XsGYearSeqVal {
        GYearSeqValImpl(String[] values) {
            this((GYearValImpl[]) Arrays.stream(values)
                        .map(val -> new GYearValImpl(val))
                        .toArray(size -> new GYearValImpl[size]));
        }
        GYearSeqValImpl(XMLGregorianCalendar[] values) {
            this((GYearValImpl[]) Arrays.stream(values)
                        .map(val -> new GYearValImpl(val))
                        .toArray(size -> new GYearValImpl[size]));
        }
        GYearSeqValImpl(XsGYearVal[] values) {
            this(Arrays.copyOf(values, values.length, GYearValImpl[].class));
        }
        GYearSeqValImpl(GYearValImpl[] values) {
            super(values);
        }
        @Override
        public XsGYearVal[] getGYearItems() {
            return getItems();
        }
    }
    static class GYearValImpl extends AnyAtomicTypeValImpl implements XsGYearVal {
        private XMLGregorianCalendar value = null;
        public GYearValImpl(String value) {
            this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
        }
        GYearValImpl(XMLGregorianCalendar value) {
            super("gYear");
            checkNull(value);
            checkType("gYear", value.getXMLSchemaType());
            this.value = value;
        }
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return value;
        }
        @Override
        public XsGYearVal[] getGYearItems() {
            return new XsGYearVal[]{this};
        }
        @Override
        public String toString() {
            return value.toXMLFormat();
        }
    }
    static class GYearMonthSeqValImpl extends AnyAtomicTypeSeqValImpl<GYearMonthValImpl> implements XsGYearMonthSeqVal {
        GYearMonthSeqValImpl(String[] values) {
            this((GYearMonthValImpl[]) Arrays.stream(values)
                        .map(val -> new GYearMonthValImpl(val))
                        .toArray(size -> new GYearMonthValImpl[size]));
        }
        GYearMonthSeqValImpl(XMLGregorianCalendar[] values) {
            this((GYearMonthValImpl[]) Arrays.stream(values)
                        .map(val -> new GYearMonthValImpl(val))
                        .toArray(size -> new GYearMonthValImpl[size]));
        }
        GYearMonthSeqValImpl(XsGYearMonthVal[] values) {
            this(Arrays.copyOf(values, values.length, GYearMonthValImpl[].class));
        }
        GYearMonthSeqValImpl(GYearMonthValImpl[] values) {
            super(values);
        }
        @Override
        public XsGYearMonthVal[] getGYearMonthItems() {
            return getItems();
        }
    }
    static class GYearMonthValImpl extends AnyAtomicTypeValImpl implements XsGYearMonthVal {
        private XMLGregorianCalendar value = null;
        public GYearMonthValImpl(String value) {
            this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
        }
        GYearMonthValImpl(XMLGregorianCalendar value) {
            super("gYearMonth");
            checkNull(value);
            checkType("gYearMonth", value.getXMLSchemaType());
            this.value = value;
        }
        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return value;
        }
        @Override
        public XsGYearMonthVal[] getGYearMonthItems() {
            return new XsGYearMonthVal[]{this};
        }
        @Override
        public String toString() {
            return value.toXMLFormat();
        }
    }
    static class HexBinarySeqValImpl extends AnyAtomicTypeSeqValImpl<HexBinaryValImpl> implements XsHexBinarySeqVal {
        HexBinarySeqValImpl(byte[][] values) {
            this((XsHexBinaryVal[]) Arrays.stream(values)
                        .map(val -> new HexBinaryValImpl(val))
                        .toArray(size -> new HexBinaryValImpl[size]));
        }
        HexBinarySeqValImpl(XsHexBinaryVal[] values) {
            this(Arrays.copyOf(values, values.length, HexBinaryValImpl[].class));
        }
        HexBinarySeqValImpl(HexBinaryValImpl[] values) {
            super(values);
        }
        @Override
        public XsHexBinaryVal[] getHexBinaryItems() {
            return getItems();
        }
    }
    static class HexBinaryValImpl extends AnyAtomicTypeValImpl implements XsHexBinaryVal {
        private byte[] value = null;
        public HexBinaryValImpl(byte[] value) {
            super("hexBinary");
            checkLength(value);
            this.value = value;
        }
        @Override
        public byte[] getBytes() {
            return value;
        }
        @Override
        public XsHexBinaryVal[] getHexBinaryItems() {
            return new XsHexBinaryVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printHexBinary(value);
        }
    }
    static class IntSeqValImpl extends AnyAtomicTypeSeqValImpl<IntValImpl> implements XsIntSeqVal {
        IntSeqValImpl(int[] values) {
            this((XsIntVal[]) Arrays.stream(values)
                           .mapToObj(val -> new IntValImpl(val))
                           .toArray(size -> new IntValImpl[size]));
        }
        IntSeqValImpl(XsIntVal[] values) {
            this(Arrays.copyOf(values, values.length, IntValImpl[].class));
        }
        IntSeqValImpl(IntValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsIntVal[] getIntItems() {
            return getItems();
        }
    }
    static class IntValImpl extends AnyAtomicTypeValImpl implements XsIntVal {
        private int value = 0;
        public IntValImpl(String value) {
            this(DatatypeConverter.parseInt(value));
        }
        public IntValImpl(int value) {
            super("int");
            this.value = value;
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsIntVal[] getIntItems() {
            return new XsIntVal[]{this};
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getIntItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getIntItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getIntItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getIntItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printInt(value);
        }
    }
    static class IntegerSeqValImpl extends AnyAtomicTypeSeqValImpl<IntegerValImpl> implements XsIntegerSeqVal {
        IntegerSeqValImpl(String[] values) {
            this((IntegerValImpl[]) Arrays.stream(values)
                       .map(val -> new IntegerValImpl(val))
                       .toArray(size -> new IntegerValImpl[size]));
        }
        IntegerSeqValImpl(long[] values) {
            this((IntegerValImpl[]) Arrays.stream(values)
                       .mapToObj(val -> new IntegerValImpl(val))
                       .toArray(size -> new IntegerValImpl[size]));
        }
        IntegerSeqValImpl(BigInteger[] values) {
            this((IntegerValImpl[]) Arrays.stream(values)
                       .map(val -> new IntegerValImpl(val))
                       .toArray(size -> new IntegerValImpl[size]));
        }
        IntegerSeqValImpl(XsIntegerVal[] values) {
            this(Arrays.copyOf(values, values.length, IntegerValImpl[].class));
        }
        IntegerSeqValImpl(IntegerValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
    }
    static class IntegerValImpl extends AnyAtomicTypeValImpl implements XsIntegerVal {
        private BigInteger value = null;
        public IntegerValImpl(String value) {
            this(new BigInteger(value));
        }
        IntegerValImpl(long value) {
            this(BigInteger.valueOf(value));
        }
        IntegerValImpl(BigInteger value) {
            super("integer");
            checkNull(value);
            this.value = value;
        }
        @Override
        public BigInteger getBigInteger() {
            return value;
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return new XsIntegerVal[]{this};
        }
        @Override
        public BigDecimal getBigDecimal() {
            return new BigDecimal(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getIntegerItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getIntegerItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printInteger(value);
        }
    }
    static class LongSeqValImpl extends AnyAtomicTypeSeqValImpl<LongValImpl> implements XsLongSeqVal {
        LongSeqValImpl(long[] values) {
            this((XsLongVal[]) Arrays.stream(values)
                           .mapToObj(val -> new LongValImpl(val))
                           .toArray(size -> new LongValImpl[size]));
        }
        LongSeqValImpl(XsLongVal[] values) {
            this(Arrays.copyOf(values, values.length, LongValImpl[].class));
        }
        LongSeqValImpl(LongValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getItems();
        }
    }
    static class LongValImpl extends AnyAtomicTypeValImpl implements XsLongVal {
        private long value = 0;
        public LongValImpl(String value) {
            this(DatatypeConverter.parseLong(value));
        }
        public LongValImpl(long value) {
            super("long");
            this.value = value;
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsLongVal[] getLongItems() {
            return new XsLongVal[]{this};
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getLongItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getLongItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getLongItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printLong(value);
        }
    }
    static class ShortSeqValImpl extends AnyAtomicTypeSeqValImpl<ShortValImpl> implements XsShortSeqVal {
        ShortSeqValImpl(short[] values) {
            this(toArray(values));
        }
        ShortSeqValImpl(XsShortVal[] values) {
            this(Arrays.copyOf(values, values.length, ShortValImpl[].class));
        }
        ShortSeqValImpl(ShortValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        private static ShortValImpl[] toArray(short[] vals) {
            if (vals == null) {
                return null;
            }
            ShortValImpl[] result = new ShortValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new ShortValImpl(vals[i]);
            }
            return result;
        }
        @Override
        public XsIntVal[] getIntItems() {
            return getItems();
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsShortVal[] getShortItems() {
            return getItems();
        }
    }
    static class ShortValImpl extends AnyAtomicTypeValImpl implements XsShortVal {
        private short value = 0;
        public ShortValImpl(String value) {
            this(DatatypeConverter.parseShort(value));
        }
        public ShortValImpl(short value) {
            super("short");
            this.value = value;
        }
        @Override
        public short getShort() {
            return value;
        }
        @Override
        public XsShortVal[] getShortItems() {
            return new XsShortVal[]{this};
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsIntVal[] getIntItems() {
            return getShortItems();
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsLongVal[] getLongItems() {
            return getShortItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getShortItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getShortItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getShortItems();
        }
        @Override
        public String toString() {
            return DatatypeConverter.printShort(value);
        }
    }
    static class StringSeqValImpl extends AnyAtomicTypeSeqValImpl<StringValImpl> implements XsStringSeqVal {
        StringSeqValImpl(String[] values) {
            this((StringValImpl[]) Arrays.stream(values)
                        .map(val -> new StringValImpl(val))
                        .toArray(size -> new StringValImpl[size]));
        }
        StringSeqValImpl(XsStringVal[] values) {
            this(Arrays.copyOf(values, values.length, StringValImpl[].class));
        }
        StringSeqValImpl(StringValImpl[] values) {
            super(values);
        }
        @Override
        public XsStringVal[] getStringItems() {
            return getItems();
        }
    }
    static class StringValImpl extends AnyAtomicTypeValImpl implements XsStringVal {
        private String value = null;
        public StringValImpl(String value) {
            super("string");
            checkNull(value);
            this.value = value;
        }
        @Override
        public String getString() {
            return value;
        }
        @Override
        public XsStringVal[] getStringItems() {
            return new XsStringVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printString(value);
        }
    }
    static class TimeSeqValImpl extends AnyAtomicTypeSeqValImpl<TimeValImpl> implements XsTimeSeqVal {
        TimeSeqValImpl(String[] values) {
            this((TimeValImpl[]) Arrays.stream(values)
                       .map(val -> new TimeValImpl(val))
                       .toArray(size -> new TimeValImpl[size]));
        }
        TimeSeqValImpl(Date[] values) {
            this((TimeValImpl[]) Arrays.stream(values)
                       .map(val -> new TimeValImpl(val))
                       .toArray(size -> new TimeValImpl[size]));
        }
        TimeSeqValImpl(XMLGregorianCalendar[] values) {
            this((TimeValImpl[]) Arrays.stream(values)
                       .map(val -> new TimeValImpl(val))
                       .toArray(size -> new TimeValImpl[size]));
        }
        TimeSeqValImpl(Calendar[] values) {
            this((TimeValImpl[]) Arrays.stream(values)
                       .map(val -> new TimeValImpl(val))
                       .toArray(size -> new TimeValImpl[size]));
        }
        TimeSeqValImpl(XsTimeVal[] values) {
            this(Arrays.copyOf(values, values.length, TimeValImpl[].class));
        }
        TimeSeqValImpl(TimeValImpl[] values) {
            super(values);
        }
        @Override
        public XsTimeVal[] getTimeItems() {
            return getItems();
        }
    }
    static class TimeValImpl extends AnyAtomicTypeValImpl implements XsTimeVal {
        private Calendar value = null;
        public TimeValImpl(String value) {
            this(DatatypeConverter.parseDateTime(value));
        }
        TimeValImpl(Date value) {
            this(from(value));
        }
        TimeValImpl(XMLGregorianCalendar value) {
            this((value == null) ? (Calendar) null : value.toGregorianCalendar());
        }
        TimeValImpl(Calendar value) {
            super("time");
            checkNull(value);
/* TODO: validation
            if (value.isSet(Calendar.DAY_OF_MONTH)) {
                throw new IllegalArgumentException(
                        "time value has day: "+value.toString()
                        );
            }
 */
            this.value = value;
        }
        @Override
        public Calendar getCalendar() {
            return value;
        }
        @Override
        public XsTimeVal[] getTimeItems() {
            return new XsTimeVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printTime(value);
        }
    }
    static class UnsignedByteSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedByteValImpl> implements XsUnsignedByteSeqVal {
        UnsignedByteSeqValImpl(byte[] values) {
            this(toArray(values));
        }
        UnsignedByteSeqValImpl(XsUnsignedByteVal[] values) {
            this(Arrays.copyOf(values, values.length, UnsignedByteValImpl[].class));
        }
        UnsignedByteSeqValImpl(UnsignedByteValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        private static UnsignedByteValImpl[] toArray(byte[] vals) {
            if (vals == null) {
                return null;
            }
            UnsignedByteValImpl[] result = new UnsignedByteValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new UnsignedByteValImpl(vals[i]);
            }
            return result;
        }
        @Override
        public XsUnsignedShortVal[] getUnsignedShortItems() {
            return getItems();
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return getItems();
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsUnsignedByteVal[] getUnsignedByteItems() {
            return getItems();
        }
    }
    static class UnsignedByteValImpl extends AnyAtomicTypeValImpl implements XsUnsignedByteVal {
        private byte value = 0;
        public UnsignedByteValImpl(String value) {
            this((byte) Integer.parseUnsignedInt(value));
        }
        public UnsignedByteValImpl(byte value) {
            super("unsignedByte");
            this.value = value;
        }
        @Override
        public byte getByte() {
            return value;
        }
        @Override
        public XsUnsignedByteVal[] getUnsignedByteItems() {
            return new XsUnsignedByteVal[]{this};
        }
        @Override
        public short getShort() {
            return value;
        }
        @Override
        public XsUnsignedShortVal[] getUnsignedShortItems() {
            return getUnsignedByteItems();
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return getUnsignedByteItems();
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getUnsignedByteItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getUnsignedByteItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(Byte.toUnsignedInt(value));
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getUnsignedByteItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(Byte.toUnsignedInt(value));
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getUnsignedByteItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getUnsignedByteItems();
        }
        @Override
        public String toString() {
            return Integer.toUnsignedString(Byte.toUnsignedInt(value));
        }
    }
    static class UnsignedIntSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedIntValImpl> implements XsUnsignedIntSeqVal {
        UnsignedIntSeqValImpl(int[] values) {
            this((XsUnsignedIntVal[]) Arrays.stream(values)
                           .mapToObj(val -> new UnsignedIntValImpl(val))
                           .toArray(size -> new UnsignedIntValImpl[size]));
        }
        UnsignedIntSeqValImpl(XsUnsignedIntVal[] values) {
            this(Arrays.copyOf(values, values.length, UnsignedIntValImpl[].class));
        }
        UnsignedIntSeqValImpl(UnsignedIntValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return getItems();
        }
    }
    static class UnsignedIntValImpl extends AnyAtomicTypeValImpl implements XsUnsignedIntVal {
        private int value = 0;
        public UnsignedIntValImpl(String value) {
            this(Integer.parseUnsignedInt(value));
        }
        public UnsignedIntValImpl(int value) {
            super("unsignedInt");
            this.value = value;
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return new XsUnsignedIntVal[]{this};
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getUnsignedIntItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getUnsignedIntItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getUnsignedIntItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getUnsignedIntItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getUnsignedIntItems();
        }
        @Override
        public String toString() {
            return Integer.toUnsignedString(value);
        }
    }
    static class UnsignedLongSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedLongValImpl> implements XsUnsignedLongSeqVal {
        UnsignedLongSeqValImpl(long[] values) {
            this((XsUnsignedLongVal[]) Arrays.stream(values)
                           .mapToObj(val -> new UnsignedLongValImpl(val))
                           .toArray(size -> new UnsignedLongValImpl[size]));
        }
        UnsignedLongSeqValImpl(XsUnsignedLongVal[] values) {
            this(Arrays.copyOf(values, values.length, UnsignedLongValImpl[].class));
        }
        UnsignedLongSeqValImpl(UnsignedLongValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getItems();
        }
    }
    static class UnsignedLongValImpl extends AnyAtomicTypeValImpl implements XsUnsignedLongVal {
        private long value = 0;
        public UnsignedLongValImpl(String value) {
            this(Long.parseUnsignedLong(value));
        }
        public UnsignedLongValImpl(long value) {
            super("unsignedLong");
            this.value = value;
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return new XsUnsignedLongVal[]{this};
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getUnsignedLongItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(value);
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getUnsignedLongItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(value);
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getUnsignedLongItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getUnsignedLongItems();
        }
        @Override
        public String toString() {
            return Long.toUnsignedString(value);
        }
    }
    static class UnsignedShortSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedShortValImpl> implements XsUnsignedShortSeqVal {
        UnsignedShortSeqValImpl(short[] values) {
            this(toArray(values));
        }
        UnsignedShortSeqValImpl(XsUnsignedShortVal[] values) {
            this(Arrays.copyOf(values, values.length, UnsignedShortValImpl[].class));
        }
        UnsignedShortSeqValImpl(UnsignedShortValImpl[] values) {
            super(values);
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getItems();
        }
        private static UnsignedShortValImpl[] toArray(short[] vals) {
            if (vals == null) {
                return null;
            }
            UnsignedShortValImpl[] result = new UnsignedShortValImpl[vals.length];
            for (int i=0; i < vals.length; i++) {
                result[i] = new UnsignedShortValImpl(vals[i]);
            }
            return result;
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return getItems();
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getItems();
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getItems();
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getItems();
        }
        @Override
        public XsUnsignedShortVal[] getUnsignedShortItems() {
            return getItems();
        }
    }
    static class UnsignedShortValImpl extends AnyAtomicTypeValImpl implements XsUnsignedShortVal {
        private short value = 0;
        public UnsignedShortValImpl(String value) {
            this((short) Integer.parseUnsignedInt(value));
        }
        public UnsignedShortValImpl(short value) {
            super("unsignedShort");
            this.value = value;
        }
        @Override
        public short getShort() {
            return value;
        }
        @Override
        public XsUnsignedShortVal[] getUnsignedShortItems() {
            return new XsUnsignedShortVal[]{this};
        }
        @Override
        public int getInt() {
            return value;
        }
        @Override
        public XsUnsignedIntVal[] getUnsignedIntItems() {
            return getUnsignedShortItems();
        }
        @Override
        public long getLong() {
            return value;
        }
        @Override
        public XsUnsignedLongVal[] getUnsignedLongItems() {
            return getUnsignedShortItems();
        }
        @Override
        public XsNonNegativeIntegerVal[] getNonNegativeIntegerItems() {
            return getUnsignedShortItems();
        }
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(Short.toUnsignedInt(value));
        }
        @Override
        public XsIntegerVal[] getIntegerItems() {
            return getUnsignedShortItems();
        }
        @Override
        public BigDecimal getBigDecimal() {
            return BigDecimal.valueOf(Short.toUnsignedInt(value));
        }
        @Override
        public XsDecimalVal[] getDecimalItems() {
            return getUnsignedShortItems();
        }
        @Override
        public XsNumericVal[] getNumericItems() {
            return getUnsignedShortItems();
        }
        @Override
        public String toString() {
            return Integer.toUnsignedString(Short.toUnsignedInt(value));
        }
    }
    static class UntypedAtomicSeqValImpl extends AnyAtomicTypeSeqValImpl<UntypedAtomicValImpl> implements XsUntypedAtomicSeqVal {
        UntypedAtomicSeqValImpl(String[] values) {
            this((XsUntypedAtomicVal[]) Arrays.stream(values)
                        .map(val -> new UntypedAtomicValImpl(val))
                        .toArray(size -> new UntypedAtomicValImpl[size]));
        }
        UntypedAtomicSeqValImpl(XsUntypedAtomicVal[] values) {
            this(Arrays.copyOf(values, values.length, UntypedAtomicValImpl[].class));
        }
        UntypedAtomicSeqValImpl(UntypedAtomicValImpl[] values) {
            super(values);
        }
        @Override
        public XsUntypedAtomicVal[] getUntypedAtomicItems() {
            return getItems();
        }
    }
    static class UntypedAtomicValImpl extends AnyAtomicTypeValImpl implements XsUntypedAtomicVal {
        private String value = null;
        public UntypedAtomicValImpl(String value) {
            super("untypedAtomic");
            this.value = value;
        }
        @Override
        public String getString() {
            return value;
        }
        @Override
        public XsUntypedAtomicVal[] getUntypedAtomicItems() {
            return new XsUntypedAtomicVal[]{this};
        }
        @Override
        public String toString() {
            return DatatypeConverter.printAnySimpleType(value);
        }
    }
    static class YearMonthDurationSeqValImpl extends AnyAtomicTypeSeqValImpl<YearMonthDurationValImpl> implements XsYearMonthDurationSeqVal {
        YearMonthDurationSeqValImpl(String[] values) {
            this((YearMonthDurationValImpl[]) Arrays.stream(values)
                       .map(val -> new YearMonthDurationValImpl(val))
                       .toArray(size -> new YearMonthDurationValImpl[size]));
        }
        YearMonthDurationSeqValImpl(Duration[] values) {
            this((YearMonthDurationValImpl[]) Arrays.stream(values)
                       .map(val -> new YearMonthDurationValImpl(val))
                       .toArray(size -> new YearMonthDurationValImpl[size]));
        }
        YearMonthDurationSeqValImpl(XsYearMonthDurationVal[] values) {
            this(Arrays.copyOf(values, values.length, YearMonthDurationValImpl[].class));
        }
        YearMonthDurationSeqValImpl(YearMonthDurationValImpl[] values) {
            super(values);
        }
        @Override
        public XsDurationVal[] getDurationItems() {
            return getItems();
        }
        @Override
        public XsYearMonthDurationVal[] getYearMonthDurationItems() {
            return getItems();
        }
    }
    static class YearMonthDurationValImpl extends AnyAtomicTypeValImpl implements XsYearMonthDurationVal {
        private Duration value = null;
        public YearMonthDurationValImpl(String value) {
            this(Utilities.getDatatypeFactory().newDuration(value));
        }
        YearMonthDurationValImpl(Duration value) {
            super("yearMonthDuration");
            checkNull(value);
            checkType("yearMonthDuration", getDurationType(value));
            this.value = value;
        }
        @Override
        public Duration getDuration() {
            return value;
        }
        @Override
        public XsYearMonthDurationVal[] getYearMonthDurationItems() {
            return new XsYearMonthDurationVal[]{this};
        }
        @Override
        public XsDurationVal[] getDurationItems() {
            return getYearMonthDurationItems();
        }
        @Override
        public String toString() {
            return value.toString();
        }
    }

    // XML types
    static class QNameSeqValImpl extends AnyAtomicTypeSeqValImpl<QNameValImpl> implements XsQNameSeqVal {
        QNameSeqValImpl(String[] values) {
            this((QNameValImpl[]) Arrays.stream(values)
                       .map(val -> new QNameValImpl(val))
                       .toArray(size -> new QNameValImpl[size]));
        }
        QNameSeqValImpl(String namespace, String[] localNames) {
            this((QNameValImpl[]) Arrays.stream(localNames)
                       .map(val -> new QNameValImpl(namespace, val))
                       .toArray(size -> new QNameValImpl[size]));
        }
        QNameSeqValImpl(QName[] values) {
            this((QNameValImpl[]) Arrays.stream(values)
                       .map(val -> new QNameValImpl(val))
                       .toArray(size -> new QNameValImpl[size]));
        }
        QNameSeqValImpl(XsQNameVal[] values) {
            this(Arrays.copyOf(values, values.length, QNameValImpl[].class));
        }
        QNameSeqValImpl(QNameValImpl[] values) {
            super(values);
        }
        @Override
        public XsQNameVal[] getQNameItems() {
            return getItems();
        }
    }
    static class QNameValImpl extends AnyAtomicTypeValImpl implements XsQNameVal {
        private QName value = null;
        public QNameValImpl(String localName) {
            this(new QName(localName));
        }
        public QNameValImpl(String namespace, String localName) {
            this(new QName(namespace, localName));
        }
        QNameValImpl(QName value) {
            super("QName");
            checkNull(value);
            this.value = value;
        }
        public static QNameValImpl valueOf(String key) {
            return new QNameValImpl(QName.valueOf(key));
        }
        @Override
        public QName getQName() {
            return value;
        }
        @Override
        public XsQNameVal[] getQNameItems() {
            return new XsQNameVal[]{this};
        }
        @Override
        public String toString() {
            return value.toString();
        }
    }

    // utilities
    private static void checkType(String typeName, QName type) {
        if (!typeName.equals(type.getLocalPart())) {
            throw new IllegalArgumentException(
                    "requires "+typeName+" instead of "+type.getLocalPart()
                    );
        }
    }
    private static void checkNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot take null value");
        }
    }
    private static void checkLength(byte[] value) {
        checkNull(value);
        if (value.length == 0) {
            throw new IllegalArgumentException("cannot take empty array value");
        }
    }
    private static Calendar from(Date value) {
        if (value == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        return cal;
    }
    // javax.xml.datatype.Duration.getXMLSchemaType() requires all fields to be set
    private static QName getDurationType(Duration value) {
        boolean hasYearMonth = (
                value.isSet(DatatypeConstants.YEARS)  ||
                value.isSet(DatatypeConstants.MONTHS)
                );
        boolean hasDayTime = (
                value.isSet(DatatypeConstants.DAYS)    ||
                value.isSet(DatatypeConstants.HOURS)   ||
                value.isSet(DatatypeConstants.MINUTES) ||
                value.isSet(DatatypeConstants.SECONDS)
                );

        if (hasYearMonth && !hasDayTime) {
            return DatatypeConstants.DURATION_YEARMONTH;
        } else if ((!hasYearMonth) && hasDayTime) {
            return DatatypeConstants.DURATION_DAYTIME;
        }

        throw new IllegalArgumentException("value must be yearMonthDuration or dayTimeDuration: "+value);
    }
}
