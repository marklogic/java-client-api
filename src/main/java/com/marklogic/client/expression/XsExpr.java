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
    /**
    * Constructs or casts an expression to the xs:anyURI server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr anyURI(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:base64Binary server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsBase64BinaryExpr expression
    */
    public XsBase64BinaryExpr base64Binary(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:boolean server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr booleanExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:byte server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsByteExpr expression
    */
    public XsByteExpr byteExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:date server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsDateExpr expression
    */
    public XsDateExpr date(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:dateTime server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsDateTimeExpr expression
    */
    public XsDateTimeExpr dateTime(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:dayTimeDuration server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsDayTimeDurationExpr expression
    */
    public XsDayTimeDurationExpr dayTimeDuration(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:decimal server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsDecimalExpr expression
    */
    public XsDecimalExpr decimal(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:double server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr doubleExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:float server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsFloatExpr expression
    */
    public XsFloatExpr floatExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:gDay server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsGDayExpr expression
    */
    public XsGDayExpr gDay(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:gMonth server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsGMonthExpr expression
    */
    public XsGMonthExpr gMonth(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:gMonthDay server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsGMonthDayExpr expression
    */
    public XsGMonthDayExpr gMonthDay(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:gYear server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsGYearExpr expression
    */
    public XsGYearExpr gYear(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:gYearMonth server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsGYearMonthExpr expression
    */
    public XsGYearMonthExpr gYearMonth(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:hexBinary server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsHexBinaryExpr expression
    */
    public XsHexBinaryExpr hexBinary(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:int server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsIntExpr expression
    */
    public XsIntExpr intExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:integer server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr integer(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:language server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsLanguageExpr expression
    */
    public XsLanguageExpr language(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:long server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsLongExpr expression
    */
    public XsLongExpr longExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:Name server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNameExpr expression
    */
    public XsNameExpr Name(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:NCName server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNCNameExpr expression
    */
    public XsNCNameExpr NCName(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:negativeInteger server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNegativeIntegerExpr expression
    */
    public XsNegativeIntegerExpr negativeInteger(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:NMTOKEN server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNMTOKENExpr expression
    */
    public XsNMTOKENExpr NMTOKEN(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:nonNegativeInteger server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNonNegativeIntegerExpr expression
    */
    public XsNonNegativeIntegerExpr nonNegativeInteger(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:nonPositiveInteger server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNonPositiveIntegerExpr expression
    */
    public XsNonPositiveIntegerExpr nonPositiveInteger(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:normalizedString server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNormalizedStringExpr expression
    */
    public XsNormalizedStringExpr normalizedString(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:numeric server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr numeric(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:positiveInteger server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsPositiveIntegerExpr expression
    */
    public XsPositiveIntegerExpr positiveInteger(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:QName server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsQNameExpr expression
    */
    public XsQNameExpr QName(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:short server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsShortExpr expression
    */
    public XsShortExpr shortExpr(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:string server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsStringExpr expression
    */
    public XsStringExpr string(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:time server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsTimeExpr expression
    */
    public XsTimeExpr time(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:token server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsTokenExpr expression
    */
    public XsTokenExpr token(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:unsignedByte server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsUnsignedByteExpr expression
    */
    public XsUnsignedByteExpr unsignedByte(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:unsignedInt server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsUnsignedIntExpr expression
    */
    public XsUnsignedIntExpr unsignedInt(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:unsignedLong server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsUnsignedLongExpr expression
    */
    public XsUnsignedLongExpr unsignedLong(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:unsignedShort server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsUnsignedShortExpr expression
    */
    public XsUnsignedShortExpr unsignedShort(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:untypedAtomic server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsUntypedAtomicExpr expression
    */
    public XsUntypedAtomicExpr untypedAtomic(XsAnyAtomicTypeExpr arg1);
    /**
    * Constructs or casts an expression to the xs:yearMonthDuration server data type.
    * @param arg1  the expression to construct or cast
    * @return  a XsYearMonthDurationExpr expression
    */
    public XsYearMonthDurationExpr yearMonthDuration(XsAnyAtomicTypeExpr arg1);
    /**
     * Constructs a sequence of XsAnyAtomicTypeExpr items.
     */
    public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items);
 
    /**
     * Constructs a sequence of XsAnyURIExpr items.
     */
    public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items);
 
    /**
     * Constructs a sequence of XsBase64BinaryExpr items.
     */
    public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items);
 
    /**
     * Constructs a sequence of XsBooleanExpr items.
     */
    public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items);
 
    /**
     * Constructs a sequence of XsByteExpr items.
     */
    public XsByteSeqExpr byteExprSeq(XsByteExpr... items);
 
    /**
     * Constructs a sequence of XsDateExpr items.
     */
    public XsDateSeqExpr dateSeq(XsDateExpr... items);
 
    /**
     * Constructs a sequence of XsDateTimeExpr items.
     */
    public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items);
 
    /**
     * Constructs a sequence of XsDayTimeDurationExpr items.
     */
    public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items);
 
    /**
     * Constructs a sequence of XsDecimalExpr items.
     */
    public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items);
 
    /**
     * Constructs a sequence of XsDoubleExpr items.
     */
    public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items);
 
    /**
     * Constructs a sequence of XsFloatExpr items.
     */
    public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items);
 
    /**
     * Constructs a sequence of XsGDayExpr items.
     */
    public XsGDaySeqExpr GDaySeq(XsGDayExpr... items);
 
    /**
     * Constructs a sequence of XsGMonthExpr items.
     */
    public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items);
 
    /**
     * Constructs a sequence of XsGMonthDayExpr items.
     */
    public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items);
 
    /**
     * Constructs a sequence of XsGYearExpr items.
     */
    public XsGYearSeqExpr GYearSeq(XsGYearExpr... items);
 
    /**
     * Constructs a sequence of XsGYearMonthExpr items.
     */
    public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items);
 
    /**
     * Constructs a sequence of XsHexBinaryExpr items.
     */
    public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items);
 
    /**
     * Constructs a sequence of XsIntExpr items.
     */
    public XsIntSeqExpr intExprSeq(XsIntExpr... items);
 
    /**
     * Constructs a sequence of XsIntegerExpr items.
     */
    public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items);
 
    /**
     * Constructs a sequence of XsLanguageExpr items.
     */
    public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items);
 
    /**
     * Constructs a sequence of XsLongExpr items.
     */
    public XsLongSeqExpr longExprSeq(XsLongExpr... items);
 
    /**
     * Constructs a sequence of XsNameExpr items.
     */
    public XsNameSeqExpr NameSeq(XsNameExpr... items);
 
    /**
     * Constructs a sequence of XsNCNameExpr items.
     */
    public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items);
 
    /**
     * Constructs a sequence of XsNegativeIntegerExpr items.
     */
    public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items);
 
    /**
     * Constructs a sequence of XsNMTOKENExpr items.
     */
    public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items);
 
    /**
     * Constructs a sequence of XsNonNegativeIntegerExpr items.
     */
    public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items);
 
    /**
     * Constructs a sequence of XsNonPositiveIntegerExpr items.
     */
    public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items);
 
    /**
     * Constructs a sequence of XsNormalizedStringExpr items.
     */
    public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items);
 
    /**
     * Constructs a sequence of XsNumericExpr items.
     */
    public XsNumericSeqExpr numericSeq(XsNumericExpr... items);
 
    /**
     * Constructs a sequence of XsPositiveIntegerExpr items.
     */
    public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items);
 
    /**
     * Constructs a sequence of XsQNameExpr items.
     */
    public XsQNameSeqExpr QNameSeq(XsQNameExpr... items);
 
    /**
     * Constructs a sequence of XsShortExpr items.
     */
    public XsShortSeqExpr shortExprSeq(XsShortExpr... items);
 
    /**
     * Constructs a sequence of XsStringExpr items.
     */
    public XsStringSeqExpr stringSeq(XsStringExpr... items);
 
    /**
     * Constructs a sequence of XsTimeExpr items.
     */
    public XsTimeSeqExpr timeSeq(XsTimeExpr... items);
 
    /**
     * Constructs a sequence of XsTokenExpr items.
     */
    public XsTokenSeqExpr tokenSeq(XsTokenExpr... items);
 
    /**
     * Constructs a sequence of XsUnsignedByteExpr items.
     */
    public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items);
 
    /**
     * Constructs a sequence of XsUnsignedIntExpr items.
     */
    public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items);
 
    /**
     * Constructs a sequence of XsUnsignedLongExpr items.
     */
    public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items);
 
    /**
     * Constructs a sequence of XsUnsignedShortExpr items.
     */
    public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items);
 
    /**
     * Constructs a sequence of XsUntypedAtomicExpr items.
     */
    public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items);
 
    /**
     * Constructs a sequence of XsYearMonthDurationExpr items.
     */
    public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items);

}
