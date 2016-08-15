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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.marklogic.client.type.XsUnsignedLongVal;
import com.marklogic.client.type.XsUnsignedShortSeqVal;
import com.marklogic.client.type.XsUnsignedShortVal;
import com.marklogic.client.type.XsUntypedAtomicSeqVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongSeqVal;
import com.marklogic.client.type.XsUnsignedByteVal;
import com.marklogic.client.type.XsUnsignedIntSeqVal;
import com.marklogic.client.type.XsTimeVal;
import com.marklogic.client.type.XsUnsignedByteSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsTimeSeqVal;
import com.marklogic.client.type.XsShortVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsDateVal;
import com.marklogic.client.type.XsUntypedAtomicVal;
import com.marklogic.client.type.XsAnyURISeqVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsBase64BinarySeqVal;
import com.marklogic.client.type.XsBase64BinaryVal;
import com.marklogic.client.type.XsBooleanSeqVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsByteVal;
import com.marklogic.client.type.XsDateSeqVal;
import com.marklogic.client.type.XsByteSeqVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDateTimeSeqVal;
import com.marklogic.client.type.XsDayTimeDurationVal;
import com.marklogic.client.type.XsDayTimeDurationSeqVal;
import com.marklogic.client.type.XsDecimalVal;
import com.marklogic.client.type.XsDecimalSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsFloatSeqVal;
import com.marklogic.client.type.XsGDayVal;
import com.marklogic.client.type.XsGDaySeqVal;
import com.marklogic.client.type.XsGMonthDayVal;
import com.marklogic.client.type.XsGMonthDaySeqVal;
import com.marklogic.client.type.XsGMonthVal;
import com.marklogic.client.type.XsGMonthSeqVal;
import com.marklogic.client.type.XsGYearMonthVal;
import com.marklogic.client.type.XsGYearMonthSeqVal;
import com.marklogic.client.type.XsGYearVal;
import com.marklogic.client.type.XsGYearSeqVal;
import com.marklogic.client.type.XsHexBinaryVal;
import com.marklogic.client.type.XsHexBinarySeqVal;
import com.marklogic.client.type.XsIntVal;
import com.marklogic.client.type.XsIntSeqVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsIntegerSeqVal;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsLongSeqVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsShortSeqVal;
import com.marklogic.client.type.XsQNameSeqVal;
import com.marklogic.client.type.XsYearMonthDurationSeqVal;
import com.marklogic.client.type.XsYearMonthDurationVal;

