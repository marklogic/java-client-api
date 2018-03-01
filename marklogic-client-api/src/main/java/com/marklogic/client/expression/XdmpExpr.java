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

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.MapMapExpr;
import com.marklogic.client.type.NodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyURIExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsLongExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsNumericSeqExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedIntExpr;
import com.marklogic.client.type.XsUnsignedLongExpr;



// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the xdmp server library for a row
 * pipeline.
 */
public interface XdmpExpr {
    /**
  * Add two 64-bit integer values, discarding overflow.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:add64" target="mlserverdoc">xdmp:add64</a>
  * @param x  The first value.
  * @param y  The second value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr add64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
/**
  * AND two 64-bit integer values.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:and64" target="mlserverdoc">xdmp:and64</a>
  * @param x  The first value.
  * @param y  The second value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr and64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
/**
  * Converts base64-encoded string to plaintext.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:base64-decode" target="mlserverdoc">xdmp:base64-decode</a>
  * @param encoded  Encoded text to be decoded.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr base64Decode(XsStringExpr encoded);
/**
  * Converts plaintext into base64-encoded string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:base64-encode" target="mlserverdoc">xdmp:base64-encode</a>
  * @param plaintext  Plaintext to be encoded.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr base64Encode(XsStringExpr plaintext);
/**
  * Returns true if a value is castable. This is similar to the "castable as" XQuery predicate, except that the type is determined at runtime.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:castable-as" target="mlserverdoc">xdmp:castable-as</a>
  * @param namespaceUri  The namespace URI of the type.
  * @param localName  The local-name of the type.
  * @param item  The item to be cast.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr castableAs(XsStringExpr namespaceUri, String localName, ItemExpr item);
/**
  * Returns true if a value is castable. This is similar to the "castable as" XQuery predicate, except that the type is determined at runtime.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:castable-as" target="mlserverdoc">xdmp:castable-as</a>
  * @param namespaceUri  The namespace URI of the type.
  * @param localName  The local-name of the type.
  * @param item  The item to be cast.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr castableAs(XsStringExpr namespaceUri, XsStringExpr localName, ItemExpr item);
/**
  * Calculates the password hash for the given password and salt.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:crypt" target="mlserverdoc">xdmp:crypt</a>
  * @param password  String to be hashed.
  * @param salt  Salt to avoid 1:1 mapping from passwords to hashes. Only the first 8 characters of the salt are significant; any characters beyond the eighth are ignored.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr crypt(XsStringExpr password, String salt);
/**
  * Calculates the password hash for the given password and salt.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:crypt" target="mlserverdoc">xdmp:crypt</a>
  * @param password  String to be hashed.
  * @param salt  Salt to avoid 1:1 mapping from passwords to hashes. Only the first 8 characters of the salt are significant; any characters beyond the eighth are ignored.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr crypt(XsStringExpr password, XsStringExpr salt);
/**
  * Calculates the password hash for the given plain-text password.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:crypt2" target="mlserverdoc">xdmp:crypt2</a>
  * @param password  String to be hashed.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr crypt2(XsStringExpr password);
/**
  * Returns an xs:string representing the dayname value in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:dayname-from-date" target="mlserverdoc">xdmp:dayname-from-date</a>
  * @param arg  The date whose dayname value will be returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr daynameFromDate(XsDateExpr arg);
/**
  * Invertible function that decodes characters an NCName produced by xdmp:encode-for-NCName. Given the NCName produced by xdmp:encode-for-NCName this function returns the original string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:decode-from-NCName" target="mlserverdoc">xdmp:decode-from-NCName</a>
  * @param name  A string representing an NCName. This string must have been the result of a previous call to xdmp:decode-from-NCName or undefined results will occur.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr decodeFromNCName(XsStringExpr name);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a>
  * @param item  The item sequence whose description is returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr describe(ItemSeqExpr item);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a>
  * @param item  The item sequence whose description is returned.
  * @param maxSequenceLength  Represents the maximum number of items per sequence to print. The default is 3. () means no maximum.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength);
/**
  * Returns a string representing the description of a given item sequence. If you take the output of this function and evaluate it as an XQuery program, it returns the item(s) input to the function.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:describe" target="mlserverdoc">xdmp:describe</a>
  * @param item  The item sequence whose description is returned.
  * @param maxSequenceLength  Represents the maximum number of items per sequence to print. The default is 3. () means no maximum.
  * @param maxItemLength  Represents the maximum number of characters per item to print. The default is 64. The minimum is 8. () means no limit.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr describe(ItemSeqExpr item, XsUnsignedIntExpr maxSequenceLength, XsUnsignedIntExpr maxItemLength);
/**
  * Returns the specified string, converting all of the characters with diacritics to characters without diacritics.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:diacritic-less" target="mlserverdoc">xdmp:diacritic-less</a>
  * @param string  The string to convert.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr diacriticLess(XsStringExpr string);
/**
  * Returns the schema-defined content-type of an element ("empty", "simple", "element-only", or "mixed").
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:element-content-type" target="mlserverdoc">xdmp:element-content-type</a>
  * @param element  An element node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr elementContentType(ElementNodeExpr element);
/**
  * Invertible function that escapes characters required to be part of an NCName. This is useful when translating names from other representations such as JSON to XML. Given any string, the result is always a valid NCName. Providing all names are passed through this function the result is distinct NCNames so the results can be used for searching as well as name generation. The inverse function is xdmp:decode-for-NCName.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:encode-for-NCName" target="mlserverdoc">xdmp:encode-for-NCName</a>
  * @param name  A string which is used as an NCName (such as the localname for an element or attribute).
  * @return  a XsStringExpr expression
  */
  public XsStringExpr encodeForNCName(XsStringExpr name);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericExpr... value);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.
  * @param groupingSize  Represents size of the group, i.e. the number of digits before after which grouping separator is inserted. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String language, String letterValue, String ordchar, String zeroPadding, String groupingSeparator, long groupingSize);
