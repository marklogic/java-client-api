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
  * Returns the absolute value of arg. If arg is negative returns -arg otherwise returns arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:abs" target="mlserverdoc">fn:abs</a>
  * @param arg  A numeric value.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr abs(XsNumericExpr arg);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
  * @param arg  The date to adjust to the new timezone.
  * @return  a XsDateExpr expression
  */
  public XsDateExpr adjustDateToTimezone(XsDateExpr arg);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
  * @param arg  The date to adjust to the new timezone.
  * @param timezone  The new timezone for the date.
  * @return  a XsDateExpr expression
  */
  public XsDateExpr adjustDateToTimezone(XsDateExpr arg, String timezone);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a>
  * @param arg  The date to adjust to the new timezone.
  * @param timezone  The new timezone for the date.
  * @return  a XsDateExpr expression
  */
  public XsDateExpr adjustDateToTimezone(XsDateExpr arg, XsDayTimeDurationExpr timezone);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
  * @param arg  The dateTime to adjust to the new timezone.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
  * @param arg  The dateTime to adjust to the new timezone.
  * @param timezone  The new timezone for the dateTime.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, String timezone);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a>
  * @param arg  The dateTime to adjust to the new timezone.
  * @param timezone  The new timezone for the dateTime.
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, XsDayTimeDurationExpr timezone);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a>
  * @param arg  The time to adjust to the new timezone.
  * @return  a XsTimeExpr expression
  */
  public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a>
  * @param arg  The time to adjust to the new timezone.
  * @param timezone  The new timezone for the date.
  * @return  a XsTimeExpr expression
  */
  public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, String timezone);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone. 
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
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a ElementNodeExpr expression
  */
  public ElementNodeExpr analyzeString(String in, String regex, String flags);
/**
  * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a>
  * @param in  The string to start with.
  * @param regex  The regular expression pattern to match.
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a ElementNodeExpr expression
  */
  public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex, XsStringExpr flags);
/**
  * Returns the average of the values in the input sequence arg, that is, the sum of the values divided by the number of values. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:avg" target="mlserverdoc">fn:avg</a>
  * @param arg  The sequence of values to average.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr avg(XsAnyAtomicTypeSeqExpr arg);
/**
  * Returns the value of the base-uri property for the specified node. If the node is part of a document and does not have a base-uri attribute explicitly set, fn:base-uri typically returns the URI of the document in which the node resides.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:base-uri" target="mlserverdoc">fn:base-uri</a>
  * @param arg  The node whose base-uri is to be returned.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr baseUri(NodeExpr arg);
/**
  * Computes the effective boolean value of the sequence arg. See Section 2.4.3 Effective Boolean Value[XP]. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:boolean" target="mlserverdoc">fn:boolean</a>
  * @param arg  A sequence of items.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr booleanExpr(ItemSeqExpr arg);
/**
  * Returns the smallest (closest to negative infinity) number with no fractional part that is not less than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ceiling" target="mlserverdoc">fn:ceiling</a>
  * @param arg  A numeric value.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr ceiling(XsNumericExpr arg);
/**
  * Returns true if the specified parameters are the same Unicode code point, otherwise returns false. The codepoints are compared according to the Unicode code point collation (http://www.w3.org/2005/xpath-functions/collation/codepoint).  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr codepointEqual(XsStringExpr comparand1, String comparand2);
/**
  * Returns true if the specified parameters are the same Unicode code point, otherwise returns false. The codepoints are compared according to the Unicode code point collation (http://www.w3.org/2005/xpath-functions/collation/codepoint).  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr codepointEqual(XsStringExpr comparand1, XsStringExpr comparand2);
/**
  * Creates an xs:string from a sequence of Unicode code points. Returns the zero-length string if arg is the empty sequence. If any of the code points in arg is not a legal XML character, an error is raised.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:codepoints-to-string" target="mlserverdoc">fn:codepoints-to-string</a>
  * @param arg  A sequence of Unicode code points.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr codepointsToString(XsIntegerSeqExpr arg);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2, String collation);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a>
  * @param comparand1  A string to be compared.
  * @param comparand2  A string to be compared.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2, XsStringExpr collation);
/**
  * Returns the xs:string that is the concatenation of the values of the specified parameters. Accepts two or more xs:anyAtomicType arguments and casts them to xs:string. If any of the parameters is the empty sequence, the parameter is treated as the zero-length string. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:concat" target="mlserverdoc">fn:concat</a>
  * @param parameter1  A value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr concat(XsAnyAtomicTypeExpr... parameter1);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test for existence in the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test for existence in the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test for existence in the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test for existence in the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation);
/**
  * Returns the number of items in the value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
  * @param arg  The sequence of items to count.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr count(ItemSeqExpr arg);
/**
  * Returns the number of items in the value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
  * @param arg  The sequence of items to count.
  * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard fn:count function.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr count(ItemSeqExpr arg, double maximum);
/**
  * Returns the number of items in the value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a>
  * @param arg  The sequence of items to count.
  * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard fn:count function.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr count(ItemSeqExpr arg, XsDoubleExpr maximum);
/**
  * Returns xs:date(fn:current-dateTime()). This is an xs:date (with timezone) that is current at some time during the evaluation of a query or transformation in which fn:current-date() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-date() is *implementation dependent*.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-date" target="mlserverdoc">fn:current-date</a>
  * @return  a XsDateExpr expression
  */
  public XsDateExpr currentDate();