// converts the datatype of a Java literal on the client
public interface XsValue {
    // mappings between Java and XQuery per JAXB / JSR 222 and XQJ / JSR 225
	XsAnyURIVal               anyURI(String value);
    XsAnyURISeqVal            anyURIs(String... values);
    XsBase64BinaryVal         base64Binary(byte[] value);
    XsBase64BinarySeqVal      base64Binarys(byte[]... values);
    // appending Val to avoid Java reserved word
    XsBooleanVal              booleanVal(boolean value);
    XsBooleanSeqVal           booleanVals(boolean... values);
    XsByteVal                 byteVal(byte value);
    XsByteSeqVal              byteVals(byte... values);
    XsDateVal                 date(String value);
    XsDateVal                 date(Calendar value);
    XsDateVal                 date(XMLGregorianCalendar value);
    XsDateSeqVal              dates(String... values);
    XsDateSeqVal              dates(Calendar... values);
    XsDateSeqVal              dates(XMLGregorianCalendar... values);
    XsDateTimeVal             dateTime(String value);
    XsDateTimeVal             dateTime(Date value);
    XsDateTimeVal             dateTime(Calendar value);
    XsDateTimeVal             dateTime(XMLGregorianCalendar value);
    XsDateTimeSeqVal          dateTimes(String... values);
    XsDateTimeSeqVal          dateTimes(Date ... values);
    XsDateTimeSeqVal          dateTimes(Calendar... values);
    XsDateTimeSeqVal          dateTimes(XMLGregorianCalendar... values);
    XsDayTimeDurationVal      dayTimeDuration(String value);
    XsDayTimeDurationVal      dayTimeDuration(Duration value);
    XsDayTimeDurationSeqVal   dayTimeDurations(String... values);
    XsDayTimeDurationSeqVal   dayTimeDurations(Duration... values);
    XsDecimalVal              decimal(String value);
    XsDecimalVal              decimal(long value);
    XsDecimalVal              decimal(double value);
    XsDecimalVal              decimal(BigDecimal value);
    XsDecimalSeqVal           decimals(String... value);
    XsDecimalSeqVal           decimals(long... value);
    XsDecimalSeqVal           decimals(double... value);
    XsDecimalSeqVal           decimals(BigDecimal... values);
    XsDoubleVal               doubleVal(double value);
    XsDoubleSeqVal            doubleVals(double... values);
    XsFloatVal                floatVal(float value);
    XsFloatSeqVal             floatVals(float... values);
    XsGDayVal                 gDay(String value);
    XsGDayVal                 gDay(XMLGregorianCalendar value);
    XsGDaySeqVal              gDays(String... values);
    XsGDaySeqVal              gDays(XMLGregorianCalendar... values);
    XsGMonthVal               gMonth(String value);
    XsGMonthVal               gMonth(XMLGregorianCalendar value);
    XsGMonthSeqVal            gMonths(String... value);
    XsGMonthSeqVal            gMonths(XMLGregorianCalendar... values);
    XsGMonthDayVal            gMonthDay(String value);
    XsGMonthDayVal            gMonthDay(XMLGregorianCalendar value);
    XsGMonthDaySeqVal         gMonthDays(String... value);
    XsGMonthDaySeqVal         gMonthDays(XMLGregorianCalendar... values);
    XsGYearVal                gYear(String value);
    XsGYearVal                gYear(XMLGregorianCalendar value);
    XsGYearSeqVal             gYears(String... values);
    XsGYearSeqVal             gYears(XMLGregorianCalendar... values);
    XsGYearMonthVal           gYearMonth(String value);
    XsGYearMonthVal           gYearMonth(XMLGregorianCalendar value);
    XsGYearMonthSeqVal        gYearMonths(String... values);
    XsGYearMonthSeqVal        gYearMonths(XMLGregorianCalendar... values);
    XsHexBinaryVal            hexBinary(byte[] value);
    XsHexBinarySeqVal         hexBinarys(byte[]... values);
    XsIntVal                  intVal(int value);
    XsIntSeqVal               intVals(int... values);
    XsIntegerVal              integer(String value);
    XsIntegerVal              integer(long value);
    XsIntegerVal              integer(BigInteger value);
    XsIntegerSeqVal           integers(String... values);
    XsIntegerSeqVal           integers(long... values);
    XsIntegerSeqVal           integers(BigInteger... values);
    XsLongVal                 longVal(long value);
    XsLongSeqVal              longVals(long... values);
    XsShortVal                shortVal(short value);
    XsShortSeqVal             shortVals(short... values);
    XsStringVal               string(String value);
    XsStringSeqVal            strings(String... values);
    XsStringSeqVal            strings(XsStringVal... values);
    XsTimeVal                 time(String value);
    XsTimeVal                 time(Calendar value);
    XsTimeVal                 time(XMLGregorianCalendar value);
    XsTimeSeqVal              times(String... values);
    XsTimeSeqVal              times(Calendar... values);
    XsTimeSeqVal              times(XMLGregorianCalendar... values);
    XsUnsignedByteVal         unsignedByte(byte values);
    XsUnsignedByteSeqVal      unsignedBytes(byte... values);
    XsUnsignedIntVal          unsignedInt(int values);
    XsUnsignedIntSeqVal       unsignedInts(int... values);
    XsUnsignedLongVal         unsignedLong(long values);
    XsUnsignedLongSeqVal      unsignedLongs(long... values);
    XsUnsignedShortVal        unsignedShort(short values);
    XsUnsignedShortSeqVal     unsignedShorts(short... values);
    XsUntypedAtomicVal        untypedAtomic(String value);
    XsUntypedAtomicSeqVal     untypedAtomics(String... values);
    XsYearMonthDurationVal    yearMonthDuration(String value);
    XsYearMonthDurationVal    yearMonthDuration(Duration value);
    XsYearMonthDurationSeqVal yearMonthDurations(String... values);
    XsYearMonthDurationSeqVal yearMonthDurations(Duration... values);
    // XML types
    XsQNameVal                qname(String localName);
    XsQNameVal                qname(String namespace, String prefix, String localName);
    XsQNameVal                qname(QName value);
    XsQNameSeqVal             qnames(String... localNames);
    XsQNameSeqVal             qnames(String namespace, String prefix, String... localNames);
    XsQNameSeqVal             qnames(QName... values);
}
