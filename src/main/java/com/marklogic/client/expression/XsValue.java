package com.marklogic.client.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.marklogic.client.expression.BaseType.ItemSeqVal;
import com.marklogic.client.expression.BaseType.ItemVal;

// converts the datatype of a Java literal on the client
public interface XsValue {
    // mappings between Java and XQuery per JAXB / JSR 222 and XQJ / JSR 225
	public AnyURIVal               anyURI(String value);
    public AnyURISeqVal            anyURIs(String... values);
    public Base64BinaryVal         base64Binary(byte[] value);
    public Base64BinarySeqVal      base64Binarys(byte[]... values);
    // appending Val to avoid Java reserved word
    public BooleanVal              booleanVal(boolean value);
    public BooleanSeqVal           booleanVals(boolean... values);
    public ByteVal                 byteVal(byte value);
    public ByteSeqVal              byteVals(byte... values);
    public DateVal                 date(String value);
    public DateVal                 date(Calendar value);
    public DateVal                 date(XMLGregorianCalendar value);
    public DateSeqVal              dates(String... values);
    public DateSeqVal              dates(Calendar... values);
    public DateSeqVal              dates(XMLGregorianCalendar... values);
    public DateTimeVal             dateTime(String value);
    public DateTimeVal             dateTime(Date value);
    public DateTimeVal             dateTime(Calendar value);
    public DateTimeVal             dateTime(XMLGregorianCalendar value);
    public DateTimeSeqVal          dateTimes(String... values);
    public DateTimeSeqVal          dateTimes(Date ... values);
    public DateTimeSeqVal          dateTimes(Calendar... values);
    public DateTimeSeqVal          dateTimes(XMLGregorianCalendar... values);
    public DayTimeDurationVal      dayTimeDuration(String value);
    public DayTimeDurationVal      dayTimeDuration(Duration value);
    public DayTimeDurationSeqVal   dayTimeDurations(String... values);
    public DayTimeDurationSeqVal   dayTimeDurations(Duration... values);
    public DecimalVal              decimal(String value);
    public DecimalVal              decimal(long value);
    public DecimalVal              decimal(double value);
    public DecimalVal              decimal(BigDecimal value);
    public DecimalSeqVal           decimals(String... value);
    public DecimalSeqVal           decimals(long... value);
    public DecimalSeqVal           decimals(double... value);
    public DecimalSeqVal           decimals(BigDecimal... values);
    public DoubleVal               doubleVal(double value);
    public DoubleSeqVal            doubleVals(double... values);
    public FloatVal                floatVal(float value);
    public FloatSeqVal             floatVals(float... values);
    public GDayVal                 gDay(String value);
    public GDayVal                 gDay(XMLGregorianCalendar value);
    public GDaySeqVal              gDays(String... values);
    public GDaySeqVal              gDays(XMLGregorianCalendar... values);
    public GMonthVal               gMonth(String value);
    public GMonthVal               gMonth(XMLGregorianCalendar value);
    public GMonthSeqVal            gMonths(String... value);
    public GMonthSeqVal            gMonths(XMLGregorianCalendar... values);
    public GMonthDayVal            gMonthDay(String value);
    public GMonthDayVal            gMonthDay(XMLGregorianCalendar value);
    public GMonthDaySeqVal         gMonthDays(String... value);
    public GMonthDaySeqVal         gMonthDays(XMLGregorianCalendar... values);
    public GYearVal                gYear(String value);
    public GYearVal                gYear(XMLGregorianCalendar value);
    public GYearSeqVal             gYears(String... values);
    public GYearSeqVal             gYears(XMLGregorianCalendar... values);
    public GYearMonthVal           gYearMonth(String value);
    public GYearMonthVal           gYearMonth(XMLGregorianCalendar value);
    public GYearMonthSeqVal        gYearMonths(String... values);
    public GYearMonthSeqVal        gYearMonths(XMLGregorianCalendar... values);
    public HexBinaryVal            hexBinary(byte[] value);
    public HexBinarySeqVal         hexBinarys(byte[]... values);
    public IntVal                  intVal(int value);
    public IntSeqVal               intVals(int... values);
    public IntegerVal              integer(String value);
    public IntegerVal              integer(long value);
    public IntegerVal              integer(BigInteger value);
    public IntegerSeqVal           integers(String... values);
    public IntegerSeqVal           integers(long... values);
    public IntegerSeqVal           integers(BigInteger... values);
    public LongVal                 longVal(long value);
    public LongSeqVal              longVals(long... values);
    public ShortVal                shortVal(short value);
    public ShortSeqVal             shortVals(short... values);
    public StringVal               string(String value);
    public StringSeqVal            strings(String... values);
    public TimeVal                 time(String value);
    public TimeVal                 time(Calendar value);
    public TimeVal                 time(XMLGregorianCalendar value);
    public TimeSeqVal              times(String... values);
    public TimeSeqVal              times(Calendar... values);
    public TimeSeqVal              times(XMLGregorianCalendar... values);
    public UnsignedByteVal         unsignedByte(byte values);
    public UnsignedByteSeqVal      unsignedBytes(byte... values);
    public UnsignedIntVal          unsignedInt(int values);
    public UnsignedIntSeqVal       unsignedInts(int... values);
    public UnsignedLongVal         unsignedLong(long values);
    public UnsignedLongSeqVal      unsignedLongs(long... values);
    public UnsignedShortVal        unsignedShort(short values);
    public UnsignedShortSeqVal     unsignedShorts(short... values);
    public UntypedAtomicVal        untypedAtomic(String value);
    public UntypedAtomicSeqVal     untypedAtomics(String... values);
    public YearMonthDurationVal    yearMonthDuration(String value);
    public YearMonthDurationVal    yearMonthDuration(Duration value);
    public YearMonthDurationSeqVal yearMonthDurations(String... values);
    public YearMonthDurationSeqVal yearMonthDurations(Duration... values);
    // XML types
    public QNameVal                qname(String localName);
    public QNameVal                qname(String namespace, String prefix, String localName);
    public QNameVal                qname(QName value);
    public QNameSeqVal             qnames(String... localNames);
    public QNameSeqVal             qnames(String namespace, String prefix, String... localNames);
    public QNameSeqVal             qnames(QName... values);

