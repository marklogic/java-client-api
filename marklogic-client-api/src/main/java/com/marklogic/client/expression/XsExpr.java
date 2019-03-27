/*
 * Copyright 2016-2019 MarkLogic Corporation
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

import com.marklogic.client.type.ServerExpression;
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
  *
  * <a name="ml-server-type-anyURI"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public XsAnyURIExpr anyURI(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:base64Binary server data type.
  *
  * <a name="ml-server-type-base64Binary"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_base64Binary.html">xs:base64Binary</a> server data type
  */
  public XsBase64BinaryExpr base64Binary(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:boolean server data type.
  *
  * <a name="ml-server-type-boolean"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public XsBooleanExpr booleanExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:byte server data type.
  *
  * <a name="ml-server-type-byte"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_byte.html">xs:byte</a> server data type
  */
  public XsByteExpr byteExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:date server data type.
  *
  * <a name="ml-server-type-date"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public XsDateExpr date(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:dateTime server data type.
  *
  * <a name="ml-server-type-dateTime"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public XsDateTimeExpr dateTime(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:dayTimeDuration server data type.
  *
  * <a name="ml-server-type-dayTimeDuration"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public XsDayTimeDurationExpr dayTimeDuration(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:decimal server data type.
  *
  * <a name="ml-server-type-decimal"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_decimal.html">xs:decimal</a> server data type
  */
  public XsDecimalExpr decimal(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:double server data type.
  *
  * <a name="ml-server-type-double"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public XsDoubleExpr doubleExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:float server data type.
  *
  * <a name="ml-server-type-float"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_float.html">xs:float</a> server data type
  */
  public XsFloatExpr floatExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gDay server data type.
  *
  * <a name="ml-server-type-gDay"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gDay.html">xs:gDay</a> server data type
  */
  public XsGDayExpr gDay(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gMonth server data type.
  *
  * <a name="ml-server-type-gMonth"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gMonth.html">xs:gMonth</a> server data type
  */
  public XsGMonthExpr gMonth(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gMonthDay server data type.
  *
  * <a name="ml-server-type-gMonthDay"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gMonthDay.html">xs:gMonthDay</a> server data type
  */
  public XsGMonthDayExpr gMonthDay(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gYear server data type.
  *
  * <a name="ml-server-type-gYear"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gYear.html">xs:gYear</a> server data type
  */
  public XsGYearExpr gYear(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gYearMonth server data type.
  *
  * <a name="ml-server-type-gYearMonth"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gYearMonth.html">xs:gYearMonth</a> server data type
  */
  public XsGYearMonthExpr gYearMonth(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:hexBinary server data type.
  *
  * <a name="ml-server-type-hexBinary"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_hexBinary.html">xs:hexBinary</a> server data type
  */
  public XsHexBinaryExpr hexBinary(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:int server data type.
  *
  * <a name="ml-server-type-int"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_int.html">xs:int</a> server data type
  */
  public XsIntExpr intExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:integer server data type.
  *
  * <a name="ml-server-type-integer"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public XsIntegerExpr integer(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:language server data type.
  *
  * <a name="ml-server-type-language"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_language.html">xs:language</a> server data type
  */
  public XsLanguageExpr language(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:long server data type.
  *
  * <a name="ml-server-type-long"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a> server data type
  */
  public XsLongExpr longExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:Name server data type.
  *
  * <a name="ml-server-type-Name"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_Name.html">xs:Name</a> server data type
  */
  public XsNameExpr Name(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:NCName server data type.
  *
  * <a name="ml-server-type-NCName"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NCName.html">xs:NCName</a> server data type
  */
  public XsNCNameExpr NCName(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:negativeInteger server data type.
  *
  * <a name="ml-server-type-negativeInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_negativeInteger.html">xs:negativeInteger</a> server data type
  */
  public XsNegativeIntegerExpr negativeInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:NMTOKEN server data type.
  *
  * <a name="ml-server-type-NMTOKEN"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NMTOKEN.html">xs:NMTOKEN</a> server data type
  */
  public XsNMTOKENExpr NMTOKEN(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:nonNegativeInteger server data type.
  *
  * <a name="ml-server-type-nonNegativeInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_nonNegativeInteger.html">xs:nonNegativeInteger</a> server data type
  */
  public XsNonNegativeIntegerExpr nonNegativeInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:nonPositiveInteger server data type.
  *
  * <a name="ml-server-type-nonPositiveInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_nonPositiveInteger.html">xs:nonPositiveInteger</a> server data type
  */
  public XsNonPositiveIntegerExpr nonPositiveInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:normalizedString server data type.
  *
  * <a name="ml-server-type-normalizedString"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_normalizedString.html">xs:normalizedString</a> server data type
  */
  public XsNormalizedStringExpr normalizedString(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:numeric server data type.
  *
  * <a name="ml-server-type-numeric"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public XsNumericExpr numeric(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:positiveInteger server data type.
  *
  * <a name="ml-server-type-positiveInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_positiveInteger.html">xs:positiveInteger</a> server data type
  */
  public XsPositiveIntegerExpr positiveInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:QName server data type.
  *
  * <a name="ml-server-type-QName"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public XsQNameExpr QName(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:short server data type.
  *
  * <a name="ml-server-type-short"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_short.html">xs:short</a> server data type
  */
  public XsShortExpr shortExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:string server data type.
  *
  * <a name="ml-server-type-string"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public XsStringExpr string(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:time server data type.
  *
  * <a name="ml-server-type-time"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public XsTimeExpr time(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:token server data type.
  *
  * <a name="ml-server-type-token"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_token.html">xs:token</a> server data type
  */
  public XsTokenExpr token(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedByte server data type.
  *
  * <a name="ml-server-type-unsignedByte"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedByte.html">xs:unsignedByte</a> server data type
  */
  public XsUnsignedByteExpr unsignedByte(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedInt server data type.
  *
  * <a name="ml-server-type-unsignedInt"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public XsUnsignedIntExpr unsignedInt(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedLong server data type.
  *
  * <a name="ml-server-type-unsignedLong"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public XsUnsignedLongExpr unsignedLong(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedShort server data type.
  *
  * <a name="ml-server-type-unsignedShort"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedShort.html">xs:unsignedShort</a> server data type
  */
  public XsUnsignedShortExpr unsignedShort(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:untypedAtomic server data type.
  *
  * <a name="ml-server-type-untypedAtomic"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_untypedAtomic.html">xs:untypedAtomic</a> server data type
  */
  public XsUntypedAtomicExpr untypedAtomic(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:yearMonthDuration server data type.
  *
  * <a name="ml-server-type-yearMonthDuration"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_yearMonthDuration.html">xs:yearMonthDuration</a> server data type
  */
  public XsYearMonthDurationExpr yearMonthDuration(ServerExpression arg1);
/**
  * Constructs a sequence of XsAnyAtomicTypeExpr items.
  * @param items  the XsAnyAtomicTypeExpr items collected by the sequence
  * @return  a XsAnyAtomicTypeSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsAnyAtomicTypeSeqExpr anyAtomicTypeSeq(XsAnyAtomicTypeExpr... items);
 
/**
  * Constructs a sequence of XsAnyURIExpr items.
  * @param items  the XsAnyURIExpr items collected by the sequence
  * @return  a XsAnyURISeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsAnyURISeqExpr anyURISeq(XsAnyURIExpr... items);
 
/**
  * Constructs a sequence of XsBase64BinaryExpr items.
  * @param items  the XsBase64BinaryExpr items collected by the sequence
  * @return  a XsBase64BinarySeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsBase64BinarySeqExpr base64BinarySeq(XsBase64BinaryExpr... items);
 
/**
  * Constructs a sequence of XsBooleanExpr items.
  * @param items  the XsBooleanExpr items collected by the sequence
  * @return  a XsBooleanSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsBooleanSeqExpr booleanExprSeq(XsBooleanExpr... items);
 
/**
  * Constructs a sequence of XsByteExpr items.
  * @param items  the XsByteExpr items collected by the sequence
  * @return  a XsByteSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsByteSeqExpr byteExprSeq(XsByteExpr... items);
 
/**
  * Constructs a sequence of XsDateExpr items.
  * @param items  the XsDateExpr items collected by the sequence
  * @return  a XsDateSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsDateSeqExpr dateSeq(XsDateExpr... items);
 
/**
  * Constructs a sequence of XsDateTimeExpr items.
  * @param items  the XsDateTimeExpr items collected by the sequence
  * @return  a XsDateTimeSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsDateTimeSeqExpr dateTimeSeq(XsDateTimeExpr... items);
 
/**
  * Constructs a sequence of XsDayTimeDurationExpr items.
  * @param items  the XsDayTimeDurationExpr items collected by the sequence
  * @return  a XsDayTimeDurationSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsDayTimeDurationSeqExpr dayTimeDurationSeq(XsDayTimeDurationExpr... items);
 
/**
  * Constructs a sequence of XsDecimalExpr items.
  * @param items  the XsDecimalExpr items collected by the sequence
  * @return  a XsDecimalSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsDecimalSeqExpr decimalSeq(XsDecimalExpr... items);
 
/**
  * Constructs a sequence of XsDoubleExpr items.
  * @param items  the XsDoubleExpr items collected by the sequence
  * @return  a XsDoubleSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsDoubleSeqExpr doubleExprSeq(XsDoubleExpr... items);
 
/**
  * Constructs a sequence of XsFloatExpr items.
  * @param items  the XsFloatExpr items collected by the sequence
  * @return  a XsFloatSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsFloatSeqExpr floatExprSeq(XsFloatExpr... items);
 
/**
  * Constructs a sequence of XsGDayExpr items.
  * @param items  the XsGDayExpr items collected by the sequence
  * @return  a XsGDaySeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsGDaySeqExpr GDaySeq(XsGDayExpr... items);
 
/**
  * Constructs a sequence of XsGMonthExpr items.
  * @param items  the XsGMonthExpr items collected by the sequence
  * @return  a XsGMonthSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsGMonthSeqExpr GMonthSeq(XsGMonthExpr... items);
 
/**
  * Constructs a sequence of XsGMonthDayExpr items.
  * @param items  the XsGMonthDayExpr items collected by the sequence
  * @return  a XsGMonthDaySeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsGMonthDaySeqExpr GMonthDaySeq(XsGMonthDayExpr... items);
 
/**
  * Constructs a sequence of XsGYearExpr items.
  * @param items  the XsGYearExpr items collected by the sequence
  * @return  a XsGYearSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsGYearSeqExpr GYearSeq(XsGYearExpr... items);
 
/**
  * Constructs a sequence of XsGYearMonthExpr items.
  * @param items  the XsGYearMonthExpr items collected by the sequence
  * @return  a XsGYearMonthSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsGYearMonthSeqExpr GYearMonthSeq(XsGYearMonthExpr... items);
 
/**
  * Constructs a sequence of XsHexBinaryExpr items.
  * @param items  the XsHexBinaryExpr items collected by the sequence
  * @return  a XsHexBinarySeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsHexBinarySeqExpr hexBinarySeq(XsHexBinaryExpr... items);
 
/**
  * Constructs a sequence of XsIntExpr items.
  * @param items  the XsIntExpr items collected by the sequence
  * @return  a XsIntSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsIntSeqExpr intExprSeq(XsIntExpr... items);
 
/**
  * Constructs a sequence of XsIntegerExpr items.
  * @param items  the XsIntegerExpr items collected by the sequence
  * @return  a XsIntegerSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsIntegerSeqExpr integerSeq(XsIntegerExpr... items);
 
/**
  * Constructs a sequence of XsLanguageExpr items.
  * @param items  the XsLanguageExpr items collected by the sequence
  * @return  a XsLanguageSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsLanguageSeqExpr languageSeq(XsLanguageExpr... items);
 
/**
  * Constructs a sequence of XsLongExpr items.
  * @param items  the XsLongExpr items collected by the sequence
  * @return  a XsLongSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsLongSeqExpr longExprSeq(XsLongExpr... items);
 
/**
  * Constructs a sequence of XsNameExpr items.
  * @param items  the XsNameExpr items collected by the sequence
  * @return  a XsNameSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNameSeqExpr NameSeq(XsNameExpr... items);
 
/**
  * Constructs a sequence of XsNCNameExpr items.
  * @param items  the XsNCNameExpr items collected by the sequence
  * @return  a XsNCNameSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNCNameSeqExpr NCNameSeq(XsNCNameExpr... items);
 
/**
  * Constructs a sequence of XsNegativeIntegerExpr items.
  * @param items  the XsNegativeIntegerExpr items collected by the sequence
  * @return  a XsNegativeIntegerSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNegativeIntegerSeqExpr negativeIntegerSeq(XsNegativeIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNMTOKENExpr items.
  * @param items  the XsNMTOKENExpr items collected by the sequence
  * @return  a XsNMTOKENSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNMTOKENSeqExpr NMTOKENSeq(XsNMTOKENExpr... items);
 
/**
  * Constructs a sequence of XsNonNegativeIntegerExpr items.
  * @param items  the XsNonNegativeIntegerExpr items collected by the sequence
  * @return  a XsNonNegativeIntegerSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNonNegativeIntegerSeqExpr nonNegativeIntegerSeq(XsNonNegativeIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNonPositiveIntegerExpr items.
  * @param items  the XsNonPositiveIntegerExpr items collected by the sequence
  * @return  a XsNonPositiveIntegerSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNonPositiveIntegerSeqExpr nonPositiveIntegerSeq(XsNonPositiveIntegerExpr... items);
 
/**
  * Constructs a sequence of XsNormalizedStringExpr items.
  * @param items  the XsNormalizedStringExpr items collected by the sequence
  * @return  a XsNormalizedStringSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNormalizedStringSeqExpr normalizedStringSeq(XsNormalizedStringExpr... items);
 
/**
  * Constructs a sequence of XsNumericExpr items.
  * @param items  the XsNumericExpr items collected by the sequence
  * @return  a XsNumericSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsNumericSeqExpr numericSeq(XsNumericExpr... items);
 
/**
  * Constructs a sequence of XsPositiveIntegerExpr items.
  * @param items  the XsPositiveIntegerExpr items collected by the sequence
  * @return  a XsPositiveIntegerSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsPositiveIntegerSeqExpr positiveIntegerSeq(XsPositiveIntegerExpr... items);
 
/**
  * Constructs a sequence of XsQNameExpr items.
  * @param items  the XsQNameExpr items collected by the sequence
  * @return  a XsQNameSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsQNameSeqExpr QNameSeq(XsQNameExpr... items);
 
/**
  * Constructs a sequence of XsShortExpr items.
  * @param items  the XsShortExpr items collected by the sequence
  * @return  a XsShortSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsShortSeqExpr shortExprSeq(XsShortExpr... items);
 
/**
  * Constructs a sequence of XsStringExpr items.
  * @param items  the XsStringExpr items collected by the sequence
  * @return  a XsStringSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsStringSeqExpr stringSeq(XsStringExpr... items);
 
/**
  * Constructs a sequence of XsTimeExpr items.
  * @param items  the XsTimeExpr items collected by the sequence
  * @return  a XsTimeSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsTimeSeqExpr timeSeq(XsTimeExpr... items);
 
/**
  * Constructs a sequence of XsTokenExpr items.
  * @param items  the XsTokenExpr items collected by the sequence
  * @return  a XsTokenSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsTokenSeqExpr tokenSeq(XsTokenExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedByteExpr items.
  * @param items  the XsUnsignedByteExpr items collected by the sequence
  * @return  a XsUnsignedByteSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsUnsignedByteSeqExpr unsignedByteSeq(XsUnsignedByteExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedIntExpr items.
  * @param items  the XsUnsignedIntExpr items collected by the sequence
  * @return  a XsUnsignedIntSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsUnsignedIntSeqExpr unsignedIntSeq(XsUnsignedIntExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedLongExpr items.
  * @param items  the XsUnsignedLongExpr items collected by the sequence
  * @return  a XsUnsignedLongSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsUnsignedLongSeqExpr unsignedLongSeq(XsUnsignedLongExpr... items);
 
/**
  * Constructs a sequence of XsUnsignedShortExpr items.
  * @param items  the XsUnsignedShortExpr items collected by the sequence
  * @return  a XsUnsignedShortSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsUnsignedShortSeqExpr unsignedShortSeq(XsUnsignedShortExpr... items);
 
/**
  * Constructs a sequence of XsUntypedAtomicExpr items.
  * @param items  the XsUntypedAtomicExpr items collected by the sequence
  * @return  a XsUntypedAtomicSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsUntypedAtomicSeqExpr untypedAtomicSeq(XsUntypedAtomicExpr... items);
 
/**
  * Constructs a sequence of XsYearMonthDurationExpr items.
  * @param items  the XsYearMonthDurationExpr items collected by the sequence
  * @return  a XsYearMonthDurationSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public XsYearMonthDurationSeqExpr yearMonthDurationSeq(XsYearMonthDurationExpr... items);

}
