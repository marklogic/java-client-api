package com.marklogic.client.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.marklogic.client.expression.XsValue;

public class XsValueImpl implements XsValue {
	@Override
	public XsValue.AnyURIVal anyURI(String value) {
		return new AnyURIValImpl(value);
	}
	@Override
	public XsValue.AnyURISeqVal anyURIs(String... values) {
		return new AnyURISeqValImpl(values);
	}

	@Override
	public XsValue.Base64BinaryVal base64Binary(byte[] value) {
		return new Base64BinaryValImpl(value);
	}
	@Override
	public XsValue.Base64BinarySeqVal base64Binarys(byte[]... values) {
		return new Base64BinarySeqValImpl(values);
	}

	@Override
	public XsValue.BooleanVal booleanVal(boolean value) {
		return new BooleanValImpl(value);
	}
	@Override
	public XsValue.BooleanSeqVal booleanVals(boolean... values) {
		return new BooleanSeqValImpl(values);
	}

	@Override
	public XsValue.ByteVal byteVal(byte value) {
		return new ByteValImpl(value);
	}
	@Override
	public XsValue.ByteSeqVal byteVals(byte... values) {
		return new ByteSeqValImpl(values);
	}

	@Override
	public XsValue.DateVal date(String value) {
		return new DateValImpl(value);
	}
	@Override
	public XsValue.DateVal date(Calendar value) {
		return new DateValImpl(value);
	}
	@Override
	public XsValue.DateVal date(XMLGregorianCalendar value) {
		return new DateValImpl(value);
	}
	@Override
	public XsValue.DateSeqVal dates(String... values) {
		return new DateSeqValImpl(values);
	}
	@Override
	public XsValue.DateSeqVal dates(Calendar... values) {
		return new DateSeqValImpl(values);
	}
	@Override
	public XsValue.DateSeqVal dates(XMLGregorianCalendar... values) {
		return new DateSeqValImpl(values);
	}