/**
  * Returns a formatted number value based on the picture argument. The difference between this function and the W3C standards fn:format-number function is that this function imitates the XSLT xsl:number instruction, which has richer formatting options than the fn:format-number function. This function can be used for spelled-out and ordinal numbering in many languages. This function is available in XSLT as well as in all dialects of XQuery and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:format-number" target="mlserverdoc">xdmp:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given numeric $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. Unlike fn:format-number(), here the picture string allows spelled-out (uppercase, lowercase and Capitalcase) formatting.
  * @param language  The desired language for string representation of the numeric $value. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param letterValue  Same as letter-value attribute in xsl:number. This argument is ignored during formatting as of now. It may be used in future. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param ordchar  If $ordchar is "yes" then ordinal numbering is attempted. If this is any other string, including an empty string, then then cardinal numbering is generated. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param zeroPadding  Value of $zero-padding is used to pad integer part of a number on the left and fractional part on the right, if needed. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @param groupingSeparator  Value of $grouping-separator is a character, used to groups of digits, especially useful in making long sequence of digits more readable. For example, 10,000,000- here "," is used as a separator after each group of three digits. An empty sequence must be passed in even if a user doesn't want to specify this argument.
  * @param groupingSize  Represents size of the group, i.e. the number of digits before after which grouping separator is inserted. An empty sequence must be passed in even if a user doesn't want to specifiy this argument.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr letterValue, XsStringExpr ordchar, XsStringExpr zeroPadding, XsStringExpr groupingSeparator, XsIntegerExpr groupingSize);
/**
  * Atomizes a JSON node, returning a JSON value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:from-json" target="mlserverdoc">xdmp:from-json</a>
  * @param arg  A node of kind object-node(), array-node(), text(), number-node(), boolean-node(), null-node(), or document-node().
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr fromJson(NodeExpr arg);
/**
  * Returns the name of the current user.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:get-current-user" target="mlserverdoc">xdmp:get-current-user</a>
  * @return  a XsStringExpr expression
  */
  public XsStringExpr getCurrentUser();
/**
  * Returns the 32-bit hash of a string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hash32" target="mlserverdoc">xdmp:hash32</a>
  * @param string  The string to be hashed.
  * @return  a XsUnsignedIntExpr expression
  */
  public XsUnsignedIntExpr hash32(XsStringExpr string);