/**
  * Returns the current dateTime value (with timezone) from the dynamic context. (See Section C.2 Dynamic Context Components[XP].) This is an xs:dateTime that is current at some time during the evaluation of a query or transformation in which fn:current-dateTime() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-dateTime() is *implementation dependent*.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-dateTime" target="mlserverdoc">fn:current-dateTime</a>
  * @return  a XsDateTimeExpr expression
  */
  public XsDateTimeExpr currentDateTime();
/**
  * Returns xs:time(fn:current-dateTime()). This is an xs:time (with timezone) that is current at some time during the evaluation of a query or transformation in which fn:current-time() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-time() is *implementation dependent*.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:current-time" target="mlserverdoc">fn:current-time</a>
  * @return  a XsTimeExpr expression
  */
  public XsTimeExpr currentTime();
/**
  * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:day-from-date" target="mlserverdoc">fn:day-from-date</a>
  * @param arg  The date whose day component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr dayFromDate(XsDateExpr arg);
/**
  * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:day-from-dateTime" target="mlserverdoc">fn:day-from-dateTime</a>
  * @param arg  The dateTime whose day component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr dayFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:integer representing the days component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:days-from-duration" target="mlserverdoc">fn:days-from-duration</a>
  * @param arg  The duration whose day component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr daysFromDuration(XsDurationExpr arg);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, String collation);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a>
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, XsStringExpr collation);
/**
  * Returns the value of the default collation property from the static context. Components of the static context are discussed in Section C.1 Static Context Components[XP].  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:default-collation" target="mlserverdoc">fn:default-collation</a>
  * @return  a XsStringExpr expression
  */
  public XsStringExpr defaultCollation();
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
  * @param arg  A sequence of items.
  * @return  a XsAnyAtomicTypeSeqExpr expression sequence
  */
  public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg);
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
  * @param arg  A sequence of items.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeSeqExpr expression sequence
  */
  public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, String collation);
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a>
  * @param arg  A sequence of items.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeSeqExpr expression sequence
  */
  public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
