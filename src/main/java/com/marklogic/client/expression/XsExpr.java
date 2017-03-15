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
package com.marklogic.client.expression;



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

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the xs server library for a row
 * pipeline and constructs client values with xs.* server types.
 */
public interface XsExpr extends XsValue {
    public XsAnyURIExpr anyURI(XsAnyAtomicTypeExpr arg1);
    public XsBase64BinaryExpr base64Binary(XsAnyAtomicTypeExpr arg1);
    public XsBooleanExpr booleanExpr(XsAnyAtomicTypeExpr arg1);
    public XsByteExpr byteExpr(XsAnyAtomicTypeExpr arg1);
    public XsDateExpr date(XsAnyAtomicTypeExpr arg1);
    public XsDateTimeExpr dateTime(XsAnyAtomicTypeExpr arg1);
    public XsDayTimeDurationExpr dayTimeDuration(XsAnyAtomicTypeExpr arg1);
    public XsDecimalExpr decimal(XsAnyAtomicTypeExpr arg1);
    public XsDoubleExpr doubleExpr(XsAnyAtomicTypeExpr arg1);
    public XsFloatExpr floatExpr(XsAnyAtomicTypeExpr arg1);
    public XsGDayExpr gDay(XsAnyAtomicTypeExpr arg1);
    public XsGMonthExpr gMonth(XsAnyAtomicTypeExpr arg1);
    public XsGMonthDayExpr gMonthDay(XsAnyAtomicTypeExpr arg1);
    public XsGYearExpr gYear(XsAnyAtomicTypeExpr arg1);
    public XsGYearMonthExpr gYearMonth(XsAnyAtomicTypeExpr arg1);
    public XsHexBinaryExpr hexBinary(XsAnyAtomicTypeExpr arg1);
    public XsIntExpr intExpr(XsAnyAtomicTypeExpr arg1);
    public XsIntegerExpr integer(XsAnyAtomicTypeExpr arg1);
    public XsLanguageExpr language(XsAnyAtomicTypeExpr arg1);
    public XsLongExpr longExpr(XsAnyAtomicTypeExpr arg1);
    public XsNameExpr Name(XsAnyAtomicTypeExpr arg1);
    public XsNCNameExpr NCName(XsAnyAtomicTypeExpr arg1);
    public XsNegativeIntegerExpr negativeInteger(XsAnyAtomicTypeExpr arg1);
    public XsNMTOKENExpr NMTOKEN(XsAnyAtomicTypeExpr arg1);
    public XsNonNegativeIntegerExpr nonNegativeInteger(XsAnyAtomicTypeExpr arg1);
    public XsNonPositiveIntegerExpr nonPositiveInteger(XsAnyAtomicTypeExpr arg1);
    public XsNormalizedStringExpr normalizedString(XsAnyAtomicTypeExpr arg1);
    public XsNumericExpr numeric(XsAnyAtomicTypeExpr arg1);
    public XsPositiveIntegerExpr positiveInteger(XsAnyAtomicTypeExpr arg1);
    public XsQNameExpr QName(XsAnyAtomicTypeExpr arg1);
    public XsShortExpr shortExpr(XsAnyAtomicTypeExpr arg1);
    public XsStringExpr string(XsAnyAtomicTypeExpr arg1);
    public XsTimeExpr time(XsAnyAtomicTypeExpr arg1);
    public XsTokenExpr token(XsAnyAtomicTypeExpr arg1);
    public XsUnsignedByteExpr unsignedByte(XsAnyAtomicTypeExpr arg1);
    public XsUnsignedIntExpr unsignedInt(XsAnyAtomicTypeExpr arg1);
    public XsUnsignedLongExpr unsignedLong(XsAnyAtomicTypeExpr arg1);
    public XsUnsignedShortExpr unsignedShort(XsAnyAtomicTypeExpr arg1);
    public XsUntypedAtomicExpr untypedAtomic(XsAnyAtomicTypeExpr arg1);
    public XsYearMonthDurationExpr yearMonthDuration(XsAnyAtomicTypeExpr arg1);
    public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items);
 
    public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items);
 
    public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items);
 
    public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items);
 
    public XsByteSeqExpr byteExprSeq(XsByteExpr... items);
 
    public XsDateSeqExpr dateSeq(XsDateExpr... items);
 
    public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items);
 
    public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items);
 
    public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items);
 
    public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items);
 
    public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items);
 
    public XsGDaySeqExpr GDaySeq(XsGDayExpr... items);
 
    public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items);
 
    public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items);
 
    public XsGYearSeqExpr GYearSeq(XsGYearExpr... items);
 
    public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items);
 
    public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items);
 
    public XsIntSeqExpr intExprSeq(XsIntExpr... items);
 
    public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items);
 
    public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items);
 
    public XsLongSeqExpr longExprSeq(XsLongExpr... items);
 
    public XsNameSeqExpr NameSeq(XsNameExpr... items);
 
    public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items);
 
    public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items);
 
    public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items);
 
    public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items);
 
    public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items);
 
    public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items);
 
    public XsNumericSeqExpr numericSeq(XsNumericExpr... items);
 
    public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items);
 
    public XsQNameSeqExpr QNameSeq(XsQNameExpr... items);
 
    public XsShortSeqExpr shortExprSeq(XsShortExpr... items);
 
    public XsStringSeqExpr stringSeq(XsStringExpr... items);
 
    public XsTimeSeqExpr timeSeq(XsTimeExpr... items);
 
    public XsTokenSeqExpr tokenSeq(XsTokenExpr... items);
 
    public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items);
 
    public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items);
 
    public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items);
 
    public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items);
 
    public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items);
 
    public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items);

}