/**
  * Returns the 64-bit hash of a string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hash64" target="mlserverdoc">xdmp:hash64</a>
  * @param string  The string to be hashed.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr hash64(XsStringExpr string);
/**
  * Parses a hexadecimal string, returning an integer.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hex-to-integer" target="mlserverdoc">xdmp:hex-to-integer</a>
  * @param hex  The hexadecimal string.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr hexToInteger(XsStringExpr hex);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the md5 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-md5" target="mlserverdoc">xdmp:hmac-md5</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacMd5(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA1 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha1" target="mlserverdoc">xdmp:hmac-sha1</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha1(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA256 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha256" target="mlserverdoc">xdmp:hmac-sha256</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha256(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, String encoding);
/**
  * Calculates the Hash-based Message Authentication Code (HMAC) using the SHA512 hash function of the given secret key and message arguments.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:hmac-sha512" target="mlserverdoc">xdmp:hmac-sha512</a>
  * @param secretkey  The secret key. Must be xs:string or a binary node.
  * @param message  Message to be authenticated. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr hmacSha512(ItemExpr secretkey, ItemExpr message, XsStringExpr encoding);
/**
  * Returns the string where the first letter of each token has been uppercased.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:initcap" target="mlserverdoc">xdmp:initcap</a>
  * @param string  The string to modify.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr initcap(XsStringExpr string);
/**
  * Returns a hexadecimal representation of an integer.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:integer-to-hex" target="mlserverdoc">xdmp:integer-to-hex</a>
  * @param val  The integer value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr integerToHex(XsIntegerExpr val);
/**
  * Returns an octal representation of an integer.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:integer-to-octal" target="mlserverdoc">xdmp:integer-to-octal</a>
  * @param val  The integer value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr integerToOctal(XsIntegerExpr val);
/**
  * Construct a context-independent string from a QName. This string is of the form "{namespaceURI}localname" and is suitable for use as a map key.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:key-from-QName" target="mlserverdoc">xdmp:key-from-QName</a>
  * @param name  The QName to compute a key for.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr keyFromQName(XsQNameExpr name);
/**
  * Left-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:lshift64" target="mlserverdoc">xdmp:lshift64</a>
  * @param x  The value to shift.
  * @param y  The left shift to perform. This value may be negative.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, long y);
/**
  * Left-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:lshift64" target="mlserverdoc">xdmp:lshift64</a>
  * @param x  The value to shift.
  * @param y  The left shift to perform. This value may be negative.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr lshift64(XsUnsignedLongExpr x, XsLongExpr y);
/**
  * Calculates the md5 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr md5(ItemExpr data);
/**
  * Calculates the md5 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr md5(ItemExpr data, String encoding);
/**
  * Calculates the md5 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:md5" target="mlserverdoc">xdmp:md5</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr md5(ItemExpr data, XsStringExpr encoding);
/**
  * Returns month name, calculated from the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:month-name-from-date" target="mlserverdoc">xdmp:month-name-from-date</a>
  * @param arg  The date whose month-name will be returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr monthNameFromDate(XsDateExpr arg);
/**
  * Muliply two 64-bit integer values, discarding overflow.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:mul64" target="mlserverdoc">xdmp:mul64</a>
  * @param x  The first value.
  * @param y  The second value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr mul64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
/**
  * Returns any collections for the node's document in the database. If the specified node does not come from a document in a database, then xdmp:node-collections returns an empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-collections" target="mlserverdoc">xdmp:node-collections</a>
  * @param node  The node whose collections are to be returned.
  * @return  a XsStringSeqExpr expression sequence
  */
  public XsStringSeqExpr nodeCollections(NodeExpr node);
/**
  * Returns an xs:string representing the node's kind: either "document", "element", "attribute", "text", "namespace", "processing-instruction", "binary", or "comment".  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-kind" target="mlserverdoc">xdmp:node-kind</a>
  * @param node  The node whose kind is to be returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr nodeKind(NodeExpr node);
/**
  * Returns the metadata value of a given node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-metadata" target="mlserverdoc">xdmp:node-metadata</a>
  * @param node  The node whose metadata are to be returned.
  * @return  a MapMapExpr expression
  */
  public MapMapExpr nodeMetadata(NodeExpr node);