/**
  * Returns the value of the document-uri property for the specified node. If the node is a document node, then the value returned is the URI of the document. If the node is not a document node, then fn:document-uri returns the empty sequence.
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
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
  * @param parameter1  The parameter from which to test.
  * @param parameter2  The string to test whether it is at the end of the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
  * @param parameter1  The parameter from which to test.
  * @param parameter2  The string to test whether it is at the end of the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
  * @param parameter1  The parameter from which to test.
  * @param parameter2  The string to test whether it is at the end of the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a>
  * @param parameter1  The parameter from which to test.
  * @param parameter2  The string to test whether it is at the end of the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
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
  * Returns the xs:boolean value false. Equivalent to xs:boolean("0").
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:false" target="mlserverdoc">fn:false</a>
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr falseExpr();
/**
  * Returns the largest (closest to positive infinity) number with no fractional part that is not greater than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
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
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, String picture);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, String picture, String language);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a>
  * @param value  The given date $value that needs to be formatted.
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the date $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a>
  * @param value  The given dateTime $value that needs to be formatted.
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the dateTime $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.
  * @param decimalFormatName  Represents a named  instruction. It is used to assign values to the variables mentioned above based on the picture string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String decimalFormatName);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a>
  * @param value  The given numeric $value that needs to be formatted.
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.
  * @param decimalFormatName  Represents a named  instruction. It is used to assign values to the variables mentioned above based on the picture string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr decimalFormatName);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, String picture);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, String picture, String language);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a>
  * @param value  The given time $value that needs to be formatted.
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.
  * @param language  The desired language for string representation of the time $value.
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".
  * @param country  $country is used the specification to take into account country specific string representation.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country);
/**
  * Returns a string that uniquely identifies a given node.  
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:generate-id" target="mlserverdoc">fn:generate-id</a>
  * @param node  The node whose ID will be generated.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr generateId(NodeExpr node);
/**
  * Returns the first item in a sequence. For more details, see XPath 3.0 Functions and Operators.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:head" target="mlserverdoc">fn:head</a>
  * @param seq  A sequence of items.
  * @return  a ItemExpr expression
  */
  public ItemExpr head(ItemSeqExpr seq);
/**
  * Returns an xs:integer between 0 and 23, both inclusive, representing the hours component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-dateTime" target="mlserverdoc">fn:hours-from-dateTime</a>
  * @param arg  The dateTime whose hours component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr hoursFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:integer representing the hours component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-duration" target="mlserverdoc">fn:hours-from-duration</a>
  * @param arg  The duration whose hour component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr hoursFromDuration(XsDurationExpr arg);
/**
  * Returns an xs:integer between 0 and 23, both inclusive, representing the value of the hours component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:hours-from-time" target="mlserverdoc">fn:hours-from-time</a>
  * @param arg  The time whose hours component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr hoursFromTime(XsTimeExpr arg);
/**
  * Returns the value of the implicit timezone property from the dynamic context. Components of the dynamic context are discussed in Section C.2 Dynamic Context Components[XP].
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:implicit-timezone" target="mlserverdoc">fn:implicit-timezone</a>
  * @return  a XsDayTimeDurationExpr expression
  */
  public XsDayTimeDurationExpr implicitTimezone();
/**
  * Returns the prefixes of the in-scope namespaces for element. For namespaces that have a prefix, it returns the prefix as an xs:NCName. For the default namespace, which has no prefix, it returns the zero-length string. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:in-scope-prefixes" target="mlserverdoc">fn:in-scope-prefixes</a>
  * @param element  The element whose in-scope prefixes will be returned.
  * @return  a XsStringSeqExpr expression sequence
  */
  public XsStringSeqExpr inScopePrefixes(ElementNodeExpr element);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
  * @param seqParam  A sequence of values.
  * @param srchParam  A value to find on the list.
  * @return  a XsIntegerSeqExpr expression sequence
  */
  public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
  * @param seqParam  A sequence of values.
  * @param srchParam  A value to find on the list.
  * @return  a XsIntegerSeqExpr expression sequence
  */
  public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
  * @param seqParam  A sequence of values.
  * @param srchParam  A value to find on the list.
  * @param collationLiteral  A collation identifier.
  * @return  a XsIntegerSeqExpr expression sequence
  */
  public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam, String collationLiteral);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a>
  * @param seqParam  A sequence of values.
  * @param srchParam  A value to find on the list.
  * @param collationLiteral  A collation identifier.
  * @return  a XsIntegerSeqExpr expression sequence
  */
  public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam, XsStringExpr collationLiteral);
