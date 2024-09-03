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

import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDateVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the xdmp server library for a row
 * pipeline.
 */
public interface XdmpExpr {
    /**
  * Add two 64-bit integer values, discarding overflow.
  *
  * <a name="ml-server-type-add64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:add64" target="mlserverdoc">xdmp:add64</a> server function.
  * @param x  The first value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The second value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression add64(ServerExpression x, ServerExpression y);
/**
  * AND two 64-bit integer values.
  *
  * <a name="ml-server-type-and64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:and64" target="mlserverdoc">xdmp:and64</a> server function.
  * @param x  The first value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The second value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression and64(ServerExpression x, ServerExpression y);
/**
  * Converts base64-encoded string to plaintext.
  *
  * <a name="ml-server-type-base64-decode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:base64-decode" target="mlserverdoc">xdmp:base64-decode</a> server function.
  * @param encoded  Encoded text to be decoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression base64Decode(ServerExpression encoded);
/**
  * Converts plaintext into base64-encoded string.
  *
  * <a name="ml-server-type-base64-encode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:base64-encode" target="mlserverdoc">xdmp:base64-encode</a> server function.
  * @param plaintext  Plaintext to be encoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression base64Encode(ServerExpression plaintext);
/**
  * Returns true if a value is castable. This is similar to the "castable as" XQuery predicate, except that the type is determined at runtime.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:castable-as" target="mlserverdoc">xdmp:castable-as</a> server function.
  * @param namespaceUri  The namespace URI of the type.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param localName  The local-name of the type.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param item  The item to be cast.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression castableAs(ServerExpression namespaceUri, String localName, ServerExpression item);
/**
  * Returns true if a value is castable. This is similar to the "castable as" XQuery predicate, except that the type is determined at runtime.
  *
  * <a name="ml-server-type-castable-as"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:castable-as" target="mlserverdoc">xdmp:castable-as</a> server function.
  * @param namespaceUri  The namespace URI of the type.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param localName  The local-name of the type.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param item  The item to be cast.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression castableAs(ServerExpression namespaceUri, ServerExpression localName, ServerExpression item);
/**
  * Calculates the password hash for the given password and salt.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:crypt" target="mlserverdoc">xdmp:crypt</a> server function.
  * @param password  String to be hashed.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param salt  Salt to avoid 1:1 mapping from passwords to hashes. Only the first 8 characters of the salt are significant; any characters beyond the eighth are ignored.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression crypt(ServerExpression password, String salt);
/**
  * Calculates the password hash for the given password and salt.
  *
  * <a name="ml-server-type-crypt"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:crypt" target="mlserverdoc">xdmp:crypt</a> server function.
  * @param password  String to be hashed.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param salt  Salt to avoid 1:1 mapping from passwords to hashes. Only the first 8 characters of the salt are significant; any characters beyond the eighth are ignored.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression crypt(ServerExpression password, ServerExpression salt);
/**
  * Calculates the password hash for the given plain-text password.
  *
  * <a name="ml-server-type-crypt2"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:crypt2" target="mlserverdoc">xdmp:crypt2</a> server function.
  * @param password  String to be hashed.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression crypt2(ServerExpression password);
/**
  * Returns a string representing the dayname value in the localized value of arg. 
  *
  * <a name="ml-server-type-dayname-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:dayname-from-date" target="mlserverdoc">xdmp:dayname-from-date</a> server function.
  * @param arg  The date whose dayname value will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression daynameFromDate(ServerExpression arg);
/**
  * Invertible function that decodes characters an NCName produced by xdmp:encode-for-NCName. Given the NCName produced by xdmp:encode-for-NCName this function returns the original string.
  *
  * <a name="ml-server-type-decode-from-NCName"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:decode-from-NCName" target="mlserverdoc">xdmp:decode-from-NCName</a> server function.
  * @param name  A string representing an NCName. This string must have been the result of a previous call to xdmp:decode-from-NCName or undefined results will occur.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression decodeFromNCName(ServerExpression name);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  *
  * <a name="ml-server-type-describe"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a> server function.
  * @param item  The item sequence whose description is returned.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression describe(ServerExpression item);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a> server function.
  * @param item  The item sequence whose description is returned.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param maxSequenceLength  Represents the maximum number of items per sequence to print. The default is 3.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression describe(ServerExpression item, ServerExpression maxSequenceLength);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a> server function.
  * @param item  The item sequence whose description is returned.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param maxSequenceLength  Represents the maximum number of items per sequence to print. The default is 3.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param maxItemLength  Represents the maximum number of characters per item to print. The default is 64. The minimum is 8.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression describe(ServerExpression item, ServerExpression maxSequenceLength, ServerExpression maxItemLength);
/**
  * Returns the specified string, converting all of the characters with diacritics to characters without diacritics.
  *
  * <a name="ml-server-type-diacritic-less"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:diacritic-less" target="mlserverdoc">xdmp:diacritic-less</a> server function.
  * @param string  The string to convert.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression diacriticLess(ServerExpression string);
/**
  * Returns the schema-defined content-type of an element ("empty", "simple", "element-only", or "mixed").
  *
  * <a name="ml-server-type-element-content-type"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:element-content-type" target="mlserverdoc">xdmp:element-content-type</a> server function.
  * @param element  An element node.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression elementContentType(ServerExpression element);
/**
  * Invertible function that escapes characters required to be part of an NCName. This is useful when translating names from other representations such as JSON to XML. Given any string, the result is always a valid NCName. Providing all names are passed through this function the result is distinct NCNames so the results can be used for searching as well as name generation. The inverse function is xdmp:decode-from-NCName.
  *
  * <a name="ml-server-type-encode-for-NCName"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:encode-for-NCName" target="mlserverdoc">xdmp:encode-for-NCName</a> server function.
  * @param name  A string which is used as an NCName (such as the localname for an element or attribute).  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression encodeForNCName(ServerExpression name);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  *
  * <a name="ml-server-type-format-number"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language, String letterValue);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression letterValue);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language, String letterValue, String ordchar);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression letterValue, ServerExpression ordchar);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language, String letterValue, String ordchar, String zeroPadding);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression letterValue, ServerExpression ordchar, ServerExpression zeroPadding);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression letterValue, ServerExpression ordchar, ServerExpression zeroPadding, ServerExpression groupingSeparator);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSize  Represents size of the group, i.e. the number of digits before after which grouping separator is inserted. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, long groupingSize);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param groupingSize  Represents size of the group, i.e. the number of digits before after which grouping separator is inserted. An empty sequence must be passed in even if a user doesn't want to specify this argument.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression letterValue, ServerExpression ordchar, ServerExpression zeroPadding, ServerExpression groupingSeparator, ServerExpression groupingSize);
/**
  * Atomizes a JSON node, returning a JSON value.
  *
  * <a name="ml-server-type-from-json"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:from-json" target="mlserverdoc">xdmp:from-json</a> server function.
  * @param arg  A node of kind object-node(), array-node(), text(), number-node(), boolean-node(), null-node(), or document-node().  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression fromJson(ServerExpression arg);
/**
  * Returns the name of the current user.
  *
  * <a name="ml-server-type-get-current-user"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:get-current-user" target="mlserverdoc">xdmp:get-current-user</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression getCurrentUser();
/**
  * Returns the 32-bit hash of a string.
  *
  * <a name="ml-server-type-hash32"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hash32" target="mlserverdoc">xdmp:hash32</a> server function.
  * @param string  The string to be hashed.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression hash32(ServerExpression string);
/**
  * Returns the 64-bit hash of a string.
  *
  * <a name="ml-server-type-hash64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hash64" target="mlserverdoc">xdmp:hash64</a> server function.
  * @param string  The string to be hashed.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression hash64(ServerExpression string);
/**
  * Parses a hexadecimal string, returning an integer.
  *
  * <a name="ml-server-type-hex-to-integer"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hex-to-integer" target="mlserverdoc">xdmp:hex-to-integer</a> server function.
  * @param hex  The hexadecimal string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression hexToInteger(ServerExpression hex);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  *
  * <a name="ml-server-type-hmac-md5"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacMd5(ServerExpression secretkey, ServerExpression message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacMd5(ServerExpression secretkey, ServerExpression message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacMd5(ServerExpression secretkey, ServerExpression message, ServerExpression encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  *
  * <a name="ml-server-type-hmac-sha1"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha1(ServerExpression secretkey, ServerExpression message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha1(ServerExpression secretkey, ServerExpression message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha1(ServerExpression secretkey, ServerExpression message, ServerExpression encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  *
  * <a name="ml-server-type-hmac-sha256"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha256(ServerExpression secretkey, ServerExpression message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha256(ServerExpression secretkey, ServerExpression message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha256(ServerExpression secretkey, ServerExpression message, ServerExpression encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  *
  * <a name="ml-server-type-hmac-sha512"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha512(ServerExpression secretkey, ServerExpression message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha512(ServerExpression secretkey, ServerExpression message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a> server function.
  * @param secretkey  The secret key. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param message  Message to be authenticated. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression hmacSha512(ServerExpression secretkey, ServerExpression message, ServerExpression encoding);
/**
  * Returns the string where the first letter of each token has been uppercased.
  *
  * <a name="ml-server-type-initcap"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:initcap" target="mlserverdoc">xdmp:initcap</a> server function.
  * @param string  The string to modify.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression initcap(ServerExpression string);
/**
  * Returns a hexadecimal representation of an integer.
  *
  * <a name="ml-server-type-integer-to-hex"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:integer-to-hex" target="mlserverdoc">xdmp:integer-to-hex</a> server function.
  * @param val  The integer value.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression integerToHex(ServerExpression val);
/**
  * Returns an octal representation of an integer.
  *
  * <a name="ml-server-type-integer-to-octal"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:integer-to-octal" target="mlserverdoc">xdmp:integer-to-octal</a> server function.
  * @param val  The integer value.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression integerToOctal(ServerExpression val);
/**
  * Construct a context-independent string from a QName. This string is of the form "{namespaceURI}localname" and is suitable for use as a map key.
  *
  * <a name="ml-server-type-key-from-QName"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:key-from-QName" target="mlserverdoc">xdmp:key-from-QName</a> server function.
  * @param name  The QName to compute a key for.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression keyFromQName(ServerExpression name);
/**
  * Left-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:lshift64" target="mlserverdoc">xdmp:lshift64</a> server function.
  * @param x  The value to shift.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The left shift to perform. This value may be negative.  (of <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression lshift64(ServerExpression x, long y);
/**
  * Left-shift a 64-bit integer value.
  *
  * <a name="ml-server-type-lshift64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:lshift64" target="mlserverdoc">xdmp:lshift64</a> server function.
  * @param x  The value to shift.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The left shift to perform. This value may be negative.  (of <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression lshift64(ServerExpression x, ServerExpression y);
/**
  * Calculates the md5 hash of the given argument.
  *
  * <a name="ml-server-type-md5"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression md5(ServerExpression data);
/**
  * Calculates the md5 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression md5(ServerExpression data, String encoding);
/**
  * Calculates the md5 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression md5(ServerExpression data, ServerExpression encoding);
/**
  * Returns month name, calculated from the localized value of arg. 
  *
  * <a name="ml-server-type-month-name-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:month-name-from-date" target="mlserverdoc">xdmp:month-name-from-date</a> server function.
  * @param arg  The date whose month-name will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression monthNameFromDate(ServerExpression arg);
/**
  * Multiply two 64-bit integer values, discarding overflow.
  *
  * <a name="ml-server-type-mul64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:mul64" target="mlserverdoc">xdmp:mul64</a> server function.
  * @param x  The first value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The second value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression mul64(ServerExpression x, ServerExpression y);
/**
  * Returns any collections for the node's document in the database. If the specified node does not come from a document in a database, then xdmp:node-collections returns an empty sequence.
  *
  * <a name="ml-server-type-node-collections"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-collections" target="mlserverdoc">xdmp:node-collections</a> server function.
  * @param node  The node whose collections are to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression nodeCollections(ServerExpression node);
/**
  * Returns an xs:string representing the node's kind: either "document", "element", "attribute", "text", "namespace", "processing-instruction", "binary", or "comment".  
  *
  * <a name="ml-server-type-node-kind"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-kind" target="mlserverdoc">xdmp:node-kind</a> server function.
  * @param node  The node whose kind is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression nodeKind(ServerExpression node);
/**
  * Returns the metadata value of a given node.
  *
  * <a name="ml-server-type-node-metadata"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-metadata" target="mlserverdoc">xdmp:node-metadata</a> server function.
  * @param node  The node whose metadata are to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/map_map.html">map:map</a> server data type
  */
  public ServerExpression nodeMetadata(ServerExpression node);
/**
  * Returns the metadata value of a node for a particular key.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-metadata-value" target="mlserverdoc">xdmp:node-metadata-value</a> server function.
  * @param node  The node whose metadata are to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param keyName  Name of the key for the metadata.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression nodeMetadataValue(ServerExpression node, String keyName);
/**
  * Returns the metadata value of a node for a particular key.
  *
  * <a name="ml-server-type-node-metadata-value"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-metadata-value" target="mlserverdoc">xdmp:node-metadata-value</a> server function.
  * @param node  The node whose metadata are to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param keyName  Name of the key for the metadata.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression nodeMetadataValue(ServerExpression node, ServerExpression keyName);
/**
  * Returns the permissions to a node's document.
  *
  * <a name="ml-server-type-node-permissions"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a> server function.
  * @param node  The node.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression nodePermissions(ServerExpression node);
/**
  * Returns the permissions to a node's document.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a> server function.
  * @param node  The node.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param outputKind  The output kind. It can be either "elements" or "objects". With "elements", the built-in returns a sequence of XML elements. With "objects", the built-in returns a sequence of map:map. The default is "elements".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression nodePermissions(ServerExpression node, String outputKind);
/**
  * Returns the permissions to a node's document.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a> server function.
  * @param node  The node.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param outputKind  The output kind. It can be either "elements" or "objects". With "elements", the built-in returns a sequence of XML elements. With "objects", the built-in returns a sequence of map:map. The default is "elements".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression nodePermissions(ServerExpression node, ServerExpression outputKind);
/**
  * Returns the document-uri property of the parameter or its ancestor.
  *
  * <a name="ml-server-type-node-uri"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:node-uri" target="mlserverdoc">xdmp:node-uri</a> server function.
  * @param node  The node whose URI is returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression nodeUri(ServerExpression node);
/**
  * NOT a 64-bit integer value.
  *
  * <a name="ml-server-type-not64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:not64" target="mlserverdoc">xdmp:not64</a> server function.
  * @param x  The input value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression not64(ServerExpression x);
/**
  * Parses an octal string, returning an integer.
  *
  * <a name="ml-server-type-octal-to-integer"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:octal-to-integer" target="mlserverdoc">xdmp:octal-to-integer</a> server function.
  * @param octal  The octal string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression octalToInteger(ServerExpression octal);
/**
  * OR two 64-bit integer values.
  *
  * <a name="ml-server-type-or64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:or64" target="mlserverdoc">xdmp:or64</a> server function.
  * @param x  The first value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The second value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression or64(ServerExpression x, ServerExpression y);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, String value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  *
  * <a name="ml-server-type-parse-dateTime"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, ServerExpression value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, String value, String language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, ServerExpression value, ServerExpression language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, String value, String language, String calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, ServerExpression value, ServerExpression language, ServerExpression calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, String value, String language, String calendar, String country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a> server function.
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value representing the dateTime value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseDateTime(ServerExpression picture, ServerExpression value, ServerExpression language, ServerExpression calendar, ServerExpression country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, String value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  *
  * <a name="ml-server-type-parse-yymmdd"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, ServerExpression value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, String value, String language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, ServerExpression value, ServerExpression language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, String value, String language, String calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, ServerExpression value, ServerExpression language, ServerExpression calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, String value, String language, String calendar, String country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a> server function.
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1    (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The given string $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The language used in string representation of the date, time or dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression parseYymmdd(ServerExpression picture, ServerExpression value, ServerExpression language, ServerExpression calendar, ServerExpression country);
/**
  * Returns a string whose value corresponds to the path of the node.
  *
  * <a name="ml-server-type-path"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a> server function.
  * @param node  The node whose path is returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression path(ServerExpression node);
/**
  * Returns a string whose value corresponds to the path of the node.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a> server function.
  * @param node  The node whose path is returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param includeDocument  If true, then the path is presented with a leading doc(..)/.., otherwise the path is presented as /...  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression path(ServerExpression node, boolean includeDocument);
/**
  * Returns a string whose value corresponds to the path of the node.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a> server function.
  * @param node  The node whose path is returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @param includeDocument  If true, then the path is presented with a leading doc(..)/.., otherwise the path is presented as /...  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression path(ServerExpression node, ServerExpression includeDocument);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a> server function.
  * @param test  The string to test for existence in the second parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param target  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression position(ServerExpression test, String target);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  *
  * <a name="ml-server-type-position"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a> server function.
  * @param test  The string to test for existence in the second parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param target  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression position(ServerExpression test, ServerExpression target);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a> server function.
  * @param test  The string to test for existence in the second parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param target  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression position(ServerExpression test, String target, String collation);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a> server function.
  * @param test  The string to test for existence in the second parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param target  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression position(ServerExpression test, ServerExpression target, ServerExpression collation);
/**
  * Construct a QName from a string of the form "{namespaceURI}localname". This function is useful for constructing Clark notation parameters for the xdmp:xslt-eval and xdmp:xslt-invoke functions.
  *
  * <a name="ml-server-type-QName-from-key"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:QName-from-key" target="mlserverdoc">xdmp:QName-from-key</a> server function.
  * @param key  The string from which to construct a QName.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression QNameFromKey(ServerExpression key);
/**
  * Returns an integer between 1 and 4, both inclusive, calculating the quarter component in the localized value of arg. 
  *
  * <a name="ml-server-type-quarter-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:quarter-from-date" target="mlserverdoc">xdmp:quarter-from-date</a> server function.
  * @param arg  The date whose quarter component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression quarterFromDate(ServerExpression arg);
/**
  * Returns a random unsigned integer between 0 and a number up to 64 bits long.
  *
  * <a name="ml-server-type-random"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:random" target="mlserverdoc">xdmp:random</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression random();
/**
  * Returns a random unsigned integer between 0 and a number up to 64 bits long.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:random" target="mlserverdoc">xdmp:random</a> server function.
  * @param max  The optional maximum value (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression random(ServerExpression max);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:resolve-uri" target="mlserverdoc">xdmp:resolve-uri</a> server function.
  * @param relative  A URI reference to resolve against the base.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param base  An absolute URI to use as the base of the resolution.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression resolveUri(ServerExpression relative, String base);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  *
  * <a name="ml-server-type-resolve-uri"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:resolve-uri" target="mlserverdoc">xdmp:resolve-uri</a> server function.
  * @param relative  A URI reference to resolve against the base.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param base  An absolute URI to use as the base of the resolution.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression resolveUri(ServerExpression relative, ServerExpression base);
/**
  * Right-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:rshift64" target="mlserverdoc">xdmp:rshift64</a> server function.
  * @param x  The value to shift.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The right shift to perform. This value may be negative.  (of <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression rshift64(ServerExpression x, long y);
/**
  * Right-shift a 64-bit integer value.
  *
  * <a name="ml-server-type-rshift64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:rshift64" target="mlserverdoc">xdmp:rshift64</a> server function.
  * @param x  The value to shift.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The right shift to perform. This value may be negative.  (of <a href="{@docRoot}/doc-files/types/xs_long.html">xs:long</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression rshift64(ServerExpression x, ServerExpression y);
/**
  * Calculates the SHA1 hash of the given argument.
  *
  * <a name="ml-server-type-sha1"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha1(ServerExpression data);
/**
  * Calculates the SHA1 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha1(ServerExpression data, String encoding);
/**
  * Calculates the SHA1 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha1(ServerExpression data, ServerExpression encoding);
/**
  * Calculates the SHA256 hash of the given argument.
  *
  * <a name="ml-server-type-sha256"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha256(ServerExpression data);
/**
  * Calculates the SHA256 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha256(ServerExpression data, String encoding);
/**
  * Calculates the SHA256 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha256(ServerExpression data, ServerExpression encoding);
/**
  * Calculates the SHA384 hash of the given argument.
  *
  * <a name="ml-server-type-sha384"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha384(ServerExpression data);
/**
  * Calculates the SHA384 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha384(ServerExpression data, String encoding);
/**
  * Calculates the SHA384 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha384(ServerExpression data, ServerExpression encoding);
/**
  * Calculates the SHA512 hash of the given argument.
  *
  * <a name="ml-server-type-sha512"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha512(ServerExpression data);
/**
  * Calculates the SHA512 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha512(ServerExpression data, String encoding);
/**
  * Calculates the SHA512 hash of the given argument.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a> server function.
  * @param data  Data to be hashed. Must be xs:string or a binary node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression sha512(ServerExpression data, ServerExpression encoding);
/**
  * Combines an initial hash with a subsequent hash.
  *
  * <a name="ml-server-type-step64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:step64" target="mlserverdoc">xdmp:step64</a> server function.
  * @param initial  An initial hash.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param step  A step hash to be combined with the initial hash.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression step64(ServerExpression initial, ServerExpression step);
/**
  * Formats a dateTime value using POSIX strftime. This function uses the POSIX strftime system call in the way it is implemented on each platform. For other XQuery functions that have more functionality (for example, for things like timezones), use one or more if the various XQuery or XSLT standard functions such as fn:format-dateTime .
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:strftime" target="mlserverdoc">xdmp:strftime</a> server function.
  * @param format  The strftime format string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression strftime(ServerExpression format, String value);
/**
  * Formats a dateTime value using POSIX strftime. This function uses the POSIX strftime system call in the way it is implemented on each platform. For other XQuery functions that have more functionality (for example, for things like timezones), use one or more if the various XQuery or XSLT standard functions such as fn:format-dateTime .
  *
  * <a name="ml-server-type-strftime"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:strftime" target="mlserverdoc">xdmp:strftime</a> server function.
  * @param format  The strftime format string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param value  The dateTime value.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression strftime(ServerExpression format, ServerExpression value);
/**
  * Converts a 64 bit timestamp value to an xs:dateTime.
  *
  * <a name="ml-server-type-timestamp-to-wallclock"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:timestamp-to-wallclock" target="mlserverdoc">xdmp:timestamp-to-wallclock</a> server function.
  * @param timestamp  The timestamp.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression timestampToWallclock(ServerExpression timestamp);
/**
  * Constructs a JSON document.
  *
  * <a name="ml-server-type-to-json"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:to-json" target="mlserverdoc">xdmp:to-json</a> server function.
  * @param item  A sequence of items from which the JSON document is to be constructed. The item sequence from which the JSON document is constructed.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public ServerExpression toJson(ServerExpression item);
/**
  * Returns the name of the simple type of the atomic value argument as an xs:QName. 
  *
  * <a name="ml-server-type-type"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:type" target="mlserverdoc">xdmp:type</a> server function.
  * @param value  The value to return the type of.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression type(ServerExpression value);
/**
  * Parses a string as XML, returning one or more document nodes.
  *
  * <a name="ml-server-type-unquote"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:unquote" target="mlserverdoc">xdmp:unquote</a> server function.
  * @param arg  Input to be unquoted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public ServerExpression unquote(ServerExpression arg);
/**
  * Parses a string as XML, returning one or more document nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:unquote" target="mlserverdoc">xdmp:unquote</a> server function.
  * @param arg  Input to be unquoted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param defaultNamespace  Default namespace for nodes in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public ServerExpression unquote(ServerExpression arg, String defaultNamespace);
/**
  * Parses a string as XML, returning one or more document nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:unquote" target="mlserverdoc">xdmp:unquote</a> server function.
  * @param arg  Input to be unquoted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param defaultNamespace  Default namespace for nodes in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public ServerExpression unquote(ServerExpression arg, ServerExpression defaultNamespace);
/**
  * Parses a string as XML, returning one or more document nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:unquote" target="mlserverdoc">xdmp:unquote</a> server function.
  * @param arg  Input to be unquoted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param defaultNamespace  Default namespace for nodes in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param options  The options for getting this document. The default value is (). Options include:  "repair-full" Specifies that malformed XML content be repaired. XML content with multiple top-level elements will be parsed as multiple documents. This option has no effect on binary or text documents. "repair-none" Specifies that malformed XML content be rejected. XML content will be parsed as a single document, so a maximum of one document node will be returned. This option has no effect on binary or text documents. "format-text" Specifies to get the document as a text document, regardless of the URI specified. "format-binary" Specifies to get the document as a binary document, regardless of the URI specified. "format-xml" Specifies to get the document as an XML document, regardless of the URI specified. "format-json" Specifies to get the document as a JSON document, regardless of the URI specified. "default-language=xx"  If the root element node specified in the first parameter does not already have an xml:lang attribute, the language to specify in an xml:lang attribute on the root element node. If default-language is not specified, then nothing is added to the root element node. Some examples are default-language=en and default-language=fr.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public ServerExpression unquote(ServerExpression arg, String defaultNamespace, String options);
/**
  * Parses a string as XML, returning one or more document nodes.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:unquote" target="mlserverdoc">xdmp:unquote</a> server function.
  * @param arg  Input to be unquoted.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param defaultNamespace  Default namespace for nodes in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param options  The options for getting this document. The default value is (). Options include:  "repair-full" Specifies that malformed XML content be repaired. XML content with multiple top-level elements will be parsed as multiple documents. This option has no effect on binary or text documents. "repair-none" Specifies that malformed XML content be rejected. XML content will be parsed as a single document, so a maximum of one document node will be returned. This option has no effect on binary or text documents. "format-text" Specifies to get the document as a text document, regardless of the URI specified. "format-binary" Specifies to get the document as a binary document, regardless of the URI specified. "format-xml" Specifies to get the document as an XML document, regardless of the URI specified. "format-json" Specifies to get the document as a JSON document, regardless of the URI specified. "default-language=xx"  If the root element node specified in the first parameter does not already have an xml:lang attribute, the language to specify in an xml:lang attribute on the root element node. If default-language is not specified, then nothing is added to the root element node. Some examples are default-language=en and default-language=fr.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/document-node.html">document-node</a> server data type
  */
  public ServerExpression unquote(ServerExpression arg, ServerExpression defaultNamespace, ServerExpression options);
/**
  * Returns the content type of the given URI as matched in the mimetypes configuration. xdmp:content-type continues to work too.
  *
  * <a name="ml-server-type-uri-content-type"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:uri-content-type" target="mlserverdoc">xdmp:uri-content-type</a> server function.
  * @param uri  The document URI.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression uriContentType(ServerExpression uri);
/**
  * Returns the format of the given URI as matched in the mimetypes configuration.
  *
  * <a name="ml-server-type-uri-format"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:uri-format" target="mlserverdoc">xdmp:uri-format</a> server function.
  * @param uri  The document URI.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression uriFormat(ServerExpression uri);
/**
  * Converts URL-encoded string to plaintext. This decodes the string created with xdmp:url-encode.
  *
  * <a name="ml-server-type-url-decode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:url-decode" target="mlserverdoc">xdmp:url-decode</a> server function.
  * @param encoded  Encoded text to be decoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression urlDecode(ServerExpression encoded);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  *
  * <a name="ml-server-type-url-encode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a> server function.
  * @param plaintext  Plaintext to be encoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression urlEncode(ServerExpression plaintext);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a> server function.
  * @param plaintext  Plaintext to be encoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param noSpacePlus  True to encode space as "%20" instead of "+".  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression urlEncode(ServerExpression plaintext, boolean noSpacePlus);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a> server function.
  * @param plaintext  Plaintext to be encoded.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param noSpacePlus  True to encode space as "%20" instead of "+".  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression urlEncode(ServerExpression plaintext, ServerExpression noSpacePlus);
/**
  * Converts an xs:dateTime to a 64 bit timestamp value.
  *
  * <a name="ml-server-type-wallclock-to-timestamp"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:wallclock-to-timestamp" target="mlserverdoc">xdmp:wallclock-to-timestamp</a> server function.
  * @param timestamp  The xs:datetime value.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression wallclockToTimestamp(ServerExpression timestamp);
/**
  * Returns an integer between 1 and 53, both inclusive, representing the week value in the localized value of arg. 
  *
  * <a name="ml-server-type-week-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:week-from-date" target="mlserverdoc">xdmp:week-from-date</a> server function.
  * @param arg  The date whose weeks of the year will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression weekFromDate(ServerExpression arg);
/**
  * Returns an integer in the range 1 to 7, inclusive, representing the weekday value in the localized value of arg. Monday is the first weekday value (value of 1), and Sunday is the last (value of 7). 
  *
  * <a name="ml-server-type-weekday-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:weekday-from-date" target="mlserverdoc">xdmp:weekday-from-date</a> server function.
  * @param arg  The date whose weekday value will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression weekdayFromDate(ServerExpression arg);
/**
  * XOR two 64-bit integer values.
  *
  * <a name="ml-server-type-xor64"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:xor64" target="mlserverdoc">xdmp:xor64</a> server function.
  * @param x  The first value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @param y  The second value.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression xor64(ServerExpression x, ServerExpression y);
/**
  * Returns an integer between 1 and 366, both inclusive, representing the yearday value in the localized value of arg. 
  *
  * <a name="ml-server-type-yearday-from-date"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/xdmp:yearday-from-date" target="mlserverdoc">xdmp:yearday-from-date</a> server function.
  * @param arg  The date whose days of the year will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression yeardayFromDate(ServerExpression arg);
}
