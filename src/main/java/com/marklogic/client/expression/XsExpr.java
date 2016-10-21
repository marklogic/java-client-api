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

import com.marklogic.client.type.XsLongSeqExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsDoubleSeqExpr;
 import com.marklogic.client.type.XsGMonthDayExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.XsNegativeIntegerExpr;
 import com.marklogic.client.type.XsGDaySeqExpr;
 import com.marklogic.client.type.XsNormalizedStringExpr;
 import com.marklogic.client.type.XsUnsignedIntSeqExpr;
 import com.marklogic.client.type.XsQNameSeqExpr;
 import com.marklogic.client.type.XsIntSeqExpr;
 import com.marklogic.client.type.XsGMonthSeqExpr;
 import com.marklogic.client.type.XsGDayExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsPositiveIntegerExpr;
 import com.marklogic.client.type.XsHexBinaryExpr;
 import com.marklogic.client.type.XsDateSeqExpr;
 import com.marklogic.client.type.XsDecimalSeqExpr;
 import com.marklogic.client.type.XsNMTOKENExpr;
 import com.marklogic.client.type.XsShortSeqExpr;
 import com.marklogic.client.type.XsGYearExpr;
 import com.marklogic.client.type.XsUntypedAtomicSeqExpr;
 import com.marklogic.client.type.XsLongExpr;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.XsTokenSeqExpr;
 import com.marklogic.client.type.XsDateTimeSeqExpr;
 import com.marklogic.client.type.XsGYearMonthSeqExpr;
 import com.marklogic.client.type.XsUnsignedByteSeqExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsGMonthExpr;
 import com.marklogic.client.type.XsNameExpr;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsByteExpr;
 import com.marklogic.client.type.XsBase64BinarySeqExpr;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsUnsignedShortExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsLanguageExpr;
 import com.marklogic.client.type.XsNameSeqExpr;
 import com.marklogic.client.type.XsYearMonthDurationSeqExpr;
 import com.marklogic.client.type.XsGYearMonthExpr;
 import com.marklogic.client.type.XsTokenExpr;
 import com.marklogic.client.type.XsTimeSeqExpr;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.XsNonPositiveIntegerExpr;
 import com.marklogic.client.type.XsDurationSeqExpr;
 import com.marklogic.client.type.XsUnsignedShortSeqExpr;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.XsGMonthDaySeqExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
 import com.marklogic.client.type.XsLanguageSeqExpr;
 import com.marklogic.client.type.XsBase64BinaryExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsUntypedAtomicExpr;
 import com.marklogic.client.type.XsIntegerSeqExpr;
 import com.marklogic.client.type.XsNormalizedStringSeqExpr;
 import com.marklogic.client.type.XsNonPositiveIntegerSeqExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsAnySimpleTypeSeqExpr;
 import com.marklogic.client.type.XsBooleanSeqExpr;
 import com.marklogic.client.type.XsByteSeqExpr;
 import com.marklogic.client.type.XsShortExpr;
 import com.marklogic.client.type.XsNumericSeqExpr;
 import com.marklogic.client.type.XsAnyURISeqExpr;
 import com.marklogic.client.type.XsHexBinarySeqExpr;
 import com.marklogic.client.type.XsDurationExpr;
 import com.marklogic.client.type.XsAnySimpleTypeExpr;
 import com.marklogic.client.type.XsUnsignedByteExpr;
 import com.marklogic.client.type.XsNonNegativeIntegerSeqExpr;
 import com.marklogic.client.type.XsPositiveIntegerSeqExpr;
 import com.marklogic.client.type.XsNMTOKENSeqExpr;
 import com.marklogic.client.type.XsDayTimeDurationSeqExpr;
 import com.marklogic.client.type.XsNonNegativeIntegerExpr;
 import com.marklogic.client.type.XsUnsignedLongSeqExpr;
 import com.marklogic.client.type.XsNegativeIntegerSeqExpr;
 import com.marklogic.client.type.XsGYearSeqExpr;
 import com.marklogic.client.type.XsYearMonthDurationExpr;
 import com.marklogic.client.type.XsNCNameSeqExpr;
 import com.marklogic.client.type.XsNCNameExpr;
 import com.marklogic.client.type.XsFloatExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.XsFloatSeqExpr;