/**
  * Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.) 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:insert-before" target="mlserverdoc">fn:insert-before</a>
  * @param target  The sequence of items into which new items will be inserted.
  * @param position  The position in the target sequence at which the new items will be added.
  * @param inserts  The items to insert into the target sequence.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr insertBefore(ItemSeqExpr target, long position, ItemSeqExpr inserts);
/**
  * Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.) 
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
  * This function tests whether the language of node, or the context node if the second argument is omitted, as specified by xml:lang attributes is the same as, or is a sublanguage of, the language specified by testlang. The language of the argument node, or the context node if the second argument is omitted, is determined by the value of the xml:lang attribute on the node, or, if the node has no such attribute, by the value of the xml:lang attribute on the nearest ancestor of the node that has an xml:lang attribute. If there is no such ancestor, then the function returns false 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:lang" target="mlserverdoc">fn:lang</a>
  * @param testlang  The language against which to test the node.
  * @param node  The node to test.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr lang(XsStringExpr testlang, NodeExpr node);
/**
  * Returns the local part of the name of arg as an xs:string that will either be the zero-length string or will have the lexical form of an xs:NCName. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:local-name" target="mlserverdoc">fn:local-name</a>
  * @param arg  The node whose local name is to be returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr localName(NodeExpr arg);
/**
  * Returns an xs:NCName representing the local part of arg. If arg is the empty sequence, returns the empty sequence.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:local-name-from-QName" target="mlserverdoc">fn:local-name-from-QName</a>
  * @param arg  A qualified name.
  * @return  a XsNCNameExpr expression
  */
  public XsNCNameExpr localNameFromQName(XsQNameExpr arg);
/**
  * Returns the specified string converting all of the characters to lower-case characters. If a character does not have a corresponding lower-case character, then the original character is returned. The lower-case characters are determined using the Unicode Case Mappings.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:lower-case" target="mlserverdoc">fn:lower-case</a>
  * @param string  The string to convert.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr lowerCase(XsStringExpr string);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
  * @param input  The input from which to match.
  * @param pattern  The regular expression to match.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr matches(XsStringExpr input, String pattern);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
  * @param input  The input from which to match.
  * @param pattern  The regular expression to match.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
  * @param input  The input from which to match.
  * @param pattern  The regular expression to match.
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr matches(XsStringExpr input, String pattern, String flags);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a>
  * @param input  The input from which to match.
  * @param pattern  The regular expression to match.
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
  * @param arg  The sequence of values whose maximum will be returned.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
  * @param arg  The sequence of values whose maximum will be returned.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, String collation);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a>
  * @param arg  The sequence of values whose maximum will be returned.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
  * @param arg  The sequence of values whose minimum will be returned.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
  * @param arg  The sequence of values whose minimum will be returned.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, String collation);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a>
  * @param arg  The sequence of values whose minimum will be returned.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation);
/**
  * Returns an xs:integer value between 0 and 59, both inclusive, representing the minute component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-dateTime" target="mlserverdoc">fn:minutes-from-dateTime</a>
  * @param arg  The dateTime whose minutes component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr minutesFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:integer representing the minutes component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-duration" target="mlserverdoc">fn:minutes-from-duration</a>
  * @param arg  The duration whose minute component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr minutesFromDuration(XsDurationExpr arg);
/**
  * Returns an xs:integer value between 0 to 59, both inclusive, representing the value of the minutes component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:minutes-from-time" target="mlserverdoc">fn:minutes-from-time</a>
  * @param arg  The time whose minutes component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr minutesFromTime(XsTimeExpr arg);
/**
  * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:month-from-date" target="mlserverdoc">fn:month-from-date</a>
  * @param arg  The date whose month component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr monthFromDate(XsDateExpr arg);
/**
  * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:month-from-dateTime" target="mlserverdoc">fn:month-from-dateTime</a>
  * @param arg  The dateTime whose month component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr monthFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:integer representing the months component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:months-from-duration" target="mlserverdoc">fn:months-from-duration</a>
  * @param arg  The duration whose month component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr monthsFromDuration(XsDurationExpr arg);
/**
  * Returns the name of a node, as an xs:string that is either the zero-length string, or has the lexical form of an xs:QName. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:name" target="mlserverdoc">fn:name</a>
  * @param arg  The node whose name is to be returned.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr name(NodeExpr arg);
/**
  * Returns the namespace URI of the xs:QName of the node specified by arg. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri" target="mlserverdoc">fn:namespace-uri</a>
  * @param arg  The node whose namespace URI is to be returned.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr namespaceUri(NodeExpr arg);
/**
  * Returns the namespace URI of one of the in-scope namespaces for element, identified by its namespace prefix. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri-for-prefix" target="mlserverdoc">fn:namespace-uri-for-prefix</a>
  * @param prefix  A namespace prefix to look up.
  * @param element  An element node providing namespace context.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr namespaceUriForPrefix(XsStringExpr prefix, ElementNodeExpr element);
/**
  * Returns the namespace URI for arg as an xs:string. If arg is the empty sequence, the empty sequence is returned. If arg is in no namespace, the zero-length string is returned.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:namespace-uri-from-QName" target="mlserverdoc">fn:namespace-uri-from-QName</a>
  * @param arg  A qualified name.
  * @return  a XsAnyURIExpr expression
  */
  public XsAnyURIExpr namespaceUriFromQName(XsQNameExpr arg);
