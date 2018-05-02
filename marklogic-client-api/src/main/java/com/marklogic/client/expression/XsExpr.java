/*
 * Copyright 2016-2018 MarkLogic Corporation
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

import com.marklogic.client.type.ItemSeqExpr;

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
    /**
  * Constructs or casts an expression to the xs:anyURI server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr anyURI(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:base64Binary server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsBase64BinaryExpr expression
  */
  public XsBase64BinaryExpr base64Binary(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:boolean server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr booleanExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:byte server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsByteExpr expression
  */
  public XsByteExpr byteExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:date server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsDateExpr expression
  */
  public XsDateExpr date(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:dateTime server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr dateTime(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:dayTimeDuration server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsDayTimeDurationExpr expression
  */
  public XsDayTimeDurationExpr dayTimeDuration(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:decimal server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsDecimalExpr expression
  */
  public XsDecimalExpr decimal(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:double server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsDoubleExpr expression
  */
  public XsDoubleExpr doubleExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:float server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsFloatExpr expression
  */
  public XsFloatExpr floatExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:gDay server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsGDayExpr expression
  */
  public XsGDayExpr gDay(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:gMonth server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsGMonthExpr expression
  */
  public XsGMonthExpr gMonth(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:gMonthDay server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsGMonthDayExpr expression
  */
  public XsGMonthDayExpr gMonthDay(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:gYear server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsGYearExpr expression
  */
  public XsGYearExpr gYear(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:gYearMonth server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsGYearMonthExpr expression
  */
  public XsGYearMonthExpr gYearMonth(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:hexBinary server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsHexBinaryExpr expression
  */
  public XsHexBinaryExpr hexBinary(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:int server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsIntExpr expression
  */
  public XsIntExpr intExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:integer server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr integer(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:language server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsLanguageExpr expression
  */
  public XsLanguageExpr language(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:long server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsLongExpr expression
  */
  public XsLongExpr longExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:Name server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNameExpr expression
  */
  public XsNameExpr Name(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:NCName server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNCNameExpr expression
  */
  public XsNCNameExpr NCName(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:negativeInteger server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNegativeIntegerExpr expression
  */
  public XsNegativeIntegerExpr negativeInteger(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:NMTOKEN server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNMTOKENExpr expression
  */
  public XsNMTOKENExpr NMTOKEN(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:nonNegativeInteger server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNonNegativeIntegerExpr expression
  */
  public XsNonNegativeIntegerExpr nonNegativeInteger(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:nonPositiveInteger server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNonPositiveIntegerExpr expression
  */
  public XsNonPositiveIntegerExpr nonPositiveInteger(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:normalizedString server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNormalizedStringExpr expression
  */
  public XsNormalizedStringExpr normalizedString(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:numeric server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr numeric(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:positiveInteger server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsPositiveIntegerExpr expression
  */
  public XsPositiveIntegerExpr positiveInteger(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:QName server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsQNameExpr expression
  */
  public XsQNameExpr QName(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:short server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsShortExpr expression
  */
  public XsShortExpr shortExpr(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:string server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsStringExpr expression
  */
  public XsStringExpr string(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:time server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsTimeExpr expression
  */
  public XsTimeExpr time(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:token server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsTokenExpr expression
  */
  public XsTokenExpr token(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:unsignedByte server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsUnsignedByteExpr expression
  */
  public XsUnsignedByteExpr unsignedByte(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:unsignedInt server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsUnsignedIntExpr expression
  */
  public XsUnsignedIntExpr unsignedInt(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:unsignedLong server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr unsignedLong(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:unsignedShort server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsUnsignedShortExpr expression
  */
  public XsUnsignedShortExpr unsignedShort(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:untypedAtomic server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsUntypedAtomicExpr expression
  */
  public XsUntypedAtomicExpr untypedAtomic(ItemSeqExpr arg1);
/**
  * Constructs or casts an expression to the xs:yearMonthDuration server data type.
  * @param arg1  the expression to construct or cast
  * @return  a XsYearMonthDurationExpr expression
  */
  public XsYearMonthDurationExpr yearMonthDuration(ItemSeqExpr arg1);
/**
  * Constructs a sequence of XsAnyAtomicTypeExpr items.
  * @param items  the XsAnyAtomicTypeExpr items collected by the sequence
  * @return  a XsAnyAtomicTypeSeqExpr sequence
  */
  public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items);
 
/**
  * Constructs a sequence of XsAnyURIExpr items.
  * @param items  the XsAnyURIExpr items collected by the sequence
  * @return  a XsAnyURISeqExpr sequence
  */
  public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items);
 
/**
  * Constructs a sequence of XsBase64BinaryExpr items.
  * @param items  the XsBase64BinaryExpr items collected by the sequence
  * @return  a XsBase64BinarySeqExpr sequence
  */
  public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items);
 
/**
  * Constructs a sequence of XsBooleanExpr items.
  * @param items  the XsBooleanExpr items collected by the sequence
  * @return  a XsBooleanSeqExpr sequence
  */
  public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items);
 
/**
  * Constructs a sequence of XsByteExpr items.
  * @param items  the XsByteExpr items collected by the sequence
  * @return  a XsByteSeqExpr sequence
  */
  public XsByteSeqExpr byteExprSeq(XsByteExpr... items);
 
/**
  * Constructs a sequence of XsDateExpr items.
  * @param items  the XsDateExpr items collected by the sequence
  * @return  a XsDateSeqExpr sequence
  */
  public XsDateSeqExpr dateSeq(XsDateExpr... items);
 
/**
  * Constructs a sequence of XsDateTimeExpr items.
  * @param items  the XsDateTimeExpr items collected by the sequence
  * @return  a XsDateTimeSeqExpr sequence
  */
  public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items);
 
/**
  * Constructs a sequence of XsDayTimeDurationExpr items.
  * @param items  the XsDayTimeDurationExpr items collected by the sequence
  * @return  a XsDayTimeDurationSeqExpr sequence
  */
  public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items);
 
/**
  * Constructs a sequence of XsDecimalExpr items.
  * @param items  the XsDecimalExpr items collected by the sequence
  * @return  a XsDecimalSeqExpr sequence
  */
  public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items);
 
/**
  * Constructs a sequence of XsDoubleExpr items.
  * @param items  the XsDoubleExpr items collected by the sequence
  * @return  a XsDoubleSeqExpr sequence
  */
  public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items);
 
/**
  * Constructs a sequence of XsFloatExpr items.
  * @param items  the XsFloatExpr items collected by the sequence
  * @return  a XsFloatSeqExpr sequence
  */
  public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items);
 
/**
  * Constructs a sequence of XsGDayExpr items.
  * @param items  the XsGDayExpr items collected by the sequence
  * @return  a XsGDaySeqExpr sequence
  */
  public XsGDaySeqExpr GDaySeq(XsGDayExpr... items);
 
/**
  * Constructs a sequence of XsGMonthExpr items.
  * @param items  the XsGMonthExpr items collected by the sequence
  * @return  a XsGMonthSeqExpr sequence
  */
  public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items);
 
/**
  * Constructs a sequence of XsGMonthDayExpr items.
  * @param items  the XsGMonthDayExpr items collected by the sequence
  * @return  a XsGMonthDaySeqExpr sequence
  */
  public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items);
 
/**
  * Constructs a sequence of XsGYearExpr items.
  * @param items  the XsGYearExpr items collected by the sequence
  * @return  a XsGYearSeqExpr sequence
  */
  public XsGYearSeqExpr GYearSeq(XsGYearExpr... items);
 
/**
  * Constructs a sequence of XsGYearMonthExpr items.
  * @param items  the XsGYearMonthExpr items collected by the sequence
  * @return  a XsGYearMonthSeqExpr sequence
  */
  public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items);
 
/**
  * Constructs a sequence of XsHexBinaryExpr items.
  * @param items  the XsHexBinaryExpr items collected by the sequence
  * @return  a XsHexBinarySeqExpr sequence
  */
  public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items);
 
/**
  * Constructs a sequence of XsIntExpr items.
  * @param items  the XsIntExpr items collected by the sequence
  * @return  a XsIntSeqExpr sequence
  */
  public XsIntSeqExpr intExprSeq(XsIntExpr... items);
 
/**
  * Constructs a sequence of XsIntegerExpr items.
  * @param items  the XsIntegerExpr items collected by the sequence
  * @return  a XsIntegerSeqExpr sequence
  */
  public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items);
 
/**
  * Constructs a sequence of XsLanguageExpr items.
  * @param items  the XsLanguageExpr items collected by the sequence
  * @return  a XsLanguageSeqExpr sequence
  */
  public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items);
 
/**
  * Constructs a sequence of XsLongExpr items.
  * @param items  the XsLongExpr items collected by the sequence
  * @return  a XsLongSeqExpr sequence
  */
  public XsLongSeqExpr longExprSeq(XsLongExpr... items);
 
/**
  * Constructs a sequence of XsNameExpr items.
  * @param items  the XsNameExpr items collected by the sequence
  * @return  a XsNameSeqExpr sequence
  */
  public XsNameSeqExpr NameSeq(XsNameExpr... items);
 
/**
  * Constructs a sequence of XsNCNameExpr items.
  * @param items  the XsNCNameExpr items collected by the sequence
  * @return  a XsNCNameSeqExpr sequence
  */
  public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items);
 
/**
  * Constructs a sequence of XsNegativeIntegerExpr items.
  * @param items  the XsNegativeIntegerExpr items collected by the sequence
  * @return  a XsNegativeIntegerSeqExpr sequence
  */
  public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNMTOKENExpr items.
  * @param items  the XsNMTOKENExpr items collected by the sequence
  * @return  a XsNMTOKENSeqExpr sequence
  */
  public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items);
 
/**
  * Constructs a sequence of XsNonNegativeIntegerExpr items.
  * @param items  the XsNonNegativeIntegerExpr items collected by the sequence
  * @return  a XsNonNegativeIntegerSeqExpr sequence
  */
  public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNonPositiveIntegerExpr items.
  * @param items  the XsNonPositiveIntegerExpr items collected by the sequence
  * @return  a XsNonPositiveIntegerSeqExpr sequence
  */
  public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNormalizedStringExpr items.
  * @param items  the XsNormalizedStringExpr items collected by the sequence
  * @return  a XsNormalizedStringSeqExpr sequence
  */
  public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items);
 
/**
  * Constructs a sequence of XsNumericExpr items.
  * @param items  the XsNumericExpr items collected by the sequence
  * @return  a XsNumericSeqExpr sequence
  */
  public XsNumericSeqExpr numericSeq(XsNumericExpr... items);
 
/**
  * Constructs a sequence of XsPositiveIntegerExpr items.
  * @param items  the XsPositiveIntegerExpr items collected by the sequence
  * @return  a XsPositiveIntegerSeqExpr sequence
  */
  public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items);
 
/**
  * Constructs a sequence of XsQNameExpr items.
  * @param items  the XsQNameExpr items collected by the sequence
  * @return  a XsQNameSeqExpr sequence
  */
  public XsQNameSeqExpr QNameSeq(XsQNameExpr... items);
 
/**
  * Constructs a sequence of XsShortExpr items.
  * @param items  the XsShortExpr items collected by the sequence
  * @return  a XsShortSeqExpr sequence
  */
  public XsShortSeqExpr shortExprSeq(XsShortExpr... items);
 
/**
  * Constructs a sequence of XsStringExpr items.
  * @param items  the XsStringExpr items collected by the sequence
  * @return  a XsStringSeqExpr sequence
  */
  public XsStringSeqExpr stringSeq(XsStringExpr... items);
 
/**
  * Constructs a sequence of XsTimeExpr items.
  * @param items  the XsTimeExpr items collected by the sequence
  * @return  a XsTimeSeqExpr sequence
  */
  public XsTimeSeqExpr timeSeq(XsTimeExpr... items);
 
/**
  * Constructs a sequence of XsTokenExpr items.
  * @param items  the XsTokenExpr items collected by the sequence
  * @return  a XsTokenSeqExpr sequence
  */
  public XsTokenSeqExpr tokenSeq(XsTokenExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedByteExpr items.
  * @param items  the XsUnsignedByteExpr items collected by the sequence
  * @return  a XsUnsignedByteSeqExpr sequence
  */
  public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedIntExpr items.
  * @param items  the XsUnsignedIntExpr items collected by the sequence
  * @return  a XsUnsignedIntSeqExpr sequence
  */
  public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedLongExpr items.
  * @param items  the XsUnsignedLongExpr items collected by the sequence
  * @return  a XsUnsignedLongSeqExpr sequence
  */
  public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedShortExpr items.
  * @param items  the XsUnsignedShortExpr items collected by the sequence
  * @return  a XsUnsignedShortSeqExpr sequence
  */
  public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items);
 
/**
  * Constructs a sequence of XsUntypedAtomicExpr items.
  * @param items  the XsUntypedAtomicExpr items collected by the sequence
  * @return  a XsUntypedAtomicSeqExpr sequence
  */
  public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items);
 
/**
  * Constructs a sequence of XsYearMonthDurationExpr items.
  * @param items  the XsYearMonthDurationExpr items collected by the sequence
  * @return  a XsYearMonthDurationSeqExpr sequence
  */
  public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items);

}