// IMPORTANT: Do not edit. This file is generated. 
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
    public XsDurationExpr duration(XsAnyAtomicTypeExpr arg1);
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
    public XsNMTOKENExpr NMTOKEN(XsAnyAtomicTypeExpr arg1);
    public XsNegativeIntegerExpr negativeInteger(XsAnyAtomicTypeExpr arg1);
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
    public XsYearMonthDurationExpr yearMonthDuration(XsAnyAtomicTypeExpr arg1);     public XsAnyAtomicTypeSeqExpr anyAtomicType(XsAnyAtomicTypeExpr... items);
     public XsAnySimpleTypeSeqExpr anySimpleType(XsAnySimpleTypeExpr... items);
     public XsAnyURISeqExpr anyURI(XsAnyURIExpr... items);
     public XsBase64BinarySeqExpr base64Binary(XsBase64BinaryExpr... items);
     public XsBooleanSeqExpr booleanExpr(XsBooleanExpr... items);
     public XsByteSeqExpr byteExpr(XsByteExpr... items);
     public XsDateSeqExpr date(XsDateExpr... items);
     public XsDateTimeSeqExpr dateTime(XsDateTimeExpr... items);
     public XsDayTimeDurationSeqExpr dayTimeDuration(XsDayTimeDurationExpr... items);
     public XsDecimalSeqExpr decimal(XsDecimalExpr... items);
     public XsDoubleSeqExpr doubleExpr(XsDoubleExpr... items);
     public XsDurationSeqExpr duration(XsDurationExpr... items);
     public XsFloatSeqExpr floatExpr(XsFloatExpr... items);
     public XsGDaySeqExpr gDay(XsGDayExpr... items);
     public XsGMonthSeqExpr gMonth(XsGMonthExpr... items);
     public XsGMonthDaySeqExpr gMonthDay(XsGMonthDayExpr... items);
     public XsGYearSeqExpr gYear(XsGYearExpr... items);
     public XsGYearMonthSeqExpr gYearMonth(XsGYearMonthExpr... items);
     public XsHexBinarySeqExpr hexBinary(XsHexBinaryExpr... items);
     public XsIntegerSeqExpr integer(XsIntegerExpr... items);
     public XsIntSeqExpr intExpr(XsIntExpr... items);
     public XsLanguageSeqExpr language(XsLanguageExpr... items);
     public XsLongSeqExpr longExpr(XsLongExpr... items);
     public XsNameSeqExpr name(XsNameExpr... items);
     public XsNCNameSeqExpr nCName(XsNCNameExpr... items);
     public XsNegativeIntegerSeqExpr negativeInteger(XsNegativeIntegerExpr... items);
     public XsNMTOKENSeqExpr nMTOKEN(XsNMTOKENExpr... items);
     public XsNonNegativeIntegerSeqExpr nonNegativeInteger(XsNonNegativeIntegerExpr... items);
     public XsNonPositiveIntegerSeqExpr nonPositiveInteger(XsNonPositiveIntegerExpr... items);
     public XsNormalizedStringSeqExpr normalizedString(XsNormalizedStringExpr... items);
     public XsNumericSeqExpr numeric(XsNumericExpr... items);
     public XsPositiveIntegerSeqExpr positiveInteger(XsPositiveIntegerExpr... items);
     public XsQNameSeqExpr qName(XsQNameExpr... items);
     public XsShortSeqExpr shortExpr(XsShortExpr... items);
     public XsStringSeqExpr string(XsStringExpr... items);
     public XsTimeSeqExpr time(XsTimeExpr... items);
     public XsTokenSeqExpr token(XsTokenExpr... items);
     public XsUnsignedByteSeqExpr unsignedByte(XsUnsignedByteExpr... items);
     public XsUnsignedIntSeqExpr unsignedInt(XsUnsignedIntExpr... items);
     public XsUnsignedLongSeqExpr unsignedLong(XsUnsignedLongExpr... items);
     public XsUnsignedShortSeqExpr unsignedShort(XsUnsignedShortExpr... items);
     public XsUntypedAtomicSeqExpr untypedAtomic(XsUntypedAtomicExpr... items);
     public XsYearMonthDurationSeqExpr yearMonthDuration(XsYearMonthDurationExpr... items);

}
