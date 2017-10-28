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
import com.marklogic.client.type.NodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
import com.marklogic.client.type.XsAnyURIExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDayTimeDurationExpr;
import com.marklogic.client.type.XsDecimalExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsDurationExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsIntegerSeqExpr;
import com.marklogic.client.type.XsNCNameExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsNumericSeqExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsTimeExpr;



// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the fn server library for a row
 * pipeline.
 */
public interface FnExpr {
    /**
    *  Returns the absolute value of arg. If arg is negative returns -arg otherwise returns arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive zero (+0) or negative zero (-0), then positive zero (+0) is returned. If the argument is positive or negative infinity, positive infinity is returned. <p> For detailed type semantics, see Section 7.2.1 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions of [XQuery 1.0 and XPath 2.0 Formal Semantics]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:abs" target="mlserverdoc">fn:abs</a>
    * @param arg  A numeric value.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr abs(XsNumericExpr arg);
    /**
    * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with 00:00:00 for the time component and date and timezone components that are the same as the date and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a date value that has date and timezone components that are the same as the date and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
    * @param arg  The date to adjust to the new timezone.
    * @return  a XsDateExpr expression
    */
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg);
    /**
    * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with 00:00:00 for the time component and date and timezone components that are the same as the date and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a date value that has date and timezone components that are the same as the date and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
    * @param arg  The date to adjust to the new timezone.
    * @param timezone  The new timezone for the date.
    * @return  a XsDateExpr expression
    */
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg, String timezone);
    /**
    * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with 00:00:00 for the time component and date and timezone components that are the same as the date and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a date value that has date and timezone components that are the same as the date and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
    * @param arg  The date to adjust to the new timezone.
    * @param timezone  The new timezone for the date.
    * @return  a XsDateExpr expression
    */
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg, XsDayTimeDurationExpr timezone);
    /**
    * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then the result is an xs:dateTime value with a timezone component of timezone that is equal to arg. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
    * @param arg  The dateTime to adjust to the new timezone.
    * @return  a XsDateTimeExpr expression
    */
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg);
    /**
    * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then the result is an xs:dateTime value with a timezone component of timezone that is equal to arg. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
    * @param arg  The dateTime to adjust to the new timezone.
    * @param timezone  The new timezone for the dateTime.
    * @return  a XsDateTimeExpr expression
    */
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, String timezone);
    /**
    * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then the result is an xs:dateTime value with a timezone component of timezone that is equal to arg. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
    * @param arg  The dateTime to adjust to the new timezone.
    * @param timezone  The new timezone for the dateTime.
    * @return  a XsDateTimeExpr expression
    */
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, XsDayTimeDurationExpr timezone);
    /**
    * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with an arbitrary date for the date component and time and timezone components that are the same as the time and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a time value that has time and timezone components that are the same as the time and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a>
    * @param arg  The time to adjust to the new timezone.
    * @return  a XsTimeExpr expression
    */
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg);
    /**
    * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with an arbitrary date for the date component and time and timezone components that are the same as the time and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a time value that has time and timezone components that are the same as the time and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a>
    * @param arg  The time to adjust to the new timezone.
    * @param timezone  The new timezone for the date.
    * @return  a XsTimeExpr expression
    */
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, String timezone);
    /**
    * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. <p> If timezone is not specified, then timezone is the value of the implicit timezone in the dynamic context. <p> If arg is the empty sequence, then the result is the empty sequence. <p> A dynamic error is raised [err:FODT0003] if timezone is less than -PT14H or greater than PT14H or if does not contain an integral number of minutes. <p> If arg does not have a timezone component and timezone is the empty sequence, then the result is arg. <p> If arg does not have a timezone component and timezone is not the empty sequence, then the result is arg with timezone as the timezone component. <p> If arg has a timezone component and timezone is the empty sequence, then the result is the localized value of arg without its timezone component. <p> If arg has a timezone component and timezone is not the empty sequence, then: <p> Let $srcdt be an xs:dateTime value, with an arbitrary date for the date component and time and timezone components that are the same as the time and timezone components of arg. <p> Let $r be the result of evaluating fn:adjust-dateTime-to-timezone($srcdt, timezone) <p> The result of this function will be a time value that has time and timezone components that are the same as the time and timezone components of $r. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a>
    * @param arg  The time to adjust to the new timezone.
    * @param timezone  The new timezone for the date.
    * @return  a XsTimeExpr expression
    */
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, XsDayTimeDurationExpr timezone);
    /**
    * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a>
    * @param in  The string to start with.
    * @param regex  The regular expression pattern to match.
    * @return  a ElementNodeExpr expression
    */
    public ElementNodeExpr analyzeString(String in, String regex);
    /**
    * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a>
    * @param in  The string to start with.
    * @param regex  The regular expression pattern to match.
    * @return  a ElementNodeExpr expression
    */
    public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex);
    /**
    * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a>
    * @param in  The string to start with.
    * @param regex  The regular expression pattern to match.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a ElementNodeExpr expression
    */
    public ElementNodeExpr analyzeString(String in, String regex, String flags);
    /**
    * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a>
    * @param in  The string to start with.
    * @param regex  The regular expression pattern to match.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a ElementNodeExpr expression
    */
    public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex, XsStringExpr flags);
    /**
    *  Returns the average of the values in the input sequence arg, that is, the sum of the values divided by the number of values. <p> If arg is the empty sequence, the empty sequence is returned. <p> If arg contains values of type xs:untypedAtomic they are cast to xs:double. <p> Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. After these operations, arg must contain items of a single type, which must be one of the four numeric types,xs:yearMonthDuration or xs:dayTimeDuration or one if its subtypes. <p> If the above conditions are not met, then a type error is raised [err:FORG0006]. <p> Otherwise, returns the average of the values computed as sum(arg) div count(arg). <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:avg" target="mlserverdoc">fn:avg</a>
    * @param arg  The sequence of values to average.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr avg(XsAnyAtomicTypeSeqExpr arg);
    /**
    * Returns the value of the base-uri property for the specified node. If the node is part of a document and does not have a base-uri attribute explicitly set, <code>fn:base-uri</code> typically returns the URI of the document in which the node resides.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:base-uri" target="mlserverdoc">fn:base-uri</a>
    * @param arg  The node whose base-uri is to be returned.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr baseUri(NodeExpr arg);
    /**
    *  Computes the effective boolean value of the sequence arg. See Section 2.4.3 Effective Boolean Value[XP].  <p>NOTE: NEW 1.0 SEMANTICS NOT IMPLEMENTED: STILL USES MAY 2003 SEMANTICS. <p> If arg is the empty sequence, fn:boolean returns false. <p> If arg is a sequence whose first item is a node, fn:boolean returns true. <p> If arg is a singleton value of type xs:boolean or a derived from xs:boolean, fn:boolean returns arg. <p> If arg is a singleton value of type xs:string or a type derived from xs:string or xs:untypedAtomic, fn:boolean returns false if the operand value has zero length; otherwise it returns true. <p> If arg is a singleton value of any numeric type or a type derived from a numeric type, fn:boolean returns false if the operand value is NaN or is numerically equal to zero; otherwise it returns true. <p> In all other cases, fn:boolean raises a type error [err:FORG0006] when run in XQuery strict mode (1.0). <p> The static semantics of this function are described in <a>Section 7.2.4 The fn:boolean function[FS]</a>. <p> Note: <p> The result of this function is not necessarily the same as " arg cast as xs:boolean ". For example, fn:boolean("false") returns the value "true" whereas "false" cast as xs:boolean returns false. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:boolean" target="mlserverdoc">fn:boolean</a>
    * @param arg  A sequence of items.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr booleanExpr(ItemSeqExpr arg);
    /**
    *  Returns the smallest (closest to negative infinity) number with no fractional part that is not less than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. If the argument is less than zero and greater than -1, negative zero is returned. <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ceiling" target="mlserverdoc">fn:ceiling</a>
    * @param arg  A numeric value.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr ceiling(XsNumericExpr arg);
    /**
    *  Returns <code>true</code> if the specified parameters are the same Unicode code point, otherwise returns <code>false</code>. The codepoints are compared according to the Unicode code point collation (<a>http://www.w3.org/2005/xpath-functions/collation/codepoint</a>).  <p> If either argument is the empty sequence, the result is the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr codepointEqual(XsStringExpr comparand1, String comparand2);
    /**
    *  Returns <code>true</code> if the specified parameters are the same Unicode code point, otherwise returns <code>false</code>. The codepoints are compared according to the Unicode code point collation (<a>http://www.w3.org/2005/xpath-functions/collation/codepoint</a>).  <p> If either argument is the empty sequence, the result is the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr codepointEqual(XsStringExpr comparand1, XsStringExpr comparand2);
    /**
    * Creates an <code>xs:string</code> from a sequence of Unicode code points. Returns the zero-length string if arg is the empty sequence. If any of the code points in arg is not a legal XML character, an error is raised.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoints-to-string" target="mlserverdoc">fn:codepoints-to-string</a>
    * @param arg  A sequence of Unicode code points.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr codepointsToString(XsIntegerSeqExpr arg);
    /**
    *  Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2);
    /**
    *  Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2);
    /**
    *  Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2, String collation);
    /**
    *  Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
    * @param comparand1  A string to be compared.
    * @param comparand2  A string to be compared.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2, XsStringExpr collation);
    /**
    *  Returns the <code>xs:string</code> that is the concatenation of the values of the specified parameters. Accepts two or more <code>xs:anyAtomicType</code> arguments and casts them to <code>xs:string</code>. If any of the parameters is the empty sequence, the parameter is treated as the zero-length string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:concat" target="mlserverdoc">fn:concat</a>
    * @param parameter1  A value.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr concat(XsAnyAtomicTypeExpr... parameter1);
    /**
    * Returns <code>true</code> if the first parameter contains the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test for existence in the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2);
    /**
    * Returns <code>true</code> if the first parameter contains the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test for existence in the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2);
    /**
    * Returns <code>true</code> if the first parameter contains the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test for existence in the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2, String collation);
    /**
    * Returns <code>true</code> if the first parameter contains the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test for existence in the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation);
    /**
    * Returns the number of items in the value of arg. <p> Returns 0 if arg is the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
    * @param arg  The sequence of items to count.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr count(ItemSeqExpr arg);
    /**
    * Returns the number of items in the value of arg. <p> Returns 0 if arg is the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
    * @param arg  The sequence of items to count.
    * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard <code>fn:count</code> function.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr count(ItemSeqExpr arg, double maximum);
    /**
    * Returns the number of items in the value of arg. <p> Returns 0 if arg is the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
    * @param arg  The sequence of items to count.
    * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard <code>fn:count</code> function.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr count(ItemSeqExpr arg, XsDoubleExpr maximum);
    /**
    * Returns <code>xs:date(fn:current-dateTime())</code>. This is an <code>xs:date</code> (with timezone) that is current at some time during the evaluation of a query or transformation in which <code>fn:current-date()</code> is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of <code>fn:current-date()</code> is *implementation dependent*.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-date" target="mlserverdoc">fn:current-date</a>
    
    */
    public XsDateExpr currentDate();
    /**
    * Returns the current dateTime value (with timezone) from the dynamic context. (See <a>Section C.2 Dynamic Context Components[XP]</a>.) This is an <code>xs:dateTime</code> that is current at some time during the evaluation of a query or transformation in which <code>fn:current-dateTime()</code> is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of <code>fn:current-dateTime()</code> is *implementation dependent*.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-dateTime" target="mlserverdoc">fn:current-dateTime</a>
    
    */
    public XsDateTimeExpr currentDateTime();
    /**
    * Returns <code>xs:time(fn:current-dateTime())</code>. This is an <code>xs:time</code> (with timezone) that is current at some time during the evaluation of a query or transformation in which <code>fn:current-time()</code> is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of <code>fn:current-time()</code> is *implementation dependent*.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-time" target="mlserverdoc">fn:current-time</a>
    
    */
    public XsTimeExpr currentTime();
    /**
    * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:day-from-date" target="mlserverdoc">fn:day-from-date</a>
    * @param arg  The date whose day component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr dayFromDate(XsDateExpr arg);
    /**
    *  Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:day-from-dateTime" target="mlserverdoc">fn:day-from-dateTime</a>
    * @param arg  The dateTime whose day component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr dayFromDateTime(XsDateTimeExpr arg);
    /**
    *  Returns an xs:integer representing the days component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:days-from-duration" target="mlserverdoc">fn:days-from-duration</a>
    * @param arg  The duration whose day component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr daysFromDuration(XsDurationExpr arg);
    /**
    * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  <p>If the two sequences are both empty, the function returns true. <p> If the two sequences are of different lengths, the function returns false. <p> If the two sequences are of the same length, the function returns true if and only if every item in the sequence parameter1 is deep-equal to the item at the same position in the sequence parameter2. The rules for deciding whether two items are deep-equal follow. <p> Call the two items $i1 and $i2 respectively. <p> If $i1 and $i2 are both atomic values, they are deep-equal if and only if ($i1 eq $i2) is true. Or if both values are NaN. If the eq operator is not defined for $i1 and $i2, the function returns false. <p> If one of the pair $i1 or $i2 is an atomic value and the other is a node, the function returns false. <p> If $i1 and $i2 are both nodes, they are compared as described below: <p> If the two nodes are of different kinds, the result is false. <p> If the two nodes are both document nodes then they are deep-equal if and only if the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). <p> If the two nodes are both element nodes then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the two nodes are both annotated as having simple content or both nodes are annotated as having complex content.</li><li>the two nodes have the same number of attributes, and for every attribute $a1 in $i1/@* there exists an attribute $a2 in $i2/@* such that $a1 and $a2 are deep-equal. </li><li>One of the following conditions holds: <ul><li>Both element nodes have a type annotation that is simple content, and the typed value of $i1 is deep-equal to the typed value of $i2. </li><li>Both element nodes have a type annotation that is complex content with elementOnly content, and each child element of $i1 is deep-equal to the corresponding child element of $i2. </li><li>Both element nodes have a type annotation that is complex content with mixed content, and the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). </li><li>Both element nodes have a type annotation that is complex content with empty content. </li></ul> </li></ol> <p> If the two nodes are both attribute nodes then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the typed value of $i1 is deep-equal to the typed value of $i2.</li></ol> <p> If the two nodes are both processing instruction nodes or namespace bindings, then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)). </li><li>the string value of $i1 is equal to the string value of $i2.</li></ol> <p> If the two nodes are both text nodes and their parent nodes are not object nodes, then they are deep-equal if and only if their string-values are both equal. <p> If the two nodes are both text nodes and their parent nodes are both object nodes, then they are deep-equal if and only if their keys and string-values are both equal. <p> If the two nodes are both comment nodes, then they are deep-equal if and only if their string-values are equal. <p> If the two nodes are both object nodes, then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same number of children, and the children have the same set of keys.</li><li>two children of the two nodes with the same key are deep-equal.</li><li>the order of children does not matter. </li></ol> <p> If the two nodes are both boolean nodes, then they are deep-equal if and only if their keys and boolean values are equal. <p> If the two nodes are both number nodes, then they are deep-equal if and only if their keys and values are equal. <p> If the two nodes are both null nodes, they are deep-equal. <p> Notes: <p> The two nodes are not required to have the same type annotation, and they are not required to have the same in-scope namespaces. They may also differ in their parent, their base URI, and the values returned by the is-id and is-idrefs accesors (see Section 5.5 is-id Accessor[DM] and Section 5.6 is-idrefs Accessor[DM]). The order of children is significant, but the order of attributes is insignificant. <p> The following note applies to the Jan 2007 XQuery specification, but not to the May 2003 XQuery specification: The contents of comments and processing instructions are significant only if these nodes appear directly as items in the two sequences being compared. The content of a comment or processing instruction that appears as a descendant of an item in one of the sequences being compared does not affect the result. However, the presence of a comment or processing instruction, if it causes a text node to be split into two text nodes, may affect the result. <p> The result of fn:deep-equal(1, current-dateTime()) is false; it does not raise an error. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
    * @param parameter1  The first sequence of items, each item should be an atomic value or node.
    * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2);
    /**
    * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  <p>If the two sequences are both empty, the function returns true. <p> If the two sequences are of different lengths, the function returns false. <p> If the two sequences are of the same length, the function returns true if and only if every item in the sequence parameter1 is deep-equal to the item at the same position in the sequence parameter2. The rules for deciding whether two items are deep-equal follow. <p> Call the two items $i1 and $i2 respectively. <p> If $i1 and $i2 are both atomic values, they are deep-equal if and only if ($i1 eq $i2) is true. Or if both values are NaN. If the eq operator is not defined for $i1 and $i2, the function returns false. <p> If one of the pair $i1 or $i2 is an atomic value and the other is a node, the function returns false. <p> If $i1 and $i2 are both nodes, they are compared as described below: <p> If the two nodes are of different kinds, the result is false. <p> If the two nodes are both document nodes then they are deep-equal if and only if the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). <p> If the two nodes are both element nodes then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the two nodes are both annotated as having simple content or both nodes are annotated as having complex content.</li><li>the two nodes have the same number of attributes, and for every attribute $a1 in $i1/@* there exists an attribute $a2 in $i2/@* such that $a1 and $a2 are deep-equal. </li><li>One of the following conditions holds: <ul><li>Both element nodes have a type annotation that is simple content, and the typed value of $i1 is deep-equal to the typed value of $i2. </li><li>Both element nodes have a type annotation that is complex content with elementOnly content, and each child element of $i1 is deep-equal to the corresponding child element of $i2. </li><li>Both element nodes have a type annotation that is complex content with mixed content, and the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). </li><li>Both element nodes have a type annotation that is complex content with empty content. </li></ul> </li></ol> <p> If the two nodes are both attribute nodes then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the typed value of $i1 is deep-equal to the typed value of $i2.</li></ol> <p> If the two nodes are both processing instruction nodes or namespace bindings, then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)). </li><li>the string value of $i1 is equal to the string value of $i2.</li></ol> <p> If the two nodes are both text nodes and their parent nodes are not object nodes, then they are deep-equal if and only if their string-values are both equal. <p> If the two nodes are both text nodes and their parent nodes are both object nodes, then they are deep-equal if and only if their keys and string-values are both equal. <p> If the two nodes are both comment nodes, then they are deep-equal if and only if their string-values are equal. <p> If the two nodes are both object nodes, then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same number of children, and the children have the same set of keys.</li><li>two children of the two nodes with the same key are deep-equal.</li><li>the order of children does not matter. </li></ol> <p> If the two nodes are both boolean nodes, then they are deep-equal if and only if their keys and boolean values are equal. <p> If the two nodes are both number nodes, then they are deep-equal if and only if their keys and values are equal. <p> If the two nodes are both null nodes, they are deep-equal. <p> Notes: <p> The two nodes are not required to have the same type annotation, and they are not required to have the same in-scope namespaces. They may also differ in their parent, their base URI, and the values returned by the is-id and is-idrefs accesors (see Section 5.5 is-id Accessor[DM] and Section 5.6 is-idrefs Accessor[DM]). The order of children is significant, but the order of attributes is insignificant. <p> The following note applies to the Jan 2007 XQuery specification, but not to the May 2003 XQuery specification: The contents of comments and processing instructions are significant only if these nodes appear directly as items in the two sequences being compared. The content of a comment or processing instruction that appears as a descendant of an item in one of the sequences being compared does not affect the result. However, the presence of a comment or processing instruction, if it causes a text node to be split into two text nodes, may affect the result. <p> The result of fn:deep-equal(1, current-dateTime()) is false; it does not raise an error. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
    * @param parameter1  The first sequence of items, each item should be an atomic value or node.
    * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, String collation);
    /**
    * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  <p>If the two sequences are both empty, the function returns true. <p> If the two sequences are of different lengths, the function returns false. <p> If the two sequences are of the same length, the function returns true if and only if every item in the sequence parameter1 is deep-equal to the item at the same position in the sequence parameter2. The rules for deciding whether two items are deep-equal follow. <p> Call the two items $i1 and $i2 respectively. <p> If $i1 and $i2 are both atomic values, they are deep-equal if and only if ($i1 eq $i2) is true. Or if both values are NaN. If the eq operator is not defined for $i1 and $i2, the function returns false. <p> If one of the pair $i1 or $i2 is an atomic value and the other is a node, the function returns false. <p> If $i1 and $i2 are both nodes, they are compared as described below: <p> If the two nodes are of different kinds, the result is false. <p> If the two nodes are both document nodes then they are deep-equal if and only if the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). <p> If the two nodes are both element nodes then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the two nodes are both annotated as having simple content or both nodes are annotated as having complex content.</li><li>the two nodes have the same number of attributes, and for every attribute $a1 in $i1/@* there exists an attribute $a2 in $i2/@* such that $a1 and $a2 are deep-equal. </li><li>One of the following conditions holds: <ul><li>Both element nodes have a type annotation that is simple content, and the typed value of $i1 is deep-equal to the typed value of $i2. </li><li>Both element nodes have a type annotation that is complex content with elementOnly content, and each child element of $i1 is deep-equal to the corresponding child element of $i2. </li><li>Both element nodes have a type annotation that is complex content with mixed content, and the sequence $i1/(*|text()) is deep-equal to the sequence $i2/(*|text()). </li><li>Both element nodes have a type annotation that is complex content with empty content. </li></ul> </li></ol> <p> If the two nodes are both attribute nodes then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).</li><li>the typed value of $i1 is deep-equal to the typed value of $i2.</li></ol> <p> If the two nodes are both processing instruction nodes or namespace bindings, then they are deep-equal if and only if both the following conditions are satisfied:  <ol><li>the two nodes have the same name, that is (node-name($i1) eq node-name($i2)). </li><li>the string value of $i1 is equal to the string value of $i2.</li></ol> <p> If the two nodes are both text nodes and their parent nodes are not object nodes, then they are deep-equal if and only if their string-values are both equal. <p> If the two nodes are both text nodes and their parent nodes are both object nodes, then they are deep-equal if and only if their keys and string-values are both equal. <p> If the two nodes are both comment nodes, then they are deep-equal if and only if their string-values are equal. <p> If the two nodes are both object nodes, then they are deep-equal if and only if all of the following conditions are satisfied:  <ol><li>the two nodes have the same number of children, and the children have the same set of keys.</li><li>two children of the two nodes with the same key are deep-equal.</li><li>the order of children does not matter. </li></ol> <p> If the two nodes are both boolean nodes, then they are deep-equal if and only if their keys and boolean values are equal. <p> If the two nodes are both number nodes, then they are deep-equal if and only if their keys and values are equal. <p> If the two nodes are both null nodes, they are deep-equal. <p> Notes: <p> The two nodes are not required to have the same type annotation, and they are not required to have the same in-scope namespaces. They may also differ in their parent, their base URI, and the values returned by the is-id and is-idrefs accesors (see Section 5.5 is-id Accessor[DM] and Section 5.6 is-idrefs Accessor[DM]). The order of children is significant, but the order of attributes is insignificant. <p> The following note applies to the Jan 2007 XQuery specification, but not to the May 2003 XQuery specification: The contents of comments and processing instructions are significant only if these nodes appear directly as items in the two sequences being compared. The content of a comment or processing instruction that appears as a descendant of an item in one of the sequences being compared does not affect the result. However, the presence of a comment or processing instruction, if it causes a text node to be split into two text nodes, may affect the result. <p> The result of fn:deep-equal(1, current-dateTime()) is false; it does not raise an error. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
    * @param parameter1  The first sequence of items, each item should be an atomic value or node.
    * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, XsStringExpr collation);
    /**
    * Returns the value of the default collation property from the static context. Components of the static context are discussed in <a>Section C.1 Static Context Components[XP]</a>.  <p>The default collation property can never be undefined. If it is not explicitly defined, a system defined default codepoint is used. In the <code>1.0</code> XQuery dialect, if this is not provided, the Unicode code point collation (<code>http://www.w3.org/2005/xpath-functions/collation/codepoint</code>) is used. In the <code>1.0-ml</code> and <code>0.9-ml</code> XQuery dialects, the MarkLogic-defined codepoint URI is used (<code>http://marklogic.com/collation/codepoint</code>). 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:default-collation" target="mlserverdoc">fn:default-collation</a>
    
    */
    public XsStringExpr defaultCollation();
    /**
    *  Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. <p> The static type of the result is a sequence of prime types as defined in <a>Section 7.2.7 The fn:distinct-values function[FS]</a>. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> If arg is the empty sequence, the empty sequence is returned. <p> For xs:float and xs:double values, positive zero is equal to negative zero and, although NaN does not equal itself, if arg contains multiple NaN values a single NaN is returned. <p> If xs:dateTime, xs:date or xs:time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for the purpose of comparison. Note that xs:dateTime, xs:date or xs:time values can compare equal even if their timezones are different. <p> Which value of a set of values that compare equal is returned is implementation dependent. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
    * @param arg  A sequence of items.
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg);
    /**
    *  Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. <p> The static type of the result is a sequence of prime types as defined in <a>Section 7.2.7 The fn:distinct-values function[FS]</a>. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> If arg is the empty sequence, the empty sequence is returned. <p> For xs:float and xs:double values, positive zero is equal to negative zero and, although NaN does not equal itself, if arg contains multiple NaN values a single NaN is returned. <p> If xs:dateTime, xs:date or xs:time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for the purpose of comparison. Note that xs:dateTime, xs:date or xs:time values can compare equal even if their timezones are different. <p> Which value of a set of values that compare equal is returned is implementation dependent. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
    * @param arg  A sequence of items.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, String collation);
    /**
    *  Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. <p> The static type of the result is a sequence of prime types as defined in <a>Section 7.2.7 The fn:distinct-values function[FS]</a>. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> If arg is the empty sequence, the empty sequence is returned. <p> For xs:float and xs:double values, positive zero is equal to negative zero and, although NaN does not equal itself, if arg contains multiple NaN values a single NaN is returned. <p> If xs:dateTime, xs:date or xs:time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for the purpose of comparison. Note that xs:dateTime, xs:date or xs:time values can compare equal even if their timezones are different. <p> Which value of a set of values that compare equal is returned is implementation dependent. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
    * @param arg  A sequence of items.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
    /**
    * Returns the value of the document-uri property for the specified node. If the node is a document node, then the value returned is the URI of the document. If the node is not a document node, then <code>fn:document-uri</code> returns the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:document-uri" target="mlserverdoc">fn:document-uri</a>
    * @param arg  The node whose document-uri is to be returned.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr documentUri(NodeExpr arg);
    /**
    * If the value of arg is the empty sequence, the function returns true; otherwise, the function returns false.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:empty" target="mlserverdoc">fn:empty</a>
    * @param arg  A sequence to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr empty(ItemSeqExpr arg);
    /**
    * Invertible function that escapes characters required to be escaped inside path segments of URIs.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:encode-for-uri" target="mlserverdoc">fn:encode-for-uri</a>
    * @param uriPart  A string representing an unescaped URI.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr encodeForUri(XsStringExpr uriPart);
    /**
    * Returns <code>true</code> if the first parameter ends with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
    * @param parameter1  The parameter from which to test.
    * @param parameter2  The string to test whether it is at the end of the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2);
    /**
    * Returns <code>true</code> if the first parameter ends with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
    * @param parameter1  The parameter from which to test.
    * @param parameter2  The string to test whether it is at the end of the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2);
    /**
    * Returns <code>true</code> if the first parameter ends with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
    * @param parameter1  The parameter from which to test.
    * @param parameter2  The string to test whether it is at the end of the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2, String collation);
    /**
    * Returns <code>true</code> if the first parameter ends with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
    * @param parameter1  The parameter from which to test.
    * @param parameter2  The string to test whether it is at the end of the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation);
    /**
    * %-escapes everything except printable ASCII characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:escape-html-uri" target="mlserverdoc">fn:escape-html-uri</a>
    * @param uriPart  A string representing an unescaped URI.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr escapeHtmlUri(XsStringExpr uriPart);
    /**
    * If the value of arg is not the empty sequence, the function returns true; otherwise, the function returns false.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:exists" target="mlserverdoc">fn:exists</a>
    * @param arg  A sequence to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr exists(ItemSeqExpr arg);
    /**
    * Returns the <code>xs:boolean</code> value <code>false</code>. Equivalent to <code>xs:boolean("0")</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:false" target="mlserverdoc">fn:false</a>
    
    */
    public XsBooleanExpr falseExpr();
    /**
    *  Returns the largest (closest to positive infinity) number with no fractional part that is not greater than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For float and double arguments, if the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:floor" target="mlserverdoc">fn:floor</a>
    * @param arg  A numeric value.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr floor(XsNumericExpr arg);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, String picture);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar, String country);
    /**
    * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
    * @param value  The given date <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given date <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the date <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar, String country);
    /**
    * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
    * @param value  The given dateTime <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given dateTime <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the dateTime <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
    /**
    * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
    * @param value  The given numeric <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given number <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see <a>http://www.w3.org/TR/xslt20/#function-format-number</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture);
    /**
    * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
    * @param value  The given numeric <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given number <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see <a>http://www.w3.org/TR/xslt20/#function-format-number</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture);
    /**
    * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
    * @param value  The given numeric <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given number <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see <a>http://www.w3.org/TR/xslt20/#function-format-number</a>.
    * @param decimalFormatName  Represents a named <code>&lt;xsl:decimal-format&gt;</code> instruction. It is used to assign values to the variables mentioned above based on the picture string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String decimalFormatName);
    /**
    * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
    * @param value  The given numeric <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given number <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see <a>http://www.w3.org/TR/xslt20/#function-format-number</a>.
    * @param decimalFormatName  Represents a named <code>&lt;xsl:decimal-format&gt;</code> instruction. It is used to assign values to the variables mentioned above based on the picture string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr decimalFormatName);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, String picture);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar, String country);
    /**
    * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
    * @param value  The given time <code>$value</code> that needs to be formatted.
    * @param picture  The desired string representation of the given time <code>$value</code>. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see <a>http://www.w3.org/TR/xslt20/#date-picture-string</a>.
    * @param language  The desired language for string representation of the time <code>$value</code>.
    * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
    * @param country  $country is used the specification to take into account country specific string representation.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
    /**
    * Returns a string that uniquely identifies a given node.  <p> If node is the empty sequence, the zero-length string is returned.  <p> If the function is called without an argument, the context item is used as the default argument. The behavior of the function when the argument is omitted is the same as if the context item is passed as an argument. <p>If the context item is undefined an error is raised: [err:XPDY0002]. If the context item is not a node an error is raised: [err:XPTY0004]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:generate-id" target="mlserverdoc">fn:generate-id</a>
    * @param node  The node whose ID will be generated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr generateId(NodeExpr node);
    /**
    * Returns the first item in a sequence. For more details, see <a>XPath 3.0 Functions and Operators</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:head" target="mlserverdoc">fn:head</a>
    * @param seq  A sequence of items.
    * @return  a ItemExpr expression
    */
    public ItemExpr head(ItemSeqExpr seq);
    /**
    *  Returns an xs:integer between 0 and 23, both inclusive, representing the hours component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-dateTime" target="mlserverdoc">fn:hours-from-dateTime</a>
    * @param arg  The dateTime whose hours component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr hoursFromDateTime(XsDateTimeExpr arg);
    /**
    * Returns an xs:integer representing the hours component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-duration" target="mlserverdoc">fn:hours-from-duration</a>
    * @param arg  The duration whose hour component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr hoursFromDuration(XsDurationExpr arg);
    /**
    *  Returns an xs:integer between 0 and 23, both inclusive, representing the value of the hours component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-time" target="mlserverdoc">fn:hours-from-time</a>
    * @param arg  The time whose hours component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr hoursFromTime(XsTimeExpr arg);
    /**
    * Returns the value of the implicit timezone property from the dynamic context. Components of the dynamic context are discussed in <a>Section C.2 Dynamic Context Components[XP]</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:implicit-timezone" target="mlserverdoc">fn:implicit-timezone</a>
    
    */
    public XsDayTimeDurationExpr implicitTimezone();
    /**
    *  Returns the prefixes of the in-scope namespaces for element. For namespaces that have a prefix, it returns the prefix as an xs:NCName. For the default namespace, which has no prefix, it returns the zero-length string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:in-scope-prefixes" target="mlserverdoc">fn:in-scope-prefixes</a>
    * @param element  The element whose in-scope prefixes will be returned.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr inScopePrefixes(ElementNodeExpr element);
    /**
    *  Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> The items in the sequence seqParam are compared with srchParam under the rules for the eq operator. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. If an item compares equal, then the position of that item in the sequence srchParam is included in the result. <p> If the value of seqParam is the empty sequence, or if no item in seqParam matches srchParam, then the empty sequence is returned. <p> The first item in a sequence is at position 1, not position 0. <p> The result sequence is in ascending numeric order. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
    * @param seqParam  A sequence of values.
    * @param srchParam  A value to find on the list.
    * @return  a XsIntegerSeqExpr expression sequence
    */
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam);
    /**
    *  Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> The items in the sequence seqParam are compared with srchParam under the rules for the eq operator. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. If an item compares equal, then the position of that item in the sequence srchParam is included in the result. <p> If the value of seqParam is the empty sequence, or if no item in seqParam matches srchParam, then the empty sequence is returned. <p> The first item in a sequence is at position 1, not position 0. <p> The result sequence is in ascending numeric order. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
    * @param seqParam  A sequence of values.
    * @param srchParam  A value to find on the list.
    * @return  a XsIntegerSeqExpr expression sequence
    */
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam);
    /**
    *  Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> The items in the sequence seqParam are compared with srchParam under the rules for the eq operator. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. If an item compares equal, then the position of that item in the sequence srchParam is included in the result. <p> If the value of seqParam is the empty sequence, or if no item in seqParam matches srchParam, then the empty sequence is returned. <p> The first item in a sequence is at position 1, not position 0. <p> The result sequence is in ascending numeric order. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
    * @param seqParam  A sequence of values.
    * @param srchParam  A value to find on the list.
    * @param collationLiteral  A collation identifier.
    * @return  a XsIntegerSeqExpr expression sequence
    */
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam, String collationLiteral);
    /**
    *  Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. The collation is used when string comparison is required. <p> The items in the sequence seqParam are compared with srchParam under the rules for the eq operator. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. If an item compares equal, then the position of that item in the sequence srchParam is included in the result. <p> If the value of seqParam is the empty sequence, or if no item in seqParam matches srchParam, then the empty sequence is returned. <p> The first item in a sequence is at position 1, not position 0. <p> The result sequence is in ascending numeric order. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
    * @param seqParam  A sequence of values.
    * @param srchParam  A value to find on the list.
    * @param collationLiteral  A collation identifier.
    * @return  a XsIntegerSeqExpr expression sequence
    */
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam, XsStringExpr collationLiteral);
    /**
    *  Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.) <p> If target is the empty sequence, inserts is returned. If inserts is the empty sequence, target is returned. <p> The value returned by the function consists of all items of target whose index is less than position, followed by all items of inserts, followed by the remaining elements of target, in that sequence. <p> If position is less than one (1), the first position, the effective value of position is one (1). If position is greater than the number of items in target, then the effective value of position is equal to the number of items in target plus 1. <p> For detailed semantics see, <a>Section 7.2.15 The fn:insert-before function[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:insert-before" target="mlserverdoc">fn:insert-before</a>
    * @param target  The sequence of items into which new items will be inserted.
    * @param position  The position in the target sequence at which the new items will be added.
    * @param inserts  The items to insert into the target sequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr insertBefore(ItemSeqExpr target, long position, ItemSeqExpr inserts);
    /**
    *  Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.) <p> If target is the empty sequence, inserts is returned. If inserts is the empty sequence, target is returned. <p> The value returned by the function consists of all items of target whose index is less than position, followed by all items of inserts, followed by the remaining elements of target, in that sequence. <p> If position is less than one (1), the first position, the effective value of position is one (1). If position is greater than the number of items in target, then the effective value of position is equal to the number of items in target plus 1. <p> For detailed semantics see, <a>Section 7.2.15 The fn:insert-before function[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:insert-before" target="mlserverdoc">fn:insert-before</a>
    * @param target  The sequence of items into which new items will be inserted.
    * @param position  The position in the target sequence at which the new items will be added.
    * @param inserts  The items to insert into the target sequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr insertBefore(ItemSeqExpr target, XsIntegerExpr position, ItemSeqExpr inserts);
    /**
    * Idempotent function that escapes non-URI characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:iri-to-uri" target="mlserverdoc">fn:iri-to-uri</a>
    * @param uriPart  A string representing an unescaped URI.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr iriToUri(XsStringExpr uriPart);
    /**
    *  This function tests whether the language of node, or the context node if the second argument is omitted, as specified by xml:lang attributes is the same as, or is a sublanguage of, the language specified by testlang. The language of the argument node, or the context node if the second argument is omitted, is determined by the value of the xml:lang attribute on the node, or, if the node has no such attribute, by the value of the xml:lang attribute on the nearest ancestor of the node that has an xml:lang attribute. If there is no such ancestor, then the function returns false <p> If the second argument is omitted and the context item is undefined an error is raised: [err:XPDY0002]. If the context item is not a node an error is raised [err:XPTY0004]. <p> If testlang is the empty sequence it is interpreted as the zero-length string. <p> The relevant xml:lang attribute is determined by the value of the XPath expression: (ancestor-or-self::* /@xml:lang)[last()]  <p>If this expression returns an empty sequence, the function returns false. <p> Otherwise, the function returns true if and only if the string-value of the relevant xml:lang attribute is equal to testlang based on a caseless default match as specified in section 3.13 of [The Unicode Standard], or if the string-value of the relevant testlang attribute contains a hyphen, "-" (The character "-" is HYPHEN-MINUS, #x002D) such that the part of the string-value preceding that hyphen is equal to testlang, using caseless matching. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:lang" target="mlserverdoc">fn:lang</a>
    * @param testlang  The language against which to test the node.
    * @param node  The node to test.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr lang(XsStringExpr testlang, NodeExpr node);
    /**
    * Returns the local part of the name of arg as an xs:string that will either be the zero-length string or will have the lexical form of an xs:NCName. <p> If the argument is omitted, it defaults to the context node. If the context item is undefined an error is raised: [err:XPDY0002]. If the context item is not a node an error is raised: [err:XPTY0004]. <p> If the argument is supplied and is the empty sequence, the function returns the zero-length string. <p> If the target node has no name (that is, if it is a document node, a comment, a text node, or a namespace node having no name), the function returns the zero-length string. <p> Otherwise, the value returned will be the local part of the expanded-QName of the target node (as determined by the dm:node-name accessor in Section 5.11 node-name Accessor[DM]. This will be an xs:string whose lexical form is an xs:NCName. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:local-name" target="mlserverdoc">fn:local-name</a>
    * @param arg  The node whose local name is to be returned.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr localName(NodeExpr arg);
    /**
    * Returns an <code>xs:NCName</code> representing the local part of arg. If arg is the empty sequence, returns the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:local-name-from-QName" target="mlserverdoc">fn:local-name-from-QName</a>
    * @param arg  A qualified name.
    * @return  a XsNCNameExpr expression
    */
    public XsNCNameExpr localNameFromQName(XsQNameExpr arg);
    /**
    * Returns the specified string converting all of the characters to lower-case characters. If a character does not have a corresponding lower-case character, then the original character is returned. The lower-case characters are determined using the <a>Unicode Case Mappings</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:lower-case" target="mlserverdoc">fn:lower-case</a>
    * @param string  The string to convert.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr lowerCase(XsStringExpr string);
    /**
    * Returns <code>true</code> if the specified input matches the specified pattern, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
    * @param input  The input from which to match.
    * @param pattern  The regular expression to match.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr matches(XsStringExpr input, String pattern);
    /**
    * Returns <code>true</code> if the specified input matches the specified pattern, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
    * @param input  The input from which to match.
    * @param pattern  The regular expression to match.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern);
    /**
    * Returns <code>true</code> if the specified input matches the specified pattern, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
    * @param input  The input from which to match.
    * @param pattern  The regular expression to match.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr matches(XsStringExpr input, String pattern, String flags);
    /**
    * Returns <code>true</code> if the specified input matches the specified pattern, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
    * @param input  The input from which to match.
    * @param pattern  The regular expression to match.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags);
    /**
    *  Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the ge operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] ge $v) then $c[1] else fn:max(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:max function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
    * @param arg  The sequence of values whose maximum will be returned.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg);
    /**
    *  Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the ge operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] ge $v) then $c[1] else fn:max(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:max function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
    * @param arg  The sequence of values whose maximum will be returned.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, String collation);
    /**
    *  Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the ge operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] ge $v) then $c[1] else fn:max(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:max function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
    * @param arg  The sequence of values whose maximum will be returned.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
    /**
    *  Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the le operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] le $v) then $c[1] else fn:min(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:min function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
    * @param arg  The sequence of values whose minimum will be returned.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg);
    /**
    *  Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the le operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] le $v) then $c[1] else fn:min(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:min function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
    * @param arg  The sequence of values whose minimum will be returned.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, String collation);
    /**
    *  Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. <p> The following rules are applied to the input sequence:  <ul><li>Values of type xs:untypedAtomic in arg are cast to xs:double.</li><li>For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. </li></ul> <p> The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence.This function returns an item from the converted sequence rather than the input sequence. <p> If the converted sequence is empty, the empty sequence is returned. <p> All items in arg must be numeric or derived from a single base type for which the le operator is defined. In addition, the values in the sequence must have a total order. If date/time values do not have a timezone, they are considered to have the implicit timezone provided by the dynamic context for purposes of comparison. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. <p> If any of these conditions is not met, then a type error is raised [err:FORG0006]. <p> If the converted sequence contains the value NaN, the value NaN is returned. <p> If the items in the value of arg are of type xs:string or types derived by restriction from xs:string, then the determination of the item with the largest value is made according to the collation that is used. If the type of the items in arg is not xs:string and collation is specified, the collation is ignored. <p> The collation used by the invocation of this function is determined according to the rules in 7.3.1 Collations. <p> Otherwise, the result of the function is the result of the expression: <pre> if (every $v in $c satisfies $c[1] le $v) then $c[1] else fn:min(fn:subsequence($c, 2)) </pre> <p> evaluated with collation as the default collation if specified, and with $c as the converted sequence. <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> If the converted sequence contains exactly one value then that value is returned. <p> The default type when the fn:min function is applied to xs:untypedAtomic values is xs:double. This differs from the default type for operators such as gt, and for sorting in XQuery and XSLT, which is xs:string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
    * @param arg  The sequence of values whose minimum will be returned.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
    /**
    * Returns an xs:integer value between 0 and 59, both inclusive, representing the minute component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-dateTime" target="mlserverdoc">fn:minutes-from-dateTime</a>
    * @param arg  The dateTime whose minutes component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr minutesFromDateTime(XsDateTimeExpr arg);
    /**
    * Returns an xs:integer representing the minutes component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-duration" target="mlserverdoc">fn:minutes-from-duration</a>
    * @param arg  The duration whose minute component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr minutesFromDuration(XsDurationExpr arg);
    /**
    *  Returns an xs:integer value between 0 to 59, both inclusive, representing the value of the minutes component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-time" target="mlserverdoc">fn:minutes-from-time</a>
    * @param arg  The time whose minutes component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr minutesFromTime(XsTimeExpr arg);
    /**
    * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:month-from-date" target="mlserverdoc">fn:month-from-date</a>
    * @param arg  The date whose month component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr monthFromDate(XsDateExpr arg);
    /**
    *  Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:month-from-dateTime" target="mlserverdoc">fn:month-from-dateTime</a>
    * @param arg  The dateTime whose month component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr monthFromDateTime(XsDateTimeExpr arg);
    /**
    * Returns an xs:integer representing the months component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:months-from-duration" target="mlserverdoc">fn:months-from-duration</a>
    * @param arg  The duration whose month component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr monthsFromDuration(XsDurationExpr arg);
    /**
    * Returns the name of a node, as an <code>xs:string</code> that is either the zero-length string, or has the lexical form of an <code>xs:QName</code>. <p> If the argument is omitted, it defaults to the context node. If the context item is undefined an error is raised: [err:XPDY002]. If the context item is not a node an error is raised: [err:XPTY0004]. <p> If the argument is supplied and is the empty sequence, the function returns the zero-length string. <p> If the target node has no name (that is, if it is a document node, a comment, a text node, or a namespace node having no name), the function returns the zero-length string.  <p> If the specified node was created with a namespace prefix, that namespace prefix is returned with the element localname (for example, <code>a:hello</code>). Note that the namespace prefix is not always the same prefix that would be returned if you serialized the QName of the node, as the serialized QName will use the namespace from the XQuery context in which it was serialized.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:name" target="mlserverdoc">fn:name</a>
    * @param arg  The node whose name is to be returned.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr name(NodeExpr arg);
    /**
    * Returns the namespace URI of the xs:QName of the node specified by arg. <p> If the argument is omitted, it defaults to the context node. If the context item is undefined an error is raised: [err:XPDY0002]. If the context item is not a node an error is raised: [err:XPTY0004]. <p> If arg is the empty sequence, the xs:anyURI corresponding to the zero-length string is returned. <p> If arg is neither an element nor an attribute node, or if it is an element or attribute node whose expanded-QName (as determined by the dm:node-name accessor in the Section 5.11 node-name Accessor[DM]) is in no namespace, then the function returns the xs:anyURI corresponding to the zero-length string. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri" target="mlserverdoc">fn:namespace-uri</a>
    * @param arg  The node whose namespace URI is to be returned.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr namespaceUri(NodeExpr arg);
    /**
    *  Returns the namespace URI of one of the in-scope namespaces for element, identified by its namespace prefix. <p> If element has an in-scope namespace whose namespace prefix is equal to prefix, it returns the namespace URI of that namespace. If prefix is the zero-length string or the empty sequence, it returns the namespace URI of the default (unnamed) namespace. Otherwise, it returns the empty sequence. <p> Prefixes are equal only if their Unicode code points match exactly. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri-for-prefix" target="mlserverdoc">fn:namespace-uri-for-prefix</a>
    * @param prefix  A namespace prefix to look up.
    * @param element  An element node providing namespace context.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr namespaceUriForPrefix(XsStringExpr prefix, ElementNodeExpr element);
    /**
    * Returns the namespace URI for arg as an <code>xs:string</code>. If arg is the empty sequence, the empty sequence is returned. If arg is in no namespace, the zero-length string is returned.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri-from-QName" target="mlserverdoc">fn:namespace-uri-from-QName</a>
    * @param arg  A qualified name.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr namespaceUriFromQName(XsQNameExpr arg);
    /**
    *  Summary: Returns an xs:boolean indicating whether the argument node is "nilled". If the argument is not an element node, returns the empty sequence. If the argument is the empty sequence, returns the empty sequence. For element nodes, true() is returned if the element is nilled, otherwise false(). <p> Elements may be defined in a schema as nillable, which allows an empty instance of an element to a appear in a document even though its type requires that it always have some content. Nilled elements should always be empty but an element is not considered nilled just because it's empty. It must also have the type annotation attribute xsi:nil="true". 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:nilled" target="mlserverdoc">fn:nilled</a>
    * @param arg  The node to test for nilled status.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr nilled(NodeExpr arg);
    /**
    * Returns an expanded-QName for node kinds that can have names. For other kinds of nodes it returns the empty sequence. If arg is the empty sequence, the empty sequence is returned.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:node-name" target="mlserverdoc">fn:node-name</a>
    * @param arg  The node whose name is to be returned.
    * @return  a XsQNameExpr expression
    */
    public XsQNameExpr nodeName(NodeExpr arg);
    /**
    * Returns the specified string with normalized whitespace, which strips off any leading or trailing whitespace and replaces any other sequences of more than one whitespace characters with a single space character (#x20).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:normalize-space" target="mlserverdoc">fn:normalize-space</a>
    * @param input  The string from which to normalize whitespace.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr normalizeSpace(XsStringExpr input);
    /**
    * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a>
    * @param arg  The string to normalize.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr normalizeUnicode(XsStringExpr arg);
    /**
    * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a>
    * @param arg  The string to normalize.
    * @param normalizationForm  The form under which to normalize the specified string: NFC, NFD, NFKC, or NFKD.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr normalizeUnicode(XsStringExpr arg, String normalizationForm);
    /**
    * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a>
    * @param arg  The string to normalize.
    * @param normalizationForm  The form under which to normalize the specified string: NFC, NFD, NFKC, or NFKD.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr normalizeUnicode(XsStringExpr arg, XsStringExpr normalizationForm);
    /**
    * Returns <code>true</code> if the effective boolean value is <code>false</code>, and <code>false</code> if the effective boolean value is <code>true</code>. The <code>arg</code> parameter is first reduced to an effective boolean value by applying the <code>fn:boolean</code> function.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:not" target="mlserverdoc">fn:not</a>
    * @param arg  The expression to negate.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr not(ItemSeqExpr arg);
    /**
    *  Returns the value indicated by arg or, if arg is not specified, the context item after atomization, converted to an xs:double. If arg is the empty sequence or if arg or the context item cannot be converted to an xs:double, the xs:double value NaN is returned. If the context item is undefined an error is raised: [err:XPDY0002]. <p> Calling the zero-argument version of the function is defined to give the same result as calling the single-argument version with an argument of ".". That is, fn:number() is equivalent to fn:number(.). <p> If arg is the empty sequence, NaN is returned. Otherwise, arg, or the context item after atomization, is converted to an xs:double following the rules of 17.1.3.2 Casting to xs:double. If the conversion to xs:double fails, the xs:double value NaN is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:number" target="mlserverdoc">fn:number</a>
    * @param arg  The value to be returned as an xs:double value.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr number(XsAnyAtomicTypeExpr arg);
    /**
    * Returns an <code>xs:NCName</code> representing the prefix of arg. The empty sequence is returned if arg is the empty sequence or if the value of arg contains no prefix.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:prefix-from-QName" target="mlserverdoc">fn:prefix-from-QName</a>
    * @param arg  A qualified name.
    * @return  a XsNCNameExpr expression
    */
    public XsNCNameExpr prefixFromQName(XsQNameExpr arg);
    /**
    *  Returns an <code>xs:QName</code> with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a>
    * @param paramURI  A namespace URI, as a string.
    * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".
    * @return  a XsQNameExpr expression
    */
    public XsQNameExpr QName(XsStringExpr paramURI, String paramQName);
    /**
    *  Returns an <code>xs:QName</code> with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a>
    * @param paramURI  A namespace URI, as a string.
    * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".
    * @return  a XsQNameExpr expression
    */
    public XsQNameExpr QName(XsStringExpr paramURI, XsStringExpr paramQName);
    /**
    *  Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed. <p> If position is less than 1 or greater than the number of items in target, target is returned. Otherwise, the value returned by the function consists of all items of target whose index is less than position, followed by all items of target whose index is greater than position. If target is the empty sequence, the empty sequence is returned. <p> For detailed type semantics, see <a>Section 7.2.11 The fn:remove function[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:remove" target="mlserverdoc">fn:remove</a>
    * @param target  The sequence of items from which items will be removed.
    * @param position  The position in the target sequence from which the items will be removed.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr remove(ItemSeqExpr target, long position);
    /**
    *  Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed. <p> If position is less than 1 or greater than the number of items in target, target is returned. Otherwise, the value returned by the function consists of all items of target whose index is less than position, followed by all items of target whose index is greater than position. If target is the empty sequence, the empty sequence is returned. <p> For detailed type semantics, see <a>Section 7.2.11 The fn:remove function[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:remove" target="mlserverdoc">fn:remove</a>
    * @param target  The sequence of items from which items will be removed.
    * @param position  The position in the target sequence from which the items will be removed.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr remove(ItemSeqExpr target, XsIntegerExpr position);
    /**
    * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
    * @param input  The string to start with.
    * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
    * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see <a>http://www.w3.org/TR/xpath-functions/#func-replace</a>).
    * @return  a XsStringExpr expression
    */
    public XsStringExpr replace(XsStringExpr input, String pattern, String replacement);
    /**
    * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
    * @param input  The string to start with.
    * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
    * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see <a>http://www.w3.org/TR/xpath-functions/#func-replace</a>).
    * @return  a XsStringExpr expression
    */
    public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement);
    /**
    * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
    * @param input  The string to start with.
    * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
    * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see <a>http://www.w3.org/TR/xpath-functions/#func-replace</a>).
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr replace(XsStringExpr input, String pattern, String replacement, String flags);
    /**
    * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
    * @param input  The string to start with.
    * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
    * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see <a>http://www.w3.org/TR/xpath-functions/#func-replace</a>).
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement, XsStringExpr flags);
    /**
    *  Returns an <code>xs:QName</code> value (that is, an expanded QName) by taking an <code>xs:string</code> that has the lexical form of an <code>xs:QName</code> (a string in the form "prefix:local-name" or "local-name") and resolving it using the in-scope namespaces for a given element. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:resolve-QName" target="mlserverdoc">fn:resolve-QName</a>
    * @param qname  A string of the form "prefix:local-name".
    * @param element  An element providing the in-scope namespaces to use to resolve the qualified name.
    * @return  a XsQNameExpr expression
    */
    public XsQNameExpr resolveQName(XsStringExpr qname, ElementNodeExpr element);
    /**
    * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:resolve-uri" target="mlserverdoc">fn:resolve-uri</a>
    * @param relative  A URI reference to resolve against the base.
    * @param base  An absolute URI to use as the base of the resolution.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr resolveUri(XsStringExpr relative, String base);
    /**
    * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:resolve-uri" target="mlserverdoc">fn:resolve-uri</a>
    * @param relative  A URI reference to resolve against the base.
    * @param base  An absolute URI to use as the base of the resolution.
    * @return  a XsAnyURIExpr expression
    */
    public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base);
    /**
    * Reverses the order of items in a sequence. If $arg is the empty sequence, the empty sequence is returned. <p> For detailed type semantics, see <a>Section 7.2.12 The fn:reverse function[FS]</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:reverse" target="mlserverdoc">fn:reverse</a>
    * @param target  The sequence of items to be reversed.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr reverse(ItemSeqExpr target);
    /**
    * Returns the root of the tree to which arg belongs. This will usually, but not necessarily, be a document node. <p> If arg is the empty sequence, the empty sequence is returned. <p> If arg is a document node, arg is returned. <p> If the function is called without an argument, the context item is used as the default argument. If the context item is undefined an error is raised: [err:XPDY0002]. If the context item is not a node an error is raised: [err:XPTY0004]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:root" target="mlserverdoc">fn:root</a>
    * @param arg  The node whose root node will be returned.
    * @return  a NodeExpr expression
    */
    public NodeExpr root(NodeExpr arg);
    /**
    *  Returns the number with no fractional part that is closest to the argument. If there are two such numbers, then the one that is closest to positive infinity is returned. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive infinity, then positive infinity is returned. If the argument is negative infinity, then negative infinity is returned. If the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. If the argument is less than zero, but greater than or equal to -0.5, then negative zero is returned. In the cases where positive zero or negative zero is returned, negative zero or positive zero may be returned as [XML Schema Part 2: Datatypes Second Edition] does not distinguish between the values positive zero and negative zero. <p> For the last two cases, note that the result is not the same as fn:floor(x+0.5). <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round" target="mlserverdoc">fn:round</a>
    * @param arg  A numeric value to round.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr round(XsNumericExpr arg);
    /**
    *  The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> If no precision is specified, the result produces is the same as with precision=0. <p> For arguments of type xs:float and xs:double, if the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. If the argument is less than zero, but greater than or equal o -0.5, then negative zero is returned. <p> If arg is of type xs:float or xs:double, rounding occurs on the value of the mantissa computed with exponent = 0. <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
    * @param arg  A numeric value to round.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg);
    /**
    *  The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> If no precision is specified, the result produces is the same as with precision=0. <p> For arguments of type xs:float and xs:double, if the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. If the argument is less than zero, but greater than or equal o -0.5, then negative zero is returned. <p> If arg is of type xs:float or xs:double, rounding occurs on the value of the mantissa computed with exponent = 0. <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
    * @param arg  A numeric value to round.
    * @param precision  The precision to which to round the value.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg, long precision);
    /**
    *  The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> If no precision is specified, the result produces is the same as with precision=0. <p> For arguments of type xs:float and xs:double, if the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. If the argument is less than zero, but greater than or equal o -0.5, then negative zero is returned. <p> If arg is of type xs:float or xs:double, rounding occurs on the value of the mantissa computed with exponent = 0. <p> For detailed type semantics, see Section 7.2.3 The fn:abs, fn:ceiling, fn:floor, fn:round, and fn:round-half-to-even functions[FS]. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
    * @param arg  A numeric value to round.
    * @param precision  The precision to which to round the value.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg, XsIntegerExpr precision);
    /**
    *  Returns an xs:decimal value between 0 and 60.999..., both inclusive representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-dateTime" target="mlserverdoc">fn:seconds-from-dateTime</a>
    * @param arg  The dateTime whose seconds component will be returned.
    * @return  a XsDecimalExpr expression
    */
    public XsDecimalExpr secondsFromDateTime(XsDateTimeExpr arg);
    /**
    * Returns an xs:decimal representing the seconds component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-duration" target="mlserverdoc">fn:seconds-from-duration</a>
    * @param arg  The duration whose minute component will be returned.
    * @return  a XsDecimalExpr expression
    */
    public XsDecimalExpr secondsFromDuration(XsDurationExpr arg);
    /**
    *  Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-time" target="mlserverdoc">fn:seconds-from-time</a>
    * @param arg  The time whose seconds component will be returned.
    * @return  a XsDecimalExpr expression
    */
    public XsDecimalExpr secondsFromTime(XsTimeExpr arg);
    /**
    * Returns <code>true</code> if the first parameter starts with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test whether it is at the beginning of the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2);
    /**
    * Returns <code>true</code> if the first parameter starts with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test whether it is at the beginning of the first parameter.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2);
    /**
    * Returns <code>true</code> if the first parameter starts with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test whether it is at the beginning of the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2, String collation);
    /**
    * Returns <code>true</code> if the first parameter starts with the string from the second parameter, otherwise returns <code>false</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
    * @param parameter1  The string from which to test.
    * @param parameter2  The string to test whether it is at the beginning of the first parameter.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsBooleanExpr expression
    */
    public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation);
    /**
    * Returns the value of arg represented as an <code>xs:string</code>. If no argument is supplied, this function returns the string value of the context item (.).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string" target="mlserverdoc">fn:string</a>
    * @param arg  The item to be rendered as a string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr string(ItemExpr arg);
    /**
    *  Returns an <code>xs:string</code> created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator. <p> If the value of parameter1 is the empty sequence, the zero-length string is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-join" target="mlserverdoc">fn:string-join</a>
    * @param parameter1  A sequence of strings.
    * @param parameter2  A separator string to concatenate between the items in $parameter1.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr stringJoin(XsStringSeqExpr parameter1, String parameter2);
    /**
    *  Returns an <code>xs:string</code> created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator. <p> If the value of parameter1 is the empty sequence, the zero-length string is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-join" target="mlserverdoc">fn:string-join</a>
    * @param parameter1  A sequence of strings.
    * @param parameter2  A separator string to concatenate between the items in $parameter1.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr stringJoin(XsStringSeqExpr parameter1, XsStringExpr parameter2);
    /**
    * Returns an integer representing the length of the specified string. The length is 1-based, so a string that is one character long returns a value of 1.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-length" target="mlserverdoc">fn:string-length</a>
    * @param sourceString  The string to calculate the length.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr stringLength(XsStringExpr sourceString);
    /**
    *  Returns the sequence of Unicode code points that constitute an xs:string. If arg is a zero-length string or the empty sequence, the empty sequence is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-to-codepoints" target="mlserverdoc">fn:string-to-codepoints</a>
    * @param arg  A string.
    * @return  a XsIntegerSeqExpr expression sequence
    */
    public XsIntegerSeqExpr stringToCodepoints(XsStringExpr arg);
    /**
    *  Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. <p> In the two-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p] <p> In the three-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p and $p lt fn:round(startingLoc) + fn:round(length)] <p> Notes: <p> If sourceSeq is the empty sequence, the empty sequence is returned. <p> If startingLoc is zero or negative, the subsequence includes items from the beginning of the sourceSeq. <p> If length is not specified, the subsequence includes items to the end of sourceSeq. <p> If length is greater than the number of items in the value of sourceSeq following startingLoc, the subsequence includes items to the end of sourceSeq. <p> The first item of a sequence is located at position 1, not position 0. <p> For detailed type semantics, see Section 7.2.13 The fn:subsequence functionFS. <p> The reason the function accepts arguments of type xs:double is that many computations on untyped data return an xs:double result; and the reason for the rounding rules is to compensate for any imprecision in these floating-point computations. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
    * @param sourceSeq  The sequence of items from which a subsequence will be selected.
    * @param startingLoc  The starting position of the start of the subsequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc);
    /**
    *  Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. <p> In the two-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p] <p> In the three-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p and $p lt fn:round(startingLoc) + fn:round(length)] <p> Notes: <p> If sourceSeq is the empty sequence, the empty sequence is returned. <p> If startingLoc is zero or negative, the subsequence includes items from the beginning of the sourceSeq. <p> If length is not specified, the subsequence includes items to the end of sourceSeq. <p> If length is greater than the number of items in the value of sourceSeq following startingLoc, the subsequence includes items to the end of sourceSeq. <p> The first item of a sequence is located at position 1, not position 0. <p> For detailed type semantics, see Section 7.2.13 The fn:subsequence functionFS. <p> The reason the function accepts arguments of type xs:double is that many computations on untyped data return an xs:double result; and the reason for the rounding rules is to compensate for any imprecision in these floating-point computations. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
    * @param sourceSeq  The sequence of items from which a subsequence will be selected.
    * @param startingLoc  The starting position of the start of the subsequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc);
    /**
    *  Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. <p> In the two-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p] <p> In the three-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p and $p lt fn:round(startingLoc) + fn:round(length)] <p> Notes: <p> If sourceSeq is the empty sequence, the empty sequence is returned. <p> If startingLoc is zero or negative, the subsequence includes items from the beginning of the sourceSeq. <p> If length is not specified, the subsequence includes items to the end of sourceSeq. <p> If length is greater than the number of items in the value of sourceSeq following startingLoc, the subsequence includes items to the end of sourceSeq. <p> The first item of a sequence is located at position 1, not position 0. <p> For detailed type semantics, see Section 7.2.13 The fn:subsequence functionFS. <p> The reason the function accepts arguments of type xs:double is that many computations on untyped data return an xs:double result; and the reason for the rounding rules is to compensate for any imprecision in these floating-point computations. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
    * @param sourceSeq  The sequence of items from which a subsequence will be selected.
    * @param startingLoc  The starting position of the start of the subsequence.
    * @param length  The length of the subsequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc, double length);
    /**
    *  Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. <p> In the two-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p] <p> In the three-argument case, returns: <p> sourceSeq[fn:round(startingLoc) le $p and $p lt fn:round(startingLoc) + fn:round(length)] <p> Notes: <p> If sourceSeq is the empty sequence, the empty sequence is returned. <p> If startingLoc is zero or negative, the subsequence includes items from the beginning of the sourceSeq. <p> If length is not specified, the subsequence includes items to the end of sourceSeq. <p> If length is greater than the number of items in the value of sourceSeq following startingLoc, the subsequence includes items to the end of sourceSeq. <p> The first item of a sequence is located at position 1, not position 0. <p> For detailed type semantics, see Section 7.2.13 The fn:subsequence functionFS. <p> The reason the function accepts arguments of type xs:double is that many computations on untyped data return an xs:double result; and the reason for the rounding rules is to compensate for any imprecision in these floating-point computations. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
    * @param sourceSeq  The sequence of items from which a subsequence will be selected.
    * @param startingLoc  The starting position of the start of the subsequence.
    * @param length  The length of the subsequence.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc, XsNumericExpr length);
    /**
    * Returns a substring starting from the startingLoc and continuing for length characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a>
    * @param sourceString  The string from which to create a substring.
    * @param startingLoc  The number of characters from the start of the $sourceString.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substring(XsStringExpr sourceString, double startingLoc);
    /**
    * Returns a substring starting from the startingLoc and continuing for length characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a>
    * @param sourceString  The string from which to create a substring.
    * @param startingLoc  The number of characters from the start of the $sourceString.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc);
    /**
    * Returns a substring starting from the startingLoc and continuing for length characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a>
    * @param sourceString  The string from which to create a substring.
    * @param startingLoc  The number of characters from the start of the $sourceString.
    * @param length  The number of characters beyond the $startingLoc.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substring(XsStringExpr sourceString, double startingLoc, double length);
    /**
    * Returns a substring starting from the startingLoc and continuing for length characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a>
    * @param sourceString  The string from which to create a substring.
    * @param startingLoc  The number of characters from the start of the $sourceString.
    * @param length  The number of characters beyond the $startingLoc.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc, XsNumericExpr length);
    /**
    * Returns the substring created by taking all of the input characters that occur after the specified after characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a>
    * @param input  The string from which to create the substring.
    * @param after  The string after which the substring is created.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringAfter(XsStringExpr input, String after);
    /**
    * Returns the substring created by taking all of the input characters that occur after the specified after characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a>
    * @param input  The string from which to create the substring.
    * @param after  The string after which the substring is created.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after);
    /**
    * Returns the substring created by taking all of the input characters that occur after the specified after characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a>
    * @param input  The string from which to create the substring.
    * @param after  The string after which the substring is created.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringAfter(XsStringExpr input, String after, String collation);
    /**
    * Returns the substring created by taking all of the input characters that occur after the specified after characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a>
    * @param input  The string from which to create the substring.
    * @param after  The string after which the substring is created.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after, XsStringExpr collation);
    /**
    * Returns the substring created by taking all of the input characters that occur before the specified before characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a>
    * @param input  The string from which to create the substring.
    * @param before  The string before which the substring is created.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringBefore(XsStringExpr input, String before);
    /**
    * Returns the substring created by taking all of the input characters that occur before the specified before characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a>
    * @param input  The string from which to create the substring.
    * @param before  The string before which the substring is created.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before);
    /**
    * Returns the substring created by taking all of the input characters that occur before the specified before characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a>
    * @param input  The string from which to create the substring.
    * @param before  The string before which the substring is created.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringBefore(XsStringExpr input, String before, String collation);
    /**
    * Returns the substring created by taking all of the input characters that occur before the specified before characters.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a>
    * @param input  The string from which to create the substring.
    * @param before  The string before which the substring is created.
    * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the <em>Search Developer's Guide</em>.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before, XsStringExpr collation);
    /**
    *  Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. <p> Any values of type xs:untypedAtomic in arg are cast to xs:double. The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence. <p> If the converted sequence is empty, then the single-argument form of the function returns the xs:integer value 0; the two-argument form returns the value of the argument zero. <p> If the converted sequence contains the value NaN, NaN is returned. <p> All items in arg must be numeric or derived from a single base type. In addition, the type must support addition. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. The sum of a sequence of integers will therefore be an integer, while the sum of a numeric sequence that includes at least one xs:double will be an xs:double. <p> If the above conditions are not met, a type error is raised [err:FORG0006]. <p> Otherwise, the result of the function, using the second signature, is the result of the expression: <pre> if (fn:count($c) eq 0) then zero else if (fn:count($c) eq 1) then $c[1] else $c[1] + fn:sum(subsequence($c, 2)) </pre> <p> where $c is the converted sequence. <p> The result of the function, using the first signature, is the result of the expression:fn:sum(arg, 0). <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> The second argument allows an appropriate value to be defined to represent the sum of an empty sequence. For example, when summing a sequence of durations it would be appropriate to return a zero-length duration of the appropriate type. This argument is necessary because a system that does dynamic typing cannot distinguish "an empty sequence of integers", for example, from "an empty sequence of durations". <p> If the converted sequence contains exactly one value then that value is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
    * @param arg  The sequence of values to be summed.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg);
    /**
    *  Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. <p> Any values of type xs:untypedAtomic in arg are cast to xs:double. The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence. <p> If the converted sequence is empty, then the single-argument form of the function returns the xs:integer value 0; the two-argument form returns the value of the argument zero. <p> If the converted sequence contains the value NaN, NaN is returned. <p> All items in arg must be numeric or derived from a single base type. In addition, the type must support addition. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. The sum of a sequence of integers will therefore be an integer, while the sum of a numeric sequence that includes at least one xs:double will be an xs:double. <p> If the above conditions are not met, a type error is raised [err:FORG0006]. <p> Otherwise, the result of the function, using the second signature, is the result of the expression: <pre> if (fn:count($c) eq 0) then zero else if (fn:count($c) eq 1) then $c[1] else $c[1] + fn:sum(subsequence($c, 2)) </pre> <p> where $c is the converted sequence. <p> The result of the function, using the first signature, is the result of the expression:fn:sum(arg, 0). <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> The second argument allows an appropriate value to be defined to represent the sum of an empty sequence. For example, when summing a sequence of durations it would be appropriate to return a zero-length duration of the appropriate type. This argument is necessary because a system that does dynamic typing cannot distinguish "an empty sequence of integers", for example, from "an empty sequence of durations". <p> If the converted sequence contains exactly one value then that value is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
    * @param arg  The sequence of values to be summed.
    * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, String zero);
    /**
    *  Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. <p> Any values of type xs:untypedAtomic in arg are cast to xs:double. The items in the resulting sequence may be reordered in an arbitrary order. The resulting sequence is referred to below as the converted sequence. <p> If the converted sequence is empty, then the single-argument form of the function returns the xs:integer value 0; the two-argument form returns the value of the argument zero. <p> If the converted sequence contains the value NaN, NaN is returned. <p> All items in arg must be numeric or derived from a single base type. In addition, the type must support addition. Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values. For numeric values, the numeric promotion rules defined in 6.2 Operators on Numeric Values are used to promote all values to a single common type. The sum of a sequence of integers will therefore be an integer, while the sum of a numeric sequence that includes at least one xs:double will be an xs:double. <p> If the above conditions are not met, a type error is raised [err:FORG0006]. <p> Otherwise, the result of the function, using the second signature, is the result of the expression: <pre> if (fn:count($c) eq 0) then zero else if (fn:count($c) eq 1) then $c[1] else $c[1] + fn:sum(subsequence($c, 2)) </pre> <p> where $c is the converted sequence. <p> The result of the function, using the first signature, is the result of the expression:fn:sum(arg, 0). <p> For detailed type semantics, see <a>Section 7.2.10 The fn:min, fn:max, fn:avg, and fn:sum functions[FS]</a>. <p> Notes: <p> The second argument allows an appropriate value to be defined to represent the sum of an empty sequence. For example, when summing a sequence of durations it would be appropriate to return a zero-length duration of the appropriate type. This argument is necessary because a system that does dynamic typing cannot distinguish "an empty sequence of integers", for example, from "an empty sequence of durations". <p> If the converted sequence contains exactly one value then that value is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
    * @param arg  The sequence of values to be summed.
    * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.
    * @return  a XsAnyAtomicTypeExpr expression
    */
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr zero);
    /**
    * Returns all but the first item in a sequence. For more details, see <a>XPath 3.0 Functions and Operators</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tail" target="mlserverdoc">fn:tail</a>
    * @param seq  The function value.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr tail(ItemSeqExpr seq);
    /**
    *  Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:timezone-from-date" target="mlserverdoc">fn:timezone-from-date</a>
    * @param arg  The date whose timezone component will be returned.
    * @return  a XsDayTimeDurationExpr expression
    */
    public XsDayTimeDurationExpr timezoneFromDate(XsDateExpr arg);
    /**
    *  Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:timezone-from-dateTime" target="mlserverdoc">fn:timezone-from-dateTime</a>
    * @param arg  The dateTime whose timezone component will be returned.
    * @return  a XsDayTimeDurationExpr expression
    */
    public XsDayTimeDurationExpr timezoneFromDateTime(XsDateTimeExpr arg);
    /**
    *  Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:timezone-from-time" target="mlserverdoc">fn:timezone-from-time</a>
    * @param arg  The time whose timezone component will be returned.
    * @return  a XsDayTimeDurationExpr expression
    */
    public XsDayTimeDurationExpr timezoneFromTime(XsTimeExpr arg);
    /**
    * Returns a sequence of strings contructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a>
    * @param input  The string to tokenize.
    * @param pattern  The regular expression pattern from which to separate the tokens.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr tokenize(XsStringExpr input, String pattern);
    /**
    * Returns a sequence of strings contructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a>
    * @param input  The string to tokenize.
    * @param pattern  The regular expression pattern from which to separate the tokens.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern);
    /**
    * Returns a sequence of strings contructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a>
    * @param input  The string to tokenize.
    * @param pattern  The regular expression pattern from which to separate the tokens.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr tokenize(XsStringExpr input, String pattern, String flags);
    /**
    * Returns a sequence of strings contructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a>
    * @param input  The string to tokenize.
    * @param pattern  The regular expression pattern from which to separate the tokens.
    * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in <a>http://www.w3.org/TR/xpath-functions/#flags</a>.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags);
    /**
    * Returns a string where every character in src that occurs in some position in the mapString is translated into the transString character in the corresponding location of the mapString character.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:translate" target="mlserverdoc">fn:translate</a>
    * @param src  The string to translate characters.
    * @param mapString  The string representing characters to be translated.
    * @param transString  The string representing the characters to which the $mapString characters are translated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr translate(XsStringExpr src, String mapString, String transString);
    /**
    * Returns a string where every character in src that occurs in some position in the mapString is translated into the transString character in the corresponding location of the mapString character.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:translate" target="mlserverdoc">fn:translate</a>
    * @param src  The string to translate characters.
    * @param mapString  The string representing characters to be translated.
    * @param transString  The string representing the characters to which the $mapString characters are translated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr translate(XsStringExpr src, XsStringExpr mapString, XsStringExpr transString);
    /**
    * Returns the <code>xs:boolean</code> value <code>true</code>. Equivalent to <code>xs:boolean("1")</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:true" target="mlserverdoc">fn:true</a>
    
    */
    public XsBooleanExpr trueExpr();
    /**
    *  Returns the items of sourceSeq in an implementation dependent order. <p> Note: <p> Query optimizers may be able to do a better job if the order of the output sequence is not specified. For example, when retrieving prices from a purchase order, if an index exists on prices, it may be more efficient to return the prices in index order rather than in document order. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:unordered" target="mlserverdoc">fn:unordered</a>
    * @param sourceSeq  The sequence of items.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr unordered(ItemSeqExpr sourceSeq);
    /**
    * Returns the specified string converting all of the characters to upper-case characters. If a character does not have a corresponding upper-case character, then the original character is returned. The upper-case characters are determined using the <a>Unicode Case Mappings</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:upper-case" target="mlserverdoc">fn:upper-case</a>
    * @param string  The string to upper-case.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr upperCase(XsStringExpr string);
    /**
    * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:year-from-date" target="mlserverdoc">fn:year-from-date</a>
    * @param arg  The date whose year component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr yearFromDate(XsDateExpr arg);
    /**
    * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:year-from-dateTime" target="mlserverdoc">fn:year-from-dateTime</a>
    * @param arg  The dateTime whose year component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr yearFromDateTime(XsDateTimeExpr arg);
    /**
    * Returns an xs:integer representing the years component in the canonical lexical representation of the value of arg. The result may be negative. <p> If arg is the empty sequence, returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:years-from-duration" target="mlserverdoc">fn:years-from-duration</a>
    * @param arg  The duration whose year component will be returned.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr yearsFromDuration(XsDurationExpr arg);
}