	public interface AnySimpleTypeSeqVal extends ItemSeqVal, Xs.AnySimpleTypeSeqExpr {
		public AnySimpleTypeVal[] getAnySimpleTypeItems();
	}
	public interface AnySimpleTypeVal extends ItemVal, AnySimpleTypeSeqVal, Xs.AnySimpleTypeParam { }

	public interface AnyAtomicTypeSeqVal extends AnySimpleTypeSeqVal, Xs.AnyAtomicTypeSeqExpr {
		public AnyAtomicTypeVal[] getAnyAtomicTypeItems();
	}
	public interface AnyAtomicTypeVal extends AnySimpleTypeVal, AnyAtomicTypeSeqVal, Xs.AnyAtomicTypeParam { }

	public interface NumericSeqVal extends AnySimpleTypeSeqVal, Xs.NumericSeqExpr {
		public NumericVal[] getNumericItems();
	}
	public interface NumericVal extends AnySimpleTypeVal, NumericSeqVal, Xs.NumericParam { }

	public interface DurationSeqVal extends AnyAtomicTypeSeqVal, Xs.DurationSeqExpr {
		public DurationVal[] getDurationItems();
	}
	public interface DurationVal extends AnyAtomicTypeVal, DurationSeqVal, Xs.DurationParam { }

	public interface UntypedAtomicSeqVal extends AnyAtomicTypeSeqVal, Xs.UntypedAtomicSeqExpr {
		public UntypedAtomicVal[] getUntypedAtomicItems();
	}
	public interface UntypedAtomicVal extends AnyAtomicTypeVal, UntypedAtomicSeqVal, Xs.UntypedAtomicParam {
		public String getString();
	}

