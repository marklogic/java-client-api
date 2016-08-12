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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
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
	public XsAnyURISeqVal anyURIs(String... values) {
		return new AnyURISeqValImpl(values);
	}

	@Override
	public XsBase64BinaryVal base64Binary(byte[] value) {
		return new Base64BinaryValImpl(value);
	}
	@Override
	public XsBase64BinarySeqVal base64Binarys(byte[]... values) {
		return new Base64BinarySeqValImpl(values);
	}

	@Override
	public XsBooleanVal booleanVal(boolean value) {
		return new BooleanValImpl(value);
	}
	@Override
	public XsBooleanSeqVal booleanVals(boolean... values) {
		return new BooleanSeqValImpl(values);
	}

	@Override
	public XsByteVal byteVal(byte value) {
		return new ByteValImpl(value);
	}
	@Override
	public XsByteSeqVal byteVals(byte... values) {
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
	public XsDateSeqVal dates(String... values) {
		return new DateSeqValImpl(values);
	}
	@Override
	public XsDateSeqVal dates(Calendar... values) {
		return new DateSeqValImpl(values);
	}
	@Override
	public XsDateSeqVal dates(XMLGregorianCalendar... values) {
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
	public XsDateTimeSeqVal dateTimes(String... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsDateTimeSeqVal dateTimes(Date... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsDateTimeSeqVal dateTimes(Calendar... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsDateTimeSeqVal dateTimes(XMLGregorianCalendar... values) {
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
	public XsDayTimeDurationSeqVal dayTimeDurations(String... values) {
		return new DayTimeDurationSeqValImpl(values);
	}
	@Override
	public XsDayTimeDurationSeqVal dayTimeDurations(Duration... values) {
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
	public XsDecimalSeqVal decimals(String... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsDecimalSeqVal decimals(long... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsDecimalSeqVal decimals(double... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsDecimalSeqVal decimals(BigDecimal... values) {
		return new DecimalSeqValImpl(values);
	}

	@Override
	public XsDoubleVal doubleVal(double value) {
		return new DoubleValImpl(value);
	}
	@Override
	public XsDoubleSeqVal doubleVals(double... values) {
		return new DoubleSeqValImpl(values);
	}
	@Override
	public XsFloatVal floatVal(float value) {
		return new FloatValImpl(value);
	}
	@Override
	public XsFloatSeqVal floatVals(float... values) {
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
	public XsGDaySeqVal gDays(String... values) {
		return new GDaySeqValImpl(values);
	}
	@Override
	public XsGDaySeqVal gDays(XMLGregorianCalendar... values) {
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
	public XsGMonthSeqVal gMonths(String... values) {
		return new GMonthSeqValImpl(values);
	}
	@Override
	public XsGMonthSeqVal gMonths(XMLGregorianCalendar... values) {
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
	public XsGMonthDaySeqVal gMonthDays(String... values) {
		return new GMonthDaySeqValImpl(values);
	}
	@Override
	public XsGMonthDaySeqVal gMonthDays(XMLGregorianCalendar... values) {
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
	public XsGYearSeqVal gYears(String... values) {
		return new GYearSeqValImpl(values);
	}
	@Override
	public XsGYearSeqVal gYears(XMLGregorianCalendar... values) {
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
	public XsGYearMonthSeqVal gYearMonths(String... values) {
		return new GYearMonthSeqValImpl(values);
	}
	@Override
	public XsGYearMonthSeqVal gYearMonths(XMLGregorianCalendar... values) {
		return new GYearMonthSeqValImpl(values);
	}

	@Override
	public XsHexBinaryVal hexBinary(byte[] value) {
		return new HexBinaryValImpl(value);
	}
	@Override
	public XsHexBinarySeqVal hexBinarys(byte[]... values) {
		return new HexBinarySeqValImpl(values);
	}

	@Override
	public XsIntVal intVal(int value) {
		return new IntValImpl(value);
	}
	@Override
	public XsIntSeqVal intVals(int... values) {
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
	public XsIntegerSeqVal integers(String... values) {
		return new IntegerSeqValImpl(values);
	}
	@Override
	public XsIntegerSeqVal integers(long... values) {
		return new IntegerSeqValImpl(values);
	}
	@Override
	public XsIntegerSeqVal integers(BigInteger... values) {
		return new IntegerSeqValImpl(values);
	}

	@Override
	public XsLongVal longVal(long value) {
		return new LongValImpl(value);
	}
	@Override
	public XsLongSeqVal longVals(long... values) {
		return new LongSeqValImpl(values);
	}

	@Override
	public XsShortVal shortVal(short value) {
		return new ShortValImpl(value);
	}
	@Override
	public XsShortSeqVal shortVals(short... values) {
		return new ShortSeqValImpl(values);
	}

	@Override
	public XsStringVal string(String value) {
		return new StringValImpl(value);
	}
	@Override
	public XsStringSeqVal strings(String... values) {
		return new StringSeqValImpl(values);
	}
    @Override
    public XsStringSeqVal strings(XsStringVal... values) {
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
	public XsTimeSeqVal times(String... values) {
		return new TimeSeqValImpl(values);
	}
	@Override
	public XsTimeSeqVal times(Calendar... values) {
		return new TimeSeqValImpl(values);
	}
	@Override
	public XsTimeSeqVal times(XMLGregorianCalendar... values) {
		return new TimeSeqValImpl(values);
	}

	@Override
	public XsUnsignedByteVal unsignedByte(byte value) {
		return new UnsignedByteValImpl(value);
	}
	@Override
	public XsUnsignedByteSeqVal unsignedBytes(byte... values) {
		return new UnsignedByteSeqValImpl(values);
	}

	@Override
	public XsUnsignedIntVal unsignedInt(int value) {
		return new UnsignedIntValImpl(value);
	}
	@Override
	public XsUnsignedIntSeqVal unsignedInts(int... values) {
		return new UnsignedIntSeqValImpl(values);
	}

	@Override
	public XsUnsignedLongVal unsignedLong(long value) {
		return new UnsignedLongValImpl(value);
	}
	@Override
	public XsUnsignedLongSeqVal unsignedLongs(long... values) {
		return new UnsignedLongSeqValImpl(values);
	}

	@Override
	public XsUnsignedShortVal unsignedShort(short value) {
		return new UnsignedShortValImpl(value);
	}
	@Override
	public XsUnsignedShortSeqVal unsignedShorts(short... values) {
		return new UnsignedShortSeqValImpl(values);
	}

	@Override
	public XsUntypedAtomicVal untypedAtomic(String value) {
		return new UntypedAtomicValImpl(value);
	}
	@Override
	public XsUntypedAtomicSeqVal untypedAtomics(String... values) {
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
	public XsYearMonthDurationSeqVal yearMonthDurations(String... values) {
		return new YearMonthDurationSeqValImpl(values);
	}
	@Override
	public XsYearMonthDurationSeqVal yearMonthDurations(Duration... values) {
		return new YearMonthDurationSeqValImpl(values);
	}

	@Override
	public XsQNameVal qname(String localName) {
		return new QNameValImpl(localName);
	}
	@Override
	public XsQNameVal qname(String namespace, String prefix, String localName) {
		return new QNameValImpl(namespace, localName, prefix);
	}
	@Override
	public XsQNameVal qname(QName value) {
		return new QNameValImpl(value);
	}
	@Override
	public XsQNameSeqVal qnames(String... localNames) {
		return new QNameSeqValImpl(localNames);
	}
	@Override
	public XsQNameSeqVal qnames(String namespace, String prefix, String... localNames) {
		return new QNameSeqValImpl(namespace, prefix, localNames);
	}
	@Override
	public XsQNameSeqVal qnames(QName... values) {
		return new QNameSeqValImpl(values);
	}

	static XsAnySimpleTypeSeqVal anySimpleTypes(XsAnySimpleTypeVal... items) {
		return new AnySimpleTypeSeqValImpl<AnySimpleTypeValImpl>(BaseTypeImpl.convertList(items, AnySimpleTypeValImpl.class));
	}
	static XsAnyAtomicTypeSeqVal anyAtomicTypes(XsAnyAtomicTypeVal... items) {
		return new AnyAtomicTypeSeqValImpl<AnyAtomicTypeValImpl>(BaseTypeImpl.convertList(items, AnyAtomicTypeValImpl.class));
	}

	static class AnySimpleTypeSeqValImpl<T extends AnySimpleTypeValImpl>
	extends BaseTypeImpl.BaseListImpl<T>
	implements XsAnySimpleTypeSeqVal, BaseTypeImpl.BaseArgImpl {
		AnySimpleTypeSeqValImpl(T[] values) {
			super(values);
		}
		@Override
		public XsAnySimpleTypeVal[] getAnySimpleTypeItems() {
			return getItems();
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
	static class AnyAtomicTypeValImpl extends AnySimpleTypeValImpl implements XsAnyAtomicTypeVal {
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
	}

    // implementations
	static class AnyURISeqValImpl extends AnyAtomicTypeSeqValImpl<AnyURIValImpl> implements XsAnyURISeqVal {
		AnyURISeqValImpl(String[] values) {
			super(Arrays.stream(values)
					              .map(val -> new AnyURIValImpl(val))
					              .toArray(size -> new AnyURIValImpl[size]));
		}
		@Override
		public XsAnyURIVal[] getAnyURIItems() {
			return getItems();
		}
    }
    static class AnyURIValImpl extends AnyAtomicTypeValImpl implements XsAnyURIVal {
    	private String value = null;
    	AnyURIValImpl(String value) {
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
			super(Arrays.stream(values)
		                                .map(val -> new Base64BinaryValImpl(val))
		                                .toArray(size -> new Base64BinaryValImpl[size]));
		}

		@Override
		public XsBase64BinaryVal[] getBase64BinaryItems() {
			return getItems();
		}
    }
    static class Base64BinaryValImpl extends AnyAtomicTypeValImpl implements XsBase64BinaryVal {
    	private byte[] value = null;
    	Base64BinaryValImpl(byte[] value) {
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
			super(toArray(values));
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
    	BooleanValImpl(boolean value) {
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
			super(toArray(values));
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
    	ByteValImpl(byte value) {
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
    	DateValImpl(String value) {
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
    	DateTimeValImpl(String value) {
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
    	DayTimeDurationValImpl(String value) {
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
    	DecimalValImpl(String value) {
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
			super(toArray(values));
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
    	DoubleValImpl(double value) {
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
			super(toArray(values));
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
    	FloatValImpl(float value) {
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
    	GDayValImpl(String value) {
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
    	GMonthValImpl(String value) {
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
        	return value.toXMLFormat();
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
    	GMonthDayValImpl(String value) {
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
    	GYearValImpl(String value) {
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
    	GYearMonthValImpl(String value) {
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
			super(Arrays.stream(values)
		                .map(val -> new HexBinaryValImpl(val))
		                .toArray(size -> new HexBinaryValImpl[size]));
		}
		@Override
		public XsHexBinaryVal[] getHexBinaryItems() {
			return getItems();
		}
    }
    static class HexBinaryValImpl extends AnyAtomicTypeValImpl implements XsHexBinaryVal {
    	private byte[] value = null;
    	HexBinaryValImpl(byte[] value) {
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
			super(Arrays.stream(values)
   					    .mapToObj(val -> new IntValImpl(val))
   					    .toArray(size -> new IntValImpl[size]));
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
    	IntValImpl(int value) {
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
    	IntegerValImpl(String value) {
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
			super(Arrays.stream(values)
   					    .mapToObj(val -> new LongValImpl(val))
   					    .toArray(size -> new LongValImpl[size]));
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
    	LongValImpl(long value) {
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
			super(toArray(values));
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
    	ShortValImpl(short value) {
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
			this((StringValImpl[]) Arrays.stream(values)
	                .map(val -> {
	                	if (!(val instanceof StringValImpl)) {
	                		throw new IllegalArgumentException("argument with unknown class "+val.getClass().getName());
	                	}
	                	return (StringValImpl) val;
	                	})
	                .toArray(size -> new StringValImpl[size]));
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
    	StringValImpl(String value) {
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
    	TimeValImpl(String value) {
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
			super(toArray(values));
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
    	UnsignedByteValImpl(byte value) {
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
			super(Arrays.stream(values)
   					    .mapToObj(val -> new UnsignedIntValImpl(val))
   					    .toArray(size -> new UnsignedIntValImpl[size]));
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
    	UnsignedIntValImpl(int value) {
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
			super(Arrays.stream(values)
   					    .mapToObj(val -> new UnsignedLongValImpl(val))
   					    .toArray(size -> new UnsignedLongValImpl[size]));
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
    	UnsignedLongValImpl(long value) {
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
			super(toArray(values));
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
    	UnsignedShortValImpl(short value) {
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
			super(Arrays.stream(values)
		                .map(val -> new UntypedAtomicValImpl(val))
		                .toArray(size -> new UntypedAtomicValImpl[size]));
		}
		@Override
		public XsUntypedAtomicVal[] getUntypedAtomicItems() {
			return getItems();
		}
    }
    static class UntypedAtomicValImpl extends AnyAtomicTypeValImpl implements XsUntypedAtomicVal {
    	private String value = null;
    	UntypedAtomicValImpl(String value) {
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
    	YearMonthDurationValImpl(String value) {
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
    	QNameSeqValImpl(String namespace, String prefix, String[] localNames) {
			this((QNameValImpl[]) Arrays.stream(localNames)
		               .map(val -> new QNameValImpl(namespace, prefix, val))
		               .toArray(size -> new QNameValImpl[size]));
    	}
    	QNameSeqValImpl(QName[] values) {
			this((QNameValImpl[]) Arrays.stream(values)
		               .map(val -> new QNameValImpl(val))
		               .toArray(size -> new QNameValImpl[size]));
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
    	QNameValImpl(String localName) {
    		this(new QName(localName));
    	}
    	QNameValImpl(String namespace, String prefix, String localName) {
    		this(new QName(namespace, localName, prefix));
    	}
    	QNameValImpl(QName value) {
    		super("QName");
    		checkNull(value);
    		this.value = value;
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