	@Override
	public XsValue.DateTimeVal dateTime(String value) {
		return new DateTimeValImpl(value);
	}
	@Override
	public XsValue.DateTimeVal dateTime(Date value) {
		return new DateTimeValImpl(value);
	}
	@Override
	public XsValue.DateTimeVal dateTime(Calendar value) {
		return new DateTimeValImpl(value);
	}
	@Override
	public XsValue.DateTimeVal dateTime(XMLGregorianCalendar value) {
		return new DateTimeValImpl(value);
	}
	@Override
	public XsValue.DateTimeSeqVal dateTimes(String... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsValue.DateTimeSeqVal dateTimes(Date... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsValue.DateTimeSeqVal dateTimes(Calendar... values) {
		return new DateTimeSeqValImpl(values);
	}
	@Override
	public XsValue.DateTimeSeqVal dateTimes(XMLGregorianCalendar... values) {
		return new DateTimeSeqValImpl(values);
	}

	@Override
	public XsValue.DayTimeDurationVal dayTimeDuration(String value) {
		return new DayTimeDurationValImpl(value);
	}
	@Override
	public XsValue.DayTimeDurationVal dayTimeDuration(Duration value) {
		return new DayTimeDurationValImpl(value);
	}
	@Override
	public XsValue.DayTimeDurationSeqVal dayTimeDurations(String... values) {
		return new DayTimeDurationSeqValImpl(values);
	}
	@Override
	public XsValue.DayTimeDurationSeqVal dayTimeDurations(Duration... values) {
		return new DayTimeDurationSeqValImpl(values);
	}

	@Override
	public XsValue.DecimalVal decimal(String value) {
		return new DecimalValImpl(value);
	}
	@Override
	public XsValue.DecimalVal decimal(long value) {
		return new DecimalValImpl(value);
	}
	@Override
	public XsValue.DecimalVal decimal(double value) {
		return new DecimalValImpl(value);
	}
	@Override
	public XsValue.DecimalVal decimal(BigDecimal value) {
		return new DecimalValImpl(value);
	}
	@Override
	public XsValue.DecimalSeqVal decimals(String... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsValue.DecimalSeqVal decimals(long... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsValue.DecimalSeqVal decimals(double... values) {
		return new DecimalSeqValImpl(values);
	}
	@Override
	public XsValue.DecimalSeqVal decimals(BigDecimal... values) {
		return new DecimalSeqValImpl(values);
	}

	@Override
	public XsValue.DoubleVal doubleVal(double value) {
		return new DoubleValImpl(value);
	}
	@Override
	public XsValue.DoubleSeqVal doubleVals(double... values) {
		return new DoubleSeqValImpl(values);
	}
	@Override
	public XsValue.FloatVal floatVal(float value) {
		return new FloatValImpl(value);
	}
	@Override
	public XsValue.FloatSeqVal floatVals(float... values) {
		return new FloatSeqValImpl(values);
	}

	@Override
	public XsValue.GDayVal gDay(String value) {
		return new GDayValImpl(value);
	}
	@Override
	public XsValue.GDayVal gDay(XMLGregorianCalendar value) {
		return new GDayValImpl(value);
	}
	@Override
	public XsValue.GDaySeqVal gDays(String... values) {
		return new GDaySeqValImpl(values);
	}
	@Override
	public XsValue.GDaySeqVal gDays(XMLGregorianCalendar... values) {
		return new GDaySeqValImpl(values);
	}

	@Override
	public XsValue.GMonthVal gMonth(String value) {
		return new GMonthValImpl(value);
	}
	@Override
	public XsValue.GMonthVal gMonth(XMLGregorianCalendar value) {
		return new GMonthValImpl(value);
	}
	@Override
	public XsValue.GMonthSeqVal gMonths(String... values) {
		return new GMonthSeqValImpl(values);
	}
	@Override
	public XsValue.GMonthSeqVal gMonths(XMLGregorianCalendar... values) {
		return new GMonthSeqValImpl(values);
	}

	@Override
	public XsValue.GMonthDayVal gMonthDay(String value) {
		return new GMonthDayValImpl(value);
	}
	@Override
	public XsValue.GMonthDayVal gMonthDay(XMLGregorianCalendar value) {
		return new GMonthDayValImpl(value);
	}
	@Override
	public XsValue.GMonthDaySeqVal gMonthDays(String... values) {
		return new GMonthDaySeqValImpl(values);
	}
	@Override
	public XsValue.GMonthDaySeqVal gMonthDays(XMLGregorianCalendar... values) {
		return new GMonthDaySeqValImpl(values);
	}

	@Override
	public XsValue.GYearVal gYear(String value) {
		return new GYearValImpl(value);
	}
	@Override
	public XsValue.GYearVal gYear(XMLGregorianCalendar value) {
		return new GYearValImpl(value);
	}
	@Override
	public XsValue.GYearSeqVal gYears(String... values) {
		return new GYearSeqValImpl(values);
	}
	@Override
	public XsValue.GYearSeqVal gYears(XMLGregorianCalendar... values) {
		return new GYearSeqValImpl(values);
	}

	@Override
	public XsValue.GYearMonthVal gYearMonth(String value) {
		return new GYearMonthValImpl(value);
	}
	@Override
	public XsValue.GYearMonthVal gYearMonth(XMLGregorianCalendar value) {
		return new GYearMonthValImpl(value);
	}
	@Override
	public XsValue.GYearMonthSeqVal gYearMonths(String... values) {
		return new GYearMonthSeqValImpl(values);
	}
	@Override
	public XsValue.GYearMonthSeqVal gYearMonths(XMLGregorianCalendar... values) {
		return new GYearMonthSeqValImpl(values);
	}

	@Override
	public XsValue.HexBinaryVal hexBinary(byte[] value) {
		return new HexBinaryValImpl(value);
	}
	@Override
	public XsValue.HexBinarySeqVal hexBinarys(byte[]... values) {
		return new HexBinarySeqValImpl(values);
	}

	@Override
	public XsValue.IntVal intVal(int value) {
		return new IntValImpl(value);
	}
	@Override
	public XsValue.IntSeqVal intVals(int... values) {
		return new IntSeqValImpl(values);
	}

	@Override
	public XsValue.IntegerVal integer(String value) {
		return new IntegerValImpl(value);
	}
	@Override
	public XsValue.IntegerVal integer(long value) {
		return new IntegerValImpl(value);
	}
	@Override
	public XsValue.IntegerVal integer(BigInteger value) {
		return new IntegerValImpl(value);
	}
	@Override
	public XsValue.IntegerSeqVal integers(String... values) {
		return new IntegerSeqValImpl(values);
	}
	@Override
	public XsValue.IntegerSeqVal integers(long... values) {
		return new IntegerSeqValImpl(values);
	}
	@Override
	public XsValue.IntegerSeqVal integers(BigInteger... values) {
		return new IntegerSeqValImpl(values);
	}

	@Override
	public XsValue.LongVal longVal(long value) {
		return new LongValImpl(value);
	}
	@Override
	public XsValue.LongSeqVal longVals(long... values) {
		return new LongSeqValImpl(values);
	}

	@Override
	public XsValue.ShortVal shortVal(short value) {
		return new ShortValImpl(value);
	}
	@Override
	public XsValue.ShortSeqVal shortVals(short... values) {
		return new ShortSeqValImpl(values);
	}

	@Override
	public XsValue.StringVal string(String value) {
		return new StringValImpl(value);
	}
	@Override
	public XsValue.StringSeqVal strings(String... values) {
		return new StringSeqValImpl(values);
	}

	@Override
	public XsValue.TimeVal time(String value) {
		return new TimeValImpl(value);
	}
	@Override
	public XsValue.TimeVal time(Calendar value) {
		return new TimeValImpl(value);
	}
	@Override
	public XsValue.TimeVal time(XMLGregorianCalendar value) {
		return new TimeValImpl(value);
	}
	@Override
	public XsValue.TimeSeqVal times(String... values) {
		return new TimeSeqValImpl(values);
	}
	@Override
	public XsValue.TimeSeqVal times(Calendar... values) {
		return new TimeSeqValImpl(values);
	}
	@Override
	public XsValue.TimeSeqVal times(XMLGregorianCalendar... values) {
		return new TimeSeqValImpl(values);
	}

	@Override
	public XsValue.UnsignedByteVal unsignedByte(byte value) {
		return new UnsignedByteValImpl(value);
	}
	@Override
	public XsValue.UnsignedByteSeqVal unsignedBytes(byte... values) {
		return new UnsignedByteSeqValImpl(values);
	}

	@Override
	public XsValue.UnsignedIntVal unsignedInt(int value) {
		return new UnsignedIntValImpl(value);
	}
	@Override
	public XsValue.UnsignedIntSeqVal unsignedInts(int... values) {
		return new UnsignedIntSeqValImpl(values);
	}

	@Override
	public XsValue.UnsignedLongVal unsignedLong(long value) {
		return new UnsignedLongValImpl(value);
	}
	@Override
	public XsValue.UnsignedLongSeqVal unsignedLongs(long... values) {
		return new UnsignedLongSeqValImpl(values);
	}

	@Override
	public XsValue.UnsignedShortVal unsignedShort(short value) {
		return new UnsignedShortValImpl(value);
	}
	@Override
	public XsValue.UnsignedShortSeqVal unsignedShorts(short... values) {
		return new UnsignedShortSeqValImpl(values);
	}

	@Override
	public XsValue.UntypedAtomicVal untypedAtomic(String value) {
		return new UntypedAtomicValImpl(value);
	}
	@Override
	public XsValue.UntypedAtomicSeqVal untypedAtomics(String... values) {
		return new UntypedAtomicSeqValImpl(values);
	}

	@Override
	public XsValue.YearMonthDurationVal yearMonthDuration(String value) {
		return new YearMonthDurationValImpl(value);
	}
	@Override
	public XsValue.YearMonthDurationVal yearMonthDuration(Duration value) {
		return new YearMonthDurationValImpl(value);
	}
	@Override
	public XsValue.YearMonthDurationSeqVal yearMonthDurations(String... values) {
		return new YearMonthDurationSeqValImpl(values);
	}
	@Override
	public XsValue.YearMonthDurationSeqVal yearMonthDurations(Duration... values) {
		return new YearMonthDurationSeqValImpl(values);
	}

	@Override
	public XsValue.QNameVal qname(String localName) {
		return new QNameValImpl(localName);
	}
	@Override
	public XsValue.QNameVal qname(String namespace, String prefix, String localName) {
		return new QNameValImpl(namespace, localName, prefix);
	}
	@Override
	public XsValue.QNameVal qname(QName value) {
		return new QNameValImpl(value);
	}
	@Override
	public XsValue.QNameSeqVal qnames(String... localNames) {
		return new QNameSeqValImpl(localNames);
	}
	@Override
	public XsValue.QNameSeqVal qnames(String namespace, String prefix, String... localNames) {
		return new QNameSeqValImpl(namespace, prefix, localNames);
	}
	@Override
	public XsValue.QNameSeqVal qnames(QName... values) {
		return new QNameSeqValImpl(values);
	}

	static XsValue.AnySimpleTypeSeqVal anySimpleTypes(XsValue.AnySimpleTypeVal... items) {
		return new AnySimpleTypeSeqValImpl<AnySimpleTypeValImpl>(BaseTypeImpl.convertList(items, AnySimpleTypeValImpl.class));
	}
	static XsValue.AnyAtomicTypeSeqVal anyAtomicTypes(XsValue.AnyAtomicTypeVal... items) {
		return new AnyAtomicTypeSeqValImpl<AnyAtomicTypeValImpl>(BaseTypeImpl.convertList(items, AnyAtomicTypeValImpl.class));
	}

	static class AnySimpleTypeSeqValImpl<T extends AnySimpleTypeValImpl>
	extends BaseTypeImpl.BaseListImpl<T>
	implements XsValue.AnySimpleTypeSeqVal, BaseTypeImpl.BaseArgImpl {
		AnySimpleTypeSeqValImpl(T[] values) {
			super(values);
		}
		@Override
		public AnySimpleTypeVal[] getAnySimpleTypeItems() {
			return getItems();
		}
	}
	static class AnySimpleTypeValImpl implements XsValue.AnySimpleTypeVal, BaseTypeImpl.BaseArgImpl {
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
		public AnySimpleTypeVal[] getAnySimpleTypeItems() {
			return getItems();
		}
		@Override
		public XsValue.AnySimpleTypeVal[] getItems() {
			return new XsValue.AnySimpleTypeVal[]{this};
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
	implements XsValue.AnyAtomicTypeSeqVal {
		AnyAtomicTypeSeqValImpl(T[] values) {
			super(values);
		}
		@Override
		public AnyAtomicTypeVal[] getAnyAtomicTypeItems() {
			return getItems();
		}
	}
	static class AnyAtomicTypeValImpl extends AnySimpleTypeValImpl implements XsValue.AnyAtomicTypeVal {
		AnyAtomicTypeValImpl(String typeName) {
			super(typeName);
		}
		AnyAtomicTypeValImpl(String typePrefix, String typeName) {
			super(typePrefix, typeName);
		}
		@Override
		public XsValue.AnyAtomicTypeVal[] getAnyAtomicTypeItems() {
			return new XsValue.AnyAtomicTypeVal[]{this};
		}
	}

    // implementations
	static class AnyURISeqValImpl extends AnyAtomicTypeSeqValImpl<AnyURIValImpl> implements XsValue.AnyURISeqVal {
		AnyURISeqValImpl(String[] values) {
			super(Arrays.stream(values)
					              .map(val -> new AnyURIValImpl(val))
					              .toArray(size -> new AnyURIValImpl[size]));
		}
		@Override
		public XsValue.AnyURIVal[] getAnyURIItems() {
			return getItems();
		}
    }
    static class AnyURIValImpl extends AnyAtomicTypeValImpl implements XsValue.AnyURIVal {
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
		public XsValue.AnyURIVal[] getAnyURIItems() {
			return new XsValue.AnyURIVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printAnySimpleType(value);
        }
    }
    static class Base64BinarySeqValImpl extends AnyAtomicTypeSeqValImpl<Base64BinaryValImpl> implements XsValue.Base64BinarySeqVal {
    	Base64BinarySeqValImpl(byte[][] values) {
			super(Arrays.stream(values)
		                                .map(val -> new Base64BinaryValImpl(val))
		                                .toArray(size -> new Base64BinaryValImpl[size]));
		}

		@Override
		public Base64BinaryVal[] getBase64BinaryItems() {
			return getItems();
		}
    }
    static class Base64BinaryValImpl extends AnyAtomicTypeValImpl implements XsValue.Base64BinaryVal {
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
		public XsValue.Base64BinaryVal[] getBase64BinaryItems() {
			return new XsValue.Base64BinaryVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printBase64Binary(value);
        }
    }
    static class BooleanSeqValImpl extends AnyAtomicTypeSeqValImpl<BooleanValImpl> implements XsValue.BooleanSeqVal {
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
		public BooleanVal[] getBooleanItems() {
			return getItems();
		}
    }
    static class BooleanValImpl extends AnyAtomicTypeValImpl implements XsValue.BooleanVal {
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
		public XsValue.BooleanVal[] getBooleanItems() {
			return new XsValue.BooleanVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printBoolean(value);
        }
    }
    static class ByteSeqValImpl extends AnyAtomicTypeSeqValImpl<ByteValImpl> implements XsValue.ByteSeqVal {
    	ByteSeqValImpl(byte[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public ShortVal[] getShortItems() {
			return getItems();
		}
		@Override
		public IntVal[] getIntItems() {
			return getItems();
		}
		@Override
		public LongVal[] getLongItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public ByteVal[] getByteItems() {
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
    static class ByteValImpl extends AnyAtomicTypeValImpl implements XsValue.ByteVal {
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
		public XsValue.ByteVal[] getByteItems() {
			return new XsValue.ByteVal[]{this};
		}
		@Override
		public short getShort() {
			return value;
		}
		@Override
		public ShortVal[] getShortItems() {
			return getByteItems();
		}
		@Override
		public int getInt() {
			return value;
		}
		@Override
		public IntVal[] getIntItems() {
			return getByteItems();
		}
		@Override
		public long getLong() {
			return value;
		}
		@Override
		public LongVal[] getLongItems() {
			return getByteItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getByteItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getByteItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getByteItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printByte(value);
        }
    }
    static class DateSeqValImpl extends AnyAtomicTypeSeqValImpl<DateValImpl> implements XsValue.DateSeqVal {
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
		public DateVal[] getDateItems() {
			return getItems();
		}
    }
    static class DateValImpl extends AnyAtomicTypeValImpl implements XsValue.DateVal {
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
		public XsValue.DateVal[] getDateItems() {
			return new XsValue.DateVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printDate(value);
        }
    }
    static class DateTimeSeqValImpl extends AnyAtomicTypeSeqValImpl<DateTimeValImpl> implements XsValue.DateTimeSeqVal {
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
		public DateTimeVal[] getDateTimeItems() {
			return getItems();
		}
    }
    static class DateTimeValImpl extends AnyAtomicTypeValImpl implements XsValue.DateTimeVal {
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
		public XsValue.DateTimeVal[] getDateTimeItems() {
			return new XsValue.DateTimeVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printDateTime(value);
        }
    }
    static class DayTimeDurationSeqValImpl extends AnyAtomicTypeSeqValImpl<DayTimeDurationValImpl> implements XsValue.DayTimeDurationSeqVal {
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
		public DurationVal[] getDurationItems() {
			return getItems();
		}
		@Override
		public DayTimeDurationVal[] getDayTimeDurationItems() {
			return getItems();
		}
    }
    static class DayTimeDurationValImpl extends AnyAtomicTypeValImpl implements XsValue.DayTimeDurationVal {
    	private Duration value = null;
    	DayTimeDurationValImpl(String value) {
    		this(Utilities.getDatatypeFactory().newDuration(value));
    	}
    	DayTimeDurationValImpl(Duration value) {
    		super("dayTimeDuration");
    		checkNull(value);
    		checkType("dayTimeDuration", value.getXMLSchemaType());
    		this.value = value;
    	}
		@Override
        public Duration getDuration() {
        	return value;
        }
		@Override
		public XsValue.DayTimeDurationVal[] getDayTimeDurationItems() {
			return new XsValue.DayTimeDurationVal[]{this};
		}
		@Override
		public DurationVal[] getDurationItems() {
			return getDayTimeDurationItems();
		}
		@Override
        public String toString() {
        	return value.toString();
        }
    }
    static class DecimalSeqValImpl extends AnyAtomicTypeSeqValImpl<DecimalValImpl> implements XsValue.DecimalSeqVal {
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
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
    }
    static class DecimalValImpl extends AnyAtomicTypeValImpl implements XsValue.DecimalVal {
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
		public XsValue.DecimalVal[] getDecimalItems() {
			return new XsValue.DecimalVal[]{this};
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getDecimalItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printDecimal(value);
        }
    }
    static class DoubleSeqValImpl extends AnyAtomicTypeSeqValImpl<DoubleValImpl> implements XsValue.DoubleSeqVal {
    	DoubleSeqValImpl(double[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public DoubleVal[] getDoubleItems() {
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
    static class DoubleValImpl extends AnyAtomicTypeValImpl implements XsValue.DoubleVal {
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
		public XsValue.DoubleVal[] getDoubleItems() {
			return new XsValue.DoubleVal[]{this};
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getDoubleItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printDouble(value);
        }
    }
    static class FloatSeqValImpl extends AnyAtomicTypeSeqValImpl<FloatValImpl> implements XsValue.FloatSeqVal {
    	FloatSeqValImpl(float[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public FloatVal[] getFloatItems() {
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
    static class FloatValImpl extends AnyAtomicTypeValImpl implements XsValue.FloatVal {
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
		public XsValue.FloatVal[] getFloatItems() {
			return new XsValue.FloatVal[]{this};
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getFloatItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printFloat(value);
        }
    }
    static class GDaySeqValImpl extends AnyAtomicTypeSeqValImpl<GDayValImpl> implements XsValue.GDaySeqVal {
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
		public GDayVal[] getGDayItems() {
			return getItems();
		}
    }
    static class GDayValImpl extends AnyAtomicTypeValImpl implements XsValue.GDayVal {
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
		public XsValue.GDayVal[] getGDayItems() {
			return new XsValue.GDayVal[]{this};
		}
		@Override
        public String toString() {
        	return value.toXMLFormat();
        }
    }
    static class GMonthSeqValImpl extends AnyAtomicTypeSeqValImpl<GMonthValImpl> implements XsValue.GMonthSeqVal {
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
		public GMonthVal[] getGMonthItems() {
			return getItems();
		}
    }
    static class GMonthValImpl extends AnyAtomicTypeValImpl implements XsValue.GMonthVal {
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
		public XsValue.GMonthVal[] getGMonthItems() {
			return new XsValue.GMonthVal[]{this};
		}
		@Override
        public String toString() {
        	return value.toXMLFormat();
        }
    }
    static class GMonthDaySeqValImpl extends AnyAtomicTypeSeqValImpl<GMonthDayValImpl> implements XsValue.GMonthDaySeqVal {
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
		public GMonthDayVal[] getGMonthDayItems() {
			return getItems();
		}
    }
    static class GMonthDayValImpl extends AnyAtomicTypeValImpl implements XsValue.GMonthDayVal {
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
		public XsValue.GMonthDayVal[] getGMonthDayItems() {
			return new XsValue.GMonthDayVal[]{this};
		}
		@Override
        public String toString() {
        	return value.toXMLFormat();
        }
    }
    static class GYearSeqValImpl extends AnyAtomicTypeSeqValImpl<GYearValImpl> implements XsValue.GYearSeqVal {
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
		public GYearVal[] getGYearItems() {
			return getItems();
		}
    }
    static class GYearValImpl extends AnyAtomicTypeValImpl implements XsValue.GYearVal {
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
		public XsValue.GYearVal[] getGYearItems() {
			return new XsValue.GYearVal[]{this};
		}
		@Override
        public String toString() {
        	return value.toXMLFormat();
        }
    }
// TODO: confim
    static class GYearMonthSeqValImpl extends AnyAtomicTypeSeqValImpl<GYearMonthValImpl> implements XsValue.GYearMonthSeqVal {
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
		public GYearMonthVal[] getGYearMonthItems() {
			return getItems();
		}
    }
 // TODO: confim
    static class GYearMonthValImpl extends AnyAtomicTypeValImpl implements XsValue.GYearMonthVal {
    	private XMLGregorianCalendar value = null;
    	GYearMonthValImpl(String value) {
    		this(Utilities.getDatatypeFactory().newXMLGregorianCalendar(value));
    	}
    	GYearMonthValImpl(XMLGregorianCalendar value) {
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
		public XsValue.GYearMonthVal[] getGYearMonthItems() {
			return new XsValue.GYearMonthVal[]{this};
		}
		@Override
        public String toString() {
        	return value.toXMLFormat();
        }
    }
    static class HexBinarySeqValImpl extends AnyAtomicTypeSeqValImpl<HexBinaryValImpl> implements XsValue.HexBinarySeqVal {
    	HexBinarySeqValImpl(byte[][] values) {
			super(Arrays.stream(values)
		                .map(val -> new HexBinaryValImpl(val))
		                .toArray(size -> new HexBinaryValImpl[size]));
		}
		@Override
		public HexBinaryVal[] getHexBinaryItems() {
			return getItems();
		}
    }
    static class HexBinaryValImpl extends AnyAtomicTypeValImpl implements XsValue.HexBinaryVal {
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
		public XsValue.HexBinaryVal[] getHexBinaryItems() {
			return new XsValue.HexBinaryVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printHexBinary(value);
        }
    }
    static class IntSeqValImpl extends AnyAtomicTypeSeqValImpl<IntValImpl> implements XsValue.IntSeqVal {
    	IntSeqValImpl(int[] values) {
			super(Arrays.stream(values)
   					    .mapToObj(val -> new IntValImpl(val))
   					    .toArray(size -> new IntValImpl[size]));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public LongVal[] getLongItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public IntVal[] getIntItems() {
			return getItems();
		}
    }
    static class IntValImpl extends AnyAtomicTypeValImpl implements XsValue.IntVal {
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
		public XsValue.IntVal[] getIntItems() {
			return new XsValue.IntVal[]{this};
		}
		@Override
		public long getLong() {
			return value;
		}
		@Override
		public LongVal[] getLongItems() {
			return getIntItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getIntItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getIntItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getIntItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printInt(value);
        }
    }
    static class IntegerSeqValImpl extends AnyAtomicTypeSeqValImpl<IntegerValImpl> implements XsValue.IntegerSeqVal {
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
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
    }
    static class IntegerValImpl extends AnyAtomicTypeValImpl implements XsValue.IntegerVal {
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
		public XsValue.IntegerVal[] getIntegerItems() {
			return new XsValue.IntegerVal[]{this};
		}
		@Override
		public BigDecimal getBigDecimal() {
			return new BigDecimal(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getIntegerItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getIntegerItems();
		}
    	@Override
        public String toString() {
        	return DatatypeConverter.printInteger(value);
        }
    }
    static class LongSeqValImpl extends AnyAtomicTypeSeqValImpl<LongValImpl> implements XsValue.LongSeqVal {
    	LongSeqValImpl(long[] values) {
			super(Arrays.stream(values)
   					    .mapToObj(val -> new LongValImpl(val))
   					    .toArray(size -> new LongValImpl[size]));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public LongVal[] getLongItems() {
			return getItems();
		}
    }
    static class LongValImpl extends AnyAtomicTypeValImpl implements XsValue.LongVal {
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
		public XsValue.LongVal[] getLongItems() {
			return new XsValue.LongVal[]{this};
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getLongItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getLongItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getLongItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printLong(value);
        }
    }
    static class ShortSeqValImpl extends AnyAtomicTypeSeqValImpl<ShortValImpl> implements XsValue.ShortSeqVal {
    	ShortSeqValImpl(short[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
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
		public IntVal[] getIntItems() {
			return getItems();
		}
		@Override
		public LongVal[] getLongItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public ShortVal[] getShortItems() {
			return getItems();
		}
    }
    static class ShortValImpl extends AnyAtomicTypeValImpl implements XsValue.ShortVal {
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
		public XsValue.ShortVal[] getShortItems() {
			return new XsValue.ShortVal[]{this};
		}
		@Override
		public int getInt() {
			return value;
		}
		@Override
		public IntVal[] getIntItems() {
			return getShortItems();
		}
		@Override
		public long getLong() {
			return value;
		}
		@Override
		public LongVal[] getLongItems() {
			return getShortItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getShortItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getShortItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getShortItems();
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printShort(value);
        }
    }
    static class StringSeqValImpl extends AnyAtomicTypeSeqValImpl<StringValImpl> implements XsValue.StringSeqVal {
    	StringSeqValImpl(String[] values) {
			super(Arrays.stream(values)
		                .map(val -> new StringValImpl(val))
		                .toArray(size -> new StringValImpl[size]));
		}
		@Override
		public StringVal[] getStringItems() {
			return getItems();
		}
    }
    static class StringValImpl extends AnyAtomicTypeValImpl implements XsValue.StringVal {
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
		public XsValue.StringVal[] getStringItems() {
			return new XsValue.StringVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printString(value);
        }
    }
    static class TimeSeqValImpl extends AnyAtomicTypeSeqValImpl<TimeValImpl> implements XsValue.TimeSeqVal {
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
		public TimeVal[] getTimeItems() {
			return getItems();
		}
    }
    static class TimeValImpl extends AnyAtomicTypeValImpl implements XsValue.TimeVal {
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
		public XsValue.TimeVal[] getTimeItems() {
			return new XsValue.TimeVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printTime(value);
        }
    }
    static class UnsignedByteSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedByteValImpl> implements XsValue.UnsignedByteSeqVal {
    	UnsignedByteSeqValImpl(byte[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
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
		public UnsignedShortVal[] getUnsignedShortItems() {
			return getItems();
		}
		@Override
		public UnsignedIntVal[] getUnsignedIntItems() {
			return getItems();
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public UnsignedByteVal[] getUnsignedByteItems() {
			return getItems();
		}
    }
    static class UnsignedByteValImpl extends AnyAtomicTypeValImpl implements XsValue.UnsignedByteVal {
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
		public XsValue.UnsignedByteVal[] getUnsignedByteItems() {
			return new XsValue.UnsignedByteVal[]{this};
		}
		@Override
		public short getShort() {
        	return value;
		}
		@Override
		public UnsignedShortVal[] getUnsignedShortItems() {
			return getUnsignedByteItems();
		}
		@Override
		public int getInt() {
        	return value;
		}
		@Override
		public UnsignedIntVal[] getUnsignedIntItems() {
			return getUnsignedByteItems();
		}
		@Override
		public long getLong() {
        	return value;
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getUnsignedByteItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getUnsignedByteItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(Byte.toUnsignedInt(value));
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getUnsignedByteItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(Byte.toUnsignedInt(value));
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getUnsignedByteItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getUnsignedByteItems();
		}
		@Override
        public String toString() {
        	return Integer.toUnsignedString(Byte.toUnsignedInt(value));
        }
    }
    static class UnsignedIntSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedIntValImpl> implements XsValue.UnsignedIntSeqVal {
    	UnsignedIntSeqValImpl(int[] values) {
			super(Arrays.stream(values)
   					    .mapToObj(val -> new UnsignedIntValImpl(val))
   					    .toArray(size -> new UnsignedIntValImpl[size]));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public UnsignedIntVal[] getUnsignedIntItems() {
			return getItems();
		}
    }
    static class UnsignedIntValImpl extends AnyAtomicTypeValImpl implements XsValue.UnsignedIntVal {
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
		public XsValue.UnsignedIntVal[] getUnsignedIntItems() {
			return new XsValue.UnsignedIntVal[]{this};
		}
		@Override
		public long getLong() {
        	return value;
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getUnsignedIntItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getUnsignedIntItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getUnsignedIntItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getUnsignedIntItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getUnsignedIntItems();
		}
		@Override
        public String toString() {
        	return Integer.toUnsignedString(value);
        }
    }
    static class UnsignedLongSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedLongValImpl> implements XsValue.UnsignedLongSeqVal {
    	UnsignedLongSeqValImpl(long[] values) {
			super(Arrays.stream(values)
   					    .mapToObj(val -> new UnsignedLongValImpl(val))
   					    .toArray(size -> new UnsignedLongValImpl[size]));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
			return getItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getItems();
		}
    }
    static class UnsignedLongValImpl extends AnyAtomicTypeValImpl implements XsValue.UnsignedLongVal {
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
		public XsValue.UnsignedLongVal[] getUnsignedLongItems() {
			return new XsValue.UnsignedLongVal[]{this};
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getUnsignedLongItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(value);
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getUnsignedLongItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(value);
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getUnsignedLongItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getUnsignedLongItems();
		}
		@Override
        public String toString() {
        	return Long.toUnsignedString(value);
        }
    }
    static class UnsignedShortSeqValImpl extends AnyAtomicTypeSeqValImpl<UnsignedShortValImpl> implements XsValue.UnsignedShortSeqVal {
    	UnsignedShortSeqValImpl(short[] values) {
			super(toArray(values));
		}
		@Override
		public XsValue.NumericVal[] getNumericItems() {
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
		public UnsignedIntVal[] getUnsignedIntItems() {
			return getItems();
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getItems();
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getItems();
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getItems();
		}
		@Override
		public UnsignedShortVal[] getUnsignedShortItems() {
			return getItems();
		}
    }
    static class UnsignedShortValImpl extends AnyAtomicTypeValImpl implements XsValue.UnsignedShortVal {
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
		public XsValue.UnsignedShortVal[] getUnsignedShortItems() {
			return new XsValue.UnsignedShortVal[]{this};
		}
		@Override
		public int getInt() {
        	return value;
		}
		@Override
		public UnsignedIntVal[] getUnsignedIntItems() {
			return getUnsignedShortItems();
		}
		@Override
		public long getLong() {
        	return value;
		}
		@Override
		public UnsignedLongVal[] getUnsignedLongItems() {
			return getUnsignedShortItems();
		}
		@Override
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems() {
			return getUnsignedShortItems();
		}
		@Override
		public BigInteger getBigInteger() {
			return BigInteger.valueOf(Short.toUnsignedInt(value));
		}
		@Override
		public IntegerVal[] getIntegerItems() {
			return getUnsignedShortItems();
		}
		@Override
		public BigDecimal getBigDecimal() {
			return BigDecimal.valueOf(Short.toUnsignedInt(value));
		}
		@Override
		public DecimalVal[] getDecimalItems() {
			return getUnsignedShortItems();
		}
		@Override
		public NumericVal[] getNumericItems() {
			return getUnsignedShortItems();
		}
		@Override
        public String toString() {
        	return Integer.toUnsignedString(Short.toUnsignedInt(value));
        }
    }
    static class UntypedAtomicSeqValImpl extends AnyAtomicTypeSeqValImpl<UntypedAtomicValImpl> implements XsValue.UntypedAtomicSeqVal {
    	UntypedAtomicSeqValImpl(String[] values) {
			super(Arrays.stream(values)
		                .map(val -> new UntypedAtomicValImpl(val))
		                .toArray(size -> new UntypedAtomicValImpl[size]));
		}
		@Override
		public UntypedAtomicVal[] getUntypedAtomicItems() {
			return getItems();
		}
    }
    static class UntypedAtomicValImpl extends AnyAtomicTypeValImpl implements XsValue.UntypedAtomicVal {
    	private String value = null;
    	UntypedAtomicValImpl(String value) {
    		super("unsignedAtomic");
    		this.value = value;
    	}
		@Override
        public String getString() {
        	return value;
        }
		@Override
		public XsValue.UntypedAtomicVal[] getUntypedAtomicItems() {
			return new XsValue.UntypedAtomicVal[]{this};
		}
		@Override
        public String toString() {
        	return DatatypeConverter.printAnySimpleType(value);
        }
    }
    static class YearMonthDurationSeqValImpl extends AnyAtomicTypeSeqValImpl<YearMonthDurationValImpl> implements XsValue.YearMonthDurationSeqVal {
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
		public DurationVal[] getDurationItems() {
			return getItems();
		}
		@Override
		public YearMonthDurationVal[] getYearMonthDurationItems() {
			return getItems();
		}
    }
    static class YearMonthDurationValImpl extends AnyAtomicTypeValImpl implements XsValue.YearMonthDurationVal {
    	private Duration value = null;
    	YearMonthDurationValImpl(String value) {
    		this(Utilities.getDatatypeFactory().newDuration(value));
    	}
    	YearMonthDurationValImpl(Duration value) {
    		super("yearMonthDuration");
    		checkNull(value);
    		checkType("yearMonthDuration", value.getXMLSchemaType());
    		this.value = value;
    	}
		@Override
        public Duration getDuration() {
        	return value;
        }
		@Override
		public XsValue.YearMonthDurationVal[] getYearMonthDurationItems() {
			return new XsValue.YearMonthDurationVal[]{this};
		}
		@Override
		public DurationVal[] getDurationItems() {
			return getYearMonthDurationItems();
		}
		@Override
        public String toString() {
        	return value.toString();
        }
    }

    // XML types
    static class QNameSeqValImpl extends AnyAtomicTypeSeqValImpl<QNameValImpl> implements XsValue.QNameSeqVal {
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
		public QNameVal[] getQNameItems() {
			return getItems();
		}
    }
    static class QNameValImpl extends AnyAtomicTypeValImpl implements XsValue.QNameVal {
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
		public XsValue.QNameVal[] getQNameItems() {
			return new XsValue.QNameVal[]{this};
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
}