	public interface AnyURISeqVal extends AnyAtomicTypeSeqVal, Xs.AnyURISeqExpr {
		public AnyURIVal[] getAnyURIItems();
	}
	public interface AnyURIVal extends AnyAtomicTypeVal, AnyURISeqVal, Xs.AnyURIParam {
		public String getString();
	}
	public interface Base64BinarySeqVal extends AnyAtomicTypeSeqVal, Xs.Base64BinarySeqExpr {
		public Base64BinaryVal[] getBase64BinaryItems();
	}
	public interface Base64BinaryVal extends AnyAtomicTypeVal, Base64BinarySeqVal, Xs.Base64BinaryParam {
		public byte[] getBytes();
	}
	public interface BooleanSeqVal extends AnyAtomicTypeSeqVal, Xs.BooleanSeqExpr {
		public BooleanVal[] getBooleanItems();
	}
	public interface BooleanVal extends AnyAtomicTypeVal, BooleanSeqVal, Xs.BooleanParam {
		public boolean getBoolean();
	}
	public interface ByteSeqVal extends ShortSeqVal, Xs.ByteSeqExpr {
		public ByteVal[] getByteItems();
	}
	public interface ByteVal extends ShortVal, ByteSeqVal, Xs.ByteParam {
		public byte getByte();
	}
	public interface DateSeqVal extends AnyAtomicTypeSeqVal, Xs.DateSeqExpr {
		public DateVal[] getDateItems();
	}
	public interface DateVal extends AnyAtomicTypeVal, DateSeqVal, Xs.DateParam {
		// follows JAXB rather than XQJ, which uses XMLGregorianCalendar
		public Calendar getCalendar();
	}
	public interface DateTimeSeqVal extends AnyAtomicTypeSeqVal, Xs.DateTimeSeqExpr {
		public DateTimeVal[] getDateTimeItems();
	}
	public interface DateTimeVal extends AnyAtomicTypeVal, DateTimeSeqVal, Xs.DateTimeParam {
		// follows JAXB rather than XQJ, which uses XMLGregorianCalendar
		public Calendar getCalendar();
	}
	public interface DayTimeDurationSeqVal extends DurationSeqVal, Xs.DayTimeDurationSeqExpr {
		public DayTimeDurationVal[] getDayTimeDurationItems();
	}
	public interface DayTimeDurationVal extends DurationVal, DayTimeDurationSeqVal, Xs.DayTimeDurationParam {
		public Duration getDuration();
	}
	public interface DecimalSeqVal extends AnyAtomicTypeSeqVal, NumericSeqVal, Xs.DecimalSeqExpr {
		public DecimalVal[] getDecimalItems();
	}
	public interface DecimalVal extends AnyAtomicTypeVal, NumericVal, DecimalSeqVal, Xs.DecimalParam { 
		public BigDecimal getBigDecimal();
	}
	public interface DoubleSeqVal extends AnyAtomicTypeSeqVal, NumericSeqVal, Xs.DoubleSeqExpr {
		public DoubleVal[] getDoubleItems();
	}
	public interface DoubleVal extends AnyAtomicTypeVal, NumericVal, DoubleSeqVal, Xs.DoubleParam {
		public double getDouble();
	}
	public interface FloatSeqVal extends AnyAtomicTypeSeqVal, NumericSeqVal, Xs.FloatSeqExpr {
		public FloatVal[] getFloatItems();
	}
	public interface FloatVal extends AnyAtomicTypeVal, NumericVal, FloatSeqVal, Xs.FloatParam { 
		public float getFloat();
	}
	public interface GDaySeqVal extends AnyAtomicTypeSeqVal, Xs.GDaySeqExpr {
		public GDayVal[] getGDayItems();
	}
	public interface GDayVal extends AnyAtomicTypeVal, GDaySeqVal, Xs.GDayParam {
		public XMLGregorianCalendar getXMLGregorianCalendar();
	}
	public interface GMonthSeqVal extends AnyAtomicTypeSeqVal, Xs.GMonthSeqExpr {
		public GMonthVal[] getGMonthItems();
	}
	public interface GMonthVal extends AnyAtomicTypeVal, GMonthSeqVal, Xs.GMonthParam {
		public XMLGregorianCalendar getXMLGregorianCalendar();
	}
	public interface GMonthDaySeqVal extends AnyAtomicTypeSeqVal, Xs.GMonthDaySeqExpr {
		public GMonthDayVal[] getGMonthDayItems();
	}
	public interface GMonthDayVal extends AnyAtomicTypeVal, GMonthDaySeqVal, Xs.GMonthDayParam {
		public XMLGregorianCalendar getXMLGregorianCalendar();
	}
	public interface GYearSeqVal extends AnyAtomicTypeSeqVal, Xs.GYearSeqExpr {
		public GYearVal[] getGYearItems();
	}
	public interface GYearVal extends AnyAtomicTypeVal, GYearSeqVal, Xs.GYearParam {
		public XMLGregorianCalendar getXMLGregorianCalendar();
	}
	public interface GYearMonthSeqVal extends AnyAtomicTypeSeqVal, Xs.GYearSeqExpr {
		public GYearMonthVal[] getGYearMonthItems();
	}
	public interface GYearMonthVal extends AnyAtomicTypeVal, GYearMonthSeqVal, Xs.GYearParam {
		public XMLGregorianCalendar getXMLGregorianCalendar();
	}
	public interface HexBinarySeqVal extends AnyAtomicTypeSeqVal, Xs.HexBinarySeqExpr {
		public HexBinaryVal[] getHexBinaryItems();
	}
	public interface HexBinaryVal extends AnyAtomicTypeVal, HexBinarySeqVal, Xs.HexBinaryParam {
		public byte[] getBytes();
	}
	public interface IntSeqVal extends LongSeqVal, Xs.IntSeqExpr {
		public IntVal[] getIntItems();
	}
	public interface IntVal extends LongVal, IntSeqVal, Xs.IntParam {
		public int getInt();
	}
	public interface IntegerSeqVal extends DecimalSeqVal, Xs.IntegerSeqExpr {
		public IntegerVal[] getIntegerItems();
	}
	public interface IntegerVal extends DecimalVal, IntegerSeqVal, Xs.IntegerParam {
		public BigInteger getBigInteger();
	}
	public interface LongSeqVal extends IntegerSeqVal, Xs.LongSeqExpr {
		public LongVal[] getLongItems();
	}
	public interface LongVal extends IntegerVal, LongSeqVal, Xs.LongParam {
		public long getLong();
	}
	public interface NonNegativeIntegerSeqVal extends IntegerSeqVal, Xs.NonNegativeIntegerSeqExpr {
		public NonNegativeIntegerVal[] getNonNegativeIntegerItems();
	}
	public interface NonNegativeIntegerVal extends IntegerVal, NonNegativeIntegerSeqVal, Xs.NonNegativeIntegerParam { }
	public interface ShortSeqVal extends IntSeqVal, Xs.ShortSeqExpr {
		public ShortVal[] getShortItems();
	}
	public interface ShortVal extends IntVal, ShortSeqVal, Xs.ShortParam {
		public short getShort();
	}
	public interface StringSeqVal extends AnyAtomicTypeSeqVal, Xs.StringSeqExpr {
		public StringVal[] getStringItems();
	}
	public interface StringVal extends AnyAtomicTypeVal, StringSeqVal, Xs.StringParam {
		public String getString();
	}
	public interface TimeSeqVal extends AnyAtomicTypeSeqVal, Xs.TimeSeqExpr {
		public TimeVal[] getTimeItems();
	}
	public interface TimeVal extends AnyAtomicTypeVal, TimeSeqVal, Xs.TimeParam {
		// follows JAXB rather than XQJ, which uses XMLGregorianCalendar 
		public Calendar getCalendar();
	}
	public interface UnsignedByteSeqVal extends UnsignedShortSeqVal, Xs.UnsignedByteSeqExpr {
		public UnsignedByteVal[] getUnsignedByteItems();
	}
	// Java 8 introduced Integer and Long methods on unsigned ints and longs 
	public interface UnsignedByteVal extends UnsignedShortVal, UnsignedByteSeqVal, Xs.UnsignedByteParam {
		public byte getByte();
	}
	public interface UnsignedIntSeqVal extends UnsignedLongSeqVal, Xs.UnsignedIntSeqExpr {
		public UnsignedIntVal[] getUnsignedIntItems();
	}
	public interface UnsignedIntVal extends UnsignedLongVal, UnsignedIntSeqVal, Xs.UnsignedIntParam {
		public int getInt();
	}
	public interface UnsignedShortSeqVal extends UnsignedIntSeqVal, Xs.UnsignedShortSeqExpr {
		public UnsignedShortVal[] getUnsignedShortItems();
	}
	public interface UnsignedShortVal extends UnsignedIntVal, UnsignedShortSeqVal, Xs.UnsignedShortParam {
		public short getShort();
	}
	public interface UnsignedLongSeqVal extends NonNegativeIntegerSeqVal, Xs.UnsignedLongSeqExpr {
		public UnsignedLongVal[] getUnsignedLongItems();
	}
	public interface UnsignedLongVal extends NonNegativeIntegerVal, UnsignedLongSeqVal, Xs.UnsignedLongParam {
		public long getLong();
	}
	public interface YearMonthDurationSeqVal extends DurationSeqVal, Xs.YearMonthDurationSeqExpr {
		public YearMonthDurationVal[] getYearMonthDurationItems();
	}
	public interface YearMonthDurationVal extends DurationVal, YearMonthDurationSeqVal, Xs.YearMonthDurationParam {
		public Duration getDuration();
	}
	// XML types
	public interface QNameSeqVal extends AnyAtomicTypeSeqVal, Xs.QNameSeqExpr {
		public QNameVal[] getQNameItems();
	}
	public interface QNameVal extends AnyAtomicTypeVal, QNameSeqVal, Xs.QNameParam {
		public QName getQName();
	}
}
