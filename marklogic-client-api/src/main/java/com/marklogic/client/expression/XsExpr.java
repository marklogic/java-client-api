/*
 * Copyright (c) 2024 MarkLogic Corporation
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



import com.marklogic.client.type.ServerExpression;

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
  public ServerExpression anyURI(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:base64Binary server data type.
  *
  * <a name="ml-server-type-base64Binary"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_base64Binary.html">xs:base64Binary</a> server data type
  */
  public ServerExpression base64Binary(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:boolean server data type.
  *
  * <a name="ml-server-type-boolean"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression booleanExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:byte server data type.
  *
  * <a name="ml-server-type-byte"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_byte.html">xs:byte</a> server data type
  */
  public ServerExpression byteExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:date server data type.
  *
  * <a name="ml-server-type-date"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public ServerExpression date(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:dateTime server data type.
  *
  * <a name="ml-server-type-dateTime"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression dateTime(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:dayTimeDuration server data type.
  *
  * <a name="ml-server-type-dayTimeDuration"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public ServerExpression dayTimeDuration(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:decimal server data type.
  *
  * <a name="ml-server-type-decimal"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_decimal.html">xs:decimal</a> server data type
  */
  public ServerExpression decimal(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:double server data type.
  *
  * <a name="ml-server-type-double"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression doubleExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:float server data type.
  *
  * <a name="ml-server-type-float"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_float.html">xs:float</a> server data type
  */
  public ServerExpression floatExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gDay server data type.
  *
  * <a name="ml-server-type-gDay"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gDay.html">xs:gDay</a> server data type
  */
  public ServerExpression gDay(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gMonth server data type.
  *
  * <a name="ml-server-type-gMonth"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gMonth.html">xs:gMonth</a> server data type
  */
  public ServerExpression gMonth(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gMonthDay server data type.
  *
  * <a name="ml-server-type-gMonthDay"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gMonthDay.html">xs:gMonthDay</a> server data type
  */
  public ServerExpression gMonthDay(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gYear server data type.
  *
  * <a name="ml-server-type-gYear"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gYear.html">xs:gYear</a> server data type
  */
  public ServerExpression gYear(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:gYearMonth server data type.
  *
  * <a name="ml-server-type-gYearMonth"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_gYearMonth.html">xs:gYearMonth</a> server data type
  */
  public ServerExpression gYearMonth(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:hexBinary server data type.
  *
  * <a name="ml-server-type-hexBinary"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_hexBinary.html">xs:hexBinary</a> server data type
  */
  public ServerExpression hexBinary(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:int server data type.
  *
  * <a name="ml-server-type-int"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_int.html">xs:int</a> server data type
  */
  public ServerExpression intExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:integer server data type.
  *
  * <a name="ml-server-type-integer"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression integer(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:language server data type.
  *
  * <a name="ml-server-type-language"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_language.html">xs:language</a> server data type
  */
  public ServerExpression language(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:long server data type.
  *
  * <a name="ml-server-type-long"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a> server data type
  */
  public ServerExpression longExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:Name server data type.
  *
  * <a name="ml-server-type-Name"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_Name.html">xs:Name</a> server data type
  */
  public ServerExpression Name(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:NCName server data type.
  *
  * <a name="ml-server-type-NCName"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NCName.html">xs:NCName</a> server data type
  */
  public ServerExpression NCName(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:negativeInteger server data type.
  *
  * <a name="ml-server-type-negativeInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_negativeInteger.html">xs:negativeInteger</a> server data type
  */
  public ServerExpression negativeInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:NMTOKEN server data type.
  *
  * <a name="ml-server-type-NMTOKEN"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NMTOKEN.html">xs:NMTOKEN</a> server data type
  */
  public ServerExpression NMTOKEN(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:nonNegativeInteger server data type.
  *
  * <a name="ml-server-type-nonNegativeInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_nonNegativeInteger.html">xs:nonNegativeInteger</a> server data type
  */
  public ServerExpression nonNegativeInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:nonPositiveInteger server data type.
  *
  * <a name="ml-server-type-nonPositiveInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_nonPositiveInteger.html">xs:nonPositiveInteger</a> server data type
  */
  public ServerExpression nonPositiveInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:normalizedString server data type.
  *
  * <a name="ml-server-type-normalizedString"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_normalizedString.html">xs:normalizedString</a> server data type
  */
  public ServerExpression normalizedString(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:numeric server data type.
  *
  * <a name="ml-server-type-numeric"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression numeric(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:positiveInteger server data type.
  *
  * <a name="ml-server-type-positiveInteger"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_positiveInteger.html">xs:positiveInteger</a> server data type
  */
  public ServerExpression positiveInteger(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:QName server data type.
  *
  * <a name="ml-server-type-QName"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression QName(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:short server data type.
  *
  * <a name="ml-server-type-short"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_short.html">xs:short</a> server data type
  */
  public ServerExpression shortExpr(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:string server data type.
  *
  * <a name="ml-server-type-string"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression string(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:time server data type.
  *
  * <a name="ml-server-type-time"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public ServerExpression time(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:token server data type.
  *
  * <a name="ml-server-type-token"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_token.html">xs:token</a> server data type
  */
  public ServerExpression token(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedByte server data type.
  *
  * <a name="ml-server-type-unsignedByte"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedByte.html">xs:unsignedByte</a> server data type
  */
  public ServerExpression unsignedByte(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedInt server data type.
  *
  * <a name="ml-server-type-unsignedInt"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression unsignedInt(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedLong server data type.
  *
  * <a name="ml-server-type-unsignedLong"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression unsignedLong(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:unsignedShort server data type.
  *
  * <a name="ml-server-type-unsignedShort"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedShort.html">xs:unsignedShort</a> server data type
  */
  public ServerExpression unsignedShort(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:untypedAtomic server data type.
  *
  * <a name="ml-server-type-untypedAtomic"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_untypedAtomic.html">xs:untypedAtomic</a> server data type
  */
  public ServerExpression untypedAtomic(ServerExpression arg1);
/**
  * Constructs or casts an expression to the xs:yearMonthDuration server data type.
  *
  * <a name="ml-server-type-yearMonthDuration"></a>
  
  * @param arg1  the expression to construct or cast.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_yearMonthDuration.html">xs:yearMonthDuration</a> server data type
  */
  public ServerExpression yearMonthDuration(ServerExpression arg1);
}