/**
  * Returns the metadata value of a node for a particular key.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-metadata-value" target="mlserverdoc">xdmp:node-metadata-value</a>
  * @param uri  The node whose metadata are to be returned.
  * @param keyName  Name of the key for the metadata.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr nodeMetadataValue(NodeExpr uri, String keyName);
/**
  * Returns the metadata value of a node for a particular key.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-metadata-value" target="mlserverdoc">xdmp:node-metadata-value</a>
  * @param uri  The node whose metadata are to be returned.
  * @param keyName  Name of the key for the metadata.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr nodeMetadataValue(NodeExpr uri, XsStringExpr keyName);
/**
  * Returns the permissions to a node's document.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a>
  * @param node  The node.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr nodePermissions(NodeExpr node);
/**
  * Returns the permissions to a node's document.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a>
  * @param node  The node.
  * @param outputKind  The output kind. It can be either "elements" or "objects". With "elements", the built-in returns a sequence of XML elements. With "objects", the built-in returns a sequence of map:map. The default is "elements".
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr nodePermissions(NodeExpr node, String outputKind);
/**
  * Returns the permissions to a node's document.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-permissions" target="mlserverdoc">xdmp:node-permissions</a>
  * @param node  The node.
  * @param outputKind  The output kind. It can be either "elements" or "objects". With "elements", the built-in returns a sequence of XML elements. With "objects", the built-in returns a sequence of map:map. The default is "elements".
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr nodePermissions(NodeExpr node, XsStringExpr outputKind);
/**
  * Returns the document-uri property of the parameter or its ancestor.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:node-uri" target="mlserverdoc">xdmp:node-uri</a>
  * @param node  The node whose URI is returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr nodeUri(NodeExpr node);
/**
  * NOT a 64-bit integer value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:not64" target="mlserverdoc">xdmp:not64</a>
  * @param x  The input value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr not64(XsUnsignedLongExpr x);
/**
  * Parses an octal string, returning an integer.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:octal-to-integer" target="mlserverdoc">xdmp:octal-to-integer</a>
  * @param octal  The octal string.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr octalToInteger(XsStringExpr octal);
/**
  * OR two 64-bit integer values.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:or64" target="mlserverdoc">xdmp:or64</a>
  * @param x  The first value.
  * @param y  The second value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr or64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, String value, String language, String calendar, String country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-dateTime" target="mlserverdoc">xdmp:parse-dateTime</a>
  * @param picture  The desired string representation of the given $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string. This follows the specification of picture string in the W3C XSLT 2.0 specification for the fn:format-dateTime function.  Symbol Description ----------------------------------- 'Y' year(absolute value) 'M' month in year 'D' day in month 'd' day in year 'F' day of week 'W' week in year 'w' week in month 'H' hour in day 'h' hour in half-day 'P' am/pm marker 'm' minute in hour 's' second in minute 'f' fractional seconds 'Z' timezone as a time offset from UTC for example PST 'z' timezone as an offset using GMT, for example GMT+1 
  * @param value  The given string $value representing the dateTime value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseDateTime(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, String value, String language, String calendar, String country);
/**
  * Parses a string containing date, time or dateTime using the supplied picture argument and returns a dateTime value. While this function is closely related to other XSLT functions, it is available in XSLT as well as in all XQuery dialects and in Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:parse-yymmdd" target="mlserverdoc">xdmp:parse-yymmdd</a>
  * @param picture  The desired string representation of the given $value. This follows the specification of picture string which is compatible to the format specification in icu. See http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html for more details. The following is the summary of the formatting symbols:  Symbol Description ---------------------------- "y" year(absolute value) "M" month in year "d" day in month "D" day in year "E" day of week "w" week in year "W" week in month "H" hour in day "K" hour in half-day "a" am/pm marker "s" second in minute "S" fractional seconds "Z" timezone as a time offset from UTC for example PST "ZZZZ" timezone as an offset using GMT, for example GMT+1  
  * @param value  The given string $value that needs to be formatted.
  * @param language  The language used in string representation of the date, time or dateTime value.
  * @param calendar  This argument is reserved for future use. The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used to take into account if there any country specific interpretation of the string while converting it into dateTime value.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr parseYymmdd(XsStringExpr picture, XsStringExpr value, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
/**
  * Returns a string whose value corresponds to the path of the node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a>
  * @param node  The node whose path is returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr path(NodeExpr node);
/**
  * Returns a string whose value corresponds to the path of the node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a>
  * @param node  The node whose path is returned.
  * @param includeDocument  If true, then the path is presented with a leading doc(..)/.., otherwise the path is presented as /...
  * @return  a XsStringExpr expression
  */
  public XsStringExpr path(NodeExpr node, boolean includeDocument);
/**
  * Returns a string whose value corresponds to the path of the node.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:path" target="mlserverdoc">xdmp:path</a>
  * @param node  The node whose path is returned.
  * @param includeDocument  If true, then the path is presented with a leading doc(..)/.., otherwise the path is presented as /...
  * @return  a XsStringExpr expression
  */
  public XsStringExpr path(NodeExpr node, XsBooleanExpr includeDocument);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a>
  * @param test  The string to test for existence in the second parameter.
  * @param target  The string from which to test.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr position(XsStringExpr test, String target);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a>
  * @param test  The string to test for existence in the second parameter.
  * @param target  The string from which to test.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr position(XsStringExpr test, XsStringExpr target);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a>
  * @param test  The string to test for existence in the second parameter.
  * @param target  The string from which to test.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr position(XsStringExpr test, String target, String collation);