/**
  * Summary: Returns an xs:boolean indicating whether the argument node is "nilled". If the argument is not an element node, returns the empty sequence. If the argument is the empty sequence, returns the empty sequence. For element nodes, true() is returned if the element is nilled, otherwise false(). 
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
  * Returns true if the effective boolean value is false, and false if the effective boolean value is true. The arg parameter is first reduced to an effective boolean value by applying the fn:boolean function.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:not" target="mlserverdoc">fn:not</a>
  * @param arg  The expression to negate.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr not(ItemSeqExpr arg);
/**
  * Returns the value indicated by arg or, if arg is not specified, the context item after atomization, converted to an xs:double. If arg is the empty sequence or if arg or the context item cannot be converted to an xs:double, the xs:double value NaN is returned. If the context item is undefined an error is raised: [err:XPDY0002]. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:number" target="mlserverdoc">fn:number</a>
  * @param arg  The value to be returned as an xs:double value.
  * @return  a XsDoubleExpr expression
  */
  public XsDoubleExpr number(XsAnyAtomicTypeExpr arg);
/**
  * Returns an xs:NCName representing the prefix of arg. The empty sequence is returned if arg is the empty sequence or if the value of arg contains no prefix.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:prefix-from-QName" target="mlserverdoc">fn:prefix-from-QName</a>
  * @param arg  A qualified name.
  * @return  a XsNCNameExpr expression
  */
  public XsNCNameExpr prefixFromQName(XsQNameExpr arg);
/**
  * Returns an xs:QName with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a>
  * @param paramURI  A namespace URI, as a string.
  * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".
  * @return  a XsQNameExpr expression
  */
  public XsQNameExpr QName(XsStringExpr paramURI, String paramQName);
/**
  * Returns an xs:QName with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a>
  * @param paramURI  A namespace URI, as a string.
  * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".
  * @return  a XsQNameExpr expression
  */
  public XsQNameExpr QName(XsStringExpr paramURI, XsStringExpr paramQName);
