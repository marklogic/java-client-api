/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

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

/**
 * XsValue takes Java values and constructs atomic values and
 * sequences of atomic values with an xs schema data type
 *
 * The typed values can then be passed to expression functions
 * for execution on the server.
 */
public interface XsValue {
    // mappings between Java and XQuery per JAXB / JSR 222 and XQJ / JSR 225
    /**
     * Takes a uri as a string and constructs an xs:anyURI value
     * @param value	the uri as a string
     * @return	a value with an xs:anyURI data type
     */
    XsAnyURIVal               anyURI(String value);
    /**
     * Takes any number of uris as a string and constructs an xs:anyURI sequence
     * @param values	the uris as strings
     * @return	a value sequence with an xs:anyURI data type
     */
    XsAnyURISeqVal            anyURISeq(String... values);
    /**
     * Takes any number of xs:anyURI values and constructs an xs:anyURI sequence
     * @param values	values with an xs:anyURI data type
     * @return	a value sequence with an xs:anyURI data type
     */
    XsAnyURISeqVal            anyURISeq(XsAnyURIVal... values);
    /**
     * Takes a binary value as a byte array and constructs an xs:base64Binary value
     * @param value	the binary as a byte array
     * @return	a value with an xs:base64Binary data type
     */
    XsBase64BinaryVal         base64Binary(byte[] value);
    /**
     * Takes any number of binary values as a byte array and constructs an xs:base64Binary sequence
     * @param values	the binary values as byte arrays
     * @return	a value sequence with an xs:base64Binary data type
     */
    XsBase64BinarySeqVal      base64BinarySeq(byte[]... values);
    /**
     * Takes any number of xs:base64Binary values and constructs an xs:base64Binary sequence
     * @param values	values with an xs:base64Binary data type
     * @return	a value sequence with an xs:base64Binary data type
     */
    XsBase64BinarySeqVal      base64BinarySeq(XsBase64BinaryVal... values);
    // appending Val to avoid Java reserved word
    /**
     * Takes a boolean primitive and constructs an xs:boolean value
     * @param value	the boolean primitive
     * @return	a value with an xs:boolean data type
     */
    XsBooleanVal              booleanVal(boolean value);
    /**
     * Takes any number of boolean primitives and constructs an xs:boolean sequence
     * @param values	the boolean primitives
     * @return	a value sequence with an xs:boolean data type
     */
    XsBooleanSeqVal           booleanSeq(boolean... values);
    /**
     * Takes any number of xs:boolean values and constructs an xs:boolean sequence
     * @param values	values with an xs:boolean data type
     * @return	a value sequence with an xs:boolean data type
     */
    XsBooleanSeqVal           booleanSeq(XsBooleanVal... values);
    /**
     * Takes a byte primitive and constructs an xs:byte value
     * @param value	the byte primitive
     * @return	a value with an xs:byte data type
     */
    XsByteVal                 byteVal(byte value);
    /**
     * Takes any number of byte primitives and constructs an xs:byte sequence
     * @param values	the byte primitives
     * @return	a value sequence with an xs:byte data type
     */
    XsByteSeqVal              byteSeq(byte... values);
    /**
     * Takes any number of xs:byte values and constructs an xs:byte sequence
     * @param values	values with an xs:byte data type
     * @return	a value sequence with an xs:byte data type
     */
    XsByteSeqVal              byteSeq(XsByteVal... values);
    /**
     * Takes a date in a string format based on ISO 8601 and constructs an xs:date value
     * @param value	the date as a string
     * @return	a value with an xs:date data type
     */
    XsDateVal                 date(String value);
    /**
     * Takes a date as a Calendar value and constructs an xs:date value
     * @param value	the date as a Calendar object
     * @return	a value with an xs:date data type
     */
    XsDateVal                 date(Calendar value);
    /**
     * Takes a date as an XMLGregorianCalendar value and constructs an xs:date value
     * @param value	the date as an XMLGregorianCalendar object
     * @return	a value with an xs:date data type
     */
    XsDateVal                 date(XMLGregorianCalendar value);
    /**
     * Takes any number of dates in a string format based on ISO 8601 and constructs an xs:date sequence
     * @param values	the dates as strings
     * @return	a value sequence with an xs:date data type
     */
    XsDateSeqVal              dateSeq(String... values);
    /**
     * Takes any number of dates as Calendar values and constructs an xs:date sequence
     * @param values	the dates as Calendar objects
     * @return	a value sequence with an xs:date data type
     */
    XsDateSeqVal              dateSeq(Calendar... values);
    /**
     * Takes any number of dates as XMLGregorianCalendar values and constructs an xs:date sequence
     * @param values	the dates as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:date data type
     */
    XsDateSeqVal              dateSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:date values and constructs an xs:date sequence
     * @param values	values with an xs:date data type
     * @return	a value sequence with an xs:date data type
     */
    XsDateSeqVal              dateSeq(XsDateVal... values);
    /**
     * Takes a timestamp in a string format based on ISO 8601 and constructs an xs:dateTime value
     * @param value	the timestamp as a string
     * @return	a value with an xs:dateTime data type
     */
    XsDateTimeVal             dateTime(String value);
    /**
     * Takes a timestamp as a Date value and constructs an xs:dateTime value
     * @param value	the timestamp as a Date object
     * @return	a value with an xs:dateTime data type
     */
    XsDateTimeVal             dateTime(Date value);
    /**
     * Takes a timestamp as a Calendar value and constructs an xs:dateTime value
     * @param value	the timestamp as a Calendar object
     * @return	a value with an xs:dateTime data type
     */
    XsDateTimeVal             dateTime(Calendar value);
    /**
     * Takes a timestamp as an XMLGregorianCalendar value and constructs an xs:dateTime value
     * @param value	the timestamp as an XMLGregorianCalendar object
     * @return	a value with an xs:dateTime data type
     */
    XsDateTimeVal             dateTime(XMLGregorianCalendar value);
    /**
     * Takes any number of timestamps in a string format based on ISO 8601 and constructs an xs:dateTime sequence
     * @param values	the timestamps as strings
     * @return	a value sequence with an xs:dateTime data type
     */
    XsDateTimeSeqVal          dateTimeSeq(String... values);
    /**
     * Takes any number of timestamps as Date values and constructs an xs:dateTime sequence
     * @param values	the timestamps as Date objects
     * @return	a value sequence with an xs:dateTime data type
     */
    XsDateTimeSeqVal          dateTimeSeq(Date ... values);
    /**
     * Takes any number of timestamps as Calendar values and constructs an xs:dateTime sequence
     * @param values	the timestamps as Calendar objects
     * @return	a value sequence with an xs:dateTime data type
     */
    XsDateTimeSeqVal          dateTimeSeq(Calendar... values);
    /**
     * Takes any number of timestamps as XMLGregorianCalendar values and constructs an xs:dateTime sequence
     * @param values	the timestamps as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:dateTime data type
     */
    XsDateTimeSeqVal          dateTimeSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:dateTime values and constructs an xs:dateTime sequence
     * @param values	values with an xs:dateTime data type
     * @return	a value sequence with an xs:dateTime data type
     */
    XsDateTimeSeqVal          dateTimeSeq(XsDateTimeVal... values);
    /**
     * Takes a duration of increments of a day or less in a string format based on ISO 8601 and
     * constructs an xs:dayTimeDuration value
     * @param value	the duration as a string
     * @return	a value with an xs:dayTimeDuration data type
     */
    XsDayTimeDurationVal      dayTimeDuration(String value);
    /**
     * Takes a duration in increments of a day or less as a Duration value and
     * constructs an xs:dayTimeDuration value
     * @param value	the duration as a Duration object
     * @return	a value with an xs:dayTimeDuration data type
     */
    XsDayTimeDurationVal      dayTimeDuration(Duration value);
    /**
     * Takes any number of duration in increments of a day or less as a string and
     * constructs an xs:dayTimeDuration sequence
     * @param values	the durations as strings
     * @return	a value sequence with an xs:dayTimeDuration data type
     */
    XsDayTimeDurationSeqVal   dayTimeDurationSeq(String... values);
    /**
     * Takes any number of duration in increments of a day or less as a Duration and
     * constructs an xs:dayTimeDuration sequence
     * @param values	the durations as Duration objects
     * @return	a value sequence with an xs:dayTimeDuration data type
     */
    XsDayTimeDurationSeqVal   dayTimeDurationSeq(Duration... values);
    /**
     * Takes any number of xs:dayTimeDuration values and constructs an xs:dayTimeDuration sequence
     * @param values	values with an xs:dayTimeDuration data type
     * @return	a value sequence with an xs:dayTimeDuration data type
     */
    XsDayTimeDurationSeqVal   dayTimeDurationSeq(XsDayTimeDurationVal... values);
    /**
     * Takes a real number as a decimal string and constructs an xs:decimal value
     * @param value	the number as a string
     * @return	a value with an xs:decimal data type
     */
    XsDecimalVal              decimal(String value);
    /**
     * Takes a real number as a long primitive and constructs an xs:decimal value
     * @param value	the number as a long primitive
     * @return	a value with an xs:decimal data type
     */
    XsDecimalVal              decimal(long value);
    /**
     * Takes a real number as a double primitive and constructs an xs:decimal value
     * @param value	the number as a double primitive
     * @return	a value with an xs:decimal data type
     */
    XsDecimalVal              decimal(double value);
    /**
     * Takes a real number as a BigDecimal value and constructs an xs:decimal value
     * @param value	the number as a BigDecimal object
     * @return	a value with an xs:decimal data type
     */
    XsDecimalVal              decimal(BigDecimal value);
    /**
     * Takes any number of real numbers as a string and constructs an xs:decimal sequence
     * @param values	the real numbers as strings
     * @return	a value sequence with an xs:decimal data type
     */
    XsDecimalSeqVal           decimalSeq(String... values);
    /**
     * Takes any number of real numbers as a long primitive and constructs an xs:decimal sequence
     * @param values	the real numbers as long primitives
     * @return	a value sequence with an xs:decimal data type
     */
    XsDecimalSeqVal           decimalSeq(long... values);
    /**
     * Takes any number of real numbers as a double primitive and constructs an xs:decimal sequence
     * @param values	the real numbers as double primitives
     * @return	a value sequence with an xs:decimal data type
     */
    XsDecimalSeqVal           decimalSeq(double... values);
    /**
     * Takes any number of real numbers as a BigDecimal value and constructs an xs:decimal sequence
     * @param values	the real numbers as BigDecimal objects
     * @return	a value sequence with an xs:decimal data type
     */
    XsDecimalSeqVal           decimalSeq(BigDecimal... values);
    /**
     * Takes any number of xs:decimal values and constructs an xs:decimal sequence
     * @param values	values with an xs:decimal data type
     * @return	a value sequence with an xs:decimal data type
     */
    XsDecimalSeqVal           decimalSeq(XsDecimalVal... values);
    /**
     * Takes a double primitive and constructs an xs:double value
     * @param value	the double primitive
     * @return	a value with an xs:double data type
     */
    XsDoubleVal               doubleVal(double value);
    /**
     * Takes any number of double primitives and constructs an xs:double sequence
     * @param values	the double primitives
     * @return	a value sequence with an xs:double data type
     */
    XsDoubleSeqVal            doubleSeq(double... values);
    /**
     * Takes any number of xs:double values and constructs an xs:double sequence
     * @param values	values with an xs:double data type
     * @return	a value sequence with an xs:double data type
     */
    XsDoubleSeqVal            doubleSeq(XsDoubleVal... values);
    /**
     * Takes a float primitive and constructs an xs:float value
     * @param value	the float primitive
     * @return	a value with an xs:float data type
     */
    XsFloatVal                floatVal(float value);
    /**
     * Takes any number of float primitives and constructs an xs:float sequence
     * @param values	the float primitives
     * @return	a value sequence with an xs:float data type
     */
    XsFloatSeqVal             floatSeq(float... values);
    /**
     * Takes any number of xs:float values and constructs an xs:float sequence
     * @param values	values with an xs:float data type
     * @return	a value sequence with an xs:float data type
     */
    XsFloatSeqVal             floatSeq(XsFloatVal... values);
    /**
     * Takes a day as a string and constructs an xs:gDay value
     * @param value	the day of the month as a string
     * @return	a value with an xs:gDay data type
     */
    XsGDayVal                 gDay(String value);
    /**
     * Takes a day as a XMLGregorianCalendar value and constructs an xs:gDay value
     * @param value	the day of the month as a XMLGregorianCalendar object
     * @return	a value with an xs:gDay data type
     */
    XsGDayVal                 gDay(XMLGregorianCalendar value);
    /**
     * Takes any number of days as a string and constructs an xs:gDay sequence
     * @param values	the days as strings
     * @return	a value sequence with an xs:gDay data type
     */
    XsGDaySeqVal              gDaySeq(String... values);
    /**
     * Takes any number of days as a XMLGregorianCalendar value and constructs an xs:gDay sequence
     * @param values	the days as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:gDay data type
     */
    XsGDaySeqVal              gDaySeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:gDay values and constructs an xs:gDay sequence
     * @param values	values with an xs:gDay data type
     * @return	a value sequence with an xs:gDay data type
     */
    XsGDaySeqVal              gDaySeq(XsGDayVal... values);
    /**
     * Takes a month as a string and constructs an xs:gMonth value
     * @param value	the month of the year as a string
     * @return	a value with an xs:gMonth data type
     */
    XsGMonthVal               gMonth(String value);
    /**
     * Takes a month as a XMLGregorianCalendar value and constructs an xs:gMonth value
     * @param value	the day of the month as a XMLGregorianCalendar object
     * @return	a value with an xs:gMonth data type
     */
    XsGMonthVal               gMonth(XMLGregorianCalendar value);
    /**
     * Takes any number of months as a string and constructs an xs:gMonth sequence
     * @param values	the months as strings
     * @return	a value sequence with an xs:gMonth data type
     */
    XsGMonthSeqVal            gMonthSeq(String... values);
    /**
     * Takes any number of months as a XMLGregorianCalendar value and constructs an xs:gMonth sequence
     * @param values	the months as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:gMonth data type
     */
    XsGMonthSeqVal            gMonthSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:gMonth values and constructs an xs:gMonth sequence
     * @param values	values with an xs:gMonth data type
     * @return	a value sequence with an xs:gMonth data type
     */
    XsGMonthSeqVal            gMonthSeq(XsGMonthVal... values);
    /**
     * Takes a day and month as a string and constructs an xs:gMonthDay value
     * @param value	the day and month as a string
     * @return	a value with an xs:gMonthDay data type
     */
    XsGMonthDayVal            gMonthDay(String value);
    /**
     * Takes a day and month as a XMLGregorianCalendar value and constructs an xs:gMonthDay value
     * @param value	the day and month as a XMLGregorianCalendar object
     * @return	a value with an xs:gMonthDay data type
     */
    XsGMonthDayVal            gMonthDay(XMLGregorianCalendar value);
    /**
     * Takes any number of days and months as a string and constructs an xs:gMonthDay sequence
     * @param values	the days and months as strings
     * @return	a value sequence with an xs:gMonthDay data type
     */
    XsGMonthDaySeqVal         gMonthDaySeq(String... values);
    /**
     * Takes any number of days and months as a XMLGregorianCalendar value and constructs an xs:gMonthDay sequence
     * @param values	the days and months as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:gMonthDay data type
     */
    XsGMonthDaySeqVal         gMonthDaySeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:gMonthDay values and constructs an xs:gMonthDay sequence
     * @param values	values with an xs:gMonthDay data type
     * @return	a value sequence with an xs:gMonthDay data type
     */
    XsGMonthDaySeqVal         gMonthDaySeq(XsGMonthDayVal... values);
    /**
     * Takes a year as a string and constructs an xs:gYear value
     * @param value	the year as a string
     * @return	a value with an xs:gYear data type
     */
    XsGYearVal                gYear(String value);
    /**
     * Takes a year as a XMLGregorianCalendar value and constructs an xs:gYear value
     * @param value	the year as a XMLGregorianCalendar object
     * @return	a value with an xs:gYear data type
     */
    XsGYearVal                gYear(XMLGregorianCalendar value);
    /**
     * Takes any number of years as a string and constructs an xs:gYear sequence
     * @param values	the years as strings
     * @return	a value sequence with an xs:gYear data type
     */
    XsGYearSeqVal             gYearSeq(String... values);
    /**
     * Takes any number of years as a XMLGregorianCalendar value and constructs an xs:gYear sequence
     * @param values	the years as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:gYear data type
     */
    XsGYearSeqVal             gYearSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:gYear values and constructs an xs:gYear sequence
     * @param values	values with an xs:gYear data type
     * @return	a value sequence with an xs:gYear data type
     */
    XsGYearSeqVal             gYearSeq(XsGYearVal... values);
    /**
     * Takes a month and year as a string and constructs an xs:gYearMonth value
     * @param value	the month and year as a string
     * @return	a value with an xs:gYearMonth data type
     */
    XsGYearMonthVal           gYearMonth(String value);
    /**
     * Takes a month and year as a XMLGregorianCalendar value and constructs an xs:gYearMonth value
     * @param value	the month and year as a XMLGregorianCalendar object
     * @return	a value with an xs:gYearMonth data type
     */
    XsGYearMonthVal           gYearMonth(XMLGregorianCalendar value);
    /**
     * Takes any number of months and years as a string and constructs an xs:gYearMonth sequence
     * @param values	the months and years as strings
     * @return	a value sequence with an xs:gYearMonth data type
     */
    XsGYearMonthSeqVal        gYearMonthSeq(String... values);
    /**
     * Takes any number of months and years as a XMLGregorianCalendar value and constructs an xs:gYearMonth sequence
     * @param values	the months and years as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:gYearMonth data type
     */
    XsGYearMonthSeqVal        gYearMonthSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:gYearMonth values and constructs an xs:gYearMonth sequence
     * @param values	values with an xs:gYearMonth data type
     * @return	a value sequence with an xs:gYearMonth data type
     */
    XsGYearMonthSeqVal        gYearMonthSeq(XsGYearMonthVal... values);
    /**
     * Takes a binary value as a byte array and constructs an xs:hexBinary value
     * @param value	the binary as a byte array
     * @return	a value with an xs:hexBinary data type
     */
    XsHexBinaryVal            hexBinary(byte[] value);
    /**
     * Takes any number of binary values as a byte array and constructs an xs:hexBinary sequence
     * @param values	the binary values as byte arrays
     * @return	a value sequence with an xs:hexBinary data type
     */
    XsHexBinarySeqVal         hexBinarySeq(byte[]... values);
    /**
     * Takes any number of xs:hexBinary values and constructs an xs:hexBinary sequence
     * @param values	values with an xs:hexBinary data type
     * @return	a value sequence with an xs:hexBinary data type
     */
    XsHexBinarySeqVal         hexBinarySeq(XsHexBinaryVal... values);
    /**
     * Takes an int primitive and constructs an xs:int value
     * @param value	the int primitive
     * @return	a value with an xs:int data type
     */
    XsIntVal                  intVal(int value);
    /**
     * Takes any number of int primitives and constructs an xs:int sequence
     * @param values	the int primitives
     * @return	a value sequence with an xs:int data type
     */
    XsIntSeqVal               intSeq(int... values);
    /**
     * Takes any number of xs:int values and constructs an xs:int sequence
     * @param values	values with an xs:int data type
     * @return	a value sequence with an xs:int data type
     */
    XsIntSeqVal               intSeq(XsIntVal... values);
    /**
     * Takes an integer number as a string and constructs an xs:integer value
     * @param value	the number as a string
     * @return	a value with an xs:integer data type
     */
    XsIntegerVal              integer(String value);
    /**
     * Takes an integer number as a long primitive and constructs an xs:integer value
     * @param value	the number as a long primitive
     * @return	a value with an xs:integer data type
     */
    XsIntegerVal              integer(long value);
    /**
     * Takes an integer number as a BigInteger value and constructs an xs:integer value
     * @param value	the number as a BigInteger object
     * @return	a value with an xs:integer data type
     */
    XsIntegerVal              integer(BigInteger value);
    /**
     * Takes any number of integer numbers as a string and constructs an xs:integer sequence
     * @param values	the integer numbers as strings
     * @return	a value sequence with an xs:integer data type
     */
    XsIntegerSeqVal           integerSeq(String... values);
    /**
     * Takes any number of integer numbers as a long primitive and constructs an xs:integer sequence
     * @param values	the integer numbers as long primitives
     * @return	a value sequence with an xs:integer data type
     */
    XsIntegerSeqVal           integerSeq(long... values);
    /**
     * Takes any number of integer numbers as a BigInteger value and constructs an xs:integer sequence
     * @param values	the integer numbers as BigInteger objects
     * @return	a value sequence with an xs:integer data type
     */
    XsIntegerSeqVal           integerSeq(BigInteger... values);
    /**
     * Takes any number of xs:integer values and constructs an xs:integer sequence
     * @param values	values with an xs:integer data type
     * @return	a value sequence with an xs:integer data type
     */
    XsIntegerSeqVal           integerSeq(XsIntegerVal... values);
    /**
     * Takes a long primitive and constructs an xs:long value
     * @param value	the long primitive
     * @return	a value with an xs:long data type
     */
    XsLongVal                 longVal(long value);
    /**
     * Takes any number of long primitives and constructs an xs:long sequence
     * @param values	the long primitives
     * @return	a value sequence with an xs:long data type
     */
    XsLongSeqVal              longSeq(long... values);
    /**
     * Takes any number of xs:long values and constructs an xs:long sequence
     * @param values	values with an xs:long data type
     * @return	a value sequence with an xs:long data type
     */
    XsLongSeqVal              longSeq(XsLongVal... values);
    /**
     * Takes a short primitive and constructs an xs:short value
     * @param value	the short primitive
     * @return	a value with an xs:short data type
     */
    XsShortVal                shortVal(short value);
    /**
     * Takes any number of short primitives and constructs an xs:short sequence
     * @param values	the short primitives
     * @return	a value sequence with an xs:short data type
     */
    XsShortSeqVal             shortSeq(short... values);
    /**
     * Takes any number of xs:short values and constructs an xs:short sequence
     * @param values	values with an xs:short data type
     * @return	a value sequence with an xs:short data type
     */
    XsShortSeqVal             shortSeq(XsShortVal... values);
    /**
     * Takes a String literal and constructs an xs:string value
     * @param value	the String literal
     * @return	a value with an xs:string data type
     */
    XsStringVal               string(String value);
    /**
     * Takes any number of String literals and constructs an xs:string sequence
     * @param values	the String literals
     * @return	a value sequence with an xs:string data type
     */
    XsStringSeqVal            stringSeq(String... values);
    /**
     * Takes any number of xs:string values and constructs an xs:string sequence
     * @param values	the xs:string values
     * @return	a value sequence with an xs:string data type
     */
    XsStringSeqVal            stringSeq(XsStringVal... values);
    /**
     * Takes a time of day in a string format based on ISO 8601 and constructs an xs:time value
     * @param value	the time of day as a string
     * @return	a value with an xs:time data type
     */
    XsTimeVal                 time(String value);
    /**
     * Takes a time of day as a Calendar value and constructs an xs:time value
     * @param value	the time of day as a Calendar object
     * @return	a value with an xs:time data type
     */
    XsTimeVal                 time(Calendar value);
    /**
     * Takes a time of day as a XMLGregorianCalendar value and constructs an xs:time value
     * @param value	the time of day as a XMLGregorianCalendar object
     * @return	a value with an xs:time data type
     */
    XsTimeVal                 time(XMLGregorianCalendar value);
    /**
     * Takes any number of times of day in a string format based on ISO 8601 and constructs an xs:time sequence
     * @param values	the times of day as strings
     * @return	a value sequence with an xs:time data type
     */
    XsTimeSeqVal              timeSeq(String... values);
    /**
     * Takes any number of times of day as Calendar values and constructs an xs:time sequence
     * @param values	the times of day as Calendar objects
     * @return	a value sequence with an xs:time data type
     */
    XsTimeSeqVal              timeSeq(Calendar... values);
    /**
     * Takes any number of times of day as XMLGregorianCalendar values and constructs an xs:time sequence
     * @param values	the times of day as XMLGregorianCalendar objects
     * @return	a value sequence with an xs:time data type
     */
    XsTimeSeqVal              timeSeq(XMLGregorianCalendar... values);
    /**
     * Takes any number of xs:time values and constructs an xs:time sequence
     * @param values	values with an xs:time data type
     * @return	a value sequence with an xs:time data type
     */
    XsTimeSeqVal              timeSeq(XsTimeVal... values);
    /**
     * Takes an unsigned byte primitive and constructs an xs:unsignedByte value
     * @param value	the unsigned byte primitive
     * @return	a value with an xs:unsignedByte data type
     */
    XsUnsignedByteVal         unsignedByte(byte value);
    /**
     * Takes any number of unsigned byte primitives and constructs an xs:unsignedByte sequence
     * @param values	the unsigned byte primitives
     * @return	a value sequence with an xs:unsignedByte data type
     */
    XsUnsignedByteSeqVal      unsignedByteSeq(byte... values);
    /**
     * Takes any number of xs:unsignedByte values and constructs an xs:unsignedByte sequence
     * @param values	values with an xs:unsignedByte data type
     * @return	a value sequence with an xs:unsignedByte data type
     */
    XsUnsignedByteSeqVal      unsignedByteSeq(XsUnsignedByteVal... values);
    /**
     * Takes an unsigned int primitive and constructs an xs:unsignedInt value
     * @param value	the unsigned int primitive
     * @return	a value with an xs:unsignedInt data type
     */
    XsUnsignedIntVal          unsignedInt(int value);
    /**
     * Takes any number of unsigned int primitives and constructs an xs:unsignedInt sequence
     * @param values	the unsigned int primitives
     * @return	a value sequence with an xs:unsignedInt data type
     */
    XsUnsignedIntSeqVal       unsignedIntSeq(int... values);
    /**
     * Takes any number of xs:unsignedInt values and constructs an xs:unsignedInt sequence
     * @param values	values with an xs:unsignedInt data type
     * @return	a value sequence with an xs:unsignedInt data type
     */
    XsUnsignedIntSeqVal       unsignedIntSeq(XsUnsignedIntVal... values);
    /**
     * Takes an unsigned long primitive and constructs an xs:unsignedLong value
     * @param value	the unsigned long primitive
     * @return	a value with an xs:unsignedLong data type
     */
    XsUnsignedLongVal         unsignedLong(long value);
    /**
     * Takes any number of unsigned long primitives and constructs an xs:unsignedLong sequence
     * @param values	the unsigned long primitives
     * @return	a value sequence with an xs:unsignedLong data type
     */
    XsUnsignedLongSeqVal      unsignedLongSeq(long... values);
    /**
     * Takes any number of xs:unsignedLong values and constructs an xs:unsignedLong sequence
     * @param values	values with an xs:unsignedLong data type
     * @return	a value sequence with an xs:unsignedLong data type
     */
    XsUnsignedLongSeqVal      unsignedLongSeq(XsUnsignedLongVal... values);
    /**
     * Takes an unsigned short primitive and constructs an xs:unsignedShort value
     * @param value	the unsigned short primitive
     * @return	a value with an xs:unsignedShort data type
     */
    XsUnsignedShortVal        unsignedShort(short value);
    /**
     * Takes any number of unsigned short primitives and constructs an xs:unsignedShort sequence
     * @param values	the unsigned short primitives
     * @return	a value sequence with an xs:unsignedShort data type
     */
    XsUnsignedShortSeqVal     unsignedShortSeq(short... values);
    /**
     * Takes any number of xs:unsignedShort values and constructs an xs:unsignedShort sequence
     * @param values	values with an xs:unsignedShort data type
     * @return	a value sequence with an xs:unsignedShort data type
     */
    XsUnsignedShortSeqVal     unsignedShortSeq(XsUnsignedShortVal... values);
    /**
     * Takes a untyped atomic value as a string and constructs an xs:untypedAtomic value
     * @param value	the untyped atomic value as a string
     * @return	a value with an xs:untypedAtomic data type
     */
    XsUntypedAtomicVal        untypedAtomic(String value);
    /**
     * Takes any number of untyped atomic values as a string and constructs an xs:untypedAtomic sequence
     * @param values	the untyped atomic values as strings
     * @return	a value sequence with an xs:untypedAtomic data type
     */
    XsUntypedAtomicSeqVal     untypedAtomicSeq(String... values);
    /**
     * Takes any number of xs:untypedAtomic values and constructs an xs:untypedAtomic sequence
     * @param values	values with an xs:untypedAtomic data type
     * @return	a value sequence with an xs:untypedAtomic data type
     */
    XsUntypedAtomicSeqVal     untypedAtomicSeq(XsUntypedAtomicVal... values);
    /**
     * Takes a duration of increments of a month or more in a string format based on ISO 8601 and
     * constructs an xs:yearMonthDuration value
     * @param value	the duration as a string
     * @return	a value with an xs:yearMonthDuration data type
     */
    XsYearMonthDurationVal    yearMonthDuration(String value);
    /**
     * Takes a duration in increments of a month or more day or less as a Duration value and
     * constructs an xs:yearMonthDuration value
     * @param value	the duration as a Duration object
     * @return	a value with an xs:yearMonthDuration data type
     */
    XsYearMonthDurationVal    yearMonthDuration(Duration value);
    /**
     * Takes any number of duration in increments of a month or more as a string and
     * constructs an xs:yearMonthDuration sequence
     * @param values	the durations as strings
     * @return	a value sequence with an xs:yearMonthDuration data type
     */
    XsYearMonthDurationSeqVal yearMonthDurationSeq(String... values);
    /**
     * Takes any number of duration in increments of a month or more as a Duration and
     * constructs an xs:yearMonthDuration sequence
     * @param values	the durations as Duration objects
     * @return	a value sequence with an xs:yearMonthDuration data type
     */
    XsYearMonthDurationSeqVal yearMonthDurationSeq(Duration... values);
    /**
     * Takes any number of xs:yearMonthDuration values and constructs an xs:yearMonthDuration sequence
     * @param values	values with an xs:yearMonthDuration data type
     * @return	a value sequence with an xs:yearMonthDuration data type
     */
    XsYearMonthDurationSeqVal yearMonthDurationSeq(XsYearMonthDurationVal... values);
    // XML types
    /**
     * Constructs a qualified name as a xs:QName schema value
     * @param localName	the local name for a qualified name
     * @return	a value with an xs:QName data type
     */
    XsQNameVal                QName(String localName);
    /**
     * Constructs a qualified name as a xs:QName schema value
     * @param namespace	the namespace for the qualified name
     * @param localName	the local name for a qualified name
     * @return	a value with an xs:QName data type
     */
    XsQNameVal                QName(String namespace, String localName);
    /**
     * Constructs a qualified name as an xs:QName schema value
     * @param value	the qualified name as a QName object
     * @return	a value with an xs:QName data type
     */
    XsQNameVal                QName(QName value);
    /**
     * Takes any number of local names as a string and constructs an xs:QName sequence
     * @param localNames	the local names as strings
     * @return	a value sequence with an xs:QName data type
     */
    XsQNameSeqVal             QNameSeq(String... localNames);
    /**
     * Takes one namespace, one prefix, and any number of local names as a string and
     * constructs an xs:QName sequence
     * @param namespace	the namespace for the qualified names
     * @param localNames	the local names as strings
     * @return	a value sequence with an xs:QName data type
     */
    XsQNameSeqVal             QNameSeq(String namespace, String... localNames);
    /**
     * Takes any number of QName values and constructs an xs:QName sequence
     * @param values	the qualified names as QName objects
     * @return	a value sequence with an xs:QName data type
     */
    XsQNameSeqVal             QNameSeq(QName... values);
    /**
     * Takes any number of xs:QName values and constructs an xs:QName sequence
     * @param values	values with an xs:QName data type
     * @return	a value sequence with an xs:QName data type
     */
    XsQNameSeqVal             QNameSeq(XsQNameVal... values);
}