/**
  * Returns an integer value representing the starting position of a string within the search string. Note, the string starting position is 1. If the first parameter is empty, the result is the empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:position" target="mlserverdoc">xdmp:position</a>
  * @param test  The string to test for existence in the second parameter.
  * @param target  The string from which to test.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr position(XsStringExpr test, XsStringExpr target, XsStringExpr collation);
/**
  * Construct a QName from a string of the form "{namespaceURI}localname". This function is useful for constructing Clark notation parameters for the xdmp:xslt-eval and xdmp:xslt-invoke functions.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:QName-from-key" target="mlserverdoc">xdmp:QName-from-key</a>
  * @param key  The string from which to construct a QName.
  * @return  a XsQNameExpr expression
  */
  public XsQNameExpr QNameFromKey(XsStringExpr key);
/**
  * Returns an xs:integer between 1 and 4, both inclusive, calculating the quarter component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:quarter-from-date" target="mlserverdoc">xdmp:quarter-from-date</a>
  * @param arg  The date whose quarter component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr quarterFromDate(XsDateExpr arg);
/**
  * Returns a random unsigned integer between 0 and a number up to 64 bits long.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:random" target="mlserverdoc">xdmp:random</a>
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr random();
/**
  * Returns a random unsigned integer between 0 and a number up to 64 bits long.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:random" target="mlserverdoc">xdmp:random</a>
  * @param max  The optional maximum value (inclusive).
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr random(XsUnsignedLongExpr max);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:resolve-uri" target="mlserverdoc">xdmp:resolve-uri</a>
  * @param relative  A URI reference to resolve against the base.
  * @param base  An absolute URI to use as the base of the resolution.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr resolveUri(XsStringExpr relative, String base);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:resolve-uri" target="mlserverdoc">xdmp:resolve-uri</a>
  * @param relative  A URI reference to resolve against the base.
  * @param base  An absolute URI to use as the base of the resolution.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base);
/**
  * Right-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:rshift64" target="mlserverdoc">xdmp:rshift64</a>
  * @param x  The value to shift.
  * @param y  The right shift to perform. This value may be negative.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, long y);
/**
  * Right-shift a 64-bit integer value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:rshift64" target="mlserverdoc">xdmp:rshift64</a>
  * @param x  The value to shift.
  * @param y  The right shift to perform. This value may be negative.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr rshift64(XsUnsignedLongExpr x, XsLongExpr y);
/**
  * Calculates the SHA1 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha1(ItemExpr data);
/**
  * Calculates the SHA1 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha1(ItemExpr data, String encoding);
/**
  * Calculates the SHA1 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha1" target="mlserverdoc">xdmp:sha1</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha1(ItemExpr data, XsStringExpr encoding);
/**
  * Calculates the SHA256 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha256(ItemExpr data);
/**
  * Calculates the SHA256 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha256(ItemExpr data, String encoding);
/**
  * Calculates the SHA256 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha256" target="mlserverdoc">xdmp:sha256</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha256(ItemExpr data, XsStringExpr encoding);
/**
  * Calculates the SHA384 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha384(ItemExpr data);
/**
  * Calculates the SHA384 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha384(ItemExpr data, String encoding);
/**
  * Calculates the SHA384 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha384" target="mlserverdoc">xdmp:sha384</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha384(ItemExpr data, XsStringExpr encoding);
/**
  * Calculates the SHA512 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha512(ItemExpr data);
/**
  * Calculates the SHA512 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha512(ItemExpr data, String encoding);
/**
  * Calculates the SHA512 hash of the given argument.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:sha512" target="mlserverdoc">xdmp:sha512</a>
  * @param data  Data to be hashed. Must be xs:string or a binary node.
  * @param encoding  Encoding format for the output string, must be "hex" for hexadecimal or "base64". Default is "hex".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr sha512(ItemExpr data, XsStringExpr encoding);
/**
  * Combines an initial hash with a subsequent hash.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:step64" target="mlserverdoc">xdmp:step64</a>
  * @param initial  An initial hash.
  * @param step  A step hash to be combined with the initial hash.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr step64(XsUnsignedLongExpr initial, XsUnsignedLongExpr step);
/**
  * Formats a dateTime value using POSIX strftime. This function uses the POSIX strftime system call in the way it is implemented on each platform. For other XQuery functions that have more functionality (for example, for things like timezones), use one or more if the various XQuery or XSLT standard functions such as fn:format-dateTime.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:strftime" target="mlserverdoc">xdmp:strftime</a>
  * @param format  The strftime format string.
  * @param value  The dateTime value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr strftime(XsStringExpr format, String value);
/**
  * Formats a dateTime value using POSIX strftime. This function uses the POSIX strftime system call in the way it is implemented on each platform. For other XQuery functions that have more functionality (for example, for things like timezones), use one or more if the various XQuery or XSLT standard functions such as fn:format-dateTime.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:strftime" target="mlserverdoc">xdmp:strftime</a>
  * @param format  The strftime format string.
  * @param value  The dateTime value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr strftime(XsStringExpr format, XsDateTimeExpr value);
/**
  * Converts a 64 bit timestamp value to an xs:dateTime.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:timestamp-to-wallclock" target="mlserverdoc">xdmp:timestamp-to-wallclock</a>
  * @param timestamp  The timestamp.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr timestampToWallclock(XsUnsignedLongExpr timestamp);
/**
  * Constructs a JSON document.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:to-json" target="mlserverdoc">xdmp:to-json</a>
  * @param item  A sequence of items from which the JSON document is to be constructed. The item sequence from which the JSON document is constructed.
  * @return  a NodeExpr expression
  */
  public NodeExpr toJson(ItemSeqExpr item);