/**
  * Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:remove" target="mlserverdoc">fn:remove</a>
  * @param target  The sequence of items from which items will be removed.
  * @param position  The position in the target sequence from which the items will be removed.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr remove(ItemSeqExpr target, long position);
/**
  * Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed. 
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
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).
  * @return  a XsStringExpr expression
  */
  public XsStringExpr replace(XsStringExpr input, String pattern, String replacement);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
  * @param input  The string to start with.
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).
  * @return  a XsStringExpr expression
  */
  public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
  * @param input  The string to start with.
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr replace(XsStringExpr input, String pattern, String replacement, String flags);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a>
  * @param input  The string to start with.
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement, XsStringExpr flags);
/**
  * Returns an xs:QName value (that is, an expanded QName) by taking an xs:string that has the lexical form of an xs:QName (a string in the form "prefix:local-name" or "local-name") and resolving it using the in-scope namespaces for a given element. 
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
  * Reverses the order of items in a sequence. If $arg is the empty sequence, the empty sequence is returned. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:reverse" target="mlserverdoc">fn:reverse</a>
  * @param target  The sequence of items to be reversed.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr reverse(ItemSeqExpr target);
/**
  * Returns the root of the tree to which arg belongs. This will usually, but not necessarily, be a document node. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:root" target="mlserverdoc">fn:root</a>
  * @param arg  The node whose root node will be returned.
  * @return  a NodeExpr expression
  */
  public NodeExpr root(NodeExpr arg);
/**
  * Returns the number with no fractional part that is closest to the argument. If there are two such numbers, then the one that is closest to positive infinity is returned. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round" target="mlserverdoc">fn:round</a>
  * @param arg  A numeric value to round.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr round(XsNumericExpr arg);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
  * @param arg  A numeric value to round.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr roundHalfToEven(XsNumericExpr arg);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
  * @param arg  A numeric value to round.
  * @param precision  The precision to which to round the value.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr roundHalfToEven(XsNumericExpr arg, long precision);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a>
  * @param arg  A numeric value to round.
  * @param precision  The precision to which to round the value.
  * @return  a XsNumericExpr expression
  */
  public XsNumericExpr roundHalfToEven(XsNumericExpr arg, XsIntegerExpr precision);
/**
  * Returns an xs:decimal value between 0 and 60.999..., both inclusive representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-dateTime" target="mlserverdoc">fn:seconds-from-dateTime</a>
  * @param arg  The dateTime whose seconds component will be returned.
  * @return  a XsDecimalExpr expression
  */
  public XsDecimalExpr secondsFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:decimal representing the seconds component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-duration" target="mlserverdoc">fn:seconds-from-duration</a>
  * @param arg  The duration whose minute component will be returned.
  * @return  a XsDecimalExpr expression
  */
  public XsDecimalExpr secondsFromDuration(XsDurationExpr arg);
/**
  * Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:seconds-from-time" target="mlserverdoc">fn:seconds-from-time</a>
  * @param arg  The time whose seconds component will be returned.
  * @return  a XsDecimalExpr expression
  */
  public XsDecimalExpr secondsFromTime(XsTimeExpr arg);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a>
  * @param parameter1  The string from which to test.
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation);
/**
  * Returns the value of arg represented as an xs:string. If no argument is supplied, this function returns the string value of the context item (.).
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string" target="mlserverdoc">fn:string</a>
  * @param arg  The item to be rendered as a string.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr string(ItemExpr arg);
/**
  * Returns an xs:string created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-join" target="mlserverdoc">fn:string-join</a>
  * @param parameter1  A sequence of strings.
  * @param parameter2  A separator string to concatenate between the items in $parameter1.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr stringJoin(XsStringSeqExpr parameter1, String parameter2);
/**
  * Returns an xs:string created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator. 
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
  * Returns the sequence of Unicode code points that constitute an xs:string. If arg is a zero-length string or the empty sequence, the empty sequence is returned. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:string-to-codepoints" target="mlserverdoc">fn:string-to-codepoints</a>
  * @param arg  A string.
  * @return  a XsIntegerSeqExpr expression sequence
  */
  public XsIntegerSeqExpr stringToCodepoints(XsStringExpr arg);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.
  * @param startingLoc  The starting position of the start of the subsequence.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.
  * @param startingLoc  The starting position of the start of the subsequence.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a>
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.
  * @param startingLoc  The starting position of the start of the subsequence.
  * @param length  The length of the subsequence.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc, double length);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length. 
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
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr substringAfter(XsStringExpr input, String after, String collation);
/**
  * Returns the substring created by taking all of the input characters that occur after the specified after characters.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a>
  * @param input  The string from which to create the substring.
  * @param after  The string after which the substring is created.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
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
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr substringBefore(XsStringExpr input, String before, String collation);
/**
  * Returns the substring created by taking all of the input characters that occur before the specified before characters.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a>
  * @param input  The string from which to create the substring.
  * @param before  The string before which the substring is created.
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before, XsStringExpr collation);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
  * @param arg  The sequence of values to be summed.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
  * @param arg  The sequence of values to be summed.
  * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, String zero);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a>
  * @param arg  The sequence of values to be summed.
  * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.
  * @return  a XsAnyAtomicTypeExpr expression
  */
  public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr zero);
/**
  * Returns all but the first item in a sequence. For more details, see XPath 3.0 Functions and Operators.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tail" target="mlserverdoc">fn:tail</a>
  * @param seq  The function value.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr tail(ItemSeqExpr seq);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:timezone-from-date" target="mlserverdoc">fn:timezone-from-date</a>
  * @param arg  The date whose timezone component will be returned.
  * @return  a XsDayTimeDurationExpr expression
  */
  public XsDayTimeDurationExpr timezoneFromDate(XsDateExpr arg);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:timezone-from-dateTime" target="mlserverdoc">fn:timezone-from-dateTime</a>
  * @param arg  The dateTime whose timezone component will be returned.
  * @return  a XsDayTimeDurationExpr expression
  */
  public XsDayTimeDurationExpr timezoneFromDateTime(XsDateTimeExpr arg);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence. 
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
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
  * @return  a XsStringSeqExpr expression sequence
  */
  public XsStringSeqExpr tokenize(XsStringExpr input, String pattern, String flags);
/**
  * Returns a sequence of strings contructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a>
  * @param input  The string to tokenize.
  * @param pattern  The regular expression pattern from which to separate the tokens.
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.
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
  * Returns the xs:boolean value true. Equivalent to xs:boolean("1").
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:true" target="mlserverdoc">fn:true</a>
  * @return  a XsBooleanExpr expression
  */
  public XsBooleanExpr trueExpr();
/**
  * Returns the items of sourceSeq in an implementation dependent order. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:unordered" target="mlserverdoc">fn:unordered</a>
  * @param sourceSeq  The sequence of items.
  * @return  a ItemSeqExpr expression sequence
  */
  public ItemSeqExpr unordered(ItemSeqExpr sourceSeq);
/**
  * Returns the specified string converting all of the characters to upper-case characters. If a character does not have a corresponding upper-case character, then the original character is returned. The upper-case characters are determined using the Unicode Case Mappings.
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:upper-case" target="mlserverdoc">fn:upper-case</a>
  * @param string  The string to upper-case.
  * @return  a XsStringExpr expression
  */
  public XsStringExpr upperCase(XsStringExpr string);
/**
  * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:year-from-date" target="mlserverdoc">fn:year-from-date</a>
  * @param arg  The date whose year component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr yearFromDate(XsDateExpr arg);
/**
  * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:year-from-dateTime" target="mlserverdoc">fn:year-from-dateTime</a>
  * @param arg  The dateTime whose year component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr yearFromDateTime(XsDateTimeExpr arg);
/**
  * Returns an xs:integer representing the years component in the canonical lexical representation of the value of arg. The result may be negative. 
  * <p>
  * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/fn:years-from-duration" target="mlserverdoc">fn:years-from-duration</a>
  * @param arg  The duration whose year component will be returned.
  * @return  a XsIntegerExpr expression
  */
  public XsIntegerExpr yearsFromDuration(XsDurationExpr arg);
}