/**
  * Returns the name of the simple type of the atomic value argument as an xs:QName. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:type" target="mlserverdoc">xdmp:type</a>
  * @param value  The value to return the type of.
  * @return  a XsQNameExpr expression
  */
  public XsQNameExpr type(XsAnyAtomicTypeExpr value);
/**
  * Converts URL-encoded string to plaintext. This decodes the string created with xdmp:url-encode.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:url-decode" target="mlserverdoc">xdmp:url-decode</a>
  * @param encoded  Encoded text to be decoded.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr urlDecode(XsStringExpr encoded);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a>
  * @param plaintext  Plaintext to be encoded.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr urlEncode(XsStringExpr plaintext);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a>
  * @param plaintext  Plaintext to be encoded.
  * @param noSpacePlus  True to encode space as "%20" instead of "+".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr urlEncode(XsStringExpr plaintext, boolean noSpacePlus);
/**
  * Converts plaintext into URL-encoded string. To decode the string, use xdmp:url-decode. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:url-encode" target="mlserverdoc">xdmp:url-encode</a>
  * @param plaintext  Plaintext to be encoded.
  * @param noSpacePlus  True to encode space as "%20" instead of "+".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr urlEncode(XsStringExpr plaintext, XsBooleanExpr noSpacePlus);
/**
  * Converts an xs:dateTime to a 64 bit timestamp value.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:wallclock-to-timestamp" target="mlserverdoc">xdmp:wallclock-to-timestamp</a>
  * @param timestamp  The xs:datetime value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr wallclockToTimestamp(XsDateTimeExpr timestamp);
/**
  * Returns an xs:integer between 1 and 53, both inclusive, representing the week value in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:week-from-date" target="mlserverdoc">xdmp:week-from-date</a>
  * @param arg  The date whose weeks of the year will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr weekFromDate(XsDateExpr arg);
/**
  * Returns an xs:integer in the range 1 to 7, inclusive, representing the weekday value in the localized value of arg. Monday is the first weekday value (value of 1), and Sunday is the last (value of 7). 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:weekday-from-date" target="mlserverdoc">xdmp:weekday-from-date</a>
  * @param arg  The date whose weekday value will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr weekdayFromDate(XsDateExpr arg);
/**
  * XOR two 64-bit integer values.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:xor64" target="mlserverdoc">xdmp:xor64</a>
  * @param x  The first value.
  * @param y  The second value.
  * @return  a XsUnsignedLongExpr expression
  */
  public XsUnsignedLongExpr xor64(XsUnsignedLongExpr x, XsUnsignedLongExpr y);
/**
  * Returns an xs:integer between 1 and 366, both inclusive, representing the yearday value in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/xdmp:yearday-from-date" target="mlserverdoc">xdmp:yearday-from-date</a>
  * @param arg  The date whose days of the year will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr yeardayFromDate(XsDateExpr arg);
}
